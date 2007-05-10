begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  DBBroker.java - eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|Collection
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
name|BinaryDocument
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
name|dom
operator|.
name|DocumentSet
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
name|NodeProxy
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
name|StoredNode
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
name|SymbolTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|StreamListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|security
operator|.
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|EmbeddedXMLStreamReader
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
name|serializers
operator|.
name|Serializer
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
name|LockException
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
name|exist
operator|.
name|xquery
operator|.
name|XQuery
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

begin_comment
comment|/**  * This is the base class for all database backends. All the basic database  * operations like storing, removing or index access are provided by subclasses  * of this class.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DBBroker
extends|extends
name|Observable
block|{
specifier|public
specifier|final
specifier|static
name|int
name|MATCH_EXACT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MATCH_REGEXP
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MATCH_WILDCARDS
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ROOT_COLLECTION_NAME
init|=
literal|"db"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ROOT_COLLECTION
init|=
literal|"/"
operator|+
name|ROOT_COLLECTION_NAME
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SYSTEM_COLLECTION
init|=
name|ROOT_COLLECTION
operator|+
literal|"/system"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TEMP_COLLECTION
init|=
name|SYSTEM_COLLECTION
operator|+
literal|"/temp"
decl_stmt|;
comment|//TODO : move elsewhere
specifier|public
specifier|static
name|String
name|PROPERTY_XUPDATE_GROWTH_FACTOR
init|=
literal|"xupdate.growth-factor"
decl_stmt|;
comment|//TODO : move elsewhere
specifier|public
specifier|static
name|String
name|PROPERTY_XUPDATE_FRAGMENTATION_FACTOR
init|=
literal|"xupdate.fragmentation"
decl_stmt|;
comment|//TODO : move elsewhere
specifier|public
specifier|static
name|String
name|PROPERTY_XUPDATE_CONSISTENCY_CHECKS
init|=
literal|"xupdate.consistency-checks"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DBBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|boolean
name|caseSensitive
init|=
literal|true
decl_stmt|;
specifier|protected
name|Configuration
name|config
decl_stmt|;
specifier|protected
name|BrokerPool
name|pool
decl_stmt|;
specifier|protected
name|User
name|user
init|=
literal|null
decl_stmt|;
specifier|protected
name|XQuery
name|xqueryService
decl_stmt|;
specifier|private
name|int
name|referenceCount
init|=
literal|0
decl_stmt|;
specifier|protected
name|String
name|id
decl_stmt|;
specifier|protected
name|IndexController
name|indexController
decl_stmt|;
comment|//TODO : use a property object
specifier|public
name|HashMap
name|customProperties
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|public
name|DBBroker
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|Boolean
name|temp
init|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|NativeValueIndex
operator|.
name|PROPERTY_INDEX_CASE_SENSITIVE
argument_list|)
decl_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
name|caseSensitive
operator|=
name|temp
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
comment|//Copy specific properties
comment|//TODO : think about an automatic copy
name|customProperties
operator|.
name|put
argument_list|(
name|PROPERTY_XUPDATE_GROWTH_FACTOR
argument_list|,
operator|new
name|Integer
argument_list|(
name|config
operator|.
name|getInteger
argument_list|(
name|PROPERTY_XUPDATE_GROWTH_FACTOR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|customProperties
operator|.
name|put
argument_list|(
name|PROPERTY_XUPDATE_FRAGMENTATION_FACTOR
argument_list|,
operator|new
name|Integer
argument_list|(
name|config
operator|.
name|getInteger
argument_list|(
name|PROPERTY_XUPDATE_FRAGMENTATION_FACTOR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|temp
operator|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_XUPDATE_CONSISTENCY_CHECKS
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
name|customProperties
operator|.
name|put
argument_list|(
name|PROPERTY_XUPDATE_CONSISTENCY_CHECKS
argument_list|,
operator|new
name|Boolean
argument_list|(
name|temp
operator|.
name|booleanValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|xqueryService
operator|=
operator|new
name|XQuery
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|indexController
operator|=
operator|new
name|IndexController
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Set the user that is currently using this DBBroker object. 	 * 	 * @param user 	 */
specifier|public
name|void
name|setUser
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
comment|/* 		 * synchronized (this){ System.out.println("DBBroker.setUser(" + 		 * user.getName() + ")"); Thread.dumpStack(); } 		 */
comment|// debugging user escalation permissions problem - deliriumsky.
block|}
comment|/** 	 * @return The user that is currently using this DBBroker object 	 */
specifier|public
name|User
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
specifier|public
name|IndexController
name|getIndexController
parameter_list|()
block|{
return|return
name|indexController
return|;
block|}
comment|//TODO : give more abstraction in the future (Symbolprovider or something like this)
specifier|public
specifier|abstract
name|SymbolTable
name|getSymbols
parameter_list|()
function_decl|;
comment|/** 	 * @return A reference to the global {@link XQuery} service. 	 */
specifier|public
name|XQuery
name|getXQueryService
parameter_list|()
block|{
return|return
name|xqueryService
return|;
block|}
specifier|public
specifier|abstract
name|ElementIndex
name|getElementIndex
parameter_list|()
function_decl|;
comment|/** Flush all data that has not been written before. */
specifier|public
name|void
name|flush
parameter_list|()
block|{
comment|/* 		 * do nothing 		 */
block|}
comment|/** Observer Design Pattern: List of ContentLoadingObserver objects */
specifier|protected
name|List
name|contentLoadingObservers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/** Remove all observers */
specifier|public
name|void
name|clearContentLoadingObservers
parameter_list|()
block|{
name|contentLoadingObservers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** Observer Design Pattern: add an observer. */
specifier|public
name|void
name|addContentLoadingObserver
parameter_list|(
name|ContentLoadingObserver
name|observer
parameter_list|)
block|{
if|if
condition|(
operator|!
name|contentLoadingObservers
operator|.
name|contains
argument_list|(
name|observer
argument_list|)
condition|)
name|contentLoadingObservers
operator|.
name|add
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
comment|/** Observer Design Pattern: remove an observer. */
specifier|public
name|void
name|removeContentLoadingObserver
parameter_list|(
name|ContentLoadingObserver
name|observer
parameter_list|)
block|{
if|if
condition|(
name|contentLoadingObservers
operator|.
name|contains
argument_list|(
name|observer
argument_list|)
condition|)
name|contentLoadingObservers
operator|.
name|remove
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Adds all the documents in the database to the specified DocumentSet. 	 *  	 * @param docs 	 *            a (possibly empty) document set to which the found documents 	 *            are added. 	 *  	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getAllXMLResources
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
function_decl|;
comment|/** 	 * Returns the database collection identified by the specified path. The 	 * path should be absolute, e.g. /db/shakespeare. 	 *  	 * @return collection or null if no collection matches the path 	 *  	 * deprecated Use XmldbURI instead! 	 *  	 * public abstract Collection getCollection(String name); 	 */
comment|/** 	 * Returns the database collection identified by the specified path. The 	 * path should be absolute, e.g. /db/shakespeare. 	 *  	 * @return collection or null if no collection matches the path 	 */
specifier|public
specifier|abstract
name|Collection
name|getCollection
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * Returns the database collection identified by the specified path. The 	 * storage address is used to locate the collection without looking up the 	 * path in the btree. 	 *  	 * @return deprecated Use XmldbURI instead! 	 *  	 * public abstract Collection getCollection(String name, long address); 	 */
comment|/** 	 * Returns the database collection identified by the specified path. The 	 * storage address is used to locate the collection without looking up the 	 * path in the btree. 	 *  	 * @return Database collection 	 *  	 * public abstract Collection getCollection(XmldbURI uri, long address); 	 */
comment|/** 	 * Open a collection for reading or writing. The collection is identified by 	 * its absolute path, e.g. /db/shakespeare. It will be loaded and locked 	 * according to the lockMode argument. 	 *  	 * The caller should take care to release the collection lock properly. 	 *  	 * @param name 	 *            the collection path 	 * @param lockMode 	 *            one of the modes specified in class 	 *            {@link org.exist.storage.lock.Lock} 	 * @return collection or null if no collection matches the path 	 *  	 * deprecated Use XmldbURI instead! 	 *  	 * public abstract Collection openCollection(String name, int lockMode); 	 */
comment|/** 	 * Open a collection for reading or writing. The collection is identified by 	 * its absolute path, e.g. /db/shakespeare. It will be loaded and locked 	 * according to the lockMode argument. 	 *  	 * The caller should take care to release the collection lock properly. 	 *  	 * @param uri 	 *            The collection path 	 * @param lockMode 	 *            one of the modes specified in class 	 *            {@link org.exist.storage.lock.Lock} 	 * @return collection or null if no collection matches the path 	 *  	 */
specifier|public
specifier|abstract
name|Collection
name|openCollection
parameter_list|(
name|XmldbURI
name|uri
parameter_list|,
name|int
name|lockMode
parameter_list|)
function_decl|;
comment|/** 	 * Returns the database collection identified by the specified path. If the 	 * collection does not yet exist, it is created - including all ancestors. 	 * The path should be absolute, e.g. /db/shakespeare. 	 *  	 * @return collection or null if no collection matches the path 	 *  	 * deprecated Use XmldbURI instead! 	 *  	 * public Collection getOrCreateCollection(Txn transaction, String name) 	 * throws PermissionDeniedException { return null; } 	 */
comment|/** 	 * Returns the database collection identified by the specified path. If the 	 * collection does not yet exist, it is created - including all ancestors. 	 * The path should be absolute, e.g. /db/shakespeare. 	 *  	 * @param transaction The transaction, which registers the acquired write locks. The locks should be released on commit/abort. 	 * @param uri The collection's URI 	 * @return The collection or<code>null</code> if no collection matches the path 	 * @throws PermissionDeniedException 	 * @throws IOException 	 */
specifier|public
specifier|abstract
name|Collection
name|getOrCreateCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
function_decl|;
comment|/** 	 * Returns the configuration object used to initialize the current database 	 * instance. 	 *  	 */
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/** 	 * Return a {@link org.exist.storage.dom.NodeIterator} starting at the 	 * specified node. 	 *  	 * @param node 	 * @return NodeIterator of node. 	 */
specifier|public
name|Iterator
name|getNodeIterator
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented for this storage backend"
argument_list|)
throw|;
block|}
comment|/** 	 * Return the document stored at the specified path. The path should be 	 * absolute, e.g. /db/shakespeare/plays/hamlet.xml. 	 *  	 * @return the document or null if no document could be found at the 	 *         specified location. 	 *  	 * deprecated Use XmldbURI instead! 	 *  	 * public abstract Document getXMLResource(String path) throws 	 * PermissionDeniedException; 	 */
comment|/** 	 * Return the document stored at the specified path. The path should be 	 * absolute, e.g. /db/shakespeare/plays/hamlet.xml. 	 *  	 * @return the document or null if no document could be found at the 	 *         specified location. 	 */
specifier|public
specifier|abstract
name|Document
name|getXMLResource
parameter_list|(
name|XmldbURI
name|docURI
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 * deprecated Use XmldbURI instead! 	 *  	 * public abstract DocumentImpl getXMLResource(String docPath, int lockMode) 	 * throws PermissionDeniedException; 	 */
comment|/** 	 * Return the document stored at the specified path. The path should be 	 * absolute, e.g. /db/shakespeare/plays/hamlet.xml, with the specified lock. 	 *  	 * @return the document or null if no document could be found at the 	 *         specified location. 	 */
specifier|public
specifier|abstract
name|DocumentImpl
name|getXMLResource
parameter_list|(
name|XmldbURI
name|docURI
parameter_list|,
name|int
name|lockMode
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 * Get a new document id that does not yet exist within the collection. 	 */
specifier|public
specifier|abstract
name|int
name|getNextResourceId
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 * Get the string value of the specified node. 	 *  	 * If addWhitespace is set to true, an extra space character will be added 	 * between adjacent elements in mixed content nodes. 	 */
specifier|public
name|String
name|getNodeValue
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|boolean
name|addWhitespace
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented for this storage backend"
argument_list|)
throw|;
block|}
comment|/** 	 * Get an instance of the Serializer used for converting nodes back to XML. 	 * Subclasses of DBBroker may have specialized subclasses of Serializer to 	 * convert a node into an XML-string 	 */
specifier|public
specifier|abstract
name|Serializer
name|getSerializer
parameter_list|()
function_decl|;
comment|/** 	 * Get the TextSearchEngine associated with this broker. Every subclass of 	 * DBBroker will have it's own implementation of TextSearchEngine. 	 */
specifier|public
specifier|abstract
name|TextSearchEngine
name|getTextEngine
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|NativeValueIndex
name|getValueIndex
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|Serializer
name|newSerializer
parameter_list|()
function_decl|;
comment|/** 	 * Get a node with given owner document and id from the database. 	 *  	 * @param doc 	 *            the document the node belongs to 	 * @param nodeId 	 *            the node's unique identifier 	 */
specifier|public
specifier|abstract
name|StoredNode
name|objectWith
parameter_list|(
name|Document
name|doc
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|StoredNode
name|objectWith
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
function_decl|;
comment|/** 	 * Remove the collection and all its subcollections from the database. 	 *  	 */
specifier|public
specifier|abstract
name|boolean
name|removeCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
function_decl|;
comment|/** 	 * Remove a document from the database. 	 *  	 */
specifier|public
name|void
name|removeXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|document
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|removeXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|boolean
name|freeDocId
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 * Reindex a collection. 	 *  	 * @param collectionName 	 * @throws PermissionDeniedException 	 *  	 * public abstract void reindexCollection(String collectionName) throws 	 * PermissionDeniedException; 	 */
specifier|public
specifier|abstract
name|void
name|reindexCollection
parameter_list|(
name|XmldbURI
name|collectionName
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
specifier|abstract
name|void
name|repair
parameter_list|()
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Saves the specified collection to storage. Collections are usually cached      * in memory. If a collection is modified, this method needs to be called to      * make the changes persistent. Note: appending a new document to a      * collection does not require a save.       *       * @param transaction       * @param collection Collection to store      * @throws org.exist.security.PermissionDeniedException       */
specifier|public
specifier|abstract
name|void
name|saveCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
function_decl|;
specifier|public
name|void
name|closeDocument
parameter_list|()
block|{
block|}
comment|/** 	 * Shut down the database instance. All open files, jdbc connections etc. 	 * should be closed. 	 */
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
block|}
comment|/** 	 * Store a node into the database. This method is called by the parser to 	 * write a node to the storage backend. 	 *  	 * @param node 	 *            the node to be stored 	 * @param currentPath 	 *            path expression which points to this node's element-parent or 	 *            to itself if it is an element (currently used by the Broker to 	 *            determine if a node's content should be fulltext-indexed). 	 */
specifier|public
specifier|abstract
name|void
name|storeNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|boolean
name|index
parameter_list|)
function_decl|;
specifier|public
name|void
name|storeNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
block|{
name|storeNode
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|currentPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|endElement
argument_list|(
name|node
argument_list|,
name|currentPath
argument_list|,
name|content
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|endElement
parameter_list|(
specifier|final
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|String
name|content
parameter_list|,
name|boolean
name|remove
parameter_list|)
function_decl|;
comment|/** 	 * Store a document (descriptor) into the database. 	 *  	 * @param doc 	 *            the document's metadata to store. 	 */
specifier|public
specifier|abstract
name|void
name|storeXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/** 	 * Stores the given data under the given binary resource descriptor 	 * (BinaryDocument). 	 *  	 * @param blob 	 *            the binary document descriptor 	 * @param data 	 *            the document binary data 	 */
specifier|public
specifier|abstract
name|void
name|storeBinaryResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|BinaryDocument
name|blob
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
function_decl|;
comment|/** 	 * Stores the given data under the given binary resource descriptor 	 * (BinaryDocument). 	 *  	 * @param blob 	 *            the binary document descriptor 	 * @param is 	 *            the document binary data as input stream 	 */
specifier|public
specifier|abstract
name|void
name|storeBinaryResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|BinaryDocument
name|blob
parameter_list|,
name|InputStream
name|is
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|getCollectionResources
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 * Retrieve the binary data stored under the resource descriptor 	 * BinaryDocument. 	 *  	 * @param blob 	 *            the binary document descriptor 	 * @return the document binary data 	 */
specifier|public
specifier|abstract
name|byte
index|[]
name|getBinaryResource
parameter_list|(
name|BinaryDocument
name|blob
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|readBinaryResource
parameter_list|(
specifier|final
name|BinaryDocument
name|blob
parameter_list|,
specifier|final
name|OutputStream
name|os
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|getResourceMetadata
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/** 	 * Completely delete this binary document (descriptor and binary data). 	 *  	 * @param blob 	 *            the binary document descriptor 	 * @throws PermissionDeniedException 	 *             if you don't have the right to do this 	 */
specifier|public
specifier|abstract
name|void
name|removeBinaryResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|BinaryDocument
name|blob
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 * Move a collection and all its subcollections to another collection and 	 * rename it. Moving a collection just modifies the collection path and all 	 * resource paths. The data itself remains in place. 	 *  	 * @param collection 	 *            the collection to move 	 * @param destination 	 *            the destination collection 	 * @param newName 	 *            the new name the collection should have in the destination 	 *            collection 	 */
specifier|public
specifier|abstract
name|void
name|moveCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|Collection
name|destination
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
function_decl|;
comment|/** 	 * Move a resource to the destination collection and rename it. 	 *  	 * @param doc 	 *            the resource to move 	 * @param destination 	 *            the destination collection 	 * @param newName 	 *            the new name the resource should have in the destination 	 *            collection 	 */
specifier|public
specifier|abstract
name|void
name|moveXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|Collection
name|destination
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
function_decl|;
comment|/** 	 * Copy a collection to the destination collection and rename it. 	 *  	 * @param transaction The transaction, which registers the acquired write locks. The locks should be released on commit/abort. 	 * @param collection The origin collection 	 * @param destination The destination parent collection 	 * @param newName The new name of the collection 	 * @throws PermissionDeniedException 	 * @throws LockException 	 * @throws IOException 	 */
specifier|public
specifier|abstract
name|void
name|copyCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|Collection
name|destination
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
function_decl|;
comment|/** 	 * Copy a resource to the destination collection and rename it. 	 *  	 * @param doc 	 *            the resource to copy 	 * @param destination 	 *            the destination collection 	 * @param newName 	 *            the new name the resource should have in the destination 	 *            collection 	 * @throws PermissionDeniedException 	 * @throws LockException 	 */
specifier|public
specifier|abstract
name|void
name|copyXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|Collection
name|destination
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
function_decl|;
comment|/** 	 * Defragment pages of this document. This will minimize the number of split 	 * pages. 	 *  	 * @param doc 	 *            to defrag 	 */
specifier|public
specifier|abstract
name|void
name|defragXMLResource
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/** 	 * Perform a consistency check on the specified document. 	 *  	 * This checks if the DOM tree is consistent. 	 *  	 * @param doc 	 */
specifier|public
specifier|abstract
name|void
name|checkXMLResourceTree
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|checkXMLResourceConsistency
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|/** 	 * Sync dom and collection state data (pages) to disk. In case of 	 * {@link org.exist.storage.sync.Sync#MAJOR_SYNC}, sync all states (dom, 	 * collection, text and element) to disk. 	 *  	 * @param syncEvent 	 *            Sync.MAJOR_SYNC or Sync.MINOR_SYNC 	 */
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|(
name|int
name|syncEvent
parameter_list|)
function_decl|;
comment|/** 	 * Update a node's data. To keep nodes in a correct sequential order, it is 	 * sometimes necessary to update a previous written node. Warning: don't use 	 * it for other purposes. 	 *  	 * @param node 	 *            Description of the Parameter 	 */
specifier|public
specifier|abstract
name|void
name|updateNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|boolean
name|reindex
parameter_list|)
function_decl|;
comment|/** 	 * Is the database running read-only? Returns false by default. Storage 	 * backends should override this if they support read-only mode. 	 *  	 * @return boolean 	 */
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
specifier|public
specifier|abstract
name|void
name|insertNodeAfter
parameter_list|(
name|Txn
name|transaction
parameter_list|,
specifier|final
name|StoredNode
name|previous
parameter_list|,
specifier|final
name|StoredNode
name|node
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|indexNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|)
function_decl|;
specifier|public
name|void
name|indexNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
name|indexNode
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|removeNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|removeAllNodes
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|StreamListener
name|listener
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|endRemove
parameter_list|(
name|Txn
name|transaction
parameter_list|)
function_decl|;
comment|/** 	 * Create a temporary document in the temp collection and store the supplied 	 * data. 	 *  	 * @param doc 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 * @throws LockException 	 */
specifier|public
specifier|abstract
name|DocumentImpl
name|storeTempResource
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
function_decl|;
comment|/** 	 * Clean up any temporary resources. 	 *  	 */
specifier|public
specifier|abstract
name|void
name|cleanUpTempCollection
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** 	 * Clean up temporary resources. Called by the sync daemon. 	 *  	 */
specifier|public
specifier|abstract
name|void
name|cleanUpTempResources
parameter_list|()
function_decl|;
comment|/** Convenience method that allows to check available memory during broker-related processes. 	 * This method should eventually trigger flush() events. 	 */
specifier|public
specifier|abstract
name|void
name|checkAvailableMemory
parameter_list|()
function_decl|;
comment|/** 	 *  	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getXMLResourcesByDoctype
parameter_list|(
name|String
name|doctype
parameter_list|,
name|DocumentSet
name|result
parameter_list|)
function_decl|;
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
return|return
name|referenceCount
return|;
block|}
specifier|public
name|void
name|incReferenceCount
parameter_list|()
block|{
operator|++
name|referenceCount
expr_stmt|;
block|}
specifier|public
name|void
name|decReferenceCount
parameter_list|()
block|{
operator|--
name|referenceCount
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|int
name|getPageSize
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|IndexSpec
name|getIndexConfiguration
parameter_list|()
function_decl|;
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
specifier|abstract
name|EmbeddedXMLStreamReader
name|getXMLStreamReader
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLStreamException
function_decl|;
specifier|public
specifier|abstract
name|EmbeddedXMLStreamReader
name|getXMLStreamReader
parameter_list|(
name|NodeProxy
name|node
parameter_list|,
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLStreamException
function_decl|;
block|}
end_class

end_unit

