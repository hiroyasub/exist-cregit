begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2005-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    * Original code is  *   * Copyright 2001-2004 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License")  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *    * $Id$  */
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
name|util
operator|.
name|DeadlockException
import|;
end_import

begin_comment
comment|/**  * A reentrant read/write lock, which allows multiple readers to acquire a lock.  * Waiting writers are preferred.  *<p/>  * This is an adapted and bug-fixed version of code taken from Apache's Turbine  * JCS.  */
end_comment

begin_class
specifier|public
class|class
name|MultiReadReentrantLock
implements|implements
name|Lock
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
name|MultiReadReentrantLock
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Object
name|id
decl_stmt|;
comment|/**      * Number of threads waiting to read.      */
specifier|private
name|int
name|waitingForReadLock
init|=
literal|0
decl_stmt|;
comment|/**      * Number of threads reading.      */
specifier|private
name|List
name|outstandingReadLocks
init|=
operator|new
name|ArrayList
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|/**      * The thread that has the write lock or null.      */
specifier|private
name|Thread
name|writeLockedThread
decl_stmt|;
comment|/**      * The number of (nested) write locks that have been requested from      * writeLockedThread.      */
specifier|private
name|int
name|outstandingWriteLocks
init|=
literal|0
decl_stmt|;
comment|/**      * Threads waiting to get a write lock are tracked in this ArrayList to      * ensure that write locks are issued in the same order they are requested.      */
specifier|private
name|List
name|waitingForWriteLock
init|=
literal|null
decl_stmt|;
specifier|protected
name|Stack
name|suspendedThreads
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
class|class
name|SuspendedWaiter
block|{
name|Thread
name|thread
decl_stmt|;
name|int
name|outstanding
decl_stmt|;
name|List
name|waiters
decl_stmt|;
specifier|public
name|SuspendedWaiter
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|int
name|outstandingWriteLocks
parameter_list|,
name|List
name|waiters
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
name|outstanding
operator|=
name|outstandingWriteLocks
expr_stmt|;
name|this
operator|.
name|waiters
operator|=
name|waiters
expr_stmt|;
block|}
specifier|public
name|void
name|wakeUp
parameter_list|()
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
name|waiters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|WaitingThread
name|wt
init|=
operator|(
name|WaitingThread
operator|)
name|waiters
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|wt
operator|.
name|lockReleased
argument_list|()
expr_stmt|;
block|}
name|waiters
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Default constructor.      */
specifier|public
name|MultiReadReentrantLock
parameter_list|(
name|Object
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* @deprecated Use other method     * @see org.exist.storage.lock.Lock#acquire()     */
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
name|Lock
operator|.
name|READ_LOCK
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|acquire
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|LockException
block|{
if|if
condition|(
name|mode
operator|==
name|Lock
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
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|Lock
operator|.
name|WRITE_LOCK
case|:
return|return
name|writeLock
argument_list|()
return|;
default|default:
return|return
name|readLock
argument_list|()
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.Lock#attempt(int) 	 */
specifier|public
name|boolean
name|attempt
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented"
argument_list|)
throw|;
block|}
comment|/**      * Issue a read lock if there is no outstanding write lock or threads      * waiting to get a write lock. Caller of this method must be careful to      * avoid synchronizing the calling code so as to avoid deadlock.      */
specifier|private
specifier|synchronized
name|boolean
name|readLock
parameter_list|()
throws|throws
name|LockException
block|{
specifier|final
name|Thread
name|thisThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
if|if
condition|(
name|writeLockedThread
operator|==
name|thisThread
condition|)
block|{
comment|// add acquired lock to the current list of read locks
name|outstandingReadLocks
operator|.
name|add
argument_list|(
operator|new
name|LockOwner
argument_list|(
name|thisThread
argument_list|)
argument_list|)
expr_stmt|;
comment|//            LOG.debug("Thread already holds a write lock");
return|return
literal|true
return|;
block|}
name|deadlockCheck
argument_list|()
expr_stmt|;
name|waitingForReadLock
operator|++
expr_stmt|;
if|if
condition|(
name|writeLockedThread
operator|!=
literal|null
condition|)
block|{
name|WaitingThread
name|waiter
init|=
operator|new
name|WaitingThread
argument_list|(
name|thisThread
argument_list|,
name|this
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|DeadlockDetection
operator|.
name|addResourceWaiter
argument_list|(
name|thisThread
argument_list|,
name|waiter
argument_list|)
expr_stmt|;
while|while
condition|(
name|writeLockedThread
operator|!=
literal|null
condition|)
block|{
comment|//                LOG.debug("readLock wait by " + thisThread.getName() + " for " + getId());
name|waiter
operator|.
name|doWait
argument_list|()
expr_stmt|;
comment|//            LOG.debug("wake up from readLock wait");
block|}
name|DeadlockDetection
operator|.
name|clearResourceWaiter
argument_list|(
name|thisThread
argument_list|)
expr_stmt|;
block|}
comment|//        LOG.debug("readLock acquired by thread: " + Thread.currentThread().getName());
name|waitingForReadLock
operator|--
expr_stmt|;
comment|// add acquired lock to the current list of read locks
name|outstandingReadLocks
operator|.
name|add
argument_list|(
operator|new
name|LockOwner
argument_list|(
name|thisThread
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Issue a write lock if there are no outstanding read or write locks.      * Caller of this method must be careful to avoid synchronizing the calling      * code so as to avoid deadlock.      */
specifier|private
name|boolean
name|writeLock
parameter_list|()
throws|throws
name|LockException
block|{
name|Thread
name|thisThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|WaitingThread
name|waiter
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|writeLockedThread
operator|==
name|thisThread
condition|)
block|{
name|outstandingWriteLocks
operator|++
expr_stmt|;
comment|//                LOG.debug("acquired additional write lock on " + getId());
return|return
literal|true
return|;
block|}
if|if
condition|(
name|writeLockedThread
operator|==
literal|null
operator|&&
name|grantWriteLock
argument_list|()
condition|)
block|{
name|writeLockedThread
operator|=
name|thisThread
expr_stmt|;
name|outstandingWriteLocks
operator|++
expr_stmt|;
comment|//                LOG.debug( "writeLock on " + getId() + " acquired without waiting by " + writeLockedThread.getName());
return|return
literal|true
return|;
block|}
comment|//            if (writeLockedThread == thisThread) {
comment|//                LOG.debug("nested write lock: " + outstandingWriteLocks);
comment|//            }
name|deadlockCheck
argument_list|()
expr_stmt|;
if|if
condition|(
name|waitingForWriteLock
operator|==
literal|null
condition|)
name|waitingForWriteLock
operator|=
operator|new
name|ArrayList
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|waiter
operator|=
operator|new
name|WaitingThread
argument_list|(
name|thisThread
argument_list|,
name|thisThread
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|waitingForWriteLock
operator|.
name|add
argument_list|(
name|waiter
argument_list|)
expr_stmt|;
name|DeadlockDetection
operator|.
name|addResourceWaiter
argument_list|(
name|thisThread
argument_list|,
name|waiter
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|thisThread
init|)
block|{
if|if
condition|(
name|thisThread
operator|!=
name|writeLockedThread
condition|)
block|{
while|while
condition|(
name|thisThread
operator|!=
name|writeLockedThread
condition|)
block|{
comment|//                	LOG.debug("writeLock wait on " + getId() + ". held by " + (writeLockedThread == null ? "null" : writeLockedThread.getName())
comment|//                            + ". outstanding: " + outstandingWriteLocks);
if|if
condition|(
name|LockOwner
operator|.
name|DEBUG
condition|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Waiting for write: "
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|waitingForWriteLock
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|WaitingThread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|debugReadLocks
argument_list|(
literal|"WAIT"
argument_list|)
expr_stmt|;
block|}
comment|//                    checkForDeadlock();
try|try
block|{
name|waiter
operator|.
name|doWait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|DeadlockDetection
operator|.
name|clearResourceWaiter
argument_list|(
name|thisThread
argument_list|)
expr_stmt|;
name|int
name|i
init|=
name|waitingForWriteLock
operator|.
name|indexOf
argument_list|(
name|waiter
argument_list|)
decl_stmt|;
name|waitingForWriteLock
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
name|outstandingWriteLocks
operator|++
expr_stmt|;
comment|//testing
comment|//            LOG.debug( "writeLock on " + getId() + " acquired by " + writeLockedThread.getName());
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|DeadlockDetection
operator|.
name|clearResourceWaiter
argument_list|(
name|thisThread
argument_list|)
expr_stmt|;
name|int
name|i
init|=
name|waitingForWriteLock
operator|.
name|indexOf
argument_list|(
name|waiter
argument_list|)
decl_stmt|;
name|waitingForWriteLock
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/* @deprecated : use other method      * @see org.exist.storage.lock.Lock#release()      */
specifier|public
name|void
name|release
parameter_list|()
block|{
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|release
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|Lock
operator|.
name|NO_LOCK
case|:
break|break;
case|case
name|Lock
operator|.
name|WRITE_LOCK
case|:
name|releaseWrite
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
default|default:
name|releaseRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|public
name|void
name|release
parameter_list|(
name|int
name|mode
parameter_list|,
name|int
name|count
parameter_list|)
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|Lock
operator|.
name|WRITE_LOCK
case|:
name|releaseWrite
argument_list|(
name|count
argument_list|)
expr_stmt|;
break|break;
default|default:
name|releaseRead
argument_list|(
name|count
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|releaseWrite
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|==
name|writeLockedThread
condition|)
block|{
comment|//log.info( "outstandingWriteLocks= " + outstandingWriteLocks );
if|if
condition|(
name|outstandingWriteLocks
operator|>
literal|0
condition|)
name|outstandingWriteLocks
operator|-=
name|count
expr_stmt|;
comment|//            else {
comment|//                LOG.info("extra lock release, writelocks are " + outstandingWriteLocks + "and done was called");
comment|//            }
if|if
condition|(
name|outstandingWriteLocks
operator|>
literal|0
condition|)
block|{
comment|//                LOG.debug("writeLock released for a nested writeLock request: " + outstandingWriteLocks +
comment|//                    "; thread: " + writeLockedThread.getName());
return|return;
block|}
comment|// if another thread is waiting for a write lock, we immediately pass control to it.
comment|// no further checks should be required here.
if|if
condition|(
name|suspendedThreads
operator|.
name|empty
argument_list|()
operator|&&
name|grantWriteLockAfterRead
argument_list|()
condition|)
block|{
name|WaitingThread
name|waiter
init|=
operator|(
name|WaitingThread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|writeLockedThread
operator|=
name|waiter
operator|.
name|getThread
argument_list|()
expr_stmt|;
comment|//                if (LOG.isDebugEnabled()) {
comment|//                    LOG.debug("writeLock released and before notifying a write lock waiting thread " + writeLockedThread);
comment|//                }
synchronized|synchronized
init|(
name|writeLockedThread
init|)
block|{
name|writeLockedThread
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
comment|//                if (LOG.isDebugEnabled()) {
comment|//                    LOG.debug("writeLock released by " + Thread.currentThread().getName() +
comment|//                            " after notifying a write lock waiting thread " + writeLockedThread.getName());
comment|//                }
block|}
else|else
block|{
name|writeLockedThread
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|waitingForReadLock
operator|>
literal|0
operator|&&
name|suspendedThreads
operator|.
name|empty
argument_list|()
condition|)
block|{
comment|//                    LOG.debug("writeLock " + Thread.currentThread().getName() + " released, notified waiting readers");
comment|// wake up pending read locks
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|//                } else {
comment|//                    LOG.debug("writeLock released, no readers waiting");
comment|//                }
block|}
if|if
condition|(
operator|!
name|suspendedThreads
operator|.
name|empty
argument_list|()
condition|)
block|{
name|SuspendedWaiter
name|waiter
init|=
operator|(
name|SuspendedWaiter
operator|)
name|suspendedThreads
operator|.
name|pop
argument_list|()
decl_stmt|;
name|writeLockedThread
operator|=
name|waiter
operator|.
name|thread
expr_stmt|;
name|outstandingWriteLocks
operator|=
name|waiter
operator|.
name|outstanding
expr_stmt|;
name|waiter
operator|.
name|wakeUp
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Passing lock to suspended thread: "
operator|+
name|writeLockedThread
operator|.
name|getName
argument_list|()
operator|+
literal|" waiting on "
operator|+
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Possible lock problem: a thread released a write lock it didn't hold. Either the "
operator|+
literal|"thread was interrupted or it never acquired the lock."
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//        LOG.debug("writeLock released: " + getId() + "; outstanding: " + outstandingWriteLocks +
comment|//            "; thread: " + Thread.currentThread().getName() + " suspended: " + suspendedThreads.size());
block|}
comment|/**      * Threads call this method to relinquish a lock that they previously got      * from this object.      *      * @throws IllegalStateException if called when there are no outstanding locks or there is a      *                               write lock issued to a different thread.      */
specifier|private
specifier|synchronized
name|void
name|releaseRead
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
operator|!
name|outstandingReadLocks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|removeReadLock
argument_list|(
name|count
argument_list|)
expr_stmt|;
comment|//            if (LOG.isDebugEnabled()) {
comment|//                LOG.debug("readLock on " + getId() + " released by " + Thread.currentThread().getName());
comment|//                LOG.debug("remaining read locks: " + listReadLocks());
comment|//            }
if|if
condition|(
name|writeLockedThread
operator|==
literal|null
operator|&&
name|grantWriteLockAfterRead
argument_list|()
condition|)
block|{
name|WaitingThread
name|waiter
init|=
operator|(
name|WaitingThread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|writeLockedThread
operator|=
name|waiter
operator|.
name|getThread
argument_list|()
expr_stmt|;
comment|//                if (LOG.isDebugEnabled()) {
comment|//                    LOG.debug("readLock released and before notifying a write lock waiting thread " + writeLockedThread);
comment|//                    LOG.debug("remaining read locks: " + outstandingReadLocks.size());
comment|//                }
synchronized|synchronized
init|(
name|writeLockedThread
init|)
block|{
name|writeLockedThread
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|//                if (LOG.isDebugEnabled()) {
comment|//                    LOG.debug("readLock released and after notifying a write lock waiting thread " + writeLockedThread);
comment|//                }
block|}
return|return;
block|}
else|else
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
operator|.
name|getName
argument_list|()
operator|+
literal|" released a read lock it didn't hold. Either the "
operator|+
literal|"thread was interrupted or it never acquired the lock. "
operator|+
literal|"Write lock: "
operator|+
operator|(
name|writeLockedThread
operator|!=
literal|null
condition|?
name|writeLockedThread
operator|.
name|getName
argument_list|()
else|:
literal|"null"
operator|)
argument_list|,
operator|new
name|Throwable
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LockOwner
operator|.
name|DEBUG
condition|)
name|debugReadLocks
argument_list|(
literal|"ILLEGAL RELEASE"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|isLockedForWrite
parameter_list|()
block|{
return|return
name|writeLockedThread
operator|!=
literal|null
operator|||
operator|(
name|waitingForWriteLock
operator|!=
literal|null
operator|&&
name|waitingForWriteLock
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasLock
parameter_list|()
block|{
return|return
operator|!
name|outstandingReadLocks
operator|.
name|isEmpty
argument_list|()
operator|||
name|isLockedForWrite
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isLockedForRead
parameter_list|(
name|Thread
name|owner
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|outstandingReadLocks
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
if|if
condition|(
operator|(
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getOwner
argument_list|()
operator|==
name|owner
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|removeReadLock
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|Object
name|owner
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|outstandingReadLocks
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
operator|&&
name|count
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|LockOwner
name|current
init|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|getOwner
argument_list|()
operator|==
name|owner
condition|)
block|{
name|outstandingReadLocks
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
operator|--
name|count
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|deadlockCheck
parameter_list|()
throws|throws
name|DeadlockException
block|{
comment|//    	if (writeLockedThread != null) {
comment|//    		Lock lock = DeadlockDetection.isWaitingFor(writeLockedThread);
comment|//    		if (lock != null&& lock.hasLock())
comment|//    			throw new DeadlockException();
comment|//    	}
specifier|final
name|int
name|size
init|=
name|outstandingReadLocks
operator|.
name|size
argument_list|()
decl_stmt|;
name|LockOwner
name|next
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|Lock
name|lock
init|=
name|DeadlockDetection
operator|.
name|isWaitingFor
argument_list|(
name|next
operator|.
name|getOwner
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
comment|//                LOG.debug("Checking for deadlock...");
name|lock
operator|.
name|wakeUp
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Detect circular wait on different resources: thread A has a write lock on      * resource R1; thread B has a write lock on resource R2; thread A tries to      * acquire lock on R2; thread B now tries to acquire lock on R1. Solution:      * suspend existing write lock of thread A and grant it to B.      *      * @return true if the write lock should be granted to the current thread      */
specifier|private
name|void
name|checkForDeadlock
parameter_list|()
throws|throws
name|DeadlockException
block|{
name|Thread
name|waiter
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ArrayList
name|waiters
init|=
operator|new
name|ArrayList
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|DeadlockDetection
operator|.
name|wouldDeadlock
argument_list|(
name|waiter
argument_list|,
name|writeLockedThread
argument_list|,
name|waiters
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deadlock detected on lock "
operator|+
name|getId
argument_list|()
argument_list|)
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
name|waiters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|WaitingThread
name|wt
init|=
operator|(
name|WaitingThread
operator|)
name|waiters
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiter: "
operator|+
name|wt
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" -> "
operator|+
name|wt
operator|.
name|getLock
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Check if a write lock can be granted, either because there are no      * read locks, the read lock belongs to the current thread and can be      * upgraded or the thread which holds the lock is blocked by another      * lock held by the current thread.      *      * @return true if the write lock can be granted      */
specifier|private
name|boolean
name|grantWriteLock
parameter_list|()
block|{
name|Thread
name|waiter
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|outstandingReadLocks
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|LockOwner
name|next
decl_stmt|;
comment|// walk through outstanding read locks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// if the read lock is owned by the current thread, all is ok and we continue
if|if
condition|(
name|next
operator|.
name|getOwner
argument_list|()
operator|!=
name|waiter
condition|)
block|{
comment|// otherwise, check if the lock belongs to a thread which is currently blocked
comment|// by a lock owned by the current thread. if yes, it will be safe to grant the
comment|// write lock: the other thread will be blocked anyway.
if|if
condition|(
operator|!
name|DeadlockDetection
operator|.
name|isBlockedBy
argument_list|(
name|waiter
argument_list|,
name|next
operator|.
name|getOwner
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Check if a write lock can be granted, either because there are no      * read locks or the read lock belongs to the current thread and can be      * upgraded. This method is called whenever a lock is released.      *      * @return true if the write lock can be granted      */
specifier|private
name|boolean
name|grantWriteLockAfterRead
parameter_list|()
block|{
comment|// waiting write locks?
if|if
condition|(
name|waitingForWriteLock
operator|!=
literal|null
operator|&&
name|waitingForWriteLock
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// yes, check read locks
specifier|final
name|int
name|size
init|=
name|outstandingReadLocks
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
comment|// grant lock if all read locks are held by the write thread
name|WaitingThread
name|waiter
init|=
operator|(
name|WaitingThread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|isCompatible
argument_list|(
name|waiter
operator|.
name|getThread
argument_list|()
argument_list|)
return|;
block|}
else|else
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Check if the specified thread has a read lock on the resource.      *      * @param owner the thread      * @return true if owner has a read lock      */
specifier|private
name|boolean
name|hasReadLock
parameter_list|(
name|Thread
name|owner
parameter_list|)
block|{
name|LockOwner
name|next
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outstandingReadLocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getOwner
argument_list|()
operator|==
name|owner
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Thread
name|getWriteLockedThread
parameter_list|()
block|{
return|return
name|writeLockedThread
return|;
block|}
comment|/**      * Check if the specified thread holds either a write or a read lock      * on the resource.      *      * @param owner the thread      * @return true if owner has a lock      */
specifier|public
name|boolean
name|hasLock
parameter_list|(
name|Thread
name|owner
parameter_list|)
block|{
if|if
condition|(
name|writeLockedThread
operator|==
name|owner
condition|)
return|return
literal|true
return|;
return|return
name|hasReadLock
argument_list|(
name|owner
argument_list|)
return|;
block|}
specifier|public
name|void
name|wakeUp
parameter_list|()
block|{
block|}
comment|/**      * Check if the pending request for a write lock is compatible      * with existing read locks and other write requests. A lock request is      * compatible with another lock request if: (a) it belongs to the same thread,      * (b) it belongs to a different thread, but this thread is also waiting for a write lock.      *      * @param waiting      * @return true if the lock request is compatible with all other requests and the      * lock can be granted.      */
specifier|private
name|boolean
name|isCompatible
parameter_list|(
name|Thread
name|waiting
parameter_list|)
block|{
name|LockOwner
name|next
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outstandingReadLocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// if the read lock is owned by the current thread, all is ok and we continue
if|if
condition|(
name|next
operator|.
name|getOwner
argument_list|()
operator|!=
name|waiting
condition|)
block|{
comment|// otherwise, check if the lock belongs to a thread which is currently blocked
comment|// by a lock owned by the current thread. if yes, it will be safe to grant the
comment|// write lock: the other thread will be blocked anyway.
if|if
condition|(
operator|!
name|DeadlockDetection
operator|.
name|isBlockedBy
argument_list|(
name|waiting
argument_list|,
name|next
operator|.
name|getOwner
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|synchronized
name|LockInfo
name|getLockInfo
parameter_list|()
block|{
name|LockInfo
name|info
decl_stmt|;
name|String
index|[]
name|readers
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|outstandingReadLocks
operator|!=
literal|null
condition|)
block|{
name|readers
operator|=
operator|new
name|String
index|[
name|outstandingReadLocks
operator|.
name|size
argument_list|()
index|]
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
name|outstandingReadLocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LockOwner
name|owner
init|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|readers
index|[
name|i
index|]
operator|=
name|owner
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|writeLockedThread
operator|!=
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|LockInfo
argument_list|(
name|LockInfo
operator|.
name|RESOURCE_LOCK
argument_list|,
name|LockInfo
operator|.
name|WRITE_LOCK
argument_list|,
name|getId
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|writeLockedThread
operator|.
name|getName
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|info
operator|.
name|setReadLocks
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|=
operator|new
name|LockInfo
argument_list|(
name|LockInfo
operator|.
name|RESOURCE_LOCK
argument_list|,
name|LockInfo
operator|.
name|READ_LOCK
argument_list|,
name|getId
argument_list|()
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|waitingForWriteLock
operator|!=
literal|null
condition|)
block|{
name|String
name|waitingForWrite
index|[]
init|=
operator|new
name|String
index|[
name|waitingForWriteLock
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|waitingForWriteLock
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|waitingForWrite
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|WaitingThread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|setWaitingForWrite
argument_list|(
name|waitingForWrite
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
specifier|private
name|void
name|debugReadLocks
parameter_list|(
name|String
name|msg
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
name|outstandingReadLocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LockOwner
name|owner
init|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|msg
operator|+
literal|": "
operator|+
name|owner
operator|.
name|getOwner
argument_list|()
argument_list|,
name|owner
operator|.
name|getStack
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|listReadLocks
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outstandingReadLocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LockOwner
name|owner
init|=
operator|(
name|LockOwner
operator|)
name|outstandingReadLocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Thread
operator|)
name|owner
operator|.
name|getOwner
argument_list|()
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

