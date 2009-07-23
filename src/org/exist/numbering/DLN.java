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
comment|/**  * Represents a node id in the form of a dynamic level number (DLN). DLN's are  * hierarchical ids, which borrow from Dewey's decimal classification. Examples for  * node ids: 1, 1.1, 1.2, 1.2.1, 1.2.2, 1.3. In this case, 1 represents the root node, 1.1 is  * the first node on the second level, 1.2 the second, and so on.  *   * To support efficient insertion of new nodes between existing nodes, we use the  * concept of sublevel ids. Between two nodes 1.1 and 1.2, a new node can be inserted  * as 1.1/1, where the / is the sublevel separator. The / does not start a new level. 1.1 and   * 1.1/1 are thus on the same level of the tree.  *   * In the binary encoding, the '.' is represented by a 0-bit while '/' is written as a 1-bit.  */
end_comment

begin_class
specifier|public
class|class
name|DLN
extends|extends
name|DLNBase
implements|implements
name|NodeId
block|{
comment|/** 	 * Constructs a new DLN with a single id with value 1. 	 * 	 */
specifier|public
name|DLN
parameter_list|()
block|{
name|this
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new DLN by parsing the string argument.      * In the string, levels are separated by a '.', sublevels by      * a '/'. For example, '1.2/1' or '1.2/1.2' are valid ids.      *       * @param s      */
specifier|public
name|DLN
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|bits
operator|=
operator|new
name|byte
index|[
literal|1
index|]
expr_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|boolean
name|subValue
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|p
init|=
literal|0
init|;
name|p
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|p
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'/'
condition|)
block|{
name|addLevelId
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|subValue
argument_list|)
expr_stmt|;
name|subValue
operator|=
name|ch
operator|==
literal|'/'
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|addLevelId
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|subValue
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Constructs a new DLN, using the passed id as its      * single level value.      *       * @param id      */
specifier|public
name|DLN
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|bits
operator|=
operator|new
name|byte
index|[
literal|1
index|]
expr_stmt|;
name|addLevelId
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new DLN by copying the data of the      * passed DLN.      *       * @param other      */
specifier|public
name|DLN
parameter_list|(
name|DLN
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reads a DLN from the given byte[].      *       * @param units number of bits to read      * @param data the byte[] to read from      * @param startOffset the start offset to start reading at      */
specifier|public
name|DLN
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
block|{
name|super
argument_list|(
name|units
argument_list|,
name|data
argument_list|,
name|startOffset
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reads a DLN from the given {@link VariableByteInput} stream.      *       * @see #write(VariableByteOutputStream)      * @param is      * @throws IOException      */
specifier|public
name|DLN
parameter_list|(
name|short
name|bitCnt
parameter_list|,
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|bitCnt
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DLN
parameter_list|(
name|byte
name|prefixLen
parameter_list|,
name|DLN
name|previous
parameter_list|,
name|short
name|bitCnt
parameter_list|,
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|prefixLen
argument_list|,
name|previous
argument_list|,
name|bitCnt
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new DLN by copying nbits bits from the given       * byte[].      *       * @param data      * @param nbits      */
specifier|protected
name|DLN
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|nbits
parameter_list|)
block|{
name|super
argument_list|(
name|data
argument_list|,
name|nbits
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a new DLN representing the first child      * node of this node.      *      * @return new child node id      */
specifier|public
name|NodeId
name|newChild
parameter_list|()
block|{
name|DLN
name|child
init|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|child
operator|.
name|addLevelId
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|child
return|;
block|}
comment|/**      * Returns a new DLN representing the next following      * sibling of this node.      *      * @return new sibling node id.      */
specifier|public
name|NodeId
name|nextSibling
parameter_list|()
block|{
name|DLN
name|sibling
init|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|sibling
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
return|return
name|sibling
return|;
block|}
specifier|public
name|NodeId
name|precedingSibling
parameter_list|()
block|{
name|DLN
name|sibling
init|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|sibling
operator|.
name|decrementLevelId
argument_list|()
expr_stmt|;
return|return
name|sibling
return|;
block|}
specifier|public
name|NodeId
name|getChild
parameter_list|(
name|int
name|child
parameter_list|)
block|{
name|DLN
name|nodeId
init|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|nodeId
operator|.
name|addLevelId
argument_list|(
name|child
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|nodeId
return|;
block|}
specifier|public
name|NodeId
name|insertNode
parameter_list|(
name|NodeId
name|right
parameter_list|)
block|{
name|DLN
name|rightNode
init|=
operator|(
name|DLN
operator|)
name|right
decl_stmt|;
if|if
condition|(
name|right
operator|==
literal|null
condition|)
return|return
name|nextSibling
argument_list|()
return|;
name|int
name|lastLeft
init|=
name|lastLevelOffset
argument_list|()
decl_stmt|;
name|int
name|lastRight
init|=
name|rightNode
operator|.
name|lastLevelOffset
argument_list|()
decl_stmt|;
name|int
name|lenLeft
init|=
name|getSubLevelCount
argument_list|(
name|lastLeft
argument_list|)
decl_stmt|;
name|int
name|lenRight
init|=
name|rightNode
operator|.
name|getSubLevelCount
argument_list|(
name|lastRight
argument_list|)
decl_stmt|;
name|DLN
name|newNode
decl_stmt|;
if|if
condition|(
name|lenLeft
operator|>
name|lenRight
condition|)
block|{
name|newNode
operator|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newNode
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|lenLeft
operator|<
name|lenRight
condition|)
block|{
name|newNode
operator|=
operator|(
name|DLN
operator|)
name|rightNode
operator|.
name|insertBefore
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|newNode
operator|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newNode
operator|.
name|addLevelId
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|newNode
return|;
block|}
specifier|public
name|NodeId
name|insertBefore
parameter_list|()
block|{
name|int
name|lastPos
init|=
name|lastFieldPosition
argument_list|()
decl_stmt|;
name|int
name|lastId
init|=
name|getLevelId
argument_list|(
name|lastPos
argument_list|)
decl_stmt|;
name|DLN
name|newNode
init|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|//        System.out.println("insertBefore: " + newNode.toString() + " = " + newNode.bitIndex);
if|if
condition|(
name|lastId
operator|==
literal|1
condition|)
block|{
name|newNode
operator|.
name|setLevelId
argument_list|(
name|lastPos
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|newNode
operator|.
name|addLevelId
argument_list|(
literal|35
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newNode
operator|.
name|setLevelId
argument_list|(
name|lastPos
argument_list|,
name|lastId
operator|-
literal|1
argument_list|)
expr_stmt|;
name|newNode
operator|.
name|compact
argument_list|()
expr_stmt|;
comment|//            System.out.println("newNode: " + newNode.toString() + " = " + newNode.bitIndex + "; last = " + lastPos);
block|}
return|return
name|newNode
return|;
block|}
specifier|public
name|NodeId
name|append
parameter_list|(
name|NodeId
name|otherId
parameter_list|)
block|{
name|DLN
name|other
init|=
operator|(
name|DLN
operator|)
name|otherId
decl_stmt|;
name|DLN
name|newId
init|=
operator|new
name|DLN
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<=
name|other
operator|.
name|bitIndex
condition|)
block|{
name|boolean
name|subLevel
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
name|subLevel
operator|=
operator|(
operator|(
name|other
operator|.
name|bits
index|[
name|offset
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|offset
operator|++
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|!=
literal|0
operator|)
expr_stmt|;
name|int
name|id
init|=
name|other
operator|.
name|getLevelId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|newId
operator|.
name|addLevelId
argument_list|(
name|id
argument_list|,
name|subLevel
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|DLN
operator|.
name|getUnitsRequired
argument_list|(
name|id
argument_list|)
operator|*
name|BITS_PER_UNIT
expr_stmt|;
block|}
return|return
name|newId
return|;
block|}
comment|/**      * Returns a new DLN representing the parent of the      * current node. If the current node is the root element      * of the document, the method returns       * {@link NodeId#DOCUMENT_NODE}. If the current node      * is the document node, null is returned.      *       * @see NodeId#getParentId()      */
specifier|public
name|NodeId
name|getParentId
parameter_list|()
block|{
if|if
condition|(
name|this
operator|==
name|DOCUMENT_NODE
condition|)
return|return
literal|null
return|;
name|int
name|last
init|=
name|lastLevelOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|==
literal|0
condition|)
return|return
name|DOCUMENT_NODE
return|;
return|return
operator|new
name|DLN
argument_list|(
name|bits
argument_list|,
name|last
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isDescendantOf
parameter_list|(
name|NodeId
name|ancestor
parameter_list|)
block|{
name|DLN
name|other
init|=
operator|(
name|DLN
operator|)
name|ancestor
decl_stmt|;
return|return
name|startsWith
argument_list|(
name|other
argument_list|)
operator|&&
name|bitIndex
operator|>
name|other
operator|.
name|bitIndex
operator|&&
name|isLevelSeparator
argument_list|(
name|other
operator|.
name|bitIndex
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isDescendantOrSelfOf
parameter_list|(
name|NodeId
name|other
parameter_list|)
block|{
name|DLN
name|ancestor
init|=
operator|(
name|DLN
operator|)
name|other
decl_stmt|;
return|return
name|startsWith
argument_list|(
name|ancestor
argument_list|)
operator|&&
operator|(
name|bitIndex
operator|==
name|ancestor
operator|.
name|bitIndex
operator|||
name|isLevelSeparator
argument_list|(
operator|(
name|ancestor
operator|)
operator|.
name|bitIndex
operator|+
literal|1
argument_list|)
operator|)
return|;
block|}
specifier|public
name|boolean
name|isChildOf
parameter_list|(
name|NodeId
name|parent
parameter_list|)
block|{
name|DLN
name|other
init|=
operator|(
name|DLN
operator|)
name|parent
decl_stmt|;
if|if
condition|(
operator|!
name|startsWith
argument_list|(
name|other
argument_list|)
condition|)
return|return
literal|false
return|;
name|int
name|levels
init|=
name|getLevelCount
argument_list|(
name|other
operator|.
name|bitIndex
operator|+
literal|2
argument_list|)
decl_stmt|;
return|return
name|levels
operator|==
literal|1
return|;
block|}
specifier|public
name|int
name|computeRelation
parameter_list|(
name|NodeId
name|ancestor
parameter_list|)
block|{
name|DLN
name|other
init|=
operator|(
name|DLN
operator|)
name|ancestor
decl_stmt|;
if|if
condition|(
name|other
operator|==
name|NodeId
operator|.
name|DOCUMENT_NODE
condition|)
return|return
name|getLevelCount
argument_list|(
literal|0
argument_list|)
operator|==
literal|1
condition|?
name|IS_CHILD
else|:
name|IS_DESCENDANT
return|;
if|if
condition|(
name|startsWith
argument_list|(
name|other
argument_list|)
condition|)
block|{
if|if
condition|(
name|bitIndex
operator|==
name|other
operator|.
name|bitIndex
condition|)
return|return
name|IS_SELF
return|;
if|if
condition|(
name|bitIndex
operator|>
name|other
operator|.
name|bitIndex
operator|&&
name|isLevelSeparator
argument_list|(
name|other
operator|.
name|bitIndex
operator|+
literal|1
argument_list|)
condition|)
block|{
if|if
condition|(
name|getLevelCount
argument_list|(
name|other
operator|.
name|bitIndex
operator|+
literal|2
argument_list|)
operator|==
literal|1
condition|)
return|return
name|IS_CHILD
return|;
return|return
name|IS_DESCENDANT
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|boolean
name|isSiblingOf
parameter_list|(
name|NodeId
name|sibling
parameter_list|)
block|{
comment|//DLN other = (DLN) sibling;
name|NodeId
name|parent
init|=
name|getParentId
argument_list|()
decl_stmt|;
return|return
name|sibling
operator|.
name|isChildOf
argument_list|(
name|parent
argument_list|)
return|;
block|}
comment|/**      * Returns the level within the document tree at which      * this node occurs.      */
specifier|public
name|int
name|getTreeLevel
parameter_list|()
block|{
return|return
name|getLevelCount
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|NodeId
name|other
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
operator|(
name|DLNBase
operator|)
name|other
argument_list|)
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|DLN
operator|)
name|other
argument_list|)
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|NodeId
name|otherId
parameter_list|)
block|{
if|if
condition|(
name|otherId
operator|==
literal|null
condition|)
return|return
literal|1
return|;
specifier|final
name|DLN
name|other
init|=
operator|(
name|DLN
operator|)
name|otherId
decl_stmt|;
specifier|final
name|int
name|a1len
init|=
name|bits
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|a2len
init|=
name|other
operator|.
name|bits
operator|.
name|length
decl_stmt|;
name|int
name|limit
init|=
name|a1len
operator|<=
name|a2len
condition|?
name|a1len
else|:
name|a2len
decl_stmt|;
name|byte
index|[]
name|obits
init|=
name|other
operator|.
name|bits
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b1
init|=
name|bits
index|[
name|i
index|]
decl_stmt|;
name|byte
name|b2
init|=
name|obits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b1
operator|!=
name|b2
condition|)
return|return
operator|(
name|b1
operator|&
literal|0xFF
operator|)
operator|-
operator|(
name|b2
operator|&
literal|0xFF
operator|)
return|;
block|}
return|return
operator|(
name|a1len
operator|-
name|a2len
operator|)
return|;
block|}
specifier|public
name|boolean
name|after
parameter_list|(
name|NodeId
name|other
parameter_list|,
name|boolean
name|isFollowing
parameter_list|)
block|{
if|if
condition|(
name|compareTo
argument_list|(
name|other
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|isFollowing
condition|)
return|return
operator|!
name|isDescendantOf
argument_list|(
name|other
argument_list|)
return|;
else|else
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|before
parameter_list|(
name|NodeId
name|other
parameter_list|,
name|boolean
name|isPreceding
parameter_list|)
block|{
if|if
condition|(
name|compareTo
argument_list|(
name|other
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|isPreceding
condition|)
return|return
operator|!
name|other
operator|.
name|isDescendantOf
argument_list|(
name|this
argument_list|)
return|;
else|else
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Write the node id to a {@link VariableByteOutputStream}.      *      * @param os      * @throws IOException      */
specifier|public
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|units
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeId
name|write
parameter_list|(
name|NodeId
name|prevId
parameter_list|,
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
comment|//        if (prevId == null) {
comment|//            write(os);
comment|//            return this;
comment|//        }
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|prevId
operator|!=
literal|null
condition|)
block|{
name|DLN
name|previous
init|=
operator|(
name|DLN
operator|)
name|prevId
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|bits
operator|.
name|length
argument_list|,
name|previous
operator|.
name|bits
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|bits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|!=
name|previous
operator|.
name|bits
index|[
name|i
index|]
condition|)
break|break;
block|}
block|}
name|os
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|units
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|bits
argument_list|,
name|i
argument_list|,
name|bits
operator|.
name|length
operator|-
name|i
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|DLN
name|left
init|=
operator|new
name|DLN
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|DLN
name|right
init|=
operator|new
name|DLN
argument_list|(
literal|"2.3.12"
argument_list|)
decl_stmt|;
name|NodeId
name|r
init|=
name|left
operator|.
name|append
argument_list|(
name|right
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"r = "
operator|+
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

