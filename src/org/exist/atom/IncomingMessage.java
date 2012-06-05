begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2006-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
package|;
end_package

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  *   * @author R. Alexander Milowski  */
end_comment

begin_interface
specifier|public
interface|interface
name|IncomingMessage
block|{
name|String
name|getMethod
parameter_list|()
function_decl|;
name|String
name|getPath
parameter_list|()
function_decl|;
name|String
name|getHeader
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|long
name|getContentLength
parameter_list|()
function_decl|;
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|String
name|getModuleBase
parameter_list|()
function_decl|;
name|HttpServletRequest
name|getRequest
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

