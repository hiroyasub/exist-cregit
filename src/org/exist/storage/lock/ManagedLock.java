begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

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
comment|/**  * Provides a simple wrapper around a Lock  * so that it may be used in a try-with-resources  * statement  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|ManagedLock
parameter_list|<
name|T
parameter_list|>
implements|implements
name|AutoCloseable
block|{
specifier|protected
specifier|final
name|T
name|lock
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|closer
decl_stmt|;
specifier|protected
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
name|ManagedLock
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
comment|/**      * Acquires and manages a lock with a specific mode      *      * @param lock The lock to call {@link Lock#acquire(Lock.LockMode)} on      * @param mode the mode of the lock      *      * @return A managed lock which will be released with {@link #close()}      */
specifier|public
specifier|static
name|ManagedLock
argument_list|<
name|Lock
argument_list|>
name|acquire
parameter_list|(
specifier|final
name|Lock
name|lock
parameter_list|,
specifier|final
name|Lock
operator|.
name|LockMode
name|mode
parameter_list|)
throws|throws
name|LockException
block|{
if|if
condition|(
operator|!
name|lock
operator|.
name|acquire
argument_list|(
name|mode
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to acquire lock"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ManagedLock
argument_list|<>
argument_list|(
name|lock
argument_list|,
parameter_list|()
lambda|->
name|lock
operator|.
name|release
argument_list|(
name|mode
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Attempts to acquire and manage a lock with a specific mode      *      * @param lock The lock to call {@link Lock#attempt(Lock.LockMode)} on      * @param mode the mode of the lock      *      * @return A managed lock which will be released with {@link #close()}      */
specifier|public
specifier|static
name|ManagedLock
argument_list|<
name|Lock
argument_list|>
name|attempt
parameter_list|(
specifier|final
name|Lock
name|lock
parameter_list|,
specifier|final
name|Lock
operator|.
name|LockMode
name|mode
parameter_list|)
throws|throws
name|LockException
block|{
if|if
condition|(
operator|!
name|lock
operator|.
name|attempt
argument_list|(
name|mode
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to attempt to acquire lock"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ManagedLock
argument_list|<>
argument_list|(
name|lock
argument_list|,
parameter_list|()
lambda|->
name|lock
operator|.
name|release
argument_list|(
name|mode
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Acquires and manages a lock with a specific mode      *      * @param lock The lock to call {@link java.util.concurrent.locks.Lock#lock()} on      * @param mode the mode of the lock      *      * @return A managed lock which will be released with {@link #close()}      */
specifier|public
specifier|static
name|ManagedLock
argument_list|<
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
argument_list|>
name|acquire
parameter_list|(
specifier|final
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
name|lock
parameter_list|,
specifier|final
name|Lock
operator|.
name|LockMode
name|mode
parameter_list|)
block|{
specifier|final
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
name|modeLock
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|READ_LOCK
case|:
name|modeLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
break|break;
case|case
name|WRITE_LOCK
case|:
name|modeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
name|modeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
operator|new
name|ManagedLock
argument_list|<>
argument_list|(
name|lock
argument_list|,
name|modeLock
operator|::
name|unlock
argument_list|)
return|;
block|}
comment|/**      * Attempts to acquire and manage a lock with a specific mode      *      * @param lock The lock to call {@link java.util.concurrent.locks.Lock#tryLock()} on      * @param mode the mode of the lock      *      * @return A managed lock which will be released with {@link #close()}      */
specifier|public
specifier|static
name|ManagedLock
argument_list|<
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
argument_list|>
name|attempt
parameter_list|(
specifier|final
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
name|lock
parameter_list|,
specifier|final
name|Lock
operator|.
name|LockMode
name|mode
parameter_list|)
throws|throws
name|LockException
block|{
specifier|final
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
name|modeLock
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|READ_LOCK
case|:
name|modeLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
break|break;
case|case
name|WRITE_LOCK
case|:
name|modeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
if|if
condition|(
operator|!
name|modeLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to attempt to acquire lock"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ManagedLock
argument_list|<>
argument_list|(
name|lock
argument_list|,
name|modeLock
operator|::
name|unlock
argument_list|)
return|;
block|}
comment|/**      * Acquires and manages a lock      *      * @param lock The lock to call {@link java.util.concurrent.locks.Lock#lock()} on      *      * @return A managed lock which will be released with {@link #close()}      */
specifier|public
specifier|static
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
name|acquire
parameter_list|(
specifier|final
name|ReentrantLock
name|lock
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
operator|new
name|ManagedLock
argument_list|<>
argument_list|(
name|lock
argument_list|,
name|lock
operator|::
name|unlock
argument_list|)
return|;
block|}
comment|/**      * Attempts to acquire and manage a lock      *      * @param lock The lock to call {@link java.util.concurrent.locks.Lock#tryLock()} on      *      * @return A managed lock which will be released with {@link #close()}      */
specifier|public
specifier|static
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
name|attempt
parameter_list|(
specifier|final
name|ReentrantLock
name|lock
parameter_list|)
throws|throws
name|LockException
block|{
if|if
condition|(
operator|!
name|lock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to attempt to acquire lock"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ManagedLock
argument_list|<>
argument_list|(
name|lock
argument_list|,
name|lock
operator|::
name|unlock
argument_list|)
return|;
block|}
comment|/**      * Determines if the lock has already been released      *      * @return true if the lock has already been released      */
name|boolean
name|isReleased
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/**      * Releases the lock      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closer
operator|.
name|run
argument_list|()
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

