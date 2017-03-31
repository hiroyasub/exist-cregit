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
name|collections
operator|.
name|CollectionCache
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
name|management
operator|.
name|Agent
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

begin_class
specifier|public
class|class
name|CollectionCacheManager
implements|implements
name|CacheManager
implements|,
name|BrokerPoolService
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
name|CollectionCacheManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
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
specifier|final
name|String
name|brokerPoolId
decl_stmt|;
specifier|private
name|CollectionCache
name|collectionCache
decl_stmt|;
specifier|private
name|int
name|maxCacheSize
decl_stmt|;
specifier|public
name|CollectionCacheManager
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|CollectionCache
name|cache
parameter_list|)
block|{
name|this
operator|.
name|brokerPoolId
operator|=
name|pool
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|collectionCache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|collectionCache
operator|.
name|setCacheManager
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|int
name|cacheSize
init|=
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
decl_stmt|;
name|this
operator|.
name|maxCacheSize
operator|=
name|cacheSize
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
literal|"collection collectionCache will be using "
operator|+
name|this
operator|.
name|maxCacheSize
operator|+
literal|" bytes max."
argument_list|)
expr_stmt|;
block|}
comment|//TODO(AR) move to some start method...
name|registerMBean
argument_list|(
name|brokerPoolId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerCache
parameter_list|(
specifier|final
name|Cache
name|cache
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|deregisterCache
parameter_list|(
specifier|final
name|Cache
name|cache
parameter_list|)
block|{
name|this
operator|.
name|collectionCache
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|requestMem
parameter_list|(
specifier|final
name|Cache
name|cache
parameter_list|)
block|{
specifier|final
name|int
name|realSize
init|=
name|collectionCache
operator|.
name|getRealSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|realSize
operator|<
name|maxCacheSize
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
specifier|final
name|int
name|newCacheSize
init|=
operator|(
name|int
operator|)
operator|(
name|collectionCache
operator|.
name|getBuffers
argument_list|()
operator|*
name|collectionCache
operator|.
name|getGrowthFactor
argument_list|()
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Growing cache {} (a {}) from {} to {} bytes. Current memory usage = {}"
argument_list|,
name|collectionCache
operator|.
name|getName
argument_list|()
argument_list|,
name|collectionCache
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|collectionCache
operator|.
name|getBuffers
argument_list|()
argument_list|,
name|newCacheSize
argument_list|,
name|realSize
argument_list|)
expr_stmt|;
block|}
name|collectionCache
operator|.
name|resize
argument_list|(
name|newCacheSize
argument_list|)
expr_stmt|;
return|return
name|newCacheSize
return|;
block|}
block|}
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
literal|"Cache has reached max. size: "
operator|+
name|realSize
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkCaches
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkDistribution
parameter_list|()
block|{
block|}
comment|/**      * @return Maximum size of all Caches in bytes      */
annotation|@
name|Override
specifier|public
name|long
name|getMaxTotal
parameter_list|()
block|{
return|return
name|maxCacheSize
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
name|maxCacheSize
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
name|collectionCache
operator|.
name|getRealSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDefaultInitialSize
parameter_list|()
block|{
return|return
name|DEFAULT_CACHE_SIZE_BYTES
return|;
block|}
specifier|private
name|void
name|registerMBean
parameter_list|(
specifier|final
name|String
name|instanceName
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
name|instanceName
argument_list|,
literal|"org.exist.management."
operator|+
name|instanceName
operator|+
literal|":type=CollectionCacheManager"
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|CacheManager
argument_list|(
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
literal|"Exception while registering cache mbean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

