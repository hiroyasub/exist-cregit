begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|EXistException
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
name|triggers
operator|.
name|TriggerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|sync
operator|.
name|Sync
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
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Manages index configurations. Index configurations are stored in a collection  * hierarchy below /db/system/config. CollectionConfigurationManager is called  * by {@link org.exist.collections.Collection} to retrieve the  * {@link org.exist.collections.CollectionConfiguration} instance for a given collection.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CollectionConfigurationManager
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|CollectionConfigurationManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONFIG_COLLECTION
init|=
literal|"/db/system/config"
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|Map
name|cache
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|public
name|CollectionConfigurationManager
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
operator|.
name|pool
operator|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
expr_stmt|;
name|checkConfigCollection
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Add a new collection configuration. The XML document is passed as a string. 	 *  	 * @param broker 	 * @param collection the collection to which the configuration applies. 	 * @param config the xconf document as a string. 	 * @throws CollectionConfigurationException 	 */
specifier|public
name|void
name|addConfiguration
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|String
name|config
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
try|try
block|{
name|String
name|path
init|=
name|CONFIG_COLLECTION
operator|+
name|collection
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Collection
name|confCol
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|confCol
operator|==
literal|null
condition|)
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to create config collection: "
operator|+
name|path
argument_list|)
throw|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|confCol
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|confCol
operator|.
name|validate
argument_list|(
name|broker
argument_list|,
literal|"collection.xconf"
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|confCol
operator|.
name|store
argument_list|(
name|broker
argument_list|,
name|info
argument_list|,
name|config
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to store collection configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to store collection configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to store collection configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to store collection configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to store collection configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to store collection configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Retrieve the collection configuration instance for the given collection. This      * creates a new CollectionConfiguration object and recursively scans the collection      * hierarchy for available configurations.      *       * @param broker      * @param collection      * @param collectionPath      * @return      * @throws CollectionConfigurationException      */
specifier|protected
name|CollectionConfiguration
name|getConfiguration
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading config for "
operator|+
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionConfiguration
name|conf
init|=
operator|new
name|CollectionConfiguration
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
decl_stmt|;
name|int
name|p
init|=
literal|"/db"
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
name|next
decl_stmt|;
name|Collection
name|coll
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|p
operator|!=
operator|-
literal|1
condition|)
block|{
name|next
operator|=
name|CONFIG_COLLECTION
operator|+
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|coll
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|next
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|coll
operator|!=
literal|null
operator|&&
name|coll
operator|.
name|getDocumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|coll
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|confDoc
init|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|confDoc
operator|.
name|getFileName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|CollectionConfiguration
operator|.
name|COLLECTION_CONFIG_SUFFIX
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading config for "
operator|+
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|" from "
operator|+
name|confDoc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|read
argument_list|(
name|broker
argument_list|,
name|confDoc
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|coll
operator|!=
literal|null
condition|)
name|coll
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
name|p
operator|=
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// we synchronize on the global CollectionCache to avoid deadlocks.
comment|// the calling code does mostly already hold a lock on CollectionCache.
name|CollectionCache
name|collectionCache
init|=
name|pool
operator|.
name|getCollectionsCache
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|collectionCache
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|collection
operator|.
name|getName
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
comment|/**      * Notify the manager that a collection.xconf file has changed. All cached configurations      * for the corresponding collection and its sub-collections will be cleared.       *       * @param collectionPath      */
specifier|protected
name|void
name|invalidateAll
parameter_list|(
name|String
name|collectionPath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|collectionPath
operator|.
name|startsWith
argument_list|(
name|CONFIG_COLLECTION
argument_list|)
condition|)
return|return;
name|collectionPath
operator|=
name|collectionPath
operator|.
name|substring
argument_list|(
name|CONFIG_COLLECTION
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// we synchronize on the global CollectionCache to avoid deadlocks.
comment|// the calling code does mostly already hold a lock on CollectionCache.
name|CollectionCache
name|collectionCache
init|=
name|pool
operator|.
name|getCollectionsCache
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|collectionCache
init|)
block|{
name|Map
operator|.
name|Entry
name|next
decl_stmt|;
name|CollectionConfiguration
name|config
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|cache
operator|.
name|entrySet
argument_list|()
operator|.
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
name|next
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|collectionPath
argument_list|)
condition|)
block|{
name|config
operator|=
operator|(
name|CollectionConfiguration
operator|)
name|next
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
name|config
operator|.
name|getCollection
argument_list|()
operator|.
name|invalidateConfiguration
argument_list|()
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Called by the collection cache if a collection is removed from the cache.      * This will delete the cached configuration instance for this collection.      *       * @param collectionPath      */
specifier|protected
name|void
name|invalidate
parameter_list|(
name|String
name|collectionPath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|collectionPath
operator|.
name|startsWith
argument_list|(
name|CONFIG_COLLECTION
argument_list|)
condition|)
return|return;
name|collectionPath
operator|=
name|collectionPath
operator|.
name|substring
argument_list|(
name|CONFIG_COLLECTION
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionCache
name|collectionCache
init|=
name|pool
operator|.
name|getCollectionsCache
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|collectionCache
init|)
block|{
name|CollectionConfiguration
name|config
init|=
operator|(
name|CollectionConfiguration
operator|)
name|cache
operator|.
name|get
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|getCollection
argument_list|()
operator|.
name|invalidateConfiguration
argument_list|()
expr_stmt|;
name|cache
operator|.
name|remove
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Check if the config collection exists below /db/system. If not, create it. 	 *  	 * @param broker 	 * @throws EXistException 	 */
specifier|private
name|void
name|checkConfigCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
try|try
block|{
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|CONFIG_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|CONFIG_COLLECTION
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Failed to initialize /db/system/config: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

