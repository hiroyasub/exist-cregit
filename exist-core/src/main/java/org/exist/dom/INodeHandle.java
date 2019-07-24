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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_comment
comment|/**  * Interface for handling Nodes in eXist  * used for both persistent and  * in-memory nodes.  *   * @param<D> The type of the persistent  * or in-memory document  *   * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|INodeHandle
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
parameter_list|>
block|{
comment|/**      * Get the ID of the Node      *       * @return The ID of the Node      */
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
comment|/**      * Get the type of the node      * @return the type of the node as short value      */
specifier|public
name|short
name|getNodeType
parameter_list|()
function_decl|;
comment|//TODO convert to enum? what about persistence of the enum id (if it is ever persisted?)?
comment|/**      * @see org.w3c.dom.Node#getOwnerDocument()      *       * @return The persistent Owner Document      */
specifier|public
name|D
name|getOwnerDocument
parameter_list|()
function_decl|;
comment|//TODO consider extracting D into "org.exist.dom.IDocument extends org.w3c.com.Document" and returning an IDocument here
block|}
end_interface

end_unit

