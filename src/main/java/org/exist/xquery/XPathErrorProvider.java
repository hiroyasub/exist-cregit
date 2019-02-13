begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * An interface for providing XPath Error Codes  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|XPathErrorProvider
block|{
comment|/**      * Gets the XPath Error code      *      * @return The error code or {@link ErrorCodes#ERROR} if there is no defined error code      */
name|ErrorCodes
operator|.
name|ErrorCode
name|getErrorCode
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

