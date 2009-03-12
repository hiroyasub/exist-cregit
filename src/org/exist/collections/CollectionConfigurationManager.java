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
name|memtree
operator|.
name|SAXAdapter
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|InputSource
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|String
name|COLLECTION_CONFIG_FILENAME
init|=
literal|"collection.xconf"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|CollectionURI
name|COLLECTION_CONFIG_PATH
init|=
operator|new
name|CollectionURI
argument_list|(
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|Map
name|configurations
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Object
name|latch
decl_stmt|;
specifier|private
name|CollectionConfiguration
name|defaultConfig
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|CollectionConfigurationManager
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
throws|,
name|CollectionConfigurationException
block|{
name|pool
operator|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
expr_stmt|;
name|latch
operator|=
name|pool
operator|.
name|getCollectionsCache
argument_list|()
expr_stmt|;
name|checkConfigCollection
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|loadAllConfigurations
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|defaultConfig
operator|=
operator|new
name|CollectionConfiguration
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|defaultConfig
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
comment|/** 	 * Add a new collection configuration. The XML document is passed as a string. 	 *       * @param transaction The transaction that will hold the WRITE locks until they are released by commit()/abort()      * @param broker 	 * @param collection the collection to which the configuration applies. 	 * @param config the xconf document as a String. 	 * @throws CollectionConfigurationException 	 */
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
comment|//TODO : use XmldbURI.resolve() !
name|XmldbURI
name|path
init|=
name|XmldbURI
operator|.
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
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|confCol
argument_list|)
expr_stmt|;
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
comment|//TODO : unlock the collection here ?
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
synchronized|synchronized
init|(
name|latch
init|)
block|{
name|configurations
operator|.
name|remove
argument_list|(
operator|new
name|CollectionURI
argument_list|(
name|path
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|loadConfiguration
argument_list|(
name|broker
argument_list|,
name|confCol
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
comment|/**      * Check the passed collection configuration. Throws an exception if errors are detected in the      * configuration document. Note: some configuration settings depend on the current environment, in particular      * the availability of trigger or index classes.      *      * @param broker DBBroker      * @param config the configuration to test      * @throws CollectionConfigurationException if errors were detected      */
specifier|public
name|void
name|testConfiguration
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|config
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
try|try
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|adapter
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|CollectionConfiguration
name|conf
init|=
operator|new
name|CollectionConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|conf
operator|.
name|read
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
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
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
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
comment|/**      * Retrieve the collection configuration instance for the given collection. This      * creates a new CollectionConfiguration object and recursively scans the collection      * hierarchy for available configurations.      *       * @param broker      * @param collection      * @return The collection configuration      * @throws CollectionConfigurationException      */
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
name|CollectionURI
name|path
init|=
operator|new
name|CollectionURI
argument_list|(
name|COLLECTION_CONFIG_PATH
argument_list|)
decl_stmt|;
name|path
operator|.
name|append
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
comment|/*     	 * This used to go from the root collection (/db), and continue all the     	 * way to the end of the path, checking each collection on the way.  I     	 * modified it to start at the collection path and work its way back to     	 * the root, stopping at the first config file it finds. This should be     	 * more efficient, and fit more appropriately will the XmldbURI api     	 */
name|CollectionConfiguration
name|conf
decl_stmt|;
synchronized|synchronized
init|(
name|latch
init|)
block|{
while|while
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|COLLECTION_CONFIG_PATH
argument_list|)
condition|)
block|{
name|conf
operator|=
operator|(
name|CollectionConfiguration
operator|)
name|configurations
operator|.
name|get
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
return|return
name|conf
return|;
name|path
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
block|}
block|}
comment|// use default configuration
return|return
name|defaultConfig
return|;
block|}
specifier|protected
name|void
name|loadAllConfigurations
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
argument_list|)
decl_stmt|;
name|loadAllConfigurations
argument_list|(
name|broker
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|loadAllConfigurations
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|configCollection
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
if|if
condition|(
name|configCollection
operator|==
literal|null
condition|)
return|return;
name|loadConfiguration
argument_list|(
name|broker
argument_list|,
name|configCollection
argument_list|)
expr_stmt|;
name|XmldbURI
name|path
init|=
name|configCollection
operator|.
name|getURI
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|configCollection
operator|.
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|XmldbURI
name|childName
init|=
operator|(
name|XmldbURI
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Collection
name|child
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|path
operator|.
name|appendInternal
argument_list|(
name|childName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
name|LOG
operator|.
name|error
argument_list|(
literal|"Collection is registered but could not be loaded: "
operator|+
name|childName
argument_list|)
expr_stmt|;
name|loadAllConfigurations
argument_list|(
name|broker
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|loadConfiguration
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|configCollection
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
if|if
condition|(
name|configCollection
operator|!=
literal|null
operator|&&
name|configCollection
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
name|configCollection
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
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"Reading collection configuration from '"
operator|+
name|confDoc
operator|.
name|getURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|CollectionConfiguration
name|conf
init|=
operator|new
name|CollectionConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
comment|// TODO DWES Temporary workaround for bug
comment|// [ 1807744 ] Invalid collection.xconf causes a non startable database
comment|// http://sourceforge.net/tracker/index.php?func=detail&aid=1807744&group_id=17691&atid=117691
try|try
block|{
name|conf
operator|.
name|read
argument_list|(
name|broker
argument_list|,
name|confDoc
argument_list|,
literal|false
argument_list|,
name|configCollection
operator|.
name|getURI
argument_list|()
argument_list|,
name|confDoc
operator|.
name|getFileURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Failed to read configuration document "
operator|+
name|confDoc
operator|.
name|getFileURI
argument_list|()
operator|+
literal|" in "
operator|+
name|configCollection
operator|.
name|getURI
argument_list|()
operator|+
literal|". "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|latch
init|)
block|{
name|configurations
operator|.
name|put
argument_list|(
operator|new
name|CollectionURI
argument_list|(
name|configCollection
operator|.
name|getURI
argument_list|()
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|//Allow just one configuration document per collection
comment|//TODO : do not break if a system property allows several ones -pb
break|break;
block|}
block|}
block|}
block|}
comment|/**      * Notify the manager that a collection.xconf file has changed. All cached configurations      * for the corresponding collection and its sub-collections will be cleared.       *       * @param collectionPath      */
specifier|public
name|void
name|invalidateAll
parameter_list|(
name|XmldbURI
name|collectionPath
parameter_list|)
block|{
comment|//TODO : use XmldbURI.resolve !
if|if
condition|(
operator|!
name|collectionPath
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
argument_list|)
condition|)
return|return;
synchronized|synchronized
init|(
name|latch
init|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalidating collection "
operator|+
name|collectionPath
argument_list|)
expr_stmt|;
name|configurations
operator|.
name|remove
argument_list|(
operator|new
name|CollectionURI
argument_list|(
name|collectionPath
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
comment|//TODO : use XmldbURI.resolve !
comment|//    	if (!collectionPath.startsWith(XmldbURI.CONFIG_COLLECTION_URI))
comment|//    		return;
comment|//    	collectionPath = collectionPath.trimFromBeginning(XmldbURI.CONFIG_COLLECTION_URI);
comment|//		CollectionCache collectionCache = pool.getCollectionsCache();
comment|//		synchronized (collectionCache) {
comment|//	    	CollectionConfiguration config = (CollectionConfiguration) cache.get(collectionPath);
comment|//	    	if (config != null) {
comment|//	    		config.getCollection().invalidateConfiguration();
comment|//	    		cache.remove(collectionPath);
comment|//	    	}
comment|//		}
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
name|XmldbURI
operator|.
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
name|XmldbURI
operator|.
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
name|IOException
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
specifier|public
name|void
name|checkRootCollectionConfigCollection
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
comment|//Create a configuration collection for the root collection
name|Collection
name|rootCollectionConfiguration
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_CONFIG_URI
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootCollectionConfiguration
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
name|rootCollectionConfiguration
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_CONFIG_URI
argument_list|)
expr_stmt|;
name|SanityCheck
operator|.
name|THROW_ASSERT
argument_list|(
name|rootCollectionConfiguration
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
name|rootCollectionConfiguration
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
name|IOException
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
comment|/** Create a stored default configuration document for the root collection       * @param broker The broker which will do the operation      * @throws EXistException      */
specifier|public
name|void
name|checkRootCollectionConfig
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|String
name|configuration
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
comment|//Copied from the legacy conf.xml in order to make the test suite work
comment|//TODO : backward compatibility could be ensured by copying the relevant parts of conf.xml
literal|"<fulltext attributes=\"true\" default=\"all\">"
operator|+
literal|"<exclude path=\"/auth\" />"
operator|+
literal|"</fulltext>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"collection "
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|+
literal|" not found!"
argument_list|)
throw|;
block|}
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
comment|//We already have a configuration document : do not erase it
if|if
condition|(
name|conf
operator|.
name|getDocName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
comment|//Configure the root collection
name|addConfiguration
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Configured '"
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|debugCache
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|configurations
operator|.
name|keySet
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
name|buf
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

