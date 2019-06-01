begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2008 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|servlet
operator|.
name|http
operator|.
name|HttpSession
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
name|security
operator|.
name|AuthenticationException
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
name|SecurityManager
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
name|Subject
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
name|util
operator|.
name|Base64Decoder
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|BasicAuthenticator
implements|implements
name|Authenticator
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
name|BasicAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|BasicAuthenticator
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Subject
name|authenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|boolean
name|sendChallenge
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|credentials
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Authorization"
argument_list|)
decl_stmt|;
name|String
name|username
init|=
literal|null
decl_stmt|;
name|String
name|password
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Base64Decoder
name|dec
init|=
operator|new
name|Base64Decoder
argument_list|()
decl_stmt|;
name|dec
operator|.
name|translate
argument_list|(
name|credentials
operator|.
name|substring
argument_list|(
literal|"Basic "
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|c
init|=
name|dec
operator|.
name|getByteArray
argument_list|()
decl_stmt|;
specifier|final
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|c
argument_list|)
decl_stmt|;
comment|// LOG.debug("BASIC auth credentials: "+s);
specifier|final
name|int
name|p
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|username
operator|=
name|p
operator|<
literal|0
condition|?
name|s
else|:
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|password
operator|=
name|p
operator|<
literal|0
condition|?
literal|null
else|:
name|s
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid BASIC authentication header received: "
operator|+
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|iae
argument_list|)
expr_stmt|;
name|credentials
operator|=
literal|null
expr_stmt|;
block|}
comment|// get the user from the session if possible
specifier|final
name|HttpSession
name|session
init|=
name|request
operator|.
name|getSession
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Subject
name|user
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
operator|(
name|Subject
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
name|XQueryContext
operator|.
name|HTTP_SESSIONVAR_XMLDB_USER
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
operator|(
name|username
operator|==
literal|null
operator|||
name|user
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|username
argument_list|)
operator|)
condition|)
block|{
return|return
name|user
return|;
block|}
block|}
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|removeAttribute
argument_list|(
name|XQueryContext
operator|.
name|HTTP_SESSIONVAR_XMLDB_USER
argument_list|)
expr_stmt|;
block|}
comment|// get the credentials
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
block|{
comment|// prompt for credentials
comment|// LOG.debug("Sending BASIC auth challenge.");
if|if
condition|(
name|sendChallenge
condition|)
block|{
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|// authenticate the credentials
specifier|final
name|SecurityManager
name|secman
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
try|try
block|{
name|user
operator|=
name|secman
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
name|e
parameter_list|)
block|{
comment|// if authentication failed then send a challenge request again
if|if
condition|(
name|sendChallenge
condition|)
block|{
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|// store the user in the session
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|setAttribute
argument_list|(
name|XQueryContext
operator|.
name|HTTP_SESSIONVAR_XMLDB_USER
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
comment|// return the authenticated user
return|return
name|user
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see 	 * org.exist.http.servlets.Authenticator#sendChallenge(javax.servlet.http 	 * .HttpServletRequest, javax.servlet.http.HttpServletResponse) 	 */
specifier|public
name|void
name|sendChallenge
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setHeader
argument_list|(
literal|"WWW-Authenticate"
argument_list|,
literal|"Basic realm=\"exist\""
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
