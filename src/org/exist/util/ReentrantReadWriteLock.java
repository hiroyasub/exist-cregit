begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*   File: ReentrantLock.java    Originally written by Doug Lea and released into the public domain.   This may be used for any purposes whatsoever without acknowledgment.   Thanks for the assistance and support of Sun Microsystems Labs,   and everyone contributing, testing, and using this code.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

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
specifier|protected
name|String
name|id_
init|=
literal|null
decl_stmt|;
specifier|protected
name|Thread
name|owner_
init|=
literal|null
decl_stmt|;
specifier|protected
name|long
name|holds_
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|mode_
init|=
name|Lock
operator|.
name|READ_LOCK
decl_stmt|;
specifier|private
name|long
name|timeOut_
init|=
literal|60000L
decl_stmt|;
specifier|public
name|ReentrantReadWriteLock
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|id_
operator|=
name|id
expr_stmt|;
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
if|if
condition|(
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
throw|throw
operator|new
name|LockException
argument_list|()
throw|;
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
name|mode_
operator|=
name|mode
expr_stmt|;
comment|//				System.out.println("thread " + caller.getName() + " acquired lock on " + id_ +
comment|//					"; locks held = " + holds_);
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
name|mode_
operator|=
name|mode
expr_stmt|;
comment|//				System.out.println("thread " + caller.getName() + " acquired lock on " + id_);
return|return
literal|true
return|;
block|}
else|else
block|{
name|long
name|waitTime
init|=
name|timeOut_
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
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
name|waitTime
argument_list|)
expr_stmt|;
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
name|mode_
operator|=
name|mode
expr_stmt|;
comment|//							System.out.println("thread " + caller.getName() + " acquired lock on " + id_ +
comment|//								"; locks held = " + holds_);
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
name|mode_
operator|=
name|mode
expr_stmt|;
comment|//							System.out.println("thread " + caller.getName() + " acquired lock on " + id_ +
comment|//								"; locks held = " + holds_);
return|return
literal|true
return|;
block|}
else|else
block|{
name|waitTime
operator|=
name|timeOut_
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
if|if
condition|(
name|waitTime
operator|<=
literal|0
condition|)
block|{
comment|// blocking thread found: if the lock is read only, remove it
if|if
condition|(
name|mode_
operator|==
name|Lock
operator|.
name|READ_LOCK
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"releasing blocking thread "
operator|+
name|owner_
operator|.
name|getName
argument_list|()
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
name|mode_
operator|=
name|mode
expr_stmt|;
comment|//									System.out.println("thread " + caller.getName() + " acquired lock on " + id_ +
comment|//										"; locks held = " + holds_);
return|return
literal|true
return|;
block|}
else|else
throw|throw
operator|new
name|LockException
argument_list|(
literal|"time out while acquiring a lock"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
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
literal|"interrupted while waiting for lock"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/** 	 * Release the lock. 	 * @exception Error thrown if not current owner of lock 	 **/
specifier|public
specifier|synchronized
name|void
name|release
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
throw|throw
operator|new
name|Error
argument_list|(
literal|"Illegal lock usage. Thread "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|+
literal|" tried to release lock on "
operator|+
name|id_
argument_list|)
throw|;
if|if
condition|(
operator|--
name|holds_
operator|==
literal|0
condition|)
block|{
comment|//			System.out.println("thread " + owner_.getName() + " released lock on " + id_ +
comment|//				"; locks held = " + holds_);
name|owner_
operator|=
literal|null
expr_stmt|;
name|mode_
operator|=
name|Lock
operator|.
name|READ_LOCK
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**  	 * Release the lock N times.<code>release(n)</code> is 	 * equivalent in effect to: 	 *<pre> 	 *   for (int i = 0; i< n; ++i) release(); 	 *</pre> 	 *<p> 	 * @exception Error thrown if not current owner of lock 	 * or has fewer than N holds on the lock 	 **/
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|long
name|n
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
operator|||
name|n
operator|>
name|holds_
condition|)
throw|throw
operator|new
name|Error
argument_list|(
literal|"Illegal Lock usage"
argument_list|)
throw|;
name|holds_
operator|-=
name|n
expr_stmt|;
if|if
condition|(
name|holds_
operator|==
literal|0
condition|)
block|{
name|owner_
operator|=
literal|null
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Return the number of unreleased acquires performed 	 * by the current thread. 	 * Returns zero if current thread does not hold lock. 	 **/
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
return|return
literal|0
return|;
return|return
name|holds_
return|;
block|}
block|}
end_class

end_unit

