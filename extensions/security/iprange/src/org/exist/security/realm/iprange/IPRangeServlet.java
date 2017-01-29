begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|iprange
package|;
end_package

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
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
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
name|servlet
operator|.
name|http
operator|.
name|HttpSession
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
name|PrintWriter
import|;
end_import

begin_comment
comment|/**  * IPRange authenticator servlet.  *  * @author<a href="mailto:wshager@gmail.com">Wouter Hager</a>  */
end_comment

begin_class
specifier|public
class|class
name|IPRangeServlet
extends|extends
name|HttpServlet
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
name|IPRangeServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|568037449837549034L
decl_stmt|;
specifier|public
specifier|static
name|AbstractRealm
name|realm
init|=
literal|null
decl_stmt|;
specifier|public
name|IPRangeServlet
parameter_list|()
throws|throws
name|ServletException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
specifier|final
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
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
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
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Get reverse proxy header when available, otherwise use regular IP address
name|String
name|ipAddress
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"X-Forwarded-For"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ipAddress
operator|==
literal|null
condition|)
block|{
name|ipAddress
operator|=
name|request
operator|.
name|getRemoteAddr
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Detected IPaddress "
operator|+
name|ipAddress
argument_list|)
expr_stmt|;
name|String
name|jsonResponse
init|=
literal|"{\"fail\":\"IP range not authenticated\"}"
decl_stmt|;
try|try
block|{
specifier|final
name|SecurityManager
name|securityManager
init|=
name|IPRangeRealm
operator|.
name|instance
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|user
init|=
name|securityManager
operator|.
name|authenticate
argument_list|(
name|ipAddress
argument_list|,
name|ipAddress
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"IPRangeServlet user "
operator|+
name|user
operator|.
name|getUsername
argument_list|()
operator|+
literal|" found"
argument_list|)
expr_stmt|;
comment|// Security check
if|if
condition|(
name|user
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"User "
operator|+
name|user
operator|.
name|getUsername
argument_list|()
operator|+
literal|" has DBA rights, will not be authorized"
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|HttpSession
name|session
init|=
name|request
operator|.
name|getSession
argument_list|()
decl_stmt|;
comment|// store the user in the session
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|jsonResponse
operator|=
literal|"{\"user\":\""
operator|+
name|user
operator|.
name|getUsername
argument_list|()
operator|+
literal|"\",\"isAdmin\":\""
operator|+
name|user
operator|.
name|hasDbaRole
argument_list|()
operator|+
literal|"\"}"
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"IPRangeServlet setting session attr "
operator|+
name|XQueryContext
operator|.
name|HTTP_SESSIONVAR_XMLDB_USER
argument_list|)
expr_stmt|;
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
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"IPRangeServlet session is null"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"IPRangeServlet user not found"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json"
argument_list|)
expr_stmt|;
specifier|final
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|jsonResponse
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

