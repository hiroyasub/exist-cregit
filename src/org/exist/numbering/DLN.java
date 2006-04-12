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
comment|/**  * Represents a node id in the form of a dynamic level number (DLN). DLN's are  * hierarchical ids, which borrow from Dewey's decimal classification. Examples for  * node ids: 1, 1.1, 1.2, 1.2.1, 1.2.2, 1.3. In this case, 1 represents the root node, 1.1 is  * the first node on the second level, 1.2 the second, and so on.   */
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
specifier|public
name|DLN
parameter_list|(
name|int
index|[]
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|id
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|id
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|addLevelId
argument_list|(
name|id
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|DLN
parameter_list|(
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
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
return|;
block|}
specifier|public
name|boolean
name|isDescendantOrSelfOf
parameter_list|(
name|NodeId
name|ancestor
parameter_list|)
block|{
return|return
name|startsWith
argument_list|(
operator|(
name|DLN
operator|)
name|ancestor
argument_list|)
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
literal|1
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
name|isSiblingOf
parameter_list|(
name|NodeId
name|sibling
parameter_list|)
block|{
name|DLN
name|other
init|=
operator|(
name|DLN
operator|)
name|sibling
decl_stmt|;
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
name|other
operator|.
name|lastLevelOffset
argument_list|()
condition|)
return|return
name|compareBits
argument_list|(
name|other
argument_list|,
name|last
argument_list|)
return|;
else|else
return|return
name|super
operator|.
name|compareTo
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/**      * Returns the level within the document tree at which      * this node occurs.      *      * @return      */
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
name|NodeId
name|other
parameter_list|)
block|{
return|return
name|super
operator|.
name|compareTo
argument_list|(
name|other
argument_list|)
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
name|writeByte
argument_list|(
operator|(
name|byte
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
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|DLN
name|id0
init|=
operator|new
name|DLN
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
decl_stmt|;
name|DLN
name|id1
init|=
operator|new
name|DLN
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|}
argument_list|)
decl_stmt|;
name|DLN
name|id2
init|=
operator|new
name|DLN
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|1
block|}
argument_list|)
decl_stmt|;
name|DLN
name|id3
init|=
operator|new
name|DLN
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
decl_stmt|;
name|DLN
name|id4
init|=
operator|new
name|DLN
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|1
block|}
argument_list|)
decl_stmt|;
name|DLN
name|id5
init|=
operator|new
name|DLN
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|4
block|,
literal|1
block|}
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|id0
operator|.
name|toString
argument_list|()
operator|+
literal|" sibling of "
operator|+
name|id1
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|id0
operator|.
name|isSiblingOf
argument_list|(
name|id1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|id0
operator|.
name|toString
argument_list|()
operator|+
literal|" sibling of "
operator|+
name|id2
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|id0
operator|.
name|isSiblingOf
argument_list|(
name|id2
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|id2
operator|.
name|toString
argument_list|()
operator|+
literal|" sibling of "
operator|+
name|id3
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|id2
operator|.
name|isSiblingOf
argument_list|(
name|id3
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|id4
operator|.
name|toString
argument_list|()
operator|+
literal|" sibling of "
operator|+
name|id1
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|id4
operator|.
name|isSiblingOf
argument_list|(
name|id1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|id5
operator|.
name|toString
argument_list|()
operator|+
literal|" sibling of "
operator|+
name|id1
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|id5
operator|.
name|isSiblingOf
argument_list|(
name|id1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

