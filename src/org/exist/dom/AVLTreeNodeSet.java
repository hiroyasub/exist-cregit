begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|java
operator|.
name|util
operator|.
name|Stack
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

begin_class
specifier|public
class|class
name|AVLTreeNodeSet
extends|extends
name|AbstractNodeSet
block|{
specifier|private
name|Node
name|root
decl_stmt|;
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
specifier|public
name|AVLTreeNodeSet
parameter_list|()
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
return|return
operator|new
name|InorderTraversal
argument_list|()
return|;
block|}
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
block|{
return|return
operator|new
name|InorderTraversal
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#addAll(org.exist.dom.NodeSet) 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#item(int) 	 */
specifier|public
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|pos
condition|)
return|return
name|p
operator|.
name|getNode
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#get(int) 	 */
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
name|NodeProxy
operator|)
name|itemAt
argument_list|(
name|pos
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#get(org.exist.dom.NodeProxy) 	 */
specifier|public
specifier|final
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
name|Node
name|n
init|=
name|searchData
argument_list|(
name|p
argument_list|)
decl_stmt|;
return|return
name|n
operator|==
literal|null
condition|?
literal|null
else|:
name|n
operator|.
name|getData
argument_list|()
return|;
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
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|pos
condition|)
return|return
name|p
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
operator|new
name|Node
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
return|return;
block|}
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|c
init|=
name|tempNode
operator|.
name|data
operator|.
name|compareTo
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// inserts s into left subtree.
if|if
condition|(
name|tempNode
operator|.
name|hasLeftChild
argument_list|()
condition|)
block|{
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|Node
name|newNode
init|=
name|tempNode
operator|.
name|addLeft
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
name|balance
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
comment|// inserts s to right subtree
if|if
condition|(
name|tempNode
operator|.
name|hasRightChild
argument_list|()
condition|)
block|{
name|tempNode
operator|=
name|tempNode
operator|.
name|rightChild
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|Node
name|newNode
init|=
name|tempNode
operator|.
name|addRight
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
name|balance
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
specifier|public
name|Node
name|getMinNode
parameter_list|()
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
while|while
condition|(
name|tempNode
operator|.
name|hasLeftChild
argument_list|()
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|getLeftChild
argument_list|()
expr_stmt|;
return|return
name|tempNode
return|;
block|}
specifier|public
name|Node
name|getMaxNode
parameter_list|()
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
while|while
condition|(
name|tempNode
operator|.
name|hasRightChild
argument_list|()
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|getRightChild
argument_list|()
expr_stmt|;
return|return
name|tempNode
return|;
block|}
specifier|private
name|void
name|balance
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|Node
name|currentNode
decl_stmt|,
name|currentParent
decl_stmt|;
name|currentNode
operator|=
name|node
expr_stmt|;
name|currentParent
operator|=
name|node
operator|.
name|parent
expr_stmt|;
while|while
condition|(
name|currentNode
operator|!=
name|root
condition|)
block|{
name|int
name|h
init|=
name|currentParent
operator|.
name|height
decl_stmt|;
name|currentParent
operator|.
name|setHeight
argument_list|()
expr_stmt|;
if|if
condition|(
name|h
operator|==
name|currentParent
operator|.
name|height
condition|)
return|return;
comment|// Case 1
if|if
condition|(
name|currentParent
operator|.
name|balanced
argument_list|()
condition|)
block|{
name|currentNode
operator|=
name|currentParent
expr_stmt|;
name|currentParent
operator|=
name|currentNode
operator|.
name|parent
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentParent
operator|.
name|leftHeight
argument_list|()
operator|-
name|currentParent
operator|.
name|rightHeight
argument_list|()
operator|==
literal|2
condition|)
block|{
name|Node
name|nodeA
init|=
name|currentParent
decl_stmt|,
name|nodeB
init|=
name|nodeA
operator|.
name|getLeftChild
argument_list|()
decl_stmt|,
name|nodeC
init|=
name|nodeB
operator|.
name|getLeftChild
argument_list|()
decl_stmt|,
name|nodeD
init|=
name|nodeB
operator|.
name|getRightChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeB
operator|.
name|leftHeight
argument_list|()
operator|>
name|nodeB
operator|.
name|rightHeight
argument_list|()
condition|)
block|{
comment|// right rotation for Case 2
name|nodeA
operator|.
name|addLeftChild
argument_list|(
name|nodeD
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeA
operator|!=
name|root
condition|)
block|{
if|if
condition|(
name|nodeA
operator|.
name|isLeftChild
argument_list|()
condition|)
name|nodeA
operator|.
name|parent
operator|.
name|addLeftChild
argument_list|(
name|nodeB
argument_list|)
expr_stmt|;
else|else
name|nodeA
operator|.
name|parent
operator|.
name|addRightChild
argument_list|(
name|nodeB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|root
operator|=
name|nodeB
expr_stmt|;
block|}
empty_stmt|;
name|nodeB
operator|.
name|addRightChild
argument_list|(
name|nodeA
argument_list|)
expr_stmt|;
name|nodeA
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|nodeB
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|currentNode
operator|=
name|nodeB
expr_stmt|;
name|currentParent
operator|=
name|currentNode
operator|.
name|parent
expr_stmt|;
continue|continue;
block|}
comment|// Case 3 and Case 4
name|Node
name|nodeE
init|=
literal|null
decl_stmt|,
name|nodeF
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodeD
operator|.
name|hasLeftChild
argument_list|()
condition|)
block|{
name|nodeE
operator|=
name|nodeD
operator|.
name|getLeftChild
argument_list|()
expr_stmt|;
name|nodeB
operator|.
name|addRightChild
argument_list|(
name|nodeE
argument_list|)
expr_stmt|;
block|}
else|else
name|nodeB
operator|.
name|removeRightChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeD
operator|.
name|hasRightChild
argument_list|()
condition|)
block|{
name|nodeF
operator|=
name|nodeD
operator|.
name|getRightChild
argument_list|()
expr_stmt|;
name|nodeA
operator|.
name|addLeftChild
argument_list|(
name|nodeF
argument_list|)
expr_stmt|;
block|}
else|else
name|nodeA
operator|.
name|removeLeftChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentParent
operator|!=
name|root
condition|)
block|{
if|if
condition|(
name|nodeA
operator|.
name|isLeftChild
argument_list|()
condition|)
name|nodeA
operator|.
name|parent
operator|.
name|addLeftChild
argument_list|(
name|nodeD
argument_list|)
expr_stmt|;
else|else
name|nodeA
operator|.
name|parent
operator|.
name|addRightChild
argument_list|(
name|nodeD
argument_list|)
expr_stmt|;
block|}
else|else
name|root
operator|=
name|nodeD
expr_stmt|;
name|nodeD
operator|.
name|addLeftChild
argument_list|(
name|nodeB
argument_list|)
expr_stmt|;
name|nodeD
operator|.
name|addRightChild
argument_list|(
name|nodeA
argument_list|)
expr_stmt|;
name|nodeB
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|nodeA
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|nodeD
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|currentNode
operator|=
name|nodeD
expr_stmt|;
name|currentParent
operator|=
name|currentNode
operator|.
name|parent
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentParent
operator|.
name|leftHeight
argument_list|()
operator|-
name|currentParent
operator|.
name|rightHeight
argument_list|()
operator|==
operator|-
literal|2
condition|)
block|{
name|Node
name|nodeA
init|=
name|currentParent
decl_stmt|,
name|nodeB
init|=
name|nodeA
operator|.
name|getRightChild
argument_list|()
decl_stmt|,
name|nodeC
init|=
name|nodeB
operator|.
name|getLeftChild
argument_list|()
decl_stmt|,
name|nodeD
init|=
name|nodeB
operator|.
name|getRightChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeB
operator|.
name|leftHeight
argument_list|()
operator|<
name|nodeB
operator|.
name|rightHeight
argument_list|()
condition|)
block|{
comment|// left rotation for Case 2
name|nodeA
operator|.
name|addRightChild
argument_list|(
name|nodeC
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeA
operator|!=
name|root
condition|)
block|{
if|if
condition|(
name|nodeA
operator|.
name|isLeftChild
argument_list|()
condition|)
name|nodeA
operator|.
name|parent
operator|.
name|addLeftChild
argument_list|(
name|nodeB
argument_list|)
expr_stmt|;
else|else
name|nodeA
operator|.
name|parent
operator|.
name|addRightChild
argument_list|(
name|nodeB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|root
operator|=
name|nodeB
expr_stmt|;
block|}
empty_stmt|;
name|nodeB
operator|.
name|addLeftChild
argument_list|(
name|nodeA
argument_list|)
expr_stmt|;
name|nodeA
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|nodeB
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|currentNode
operator|=
name|nodeB
expr_stmt|;
name|currentParent
operator|=
name|currentNode
operator|.
name|parent
expr_stmt|;
continue|continue;
block|}
comment|// Case 3 and Case 4
name|Node
name|nodeE
init|=
literal|null
decl_stmt|,
name|nodeF
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodeC
operator|.
name|hasLeftChild
argument_list|()
condition|)
block|{
name|nodeE
operator|=
name|nodeC
operator|.
name|getLeftChild
argument_list|()
expr_stmt|;
name|nodeA
operator|.
name|addRightChild
argument_list|(
name|nodeE
argument_list|)
expr_stmt|;
block|}
else|else
name|nodeA
operator|.
name|removeRightChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeC
operator|.
name|hasRightChild
argument_list|()
condition|)
block|{
name|nodeF
operator|=
name|nodeC
operator|.
name|getRightChild
argument_list|()
expr_stmt|;
name|nodeB
operator|.
name|addLeftChild
argument_list|(
name|nodeF
argument_list|)
expr_stmt|;
block|}
else|else
name|nodeB
operator|.
name|removeLeftChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeA
operator|!=
name|root
condition|)
block|{
if|if
condition|(
name|nodeA
operator|.
name|isLeftChild
argument_list|()
condition|)
name|nodeA
operator|.
name|parent
operator|.
name|addLeftChild
argument_list|(
name|nodeC
argument_list|)
expr_stmt|;
else|else
name|nodeA
operator|.
name|parent
operator|.
name|addRightChild
argument_list|(
name|nodeC
argument_list|)
expr_stmt|;
block|}
else|else
name|root
operator|=
name|nodeC
expr_stmt|;
name|nodeC
operator|.
name|addLeftChild
argument_list|(
name|nodeA
argument_list|)
expr_stmt|;
name|nodeC
operator|.
name|addRightChild
argument_list|(
name|nodeB
argument_list|)
expr_stmt|;
name|nodeB
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|nodeA
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|nodeC
operator|.
name|setHeight
argument_list|()
expr_stmt|;
name|currentNode
operator|=
name|nodeC
expr_stmt|;
name|currentParent
operator|=
name|currentNode
operator|.
name|parent
expr_stmt|;
continue|continue;
block|}
block|}
block|}
specifier|public
specifier|final
name|Node
name|searchData
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
while|while
condition|(
name|tempNode
operator|!=
literal|null
condition|)
block|{
name|int
name|c
init|=
name|tempNode
operator|.
name|data
operator|.
name|compareTo
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
return|return
name|tempNode
return|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|rightChild
expr_stmt|;
else|else
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
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
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
while|while
condition|(
name|tempNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tempNode
operator|.
name|data
operator|.
name|doc
operator|.
name|docId
operator|==
name|doc
operator|.
name|docId
condition|)
block|{
if|if
condition|(
name|tempNode
operator|.
name|data
operator|.
name|gid
operator|==
name|nodeId
condition|)
return|return
name|tempNode
operator|.
name|data
return|;
if|else if
condition|(
name|tempNode
operator|.
name|data
operator|.
name|gid
operator|<
name|nodeId
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|rightChild
expr_stmt|;
else|else
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
if|else if
condition|(
name|tempNode
operator|.
name|data
operator|.
name|doc
operator|.
name|docId
operator|<
name|doc
operator|.
name|docId
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|rightChild
expr_stmt|;
else|else
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
name|boolean
name|containsDoc
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
while|while
condition|(
name|tempNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tempNode
operator|.
name|data
operator|.
name|doc
operator|.
name|docId
operator|==
name|doc
operator|.
name|docId
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|tempNode
operator|.
name|data
operator|.
name|doc
operator|.
name|docId
operator|<
name|doc
operator|.
name|docId
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|rightChild
expr_stmt|;
else|else
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#contains(org.exist.dom.NodeProxy) 	 */
specifier|public
specifier|final
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
return|return
name|searchData
argument_list|(
name|proxy
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#contains(org.exist.dom.DocumentImpl, long) 	 */
specifier|public
specifier|final
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
return|return
name|get
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#remove(org.exist.dom.NodeProxy) 	 */
specifier|public
name|void
name|remove
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
block|{
name|Node
name|n
init|=
name|searchData
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|node
operator|.
name|gid
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//System.out.println("removing node " + n.data.gid);
name|removeNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|//System.out.println(getLength());
block|}
specifier|public
name|void
name|removeNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
operator|--
name|size
expr_stmt|;
name|Node
name|tempNode
init|=
name|node
decl_stmt|;
while|while
condition|(
name|tempNode
operator|.
name|hasLeftChild
argument_list|()
operator|||
name|tempNode
operator|.
name|hasRightChild
argument_list|()
condition|)
block|{
if|if
condition|(
name|tempNode
operator|.
name|hasLeftChild
argument_list|()
condition|)
block|{
name|tempNode
operator|=
name|tempNode
operator|.
name|getLeftChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|tempNode
operator|.
name|hasRightChild
argument_list|()
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|getRightChild
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tempNode
operator|=
name|tempNode
operator|.
name|getRightChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|tempNode
operator|.
name|hasLeftChild
argument_list|()
condition|)
name|tempNode
operator|=
name|tempNode
operator|.
name|getLeftChild
argument_list|()
expr_stmt|;
block|}
name|node
operator|.
name|setData
argument_list|(
name|tempNode
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tempNode
operator|==
name|root
condition|)
block|{
name|root
operator|=
literal|null
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|tempNode
operator|.
name|isLeftChild
argument_list|()
condition|)
block|{
name|node
operator|=
name|tempNode
operator|.
name|parent
expr_stmt|;
name|node
operator|.
name|removeLeftChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|hasRightChild
argument_list|()
condition|)
name|balance
argument_list|(
name|node
operator|.
name|getRightChild
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|balance
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
name|tempNode
operator|.
name|parent
expr_stmt|;
name|node
operator|.
name|removeRightChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|hasLeftChild
argument_list|()
condition|)
name|balance
argument_list|(
name|node
operator|.
name|getLeftChild
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|balance
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
expr|new
name|InorderTraversal
argument_list|()
operator|)
return|;
block|}
class|class
name|InorderTraversal
implements|implements
name|Iterator
implements|,
name|SequenceIterator
block|{
specifier|private
name|Stack
name|nodes
decl_stmt|;
specifier|public
name|InorderTraversal
parameter_list|()
block|{
name|nodes
operator|=
operator|new
name|Stack
argument_list|()
expr_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|Node
name|tempNode
init|=
name|root
decl_stmt|;
do|do
block|{
name|nodes
operator|.
name|push
argument_list|(
name|tempNode
argument_list|)
expr_stmt|;
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
do|while
condition|(
name|tempNode
operator|!=
literal|null
condition|)
do|;
block|}
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|nodes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|false
return|;
else|else
return|return
literal|true
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
name|Node
name|currentNode
init|=
operator|(
name|Node
operator|)
name|nodes
operator|.
name|peek
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentNode
operator|.
name|hasRightChild
argument_list|()
condition|)
block|{
name|Node
name|tempNode
init|=
name|currentNode
operator|.
name|rightChild
decl_stmt|;
do|do
block|{
name|nodes
operator|.
name|push
argument_list|(
name|tempNode
argument_list|)
expr_stmt|;
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
do|while
condition|(
name|tempNode
operator|!=
literal|null
condition|)
do|;
block|}
return|return
name|currentNode
operator|.
name|getData
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#remove() 		 */
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Method remove is not implemented"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
name|Node
name|currentNode
init|=
operator|(
name|Node
operator|)
name|nodes
operator|.
name|peek
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentNode
operator|.
name|hasRightChild
argument_list|()
condition|)
block|{
name|Node
name|tempNode
init|=
name|currentNode
operator|.
name|rightChild
decl_stmt|;
do|do
block|{
name|nodes
operator|.
name|push
argument_list|(
name|tempNode
argument_list|)
expr_stmt|;
name|tempNode
operator|=
name|tempNode
operator|.
name|leftChild
expr_stmt|;
block|}
do|while
condition|(
name|tempNode
operator|!=
literal|null
condition|)
do|;
block|}
return|return
name|currentNode
operator|.
name|getData
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|final
specifier|static
class|class
name|Node
block|{
specifier|public
name|Node
parameter_list|(
name|NodeProxy
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|void
name|setData
parameter_list|(
name|NodeProxy
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|NodeProxy
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|public
name|boolean
name|hasLeftChild
parameter_list|()
block|{
return|return
operator|(
name|leftChild
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|boolean
name|hasRightChild
parameter_list|()
block|{
return|return
operator|(
name|rightChild
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|Node
name|getLeftChild
parameter_list|()
block|{
return|return
name|leftChild
return|;
block|}
specifier|public
name|Node
name|getRightChild
parameter_list|()
block|{
return|return
name|rightChild
return|;
block|}
specifier|public
name|boolean
name|balanced
parameter_list|()
block|{
return|return
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|leftHeight
argument_list|()
operator|-
name|rightHeight
argument_list|()
argument_list|)
operator|<=
literal|1
operator|)
return|;
block|}
specifier|public
name|Node
name|addLeft
parameter_list|(
name|NodeProxy
name|data
parameter_list|)
block|{
name|Node
name|tempNode
init|=
operator|new
name|Node
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|leftChild
operator|=
name|tempNode
expr_stmt|;
name|tempNode
operator|.
name|parent
operator|=
name|this
expr_stmt|;
return|return
name|tempNode
return|;
block|}
specifier|public
name|Node
name|addLeftChild
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|leftChild
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
name|node
operator|.
name|parent
operator|=
name|this
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Node
name|addRight
parameter_list|(
name|NodeProxy
name|data
parameter_list|)
block|{
name|Node
name|tempNode
init|=
operator|new
name|Node
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|rightChild
operator|=
name|tempNode
expr_stmt|;
name|tempNode
operator|.
name|parent
operator|=
name|this
expr_stmt|;
return|return
name|tempNode
return|;
block|}
specifier|public
name|Node
name|addRightChild
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|rightChild
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
name|node
operator|.
name|parent
operator|=
name|this
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Node
name|removeLeftChild
parameter_list|()
block|{
name|Node
name|tempNode
init|=
name|leftChild
decl_stmt|;
name|leftChild
operator|=
literal|null
expr_stmt|;
return|return
name|tempNode
return|;
block|}
specifier|public
name|Node
name|removeRightChild
parameter_list|()
block|{
name|Node
name|tempNode
init|=
name|rightChild
decl_stmt|;
name|rightChild
operator|=
literal|null
expr_stmt|;
return|return
name|tempNode
return|;
block|}
specifier|public
name|int
name|degree
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|leftChild
operator|!=
literal|null
condition|)
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|rightChild
operator|!=
literal|null
condition|)
name|i
operator|++
expr_stmt|;
return|return
name|i
return|;
block|}
specifier|public
name|void
name|setHeight
parameter_list|()
block|{
name|height
operator|=
name|Math
operator|.
name|max
argument_list|(
name|leftHeight
argument_list|()
argument_list|,
name|rightHeight
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLeftChild
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|parent
operator|.
name|leftChild
operator|)
return|;
block|}
specifier|public
name|boolean
name|isRightChild
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|parent
operator|.
name|rightChild
operator|)
return|;
block|}
specifier|public
name|int
name|leftHeight
parameter_list|()
block|{
if|if
condition|(
name|hasLeftChild
argument_list|()
condition|)
return|return
operator|(
literal|1
operator|+
name|leftChild
operator|.
name|height
operator|)
return|;
else|else
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|rightHeight
parameter_list|()
block|{
if|if
condition|(
name|hasRightChild
argument_list|()
condition|)
return|return
operator|(
literal|1
operator|+
name|rightChild
operator|.
name|height
operator|)
return|;
else|else
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|height
parameter_list|()
block|{
return|return
name|height
return|;
block|}
name|NodeProxy
name|data
decl_stmt|;
name|Node
name|parent
decl_stmt|;
name|Node
name|leftChild
decl_stmt|;
name|Node
name|rightChild
decl_stmt|;
name|int
name|height
decl_stmt|;
block|}
block|}
end_class

end_unit

