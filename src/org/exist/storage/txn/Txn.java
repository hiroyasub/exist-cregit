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
name|java
operator|.
name|util
operator|.
name|Stack
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
block|{
specifier|private
name|long
name|id
decl_stmt|;
specifier|private
name|List
name|locksHeld
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|Txn
parameter_list|(
name|long
name|transactionId
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|transactionId
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
block|}
specifier|public
name|void
name|registerLock
parameter_list|(
name|Lock
name|lock
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
name|Lock
operator|.
name|READ_LOCK
argument_list|)
argument_list|)
expr_stmt|;
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
name|releaseLock
parameter_list|(
name|Lock
name|lock
parameter_list|)
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|releaseAll
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Locks: "
operator|+
name|locksHeld
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
operator|>
operator|-
literal|1
condition|;
name|i
operator|--
control|)
block|{
name|LockInfo
name|info
init|=
operator|(
name|LockInfo
operator|)
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
block|}
end_class

end_unit

