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
name|storage
operator|.
name|cache
package|;
end_package

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

begin_comment
comment|/**  * A cache implementation based on a Least Reference Density (LRD)  * replacement policy.  *   * The class maintains a global reference counter, containing the sum of all  * references in the cache. Each object has a timestamp, which is equal to  * the number of global references at the time, the object has been added to  * the cache.  *   * If the cache is full, the object with the least reference density is removed.  * The reference density is computed as the ratio between the object's reference  * counter and the number of references added since the object has been included  * into the cache, i.e. RC(i) / (GR - TS(i)).  *   * @author wolf  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|LRDCache
parameter_list|<
name|T
extends|extends
name|Cacheable
parameter_list|>
extends|extends
name|GClockCache
argument_list|<
name|T
argument_list|>
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
name|LRDCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxReferences
decl_stmt|;
specifier|private
specifier|final
name|int
name|ageingPeriod
decl_stmt|;
specifier|private
name|int
name|totalReferences
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|nextCleanup
decl_stmt|;
specifier|public
name|LRDCache
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|cacheableClazz
parameter_list|,
specifier|final
name|int
name|size
parameter_list|,
specifier|final
name|double
name|growthFactor
parameter_list|,
specifier|final
name|double
name|growthThreshold
parameter_list|,
specifier|final
name|CacheType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|cacheableClazz
argument_list|,
name|size
argument_list|,
name|growthFactor
argument_list|,
name|growthThreshold
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|maxReferences
operator|=
name|size
operator|*
literal|10000
expr_stmt|;
name|ageingPeriod
operator|=
name|size
operator|*
literal|5000
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|T
name|item
parameter_list|,
specifier|final
name|int
name|initialRefCount
parameter_list|)
block|{
specifier|final
name|T
name|old
init|=
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
name|totalReferences
operator|++
expr_stmt|;
block|}
else|else
block|{
name|item
operator|.
name|setReferenceCount
argument_list|(
name|initialRefCount
argument_list|)
expr_stmt|;
name|item
operator|.
name|setTimestamp
argument_list|(
name|totalReferences
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
name|totalReferences
operator|+=
name|initialRefCount
expr_stmt|;
block|}
if|if
condition|(
name|totalReferences
operator|>
name|maxReferences
condition|)
block|{
name|cleanup
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|totalReferences
operator|>
name|nextCleanup
condition|)
block|{
name|ageReferences
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|T
name|removeOne
parameter_list|(
specifier|final
name|T
name|item
parameter_list|)
block|{
name|T
name|old
decl_stmt|;
name|double
name|rd
init|=
literal|0
decl_stmt|;
name|double
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
specifier|final
name|int
name|len
init|=
name|items
operator|.
name|length
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
name|len
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
break|break;
block|}
else|else
block|{
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
block|}
block|}
if|if
condition|(
name|bucket
operator|<
literal|0
condition|)
block|{
name|bucket
operator|=
literal|0
expr_stmt|;
block|}
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
comment|//                accounting.stats();
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
comment|/** 	 * Periodically adjust items with large reference counts to give 	 * younger items a chance to survive. 	 */
specifier|private
name|void
name|ageReferences
parameter_list|()
block|{
specifier|final
name|int
name|limit
init|=
name|ageingPeriod
operator|/
literal|10
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
specifier|final
name|T
name|item
init|=
name|items
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|refCount
init|=
name|item
operator|.
name|getReferenceCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|refCount
operator|>
name|limit
condition|)
block|{
name|item
operator|.
name|setReferenceCount
argument_list|(
name|refCount
operator|-
name|limit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|.
name|setReferenceCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|nextCleanup
operator|+=
name|ageingPeriod
expr_stmt|;
block|}
comment|/** 	 * Periodically reset all reference counts to 1. 	 */
specifier|protected
name|void
name|cleanup
parameter_list|()
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
literal|"totalReferences = "
operator|+
name|totalReferences
operator|+
literal|"; maxReferences = "
operator|+
name|maxReferences
argument_list|)
expr_stmt|;
block|}
name|totalReferences
operator|=
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|T
name|item
init|=
name|items
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|item
operator|.
name|setReferenceCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|item
operator|.
name|setTimestamp
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|nextCleanup
operator|=
name|totalReferences
operator|+
name|ageingPeriod
expr_stmt|;
block|}
block|}
end_class

end_unit

