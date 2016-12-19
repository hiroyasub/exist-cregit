begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2005-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  * File: ReentrantLock.java  *  * Originally written by Doug Lea and released into the public domain.  * This may be used for any purposes whatsoever without acknowledgment.  * Thanks for the assistance and support of Sun Microsystems Labs,  * and everyone contributing, testing, and using this code.  *  * $Id$  * */
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|util
operator|.
name|LockException
import|;
end_import

begin_comment
comment|/**  * A lock with the same semantics as builtin  * Java synchronized locks: Once a thread has a lock, it  * can re-obtain it any number of times without blocking.  * The lock is made available to other threads when  * as many releases as acquires have occurred.  *   * The lock has a timeout: a read lock will be released if the  * timeout is reached. */
end_comment

begin_class
specifier|public
class|class
name|ReentrantReadWriteLock
implements|implements
name|Lock
block|{
specifier|private
specifier|static
specifier|final
name|int
name|WAIT_CHECK_PERIOD
init|=
literal|200
decl_stmt|;
specifier|private
specifier|static
class|class
name|SuspendedWaiter
block|{
specifier|final
name|Thread
name|thread
decl_stmt|;
specifier|final
name|LockMode
name|lockMode
decl_stmt|;
specifier|final
name|int
name|lockCount
decl_stmt|;
specifier|public
name|SuspendedWaiter
parameter_list|(
specifier|final
name|Thread
name|thread
parameter_list|,
specifier|final
name|LockMode
name|lockMode
parameter_list|,
specifier|final
name|int
name|lockCount
parameter_list|)
block|{
name|this
operator|.
name|thread
operator|=
name|thread
expr_stmt|;
name|this
operator|.
name|lockMode
operator|=
name|lockMode
expr_stmt|;
name|this
operator|.
name|lockCount
operator|=
name|lockCount
expr_stmt|;
block|}
block|}
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
name|ReentrantReadWriteLock
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Object
name|id_
decl_stmt|;
specifier|private
name|Thread
name|owner_
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|SuspendedWaiter
argument_list|>
name|suspendedThreads
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|holds_
init|=
literal|0
decl_stmt|;
specifier|private
name|LockMode
name|mode_
init|=
name|LockMode
operator|.
name|NO_LOCK
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|LockMode
argument_list|>
name|modeStack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|writeLocks
init|=
literal|0
decl_stmt|;
specifier|private
name|LockListener
name|listener
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|StackTraceElement
index|[]
argument_list|>
name|seStack
decl_stmt|;
specifier|public
name|ReentrantReadWriteLock
parameter_list|(
specifier|final
name|Object
name|id
parameter_list|)
block|{
name|this
operator|.
name|id_
operator|=
name|id
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|seStack
operator|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|seStack
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id_
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acquire
parameter_list|()
throws|throws
name|LockException
block|{
return|return
name|acquire
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acquire
parameter_list|(
specifier|final
name|LockMode
name|mode
parameter_list|)
throws|throws
name|LockException
block|{
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|NO_LOCK
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"acquired with no lock !"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|()
throw|;
block|}
specifier|final
name|Thread
name|caller
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|WaitingThread
name|waitingOnResource
decl_stmt|;
if|if
condition|(
name|caller
operator|==
name|owner_
condition|)
block|{
operator|++
name|holds_
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|seStack
operator|.
name|push
argument_list|(
name|t
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|owner_
operator|==
literal|null
condition|)
block|{
name|owner_
operator|=
name|caller
expr_stmt|;
name|holds_
operator|=
literal|1
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|seStack
operator|.
name|push
argument_list|(
name|t
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
operator|(
name|waitingOnResource
operator|=
name|DeadlockDetection
operator|.
name|deadlockCheckResource
argument_list|(
name|caller
argument_list|,
name|owner_
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|waitingOnResource
operator|.
name|suspendWaiting
argument_list|()
expr_stmt|;
specifier|final
name|SuspendedWaiter
name|suspended
init|=
operator|new
name|SuspendedWaiter
argument_list|(
name|owner_
argument_list|,
name|mode_
argument_list|,
name|holds_
argument_list|)
decl_stmt|;
name|suspendedThreads
operator|.
name|push
argument_list|(
name|suspended
argument_list|)
expr_stmt|;
name|owner_
operator|=
name|caller
expr_stmt|;
name|holds_
operator|=
literal|1
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
name|listener
operator|=
name|waitingOnResource
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|DeadlockDetection
operator|.
name|addCollectionWaiter
argument_list|(
name|caller
argument_list|,
name|this
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|wait
argument_list|(
name|WAIT_CHECK_PERIOD
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|waitingOnResource
operator|=
name|DeadlockDetection
operator|.
name|deadlockCheckResource
argument_list|(
name|caller
argument_list|,
name|owner_
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|waitingOnResource
operator|.
name|suspendWaiting
argument_list|()
expr_stmt|;
specifier|final
name|SuspendedWaiter
name|suspended
init|=
operator|new
name|SuspendedWaiter
argument_list|(
name|owner_
argument_list|,
name|mode_
argument_list|,
name|holds_
argument_list|)
decl_stmt|;
name|suspendedThreads
operator|.
name|push
argument_list|(
name|suspended
argument_list|)
expr_stmt|;
name|owner_
operator|=
name|caller
expr_stmt|;
name|holds_
operator|=
literal|1
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
name|listener
operator|=
name|waitingOnResource
expr_stmt|;
name|DeadlockDetection
operator|.
name|clearCollectionWaiter
argument_list|(
name|owner_
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|caller
operator|==
name|owner_
condition|)
block|{
operator|++
name|holds_
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|seStack
operator|.
name|push
argument_list|(
name|t
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
name|DeadlockDetection
operator|.
name|clearCollectionWaiter
argument_list|(
name|owner_
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|owner_
operator|==
literal|null
condition|)
block|{
name|owner_
operator|=
name|caller
expr_stmt|;
name|holds_
operator|=
literal|1
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|seStack
operator|.
name|push
argument_list|(
name|t
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
name|DeadlockDetection
operator|.
name|clearCollectionWaiter
argument_list|(
name|owner_
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|ex
parameter_list|)
block|{
name|notify
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Interrupted while waiting for lock"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|wakeUp
parameter_list|()
block|{
name|notify
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|attempt
parameter_list|(
specifier|final
name|LockMode
name|mode
parameter_list|)
block|{
specifier|final
name|Thread
name|caller
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|caller
operator|==
name|owner_
condition|)
block|{
operator|++
name|holds_
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|seStack
operator|.
name|push
argument_list|(
name|t
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|owner_
operator|==
literal|null
condition|)
block|{
name|owner_
operator|=
name|caller
expr_stmt|;
name|holds_
operator|=
literal|1
expr_stmt|;
name|modeStack
operator|.
name|push
argument_list|(
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|seStack
operator|.
name|push
argument_list|(
name|t
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mode_
operator|=
name|mode
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|isLockedForWrite
parameter_list|()
block|{
return|return
name|writeLocks
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLockedForRead
parameter_list|(
specifier|final
name|Thread
name|owner
parameter_list|)
block|{
comment|// always returns false for this lock
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|hasLock
parameter_list|()
block|{
return|return
name|holds_
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasLock
parameter_list|(
specifier|final
name|Thread
name|owner
parameter_list|)
block|{
return|return
name|this
operator|.
name|owner_
operator|==
name|owner
return|;
block|}
specifier|public
name|Thread
name|getOwner
parameter_list|()
block|{
return|return
name|this
operator|.
name|owner_
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
specifier|final
name|LockMode
name|mode
parameter_list|)
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|!=
name|owner_
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
name|warn
argument_list|(
literal|"Possible lock problem: thread "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|+
literal|" Released a lock on "
operator|+
name|getId
argument_list|()
operator|+
literal|" it didn't hold."
operator|+
literal|" Either the thread was interrupted or it never acquired the lock."
operator|+
literal|" The lock was owned by: "
operator|+
name|owner_
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lock was acquired by :"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|seStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StackTraceElement
index|[]
name|se
init|=
name|seStack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|se
argument_list|)
expr_stmt|;
name|se
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return;
block|}
name|LockMode
name|top
init|=
name|modeStack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|mode_
operator|=
name|top
expr_stmt|;
name|top
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|mode_
operator|!=
name|mode
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Released lock of different type. Expected "
operator|+
name|mode_
operator|+
literal|" got "
operator|+
name|mode
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mode_
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|)
block|{
name|writeLocks
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
name|seStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|--
name|holds_
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|suspendedThreads
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|SuspendedWaiter
name|suspended
init|=
name|suspendedThreads
operator|.
name|pop
argument_list|()
decl_stmt|;
name|owner_
operator|=
name|suspended
operator|.
name|thread
expr_stmt|;
name|mode_
operator|=
name|suspended
operator|.
name|lockMode
expr_stmt|;
name|holds_
operator|=
name|suspended
operator|.
name|lockCount
expr_stmt|;
block|}
else|else
block|{
name|owner_
operator|=
literal|null
expr_stmt|;
name|mode_
operator|=
name|LockMode
operator|.
name|NO_LOCK
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|lockReleased
argument_list|()
expr_stmt|;
name|listener
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|(
specifier|final
name|LockMode
name|mode
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not support releasing multiple locks"
argument_list|)
throw|;
block|}
comment|/**      * Return the number of unreleased acquires performed      * by the current thread.      * Returns zero if current thread does not hold lock.      **/
specifier|public
specifier|synchronized
name|long
name|holds
parameter_list|()
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|!=
name|owner_
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|holds_
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|LockInfo
name|getLockInfo
parameter_list|()
block|{
specifier|final
name|String
name|lockType
init|=
name|mode_
operator|==
name|LockMode
operator|.
name|WRITE_LOCK
condition|?
name|LockInfo
operator|.
name|WRITE_LOCK
else|:
name|LockInfo
operator|.
name|READ_LOCK
decl_stmt|;
return|return
operator|new
name|LockInfo
argument_list|(
name|LockInfo
operator|.
name|COLLECTION_LOCK
argument_list|,
name|lockType
argument_list|,
name|getId
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
operator|(
name|owner_
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|owner_
operator|.
name|getName
argument_list|()
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|debug
parameter_list|(
specifier|final
name|PrintStream
name|out
parameter_list|)
block|{
name|getLockInfo
argument_list|()
operator|.
name|debug
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

