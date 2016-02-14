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
name|indexing
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

begin_comment
comment|/**  * Manages all custom indexes registered with the database instance.  */
end_comment

begin_class
specifier|public
class|class
name|IndexManager
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
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Index
argument_list|>
name|indexers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Index
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Constructs a new IndexManager and registers the indexes specified in      * the global configuration object, i.e. in the :      *<pre>      *&lt;modules&gt;      *&lt;module id="foo" class="bar" foo1="bar1" ... /&gt;      *&lt;/modules&gt;      *</pre>      * section of the configuration file.      *      * @param pool the BrokerPool representing the current database instance      * @param config the configuration object      * @throws DatabaseConfigurationException      */
specifier|public
name|IndexManager
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
specifier|final
name|Configuration
operator|.
name|IndexModuleConfig
name|modConfigs
index|[]
init|=
operator|(
name|Configuration
operator|.
name|IndexModuleConfig
index|[]
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_INDEXER_MODULES
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dataDir
init|=
operator|(
name|Path
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|modConfigs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
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
specifier|private
name|AbstractIndex
name|initIndex
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|id
parameter_list|,
name|Element
name|config
parameter_list|,
name|Path
name|dataDir
parameter_list|,
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
catch|catch
parameter_list|(
specifier|final
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
return|return
name|index
return|;
block|}
comment|/**      * Returns the {@link org.exist.storage.BrokerPool} on with this IndexManager operates.      *       * @return the broker pool      */
specifier|public
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
comment|/**      * Returns an iterator over the registered indexes.      *       * @return the iterator      */
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
comment|/**       * Returns the index registered with the provided ID.      *       * @param indexId the ID      * @return the index      */
specifier|public
specifier|synchronized
name|Index
name|getIndexById
parameter_list|(
name|String
name|indexId
parameter_list|)
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
name|indexer
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexId
operator|.
name|equals
argument_list|(
name|indexer
operator|.
name|getIndexId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|indexer
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**       * Returns the index registered with the provided human-readable name.      * @param indexName the name      * @return the index      */
specifier|public
specifier|synchronized
name|Index
name|getIndexByName
parameter_list|(
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
specifier|protected
specifier|synchronized
name|List
argument_list|<
name|IndexWorker
argument_list|>
name|getWorkers
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|IndexWorker
argument_list|>
name|workerList
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexWorker
argument_list|>
argument_list|(
name|indexers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Index
name|index
range|:
name|indexers
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|IndexWorker
name|worker
init|=
name|index
operator|.
name|getWorker
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|worker
operator|!=
literal|null
condition|)
block|{
name|workerList
operator|.
name|add
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|workerList
return|;
block|}
comment|/**      * Shutdowns all registered indexes by calling {@link org.exist.indexing.Index#close()}      * on them.      *      * @throws DBException      */
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|DBException
block|{
name|Index
name|index
decl_stmt|;
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
name|index
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|Index
name|index
decl_stmt|;
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
name|index
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|index
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**       * Physically destroy the registered indexes by calling {@link org.exist.indexing.Index#remove()}      * on them.      *       * @throws DBException      */
specifier|public
name|void
name|removeIndexes
parameter_list|()
throws|throws
name|DBException
block|{
name|Index
name|index
decl_stmt|;
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
name|index
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|index
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Reopens the registered index in case they have been closed by a previous operation       * such as {@link org.exist.indexing.Index#close()} by calling {@link org.exist.indexing.Index#open()}      * on them.      *       * @throws DatabaseConfigurationException      */
specifier|public
name|void
name|reopenIndexes
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
name|Index
name|index
decl_stmt|;
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
name|index
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
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
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
block|{
name|Index
name|index
decl_stmt|;
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
name|index
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
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

