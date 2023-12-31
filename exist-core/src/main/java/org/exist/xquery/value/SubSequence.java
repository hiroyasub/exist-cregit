begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|collections
operator|.
name|Collection
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
name|dom
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
name|dom
operator|.
name|persistent
operator|.
name|*
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
name|Cardinality
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
name|XQueryContext
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
name|Document
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple
operator|.
name|Tuple
import|;
end_import

begin_comment
comment|/**  * An immutable sequence that wraps an existing  * sequence, and provides access to a subset  * of the wrapped sequence, i.e. a sub-sequence.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|SubSequence
extends|extends
name|AbstractSequence
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SubSequence
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|long
name|fromInclusive
decl_stmt|;
specifier|private
specifier|final
name|long
name|toExclusive
decl_stmt|;
specifier|private
specifier|final
name|Sequence
name|sequence
decl_stmt|;
comment|/**      * @param fromInclusive The starting position in the {@code sequence} for the sub-sequence,      *     should be 1 for the first item in the {@code sequence}. This can be out-of-bounds      *     for the {@code sequence}.      * @param sequence The underlying sequence, for which we will provide a sub-sequence.      */
specifier|public
name|SubSequence
parameter_list|(
specifier|final
name|long
name|fromInclusive
parameter_list|,
specifier|final
name|Sequence
name|sequence
parameter_list|)
block|{
name|this
argument_list|(
name|fromInclusive
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|sequence
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param fromInclusive The starting position in the {@code sequence} for the sub-sequence,      *     should be 1 for the first item in the {@code sequence}. This can be out-of-bounds      *     for the {@code sequence}.      * @param toExclusive The End of sequence position for the sub-sequence. If you want everything      *     from the sequence, then this is the {@link Sequence#getItemCountLong()} + 1.      *     Specifying an ending position past the end of the sequence is allowed.      *     If you don't know the length of the sequence, then {@link Long#MAX_VALUE} can be used.      * @param sequence The underlying sequence, for which we will provide a sub-sequence.      */
specifier|public
name|SubSequence
parameter_list|(
specifier|final
name|long
name|fromInclusive
parameter_list|,
specifier|final
name|long
name|toExclusive
parameter_list|,
specifier|final
name|Sequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|fromInclusive
operator|=
name|fromInclusive
operator|<=
literal|0
condition|?
literal|1
else|:
name|fromInclusive
expr_stmt|;
name|this
operator|.
name|toExclusive
operator|=
name|toExclusive
expr_stmt|;
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot add an item to a sub-sequence"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|sequence
operator|.
name|getItemType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|SequenceIterator
operator|.
name|EMPTY_ITERATOR
return|;
block|}
return|return
operator|new
name|SubSequenceIterator
argument_list|(
name|fromInclusive
argument_list|,
name|toExclusive
argument_list|,
name|sequence
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|iterate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getItemCountLong
parameter_list|()
block|{
if|if
condition|(
name|toExclusive
operator|<
literal|1
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|subseqAvailable
init|=
name|sequence
operator|.
name|getItemCountLong
argument_list|()
operator|-
operator|(
name|fromInclusive
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|subseqAvailable
operator|<
literal|0
condition|)
block|{
name|subseqAvailable
operator|=
literal|0
expr_stmt|;
block|}
name|long
name|length
init|=
name|toExclusive
operator|-
name|fromInclusive
decl_stmt|;
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|subseqAvailable
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
specifier|final
name|long
name|length
init|=
name|toExclusive
operator|-
name|fromInclusive
decl_stmt|;
return|return
name|length
operator|<
literal|1
operator|||
name|sequence
operator|.
name|isEmpty
argument_list|()
operator|||
name|sequence
operator|.
name|getItemCountLong
argument_list|()
operator|-
name|fromInclusive
operator|<
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasOne
parameter_list|()
block|{
specifier|final
name|long
name|subseqAvailable
init|=
name|sequence
operator|.
name|getItemCountLong
argument_list|()
operator|-
operator|(
name|fromInclusive
operator|-
literal|1
operator|)
decl_stmt|;
specifier|final
name|long
name|length
init|=
name|toExclusive
operator|-
name|fromInclusive
decl_stmt|;
return|return
name|subseqAvailable
operator|>
literal|0
operator|&&
name|length
operator|==
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasMany
parameter_list|()
block|{
specifier|final
name|long
name|subseqAvailable
init|=
name|sequence
operator|.
name|getItemCountLong
argument_list|()
operator|-
operator|(
name|fromInclusive
operator|-
literal|1
operator|)
decl_stmt|;
specifier|final
name|long
name|length
init|=
name|toExclusive
operator|-
name|fromInclusive
decl_stmt|;
return|return
name|subseqAvailable
operator|>
literal|1
operator|&&
name|length
operator|>
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|Cardinality
name|getCardinality
parameter_list|()
block|{
specifier|final
name|long
name|length
init|=
name|toExclusive
operator|-
name|fromInclusive
decl_stmt|;
if|if
condition|(
name|length
operator|<
literal|1
operator|||
name|sequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Cardinality
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|long
name|subseqAvailable
init|=
name|sequence
operator|.
name|getItemCountLong
argument_list|()
operator|-
operator|(
name|fromInclusive
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|subseqAvailable
operator|<
literal|1
condition|)
block|{
return|return
name|Cardinality
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
name|subseqAvailable
operator|>
literal|0
operator|&&
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
block|}
if|if
condition|(
name|subseqAvailable
operator|>
literal|1
operator|&&
name|length
operator|>
literal|1
condition|)
block|{
return|return
name|Cardinality
operator|.
name|_MANY
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown Cardinality of: "
operator|+
name|toString
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Item
name|itemAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
comment|// NOTE: remember that itemAt(pos) is zero based index addressing!
specifier|final
name|long
name|length
init|=
name|toExclusive
operator|-
name|fromInclusive
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|long
name|subseqAvailable
init|=
name|sequence
operator|.
name|getItemCountLong
argument_list|()
operator|-
operator|(
name|fromInclusive
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|pos
operator|>=
name|subseqAvailable
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|sequence
operator|.
name|itemAt
argument_list|(
operator|(
name|int
operator|)
name|fromInclusive
operator|-
literal|1
operator|+
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|tail
parameter_list|()
block|{
if|if
condition|(
name|isEmpty
argument_list|()
operator|||
name|hasOne
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
return|return
operator|new
name|SubSequence
argument_list|(
name|fromInclusive
operator|+
literal|1
argument_list|,
name|toExclusive
operator|-
literal|1
argument_list|,
name|sequence
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
block|}
specifier|final
name|Map
argument_list|<
name|DocumentImpl
argument_list|,
name|Tuple2
argument_list|<
name|DocumentImpl
argument_list|,
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
argument_list|>
argument_list|>
name|expandedDocs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|NodeSet
name|nodeSet
init|=
operator|new
name|NewArrayNodeSet
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|iterator
init|=
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sub-sequence cannot be converted into"
operator|+
literal|" a node set. It contains an item of type: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|NodeValue
name|v
init|=
operator|(
name|NodeValue
operator|)
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
specifier|final
name|NodeProxy
name|p
init|=
name|makePersistent
argument_list|(
operator|(
name|NodeImpl
operator|)
name|v
argument_list|,
name|expandedDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sub-sequence cannot be converted into"
operator|+
literal|" a node set. It contains an in-memory node which cannot be persisted."
argument_list|)
throw|;
block|}
else|else
block|{
name|nodeSet
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|nodeSet
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
name|nodeSet
return|;
block|}
specifier|private
annotation|@
name|Nullable
name|NodeProxy
name|makePersistent
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
specifier|final
name|Map
argument_list|<
name|DocumentImpl
argument_list|,
name|Tuple2
argument_list|<
name|DocumentImpl
argument_list|,
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
argument_list|>
argument_list|>
name|expandedDocs
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// found an in-memory document
specifier|final
name|DocumentImpl
name|doc
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|node
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocumentImpl
name|expandedDoc
decl_stmt|;
specifier|final
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
name|newDoc
decl_stmt|;
if|if
condition|(
name|expandedDocs
operator|.
name|containsKey
argument_list|(
name|doc
argument_list|)
condition|)
block|{
specifier|final
name|Tuple2
argument_list|<
name|DocumentImpl
argument_list|,
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
argument_list|>
name|expandedDocNewDoc
init|=
name|expandedDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|expandedDoc
operator|=
name|expandedDocNewDoc
operator|.
name|_1
expr_stmt|;
name|newDoc
operator|=
name|expandedDocNewDoc
operator|.
name|_2
expr_stmt|;
block|}
else|else
block|{
comment|// make this document persistent: doc.makePersistent()
comment|// returns a map of all root node ids mapped to the corresponding
comment|// persistent node. We scan the current sequence and replace all
comment|// in-memory nodes with their new persistent node objects.
name|expandedDoc
operator|=
name|doc
operator|.
name|expandRefs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|newDoc
operator|=
name|expandedDoc
operator|.
name|makePersistent
argument_list|()
expr_stmt|;
name|expandedDocs
operator|.
name|put
argument_list|(
name|doc
argument_list|,
name|Tuple
argument_list|(
name|expandedDoc
argument_list|,
name|newDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newDoc
operator|!=
literal|null
condition|)
block|{
specifier|final
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
if|if
condition|(
name|node
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
specifier|final
name|Document
name|nodeOwnerDoc
decl_stmt|;
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
block|{
name|nodeOwnerDoc
operator|=
operator|(
name|Document
operator|)
name|node
expr_stmt|;
block|}
else|else
block|{
name|nodeOwnerDoc
operator|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|nodeOwnerDoc
operator|==
name|doc
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
block|{
name|node
operator|=
name|expandedDoc
operator|.
name|getAttribute
argument_list|(
name|node
operator|.
name|getNodeNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Internal error: nodeId == null"
argument_list|)
throw|;
block|}
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
block|{
name|nodeId
operator|=
name|rootId
expr_stmt|;
block|}
else|else
block|{
name|nodeId
operator|=
name|rootId
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
specifier|final
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
comment|// replace the node by the NodeProxy
return|return
name|p
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|MemoryNodeSet
name|toMemNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|MemoryNodeSet
operator|.
name|EMPTY
return|;
block|}
specifier|final
name|ValueSequence
name|memNodeSet
init|=
operator|new
name|ValueSequence
argument_list|(
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|DocumentImpl
argument_list|>
name|expandedDocs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|iterator
init|=
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sub-sequence cannot be converted into"
operator|+
literal|" a MemoryNodeSet. It contains items which are not nodes"
argument_list|)
throw|;
block|}
specifier|final
name|NodeValue
name|v
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sub-sequence cannot be converted into"
operator|+
literal|" a MemoryNodeSet. It contains nodes from stored resources."
argument_list|)
throw|;
block|}
specifier|final
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
name|node
init|=
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|item
decl_stmt|;
specifier|final
name|DocumentImpl
name|ownerDoc
init|=
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|?
operator|(
name|DocumentImpl
operator|)
name|node
else|:
name|node
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|ownerDoc
operator|.
name|hasReferenceNodes
argument_list|()
operator|&&
operator|!
name|expandedDocs
operator|.
name|contains
argument_list|(
name|ownerDoc
argument_list|)
condition|)
block|{
name|ownerDoc
operator|.
name|expand
argument_list|()
expr_stmt|;
name|expandedDocs
operator|.
name|add
argument_list|(
name|ownerDoc
argument_list|)
expr_stmt|;
block|}
name|memNodeSet
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|memNodeSet
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
try|try
block|{
specifier|final
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|iterator
init|=
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
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
block|{
specifier|final
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
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
block|{
name|docs
operator|.
name|add
argument_list|(
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|docs
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|DocumentSet
operator|.
name|EMPTY_DOCUMENT_SET
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|getCollectionIterator
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|CollectionIterator
argument_list|(
name|iterate
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getCollectionIterator
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
specifier|final
name|SequenceIterator
name|iterator
decl_stmt|;
try|try
block|{
name|iterator
operator|=
name|iterate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// should never happen!
block|}
comment|// needed to guard against returning true for an empty-sequence below
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|item
operator|instanceof
name|NodeValue
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|NodeValue
name|nv
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
if|if
condition|(
name|nv
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// else, all items were persistent
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|conversionPreference
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|javaClass
parameter_list|)
block|{
return|return
name|sequence
operator|.
name|conversionPreference
argument_list|(
name|javaClass
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
name|sequence
operator|.
name|isCacheable
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
name|sequence
operator|.
name|getState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChanged
parameter_list|(
specifier|final
name|int
name|previousState
parameter_list|)
block|{
return|return
name|sequence
operator|.
name|hasChanged
argument_list|(
name|previousState
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
return|return
name|sequence
operator|.
name|isCached
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setIsCached
parameter_list|(
specifier|final
name|boolean
name|cached
parameter_list|)
block|{
name|sequence
operator|.
name|setIsCached
argument_list|(
name|cached
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSelfAsContext
parameter_list|(
specifier|final
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
block|{
name|sequence
operator|.
name|setSelfAsContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearContext
parameter_list|(
specifier|final
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
block|{
name|sequence
operator|.
name|clearContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
block|{
name|sequence
operator|.
name|destroy
argument_list|(
name|context
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"SubSequence("
argument_list|)
operator|.
name|append
argument_list|(
literal|"fi="
argument_list|)
operator|.
name|append
argument_list|(
name|fromInclusive
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"te="
argument_list|)
operator|.
name|append
argument_list|(
name|toExclusive
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|CollectionIterator
implements|implements
name|Iterator
argument_list|<
name|Collection
argument_list|>
block|{
specifier|private
specifier|final
name|SequenceIterator
name|iterator
decl_stmt|;
specifier|private
name|Collection
name|nextCollection
init|=
literal|null
decl_stmt|;
name|CollectionIterator
parameter_list|(
specifier|final
name|SequenceIterator
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
name|next
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextCollection
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
name|next
parameter_list|()
block|{
specifier|final
name|Collection
name|oldCollection
init|=
name|nextCollection
decl_stmt|;
name|nextCollection
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
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
block|{
specifier|final
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
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
block|{
specifier|final
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|node
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|equals
argument_list|(
name|oldCollection
argument_list|)
condition|)
block|{
name|nextCollection
operator|=
name|p
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|oldCollection
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|SubSequenceIterator
implements|implements
name|SequenceIterator
block|{
specifier|private
name|long
name|position
decl_stmt|;
specifier|private
specifier|final
name|long
name|toExclusive
decl_stmt|;
specifier|private
specifier|final
name|SequenceIterator
name|iterator
decl_stmt|;
specifier|public
name|SubSequenceIterator
parameter_list|(
specifier|final
name|long
name|fromInclusive
parameter_list|,
specifier|final
name|long
name|toExclusive
parameter_list|,
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|position
operator|=
literal|1
expr_stmt|;
name|this
operator|.
name|toExclusive
operator|=
name|toExclusive
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|sequence
operator|.
name|iterate
argument_list|()
expr_stmt|;
comment|// move sequence iterator to start of sub-sequence
if|if
condition|(
name|position
operator|!=
name|fromInclusive
condition|)
block|{
comment|// move to start
if|if
condition|(
name|iterator
operator|.
name|skip
argument_list|(
name|fromInclusive
operator|-
name|position
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|position
operator|=
name|fromInclusive
expr_stmt|;
block|}
else|else
block|{
comment|// SequenceIterator does not support skipping, we have to iterate through each item :-/
for|for
control|(
init|;
name|position
operator|<
name|fromInclusive
condition|;
name|position
operator|++
control|)
block|{
name|iterator
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|position
operator|<
name|toExclusive
return|;
block|}
annotation|@
name|Override
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|position
operator|<
name|toExclusive
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|position
operator|++
expr_stmt|;
return|return
name|item
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skippable
parameter_list|()
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|iterator
operator|.
name|skippable
argument_list|()
argument_list|,
name|toExclusive
operator|-
name|position
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
parameter_list|)
block|{
specifier|final
name|long
name|seqSkipable
init|=
name|iterator
operator|.
name|skippable
argument_list|()
decl_stmt|;
if|if
condition|(
name|seqSkipable
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// underlying iterator does not support skipping
block|}
specifier|final
name|long
name|skip
init|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|seqSkipable
argument_list|,
name|toExclusive
operator|-
name|position
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|skip
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
comment|// nothing to skip
block|}
specifier|final
name|long
name|skipped
init|=
name|iterator
operator|.
name|skip
argument_list|(
name|skip
argument_list|)
decl_stmt|;
name|position
operator|+=
name|skipped
expr_stmt|;
return|return
name|skipped
return|;
block|}
block|}
block|}
end_class

end_unit

