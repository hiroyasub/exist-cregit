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
name|util
operator|.
name|LockException
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
block|}
empty_stmt|;
specifier|private
name|long
name|id
decl_stmt|;
specifier|private
name|State
name|state
decl_stmt|;
specifier|private
name|List
argument_list|<
name|LockInfo
argument_list|>
name|locksHeld
init|=
operator|new
name|ArrayList
argument_list|<
name|LockInfo
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|TxnListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|TxnListener
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|originId
decl_stmt|;
specifier|private
name|TransactionManager
name|tm
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
name|state
operator|=
name|State
operator|.
name|STARTED
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
name|registerLock
parameter_list|(
name|Lock
name|lock
parameter_list|,
name|int
name|lockMode
parameter_list|)
block|{
name|locksHeld
operator|.
name|add
argument_list|(
operator|new
name|LockInfo
argument_list|(
name|lock
argument_list|,
name|lockMode
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acquireLock
parameter_list|(
name|Lock
name|lock
parameter_list|,
name|int
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
name|lock
argument_list|,
name|lockMode
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
name|lock
operator|.
name|release
argument_list|(
name|info
operator|.
name|lockMode
argument_list|)
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
class|class
name|LockInfo
block|{
name|Lock
name|lock
decl_stmt|;
name|int
name|lockMode
decl_stmt|;
specifier|public
name|LockInfo
parameter_list|(
name|Lock
name|lock
parameter_list|,
name|int
name|lockMode
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
name|lockMode
operator|=
name|lockMode
expr_stmt|;
block|}
block|}
comment|/**      * Get origin of transaction      * @return Id      */
specifier|public
name|String
name|getOriginId
parameter_list|()
block|{
return|return
name|originId
return|;
block|}
comment|/**      *  Set origin of transaction. Purpose is to be able to       * see the origin of the transaction.      *       * @param id  Identifier of origin, FQN or URI.      */
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
specifier|public
name|void
name|success
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
specifier|public
name|void
name|failure
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
block|}
end_class

end_unit

