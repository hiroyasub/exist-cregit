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
operator|.
name|persistent
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
name|INodeHandle
import|;
end_import

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

begin_interface
specifier|public
interface|interface
name|NodeHandle
extends|extends
name|INodeHandle
argument_list|<
name|DocumentImpl
argument_list|>
block|{
specifier|public
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|dln
parameter_list|)
function_decl|;
comment|/**      * Returns the internal storage address of this node in dom.dbx.      *      * @return long      */
specifier|public
name|long
name|getInternalAddress
parameter_list|()
function_decl|;
comment|/**      * Sets the internal storage address of this node in dom.dbx.      *      * @param internalAddress The internalAddress to set      */
specifier|public
name|void
name|setInternalAddress
parameter_list|(
name|long
name|internalAddress
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

