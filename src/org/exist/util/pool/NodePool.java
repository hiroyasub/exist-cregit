begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|pool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|AttrImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|CDATASectionImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|CommentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|ElementImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|ProcessingInstructionImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|TextImpl
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
name|hashtable
operator|.
name|Int2ObjectHashMap
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
comment|/**  * A pool of node objects. Storing a resource creates many, short-lived DOM node  * objects. To reduce garbage collection, we use a pool to cache a certain number  * of objects.  */
end_comment

begin_class
specifier|public
class|class
name|NodePool
block|{
specifier|public
specifier|final
specifier|static
name|int
name|MAX_OBJECTS
init|=
literal|20
decl_stmt|;
specifier|public
specifier|static
name|NodePool
name|getInstance
parameter_list|()
block|{
return|return
name|pools
operator|.
name|get
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|PoolThreadLocal
extends|extends
name|ThreadLocal
argument_list|<
name|NodePool
argument_list|>
block|{
specifier|protected
name|NodePool
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|NodePool
argument_list|(
name|MAX_OBJECTS
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|NodePool
argument_list|>
name|pools
init|=
operator|new
name|PoolThreadLocal
argument_list|()
decl_stmt|;
specifier|private
name|int
name|maxActive
decl_stmt|;
specifier|private
name|Int2ObjectHashMap
argument_list|<
name|Pool
argument_list|>
name|poolMap
init|=
operator|new
name|Int2ObjectHashMap
argument_list|<
name|Pool
argument_list|>
argument_list|(
literal|17
argument_list|)
decl_stmt|;
specifier|public
name|NodePool
parameter_list|(
name|int
name|maxObjects
parameter_list|)
block|{
name|this
operator|.
name|maxActive
operator|=
name|maxObjects
expr_stmt|;
block|}
specifier|public
name|NodeImpl
name|borrowNode
parameter_list|(
name|short
name|key
parameter_list|)
block|{
name|Pool
name|pool
init|=
operator|(
name|Pool
operator|)
name|poolMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|pool
operator|=
operator|new
name|Pool
argument_list|()
expr_stmt|;
name|poolMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|pool
argument_list|)
expr_stmt|;
block|}
return|return
name|pool
operator|.
name|borrowNode
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|void
name|returnNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
name|Pool
name|pool
init|=
operator|(
name|Pool
operator|)
name|poolMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getNodeType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|returnNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getSize
parameter_list|(
name|short
name|key
parameter_list|)
block|{
name|Pool
name|pool
init|=
operator|(
name|Pool
operator|)
name|poolMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|pool
operator|.
name|stack
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|NodeImpl
name|makeObject
parameter_list|(
name|short
name|key
parameter_list|)
block|{
switch|switch
condition|(
name|key
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
return|return
operator|new
name|ElementImpl
argument_list|()
return|;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
operator|new
name|TextImpl
argument_list|()
return|;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
operator|new
name|AttrImpl
argument_list|()
return|;
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
return|return
operator|new
name|CDATASectionImpl
argument_list|()
return|;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
return|return
operator|new
name|ProcessingInstructionImpl
argument_list|()
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
operator|new
name|CommentImpl
argument_list|()
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to create object of type "
operator|+
name|key
argument_list|)
throw|;
block|}
specifier|private
class|class
name|Pool
block|{
specifier|private
name|LinkedList
argument_list|<
name|NodeImpl
argument_list|>
name|stack
init|=
operator|new
name|LinkedList
argument_list|<
name|NodeImpl
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|NodeImpl
name|borrowNode
parameter_list|(
name|short
name|key
parameter_list|)
block|{
if|if
condition|(
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|makeObject
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
name|stack
operator|.
name|removeLast
argument_list|()
return|;
block|}
specifier|public
name|void
name|returnNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
comment|// Only cache up to maxActive nodes
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|<
name|maxActive
condition|)
name|stack
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

