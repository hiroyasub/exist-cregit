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
name|java
operator|.
name|net
operator|.
name|URI
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * Implements the WebDAV COPY method.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Copy
extends|extends
name|AbstractWebDAVMethod
block|{
comment|/**      *      */
specifier|public
name|Copy
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
operator|==
literal|null
condition|)
block|{
name|XmldbURI
name|docUri
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
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No resource or collection found for path: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|NOT_FOUND_ERR
argument_list|)
expr_stmt|;
return|return;
block|}
name|resource
operator|=
name|collection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|docUri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No resource found for path: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|NOT_FOUND_ERR
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|//TODO : release collection lock here ?
name|String
name|destination
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Destination"
argument_list|)
decl_stmt|;
name|XmldbURI
name|destPath
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|uri
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|host
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getServerName
argument_list|()
argument_list|)
operator|&&
name|port
operator|==
name|request
operator|.
name|getServerPort
argument_list|()
operator|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_IMPLEMENTED
argument_list|,
literal|"Copying to a different server is not yet implemented"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//TODO: use XmldbURI for this stuff too
name|String
name|tempDestPath
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|tempDestPath
operator|.
name|startsWith
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
argument_list|)
condition|)
name|tempDestPath
operator|=
name|tempDestPath
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tempDestPath
operator|.
name|startsWith
argument_list|(
name|request
operator|.
name|getServletPath
argument_list|()
argument_list|)
condition|)
name|tempDestPath
operator|=
name|tempDestPath
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getServletPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|destPath
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|tempDestPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"Malformed URL in destination header"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
name|copyResource
argument_list|(
name|user
argument_list|,
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|collection
argument_list|,
name|resource
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
else|else
name|copyCollection
argument_list|(
name|user
argument_list|,
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|collection
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|SC_RESOURCE_IS_LOCKED
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
name|resource
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyResource
parameter_list|(
name|User
name|user
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|Collection
name|sourceCollection
parameter_list|,
name|DocumentImpl
name|resource
parameter_list|,
name|XmldbURI
name|destination
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|XmldbURI
name|newResourceName
init|=
name|destination
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
if|if
condition|(
name|newResourceName
operator|==
literal|null
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
literal|"Bad destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
return|return;
block|}
name|destination
operator|=
name|destination
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
name|boolean
name|replaced
init|=
literal|false
decl_stmt|;
name|Collection
name|destCollection
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
name|destCollection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destination
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destCollection
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CONFLICT
argument_list|,
literal|"Destination collection not found"
argument_list|)
expr_stmt|;
return|return;
block|}
name|DocumentImpl
name|oldDoc
init|=
name|destCollection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|newResourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldDoc
operator|!=
literal|null
condition|)
block|{
name|boolean
name|overwrite
init|=
name|overwrite
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|overwrite
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_PRECONDITION_FAILED
argument_list|,
literal|"Destination resource exists and overwrite is not allowed"
argument_list|)
expr_stmt|;
return|return;
block|}
name|replaced
operator|=
literal|true
expr_stmt|;
block|}
comment|//TODO : release collection lock here ?
name|broker
operator|.
name|copyResource
argument_list|(
name|transaction
argument_list|,
name|resource
argument_list|,
name|destCollection
argument_list|,
name|newResourceName
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
if|if
condition|(
name|replaced
condition|)
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NO_CONTENT
argument_list|)
expr_stmt|;
else|else
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CREATED
argument_list|)
expr_stmt|;
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
name|transaction
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|destCollection
operator|!=
literal|null
condition|)
name|destCollection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyCollection
parameter_list|(
name|User
name|user
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|XmldbURI
name|destination
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
throws|,
name|EXistException
block|{
name|XmldbURI
name|newCollectionName
init|=
name|destination
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
if|if
condition|(
name|newCollectionName
operator|==
literal|null
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
literal|"Bad destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
return|return;
block|}
name|destination
operator|=
name|destination
operator|.
name|lastSegment
argument_list|()
expr_stmt|;
name|boolean
name|replaced
init|=
literal|false
decl_stmt|;
name|Collection
name|destCollection
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
name|destCollection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|destination
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|destCollection
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CONFLICT
argument_list|,
literal|"Destination collection not found"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|destCollection
operator|.
name|hasChildCollection
argument_list|(
name|newCollectionName
argument_list|)
condition|)
block|{
name|boolean
name|overwrite
init|=
name|overwrite
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|overwrite
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_PRECONDITION_FAILED
argument_list|,
literal|"Destination collection exists and overwrite is not allowed"
argument_list|)
expr_stmt|;
return|return;
block|}
name|replaced
operator|=
literal|true
expr_stmt|;
block|}
comment|//TODO : release collection lock here ?
name|broker
operator|.
name|copyCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|,
name|destCollection
argument_list|,
name|newCollectionName
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
if|if
condition|(
name|replaced
condition|)
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NO_CONTENT
argument_list|)
expr_stmt|;
else|else
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CREATED
argument_list|)
expr_stmt|;
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
name|transaction
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|destCollection
operator|!=
literal|null
condition|)
name|destCollection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|overwrite
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|header
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Overwrite"
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|header
operator|.
name|equals
argument_list|(
literal|"T"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

