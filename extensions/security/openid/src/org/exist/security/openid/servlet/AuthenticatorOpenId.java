begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|openid
operator|.
name|servlet
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
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|*
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
name|*
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|security
operator|.
name|DefaultIdentityService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|security
operator|.
name|authentication
operator|.
name|FormAuthenticator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Authentication
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|UserIdentity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
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
name|AXSchemaType
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
name|Account
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
name|AbstractRealm
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
name|internal
operator|.
name|SubjectAccreditedImpl
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
name|openid
operator|.
name|OpenIDUtility
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
name|openid
operator|.
name|SessionAuthentication
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
name|openid
operator|.
name|AccountImpl
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
name|util
operator|.
name|HTTPUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|OpenIDException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|association
operator|.
name|AssociationSessionType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|consumer
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|discovery
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ax
operator|.
name|AxMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ax
operator|.
name|FetchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|ax
operator|.
name|FetchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|sreg
operator|.
name|SRegMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|sreg
operator|.
name|SRegRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|message
operator|.
name|sreg
operator|.
name|SRegResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|AuthenticatorOpenId
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AuthenticatorOpenId
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|ConsumerManager
name|manager
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|AbstractRealm
name|openIDrealm
init|=
literal|null
decl_stmt|;
comment|//XXX: get one from SM
specifier|public
name|AuthenticatorOpenId
parameter_list|()
throws|throws
name|ConsumerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// --- Forward proxy setup (only if needed) ---
name|ProxyProperties
name|proxyProps
init|=
name|getProxyProperties
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxyProps
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ProxyProperties: "
operator|+
name|proxyProps
argument_list|)
expr_stmt|;
name|HttpClientFactory
operator|.
name|setProxyProperties
argument_list|(
name|proxyProps
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|manager
operator|=
operator|new
name|ConsumerManager
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConsumerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|manager
operator|.
name|setAssociations
argument_list|(
operator|new
name|InMemoryConsumerAssociationStore
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setNonceVerifier
argument_list|(
operator|new
name|InMemoryNonceVerifier
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setMinAssocSessEnc
argument_list|(
name|AssociationSessionType
operator|.
name|DH_SHA256
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|doPost
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"is_return"
argument_list|)
argument_list|)
condition|)
block|{
name|processReturn
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|identifier
init|=
name|req
operator|.
name|getParameter
argument_list|(
literal|"openid_identifier"
argument_list|)
decl_stmt|;
if|if
condition|(
name|identifier
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|authRequest
argument_list|(
name|identifier
argument_list|,
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//				this.getServletContext().getRequestDispatcher("/openid/login.xql")
comment|//						.forward(req, resp);
name|resp
operator|.
name|sendRedirect
argument_list|(
literal|"openid/login.xql"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|processReturn
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|Account
name|principal
init|=
name|this
operator|.
name|verifyResponse
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|String
name|returnURL
init|=
name|req
operator|.
name|getParameter
argument_list|(
literal|"exist_return"
argument_list|)
decl_stmt|;
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
block|{
comment|//			this.getServletContext().getRequestDispatcher("/openid/login.xql").forward(req, resp);
name|resp
operator|.
name|sendRedirect
argument_list|(
name|returnURL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|HttpSession
name|session
init|=
name|req
operator|.
name|getSession
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|//			((XQueryURLRewrite.RequestWrapper)req).setUserPrincipal(principal);
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
comment|//TODO: hardcoded to jetty - rewrite
comment|//*******************************************************
name|DefaultIdentityService
name|_identityService
init|=
operator|new
name|DefaultIdentityService
argument_list|()
decl_stmt|;
name|UserIdentity
name|user
init|=
name|_identityService
operator|.
name|newUserIdentity
argument_list|(
name|subject
argument_list|,
name|principal
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|FormAuthenticator
name|authenticator
init|=
operator|new
name|FormAuthenticator
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Authentication
name|cached
init|=
operator|new
name|SessionAuthentication
argument_list|(
name|session
argument_list|,
name|authenticator
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
name|SessionAuthentication
operator|.
name|__J_AUTHENTICATED
argument_list|,
name|cached
argument_list|)
expr_stmt|;
comment|//*******************************************************
name|resp
operator|.
name|sendRedirect
argument_list|(
name|returnURL
argument_list|)
expr_stmt|;
block|}
block|}
comment|// authentication request
specifier|public
name|String
name|authRequest
parameter_list|(
name|String
name|userSuppliedString
parameter_list|,
name|HttpServletRequest
name|httpReq
parameter_list|,
name|HttpServletResponse
name|httpResp
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
try|try
block|{
name|String
name|returnAfterAuthentication
init|=
name|httpReq
operator|.
name|getParameter
argument_list|(
literal|"return_to"
argument_list|)
decl_stmt|;
comment|// configure the return_to URL where your application will receive
comment|// the authentication responses from the OpenID provider
name|String
name|returnToUrl
init|=
name|httpReq
operator|.
name|getRequestURL
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"?is_return=true&exist_return="
operator|+
name|returnAfterAuthentication
decl_stmt|;
comment|// perform discovery on the user-supplied identifier
name|List
argument_list|<
name|?
argument_list|>
name|discoveries
init|=
name|manager
operator|.
name|discover
argument_list|(
name|userSuppliedString
argument_list|)
decl_stmt|;
comment|// attempt to associate with the OpenID provider
comment|// and retrieve one service endpoint for authentication
name|DiscoveryInformation
name|discovered
init|=
name|manager
operator|.
name|associate
argument_list|(
name|discoveries
argument_list|)
decl_stmt|;
comment|// store the discovery information in the user's session
name|httpReq
operator|.
name|getSession
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"openid-disc"
argument_list|,
name|discovered
argument_list|)
expr_stmt|;
comment|// obtain a AuthRequest message to be sent to the OpenID provider
name|AuthRequest
name|authReq
init|=
name|manager
operator|.
name|authenticate
argument_list|(
name|discovered
argument_list|,
name|returnToUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|authReq
operator|.
name|getOPEndpoint
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"myopenid.com"
argument_list|)
operator|>
literal|0
condition|)
block|{
name|SRegRequest
name|sregReq
init|=
name|SRegRequest
operator|.
name|createFetchRequest
argument_list|()
decl_stmt|;
name|sregReq
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sregReq
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|EMAIL
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sregReq
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|COUNTRY
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sregReq
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|LANGUAGE
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|authReq
operator|.
name|addExtension
argument_list|(
name|sregReq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FetchRequest
name|fetch
init|=
name|FetchRequest
operator|.
name|createFetchRequest
argument_list|()
decl_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|FIRSTNAME
operator|.
name|getAlias
argument_list|()
argument_list|,
name|AXSchemaType
operator|.
name|FIRSTNAME
operator|.
name|getNamespace
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|LASTNAME
operator|.
name|getAlias
argument_list|()
argument_list|,
name|AXSchemaType
operator|.
name|LASTNAME
operator|.
name|getNamespace
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|EMAIL
operator|.
name|getAlias
argument_list|()
argument_list|,
name|AXSchemaType
operator|.
name|EMAIL
operator|.
name|getNamespace
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|COUNTRY
operator|.
name|getAlias
argument_list|()
argument_list|,
name|AXSchemaType
operator|.
name|COUNTRY
operator|.
name|getNamespace
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|addAttribute
argument_list|(
name|AXSchemaType
operator|.
name|LANGUAGE
operator|.
name|getAlias
argument_list|()
argument_list|,
name|AXSchemaType
operator|.
name|LANGUAGE
operator|.
name|getNamespace
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// wants up to three email addresses
name|fetch
operator|.
name|setCount
argument_list|(
name|AXSchemaType
operator|.
name|EMAIL
operator|.
name|getAlias
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|authReq
operator|.
name|addExtension
argument_list|(
name|fetch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|discovered
operator|.
name|isVersion2
argument_list|()
condition|)
block|{
comment|// Option 1: GET HTTP-redirect to the OpenID Provider endpoint
comment|// The only method supported in OpenID 1.x
comment|// redirect-URL usually limited ~2048 bytes
name|httpResp
operator|.
name|sendRedirect
argument_list|(
name|authReq
operator|.
name|getDestinationUrl
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// Option 2: HTML FORM Redirection (Allows payloads>2048 bytes)
name|Object
name|OPEndpoint
init|=
name|authReq
operator|.
name|getDestinationUrl
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|ServletOutputStream
name|out
init|=
name|httpResp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|httpResp
operator|.
name|setContentType
argument_list|(
literal|"text/html; charset=UTF-8"
argument_list|)
expr_stmt|;
name|httpResp
operator|.
name|addHeader
argument_list|(
literal|"pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|httpResp
operator|.
name|addHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<title>OpenID HTML FORM Redirection</title>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<body onload=\"document.forms['openid-form-redirection'].submit();\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<form name=\"openid-form-redirection\" action=\""
operator|+
name|OPEndpoint
operator|+
literal|"\" method=\"post\" accept-charset=\"utf-8\">"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameterMap
init|=
name|authReq
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|parameterMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<input type=\"hidden\" name=\""
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"\" value=\""
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"<button type=\"submit\">Continue...</button>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</form>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</body>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</html>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OpenIDException
name|e
parameter_list|)
block|{
comment|// present error to the user
name|LOG
operator|.
name|debug
argument_list|(
literal|"OpenIDException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|ServletOutputStream
name|out
init|=
name|httpResp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|httpResp
operator|.
name|setContentType
argument_list|(
literal|"text/html; charset=\"UTF-8\""
argument_list|)
expr_stmt|;
name|httpResp
operator|.
name|addHeader
argument_list|(
literal|"pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|httpResp
operator|.
name|addHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|httpResp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<html><head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<title>OpenIDServlet Error</title>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<link rel=\"stylesheet\" type=\"text/css\" href=\"error.css\"></link></head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<body><div id=\"container\"><h1>Error found</h1>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<h2>Message:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</h2>"
argument_list|)
expr_stmt|;
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
comment|// t can be null
name|out
operator|.
name|print
argument_list|(
name|HTTPUtils
operator|.
name|printStackTraceHTML
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</div></body></html>"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|// authentication response
specifier|public
name|Account
name|verifyResponse
parameter_list|(
name|HttpServletRequest
name|httpReq
parameter_list|)
throws|throws
name|ServletException
block|{
try|try
block|{
comment|// extract the parameters from the authentication response
comment|// (which comes in as a HTTP request from the OpenID provider)
name|ParameterList
name|response
init|=
operator|new
name|ParameterList
argument_list|(
name|httpReq
operator|.
name|getParameterMap
argument_list|()
argument_list|)
decl_stmt|;
comment|// retrieve the previously stored discovery information
name|DiscoveryInformation
name|discovered
init|=
operator|(
name|DiscoveryInformation
operator|)
name|httpReq
operator|.
name|getSession
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"openid-disc"
argument_list|)
decl_stmt|;
comment|// extract the receiving URL from the HTTP request
name|StringBuffer
name|receivingURL
init|=
name|httpReq
operator|.
name|getRequestURL
argument_list|()
decl_stmt|;
name|String
name|queryString
init|=
name|httpReq
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
operator|&&
name|queryString
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|receivingURL
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
operator|.
name|append
argument_list|(
name|httpReq
operator|.
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the response; ConsumerManager needs to be the same
comment|// (static) instance used to place the authentication request
name|VerificationResult
name|verification
init|=
name|manager
operator|.
name|verify
argument_list|(
name|receivingURL
operator|.
name|toString
argument_list|()
argument_list|,
name|response
argument_list|,
name|discovered
argument_list|)
decl_stmt|;
comment|// examine the verification result and extract the verified
comment|// identifier
name|Identifier
name|verified
init|=
name|verification
operator|.
name|getVerifiedId
argument_list|()
decl_stmt|;
if|if
condition|(
name|verified
operator|!=
literal|null
condition|)
block|{
comment|// success
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
name|principal
init|=
operator|new
name|SubjectAccreditedImpl
argument_list|(
operator|new
name|AccountImpl
argument_list|(
name|openIDrealm
argument_list|,
name|verified
argument_list|)
argument_list|,
name|verified
argument_list|)
decl_stmt|;
name|AuthSuccess
name|authSuccess
init|=
operator|(
name|AuthSuccess
operator|)
name|verification
operator|.
name|getAuthResponse
argument_list|()
decl_stmt|;
name|authSuccess
operator|.
name|getExtensions
argument_list|()
expr_stmt|;
if|if
condition|(
name|authSuccess
operator|.
name|hasExtension
argument_list|(
name|SRegMessage
operator|.
name|OPENID_NS_SREG
argument_list|)
condition|)
block|{
name|MessageExtension
name|ext
init|=
name|authSuccess
operator|.
name|getExtension
argument_list|(
name|SRegMessage
operator|.
name|OPENID_NS_SREG
argument_list|)
decl_stmt|;
if|if
condition|(
name|ext
operator|instanceof
name|SRegResponse
condition|)
block|{
name|SRegResponse
name|sregResp
init|=
operator|(
name|SRegResponse
operator|)
name|ext
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|sregResp
operator|.
name|getAttributeNames
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
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
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|name
operator|+
literal|" : "
operator|+
name|sregResp
operator|.
name|getParameterValue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|principal
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|name
argument_list|)
argument_list|,
name|sregResp
operator|.
name|getParameterValue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|authSuccess
operator|.
name|hasExtension
argument_list|(
name|AxMessage
operator|.
name|OPENID_NS_AX
argument_list|)
condition|)
block|{
name|FetchResponse
name|fetchResp
init|=
operator|(
name|FetchResponse
operator|)
name|authSuccess
operator|.
name|getExtension
argument_list|(
name|AxMessage
operator|.
name|OPENID_NS_AX
argument_list|)
decl_stmt|;
name|List
name|aliases
init|=
name|fetchResp
operator|.
name|getAttributeAliases
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|aliases
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|alias
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|List
name|values
init|=
name|fetchResp
operator|.
name|getAttributeValues
argument_list|(
name|alias
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
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
name|alias
operator|+
literal|" : "
operator|+
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|principal
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|valueOfAlias
argument_list|(
name|alias
argument_list|)
argument_list|,
operator|(
name|String
operator|)
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|OpenIDUtility
operator|.
name|registerUser
argument_list|(
name|principal
argument_list|)
expr_stmt|;
return|return
name|principal
return|;
block|}
block|}
catch|catch
parameter_list|(
name|OpenIDException
name|e
parameter_list|)
block|{
comment|// present error to the user
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|ProxyProperties
name|getProxyProperties
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
block|{
name|ProxyProperties
name|proxyProps
decl_stmt|;
name|String
name|host
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"proxy.host"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"proxy.host: "
operator|+
name|host
argument_list|)
expr_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
name|proxyProps
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|proxyProps
operator|=
operator|new
name|ProxyProperties
argument_list|()
expr_stmt|;
name|String
name|port
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"proxy.port"
argument_list|)
decl_stmt|;
name|String
name|username
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"proxy.username"
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"proxy.password"
argument_list|)
decl_stmt|;
name|String
name|domain
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"proxy.domain"
argument_list|)
decl_stmt|;
name|proxyProps
operator|.
name|setProxyHostName
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|proxyProps
operator|.
name|setProxyPort
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|proxyProps
operator|.
name|setUserName
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|proxyProps
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|proxyProps
operator|.
name|setDomain
argument_list|(
name|domain
argument_list|)
expr_stmt|;
block|}
return|return
name|proxyProps
return|;
block|}
block|}
end_class

end_unit

