begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-01,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public  * License along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id:  */
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
name|util
operator|.
name|LongLinkedList
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
name|XMLUtil
import|;
end_import

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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
comment|/**  * Base class for all node set implementations returned by most  * xpath expressions. It implements NodeList plus some additional  * methods needed by the xpath engine.  *  * There are three classes extending NodeSet: NodeIDSet, ArraySet  * and VirtualNodeSet. Depending on the context each of these  * implementations has its advantages and drawbacks. ArraySet  * uses a sorted array and binary search, while NodeIDSet is based  * on a HashSet. VirtualNodeSet is specifically used for steps like  * descendant::* etc..  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NodeSet
implements|implements
name|NodeList
block|{
specifier|public
specifier|final
specifier|static
name|int
name|ANCESTOR
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DESCENDANT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PRECEDING
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FOLLOWING
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
name|NodeSet
name|EMPTY_SET
init|=
operator|new
name|EmptyNodeSet
argument_list|()
decl_stmt|;
specifier|public
specifier|abstract
name|Iterator
name|iterator
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
if|if
condition|(
operator|(
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|doc
operator|==
name|doc
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeList
name|other
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
specifier|abstract
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
specifier|public
name|void
name|remove
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
specifier|public
specifier|abstract
name|int
name|getLength
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
function_decl|;
specifier|public
name|NodeProxy
name|nodeHasParent
parameter_list|(
name|NodeProxy
name|p
parameter_list|,
name|boolean
name|directParent
parameter_list|)
block|{
return|return
name|nodeHasParent
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|,
name|directParent
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|nodeHasParent
parameter_list|(
name|NodeProxy
name|p
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
name|nodeHasParent
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|nodeHasParent
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
name|nodeHasParent
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** 	 * Check if node has a parent contained in this node set. 	 * 	 * If directParent is true, only immediate ancestors are considered. 	 * Otherwise the method will call itself recursively for the node's 	 * parents. 	 * 	 * If includeSelf is true, the method returns also true if 	 * the node itself is contained in the node set. 	 */
specifier|public
name|NodeProxy
name|nodeHasParent
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|gid
operator|<
literal|1
condition|)
return|return
literal|null
return|;
name|NodeProxy
name|parent
decl_stmt|;
if|if
condition|(
name|includeSelf
operator|&&
operator|(
name|parent
operator|=
name|get
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
operator|)
operator|!=
literal|null
condition|)
return|return
name|parent
return|;
if|if
condition|(
name|level
operator|<
literal|0
condition|)
name|level
operator|=
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|gid
argument_list|)
expr_stmt|;
comment|// calculate parent's gid
name|long
name|pid
init|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
decl_stmt|;
name|includeSelf
operator|=
literal|false
expr_stmt|;
if|if
condition|(
operator|(
name|parent
operator|=
name|get
argument_list|(
name|doc
argument_list|,
name|pid
argument_list|)
operator|)
operator|!=
literal|null
condition|)
return|return
name|parent
return|;
if|else if
condition|(
name|directParent
condition|)
return|return
literal|null
return|;
else|else
return|return
name|nodeHasParent
argument_list|(
name|doc
argument_list|,
name|pid
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|,
name|level
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|ArraySet
name|getChildren
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
return|return
name|getChildren
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|ArraySet
name|getChildren
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|,
name|boolean
name|rememberContext
parameter_list|)
block|{
name|NodeProxy
name|n
decl_stmt|,
name|p
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|DESCENDANT
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|p
operator|=
name|al
operator|.
name|nodeHasParent
argument_list|(
name|n
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rememberContext
condition|)
name|n
operator|.
name|addContextNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
else|else
name|n
operator|.
name|copyContext
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|ANCESTOR
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|=
name|al
operator|.
name|parentWithChild
argument_list|(
name|n
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rememberContext
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|NodeSet
name|getDescendants
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
return|return
name|getDescendants
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|getDescendants
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
name|getDescendants
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
name|includeSelf
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** 	 *  For a given set of potential ancestor nodes, get the 	 * descendants in this node set 	 * 	 *@param  al    node set containing potential ancestors 	 *@param  mode  determines if either the ancestor or the descendant 	 * nodes should be returned. Possible values are ANCESTOR or DESCENDANT. 	 *@return 	 */
specifier|public
name|NodeSet
name|getDescendants
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|boolean
name|rememberContext
parameter_list|)
block|{
name|NodeProxy
name|n
decl_stmt|,
name|p
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|DESCENDANT
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|p
operator|=
name|al
operator|.
name|nodeHasParent
argument_list|(
name|n
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rememberContext
condition|)
name|n
operator|.
name|addContextNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
else|else
name|n
operator|.
name|copyContext
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|ANCESTOR
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|=
name|al
operator|.
name|parentWithChild
argument_list|(
name|n
operator|.
name|doc
argument_list|,
name|n
operator|.
name|gid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rememberContext
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
return|return
name|result
return|;
block|}
comment|/** 		 *  For a given set of potential ancestor nodes, get the 		 * descendants in this node set 		 * 		 *@param  al    node set containing potential ancestors 		 *@param  mode  determines if either the ancestor or the descendant 		 * nodes should be returned. Possible values are ANCESTOR or DESCENDANT. 		 *@return 		 */
specifier|public
name|NodeSet
name|getAncestors
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|boolean
name|rememberContext
parameter_list|)
block|{
name|NodeProxy
name|n
decl_stmt|,
name|p
decl_stmt|,
name|temp
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|al
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|=
name|al
operator|.
name|parentWithChild
argument_list|(
name|n
operator|.
name|doc
argument_list|,
name|n
operator|.
name|gid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
name|temp
operator|=
name|result
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|rememberContext
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|rememberContext
condition|)
name|temp
operator|.
name|addContextNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Select all nodes from the passed node set, which 	 * are preceding or following siblings of the nodes in 	 * this set. 	 *  	 * @param siblings a node set containing potential siblings 	 * @param mode either FOLLOWING or PRECEDING 	 * @return 	 */
specifier|public
name|NodeSet
name|getSiblings
parameter_list|(
name|NodeSet
name|siblings
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
if|if
condition|(
name|siblings
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|||
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|Iterator
name|ia
init|=
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
name|ib
init|=
name|siblings
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|NodeProxy
name|na
init|=
operator|(
name|NodeProxy
operator|)
name|ia
operator|.
name|next
argument_list|()
decl_stmt|,
name|nb
init|=
operator|(
name|NodeProxy
operator|)
name|ib
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|pa
decl_stmt|,
name|pb
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// first, try to find nodes belonging to the same doc
if|if
condition|(
name|na
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|<
name|nb
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
condition|)
block|{
if|if
condition|(
name|ia
operator|.
name|hasNext
argument_list|()
condition|)
name|na
operator|=
operator|(
name|NodeProxy
operator|)
name|ia
operator|.
name|next
argument_list|()
expr_stmt|;
else|else
break|break;
block|}
if|else if
condition|(
name|na
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|>
name|nb
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
condition|)
block|{
if|if
condition|(
name|ib
operator|.
name|hasNext
argument_list|()
condition|)
name|nb
operator|=
operator|(
name|NodeProxy
operator|)
name|ib
operator|.
name|next
argument_list|()
expr_stmt|;
else|else
break|break;
block|}
else|else
block|{
comment|// same document: check if the nodes have the same parent
name|pa
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|na
operator|.
name|doc
argument_list|,
name|na
operator|.
name|gid
argument_list|)
expr_stmt|;
name|pb
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|nb
operator|.
name|doc
argument_list|,
name|nb
operator|.
name|gid
argument_list|)
expr_stmt|;
if|if
condition|(
name|pa
operator|<
name|pb
condition|)
block|{
comment|// wrong parent: proceed
if|if
condition|(
name|ia
operator|.
name|hasNext
argument_list|()
condition|)
name|na
operator|=
operator|(
name|NodeProxy
operator|)
name|ia
operator|.
name|next
argument_list|()
expr_stmt|;
else|else
break|break;
block|}
if|else if
condition|(
name|pa
operator|>
name|pb
condition|)
block|{
comment|// wrong parent: proceed
if|if
condition|(
name|ib
operator|.
name|hasNext
argument_list|()
condition|)
name|nb
operator|=
operator|(
name|NodeProxy
operator|)
name|ib
operator|.
name|next
argument_list|()
expr_stmt|;
else|else
break|break;
block|}
else|else
block|{
comment|// found two nodes with the same parent
comment|// now, compare the ids: a node is a following sibling
comment|// if its id is greater than the id of the other node
if|if
condition|(
name|nb
operator|.
name|gid
operator|<
name|na
operator|.
name|gid
condition|)
block|{
comment|// found a preceding sibling
if|if
condition|(
name|mode
operator|==
name|PRECEDING
condition|)
name|result
operator|.
name|add
argument_list|(
name|nb
argument_list|)
expr_stmt|;
if|if
condition|(
name|ib
operator|.
name|hasNext
argument_list|()
condition|)
name|nb
operator|=
operator|(
name|NodeProxy
operator|)
name|ib
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|nb
operator|.
name|gid
operator|>
name|na
operator|.
name|gid
condition|)
block|{
comment|// found a following sibling
if|if
condition|(
name|mode
operator|==
name|FOLLOWING
condition|)
name|result
operator|.
name|add
argument_list|(
name|nb
argument_list|)
expr_stmt|;
if|if
condition|(
name|ib
operator|.
name|hasNext
argument_list|()
condition|)
name|nb
operator|=
operator|(
name|NodeProxy
operator|)
name|ib
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// equal nodes: proceed with next node
block|}
if|else if
condition|(
name|ib
operator|.
name|hasNext
argument_list|()
condition|)
name|nb
operator|=
operator|(
name|NodeProxy
operator|)
name|ib
operator|.
name|next
argument_list|()
expr_stmt|;
else|else
break|break;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Search for a node contained in this node set, which is an 	 * ancestor of the argument node. 	 * If directParent is true, only immediate ancestors are considered. 	 */
specifier|public
name|NodeProxy
name|parentWithChild
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|directParent
parameter_list|)
block|{
return|return
name|parentWithChild
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|directParent
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|parentWithChild
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
name|parentWithChild
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** 	 * Search for a node contained in this node set, which is an 	 * ancestor of the argument node. 	 * If directParent is true, only immediate ancestors are considered. 	 * If includeSelf is true, the method returns true even if 	 * the node itself is contained in the node set. 	 */
specifier|protected
name|NodeProxy
name|parentWithChild
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|gid
operator|<
literal|1
condition|)
return|return
literal|null
return|;
name|NodeProxy
name|temp
decl_stmt|;
if|if
condition|(
name|includeSelf
operator|&&
operator|(
name|temp
operator|=
name|get
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
operator|)
operator|!=
literal|null
condition|)
return|return
name|temp
return|;
if|if
condition|(
name|level
operator|<
literal|0
condition|)
name|level
operator|=
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|gid
argument_list|)
expr_stmt|;
comment|// calculate parent's gid
name|long
name|pid
init|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|temp
operator|=
name|get
argument_list|(
name|doc
argument_list|,
name|pid
argument_list|)
operator|)
operator|!=
literal|null
condition|)
return|return
name|temp
return|;
if|else if
condition|(
name|directParent
condition|)
return|return
literal|null
return|;
else|else
return|return
name|parentWithChild
argument_list|(
name|doc
argument_list|,
name|pid
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|,
name|level
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|parentWithChild
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
name|parentWithChild
argument_list|(
name|proxy
operator|.
name|doc
argument_list|,
name|proxy
operator|.
name|gid
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|getParents
parameter_list|()
block|{
name|ArraySet
name|parents
init|=
operator|new
name|ArraySet
argument_list|(
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|long
name|pid
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// calculate parent's gid
name|pid
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|)
expr_stmt|;
if|if
condition|(
name|pid
operator|>
operator|-
literal|1
condition|)
name|parents
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|p
operator|.
name|doc
argument_list|,
name|pid
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|parents
return|;
block|}
specifier|public
name|boolean
name|hasIndex
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|hasIndex
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|NodeSet
name|getRange
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|lower
parameter_list|,
name|long
name|upper
parameter_list|)
block|{
name|NodeProxy
name|p
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|doc
operator|.
name|docId
operator|==
name|doc
operator|.
name|docId
operator|&&
name|p
operator|.
name|gid
operator|>=
name|lower
operator|&&
name|p
operator|.
name|gid
operator|<=
name|upper
condition|)
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|NodeSet
name|intersection
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|NodeIDSet
name|r
init|=
operator|new
name|NodeIDSet
argument_list|()
decl_stmt|;
name|NodeProxy
name|l
decl_stmt|,
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|l
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|contains
argument_list|(
name|l
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
name|i
init|=
name|other
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|l
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|l
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|p
operator|=
name|r
operator|.
name|get
argument_list|(
name|l
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|addMatches
argument_list|(
name|l
operator|.
name|matches
argument_list|)
expr_stmt|;
block|}
else|else
name|r
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
specifier|public
name|NodeSet
name|union
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|getLength
argument_list|()
operator|+
name|other
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|NodeProxy
name|p
decl_stmt|,
name|c
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|contains
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|c
operator|=
name|other
operator|.
name|get
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|c
operator|.
name|addMatches
argument_list|(
name|p
operator|.
name|matches
argument_list|)
expr_stmt|;
block|}
else|else
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|NodeSet
name|getContextNodes
parameter_list|(
name|NodeSet
name|contextNodes
parameter_list|,
name|boolean
name|rememberContext
parameter_list|)
block|{
name|NodeSet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|NodeProxy
name|current
decl_stmt|,
name|context
decl_stmt|;
name|LongLinkedList
name|contextList
decl_stmt|;
name|LongLinkedList
operator|.
name|ListItem
name|item
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|contextList
operator|=
name|current
operator|.
name|getContext
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|contextList
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|item
operator|=
operator|(
name|LongLinkedList
operator|.
name|ListItem
operator|)
name|j
operator|.
name|next
argument_list|()
expr_stmt|;
name|context
operator|=
name|contextNodes
operator|.
name|get
argument_list|(
name|current
operator|.
name|doc
argument_list|,
name|item
operator|.
name|l
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|result
operator|.
name|contains
argument_list|(
name|context
argument_list|)
condition|)
block|{
if|if
condition|(
name|rememberContext
condition|)
block|{
name|context
operator|.
name|addContextNode
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|addMatches
argument_list|(
name|current
operator|.
name|matches
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

