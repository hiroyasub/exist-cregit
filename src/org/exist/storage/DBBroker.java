begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  DBBroker.java - eXist Open Source Native XML Database  *  Copyright (C) 2003 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|Observable
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
name|NodeImpl
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
name|QName
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
name|Occurrences
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
name|VariableByteInputStream
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
name|VariableByteOutputStream
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
comment|/**  * This is the base class for all database backends. All other components rely  * on the methods defined here.  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  *@created    20. Mai 2002  */
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
comment|// constants for database type
specifier|public
specifier|final
specifier|static
name|int
name|MYSQL
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NATIVE
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ORACLE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|POSTGRESQL
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DBM
init|=
literal|3
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
specifier|static
name|File
name|symbolsFile
decl_stmt|;
specifier|protected
specifier|static
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
specifier|static
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
name|symbolsFile
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
specifier|protected
specifier|static
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
name|symbolsFile
argument_list|)
decl_stmt|;
name|VariableByteInputStream
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
specifier|static
specifier|synchronized
name|SymbolTable
name|getSymbols
parameter_list|()
block|{
return|return
name|symbols
return|;
block|}
comment|/** 	 *  Constructor for the DBBroker object 	 * 	 *@param  config  Description of the Parameter 	 */
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
name|sym
decl_stmt|,
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
name|symbols
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
name|symbols
operator|=
operator|new
name|SymbolTable
argument_list|()
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
block|}
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
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
specifier|public
name|User
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/** 	 *  find elements by their tag name. This method is comparable to the DOM's 	 *  method call getElementsByTagName. All elements matching tagName and 	 *  belonging to one of the documents in the DocumentSet docs are returned. 	 * 	 *@param  docs     Description of the Parameter 	 *@param  tagName  Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|public
specifier|abstract
name|NodeSet
name|findElementsByTagName
parameter_list|(
name|byte
name|type
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|qname
parameter_list|)
function_decl|;
comment|/**  flush all data that has not been written before. */
specifier|public
name|void
name|flush
parameter_list|()
block|{
comment|/* 		 *  do nothing 		 */
block|}
comment|/** 	 *  get all the documents in this database repository. The documents are 	 *  returned as a DocumentSet. 	 * 	 *@param  user  Description of the Parameter 	 *@return       The allDocuments value 	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getAllDocuments
parameter_list|()
function_decl|;
comment|/** 	 *  find elements by their tag name. This method is comparable to the DOM's 	 *  method call getElementsByTagName. All elements matching tagName and 	 *  belonging to one of the documents in the DocumentSet docs are returned. 	 * 	 *@param  docs  Description of the Parameter 	 *@param  name  Description of the Parameter 	 *@return       The attributesByName value 	 */
specifier|public
specifier|abstract
name|NodeSet
name|getAttributesByName
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|qname
parameter_list|)
function_decl|;
comment|/** 	 *  Gets the collection attribute of the DBBroker object 	 * 	 *@param  name  Description of the Parameter 	 *@return       The collection value 	 */
specifier|public
specifier|abstract
name|Collection
name|getCollection
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|Collection
name|getCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|address
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 *  get the configuration. 	 * 	 *@return    The configuration value 	 */
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/** 	 *  Gets the dOMIterator attribute of the DBBroker object 	 * 	 *@param  doc  Description of the Parameter 	 *@param  gid  Description of the Parameter 	 *@return      The dOMIterator value 	 */
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
comment|/** 	 *  Gets the dOMIterator attribute of the DBBroker object 	 * 	 *@param  proxy  Description of the Parameter 	 *@return        The dOMIterator value 	 */
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
comment|/** 	 *  return the type of database this broker is connected to. 	 * 	 *@return    one of the constants defined above. 	 */
specifier|public
specifier|abstract
name|int
name|getDatabaseType
parameter_list|()
function_decl|;
comment|/** 	 *  get a document by it's file name. The document's file name is used to 	 *  identify a document. File names are stored without the leading path. 	 * 	 *@param  fileName                       Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                The document value 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
specifier|public
specifier|abstract
name|Document
name|getDocument
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Gets the documentsByCollection attribute of the DBBroker object 	 * 	 *@param  collection                     Description of the Parameter 	 *@return                                The documentsByCollection value 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getDocumentsByCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Gets the documentsByCollection attribute of the DBBroker object 	 * 	 *@param  collection                     Description of the Parameter 	 *@param  inclusive                      Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                The documentsByCollection value 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getDocumentsByCollection
parameter_list|(
name|String
name|collection
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  get all the documents in this database matching the given 	 *  document-type's name. 	 * 	 *@param  doctypeName  Description of the Parameter 	 *@param  user         Description of the Parameter 	 *@return              The documentsByDoctype value 	 */
specifier|public
specifier|abstract
name|DocumentSet
name|getDocumentsByDoctype
parameter_list|(
name|String
name|doctype
parameter_list|)
function_decl|;
comment|/** 	 *  get a common prefix for a namespace URI. It should be guaranteed that 	 *  only one prefix is associated with one namespace URI throughout the 	 *  database. 	 * 	 *@param  namespace  Description of the Parameter 	 *@return            The namespacePrefix value 	 */
specifier|public
name|String
name|getNamespacePrefix
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
comment|/** 	 *  get the namespace associated with the given prefix. Every broker 	 *  subclass should keep an internal map, where it stores the prefixes used 	 *  for different namespaces. It should be guaranteed that only one prefix 	 *  is associated with one namespace URI. 	 * 	 *@param  prefix  Description of the Parameter 	 *@return         The namespaceURI value 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
comment|/** 	 *  Gets the nextDocId attribute of the DBBroker object 	 * 	 *@param  collection  Description of the Parameter 	 *@return             The nextDocId value 	 */
specifier|public
specifier|abstract
name|int
name|getNextDocId
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 *  Gets the nodeValue attribute of the DBBroker object 	 * 	 *@param  proxy  Description of the Parameter 	 *@return        The nodeValue value 	 */
specifier|public
name|String
name|getNodeValue
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
comment|/** 	 *  find all Nodes whose string value is equal to expr in the document set. 	 * 	 *@param  context   Description of the Parameter 	 *@param  docs      Description of the Parameter 	 *@param  relation  Description of the Parameter 	 *@param  expr      Description of the Parameter 	 *@return           The nodesEqualTo value 	 */
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
name|String
name|expr
parameter_list|)
function_decl|;
comment|/** 	 *  Retrieve a collection by name. This method is used by NativeBroker.java. 	 * 	 *@param  name                           Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                The orCreateCollection value 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
specifier|public
name|Collection
name|getOrCreateCollection
parameter_list|(
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
comment|/** 	 *  get a range of nodes with given owner document from the database, 	 *  starting at first and ending at last. 	 * 	 *@param  doc    the document the node's belong to 	 *@param  first  unique id of the first node to retrieve 	 *@param  last   unique id of the last node to retrieve 	 *@return        The range value 	 */
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
comment|/** 	 *  get an instance of the Serializer used for converting nodes back to XML. 	 *  Subclasses of DBBroker may have specialized subclasses of Serializer to 	 *  convert a node into an XML-string 	 * 	 *@return    The serializer value 	 */
specifier|public
specifier|abstract
name|Serializer
name|getSerializer
parameter_list|()
function_decl|;
comment|/** 	 *  get the TextSearchEngine associated with this broker. Every subclass of 	 *  DBBroker will have it's own implementation of TextSearchEngine. 	 * 	 *@return    The textEngine value 	 */
specifier|public
specifier|abstract
name|TextSearchEngine
name|getTextEngine
parameter_list|()
function_decl|;
comment|/** 	 *  Gets the caseSensitive attribute of the DBBroker object 	 * 	 *@return    The caseSensitive value 	 */
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|()
block|{
return|return
name|caseSensitive
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
specifier|abstract
name|Serializer
name|newSerializer
parameter_list|()
function_decl|;
comment|/** 	 *  get a node with given owner document and id from the database. 	 * 	 *@param  doc  the document the node belongs to 	 *@param  gid  the node's unique identifier 	 *@return      Description of the Return Value 	 */
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
comment|/** 	 *  associate a prefix with a given namespace. Every broker subclass should 	 *  keep an internal map, where it stores the prefixes used for different 	 *  namespaces. It should be guaranteed that only one prefix is associated 	 *  with one namespace URI. 	 * 	 *@param  namespace  Description of the Parameter 	 *@param  prefix     Description of the Parameter 	 */
specifier|public
name|void
name|registerNamespace
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
comment|// do nothing
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  name                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
specifier|public
specifier|abstract
name|boolean
name|removeCollection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  remove the document with the given document name. 	 * 	 *@param  docName                        Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
specifier|public
specifier|abstract
name|void
name|removeDocument
parameter_list|(
name|String
name|docName
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Store a collection into the database. 	 * 	 *@param  collection  Description of the Parameter 	 */
specifier|public
specifier|abstract
name|void
name|saveCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
name|void
name|addDocument
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
block|}
specifier|public
name|void
name|closeDocument
parameter_list|()
block|{
block|}
comment|/** 	 *  shutdown the broker. All open files, jdbc connections etc. should be 	 *  closed. 	 */
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
name|NodeImpl
name|node
parameter_list|,
name|CharSequence
name|currentPath
parameter_list|)
function_decl|;
comment|/** 	 *  Store a document into the database. 	 * 	 *@param  doc  Description of the Parameter 	 */
specifier|public
specifier|abstract
name|void
name|storeDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/**  Description of the Method */
specifier|public
name|void
name|sync
parameter_list|()
block|{
comment|/* 		 *  do nothing 		 */
block|}
comment|/** 	 *  Update a node's data. This method is only used by the NativeBroker. To 	 *  keep nodes in a correct sequential order, it sometimes needs to update a 	 *  previous written node. Warning: don't use it for other purposes. 	 *  RelationalBroker does not implement this method. 	 * 	 *@param  node  Description of the Parameter 	 */
specifier|public
name|void
name|update
parameter_list|(
name|NodeImpl
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
name|void
name|insertAfter
parameter_list|(
specifier|final
name|NodeImpl
name|previous
parameter_list|,
specifier|final
name|NodeImpl
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
name|reindex
parameter_list|(
name|DocumentImpl
name|oldDoc
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|NodeImpl
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
name|NodeImpl
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
name|removeNode
parameter_list|(
specifier|final
name|NodeImpl
name|node
parameter_list|,
name|String
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
specifier|public
name|Occurrences
index|[]
name|scanIndexedElements
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|PermissionDeniedException
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
name|readDocumentMetadata
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

