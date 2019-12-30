begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a remote method call site.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|RemoteCallSite
block|{
comment|/**      * Executes the given {@code methodName} with the given {@code parameters} and returns the result. In case of      * an error a {@link XMLDBException} will be thrown.      *      * @param methodName the method name to be invoked      * @param parameters the list of method arguments      * @return the result of the method      * @throws XMLDBException in case of the invocation fails      */
name|Object
name|execute
parameter_list|(
name|String
name|methodName
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

