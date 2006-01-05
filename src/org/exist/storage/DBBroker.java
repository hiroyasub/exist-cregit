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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|NodeSet
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
name|storage
operator|.
name|io
operator|.
name|VariableByteInput
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
name|io
operator|.
name|VariableByteInputStream
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
name|io
operator|.
name|VariableByteOutputStream
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|NodeList
import|;
end_import

begin_comment
comment|/**  * This is the base class for all database backends. All the basic database operations like storing,  * removing or index access are provided by subclasses of this class.  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  */
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
name|int
name|NATIVE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NATIVE_CLUSTER
init|=
literal|1
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
name|File
name|symbolsFile
decl_stmt|;
specifier|protected
name|SymbolTable
name|symbols
init|=
literal|null
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
name|int
name|xupdateGrowthFactor
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|docFragmentationLimit
init|=
literal|25
decl_stmt|;
specifier|protected
name|boolean
name|xupdateConsistencyChecks
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|id
decl_stmt|;
comment|/** 	 * Save the global symbol table. The global symbol table stores 	 * QNames and namespace/prefix mappings. 	 *   	 * @throws EXistException 	 */
specifier|protected
name|void
name|saveSymbols
parameter_list|()
throws|throws
name|EXistException
block|{
synchronized|synchronized
init|(
name|symbols
init|)
block|{
try|try
block|{
name|VariableByteOutputStream
name|os
init|=
operator|new
name|VariableByteOutputStream
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|symbols
operator|.
name|write
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|symbols
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"file not found: "
operator|+
name|symbols
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
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
name|EXistException
argument_list|(
literal|"io error occurred while creating "
operator|+
name|symbolsFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** 	 * Read the global symbol table. The global symbol table stores 	 * QNames and namespace/prefix mappings. 	 *   	 * @throws EXistException 	 */
specifier|protected
name|void
name|loadSymbols
parameter_list|()
throws|throws
name|EXistException
block|{
try|try
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|symbols
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|VariableByteInput
name|is
init|=
operator|new
name|VariableByteInputStream
argument_list|(
name|fis
argument_list|)
decl_stmt|;
name|symbols
operator|.
name|read
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"could not read "
operator|+
name|symbolsFile
operator|.
name|getAbsolutePath
argument_list|()
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
name|EXistException
argument_list|(
literal|"io error occurred while reading "
operator|+
name|symbolsFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|backupSymbolsTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|symbols
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SymbolTable
name|getSymbols
parameter_list|()
block|{
return|return
name|symbols
return|;
block|}
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
decl_stmt|;
if|if
condition|(
operator|(
name|temp
operator|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"indexer.case-sensitive"
argument_list|)
operator|)
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
name|String
name|dataDir
decl_stmt|;
if|if
condition|(
operator|(
name|dataDir
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"db-connection.data-dir"
argument_list|)
operator|)
operator|==
literal|null
condition|)
name|dataDir
operator|=
literal|"data"
expr_stmt|;
if|if
condition|(
operator|(
name|symbols
operator|=
operator|(
name|SymbolTable
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"db-connection.symbol-table"
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|symbolsFile
operator|=
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"symbols.dbx"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading symbol table from "
operator|+
name|symbolsFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|symbols
operator|=
operator|new
name|SymbolTable
argument_list|(
name|symbolsFile
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|symbolsFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|saveSymbols
argument_list|()
expr_stmt|;
block|}
else|else
name|loadSymbols
argument_list|()
expr_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
literal|"db-connection.symbol-table"
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|xupdateGrowthFactor
operator|=
name|config
operator|.
name|getInteger
argument_list|(
literal|"xupdate.growth-factor"
argument_list|)
operator|)
operator|<
literal|0
condition|)
name|xupdateGrowthFactor
operator|=
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|docFragmentationLimit
operator|=
name|config
operator|.
name|getInteger
argument_list|(
literal|"xupdate.fragmentation"
argument_list|)
operator|)
operator|<
literal|0
condition|)
name|docFragmentationLimit
operator|=
literal|50
expr_stmt|;
if|if
condition|(
operator|(
name|temp
operator|=
operator|(
name|Boolean
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"xupdate.consistency-checks"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
name|xupdateConsistencyChecks
operator|=
name|temp
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"fragmentation = "
operator|+
name|docFragmentationLimit
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
block|}
comment|/** 	 * Set the user that is currently using this DBBroker object. 	 *  	 * @param user 	 */
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
block|}
comment|/** 	 * Get the user that is currently using this DBBroker object. 	 *  	 * @return 	 */
specifier|public
name|User
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/** 	 * Returns a reference to the global {@link XQuery} service. 	 *  	 * @return 	 */
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
comment|/**  Flush all data that has not been written before. */
specifier|public
name|void
name|flush
parameter_list|()
block|{
comment|/* 		 *  do nothing 		 */
block|}
comment|/** 	 *  Adds all the documents in the database to the specified DocumentSet.      *        *  @param docs a (possibly empty) document set to which the found      *  documents are added. 	 * 	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getAllDocuments
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
function_decl|;
comment|/** 	 *  Returns the database collection identified by the specified path. 	 * The path should be absolute, e.g. /db/shakespeare. 	 *  	 * @return collection or null if no collection matches the path 	 */
specifier|public
specifier|abstract
name|Collection
name|getCollection
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** 	 * Returns the database collection identified by the specified path. 	 * The storage address is used to locate the collection without 	 * looking up the path in the btree. 	 *  	 * @return 	 */
specifier|public
specifier|abstract
name|Collection
name|getCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|address
parameter_list|)
function_decl|;
comment|/** 	 * Open a collection for reading or writing. The collection is identified by its 	 * absolute path, e.g. /db/shakespeare. It will be loaded and locked according to the 	 * lockMode argument.  	 *  	 * The caller should take care to release the collection lock properly. 	 *  	 * @param name the collection path 	 * @param lockMode one of the modes specified in class {@link org.exist.storage.lock.Lock} 	 * @return collection or null if no collection matches the path 	 */
specifier|public
specifier|abstract
name|Collection
name|openCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|lockMode
parameter_list|)
function_decl|;
comment|/** 	 *  Returns the database collection identified by the specified path. 	 * If the collection does not yet exist, it is created - including all 	 * ancestors. The path should be absolute, e.g. /db/shakespeare. 	 *  	 * @return collection or null if no collection matches the path 	 */
specifier|public
name|Collection
name|getOrCreateCollection
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 *  Returns the configuration object used to initialize the  	 * current database instance. 	 *  	 */
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/** 	 *  Return a {@link org.exist.storage.dom.DOMFileIterator} starting 	 * at the specified node. 	 * 	 */
specifier|public
name|Iterator
name|getDOMIterator
parameter_list|(
name|Document
name|doc
parameter_list|,
name|long
name|gid
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
comment|/** 	 *  Return a {@link org.exist.storage.dom.DOMFileIterator} starting 	 * at the specified node. 	 * 	 */
specifier|public
name|Iterator
name|getDOMIterator
parameter_list|(
name|NodeProxy
name|proxy
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
comment|/** 	 * Return a {@link org.exist.storage.dom.NodeIterator} starting 	 * at the specified node. 	 *  	 * @param proxy 	 * @return 	 */
specifier|public
name|Iterator
name|getNodeIterator
parameter_list|(
name|NodeProxy
name|proxy
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
comment|/** 	 *  Return the document stored at the specified path. The 	 * path should be absolute, e.g. /db/shakespeare/plays/hamlet.xml. 	 *  	 * @return the document or null if no document could be found at the 	 * specified location. 	 */
specifier|public
specifier|abstract
name|Document
name|getDocument
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
specifier|abstract
name|DocumentImpl
name|openDocument
parameter_list|(
name|String
name|docPath
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
name|getNextDocumentId
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 * Get the string value of the specified node.      *       * If addWhitespace is set to true, an extra space character will be      * added between adjacent elements in mixed content nodes. 	 */
specifier|public
name|String
name|getNodeValue
parameter_list|(
name|NodeProxy
name|proxy
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
comment|/** 	 *  Find all Nodes whose string value is equal to expr in the document set. 	 * 	 *@param  context   the set of nodes to process 	 *@param  docs      the current set of documents 	 *@param  relation  less-than, equal etc. One of the constants specified in 	 *{@link org.exist.xquery.Constants} 	 *@param  expr      the string value to search for         	 */
specifier|public
specifier|abstract
name|NodeSet
name|getNodesEqualTo
parameter_list|(
name|NodeSet
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|int
name|relation
parameter_list|,
name|int
name|truncation
parameter_list|,
name|String
name|expr
parameter_list|,
name|Collator
name|collator
parameter_list|)
function_decl|;
comment|/** 	 *  Get a range of nodes with given owner document from the database, 	 *  starting at first and ending at last. 	 * 	 *@param  doc    the document the nodes belong to 	 *@param  first  unique id of the first node to retrieve 	 *@param  last   unique id of the last node to retrieve 	 */
specifier|public
specifier|abstract
name|NodeList
name|getRange
parameter_list|(
name|Document
name|doc
parameter_list|,
name|long
name|first
parameter_list|,
name|long
name|last
parameter_list|)
function_decl|;
comment|/** 	 *  Get an instance of the Serializer used for converting nodes back to XML. 	 *  Subclasses of DBBroker may have specialized subclasses of Serializer to 	 *  convert a node into an XML-string 	 */
specifier|public
specifier|abstract
name|Serializer
name|getSerializer
parameter_list|()
function_decl|;
comment|/** 	 *  Get the TextSearchEngine associated with this broker. Every subclass of 	 *  DBBroker will have it's own implementation of TextSearchEngine. 	 */
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
name|NativeValueIndexByQName
name|getQNameValueIndex
parameter_list|()
function_decl|;
comment|/** 	 *  Is string comparison case sensitive? 	 * 	 */
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|()
block|{
return|return
name|caseSensitive
return|;
block|}
specifier|public
specifier|abstract
name|Serializer
name|newSerializer
parameter_list|()
function_decl|;
comment|/** 	 *  Get a node with given owner document and id from the database. 	 * 	 *@param  doc  the document the node belongs to 	 *@param  gid  the node's unique identifier 	 */
specifier|public
specifier|abstract
name|Node
name|objectWith
parameter_list|(
name|Document
name|doc
parameter_list|,
name|long
name|gid
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|Node
name|objectWith
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
function_decl|;
comment|/** 	 * Remove the collection and all its subcollections from 	 * the database. 	 *  	 */
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
function_decl|;
comment|/** 	 *  Remove a document from the database. 	 * 	 */
specifier|public
name|void
name|removeDocument
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
name|removeDocument
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
name|removeDocument
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
comment|/** 	 * Reindex a collection. 	 *  	 * @param collectionName 	 * @throws PermissionDeniedException 	 */
specifier|public
specifier|abstract
name|void
name|reindex
parameter_list|(
name|String
name|collectionName
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|repair
parameter_list|()
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Saves the specified collection to storage. Collections are usually cached in      * memory. If a collection is modified, this method needs to be called to make      * the changes persistent.      * Note: appending a new document to a collection does not require a save.      * Instead, {@link #addDocument(Collection, DocumentImpl)} is called.      *      * @param collection to store 	 */
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
function_decl|;
specifier|public
name|void
name|closeDocument
parameter_list|()
block|{
block|}
comment|/** 	 *  Shut down the database instance. All open files, jdbc connections etc. should be 	 *  closed. 	 */
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
block|}
comment|/** 	 *  Store a node into the database. This method is called by the parser to 	 *  write a node to the storage backend. 	 * 	 *@param  node         the node to be stored 	 *@param  currentPath  path expression which points to this node's 	 *      element-parent or to itself if it is an element (currently used by 	 *      the Broker to determine if a node's content should be 	 *      fulltext-indexed). 	 */
specifier|public
specifier|abstract
name|void
name|store
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
name|store
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
name|store
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
comment|/**      * Update indexes for the given element node. This method is called when the indexer      * encounters a closing element tag. It updates any range indexes defined on the      * element value and adds the element id to the structural index.      *       * @param node the current element node      * @param currentPath node path leading to the element      * @param content contains the string value of the element. Needed if a range index      * is defined on it.      */
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
parameter_list|)
function_decl|;
comment|/** 	 * Store a document (descriptor) into the database      * (all metadata information which is returned by       * {@link org.exist.dom.DocumentImpl#serialize()}). 	 * 	 * @param doc the document's metadata to store. 	 */
specifier|public
specifier|abstract
name|void
name|storeDocument
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|readDocuments
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|readDocumentMeta
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/**      * Stores the given data under the given binary resource descriptor       * (BinaryDocument).      *       * @param blob the binary document descriptor      * @param data the document binary data      */
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
comment|/**      * Retrieve the binary data stored under the resource descriptor      * BinaryDocument.      *       * @param blob the binary document descriptor      * @return the document binary data      */
specifier|public
specifier|abstract
name|byte
index|[]
name|getBinaryResourceData
parameter_list|(
name|BinaryDocument
name|blob
parameter_list|)
function_decl|;
comment|/**      * Completely delete this binary document (descriptor and binary      * data).      *       * @param blob the binary document descriptor      * @throws PermissionDeniedException if you don't have the right to do this      */
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
comment|/** 	 * Move a collection and all its subcollections to another collection and rename it. 	 * Moving a collection just modifies the collection path and all resource paths. The 	 * data itself remains in place. 	 *  	 * @param collection the collection to move 	 * @param destination the destination collection 	 * @param newName the new name the collection should have in the destination collection 	 */
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
name|String
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
function_decl|;
comment|/** 	 * Move a resource to the destination collection and rename it. 	 *  	 * @param doc the resource to move 	 * @param destination the destination collection 	 * @param new Name the new name the resource should have in the destination collection 	 */
specifier|public
specifier|abstract
name|void
name|moveResource
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
name|String
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
function_decl|;
comment|/** 	 * Copy a collection to the destination collection and rename it. 	 *  	 * @param doc the resource to move 	 * @param destination the destination collection 	 * @param new Name the new name the resource should have in the destination collection 	 */
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
name|String
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
function_decl|;
comment|/** 	 * Copy a resource to the destination collection and rename it. 	 *  	 * @param doc the resource to copy 	 * @param destination the destination collection 	 * @param newName the new name the resource should have in the destination collection 	 * @throws PermissionDeniedException 	 * @throws LockException 	 */
specifier|public
specifier|abstract
name|void
name|copyResource
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
name|String
name|newName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
function_decl|;
comment|/**      * Defragment pages of this document. This will minimize the number of      * split pages.      *       * @param doc to defrag      */
specifier|public
specifier|abstract
name|void
name|defrag
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
name|checkTree
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|consistencyCheck
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|/**      * Sync dom and collection state data (pages) to disk.      * In case of {@link org.exist.storage.sync.Sync.MAJOR_SYNC}, sync all      * states (dom, collection, text and element) to disk.      *       * @param syncEvent Sync.MAJOR_SYNC or Sync.MINOR_SYNC      */
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|(
name|int
name|syncEvent
parameter_list|)
function_decl|;
comment|/** 	 *  Update a node's data. To keep nodes in a correct sequential order, it is sometimes  	 * necessary to update a previous written node. Warning: don't use it for other purposes. 	 * 	 *@param  node  Description of the Parameter 	 */
specifier|public
name|void
name|update
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
comment|/** 	 * Is the database running read-only? Returns false by default. 	 * Storage backends should override this if they support read-only 	 * mode. 	 *  	 * @return boolean 	 */
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
name|insertAfter
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
name|void
name|reindex
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|oldDoc
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|index
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
name|index
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
name|void
name|index
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
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
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
specifier|abstract
name|void
name|removeAll
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
name|endRemove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
comment|/** 	 * Create a temporary document in the temp collection and store the 	 * supplied data. 	 *  	 * @param data 	 * @return 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 * @throws LockException 	 */
specifier|public
specifier|abstract
name|DocumentImpl
name|storeTemporaryDoc
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
comment|/** 	 * Clean up any temporary resources. 	 * 	 */
specifier|public
specifier|abstract
name|void
name|cleanUpAll
parameter_list|()
function_decl|;
comment|/** 	 * Clean up temporary resources. Called by the sync daemon. 	 * 	 */
specifier|public
specifier|abstract
name|void
name|cleanUp
parameter_list|()
function_decl|;
comment|/** 	 * Remove the temporary document fragments specified by a list 	 * of names. 	 *  	 * @param docs 	 */
specifier|public
specifier|abstract
name|void
name|removeTempDocs
parameter_list|(
name|List
name|docs
parameter_list|)
function_decl|;
comment|/** 	 *    	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getDocumentsByDoctype
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
name|int
name|getXUpdateGrowthFactor
parameter_list|()
block|{
return|return
name|xupdateGrowthFactor
return|;
block|}
specifier|public
name|int
name|getFragmentationLimit
parameter_list|()
block|{
return|return
name|docFragmentationLimit
return|;
block|}
specifier|public
name|boolean
name|consistencyChecksEnabled
parameter_list|()
block|{
return|return
name|xupdateConsistencyChecks
return|;
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
name|int
name|getBackendType
parameter_list|()
function_decl|;
block|}
end_class

end_unit

