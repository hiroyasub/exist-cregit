begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|List
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
name|collections
operator|.
name|IndexInfo
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
name|SecurityManager
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
name|xacml
operator|.
name|AccessContext
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
name|Constants
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
name|XPathException
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceIterator
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

begin_comment
comment|/**  *  Helper class for accessing grammars.  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseResources
block|{
comment|/** Local reference to database  */
specifier|private
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
comment|/** Local logger */
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DatabaseResources
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Path to grammar in database  */
specifier|public
name|String
name|GRAMMARBASE
init|=
name|DBBroker
operator|.
name|SYSTEM_COLLECTION
operator|+
literal|"/grammar"
decl_stmt|;
specifier|public
name|String
name|XSDBASE
init|=
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
comment|// TODO check is this ok
specifier|public
name|String
name|DTDBASE
init|=
name|GRAMMARBASE
operator|+
literal|"/dtd"
decl_stmt|;
specifier|public
name|String
name|DTDCATALOG
init|=
name|DTDBASE
operator|+
literal|"/catalog.xml"
decl_stmt|;
specifier|public
specifier|static
name|String
name|NOGRAMMAR
init|=
literal|"NONE"
decl_stmt|;
comment|//    // TODO remove
comment|//    public static int GRAMMAR_UNKNOWN = 0;
specifier|public
specifier|static
name|int
name|GRAMMAR_XSD
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|int
name|GRAMMAR_DTD
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OASISCATALOGURN
init|=
literal|"urn:oasis:names:tc:entity:xmlns:xml:catalog"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FINDSCHEMA
init|=
literal|"for $schema in collection('COLLECTION')/xs:schema"
operator|+
literal|"[@targetNamespace = 'TARGET'] return document-uri($schema)"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FINDCATALOG
init|=
literal|"declare namespace catalogns='"
operator|+
name|OASISCATALOGURN
operator|+
literal|"';"
operator|+
literal|"for $catalog in collection('COLLECTION')/catalogns:catalog "
operator|+
literal|"return document-uri($catalog)"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FINDDTD
init|=
literal|"let $docs := for $doc in collection($collection) "
operator|+
literal|"return document-uri($doc) for $doc in $docs "
operator|+
literal|"where ends-with($doc, '.dtd') return $doc"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FINDXSDINCATALOG
init|=
literal|"declare namespace ctlg='"
operator|+
name|OASISCATALOGURN
operator|+
literal|"';"
operator|+
literal|"for $schema in fn:document('CATALOGFILE')/ctlg:catalog"
operator|+
literal|"/ctlg:uri[@name = 'NAMESPACE']/@uri return $schema"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FINDDTDINCATALOG
init|=
literal|"declare namespace ctlg='"
operator|+
name|OASISCATALOGURN
operator|+
literal|"';"
operator|+
literal|"for $dtd in fn:document('CATALOGFILE')/ctlg:catalog"
operator|+
literal|"/ctlg:public[@publicId = 'PUBLICID']/@uri return $dtd"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FINDPUBLICIDINCATALOGS
init|=
literal|"declare namespace ctlg='"
operator|+
name|OASISCATALOGURN
operator|+
literal|"';"
operator|+
literal|"for $dtd in collection('COLLECTION')/ctlg:catalog"
operator|+
literal|"/ctlg:public[@publicId = 'PUBLICID']/@uri "
operator|+
literal|"return document-uri($dtd)"
decl_stmt|;
comment|/**      *  Execute xquery.      *      * @param query  The xQuery      * @return  Sequence when results are available, null when errors occur.      */
specifier|private
name|Sequence
name|executeQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error getting DBBroker"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|result
operator|=
name|broker
operator|.
name|getXQueryService
argument_list|()
operator|.
name|execute
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|VALIDATION_INTERNAL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Problem executing xquery"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      *  Execute xquery, return single result.      *      * @param   query  The xQuery      * @return  String When a result is available, null when an error occured.      */
specifier|private
name|String
name|executeQuerySingleResult
parameter_list|(
name|String
name|xquery
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// execute query
name|Sequence
name|sequence
init|=
name|executeQuery
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
name|SequenceIterator
name|i
init|=
name|sequence
operator|.
name|iterate
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Single query result: '"
operator|+
name|result
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"No query result."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"xQuery issue "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      *  Execute xquery, return multiple result.      *      * @param   query  The xQuery      * @return  List of Strings when a result is available, null when an      *          error occured.      */
specifier|private
name|List
name|executeQueryListResult
parameter_list|(
name|String
name|xquery
parameter_list|)
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
comment|// execute query
name|Sequence
name|sequence
init|=
name|executeQuery
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
name|SequenceIterator
name|i
init|=
name|sequence
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Query yielded "
operator|+
name|sequence
operator|.
name|getLength
argument_list|()
operator|+
literal|" hits."
argument_list|)
expr_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|path
init|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"xQuery issue."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      *  Find document path of XSD describing a namespace.      *      * @param collection    Start point for search, e.g. '/db'.      * @param namespace     Namespace that needs to be found.      * @return              Document path (e.g. '/db/foo/bar.xsd') if found,      *                      null if namespace could not be found.      */
specifier|public
name|String
name|getSchemaPath
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Find schema with namespace '"
operator|+
name|namespace
operator|+
literal|"' in '"
operator|+
name|collection
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
comment|// Fill parameters for query
name|String
name|xquery
init|=
name|FINDSCHEMA
operator|.
name|replaceAll
argument_list|(
literal|"COLLECTION"
argument_list|,
name|collection
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"TARGET"
argument_list|,
name|namespace
argument_list|)
decl_stmt|;
return|return
name|executeQuerySingleResult
argument_list|(
name|xquery
argument_list|)
return|;
block|}
comment|/**      *  Find catalogs in database recursively.      *      * @param collection  Start point for search, e.g. /db      * @return  List of document paths (strings), e.g. /db/foo/bar/catalog.xml.      */
specifier|public
name|List
name|getCatalogs
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Find catalogs with namespace '"
operator|+
name|OASISCATALOGURN
operator|+
literal|"' in '"
operator|+
name|collection
operator|+
literal|"'."
argument_list|)
expr_stmt|;
comment|// Fill parameters for query
name|String
name|xquery
init|=
name|FINDCATALOG
operator|.
name|replaceAll
argument_list|(
literal|"COLLECTION"
argument_list|,
name|collection
argument_list|)
decl_stmt|;
return|return
name|executeQueryListResult
argument_list|(
name|xquery
argument_list|)
return|;
block|}
comment|/**      *  Find document catalogPath of DTD describing a publicId.      *      * @param collection    Start point for search, e.g. '/db'.      * @param publicId      PublicID that needs to be found.      * @return Document catalogPath (e.g. '/db/foo/bar.dtd') if found,      *                      null if publicID could not be found.      */
specifier|public
name|String
name|getDtdPath
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
name|publicId
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Find DTD with publicID '"
operator|+
name|publicId
operator|+
literal|"' in '"
operator|+
name|collection
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|String
name|dtdPath
init|=
literal|null
decl_stmt|;
comment|// Find all catalogs containing publicId
name|String
name|xquery
init|=
name|FINDPUBLICIDINCATALOGS
operator|.
name|replaceAll
argument_list|(
literal|"COLLECTION"
argument_list|,
name|collection
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"PUBLICID"
argument_list|,
name|publicId
argument_list|)
decl_stmt|;
name|String
name|catalogPath
init|=
name|executeQuerySingleResult
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
comment|// Get from selected catalog file the publicId
if|if
condition|(
name|catalogPath
operator|!=
literal|null
condition|)
block|{
name|XmldbURI
name|col
init|=
literal|null
decl_stmt|;
try|try
block|{
name|col
operator|=
operator|new
name|XmldbURI
argument_list|(
literal|"xmldb:exist://"
operator|+
name|getCollectionPath
argument_list|(
name|catalogPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|String
name|docName
init|=
name|getDocumentName
argument_list|(
name|catalogPath
argument_list|)
decl_stmt|;
name|dtdPath
operator|=
name|getDtdPathFromCatalog
argument_list|(
name|col
argument_list|,
name|docName
argument_list|,
name|publicId
argument_list|)
expr_stmt|;
block|}
return|return
name|dtdPath
return|;
block|}
comment|/**      *  Get document from database.      *      * @param isBinary      Indicate wether resource is binary.      * @param documentPath  Path to the resource.      * @return              Byte array of resource, null if not found.      */
specifier|public
name|byte
index|[]
name|getGrammar
parameter_list|(
name|boolean
name|isBinary
parameter_list|,
name|String
name|documentPath
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Get resource '"
operator|+
name|documentPath
operator|+
literal|"' binary="
operator|+
name|isBinary
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
if|if
condition|(
name|isBinary
condition|)
block|{
name|BinaryDocument
name|binDoc
init|=
operator|(
name|BinaryDocument
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|documentPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
comment|// if document is not present, null is returned
if|if
condition|(
name|binDoc
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Binary document '"
operator|+
name|documentPath
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|binDoc
argument_list|)
expr_stmt|;
name|binDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|DocumentImpl
name|doc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|documentPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
comment|// if document is not present, null is returned
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Xml document '"
operator|+
name|documentPath
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|data
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error opening document"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error serializing document"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|data
return|;
block|}
comment|/**      *  Inser document to database. Not well tested yet.      *      * @param grammar      ByteArray containing file.      * @param isBinary     Indicate wether resource is binary.      * @param documentPath Path to the resource.      * @return             TRUE if successfull, FALSE if not.      */
specifier|public
name|boolean
name|insertGrammar
parameter_list|(
name|boolean
name|isBinary
parameter_list|,
name|String
name|documentPath
parameter_list|,
name|byte
index|[]
name|grammar
parameter_list|)
block|{
name|boolean
name|insertIsSuccesfull
init|=
literal|false
decl_stmt|;
name|String
name|collectionName
init|=
name|DatabaseResources
operator|.
name|getCollectionPath
argument_list|(
name|documentPath
argument_list|)
decl_stmt|;
name|String
name|documentName
init|=
name|DatabaseResources
operator|.
name|getDocumentName
argument_list|(
name|documentPath
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|brokerPool
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
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|collectionName
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
if|if
condition|(
name|isBinary
condition|)
block|{
comment|// TODO : call mime-type stuff for goof mimetypes
name|BinaryDocument
name|doc
init|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|documentName
argument_list|,
name|grammar
argument_list|,
literal|"text/text"
argument_list|)
decl_stmt|;
block|}
else|else
block|{
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|documentName
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|grammar
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|grammar
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|insertIsSuccesfull
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|insertIsSuccesfull
return|;
block|}
comment|/**      * Creates a new instance of DatabaseResources.      *      * @param pool  Instance shared broker pool.      */
specifier|public
name|DatabaseResources
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing DatabaseResources"
argument_list|)
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      *  Get document name from path.      *      *  /db/foo/bar/doc.xml gives doc.xml      *  xmldb:exist:///db/fo/bar/doc.xml gives doc.xml      *      * @param path  The Path      * @return  Document name.      */
specifier|static
specifier|public
name|String
name|getDocumentName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|docName
init|=
literal|null
decl_stmt|;
name|int
name|separatorPos
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separatorPos
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|docName
operator|=
name|path
expr_stmt|;
block|}
else|else
block|{
name|docName
operator|=
name|path
operator|.
name|substring
argument_list|(
name|separatorPos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|docName
return|;
block|}
comment|/**      *  Get collection pathname from path.      *      *  /db/foo/bar/doc.xml gives /db/foo/bar      *  xmldb:exist:///db/fo/bar/doc.xml gives xmldb:exist:///db/fo/bar      *      * @param path  The Path      * @return  Collection path name, "" if none available (doc.xml)      */
specifier|static
specifier|public
name|String
name|getCollectionPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|pathName
init|=
literal|null
decl_stmt|;
name|int
name|separatorPos
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separatorPos
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
comment|// no path
name|pathName
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|pathName
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|separatorPos
argument_list|)
expr_stmt|;
block|}
return|return
name|pathName
return|;
block|}
comment|//    /**
comment|//     * @deprecated Get rid of this code.
comment|//     */
comment|//    public boolean hasGrammar(int type, String id){
comment|//        return !getGrammarPath(type, id).equalsIgnoreCase("NONE");
comment|//    }
comment|//
comment|//    /**
comment|//     * @deprecated Get rid of this code.
comment|//     */
comment|//    public String getGrammarPath(int type, String id){
comment|//
comment|//        logger.info("Get path of '"+id+"'");
comment|//
comment|//        String result="EMPTY";
comment|//        String query = getGrammarQuery(type, id);
comment|//
comment|//        DBBroker broker = null;
comment|//        try {
comment|//            broker = brokerPool.get(SecurityManager.SYSTEM_USER);
comment|//        } catch (EXistException ex){
comment|//            logger.error("Error getting DBBroker", ex);
comment|//        }
comment|//
comment|//        XQuery xquery = broker.getXQueryService();
comment|//        try{
comment|//            Sequence seq = xquery.execute(query, null);
comment|//
comment|//            SequenceIterator i = seq.iterate();
comment|//            if(i.hasNext()){
comment|//                result= i.nextItem().getStringValue();
comment|//
comment|//            } else {
comment|//                logger.debug("No xQuery result");
comment|//            }
comment|//
comment|//        } catch (XPathException ex){
comment|//            logger.error("XSD xQuery error: "+ ex.getMessage());
comment|//        }
comment|//
comment|//        brokerPool.release(broker);
comment|//
comment|//        return result;
comment|//    }
comment|//    /**
comment|//     * @deprecated Get rid of this code.
comment|//     */
comment|//    public String getGrammarQuery(int type, String id){ // TODO double
comment|//        String query="NOQUERY";
comment|//        if(type==GRAMMAR_XSD){
comment|//            query = "let $top := collection('"+XSDBASE+"') " +
comment|//                    "let $schemas := $top/xs:schema[ @targetNamespace = \"" + id+ "\" ] "+
comment|//                    "return if($schemas) then document-uri($schemas[1]) else \""+NOGRAMMAR+"\" " ;
comment|//        } else if(type==GRAMMAR_DTD){
comment|//            query = "let $top := doc('"+DTDCATALOG+"') "+
comment|//                    "let $dtds := $top//public[@publicId = \""+id+"\"]/@uri " +
comment|//                    "return if($dtds) then $dtds[1] else \""+NOGRAMMAR+"\"" ;
comment|//        } else {
comment|//            logger.error("Unknown grammar type, not able to find query.");
comment|//        }
comment|//
comment|//        return query;
comment|//    }
comment|//    /**
comment|//     *  Get GRAMMAR resource specified by DB path
comment|//     *
comment|//     * @deprecated Get rid of this code.
comment|//     * @param path          Path in DB to resource.
comment|//     * @param isBinary      Flag is resource binary?
comment|//     * @return              Reader to the resource.
comment|//     */
comment|//    public byte[] getGrammar(int type, String path ){
comment|//
comment|//        byte[] data = null;
comment|//        boolean isBinary=false;
comment|//
comment|//        if(type==GRAMMAR_DTD){
comment|//            isBinary=true;
comment|//        }
comment|//
comment|//        logger.debug("Get resource '"+path + "' binary="+ isBinary);
comment|//
comment|//        DBBroker broker = null;
comment|//        try {
comment|//
comment|//            broker = brokerPool.get(SecurityManager.SYSTEM_USER);
comment|//
comment|//
comment|//            if(isBinary){
comment|//                BinaryDocument binDoc = (BinaryDocument) broker.openDocument(path, Lock.READ_LOCK);
comment|//                data = broker.getBinaryResourceData(binDoc);
comment|//                binDoc.getUpdateLock().release(Lock.READ_LOCK);
comment|//
comment|//            } else {
comment|//
comment|//                DocumentImpl doc = broker.openDocument(path, Lock.READ_LOCK);
comment|//                Serializer serializer = broker.getSerializer();
comment|//                serializer.reset();
comment|//                data = serializer.serialize(doc).getBytes();
comment|//                doc.getUpdateLock().release(Lock.READ_LOCK);
comment|//            }
comment|//        } catch (PermissionDeniedException ex){
comment|//            logger.error("Error opening document", ex);
comment|//        } catch (SAXException ex){
comment|//            logger.error("Error serializing document", ex);
comment|//        }  catch (EXistException ex){
comment|//            logger.error(ex);
comment|//        } finally {
comment|//            if(brokerPool!=null){
comment|//                brokerPool.release(broker);
comment|//            }
comment|//        }
comment|//
comment|//        return data;
comment|//    }
comment|/**      *  Get schema path information from catalog.      *      * @param collection Collection containing the catalog file      * @param docName    Catalog filename      * @param namespace  This namespace needs to be resolved      * @return           Path to schema, or null if not found.      */
specifier|public
name|String
name|getSchemaPathFromCatalog
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
name|docName
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
name|String
name|xquery
init|=
name|FINDXSDINCATALOG
operator|.
name|replaceAll
argument_list|(
literal|"CATALOGFILE"
argument_list|,
name|collection
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/"
operator|+
name|docName
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"NAMESPACE"
argument_list|,
name|namespace
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|executeQuerySingleResult
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|collection
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/"
operator|+
name|path
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/**      *  Get DTD path information from catalog.      *      * @param collection Collection containing the catalog file      * @param docName    Catalog filename      * @param publicId   This publicId needs to be resolved      * @return           Path to DTD, or null if not found.      */
specifier|public
name|String
name|getDtdPathFromCatalog
parameter_list|(
name|XmldbURI
name|collection
parameter_list|,
name|String
name|docName
parameter_list|,
name|String
name|publicId
parameter_list|)
block|{
name|String
name|xquery
init|=
name|FINDDTDINCATALOG
operator|.
name|replaceAll
argument_list|(
literal|"CATALOGFILE"
argument_list|,
name|collection
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/"
operator|+
name|docName
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"PUBLICID"
argument_list|,
name|publicId
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|executeQuerySingleResult
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|collection
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/"
operator|+
name|path
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
block|}
end_class

end_unit

