begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|Cacheable
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
name|Lock
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
comment|/**  * Global cache for {@link org.exist.collections.Collection} objects. The  * cache is owned by {@link org.exist.storage.index.CollectionStore}. It is not  * synchronized. Thus a lock should be obtained on the collection store before  * accessing the cache.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CollectionCache
extends|extends
name|LRUCache
block|{
specifier|private
name|Object2LongHashMap
name|names
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|CollectionCache
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|blockBuffers
parameter_list|,
name|double
name|growthThreshold
parameter_list|)
block|{
name|super
argument_list|(
name|blockBuffers
argument_list|,
literal|2.0
argument_list|,
literal|0.000001
argument_list|,
name|CacheManager
operator|.
name|DATA_CACHE
argument_list|)
expr_stmt|;
name|this
operator|.
name|names
operator|=
operator|new
name|Object2LongHashMap
argument_list|(
name|blockBuffers
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|setFileName
argument_list|(
literal|"collection cache"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
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
specifier|public
name|void
name|add
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|int
name|initialRefCount
parameter_list|)
block|{
comment|// don't cache the collection during initialization: SecurityManager is not yet online
if|if
condition|(
name|pool
operator|.
name|isInitializing
argument_list|()
condition|)
return|return;
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
specifier|public
name|Collection
name|get
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
return|return
operator|(
name|Collection
operator|)
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
operator|(
name|Collection
operator|)
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * Overwritten to lock collections before they are removed.      */
specifier|protected
name|void
name|removeOne
parameter_list|(
name|Cacheable
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
name|Cacheable
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
name|Cacheable
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
name|Collection
name|old
init|=
operator|(
name|Collection
operator|)
name|cached
decl_stmt|;
specifier|final
name|Lock
name|lock
init|=
name|old
operator|.
name|getLock
argument_list|()
decl_stmt|;
if|if
condition|(
name|lock
operator|.
name|attempt
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
condition|)
block|{
try|try
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
name|old
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
name|old
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
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|remove
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
specifier|final
name|Collection
name|col
init|=
operator|(
name|Collection
operator|)
name|item
decl_stmt|;
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
name|col
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|pool
operator|.
name|getConfigurationManager
argument_list|()
operator|!=
literal|null
condition|)
comment|// might be null during db initialization
block|{
name|pool
operator|.
name|getConfigurationManager
argument_list|()
operator|.
name|invalidate
argument_list|(
name|col
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
operator|(
name|Collection
operator|)
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
specifier|public
name|void
name|resize
parameter_list|(
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Growing collection cache to "
operator|+
name|newSize
argument_list|)
expr_stmt|;
name|SequencedLongHashMap
argument_list|<
name|Cacheable
argument_list|>
name|newMap
init|=
operator|new
name|SequencedLongHashMap
argument_list|<
name|Cacheable
argument_list|>
argument_list|(
name|newSize
operator|*
literal|2
argument_list|)
decl_stmt|;
name|Object2LongHashMap
name|newNames
init|=
operator|new
name|Object2LongHashMap
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
name|SequencedLongHashMap
operator|.
name|Entry
argument_list|<
name|Cacheable
argument_list|>
name|next
init|=
name|map
operator|.
name|getFirstEntry
argument_list|()
decl_stmt|;
name|Cacheable
name|cacheable
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|cacheable
operator|=
name|next
operator|.
name|getValue
argument_list|()
expr_stmt|;
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
operator|(
operator|(
name|Collection
operator|)
name|cacheable
operator|)
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
name|next
operator|=
name|next
operator|.
name|getNext
argument_list|()
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
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

