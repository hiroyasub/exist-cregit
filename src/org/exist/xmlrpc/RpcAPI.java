begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  *  Defines the methods callable through the XMLRPC interface.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_interface
specifier|public
interface|interface
name|RpcAPI
block|{
specifier|public
specifier|final
specifier|static
name|String
name|SORT_EXPR
init|=
literal|"sort-expr"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACES
init|=
literal|"namespaces"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VARIABLES
init|=
literal|"variables"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|BASE_URI
init|=
literal|"base-uri"
decl_stmt|;
comment|/** 	 * Shut down the database. 	 *  	 * @return boolean 	 */
specifier|public
name|boolean
name|shutdown
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
name|boolean
name|sync
parameter_list|(
name|User
name|user
parameter_list|)
function_decl|;
comment|/** 	 *  Retrieve document by name. XML content is indented if prettyPrint is set 	 *  to>=0. Use supplied encoding for output.  	 *  	 *  This method is provided to retrieve a document with encodings other than UTF-8. Since the data is 	 *  handled as binary data, character encodings are preserved. byte[]-values 	 *  are automatically BASE64-encoded by the XMLRPC library. 	 * 	 *@param  name                           the document's name. 	 *@param  prettyPrint                    pretty print XML if>0. 	 *@param  encoding                       character encoding to use. 	 *@param  user 	 *@return   Document data as binary array.  	 *@deprecated Use {@link #getDocument(User, String, Hashtable)} instead. 	 */
name|byte
index|[]
name|getDocument
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|encoding
parameter_list|,
name|int
name|prettyPrint
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Retrieve document by name. XML content is indented if prettyPrint is set 	 *  to>=0. Use supplied encoding for output and apply the specified stylesheet.  	 *  	 *  This method is provided to retrieve a document with encodings other than UTF-8. Since the data is 	 *  handled as binary data, character encodings are preserved. byte[]-values 	 *  are automatically BASE64-encoded by the XMLRPC library. 	 * 	 *@param  name                           the document's name. 	 *@param  prettyPrint                    pretty print XML if>0. 	 *@param  encoding                       character encoding to use. 	 *@param  user                           Description of the Parameter 	 *@return                                The document value 	 *@deprecated Use {@link #getDocument(User, String, Hashtable)} instead. 	 */
name|byte
index|[]
name|getDocument
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|encoding
parameter_list|,
name|int
name|prettyPrint
parameter_list|,
name|String
name|stylesheet
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 * Retrieve document by name.  All optional output parameters are passed as key/value pairs 	 * int the hashtable<code>parameters</code>. 	 *  	 * Valid keys may either be taken from {@link javax.xml.transform.OutputKeys} or  	 * {@link org.exist.storage.serializers.EXistOutputKeys}. For example, the encoding is identified by 	 * the value of key {@link javax.xml.transform.OutputKeys#ENCODING}. 	 * 	 *@param  name                           the document's name. 	 *@param  parameters                      Hashtable of parameters. 	 *@return                                The document value 	 */
name|byte
index|[]
name|getDocument
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|getDocumentAsString
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|prettyPrint
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|getDocumentAsString
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|prettyPrint
parameter_list|,
name|String
name|stylesheet
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|getDocumentAsString
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|byte
index|[]
name|getBinaryResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Does the document identified by<code>name</code> exist in the 	 *  repository? 	 * 	 *@param  name                           Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|boolean
name|hasDocument
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Get a list of all documents contained in the database. 	 * 	 *@param  user 	 *@return  list of document paths 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|Vector
name|getDocumentListing
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Get a list of all documents contained in the collection. 	 * 	 *@param  collection                     the collection to use. 	 *@param  user                           Description of the Parameter 	 *@return                                list of document paths 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|Vector
name|getDocumentListing
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|listDocumentPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|listCollectionPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Describe a collection: returns a struct with the  following fields: 	 *   	 *<pre> 	 *	name				The name of the collection 	 *	 	 *	owner				The name of the user owning the collection. 	 *	 	 *	group				The group owning the collection. 	 *	 	 *	permissions	The permissions that apply to this collection (int value) 	 *	 	 *	created			The creation date of this collection (long value) 	 *	 	 *	collections		An array containing the names of all subcollections. 	 *	 	 *	documents		An array containing a struct for each document in the collection. 	 *</pre> 	 * 	 *	Each of the elements in the "documents" array is another struct containing the properties 	 *	of the document: 	 * 	 *<pre> 	 *	name				The full path of the document. 	 *	 	 *	owner				The name of the user owning the document. 	 *	 	 *	group				The group owning the document. 	 *	 	 *	permissions	The permissions that apply to this document (int) 	 *	 	 *	type					Type of the resource: either "XMLResource" or "BinaryResource" 	 *</pre> 	 * 	 *@param  rootCollection                 Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                The collectionDesc value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|Hashtable
name|getCollectionDesc
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|rootCollection
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|describeCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|describeResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resourceName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 * Returns the number of resources in the collection identified by 	 * collectionName. 	 *  	 * @param user 	 * @param collection 	 * @return 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 */
name|int
name|getResourceCount
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Retrieve a single node from a document. The node is identified by it's 	 *  internal id. 	 * 	 *@param  doc                            the document containing the node 	 *@param  id                             the node's internal id 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|byte
index|[]
name|retrieve
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|doc
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Retrieve a single node from a document. The node is identified by it's 	 *  internal id. 	 * 	 *@param  doc                            the document containing the node 	 *@param  id                             the node's internal id 	 *@param  prettyPrint                    result is pretty printed if>0 	 *@param  encoding                       character encoding to use 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|byte
index|[]
name|retrieve
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|doc
parameter_list|,
name|String
name|id
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|retrieveAsString
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|doc
parameter_list|,
name|String
name|id
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
specifier|public
name|byte
index|[]
name|retrieveAll
parameter_list|(
name|User
name|user
parameter_list|,
name|int
name|resultId
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|queryP
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xpath
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|queryP
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xpath
parameter_list|,
name|String
name|docName
parameter_list|,
name|String
name|s_id
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  execute XPath query and return howmany nodes from the result set, 	 *  starting at position<code>start</code>. If<code>prettyPrint</code> is 	 *  set to>0 (true), results are pretty printed. 	 * 	 *@param  xpath                          the XPath query to execute 	 *@param  howmany                        maximum number of results to 	 *      return. 	 *@param  start                          item in the result set to start 	 *      with. 	 *@param  prettyPrint                    turn on pretty printing if>0. 	 *@param  encoding                       the character encoding to use. 	 *@param  sortExpr                       Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 *@depreceated                           use Vector query() or int 	 *      executeQuery() instead 	 */
name|String
name|query
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xquery
parameter_list|,
name|int
name|howmany
parameter_list|,
name|int
name|start
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  execute XPath query and return a summary of hits per document and hits 	 *  per doctype. This method returns a struct with the following fields: 	 * 	 *<table border="1"> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "queryTime" 	 *</td> 	 * 	 *<td> 	 *        int 	 *</td> 	 * 	 *</tr> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "hits" 	 *</td> 	 * 	 *<td> 	 *        int 	 *</td> 	 * 	 *</tr> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "documents" 	 *</td> 	 * 	 *<td> 	 *        array of array: Object[][3] 	 *</td> 	 * 	 *</tr> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "doctypes" 	 *</td> 	 * 	 *<td> 	 *        array of array: Object[][2] 	 *</td> 	 * 	 *</tr> 	 * 	 *</table> 	 *  Documents and doctypes represent tables where each row describes one 	 *  document or doctype for which hits were found. Each document entry has 	 *  the following structure: docId (int), docName (string), hits (int) The 	 *  doctype entry has this structure: doctypeName (string), hits (int) 	 * 	 *@param  xpath                          Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 *@depreceated                           use Vector query() or int 	 *      executeQuery() instead 	 */
name|Hashtable
name|querySummary
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|xquery
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|createResourceId
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Parse an XML document and store it into the database. The document will 	 *  later be identified by<code>docName</code>. Some xmlrpc clients seem to 	 *  have problems with character encodings when sending xml content. To 	 *  avoid this, parse() accepts the xml document content as byte[]. If 	 *<code>overwrite</code> is>0, an existing document with the same name 	 *  will be replaced by the new document. 	 * 	 *@param  xmlData                        The document data 	 *@param  docName                      The path where the document will be stored  	 *@return                                		 	 *@exception  EXistException 	 *@exception  PermissionDeniedException 	 */
name|boolean
name|parse
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xmlData
parameter_list|,
name|String
name|docName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Parse an XML document and store it into the database. The document will 	 *  later be identified by<code>docName</code>. Some xmlrpc clients seem to 	 *  have problems with character encodings when sending xml content. To 	 *  avoid this, parse() accepts the xml document content as byte[]. If 	 *<code>overwrite</code> is>0, an existing document with the same name 	 *  will be replaced by the new document. 	 * 	 *@param  xmlData                        The document data 	 *@param  docName                      The path where the document will be stored  	 *@param  overwrite                      Overwrite an existing document with the same path? 	 *@return                                		 	 *@exception  EXistException 	 *@exception  PermissionDeniedException 	 */
name|boolean
name|parse
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xmlData
parameter_list|,
name|String
name|docName
parameter_list|,
name|int
name|overwrite
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|parse
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|xml
parameter_list|,
name|String
name|docName
parameter_list|,
name|int
name|overwrite
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|parse
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|xml
parameter_list|,
name|String
name|docName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 * An alternative to parse() for larger XML documents. The document 	 * is first uploaded chunk by chunk using upload(), then parseLocal() is 	 * called to actually store the uploaded file. 	 *  	 * @param user 	 * @param chunk the current chunk 	 * @param length total length of the file  	 * @return the name of the file to which the chunk has been appended. 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 */
name|String
name|upload
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|chunk
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 * An alternative to parse() for larger XML documents. The document 	 * is first uploaded chunk by chunk using upload(), then parseLocal() is 	 * called to actually store the uploaded file. 	 *  	 * @param user 	 * @param chunk the current chunk 	 * @param file the name of the file to which the chunk will be appended. This 	 * should be the file name returned by the first call to upload. 	 * @param length total length of the file  	 * @return the name of the file to which the chunk has been appended. 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 */
name|String
name|upload
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|file
parameter_list|,
name|byte
index|[]
name|chunk
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|uploadCompressed
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|String
name|uploadCompressed
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|file
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 * Parse a file previously uploaded with upload. 	 *  	 * The temporary file will be removed. 	 *  	 * @param user 	 * @param localFile 	 * @throws EXistException 	 * @throws IOException 	 */
specifier|public
name|boolean
name|parseLocal
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|localFile
parameter_list|,
name|String
name|docName
parameter_list|,
name|boolean
name|replace
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
function_decl|;
comment|/** 	 * Store data as a binary resource. 	 *  	 * @param user 	 * @param data the data to be stored 	 * @param docName the path to the new document 	 * @param replace if true, an old document with the same path will be overwritten 	 * @return 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 */
specifier|public
name|boolean
name|storeBinary
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|String
name|docName
parameter_list|,
name|boolean
name|replace
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Remove a document from the database. 	 * 	 *@param  docName path to the document to be removed 	 *@param  user                            	 *@return                                true on success. 	 *@exception  EXistException              	 *@exception  PermissionDeniedException   	 */
name|boolean
name|remove
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|docName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Remove an entire collection from the database. 	 * 	 *@param  name path to the collection to be removed. 	 *@param  user 	 *@return 	 *@exception  EXistException              	 *@exception  PermissionDeniedException  	 */
name|boolean
name|removeCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/**  	 * Create a new collection on the database. 	 *  	 * @param user 	 * @param name the path to the new collection. 	 * @return 	 * @throws EXistException 	 * @throws PermissionDeniedException 	 */
name|boolean
name|createCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Execute XPath query and return a reference to the result set. The 	 *  returned reference may be used later to get a summary of results or 	 *  retrieve the actual hits. 	 * 	 *@param  xpath                          Description of the Parameter 	 *@param  encoding                       Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|int
name|executeQuery
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xpath
parameter_list|,
name|String
name|encoding
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|int
name|executeQuery
parameter_list|(
name|User
name|user
parameter_list|,
name|byte
index|[]
name|xpath
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|int
name|executeQuery
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|xpath
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Retrieve a summary of the result set identified by it's result-set-id. 	 *  This method returns a struct with the following fields: 	 * 	 *<tableborder="1"> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "queryTime" 	 *</td> 	 * 	 *<td> 	 *        int 	 *</td> 	 * 	 *</tr> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "hits" 	 *</td> 	 * 	 *<td> 	 *        int 	 *</td> 	 * 	 *</tr> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "documents" 	 *</td> 	 * 	 *<td> 	 *        array of array: Object[][3] 	 *</td> 	 * 	 *</tr> 	 * 	 *<tr> 	 * 	 *<td> 	 *        "doctypes" 	 *</td> 	 * 	 *<td> 	 *        array of array: Object[][2] 	 *</td> 	 * 	 *</tr> 	 * 	 *</table> 	 *  Documents and doctypes represent tables where each row describes one 	 *  document or doctype for which hits were found. Each document entry has 	 *  the following structure: docId (int), docName (string), hits (int) The 	 *  doctype entry has this structure: doctypeName (string), hits (int) 	 * 	 *@param  resultId                       Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|Hashtable
name|querySummary
parameter_list|(
name|User
name|user
parameter_list|,
name|int
name|resultId
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|getPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Get the number of hits in the result set identified by it's 	 *  result-set-id. 	 * 	 *@param  resultId                       Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                The hits value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|int
name|getHits
parameter_list|(
name|User
name|user
parameter_list|,
name|int
name|resultId
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/** 	 *  Retrieve a single result from the result-set identified by resultId. The 	 *  XML fragment at position num in the result set is returned. 	 * 	 *@param  resultId                       Description of the Parameter 	 *@param  num                            Description of the Parameter 	 *@param  prettyPrint                    Description of the Parameter 	 *@param  encoding                       Description of the Parameter 	 *@param  user                           Description of the Parameter 	 *@return                                Description of the Return Value 	 *@exception  EXistException             Description of the Exception 	 *@exception  PermissionDeniedException  Description of the Exception 	 */
name|byte
index|[]
name|retrieve
parameter_list|(
name|User
name|user
parameter_list|,
name|int
name|resultId
parameter_list|,
name|int
name|num
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|setUser
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|passwd
parameter_list|,
name|Vector
name|groups
parameter_list|,
name|String
name|home
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|setUser
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|passwd
parameter_list|,
name|Vector
name|groups
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|setPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|permissions
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|setPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|,
name|int
name|permissions
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|setPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|ownerGroup
parameter_list|,
name|String
name|permissions
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|setPermissions
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|ownerGroup
parameter_list|,
name|int
name|permissions
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
specifier|public
name|boolean
name|lockResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|userName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
specifier|public
name|boolean
name|unlockResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
specifier|public
name|String
name|hasUserLock
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Hashtable
name|getUser
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Vector
name|getUsers
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|removeUser
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Vector
name|getGroups
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Vector
name|getIndexedElements
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|Vector
name|scanIndexTerms
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
name|boolean
name|releaseQueryResult
parameter_list|(
name|User
name|user
parameter_list|,
name|int
name|handle
parameter_list|)
function_decl|;
name|int
name|xupdate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|byte
index|[]
name|xupdate
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|SAXException
function_decl|;
name|int
name|xupdateResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|,
name|byte
index|[]
name|xupdate
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|SAXException
function_decl|;
name|int
name|xupdateResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|resource
parameter_list|,
name|byte
index|[]
name|xupdate
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|SAXException
function_decl|;
name|Date
name|getCreationDate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
name|Vector
name|getTimestamps
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|documentName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
name|boolean
name|copyCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|namedest
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
name|Vector
name|getDocumentChunk
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|Hashtable
name|parameters
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
function_decl|;
name|byte
index|[]
name|getDocumentChunk
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|stop
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
function_decl|;
name|boolean
name|moveCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|collectionPath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|moveResource
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|docPath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
name|boolean
name|reindexCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
function_decl|;
block|}
end_interface

end_unit

