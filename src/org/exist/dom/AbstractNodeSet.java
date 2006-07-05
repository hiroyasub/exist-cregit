begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public  * License along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|Expression
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|AbstractSequence
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
name|xquery
operator|.
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceIterator
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
name|value
operator|.
name|Type
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
comment|/**  * Abstract base class for all node set implementations. A node set is a special type of sequence,  * which contains only nodes. Class NodeSet thus implements the {@link org.exist.xquery.value.Sequence}   * as well as the DOM {@link org.w3c.dom.NodeList} interfaces.  *   * Please note that a node set may or may not contain duplicate nodes. Some implementations  * (e.g. {@link org.exist.dom.ExtArrayNodeSet}) remove duplicates when sorting the set.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNodeSet
extends|extends
name|AbstractSequence
implements|implements
name|NodeSet
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractNodeSet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// indicates the type of an optional value index that may have
comment|// been defined on the nodes in this set.
specifier|protected
name|int
name|indexType
init|=
name|Type
operator|.
name|ANY_TYPE
decl_stmt|;
specifier|protected
name|boolean
name|hasTextIndex
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|hasMixedContent
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isCached
init|=
literal|false
decl_stmt|;
specifier|protected
name|AbstractNodeSet
parameter_list|()
block|{
name|isEmpty
operator|=
literal|true
expr_stmt|;
block|}
comment|/** 	 * Return an iterator on the nodes in this list. The iterator returns nodes 	 * according to the internal ordering of nodes (i.e. level first), not in document- 	 * order. 	 *  	 * @return 	 */
specifier|public
specifier|abstract
name|NodeSetIterator
name|iterator
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#iterate() 	 */
specifier|public
specifier|abstract
name|SequenceIterator
name|iterate
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#unorderedIterator() 	 */
specifier|public
specifier|abstract
name|SequenceIterator
name|unorderedIterator
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|/** 	 * Check if this node set contains a node matching the document and 	 * node-id of the given NodeProxy object. 	 *  	 * @param proxy 	 * @return 	 */
specifier|public
specifier|abstract
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
comment|/** 	 * Check if this node set contains nodes belonging to the given document. 	 *  	 * @param doc 	 * @return 	 */
specifier|public
name|boolean
name|containsDoc
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/** 	 * Add a new proxy object to the node set. Please note: node set 	 * implementations may allow duplicates. 	 *  	 * @param proxy 	 */
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
comment|/** 	 * Add a proxy object to the node set. The sizeHint parameter 	 * gives a hint about the number of items to be expected for the 	 * current document. 	 *  	 * @param proxy 	 * @param sizeHint 	 */
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|,
name|int
name|sizeHint
parameter_list|)
block|{
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Add a sequence item to the node set. The item has to be 	 * a subtype of node. 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"item has wrong type"
argument_list|)
throw|;
name|add
argument_list|(
operator|(
name|NodeProxy
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Add all items from the given sequence to the node set. All items 	 * have to be a subtype of node. 	 *  	 * @param other 	 * @throws XPathException 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|other
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"sequence argument is not a node sequence"
argument_list|)
throw|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|other
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Add all nodes from the given node set. 	 *  	 * @param other 	 */
specifier|public
specifier|abstract
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
comment|/** 	 * Return the number of nodes contained in this node set. 	 */
specifier|public
specifier|abstract
name|int
name|getLength
parameter_list|()
function_decl|;
specifier|public
name|void
name|setIsCached
parameter_list|(
name|boolean
name|cached
parameter_list|)
block|{
name|isCached
operator|=
name|cached
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
return|return
name|isCached
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#removeDuplicates()      */
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
comment|// all instances of NodeSet will automatically remove duplicates
comment|// upon a call to getLength() or iterate()
block|}
specifier|public
specifier|abstract
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/** 	 * Get the node at position pos within this node set. 	 * @param pos 	 * @return 	 */
specifier|public
specifier|abstract
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/** 	 * Get a node from this node set matching the document and node id of 	 * the given NodeProxy. 	 *   	 * @param p 	 * @return 	 */
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
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
name|DocumentSet
name|ds
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|NodeProxy
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
name|ds
operator|.
name|add
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ds
return|;
block|}
comment|/** 	 * Get all children of the given parent node contained in this node set. 	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all children found in this node set. If mode is {@link #ANCESTOR}, 	 * the parent itself will be returned if it has child nodes in this set. 	 *  	 * @param parent 	 * @param mode 	 * @param rememberContext 	 * @return 	 */
specifier|public
name|NodeSet
name|hasChildrenInSet
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
comment|// just forward to selectParentChild. Subclasses may overwrite this.
return|return
name|selectParentChild
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
name|contextId
argument_list|)
return|;
block|}
comment|/** 	 * Check if any child nodes are found within this node set for a given 	 * set of potential parent nodes. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all child nodes found in this node set for each parent node. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those parent nodes, 	 * for which children have been found. 	 *   	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 * @return 	 */
specifier|public
name|NodeSet
name|selectParentChild
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
return|return
name|selectParentChild
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
name|Expression
operator|.
name|NO_CONTEXT_ID
argument_list|)
return|;
block|}
comment|/** 	 * Check if any child nodes are found within this node set for a given 	 * set of potential ancestor nodes. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all child nodes found in this node set for each parent node. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those parent nodes, 	 * for which children have been found. 	 *   	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 * @return 	 */
specifier|public
name|NodeSet
name|selectParentChild
parameter_list|(
name|NodeSet
name|al
parameter_list|,
name|int
name|mode
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
return|return
name|NodeSetHelper
operator|.
name|selectParentChild
argument_list|(
name|this
argument_list|,
name|al
argument_list|,
name|mode
argument_list|,
name|contextId
argument_list|)
return|;
block|}
comment|/** 	 * Check if any descendant nodes are found within this node set for a given 	 * set of potential ancestor nodes. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all descendant nodes found in this node set for each ancestor. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those ancestor nodes, 	 * for which descendants have been found. 	 *   	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 * @param includeSelf if true, check if the ancestor node itself is contained in 	 * the set of descendant nodes (descendant-or-self axis) 	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 *  	 * @return 	 */
specifier|public
name|NodeSet
name|selectAncestorDescendant
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
name|int
name|contextId
parameter_list|)
block|{
return|return
name|NodeSetHelper
operator|.
name|selectAncestorDescendant
argument_list|(
name|this
argument_list|,
name|al
argument_list|,
name|mode
argument_list|,
name|includeSelf
argument_list|,
name|contextId
argument_list|)
return|;
block|}
comment|/** 	 * For a given set of potential ancestor nodes, return all ancestors 	 * having descendants in this node set. 	 * 	 * @param  al    node set containing potential ancestors 	 * @param includeSelf if true, check if the ancestor node itself is contained 	 * in this node set (ancestor-or-self axis) 	 * @param rememberContext if true, add the matching nodes to the context node 	 * list of each returned node (this is used to track matches for predicate evaluation) 	 * @return 	 */
specifier|public
name|NodeSet
name|selectAncestors
parameter_list|(
name|NodeSet
name|descendants
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
return|return
name|NodeSetHelper
operator|.
name|selectAncestors
argument_list|(
name|this
argument_list|,
name|descendants
argument_list|,
name|includeSelf
argument_list|,
name|contextId
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|selectFollowing
parameter_list|(
name|NodeSet
name|fl
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|NodeSetHelper
operator|.
name|selectFollowing
argument_list|(
name|fl
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|selectPreceding
parameter_list|(
name|NodeSet
name|pl
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|NodeSetHelper
operator|.
name|selectPreceding
argument_list|(
name|pl
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** 	 * Select all nodes from the passed node set, which 	 * are preceding or following siblings of the nodes in 	 * this set. If mode is {@link #FOLLOWING}, only nodes following 	 * the context node are selected. {@link #PRECEDING} selects 	 * preceding nodes. 	 *  	 * @param siblings a node set containing potential siblings 	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 * @return 	 */
specifier|public
name|NodeSet
name|selectPrecedingSiblings
parameter_list|(
name|NodeSet
name|siblings
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
return|return
name|NodeSetHelper
operator|.
name|selectPrecedingSiblings
argument_list|(
name|this
argument_list|,
name|siblings
argument_list|,
name|contextId
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|selectFollowingSiblings
parameter_list|(
name|NodeSet
name|siblings
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
return|return
name|NodeSetHelper
operator|.
name|selectFollowingSiblings
argument_list|(
name|this
argument_list|,
name|siblings
argument_list|,
name|contextId
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|directSelectAttribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
return|return
name|NodeSetHelper
operator|.
name|directSelectAttributes
argument_list|(
name|this
argument_list|,
name|qname
argument_list|,
name|contextId
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
name|NodeId
name|nodeId
parameter_list|,
name|boolean
name|directParent
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
name|NodeProxy
name|temp
init|=
name|get
argument_list|(
name|doc
argument_list|,
name|nodeId
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
return|return
name|temp
return|;
name|nodeId
operator|=
name|nodeId
operator|.
name|getParentId
argument_list|()
expr_stmt|;
while|while
condition|(
name|nodeId
operator|!=
literal|null
condition|)
block|{
name|temp
operator|=
name|get
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
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
name|nodeId
operator|=
name|nodeId
operator|.
name|getParentId
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * Check if the given node has an ancestor contained in this node set 	 * and return the ancestor found. 	 * 	 * If directParent is true, only immediate ancestors (parents) are considered. 	 * Otherwise the method will call itself recursively for all the node's 	 * parents. 	 * 	 * If includeSelf is true, the method returns also true if 	 * the node itself is contained in the node set. 	 */
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
return|return
name|parentWithChild
argument_list|(
name|proxy
operator|.
name|getDocument
argument_list|()
argument_list|,
name|proxy
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|directParent
argument_list|,
name|includeSelf
argument_list|)
return|;
block|}
comment|/** 	 * Return a new node set containing the parent nodes of all nodes in the  	 * current set. 	 * @return 	 */
specifier|public
name|NodeSet
name|getParents
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|NodeSet
name|parents
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|NodeProxy
name|parent
init|=
literal|null
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
name|NodeProxy
name|current
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeId
name|parentID
init|=
name|current
operator|.
name|getNodeId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
comment|//Filter out the temporary nodes wrapper element
if|if
condition|(
name|parentID
operator|!=
name|NodeId
operator|.
name|DOCUMENT_NODE
operator|&&
operator|!
operator|(
name|parentID
operator|.
name|getTreeLevel
argument_list|()
operator|==
literal|1
operator|&&
name|current
operator|.
name|getDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
operator|||
name|parent
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
operator|!=
name|current
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
operator|||
operator|!
name|parent
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|parentID
argument_list|)
condition|)
block|{
name|parent
operator|=
operator|new
name|NodeProxy
argument_list|(
name|current
operator|.
name|getDocument
argument_list|()
argument_list|,
name|parentID
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|,
name|StoredNode
operator|.
name|UNKNOWN_NODE_IMPL_ADDRESS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
name|parent
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|current
argument_list|)
expr_stmt|;
else|else
name|parent
operator|.
name|copyContext
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|parents
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parents
return|;
block|}
specifier|public
name|NodeSet
name|getAncestors
parameter_list|(
name|int
name|contextId
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
name|ExtArrayNodeSet
name|ancestors
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
name|current
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeSelf
condition|)
block|{
if|if
condition|(
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
name|current
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|ancestors
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|NodeId
name|parentID
init|=
name|current
operator|.
name|getNodeId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
while|while
condition|(
name|parentID
operator|!=
literal|null
condition|)
block|{
comment|//Filter out the temporary nodes wrapper element
if|if
condition|(
name|parentID
operator|!=
name|NodeId
operator|.
name|DOCUMENT_NODE
operator|&&
operator|!
operator|(
name|parentID
operator|.
name|getTreeLevel
argument_list|()
operator|==
literal|1
operator|&&
name|current
operator|.
name|getDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
operator|)
condition|)
block|{
name|NodeProxy
name|parent
init|=
operator|new
name|NodeProxy
argument_list|(
name|current
operator|.
name|getDocument
argument_list|()
argument_list|,
name|parentID
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
name|parent
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|current
argument_list|)
expr_stmt|;
else|else
name|parent
operator|.
name|copyContext
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|ancestors
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
name|parentID
operator|=
name|parentID
operator|.
name|getParentId
argument_list|()
expr_stmt|;
block|}
block|}
name|ancestors
operator|.
name|mergeDuplicates
argument_list|()
expr_stmt|;
return|return
name|ancestors
return|;
block|}
comment|/** 	 * Get a hint about how many nodes in this node set belong to the  	 * specified document. This is just used for allocating new node sets. 	 * The information does not need to be exact. -1 is returned if the 	 * size cannot be determined (the default). 	 *  	 * @param doc 	 * @return 	 */
specifier|public
name|int
name|getSizeHint
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
name|Constants
operator|.
name|NO_SIZE_HINT
return|;
block|}
comment|/** 	 * Return a new node set, which represents the intersection of the current 	 * node set with the given node set. 	 *  	 * @param other 	 * @return 	 */
specifier|public
name|NodeSet
name|intersection
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
name|AVLTreeNodeSet
name|r
init|=
operator|new
name|AVLTreeNodeSet
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
name|deepIntersection
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
comment|//ExtArrayNodeSet r = new ExtArrayNodeSet();
name|AVLTreeNodeSet
name|r
init|=
operator|new
name|AVLTreeNodeSet
argument_list|()
decl_stmt|;
name|NodeProxy
name|l
decl_stmt|,
name|p
decl_stmt|,
name|q
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
operator|(
name|p
operator|=
name|other
operator|.
name|parentWithChild
argument_list|(
name|l
argument_list|,
literal|false
argument_list|,
literal|true
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
name|p
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|l
operator|.
name|getNode
argument_list|()
argument_list|)
condition|)
name|p
operator|.
name|addMatches
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
name|p
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
operator|(
name|q
operator|=
name|parentWithChild
argument_list|(
name|l
argument_list|,
literal|false
argument_list|,
literal|true
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
operator|(
name|p
operator|=
name|r
operator|.
name|get
argument_list|(
name|q
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
name|except
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
name|AVLTreeNodeSet
name|r
init|=
operator|new
name|AVLTreeNodeSet
argument_list|()
decl_stmt|;
name|NodeProxy
name|l
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
operator|!
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
return|return
name|r
return|;
block|}
comment|/** 	 * Return a new node set which represents the union of the 	 * current node set and the given node set. 	 *  	 * @param other 	 * @return 	 */
specifier|public
name|NodeSet
name|union
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
name|ExtArrayNodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
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
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
name|c
operator|.
name|addMatches
argument_list|(
name|p
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
comment|/** 	 * Returns all context nodes associated with the nodes in 	 * this node set. 	 *   	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 * @return 	 */
specifier|public
name|NodeSet
name|getContextNodes
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|NodeProxy
name|current
decl_stmt|,
name|context
decl_stmt|;
name|ContextItem
name|contextNode
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
name|contextNode
operator|=
name|current
operator|.
name|getContext
argument_list|()
expr_stmt|;
while|while
condition|(
name|contextNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|contextNode
operator|.
name|getContextId
argument_list|()
operator|==
name|contextId
condition|)
block|{
name|context
operator|=
name|contextNode
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|context
operator|.
name|addMatches
argument_list|(
name|current
argument_list|)
expr_stmt|;
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
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
name|context
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastDoc
operator|!=
literal|null
operator|&&
name|lastDoc
operator|.
name|getDocId
argument_list|()
operator|!=
name|context
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
condition|)
block|{
name|lastDoc
operator|=
name|context
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|context
argument_list|,
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
name|result
operator|.
name|add
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
name|contextNode
operator|=
name|contextNode
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Always returns this. 	 *  	 * @see org.exist.xquery.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#getState() 	 */
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#hasChanged(int) 	 */
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/** 	 * If all nodes in this set have an index, returns the common 	 * supertype used to build the index, e.g. xs:integer or xs:string. 	 * If the nodes have different index types or no node has been indexed, 	 * returns {@link Type#ITEM}. 	 *  	 * @see org.exist.xquery.GeneralComparison 	 * @see org.exist.xquery.ValueComparison 	 * @return 	 */
specifier|public
name|int
name|getIndexType
parameter_list|()
block|{
comment|//Is the index type initialized ?
if|if
condition|(
name|indexType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|hasTextIndex
operator|=
literal|true
expr_stmt|;
name|hasMixedContent
operator|=
literal|true
expr_stmt|;
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
name|NodeProxy
name|node
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
condition|)
block|{
comment|//Temporary nodes return default values
name|indexType
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
name|hasTextIndex
operator|=
literal|false
expr_stmt|;
name|hasMixedContent
operator|=
literal|false
expr_stmt|;
break|break;
block|}
name|int
name|nodeIndexType
init|=
name|node
operator|.
name|getIndexType
argument_list|()
decl_stmt|;
comment|//Refine type
comment|//TODO : use common subtype
if|if
condition|(
name|indexType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|indexType
operator|=
name|nodeIndexType
expr_stmt|;
block|}
else|else
block|{
comment|//Broaden type
comment|//TODO : use common supertype
if|if
condition|(
name|indexType
operator|!=
name|nodeIndexType
condition|)
name|indexType
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|node
operator|.
name|hasTextIndex
argument_list|()
condition|)
block|{
name|hasTextIndex
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|node
operator|.
name|hasMixedContent
argument_list|()
condition|)
block|{
name|hasMixedContent
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
return|return
name|indexType
return|;
block|}
specifier|public
name|boolean
name|hasTextIndex
parameter_list|()
block|{
if|if
condition|(
name|indexType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|getIndexType
argument_list|()
expr_stmt|;
comment|//		    int type;
comment|//		    NodeProxy p;
comment|//			for (Iterator i = iterator(); i.hasNext();) {
comment|//			    p = (NodeProxy) i.next();
comment|//			    hasTextIndex = p.hasTextIndex();
comment|//			    if(!hasTextIndex)
comment|//			        break;
comment|//			}
block|}
return|return
name|hasTextIndex
return|;
block|}
specifier|public
name|boolean
name|hasMixedContent
parameter_list|()
block|{
if|if
condition|(
name|indexType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|getIndexType
argument_list|()
expr_stmt|;
block|}
return|return
name|hasMixedContent
return|;
block|}
specifier|public
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|NodeProxy
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
name|p
operator|.
name|clearContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AbstractSequence#isPersistentSet()      */
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
comment|// node sets are always persistent
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"NodeSet("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|NodeProxy
name|p
init|=
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

