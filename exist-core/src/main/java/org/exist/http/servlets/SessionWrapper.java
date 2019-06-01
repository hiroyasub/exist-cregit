begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
operator|.
name|Enumeration
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|SessionWrapper
block|{
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getAttributeNames
parameter_list|()
function_decl|;
specifier|public
name|long
name|getCreationTime
parameter_list|()
function_decl|;
specifier|public
name|String
name|getId
parameter_list|()
function_decl|;
specifier|public
name|long
name|getLastAccessedTime
parameter_list|()
function_decl|;
specifier|public
name|int
name|getMaxInactiveInterval
parameter_list|()
function_decl|;
specifier|public
name|void
name|invalidate
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isNew
parameter_list|()
function_decl|;
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
function_decl|;
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
function_decl|;
specifier|public
name|void
name|setMaxInactiveInterval
parameter_list|(
name|int
name|arg0
parameter_list|)
function_decl|;
block|}
end_interface

end_unit
