begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|xacml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TreeModelEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TreeModelListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|tree
operator|.
name|TreeModel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|tree
operator|.
name|TreePath
import|;
end_import

begin_comment
comment|/**  * XACML (exactly one)  * |  * --Top-level PolicySet (zero or more)  *   |  *   --Target (exactly one, may be empty)  *   |  *   --Policy (zero or more)  *     |  *     --Target (exactly one, may be empty)  *     |  *     --Rule (zero or more)  *       |  *       --Target (exactly one, may be empty)  *       |  *       --Condition (exactly one, may be empty)  *   */
end_comment

begin_class
specifier|public
class|class
name|XACMLTreeModel
implements|implements
name|NodeChangeListener
implements|,
name|TreeModel
block|{
specifier|private
name|List
argument_list|<
name|TreeModelListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|TreeModelListener
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|private
name|RootNode
name|root
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|XACMLTreeModel
parameter_list|()
block|{
block|}
specifier|public
name|XACMLTreeModel
parameter_list|(
name|RootNode
name|root
parameter_list|)
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Root node cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|root
operator|.
name|addNodeChangeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|(
name|Object
name|parent
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|instanceof
name|NodeContainer
condition|)
return|return
operator|(
operator|(
name|NodeContainer
operator|)
name|parent
operator|)
operator|.
name|getChildCount
argument_list|()
return|;
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isLeaf
parameter_list|(
name|Object
name|parent
parameter_list|)
block|{
return|return
operator|!
operator|(
name|parent
operator|==
name|root
operator|||
name|parent
operator|instanceof
name|PolicyElementNode
operator|)
return|;
block|}
specifier|public
name|Object
name|getChild
parameter_list|(
name|Object
name|parent
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|instanceof
name|NodeContainer
condition|)
return|return
operator|(
operator|(
name|NodeContainer
operator|)
name|parent
operator|)
operator|.
name|getChild
argument_list|(
name|index
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getIndexOfChild
parameter_list|(
name|Object
name|parent
parameter_list|,
name|Object
name|child
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|instanceof
name|NodeContainer
condition|)
return|return
operator|(
operator|(
name|NodeContainer
operator|)
name|parent
operator|)
operator|.
name|indexOfChild
argument_list|(
name|child
argument_list|)
return|;
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|void
name|valueForPathChanged
parameter_list|(
name|TreePath
name|path
parameter_list|,
name|Object
name|newValue
parameter_list|)
block|{
comment|//do nothing
block|}
specifier|public
name|void
name|addTreeModelListener
parameter_list|(
name|TreeModelListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeTreeModelListener
parameter_list|(
name|TreeModelListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasUnsavedChanges
parameter_list|()
block|{
return|return
name|root
operator|.
name|isModified
argument_list|(
literal|true
argument_list|)
return|;
block|}
specifier|public
name|void
name|revert
parameter_list|()
block|{
name|root
operator|.
name|revert
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|commit
parameter_list|()
block|{
name|root
operator|.
name|commit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|XACMLTreeNode
name|node
parameter_list|)
block|{
name|TreePath
name|path
init|=
name|getPathToNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|TreeModelEvent
name|event
init|=
operator|new
name|TreeModelEvent
argument_list|(
name|this
argument_list|,
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|TreeModelListener
name|listener
range|:
name|listeners
control|)
name|listener
operator|.
name|treeNodesChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|nodeAdded
parameter_list|(
name|XACMLTreeNode
name|node
parameter_list|,
name|int
name|newIndex
parameter_list|)
block|{
name|TreeModelEvent
name|event
init|=
name|getEvent
argument_list|(
name|node
argument_list|,
name|newIndex
argument_list|)
decl_stmt|;
for|for
control|(
name|TreeModelListener
name|listener
range|:
name|listeners
control|)
name|listener
operator|.
name|treeNodesInserted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|nodeRemoved
parameter_list|(
name|XACMLTreeNode
name|removedNode
parameter_list|,
name|int
name|oldChildIndex
parameter_list|)
block|{
name|TreeModelEvent
name|event
init|=
name|getEvent
argument_list|(
name|removedNode
argument_list|,
name|oldChildIndex
argument_list|)
decl_stmt|;
for|for
control|(
name|TreeModelListener
name|listener
range|:
name|listeners
control|)
name|listener
operator|.
name|treeNodesRemoved
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TreeModelEvent
name|getEvent
parameter_list|(
name|XACMLTreeNode
name|node
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|TreePath
name|path
init|=
name|getPathToNode
argument_list|(
name|node
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|int
index|[]
name|childIndices
init|=
block|{
name|index
block|}
decl_stmt|;
name|Object
index|[]
name|child
init|=
block|{
name|node
block|}
decl_stmt|;
name|TreeModelEvent
name|event
init|=
operator|new
name|TreeModelEvent
argument_list|(
name|this
argument_list|,
name|path
argument_list|,
name|childIndices
argument_list|,
name|child
argument_list|)
decl_stmt|;
return|return
name|event
return|;
block|}
specifier|public
specifier|static
name|TreePath
name|getPathToNode
parameter_list|(
name|XACMLTreeNode
name|node
parameter_list|)
block|{
name|NodeContainer
name|parent
init|=
name|node
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
return|return
operator|new
name|TreePath
argument_list|(
name|node
argument_list|)
return|;
return|return
name|getPathToNode
argument_list|(
name|parent
argument_list|)
operator|.
name|pathByAddingChild
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
end_class

end_unit

