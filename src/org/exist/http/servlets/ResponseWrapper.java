begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2006 The eXist team  *  http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|ResponseWrapper
block|{
comment|/** 	 * @param name	Name of the Cookie 	 * @param value	Value of the Cookie 	 */
specifier|public
name|void
name|addCookie
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
comment|/** 	 * @param name	Name of the Cookie 	 * @param value	Value of the Cookie 	 * @param maxAge maxAge of the Cookie 	 */
specifier|public
name|void
name|addCookie
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|maxAge
parameter_list|)
function_decl|;
comment|/** 	 * @param name	Name of the Cookie 	 * @param value	Value of the Cookie 	 * @param maxAge maxAge of the Cookie 	 * @param secure security of the Cookie 	 */
specifier|public
name|void
name|addCookie
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|maxAge
parameter_list|,
name|boolean
name|secure
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|addDateHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|long
name|arg1
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|addHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|addIntHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 The name of the header. 	 * @return A boolean value indicating whether it contains the header name. 	 */
specifier|public
name|boolean
name|containsHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @return The encoded value 	 */
specifier|public
name|String
name|encodeURL
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
comment|/***/
specifier|public
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** 	 * @return Returns the default character encoding 	 */
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
function_decl|;
comment|/** 	 * @return Returns the default locale 	 */
specifier|public
name|Locale
name|getLocale
parameter_list|()
function_decl|;
comment|/** 	 * @param contentType Content Type of the response 	 */
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|contentType
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|setDateHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|long
name|arg1
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|setHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|setIntHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
function_decl|;
comment|/**      * @param arg0      */
specifier|public
name|void
name|setStatusCode
parameter_list|(
name|int
name|arg0
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 */
specifier|public
name|void
name|setLocale
parameter_list|(
name|Locale
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|void
name|sendRedirect
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @return the value of Date Header corresponding to given name, 	 * 0 if none has been set. */
specifier|public
name|long
name|getDateHeader
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

