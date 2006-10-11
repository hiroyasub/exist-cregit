begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  * Original code is  *   * Copyright 2001-2004 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License")  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *    * $Id$  */
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

begin_comment
comment|/**  * A reentrant read/write lock, which allows multiple readers to acquire a lock.  * Waiting writers are preferred.  *   * This is an adapted and bug-fixed version of code taken from Apache's Turbine  * JCS.  *    */
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
name|log
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
comment|/** Number of threads waiting to read. */
specifier|private
name|int
name|waitingForReadLock
init|=
literal|0
decl_stmt|;
comment|/** Number of threads reading. */
specifier|private
name|int
name|outstandingReadLocks
init|=
literal|0
decl_stmt|;
comment|/** The thread that has the write lock or null. */
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
comment|/** Default constructor. */
specifier|public
name|MultiReadReentrantLock
parameter_list|()
block|{
block|}
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
if|if
condition|(
name|writeLockedThread
operator|==
name|Thread
operator|.
name|currentThread
argument_list|()
condition|)
block|{
name|outstandingReadLocks
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|waitingForReadLock
operator|++
expr_stmt|;
while|while
condition|(
name|writeLockedThread
operator|!=
literal|null
condition|)
block|{
comment|//            log.debug( "readLock wait" );
try|try
block|{
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Interrupted while waiting for read lock"
argument_list|)
throw|;
block|}
comment|//            log.debug( "wake up from readLock wait" );
block|}
comment|//        log.debug( "readLock acquired" );
name|waitingForReadLock
operator|--
expr_stmt|;
name|outstandingReadLocks
operator|++
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
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|writeLockedThread
operator|==
literal|null
operator|&&
name|outstandingReadLocks
operator|==
literal|0
condition|)
block|{
name|writeLockedThread
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
name|outstandingWriteLocks
operator|++
expr_stmt|;
comment|//                log.debug( "writeLock acquired without waiting by " + writeLockedThread.getName());
return|return
literal|true
return|;
block|}
comment|//            if ( writeLockedThread == thisThread )
comment|//            {
comment|//                log.debug("nested write lock: " + outstandingWriteLocks);
comment|//            }
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
name|waitingForWriteLock
operator|.
name|add
argument_list|(
name|thisThread
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|thisThread
init|)
block|{
while|while
condition|(
name|thisThread
operator|!=
name|writeLockedThread
condition|)
block|{
comment|//                log.debug( "writeLock wait: outstanding: " +
comment|// outstandingWriteLocks + " / " + outstandingReadLocks);
try|try
block|{
comment|// set this so if there is an error the app will not
comment|// completely die!
name|thisThread
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Interrupted"
argument_list|)
throw|;
block|}
comment|//                log.debug( "wake up from writeLock wait" );
block|}
name|outstandingWriteLocks
operator|++
expr_stmt|;
comment|//testing
comment|//            log.debug( "writeLock acquired " + writeLockedThread.getName());
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|int
name|i
init|=
name|waitingForWriteLock
operator|.
name|indexOf
argument_list|(
name|thisThread
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
name|WRITE_LOCK
case|:
name|releaseWrite
argument_list|()
expr_stmt|;
break|break;
default|default:
name|releaseRead
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|releaseWrite
parameter_list|()
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
operator|--
expr_stmt|;
comment|//            else {
comment|//                log.info("extra lock release, writelocks are "
comment|//                        + outstandingWriteLocks + "and done was called");
comment|//            }
if|if
condition|(
name|outstandingWriteLocks
operator|>
literal|0
condition|)
block|{
comment|//                log.debug( "writeLock released for a nested writeLock request: " + outstandingWriteLocks +
comment|//                        "; thread: " + writeLockedThread.getName());
return|return;
block|}
comment|// could pull out of sub if block to get nested tracking working.
if|if
condition|(
name|outstandingReadLocks
operator|==
literal|0
operator|&&
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
name|writeLockedThread
operator|=
operator|(
name|Thread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//                if ( log.isDebugEnabled() )
comment|//                {
comment|//                    log.debug( "writeLock released and before notifying a write
comment|// lock waiting thread "
comment|//                         + writeLockedThread );
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
comment|//                if ( log.isDebugEnabled() )
comment|//                {
comment|//                    log.debug( "writeLock released and after notifying a write
comment|// lock waiting thread "
comment|//                         + writeLockedThread );
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
condition|)
block|{
comment|//                    log.debug( "writeLock released, notified waiting readers"
comment|// );
name|notifyAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//                    log.debug( "writeLock released, no readers waiting" );
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Possible lock problem: a thread released a write lock it didn't hold. Either the "
operator|+
literal|"thread was interrupted or it never acquired the lock."
argument_list|)
expr_stmt|;
block|}
comment|//        log.debug("writeLock released: " + outstandingWriteLocks +
comment|//                "; thread: " + Thread.currentThread().getName());
block|}
comment|/**      * Threads call this method to relinquish a lock that they previously got      * from this object.      *       * @throws IllegalStateException      *                   if called when there are no outstanding locks or there is a      *                   write lock issued to a different thread.      */
specifier|private
specifier|synchronized
name|void
name|releaseRead
parameter_list|()
block|{
if|if
condition|(
name|outstandingReadLocks
operator|>
literal|0
condition|)
block|{
name|outstandingReadLocks
operator|--
expr_stmt|;
if|if
condition|(
name|outstandingReadLocks
operator|==
literal|0
operator|&&
name|writeLockedThread
operator|==
literal|null
operator|&&
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
name|writeLockedThread
operator|=
operator|(
name|Thread
operator|)
name|waitingForWriteLock
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//                if ( log.isDebugEnabled() )
comment|//                {
comment|//                    log.debug( "readLock released and before notifying a write
comment|// lock waiting thread "
comment|//                         + writeLockedThread );
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
comment|//                if ( log.isDebugEnabled() )
comment|//                {
comment|//                    log.debug( "readLock released and after notifying a write
comment|// lock waiting thread "
comment|//                         + writeLockedThread );
comment|//                }
block|}
return|return;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Possible lock problem: a thread released a read lock it didn't hold. Either the "
operator|+
literal|"thread was interrupted or it never acquired the lock."
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
block|}
end_class

end_unit

