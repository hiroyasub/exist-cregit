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
name|util
package|;
end_package

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|objects
operator|.
name|Object2ObjectOpenHashMap
import|;
end_import

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|GuardedBy
import|;
end_import

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|ThreadSafe
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|ReferenceQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicInteger
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
name|StampedLock
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
name|Function
import|;
end_import

begin_comment
comment|/**  * Inspired by Guava's com.google.common.util.concurrent.Striped#lazyWeakReadWriteLock(int)  * implementation.  * See<a href="https://google.github.io/guava/releases/21.0/api/docs/com/google/common/util/concurrent/Striped.html#lazyWeakReadWriteLock-int-">https://google.github.io/guava/releases/21.0/api/docs/com/google/common/util/concurrent/Striped.html#lazyWeakReadWriteLock-int-</a>.  *  * However this is much simpler, and there is no hashing; we  * will always return the same object (stripe) for the same key.  *  * This class basically couples Weak References with a  * thread safe HashMap and manages draining expired Weak  * References from the HashMap.  *  * Weak References will be cleaned up from the internal map  * after they have been cleared by the GC. Two cleanup policies  * are provided: "Batch" and "Amortize". The policy is chosen  * by the constructor parameter {@code amortizeCleanup}.  *  * Batch Cleanup  *     With Batch Cleanup, expired Weak References will  *     be collected up to the {@link #MAX_EXPIRED_REFERENCE_READ_COUNT}  *     limit, at which point the calling thread which causes  *     that ceiling to be detected will cleanup all expired references.  *  * Amortize Cleanup  *     With Amortize Cleanup, each calling thread will attempt  *     to cleanup up to {@link #DRAIN_MAX} expired weak  *     references on each write operation, or after  *     {@link #READ_DRAIN_THRESHOLD} since the last cleanup.  *  * With either cleanup policy, only a single calling thread  * performs the cleanup at any time.  *  * @param<K> The type of the key for the stripe.  * @param<S> The type of the stripe.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|WeakLazyStripes
parameter_list|<
name|K
parameter_list|,
name|S
parameter_list|>
block|{
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_CAPACITY
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|float
name|LOAD_FACTOR
init|=
literal|0.75f
decl_stmt|;
comment|/**      * When {@link #amortizeCleanup} is false, this is the      * number of reads allowed which return expired references      * before calling {@link #drainClearedReferences()}.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_EXPIRED_REFERENCE_READ_COUNT
init|=
literal|1000
decl_stmt|;
comment|/**      * When {@link #amortizeCleanup} is true, this is the      * number of reads which are performed between calls      * to {@link #drainClearedReferences()}.      */
specifier|private
specifier|static
specifier|final
name|int
name|READ_DRAIN_THRESHOLD
init|=
literal|64
decl_stmt|;
comment|/**      * When {@link #amortizeCleanup} is true, this is the      * maximum number of entries to be drained      * by {@link #drainClearedReferences()}.      */
specifier|private
specifier|static
specifier|final
name|int
name|DRAIN_MAX
init|=
literal|16
decl_stmt|;
specifier|private
specifier|final
name|ReferenceQueue
argument_list|<
name|S
argument_list|>
name|referenceQueue
decl_stmt|;
specifier|private
specifier|final
name|StampedLock
name|stripesLock
init|=
operator|new
name|StampedLock
argument_list|()
decl_stmt|;
annotation|@
name|GuardedBy
argument_list|(
literal|"stripesLock"
argument_list|)
specifier|private
specifier|final
name|Object2ObjectOpenHashMap
argument_list|<
name|K
argument_list|,
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
argument_list|>
name|stripes
decl_stmt|;
comment|/**      * The number of reads on {@link #stripes} which have returned      * expired weak references.      */
specifier|private
specifier|final
name|AtomicInteger
name|expiredReferenceReadCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**      * The number of reads on {@link #stripes} since      * {@link #drainClearedReferences()} was last      * completed.      */
specifier|private
specifier|final
name|AtomicInteger
name|readCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|creator
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|amortizeCleanup
decl_stmt|;
comment|/**      * Guard so that only a single thread drains      * references at once.      */
specifier|private
specifier|final
name|AtomicBoolean
name|draining
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|/**      * Constructs a WeakLazyStripes where the concurrencyLevel      * is the lower of either ConcurrentHashMap#DEFAULT_CONCURRENCY_LEVEL      * or {@code Runtime.getRuntime().availableProcessors() * 2}.      *      * @param creator A factory for creating new Stripes when needed      */
specifier|public
name|WeakLazyStripes
parameter_list|(
specifier|final
name|Function
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|creator
parameter_list|)
block|{
name|this
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|16
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|*
literal|2
argument_list|)
argument_list|,
name|creator
argument_list|)
expr_stmt|;
comment|// 16 == ConcurrentHashMap#DEFAULT_CONCURRENCY_LEVEL
block|}
comment|/**      * Constructs a WeakLazyStripes.      *      * @param concurrencyLevel The concurrency level for the underlying stripes map      * @param creator A factory for creating new Stripes when needed      */
specifier|public
name|WeakLazyStripes
parameter_list|(
specifier|final
name|int
name|concurrencyLevel
parameter_list|,
specifier|final
name|Function
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|creator
parameter_list|)
block|{
name|this
argument_list|(
name|concurrencyLevel
argument_list|,
name|creator
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a WeakLazyStripes.      *      * @param concurrencyLevel The concurrency level for the underlying stripes map      * @param creator A factory for creating new Stripes when needed      * @param amortizeCleanup true if the cleanup of weak references should be      *     amortized across many calls (default), false if the cleanup should be batched up      *     and apportioned to a particular caller at a threshold      */
specifier|public
name|WeakLazyStripes
parameter_list|(
specifier|final
name|int
name|concurrencyLevel
parameter_list|,
specifier|final
name|Function
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|creator
parameter_list|,
specifier|final
name|boolean
name|amortizeCleanup
parameter_list|)
block|{
name|this
operator|.
name|stripes
operator|=
operator|new
name|Object2ObjectOpenHashMap
argument_list|<>
argument_list|(
name|INITIAL_CAPACITY
argument_list|,
name|LOAD_FACTOR
argument_list|)
expr_stmt|;
name|this
operator|.
name|referenceQueue
operator|=
operator|new
name|ReferenceQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|creator
operator|=
name|creator
expr_stmt|;
name|this
operator|.
name|amortizeCleanup
operator|=
name|amortizeCleanup
expr_stmt|;
block|}
comment|/**      * Get the stripe for the given key      *      * If the stripe does not exist, it will be created by      * calling {@link Function#apply(Object)} on {@link #creator}      *      * @param key the key for the stripe      * @return the stripe      */
specifier|public
name|S
name|get
parameter_list|(
specifier|final
name|K
name|key
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|Holder
argument_list|<
name|Boolean
argument_list|>
name|written
init|=
operator|new
name|Holder
argument_list|<>
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// 1) attempt lookup via optimistic read and immediate conversion to write lock
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|stripeRef
init|=
name|getOptimistic
argument_list|(
name|key
argument_list|,
name|written
argument_list|)
decl_stmt|;
if|if
condition|(
name|stripeRef
operator|==
literal|null
condition|)
block|{
comment|// 2) attempt lookup via pessimistic read and immediate conversion to write lock
name|stripeRef
operator|=
name|getPessimistic
argument_list|(
name|key
argument_list|,
name|written
argument_list|)
expr_stmt|;
if|if
condition|(
name|stripeRef
operator|==
literal|null
condition|)
block|{
comment|// 3) attempt lookup via exclusive write lock
name|stripeRef
operator|=
name|getExclusive
argument_list|(
name|key
argument_list|,
name|written
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|amortizeCleanup
condition|)
block|{
if|if
condition|(
name|written
operator|.
name|value
condition|)
block|{
comment|// TODO (AR) if we find that we are too frequently draining and it is expensive
comment|// then we could make the read and write drain paths both use the DRAIN_THRESHOLD
name|drainClearedReferences
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|readCount
operator|.
name|get
argument_list|()
operator|>=
name|READ_DRAIN_THRESHOLD
condition|)
block|{
name|drainClearedReferences
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// have we reached the threshold where we should clear
comment|// out any cleared WeakReferences from the stripes map
specifier|final
name|int
name|count
init|=
name|expiredReferenceReadCount
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>
name|MAX_EXPIRED_REFERENCE_READ_COUNT
operator|&&
name|expiredReferenceReadCount
operator|.
name|compareAndSet
argument_list|(
name|count
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|drainClearedReferences
argument_list|()
expr_stmt|;
block|}
block|}
comment|// check the weak reference before returning!
specifier|final
name|S
name|stripe
init|=
name|stripeRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|stripe
operator|!=
literal|null
condition|)
block|{
return|return
name|stripe
return|;
block|}
comment|// weak reference has expired in the mean time, so loop...
block|}
block|}
comment|/**      * Get the stripe via immediate conversion of an optimistic read lock to a write lock.      *      * @param key the stripe key      * @param written (OUT) will be set to true if {@link #stripes} was updated      *      * @return null if we could not perform an optimistic read, or a new object needed to be      *     created and we could not take the {@link #stripesLock} write lock immediately,      *     otherwise the stripe.      */
specifier|private
annotation|@
name|Nullable
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|getOptimistic
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|Holder
argument_list|<
name|Boolean
argument_list|>
name|written
parameter_list|)
block|{
comment|// optimistic read
specifier|final
name|long
name|stamp
init|=
name|stripesLock
operator|.
name|tryOptimisticRead
argument_list|()
decl_stmt|;
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|stripeRef
decl_stmt|;
try|try
block|{
name|stripeRef
operator|=
name|stripes
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// this can occur as we don't hold a lock, we just have a stamp for an optimistic read,
comment|// so `stripes` might be concurrently modified
return|return
literal|null
return|;
block|}
if|if
condition|(
name|stripeRef
operator|==
literal|null
operator|||
name|stripeRef
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
specifier|final
name|long
name|writeStamp
init|=
name|stripesLock
operator|.
name|tryConvertToWriteLock
argument_list|(
name|stamp
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeStamp
operator|!=
literal|0L
condition|)
block|{
specifier|final
name|boolean
name|wasGCd
init|=
name|stripeRef
operator|!=
literal|null
operator|&&
name|stripeRef
operator|.
name|get
argument_list|()
operator|==
literal|null
decl_stmt|;
try|try
block|{
name|stripeRef
operator|=
operator|new
name|WeakValueReference
argument_list|<>
argument_list|(
name|key
argument_list|,
name|creator
operator|.
name|apply
argument_list|(
name|key
argument_list|)
argument_list|,
name|referenceQueue
argument_list|)
expr_stmt|;
name|stripes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stripeRef
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stripesLock
operator|.
name|unlockWrite
argument_list|(
name|writeStamp
argument_list|)
expr_stmt|;
block|}
name|written
operator|.
name|value
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|wasGCd
operator|&&
operator|!
name|amortizeCleanup
condition|)
block|{
name|expiredReferenceReadCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// invalid conversion to write lock... small optimisation for the fall-through to #getPessimistic(K, Holder) in #get(K)
name|stripeRef
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|stripesLock
operator|.
name|validate
argument_list|(
name|stamp
argument_list|)
condition|)
block|{
if|if
condition|(
name|amortizeCleanup
condition|)
block|{
name|readCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// invalid optimistic read
name|stripeRef
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|stripeRef
return|;
block|}
comment|/**      * Get the stripe via immediate conversion of a read lock to a write lock.      *      * @param key the stripe key      * @param written (OUT) will be set to true if {@link #stripes} was updated      *      * @return null if a new object needed to be created and we could not take the {@link #stripesLock}      *     write lock immediately, otherwise the stripe.      */
specifier|private
annotation|@
name|Nullable
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|getPessimistic
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|Holder
argument_list|<
name|Boolean
argument_list|>
name|written
parameter_list|)
block|{
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|stripeRef
decl_stmt|;
name|long
name|stamp
init|=
name|stripesLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
try|try
block|{
name|stripeRef
operator|=
name|stripes
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|stripeRef
operator|==
literal|null
operator|||
name|stripeRef
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
specifier|final
name|long
name|writeStamp
init|=
name|stripesLock
operator|.
name|tryConvertToWriteLock
argument_list|(
name|stamp
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeStamp
operator|!=
literal|0L
condition|)
block|{
specifier|final
name|boolean
name|wasGCd
init|=
name|stripeRef
operator|!=
literal|null
operator|&&
name|stripeRef
operator|.
name|get
argument_list|()
operator|==
literal|null
decl_stmt|;
name|stamp
operator|=
name|writeStamp
expr_stmt|;
comment|// NOTE: this causes the write lock to be released in the finally further down
name|stripeRef
operator|=
operator|new
name|WeakValueReference
argument_list|<>
argument_list|(
name|key
argument_list|,
name|creator
operator|.
name|apply
argument_list|(
name|key
argument_list|)
argument_list|,
name|referenceQueue
argument_list|)
expr_stmt|;
name|stripes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stripeRef
argument_list|)
expr_stmt|;
name|written
operator|.
name|value
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|wasGCd
operator|&&
operator|!
name|amortizeCleanup
condition|)
block|{
name|expiredReferenceReadCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// invalid conversion to write lock... small optimisation for the fall-through to #getExclusive(K, Holder) in #get(K)
name|stripeRef
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|stripeRef
return|;
block|}
block|}
finally|finally
block|{
name|stripesLock
operator|.
name|unlock
argument_list|(
name|stamp
argument_list|)
expr_stmt|;
block|}
comment|// else (we don't need the lock on this path)
if|if
condition|(
name|amortizeCleanup
condition|)
block|{
name|readCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|stripeRef
return|;
block|}
comment|/**      * Get the stripe whilst holding the write lock.      *      * @param key the stripe key      * @param written (OUT) will be set to true if {@link #stripes} was updated      *      * @return the stripe      */
specifier|private
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|getExclusive
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|Holder
argument_list|<
name|Boolean
argument_list|>
name|written
parameter_list|)
block|{
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|stripeRef
decl_stmt|;
specifier|final
name|long
name|writeStamp
init|=
name|stripesLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
try|try
block|{
name|stripeRef
operator|=
name|stripes
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|stripeRef
operator|==
literal|null
operator|||
name|stripeRef
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
specifier|final
name|boolean
name|wasGCd
init|=
name|stripeRef
operator|!=
literal|null
operator|&&
name|stripeRef
operator|.
name|get
argument_list|()
operator|==
literal|null
decl_stmt|;
name|stripeRef
operator|=
operator|new
name|WeakValueReference
argument_list|<>
argument_list|(
name|key
argument_list|,
name|creator
operator|.
name|apply
argument_list|(
name|key
argument_list|)
argument_list|,
name|referenceQueue
argument_list|)
expr_stmt|;
name|stripes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|stripeRef
argument_list|)
expr_stmt|;
name|written
operator|.
name|value
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|wasGCd
operator|&&
operator|!
name|amortizeCleanup
condition|)
block|{
name|expiredReferenceReadCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|stripeRef
return|;
block|}
block|}
finally|finally
block|{
name|stripesLock
operator|.
name|unlockWrite
argument_list|(
name|writeStamp
argument_list|)
expr_stmt|;
block|}
comment|// else (we don't need the write lock on this path)
if|if
condition|(
name|amortizeCleanup
condition|)
block|{
name|readCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|stripeRef
return|;
block|}
comment|/**      * Removes cleared WeakReferences      * from the stripes map.      *      * If {@link #amortizeCleanup} is false, then      * all cleared WeakReferences will be removed,      * otherwise up to {@link #DRAIN_MAX} are removed.      */
specifier|private
name|void
name|drainClearedReferences
parameter_list|()
block|{
if|if
condition|(
name|draining
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// critical section
name|Reference
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|ref
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ref
operator|=
name|referenceQueue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|stripeRef
init|=
operator|(
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
operator|)
name|ref
decl_stmt|;
specifier|final
name|long
name|writeStamp
init|=
name|stripesLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
try|try
block|{
comment|// TODO(AR) it may be more performant to call #drainClearedReferences() at the beginning of #get(K) as oposed to the end, then we could avoid the extra check here which calls stripes#get(K)
comment|/*                         NOTE: we have to check that we have not added a new reference to replace an                         expired reference in #get(K) before calling #drainClearedReferences()                      */
specifier|final
name|WeakValueReference
argument_list|<
name|K
argument_list|,
name|S
argument_list|>
name|check
init|=
name|stripes
operator|.
name|get
argument_list|(
name|stripeRef
operator|.
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|check
operator|!=
literal|null
operator|&&
name|check
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|stripes
operator|.
name|remove
argument_list|(
name|stripeRef
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stripesLock
operator|.
name|unlockWrite
argument_list|(
name|writeStamp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|amortizeCleanup
operator|&&
operator|++
name|i
operator|==
name|DRAIN_MAX
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|amortizeCleanup
condition|)
block|{
name|readCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|draining
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Extends a WeakReference with a strong reference to a key.      *      * Used for cleaning up the {@link #stripes} from the {@link #referenceQueue}.      */
specifier|private
specifier|static
class|class
name|WeakValueReference
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|WeakReference
argument_list|<
name|V
argument_list|>
block|{
specifier|final
name|K
name|key
decl_stmt|;
specifier|public
name|WeakValueReference
parameter_list|(
specifier|final
name|K
name|key
parameter_list|,
specifier|final
name|V
name|referent
parameter_list|,
specifier|final
name|ReferenceQueue
argument_list|<
name|?
super|super
name|V
argument_list|>
name|q
parameter_list|)
block|{
name|super
argument_list|(
name|referent
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

