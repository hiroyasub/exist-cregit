begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|AVLTreeNodeSet
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
name|NodeProxy
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
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
name|util
operator|.
name|FastQSort
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
name|OrderSpec
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
name|util
operator|.
name|ExpressionDumper
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
comment|/**  * A sequence that sorts its entries in the order specified by the order specs of  * an "order by" clause. Used by {@link org.exist.xquery.ForExpr}.  *   * Contrary to class {@link org.exist.xquery.value.PreorderedValueSequence},  * all order expressions are evaluated once for each item in the sequence   *<b>while</b> items are added.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|OrderedValueSequence
extends|extends
name|AbstractSequence
block|{
specifier|private
name|OrderSpec
name|orderSpecs
index|[]
decl_stmt|;
specifier|private
name|Entry
index|[]
name|items
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|state
init|=
literal|0
decl_stmt|;
comment|// used to keep track of the type of added items.
specifier|private
name|int
name|itemType
init|=
name|Type
operator|.
name|ANY_TYPE
decl_stmt|;
specifier|public
name|OrderedValueSequence
parameter_list|(
name|OrderSpec
name|orderSpecs
index|[]
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|orderSpecs
operator|=
name|orderSpecs
expr_stmt|;
name|this
operator|.
name|items
operator|=
operator|new
name|Entry
index|[
name|size
index|]
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|OrderedValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|OrderedValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getItemCount
parameter_list|()
block|{
return|return
operator|(
name|items
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|count
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|isEmpty
return|;
block|}
specifier|public
name|boolean
name|hasOne
parameter_list|()
block|{
return|return
name|hasOne
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#add(org.exist.xquery.value.Item) 	 */
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
name|hasOne
condition|)
name|hasOne
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|isEmpty
condition|)
name|hasOne
operator|=
literal|true
expr_stmt|;
name|isEmpty
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
operator|&&
name|items
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|items
operator|=
operator|new
name|Entry
index|[
literal|2
index|]
expr_stmt|;
block|}
if|else if
condition|(
name|count
operator|==
name|items
operator|.
name|length
condition|)
block|{
name|Entry
name|newItems
index|[]
init|=
operator|new
name|Entry
index|[
name|count
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|items
argument_list|,
literal|0
argument_list|,
name|newItems
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|items
operator|=
name|newItems
expr_stmt|;
block|}
name|items
index|[
name|count
operator|++
index|]
operator|=
operator|new
name|Entry
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|checkItemType
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|setHasChanged
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#addAll(org.exist.xquery.value.Sequence) 	 */
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
name|other
operator|.
name|hasOne
argument_list|()
condition|)
name|add
argument_list|(
name|other
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|else if
condition|(
operator|!
name|other
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|sort
parameter_list|()
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|items
argument_list|,
literal|0
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#itemAt(int) 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|items
operator|!=
literal|null
operator|&&
name|pos
operator|>
operator|-
literal|1
operator|&&
name|pos
operator|<
name|count
condition|)
return|return
name|items
index|[
name|pos
index|]
operator|.
name|item
return|;
else|else
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|checkItemType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|NODE
operator|||
name|itemType
operator|==
name|type
condition|)
return|return;
if|if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
name|itemType
operator|=
name|type
expr_stmt|;
else|else
name|itemType
operator|=
name|Type
operator|.
name|NODE
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#getItemType()      */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|itemType
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//return early
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
comment|// for this method to work, all items have to be nodes
if|if
condition|(
name|itemType
operator|!=
name|Type
operator|.
name|ANY_TYPE
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|itemType
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
comment|//Was ExtArrayNodeset() which orders the nodes in document order
comment|//The order seems to change between different invocations !!!
name|NodeSet
name|set
init|=
operator|new
name|AVLTreeNodeSet
argument_list|()
decl_stmt|;
comment|//We can't make it from an ExtArrayNodeSet (probably because it is sorted ?)
comment|//NodeSet set = new ArraySet(100);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//TODO : investigate why we could have null here
if|if
condition|(
name|items
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|NodeValue
name|v
init|=
operator|(
name|NodeValue
operator|)
name|items
index|[
name|i
index|]
operator|.
name|item
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
comment|// found an in-memory document
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
name|doc
init|=
operator|(
operator|(
name|NodeImpl
operator|)
name|v
operator|)
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// make this document persistent: doc.makePersistent()
comment|// returns a map of all root node ids mapped to the corresponding
comment|// persistent node. We scan the current sequence and replace all
comment|// in-memory nodes with their new persistent node objects.
name|DocumentImpl
name|expandedDoc
init|=
name|doc
operator|.
name|expandRefs
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
name|newDoc
init|=
name|expandedDoc
operator|.
name|makePersistent
argument_list|()
decl_stmt|;
if|if
condition|(
name|newDoc
operator|!=
literal|null
condition|)
block|{
name|NodeId
name|rootId
init|=
name|newDoc
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|count
condition|;
name|j
operator|++
control|)
block|{
name|v
operator|=
operator|(
name|NodeValue
operator|)
name|items
index|[
name|j
index|]
operator|.
name|item
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|v
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getDocument
argument_list|()
operator|==
name|doc
condition|)
block|{
name|node
operator|=
name|expandedDoc
operator|.
name|getNode
argument_list|(
name|node
operator|.
name|getNodeNumber
argument_list|()
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|node
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Internal error: nodeId == null"
argument_list|)
throw|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
name|nodeId
operator|=
name|rootId
expr_stmt|;
else|else
name|nodeId
operator|=
name|rootId
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|newDoc
argument_list|,
name|nodeId
argument_list|,
name|node
operator|.
name|getNodeType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
comment|// replace the node by the NodeProxy
name|items
index|[
name|j
index|]
operator|.
name|item
operator|=
name|p
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|set
operator|.
name|add
argument_list|(
operator|(
name|NodeProxy
operator|)
name|items
index|[
name|i
index|]
operator|.
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|.
name|add
argument_list|(
operator|(
name|NodeProxy
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|set
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sequence cannot be converted into"
operator|+
literal|" a node set. Item type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|itemType
argument_list|)
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.value.Sequence#isPersistentSet()     */
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|itemType
operator|!=
name|Type
operator|.
name|ANY_TYPE
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|itemType
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|NodeValue
name|v
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|v
operator|=
operator|(
name|NodeValue
operator|)
name|items
index|[
name|i
index|]
operator|.
name|item
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|MemoryNodeSet
name|toMemNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
return|return
name|MemoryNodeSet
operator|.
name|EMPTY
return|;
if|if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|ANY_TYPE
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|itemType
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sequence cannot be converted into"
operator|+
literal|" a node set. Item type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|itemType
argument_list|)
argument_list|)
throw|;
block|}
name|NodeValue
name|v
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|v
operator|=
operator|(
name|NodeValue
operator|)
name|items
index|[
name|i
index|]
operator|.
name|item
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ValueSequence
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#removeDuplicates()      */
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
comment|// TODO: is this ever relevant?
block|}
specifier|private
name|void
name|setHasChanged
parameter_list|()
block|{
name|state
operator|=
operator|(
name|state
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|?
name|state
operator|=
literal|0
else|:
name|state
operator|+
literal|1
operator|)
expr_stmt|;
block|}
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
block|{
return|return
name|state
operator|!=
name|previousState
return|;
block|}
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|private
class|class
name|Entry
implements|implements
name|Comparable
block|{
name|Item
name|item
decl_stmt|;
name|AtomicValue
name|values
index|[]
decl_stmt|;
specifier|public
name|Entry
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|item
operator|=
name|item
expr_stmt|;
name|values
operator|=
operator|new
name|AtomicValue
index|[
name|orderSpecs
operator|.
name|length
index|]
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
name|orderSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Sequence
name|seq
init|=
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getSortExpression
argument_list|()
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|AtomicValue
operator|.
name|EMPTY_VALUE
expr_stmt|;
if|if
condition|(
name|seq
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|seq
operator|.
name|hasMany
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expected a single value for order expression "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getSortExpression
argument_list|()
argument_list|)
operator|+
literal|" ; found: "
operator|+
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 		 * @see java.lang.Comparable#compareTo(java.lang.Object) 		 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Entry
name|other
init|=
operator|(
name|Entry
operator|)
name|o
decl_stmt|;
name|int
name|cmp
init|=
literal|0
decl_stmt|;
name|AtomicValue
name|a
decl_stmt|,
name|b
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|a
operator|=
name|values
index|[
name|i
index|]
expr_stmt|;
name|b
operator|=
name|other
operator|.
name|values
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|a
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|a
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|a
operator|)
operator|.
name|isNaN
argument_list|()
operator|)
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|EMPTY_LEAST
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
else|else
name|cmp
operator|=
name|Constants
operator|.
name|SUPERIOR
expr_stmt|;
block|}
if|else if
condition|(
operator|(
name|b
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|b
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|b
operator|)
operator|.
name|isNaN
argument_list|()
operator|)
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|EMPTY_LEAST
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|Constants
operator|.
name|SUPERIOR
expr_stmt|;
else|else
name|cmp
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
block|}
if|else if
condition|(
name|a
operator|==
name|AtomicValue
operator|.
name|EMPTY_VALUE
operator|&&
name|b
operator|!=
name|AtomicValue
operator|.
name|EMPTY_VALUE
condition|)
block|{
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|EMPTY_LEAST
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
else|else
name|cmp
operator|=
name|Constants
operator|.
name|SUPERIOR
expr_stmt|;
block|}
if|else if
condition|(
name|b
operator|==
name|AtomicValue
operator|.
name|EMPTY_VALUE
operator|&&
name|a
operator|!=
name|AtomicValue
operator|.
name|EMPTY_VALUE
condition|)
block|{
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|EMPTY_LEAST
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|Constants
operator|.
name|SUPERIOR
expr_stmt|;
else|else
name|cmp
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
block|}
else|else
name|cmp
operator|=
name|a
operator|.
name|compareTo
argument_list|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getCollator
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|DESCENDING_ORDER
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|cmp
operator|*
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
name|Constants
operator|.
name|EQUAL
condition|)
break|break;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
block|}
return|return
name|cmp
return|;
block|}
block|}
specifier|private
class|class
name|OrderedValueSequenceIterator
implements|implements
name|SequenceIterator
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|count
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<
name|count
condition|)
block|{
return|return
name|items
index|[
name|pos
operator|++
index|]
operator|.
name|item
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

