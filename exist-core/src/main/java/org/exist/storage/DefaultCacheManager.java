begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

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
name|management
operator|.
name|Agent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|AgentFactory
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
name|Cache
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
name|Configuration
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
name|DatabaseConfigurationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

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

begin_comment
comment|/**  * CacheManager maintains a global memory pool available to all page caches. All caches start with a low default setting, but CacheManager can grow  * individual caches until the total memory is reached. Caches can also be shrinked if their "load" remains below a given threshold between check  * intervals.The check interval is determined by the global sync background thread.  *  * The class computes the available memory in terms of pages.  *  * @author  wolf  */
end_comment

begin_class
specifier|public
class|class
name|DefaultCacheManager
implements|implements
name|CacheManager
implements|,
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
name|DefaultCacheManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** The maximum fraction of the total memory that can be used by a single cache. */
specifier|public
specifier|final
specifier|static
name|double
name|MAX_MEM_USE
init|=
literal|0.9
decl_stmt|;
comment|/** The minimum size a cache needs to have to be considered for shrinking, defined in terms of a fraction of the overall memory. */
specifier|public
specifier|final
specifier|static
name|double
name|MIN_SHRINK_FACTOR
init|=
literal|0.5
decl_stmt|;
comment|/** The amount by which a large cache will be shrinked if other caches request a resize. */
specifier|public
specifier|final
specifier|static
name|double
name|SHRINK_FACTOR
init|=
literal|0.7
decl_stmt|;
comment|/**      * The minimum number of pages that must be read from a cache between check intervals to be not considered for shrinking. This is a measure for      * the "load" of the cache. Caches with high load will never be shrinked. A negative value means that shrinkage will not be performed.      */
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_SHRINK_THRESHOLD
init|=
literal|10000
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_SHRINK_THRESHOLD_STRING
init|=
literal|"10000"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CACHE_SIZE
init|=
literal|64
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_SIZE_ATTRIBUTE
init|=
literal|"cacheSize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CACHE_SIZE
init|=
literal|"db-connection.cache-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CACHE_CHECK_MAX_SIZE_STRING
init|=
literal|"true"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_CHECK_MAX_SIZE_ATTRIBUTE
init|=
literal|"checkMaxCacheSize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CACHE_CHECK_MAX_SIZE
init|=
literal|"db-connection.check-max-cache-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SHRINK_THRESHOLD_ATTRIBUTE
init|=
literal|"cacheShrinkThreshold"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SHRINK_THRESHOLD_PROPERTY
init|=
literal|"db-connection.cache-shrink-threshold"
decl_stmt|;
comment|/** Caches maintained by this class. */
specifier|private
name|List
argument_list|<
name|Cache
argument_list|>
name|caches
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|totalMem
decl_stmt|;
comment|/** The total maximum amount of pages shared between all caches. */
specifier|private
name|int
name|totalPageCount
decl_stmt|;
comment|/** The number of pages currently used by the active caches. */
specifier|private
name|int
name|currentPageCount
init|=
literal|0
decl_stmt|;
comment|/** The maximum number of pages that can be allocated by a single cache. */
specifier|private
name|int
name|maxCacheSize
decl_stmt|;
specifier|private
name|int
name|pageSize
decl_stmt|;
comment|/**      * The minimum number of pages that must be read from a cache between check intervals to be not considered for shrinking. This is a measure for      * the "load" of the cache. Caches with high load will never be shrinked. A negative value means that shrinkage will not be performed.      */
specifier|private
name|int
name|shrinkThreshold
init|=
name|DEFAULT_SHRINK_THRESHOLD
decl_stmt|;
comment|/**      * Signals that a resize had been requested by a cache, but the request could not be accepted during normal operations. The manager might try to      * shrink the largest cache during the next sync event.      */
specifier|private
name|Cache
name|lastRequest
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|instanceName
decl_stmt|;
specifier|public
name|DefaultCacheManager
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|instanceName
operator|=
name|pool
operator|.
name|getId
argument_list|()
expr_stmt|;
specifier|final
name|Configuration
name|configuration
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|int
name|cacheSize
decl_stmt|;
if|if
condition|(
operator|(
name|pageSize
operator|=
name|configuration
operator|.
name|getInteger
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_PAGE_SIZE
argument_list|)
operator|)
operator|<
literal|0
condition|)
block|{
comment|//TODO : should we share the page size with the native broker ?
name|pageSize
operator|=
name|BrokerPool
operator|.
name|DEFAULT_PAGE_SIZE
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|cacheSize
operator|=
name|configuration
operator|.
name|getInteger
argument_list|(
name|PROPERTY_CACHE_SIZE
argument_list|)
operator|)
operator|<
literal|0
condition|)
block|{
name|cacheSize
operator|=
name|DEFAULT_CACHE_SIZE
expr_stmt|;
block|}
name|shrinkThreshold
operator|=
name|configuration
operator|.
name|getInteger
argument_list|(
name|SHRINK_THRESHOLD_PROPERTY
argument_list|)
expr_stmt|;
name|totalMem
operator|=
name|cacheSize
operator|*
literal|1024L
operator|*
literal|1024L
expr_stmt|;
specifier|final
name|Boolean
name|checkMaxCache
init|=
operator|(
name|Boolean
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|PROPERTY_CACHE_CHECK_MAX_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkMaxCache
operator|==
literal|null
operator|||
name|checkMaxCache
condition|)
block|{
specifier|final
name|long
name|max
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
decl_stmt|;
name|long
name|maxCache
init|=
operator|(
name|max
operator|>=
operator|(
literal|768
operator|*
literal|1024
operator|*
literal|1024
operator|)
operator|)
condition|?
operator|(
name|max
operator|/
literal|2
operator|)
else|:
operator|(
name|max
operator|/
literal|3
operator|)
decl_stmt|;
if|if
condition|(
name|totalMem
operator|>
name|maxCache
condition|)
block|{
name|totalMem
operator|=
name|maxCache
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"The cacheSize=\""
operator|+
name|cacheSize
operator|+
literal|"\" setting in conf.xml is too large. Java has only "
operator|+
operator|(
name|max
operator|/
literal|1024
operator|)
operator|+
literal|"k available. Cache manager will not use more than "
operator|+
operator|(
name|totalMem
operator|/
literal|1024L
operator|)
operator|+
literal|"k "
operator|+
literal|"to avoid memory issues which may lead to database corruptions."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Checking of Max Cache Size disabled by user, this could cause memory issues which may lead to database corruptions if you don't have enough memory allocated to your JVM!"
argument_list|)
expr_stmt|;
block|}
name|int
name|buffers
init|=
operator|(
name|int
operator|)
operator|(
name|totalMem
operator|/
name|pageSize
operator|)
decl_stmt|;
name|this
operator|.
name|totalPageCount
operator|=
name|buffers
expr_stmt|;
name|this
operator|.
name|maxCacheSize
operator|=
operator|(
name|int
operator|)
operator|(
name|totalPageCount
operator|*
name|MAX_MEM_USE
operator|)
expr_stmt|;
specifier|final
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cache settings: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|totalMem
operator|/
literal|1024L
argument_list|)
operator|+
literal|"k; totalPages: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|totalPageCount
argument_list|)
operator|+
literal|"; maxCacheSize: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxCacheSize
argument_list|)
operator|+
literal|"; cacheShrinkThreshold: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|shrinkThreshold
argument_list|)
argument_list|)
expr_stmt|;
name|registerMBean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerCache
parameter_list|(
name|Cache
name|cache
parameter_list|)
block|{
name|currentPageCount
operator|+=
name|cache
operator|.
name|getBuffers
argument_list|()
expr_stmt|;
name|caches
operator|.
name|add
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|cache
operator|.
name|setCacheManager
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|registerMBean
argument_list|(
name|cache
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deregisterCache
parameter_list|(
name|Cache
name|cache
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Cache
argument_list|>
name|cacheIt
init|=
name|caches
operator|.
name|iterator
argument_list|()
init|;
name|cacheIt
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|cache
operator|==
name|cacheIt
operator|.
name|next
argument_list|()
condition|)
block|{
name|cache
operator|.
name|setCacheManager
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|cacheIt
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|currentPageCount
operator|-=
name|cache
operator|.
name|getBuffers
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|requestMem
parameter_list|(
name|Cache
name|cache
parameter_list|)
block|{
if|if
condition|(
name|currentPageCount
operator|>=
name|totalPageCount
condition|)
block|{
if|if
condition|(
name|cache
operator|.
name|getBuffers
argument_list|()
operator|<
name|maxCacheSize
condition|)
block|{
name|lastRequest
operator|=
name|cache
expr_stmt|;
block|}
comment|// no free pages available
comment|//            LOG.debug("Cache " + cache.getName() + " cannot be resized");
return|return
operator|(
operator|-
literal|1
operator|)
return|;
block|}
if|if
condition|(
operator|(
name|cache
operator|.
name|getGrowthFactor
argument_list|()
operator|>
literal|1.0
operator|)
operator|&&
operator|(
name|cache
operator|.
name|getBuffers
argument_list|()
operator|<
name|maxCacheSize
operator|)
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|currentPageCount
operator|>=
name|totalPageCount
condition|)
block|{
comment|// another cache has been resized. Give up
return|return
operator|(
operator|-
literal|1
operator|)
return|;
block|}
comment|// calculate new cache size
name|int
name|newCacheSize
init|=
operator|(
name|int
operator|)
operator|(
name|cache
operator|.
name|getBuffers
argument_list|()
operator|*
name|cache
operator|.
name|getGrowthFactor
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|newCacheSize
operator|>
name|maxCacheSize
condition|)
block|{
comment|// new cache size is too large: adjust
name|newCacheSize
operator|=
name|maxCacheSize
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|currentPageCount
operator|+
name|newCacheSize
operator|)
operator|>
name|totalPageCount
condition|)
block|{
comment|// new cache size exceeds total: adjust
name|newCacheSize
operator|=
name|cache
operator|.
name|getBuffers
argument_list|()
operator|+
operator|(
name|totalPageCount
operator|-
name|currentPageCount
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
specifier|final
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Growing cache "
operator|+
name|cache
operator|.
name|getName
argument_list|()
operator|+
literal|" (a "
operator|+
name|cache
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|") from "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|cache
operator|.
name|getBuffers
argument_list|()
argument_list|)
operator|+
literal|" to "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|newCacheSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|currentPageCount
operator|-=
name|cache
operator|.
name|getBuffers
argument_list|()
expr_stmt|;
comment|// resize the cache
name|cache
operator|.
name|resize
argument_list|(
name|newCacheSize
argument_list|)
expr_stmt|;
name|currentPageCount
operator|+=
name|newCacheSize
expr_stmt|;
comment|//                LOG.debug("currentPageCount = " + currentPageCount + "; max = " + totalPageCount);
return|return
operator|(
name|newCacheSize
operator|)
return|;
block|}
block|}
return|return
operator|(
operator|-
literal|1
operator|)
return|;
block|}
comment|/**      * Called from the global major sync event to check if caches can be shrinked. To be shrinked, the size of a cache needs to be larger than the      * factor defined by {@link #MIN_SHRINK_FACTOR} and its load needs to be lower than {@link #DEFAULT_SHRINK_THRESHOLD}.      *      * If shrinked, the cache will be reset to the default initial cache size.      */
annotation|@
name|Override
specifier|public
name|void
name|checkCaches
parameter_list|()
block|{
specifier|final
name|int
name|minSize
init|=
operator|(
name|int
operator|)
operator|(
name|totalPageCount
operator|*
name|MIN_SHRINK_FACTOR
operator|)
decl_stmt|;
name|Cache
name|cache
decl_stmt|;
name|int
name|load
decl_stmt|;
if|if
condition|(
name|shrinkThreshold
operator|>=
literal|0
condition|)
block|{
for|for
control|(
name|Cache
name|cach
range|:
name|caches
control|)
block|{
name|cache
operator|=
operator|(
name|Cache
operator|)
name|cach
expr_stmt|;
if|if
condition|(
name|cache
operator|.
name|getGrowthFactor
argument_list|()
operator|>
literal|1.0
condition|)
block|{
name|load
operator|=
name|cache
operator|.
name|getLoad
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|cache
operator|.
name|getBuffers
argument_list|()
operator|>
name|minSize
operator|)
operator|&&
operator|(
name|load
operator|<
name|shrinkThreshold
operator|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
specifier|final
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Shrinking cache: "
operator|+
name|cache
operator|.
name|getName
argument_list|()
operator|+
literal|" (a "
operator|+
name|cache
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|") to "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|cache
operator|.
name|getBuffers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|currentPageCount
operator|-=
name|cache
operator|.
name|getBuffers
argument_list|()
expr_stmt|;
name|cache
operator|.
name|resize
argument_list|(
name|getDefaultInitialSize
argument_list|()
argument_list|)
expr_stmt|;
name|currentPageCount
operator|+=
name|getDefaultInitialSize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkDistribution
parameter_list|()
block|{
if|if
condition|(
name|lastRequest
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|minSize
init|=
operator|(
name|int
operator|)
operator|(
name|totalPageCount
operator|*
name|MIN_SHRINK_FACTOR
operator|)
decl_stmt|;
name|Cache
name|cache
decl_stmt|;
for|for
control|(
name|Cache
name|cach
range|:
name|caches
control|)
block|{
name|cache
operator|=
operator|(
name|Cache
operator|)
name|cach
expr_stmt|;
if|if
condition|(
name|cache
operator|.
name|getBuffers
argument_list|()
operator|>=
name|minSize
condition|)
block|{
name|int
name|newSize
init|=
operator|(
name|int
operator|)
operator|(
name|cache
operator|.
name|getBuffers
argument_list|()
operator|*
name|SHRINK_FACTOR
operator|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
specifier|final
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Shrinking cache: "
operator|+
name|cache
operator|.
name|getName
argument_list|()
operator|+
literal|" (a "
operator|+
name|cache
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|") to "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|newSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|currentPageCount
operator|-=
name|cache
operator|.
name|getBuffers
argument_list|()
expr_stmt|;
name|cache
operator|.
name|resize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
name|currentPageCount
operator|+=
name|newSize
expr_stmt|;
break|break;
block|}
block|}
name|lastRequest
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * @return Maximum size of all Caches in pages      */
annotation|@
name|Override
specifier|public
name|long
name|getMaxTotal
parameter_list|()
block|{
return|return
operator|(
name|totalPageCount
operator|)
return|;
block|}
comment|/**      * @return Current size of all Caches in bytes      */
annotation|@
name|Override
specifier|public
name|long
name|getCurrentSize
parameter_list|()
block|{
return|return
name|currentPageCount
operator|*
name|pageSize
return|;
block|}
comment|/**      * @return Maximum size of a single Cache in bytes      */
annotation|@
name|Override
specifier|public
name|long
name|getMaxSingle
parameter_list|()
block|{
return|return
operator|(
name|maxCacheSize
operator|)
return|;
block|}
specifier|public
name|long
name|getTotalMem
parameter_list|()
block|{
return|return
operator|(
name|totalMem
operator|)
return|;
block|}
comment|/**      * Returns the default initial size for all caches.      *      * @return  Default initial size 64.      */
specifier|public
name|int
name|getDefaultInitialSize
parameter_list|()
block|{
return|return
operator|(
name|DEFAULT_CACHE_SIZE
operator|)
return|;
block|}
specifier|private
name|void
name|registerMBean
parameter_list|()
block|{
specifier|final
name|Agent
name|agent
init|=
name|AgentFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|agent
operator|.
name|addMBean
argument_list|(
operator|new
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|CacheManager
argument_list|(
name|instanceName
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while registering JMX CacheManager MBean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|registerMBean
parameter_list|(
specifier|final
name|Cache
name|cache
parameter_list|)
block|{
specifier|final
name|Agent
name|agent
init|=
name|AgentFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|agent
operator|.
name|addMBean
argument_list|(
operator|new
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|Cache
argument_list|(
name|instanceName
argument_list|,
name|cache
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while registering JMX Cache MBean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

