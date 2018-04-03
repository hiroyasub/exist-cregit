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
name|ServletConfig
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_class
specifier|public
class|class
name|PassThrough
extends|extends
name|Forward
block|{
specifier|private
specifier|final
name|ServletConfig
name|servletConfig
decl_stmt|;
specifier|public
name|PassThrough
parameter_list|(
specifier|final
name|ServletConfig
name|servletConfig
parameter_list|,
specifier|final
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|servletConfig
operator|=
name|servletConfig
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|request
operator|.
name|getRequestURI
argument_list|()
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PassThrough
parameter_list|(
specifier|final
name|ServletConfig
name|servletConfig
parameter_list|,
specifier|final
name|Element
name|config
parameter_list|,
specifier|final
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|,
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|servletConfig
operator|=
name|servletConfig
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|request
operator|.
name|getRequestURI
argument_list|()
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PassThrough
parameter_list|(
specifier|final
name|PassThrough
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|servletConfig
operator|=
name|other
operator|.
name|servletConfig
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RequestDispatcher
name|getRequestDispatcher
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|)
block|{
comment|// always forward to the servlet engine's default servlet
return|return
name|servletConfig
operator|.
name|getServletContext
argument_list|()
operator|.
name|getNamedDispatcher
argument_list|(
literal|"default"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|URLRewrite
name|copy
parameter_list|()
block|{
return|return
operator|new
name|PassThrough
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

