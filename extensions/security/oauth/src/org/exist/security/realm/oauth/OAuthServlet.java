begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|realm
operator|.
name|oauth
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
name|javax
operator|.
name|servlet
operator|.
name|RequestDispatcher
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
name|net
operator|.
name|oauth
operator|.
name|enums
operator|.
name|ResponseType
import|;
end_import

begin_import
import|import
name|net
operator|.
name|oauth
operator|.
name|exception
operator|.
name|OAuthException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|config
operator|.
name|ConsumerConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|config
operator|.
name|OAuthConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|config
operator|.
name|ProviderConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|config
operator|.
name|ServiceConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|config
operator|.
name|SuccessConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|service
operator|.
name|OAuthService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|service
operator|.
name|factory
operator|.
name|OAuthServiceAbstractFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|neurologic
operator|.
name|oauth
operator|.
name|service
operator|.
name|impl
operator|.
name|OAuth2Service
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|OAuthServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6984614473391594578L
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
block|}
comment|/* (non-Javadoc)      * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
specifier|protected
name|void
name|doGet
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
name|process
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
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
name|process
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|process
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
name|path
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|OAuthRealm
operator|.
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|OAuthRealm
operator|.
name|LOG
operator|.
name|trace
argument_list|(
literal|"the "
operator|+
name|request
operator|.
name|getMethod
argument_list|()
operator|+
literal|" method, path info "
operator|+
name|path
argument_list|)
expr_stmt|;
try|try
block|{
name|ServiceConfig
name|serviceConfig
init|=
name|OAuthRealm
operator|.
name|_
operator|.
name|getServiceConfigByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|ClassLoader
name|classLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|classLoader
operator|==
literal|null
condition|)
name|classLoader
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|classLoader
operator|.
name|loadClass
argument_list|(
name|serviceConfig
operator|.
name|getServiceClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"No class exits for "
operator|+
name|serviceConfig
operator|.
name|getServiceClass
argument_list|()
argument_list|)
throw|;
name|OAuthConfig
name|oauthConfig
init|=
name|OAuthRealm
operator|.
name|_
operator|.
name|getOAuthConfigByName
argument_list|(
name|serviceConfig
operator|.
name|getRefOAuth
argument_list|()
argument_list|)
decl_stmt|;
name|ProviderConfig
name|providerConfig
init|=
name|oauthConfig
operator|.
name|getProvider
argument_list|()
decl_stmt|;
name|ConsumerConfig
name|consumerConfig
init|=
name|oauthConfig
operator|.
name|getConsumer
argument_list|()
decl_stmt|;
if|if
condition|(
name|providerConfig
operator|==
literal|null
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"No<provider> defined under<oauth>. Cannot create OAuth Service Provider."
argument_list|)
throw|;
if|if
condition|(
name|consumerConfig
operator|==
literal|null
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"No<consumer> defined under<oauth>. Cannot create OAuth Consumer."
argument_list|)
throw|;
name|OAuthService
name|service
init|=
name|OAuthServiceAbstractFactory
operator|.
name|getOAuthServiceFactory
argument_list|(
name|oauthConfig
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|createOAuthService
argument_list|(
name|clazz
argument_list|,
name|providerConfig
argument_list|,
name|consumerConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getParameterMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"auth"
argument_list|)
condition|)
block|{
if|if
condition|(
name|service
operator|instanceof
name|OAuth2Service
condition|)
block|{
name|OAuth2Service
name|s2
init|=
operator|(
name|OAuth2Service
operator|)
name|service
decl_stmt|;
name|response
operator|.
name|sendRedirect
argument_list|(
name|s2
operator|.
name|getConsumer
argument_list|()
operator|.
name|generateRequestAuthorizationUrl
argument_list|(
name|ResponseType
operator|.
name|CODE
argument_list|,
name|s2
operator|.
name|getRedirectUri
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
throw|throw
operator|new
name|OAuthException
argument_list|(
literal|"unsuppored OAuth service "
operator|+
name|service
argument_list|)
throw|;
block|}
name|service
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
comment|//Finally
name|SuccessConfig
name|successConfig
init|=
name|serviceConfig
operator|.
name|getSuccessConfig
argument_list|()
decl_stmt|;
if|if
condition|(
name|successConfig
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|OAuthRealm
operator|.
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|OAuthRealm
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Dispatching to path \""
operator|+
name|successConfig
operator|.
name|getPath
argument_list|()
operator|+
literal|"\"."
argument_list|)
expr_stmt|;
name|RequestDispatcher
name|dispatcher
init|=
name|request
operator|.
name|getRequestDispatcher
argument_list|(
name|successConfig
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|forward
argument_list|(
name|request
argument_list|,
name|response
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
block|}
end_class

end_unit

