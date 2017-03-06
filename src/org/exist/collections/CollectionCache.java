begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|NotThreadSafe
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
name|storage
operator|.
name|BrokerPool
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
name|BrokerPoolService
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
name|CacheManager
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
name|cache
operator|.
name|LRUCache
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
name|*
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
name|hashtable
operator|.
name|Object2LongHashMap
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
name|hashtable
operator|.
name|SequencedLongHashMap
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

begin_comment
comment|/**  * Global cache for {@link org.exist.collections.Collection} objects. The  * cache is owned by {@link org.exist.storage.index.CollectionStore}.  *  * It is not synchronized. Thus a lock should be obtained on the collection store before  * accessing the cache. For the synchronization purposes of this object,  * {@link LockManager#acquireCollectionCacheLock()} or {@link LockManager#tryCollectionCacheLock()} may be used  *   * @author wolf  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|CollectionCache
extends|extends
name|LRUCache
argument_list|<
name|Collection
argument_list|>
implements|implements
name|BrokerPoolService
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
name|CollectionCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|Object2LongHashMap
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
specifier|public
name|CollectionCache
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|int
name|blockBuffers
parameter_list|,
specifier|final
name|double
name|growthThreshold
parameter_list|)
block|{
name|super
argument_list|(
literal|"collection cache"
argument_list|,
name|blockBuffers
argument_list|,
literal|2.0
argument_list|,
name|growthThreshold
argument_list|,
name|CacheManager
operator|.
name|DATA_CACHE
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|names
operator|=
operator|new
name|Object2LongHashMap
argument_list|<>
argument_list|(
name|blockBuffers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
block|{
name|add
argument_list|(
name|collection
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Collection
name|c
parameter_list|,
specifier|final
name|int
name|initialRefCount
parameter_list|)
block|{
comment|// don't cache the collection during initialization: SecurityManager is not yet online
if|if
condition|(
operator|!
name|pool
operator|.
name|isOperational
argument_list|()
condition|)
block|{
return|return;
block|}
comment|//NOTE: We must not store LockedCollections in the CollectionCache! So we call LockedCollection#unwrapLocked
specifier|final
name|Collection
name|collection
init|=
name|LockedCollection
operator|.
name|unwrapLocked
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|super
operator|.
name|add
argument_list|(
name|collection
argument_list|,
name|initialRefCount
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
decl_stmt|;
name|names
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|collection
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
name|get
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|collection
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|get
parameter_list|(
specifier|final
name|XmldbURI
name|name
parameter_list|)
block|{
specifier|final
name|long
name|key
init|=
name|names
operator|.
name|get
argument_list|(
name|name
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|// TODO(AR) we have a mix of concerns here, we should not involve collection locking in the operation of the cache or invalidating the collectionConfiguration
comment|/**      * Overwritten to lock collections before they are removed.      */
annotation|@
name|Override
specifier|protected
name|void
name|removeOne
parameter_list|(
specifier|final
name|Collection
name|item
parameter_list|)
block|{
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
name|SequencedLongHashMap
operator|.
name|Entry
argument_list|<
name|Collection
argument_list|>
name|next
init|=
name|map
operator|.
name|getFirstEntry
argument_list|()
decl_stmt|;
name|int
name|tries
init|=
literal|0
decl_stmt|;
do|do
block|{
specifier|final
name|Collection
name|cached
init|=
name|next
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|cached
operator|.
name|getKey
argument_list|()
operator|!=
name|item
operator|.
name|getKey
argument_list|()
condition|)
block|{
specifier|final
name|LockManager
name|lockManager
init|=
name|pool
operator|.
name|getLockManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|ManagedCollectionLock
name|cachedReadLock
init|=
name|lockManager
operator|.
name|tryCollectionReadLock
argument_list|(
name|cached
operator|.
name|getURI
argument_list|()
argument_list|)
init|)
block|{
if|if
condition|(
name|cached
operator|.
name|allowUnload
argument_list|()
condition|)
block|{
if|if
condition|(
name|pool
operator|.
name|getConfigurationManager
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// might be null during db initialization
name|pool
operator|.
name|getConfigurationManager
argument_list|()
operator|.
name|invalidate
argument_list|(
name|cached
operator|.
name|getURI
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|names
operator|.
name|remove
argument_list|(
name|cached
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
name|cached
operator|.
name|sync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|cached
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
comment|// not a problem, we only attempted the lock with `tryCollectionReadLock`!
block|}
block|}
if|if
condition|(
operator|!
name|removed
condition|)
block|{
name|next
operator|=
name|next
operator|.
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
operator|&&
name|tries
operator|<
literal|2
condition|)
block|{
name|next
operator|=
name|map
operator|.
name|getFirstEntry
argument_list|()
expr_stmt|;
name|tries
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unable to remove entry"
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
operator|!
name|removed
condition|)
do|;
name|cacheManager
operator|.
name|requestMem
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
specifier|final
name|Collection
name|item
parameter_list|)
block|{
name|super
operator|.
name|remove
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|names
operator|.
name|remove
argument_list|(
name|item
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// might be null during db initialization
if|if
condition|(
name|pool
operator|.
name|getConfigurationManager
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|getConfigurationManager
argument_list|()
operator|.
name|invalidate
argument_list|(
name|item
operator|.
name|getURI
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Compute and return the in-memory size of all collections      * currently contained in this cache.      *      * @see org.exist.storage.CollectionCacheManager      * @return in-memory size in bytes.      */
specifier|public
name|int
name|getRealSize
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Long
argument_list|>
name|i
init|=
name|names
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Collection
name|collection
init|=
name|get
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|size
operator|+=
name|collection
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resize
parameter_list|(
specifier|final
name|int
name|newSize
parameter_list|)
block|{
if|if
condition|(
name|newSize
operator|<
name|max
condition|)
block|{
name|shrink
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
else|else
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
name|debug
argument_list|(
literal|"Growing collection cache to "
operator|+
name|newSize
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SequencedLongHashMap
argument_list|<
name|Collection
argument_list|>
name|newMap
init|=
operator|new
name|SequencedLongHashMap
argument_list|<>
argument_list|(
name|newSize
operator|*
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|Object2LongHashMap
argument_list|<
name|String
argument_list|>
name|newNames
init|=
operator|new
name|Object2LongHashMap
argument_list|<>
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
for|for
control|(
name|SequencedLongHashMap
operator|.
name|Entry
argument_list|<
name|Collection
argument_list|>
name|next
init|=
name|map
operator|.
name|getFirstEntry
argument_list|()
init|;
name|next
operator|!=
literal|null
condition|;
name|next
operator|=
name|next
operator|.
name|getNext
argument_list|()
control|)
block|{
specifier|final
name|Collection
name|cacheable
init|=
name|next
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|newMap
operator|.
name|put
argument_list|(
name|cacheable
operator|.
name|getKey
argument_list|()
argument_list|,
name|cacheable
argument_list|)
expr_stmt|;
name|newNames
operator|.
name|put
argument_list|(
name|cacheable
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|,
name|cacheable
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|max
operator|=
name|newSize
expr_stmt|;
name|map
operator|=
name|newMap
expr_stmt|;
name|names
operator|=
name|newNames
expr_stmt|;
name|accounting
operator|.
name|reset
argument_list|()
expr_stmt|;
name|accounting
operator|.
name|setTotalSize
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|shrink
parameter_list|(
specifier|final
name|int
name|newSize
parameter_list|)
block|{
name|super
operator|.
name|shrink
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
name|names
operator|=
operator|new
name|Object2LongHashMap
argument_list|<>
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

