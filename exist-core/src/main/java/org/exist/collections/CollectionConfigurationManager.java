begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|persistent
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
name|dom
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
name|*
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
operator|.
name|LockMode
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
name|ManagedLock
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
name|XMLReaderPool
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
name|XMLReader
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_comment
comment|/**  * Manages index configurations. Index configurations are stored in a collection  * hierarchy below /db/system/config. CollectionConfigurationManager is called  * by {@link org.exist.collections.Collection} to retrieve the  * {@link org.exist.collections.CollectionConfiguration} instance for a given  * collection.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CollectionConfigurationManager
implements|implements
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
name|CollectionConfigurationManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_COLLECTION
init|=
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION
operator|+
literal|"/config"
decl_stmt|;
comment|/** /db/system/config **/
specifier|public
specifier|static
specifier|final
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
comment|/** /db/system/config/db **/
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|ROOT_COLLECTION_CONFIG_URI
init|=
name|CONFIG_COLLECTION_URI
operator|.
name|append
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_NAME
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_CONFIG_FILENAME
init|=
literal|"collection.xconf"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CollectionURI
name|COLLECTION_CONFIG_PATH
init|=
operator|new
name|CollectionURI
argument_list|(
name|CONFIG_COLLECTION_URI
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|CollectionURI
argument_list|,
name|CollectionConfiguration
argument_list|>
name|configurations
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CollectionConfiguration
name|defaultConfig
decl_stmt|;
specifier|public
name|CollectionConfigurationManager
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
block|{
name|this
operator|.
name|defaultConfig
operator|=
operator|new
name|CollectionConfiguration
argument_list|(
name|brokerPool
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startSystem
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
try|try
block|{
name|checkCreateCollection
argument_list|(
name|systemBroker
argument_list|,
name|transaction
argument_list|,
name|CONFIG_COLLECTION_URI
argument_list|)
expr_stmt|;
name|checkCreateCollection
argument_list|(
name|systemBroker
argument_list|,
name|transaction
argument_list|,
name|ROOT_COLLECTION_CONFIG_URI
argument_list|)
expr_stmt|;
name|loadAllConfigurations
argument_list|(
name|systemBroker
argument_list|)
expr_stmt|;
name|defaultConfig
operator|.
name|setIndexConfiguration
argument_list|(
name|systemBroker
operator|.
name|getIndexConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
decl||
name|CollectionConfigurationException
decl||
name|PermissionDeniedException
decl||
name|LockException
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
comment|/**      * Add a new collection configuration. The XML document is passed as a      * string.      *       * @param txn The transaction that will hold the WRITE locks until they are      *            released by commit()/abort()      * @param broker the eXist-db broker      * @param collection  the collection to which the configuration applies.      * @param config the xconf document as a String.      * @throws CollectionConfigurationException if config is invalid      */
specifier|public
name|void
name|addConfiguration
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|config
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
try|try
block|{
specifier|final
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
specifier|final
name|Collection
name|confCol
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
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
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
literal|"Failed to create config collection: "
operator|+
name|path
argument_list|)
throw|;
block|}
name|XmldbURI
name|configurationDocumentName
init|=
literal|null
decl_stmt|;
comment|// Replaces the current configuration file if there is one
specifier|final
name|CollectionConfiguration
name|conf
init|=
name|getConfiguration
argument_list|(
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
block|{
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
block|}
if|if
condition|(
name|configurationDocumentName
operator|==
literal|null
condition|)
block|{
name|configurationDocumentName
operator|=
name|CollectionConfiguration
operator|.
name|DEFAULT_COLLECTION_CONFIG_FILE_URI
expr_stmt|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|confCol
argument_list|)
expr_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|confCol
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|configurationDocumentName
argument_list|,
name|config
argument_list|)
decl_stmt|;
comment|// TODO : unlock the collection here ?
name|confCol
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|// broker.sync(Sync.MAJOR_SYNC);
block|}
catch|catch
parameter_list|(
specifier|final
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
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
comment|/**      * Check the passed collection configuration. Throws an exception if errors      * are detected in the configuration document. Note: some configuration      * settings depend on the current environment, in particular the      * availability of trigger or index classes.      *       * @param broker DBBroker      * @param config the configuration to test      * @throws CollectionConfigurationException if errors were detected      */
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
specifier|final
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|XMLReaderPool
name|parserPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|parserPool
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
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
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|parserPool
operator|.
name|returnXMLReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Document
name|doc
init|=
name|adapter
operator|.
name|getDocument
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CollectionConfigurationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getCustomIndexSpecs
parameter_list|(
specifier|final
name|String
name|customIndexId
parameter_list|)
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|readLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|configs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|CollectionConfiguration
name|config
range|:
name|configurations
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|IndexSpec
name|spec
init|=
name|config
operator|.
name|getIndexConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|spec
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Object
name|customConfig
init|=
name|spec
operator|.
name|getCustomIndexSpec
argument_list|(
name|customIndexId
argument_list|)
decl_stmt|;
if|if
condition|(
name|customConfig
operator|!=
literal|null
condition|)
block|{
name|configs
operator|.
name|add
argument_list|(
name|customConfig
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|configs
return|;
block|}
block|}
comment|/**      * Retrieve the collection configuration instance for the given collection.      * This creates a new CollectionConfiguration object and recursively scans      * the collection hierarchy for available configurations.      *      * @param collection to retrieve configuration for      * @return The collection configuration      */
specifier|protected
name|CollectionConfiguration
name|getConfiguration
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
block|{
specifier|final
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
comment|/*          * This used to go from the root collection (/db), and continue all the          * way to the end of the path, checking each collection on the way. I          * modified it to start at the collection path and work its way back to          * the root, stopping at the first config file it finds. This should be          * more efficient, and fit more appropriately with the XmldbURI api          */
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|readLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
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
specifier|final
name|CollectionConfiguration
name|conf
init|=
name|configurations
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
return|return
name|conf
return|;
block|}
name|path
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
block|}
comment|// use default configuration
return|return
name|defaultConfig
return|;
block|}
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
throws|,
name|PermissionDeniedException
throws|,
name|LockException
block|{
specifier|final
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
throws|,
name|PermissionDeniedException
throws|,
name|LockException
block|{
if|if
condition|(
name|configCollection
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|loadConfiguration
argument_list|(
name|broker
argument_list|,
name|configCollection
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|i
init|=
name|configCollection
operator|.
name|collectionIterator
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
specifier|final
name|XmldbURI
name|childName
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
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
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Collection is registered but could not be loaded: "
operator|+
name|childName
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|Collection
name|configCollection
parameter_list|)
throws|throws
name|CollectionConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
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
argument_list|(
name|broker
argument_list|)
operator|>
literal|0
condition|)
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
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
specifier|final
name|DocumentImpl
name|confDoc
init|=
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
block|{
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
block|}
specifier|final
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
argument_list|)
decl_stmt|;
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
specifier|final
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
specifier|final
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
block|}
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
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
comment|// Allow just one configuration document per collection
comment|// TODO : do not break if a system property allows several ones -pb
break|break;
block|}
block|}
block|}
block|}
specifier|public
name|CollectionConfiguration
name|getOrCreateCollectionConfiguration
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
block|{
specifier|final
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
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|readLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
specifier|final
name|CollectionConfiguration
name|conf
init|=
name|configurations
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
return|return
name|conf
return|;
block|}
block|}
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
name|CollectionConfiguration
name|conf
init|=
name|configurations
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
return|return
name|conf
return|;
block|}
name|conf
operator|=
operator|new
name|CollectionConfiguration
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
expr_stmt|;
name|configurations
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
comment|/**      * Notify the manager that a collection.xconf file has changed. All cached      * configurations for the corresponding collection and its sub-collections      * will be cleared.      *       * @param collectionPath to the collection for which configuration will be invalidated      */
specifier|public
name|void
name|invalidateAll
parameter_list|(
specifier|final
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
block|{
return|return;
block|}
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
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
literal|"Invalidating collection "
operator|+
name|collectionPath
operator|+
literal|" and subcollections"
argument_list|)
expr_stmt|;
block|}
name|CollectionURI
name|uri
init|=
operator|new
name|CollectionURI
argument_list|(
name|collectionPath
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|)
decl_stmt|;
name|configurations
operator|.
name|remove
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|String
name|str
init|=
name|uri
operator|.
name|toString
argument_list|()
decl_stmt|;
name|configurations
operator|.
name|entrySet
argument_list|()
operator|.
name|removeIf
argument_list|(
name|entry
lambda|->
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called by the collection cache if a collection is removed from the cache.      * This will delete the cached configuration instance for this collection.      *       * @param collectionPath to the collection for which configuration will be invalidated      * @param pool if not null: clear query cache      */
specifier|public
name|void
name|invalidate
parameter_list|(
specifier|final
name|XmldbURI
name|collectionPath
parameter_list|,
specifier|final
name|BrokerPool
name|pool
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
block|{
return|return;
block|}
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
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
literal|"Invalidating collection "
operator|+
name|collectionPath
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Check if the collection exists below the system collection. If not,      * create it.      *       * @param broker eXist-db broker      * @param txn according transaction      * @param uri to the collection to create      * @throws EXistException if something goes wrong      */
specifier|private
name|void
name|checkCreateCollection
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|EXistException
block|{
try|try
block|{
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|collection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|SanityCheck
operator|.
name|THROW_ASSERT
argument_list|(
name|collection
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
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|TriggerException
decl||
name|PermissionDeniedException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Failed to initialize '"
operator|+
name|uri
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
comment|/**      * Create a stored default configuration document for the root collection      *       * @param broker The broker which will do the operation      * @throws EXistException if something goes wrong      * @throws PermissionDeniedException if user does not have sufficient rights      */
specifier|public
name|void
name|checkRootCollectionConfig
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
comment|// Copied from the legacy conf.xml in order to make the test suite work
comment|// TODO : backward compatibility could be ensured by copying the
comment|// relevant parts of conf.xml
specifier|final
name|String
name|configuration
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|broker
operator|.
name|getDatabase
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
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
name|txn
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
specifier|final
name|CollectionConfiguration
name|conf
init|=
name|getConfiguration
argument_list|(
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
comment|// We already have a configuration document : do not erase
comment|// it
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
name|txn
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// Configure the root collection
name|addConfiguration
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|configuration
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
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|CollectionConfigurationException
name|e
parameter_list|)
block|{
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
comment|/*      * private void debugCache() { StringBuilder buf = new StringBuilder(); for      * (Iterator i = configurations.keySet().iterator(); i.hasNext(); ) {      * buf.append(i.next()).append(' '); } LOG.debug(buf.toString()); }      */
block|}
end_class

end_unit

