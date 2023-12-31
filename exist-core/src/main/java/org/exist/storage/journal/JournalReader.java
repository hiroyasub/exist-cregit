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
name|net
operator|.
name|jpountz
operator|.
name|xxhash
operator|.
name|StreamingXXHash64
import|;
end_import

begin_import
import|import
name|net
operator|.
name|jpountz
operator|.
name|xxhash
operator|.
name|XXHashFactory
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
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ByteConversion
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
name|*
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
specifier|private
specifier|final
name|StreamingXXHash64
name|xxHash64
init|=
name|XXHashFactory
operator|.
name|fastestInstance
argument_list|()
operator|.
name|newStreamingHash64
argument_list|(
name|Journal
operator|.
name|XXHASH64_SEED
argument_list|)
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
name|validateJournalHeader
argument_list|(
name|file
argument_list|,
name|fc
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
specifier|private
name|void
name|validateJournalHeader
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|,
specifier|final
name|SeekableByteChannel
name|fc
parameter_list|)
throws|throws
name|IOException
throws|,
name|LogException
block|{
comment|// read the magic number
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|JOURNAL_HEADER_LEN
argument_list|)
decl_stmt|;
name|fc
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|flip
argument_list|()
expr_stmt|;
comment|// check the magic number
specifier|final
name|boolean
name|validMagic
init|=
name|buf
operator|.
name|get
argument_list|()
operator|==
name|JOURNAL_MAGIC_NUMBER
index|[
literal|0
index|]
operator|&&
name|buf
operator|.
name|get
argument_list|()
operator|==
name|JOURNAL_MAGIC_NUMBER
index|[
literal|1
index|]
operator|&&
name|buf
operator|.
name|get
argument_list|()
operator|==
name|JOURNAL_MAGIC_NUMBER
index|[
literal|2
index|]
operator|&&
name|buf
operator|.
name|get
argument_list|()
operator|==
name|JOURNAL_MAGIC_NUMBER
index|[
literal|3
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|validMagic
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"File was not recognised as a valid eXist-db journal file: "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|// check the version of the journal format
specifier|final
name|short
name|storedVersion
init|=
name|ByteConversion
operator|.
name|byteToShortH
argument_list|(
operator|new
name|byte
index|[]
block|{
name|buf
operator|.
name|get
argument_list|()
block|,
name|buf
operator|.
name|get
argument_list|()
block|}
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|validVersion
init|=
name|storedVersion
operator|==
name|JOURNAL_VERSION
decl_stmt|;
if|if
condition|(
operator|!
name|validVersion
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Journal file was version "
operator|+
name|storedVersion
operator|+
literal|", but required version "
operator|+
name|JOURNAL_VERSION
operator|+
literal|": "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
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
comment|// is there a previous entry to read?
if|if
condition|(
name|fc
operator|.
name|position
argument_list|()
operator|<
name|JOURNAL_HEADER_LEN
operator|+
name|LOG_ENTRY_BASE_LEN
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// go back 8 bytes (checksum length) + 2 bytes (backLink length) and read the backLink (2 bytes) of the last entry
name|fc
operator|.
name|position
argument_list|(
name|fc
operator|.
name|position
argument_list|()
operator|-
name|LOG_ENTRY_CHECKSUM_LEN
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
name|backLink
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
name|backLink
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
name|positionLast
argument_list|()
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
if|if
condition|(
name|fileNumber
operator|>
name|Short
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Journal can only support "
operator|+
name|Short
operator|.
name|MAX_VALUE
operator|+
literal|" log files"
argument_list|)
throw|;
block|}
specifier|final
name|Lsn
name|lsn
init|=
operator|new
name|Lsn
argument_list|(
operator|(
name|short
operator|)
name|fileNumber
argument_list|,
name|fc
operator|.
name|position
argument_list|()
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
comment|// prepare the checksum for the header
name|xxHash64
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|header
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|xxHash64
operator|.
name|update
argument_list|(
name|header
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|LOG_ENTRY_HEADER_LEN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|mark
init|=
name|header
operator|.
name|position
argument_list|()
decl_stmt|;
name|header
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|LOG_ENTRY_HEADER_LEN
index|]
decl_stmt|;
name|header
operator|.
name|get
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|xxHash64
operator|.
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|LOG_ENTRY_HEADER_LEN
argument_list|)
expr_stmt|;
name|header
operator|.
name|position
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
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
name|lsn
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
specifier|final
name|int
name|remainingEntryBytes
init|=
name|size
operator|+
name|LOG_ENTRY_BACK_LINK_LEN
operator|+
name|LOG_ENTRY_CHECKSUM_LEN
decl_stmt|;
if|if
condition|(
name|remainingEntryBytes
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
name|allocateDirect
argument_list|(
name|remainingEntryBytes
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
name|remainingEntryBytes
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
name|remainingEntryBytes
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
comment|// read entry data
name|loggable
operator|.
name|read
argument_list|(
name|payload
argument_list|)
expr_stmt|;
comment|// read entry backLink
specifier|final
name|short
name|backLink
init|=
name|payload
operator|.
name|getShort
argument_list|()
decl_stmt|;
if|if
condition|(
name|backLink
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
literal|"Bad pointer to previous: backLink = "
operator|+
name|backLink
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
comment|// update the checksum for the entry data and backLink
if|if
condition|(
name|payload
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|xxHash64
operator|.
name|update
argument_list|(
name|payload
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
operator|+
name|LOG_ENTRY_BACK_LINK_LEN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|mark
init|=
name|payload
operator|.
name|position
argument_list|()
decl_stmt|;
name|payload
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|size
operator|+
name|LOG_ENTRY_BACK_LINK_LEN
index|]
decl_stmt|;
name|payload
operator|.
name|get
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|xxHash64
operator|.
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|size
operator|+
name|LOG_ENTRY_BACK_LINK_LEN
argument_list|)
expr_stmt|;
name|payload
operator|.
name|position
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
comment|// read the entry checksum
specifier|final
name|long
name|checksum
init|=
name|payload
operator|.
name|getLong
argument_list|()
decl_stmt|;
comment|// verify the checksum
specifier|final
name|long
name|calculatedChecksum
init|=
name|xxHash64
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|checksum
operator|!=
name|calculatedChecksum
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Checksum mismatch whilst reading log entry. read="
operator|+
name|checksum
operator|+
literal|" calculated="
operator|+
name|calculatedChecksum
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
name|Lsn
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
name|lsn
operator|.
name|getOffset
argument_list|()
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
comment|/**      * Re-position the file position so it points to the first entry.      *      * @throws LogException if the journal file cannot be re-positioned      */
specifier|public
name|void
name|positionFirst
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
name|JOURNAL_HEADER_LEN
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
literal|"Fatal error while seeking first journal entry: "
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
comment|/**      * Re-position the file position so it points to the last entry.      *      * @throws LogException if the journal file cannot be re-positioned      */
specifier|public
name|void
name|positionLast
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
literal|"Fatal error while seeking last journal entry: "
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

