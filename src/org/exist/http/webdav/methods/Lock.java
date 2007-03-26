begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
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
name|ServletOutputStream
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
name|DocumentBuilder
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
name|LockToken
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
name|WebDAVUtil
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

begin_comment
comment|/**  * Implements the WebDAV LOCK method.  *  * @author Dannes Wessels  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Lock
extends|extends
name|AbstractWebDAVMethod
block|{
specifier|private
name|DocumentBuilderFactory
name|docFactory
decl_stmt|;
specifier|public
name|Lock
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
name|docFactory
operator|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|docFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|LockToken
name|getDefaultToken
parameter_list|(
name|User
name|user
parameter_list|)
block|{
comment|// Fill in default information
name|LockToken
name|lockToken
init|=
operator|new
name|LockToken
argument_list|()
decl_stmt|;
name|lockToken
operator|.
name|setOwner
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|lockToken
operator|.
name|setType
argument_list|(
name|LockToken
operator|.
name|LOCK_TYPE_WRITE
argument_list|)
expr_stmt|;
name|lockToken
operator|.
name|setTimeOut
argument_list|(
name|LockToken
operator|.
name|LOCK_TIMEOUT_INFINITE
argument_list|)
expr_stmt|;
name|lockToken
operator|.
name|setScope
argument_list|(
name|LockToken
operator|.
name|LOCK_SCOPE_EXCLUSIVE
argument_list|)
expr_stmt|;
name|lockToken
operator|.
name|setDepth
argument_list|(
name|LockToken
operator|.
name|LOCK_DEPTH_0
argument_list|)
expr_stmt|;
return|return
name|lockToken
return|;
block|}
specifier|private
name|void
name|createNullResource
parameter_list|(
name|User
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Create NullResource for '"
operator|+
name|path
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|txManager
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
name|DocumentImpl
name|resource
init|=
literal|null
decl_stmt|;
name|LockToken
name|lockToken
init|=
name|getDefaultToken
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|lockToken
operator|.
name|createOpaqueLockToken
argument_list|()
expr_stmt|;
name|String
name|contentType
init|=
name|request
operator|.
name|getContentType
argument_list|()
decl_stmt|;
name|txManager
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|txn
operator|=
name|txManager
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|XmldbURI
name|collName
init|=
name|path
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|XmldbURI
name|docName
init|=
name|path
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|path
argument_list|)
decl_stmt|;
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
literal|"storing document '"
operator|+
name|path
operator|+
literal|"' in collection '"
operator|+
name|collName
argument_list|)
expr_stmt|;
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
name|collName
argument_list|,
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
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing NULL xml resource"
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|,
literal|"<nullresource/>"
argument_list|)
decl_stmt|;
comment|//TODO : unlock the collection here ?
name|resource
operator|=
name|info
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|info
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
literal|"<nullresource/>"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing NULL byte binary resource."
argument_list|)
expr_stmt|;
name|resource
operator|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|contentType
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|collection
operator|.
name|release
argument_list|(
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
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
name|resource
operator|.
name|setUserLock
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|lockToken
operator|.
name|setResourceType
argument_list|(
name|LockToken
operator|.
name|RESOURCE_TYPE_NULL_RESOURCE
argument_list|)
expr_stmt|;
name|resource
operator|.
name|getMetadata
argument_list|()
operator|.
name|setLockToken
argument_list|(
name|lockToken
argument_list|)
expr_stmt|;
name|txManager
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
try|try
block|{
name|lockResource
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|lockToken
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"NullResourceLock done."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|txManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
try|try
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|txManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
try|try
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
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
block|}
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.http.webdav.WebDAVMethod#process(org.exist.security.User, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.exist.collections.Collection, org.exist.dom.DocumentImpl)      */
specifier|public
name|void
name|process
parameter_list|(
name|User
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
name|DocumentImpl
name|resource
init|=
literal|null
decl_stmt|;
name|boolean
name|isNullResource
init|=
literal|false
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
try|try
block|{
name|resource
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|path
argument_list|,
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
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
comment|// No document found, maybe a collection
name|LOG
operator|.
name|info
argument_list|(
literal|"resource==null, document not found."
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
name|LOG
operator|.
name|info
argument_list|(
literal|"collection==null, path does not point to collection"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Create document as NullResource"
argument_list|)
expr_stmt|;
name|createNullResource
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
name|isNullResource
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|String
name|txt
init|=
literal|"Locking on collections not supported yet."
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|txt
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_IMPLEMENTED
argument_list|,
name|txt
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Aquire lock for existing document"
argument_list|)
expr_stmt|;
comment|// TODO get information from webDAV client XML
name|LockToken
name|lockToken
init|=
name|getDefaultToken
argument_list|(
name|user
argument_list|)
decl_stmt|;
comment|//                LockToken lockToken = getLockParameters(request, response);
comment|//                if(lockToken==null){
comment|//                    // Error has been handled,skip test
comment|//                    LOG.debug("No Locktoken. Stopped Lock request");
comment|//                    pool.release(broker);
comment|//                    return;
comment|//                }
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received lock request ["
operator|+
name|lockToken
operator|.
name|getScope
argument_list|()
operator|+
literal|"] "
operator|+
literal|"for owner "
operator|+
name|lockToken
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get current userlock
name|User
name|lock
init|=
name|resource
operator|.
name|getUserLock
argument_list|()
decl_stmt|;
comment|// Check if Resource is already locked.
if|if
condition|(
name|lock
operator|!=
literal|null
operator|&&
operator|!
name|lock
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resource is locked."
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|SC_RESOURCE_IS_LOCKED
argument_list|,
literal|"Resource is locked by user "
operator|+
name|user
operator|.
name|getName
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Check for request fo shared lock.
if|if
condition|(
name|lockToken
operator|.
name|getScope
argument_list|()
operator|==
name|LockToken
operator|.
name|LOCK_SCOPE_SHARED
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Shared locks are not implemented."
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_IMPLEMENTED
argument_list|,
literal|"Shared locks are not implemented."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Fill new Locktoken with new UUID
name|lockToken
operator|.
name|createOpaqueLockToken
argument_list|()
expr_stmt|;
name|resource
operator|.
name|getMetadata
argument_list|()
operator|.
name|setLockToken
argument_list|(
name|lockToken
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setUserLock
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// Make token persistant
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
name|broker
operator|.
name|storeXMLResource
argument_list|(
name|transaction
argument_list|,
name|resource
argument_list|)
expr_stmt|;
comment|//TOUNDERSTAND : this lock is released below (in the finally clause)
comment|//isn't it rather a user lock release attempt ?
comment|// ?
name|resource
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
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
name|READ_LOCK
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
name|debug
argument_list|(
literal|"Sucessfully locked '"
operator|+
name|path
operator|+
literal|"'."
argument_list|)
expr_stmt|;
comment|// Write XML response to client
name|lockResource
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|lockToken
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|isNullResource
condition|)
block|{
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|resource
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
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
name|READ_LOCK
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
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pool
operator|!=
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
block|}
block|}
block|}
comment|/**      *  Get LOCK information from HttpRequest      *      * @param request    Http Object      * @param response   Http Object      * @throws ServletException      * @throws IOException      * @return NULL if error is send to response object, or locktoken with      *         details about scope, depth and owner      */
specifier|private
name|LockToken
name|getLockParameters
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
name|LockToken
name|token
init|=
operator|new
name|LockToken
argument_list|()
decl_stmt|;
comment|// Parse XML document
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
name|DocumentBuilder
name|docBuilder
decl_stmt|;
try|try
block|{
name|docBuilder
operator|=
name|docFactory
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e1
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
name|WebDAVUtil
operator|.
name|XML_CONFIGURATION_ERR
argument_list|,
name|e1
argument_list|)
throw|;
block|}
comment|// lockinfo
name|Document
name|doc
init|=
name|WebDAVUtil
operator|.
name|parseRequestContent
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|docBuilder
argument_list|)
decl_stmt|;
name|Element
name|lockinfo
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|lockinfo
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"lockinfo"
argument_list|)
operator|&&
name|lockinfo
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|WebDAV
operator|.
name|DAV_NS
argument_list|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|WebDAVUtil
operator|.
name|UNEXPECTED_ELEMENT_ERR
operator|+
name|lockinfo
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|WebDAVUtil
operator|.
name|UNEXPECTED_ELEMENT_ERR
operator|+
name|lockinfo
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Node
name|node
init|=
name|lockinfo
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|WebDAV
operator|.
name|DAV_NS
argument_list|)
condition|)
block|{
comment|// lockinfo.lockscope
if|if
condition|(
literal|"lockscope"
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|Node
name|scopeNode
init|=
name|WebDAVUtil
operator|.
name|firstElementNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"exclusive"
operator|.
name|equals
argument_list|(
name|scopeNode
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
name|token
operator|.
name|setScope
argument_list|(
name|LockToken
operator|.
name|LOCK_SCOPE_EXCLUSIVE
argument_list|)
expr_stmt|;
if|else if
condition|(
literal|"shared"
operator|.
name|equals
argument_list|(
name|scopeNode
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
name|token
operator|.
name|setScope
argument_list|(
name|LockToken
operator|.
name|LOCK_SCOPE_SHARED
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
comment|// lockinfo.locktype
if|if
condition|(
literal|"locktype"
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|Node
name|typeNode
init|=
name|WebDAVUtil
operator|.
name|firstElementNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
literal|"write"
operator|.
name|equals
argument_list|(
name|typeNode
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|WebDAVUtil
operator|.
name|UNEXPECTED_ELEMENT_ERR
operator|+
name|typeNode
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|token
operator|.
name|setType
argument_list|(
name|LockToken
operator|.
name|LOCK_TYPE_WRITE
argument_list|)
expr_stmt|;
block|}
comment|// lockinfo.owner
if|if
condition|(
literal|"owner"
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|Node
name|href
init|=
name|WebDAVUtil
operator|.
name|firstElementNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|String
name|owner
init|=
name|WebDAVUtil
operator|.
name|getElementContent
argument_list|(
name|href
argument_list|)
decl_stmt|;
name|token
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
comment|// Return Lock Info
specifier|private
name|void
name|lockResource
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|LockToken
name|lockToken
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|XML_CONTENT_TYPE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|//        response.setCharacterEncoding("utf-8");
name|ServletOutputStream
name|sos
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"<D:prop xmlns:D=\"DAV:\">"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"<D:lockdiscovery>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"<D:activelock>"
argument_list|)
expr_stmt|;
comment|// Lock Type
name|sos
operator|.
name|println
argument_list|(
literal|"<D:locktype>"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|lockToken
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|LockToken
operator|.
name|LOCK_TYPE_WRITE
case|:
name|sos
operator|.
name|println
argument_list|(
literal|"<D:write/>"
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// This should never be reached
name|sos
operator|.
name|println
argument_list|(
literal|"<D:write/>"
argument_list|)
expr_stmt|;
break|break;
block|}
name|sos
operator|.
name|println
argument_list|(
literal|"</D:locktype>"
argument_list|)
expr_stmt|;
comment|// Lockscope
name|sos
operator|.
name|println
argument_list|(
literal|"<D:lockscope>"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|lockToken
operator|.
name|getScope
argument_list|()
condition|)
block|{
case|case
name|LockToken
operator|.
name|LOCK_SCOPE_EXCLUSIVE
case|:
name|sos
operator|.
name|println
argument_list|(
literal|"<D:exclusive/>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LockToken
operator|.
name|LOCK_SCOPE_SHARED
case|:
name|sos
operator|.
name|println
argument_list|(
literal|"<D:shared/>"
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// This should never be reached
name|sos
operator|.
name|println
argument_list|(
literal|"<D:exclusive/>"
argument_list|)
expr_stmt|;
break|break;
block|}
name|sos
operator|.
name|println
argument_list|(
literal|"</D:lockscope>"
argument_list|)
expr_stmt|;
comment|// Depth
switch|switch
condition|(
name|lockToken
operator|.
name|getDepth
argument_list|()
condition|)
block|{
case|case
name|LockToken
operator|.
name|LOCK_DEPTH_INFINIY
case|:
name|sos
operator|.
name|println
argument_list|(
literal|"<D:depth>Infinity</D:depth>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LockToken
operator|.
name|LOCK_DEPTH_0
case|:
name|sos
operator|.
name|println
argument_list|(
literal|"<D:depth>0</D:depth>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LockToken
operator|.
name|LOCK_DEPTH_1
case|:
name|sos
operator|.
name|println
argument_list|(
literal|"<D:depth>1</D:depth>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LockToken
operator|.
name|LOCK_DEPTH_NOT_SET
case|:
comment|// This should never be reached
name|sos
operator|.
name|println
argument_list|(
literal|"<D:depth>not set</D:depth>"
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// This should never be reached
name|sos
operator|.
name|println
argument_list|(
literal|"<D:depth>null</D:depth>"
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Owner
name|sos
operator|.
name|println
argument_list|(
literal|"<D:owner>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"<D:href>"
operator|+
name|lockToken
operator|.
name|getOwner
argument_list|()
operator|+
literal|"</D:href>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"</D:owner>"
argument_list|)
expr_stmt|;
comment|// Timeout
if|if
condition|(
name|lockToken
operator|.
name|getTimeOut
argument_list|()
operator|==
name|LockToken
operator|.
name|LOCK_TIMEOUT_INFINITE
condition|)
block|{
name|sos
operator|.
name|println
argument_list|(
literal|"<D:timeout>Infinite</D:timeout>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sos
operator|.
name|println
argument_list|(
literal|"<D:timeout>Second-"
operator|+
name|lockToken
operator|.
name|getTimeOut
argument_list|()
operator|+
literal|"</D:timeout>"
argument_list|)
expr_stmt|;
block|}
comment|// Lock token
name|sos
operator|.
name|println
argument_list|(
literal|"<D:locktoken>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"<D:href>opaquelocktoken:"
operator|+
name|lockToken
operator|.
name|getOpaqueLockToken
argument_list|()
operator|+
literal|"</D:href>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"</D:locktoken>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"</D:activelock>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"</D:lockdiscovery>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|println
argument_list|(
literal|"</D:prop>"
argument_list|)
expr_stmt|;
name|sos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

