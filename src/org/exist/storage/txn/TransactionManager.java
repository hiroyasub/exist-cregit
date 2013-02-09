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
name|txn
package|;
end_package

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
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|SystemTask
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
name|SystemTaskManager
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
name|recovery
operator|.
name|RecoveryManager
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * This is the central entry point to the transaction management service.  *   * There's only one TransactionManager per database instance that can be  * retrieved via {@link BrokerPool#getTransactionManager()}. TransactionManager  * provides methods to create, commit and rollback a transaction.  *   * @author wolf  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"recovery"
argument_list|)
specifier|public
class|class
name|TransactionManager
block|{
specifier|public
specifier|final
specifier|static
name|String
name|RECOVERY_GROUP_COMMIT_ATTRIBUTE
init|=
literal|"group-commit"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_RECOVERY_GROUP_COMMIT
init|=
literal|"db-connection.recovery.group-commit"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RECOVERY_FORCE_RESTART_ATTRIBUTE
init|=
literal|"force-restart"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_RECOVERY_FORCE_RESTART
init|=
literal|"db-connection.recovery.force-restart"
decl_stmt|;
comment|/**      * Logger for this class      */
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
name|TransactionManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|nextTxnId
init|=
literal|0
decl_stmt|;
specifier|private
name|Journal
name|journal
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"enabled"
argument_list|)
specifier|private
name|boolean
name|enabled
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"group-commit"
argument_list|)
specifier|private
name|boolean
name|groupCommit
init|=
literal|false
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"force-restart"
argument_list|)
specifier|private
name|boolean
name|forceRestart
init|=
literal|false
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|TxnCounter
argument_list|>
name|transactions
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|TxnCounter
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/**      * Manages all system tasks      */
specifier|private
name|SystemTaskManager
name|taskManager
decl_stmt|;
comment|/**      * Initialize the transaction manager using the specified data directory.      *       * @param dataDir      * @throws EXistException      */
specifier|public
name|TransactionManager
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|File
name|dataDir
parameter_list|,
name|boolean
name|transactionsEnabled
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
name|enabled
operator|=
name|transactionsEnabled
expr_stmt|;
if|if
condition|(
name|enabled
condition|)
block|{
name|journal
operator|=
operator|new
name|Journal
argument_list|(
name|pool
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Boolean
name|groupOpt
init|=
operator|(
name|Boolean
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_RECOVERY_GROUP_COMMIT
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupOpt
operator|!=
literal|null
condition|)
block|{
name|groupCommit
operator|=
name|groupOpt
operator|.
name|booleanValue
argument_list|()
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
literal|"GroupCommits = "
operator|+
name|groupCommit
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Boolean
name|restartOpt
init|=
operator|(
name|Boolean
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_RECOVERY_FORCE_RESTART
argument_list|)
decl_stmt|;
if|if
condition|(
name|restartOpt
operator|!=
literal|null
condition|)
block|{
name|forceRestart
operator|=
name|restartOpt
operator|.
name|booleanValue
argument_list|()
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
literal|"ForceRestart = "
operator|+
name|forceRestart
argument_list|)
expr_stmt|;
block|}
block|}
name|taskManager
operator|=
operator|new
name|SystemTaskManager
argument_list|(
name|pool
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
if|if
condition|(
name|enabled
condition|)
block|{
name|journal
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
name|transactions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
comment|/**      * Run a database recovery if required. This method is called once during      * startup from {@link org.exist.storage.BrokerPool}.      *       * @param broker      * @throws EXistException      */
specifier|public
name|boolean
name|runRecovery
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
specifier|final
name|RecoveryManager
name|recovery
init|=
operator|new
name|RecoveryManager
argument_list|(
name|broker
argument_list|,
name|journal
argument_list|,
name|forceRestart
argument_list|)
decl_stmt|;
return|return
name|recovery
operator|.
name|recover
argument_list|()
return|;
block|}
comment|/**      * Create a new transaction. Creates a new transaction id that will      * be logged to disk immediately.       */
specifier|public
name|Txn
name|beginTransaction
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|RunWithLock
argument_list|<
name|Txn
argument_list|>
argument_list|()
block|{
specifier|public
name|Txn
name|execute
parameter_list|()
block|{
specifier|final
name|long
name|txnId
init|=
name|nextTxnId
operator|++
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Starting new transaction: "
operator|+
name|txnId
argument_list|)
expr_stmt|;
specifier|final
name|Txn
name|txn
init|=
operator|new
name|Txn
argument_list|(
name|txnId
argument_list|)
decl_stmt|;
try|try
block|{
name|journal
operator|.
name|writeToLog
argument_list|(
operator|new
name|TxnStart
argument_list|(
name|txnId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create transaction. Error writing to log file."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|transactions
operator|.
name|put
argument_list|(
name|txn
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|TxnCounter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|txn
return|;
block|}
block|}
operator|.
name|run
argument_list|()
return|;
block|}
comment|/**      * Commit a transaction.      *       * @param txn      * @throws TransactionException      */
specifier|public
name|void
name|commit
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
throws|throws
name|TransactionException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
operator|new
name|RunWithLock
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|execute
parameter_list|()
block|{
if|if
condition|(
name|enabled
condition|)
block|{
try|try
block|{
name|journal
operator|.
name|writeToLog
argument_list|(
operator|new
name|TxnCommit
argument_list|(
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"transaction manager caught exception while committing"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groupCommit
condition|)
block|{
name|journal
operator|.
name|flushToLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|txn
operator|.
name|signalCommit
argument_list|()
expr_stmt|;
name|txn
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
name|transactions
operator|.
name|remove
argument_list|(
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|processSystemTasks
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Committed transaction: "
operator|+
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|abort
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
operator|||
name|txn
operator|==
literal|null
condition|)
block|{
return|return;
block|}
operator|new
name|RunWithLock
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|execute
parameter_list|()
block|{
try|try
block|{
name|journal
operator|.
name|writeToLog
argument_list|(
operator|new
name|TxnAbort
argument_list|(
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to write abort record to journal: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groupCommit
condition|)
block|{
name|journal
operator|.
name|flushToLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|txn
operator|.
name|signalAbort
argument_list|()
expr_stmt|;
name|txn
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
name|transactions
operator|.
name|remove
argument_list|(
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|processSystemTasks
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/**      * Keep track of a new operation within the given transaction.      *      * @param txnId      */
specifier|public
name|void
name|trackOperation
parameter_list|(
name|long
name|txnId
parameter_list|)
block|{
specifier|final
name|TxnCounter
name|count
init|=
name|transactions
operator|.
name|get
argument_list|(
name|txnId
argument_list|)
decl_stmt|;
comment|// checkpoint operations do not create a transaction, so we have to check for null here
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|count
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Lock
name|getLock
parameter_list|()
block|{
return|return
name|lock
return|;
block|}
comment|/**      * Create a new checkpoint. A checkpoint fixes the current database state. All dirty pages      * are written to disk and the journal file is cleaned.      *       * This method is called from       * {@link org.exist.storage.BrokerPool} within pre-defined periods. It      * should not be called from somewhere else. The database needs to      * be in a stable state (all transactions completed, no operations running).      *       * @throws TransactionException      */
specifier|public
name|void
name|checkpoint
parameter_list|(
name|boolean
name|switchFiles
parameter_list|)
throws|throws
name|TransactionException
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return;
block|}
specifier|final
name|long
name|txnId
init|=
name|nextTxnId
operator|++
decl_stmt|;
name|journal
operator|.
name|checkpoint
argument_list|(
name|txnId
argument_list|,
name|switchFiles
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Journal
name|getJournal
parameter_list|()
block|{
return|return
name|journal
return|;
block|}
specifier|public
name|void
name|reindex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
specifier|final
name|Subject
name|currentUser
init|=
name|broker
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setSubject
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|reindexCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception during reindex: "
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
finally|finally
block|{
name|broker
operator|.
name|setSubject
argument_list|(
name|currentUser
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Shutting down transaction manager. Uncommitted transactions: "
operator|+
name|transactions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|uncommitted
init|=
name|uncommittedTransaction
argument_list|()
decl_stmt|;
name|shutdown
argument_list|(
name|uncommitted
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|(
name|boolean
name|checkpoint
parameter_list|)
block|{
if|if
condition|(
name|enabled
condition|)
block|{
specifier|final
name|long
name|txnId
init|=
name|nextTxnId
operator|++
decl_stmt|;
name|journal
operator|.
name|shutdown
argument_list|(
name|txnId
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
name|transactions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|uncommittedTransaction
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|transactions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|count
return|;
block|}
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|TxnCounter
argument_list|>
name|entry
range|:
name|transactions
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|counter
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Found an uncommitted transaction with id "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|". Pending operations: "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|counter
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"There are uncommitted transactions. A recovery run may be triggered upon restart."
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|public
name|void
name|triggerSystemTask
parameter_list|(
specifier|final
name|SystemTask
name|task
parameter_list|)
block|{
operator|new
name|RunWithLock
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|execute
parameter_list|()
block|{
name|taskManager
operator|.
name|triggerSystemTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|processSystemTasks
parameter_list|()
block|{
operator|new
name|RunWithLock
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|execute
parameter_list|()
block|{
if|if
condition|(
name|transactions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|taskManager
operator|.
name|processTasks
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|debug
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Active transactions: "
operator|+
name|transactions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Run code block with a lock on the transaction manager.      * Make sure locks are acquired in the right order.      *       * @author wolf      *      */
specifier|private
specifier|abstract
class|class
name|RunWithLock
parameter_list|<
name|T
parameter_list|>
block|{
specifier|public
name|T
name|run
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// we first need to get a broker for the current thread
comment|// before we acquire the transaction manager lock. Otherwise
comment|// a deadlock may occur.
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|execute
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Transaction manager failed to acquire broker for running system tasks"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|abstract
name|T
name|execute
parameter_list|()
function_decl|;
block|}
comment|/**      * Keep track of the number of operations processed within a transaction.      * This is used to determine if there are any uncommitted transactions      * during shutdown.      */
specifier|private
specifier|final
specifier|static
class|class
name|TxnCounter
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|increment
parameter_list|()
block|{
name|counter
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

