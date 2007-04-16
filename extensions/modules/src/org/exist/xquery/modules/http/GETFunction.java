begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
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
name|http
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
name|Cookie
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
name|methods
operator|.
name|GetMethod
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
name|Base64Encoder
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
name|IntegerValue
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
name|SequenceType
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|GETFunction
extends|extends
name|BasicFunction
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
literal|"get"
argument_list|,
name|HTTPModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|HTTPModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Performs a HTTP GET request. $a is the URL, $b determines if cookies persist for the query lifetime."
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
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
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
name|GETFunction
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
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
comment|//get the persist cookies
name|boolean
name|persistCookies
init|=
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
comment|//setup get content
name|GetMethod
name|get
init|=
operator|new
name|GetMethod
argument_list|(
name|url
argument_list|)
decl_stmt|;
comment|//use existing cookies
if|if
condition|(
name|persistCookies
condition|)
block|{
name|Cookie
index|[]
name|cookies
init|=
operator|(
name|Cookie
index|[]
operator|)
name|context
operator|.
name|getXQueryContextVar
argument_list|(
name|HTTPModule
operator|.
name|HTTP_MODULE_PERSISTENT_COOKIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|cookies
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|cookies
operator|.
name|length
condition|;
name|c
operator|++
control|)
block|{
name|get
operator|.
name|setRequestHeader
argument_list|(
literal|"Cookie"
argument_list|,
name|cookies
index|[
name|c
index|]
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//execute the request
name|int
name|result
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|HttpClient
name|http
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|result
operator|=
name|http
operator|.
name|executeMethod
argument_list|(
name|get
argument_list|)
expr_stmt|;
comment|//persist cookies
if|if
condition|(
name|persistCookies
condition|)
block|{
name|HttpState
name|state
init|=
name|http
operator|.
name|getState
argument_list|()
decl_stmt|;
name|Cookie
index|[]
name|incomingCookies
init|=
name|state
operator|.
name|getCookies
argument_list|()
decl_stmt|;
name|Cookie
index|[]
name|currentCookies
init|=
operator|(
name|Cookie
index|[]
operator|)
name|context
operator|.
name|getXQueryContextVar
argument_list|(
name|HTTPModule
operator|.
name|HTTP_MODULE_PERSISTENT_COOKIES
argument_list|)
decl_stmt|;
name|context
operator|.
name|setXQueryContextVar
argument_list|(
name|HTTPModule
operator|.
name|HTTP_MODULE_PERSISTENT_COOKIES
argument_list|,
name|HTTPModule
operator|.
name|mergeCookies
argument_list|(
name|currentCookies
argument_list|,
name|incomingCookies
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//determine the type of the response document
name|Header
name|responseContentType
init|=
name|get
operator|.
name|getResponseHeader
argument_list|(
literal|"Content-Type"
argument_list|)
decl_stmt|;
name|String
name|responseMimeType
init|=
name|responseContentType
operator|.
name|getValue
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|responseContentType
operator|.
name|getValue
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
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
comment|//return the data
if|if
condition|(
name|mimeType
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
comment|// xml response
return|return
name|ModuleUtils
operator|.
name|stringToXML
argument_list|(
name|context
argument_list|,
name|get
operator|.
name|getResponseBodyAsString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|mimeType
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
comment|// html response
comment|/*if(isValidXHTML) 					{ 						return xhtml; 					} 					else 					{ 						//tidy up the html 					}*/
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
comment|// assume binary response, encode as base64
name|Base64Encoder
name|enc
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|enc
operator|.
name|translate
argument_list|(
name|get
operator|.
name|getResponseBody
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|enc
operator|.
name|getCharArray
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
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
finally|finally
block|{
name|get
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

