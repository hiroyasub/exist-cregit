begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|boolean
name|containsHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|String
name|encodeURL
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
function_decl|;
comment|/** 	 * @return 	 */
specifier|public
name|Locale
name|getLocale
parameter_list|()
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
block|}
end_interface

end_unit

