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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|ConcurrentHashMap
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
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  * A Lock Manager for Locks that are used across  * database instance functions  *  * Maintains Maps of {@link WeakReference<Lock>} by ID.  * There is a unique lock for each ID, and calls with the same  * ID will always return the same lock. Different IDs will always  * receive different locks.  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|LockManager
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|LockManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_COLLECTION_LOCK_CAPACITY
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|float
name|COLLECTION_LOCK_LOAD_FACTOR
init|=
literal|0.75f
decl_stmt|;
specifier|private
specifier|final
name|ReferenceQueue
argument_list|<
name|ReentrantReadWriteLock
argument_list|>
name|collectionLockReferences
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|WeakReference
argument_list|<
name|ReentrantReadWriteLock
argument_list|>
argument_list|>
name|collectionLocks
decl_stmt|;
specifier|public
name|LockManager
parameter_list|(
specifier|final
name|int
name|concurrencyLevel
parameter_list|)
block|{
name|this
operator|.
name|collectionLocks
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|(
name|INITIAL_COLLECTION_LOCK_CAPACITY
argument_list|,
name|COLLECTION_LOCK_LOAD_FACTOR
argument_list|,
name|concurrencyLevel
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionLockReferences
operator|=
operator|new
name|ReferenceQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Configured LockManager with concurrencyLevel={}"
argument_list|,
name|concurrencyLevel
argument_list|)
expr_stmt|;
block|}
comment|//TODO(AR) abstract getCollectionLock out as a StripedLock<T> where T is a String or other thing
comment|/**      * Retrieves a lock for a Collection      *      * This function is concerned with just the lock object      * and has no knowledge of the state of the lock. The only      * guarantee is that if this lock has not been requested before      * then it will be provided in the unlocked state      *      * @param collectionPath The path of the Collection for which a lock is requested      *      * @return A lock for the Collection      */
comment|//TODO(AR) make package private / protected
specifier|public
name|ReentrantReadWriteLock
name|getCollectionLock
parameter_list|(
specifier|final
name|String
name|collectionPath
parameter_list|)
block|{
comment|// calculate a value if not present or if the weak reference has expired
specifier|final
name|WeakReference
argument_list|<
name|ReentrantReadWriteLock
argument_list|>
name|collectionLockRef
init|=
name|collectionLocks
operator|.
name|compute
argument_list|(
name|collectionPath
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|drainClearedReferences
argument_list|(
name|collectionLockReferences
argument_list|,
name|collectionLocks
argument_list|)
expr_stmt|;
return|return
operator|new
name|WeakReference
argument_list|<>
argument_list|(
operator|new
name|ReentrantReadWriteLock
argument_list|(
name|key
argument_list|)
argument_list|,
name|collectionLockReferences
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|value
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// check the weak reference before returning!
specifier|final
name|ReentrantReadWriteLock
name|collectionLock
init|=
name|collectionLockRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|collectionLock
operator|!=
literal|null
condition|)
block|{
return|return
name|collectionLock
return|;
block|}
comment|// weak reference has expired in the mean time, regenerate
return|return
name|getCollectionLock
argument_list|(
name|collectionPath
argument_list|)
return|;
block|}
comment|//TODO(AR) Collection locks should be switched to Java's ReentrantReadWriteLock and must use the Fair Scheduler to get FIFO like ordering
comment|//See Concurrency of Operations on B-Trees - Bayer and Schkolnick 1977 - Solution 2
specifier|public
name|ManagedCollectionLock
name|acquireCollectionReadLock
parameter_list|(
specifier|final
name|XmldbURI
name|collectionPath
parameter_list|)
throws|throws
name|LockException
block|{
specifier|final
name|XmldbURI
index|[]
name|segments
init|=
name|collectionPath
operator|.
name|getPathSegments
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|'/'
operator|+
name|segments
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|ReentrantReadWriteLock
name|root
init|=
name|getCollectionLock
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to acquire READ_LOCK for: "
operator|+
name|path
argument_list|)
throw|;
block|}
name|ReentrantReadWriteLock
name|current
init|=
name|root
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|segments
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|path
operator|+=
literal|'/'
operator|+
name|segments
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
specifier|final
name|ReentrantReadWriteLock
name|son
init|=
name|getCollectionLock
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|son
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
condition|)
block|{
name|current
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to acquire READ_LOCK for: "
operator|+
name|path
argument_list|)
throw|;
block|}
name|current
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|current
operator|=
name|son
expr_stmt|;
block|}
specifier|final
name|ReentrantReadWriteLock
name|collectionReadLock
init|=
name|current
decl_stmt|;
return|return
operator|new
name|ManagedCollectionLock
argument_list|(
name|Either
operator|.
name|Left
argument_list|(
name|collectionReadLock
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|collectionReadLock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
argument_list|)
return|;
block|}
comment|//TODO(AR) there are several reasons we might lock a collection for writes
comment|// 1) When we also need to modify its parent:
comment|// 1.1) to remove a collection (which requires also modifying its parent)
comment|// 1.2) to add a new collection (which also requires modifying its parent)
comment|// 1.3) to rename a collection (which also requires modifying its parent)
comment|//... So we take read locks all the way down, util the parent collection which we write lock, and then we write lock the collection
comment|// 2) When we just need to modify its properties:
comment|// 2.1) to add/remove/rename the child documents of the collection
comment|// 2.2) to modify the collections metadata (permissions, timestamps etc)
comment|//... So we read lock all the way down until the actual collection which we write lock
specifier|public
name|ManagedCollectionLock
name|acquireCollectionWriteLock
parameter_list|(
specifier|final
name|XmldbURI
name|collectionPath
parameter_list|,
specifier|final
name|boolean
name|lockParent
parameter_list|)
throws|throws
name|LockException
block|{
specifier|final
name|XmldbURI
index|[]
name|segments
init|=
name|collectionPath
operator|.
name|getPathSegments
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|'/'
operator|+
name|segments
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Lock
operator|.
name|LockMode
name|rootMode
init|=
name|segments
operator|.
name|length
operator|==
literal|1
operator|||
operator|(
name|segments
operator|.
name|length
operator|==
literal|2
operator|&&
name|lockParent
operator|)
condition|?
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
else|:
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
decl_stmt|;
specifier|final
name|ReentrantReadWriteLock
name|root
init|=
name|getCollectionLock
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|acquire
argument_list|(
name|rootMode
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to acquire "
operator|+
name|rootMode
operator|.
name|name
argument_list|()
operator|+
literal|" for: "
operator|+
name|path
argument_list|)
throw|;
block|}
name|Lock
operator|.
name|LockMode
name|currentMode
init|=
name|rootMode
decl_stmt|;
name|ReentrantReadWriteLock
name|current
init|=
name|root
decl_stmt|;
name|ReentrantReadWriteLock
name|parent
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|lastSegmentIdx
init|=
name|segments
operator|.
name|length
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|segments
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|path
operator|+=
literal|'/'
operator|+
name|segments
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
specifier|final
name|Lock
operator|.
name|LockMode
name|sonMode
init|=
name|i
operator|==
name|lastSegmentIdx
operator|||
operator|(
name|i
operator|==
name|segments
operator|.
name|length
operator|-
literal|2
operator|&&
name|lockParent
operator|)
condition|?
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
else|:
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
decl_stmt|;
specifier|final
name|ReentrantReadWriteLock
name|son
init|=
name|getCollectionLock
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|son
operator|.
name|acquire
argument_list|(
name|sonMode
argument_list|)
condition|)
block|{
name|current
operator|.
name|release
argument_list|(
name|currentMode
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Unable to acquire "
operator|+
name|currentMode
operator|.
name|name
argument_list|()
operator|+
literal|" for: "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|i
operator|==
name|lastSegmentIdx
operator|&&
name|lockParent
operator|)
condition|)
block|{
name|current
operator|.
name|release
argument_list|(
name|currentMode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|=
name|current
expr_stmt|;
block|}
name|currentMode
operator|=
name|sonMode
expr_stmt|;
name|current
operator|=
name|son
expr_stmt|;
block|}
if|if
condition|(
name|lockParent
operator|&&
name|parent
operator|!=
literal|null
condition|)
block|{
comment|//we return two locks as a single managed lock, the first lock is the parent collection and the second is the actual collection
specifier|final
name|ReentrantReadWriteLock
name|parentCollectionLock
init|=
name|parent
decl_stmt|;
specifier|final
name|ReentrantReadWriteLock
name|collectionLock
init|=
name|current
decl_stmt|;
return|return
operator|new
name|ManagedCollectionLock
argument_list|(
name|Either
operator|.
name|Right
argument_list|(
operator|new
name|Tuple2
argument_list|<>
argument_list|(
name|parentCollectionLock
argument_list|,
name|collectionLock
argument_list|)
argument_list|)
argument_list|,
parameter_list|()
lambda|->
block|{
comment|//TODO(AR) should this order be inverted?
name|collectionLock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|parentCollectionLock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|ReentrantReadWriteLock
name|collectionLock
init|=
name|current
decl_stmt|;
return|return
operator|new
name|ManagedCollectionLock
argument_list|(
name|Either
operator|.
name|Left
argument_list|(
name|collectionLock
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|collectionLock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Removes any cleared references from a map of weak references      *      * @param referenceQueue The queue that holds notification of cleared references      * @param map The map from which to remove the cleared references      *      * @param<K> The key type of the map      * @param<V> The value type inside the {@link WeakReference<V>}      */
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|void
name|drainClearedReferences
parameter_list|(
specifier|final
name|ReferenceQueue
argument_list|<
name|V
argument_list|>
name|referenceQueue
parameter_list|,
specifier|final
name|ConcurrentMap
argument_list|<
name|K
argument_list|,
name|WeakReference
argument_list|<
name|V
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|Reference
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|ref
init|=
literal|null
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
specifier|final
name|WeakReference
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|lockRef
init|=
operator|(
name|WeakReference
argument_list|<
name|?
extends|extends
name|V
argument_list|>
operator|)
name|ref
decl_stmt|;
name|map
operator|.
name|values
argument_list|()
operator|.
name|remove
argument_list|(
name|lockRef
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

