begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public  * License along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  *   */
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
name|dbxml
operator|.
name|core
operator|.
name|data
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|NodeTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|SequenceIterator
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

begin_comment
comment|/**  * This node set is called virtual because it is just a placeholder for  * the set of relevant nodes. For XPath expressions like //* or //node(),   * it would be totally unefficient to actually retrieve all descendant nodes.  * In many cases, the expression can be resolved at a later point in time  * without retrieving the whole node set.   *  * VirtualNodeSet basically provides method getFirstParent to retrieve the first  * matching descendant of its context according to the primary type axis.  *  * Class LocationStep will always return an instance of VirtualNodeSet  * if it finds something like descendant::* etc..  *  * @author Wolfgang Meier  * @author Timo Boehme  */
end_comment

begin_class
specifier|public
class|class
name|VirtualNodeSet
extends|extends
name|AbstractNodeSet
block|{
specifier|protected
name|int
name|axis
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|NodeTest
name|test
decl_stmt|;
specifier|protected
name|NodeSet
name|context
decl_stmt|;
specifier|protected
name|NodeSet
name|realSet
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|inPredicate
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|useSelfAsContext
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|lastDocAdded
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|sizeHint
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|VirtualNodeSet
parameter_list|(
name|int
name|axis
parameter_list|,
name|NodeTest
name|test
parameter_list|,
name|NodeSet
name|context
parameter_list|)
block|{
name|this
operator|.
name|axis
operator|=
name|axis
expr_stmt|;
name|this
operator|.
name|test
operator|=
name|test
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
name|NodeProxy
name|first
init|=
name|getFirstParent
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// Timo Boehme: getFirstParent returns now only real parents
comment|//              therefore test if node is child of context
comment|//return (first != null);
return|return
operator|(
operator|(
name|first
operator|!=
literal|null
operator|)
operator|||
operator|(
name|context
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
argument_list|)
operator|!=
literal|null
operator|)
operator|)
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
name|NodeProxy
name|first
init|=
name|getFirstParent
argument_list|(
name|p
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// Timo Boehme: getFirstParent returns now only real parents
comment|//              therefore test if node is child of context
comment|//return (first != null);
return|return
operator|(
operator|(
name|first
operator|!=
literal|null
operator|)
operator|||
operator|(
name|context
operator|.
name|get
argument_list|(
name|p
operator|.
name|doc
argument_list|,
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
argument_list|)
operator|!=
literal|null
operator|)
operator|)
return|;
block|}
specifier|public
name|void
name|setInPredicate
parameter_list|(
name|boolean
name|predicate
parameter_list|)
block|{
name|inPredicate
operator|=
name|predicate
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.AbstractNodeSet#getDocumentSet() 	 */
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
name|context
operator|.
name|getDocumentSet
argument_list|()
return|;
block|}
specifier|protected
name|NodeProxy
name|getFirstParent
parameter_list|(
name|NodeProxy
name|node
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
name|getFirstParent
argument_list|(
name|node
argument_list|,
literal|null
argument_list|,
name|includeSelf
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|protected
name|NodeProxy
name|getFirstParent
parameter_list|(
name|NodeProxy
name|node
parameter_list|,
name|NodeProxy
name|first
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|int
name|recursions
parameter_list|)
block|{
return|return
name|getFirstParent
argument_list|(
name|node
argument_list|,
name|first
argument_list|,
name|includeSelf
argument_list|,
literal|true
argument_list|,
name|recursions
argument_list|)
return|;
block|}
specifier|protected
name|NodeProxy
name|getFirstParent
parameter_list|(
name|NodeProxy
name|node
parameter_list|,
name|NodeProxy
name|first
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|int
name|recursions
parameter_list|)
block|{
name|long
name|pid
init|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|node
operator|.
name|doc
argument_list|,
name|node
operator|.
name|gid
argument_list|)
decl_stmt|;
name|NodeProxy
name|parent
decl_stmt|;
comment|// check if the start-node should be included, e.g. to process an
comment|// expression like *[. = 'xxx']
if|if
condition|(
name|recursions
operator|==
literal|0
operator|&&
name|includeSelf
operator|&&
name|test
operator|.
name|matches
argument_list|(
name|node
argument_list|)
condition|)
block|{
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
condition|)
block|{
comment|// if we're on the child axis, test if
comment|// the node is a direct child of the context node
if|if
condition|(
operator|(
name|parent
operator|=
name|context
operator|.
name|get
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|node
operator|.
name|doc
argument_list|,
name|pid
argument_list|)
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|useSelfAsContext
operator|&&
name|inPredicate
condition|)
block|{
name|node
operator|.
name|addContextNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|inPredicate
condition|)
name|node
operator|.
name|addContextNode
argument_list|(
name|parent
argument_list|)
expr_stmt|;
else|else
name|node
operator|.
name|copyContext
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
block|}
else|else
comment|// descendant axis: remember the node and continue
name|first
operator|=
name|node
expr_stmt|;
block|}
comment|// if this is the first call to this method, remember the first parent node
comment|// and re-evaluate the method
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|pid
operator|<
literal|0
condition|)
comment|// given node was already document element -> no parent
return|return
literal|null
return|;
name|first
operator|=
operator|new
name|NodeProxy
argument_list|(
name|node
operator|.
name|doc
argument_list|,
name|pid
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
comment|// Timo Boehme: we need a real parent (child from context)
return|return
name|getFirstParent
argument_list|(
name|first
argument_list|,
name|first
argument_list|,
literal|false
argument_list|,
name|directParent
argument_list|,
name|recursions
operator|+
literal|1
argument_list|)
return|;
block|}
comment|// is pid member of the context set?
name|parent
operator|=
name|context
operator|.
name|get
argument_list|(
name|node
operator|.
name|doc
argument_list|,
name|pid
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|test
operator|.
name|matches
argument_list|(
name|first
argument_list|)
condition|)
block|{
if|if
condition|(
name|axis
operator|!=
name|Constants
operator|.
name|CHILD_AXIS
condition|)
block|{
comment|// if we are on the descendant-axis, we return the first node
comment|// we found while walking bottom-up.
comment|// Otherwise, we return the last one (which is node)
name|node
operator|=
name|first
expr_stmt|;
block|}
if|if
condition|(
name|useSelfAsContext
operator|&&
name|inPredicate
condition|)
block|{
name|node
operator|.
name|addContextNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|inPredicate
condition|)
block|{
name|node
operator|.
name|addContextNode
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|copyContext
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|// Timo Boehme: we return the ancestor which is child of context
comment|// TODO
return|return
name|node
return|;
block|}
if|else if
condition|(
name|pid
operator|<
literal|0
condition|)
comment|// no matching node has been found in the context
return|return
literal|null
return|;
if|else if
condition|(
name|directParent
operator|&&
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|&&
name|recursions
operator|==
literal|1
condition|)
comment|// break here if the expression is like /*/n
return|return
literal|null
return|;
else|else
block|{
comment|// continue for expressions like //*/n or /*//n
name|parent
operator|=
operator|new
name|NodeProxy
argument_list|(
name|node
operator|.
name|doc
argument_list|,
name|pid
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
return|return
name|getFirstParent
argument_list|(
name|parent
argument_list|,
name|first
argument_list|,
literal|false
argument_list|,
name|directParent
argument_list|,
name|recursions
operator|+
literal|1
argument_list|)
return|;
block|}
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
parameter_list|,
name|int
name|level
parameter_list|)
block|{
specifier|final
name|NodeProxy
name|p
init|=
name|getFirstParent
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
argument_list|,
literal|null
argument_list|,
name|includeSelf
argument_list|,
name|directParent
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
name|addInternal
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
specifier|public
name|NodeProxy
name|nodeHasParent
parameter_list|(
name|NodeProxy
name|node
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
specifier|final
name|NodeProxy
name|p
init|=
name|getFirstParent
argument_list|(
name|node
argument_list|,
literal|null
argument_list|,
name|includeSelf
argument_list|,
name|directParent
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
name|addInternal
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
specifier|private
name|void
name|addInternal
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
if|if
condition|(
name|realSet
operator|==
literal|null
condition|)
name|realSet
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|(
literal|256
argument_list|)
expr_stmt|;
name|realSet
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
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
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|NodeProxy
name|first
init|=
name|getFirstParent
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
argument_list|,
literal|null
argument_list|,
name|includeSelf
argument_list|,
name|directParent
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
name|addInternal
argument_list|(
name|first
argument_list|)
expr_stmt|;
return|return
name|first
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
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|NodeProxy
name|first
init|=
name|getFirstParent
argument_list|(
name|proxy
argument_list|,
literal|null
argument_list|,
name|includeSelf
argument_list|,
name|directParent
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
block|{
name|addInternal
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
return|return
name|first
return|;
block|}
specifier|private
specifier|final
name|NodeSet
name|getNodes
parameter_list|()
block|{
name|ExtArrayNodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|Node
name|p
decl_stmt|,
name|c
decl_stmt|;
name|NodeProxy
name|proxy
decl_stmt|;
name|NodeList
name|cl
decl_stmt|;
name|Iterator
name|domIter
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|proxy
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
name|proxy
operator|.
name|gid
operator|<
literal|0
condition|)
block|{
comment|/* // commented out by Timo Boehme (document element is already part of virtual node set (not parent!)) 								proxy.gid = proxy.doc.getDocumentElementId(); 				*/
comment|// -- inserted by Timo Boehme --
name|NodeProxy
name|docElemProxy
init|=
operator|new
name|NodeProxy
argument_list|(
name|proxy
operator|.
name|getDoc
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|docElemProxy
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|docElemProxy
argument_list|)
expr_stmt|;
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
condition|)
block|{
name|domIter
operator|=
name|docElemProxy
operator|.
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|getNodeIterator
argument_list|(
name|docElemProxy
argument_list|)
expr_stmt|;
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|domIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|setOwnerDocument
argument_list|(
name|docElemProxy
operator|.
name|doc
argument_list|)
expr_stmt|;
name|node
operator|.
name|setGID
argument_list|(
name|docElemProxy
operator|.
name|gid
argument_list|)
expr_stmt|;
name|docElemProxy
operator|.
name|match
operator|=
name|proxy
operator|.
name|match
expr_stmt|;
name|addChildren
argument_list|(
name|docElemProxy
argument_list|,
name|result
argument_list|,
name|node
argument_list|,
name|domIter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
continue|continue;
comment|// -- end of insertion --
block|}
else|else
block|{
name|domIter
operator|=
name|proxy
operator|.
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|getNodeIterator
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|domIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|.
name|setOwnerDocument
argument_list|(
name|proxy
operator|.
name|doc
argument_list|)
expr_stmt|;
name|node
operator|.
name|setGID
argument_list|(
name|proxy
operator|.
name|gid
argument_list|)
expr_stmt|;
name|addChildren
argument_list|(
name|proxy
argument_list|,
name|result
argument_list|,
name|node
argument_list|,
name|domIter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|final
name|void
name|addChildren
parameter_list|(
name|NodeProxy
name|contextNode
parameter_list|,
name|NodeSet
name|result
parameter_list|,
name|NodeImpl
name|node
parameter_list|,
name|Iterator
name|iter
parameter_list|,
name|int
name|recursions
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
name|NodeImpl
name|child
decl_stmt|;
name|Value
name|value
decl_stmt|;
name|NodeProxy
name|p
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
name|node
operator|.
name|getChildCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|child
operator|=
operator|(
name|NodeImpl
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|child
operator|.
name|setOwnerDocument
argument_list|(
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|)
expr_stmt|;
name|child
operator|.
name|setGID
argument_list|(
name|node
operator|.
name|firstChildID
argument_list|()
operator|+
name|i
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|NodeProxy
argument_list|(
name|child
operator|.
name|ownerDocument
argument_list|,
name|child
operator|.
name|gid
argument_list|,
name|child
operator|.
name|getNodeType
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|setInternalAddress
argument_list|(
name|child
operator|.
name|internalAddress
argument_list|)
expr_stmt|;
name|p
operator|.
name|match
operator|=
name|contextNode
operator|.
name|match
expr_stmt|;
if|if
condition|(
name|test
operator|.
name|matches
argument_list|(
name|child
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|)
operator|&&
name|recursions
operator|==
literal|0
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|useSelfAsContext
operator|&&
name|inPredicate
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|else if
condition|(
name|inPredicate
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|(
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
operator|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|useSelfAsContext
operator|&&
name|inPredicate
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|else if
condition|(
name|inPredicate
condition|)
name|p
operator|.
name|addContextNode
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
condition|)
return|return;
block|}
name|addChildren
argument_list|(
name|contextNode
argument_list|,
name|result
argument_list|,
name|child
argument_list|,
name|iter
argument_list|,
name|recursions
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
name|void
name|realize
parameter_list|()
block|{
if|if
condition|(
name|realSet
operator|!=
literal|null
condition|)
return|return;
comment|//Thread.dumpStack();
name|realSet
operator|=
name|getNodes
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setSelfIsContext
parameter_list|()
block|{
name|useSelfAsContext
operator|=
literal|true
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#hasIndex() 	 */
specifier|public
name|boolean
name|hasIndex
parameter_list|()
block|{
comment|// Always return false: there's no index
return|return
literal|false
return|;
block|}
comment|/* the following methods are normally never called in this context, 	 * we just provide them because they are declared abstract 	 * in the super class 	 */
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
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
block|}
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeList
name|other
parameter_list|)
block|{
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
block|}
specifier|public
name|void
name|set
parameter_list|(
name|int
name|position
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|getLength
argument_list|()
return|;
block|}
specifier|public
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|item
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|get
argument_list|(
name|proxy
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|iterate
argument_list|()
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
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|intersection
argument_list|(
name|other
argument_list|)
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
name|realize
argument_list|()
expr_stmt|;
return|return
name|realSet
operator|.
name|union
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
end_class

end_unit

