begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|beans
operator|.
name|ConstructorProperties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Caffeine
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|Weigher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|stats
operator|.
name|CacheStats
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|stats
operator|.
name|ConcurrentStatsCounter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|benmanes
operator|.
name|caffeine
operator|.
name|cache
operator|.
name|stats
operator|.
name|StatsCounter
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
name|Configuration
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Global cache for {@link org.exist.collections.Collection} objects.  *  * The CollectionCache safely permits concurrent access  * however appropriate Collection locks should be held  * on the actual collections when manipulating the  * CollectionCache  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|CollectionCache
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
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CACHE_SIZE_BYTES
init|=
literal|64
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// 64 MB
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_SIZE_ATTRIBUTE
init|=
literal|"collectionCache"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_CACHE_SIZE_BYTES
init|=
literal|"db-connection.collection-cache-mem"
decl_stmt|;
specifier|private
name|int
name|maxCacheSize
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Cache
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|>
name|cache
decl_stmt|;
specifier|private
name|StatsCounter
name|statsCounter
init|=
operator|new
name|ConcurrentStatsCounter
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|this
operator|.
name|maxCacheSize
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|configuration
operator|.
name|getInteger
argument_list|(
name|PROPERTY_CACHE_SIZE_BYTES
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|size
lambda|->
name|size
operator|>
literal|0
argument_list|)
operator|.
name|orElse
argument_list|(
name|DEFAULT_CACHE_SIZE_BYTES
argument_list|)
expr_stmt|;
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
literal|"CollectionsCache will use {} bytes max."
argument_list|,
name|this
operator|.
name|maxCacheSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
specifier|final
name|Weigher
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|>
name|collectionWeigher
init|=
parameter_list|(
name|uri
parameter_list|,
name|collection
parameter_list|)
lambda|->
name|collection
operator|.
name|getMemorySizeNoLock
argument_list|()
decl_stmt|;
name|this
operator|.
name|statsCounter
operator|=
operator|new
name|ConcurrentStatsCounter
argument_list|()
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|Caffeine
operator|.
expr|<
name|XmldbURI
operator|,
name|Collection
operator|>
name|newBuilder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
name|maxCacheSize
argument_list|)
operator|.
name|weigher
argument_list|(
name|collectionWeigher
argument_list|)
operator|.
name|recordStats
argument_list|(
parameter_list|()
lambda|->
name|statsCounter
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the maximum size of the cache in bytes      *      * @return maximum size of the cache in bytes      */
specifier|public
name|int
name|getMaxCacheSize
parameter_list|()
block|{
return|return
name|maxCacheSize
return|;
block|}
comment|/**      * Get a Snapshot of the Cache Statistics      *      * @return The cache statistics      */
specifier|public
name|Statistics
name|getStatistics
parameter_list|()
block|{
specifier|final
name|CacheStats
name|cacheStats
init|=
name|statsCounter
operator|.
name|snapshot
argument_list|()
decl_stmt|;
return|return
operator|new
name|Statistics
argument_list|(
name|cacheStats
operator|.
name|hitCount
argument_list|()
argument_list|,
name|cacheStats
operator|.
name|missCount
argument_list|()
argument_list|,
name|cacheStats
operator|.
name|loadSuccessCount
argument_list|()
argument_list|,
name|cacheStats
operator|.
name|loadFailureCount
argument_list|()
argument_list|,
name|cacheStats
operator|.
name|totalLoadTime
argument_list|()
argument_list|,
name|cacheStats
operator|.
name|evictionCount
argument_list|()
argument_list|,
name|cacheStats
operator|.
name|evictionWeight
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the Collection from the cache or creates the entry if it is not present      *      * @param collectionUri The URI of the Collection      * @param creator A function that creates (or supplies) the Collection for the URI      *      * @return The collection indicated by the URI      */
specifier|public
name|Collection
name|getOrCreate
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|,
specifier|final
name|Function
argument_list|<
name|XmldbURI
argument_list|,
name|Collection
argument_list|>
name|creator
parameter_list|)
block|{
comment|//NOTE: We must not store LockedCollections in the CollectionCache! So we call LockedCollection#unwrapLocked
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|(
name|collectionUri
argument_list|)
argument_list|,
name|uri
lambda|->
name|LockedCollection
operator|.
name|unwrapLocked
argument_list|(
name|creator
operator|.
name|apply
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the Collection from the cache or null if the Collection      * is not in the cache      *      * @param collectionUri The URI of the Collection      * @return The collection indicated by the URI or null otherwise      */
annotation|@
name|Nullable
specifier|public
name|Collection
name|getIfPresent
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
block|{
return|return
name|cache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|(
name|collectionUri
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Put's the Collection into the cache      *      * If an existing Collection object for the same URI exists      * in the Cache it will be overwritten      *      * @param collection      */
specifier|public
name|void
name|put
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
block|{
comment|//NOTE: We must not store LockedCollections in the CollectionCache! So we call LockedCollection#unwrapLocked
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|,
name|LockedCollection
operator|.
name|unwrapLocked
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes an entry from the cache      *      * @param collectionUri The URI of the Collection to remove from the Cache      */
specifier|public
name|void
name|invalidate
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|collectionUri
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes all entries from the Cache      */
specifier|public
name|void
name|invalidateAll
parameter_list|()
block|{
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
comment|/**      * Calculates the key for the Cache      *      * @param collectionUri The URI of the Collection      * @return the key for the Collection in the Cache      */
specifier|private
name|String
name|key
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
block|{
return|return
name|collectionUri
operator|.
name|getRawCollectionPath
argument_list|()
return|;
block|}
comment|/**      * Basically an eXist abstraction      * for {@link CacheStats}      *  Apache License Version 2.0      */
specifier|public
specifier|static
class|class
name|Statistics
block|{
specifier|private
specifier|final
name|long
name|hitCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|missCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|loadSuccessCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|loadFailureCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|totalLoadTime
decl_stmt|;
specifier|private
specifier|final
name|long
name|evictionCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|evictionWeight
decl_stmt|;
comment|/**          * @param hitCount the number of cache hits          * @param missCount the number of cache misses          * @param loadSuccessCount the number of successful cache loads          * @param loadFailureCount the number of failed cache loads          * @param totalLoadTime the total load time (success and failure)          * @param evictionCount the number of entries evicted from the cache          * @param evictionWeight the sum of weights of entries evicted from the cache          */
annotation|@
name|ConstructorProperties
argument_list|(
block|{
literal|"hitCount"
block|,
literal|"missCount"
block|,
literal|"loadSuccessCount"
block|,
literal|"loadFailureCount"
block|,
literal|"totalLoadTime"
block|,
literal|"evictionCount"
block|,
literal|"evictionWeight"
block|}
argument_list|)
specifier|public
name|Statistics
parameter_list|(
specifier|final
name|long
name|hitCount
parameter_list|,
specifier|final
name|long
name|missCount
parameter_list|,
specifier|final
name|long
name|loadSuccessCount
parameter_list|,
specifier|final
name|long
name|loadFailureCount
parameter_list|,
specifier|final
name|long
name|totalLoadTime
parameter_list|,
specifier|final
name|long
name|evictionCount
parameter_list|,
specifier|final
name|long
name|evictionWeight
parameter_list|)
block|{
name|this
operator|.
name|hitCount
operator|=
name|hitCount
expr_stmt|;
name|this
operator|.
name|missCount
operator|=
name|missCount
expr_stmt|;
name|this
operator|.
name|loadSuccessCount
operator|=
name|loadSuccessCount
expr_stmt|;
name|this
operator|.
name|loadFailureCount
operator|=
name|loadFailureCount
expr_stmt|;
name|this
operator|.
name|totalLoadTime
operator|=
name|totalLoadTime
expr_stmt|;
name|this
operator|.
name|evictionCount
operator|=
name|evictionCount
expr_stmt|;
name|this
operator|.
name|evictionWeight
operator|=
name|evictionWeight
expr_stmt|;
block|}
comment|/**          * Returns the number of times {@link Cache} lookup methods have returned a cached value.          *          * @return the number of times {@link Cache} lookup methods have returned a cached value          */
specifier|public
name|long
name|getHitCount
parameter_list|()
block|{
return|return
name|hitCount
return|;
block|}
comment|/**          * Returns the number of times {@link Cache} lookup methods have returned either a cached or          * uncached value. This is defined as {@code hitCount + missCount}.          *          * @return the {@code hitCount + missCount}          */
specifier|public
name|long
name|getRequestCount
parameter_list|()
block|{
return|return
name|hitCount
operator|+
name|missCount
return|;
block|}
comment|/**          * Returns the ratio of cache requests which were hits. This is defined as          * {@code hitCount / requestCount}, or {@code 1.0} when {@code requestCount == 0}. Note that          * {@code hitRate + missRate =~ 1.0}.          *          * @return the ratio of cache requests which were hits          */
specifier|public
name|double
name|getHitRate
parameter_list|()
block|{
specifier|final
name|long
name|requestCount
init|=
name|getRequestCount
argument_list|()
decl_stmt|;
return|return
name|requestCount
operator|==
literal|0
condition|?
literal|1.0
else|:
operator|(
name|double
operator|)
name|hitCount
operator|/
name|requestCount
return|;
block|}
comment|/**          * Returns the number of times {@link Cache} lookup methods have returned an uncached (newly          * loaded) value, or null. Multiple concurrent calls to {@link Cache} lookup methods on an absent          * value can result in multiple misses, all returning the results of a single cache load          * operation.          *          * @return the number of times {@link Cache} lookup methods have returned an uncached (newly          *         loaded) value, or null          */
specifier|public
name|long
name|getMissCount
parameter_list|()
block|{
return|return
name|missCount
return|;
block|}
comment|/**          * Returns the ratio of cache requests which were misses. This is defined as          * {@code missCount / requestCount}, or {@code 0.0} when {@code requestCount == 0}.          * Note that {@code hitRate + missRate =~ 1.0}. Cache misses include all requests which          * weren't cache hits, including requests which resulted in either successful or failed loading          * attempts, and requests which waited for other threads to finish loading. It is thus the case          * that {@code missCount&gt;= loadSuccessCount + loadFailureCount}. Multiple          * concurrent misses for the same key will result in a single load operation.          *          * @return the ratio of cache requests which were misses          */
specifier|public
name|double
name|getMissRate
parameter_list|()
block|{
specifier|final
name|long
name|requestCount
init|=
name|getRequestCount
argument_list|()
decl_stmt|;
return|return
name|requestCount
operator|==
literal|0
condition|?
literal|0.0
else|:
operator|(
name|double
operator|)
name|missCount
operator|/
name|requestCount
return|;
block|}
comment|/**          * Returns the total number of times that {@link Cache} lookup methods attempted to load new          * values. This includes both successful load operations, as well as those that threw exceptions.          * This is defined as {@code loadSuccessCount + loadFailureCount}.          *          * @return the {@code loadSuccessCount + loadFailureCount}          */
specifier|public
name|long
name|getLoadCount
parameter_list|()
block|{
return|return
name|loadSuccessCount
operator|+
name|loadFailureCount
return|;
block|}
comment|/**          * Returns the number of times {@link Cache} lookup methods have successfully loaded a new value.          * This is always incremented in conjunction with {@link #missCount}, though {@code missCount}          * is also incremented when an exception is encountered during cache loading (see          * {@link #loadFailureCount}). Multiple concurrent misses for the same key will result in a          * single load operation.          *          * @return the number of times {@link Cache} lookup methods have successfully loaded a new value          */
specifier|public
name|long
name|getLoadSuccessCount
parameter_list|()
block|{
return|return
name|loadSuccessCount
return|;
block|}
comment|/**          * Returns the number of times {@link Cache} lookup methods failed to load a new value, either          * because no value was found or an exception was thrown while loading. This is always incremented          * in conjunction with {@code missCount}, though {@code missCount} is also incremented when cache          * loading completes successfully (see {@link #loadSuccessCount}). Multiple concurrent misses for          * the same key will result in a single load operation.          *          * @return the number of times {@link Cache} lookup methods failed to load a new value          */
specifier|public
name|long
name|getLoadFailureCount
parameter_list|()
block|{
return|return
name|loadFailureCount
return|;
block|}
comment|/**          * Returns the ratio of cache loading attempts which threw exceptions. This is defined as          * {@code loadFailureCount / (loadSuccessCount + loadFailureCount)}, or {@code 0.0} when          * {@code loadSuccessCount + loadFailureCount == 0}.          *          * @return the ratio of cache loading attempts which threw exceptions          */
specifier|public
name|double
name|getLoadFailureRate
parameter_list|()
block|{
specifier|final
name|long
name|totalLoadCount
init|=
name|loadSuccessCount
operator|+
name|loadFailureCount
decl_stmt|;
return|return
name|totalLoadCount
operator|==
literal|0
condition|?
literal|0.0
else|:
operator|(
name|double
operator|)
name|loadFailureCount
operator|/
name|totalLoadCount
return|;
block|}
comment|/**          * Returns the total number of nanoseconds the cache has spent loading new values. This can be          * used to calculate the miss penalty. This value is increased every time {@code loadSuccessCount}          * or {@code loadFailureCount} is incremented.          *          * @return the total number of nanoseconds the cache has spent loading new values          */
specifier|public
name|long
name|getTotalLoadTime
parameter_list|()
block|{
return|return
name|totalLoadTime
return|;
block|}
comment|/**          * Returns the average time spent loading new values. This is defined as          * {@code totalLoadTime / (loadSuccessCount + loadFailureCount)}.          *          * @return the average time spent loading new values          */
specifier|public
name|double
name|getAverageLoadPenalty
parameter_list|()
block|{
specifier|final
name|long
name|totalLoadCount
init|=
name|loadSuccessCount
operator|+
name|loadFailureCount
decl_stmt|;
return|return
name|totalLoadCount
operator|==
literal|0
condition|?
literal|0.0
else|:
operator|(
name|double
operator|)
name|totalLoadTime
operator|/
name|totalLoadCount
return|;
block|}
comment|/**          * Returns the number of times an entry has been evicted. This count does not include manual          * {@linkplain Cache#invalidate invalidations}.          *          * @return the number of times an entry has been evicted          */
specifier|public
name|long
name|getEvictionCount
parameter_list|()
block|{
return|return
name|evictionCount
return|;
block|}
comment|/**          * Returns the sum of weights of evicted entries. This total does not include manual          * {@linkplain Cache#invalidate invalidations}.          *          * @return the sum of weights of evicted entities          */
specifier|public
name|long
name|getEvictionWeight
parameter_list|()
block|{
return|return
name|evictionWeight
return|;
block|}
block|}
block|}
end_class

end_unit

