begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_comment
comment|/**  * Interface for Nodes in eXist  * used for both persistent and  * in-memory nodes.  *   * @param<T> The type of the persistent  * or in-memory document  *   * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|INode
parameter_list|<
name|D
extends|extends
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
parameter_list|,
name|T
extends|extends
name|INode
parameter_list|>
extends|extends
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
extends|,
name|INodeHandle
argument_list|<
name|D
argument_list|>
extends|,
name|Comparable
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * The node is a<code>Namespace</code>.      */
specifier|public
specifier|static
specifier|final
name|short
name|NAMESPACE_NODE
init|=
literal|13
decl_stmt|;
comment|/**      * Get the qualified name of the Node      *       * @return The qualified name of the Node      */
specifier|public
name|QName
name|getQName
parameter_list|()
function_decl|;
comment|//TODO try and get rid of this after decoupling nameTyping from QName class (AR)?
specifier|public
name|void
name|setQName
parameter_list|(
name|QName
name|qname
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

