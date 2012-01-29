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
name|urlrewrite
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|HttpResponseWrapper
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
name|FilterChain
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|Redirect
extends|extends
name|URLRewrite
block|{
specifier|public
name|Redirect
parameter_list|(
name|Element
name|config
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
argument_list|(
name|config
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|String
name|redirectTo
init|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|redirectTo
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"<exist:redirect> needs an attribute 'url'."
argument_list|)
throw|;
if|if
condition|(
name|redirectTo
operator|.
name|matches
argument_list|(
literal|"^\\w+://.*"
argument_list|)
condition|)
name|setTarget
argument_list|(
name|redirectTo
argument_list|)
expr_stmt|;
comment|// do not touch URIs pointing to other server
else|else
name|setTarget
argument_list|(
name|URLRewrite
operator|.
name|normalizePath
argument_list|(
name|redirectTo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doRewrite
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
name|setHeaders
argument_list|(
operator|new
name|HttpResponseWrapper
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendRedirect
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

