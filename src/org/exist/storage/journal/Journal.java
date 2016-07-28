begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|BufferOverflowException
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
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
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
name|BrokerPool
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
name|lock
operator|.
name|FileLock
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
name|txn
operator|.
name|Checkpoint
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
name|FileUtils
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
name|ReadOnlyException
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
name|sanity
operator|.
name|SanityCheck
import|;
end_import

begin_comment
comment|/**  * Manages the journalling log. The database uses one central journal for  * all data files. If the journal exceeds the predefined maximum size, a new file is created.  * Every journal file has a unique number, which keeps growing during the lifetime of the db.  * The name of the file corresponds to the file number. The file with the highest  * number will be used for recovery.  *   * A buffer is used to temporarily buffer journal entries. To guarantee consistency, the buffer will be flushed  * and the journal is synched after every commit or whenever a db page is written to disk.  *   * Each entry has the structure:  *   *<pre>[byte: entryType, long: transactionId, short length, byte[] data, short backLink]</pre>  *   *<ul>  *<li>entryType is a unique id that identifies the log record. Entry types are registered via the   * {@link org.exist.storage.journal.LogEntryTypes} class.</li>  *<li>transactionId: the id of the transaction that created the record.</li>  *<li>length: the length of the log entry data.</li>  *<li>data: the payload data provided by the {@link org.exist.storage.journal.Loggable} object.</li>  *<li>backLink: offset to the start of the record. Used when scanning the log file backwards.</li>  *</ul>  *   * @author wolf  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"journal"
argument_list|)
comment|//TODO: conf.xml refactoring<recovery> =><recovery><journal/></recovery>
specifier|public
class|class
name|Journal
block|{
comment|/**      * Logger for this class      */
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
name|Journal
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RECOVERY_SYNC_ON_COMMIT_ATTRIBUTE
init|=
literal|"sync-on-commit"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RECOVERY_JOURNAL_DIR_ATTRIBUTE
init|=
literal|"journal-dir"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RECOVERY_SIZE_LIMIT_ATTRIBUTE
init|=
literal|"size"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_RECOVERY_SIZE_LIMIT
init|=
literal|"db-connection.recovery.size-limit"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_RECOVERY_JOURNAL_DIR
init|=
literal|"db-connection.recovery.journal-dir"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_RECOVERY_SYNC_ON_COMMIT
init|=
literal|"db-connection.recovery.sync-on-commit"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|LOG_FILE_SUFFIX
init|=
literal|"log"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|BAK_FILE_SUFFIX
init|=
literal|".bak"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|LCK_FILE
init|=
literal|"journal.lck"
decl_stmt|;
comment|/** the length of the header of each entry: entryType + transactionId + length */
specifier|public
specifier|final
specifier|static
name|int
name|LOG_ENTRY_HEADER_LEN
init|=
literal|11
decl_stmt|;
comment|/** header length + trailing back link */
specifier|public
specifier|final
specifier|static
name|int
name|LOG_ENTRY_BASE_LEN
init|=
name|LOG_ENTRY_HEADER_LEN
operator|+
literal|2
decl_stmt|;
comment|/** default maximum journal size */
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_SIZE
init|=
literal|10
decl_stmt|;
comment|//MB
comment|/** minimal size the journal needs to have to be replaced by a new file during a checkpoint */
specifier|private
specifier|static
specifier|final
name|long
name|MIN_REPLACE
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/**       * size limit for the journal file. A checkpoint will be triggered if the file      * exceeds this size limit.      */
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"size"
argument_list|)
comment|//TODO: conf.xml refactoring<recovery size=""> =><journal size="">
specifier|private
specifier|final
name|int
name|journalSizeLimit
decl_stmt|;
comment|/** the current output channel       * Only valid after switchFiles() was called at least once! */
specifier|private
name|FileOutputStream
name|os
decl_stmt|;
specifier|private
name|FileChannel
name|channel
decl_stmt|;
comment|/** Synching the journal is done by a background thread */
specifier|private
specifier|final
name|FileSyncThread
name|syncThread
decl_stmt|;
comment|/** latch used to synchronize writes to the channel */
specifier|private
specifier|final
name|Object
name|latch
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/** the data directory where journal files are written to */
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"journal-dir"
argument_list|)
comment|//TODO: conf.xml refactoring<recovery journal-dir=""> =><journal dir="">
specifier|private
specifier|final
name|Path
name|dir
decl_stmt|;
specifier|private
name|FileLock
name|fileLock
decl_stmt|;
comment|/** the current file number */
specifier|private
name|int
name|currentFile
init|=
literal|0
decl_stmt|;
comment|/** used to keep track of the current position in the file */
specifier|private
name|int
name|inFilePos
init|=
literal|0
decl_stmt|;
comment|/** temp buffer */
specifier|private
name|ByteBuffer
name|currentBuffer
decl_stmt|;
comment|/** the last LSN written by the JournalManager */
specifier|private
name|long
name|currentLsn
init|=
name|Lsn
operator|.
name|LSN_INVALID
decl_stmt|;
comment|/** the last LSN actually written to the file */
specifier|private
name|long
name|lastLsnWritten
init|=
name|Lsn
operator|.
name|LSN_INVALID
decl_stmt|;
comment|/** stores the current LSN of the last file sync on the file */
specifier|private
name|long
name|lastSyncLsn
init|=
name|Lsn
operator|.
name|LSN_INVALID
decl_stmt|;
comment|/** set to true while recovery is in progress */
specifier|private
name|boolean
name|inRecovery
init|=
literal|false
decl_stmt|;
comment|/** the {@link BrokerPool} that created this manager */
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
comment|/** if set to true, a sync will be triggered on the log file after every commit */
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"sync-on-commit"
argument_list|)
comment|//TODO: conf.xml refactoring<recovery sync-on-commit=""> =><journal sync-on-commit="">
specifier|private
specifier|final
specifier|static
name|boolean
name|DEFAULT_SYNC_ON_COMMIT
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|syncOnCommit
decl_stmt|;
specifier|private
specifier|final
name|Path
name|fsJournalDir
decl_stmt|;
specifier|public
name|Journal
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|Path
name|directory
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|fsJournalDir
operator|=
name|directory
operator|.
name|resolve
argument_list|(
literal|"fs.journal"
argument_list|)
expr_stmt|;
comment|// we use a 1 megabyte buffer:
name|currentBuffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|syncThread
operator|=
operator|new
name|FileSyncThread
argument_list|(
name|latch
argument_list|)
expr_stmt|;
name|syncThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|syncOnCommit
operator|=
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_RECOVERY_SYNC_ON_COMMIT
argument_list|,
name|DEFAULT_SYNC_ON_COMMIT
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SyncOnCommit = "
operator|+
name|syncOnCommit
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|logDir
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
operator|(
name|Path
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_RECOVERY_JOURNAL_DIR
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|logDir
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|Path
name|f
init|=
name|logDir
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|f
operator|=
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
operator|.
name|map
argument_list|(
name|h
lambda|->
name|Optional
operator|.
name|of
argument_list|(
name|h
operator|.
name|resolve
argument_list|(
name|logDir
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getConfigFilePath
argument_list|()
operator|.
name|map
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getParent
argument_list|()
operator|.
name|resolve
argument_list|(
name|logDir
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|f
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Output directory for journal files does not exist. Creating "
operator|+
name|f
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Failed to create output directory: "
operator|+
name|f
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
if|if
condition|(
operator|!
name|Files
operator|.
name|isWritable
argument_list|(
name|f
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot write to journal output directory: "
operator|+
name|f
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|dir
operator|=
name|f
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|dir
operator|=
name|directory
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using directory for the journal: "
operator|+
name|dir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|journalSizeLimit
operator|=
literal|1024
operator|*
literal|1024
operator|*
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_RECOVERY_SIZE_LIMIT
argument_list|,
name|DEFAULT_MAX_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|EXistException
throws|,
name|ReadOnlyException
block|{
specifier|final
name|Path
name|lck
init|=
name|dir
operator|.
name|resolve
argument_list|(
name|LCK_FILE
argument_list|)
decl_stmt|;
name|fileLock
operator|=
operator|new
name|FileLock
argument_list|(
name|pool
argument_list|,
name|lck
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|locked
init|=
name|fileLock
operator|.
name|tryLock
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|locked
condition|)
block|{
specifier|final
name|String
name|lastHeartbeat
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|(
name|DateFormat
operator|.
name|MEDIUM
argument_list|,
name|DateFormat
operator|.
name|MEDIUM
argument_list|)
operator|.
name|format
argument_list|(
name|fileLock
operator|.
name|getLastHeartbeat
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"The journal log directory seems to be locked by another "
operator|+
literal|"eXist process. A lock file: "
operator|+
name|lck
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" is present in the "
operator|+
literal|"log directory. Last access to the lock file: "
operator|+
name|lastHeartbeat
argument_list|)
throw|;
block|}
block|}
comment|/**      * Write a log entry to the journalling log.      *       * @param loggable      * @throws JournalException      */
specifier|public
specifier|synchronized
name|void
name|writeToLog
parameter_list|(
specifier|final
name|Loggable
name|loggable
parameter_list|)
throws|throws
name|JournalException
block|{
if|if
condition|(
name|currentBuffer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JournalException
argument_list|(
literal|"Database is shut down."
argument_list|)
throw|;
block|}
name|SanityCheck
operator|.
name|ASSERT
argument_list|(
operator|!
name|inRecovery
argument_list|,
literal|"Write to log during recovery. Should not happen!"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|loggable
operator|.
name|getLogSize
argument_list|()
decl_stmt|;
specifier|final
name|int
name|required
init|=
name|size
operator|+
name|LOG_ENTRY_BASE_LEN
decl_stmt|;
if|if
condition|(
name|required
operator|>
name|currentBuffer
operator|.
name|remaining
argument_list|()
condition|)
block|{
name|flushToLog
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|currentLsn
operator|=
name|Lsn
operator|.
name|create
argument_list|(
name|currentFile
argument_list|,
name|inFilePos
operator|+
name|currentBuffer
operator|.
name|position
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|loggable
operator|.
name|setLsn
argument_list|(
name|currentLsn
argument_list|)
expr_stmt|;
try|try
block|{
name|currentBuffer
operator|.
name|put
argument_list|(
name|loggable
operator|.
name|getLogType
argument_list|()
argument_list|)
expr_stmt|;
name|currentBuffer
operator|.
name|putLong
argument_list|(
name|loggable
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|currentBuffer
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|loggable
operator|.
name|getLogSize
argument_list|()
argument_list|)
expr_stmt|;
name|loggable
operator|.
name|write
argument_list|(
name|currentBuffer
argument_list|)
expr_stmt|;
name|currentBuffer
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
operator|(
name|size
operator|+
name|LOG_ENTRY_HEADER_LEN
operator|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BufferOverflowException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JournalException
argument_list|(
literal|"Buffer overflow while writing log record: "
operator|+
name|loggable
operator|.
name|dump
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|trackOperation
argument_list|(
name|loggable
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the last LSN physically written to the journal.      *       * @return last written LSN      */
specifier|public
name|long
name|lastWrittenLsn
parameter_list|()
block|{
return|return
name|lastLsnWritten
return|;
block|}
comment|/**      * Flush the current buffer to disk. If fsync is true, a sync will      * be called on the file to force all changes to disk.      *       * @param fsync forces all changes to disk if true and syncMode is set to SYNC_ON_COMMIT.      */
specifier|public
name|void
name|flushToLog
parameter_list|(
specifier|final
name|boolean
name|fsync
parameter_list|)
block|{
name|flushToLog
argument_list|(
name|fsync
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Flush the current buffer to disk. If fsync is true, a sync will      * be called on the file to force all changes to disk.      *       * @param fsync forces all changes to disk if true and syncMode is set to SYNC_ON_COMMIT.      * @param forceSync force changes to disk even if syncMode doesn't require it.      */
specifier|public
specifier|synchronized
name|void
name|flushToLog
parameter_list|(
specifier|final
name|boolean
name|fsync
parameter_list|,
specifier|final
name|boolean
name|forceSync
parameter_list|)
block|{
if|if
condition|(
name|inRecovery
condition|)
block|{
return|return;
block|}
name|flushBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|forceSync
operator|||
operator|(
name|fsync
operator|&&
name|syncOnCommit
operator|&&
name|currentLsn
operator|>
name|lastSyncLsn
operator|)
condition|)
block|{
name|syncThread
operator|.
name|triggerSync
argument_list|()
expr_stmt|;
name|lastSyncLsn
operator|=
name|currentLsn
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|channel
operator|!=
literal|null
operator|&&
name|channel
operator|.
name|size
argument_list|()
operator|>=
name|journalSizeLimit
condition|)
block|{
name|pool
operator|.
name|triggerCheckpoint
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
literal|"Failed to trigger checkpoint!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *       */
specifier|private
name|void
name|flushBuffer
parameter_list|()
block|{
if|if
condition|(
name|currentBuffer
operator|==
literal|null
operator|||
name|channel
operator|==
literal|null
condition|)
block|{
return|return;
comment|// the db has probably been shut down already or not fully initialized
block|}
synchronized|synchronized
init|(
name|latch
init|)
block|{
try|try
block|{
if|if
condition|(
name|currentBuffer
operator|.
name|position
argument_list|()
operator|>
literal|0
condition|)
block|{
name|currentBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|currentBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
while|while
condition|(
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|channel
operator|.
name|write
argument_list|(
name|currentBuffer
argument_list|)
expr_stmt|;
block|}
name|inFilePos
operator|+=
name|size
expr_stmt|;
name|lastLsnWritten
operator|=
name|currentLsn
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
literal|"Flushing log file failed!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|currentBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Write a checkpoint record to the journal and flush it. If switchLogFiles is true,      * a new journal will be started, but only if the file is larger than      * {@link #MIN_REPLACE}. The old log is removed.      *      * @param txnId The transaction id      * @param switchLogFiles Indicates whether a new journal file should be started      * @throws JournalException      */
specifier|public
name|void
name|checkpoint
parameter_list|(
specifier|final
name|long
name|txnId
parameter_list|,
specifier|final
name|boolean
name|switchLogFiles
parameter_list|)
throws|throws
name|JournalException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Checkpoint reached"
argument_list|)
expr_stmt|;
name|writeToLog
argument_list|(
operator|new
name|Checkpoint
argument_list|(
name|txnId
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|switchLogFiles
condition|)
block|{
comment|// if we switch files, we don't need to sync.
comment|// the file will be removed anyway.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|flushToLog
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|switchLogFiles
operator|&&
name|channel
operator|!=
literal|null
operator|&&
name|channel
operator|.
name|position
argument_list|()
operator|>
name|MIN_REPLACE
condition|)
block|{
specifier|final
name|Path
name|oldFile
init|=
name|getFile
argument_list|(
name|currentFile
argument_list|)
decl_stmt|;
specifier|final
name|RemoveThread
name|rt
init|=
operator|new
name|RemoveThread
argument_list|(
name|channel
argument_list|,
name|oldFile
argument_list|)
decl_stmt|;
try|try
block|{
name|switchFiles
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LogException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create new journal: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|rt
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|clearBackupFiles
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"IOException while writing checkpoint"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set the file number of the last file used.      *       * @param fileNum the log file number      */
specifier|public
name|void
name|setCurrentFileNum
parameter_list|(
specifier|final
name|int
name|fileNum
parameter_list|)
block|{
name|currentFile
operator|=
name|fileNum
expr_stmt|;
block|}
specifier|public
name|void
name|clearBackupFiles
parameter_list|()
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|fsJournalDir
argument_list|)
condition|)
block|{
try|try
init|(
specifier|final
name|Stream
argument_list|<
name|Path
argument_list|>
name|backupFiles
init|=
name|Files
operator|.
name|list
argument_list|(
name|fsJournalDir
argument_list|)
init|)
block|{
name|backupFiles
operator|.
name|forEach
argument_list|(
name|p
lambda|->
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checkpoint deleting: "
operator|+
name|p
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot delete file '"
operator|+
name|p
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"' from backup journal."
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not clear fs.journal backup files"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Create a new journal with a larger file number      * than the previous file.      *       * @throws LogException      */
specifier|public
name|void
name|switchFiles
parameter_list|()
throws|throws
name|LogException
block|{
operator|++
name|currentFile
expr_stmt|;
specifier|final
name|String
name|fname
init|=
name|getFileName
argument_list|(
name|currentFile
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|file
init|=
name|dir
operator|.
name|resolve
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Journal file "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|" already exists. Copying it."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|Path
name|renamed
init|=
name|Files
operator|.
name|move
argument_list|(
name|file
argument_list|,
name|file
operator|.
name|resolveSibling
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
operator|+
name|BAK_FILE_SUFFIX
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Old file renamed from '"
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"' to '"
operator|+
name|renamed
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
comment|//TODO(AR) should probably be an LogException but wasn't previously!
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating new journal: "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|latch
init|)
block|{
name|close
argument_list|()
expr_stmt|;
try|try
block|{
comment|//RandomAccessFile raf = new RandomAccessFile(file, "rw");
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|file
operator|.
name|toFile
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|channel
operator|=
name|os
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|syncThread
operator|.
name|setChannel
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Failed to open new journal: "
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
name|inFilePos
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|channel
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to close journal"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|os
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to close journal"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|int
name|journalFileNum
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
specifier|final
name|String
name|fileName
init|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|int
name|p
init|=
name|fileName
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|baseName
init|=
name|fileName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
decl_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|baseName
argument_list|,
literal|16
argument_list|)
return|;
block|}
comment|/**      * Find the journal file with the highest file number.      *       * @param files      */
specifier|public
specifier|final
specifier|static
name|int
name|findLastFile
parameter_list|(
specifier|final
name|Stream
argument_list|<
name|Path
argument_list|>
name|files
parameter_list|)
block|{
return|return
name|files
operator|.
name|map
argument_list|(
name|Journal
operator|::
name|journalFileNum
argument_list|)
operator|.
name|max
argument_list|(
name|Integer
operator|::
name|max
argument_list|)
operator|.
name|orElse
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**      * Returns a Stream of all journal files found in the data directory.      *       * @return A Stream of all journal files. NOTE - This is      * an I/O Stream and so you are responsible for closing it!      */
specifier|public
name|Stream
argument_list|<
name|Path
argument_list|>
name|getFiles
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|suffix
init|=
literal|'.'
operator|+
name|LOG_FILE_SUFFIX
decl_stmt|;
specifier|final
name|String
name|indexSuffix
init|=
literal|"_index"
operator|+
name|suffix
decl_stmt|;
return|return
name|Files
operator|.
name|find
argument_list|(
name|dir
argument_list|,
literal|1
argument_list|,
parameter_list|(
name|path
parameter_list|,
name|attrs
parameter_list|)
lambda|->
name|attrs
operator|.
name|isRegularFile
argument_list|()
operator|&&
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
operator|&&
operator|!
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
operator|.
name|endsWith
argument_list|(
name|indexSuffix
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the file corresponding to the specified      * file number.      *       * @param fileNum      */
specifier|public
name|Path
name|getFile
parameter_list|(
specifier|final
name|int
name|fileNum
parameter_list|)
block|{
return|return
name|dir
operator|.
name|resolve
argument_list|(
name|getFileName
argument_list|(
name|fileNum
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Shut down the journal. This will write a checkpoint record      * to the log, so recovery manager knows the file has been      * closed in a clean way.      *       * @param txnId      */
specifier|public
name|void
name|shutdown
parameter_list|(
specifier|final
name|long
name|txnId
parameter_list|,
specifier|final
name|boolean
name|checkpoint
parameter_list|)
block|{
if|if
condition|(
name|currentBuffer
operator|==
literal|null
condition|)
block|{
return|return;
comment|// the db has probably shut down already
block|}
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
condition|)
block|{
if|if
condition|(
name|checkpoint
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Transaction journal cleanly shutting down with checkpoint..."
argument_list|)
expr_stmt|;
try|try
block|{
name|writeToLog
argument_list|(
operator|new
name|Checkpoint
argument_list|(
name|txnId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|JournalException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"An error occurred while closing the journal file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|fileLock
operator|.
name|release
argument_list|()
expr_stmt|;
name|syncThread
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|syncThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//Nothing to do
block|}
name|currentBuffer
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Called to signal that the db is currently in      * recovery phase, so no output should be written.      *       * @param value      */
specifier|public
name|void
name|setInRecovery
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
name|inRecovery
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Translate a file number into a file name.      *       * @param fileNum      * @return The file name      */
specifier|private
specifier|static
name|String
name|getFileName
parameter_list|(
specifier|final
name|int
name|fileNum
parameter_list|)
block|{
name|String
name|hex
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|fileNum
argument_list|)
decl_stmt|;
name|hex
operator|=
literal|"0000000000"
operator|.
name|substring
argument_list|(
name|hex
operator|.
name|length
argument_list|()
argument_list|)
operator|+
name|hex
expr_stmt|;
return|return
name|hex
operator|+
literal|'.'
operator|+
name|LOG_FILE_SUFFIX
return|;
block|}
specifier|private
specifier|static
class|class
name|RemoveThread
extends|extends
name|Thread
block|{
specifier|final
name|FileChannel
name|channel
decl_stmt|;
specifier|final
name|Path
name|path
decl_stmt|;
name|RemoveThread
parameter_list|(
specifier|final
name|FileChannel
name|channel
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
block|{
name|super
argument_list|(
literal|"RemoveJournalThread"
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
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
literal|"Exception while closing journal file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

