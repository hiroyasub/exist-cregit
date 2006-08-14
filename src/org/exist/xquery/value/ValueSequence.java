begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|dom
operator|.
name|ExtArrayNodeSet
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
name|dom
operator|.
name|DocumentSet
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
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_comment
comment|/**  * A sequence that may contain a mixture of atomic values and nodes.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ValueSequence
extends|extends
name|AbstractSequence
block|{
specifier|private
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ValueSequence
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Do not change the -1 value since size computation relies on this start value
specifier|private
specifier|final
specifier|static
name|int
name|UNSET_SIZE
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|INITIAL_SIZE
init|=
literal|64
decl_stmt|;
specifier|private
name|Item
index|[]
name|values
decl_stmt|;
specifier|private
name|int
name|size
init|=
name|UNSET_SIZE
decl_stmt|;
comment|// used to keep track of the type of added items.
comment|// will be Type.ANY_TYPE if the type is unknown
comment|// and Type.ITEM if there are items of mixed type.
specifier|private
name|int
name|itemType
init|=
name|Type
operator|.
name|ANY_TYPE
decl_stmt|;
specifier|private
name|boolean
name|noDuplicates
init|=
literal|false
decl_stmt|;
specifier|public
name|ValueSequence
parameter_list|()
block|{
name|this
argument_list|(
name|INITIAL_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ValueSequence
parameter_list|(
name|int
name|initialSize
parameter_list|)
block|{
name|values
operator|=
operator|new
name|Item
index|[
name|initialSize
index|]
expr_stmt|;
block|}
specifier|public
name|ValueSequence
parameter_list|(
name|Sequence
name|otherSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|values
operator|=
operator|new
name|Item
index|[
name|otherSequence
operator|.
name|getLength
argument_list|()
index|]
expr_stmt|;
name|addAll
argument_list|(
name|otherSequence
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|size
operator|=
name|UNSET_SIZE
expr_stmt|;
name|itemType
operator|=
name|Type
operator|.
name|ANY_TYPE
expr_stmt|;
name|noDuplicates
operator|=
literal|false
expr_stmt|;
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
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
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
operator|++
name|size
expr_stmt|;
name|ensureCapacity
argument_list|()
expr_stmt|;
name|values
index|[
name|size
index|]
operator|=
name|item
expr_stmt|;
if|if
condition|(
name|itemType
operator|==
name|item
operator|.
name|getType
argument_list|()
condition|)
return|return;
if|else if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
name|itemType
operator|=
name|item
operator|.
name|getType
argument_list|()
expr_stmt|;
else|else
name|itemType
operator|=
name|Type
operator|.
name|getCommonSuperType
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|itemType
argument_list|)
expr_stmt|;
name|noDuplicates
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|otherSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|otherSequence
operator|==
literal|null
condition|)
return|return;
name|SequenceIterator
name|iterator
init|=
name|otherSequence
operator|.
name|iterate
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Iterator == null: "
operator|+
name|otherSequence
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|add
argument_list|(
name|iterator
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|itemType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|?
name|Type
operator|.
name|ITEM
else|:
name|itemType
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//		removeDuplicates();
return|return
operator|new
name|ValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
block|{
comment|//		removeDuplicates();
return|return
operator|new
name|ValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
comment|//		removeDuplicates();
return|return
name|size
operator|+
literal|1
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
return|return
name|values
index|[
name|pos
index|]
return|;
block|}
comment|/**      * Makes all in-memory nodes in this sequence persistent,      * so they can be handled like other node sets.      *  	 * @see org.exist.xquery.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|size
operator|==
name|UNSET_SIZE
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
name|NodeSet
name|set
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
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
operator|<=
name|size
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
name|values
index|[
name|i
index|]
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
comment|// found an in-memory document
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
comment|// make this document persistent: doc.makePersistent()
comment|// returns a map of all root node ids mapped to the corresponding
comment|// persistent node. We scan the current sequence and replace all
comment|// in-memory nodes with their new persistent node objects.
name|Int2ObjectHashMap
name|newRoots
init|=
name|doc
operator|.
name|makePersistent
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
operator|<=
name|size
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
name|values
index|[
name|j
index|]
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
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|newRoots
operator|.
name|get
argument_list|(
name|node
operator|.
name|getNodeNumber
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
name|values
index|[
name|j
index|]
operator|=
name|p
expr_stmt|;
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
name|values
index|[
name|i
index|]
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
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
name|UNSET_SIZE
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
operator|<=
name|size
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
name|values
index|[
name|i
index|]
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
name|void
name|sortInDocumentOrder
parameter_list|()
block|{
name|removeDuplicates
argument_list|()
expr_stmt|;
name|FastQSort
operator|.
name|sort
argument_list|(
name|values
argument_list|,
operator|new
name|MixedNodeValueComparator
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|int
name|newSize
init|=
operator|(
name|size
operator|*
literal|3
operator|)
operator|/
literal|2
decl_stmt|;
name|Item
name|newValues
index|[]
init|=
operator|new
name|Item
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|newValues
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|values
operator|=
name|newValues
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
if|if
condition|(
name|noDuplicates
condition|)
return|return;
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
name|ATOMIC
argument_list|)
condition|)
return|return;
comment|// check if the sequence contains nodes
name|boolean
name|hasNodes
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
name|hasNodes
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hasNodes
condition|)
return|return;
name|Set
name|nodes
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|nodes
operator|.
name|contains
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|Item
name|item
init|=
name|values
index|[
name|i
index|]
decl_stmt|;
name|values
index|[
name|j
operator|++
index|]
operator|=
name|item
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|values
index|[
name|j
operator|++
index|]
operator|=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
name|size
operator|=
name|j
operator|-
literal|1
expr_stmt|;
name|noDuplicates
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
operator|(
operator|(
name|NodeValue
operator|)
name|values
index|[
name|i
index|]
operator|)
operator|.
name|clearContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.value.Sequence#getDocumentSet()     */
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|NodeValue
name|node
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|values
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
name|docs
operator|.
name|add
argument_list|(
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|docs
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
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
literal|"("
argument_list|)
expr_stmt|;
name|boolean
name|moreThanOne
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
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
name|moreThanOne
condition|)
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|moreThanOne
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|next
operator|.
name|toString
argument_list|()
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
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|"ValueSequence.toString() failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|ValueSequenceIterator
implements|implements
name|SequenceIterator
block|{
specifier|private
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|public
name|ValueSequenceIterator
parameter_list|()
block|{
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<=
name|size
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
operator|<=
name|size
condition|)
return|return
name|values
index|[
name|pos
operator|++
index|]
return|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

