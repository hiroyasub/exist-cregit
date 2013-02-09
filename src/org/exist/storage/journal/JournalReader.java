begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * Read log entries from the journal file. This class is used during recovery to scan the  * last journal file. It uses a memory-mapped byte buffer on the file.  * Journal entries can be read forward (during redo) or backward (during undo).   *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|JournalReader
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JournalReader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|FileChannel
name|fc
decl_stmt|;
specifier|private
name|ByteBuffer
name|header
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|Journal
operator|.
name|LOG_ENTRY_HEADER_LEN
argument_list|)
decl_stmt|;
specifier|private
name|ByteBuffer
name|payload
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
literal|8192
argument_list|)
decl_stmt|;
specifier|private
name|int
name|fileNumber
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
comment|/**      * Opens the specified file for reading.      *       * @param broker      * @param file      * @param fileNumber      * @throws LogException      */
specifier|public
name|JournalReader
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|File
name|file
parameter_list|,
name|int
name|fileNumber
parameter_list|)
throws|throws
name|LogException
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|fileNumber
operator|=
name|fileNumber
expr_stmt|;
try|try
block|{
specifier|final
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|fc
operator|=
name|is
operator|.
name|getChannel
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Failed to read log file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the next entry found from the current position.      *       * @return the next entry      * @throws LogException if an entry could not be read due to an inconsistency on disk.      */
specifier|public
name|Loggable
name|nextEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
if|if
condition|(
name|fc
operator|.
name|position
argument_list|()
operator|+
name|Journal
operator|.
name|LOG_ENTRY_BASE_LEN
operator|>
name|fc
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|readEntry
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Returns the previous entry found by scanning backwards from the current position.      *       * @return the previous entry      * @throws LogException if an entry could not be read due to an inconsistency on disk.      * @throws LogException       */
specifier|public
name|Loggable
name|previousEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
if|if
condition|(
name|fc
operator|.
name|position
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// go back two bytes and read the back-link of the last entry
name|fc
operator|.
name|position
argument_list|(
name|fc
operator|.
name|position
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|header
operator|.
name|clear
argument_list|()
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bytes
init|=
name|fc
operator|.
name|read
argument_list|(
name|header
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Incomplete log entry found!"
argument_list|)
throw|;
block|}
name|header
operator|.
name|flip
argument_list|()
expr_stmt|;
specifier|final
name|short
name|prevLink
init|=
name|header
operator|.
name|getShort
argument_list|()
decl_stmt|;
comment|// position the channel to the start of the previous entry and mark it
specifier|final
name|long
name|prevStart
init|=
name|fc
operator|.
name|position
argument_list|()
operator|-
literal|2
operator|-
name|prevLink
decl_stmt|;
name|fc
operator|.
name|position
argument_list|(
name|prevStart
argument_list|)
expr_stmt|;
specifier|final
name|Loggable
name|loggable
init|=
name|readEntry
argument_list|()
decl_stmt|;
comment|// reset to the mark
name|fc
operator|.
name|position
argument_list|(
name|prevStart
argument_list|)
expr_stmt|;
return|return
name|loggable
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Fatal error while reading journal entry: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Loggable
name|lastEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
name|fc
operator|.
name|position
argument_list|(
name|fc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|previousEntry
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Fatal error while reading journal entry: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Read a single entry.      *       * @return The entry      * @throws LogException      */
specifier|private
name|Loggable
name|readEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
specifier|final
name|long
name|lsn
init|=
name|Lsn
operator|.
name|create
argument_list|(
name|fileNumber
argument_list|,
operator|(
name|int
operator|)
name|fc
operator|.
name|position
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|header
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|bytes
init|=
name|fc
operator|.
name|read
argument_list|(
name|header
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|<=
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|bytes
operator|<
name|Journal
operator|.
name|LOG_ENTRY_HEADER_LEN
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Incomplete log entry header found: "
operator|+
name|bytes
argument_list|)
throw|;
block|}
name|header
operator|.
name|flip
argument_list|()
expr_stmt|;
specifier|final
name|byte
name|entryType
init|=
name|header
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|long
name|transactId
init|=
name|header
operator|.
name|getLong
argument_list|()
decl_stmt|;
specifier|final
name|short
name|size
init|=
name|header
operator|.
name|getShort
argument_list|()
decl_stmt|;
if|if
condition|(
name|fc
operator|.
name|position
argument_list|()
operator|+
name|size
operator|>
name|fc
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Invalid length"
argument_list|)
throw|;
block|}
specifier|final
name|Loggable
name|loggable
init|=
name|LogEntryTypes
operator|.
name|create
argument_list|(
name|entryType
argument_list|,
name|broker
argument_list|,
name|transactId
argument_list|)
decl_stmt|;
if|if
condition|(
name|loggable
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Invalid log entry: "
operator|+
name|entryType
operator|+
literal|"; size: "
operator|+
name|size
operator|+
literal|"; id: "
operator|+
name|transactId
operator|+
literal|"; at: "
operator|+
name|Lsn
operator|.
name|dump
argument_list|(
name|lsn
argument_list|)
argument_list|)
throw|;
block|}
name|loggable
operator|.
name|setLsn
argument_list|(
name|lsn
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|+
literal|2
operator|>
name|payload
operator|.
name|capacity
argument_list|()
condition|)
block|{
comment|// resize the payload buffer
name|payload
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|size
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
name|payload
operator|.
name|clear
argument_list|()
operator|.
name|limit
argument_list|(
name|size
operator|+
literal|2
argument_list|)
expr_stmt|;
name|bytes
operator|=
name|fc
operator|.
name|read
argument_list|(
name|payload
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|<
name|size
operator|+
literal|2
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Incomplete log entry found!"
argument_list|)
throw|;
block|}
name|payload
operator|.
name|flip
argument_list|()
expr_stmt|;
name|loggable
operator|.
name|read
argument_list|(
name|payload
argument_list|)
expr_stmt|;
specifier|final
name|short
name|prevLink
init|=
name|payload
operator|.
name|getShort
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevLink
operator|!=
name|size
operator|+
name|Journal
operator|.
name|LOG_ENTRY_HEADER_LEN
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Bad pointer to previous: prevLink = "
operator|+
name|prevLink
operator|+
literal|"; size = "
operator|+
name|size
operator|+
literal|"; transactId = "
operator|+
name|transactId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Bad pointer to previous in entry: "
operator|+
name|loggable
operator|.
name|dump
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|loggable
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Re-position the file position so it points to the start of the entry      * with the given LSN.      *       * @param lsn      * @throws LogException       */
specifier|public
name|void
name|position
parameter_list|(
name|long
name|lsn
parameter_list|)
throws|throws
name|LogException
block|{
try|try
block|{
name|fc
operator|.
name|position
argument_list|(
operator|(
name|int
operator|)
name|Lsn
operator|.
name|getOffset
argument_list|(
name|lsn
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Fatal error while reading journal: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
comment|//Nothing to do
block|}
name|fc
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

