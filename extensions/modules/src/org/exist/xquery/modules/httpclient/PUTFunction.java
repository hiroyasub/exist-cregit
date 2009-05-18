begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|methods
operator|.
name|ByteArrayRequestEntity
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
name|PutMethod
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
name|RequestEntity
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
name|XMLWriter
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
name|value
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  * @author Andrzej Taramina<andrzej@chaeron.com>  * @serial 20070905  * @version 1.2  */
end_comment

begin_class
specifier|public
class|class
name|PUTFunction
extends|extends
name|BaseHTTPClientFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"put"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|"Performs a HTTP PUT request. $a is the URL, $b is the XML PUT payload/content, $c determines if cookies persist for the query lifetime. $d defines any HTTP Request Headers to set in the form<headers><header name=\"\" value=\"\"/></headers>."
operator|+
literal|" This method returns the HTTP response encoded as an XML fragment, that looks as follows:<httpclient:response xmlns:httpclient=\"http://exist-db.org/xquery/httpclient\" statusCode=\"200\"><httpclient:headers><httpclient:header name=\"name\" value=\"value\"/>...</httpclient:headers><httpclient:body type=\"xml|xhtml|text|binary\" mimetype=\"returned content mimetype\">body content</httpclient:body></httpclient:response>"
operator|+
literal|" where XML body content will be returned as a Node, HTML body content will be tidied into an XML compatible form, a body with mime-type of \"text/...\" will be returned as a URLEncoded string, and any other body content will be returned as xs:base64Binary encoded data."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|PUTFunction
parameter_list|(
name|XQueryContext
name|context
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
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|response
init|=
literal|null
decl_stmt|;
comment|// must be a URL
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
comment|//get the url
name|String
name|url
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|//get the payload
name|Item
name|payload
init|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//get the persist cookies
name|boolean
name|persistCookies
init|=
name|args
index|[
literal|2
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
comment|//serialize the node to SAX
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|OutputStreamWriter
name|osw
init|=
literal|null
decl_stmt|;
try|try
block|{
name|osw
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|baos
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Internal error"
argument_list|)
throw|;
block|}
name|XMLWriter
name|xmlWriter
init|=
operator|new
name|XMLWriter
argument_list|(
name|osw
argument_list|)
decl_stmt|;
name|SAXSerializer
name|sax
init|=
operator|new
name|SAXSerializer
argument_list|()
decl_stmt|;
name|sax
operator|.
name|setReceiver
argument_list|(
name|xmlWriter
argument_list|)
expr_stmt|;
try|try
block|{
name|payload
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|sax
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
name|osw
operator|.
name|flush
argument_list|()
expr_stmt|;
name|osw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//setup PUT request
name|PutMethod
name|put
init|=
operator|new
name|PutMethod
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|RequestEntity
name|entity
init|=
operator|new
name|ByteArrayRequestEntity
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"text/xml; charset=utf-8"
argument_list|)
decl_stmt|;
name|put
operator|.
name|setRequestEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
comment|//setup PUT Request Headers
if|if
condition|(
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setHeaders
argument_list|(
name|put
argument_list|,
operator|(
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|//execute the request
name|response
operator|=
name|doRequest
argument_list|(
name|context
argument_list|,
name|put
argument_list|,
name|persistCookies
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
operator|)
throw|;
block|}
finally|finally
block|{
name|put
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|response
operator|)
return|;
block|}
block|}
end_class

end_unit

