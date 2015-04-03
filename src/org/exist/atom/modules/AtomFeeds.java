begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2006-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|modules
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
name|OutputStream
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|IncomingMessage
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
name|OutgoingMessage
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
name|persistent
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
name|persistent
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
name|BadRequestException
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
name|NotFoundException
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
name|source
operator|.
name|URLSource
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
name|XQueryPool
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
name|util
operator|.
name|serializer
operator|.
name|SAXSerializer
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
name|serializer
operator|.
name|SerializerPool
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
comment|/**  *   * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|AtomFeeds
extends|extends
name|AtomModuleBase
implements|implements
name|Atom
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AtomProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|FEED_DOCUMENT_NAME
init|=
literal|".feed.atom"
decl_stmt|;
specifier|static
specifier|final
name|XmldbURI
name|FEED_DOCUMENT_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|FEED_DOCUMENT_NAME
argument_list|)
decl_stmt|;
name|URLSource
name|entryByIdSource
decl_stmt|;
name|URLSource
name|getFeedSource
decl_stmt|;
comment|/** Creates a new instance of AtomProtocol */
specifier|public
name|AtomFeeds
parameter_list|()
block|{
name|entryByIdSource
operator|=
operator|new
name|URLSource
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"entry-by-id.xq"
argument_list|)
argument_list|)
expr_stmt|;
name|getFeedSource
operator|=
operator|new
name|URLSource
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"get-feed.xq"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doGet
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
name|handleGet
argument_list|(
literal|true
argument_list|,
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doHead
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
name|handleGet
argument_list|(
literal|false
argument_list|,
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|handleGet
parameter_list|(
name|boolean
name|returnContent
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|IncomingMessage
name|request
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|PermissionDeniedException
throws|,
name|NotFoundException
throws|,
name|EXistException
block|{
name|DocumentImpl
name|resource
init|=
literal|null
decl_stmt|;
specifier|final
name|XmldbURI
name|pathUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|request
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|resource
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|pathUri
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
name|String
name|id
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|id
operator|=
name|id
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|id
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// Must be a collection
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|pathUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|collection
operator|.
name|getPermissionsNoLock
argument_list|()
operator|.
name|validate
argument_list|(
name|broker
operator|.
name|getSubject
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Not allowed to read collection"
argument_list|)
throw|;
block|}
specifier|final
name|DocumentImpl
name|feedDoc
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|FEED_DOCUMENT_URI
argument_list|)
decl_stmt|;
if|if
condition|(
name|feedDoc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Collection "
operator|+
name|request
operator|.
name|getPath
argument_list|()
operator|+
literal|" is not an Atom feed."
argument_list|)
throw|;
block|}
comment|// Return the collection feed
comment|// String charset = getContext().getDefaultCharset();
if|if
condition|(
name|returnContent
condition|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|getFeed
argument_list|(
name|broker
argument_list|,
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|getEntryById
argument_list|(
name|broker
argument_list|,
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|id
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|204
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// Do we have permission to read the resource
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
name|broker
operator|.
name|getSubject
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Not allowed to read resource"
argument_list|)
throw|;
block|}
if|if
condition|(
name|returnContent
condition|)
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|200
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
name|BINARY_FILE
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|resource
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|OutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|broker
operator|.
name|readBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|resource
argument_list|,
name|os
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
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot read resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O error on read of resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// xml resource
specifier|final
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
specifier|final
name|String
name|charset
init|=
name|getContext
argument_list|()
operator|.
name|getDefaultCharset
argument_list|()
decl_stmt|;
comment|// Serialize the document
try|try
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|resource
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
operator|+
literal|"; charset="
operator|+
name|charset
argument_list|)
expr_stmt|;
specifier|final
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|resource
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot read resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O error on read of resource "
operator|+
name|request
operator|.
name|getPath
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|saxe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Error while serializing XML: "
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|response
operator|.
name|setStatusCode
argument_list|(
literal|204
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
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
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|getEntryById
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|id
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|EXistException
throws|,
name|BadRequestException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|XQueryPool
name|xqueryPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|feedQuery
init|=
name|xqueryPool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|entryByIdSource
argument_list|)
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
if|if
condition|(
name|feedQuery
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|AccessContext
operator|.
name|REST
argument_list|)
expr_stmt|;
try|try
block|{
name|feedQuery
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|entryByIdSource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot compile xquery "
operator|+
name|entryByIdSource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O exception while compiling xquery "
operator|+
name|entryByIdSource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|context
operator|=
name|feedQuery
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_NAME
argument_list|)
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|declareVariable
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|resultSequence
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|feedQuery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"No topic was found."
argument_list|)
throw|;
block|}
specifier|final
name|String
name|charset
init|=
name|getContext
argument_list|()
operator|.
name|getDefaultCharset
argument_list|()
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/atom+xml; charset="
operator|+
name|charset
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
specifier|final
name|SAXSerializer
name|sax
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|w
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|resultSequence
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot read resource "
operator|+
name|path
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O error on read of resource "
operator|+
name|path
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|saxe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Error while serializing XML: "
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|resultSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot execute xquery "
operator|+
name|entryByIdSource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|xqueryPool
operator|.
name|returnCompiledXQuery
argument_list|(
name|entryByIdSource
argument_list|,
name|feedQuery
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|getFeed
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|path
parameter_list|,
name|OutgoingMessage
name|response
parameter_list|)
throws|throws
name|EXistException
throws|,
name|BadRequestException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|XQueryPool
name|xqueryPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|feedQuery
init|=
name|xqueryPool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|getFeedSource
argument_list|)
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
if|if
condition|(
name|feedQuery
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|AccessContext
operator|.
name|REST
argument_list|)
expr_stmt|;
try|try
block|{
name|feedQuery
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|getFeedSource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot compile xquery "
operator|+
name|getFeedSource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O exception while compiling xquery "
operator|+
name|getFeedSource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|context
operator|=
name|feedQuery
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|XmldbURI
index|[]
block|{
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
name|AtomProtocol
operator|.
name|FEED_DOCUMENT_NAME
argument_list|)
block|}
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Sequence
name|resultSequence
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|feedQuery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"No feed was found."
argument_list|)
throw|;
block|}
specifier|final
name|String
name|charset
init|=
name|getContext
argument_list|()
operator|.
name|getDefaultCharset
argument_list|()
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/atom+xml; charset="
operator|+
name|charset
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
specifier|final
name|SAXSerializer
name|sax
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|w
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|resultSequence
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|w
operator|.
name|flush
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Cannot read resource "
operator|+
name|path
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"I/O error on read of resource "
operator|+
name|path
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|saxe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|saxe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Error while serializing XML: "
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|resultSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Cannot execute xquery "
operator|+
name|getFeedSource
operator|.
name|getURL
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|xqueryPool
operator|.
name|returnCompiledXQuery
argument_list|(
name|getFeedSource
argument_list|,
name|feedQuery
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

