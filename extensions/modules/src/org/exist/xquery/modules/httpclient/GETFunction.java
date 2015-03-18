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
name|methods
operator|.
name|GetMethod
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
name|SequenceType
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

begin_comment
comment|/**  * Performs HTTP Get Method  *  * @author   Adam Retter<adam.retter@devon.gov.uk>  * @author   Andrzej Taramina<andrzej@chaeron.com>  * @version  1.3  * @serial   20100228  */
end_comment

begin_class
specifier|public
class|class
name|GETFunction
extends|extends
name|BaseHTTPClientFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|GETFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|"Performs a HTTP GET request."
operator|+
literal|" This method returns the HTTP response encoded as an XML fragment, that looks as follows:<httpclient:response xmlns:httpclient=\"http://exist-db.org/xquery/httpclient\" statusCode=\"200\"><httpclient:headers><httpclient:header name=\"name\" value=\"value\"/>...</httpclient:headers><httpclient:body type=\"xml|xhtml|text|binary\" mimetype=\"returned content mimetype\">body content</httpclient:body></httpclient:response>"
operator|+
literal|" where XML body content will be returned as a Node, HTML body content will be tidied into an XML compatible form, a body with mime-type of \"text/...\" will be returned as a URLEncoded string, and any other body content will be returned as xs:base64Binary encoded data."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|URI_PARAM
block|,
name|PERSIST_PARAM
block|,
name|REQUEST_HEADER_PARAM
block|}
argument_list|,
name|XML_BODY_RETURN
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|"Performs a HTTP GET request."
operator|+
literal|" This method returns the HTTP response encoded as an XML fragment, that looks as follows:<httpclient:response xmlns:httpclient=\"http://exist-db.org/xquery/httpclient\" statusCode=\"200\"><httpclient:headers><httpclient:header name=\"name\" value=\"value\"/>...</httpclient:headers><httpclient:body type=\"xml|xhtml|text|binary\" mimetype=\"returned content mimetype\">body content</httpclient:body></httpclient:response>"
operator|+
literal|" where XML body content will be returned as a Node, HTML body content will be tidied into an XML compatible form, a body with mime-type of \"text/...\" will be returned as a URLEncoded string, and any other body content will be returned as xs:base64Binary encoded data."
operator|+
literal|" When HTML is converted to XML. Features and properties of the parser may be set in the options parameter."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|URI_PARAM
block|,
name|PERSIST_PARAM
block|,
name|REQUEST_HEADER_PARAM
block|,
name|OPTIONS_PARAM
block|}
argument_list|,
name|XML_BODY_RETURN
argument_list|)
block|}
decl_stmt|;
specifier|public
name|GETFunction
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
annotation|@
name|Override
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
comment|//get the persist state
name|boolean
name|persistState
init|=
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
comment|//setup GET request
name|GetMethod
name|get
init|=
operator|new
name|GetMethod
argument_list|(
name|url
argument_list|)
decl_stmt|;
comment|//setup GET Request Headers
if|if
condition|(
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|setHeaders
argument_list|(
name|get
argument_list|,
operator|(
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|2
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
name|FeaturesAndProperties
name|featuresAndProperties
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
operator|&&
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
name|featuresAndProperties
operator|=
name|getParserFeaturesAndProperties
argument_list|(
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
name|get
argument_list|,
name|persistState
argument_list|,
name|featuresAndProperties
operator|==
literal|null
condition|?
literal|null
else|:
name|featuresAndProperties
operator|.
name|getFeatures
argument_list|()
argument_list|,
name|featuresAndProperties
operator|==
literal|null
condition|?
literal|null
else|:
name|featuresAndProperties
operator|.
name|getProperties
argument_list|()
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
name|get
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

