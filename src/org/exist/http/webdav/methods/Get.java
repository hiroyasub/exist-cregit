begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|security
operator|.
name|Permission
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
name|source
operator|.
name|ClassLoaderSource
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
name|Lock
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
name|CompiledXQuery
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
name|XQuery
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
name|XQueryContext
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
name|NodeValue
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Implements the WebDAV GET method.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Get
extends|extends
name|AbstractWebDAVMethod
block|{
specifier|private
specifier|final
specifier|static
name|String
name|SERIALIZE_ERROR
init|=
literal|"Error while serializing document: "
decl_stmt|;
comment|// We use an XQuery to list collection contents
specifier|private
name|CompiledXQuery
name|compiled
init|=
literal|null
decl_stmt|;
specifier|public
name|Get
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
name|User
name|user
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|String
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
name|byte
index|[]
name|contentData
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|resource
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
argument_list|()
expr_stmt|;
name|resource
operator|=
name|broker
operator|.
name|openDocument
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
name|resource
operator|==
literal|null
condition|)
block|{
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
name|collectionListing
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|resource
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|READ_PERMISSION_DENIED
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|contentType
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
name|contentType
operator|=
name|WebDAV
operator|.
name|XML_CONTENT
expr_stmt|;
else|else
name|contentType
operator|=
name|WebDAV
operator|.
name|BINARY_CONTENT
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|response
operator|.
name|addDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|resource
operator|.
name|getLastModified
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
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
try|try
block|{
name|serializer
operator|.
name|setProperties
argument_list|(
name|WebDAV
operator|.
name|OUTPUT_PROPERTIES
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|contentData
operator|=
name|content
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|SERIALIZE_ERROR
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
else|else
name|contentData
operator|=
name|broker
operator|.
name|getBinaryResourceData
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|resource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|SERIALIZE_ERROR
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
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|READ_PERMISSION_DENIED
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
name|collection
operator|.
name|release
argument_list|()
expr_stmt|;
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contentData
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
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
name|response
operator|.
name|setContentLength
argument_list|(
name|contentData
operator|.
name|length
argument_list|)
expr_stmt|;
name|ServletOutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|contentData
argument_list|)
expr_stmt|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * Display a listing of the collection contents.      *        * @param collection      * @param response      * @throws IOException       */
specifier|private
name|void
name|collectionListing
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
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
comment|// the collection listing is generated by an XQuery
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
try|try
block|{
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|()
expr_stmt|;
else|else
block|{
name|compiled
operator|.
name|reset
argument_list|()
expr_stmt|;
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|declareVariable
argument_list|(
literal|"collection"
argument_list|,
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"uri"
argument_list|,
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|ClassLoaderSource
argument_list|(
literal|"org/exist/http/webdav/methods/collection.xq"
argument_list|)
argument_list|)
expr_stmt|;
name|Sequence
name|result
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
name|serializer
operator|.
name|setProperties
argument_list|(
name|WebDAV
operator|.
name|OUTPUT_PROPERTIES
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|contentData
init|=
name|content
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/html; charset=UTF-8"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentLength
argument_list|(
name|contentData
operator|.
name|length
argument_list|)
expr_stmt|;
name|response
operator|.
name|addDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|collection
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
name|ServletOutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|contentData
argument_list|)
expr_stmt|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to compile xquery"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to serialize query results"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

