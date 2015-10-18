begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist EXPath HTTP Client Module send-request functions  *  Copyright (C) 2011 Adam Retter<adam@existsolutions.com>  *  www.existsolutions.com  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|expath
operator|.
name|exist
package|;
end_package

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
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpClientException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpCredentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|impl
operator|.
name|ApacheHttpConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|impl
operator|.
name|RequestParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|exist
operator|.
name|EXistElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|model
operator|.
name|exist
operator|.
name|EXistResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|exist
operator|.
name|EXistSequence
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com>  * @version EXPath HTTP Client Module Candidate 9 January 2010 http://expath.org/spec/http-client/20100109  */
end_comment

begin_class
specifier|public
class|class
name|SendRequestFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SendRequestFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|REQUEST_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"request"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"request contains the various parameters of the request, for instance the HTTP method to use or the HTTP headers. Among other things, it can also contain the other param's values: the URI and the bodies. If they are not set as parameter to the function, their value in $request, if any, is used instead. See the following section (http://www.expath.org/spec/http-client#d2e183) for the detailed definition of the http:request element. If the parameter does not follow the grammar defined in this spec, this is an error [err:HC005]."
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|HREF_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"href"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"$href is the HTTP or HTTPS URI to send the request to. It is an xs:anyURI, but is declared as a string to be able to pass literal strings (without requiring to explicitly cast it to an xs:anyURI)"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|BODIES_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"bodies"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"$bodies is the request body content, for HTTP methods that can contain a body in the request (e.g. POST). This is an error if this param is not the empty sequence for methods that must be empty (e.g. DELETE). The details of the methods are defined in their respective specs (e.g. [RFC 2616] or [RFC 4918]). In case of a multipart request, it can be a sequence of several items, each one is the body of the corresponding body descriptor in $request."
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionReturnSequenceType
name|RETURN_TYPE
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
name|ONE_OR_MORE
argument_list|,
literal|"A sequence representing the response from the server. This sequence has an http:response element as first item, which is followed by an additional item for each body or body part in the response. Further detail can be found here - http://www.expath.org/spec/http-client#d2e483"
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
comment|//http:send-request($request as element(http:request)?) as item()+
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"send-request"
argument_list|,
name|HttpClientModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|HttpClientModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Sends a HTTP request to a server and returns the response."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|REQUEST_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|,
comment|//http:send-request($request as element(http:request)?, $href as xs:string?) as item()+
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"send-request"
argument_list|,
name|HttpClientModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|HttpClientModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Sends a HTTP request to a server and returns the response."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|REQUEST_PARAM
block|,
name|HREF_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|,
comment|//http:send-request($request as element(http:request)?, $href as xs:string?, $bodies as item()*) as item()+
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"send-request"
argument_list|,
name|HttpClientModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|HttpClientModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Sends a HTTP request to a server and returns the response."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|REQUEST_PARAM
block|,
name|HREF_PARAM
block|,
name|BODIES_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
comment|/**      * SendRequestFunction Constructor      *      * @param context	The Context of the calling XQuery      * @param signature The actual signature of the function      */
specifier|public
name|SendRequestFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|bodies
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|String
name|href
init|=
literal|null
decl_stmt|;
name|NodeValue
name|request
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|getArgumentCount
argument_list|()
condition|)
block|{
case|case
literal|3
case|:
name|bodies
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
case|case
literal|2
case|:
block|{
name|Item
name|i
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
if|if
condition|(
name|i
operator|!=
literal|null
condition|)
block|{
name|href
operator|=
name|i
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
case|case
literal|1
case|:
name|request
operator|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
default|default:
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
return|return
name|sendRequest
argument_list|(
name|request
argument_list|,
name|href
argument_list|,
name|bodies
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|sendRequest
parameter_list|(
specifier|final
name|NodeValue
name|request
parameter_list|,
specifier|final
name|String
name|href
parameter_list|,
specifier|final
name|Sequence
name|bodies
parameter_list|)
throws|throws
name|XPathException
block|{
name|HttpRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|Sequence
name|b
init|=
operator|new
name|EXistSequence
argument_list|(
name|bodies
argument_list|,
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Element
name|r
init|=
operator|new
name|EXistElement
argument_list|(
name|request
argument_list|,
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RequestParser
name|parser
init|=
operator|new
name|RequestParser
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|req
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|b
argument_list|,
name|href
argument_list|)
expr_stmt|;
comment|// override anyway it href exists
if|if
condition|(
name|href
operator|!=
literal|null
operator|&&
operator|!
name|href
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|req
operator|.
name|setHref
argument_list|(
name|href
argument_list|)
expr_stmt|;
block|}
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|req
operator|.
name|getHref
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|EXistResult
name|result
init|=
name|sendOnce
argument_list|(
name|uri
argument_list|,
name|req
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
name|result
operator|.
name|getResult
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Href is not valid: "
operator|+
name|req
operator|!=
literal|null
condition|?
name|req
operator|.
name|getHref
argument_list|()
else|:
literal|""
operator|+
literal|". "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HttpClientException
name|hce
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|hce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|hce
argument_list|)
throw|;
block|}
block|}
comment|/**      * Send one request, not following redirect but handling authentication.      *       * Authentication may require to reply to an authentication challenge,      * by sending again the request, with credentials.      */
specifier|private
name|EXistResult
name|sendOnce
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|HttpRequest
name|request
parameter_list|,
specifier|final
name|RequestParser
name|parser
parameter_list|)
throws|throws
name|HttpClientException
block|{
specifier|final
name|EXistResult
name|result
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|getSendAuth
argument_list|()
condition|)
block|{
name|result
operator|=
name|sendOnceWithAuth
argument_list|(
name|uri
argument_list|,
name|request
argument_list|,
name|parser
operator|.
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|HttpConnection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
operator|new
name|ApacheHttpConnection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
specifier|final
name|EXistResult
name|firstResult
init|=
operator|new
name|EXistResult
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|HttpResponse
name|response
init|=
name|request
operator|.
name|send
argument_list|(
name|firstResult
argument_list|,
name|conn
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|==
literal|401
condition|)
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|result
operator|=
name|sendOnceWithAuth
argument_list|(
name|uri
argument_list|,
name|request
argument_list|,
name|parser
operator|.
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|firstResult
expr_stmt|;
name|registerConnectionWithContext
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|HttpClientException
name|hce
parameter_list|)
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HttpClientException
name|hcee
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|hcee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|hcee
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
name|hce
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|EXistResult
name|sendOnceWithAuth
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|HttpRequest
name|request
parameter_list|,
specifier|final
name|HttpCredentials
name|httpCredentials
parameter_list|)
throws|throws
name|HttpClientException
block|{
specifier|final
name|EXistResult
name|result
init|=
operator|new
name|EXistResult
argument_list|(
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
name|HttpConnection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conn
operator|=
operator|new
name|ApacheHttpConnection
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|request
operator|.
name|send
argument_list|(
name|result
argument_list|,
name|conn
argument_list|,
name|httpCredentials
argument_list|)
expr_stmt|;
name|registerConnectionWithContext
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HttpClientException
name|hce
parameter_list|)
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HttpClientException
name|hcee
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|hcee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|hcee
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
name|hce
throw|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|registerConnectionWithContext
parameter_list|(
specifier|final
name|HttpConnection
name|conn
parameter_list|)
block|{
name|context
operator|.
name|registerCleanupTask
argument_list|(
operator|new
name|XQueryContext
operator|.
name|CleanupTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|cleanup
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
try|try
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HttpClientException
name|hce
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|hce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|hce
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

