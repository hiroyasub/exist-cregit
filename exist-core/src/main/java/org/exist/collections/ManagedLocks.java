begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
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
name|lock
operator|.
name|ManagedLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * Simple container for a List of ManagedLocks  * which allows ARM (Automatic Resource Management)  * via {@link AutoCloseable}  *  * Locks will be released in the reverse order to which they  * are provided  */
end_comment

begin_class
specifier|public
class|class
name|ManagedLocks
parameter_list|<
name|T
extends|extends
name|ManagedLock
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|T
argument_list|>
implements|,
name|AutoCloseable
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
name|ManagedLocks
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|managedLocks
decl_stmt|;
comment|/**      * @param managedLocks A list of ManagedLocks which should      *   be in the same order that they were acquired      */
specifier|public
name|ManagedLocks
parameter_list|(
specifier|final
name|java
operator|.
name|util
operator|.
name|List
argument_list|<
name|T
argument_list|>
name|managedLocks
parameter_list|)
block|{
name|this
operator|.
name|managedLocks
operator|=
name|managedLocks
expr_stmt|;
block|}
comment|/**      * @param managedLocks An array / var-args of ManagedLocks      *   which should be in the same order that they were acquired      */
specifier|public
name|ManagedLocks
parameter_list|(
specifier|final
name|T
modifier|...
name|managedLocks
parameter_list|)
block|{
name|this
operator|.
name|managedLocks
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|managedLocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ManagedLockIterator
argument_list|()
return|;
block|}
specifier|private
class|class
name|ManagedLockIterator
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
init|=
name|managedLocks
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closeAll
argument_list|(
name|managedLocks
argument_list|)
expr_stmt|;
block|}
comment|/**      * Closes all the locks in the provided list.      *      * Locks will be closed in reverse (acquisition) order.      *      * If a {@link RuntimeException} occurs when closing      * any lock. The first exception will be recorded and      * lock closing will continue. After all locks are closed      * the first encountered exception is rethrown.      *      * @param<T> The type of the ManagedLocks      * @param managedLocks A list of locks, the list should be ordered in lock acquisition order.      */
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ManagedLock
parameter_list|>
name|void
name|closeAll
parameter_list|(
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|managedLocks
parameter_list|)
block|{
name|RuntimeException
name|firstException
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|managedLocks
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
name|T
name|managedLock
init|=
name|managedLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|managedLock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstException
operator|==
literal|null
condition|)
block|{
name|firstException
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|firstException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|firstException
throw|;
block|}
block|}
block|}
end_class

end_unit

