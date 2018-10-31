begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|SupplierE
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
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
name|exist
operator|.
name|Transaction
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
name|Lock
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
name|Lock
operator|.
name|LockMode
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
name|LockManager
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
name|ManagedCollectionLock
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
name|ManagedDocumentLock
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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|Txn
implements|implements
name|Transaction
block|{
specifier|public
enum|enum
name|State
block|{
name|STARTED
block|,
name|ABORTED
block|,
name|COMMITTED
block|,
name|CLOSED
block|}
specifier|private
specifier|final
name|TransactionManager
name|tm
decl_stmt|;
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|LockInfo
argument_list|>
name|locksHeld
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|TxnListener
argument_list|>
name|listeners
decl_stmt|;
specifier|private
name|State
name|state
decl_stmt|;
specifier|private
name|String
name|originId
decl_stmt|;
specifier|public
name|Txn
parameter_list|(
name|TransactionManager
name|tm
parameter_list|,
name|long
name|transactionId
parameter_list|)
block|{
name|this
operator|.
name|tm
operator|=
name|tm
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|transactionId
expr_stmt|;
name|this
operator|.
name|locksHeld
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|listeners
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|State
operator|.
name|STARTED
expr_stmt|;
block|}
specifier|protected
name|Txn
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
block|{
name|this
operator|.
name|tm
operator|=
name|txn
operator|.
name|tm
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|txn
operator|.
name|id
expr_stmt|;
name|this
operator|.
name|locksHeld
operator|=
name|txn
operator|.
name|locksHeld
expr_stmt|;
name|this
operator|.
name|listeners
operator|=
name|txn
operator|.
name|listeners
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|txn
operator|.
name|state
expr_stmt|;
block|}
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|protected
name|void
name|setState
parameter_list|(
specifier|final
name|State
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|acquireLock
parameter_list|(
specifier|final
name|Lock
name|lock
parameter_list|,
specifier|final
name|LockMode
name|lockMode
parameter_list|)
throws|throws
name|LockException
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|lockMode
argument_list|)
expr_stmt|;
name|locksHeld
operator|.
name|add
argument_list|(
operator|new
name|LockInfo
argument_list|(
operator|new
name|Tuple2
argument_list|<>
argument_list|(
name|lock
argument_list|,
name|lockMode
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|lock
operator|.
name|release
argument_list|(
name|lockMode
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acquireCollectionLock
parameter_list|(
specifier|final
name|SupplierE
argument_list|<
name|ManagedCollectionLock
argument_list|,
name|LockException
argument_list|>
name|fnLockAcquire
parameter_list|)
throws|throws
name|LockException
block|{
specifier|final
name|ManagedCollectionLock
name|lock
init|=
name|fnLockAcquire
operator|.
name|get
argument_list|()
decl_stmt|;
name|locksHeld
operator|.
name|add
argument_list|(
operator|new
name|LockInfo
argument_list|(
name|lock
argument_list|,
name|lock
operator|::
name|close
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acquireDocumentLock
parameter_list|(
specifier|final
name|SupplierE
argument_list|<
name|ManagedDocumentLock
argument_list|,
name|LockException
argument_list|>
name|fnLockAcquire
parameter_list|)
throws|throws
name|LockException
block|{
specifier|final
name|ManagedDocumentLock
name|lock
init|=
name|fnLockAcquire
operator|.
name|get
argument_list|()
decl_stmt|;
name|locksHeld
operator|.
name|add
argument_list|(
operator|new
name|LockInfo
argument_list|(
name|lock
argument_list|,
name|lock
operator|::
name|close
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|releaseAll
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|locksHeld
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|LockInfo
name|info
init|=
name|locksHeld
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|info
operator|.
name|closer
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|locksHeld
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|registerListener
parameter_list|(
specifier|final
name|TxnListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|signalAbort
parameter_list|()
block|{
name|state
operator|=
name|State
operator|.
name|ABORTED
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|listeners
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|listeners
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|signalCommit
parameter_list|()
block|{
name|state
operator|=
name|State
operator|.
name|COMMITTED
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|listeners
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|listeners
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|LockInfo
parameter_list|<
name|T
parameter_list|>
block|{
specifier|final
name|T
name|lock
decl_stmt|;
specifier|final
name|Runnable
name|closer
decl_stmt|;
specifier|public
name|LockInfo
parameter_list|(
specifier|final
name|T
name|lock
parameter_list|,
specifier|final
name|Runnable
name|closer
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|closer
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|success
parameter_list|()
throws|throws
name|TransactionException
block|{
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|TransactionException
block|{
name|tm
operator|.
name|commit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|failure
parameter_list|()
block|{
name|abort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|tm
operator|.
name|abort
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|tm
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get origin of transaction      * @return Id      */
annotation|@
name|Deprecated
specifier|public
name|String
name|getOriginId
parameter_list|()
block|{
return|return
name|originId
return|;
block|}
comment|/**      *  Set origin of transaction. Purpose is to be able to      * see the origin of the transaction.      *      * @param id  Identifier of origin, FQN or URI.      */
annotation|@
name|Deprecated
specifier|public
name|void
name|setOriginId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|originId
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * A Txn that wraps an underlying transaction      * so that it can be reused as though it was      * a standard transaction.      */
specifier|public
specifier|static
class|class
name|ReusableTxn
extends|extends
name|Txn
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
name|ReusableTxn
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|State
name|reusableState
init|=
name|State
operator|.
name|STARTED
decl_stmt|;
specifier|private
specifier|final
name|Txn
name|underlyingTransaction
decl_stmt|;
specifier|public
name|ReusableTxn
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|)
block|{
name|super
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|this
operator|.
name|underlyingTransaction
operator|=
name|txn
expr_stmt|;
if|if
condition|(
name|txn
operator|.
name|state
operator|!=
name|State
operator|.
name|STARTED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Underlying transaction must be in STARTED state, but is in: "
operator|+
name|txn
operator|.
name|state
operator|+
literal|" state."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|this
operator|.
name|reusableState
operator|=
name|State
operator|.
name|ABORTED
expr_stmt|;
if|if
condition|(
name|underlyingTransaction
operator|.
name|state
operator|!=
name|State
operator|.
name|ABORTED
condition|)
block|{
name|super
operator|.
name|abort
argument_list|()
expr_stmt|;
name|this
operator|.
name|underlyingTransaction
operator|.
name|setState
argument_list|(
name|State
operator|.
name|ABORTED
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|TransactionException
block|{
name|this
operator|.
name|reusableState
operator|=
name|State
operator|.
name|COMMITTED
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|reusableState
operator|==
name|State
operator|.
name|STARTED
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
literal|"Transaction was not committed or aborted, auto aborting!"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|reusableState
operator|=
name|State
operator|.
name|ABORTED
expr_stmt|;
if|if
condition|(
name|underlyingTransaction
operator|.
name|state
operator|!=
name|State
operator|.
name|CLOSED
condition|)
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|underlyingTransaction
operator|.
name|setState
argument_list|(
name|State
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|reusableState
operator|=
name|State
operator|.
name|CLOSED
expr_stmt|;
block|}
if|else if
condition|(
name|reusableState
operator|==
name|State
operator|.
name|ABORTED
condition|)
block|{
name|this
operator|.
name|reusableState
operator|=
name|State
operator|.
name|CLOSED
expr_stmt|;
if|if
condition|(
name|underlyingTransaction
operator|.
name|state
operator|!=
name|State
operator|.
name|CLOSED
condition|)
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|underlyingTransaction
operator|.
name|setState
argument_list|(
name|State
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resetting transaction state for next use."
argument_list|)
expr_stmt|;
name|this
operator|.
name|reusableState
operator|=
name|State
operator|.
name|STARTED
expr_stmt|;
comment|//reset state for next commit/abort
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|releaseAll
parameter_list|()
block|{
if|if
condition|(
name|reusableState
operator|==
name|State
operator|.
name|ABORTED
condition|)
block|{
name|super
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//do nothing as when super#releaseAll is called
comment|//then the locks acquired on the real transaction are released
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You must only call releaseAll on the real underlying transaction"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

