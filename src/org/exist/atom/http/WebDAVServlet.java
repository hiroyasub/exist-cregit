begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id: WebDAVServlet.java 2782 2006-02-25 18:55:49Z dizzzz $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|http
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
name|Enumeration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|DocumentBuilderFactory
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
name|atom
operator|.
name|Atom
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|modules
operator|.
name|AtomProtocol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
operator|.
name|DOM
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
operator|.
name|DOMDB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
operator|.
name|DateFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
operator|.
name|NodeHandler
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
name|dom
operator|.
name|ElementImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|WebDAV
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|WebDAVMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|WebDAVMethodFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
operator|.
name|Copy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
operator|.
name|Delete
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
operator|.
name|Mkcol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
operator|.
name|Move
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
operator|.
name|Put
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
name|UUIDGenerator
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
name|UserImpl
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
name|TransactionException
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
name|MimeTable
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
name|MimeType
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
name|value
operator|.
name|StringValue
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
name|Element
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
comment|/**  * Provides a WebDAV interface that also maintains the atom feed if it exists  * in the directory.  *  * @author wolf  * @author Alex Milowski  */
end_comment

begin_class
specifier|public
class|class
name|WebDAVServlet
extends|extends
name|HttpServlet
block|{
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
name|WebDAVServlet
operator|.
name|class
argument_list|)
decl_stmt|;
class|class
name|FindEntryByResource
implements|implements
name|NodeHandler
block|{
name|String
name|path
decl_stmt|;
name|Element
name|matching
decl_stmt|;
name|FindEntryByResource
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|matching
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|Node
name|parent
parameter_list|,
name|Node
name|child
parameter_list|)
block|{
name|Element
name|entry
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
name|NodeList
name|nl
init|=
name|entry
operator|.
name|getElementsByTagNameNS
argument_list|(
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"src"
argument_list|)
argument_list|)
condition|)
block|{
name|matching
operator|=
name|entry
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Element
name|getEntry
parameter_list|()
block|{
return|return
name|matching
return|;
block|}
block|}
class|class
name|AtomWebDAVMethodFactory
extends|extends
name|WebDAVMethodFactory
block|{
specifier|public
name|WebDAVMethod
name|create
parameter_list|(
name|String
name|method
parameter_list|,
name|BrokerPool
name|pool
parameter_list|)
block|{
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"PUT"
argument_list|)
condition|)
block|{
return|return
operator|new
name|AtomPut
argument_list|(
name|pool
argument_list|)
return|;
block|}
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"DELETE"
argument_list|)
condition|)
block|{
return|return
operator|new
name|AtomDelete
argument_list|(
name|pool
argument_list|)
return|;
block|}
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"MKCOL"
argument_list|)
condition|)
block|{
return|return
operator|new
name|AtomMkcol
argument_list|(
name|pool
argument_list|)
return|;
block|}
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"MOVE"
argument_list|)
condition|)
block|{
return|return
operator|new
name|AtomMove
argument_list|(
name|pool
argument_list|)
return|;
block|}
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"COPY"
argument_list|)
condition|)
block|{
return|return
operator|new
name|AtomCopy
argument_list|(
name|pool
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|create
argument_list|(
name|method
argument_list|,
name|pool
argument_list|)
return|;
block|}
block|}
block|}
class|class
name|AtomPut
extends|extends
name|Put
block|{
name|AtomPut
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|UserImpl
name|user
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|XmldbURI
name|path
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|XmldbURI
name|filename
init|=
name|path
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
name|XmldbURI
name|collUri
init|=
name|path
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|boolean
name|updateToExisting
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|updateToExisting
operator|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|filename
argument_list|)
operator|!=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Exception while getting a broker from the pool."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|super
operator|.
name|process
argument_list|(
name|user
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateToExisting
condition|)
block|{
comment|// We do nothing right now
name|LOG
operator|.
name|debug
argument_list|(
literal|"Update to existing resource, skipping feed update."
argument_list|)
expr_stmt|;
return|return;
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
block|{
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
block|}
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
name|DocumentImpl
name|feedDoc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Atom PUT collUri='"
operator|+
name|collUri
operator|+
literal|"';  path="
operator|+
name|filename
operator|+
literal|"';"
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
operator|||
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|filename
argument_list|)
condition|)
block|{
comment|// We're already in an error state from the WebDAV action so just return
name|LOG
operator|.
name|debug
argument_list|(
literal|"No collection or subcollection already exists."
argument_list|)
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return;
block|}
name|MimeType
name|mime
decl_stmt|;
name|String
name|contentType
init|=
name|request
operator|.
name|getContentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
name|mime
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|filename
argument_list|)
expr_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
name|contentType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|p
init|=
name|contentType
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
block|{
name|contentType
operator|=
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|contentType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mime
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mime
operator|==
literal|null
condition|)
block|{
name|mime
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Acquiring lock on feed document..."
argument_list|)
expr_stmt|;
name|feedDoc
operator|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_URI
argument_list|)
expr_stmt|;
name|feedDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|String
name|title
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Title"
argument_list|)
decl_stmt|;
if|if
condition|(
name|title
operator|==
literal|null
condition|)
block|{
name|title
operator|=
name|filename
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|created
init|=
name|DateFormatter
operator|.
name|toXSDDateTime
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|ElementImpl
name|feedRoot
init|=
operator|(
name|ElementImpl
operator|)
name|feedDoc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|DOMDB
operator|.
name|replaceTextElement
argument_list|(
name|transaction
argument_list|,
name|feedRoot
argument_list|,
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"updated"
argument_list|,
name|created
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|id
init|=
literal|"urn:uuid:"
operator|+
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|Element
name|mediaEntry
init|=
name|AtomProtocol
operator|.
name|generateMediaEntry
argument_list|(
name|id
argument_list|,
name|created
argument_list|,
name|title
argument_list|,
name|filename
operator|.
name|toString
argument_list|()
argument_list|,
name|mime
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|DOMDB
operator|.
name|appendChild
argument_list|(
name|transaction
argument_list|,
name|feedRoot
argument_list|,
name|mediaEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeXMLResource
argument_list|(
name|transaction
argument_list|,
name|feedDoc
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|ex
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
name|ServletException
argument_list|(
literal|"Cannot commit transaction."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|ex
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
name|ServletException
argument_list|(
literal|"DOM implementation is misconfigured."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|ex
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
name|ServletException
argument_list|(
literal|"Cannot acquire write lock."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|feedDoc
operator|!=
literal|null
condition|)
block|{
name|feedDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
class|class
name|AtomDelete
extends|extends
name|Delete
block|{
name|AtomDelete
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|UserImpl
name|user
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|XmldbURI
name|path
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|super
operator|.
name|process
argument_list|(
name|user
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
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
name|DocumentImpl
name|feedDoc
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|XmldbURI
name|filename
init|=
name|path
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
name|XmldbURI
name|collUri
init|=
name|path
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Atom DELETE collUri='"
operator|+
name|collUri
operator|+
literal|"';  path="
operator|+
name|filename
operator|+
literal|"';"
argument_list|)
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collUri
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
operator|||
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|filename
argument_list|)
condition|)
block|{
comment|// We're already in an error state from the WebDAV action so just return
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
return|return;
block|}
name|feedDoc
operator|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_URI
argument_list|)
expr_stmt|;
name|feedDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
comment|// Find the entry
name|FindEntryByResource
name|finder
init|=
operator|new
name|FindEntryByResource
argument_list|(
name|filename
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|DOM
operator|.
name|findChildren
argument_list|(
name|feedDoc
operator|.
name|getDocumentElement
argument_list|()
argument_list|,
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"entry"
argument_list|,
name|finder
argument_list|)
expr_stmt|;
name|Element
name|entry
init|=
name|finder
operator|.
name|getEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
comment|// Remove the entry
name|ElementImpl
name|feedRoot
init|=
operator|(
name|ElementImpl
operator|)
name|feedDoc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|feedRoot
operator|.
name|removeChild
argument_list|(
name|transaction
argument_list|,
name|entry
argument_list|)
expr_stmt|;
comment|// Update the feed time
name|String
name|currentDateTime
init|=
name|DateFormatter
operator|.
name|toXSDDateTime
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|DOMDB
operator|.
name|replaceTextElement
argument_list|(
name|transaction
argument_list|,
name|feedRoot
argument_list|,
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"updated"
argument_list|,
name|currentDateTime
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Store the change on the feed
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing change..."
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeXMLResource
argument_list|(
name|transaction
argument_list|,
name|feedDoc
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the entry is missing, so ignore
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|TransactionException
name|ex
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
name|ServletException
argument_list|(
literal|"Cannot commit transaction."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
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
name|ServletException
argument_list|(
literal|"Exception while getting a broker from the pool."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|ex
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
name|ServletException
argument_list|(
literal|"Cannot acquire write lock."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|feedDoc
operator|!=
literal|null
condition|)
block|{
name|feedDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
class|class
name|AtomMkcol
extends|extends
name|Mkcol
block|{
name|AtomMkcol
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|UserImpl
name|user
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|XmldbURI
name|path
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|collection
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
name|collection
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_METHOD_NOT_ALLOWED
argument_list|,
literal|"collection "
operator|+
name|request
operator|.
name|getPathInfo
argument_list|()
operator|+
literal|" already exists"
argument_list|)
expr_stmt|;
return|return;
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
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Exception while getting a broker from the pool."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|super
operator|.
name|process
argument_list|(
name|user
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|collection
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
name|collection
operator|==
literal|null
condition|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
return|return;
block|}
name|DocumentImpl
name|feedDoc
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
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
name|String
name|id
init|=
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|String
name|currentDateTime
init|=
name|DateFormatter
operator|.
name|toXSDDateTime
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentBuilderFactory
name|docFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|docFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|docFactory
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|getDOMImplementation
argument_list|()
operator|.
name|createDocument
argument_list|(
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"feed"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|DOM
operator|.
name|replaceTextElement
argument_list|(
name|root
argument_list|,
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"id"
argument_list|,
literal|"urn:uuid:"
operator|+
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|replaceTextElement
argument_list|(
name|root
argument_list|,
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"updated"
argument_list|,
name|currentDateTime
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|replaceTextElement
argument_list|(
name|root
argument_list|,
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"title"
argument_list|,
name|path
operator|.
name|lastSegment
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Element
name|editLink
init|=
name|doc
operator|.
name|createElementNS
argument_list|(
name|Atom
operator|.
name|NAMESPACE_STRING
argument_list|,
literal|"link"
argument_list|)
decl_stmt|;
name|editLink
operator|.
name|setAttribute
argument_list|(
literal|"rel"
argument_list|,
literal|"edit"
argument_list|)
expr_stmt|;
name|editLink
operator|.
name|setAttribute
argument_list|(
literal|"type"
argument_list|,
name|Atom
operator|.
name|MIME_TYPE
argument_list|)
expr_stmt|;
name|editLink
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
name|root
operator|.
name|appendChild
argument_list|(
name|editLink
argument_list|)
expr_stmt|;
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
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_URI
argument_list|,
name|doc
argument_list|)
decl_stmt|;
comment|//TODO : we should probably unlock the collection here
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
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|ex
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
name|ServletException
argument_list|(
literal|"SAX error: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|ex
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
name|ServletException
argument_list|(
literal|"Trigger failed: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|ex
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
name|ServletException
argument_list|(
literal|"SAX error: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|ex
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
name|ServletException
argument_list|(
literal|"Cannot acquire write lock."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
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
name|ServletException
argument_list|(
literal|"Permission denied."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
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
name|ServletException
argument_list|(
literal|"Database exception"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
class|class
name|AtomMove
extends|extends
name|Move
block|{
name|AtomMove
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|AtomCopy
extends|extends
name|Copy
block|{
name|AtomCopy
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|WebDAV
name|webdav
decl_stmt|;
comment|/** id of the database registred against the BrokerPool */
specifier|protected
name|String
name|databaseid
init|=
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
decl_stmt|;
comment|/* (non-Javadoc)          * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)          */
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|//<frederic.glorieux@ajlsm.com> to allow multi-instance webdav server,
comment|// use a databaseid everywhere
name|String
name|id
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"database-id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
name|this
operator|.
name|databaseid
operator|=
name|id
expr_stmt|;
name|int
name|authMethod
init|=
name|WebDAV
operator|.
name|DIGEST_AUTH
decl_stmt|;
name|String
name|param
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"authentication"
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|!=
literal|null
operator|&&
literal|"basic"
operator|.
name|equalsIgnoreCase
argument_list|(
name|param
argument_list|)
condition|)
name|authMethod
operator|=
name|WebDAV
operator|.
name|BASIC_AUTH
expr_stmt|;
name|webdav
operator|=
operator|new
name|WebDAV
argument_list|(
name|authMethod
argument_list|,
name|this
operator|.
name|databaseid
argument_list|,
operator|new
name|AtomWebDAVMethodFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)          */
specifier|protected
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|dumpHeaders
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|webdav
operator|.
name|process
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|dumpHeaders
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
operator|+
literal|" "
operator|+
name|request
operator|.
name|getPathInfo
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Enumeration
name|e
init|=
name|request
operator|.
name|getHeaderNames
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|header
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|header
operator|+
literal|" = "
operator|+
name|request
operator|.
name|getHeader
argument_list|(
name|header
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

