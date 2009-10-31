begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
package|;
end_package

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
name|serializers
operator|.
name|XIncludeFilter
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
name|*
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
name|Item
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
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|TransformerFactoryAllocator
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
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
name|transform
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXTransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TemplatesHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

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
name|IOException
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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
specifier|public
class|class
name|XSLTServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|final
specifier|static
name|String
name|REQ_ATTRIBUTE_PREFIX
init|=
literal|"xslt."
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REQ_ATTRIBUTE_STYLESHEET
init|=
literal|"xslt.stylesheet"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REQ_ATTRIBUTE_INPUT
init|=
literal|"xslt.input"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REQ_ATTRIBUTE_PROPERTIES
init|=
literal|"xslt.output."
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REQ_ATTRIBUTE_BASE
init|=
literal|"xslt.base"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XSLTServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|final
name|Map
name|cache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Boolean
name|caching
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isCaching
parameter_list|()
block|{
if|if
condition|(
name|caching
operator|==
literal|null
condition|)
block|{
name|Object
name|property
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|PROPERTY_CACHING_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
name|caching
operator|=
operator|(
name|Boolean
operator|)
name|property
expr_stmt|;
else|else
name|caching
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|caching
return|;
block|}
specifier|protected
name|void
name|doPost
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
name|String
name|stylesheet
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
name|REQ_ATTRIBUTE_STYLESHEET
argument_list|)
decl_stmt|;
if|if
condition|(
name|stylesheet
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No stylesheet source specified!"
argument_list|)
throw|;
name|Item
name|inputNode
init|=
literal|null
decl_stmt|;
name|String
name|sourceAttrib
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
name|REQ_ATTRIBUTE_INPUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceAttrib
operator|!=
literal|null
condition|)
block|{
name|Object
name|sourceObj
init|=
name|request
operator|.
name|getAttribute
argument_list|(
name|sourceAttrib
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceObj
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sourceObj
operator|instanceof
name|Item
condition|)
block|{
name|inputNode
operator|=
operator|(
name|Item
operator|)
name|sourceObj
expr_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|inputNode
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Input for XSLT servlet is not a node. Read from attribute "
operator|+
name|sourceAttrib
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Taking XSLT input from request attribute "
operator|+
name|sourceAttrib
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Input for XSLT servlet is not a node. Read from attribute "
operator|+
name|sourceAttrib
argument_list|)
throw|;
block|}
block|}
name|String
name|userParam
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"xslt.user"
argument_list|)
decl_stmt|;
name|String
name|passwd
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"xslt.password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|userParam
operator|==
literal|null
condition|)
block|{
name|userParam
operator|=
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
name|passwd
operator|=
name|userParam
expr_stmt|;
block|}
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|userParam
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|validate
argument_list|(
name|passwd
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
literal|"Wrong password or user"
argument_list|)
expr_stmt|;
block|}
block|}
name|SAXTransformerFactory
name|factory
init|=
name|TransformerFactoryAllocator
operator|.
name|getTransformerFactory
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|Templates
name|templates
init|=
name|getSource
argument_list|(
name|user
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|factory
argument_list|,
name|stylesheet
argument_list|)
decl_stmt|;
if|if
condition|(
name|templates
operator|==
literal|null
condition|)
return|return;
comment|//do the transformation
name|DBBroker
name|broker
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
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|templates
argument_list|)
decl_stmt|;
name|setParameters
argument_list|(
name|request
argument_list|,
name|handler
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
name|Properties
name|properties
init|=
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|getOutputProperties
argument_list|()
decl_stmt|;
name|setOutputProperties
argument_list|(
name|request
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|String
name|mediaType
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"media-type"
argument_list|)
decl_stmt|;
name|String
name|encoding
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"encoding"
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|encoding
operator|=
literal|"UTF-8"
expr_stmt|;
name|response
operator|.
name|setCharacterEncoding
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
if|if
condition|(
name|mediaType
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|response
operator|.
name|setContentType
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
comment|//check, do mediaType have "charset"
if|else if
condition|(
name|mediaType
operator|.
name|indexOf
argument_list|(
literal|"charset"
argument_list|)
operator|==
operator|-
literal|1
condition|)
name|response
operator|.
name|setContentType
argument_list|(
name|mediaType
operator|+
literal|"; charset="
operator|+
name|encoding
argument_list|)
expr_stmt|;
else|else
name|response
operator|.
name|setContentType
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
block|}
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
name|Writer
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
name|response
operator|.
name|getWriter
argument_list|()
argument_list|)
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|SAXResult
name|result
init|=
operator|new
name|SAXResult
argument_list|(
name|sax
argument_list|)
decl_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
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
name|Receiver
name|receiver
init|=
operator|new
name|ReceiverToSAX
argument_list|(
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|XIncludeFilter
name|xinclude
init|=
operator|new
name|XIncludeFilter
argument_list|(
name|serializer
argument_list|,
name|receiver
argument_list|)
decl_stmt|;
name|receiver
operator|=
name|xinclude
expr_stmt|;
name|String
name|moduleLoadPath
decl_stmt|;
name|String
name|base
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
name|REQ_ATTRIBUTE_BASE
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
name|moduleLoadPath
operator|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|base
argument_list|)
expr_stmt|;
else|else
name|moduleLoadPath
operator|=
name|getCurrentDir
argument_list|(
name|request
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|xinclude
operator|.
name|setModuleLoadPath
argument_list|(
name|moduleLoadPath
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setReceiver
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputNode
operator|!=
literal|null
condition|)
name|serializer
operator|.
name|toSAX
argument_list|(
operator|(
name|NodeValue
operator|)
name|inputNode
argument_list|)
expr_stmt|;
else|else
block|{
name|SAXToReceiver
name|saxreceiver
init|=
operator|new
name|SAXToReceiver
argument_list|(
name|receiver
argument_list|)
decl_stmt|;
name|XMLReader
name|reader
init|=
name|pool
operator|.
name|getParserPool
argument_list|()
operator|.
name|borrowXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|saxreceiver
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"SAX exception while transforming node: "
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
finally|finally
block|{
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
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|response
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"IO exception while transforming node: "
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
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Exception while transforming node: "
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
name|Throwable
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
literal|"An error occurred: "
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
finally|finally
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
specifier|private
name|Templates
name|getSource
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
name|SAXTransformerFactory
name|factory
parameter_list|,
name|String
name|stylesheet
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|String
name|base
decl_stmt|;
if|if
condition|(
name|stylesheet
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|stylesheet
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|canRead
argument_list|()
condition|)
name|stylesheet
operator|=
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
else|else
block|{
if|if
condition|(
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|File
argument_list|(
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|stylesheet
argument_list|)
argument_list|)
expr_stmt|;
name|stylesheet
operator|=
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
operator|new
name|File
argument_list|(
name|getCurrentDir
argument_list|(
name|request
argument_list|)
argument_list|,
name|stylesheet
argument_list|)
expr_stmt|;
name|stylesheet
operator|=
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
literal|"Stylesheet not found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
name|int
name|p
init|=
name|stylesheet
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
name|base
operator|=
name|stylesheet
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
else|else
name|base
operator|=
name|stylesheet
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading stylesheet from "
operator|+
name|stylesheet
argument_list|)
expr_stmt|;
name|CachedStylesheet
name|cached
init|=
operator|(
name|CachedStylesheet
operator|)
name|cache
operator|.
name|get
argument_list|(
name|stylesheet
argument_list|)
decl_stmt|;
if|if
condition|(
name|cached
operator|==
literal|null
condition|)
block|{
name|cached
operator|=
operator|new
name|CachedStylesheet
argument_list|(
name|factory
argument_list|,
name|user
argument_list|,
name|stylesheet
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|stylesheet
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
return|return
name|cached
operator|.
name|getTemplates
argument_list|(
name|user
argument_list|)
return|;
block|}
specifier|private
name|File
name|getCurrentDir
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|path
init|=
name|request
operator|.
name|getPathTranslated
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|request
operator|.
name|getRequestURI
argument_list|()
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
name|int
name|p
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|path
operator|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
return|return
name|file
return|;
else|else
return|return
name|file
operator|.
name|getParentFile
argument_list|()
return|;
block|}
specifier|private
name|void
name|setParameters
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|Transformer
name|transformer
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|Enumeration
name|e
init|=
name|request
operator|.
name|getAttributeNames
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
name|name
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|REQ_ATTRIBUTE_PREFIX
argument_list|)
operator|&&
operator|!
operator|(
name|name
operator|.
name|startsWith
argument_list|(
name|REQ_ATTRIBUTE_PROPERTIES
argument_list|)
operator|||
name|REQ_ATTRIBUTE_INPUT
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|REQ_ATTRIBUTE_STYLESHEET
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
name|Object
name|value
init|=
name|request
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|NodeValue
condition|)
block|{
name|NodeValue
name|nv
init|=
operator|(
name|NodeValue
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|nv
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
block|{
name|value
operator|=
name|nv
operator|.
name|toMemNodeSet
argument_list|()
expr_stmt|;
block|}
block|}
name|transformer
operator|.
name|setParameter
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|transformer
operator|.
name|setParameter
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|REQ_ATTRIBUTE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|setOutputProperties
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
for|for
control|(
name|Enumeration
name|e
init|=
name|request
operator|.
name|getAttributeNames
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
name|name
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|REQ_ATTRIBUTE_PROPERTIES
argument_list|)
condition|)
block|{
name|Object
name|value
init|=
name|request
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
name|properties
operator|.
name|setProperty
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|REQ_ATTRIBUTE_PROPERTIES
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|CachedStylesheet
block|{
name|SAXTransformerFactory
name|factory
decl_stmt|;
name|long
name|lastModified
init|=
operator|-
literal|1
decl_stmt|;
name|Templates
name|templates
init|=
literal|null
decl_stmt|;
name|String
name|uri
decl_stmt|;
specifier|public
name|CachedStylesheet
parameter_list|(
name|SAXTransformerFactory
name|factory
parameter_list|,
name|User
name|user
parameter_list|,
name|String
name|uri
parameter_list|,
name|String
name|baseURI
parameter_list|)
throws|throws
name|ServletException
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
if|if
condition|(
operator|!
name|baseURI
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://"
argument_list|)
condition|)
name|factory
operator|.
name|setURIResolver
argument_list|(
operator|new
name|ExternalResolver
argument_list|(
name|baseURI
argument_list|)
argument_list|)
expr_stmt|;
name|getTemplates
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Templates
name|getTemplates
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|ServletException
block|{
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://"
argument_list|)
condition|)
block|{
name|String
name|docPath
init|=
name|uri
operator|.
name|substring
argument_list|(
literal|"xmldb:exist://"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
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
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|docPath
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isCaching
argument_list|()
operator|||
operator|(
name|doc
operator|!=
literal|null
operator|&&
operator|(
name|templates
operator|==
literal|null
operator|||
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
operator|>
name|lastModified
operator|)
operator|)
condition|)
name|templates
operator|=
name|getSource
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|lastModified
operator|=
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Permission denied to read stylesheet: "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
throw|;
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
literal|"Error while reading stylesheet source from db: "
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
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
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
else|else
block|{
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|long
name|modified
init|=
name|connection
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isCaching
argument_list|()
operator|||
operator|(
name|templates
operator|==
literal|null
operator|||
name|modified
operator|>
name|lastModified
operator|||
name|modified
operator|==
literal|0
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"compiling stylesheet "
operator|+
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|=
name|factory
operator|.
name|newTemplates
argument_list|(
operator|new
name|StreamSource
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastModified
operator|=
name|modified
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Error while reading stylesheet source from uri: "
operator|+
name|uri
operator|+
literal|": "
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
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Error while reading stylesheet source from uri: "
operator|+
name|uri
operator|+
literal|": "
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
return|return
name|templates
return|;
block|}
specifier|private
name|Templates
name|getSource
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|stylesheet
parameter_list|)
throws|throws
name|ServletException
block|{
name|factory
operator|.
name|setURIResolver
argument_list|(
operator|new
name|DatabaseResolver
argument_list|(
name|broker
argument_list|,
name|stylesheet
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|TemplatesHandler
name|handler
init|=
name|factory
operator|.
name|newTemplatesHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
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
name|setSAXHandlers
argument_list|(
name|handler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|stylesheet
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|handler
operator|.
name|getTemplates
argument_list|()
return|;
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
literal|"A SAX exception occurred while compiling the stylesheet: "
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
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"A configuration exception occurred while compiling the stylesheet: "
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
block|}
specifier|private
class|class
name|ExternalResolver
implements|implements
name|URIResolver
block|{
specifier|private
name|String
name|baseURI
decl_stmt|;
specifier|public
name|ExternalResolver
parameter_list|(
name|String
name|base
parameter_list|)
block|{
name|this
operator|.
name|baseURI
operator|=
name|base
expr_stmt|;
block|}
comment|/* (non-Javadoc)            * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)            */
specifier|public
name|Source
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
name|URL
name|url
decl_stmt|;
try|try
block|{
comment|//TODO : use dedicated function in XmldbURI
name|url
operator|=
operator|new
name|URL
argument_list|(
name|baseURI
operator|+
literal|"/"
operator|+
name|href
argument_list|)
expr_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
return|return
operator|new
name|StreamSource
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
specifier|private
class|class
name|DatabaseResolver
implements|implements
name|URIResolver
block|{
name|DocumentImpl
name|doc
decl_stmt|;
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|DatabaseResolver
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|myDoc
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|myDoc
expr_stmt|;
block|}
comment|/* (non-Javadoc)            * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)            */
specifier|public
name|Source
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
name|Collection
name|collection
init|=
name|doc
operator|.
name|getCollection
argument_list|()
decl_stmt|;
name|String
name|path
decl_stmt|;
comment|//TODO : use dedicated function in XmldbURI
if|if
condition|(
name|href
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|path
operator|=
name|href
expr_stmt|;
else|else
name|path
operator|=
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|"/"
operator|+
name|href
expr_stmt|;
name|DocumentImpl
name|xslDoc
decl_stmt|;
try|try
block|{
name|xslDoc
operator|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|xslDoc
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document "
operator|+
name|href
operator|+
literal|" not found in collection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|xslDoc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|broker
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"Insufficient privileges to read resource "
operator|+
name|path
argument_list|)
throw|;
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|xslDoc
argument_list|)
decl_stmt|;
return|return
name|source
return|;
block|}
block|}
block|}
end_class

end_unit

