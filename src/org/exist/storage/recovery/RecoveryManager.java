begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2005-2013 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|recovery
package|;
end_package

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
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Collectors
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
name|journal
operator|.
name|Journal
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
name|journal
operator|.
name|JournalReader
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
name|journal
operator|.
name|LogEntryTypes
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
name|journal
operator|.
name|LogException
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
name|journal
operator|.
name|Loggable
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
name|journal
operator|.
name|Lsn
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
name|sync
operator|.
name|Sync
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
name|ProgressBar
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
name|hashtable
operator|.
name|Long2ObjectHashMap
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
comment|/**  * Database recovery. This class is used once during startup to check  * if the database is in a consistent state. If not, the class attempts to recover  * the database from the journalling log.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|RecoveryManager
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RecoveryManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Journal
name|logManager
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|boolean
name|restartOnError
decl_stmt|;
specifier|public
name|RecoveryManager
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Journal
name|log
parameter_list|,
name|boolean
name|restartOnError
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|logManager
operator|=
name|log
expr_stmt|;
name|this
operator|.
name|restartOnError
operator|=
name|restartOnError
expr_stmt|;
block|}
comment|/** 	 * Checks if the database is in a consistent state. If not, start a recovery run. 	 *  	 * The method scans the last log file and tries to find the last checkpoint 	 * record. If the checkpoint record is the last record in the file, 	 * the database was closed cleanly and is in a consistent state. If not, a 	 * recovery run is started beginning at the last checkpoint found. 	 *   	 * @throws LogException 	 */
specifier|public
name|boolean
name|recover
parameter_list|()
throws|throws
name|LogException
block|{
name|boolean
name|recoveryRun
init|=
literal|false
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
decl_stmt|;
try|try
init|(
specifier|final
name|Stream
argument_list|<
name|Path
argument_list|>
name|fileStream
init|=
name|logManager
operator|.
name|getFiles
argument_list|()
init|)
block|{
name|files
operator|=
name|fileStream
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
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
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Unable to find journal files in data dir"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
comment|// find the last log file in the data directory
specifier|final
name|int
name|lastNum
init|=
name|Journal
operator|.
name|findLastFile
argument_list|(
name|files
operator|.
name|stream
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|<
name|lastNum
condition|)
block|{
comment|// load the last log file
specifier|final
name|Path
name|last
init|=
name|logManager
operator|.
name|getFile
argument_list|(
name|lastNum
argument_list|)
decl_stmt|;
comment|// scan the last log file and record the last checkpoint found
specifier|final
name|JournalReader
name|reader
init|=
operator|new
name|JournalReader
argument_list|(
name|broker
argument_list|,
name|last
argument_list|,
name|lastNum
argument_list|)
decl_stmt|;
try|try
block|{
comment|// try to read the last log record to see if it is a checkpoint
name|boolean
name|checkpointFound
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|Loggable
name|lastLog
init|=
name|reader
operator|.
name|lastEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastLog
operator|!=
literal|null
operator|&&
name|lastLog
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|CHECKPOINT
condition|)
block|{
specifier|final
name|Checkpoint
name|checkpoint
init|=
operator|(
name|Checkpoint
operator|)
name|lastLog
decl_stmt|;
comment|// Found a checkpoint. To be sure it is indeed a valid checkpoint
comment|// record, we compare the LSN stored in it with the current LSN.
if|if
condition|(
name|checkpoint
operator|.
name|getStoredLsn
argument_list|()
operator|==
name|checkpoint
operator|.
name|getLsn
argument_list|()
condition|)
block|{
name|checkpointFound
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Database is in clean state. Last checkpoint: "
operator|+
name|checkpoint
operator|.
name|getDateString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|info
argument_list|(
literal|"Reading last journal log entry failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|". Will scan the log..."
argument_list|)
expr_stmt|;
comment|// if an exception occurs at this point, the journal file is probably incomplete,
comment|// which indicates a db crash
name|checkpointFound
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|checkpointFound
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unclean shutdown detected. Scanning journal..."
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|reportStatus
argument_list|(
literal|"Unclean shutdown detected. Scanning log..."
argument_list|)
expr_stmt|;
name|reader
operator|.
name|position
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Long2ObjectHashMap
argument_list|<
name|Loggable
argument_list|>
name|txnsStarted
init|=
operator|new
name|Long2ObjectHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Checkpoint
name|lastCheckpoint
init|=
literal|null
decl_stmt|;
name|long
name|lastLsn
init|=
name|Lsn
operator|.
name|LSN_INVALID
decl_stmt|;
name|Loggable
name|next
decl_stmt|;
try|try
block|{
specifier|final
name|ProgressBar
name|progress
init|=
operator|new
name|ProgressBar
argument_list|(
literal|"Scanning journal "
argument_list|,
name|FileUtils
operator|.
name|sizeQuietly
argument_list|(
name|last
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|reader
operator|.
name|nextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|//	                        LOG.debug(next.dump());
name|progress
operator|.
name|set
argument_list|(
name|Lsn
operator|.
name|getOffset
argument_list|(
name|next
operator|.
name|getLsn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_START
condition|)
block|{
comment|// new transaction starts: add it to the transactions table
name|txnsStarted
operator|.
name|put
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_ABORT
condition|)
block|{
comment|// transaction aborted: remove it from the transactions table
name|txnsStarted
operator|.
name|remove
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|CHECKPOINT
condition|)
block|{
name|txnsStarted
operator|.
name|clear
argument_list|()
expr_stmt|;
name|lastCheckpoint
operator|=
operator|(
name|Checkpoint
operator|)
name|next
expr_stmt|;
block|}
name|lastLsn
operator|=
name|next
operator|.
name|getLsn
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|LogException
name|e
parameter_list|)
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
literal|"Caught exception while reading log"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Last readable journal log entry lsn: "
operator|+
name|Lsn
operator|.
name|dump
argument_list|(
name|lastLsn
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// if the last checkpoint record is not the last record in the file
comment|// we need a recovery.
if|if
condition|(
operator|(
name|lastCheckpoint
operator|==
literal|null
operator|||
name|lastCheckpoint
operator|.
name|getLsn
argument_list|()
operator|!=
name|lastLsn
operator|)
operator|&&
name|txnsStarted
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Dirty transactions: "
operator|+
name|txnsStarted
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// starting recovery: reposition the log reader to the last checkpoint
if|if
condition|(
name|lastCheckpoint
operator|==
literal|null
condition|)
block|{
name|reader
operator|.
name|position
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|.
name|position
argument_list|(
name|lastCheckpoint
operator|.
name|getLsn
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|reader
operator|.
name|nextEntry
argument_list|()
expr_stmt|;
block|}
name|recoveryRun
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running recovery..."
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|reportStatus
argument_list|(
literal|"Running recovery..."
argument_list|)
expr_stmt|;
name|doRecovery
argument_list|(
name|txnsStarted
operator|.
name|size
argument_list|()
argument_list|,
name|last
argument_list|,
name|reader
argument_list|,
name|lastLsn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LogException
name|e
parameter_list|)
block|{
comment|// if restartOnError == true, we try to bring up the database even if there
comment|// are errors. Otherwise, an exception is thrown, which will stop the db initialization
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|reportStatus
argument_list|(
name|BrokerPool
operator|.
name|SIGNAL_ABORTED
argument_list|)
expr_stmt|;
if|if
condition|(
name|restartOnError
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Aborting recovery. eXist-db detected an error during recovery. This may not be fatal. Database will start up, but corruptions are likely."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Aborting recovery. eXist-db detected an error during recovery. This may not be fatal. Please consider running a consistency check via the export tool and create a backup if problems are reported. The db should come up again if you restart it."
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Database is in clean state. Nothing to recover from the journal."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// remove .log files from directory even if recovery failed.
comment|// Re-applying them on a second start up attempt would definitely damage the db, so we better
comment|// delete them before user tries to launch again.
name|cleanDirectory
argument_list|(
name|files
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|recoveryRun
condition|)
block|{
name|broker
operator|.
name|repairPrimary
argument_list|()
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|logManager
operator|.
name|setCurrentFileNum
argument_list|(
name|lastNum
argument_list|)
expr_stmt|;
name|logManager
operator|.
name|switchFiles
argument_list|()
expr_stmt|;
name|logManager
operator|.
name|clearBackupFiles
argument_list|()
expr_stmt|;
return|return
name|recoveryRun
return|;
block|}
comment|/**      * Called by {@link #recover()} to do the actual recovery.      *      * @param txnCount      * @param last      * @param reader      * @param lastLsn      *      * @throws LogException      */
specifier|private
name|void
name|doRecovery
parameter_list|(
specifier|final
name|int
name|txnCount
parameter_list|,
specifier|final
name|Path
name|last
parameter_list|,
specifier|final
name|JournalReader
name|reader
parameter_list|,
specifier|final
name|long
name|lastLsn
parameter_list|)
throws|throws
name|LogException
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running recovery ..."
argument_list|)
expr_stmt|;
block|}
name|logManager
operator|.
name|setInRecovery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
comment|// map to track running transactions
specifier|final
name|Long2ObjectHashMap
argument_list|<
name|Loggable
argument_list|>
name|runningTxns
init|=
operator|new
name|Long2ObjectHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// ------- REDO ---------
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"First pass: redoing "
operator|+
name|txnCount
operator|+
literal|" transactions..."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ProgressBar
name|progress
init|=
operator|new
name|ProgressBar
argument_list|(
literal|"Redo "
argument_list|,
name|FileUtils
operator|.
name|sizeQuietly
argument_list|(
name|last
argument_list|)
argument_list|)
decl_stmt|;
name|Loggable
name|next
init|=
literal|null
decl_stmt|;
name|int
name|redoCnt
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|next
operator|=
name|reader
operator|.
name|nextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|SanityCheck
operator|.
name|ASSERT
argument_list|(
name|next
operator|.
name|getLogType
argument_list|()
operator|!=
name|LogEntryTypes
operator|.
name|CHECKPOINT
argument_list|,
literal|"Found a checkpoint during recovery run! This should not ever happen."
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_START
condition|)
block|{
comment|// new transaction starts: add it to the transactions table
name|runningTxns
operator|.
name|put
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_COMMIT
condition|)
block|{
comment|// transaction committed: remove it from the transactions table
name|runningTxns
operator|.
name|remove
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|redoCnt
operator|++
expr_stmt|;
block|}
if|else if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_ABORT
condition|)
block|{
comment|// transaction aborted: remove it from the transactions table
name|runningTxns
operator|.
name|remove
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//            LOG.debug("Redo: " + next.dump());
comment|// redo the log entry
name|next
operator|.
name|redo
argument_list|()
expr_stmt|;
name|progress
operator|.
name|set
argument_list|(
name|Lsn
operator|.
name|getOffset
argument_list|(
name|next
operator|.
name|getLsn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getLsn
argument_list|()
operator|==
name|lastLsn
condition|)
block|{
break|break;
block|}
comment|// last readable entry reached. Stop here.
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception caught while redoing transactions. Aborting recovery to avoid possible damage. "
operator|+
literal|"Before starting again, make sure to run a check via the emergency export tool."
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Log entry that caused the exception: "
operator|+
name|next
operator|.
name|dump
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Recovery aborted. "
argument_list|)
throw|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Redo processed "
operator|+
name|redoCnt
operator|+
literal|" out of "
operator|+
name|txnCount
operator|+
literal|" transactions."
argument_list|)
expr_stmt|;
block|}
comment|// ------- UNDO ---------
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Second pass: undoing dirty transactions. Uncommitted transactions: "
operator|+
name|runningTxns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// see if there are uncommitted transactions pending
if|if
condition|(
name|runningTxns
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// do a reverse scan of the log, undoing all uncommitted transactions
try|try
block|{
while|while
condition|(
operator|(
name|next
operator|=
name|reader
operator|.
name|previousEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_START
condition|)
block|{
if|if
condition|(
name|runningTxns
operator|.
name|get
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|runningTxns
operator|.
name|remove
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|runningTxns
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// all dirty transactions undone
block|{
break|break;
block|}
block|}
block|}
if|else if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|TXN_COMMIT
condition|)
block|{
comment|// ignore already committed transaction
block|}
if|else if
condition|(
name|next
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|CHECKPOINT
condition|)
block|{
comment|// found last checkpoint: undo is completed
break|break;
block|}
comment|// undo the log entry if it belongs to an uncommitted transaction
if|if
condition|(
name|runningTxns
operator|.
name|get
argument_list|(
name|next
operator|.
name|getTransactionId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|//					LOG.debug("Undo: " + next.dump());
name|next
operator|.
name|undo
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception caught while undoing dirty transactions. Remaining transactions "
operator|+
literal|"to be undone: "
operator|+
name|runningTxns
operator|.
name|size
argument_list|()
operator|+
literal|". Aborting recovery to avoid possible damage. "
operator|+
literal|"Before starting again, make sure to run a check via the emergency export tool."
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log entry that caused the exception: "
operator|+
name|next
operator|.
name|dump
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Recovery aborted"
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR
argument_list|)
expr_stmt|;
name|logManager
operator|.
name|setInRecovery
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|cleanDirectory
parameter_list|(
specifier|final
name|Stream
argument_list|<
name|Path
argument_list|>
name|files
parameter_list|)
block|{
name|files
operator|.
name|forEach
argument_list|(
name|FileUtils
operator|::
name|deleteQuietly
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

