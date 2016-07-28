begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
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
name|util
operator|.
name|serializer
operator|.
name|Receiver
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ChainOfReceivers
extends|extends
name|Receiver
argument_list|<
name|NodeHandle
argument_list|>
block|{
comment|/**      * Register the next receiver in the chain. All      * events should be forwarded to this.      *      * @param next the next receiver in the chain.      */
name|void
name|setNextInChain
parameter_list|(
name|Receiver
name|next
parameter_list|)
function_decl|;
comment|/**      * Returns the next receiver in the chain.      * @return the next receiver      */
name|Receiver
name|getNextInChain
parameter_list|()
function_decl|;
comment|/**      * Walks the chain and returns the final receiver.      * @return the last receiver in the chain      */
name|Receiver
name|getLastInChain
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

