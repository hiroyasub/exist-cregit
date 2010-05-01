begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|httpclient
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpMethodBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|NameValuePair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|ProxyHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|URIException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|methods
operator|.
name|HeadMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|methods
operator|.
name|OptionsMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|util
operator|.
name|EncodingUtil
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
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentBuilderReceiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
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
name|xquery
operator|.
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|modules
operator|.
name|ModuleUtils
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
name|Base64Binary
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_comment
comment|/**  * Base class for HTTP client methods  *  * @author   Adam Retter<adam.retter@devon.gov.uk>  * @author   Andrzej Taramina<andrzej@chaeron.com>  * @version  1.3  * @serial   20100228  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseHTTPClientFunction
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|BaseHTTPClientFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|URI_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"url"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URL to process"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PUT_CONTENT_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"content"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The XML PUT payload/content. If it is an XML Node it will be serialized, any other type will be atomized into a string."
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|POST_CONTENT_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"content"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The XML POST payload/content. If it is an XML Node it will be serialized, any other type will be atomized into a string."
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|POST_FORM_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"content"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The form data in the format<httpclient:fields><httpclient:field name=\"\" value=\"\" type=\"string|file\"/>...</httpclient:fields>.  If the field values will be suitably URLEncoded and sent with the mime type application/x-www-form-urlencoded."
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PERSIST_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"persist"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Indicates if the HTTP state (eg. cookies, credentials, etc.) should persist for the life of this xquery"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|REQUEST_HEADER_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"request-headers"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Any HTTP Request Headers to set in the form<headers><header name=\"\" value=\"\"/></headers>"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|XML_BODY_RETURN
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the XML body content"
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
name|HTTPClientModule
operator|.
name|NAMESPACE_URI
decl_stmt|;
specifier|final
specifier|static
name|String
name|PREFIX
init|=
name|HTTPClientModule
operator|.
name|PREFIX
decl_stmt|;
specifier|final
specifier|static
name|String
name|HTTP_MODULE_PERSISTENT_STATE
init|=
name|HTTPClientModule
operator|.
name|HTTP_MODULE_PERSISTENT_STATE
decl_stmt|;
specifier|final
specifier|static
name|String
name|HTTP_EXCEPTION_STATUS_CODE
init|=
literal|"500"
decl_stmt|;
specifier|public
name|BaseHTTPClientFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parses header parameters and sets them on the Request.      *      * @param   method   The Http Method to set the request headers on      * @param   headers  The headers node e.g.<headers><header name="Content-Type" value="application/xml"/></headers>      *      * @throws  XPathException       */
specifier|protected
name|void
name|setHeaders
parameter_list|(
name|HttpMethod
name|method
parameter_list|,
name|Node
name|headers
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|(
name|headers
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|)
operator|&&
name|headers
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"headers"
argument_list|)
condition|)
block|{
name|NodeList
name|headerList
init|=
name|headers
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|headerList
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|header
init|=
name|headerList
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|header
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|)
operator|&&
name|header
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"header"
argument_list|)
condition|)
block|{
name|String
name|name
init|=
operator|(
operator|(
name|Element
operator|)
name|header
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
operator|(
operator|(
name|Element
operator|)
name|header
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|name
operator|==
literal|null
operator|)
operator|||
operator|(
name|value
operator|==
literal|null
operator|)
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Name or value attribute missing for request header parameter"
argument_list|)
operator|)
throw|;
block|}
name|method
operator|.
name|addRequestHeader
argument_list|(
operator|new
name|Header
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Performs a HTTP Request.      *      * @param   context         The context of the calling XQuery      * @param   method          The HTTP method for the request      * @param   persistState  	If true existing HTTP state (cookies, credentials, etc) are re-used and athe state is persisted for future HTTP Requests      *      * @return  DOCUMENT ME!      *      * @throws  IOException           * @throws  XPathException        */
specifier|protected
name|Sequence
name|doRequest
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|HttpMethod
name|method
parameter_list|,
name|boolean
name|persistState
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
name|int
name|statusCode
init|=
literal|0
decl_stmt|;
name|Sequence
name|encodedResponse
init|=
literal|null
decl_stmt|;
name|HttpClient
name|http
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
comment|//execute the request
try|try
block|{
comment|//use existing state?
if|if
condition|(
name|persistState
condition|)
block|{
comment|//get existing state
name|HttpState
name|state
init|=
operator|(
name|HttpState
operator|)
name|context
operator|.
name|getXQueryContextVar
argument_list|(
name|HTTP_MODULE_PERSISTENT_STATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|http
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
comment|//set the proxy server (if any)
name|String
name|proxyHost
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"http.proxyHost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxyHost
operator|!=
literal|null
condition|)
block|{
comment|//TODO: support for http.nonProxyHosts e.g. -Dhttp.nonProxyHosts="*.devonline.gov.uk|*.devon.gov.uk"
name|ProxyHost
name|proxy
init|=
operator|new
name|ProxyHost
argument_list|(
name|proxyHost
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"http.proxyPort"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|http
operator|.
name|getHostConfiguration
argument_list|()
operator|.
name|setProxyHost
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
comment|//perform the request
name|statusCode
operator|=
name|http
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|encodedResponse
operator|=
name|encodeResponseAsXML
argument_list|(
name|context
argument_list|,
name|method
argument_list|,
name|statusCode
argument_list|)
expr_stmt|;
comment|//persist state?
if|if
condition|(
name|persistState
condition|)
block|{
name|context
operator|.
name|setXQueryContextVar
argument_list|(
name|HTTP_MODULE_PERSISTENT_STATE
argument_list|,
name|http
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|encodedResponse
operator|=
name|encodeErrorResponse
argument_list|(
name|context
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|encodedResponse
operator|)
return|;
block|}
comment|/**      * Takes the HTTP Response and encodes it as an XML structure.      *      * @param   context     The context of the calling XQuery      * @param   method      The HTTP Request Method      * @param   statusCode  The status code returned from the http method invocation      *      * @return  The data in XML format      *      * @throws  XPathException       * @throws  IOException           */
specifier|private
name|Sequence
name|encodeResponseAsXML
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|HttpMethod
name|method
parameter_list|,
name|int
name|statusCode
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
name|Sequence
name|xmlResponse
init|=
literal|null
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"response"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"statusCode"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|statusCode
argument_list|)
argument_list|)
expr_stmt|;
comment|//Add all the response headers
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"headers"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|NameValuePair
index|[]
name|headers
init|=
name|method
operator|.
name|getResponseHeaders
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|headers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"header"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|headers
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"value"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|headers
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|method
operator|instanceof
name|HeadMethod
operator|)
operator|||
operator|(
name|method
operator|instanceof
name|OptionsMethod
operator|)
operator|)
condition|)
block|{
comment|// Head and Options methods never have any response body
comment|// Add the response body node
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"body"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|insertResponseBody
argument_list|(
name|context
argument_list|,
name|method
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|xmlResponse
operator|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
return|return
operator|(
name|xmlResponse
operator|)
return|;
block|}
comment|/**      * Takes an exception message and encodes it as an XML response structure.      *      * @param   context  The context of the calling XQuery      * @param   message  The exception error message      *      * @return  The response in XML format      *      * @throws  IOException           * @throws  XPathException       */
specifier|private
name|Sequence
name|encodeErrorResponse
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
name|Sequence
name|xmlResponse
init|=
literal|null
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"response"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"statusCode"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|HTTP_EXCEPTION_STATUS_CODE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"body"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"encoding"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"URLEncoded"
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|message
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|xmlResponse
operator|=
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
return|return
operator|(
name|xmlResponse
operator|)
return|;
block|}
comment|/**      * Takes the HTTP Response Body from the HTTP Method and attempts to insert it into the response tree we are building.      *      *<p>Conversion Preference - 1) Try and parse as XML, if successful returns a Node 2) Try and parse as HTML returning as XML compatible HTML, if      * successful returns a Node 3) Return as base64Binary encoded data</p>      *      * @param   context  The context of the calling XQuery      * @param   method   The HTTP Request Method      * @param   builder  The MemTreeBuilder that is being used      *      * @throws  IOException           * @throws  XPathException        */
specifier|private
name|void
name|insertResponseBody
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|HttpMethod
name|method
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|parsed
init|=
literal|false
decl_stmt|;
name|NodeImpl
name|responseNode
init|=
literal|null
decl_stmt|;
name|InputStream
name|bodyAsStream
init|=
name|method
operator|.
name|getResponseBodyAsStream
argument_list|()
decl_stmt|;
comment|// check if there is a response body
if|if
condition|(
name|bodyAsStream
operator|!=
literal|null
condition|)
block|{
name|long
name|contentLength
init|=
operator|(
operator|(
name|HttpMethodBase
operator|)
name|method
operator|)
operator|.
name|getResponseContentLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentLength
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
comment|//guard from overflow
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"HTTPClient response too large to be buffered: "
operator|+
name|contentLength
operator|+
literal|" bytes"
argument_list|)
operator|)
throw|;
block|}
name|ByteArrayOutputStream
name|outstream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
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
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|bodyAsStream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|outstream
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|outstream
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|body
init|=
name|outstream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
comment|// determine the type of the response document
name|MimeType
name|responseMimeType
init|=
name|getResponseMimeType
argument_list|(
name|method
operator|.
name|getResponseHeader
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"mimetype"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|method
operator|.
name|getResponseHeader
argument_list|(
literal|"Content-Type"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|//try and parse the response as XML
try|try
block|{
name|responseNode
operator|=
operator|(
name|NodeImpl
operator|)
name|ModuleUtils
operator|.
name|streamToXML
argument_list|(
name|context
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|body
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|responseNode
operator|.
name|copyTo
argument_list|(
literal|null
argument_list|,
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|se
parameter_list|)
block|{
comment|//could not parse to xml
block|}
if|if
condition|(
name|responseNode
operator|==
literal|null
condition|)
block|{
comment|//response is NOT parseable as XML
comment|//is it a html document?
if|if
condition|(
name|responseMimeType
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MimeType
operator|.
name|HTML_TYPE
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|//html document
try|try
block|{
comment|//parse html to xml(html)
name|responseNode
operator|=
operator|(
name|NodeImpl
operator|)
name|ModuleUtils
operator|.
name|htmlToXHtml
argument_list|(
name|context
argument_list|,
name|method
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|body
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"xhtml"
argument_list|)
expr_stmt|;
name|responseNode
operator|.
name|copyTo
argument_list|(
literal|null
argument_list|,
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URIException
name|ue
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ue
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ue
argument_list|)
operator|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|se
parameter_list|)
block|{
comment|//could not parse to xml(html)
block|}
block|}
block|}
if|if
condition|(
name|responseNode
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|responseMimeType
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"text/"
argument_list|)
condition|)
block|{
comment|// Assume it's a text body and URL encode it
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"encoding"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"URLEncoded"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|EncodingUtil
operator|.
name|getString
argument_list|(
name|body
argument_list|,
operator|(
operator|(
name|HttpMethodBase
operator|)
name|method
operator|)
operator|.
name|getResponseCharSet
argument_list|()
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Assume it's a binary body and Base64 encode it
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"binary"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"encoding"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"Base64Encoded"
argument_list|)
expr_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|Base64Binary
name|binary
init|=
operator|new
name|Base64Binary
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|binary
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Given the Response Header for Content-Type this function returns an appropriate eXist MimeType.      *      * @param   responseHeaderContentType  The HTTP Response Header containing the Content-Type of the Response.      *      * @return  The corresponding eXist MimeType      */
specifier|protected
name|MimeType
name|getResponseMimeType
parameter_list|(
name|Header
name|responseHeaderContentType
parameter_list|)
block|{
name|MimeType
name|returnMimeType
init|=
name|MimeType
operator|.
name|BINARY_TYPE
decl_stmt|;
if|if
condition|(
name|responseHeaderContentType
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|responseHeaderContentType
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Content-Type"
argument_list|)
condition|)
block|{
name|String
name|responseContentType
init|=
name|responseHeaderContentType
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|contentTypeEnd
init|=
name|responseContentType
operator|.
name|indexOf
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentTypeEnd
operator|==
operator|-
literal|1
condition|)
block|{
name|contentTypeEnd
operator|=
name|responseContentType
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|String
name|responseMimeType
init|=
name|responseContentType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|contentTypeEnd
argument_list|)
decl_stmt|;
name|MimeTable
name|mimeTable
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|MimeType
name|mimeType
init|=
name|mimeTable
operator|.
name|getContentType
argument_list|(
name|responseMimeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mimeType
operator|!=
literal|null
condition|)
block|{
name|returnMimeType
operator|=
name|mimeType
expr_stmt|;
block|}
block|}
block|}
return|return
operator|(
name|returnMimeType
operator|)
return|;
block|}
block|}
end_class

end_unit

