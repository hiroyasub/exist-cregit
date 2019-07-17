begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|dom
operator|.
name|persistent
operator|.
name|NodeHandle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_interface
specifier|public
interface|interface
name|IRawNodeIterator
extends|extends
name|Closeable
block|{
comment|/**      * Reposition the iterator to the start of the specified node.      *      * @param node the start node where the iterator will be positioned.      * @throws IOException if an I/O error occurs      */
specifier|public
name|void
name|seek
parameter_list|(
name|NodeHandle
name|node
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the raw data of the next node in document order.      *      * @return the raw data of the node      */
specifier|public
name|Value
name|next
parameter_list|()
function_decl|;
comment|/**      * Close the iterator      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

