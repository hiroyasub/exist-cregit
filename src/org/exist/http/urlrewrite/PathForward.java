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
name|RequestDispatcher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|HttpServletRequest
import|;
end_import

begin_class
specifier|public
class|class
name|PathForward
extends|extends
name|Forward
block|{
specifier|private
name|FilterConfig
name|filterConfig
decl_stmt|;
specifier|private
name|String
name|servletName
init|=
literal|null
decl_stmt|;
specifier|public
name|PathForward
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|,
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
name|this
operator|.
name|filterConfig
operator|=
name|filterConfig
expr_stmt|;
name|servletName
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"servlet"
argument_list|)
expr_stmt|;
name|String
name|url
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
name|servletName
operator|!=
literal|null
operator|&&
name|servletName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|servletName
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|servletName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|url
operator|==
literal|null
operator|||
name|url
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
literal|"<exist:forward> needs either an attribute 'url' or 'servlet'."
argument_list|)
throw|;
name|setTarget
argument_list|(
name|URLRewrite
operator|.
name|normalizePath
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|RequestDispatcher
name|getRequestDispatcher
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|servletName
operator|!=
literal|null
condition|)
return|return
name|filterConfig
operator|.
name|getServletContext
argument_list|()
operator|.
name|getNamedDispatcher
argument_list|(
name|servletName
argument_list|)
return|;
else|else
return|return
name|request
operator|.
name|getRequestDispatcher
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
end_class

end_unit

