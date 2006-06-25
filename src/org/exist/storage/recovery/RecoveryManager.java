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
name|recovery
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
name|IOException
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|RecoveryManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @uml.property name="logManager"      * @uml.associationEnd multiplicity="(1 1)"      */
specifier|private
name|Journal
name|logManager
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|RecoveryManager
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Journal
name|log
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
name|File
name|files
index|[]
init|=
name|logManager
operator|.
name|getFiles
argument_list|()
decl_stmt|;
comment|// find the last log file in the data directory
name|int
name|lastNum
init|=
name|Journal
operator|.
name|findLastFile
argument_list|(
name|files
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
name|File
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
operator|.
name|getLogType
argument_list|()
operator|==
name|LogEntryTypes
operator|.
name|CHECKPOINT
condition|)
block|{
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
if|if
condition|(
operator|!
name|checkpointFound
condition|)
block|{
name|reader
operator|.
name|position
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Long2ObjectHashMap
name|txnsStarted
init|=
operator|new
name|Long2ObjectHashMap
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
name|ProgressBar
name|progress
init|=
operator|new
name|ProgressBar
argument_list|(
literal|"Scanning journal "
argument_list|,
name|last
operator|.
name|length
argument_list|()
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
comment|//                        LOG.debug(next.dump());
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
name|LogException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Last readable log entry lsn: "
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
name|debug
argument_list|(
literal|"Found dirty transactions: "
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
name|reader
operator|.
name|position
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
name|doRecovery
argument_list|(
name|last
argument_list|,
name|reader
argument_list|,
name|lastLsn
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Database is in clean state."
argument_list|)
expr_stmt|;
block|}
name|cleanDirectory
argument_list|(
name|files
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
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
return|return
name|recoveryRun
return|;
block|}
comment|/**      * Called by {@link #recover()} to do the actual recovery.      *       * @param reader      * @param lastLsn      * @throws LogException      */
specifier|private
name|void
name|doRecovery
parameter_list|(
name|File
name|last
parameter_list|,
name|JournalReader
name|reader
parameter_list|,
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
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running recovery ..."
argument_list|)
expr_stmt|;
name|logManager
operator|.
name|setInRecovery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// map to track running transactions
name|Long2ObjectHashMap
name|runningTxns
init|=
operator|new
name|Long2ObjectHashMap
argument_list|()
decl_stmt|;
comment|// ------- REDO ---------
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"First pass: redoing operations"
argument_list|)
expr_stmt|;
name|ProgressBar
name|progress
init|=
operator|new
name|ProgressBar
argument_list|(
literal|"Redo "
argument_list|,
name|last
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|Loggable
name|next
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
break|break;
comment|// last readable entry reached. Stop here.
block|}
comment|// ------- UNDO ---------
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Second pass: undoing dirty transactions. Uncommitted transactions: "
operator|+
name|runningTxns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
break|break;
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
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR_SYNC
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
specifier|private
name|void
name|cleanDirectory
parameter_list|(
name|File
index|[]
name|files
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

