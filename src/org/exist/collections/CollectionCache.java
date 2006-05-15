begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|LRDCache
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
name|Long2ObjectHashMap
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
name|LRDCache
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
literal|1.25
argument_list|,
name|growthThreshold
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
name|super
operator|.
name|add
argument_list|(
name|collection
argument_list|,
name|initialRefCount
argument_list|)
expr_stmt|;
name|names
operator|.
name|put
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
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
name|long
name|key
init|=
name|names
operator|.
name|get
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|<
literal|0
condition|)
return|return
literal|null
return|;
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
comment|/** 	 * Overwritten to lock collections before they are removed. 	 */
specifier|protected
name|Cacheable
name|removeOne
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
name|Collection
name|old
decl_stmt|;
name|Lock
name|lock
decl_stmt|;
name|double
name|rd
init|=
literal|0
decl_stmt|,
name|minRd
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|bucket
init|=
operator|-
literal|1
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|old
operator|=
operator|(
name|Collection
operator|)
name|items
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|bucket
operator|=
name|i
expr_stmt|;
break|break;
block|}
else|else
block|{
name|lock
operator|=
name|old
operator|.
name|getLock
argument_list|()
expr_stmt|;
comment|// calculate the reference density
name|rd
operator|=
name|old
operator|.
name|getReferenceCount
argument_list|()
operator|/
operator|(
name|double
operator|)
operator|(
name|totalReferences
operator|-
name|old
operator|.
name|getTimestamp
argument_list|()
operator|)
expr_stmt|;
comment|// attempt to acquire a read lock on the collection.
comment|// the collection is not considered for removal if the lock
comment|// cannot be acquired immediately.
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
if|if
condition|(
operator|(
name|minRd
operator|<
literal|0
operator|||
name|rd
operator|<
name|minRd
operator|)
operator|&&
name|old
operator|.
name|allowUnload
argument_list|()
condition|)
block|{
name|minRd
operator|=
name|rd
expr_stmt|;
name|bucket
operator|=
name|i
expr_stmt|;
block|}
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|old
operator|=
operator|(
name|Collection
operator|)
name|items
index|[
name|bucket
index|]
expr_stmt|;
if|if
condition|(
name|old
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
name|old
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|old
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|remove
argument_list|(
name|old
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|old
operator|.
name|sync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|items
index|[
name|bucket
index|]
operator|=
name|item
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|,
name|item
argument_list|)
expr_stmt|;
name|accounting
operator|.
name|replacedPage
argument_list|(
name|item
argument_list|)
expr_stmt|;
if|if
condition|(
name|cacheManager
operator|!=
literal|null
operator|&&
name|accounting
operator|.
name|resizeNeeded
argument_list|()
condition|)
block|{
name|cacheManager
operator|.
name|requestMem
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|old
return|;
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
name|toString
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
argument_list|)
expr_stmt|;
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
name|size
condition|)
block|{
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
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Growing cache from "
operator|+
name|size
operator|+
literal|" to "
operator|+
name|newSize
argument_list|)
expr_stmt|;
name|Cacheable
index|[]
name|newItems
init|=
operator|new
name|Cacheable
index|[
name|newSize
index|]
decl_stmt|;
name|Long2ObjectHashMap
name|newMap
init|=
operator|new
name|Long2ObjectHashMap
argument_list|(
name|newSize
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|newItems
index|[
name|i
index|]
operator|=
name|items
index|[
name|i
index|]
expr_stmt|;
name|newMap
operator|.
name|put
argument_list|(
name|items
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
argument_list|,
name|items
index|[
name|i
index|]
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
name|items
index|[
name|i
index|]
operator|)
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|items
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|size
operator|=
name|newSize
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|newMap
expr_stmt|;
name|this
operator|.
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
name|size
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

