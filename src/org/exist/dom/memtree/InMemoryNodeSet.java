begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|/**  * DOCUMENT ME!  */
end_comment

begin_class
specifier|public
class|class
name|InMemoryNodeSet
extends|extends
name|ValueSequence
block|{
specifier|public
specifier|final
specifier|static
name|InMemoryNodeSet
name|EMPTY
init|=
operator|new
name|InMemoryNodeSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|InMemoryNodeSet
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|InMemoryNodeSet
parameter_list|(
specifier|final
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|InMemoryNodeSet
parameter_list|(
specifier|final
name|Sequence
name|otherSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|otherSequence
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|DocumentImpl
argument_list|>
name|docs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
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
for|for
control|(
specifier|final
name|DocumentImpl
name|doc
range|:
name|docs
control|)
block|{
name|doc
operator|.
name|expand
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getAttributes
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectAttributes
argument_list|(
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getDescendantAttributes
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectDescendantAttributes
argument_list|(
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getChildren
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectChildren
argument_list|(
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getChildrenForParent
parameter_list|(
specifier|final
name|NodeImpl
name|parent
parameter_list|)
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|isChildOf
argument_list|(
name|parent
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getDescendants
parameter_list|(
specifier|final
name|boolean
name|includeSelf
parameter_list|,
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectDescendants
argument_list|(
name|includeSelf
argument_list|,
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getAncestors
parameter_list|(
specifier|final
name|boolean
name|includeSelf
parameter_list|,
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectAncestors
argument_list|(
name|includeSelf
argument_list|,
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getParents
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|NodeImpl
name|parent
init|=
operator|(
name|NodeImpl
operator|)
name|node
operator|.
name|selectParentNode
argument_list|()
decl_stmt|;
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
name|parent
argument_list|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getSelf
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|test
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NODE
operator|&&
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
operator|)
operator|||
name|test
operator|.
name|matches
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getPrecedingSiblings
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectPrecedingSiblings
argument_list|(
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getFollowingSiblings
parameter_list|(
specifier|final
name|NodeTest
name|test
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|InMemoryNodeSet
name|nodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
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
specifier|final
name|NodeImpl
name|node
init|=
operator|(
name|NodeImpl
operator|)
name|values
index|[
name|i
index|]
decl_stmt|;
name|node
operator|.
name|selectFollowingSiblings
argument_list|(
name|test
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
block|}
end_class

end_unit

