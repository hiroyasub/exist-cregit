begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|io
operator|.
name|VariableByteInput
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
name|io
operator|.
name|VariableByteOutputStream
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

begin_comment
comment|/**  * A factory for creating node ids. To support different numbering  * schemes, NodeId instances should always be created through  * this interface.  *  * The NodeIdFactory for the current database instance can be  * retrieved from {@link org.exist.storage.BrokerPool#getNodeFactory()}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeIdFactory
block|{
comment|/**      * Create a new NodeId, initialized with a default      * value.      *      * @return a new NodeId.      */
name|NodeId
name|createInstance
parameter_list|()
function_decl|;
comment|/**      * Create a new NodeId, initialized with the given      * base id.      *       * @param id      * @return nodeId      */
name|NodeId
name|createInstance
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
comment|/**      * Read a NodeId from the given input stream.      *      * @see NodeId#write(org.exist.storage.io.VariableByteOutputStream)      *      * @param is the input stream to read from      * @return the NodeId read      * @throws IOException if there's a problem with the underlying input stream      */
name|NodeId
name|createFromStream
parameter_list|(
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Read a NodeId from the given input stream. Assumes that the node id was      * stored with prefix-compression, i.e. only the bytes differing from the previous      * node were written out.      *      * @see NodeId#write(NodeId, org.exist.storage.io.VariableByteOutputStream)      *       * @param previous the previous node id read or null if there is none      * @param is the input stream to read from      * @return the NodeId read      * @throws IOException if there's a problem with the underlying input stream      */
name|NodeId
name|createFromStream
parameter_list|(
name|NodeId
name|previous
parameter_list|,
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Read a NodeId from the given byte array. Start to read at      * startOffset. sizeHint indicates the length of the id in an      * implementation dependent manner. Some implementations      * may require sizeHint to be specified, others not.      *      * @param sizeHint a hint about the expected length of the id      * @param data the byte array to read from      * @param startOffset offset into the byte array      * @return the NodeId read      */
name|NodeId
name|createFromData
parameter_list|(
name|int
name|sizeHint
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|startOffset
parameter_list|)
function_decl|;
comment|/**      * Create a NodeId instance from its string representation.      *       * @param string the string representation of the node id as returned      * by {@link Object#toString()}      * @return nodeId      */
name|NodeId
name|createFromString
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
comment|/**      * Returns the number of bytes occupied by the NodeId stored      * in the byte array at the given startOffset. This method is      * similar to {@link #createFromData(int, byte[], int)}, but it      * just returns the number of bytes.      *      * @param units      * @param data      * @param startOffset      * @return number of bytes      */
name|int
name|lengthInBytes
parameter_list|(
name|int
name|units
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|startOffset
parameter_list|)
function_decl|;
comment|/**      * Returns a NodeId representing the document node of a document.      * Usually, this will be a singleton object.      *      * @return the document node id.      */
name|NodeId
name|documentNodeId
parameter_list|()
function_decl|;
name|void
name|writeEndOfDocument
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

