begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-03 Wolfgang M.  * Meier wolfgang@exist-db.org http://exist-db.org  *   * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|Range
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

begin_comment
comment|/**  * A fast node set implementation, based on arrays to store nodes and a hash  * map to organize documents.  *   * The class uses arrays to store all nodes belonging to one document. The hash  * map maps the document id to the array containing the nodes for the document.  * Nodes are just appended to the array. No order is guaranteed and calls to  * get/contains may fail although a node is present in the array (get/contains  * do a binary search and thus assume that the set is sorted). Also, duplicates  * are allowed. If you have to ensure that calls to get/contains return valid  * results at any time and no duplicates occur, use class  * {@link org.exist.dom.AVLTreeNodeSet}.  *   * Use this class, if you can either ensure that items are added in order, or  * no calls to contains/get are required during the creation phase. Only after  * a call to one of the iterator methods, the set will get sorted and  * duplicates removed.  *   * @author Wolfgang<wolfgang@exist-db.org>  * @since 0.9.3  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ExtArrayNodeSet
extends|extends
name|AbstractNodeSet
block|{
specifier|private
name|Int2ObjectHashMap
name|map
decl_stmt|;
specifier|private
name|int
name|initalSize
init|=
literal|128
decl_stmt|;
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|isSorted
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isInDocumentOrder
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|lastDoc
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Part
name|lastPart
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|state
init|=
literal|0
decl_stmt|;
specifier|public
name|ExtArrayNodeSet
parameter_list|()
block|{
name|this
operator|.
name|map
operator|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|512
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for ExtArrayNodeSet.  	 *  	 * The first argument specifies the expected number of documents in 	 * this node set. The second int argument specifies the default 	 * array size, which is used whenever a new array has to be allocated for 	 * nodes. The default array size can be overwritten by the sizeHint 	 * argument passed to {@link #add(NodeProxy, int). 	 *  	 * @param initialDocsCount 	 * @param initialArraySize 	 */
specifier|public
name|ExtArrayNodeSet
parameter_list|(
name|int
name|initialDocsCount
parameter_list|,
name|int
name|initialArraySize
parameter_list|)
block|{
name|this
operator|.
name|initalSize
operator|=
name|initialArraySize
expr_stmt|;
name|this
operator|.
name|map
operator|=
operator|new
name|Int2ObjectHashMap
argument_list|(
name|initialDocsCount
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExtArrayNodeSet
parameter_list|(
name|int
name|initialArraySize
parameter_list|)
block|{
name|this
argument_list|(
literal|512
argument_list|,
name|initialArraySize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|getPart
argument_list|(
name|proxy
operator|.
name|doc
operator|.
name|docId
argument_list|,
literal|true
argument_list|,
name|initalSize
argument_list|)
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
name|isSorted
operator|=
literal|false
expr_stmt|;
name|isInDocumentOrder
operator|=
literal|false
expr_stmt|;
name|setHasChanged
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Add a new node to the set. If a new array of nodes has to be allocated 	 * for the document, use the sizeHint parameter to determine the size of 	 * the newly allocated array. This will overwrite the default array size. 	 *  	 * If the size hint is correct, no further reallocations will be required. 	 */
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
name|getPart
argument_list|(
name|proxy
operator|.
name|doc
operator|.
name|docId
argument_list|,
literal|true
argument_list|,
name|sizeHint
operator|>
operator|-
literal|1
condition|?
name|sizeHint
else|:
name|initalSize
argument_list|)
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
name|isSorted
operator|=
literal|false
expr_stmt|;
name|isInDocumentOrder
operator|=
literal|false
expr_stmt|;
name|setHasChanged
argument_list|()
expr_stmt|;
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
name|getSizeHint
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|Part
name|part
init|=
name|getPart
argument_list|(
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|part
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|part
operator|.
name|length
return|;
block|}
specifier|private
name|Part
name|getPart
parameter_list|(
name|int
name|docId
parameter_list|,
name|boolean
name|create
parameter_list|,
name|int
name|sizeHint
parameter_list|)
block|{
if|if
condition|(
name|docId
operator|==
name|lastDoc
operator|&&
name|lastPart
operator|!=
literal|null
condition|)
return|return
name|lastPart
return|;
name|Part
name|part
init|=
operator|(
name|Part
operator|)
name|map
operator|.
name|get
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|part
operator|==
literal|null
operator|&&
name|create
condition|)
block|{
name|part
operator|=
operator|new
name|Part
argument_list|(
name|sizeHint
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|docId
argument_list|,
name|part
argument_list|)
expr_stmt|;
block|}
name|lastPart
operator|=
name|part
expr_stmt|;
name|lastDoc
operator|=
name|docId
expr_stmt|;
return|return
name|part
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#iterator() 	 */
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
name|sort
argument_list|()
expr_stmt|;
return|return
operator|new
name|ExtArrayIterator
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xpath.value.Sequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
return|return
operator|new
name|ExtArrayIterator
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.AbstractNodeSet#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
block|{
name|sort
argument_list|()
expr_stmt|;
return|return
operator|new
name|ExtArrayIterator
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#containsDoc(org.exist.dom.DocumentImpl) 	 */
specifier|public
name|boolean
name|containsDoc
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|doc
operator|.
name|docId
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#contains(org.exist.dom.DocumentImpl, long) 	 */
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
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|part
operator|==
literal|null
condition|?
literal|false
else|:
name|part
operator|.
name|contains
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#contains(org.exist.dom.NodeProxy) 	 */
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|proxy
operator|.
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|part
operator|==
literal|null
condition|?
literal|false
else|:
name|part
operator|.
name|contains
argument_list|(
name|proxy
operator|.
name|gid
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#addAll(org.exist.dom.NodeSet) 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
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
name|add
argument_list|(
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xpath.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.w3c.dom.NodeList#item(int) 	 */
specifier|public
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|NodeProxy
name|p
init|=
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
return|return
name|p
operator|==
literal|null
condition|?
literal|null
else|:
name|p
operator|.
name|getNode
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#get(int) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Part
name|part
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|map
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|part
operator|=
operator|(
name|Part
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|+
name|part
operator|.
name|length
operator|>
name|pos
condition|)
return|return
name|part
operator|.
name|get
argument_list|(
name|pos
operator|-
name|count
argument_list|)
return|;
name|count
operator|+=
name|part
operator|.
name|length
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#get(org.exist.dom.NodeProxy) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|p
operator|.
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|part
operator|==
literal|null
condition|?
literal|null
else|:
name|part
operator|.
name|get
argument_list|(
name|p
operator|.
name|gid
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#get(org.exist.dom.DocumentImpl, long) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
name|sort
argument_list|()
expr_stmt|;
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|part
operator|==
literal|null
condition|?
literal|null
else|:
name|part
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xpath.value.Sequence#itemAt(int) 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.NodeSet#remove(org.exist.dom.NodeProxy) 	 */
specifier|public
name|void
name|remove
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|node
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|part
operator|==
literal|null
condition|)
return|return;
name|part
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|part
operator|.
name|length
operator|==
literal|0
condition|)
name|map
operator|.
name|remove
argument_list|(
name|node
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|setHasChanged
argument_list|()
expr_stmt|;
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
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|part
operator|.
name|getRange
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|)
return|;
block|}
specifier|public
name|NodeSet
name|hasChildrenInSet
parameter_list|(
name|NodeProxy
name|parent
parameter_list|,
name|int
name|mode
parameter_list|,
name|boolean
name|rememberContext
parameter_list|)
block|{
specifier|final
name|Part
name|part
init|=
name|getPart
argument_list|(
name|parent
operator|.
name|doc
operator|.
name|docId
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|part
operator|==
literal|null
condition|)
return|return
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
return|;
return|return
name|part
operator|.
name|getChildrenInSet
argument_list|(
name|parent
argument_list|,
name|mode
argument_list|,
name|rememberContext
argument_list|)
return|;
block|}
specifier|public
name|void
name|sort
parameter_list|()
block|{
comment|//		long start = System.currentTimeMillis();
if|if
condition|(
name|isSorted
condition|)
return|return;
name|Part
name|part
decl_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|map
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|part
operator|=
operator|(
name|Part
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|part
operator|.
name|sort
argument_list|()
expr_stmt|;
name|size
operator|+=
name|part
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
block|}
name|isSorted
operator|=
literal|true
expr_stmt|;
name|isInDocumentOrder
operator|=
literal|false
expr_stmt|;
comment|//		System.out.println("sort took " + (System.currentTimeMillis() -
comment|// start) + "ms.");
block|}
specifier|public
name|void
name|sortInDocumentOrder
parameter_list|()
block|{
comment|//		long start = System.currentTimeMillis();
if|if
condition|(
name|isInDocumentOrder
condition|)
return|return;
name|Part
name|part
decl_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|map
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|part
operator|=
operator|(
name|Part
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|part
operator|.
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
name|size
operator|+=
name|part
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
block|}
name|isSorted
operator|=
literal|false
expr_stmt|;
name|isInDocumentOrder
operator|=
literal|true
expr_stmt|;
comment|//		System.out.println("in-document-order sort took " +
comment|// (System.currentTimeMillis() - start) + "ms.");
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xpath.value.AbstractSequence#setSelfAsContext() 	 */
specifier|public
name|void
name|setSelfAsContext
parameter_list|()
block|{
name|Part
name|part
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|map
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|part
operator|=
operator|(
name|Part
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|part
operator|.
name|setSelfAsContext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSorted
condition|)
name|size
operator|=
literal|0
expr_stmt|;
name|Part
name|part
decl_stmt|;
name|DocumentSet
name|ds
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|,
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|map
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|part
operator|=
operator|(
name|Part
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isSorted
condition|)
block|{
name|part
operator|.
name|sort
argument_list|()
expr_stmt|;
name|size
operator|+=
name|part
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|part
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|=
name|part
operator|.
name|array
index|[
name|j
index|]
operator|.
name|doc
expr_stmt|;
if|if
condition|(
name|last
operator|==
literal|null
operator|||
name|last
operator|.
name|docId
operator|!=
name|doc
operator|.
name|docId
condition|)
name|ds
operator|.
name|add
argument_list|(
name|part
operator|.
name|array
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|last
operator|=
name|doc
expr_stmt|;
block|}
block|}
name|isSorted
operator|=
literal|true
expr_stmt|;
return|return
name|ds
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.AbstractNodeSet#hasChanged(int) 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.dom.AbstractNodeSet#getState() 	 */
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|private
specifier|static
class|class
name|Part
block|{
name|NodeProxy
name|array
index|[]
decl_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
name|Part
parameter_list|(
name|int
name|initialSize
parameter_list|)
block|{
name|array
operator|=
operator|new
name|NodeProxy
index|[
name|initialSize
index|]
expr_stmt|;
block|}
name|void
name|add
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
comment|// just check if this node has already been added. We only
comment|// check the last entry, which should avoid most of the likely
comment|// duplicates. The remaining duplicates are removed by
comment|// removeDuplicates().
if|if
condition|(
name|length
operator|>
literal|0
operator|&&
name|array
index|[
name|length
operator|-
literal|1
index|]
operator|.
name|gid
operator|==
name|p
operator|.
name|gid
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|length
operator|==
name|array
operator|.
name|length
condition|)
block|{
comment|//int newLength = (length * 3)/2 + 1;
specifier|final
name|int
name|newLength
init|=
name|length
operator|<<
literal|1
decl_stmt|;
name|NodeProxy
name|temp
index|[]
init|=
operator|new
name|NodeProxy
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|temp
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|array
operator|=
name|temp
expr_stmt|;
block|}
name|array
index|[
name|length
operator|++
index|]
operator|=
name|p
expr_stmt|;
block|}
name|boolean
name|contains
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|gid
argument_list|)
operator|!=
literal|null
return|;
block|}
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|array
index|[
name|pos
index|]
return|;
block|}
name|NodeProxy
name|get
parameter_list|(
name|long
name|gid
parameter_list|)
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|/
literal|2
expr_stmt|;
name|p
operator|=
name|array
index|[
name|mid
index|]
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|gid
operator|==
name|gid
condition|)
return|return
name|p
return|;
if|if
condition|(
name|p
operator|.
name|gid
operator|>
name|gid
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|void
name|sort
parameter_list|()
block|{
name|FastQSort
operator|.
name|sortByNodeId
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|void
name|sortInDocumentOrder
parameter_list|()
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|array
argument_list|,
operator|new
name|DocumentOrderComparator
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** 		 * Check if the node identified by its node id has an ancestor 		 * contained in this node set and return the ancestor found. 		 *  		 * If directParent is true, only immediate ancestors (parents) are 		 * considered. Otherwise the method will call itself recursively for 		 * all the node's parents. 		 *  		 * If includeSelf is true, the method returns also true if the node 		 * itself is contained in the node set. 		 */
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
while|while
condition|(
name|gid
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|level
operator|==
literal|0
condition|)
name|gid
operator|=
operator|-
literal|1
expr_stmt|;
else|else
comment|// calculate parent's gid
name|gid
operator|=
operator|(
name|gid
operator|-
name|doc
operator|.
name|treeLevelStartPoints
index|[
name|level
index|]
operator|)
operator|/
name|doc
operator|.
name|treeLevelOrder
index|[
name|level
index|]
operator|+
name|doc
operator|.
name|treeLevelStartPoints
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|temp
operator|=
name|get
argument_list|(
name|gid
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
operator|--
name|level
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/** 		 * Find all nodes in the current set being children of the specified 		 * parent. 		 *  		 * @param parent 		 * @param mode 		 * @param rememberContext 		 * @return 		 */
name|NodeSet
name|getChildrenInSet
parameter_list|(
name|NodeProxy
name|parent
parameter_list|,
name|int
name|mode
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
comment|// get the range of node ids reserved for children of the parent
comment|// node
name|Range
name|range
init|=
name|XMLUtil
operator|.
name|getChildRange
argument_list|(
name|parent
operator|.
name|doc
argument_list|,
name|parent
operator|.
name|gid
argument_list|)
decl_stmt|;
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
literal|0
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
comment|// do a binary search to pick some node in the range of valid child
comment|// ids
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|/
literal|2
expr_stmt|;
name|p
operator|=
name|array
index|[
name|mid
index|]
expr_stmt|;
if|if
condition|(
name|range
operator|.
name|inRange
argument_list|(
name|p
operator|.
name|gid
argument_list|)
condition|)
break|break;
comment|// found a node, break out
if|if
condition|(
name|p
operator|.
name|gid
operator|>
name|range
operator|.
name|getStart
argument_list|()
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|low
operator|>
name|high
condition|)
return|return
name|result
return|;
comment|// no node found
comment|// find the first child node in the range
while|while
condition|(
name|mid
operator|>
literal|0
operator|&&
name|array
index|[
name|mid
operator|-
literal|1
index|]
operator|.
name|gid
operator|>=
name|range
operator|.
name|getStart
argument_list|()
condition|)
operator|--
name|mid
expr_stmt|;
comment|// walk through the range of child nodes we found
for|for
control|(
name|int
name|i
init|=
name|mid
init|;
name|i
operator|<
name|length
operator|&&
name|array
index|[
name|i
index|]
operator|.
name|gid
operator|<=
name|range
operator|.
name|getEnd
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
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
if|if
condition|(
name|rememberContext
condition|)
name|array
index|[
name|i
index|]
operator|.
name|addContextNode
argument_list|(
name|parent
argument_list|)
expr_stmt|;
else|else
name|array
index|[
name|i
index|]
operator|.
name|copyContext
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|array
index|[
name|i
index|]
argument_list|,
name|range
operator|.
name|getDistance
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NodeSet
operator|.
name|ANCESTOR
case|:
if|if
condition|(
name|rememberContext
condition|)
name|parent
operator|.
name|addContextNode
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
else|else
name|parent
operator|.
name|copyContext
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|parent
argument_list|,
literal|1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
name|NodeSet
name|getRange
parameter_list|(
name|long
name|lower
parameter_list|,
name|long
name|upper
parameter_list|)
block|{
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
operator|(
name|int
operator|)
operator|(
name|upper
operator|-
name|lower
operator|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
literal|0
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
comment|// do a binary search to pick some node in the range of valid node
comment|// ids
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|/
literal|2
expr_stmt|;
name|p
operator|=
name|array
index|[
name|mid
index|]
expr_stmt|;
if|if
condition|(
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
break|break;
comment|// found a node, break out
if|if
condition|(
name|p
operator|.
name|gid
operator|>
name|lower
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|low
operator|>
name|high
condition|)
return|return
name|result
return|;
comment|// no node found
comment|// find the first child node in the range
while|while
condition|(
name|mid
operator|>
literal|0
operator|&&
name|array
index|[
name|mid
operator|-
literal|1
index|]
operator|.
name|gid
operator|>=
name|lower
condition|)
operator|--
name|mid
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|mid
init|;
name|i
operator|<
name|length
operator|&&
name|array
index|[
name|i
index|]
operator|.
name|gid
operator|<=
name|upper
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|void
name|remove
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
operator|-
literal|1
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|/
literal|2
expr_stmt|;
name|p
operator|=
name|array
index|[
name|mid
index|]
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|gid
operator|==
name|node
operator|.
name|gid
condition|)
break|break;
if|if
condition|(
name|p
operator|.
name|gid
operator|>
name|node
operator|.
name|gid
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|low
operator|>
name|high
condition|)
return|return;
comment|// not found
if|if
condition|(
name|mid
operator|<
name|length
operator|-
literal|1
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
name|mid
operator|+
literal|1
argument_list|,
name|array
argument_list|,
name|mid
argument_list|,
name|length
operator|-
name|mid
operator|-
literal|1
argument_list|)
expr_stmt|;
operator|--
name|length
expr_stmt|;
block|}
comment|/** 		 * Remove all duplicate nodes from this part. 		 *  		 * @return the new length of the part, after removing all duplicates 		 */
name|int
name|removeDuplicates
parameter_list|()
block|{
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
literal|1
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|array
index|[
name|i
index|]
operator|.
name|gid
operator|!=
name|array
index|[
name|j
index|]
operator|.
name|gid
condition|)
block|{
if|if
condition|(
name|i
operator|!=
operator|++
name|j
condition|)
name|array
index|[
name|j
index|]
operator|=
name|array
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|length
operator|=
operator|++
name|j
expr_stmt|;
return|return
name|length
return|;
block|}
specifier|final
name|void
name|setSelfAsContext
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|array
index|[
name|i
index|]
operator|.
name|addContextNode
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|ExtArrayIterator
implements|implements
name|Iterator
implements|,
name|SequenceIterator
block|{
name|Iterator
name|docsIterator
decl_stmt|;
name|Part
name|currentPart
init|=
literal|null
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|NodeProxy
name|next
init|=
literal|null
decl_stmt|;
name|ExtArrayIterator
parameter_list|()
block|{
name|docsIterator
operator|=
name|map
operator|.
name|valueIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|docsIterator
operator|.
name|hasNext
argument_list|()
condition|)
name|currentPart
operator|=
operator|(
name|Part
operator|)
name|docsIterator
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentPart
operator|!=
literal|null
operator|&&
name|currentPart
operator|.
name|length
operator|>
literal|0
condition|)
name|next
operator|=
name|currentPart
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/* 		 * (non-Javadoc) 		 *  		 * @see java.util.Iterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
comment|/* 		 * (non-Javadoc) 		 *  		 * @see java.util.Iterator#next() 		 */
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|NodeProxy
name|n
init|=
name|next
decl_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
if|if
condition|(
operator|++
name|pos
operator|==
name|currentPart
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|docsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentPart
operator|=
operator|(
name|Part
operator|)
name|docsIterator
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentPart
operator|!=
literal|null
operator|&&
name|currentPart
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|next
operator|=
name|currentPart
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
else|else
name|next
operator|=
name|currentPart
operator|.
name|get
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/* 		 * (non-Javadoc) 		 *  		 * @see org.exist.xpath.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
return|return
operator|(
name|Item
operator|)
name|next
argument_list|()
return|;
block|}
comment|/* 		 * (non-Javadoc) 		 *  		 * @see java.util.Iterator#remove() 		 */
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
block|}
block|}
end_class

end_unit

