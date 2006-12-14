begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|NodeList
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeSet
extends|extends
name|Sequence
extends|,
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
comment|/** 	 * Constant representing an empty node set. 	 */
specifier|public
specifier|final
specifier|static
name|NodeSet
name|EMPTY_SET
init|=
operator|new
name|EmptyNodeSet
argument_list|()
decl_stmt|;
comment|/** 	 * Return an iterator on the nodes in this list. The iterator returns nodes 	 * according to the internal ordering of nodes (i.e. level first), not in document- 	 * order. 	 *  	 */
specifier|public
name|NodeSetIterator
name|iterator
parameter_list|()
function_decl|;
comment|/** 	 * Check if this node set contains a node matching the document and 	 * node-id of the given NodeProxy object. 	 *  	 * @param proxy 	 */
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
comment|/**      * Returns a DocumentSet containing all documents referenced      * in this node set.      *      * @return      */
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
function_decl|;
comment|/**      * Return an iterator on all collections referenced by documents      * contained in this node set.      *      * @return      */
specifier|public
name|Iterator
name|getCollectionIterator
parameter_list|()
function_decl|;
comment|/** 	 * Add a new proxy object to the node set. Please note: node set 	 * implementations may allow duplicates. 	 *  	 * @param proxy 	 */
specifier|public
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
function_decl|;
comment|/** 	 * Add all nodes from the given node set. 	 *  	 * @param other 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
comment|/** 	 * Get the node at position pos within this node set. 	 * @param pos 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/** 	 * Get a node from this node set matching the document and node id of 	 * the given NodeProxy. 	 *   	 * @param p 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
function_decl|;
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/** 	 * Check if any child nodes are found within this node set for a given 	 * set of potential parent nodes. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all child nodes found in this node set for each parent node. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those parent nodes, 	 * for which children have been found. 	 *   	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 */
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
function_decl|;
comment|/** 	 * Check if any child nodes are found within this node set for a given 	 * set of potential parent nodes. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all child nodes found in this node set for each parent node. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those parent nodes, 	 * for which children have been found. 	 *   	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 */
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
function_decl|;
comment|/** 	 * Check if any descendant nodes are found within this node set for a given 	 * set of potential ancestor nodes. 	 *  	 * If mode is {@link #DESCENDANT}, the returned node set will contain 	 * all descendant nodes found in this node set for each ancestor. If mode is 	 * {@link #ANCESTOR}, the returned set will contain those ancestor nodes, 	 * for which descendants have been found. 	 *   	 * @param al a node set containing potential parent nodes 	 * @param mode selection mode 	 * @param includeSelf if true, check if the ancestor node itself is contained in 	 * the set of descendant nodes (descendant-or-self axis) 	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 */
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
function_decl|;
comment|/** 	 * For a given set of potential ancestor nodes, return all ancestors 	 * having descendants in this node set. 	 * 	 * @param  descendants    node set containing potential ancestors 	 * @param includeSelf if true, check if the ancestor node itself is contained 	 * in this node set (ancestor-or-self axis) 	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 */
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
function_decl|;
comment|/**      * Select all nodes from the passed node set, which      * are preceding siblings of the nodes in      * this set.      *       * @param siblings a node set containing potential siblings      * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.        */
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
function_decl|;
comment|/**      * Select all nodes from the passed node set, which      * are following siblings of the nodes in      * this set.      *       * @param siblings a node set containing potential siblings      * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.           */
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
function_decl|;
specifier|public
name|NodeSet
name|selectPreceding
parameter_list|(
name|NodeSet
name|preceding
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|NodeSet
name|selectFollowing
parameter_list|(
name|NodeSet
name|following
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Check if the node identified by its node id has an ancestor contained in this node set 	 * and return the ancestor found. 	 * 	 * If directParent is true, only immediate ancestors (parents) are considered. 	 * Otherwise the method will call itself recursively for all the node's 	 * parents. 	 * 	 * If includeSelf is true, the method returns also true if 	 * the node itself is contained in the node set. 	 */
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
function_decl|;
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
function_decl|;
comment|/** 	 * Return a new node set containing the parent nodes of all nodes in the  	 * current set. 	 */
specifier|public
name|NodeSet
name|getParents
parameter_list|(
name|int
name|contextId
parameter_list|)
function_decl|;
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
function_decl|;
comment|/**      * Optimized method to select attributes. Use this if the context has just one or      * two nodes. Attributes will be directly looked up in the persistent DOM store.      *        * @param qname the QName of the attribute      * @param contextId used to track context nodes when evaluating predicate       * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context      * will be added to each result of the of the selection.       */
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
function_decl|;
comment|/** 	 * If all nodes in this set have an index, returns the common 	 * supertype used to build the index, e.g. xs:integer or xs:string. 	 * If the nodes have different index types or no node has been indexed, 	 * returns {@link Type#ITEM}. 	 *  	 * @see org.exist.xquery.GeneralComparison 	 * @see org.exist.xquery.ValueComparison 	 */
specifier|public
name|int
name|getIndexType
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasTextIndex
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasMixedContent
parameter_list|()
function_decl|;
comment|/** 	 * Get a hint about how many nodes in this node set belong to the  	 * specified document. This is just used for allocating new node sets. 	 * The information does not need to be exact. -1 is returned if the 	 * size cannot be determined (the default). 	 *  	 * @param doc 	 */
specifier|public
name|int
name|getSizeHint
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/** 	 * Return a new node set, which represents the intersection of the current 	 * node set with the given node set. 	 *  	 * @param other 	 */
specifier|public
name|NodeSet
name|intersection
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
comment|/** 	 * Return a new node set, containing all nodes in this node set that 	 * are contained or have descendants in the other node set. 	 *  	 * @param other 	 */
specifier|public
name|NodeSet
name|deepIntersection
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
comment|/** 	 * Return a new node set which represents the union of the 	 * current node set and the given node set. 	 *  	 * @param other 	 */
specifier|public
name|NodeSet
name|union
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
comment|/** 	 * Return a new node set containing all nodes from this node set 	 * except those nodes which are also contained in the argument node set. 	 *  	 * @param other 	 */
specifier|public
name|NodeSet
name|except
parameter_list|(
name|NodeSet
name|other
parameter_list|)
function_decl|;
comment|/**      * Create a new node set from this set containing only nodes in documents      * that are also contained in the argument set.      *      * @param otherSet      * @return      */
specifier|public
name|NodeSet
name|filterDocuments
parameter_list|(
name|NodeSet
name|otherSet
parameter_list|)
function_decl|;
specifier|public
name|void
name|setProcessInReverseOrder
parameter_list|(
name|boolean
name|inReverseOrder
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|getProcessInReverseOrder
parameter_list|()
function_decl|;
comment|/** 	 * Returns all context nodes associated with the nodes in 	 * this node set. 	 *   	 * @param contextId used to track context nodes when evaluating predicate  	 * expressions. If contextId != {@link Expression#NO_CONTEXT_ID}, the current context 	 * will be added to each result of the of the selection.  	 */
specifier|public
name|NodeSet
name|getContextNodes
parameter_list|(
name|int
name|contextId
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
function_decl|;
specifier|public
name|int
name|getState
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

