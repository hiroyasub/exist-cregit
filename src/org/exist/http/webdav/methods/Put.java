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
name|ServletInputStream
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
name|LockToken
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
comment|/**  * Implements the WebDAV PUT method.  *  * @author wolf  * @author dizzz  */
end_comment

begin_class
specifier|public
class|class
name|Put
extends|extends
name|AbstractWebDAVMethod
block|{
specifier|public
name|Put
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
comment|/* (non-Javadoc)          * @see org.exist.http.webdav.WebDAVMethod#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.exist.collections.Collection, org.exist.dom.DocumentImpl)          */
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"PUT start"
argument_list|)
expr_stmt|;
name|File
name|tempFile
init|=
name|saveRequestContent
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|tempFile
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
decl_stmt|;
name|String
name|contentType
init|=
name|request
operator|.
name|getContentType
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
name|collectionLocked
init|=
literal|true
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
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
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
name|pathUri
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
literal|"collUri='"
operator|+
name|collUri
operator|+
literal|"';  path="
operator|+
name|pathUri
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
comment|// TODO check why not use WRITE_LOCK here?
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_CONFLICT
argument_list|,
literal|"Parent collection "
operator|+
name|collUri
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|pathUri
argument_list|)
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
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
literal|"Cannot overwrite an existing collection with a resource"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//            MimeType mime = MimeTable.getInstance().getContentTypeFor(path);
comment|//            if (mime == null){
comment|//                mime = MimeType.BINARY_TYPE;
comment|//            }
comment|// TODO why is content type involved here? would not like to use it,
comment|// as it seems to block binary file upload by MS office.
name|MimeType
name|mime
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
name|pathUri
argument_list|)
expr_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
name|contentType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
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
name|mime
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing document "
operator|+
name|pathUri
operator|+
literal|"; content-type='"
operator|+
name|contentType
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
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
literal|"storing XML resource"
argument_list|)
expr_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|url
argument_list|)
decl_stmt|;
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
name|pathUri
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|doc
operator|=
name|info
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|doc
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
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|collectionLocked
operator|=
literal|false
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
name|is
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing Binary resource"
argument_list|)
expr_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
name|doc
operator|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|pathUri
argument_list|,
name|is
argument_list|,
name|contentType
argument_list|,
operator|(
name|int
operator|)
name|tempFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
comment|// Remove Null Resource flag
name|LockToken
name|token
init|=
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLockToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|setResourceType
argument_list|(
name|LockToken
operator|.
name|RESOURCE_TYPE_NOT_SPECIFIED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"token==null"
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"PUT ready"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
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
return|return;
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
name|LOG
operator|.
name|debug
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
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|TriggerException
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
name|LOG
operator|.
name|debug
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
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|SAXException
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
name|LOG
operator|.
name|debug
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
name|SC_BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
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
name|txn
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
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
name|SC_CONFLICT
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
if|if
condition|(
name|collectionLocked
operator|&&
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
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|tempFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|File
name|saveRequestContent
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ServletInputStream
name|is
init|=
name|request
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|request
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
comment|// put may send a lot of data, so save it
comment|// to a temporary file first.
name|File
name|tempFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"existSRC"
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|!=
literal|0
condition|)
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|count
decl_stmt|,
name|l
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
name|os
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|l
operator|+=
name|count
expr_stmt|;
block|}
do|while
condition|(
name|l
operator|<
name|len
condition|)
do|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|tempFile
return|;
block|}
block|}
end_class

end_unit

