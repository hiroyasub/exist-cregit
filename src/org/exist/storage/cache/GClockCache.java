begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|cache
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
name|CacheManager
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

begin_comment
comment|/**  * Cache implementation based on the GClock algorithm.   *   * Implements a mixture between LFU (Last Frequently Used) and LRU   * (Last Recently Used) replacement policies. The class   * uses reference counts to track references to cached objects. Each call to the add   * method increments the reference count of the object.  *   * If the cache is full, the object to be removed is determined by decrementing the  * reference count for each object until an object with reference count = 0 is found.   *   * The implementation tends to replace younger objects first.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|GClockCache
implements|implements
name|Cache
block|{
specifier|protected
name|Cacheable
index|[]
name|items
decl_stmt|;
specifier|protected
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|size
decl_stmt|;
specifier|protected
name|Long2ObjectHashMap
name|map
decl_stmt|;
specifier|protected
name|int
name|used
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|hitsOld
init|=
literal|0
decl_stmt|;
specifier|protected
name|Accounting
name|accounting
decl_stmt|;
specifier|protected
name|double
name|growthFactor
decl_stmt|;
specifier|protected
name|CacheManager
name|cacheManager
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|fileName
init|=
literal|"unknown"
decl_stmt|;
specifier|public
name|GClockCache
parameter_list|(
name|int
name|size
parameter_list|,
name|double
name|growthFactor
parameter_list|,
name|double
name|growthThreshold
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|growthFactor
operator|=
name|growthFactor
expr_stmt|;
name|this
operator|.
name|items
operator|=
operator|new
name|Cacheable
index|[
name|size
index|]
expr_stmt|;
name|this
operator|.
name|map
operator|=
operator|new
name|Long2ObjectHashMap
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|accounting
operator|=
operator|new
name|Accounting
argument_list|(
name|growthThreshold
argument_list|)
expr_stmt|;
name|accounting
operator|.
name|setTotalSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
name|add
argument_list|(
name|item
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Cacheable
name|item
parameter_list|,
name|int
name|initialRefCount
parameter_list|)
block|{
name|Cacheable
name|old
init|=
operator|(
name|Cacheable
operator|)
name|map
operator|.
name|get
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|old
operator|.
name|incReferenceCount
argument_list|()
expr_stmt|;
return|return;
block|}
name|item
operator|.
name|setReferenceCount
argument_list|(
name|initialRefCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|<
name|size
condition|)
block|{
name|items
index|[
name|count
operator|++
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
name|used
operator|++
expr_stmt|;
block|}
else|else
block|{
name|removeOne
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Cacheable
name|get
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Cacheable
name|get
parameter_list|(
name|long
name|key
parameter_list|)
block|{
name|Cacheable
name|item
init|=
operator|(
name|Cacheable
operator|)
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|==
literal|null
condition|)
block|{
name|accounting
operator|.
name|missesIncrement
argument_list|()
expr_stmt|;
block|}
else|else
name|accounting
operator|.
name|hitIncrement
argument_list|()
expr_stmt|;
return|return
name|item
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
name|long
name|key
init|=
name|item
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Cacheable
name|cacheable
init|=
operator|(
name|Cacheable
operator|)
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheable
operator|==
literal|null
condition|)
return|return;
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
if|if
condition|(
name|items
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|items
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
operator|==
name|key
condition|)
block|{
name|items
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|used
operator|--
expr_stmt|;
return|return;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"item not found in list"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|flush
parameter_list|()
block|{
name|boolean
name|flushed
init|=
literal|false
decl_stmt|;
name|int
name|written
init|=
literal|0
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
if|if
condition|(
name|items
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|items
index|[
name|i
index|]
operator|.
name|sync
argument_list|(
literal|false
argument_list|)
condition|)
block|{
operator|++
name|written
expr_stmt|;
name|flushed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|//LOG.debug(written + " pages written to disk");
return|return
name|flushed
return|;
block|}
specifier|public
name|boolean
name|hasDirtyItems
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
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|items
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|items
index|[
name|i
index|]
operator|.
name|isDirty
argument_list|()
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|protected
name|Cacheable
name|removeOne
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
name|Cacheable
name|old
init|=
literal|null
decl_stmt|;
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
name|int
name|bucket
decl_stmt|;
do|do
block|{
name|bucket
operator|=
operator|-
literal|1
expr_stmt|;
comment|// decrease all reference counts by 1
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
name|old
operator|=
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
block|}
if|else if
condition|(
name|old
operator|.
name|decReferenceCount
argument_list|()
operator|<
literal|1
operator|&&
name|bucket
operator|<
literal|0
condition|)
block|{
name|bucket
operator|=
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bucket
operator|>
operator|-
literal|1
condition|)
block|{
name|old
operator|=
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
comment|//LOG.debug(fileName + " replacing " + old.getKey() + " for " + item.getKey());
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
name|old
operator|.
name|sync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|used
operator|++
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
name|removed
operator|=
literal|true
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|removed
condition|)
do|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
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
block|}
return|return
name|old
return|;
block|}
specifier|public
name|int
name|getBuffers
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|int
name|getUsedBuffers
parameter_list|()
block|{
return|return
name|used
return|;
block|}
specifier|public
name|double
name|getGrowthFactor
parameter_list|()
block|{
return|return
name|growthFactor
return|;
block|}
specifier|public
name|int
name|getHits
parameter_list|()
block|{
return|return
name|accounting
operator|.
name|getHits
argument_list|()
return|;
block|}
specifier|public
name|int
name|getFails
parameter_list|()
block|{
return|return
name|accounting
operator|.
name|getMisses
argument_list|()
return|;
block|}
specifier|public
name|int
name|getThrashing
parameter_list|()
block|{
return|return
name|accounting
operator|.
name|getThrashing
argument_list|()
return|;
block|}
specifier|public
name|void
name|setCacheManager
parameter_list|(
name|CacheManager
name|manager
parameter_list|)
block|{
name|this
operator|.
name|cacheManager
operator|=
name|manager
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
block|}
else|else
block|{
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
name|items
operator|=
name|newItems
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
specifier|protected
name|void
name|shrink
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
name|flush
argument_list|()
expr_stmt|;
name|items
operator|=
operator|new
name|Cacheable
index|[
name|newSize
index|]
expr_stmt|;
name|map
operator|=
operator|new
name|Long2ObjectHashMap
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
name|size
operator|=
name|newSize
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|used
operator|=
literal|0
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
specifier|public
name|int
name|getLoad
parameter_list|()
block|{
if|if
condition|(
name|hitsOld
operator|==
literal|0
condition|)
block|{
name|hitsOld
operator|=
name|accounting
operator|.
name|getHits
argument_list|()
expr_stmt|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
name|int
name|load
init|=
name|accounting
operator|.
name|getHits
argument_list|()
operator|-
name|hitsOld
decl_stmt|;
name|hitsOld
operator|=
name|accounting
operator|.
name|getHits
argument_list|()
expr_stmt|;
return|return
name|load
return|;
block|}
specifier|public
name|void
name|setFileName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|fileName
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
block|}
end_class

end_unit

