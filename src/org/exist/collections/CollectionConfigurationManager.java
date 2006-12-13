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
name|storage
operator|.
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|exist
operator|.
name|util
operator|.
name|sanity
operator|.
name|SanityCheck
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
name|DBBroker
operator|.
name|SYSTEM_COLLECTION
operator|+
literal|"/config"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|CONFIG_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|CONFIG_COLLECTION
argument_list|)
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
comment|/** 	 * Add a new collection configuration. The XML document is passed as a string. 	 *  	 * @param broker 	 * @param collection the collection to which the configuration applies. 	 * @param config the xconf document as a String. 	 * @throws CollectionConfigurationException 	 */
specifier|public
name|void
name|addConfiguration
parameter_list|(
name|Txn
name|transaction
parameter_list|,
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
name|XmldbURI
name|path
init|=
name|CONFIG_COLLECTION_URI
operator|.
name|append
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
name|confCol
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
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
name|XmldbURI
name|configurationDocumentName
init|=
literal|null
decl_stmt|;
comment|//Replaces the current configuration file if there is one
name|CollectionConfiguration
name|conf
init|=
name|getConfiguration
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|configurationDocumentName
operator|=
name|conf
operator|.
name|getDocName
argument_list|()
expr_stmt|;
if|if
condition|(
name|configurationDocumentName
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"Replacing current configuration file '"
operator|+
name|configurationDocumentName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|configurationDocumentName
operator|==
literal|null
condition|)
name|configurationDocumentName
operator|=
name|CollectionConfiguration
operator|.
name|DEFAULT_COLLECTION_CONFIG_FILE_URI
expr_stmt|;
comment|//broker.saveCollection(transaction, confCol);
name|IndexInfo
name|info
init|=
name|confCol
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|configurationDocumentName
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|confCol
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|config
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//broker.sync(Sync.MAJOR_SYNC);
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
comment|/**      * Retrieve the collection configuration instance for the given collection. This      * creates a new CollectionConfiguration object and recursively scans the collection      * hierarchy for available configurations.      *       * @param broker      * @param collection      * @param collectionPath      * @return The collection configuration      * @throws CollectionConfigurationException      */
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
name|CollectionConfiguration
name|conf
init|=
operator|new
name|CollectionConfiguration
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|boolean
name|configFound
init|=
literal|false
decl_stmt|;
name|XmldbURI
name|path
init|=
name|CONFIG_COLLECTION_URI
operator|.
name|append
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
name|coll
init|=
literal|null
decl_stmt|;
comment|/*     	 * This used to go from the root collection (/db), and continue all the     	 * way to the end of the path, checking each collection on the way.  I     	 * modified it to start at the collection path and work its way back to     	 * the root, stopping at the first config file it finds. This should be     	 * more efficient, and fit more appropriately will the XmldbURI api     	 */
while|while
condition|(
operator|!
name|configFound
operator|&&
operator|!
name|path
operator|.
name|equals
argument_list|(
name|CONFIG_COLLECTION_URI
argument_list|)
condition|)
block|{
try|try
block|{
name|coll
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|path
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
name|getFileURI
argument_list|()
operator|.
name|endsWith
argument_list|(
name|CollectionConfiguration
operator|.
name|COLLECTION_CONFIG_SUFFIX_URI
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|configFound
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading collection configuration for '"
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|"' from '"
operator|+
name|confDoc
operator|.
name|getURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|read
argument_list|(
name|broker
argument_list|,
name|confDoc
argument_list|,
name|path
argument_list|,
name|confDoc
operator|.
name|getFileURI
argument_list|()
argument_list|)
expr_stmt|;
name|configFound
operator|=
literal|true
expr_stmt|;
comment|//Allow just one configuration document per collection
comment|//TODO : do not break if a system property allows several ones -pb
break|break;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found another collection configuration for '"
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|"' in '"
operator|+
name|confDoc
operator|.
name|getURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
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
name|path
operator|=
name|path
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|configFound
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading collection configuration for '"
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|"' from index configuration"
argument_list|)
expr_stmt|;
comment|// use default configuration
name|conf
operator|.
name|setIndexConfiguration
argument_list|(
name|broker
operator|.
name|getIndexConfiguration
argument_list|()
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
name|getURI
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
name|XmldbURI
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
name|CONFIG_COLLECTION_URI
argument_list|)
condition|)
return|return;
name|collectionPath
operator|=
name|collectionPath
operator|.
name|trimFromBeginning
argument_list|(
name|CONFIG_COLLECTION_URI
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
operator|(
operator|(
name|XmldbURI
operator|)
name|next
operator|.
name|getKey
argument_list|()
operator|)
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
name|XmldbURI
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
name|CONFIG_COLLECTION_URI
argument_list|)
condition|)
return|return;
name|collectionPath
operator|=
name|collectionPath
operator|.
name|trimFromBeginning
argument_list|(
name|CONFIG_COLLECTION_URI
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
name|collectionPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Check if the config collection exists below the system collection. If not, create it. 	 *  	 * @param broker 	 * @throws EXistException 	 */
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
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|CONFIG_COLLECTION_URI
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|root
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|CONFIG_COLLECTION_URI
argument_list|)
expr_stmt|;
name|SanityCheck
operator|.
name|THROW_ASSERT
argument_list|(
name|root
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
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
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Failed to initialize '"
operator|+
name|CONFIG_COLLECTION
operator|+
literal|"' : "
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

