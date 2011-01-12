begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
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
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_comment
comment|/**  * The interface<code>NodeTest</code>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeTest
block|{
comment|/**      * The method<code>setType</code>      *      * @param nodeType an<code>int</code> value      */
specifier|public
name|void
name|setType
parameter_list|(
name|int
name|nodeType
parameter_list|)
function_decl|;
comment|/**      * The method<code>getType</code>      *      * @return an<code>int</code> value      */
specifier|public
name|int
name|getType
parameter_list|()
function_decl|;
comment|/**      * The method<code>matches</code>      *      * @param proxy a<code>NodeProxy</code> value      * @return a<code>boolean</code> value      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
comment|/**      * The method<code>matches</code>      *      * @param node a<code>Node</code> value      * @return a<code>boolean</code> value      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|Node
name|node
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|matches
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|)
function_decl|;
comment|/**      * The method<code>isWildcardTest</code>      *      * @return a<code>boolean</code> value      */
specifier|public
name|boolean
name|isWildcardTest
parameter_list|()
function_decl|;
comment|/**      * The method<code>getName</code>      *      * @return a<code>QName</code> value      */
specifier|public
name|QName
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

