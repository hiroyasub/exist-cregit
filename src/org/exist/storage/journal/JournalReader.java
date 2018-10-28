begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|SeekableByteChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|READ
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|Journal
operator|.
name|LOG_ENTRY_BACK_LINK_LEN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|Journal
operator|.
name|LOG_ENTRY_BASE_LEN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|Journal
operator|.
name|LOG_ENTRY_HEADER_LEN
import|;
end_import

begin_comment
comment|/**  * Read log entries from the journal file. This class is used during recovery to scan the  * last journal file. It uses a memory-mapped byte buffer on the file.  * Journal entries can be read forward (during redo) or backward (during undo).  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|JournalReader
implements|implements
name|AutoCloseable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|JournalReader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|int
name|fileNumber
decl_stmt|;
specifier|private
specifier|final
name|ByteBuffer
name|header
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
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
comment|// 8 KB
annotation|@
name|Nullable
specifier|private
name|SeekableByteChannel
name|fc
decl_stmt|;
comment|/**      * Opens the specified file for reading.      *      * @param broker     the database broker      * @param file       the journal file      * @param fileNumber the number of the journal file      * @throws LogException if the journal cannot be opened      */
specifier|public
name|JournalReader
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Path
name|file
parameter_list|,
specifier|final
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
name|this
operator|.
name|fc
operator|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|file
argument_list|,
name|READ
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
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Failed to read journal file "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the next entry found from the current position.      *      * @return the next entry, or null if there are no more entries.      * @throws LogException if an entry could not be read due to an inconsistency on disk.      */
specifier|public
annotation|@
name|Nullable
name|Loggable
name|nextEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
comment|// are we at the end of the journal?
if|if
condition|(
name|fc
operator|.
name|position
argument_list|()
operator|+
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
literal|"Unable to check journal position and size: "
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
return|return
name|readEntry
argument_list|()
return|;
block|}
comment|/**      * Returns the previous entry found by scanning backwards from the current position.      *      * @return the previous entry, or null of there was no previous entry.      * @throws LogException if an entry could not be read due to an inconsistency on disk.      */
specifier|public
annotation|@
name|Nullable
name|Loggable
name|previousEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
comment|// are we at the start of the journal?
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
name|LOG_ENTRY_BACK_LINK_LEN
argument_list|)
expr_stmt|;
name|header
operator|.
name|clear
argument_list|()
operator|.
name|limit
argument_list|(
name|LOG_ENTRY_BACK_LINK_LEN
argument_list|)
expr_stmt|;
specifier|final
name|int
name|read
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
name|read
operator|!=
name|LOG_ENTRY_BACK_LINK_LEN
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Unable to read journal entry back-link!"
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
name|LOG_ENTRY_BACK_LINK_LEN
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
literal|"Fatal error while reading previous journal entry: "
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
comment|/**      * Returns the last entry in the journal.      *      * @return the last entry in the journal, or null if there are no entries in the journal.      * @throws LogException if an entry could not be read due to an inconsistency on disk.      */
specifier|public
annotation|@
name|Nullable
name|Loggable
name|lastEntry
parameter_list|()
throws|throws
name|LogException
block|{
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
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
literal|"Fatal error while reading last journal entry: "
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
comment|/**      * Read the current entry from the journal.      *      * @return The entry, or null if there is no entry.      * @throws LogException if an entry could not be read due to an inconsistency on disk.      */
specifier|private
annotation|@
name|Nullable
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
name|offset
init|=
name|fc
operator|.
name|position
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Journal can only read log files of less that 2GB"
argument_list|)
throw|;
block|}
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
operator|(
name|int
operator|)
operator|(
name|offset
operator|&
literal|0x7FFFFFFF
operator|)
operator|)
operator|+
literal|1
argument_list|)
decl_stmt|;
comment|// read the entry header
name|header
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|read
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
name|read
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
name|read
operator|!=
name|LOG_ENTRY_HEADER_LEN
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Incomplete journal entry header found, expected  "
operator|+
name|LOG_ENTRY_HEADER_LEN
operator|+
literal|" bytes, but found "
operator|+
name|read
operator|+
literal|" bytes"
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
name|LOG_ENTRY_BACK_LINK_LEN
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
name|LOG_ENTRY_BACK_LINK_LEN
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
name|LOG_ENTRY_BACK_LINK_LEN
argument_list|)
expr_stmt|;
name|read
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
name|read
operator|<
name|size
operator|+
name|LOG_ENTRY_BACK_LINK_LEN
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
name|LOG_ENTRY_HEADER_LEN
condition|)
block|{
name|LOG
operator|.
name|error
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
name|IOException
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
comment|/**      * Re-position the file position so it points to the start of the entry      * with the given LSN.      *      * @param lsn the log sequence number      * @throws LogException if the journal file cannot be re-positioned      */
specifier|public
name|void
name|position
parameter_list|(
specifier|final
name|long
name|lsn
parameter_list|)
throws|throws
name|LogException
block|{
try|try
block|{
name|checkOpen
argument_list|()
expr_stmt|;
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
literal|"Fatal error while seeking journal: "
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
specifier|private
name|void
name|checkOpen
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Journal file is closed"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|fc
operator|!=
literal|null
condition|)
block|{
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|fc
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

