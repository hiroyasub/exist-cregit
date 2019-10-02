begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|ThreadSafe
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
name|storage
operator|.
name|*
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
name|JournalException
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
name|JournalManager
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
name|util
operator|.
name|LockException
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicInteger
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * The Transaction Manager provides methods to begin, commit, and abort  * transactions.  *  * This implementation of the transaction manager is non-blocking lock-free.  * It makes use of several CAS variables to ensure thread-safe concurrent  * access. The most important of which is {@link #state} which indicates  * either:  *     1) the number of active transactions  *     2) that the Transaction Manager is executing system  *         tasks ({@link #STATE_SYSTEM}), during which time no  *         other transactions are active.  *     3) that the Transaction Manager has (or is)  *         been shutdown ({@link #STATE_SHUTDOWN}).  *  * NOTE: the Transaction Manager may optimistically briefly enter  *     the state {@link #STATE_SYSTEM} to block the initiation of  *     new transactions and then NOT execute system tasks if it  *     detects concurrent active transactions.  *  * System tasks are mutually exclusive with any other operation  * including shutdown. When shutdown is requested, if system tasks  * are executing, then the thread will spin until they are finished.  *   * There's only one TransactionManager per database instance, it can be  * accessed via {@link BrokerPool#getTransactionManager()}.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  * @author wolf  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|TransactionManager
implements|implements
name|BrokerPoolService
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
name|TransactionManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|JournalManager
argument_list|>
name|journalManager
decl_stmt|;
specifier|private
specifier|final
name|SystemTaskManager
name|systemTaskManager
decl_stmt|;
comment|/**      * The next transaction id      */
specifier|private
specifier|final
name|AtomicLong
name|nextTxnId
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/**      * Currently active transactions and their operations journal write count.      *  Key is the transaction id      *  Value is the transaction's operations journal write count.      */
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|TxnCounter
argument_list|>
name|transactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * State for when the Transaction Manager has been shutdown.      */
specifier|private
specifier|static
specifier|final
name|int
name|STATE_SHUTDOWN
init|=
operator|-
literal|2
decl_stmt|;
comment|/**      * State for when the Transaction Manager has executing system tasks.      */
specifier|private
specifier|static
specifier|final
name|int
name|STATE_SYSTEM
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * State for when the Transaction Manager is idle, i.e. no active transactions.      */
specifier|private
specifier|static
specifier|final
name|int
name|STATE_IDLE
init|=
literal|0
decl_stmt|;
comment|/**      * State of the transaction manager.      *      * Will be either {@link #STATE_SHUTDOWN}, {@link #STATE_SYSTEM},      * {@link #STATE_IDLE} or a non-zero positive integer which      * indicates the number of active transactions.      */
specifier|private
specifier|final
name|AtomicInteger
name|state
init|=
operator|new
name|AtomicInteger
argument_list|(
name|STATE_IDLE
argument_list|)
decl_stmt|;
comment|/**      * Id of the thread which is executing system tasks when      * the {@link #state} == {@link #STATE_SYSTEM}. This      * is used for reentrancy when system tasks need to      * make transactional operations.      */
specifier|private
specifier|final
name|AtomicLong
name|systemThreadId
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|/**      * Constructs a transaction manager for a Broker Pool.      *       * @param pool the broker pool      * @param journalManager the journal manager      * @param systemTaskManager the system task manager      */
specifier|public
name|TransactionManager
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|JournalManager
argument_list|>
name|journalManager
parameter_list|,
specifier|final
name|SystemTaskManager
name|systemTaskManager
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|journalManager
operator|=
name|journalManager
expr_stmt|;
name|this
operator|.
name|systemTaskManager
operator|=
name|systemTaskManager
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|throwShutdownException
parameter_list|()
block|{
comment|//TODO(AR) API should be revised in future so that this is a TransactionException
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Transaction Manager is shutdown"
argument_list|)
throw|;
block|}
comment|/**      * Create a new transaction.      *      * @return the new transaction      */
specifier|public
name|Txn
name|beginTransaction
parameter_list|()
block|{
try|try
block|{
comment|// CAS loop
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|localState
init|=
name|state
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// can NOT begin transaction when shutdown!
if|if
condition|(
name|localState
operator|==
name|STATE_SHUTDOWN
condition|)
block|{
name|throwShutdownException
argument_list|()
expr_stmt|;
block|}
comment|// must NOT begin transaction when another thread is processing system tasks!
if|if
condition|(
name|localState
operator|==
name|STATE_SYSTEM
condition|)
block|{
specifier|final
name|long
name|thisThreadId
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|systemThreadId
operator|.
name|compareAndSet
argument_list|(
name|thisThreadId
argument_list|,
name|thisThreadId
argument_list|)
condition|)
block|{
comment|// our thread is executing system tasks, allow reentrancy from our thread!
comment|// done... return from CAS loop!
return|return
name|doBeginTransaction
argument_list|()
return|;
block|}
else|else
block|{
comment|// spin whilst another thread executes the system tasks
comment|// sleep a small time to save CPU
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// if we are operational and are not preempted by another thread, begin transaction
if|if
condition|(
name|localState
operator|>=
name|STATE_IDLE
operator|&&
name|state
operator|.
name|compareAndSet
argument_list|(
name|localState
argument_list|,
name|localState
operator|+
literal|1
argument_list|)
condition|)
block|{
comment|// done... return from CAS loop!
return|return
name|doBeginTransaction
argument_list|()
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// thrown by Thread.sleep
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|//TODO(AR) API should be revised in future so that this is a TransactionException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Txn
name|doBeginTransaction
parameter_list|()
block|{
specifier|final
name|long
name|txnId
init|=
name|nextTxnId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|journalManager
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
name|journalManager
operator|.
name|get
argument_list|()
operator|.
name|journal
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
name|JournalException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create transaction. Error writing to Journal"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*          * NOTE: we intentionally increment the txn counter here          *     to set the counter to 1 to represent the TxnStart,          *     as that will not be done          *     by {@link JournalManager#journal(Loggable)} or          *     {@link Journal#writeToLog(loggable)}.          */
name|transactions
operator|.
name|put
argument_list|(
name|txnId
argument_list|,
operator|new
name|TxnCounter
argument_list|()
operator|.
name|increment
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Txn
name|txn
init|=
operator|new
name|Txn
argument_list|(
name|this
argument_list|,
name|txnId
argument_list|)
decl_stmt|;
comment|// TODO(AR) ultimately we should be doing away with DBBroker#addCurrentTransaction
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|getBroker
argument_list|()
init|)
block|{
name|broker
operator|.
name|addCurrentTransaction
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ee
argument_list|)
throw|;
block|}
return|return
name|txn
return|;
block|}
comment|/**      * Commit a transaction.      *       * @param txn the transaction to commit.      *      * @throws TransactionException if the transaction could not be committed.      */
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|txn
operator|instanceof
name|Txn
operator|.
name|ReusableTxn
condition|)
block|{
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return;
comment|//throw new IllegalStateException("Commit should be called on the transaction and not via the TransactionManager"); //TODO(AR) remove later when API is cleaned up?
block|}
comment|//we can only commit something which is in the STARTED state
if|if
condition|(
name|txn
operator|.
name|getState
argument_list|()
operator|!=
name|Txn
operator|.
name|State
operator|.
name|STARTED
condition|)
block|{
return|return;
block|}
comment|// CAS loop
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|localState
init|=
name|state
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// can NOT commit transaction when shutdown!
if|if
condition|(
name|localState
operator|==
name|STATE_SHUTDOWN
condition|)
block|{
name|throwShutdownException
argument_list|()
expr_stmt|;
block|}
comment|// must NOT commit transaction when another thread is processing system tasks!
if|if
condition|(
name|localState
operator|==
name|STATE_SYSTEM
condition|)
block|{
specifier|final
name|long
name|thisThreadId
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|systemThreadId
operator|.
name|compareAndSet
argument_list|(
name|thisThreadId
argument_list|,
name|thisThreadId
argument_list|)
condition|)
block|{
comment|// our thread is executing system tasks, allow reentrancy from our thread!
name|doCommitTransaction
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|// done... exit CAS loop!
return|return;
block|}
else|else
block|{
comment|// spin whilst another thread executes the system tasks
comment|// sleep a small time to save CPU
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// if we are have active transactions and are not preempted by another thread, commit transaction
if|if
condition|(
name|localState
operator|>
name|STATE_IDLE
operator|&&
name|state
operator|.
name|compareAndSet
argument_list|(
name|localState
argument_list|,
name|localState
operator|-
literal|1
argument_list|)
condition|)
block|{
name|doCommitTransaction
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|// done... exit CAS loop!
return|return;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// thrown by Thread.sleep
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|//TODO(AR) API should be revised in future so that this is a TransactionException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|doCommitTransaction
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
name|journalManager
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
name|journalManager
operator|.
name|get
argument_list|()
operator|.
name|journalGroup
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
name|JournalException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransactionException
argument_list|(
literal|"Failed to write commit record to journal: "
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
literal|"Committed transaction: "
operator|+
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Abort a transaction.      *      * @param txn the transaction to abort.      */
specifier|public
name|void
name|abort
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|//we can only abort something which is in the STARTED state
if|if
condition|(
name|txn
operator|.
name|getState
argument_list|()
operator|!=
name|Txn
operator|.
name|State
operator|.
name|STARTED
condition|)
block|{
return|return;
block|}
comment|// CAS loop
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|localState
init|=
name|state
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// can NOT abort transaction when shutdown!
if|if
condition|(
name|localState
operator|==
name|STATE_SHUTDOWN
condition|)
block|{
name|throwShutdownException
argument_list|()
expr_stmt|;
block|}
comment|// must NOT abort transaction when another thread is processing system tasks!
if|if
condition|(
name|localState
operator|==
name|STATE_SYSTEM
condition|)
block|{
specifier|final
name|long
name|thisThreadId
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|systemThreadId
operator|.
name|compareAndSet
argument_list|(
name|thisThreadId
argument_list|,
name|thisThreadId
argument_list|)
condition|)
block|{
comment|// our thread is executing system tasks, allow reentrancy from our thread!
name|doAbortTransaction
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|// done... exit CAS loop!
return|return;
block|}
else|else
block|{
comment|// spin whilst another thread executes the system tasks
comment|// sleep a small time to save CPU
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// if we are have active transactions and are not preempted by another thread, abort transaction
if|if
condition|(
name|localState
operator|>
name|STATE_IDLE
operator|&&
name|state
operator|.
name|compareAndSet
argument_list|(
name|localState
argument_list|,
name|localState
operator|-
literal|1
argument_list|)
condition|)
block|{
name|doAbortTransaction
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|// done... exit CAS loop!
return|return;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// thrown by Thread.sleep
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|//TODO(AR) API should be revised in future so that this is a TransactionException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|doAbortTransaction
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
block|{
if|if
condition|(
name|journalManager
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
name|journalManager
operator|.
name|get
argument_list|()
operator|.
name|journalGroup
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
name|JournalException
name|e
parameter_list|)
block|{
comment|//TODO(AR) should revise the API in future to throw TransactionException
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to write abort record to journal: "
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
literal|"Aborted transaction: "
operator|+
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Close the transaction.      *      * Ensures that the transaction has either been committed or aborted.      *      * @param txn the transaction to close      */
specifier|public
name|void
name|close
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|//if the transaction is already closed, do nothing
if|if
condition|(
name|txn
operator|.
name|getState
argument_list|()
operator|==
name|Txn
operator|.
name|State
operator|.
name|CLOSED
condition|)
block|{
return|return;
block|}
try|try
block|{
comment|//if the transaction is started, then we should auto-abort the uncommitted transaction
if|if
condition|(
name|txn
operator|.
name|getState
argument_list|()
operator|==
name|Txn
operator|.
name|State
operator|.
name|STARTED
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Transaction was not committed or aborted, auto aborting!"
argument_list|)
expr_stmt|;
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
comment|// TODO(AR) ultimately we should be doing away with DBBroker#addCurrentTransaction
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|getBroker
argument_list|()
init|)
block|{
name|broker
operator|.
name|removeCurrentTransaction
argument_list|(
name|txn
operator|instanceof
name|Txn
operator|.
name|ReusableTxn
condition|?
operator|(
operator|(
name|Txn
operator|.
name|ReusableTxn
operator|)
name|txn
operator|)
operator|.
name|getUnderlyingTransaction
argument_list|()
else|:
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ee
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|txn
operator|.
name|setState
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
comment|//transaction is now closed!
block|}
name|processSystemTasks
argument_list|()
expr_stmt|;
block|}
comment|/**      * Keep track of a new operation within the given transaction.      *      * @param txnId the transaction id.      */
specifier|public
name|void
name|trackOperation
parameter_list|(
specifier|final
name|long
name|txnId
parameter_list|)
block|{
name|transactions
operator|.
name|get
argument_list|(
name|txnId
argument_list|)
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
comment|/**      * Create a new checkpoint. A checkpoint fixes the current database state. All dirty pages      * are written to disk and the journal file is cleaned.      *      * This method is called from      * {@link org.exist.storage.BrokerPool#sync(DBBroker, Sync)} within pre-defined periods. It      * should not be called from somewhere else. The database needs to      * be in a stable state (all transactions completed, no operations running).      *      * @param switchFiles Indicates whether a new journal file should be started      *      * @throws TransactionException if an error occurs whilst writing the checkpoint.      */
specifier|public
name|void
name|checkpoint
parameter_list|(
specifier|final
name|boolean
name|switchFiles
parameter_list|)
throws|throws
name|TransactionException
block|{
if|if
condition|(
name|state
operator|.
name|get
argument_list|()
operator|==
name|STATE_SHUTDOWN
condition|)
block|{
name|throwShutdownException
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|journalManager
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|long
name|txnId
init|=
name|nextTxnId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|journalManager
operator|.
name|get
argument_list|()
operator|.
name|checkpoint
argument_list|(
name|txnId
argument_list|,
name|switchFiles
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
throw|throw
operator|new
name|TransactionException
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
block|}
comment|/**      * @deprecated This mixes concerns and should not be here!      * @param broker the  eXist-db DBBroker      * @throws IOException in response to an I/O error      */
annotation|@
name|Deprecated
specifier|public
name|void
name|reindex
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
name|broker
operator|.
name|pushSubject
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
init|(
specifier|final
name|Txn
name|transaction
init|=
name|beginTransaction
argument_list|()
init|)
block|{
name|broker
operator|.
name|reindexCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|LockException
decl||
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
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
name|popSubject
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|localState
init|=
name|state
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|STATE_SHUTDOWN
condition|)
block|{
comment|// already shutdown!
return|return;
block|}
comment|// can NOT shutdown whilst system tasks are executing
if|if
condition|(
name|localState
operator|==
name|STATE_SYSTEM
condition|)
block|{
comment|// spin whilst another thread executes the system tasks
comment|// sleep a small time to save CPU
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|state
operator|.
name|compareAndSet
argument_list|(
name|localState
argument_list|,
name|STATE_SHUTDOWN
argument_list|)
condition|)
block|{
comment|// CAS above guarantees that only a single thread will ever enter this block once!
specifier|final
name|int
name|uncommitted
init|=
name|uncommittedTransaction
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|checkpoint
init|=
name|uncommitted
operator|==
literal|0
decl_stmt|;
specifier|final
name|long
name|txnId
init|=
name|nextTxnId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|journalManager
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|journalManager
operator|.
name|get
argument_list|()
operator|.
name|shutdown
argument_list|(
name|txnId
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
name|transactions
operator|.
name|clear
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
literal|"Shutting down transaction manager. Uncommitted transactions: "
operator|+
name|transactions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// done... exit CAS loop!
return|return;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// thrown by Thread.sleep
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|int
name|uncommittedTransaction
parameter_list|()
block|{
specifier|final
name|Integer
name|uncommittedCount
init|=
name|transactions
operator|.
name|reduce
argument_list|(
literal|1000
argument_list|,
parameter_list|(
name|txnId
parameter_list|,
name|txnCounter
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|txnCounter
operator|.
name|getCount
argument_list|()
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
name|txnId
operator|+
literal|". Pending operations: "
operator|+
name|txnCounter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
argument_list|,
parameter_list|(
name|a
parameter_list|,
name|b
parameter_list|)
lambda|->
name|a
operator|+
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|uncommittedCount
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|uncommittedCount
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
name|uncommittedCount
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
name|systemTaskManager
operator|.
name|addSystemTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|processSystemTasks
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|processSystemTasks
parameter_list|()
block|{
if|if
condition|(
name|state
operator|.
name|get
argument_list|()
operator|!=
name|STATE_IDLE
condition|)
block|{
comment|// avoids taking a broker below if it is not needed
return|return;
block|}
try|try
init|(
specifier|final
name|DBBroker
name|systemBroker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
comment|// no new transactions can begin, commit, or abort whilst processing system tasks
comment|// only process system tasks if there are no active transactions, i.e. the state == IDLE
if|if
condition|(
name|state
operator|.
name|compareAndSet
argument_list|(
name|STATE_IDLE
argument_list|,
name|STATE_SYSTEM
argument_list|)
condition|)
block|{
comment|// CAS above guarantees that only a single thread will ever enter this block at once
try|try
block|{
name|this
operator|.
name|systemThreadId
operator|.
name|set
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// we have to check that `transactions` is empty
comment|// otherwise we might be in SYSTEM state but `abort` or `commit`
comment|// functions are still finishing
if|if
condition|(
name|transactions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|beginTransaction
argument_list|()
init|)
block|{
name|systemTaskManager
operator|.
name|processTasks
argument_list|(
name|systemBroker
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|systemThreadId
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// restore IDLE state
name|state
operator|.
name|set
argument_list|(
name|STATE_IDLE
argument_list|)
expr_stmt|;
block|}
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
name|error
argument_list|(
literal|"Unable to process system tasks: "
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
comment|/**      * Keep track of the number of operations processed within a transaction.      * This is used to determine if there are any uncommitted transactions      * during shutdown.      */
specifier|private
specifier|static
specifier|final
class|class
name|TxnCounter
block|{
comment|/**          * The counter variable is declared volatile as it is only ever          * written from one thread (via {@link #increment()} which is          * the `transaction` for which it is maintaining a count, whilst          * it is read from (potentially) a different thread          * (via {@link #getCount()} when {@link TransactionManager#shutdown()}          * calls {@link TransactionManager#uncommittedTransaction()}.          */
specifier|private
specifier|volatile
name|long
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|TxnCounter
name|increment
parameter_list|()
block|{
name|counter
operator|++
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|counter
return|;
block|}
block|}
block|}
end_class

end_unit

