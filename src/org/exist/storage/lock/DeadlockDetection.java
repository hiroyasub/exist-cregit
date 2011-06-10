begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * Deadlock detection for resource and collection locks. The static methods in this class  * keep track of all waiting threads, which are currently waiting on a resource or collection  * lock. In some scenarios (e.g. a complex XQuery which modifies resources), a single thread  * may acquire different read/write locks on resources in a collection. The locks can be arbitrarily  * nested. For example, a thread may first acquire a read lock on a collection, then a read lock on  * a resource and later acquires a write lock on the collection to remove the resource.  *  * Since we have locks on both, collections and resources, deadlock situations are sometimes  * unavoidable. For example, imagine the following scenario:  *  *<ul>  *<li>T1 owns write lock on resource</li>  *<li>T2 owns write lock on collection</li>  *<li>T2 wants to acquire write lock on resource locked by T1</li>  *<li>T1 tries to acquire write lock on collection currently locked by T2</li>  *<li>DEADLOCK</li>  *</ul>  *  * The code should probably be redesigned to avoid this kind of crossed collection-resource  * locking, which easily leads to circular wait conditions. However, this needs to be done with care. In  * the meantime, DeadlockDetection is used to detect deadlock situations as the one described  * above. The lock classes can  * then try to resolve the deadlock by suspending one thread.  */
end_comment

begin_class
specifier|public
class|class
name|DeadlockDetection
block|{
specifier|private
specifier|final
specifier|static
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Map
name|waitForResource
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Map
name|waitForCollection
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|ReentrantLock
name|getLock
parameter_list|()
block|{
return|return
name|lock
return|;
block|}
comment|/**      * Register a thread as waiting for a resource lock.      *      * @param thread the thread      * @param waiter the WaitingThread object which wraps around the thread      */
specifier|public
specifier|static
name|void
name|addResourceWaiter
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|WaitingThread
name|waiter
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|waitForResource
operator|.
name|put
argument_list|(
name|thread
argument_list|,
name|waiter
argument_list|)
expr_stmt|;
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
comment|/**      * Deregister a waiting thread.      *        * @param thread      * @return lock      */
specifier|public
specifier|static
name|Lock
name|clearResourceWaiter
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|WaitingThread
name|waiter
init|=
operator|(
name|WaitingThread
operator|)
name|waitForResource
operator|.
name|remove
argument_list|(
name|thread
argument_list|)
decl_stmt|;
if|if
condition|(
name|waiter
operator|!=
literal|null
condition|)
return|return
name|waiter
operator|.
name|getLock
argument_list|()
return|;
return|return
literal|null
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
specifier|public
specifier|static
name|WaitingThread
name|getResourceWaiter
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|(
name|WaitingThread
operator|)
name|waitForResource
operator|.
name|get
argument_list|(
name|thread
argument_list|)
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
comment|/**      * Check if there's a risk for a circular wait between threadA and threadB. The method tests if      * threadB is currently waiting for a resource lock (read or write). It then checks      * if threadA holds a lock on this resource. If yes, the {@link org.exist.storage.lock.WaitingThread}      * object for threadB is returned. This object can be used to suspend the waiting thread      * in order to temporarily yield the lock to threadA.      *      * @param threadA      * @param threadB      * @return waiting thread      */
specifier|public
specifier|static
name|WaitingThread
name|deadlockCheckResource
parameter_list|(
name|Thread
name|threadA
parameter_list|,
name|Thread
name|threadB
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// check if threadB is waiting for a resource lock
name|WaitingThread
name|waitingThread
init|=
operator|(
name|WaitingThread
operator|)
name|waitForResource
operator|.
name|get
argument_list|(
name|threadB
argument_list|)
decl_stmt|;
comment|// if lock != null, check if thread B waits for a resource lock currently held by thread A
if|if
condition|(
name|waitingThread
operator|!=
literal|null
condition|)
block|{
comment|//            	LOG.debug("deadlockCheck: " + threadB.getName() + " -> " + waitingThread.getLock().hasLock(threadA));
return|return
name|waitingThread
operator|.
name|getLock
argument_list|()
operator|.
name|hasLock
argument_list|(
name|threadA
argument_list|)
condition|?
name|waitingThread
else|:
literal|null
return|;
block|}
return|return
literal|null
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
comment|/**      * Check if the second thread is currently waiting for a resource lock and      * is blocked by the first thread.      *      * @param threadA the thread whose lock might be blocking threadB      * @param threadB the thread to check      * @return true if threadB is currently blocked by a lock held by threadA      */
specifier|public
specifier|static
name|boolean
name|isBlockedBy
parameter_list|(
name|Thread
name|threadA
parameter_list|,
name|Thread
name|threadB
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// check if threadB is waiting for a resource lock
name|WaitingThread
name|waitingThread
init|=
operator|(
name|WaitingThread
operator|)
name|waitForResource
operator|.
name|get
argument_list|(
name|threadB
argument_list|)
decl_stmt|;
comment|// if lock != null, check if thread B waits for a resource lock currently held by thread A
if|if
condition|(
name|waitingThread
operator|!=
literal|null
condition|)
block|{
return|return
name|waitingThread
operator|.
name|getLock
argument_list|()
operator|.
name|hasLock
argument_list|(
name|threadA
argument_list|)
return|;
block|}
return|return
literal|false
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
specifier|public
specifier|static
name|boolean
name|wouldDeadlock
parameter_list|(
name|Thread
name|waiter
parameter_list|,
name|Thread
name|owner
parameter_list|,
name|List
name|waiters
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|WaitingThread
name|wt
init|=
operator|(
name|WaitingThread
operator|)
name|waitForResource
operator|.
name|get
argument_list|(
name|owner
argument_list|)
decl_stmt|;
if|if
condition|(
name|wt
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|waiters
operator|.
name|contains
argument_list|(
name|wt
argument_list|)
condition|)
block|{
comment|// probably a deadlock, but not directly connected to the current thread
comment|// return to avoid endless loop
return|return
literal|false
return|;
block|}
name|waiters
operator|.
name|add
argument_list|(
name|wt
argument_list|)
expr_stmt|;
name|Lock
name|l
init|=
name|wt
operator|.
name|getLock
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|(
operator|(
name|MultiReadReentrantLock
operator|)
name|l
operator|)
operator|.
name|getWriteLockedThread
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
name|owner
condition|)
block|{
comment|//                System.out.println("Waiter: " + waiter.getName() + " Thread: " + t.getName() + " == " + owner.getName() +
comment|//                    " type: " + wt.getLockType());
comment|//                debug(t.getName(), l.getLockInfo());
comment|// the thread acquired the lock in the meantime
return|return
literal|false
return|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|t
operator|==
name|waiter
condition|)
return|return
literal|true
return|;
return|return
name|wouldDeadlock
argument_list|(
name|waiter
argument_list|,
name|t
argument_list|,
name|waiters
argument_list|)
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
else|else
block|{
name|Lock
name|l
init|=
operator|(
name|Lock
operator|)
name|waitForCollection
operator|.
name|get
argument_list|(
name|owner
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|Thread
name|t
init|=
operator|(
operator|(
name|ReentrantReadWriteLock
operator|)
name|l
operator|)
operator|.
name|getOwner
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
name|owner
condition|)
block|{
comment|//                    System.out.println("Thread " + t.getName() + " == " + owner.getName());
comment|//                    debug(t.getName(), l.getLockInfo());
comment|// the thread acquired the lock in the meantime
return|return
literal|false
return|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|t
operator|==
name|waiter
condition|)
return|return
literal|true
return|;
return|return
name|wouldDeadlock
argument_list|(
name|waiter
argument_list|,
name|t
argument_list|,
name|waiters
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|false
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
comment|/**      * Register a thread as waiting for a resource lock.      *      * @param waiter the thread      * @param lock the lock object      */
specifier|public
specifier|static
name|void
name|addCollectionWaiter
parameter_list|(
name|Thread
name|waiter
parameter_list|,
name|Lock
name|collectionLock
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|waitForCollection
operator|.
name|put
argument_list|(
name|waiter
argument_list|,
name|collectionLock
argument_list|)
expr_stmt|;
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
specifier|public
specifier|static
name|Lock
name|clearCollectionWaiter
parameter_list|(
name|Thread
name|waiter
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Lock
operator|)
name|waitForCollection
operator|.
name|remove
argument_list|(
name|waiter
argument_list|)
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
specifier|public
specifier|static
name|Lock
name|isWaitingFor
parameter_list|(
name|Thread
name|waiter
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Lock
operator|)
name|waitForCollection
operator|.
name|get
argument_list|(
name|waiter
argument_list|)
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
specifier|public
specifier|static
name|Map
name|getWaitingThreads
parameter_list|()
block|{
name|Map
name|table
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|waitForResource
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|WaitingThread
name|waitingThread
init|=
operator|(
name|WaitingThread
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|table
operator|.
name|put
argument_list|(
name|waitingThread
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|waitingThread
operator|.
name|getLock
argument_list|()
operator|.
name|getLockInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|i
init|=
name|waitForCollection
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Thread
name|thread
init|=
operator|(
name|Thread
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|table
operator|.
name|put
argument_list|(
name|thread
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
operator|(
name|Lock
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|getLockInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
specifier|public
specifier|static
name|void
name|debug
parameter_list|(
name|String
name|name
parameter_list|,
name|LockInfo
name|info
parameter_list|)
block|{
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|sout
argument_list|)
decl_stmt|;
name|debug
argument_list|(
name|writer
argument_list|,
name|name
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|debug
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|LockInfo
name|info
parameter_list|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"THREAD: "
operator|+
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"Lock type: "
operator|+
name|info
operator|.
name|getLockType
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Lock mode: "
operator|+
name|info
operator|.
name|getLockMode
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Lock id: "
operator|+
name|info
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Held by: "
operator|+
name|arrayToString
argument_list|(
name|info
operator|.
name|getOwners
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Read locks: "
operator|+
name|arrayToString
argument_list|(
name|info
operator|.
name|getReadLocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Wait for read: "
operator|+
name|arrayToString
argument_list|(
name|info
operator|.
name|getWaitingForRead
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"Wait for write: "
operator|+
name|arrayToString
argument_list|(
name|info
operator|.
name|getWaitingForWrite
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|debug
parameter_list|()
block|{
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|sout
argument_list|)
decl_stmt|;
name|Map
name|threads
init|=
name|getWaitingThreads
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|threads
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|debug
argument_list|(
name|writer
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|LockInfo
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|arrayToString
parameter_list|(
name|Object
index|[]
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
return|return
literal|"null"
return|;
if|if
condition|(
name|a
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|"[]"
return|;
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
operator|==
literal|null
condition|?
literal|"null"
else|:
name|a
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
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

