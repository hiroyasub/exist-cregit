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
name|indexing
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
name|backup
operator|.
name|RawDataBackup
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
name|BrokerPoolServiceException
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
name|DBBroker
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
name|btree
operator|.
name|DBException
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Manages all custom indexes registered with the database instance.  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|IndexManager
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
name|IndexManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"modules"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_MODULE_ELEMENT_NAME
init|=
literal|"module"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEXER_MODULES_CLASS_ATTRIBUTE
init|=
literal|"class"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEXER_MODULES_ID_ATTRIBUTE
init|=
literal|"id"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_INDEXER_MODULES
init|=
literal|"indexer.modules"
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Index
argument_list|>
name|indexers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Configuration
operator|.
name|IndexModuleConfig
name|modConfigs
index|[]
decl_stmt|;
specifier|private
name|Path
name|dataDir
decl_stmt|;
specifier|private
name|AtomicLong
name|configurationTimestamp
init|=
operator|new
name|AtomicLong
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * @param pool   the BrokerPool representing the current database instance      */
specifier|public
name|IndexManager
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
specifier|private
name|void
name|configurationChanged
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|long
name|prev
init|=
name|configurationTimestamp
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|prev
operator|&&
name|configurationTimestamp
operator|.
name|compareAndSet
argument_list|(
name|prev
argument_list|,
name|now
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
comment|/**      * Get the timestamp of when the index manager's configuration was last      * updated.      *      * @return the timestamp of when the index managers configuration was      *      last updated.      */
specifier|public
name|long
name|getConfigurationTimestamp
parameter_list|()
block|{
return|return
name|configurationTimestamp
operator|.
name|get
argument_list|()
return|;
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
name|this
operator|.
name|modConfigs
operator|=
operator|(
name|Configuration
operator|.
name|IndexModuleConfig
index|[]
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|PROPERTY_INDEXER_MODULES
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataDir
operator|=
operator|(
name|Path
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
expr_stmt|;
name|configurationChanged
argument_list|()
expr_stmt|;
block|}
comment|/**      * Registers the indexes specified in      * the global configuration object, i.e. in the :      *<pre>      *&lt;modules&gt;      *&lt;module id="foo" class="bar" foo1="bar1" ... /&gt;      *&lt;/modules&gt;      *</pre>      * section of the configuration file.      */
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
try|try
block|{
if|if
condition|(
name|modConfigs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Configuration
operator|.
name|IndexModuleConfig
name|modConfig
range|:
name|modConfigs
control|)
block|{
specifier|final
name|String
name|className
init|=
name|modConfig
operator|.
name|getClassName
argument_list|()
decl_stmt|;
name|initIndex
argument_list|(
name|pool
argument_list|,
name|modConfig
operator|.
name|getId
argument_list|()
argument_list|,
name|modConfig
operator|.
name|getConfig
argument_list|()
argument_list|,
name|dataDir
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check if a structural index was configured. If not, create one based on default settings.
name|AbstractIndex
name|structural
init|=
operator|(
name|AbstractIndex
operator|)
name|indexers
operator|.
name|get
argument_list|(
name|StructuralIndex
operator|.
name|STRUCTURAL_INDEX_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|structural
operator|==
literal|null
condition|)
block|{
name|structural
operator|=
name|initIndex
argument_list|(
name|pool
argument_list|,
name|StructuralIndex
operator|.
name|STRUCTURAL_INDEX_ID
argument_list|,
literal|null
argument_list|,
name|dataDir
argument_list|,
name|StructuralIndex
operator|.
name|DEFAULT_CLASS
argument_list|)
expr_stmt|;
if|if
condition|(
name|structural
operator|!=
literal|null
condition|)
block|{
name|structural
operator|.
name|setName
argument_list|(
name|StructuralIndex
operator|.
name|STRUCTURAL_INDEX_ID
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BrokerPoolServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|configurationChanged
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|AbstractIndex
name|initIndex
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|Element
name|config
parameter_list|,
specifier|final
name|Path
name|dataDir
parameter_list|,
specifier|final
name|String
name|className
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|AbstractIndex
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Class "
operator|+
name|className
operator|+
literal|" does not implement "
operator|+
name|AbstractIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|AbstractIndex
name|index
init|=
operator|(
name|AbstractIndex
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|index
operator|.
name|configure
argument_list|(
name|pool
argument_list|,
name|dataDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|index
operator|.
name|open
argument_list|()
expr_stmt|;
name|indexers
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered index "
operator|+
name|className
operator|+
literal|" as "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Class "
operator|+
name|className
operator|+
literal|" not found. Cannot configure index."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalAccessException
decl||
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while configuring index "
operator|+
name|className
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Index
name|registerIndex
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|index
operator|.
name|open
argument_list|()
expr_stmt|;
name|indexers
operator|.
name|put
argument_list|(
name|index
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered index "
operator|+
name|index
operator|.
name|getClass
argument_list|()
operator|+
literal|" as "
operator|+
name|index
operator|.
name|getIndexId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|configurationChanged
argument_list|()
expr_stmt|;
return|return
name|index
return|;
block|}
specifier|public
name|void
name|unregisterIndex
parameter_list|(
specifier|final
name|Index
name|index
parameter_list|)
throws|throws
name|DBException
block|{
name|indexers
operator|.
name|remove
argument_list|(
name|index
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unregistered index "
operator|+
name|index
operator|.
name|getClass
argument_list|()
operator|+
literal|" as "
operator|+
name|index
operator|.
name|getIndexId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|configurationChanged
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the {@link org.exist.storage.BrokerPool} on with this IndexManager operates.      *      * @return the broker pool      */
specifier|public
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
comment|/**      * Returns an iterator over the registered indexes.      *      * @return the iterator      */
specifier|protected
name|Iterator
argument_list|<
name|Index
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|indexers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * Returns the index registered with the provided ID.      *      * @param indexId the ID      * @return the index      */
specifier|public
specifier|synchronized
name|Index
name|getIndexById
parameter_list|(
specifier|final
name|String
name|indexId
parameter_list|)
block|{
return|return
name|indexers
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|indexer
lambda|->
name|indexer
operator|.
name|getIndexId
argument_list|()
operator|.
name|equals
argument_list|(
name|indexId
argument_list|)
argument_list|)
operator|.
name|findFirst
argument_list|()
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns the index registered with the provided human-readable name.      *      * @param indexName the name      * @return the index      */
specifier|public
specifier|synchronized
name|Index
name|getIndexByName
parameter_list|(
specifier|final
name|String
name|indexName
parameter_list|)
block|{
return|return
name|indexers
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
return|;
block|}
comment|/**      * Returns a set of IndexWorkers, one for each registered index. The      * returned IndexWorkers are used by the DBBroker instances to perform the      * actual indexing work.      *      * @return set of IndexWorkers      */
specifier|synchronized
name|List
argument_list|<
name|IndexWorker
argument_list|>
name|getWorkers
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|indexers
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|index
lambda|->
name|index
operator|.
name|getWorker
argument_list|(
name|broker
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Shutdowns all registered indexes by calling {@link org.exist.indexing.Index#close()}      * on them.      *      * @throws DBException      */
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Index
argument_list|>
name|i
init|=
name|iterator
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
name|Index
name|index
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BrokerPoolServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Call indexes to flush all data to disk.      *      * @throws DBException      */
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Index
argument_list|>
name|i
init|=
name|iterator
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
name|Index
name|index
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|index
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Physically destroy the registered indexes by calling {@link org.exist.indexing.Index#remove()}      * on them.      *      * @throws DBException      */
specifier|public
name|void
name|removeIndexes
parameter_list|()
throws|throws
name|DBException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Index
argument_list|>
name|i
init|=
name|iterator
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
name|Index
name|index
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|index
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Reopens the registered index in case they have been closed by a previous operation      * such as {@link org.exist.indexing.Index#close()} by calling {@link org.exist.indexing.Index#open()}      * on them.      *      * @throws DatabaseConfigurationException      */
specifier|public
name|void
name|reopenIndexes
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Index
argument_list|>
name|i
init|=
name|iterator
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
name|Index
name|index
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|index
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|backupToArchive
parameter_list|(
specifier|final
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Index
argument_list|>
name|i
init|=
name|iterator
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
name|Index
name|index
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|instanceof
name|RawBackupSupport
condition|)
block|{
operator|(
operator|(
name|RawBackupSupport
operator|)
name|index
operator|)
operator|.
name|backupToArchive
argument_list|(
name|backup
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
