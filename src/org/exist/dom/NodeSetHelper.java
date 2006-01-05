begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|xquery
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_comment
comment|/**  * Collection of static methods operating on node sets.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|NodeSetHelper
block|{
comment|/** 	 * For two given sets of potential parent and child nodes, find 	 * those nodes from the child set that actually have parents in the 	 * parent set, i.e. the parent-child relationship is true. 	 *  	 * The method returns either the matching descendant or ancestor nodes, 	 * depending on the mode constant. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all child nodes found in this node set for each parent node. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those parent nodes, 	 * for which children have been found. 	 *   	 * @param dl a node set containing potential child nodes 	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 * @param rememberContext if true, add the matching nodes to the context node 	 * list of each returned node (this is used to track matches for predicate evaluation) 	 * @return 	 */
specifier|public
specifier|static
name|NodeSet
name|selectParentChild
parameter_list|(
name|NodeSet
name|dl
parameter_list|,
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
comment|//		long start = System.currentTimeMillis();
name|ExtArrayNodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|lastDoc
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|NodeSet
operator|.
name|DESCENDANT
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|dl
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
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
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
name|lastDoc
operator|==
literal|null
operator|||
name|n
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|n
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|dl
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
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
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|NodeSet
operator|.
name|ANCESTOR
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|dl
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
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
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
name|lastDoc
operator|==
literal|null
operator|||
name|n
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|n
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|al
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
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
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad 'mode' argument"
argument_list|)
throw|;
block|}
name|result
operator|.
name|sort
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** 	 * For two given sets of potential ancestor and descendant nodes, find 	 * those nodes from the descendant set that actually have ancestors in the 	 * ancestor set, i.e. the ancestor-descendant relationship is true. 	 *  	 * The method returns either the matching descendant or ancestor nodes, 	 * depending on the mode constant. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all descendant nodes found in this node set for each ancestor. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those ancestor nodes, 	 * for which descendants have been found. 	 *  	 * @param dl a node set containing potential descendant nodes 	 * @param al a node set containing potential ancestor nodes 	 * @param mode selection mode 	 * @param includeSelf if true, check if the ancestor node itself is contained in 	 * the set of descendant nodes (descendant-or-self axis) 	 * @param rememberContext if true, add the matching nodes to the context node 	 * list of each returned node (this is used to track matches for predicate evaluation) 	 *  	 * @return 	 */
specifier|public
specifier|static
name|NodeSet
name|selectAncestorDescendant
parameter_list|(
name|NodeSet
name|dl
parameter_list|,
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
name|ExtArrayNodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|lastDoc
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|NodeSet
operator|.
name|DESCENDANT
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|dl
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
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
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
comment|// get a size hint for every new document encountered
if|if
condition|(
name|lastDoc
operator|==
literal|null
operator|||
name|n
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|n
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|dl
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|p
operator|=
name|al
operator|.
name|parentWithChild
argument_list|(
name|n
operator|.
name|getDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getGID
argument_list|()
argument_list|,
literal|false
argument_list|,
name|includeSelf
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
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
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|NodeSet
operator|.
name|ANCESTOR
case|:
for|for
control|(
name|Iterator
name|i
init|=
name|dl
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
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
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
comment|// get a size hint for every new document encountered
if|if
condition|(
name|lastDoc
operator|==
literal|null
operator|||
name|n
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|n
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|al
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
name|p
operator|=
name|al
operator|.
name|parentWithChild
argument_list|(
name|n
operator|.
name|getDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getGID
argument_list|()
argument_list|,
literal|false
argument_list|,
name|includeSelf
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
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
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad 'mode' argument"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * For two sets of potential ancestor and descendant nodes, return all the 	 * real ancestors having a descendant in the descendant set.  	 * 	 * @param  al node set containing potential ancestors 	 * @param dl node set containing potential descendants 	 * @param includeSelf if true, check if the ancestor node itself is contained 	 * in this node set (ancestor-or-self axis) 	 * @param rememberContext if true, add the matching nodes to the context node 	 * list of each returned node (this is used to track matches for predicate evaluation) 	 *@return 	 */
specifier|public
specifier|static
name|NodeSet
name|selectAncestors
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|NodeSet
name|dl
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
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|dl
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
name|NodeSet
name|ancestors
init|=
name|ancestorsForChild
argument_list|(
name|al
argument_list|,
name|n
argument_list|,
literal|false
argument_list|,
name|includeSelf
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|ancestors
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|j
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|temp
operator|=
name|result
operator|.
name|get
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
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
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Return all nodes contained in the node set that are ancestors of the node p.   	 */
specifier|private
specifier|static
name|NodeSet
name|ancestorsForChild
parameter_list|(
name|NodeSet
name|ancestors
parameter_list|,
name|NodeProxy
name|p
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
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|long
name|gid
init|=
name|p
operator|.
name|getGID
argument_list|()
decl_stmt|;
name|NodeProxy
name|temp
init|=
name|ancestors
operator|.
name|get
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|,
name|gid
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeSelf
operator|&&
name|temp
operator|!=
literal|null
condition|)
name|result
operator|.
name|add
argument_list|(
name|temp
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|==
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
condition|)
name|level
operator|=
name|p
operator|.
name|getDocument
argument_list|()
operator|.
name|getTreeLevel
argument_list|(
name|gid
argument_list|)
expr_stmt|;
while|while
condition|(
name|gid
operator|>
literal|0
condition|)
block|{
name|gid
operator|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|,
name|gid
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|temp
operator|=
name|ancestors
operator|.
name|get
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|,
name|gid
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
name|result
operator|.
name|add
argument_list|(
name|temp
argument_list|)
expr_stmt|;
if|else if
condition|(
name|directParent
condition|)
return|return
name|result
return|;
operator|--
name|level
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Select all nodes from the passed set of potential siblings, which 	 * are preceding or following siblings of the nodes in 	 * the other set. If mode is {@link #FOLLOWING}, only following 	 * nodes are selected. {@link #PRECEDING} selects 	 * preceding nodes. 	 *  	 * @param set the node set to check 	 * @param siblings a node set containing potential siblings 	 * @param mode either FOLLOWING or PRECEDING 	 * @return 	 */
specifier|public
specifier|static
name|NodeSet
name|selectSiblings
parameter_list|(
name|NodeSet
name|set
parameter_list|,
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
name|set
operator|.
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
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|Iterator
name|ia
init|=
name|siblings
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
name|ib
init|=
name|set
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
decl_stmt|;
name|NodeProxy
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
name|getDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
operator|<
name|nb
operator|.
name|getDocument
argument_list|()
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
name|getDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
operator|>
name|nb
operator|.
name|getDocument
argument_list|()
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
name|long
name|pa
init|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|na
operator|.
name|getDocument
argument_list|()
argument_list|,
name|na
operator|.
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|pb
init|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|nb
operator|.
name|getDocument
argument_list|()
argument_list|,
name|nb
operator|.
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
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
name|getGID
argument_list|()
operator|<
name|na
operator|.
name|getGID
argument_list|()
condition|)
block|{
comment|// found a preceding sibling
if|if
condition|(
name|mode
operator|==
name|NodeSet
operator|.
name|PRECEDING
condition|)
block|{
name|nb
operator|.
name|addContextNode
argument_list|(
name|na
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|nb
argument_list|)
expr_stmt|;
block|}
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
if|else if
condition|(
name|nb
operator|.
name|getGID
argument_list|()
operator|>
name|na
operator|.
name|getGID
argument_list|()
condition|)
block|{
comment|// found a following sibling
if|if
condition|(
name|mode
operator|==
name|NodeSet
operator|.
name|FOLLOWING
condition|)
block|{
name|nb
operator|.
name|addContextNode
argument_list|(
name|na
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|nb
argument_list|)
expr_stmt|;
block|}
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
specifier|public
specifier|static
name|NodeSet
name|selectFollowing
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|NodeSet
name|following
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|following
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|||
name|set
operator|.
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
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|si
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|sn
init|=
operator|(
name|NodeProxy
operator|)
name|si
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|fi
init|=
name|following
operator|.
name|iterator
argument_list|()
init|;
name|fi
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|fn
init|=
operator|(
name|NodeProxy
operator|)
name|fi
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fn
operator|.
name|after
argument_list|(
name|sn
argument_list|)
condition|)
block|{
name|fn
operator|.
name|addContextNode
argument_list|(
name|sn
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|fn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|NodeSet
name|selectPreceding
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|NodeSet
name|following
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|following
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|||
name|set
operator|.
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
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|si
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|sn
init|=
operator|(
name|NodeProxy
operator|)
name|si
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|fi
init|=
name|following
operator|.
name|iterator
argument_list|()
init|;
name|fi
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|fn
init|=
operator|(
name|NodeProxy
operator|)
name|fi
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fn
operator|.
name|before
argument_list|(
name|sn
argument_list|)
condition|)
block|{
name|fn
operator|.
name|addContextNode
argument_list|(
name|sn
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|fn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|NodeSet
name|directSelectAttributes
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|QName
name|qname
parameter_list|,
name|boolean
name|rememberContext
parameter_list|)
block|{
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|set
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
name|NodeProxy
name|n
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|n
operator|.
name|directSelectAttribute
argument_list|(
name|qname
argument_list|,
name|rememberContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

