begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|DBBroker
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
name|NodePath
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
name|Signatures
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
name|DOMException
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
name|Document
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

begin_comment
comment|/**  *  The base class for all persistent DOM nodes in the database.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_class
specifier|public
class|class
name|StoredNode
extends|extends
name|NodeImpl
block|{
specifier|public
specifier|final
specifier|static
name|int
name|NODE_IMPL_UNKNOWN_GID
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NODE_IMPL_ROOT_NODE_GID
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|UNKNOWN_NODE_IMPL_ADDRESS
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|short
name|UNKNOWN_NODE_IMPL_NODE_TYPE
init|=
operator|-
literal|1
decl_stmt|;
comment|//TOUNDERSTAND : what are the semantics of this 0 ? -pb
specifier|private
name|long
name|gid
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|internalAddress
init|=
name|UNKNOWN_NODE_IMPL_ADDRESS
decl_stmt|;
specifier|private
name|DocumentImpl
name|ownerDocument
init|=
literal|null
decl_stmt|;
specifier|private
name|short
name|nodeType
init|=
name|UNKNOWN_NODE_IMPL_NODE_TYPE
decl_stmt|;
comment|//Made this constructor protected since we need it from DocumentImpl -pb
specifier|private
name|StoredNode
parameter_list|()
block|{
block|}
specifier|public
name|StoredNode
parameter_list|(
name|short
name|nodeType
parameter_list|)
block|{
comment|//TOUNDERSTAND : what are the semantics of this 0 ? -pb
name|this
argument_list|(
name|nodeType
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StoredNode
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|this
argument_list|(
name|UNKNOWN_NODE_IMPL_NODE_TYPE
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StoredNode
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
comment|/**      * Copy constructor: creates a copy of the other node.      *       * @param other      */
specifier|public
name|StoredNode
parameter_list|(
name|StoredNode
name|other
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|other
operator|.
name|nodeType
expr_stmt|;
name|this
operator|.
name|gid
operator|=
name|other
operator|.
name|gid
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|other
operator|.
name|internalAddress
expr_stmt|;
name|this
operator|.
name|ownerDocument
operator|=
name|other
operator|.
name|ownerDocument
expr_stmt|;
block|}
comment|/**      * Reset this object to its initial state. Required by the      * parser to be able to reuse node objects.      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|//TODO : what are the semantics of this 0 ? -pb
name|this
operator|.
name|gid
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|internalAddress
operator|=
name|UNKNOWN_NODE_IMPL_ADDRESS
expr_stmt|;
name|this
operator|.
name|ownerDocument
operator|=
literal|null
expr_stmt|;
comment|//this.nodeType is *immutable*
block|}
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|INVALID_ACCESS_ERR
argument_list|,
literal|"Can't serialize "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Read a node from the specified byte array.      *       * This checks the node type and calls the {@link #deserialize(byte[], int, int)}      * method of the corresponding node class.      *       * @param data      * @param start      * @param len      * @param doc      * @return      */
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** 	 * Read a node from the specified byte array. 	 *  	 * This checks the node type and calls the {@link #deserialize(byte[], int, int)} 	 * method of the corresponding node class. The node will be allocated in the pool 	 * and should be released once it is no longer needed. 	 *  	 * @param data 	 * @param start 	 * @param len 	 * @param doc 	 * @return 	 */
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|pooled
parameter_list|)
block|{
name|short
name|type
init|=
name|Signatures
operator|.
name|getType
argument_list|(
name|data
index|[
name|start
index|]
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
name|TextImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
return|return
name|ElementImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
name|AttrImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|doc
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
return|return
name|ProcessingInstructionImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|pooled
argument_list|)
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
name|CommentImpl
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|,
name|pooled
argument_list|)
return|;
default|default :
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown node type: "
operator|+
name|type
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
switch|switch
condition|(
name|nodeType
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
return|return
name|QName
operator|.
name|DOCUMENT_QNAME
return|;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
name|QName
operator|.
name|TEXT_QNAME
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
name|QName
operator|.
name|COMMENT_QNAME
return|;
case|case
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
case|:
return|return
name|QName
operator|.
name|DOCTYPE_QNAME
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * @see java.lang.Object#equals(java.lang.Object) 	 */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|StoredNode
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|(
operator|(
name|StoredNode
operator|)
name|obj
operator|)
operator|.
name|getGID
argument_list|()
operator|==
name|getGID
argument_list|()
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|/** 	 * Return the broker instance used to create this node. 	 *  	 * @return 	 */
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
block|{
return|return
operator|(
name|DBBroker
operator|)
name|ownerDocument
operator|.
name|broker
return|;
block|}
comment|/** 	 *  Get the unique identifier assigned to this node. 	 * 	 *@return 	 */
specifier|public
name|long
name|getGID
parameter_list|()
block|{
return|return
name|gid
return|;
block|}
comment|/**      *  Set the unique node identifier of this node.      *      *@param  gid  The new gID value      */
specifier|public
name|void
name|setGID
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|this
operator|.
name|gid
operator|=
name|gid
expr_stmt|;
block|}
comment|/** 	 *  Get the internal storage address of this node 	 * 	 *@return    The internalAddress value 	 */
specifier|public
name|long
name|getInternalAddress
parameter_list|()
block|{
return|return
name|internalAddress
return|;
block|}
comment|/**      *  Set the internal storage address of this node.      *      *@param  address  The new internalAddress value      */
specifier|public
name|void
name|setInternalAddress
parameter_list|(
name|long
name|internalAddress
parameter_list|)
block|{
name|this
operator|.
name|internalAddress
operator|=
name|internalAddress
expr_stmt|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNodeType() 	 */
specifier|public
name|short
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getOwnerDocument() 	 */
specifier|public
name|Document
name|getOwnerDocument
parameter_list|()
block|{
return|return
name|ownerDocument
return|;
block|}
comment|/**      *  Set the owner document.      *      *@param  doc  The new ownerDocument value      */
specifier|public
name|void
name|setOwnerDocument
parameter_list|(
name|Document
name|ownerDocument
parameter_list|)
block|{
name|this
operator|.
name|ownerDocument
operator|=
operator|(
name|DocumentImpl
operator|)
name|ownerDocument
expr_stmt|;
block|}
comment|/** 	 *  Get the unique node identifier of this node's parent node. 	 * 	 *@return    The parentGID value 	 */
specifier|public
name|long
name|getParentGID
parameter_list|()
block|{
return|return
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|ownerDocument
argument_list|,
name|getGID
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|long
name|firstChildID
parameter_list|()
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|INVALID_ACCESS_ERR
argument_list|,
literal|"firstChildID() not implemented in "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getParentNode() 	 */
specifier|public
name|Node
name|getParentNode
parameter_list|()
block|{
name|long
name|pid
init|=
name|getParentGID
argument_list|()
decl_stmt|;
if|if
condition|(
name|pid
operator|==
name|NODE_IMPL_UNKNOWN_GID
condition|)
return|return
literal|null
return|;
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|pid
argument_list|)
return|;
block|}
specifier|protected
name|StoredNode
name|getLastNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
specifier|final
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|ownerDocument
argument_list|,
name|node
operator|.
name|getGID
argument_list|()
argument_list|,
name|node
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
name|iterator
init|=
name|getBroker
argument_list|()
operator|.
name|getNodeIterator
argument_list|(
name|p
argument_list|)
decl_stmt|;
comment|//TODO : hasNext() test ? -pb
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|getLastNode
argument_list|(
name|iterator
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|StoredNode
name|getLastNode
parameter_list|(
name|Iterator
name|iterator
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
return|return
name|node
return|;
specifier|final
name|long
name|firstChild
init|=
name|node
operator|.
name|firstChildID
argument_list|()
decl_stmt|;
specifier|final
name|long
name|lastChild
init|=
name|firstChild
operator|+
name|node
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
name|StoredNode
name|next
init|=
literal|null
decl_stmt|;
for|for
control|(
name|long
name|gid
init|=
name|firstChild
init|;
name|gid
operator|<
name|lastChild
condition|;
name|gid
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|StoredNode
operator|)
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|next
operator|.
name|setGID
argument_list|(
name|gid
argument_list|)
expr_stmt|;
comment|//Recursivity helps taversing...
name|next
operator|=
name|getLastNode
argument_list|(
name|iterator
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getPreviousSibling() 	 */
specifier|public
name|Node
name|getPreviousSibling
parameter_list|()
block|{
name|int
name|level
init|=
name|ownerDocument
operator|.
name|getTreeLevel
argument_list|(
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|0
condition|)
return|return
name|ownerDocument
operator|.
name|getPreviousSibling
argument_list|(
name|this
argument_list|)
return|;
name|long
name|pid
init|=
operator|(
name|getGID
argument_list|()
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
operator|)
operator|/
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//TODO : use XMLUtils routine ? -pb
name|long
name|firstChildId
init|=
operator|(
name|pid
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
operator|)
operator|*
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|getGID
argument_list|()
operator|>
name|firstChildId
condition|)
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|getGID
argument_list|()
operator|-
literal|1
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/** 	 * @see org.w3c.dom.Node#getNextSibling() 	 */
specifier|public
name|Node
name|getNextSibling
parameter_list|()
block|{
name|int
name|level
init|=
name|ownerDocument
operator|.
name|getTreeLevel
argument_list|(
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|0
condition|)
return|return
name|ownerDocument
operator|.
name|getFollowingSibling
argument_list|(
name|this
argument_list|)
return|;
name|long
name|pid
init|=
operator|(
name|getGID
argument_list|()
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
operator|)
operator|/
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//TODO : use XMLUtils routine ? -pb
name|long
name|firstChildId
init|=
operator|(
name|pid
operator|-
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
operator|)
operator|*
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|ownerDocument
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|getGID
argument_list|()
operator|<
name|firstChildId
operator|+
name|ownerDocument
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|-
literal|1
condition|)
return|return
name|ownerDocument
operator|.
name|getNode
argument_list|(
name|getGID
argument_list|()
operator|+
literal|1
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|NodePath
name|getPath
parameter_list|()
block|{
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeType
operator|!=
name|ATTRIBUTE_NODE
condition|)
name|path
operator|.
name|addComponent
argument_list|(
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|NodeImpl
name|parent
init|=
operator|(
name|NodeImpl
operator|)
name|getParentNode
argument_list|()
decl_stmt|;
while|while
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|getNodeType
argument_list|()
operator|!=
name|DOCUMENT_NODE
condition|)
block|{
name|path
operator|.
name|addComponentAtStart
argument_list|(
name|parent
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|=
operator|(
name|NodeImpl
operator|)
name|parent
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|getGID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|top
parameter_list|)
block|{
return|return
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Release all memory resources hold by this node.  	 */
specifier|public
name|void
name|release
parameter_list|()
block|{
name|clear
argument_list|()
expr_stmt|;
name|NodeObjectPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnNode
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

