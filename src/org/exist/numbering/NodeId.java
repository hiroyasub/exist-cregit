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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * Represents the internal id of a node within eXist. Basically, all  * stored nodes in eXist need to have an id that implements this  * interface. The id will be assigned according to used numbering  * scheme. From a given id, we can determine the relationship  * of the node it represents to any other node in the same document.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeId
extends|extends
name|Comparable
block|{
comment|/**      * Static field representing the document node.      */
specifier|public
specifier|final
specifier|static
name|NodeId
name|DOCUMENT_NODE
init|=
operator|new
name|DLN
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**      * Returns a new NodeId representing the first child      * node of this node. The returned id can be used      * for creating new nodes. The actual id of the first      * node might be different, depending on the      * implementation.      *      * @return new child node id      */
name|NodeId
name|newChild
parameter_list|()
function_decl|;
comment|/**      * Returns a new NodeId representing the next following      * sibling of this node. The returned id can be used      * to create new sibling nodes. The actual id of the      * next sibling might be different, depending on the      * implementation.      *      * @return new sibling node id.      */
name|NodeId
name|nextSibling
parameter_list|()
function_decl|;
name|NodeId
name|insertNode
parameter_list|(
name|NodeId
name|right
parameter_list|)
function_decl|;
name|NodeId
name|insertBefore
parameter_list|()
function_decl|;
comment|/**      * Returns a new NodeId representing the parent      * of the current node. If the parent is the document,      * the constant {@link #DOCUMENT_NODE} will be returned.      * For the document itself, the parent id will be null.      *      * @return the id of the parent node or null if the current node      * is the document node.      */
name|NodeId
name|getParentId
parameter_list|()
function_decl|;
comment|/**      * Returns true if the node represented by this node id comes      * after the argument node in document order. If isFollowing is set to true, the method      * behaves as if called to evaluate a following::* XPath select, i.e. it      * returns false for descendants of the current node.       *        * @param other      * @param isFollowing      * @return      */
name|boolean
name|after
parameter_list|(
name|NodeId
name|other
parameter_list|,
name|boolean
name|isFollowing
parameter_list|)
function_decl|;
comment|/**      * Returns true if the node represented by this node id comes      * before the argument node in document order. If isPreceding is set to true, the method      * behaves as if called to evaluate a preceding::* XPath select, i.e. it      * returns false for ancestors of the current node.       *        * @param other      * @param isFollowing      * @return      */
name|boolean
name|before
parameter_list|(
name|NodeId
name|other
parameter_list|,
name|boolean
name|isPreceding
parameter_list|)
function_decl|;
comment|/**      * Is the current node id a descendant of the specified node?      *      * @param ancestor node id of the potential ancestor      * @return true if the node id is a descendant of the given node, false otherwise      */
name|boolean
name|isDescendantOf
parameter_list|(
name|NodeId
name|ancestor
parameter_list|)
function_decl|;
name|boolean
name|isDescendantOrSelfOf
parameter_list|(
name|NodeId
name|ancestor
parameter_list|)
function_decl|;
comment|/**      * Is the current node a child node of the specified parent?      *       * @param parent the parent node      * @return      */
name|boolean
name|isChildOf
parameter_list|(
name|NodeId
name|parent
parameter_list|)
function_decl|;
specifier|public
name|int
name|isSiblingOf
parameter_list|(
name|NodeId
name|sibling
parameter_list|)
function_decl|;
comment|/**      * Returns the level within the document tree at which      * this node occurs.      *      * @return      */
name|int
name|getTreeLevel
parameter_list|()
function_decl|;
name|int
name|compareTo
parameter_list|(
name|NodeId
name|other
parameter_list|)
function_decl|;
name|boolean
name|equals
parameter_list|(
name|NodeId
name|other
parameter_list|)
function_decl|;
comment|/**      * Returns the size (in bytes) of this node id. Depends on      * the concrete implementation.      *      * @return      */
name|int
name|size
parameter_list|()
function_decl|;
name|int
name|units
parameter_list|()
function_decl|;
comment|/**      * Serializes the node id to an array of bytes. The first byte is      * written at offset.      *      * @param data the byte array to be filled      * @param offset offset into the array      */
name|void
name|serialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
function_decl|;
comment|/**      * Write the node id to a {@link org.exist.storage.io.VariableByteOutputStream}.      *      * @param os      * @throws java.io.IOException      */
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

