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
name|net
operator|.
name|URI
import|;
end_import

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
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|Policy
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|PolicySet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|PolicyTreeElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|Target
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|combine
operator|.
name|CombiningAlgorithm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|combine
operator|.
name|PolicyCombiningAlgorithm
import|;
end_import

begin_class
specifier|public
class|class
name|PolicySetNode
extends|extends
name|AbstractPolicyNode
block|{
specifier|private
name|List
argument_list|<
name|AbstractPolicyNode
argument_list|>
name|children
decl_stmt|;
specifier|private
name|List
argument_list|<
name|AbstractPolicyNode
argument_list|>
name|originalChildren
decl_stmt|;
specifier|public
name|PolicySetNode
parameter_list|(
name|NodeContainer
name|parent
parameter_list|,
name|PolicySet
name|policySet
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
literal|null
argument_list|,
name|policySet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PolicySetNode
parameter_list|(
name|NodeContainer
name|parent
parameter_list|,
name|String
name|documentName
parameter_list|,
name|PolicySet
name|policySet
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|documentName
argument_list|,
name|policySet
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyTreeElement
argument_list|>
name|toCopy
init|=
name|policySet
operator|.
name|getChildren
argument_list|()
decl_stmt|;
name|children
operator|=
operator|new
name|ArrayList
argument_list|<
name|AbstractPolicyNode
argument_list|>
argument_list|(
name|toCopy
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|PolicyTreeElement
name|elem
range|:
name|toCopy
control|)
name|add
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|originalChildren
operator|=
operator|new
name|ArrayList
argument_list|<
name|AbstractPolicyNode
argument_list|>
argument_list|(
name|children
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PolicyTreeElement
name|create
parameter_list|()
block|{
return|return
name|createPolicySet
argument_list|()
return|;
block|}
specifier|public
name|PolicyTreeElement
name|create
parameter_list|(
name|URI
name|id
parameter_list|)
block|{
return|return
name|createPolicySet
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|PolicySet
name|createPolicySet
parameter_list|()
block|{
return|return
name|createPolicySet
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|PolicySet
name|createPolicySet
parameter_list|(
name|URI
name|id
parameter_list|)
block|{
specifier|final
name|CombiningAlgorithm
name|alg
init|=
name|getCombiningAlgorithm
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|alg
operator|instanceof
name|PolicyCombiningAlgorithm
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Combining algorithm must be a policy combining algorithm"
argument_list|)
throw|;
block|}
specifier|final
name|PolicyCombiningAlgorithm
name|algorithm
init|=
operator|(
name|PolicyCombiningAlgorithm
operator|)
name|alg
decl_stmt|;
specifier|final
name|Target
name|target
init|=
name|getTarget
argument_list|()
operator|.
name|getTarget
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PolicyTreeElement
argument_list|>
name|copy
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyTreeElement
argument_list|>
argument_list|(
name|children
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|PolicyElementNode
name|child
range|:
name|children
control|)
name|copy
operator|.
name|add
argument_list|(
name|child
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|URI
name|useId
init|=
operator|(
name|id
operator|==
literal|null
operator|)
condition|?
name|getId
argument_list|()
else|:
name|id
decl_stmt|;
return|return
operator|new
name|PolicySet
argument_list|(
name|useId
argument_list|,
name|algorithm
argument_list|,
name|getDescription
argument_list|()
argument_list|,
name|target
argument_list|,
name|copy
argument_list|)
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|PolicyTreeElement
name|element
parameter_list|)
block|{
name|add
argument_list|(
operator|-
literal|1
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|PolicyTreeElement
name|element
parameter_list|)
block|{
if|if
condition|(
name|element
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|element
operator|instanceof
name|Policy
condition|)
block|{
name|add
argument_list|(
name|index
argument_list|,
operator|new
name|PolicyNode
argument_list|(
name|this
argument_list|,
operator|(
name|Policy
operator|)
name|element
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|element
operator|instanceof
name|PolicySet
condition|)
block|{
name|add
argument_list|(
name|index
argument_list|,
operator|new
name|PolicySetNode
argument_list|(
name|this
argument_list|,
operator|(
name|PolicySet
operator|)
name|element
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only Policies and PolicySets can be top level elements."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
name|PolicyElementNode
name|node
parameter_list|)
block|{
name|add
argument_list|(
operator|-
literal|1
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|PolicyElementNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|node
operator|.
name|getParent
argument_list|()
operator|!=
name|this
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot add a PolicyElementNode to a parent other than its declared parent."
argument_list|)
throw|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|AbstractPolicyNode
condition|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|index
operator|=
name|children
operator|.
name|size
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot insert AbstractPolicy before Target"
argument_list|)
throw|;
block|}
name|children
operator|.
name|add
argument_list|(
name|index
operator|-
literal|1
argument_list|,
operator|(
name|AbstractPolicyNode
operator|)
name|node
argument_list|)
expr_stmt|;
name|setModified
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nodeAdded
argument_list|(
name|node
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only PolicyNodes and PolicySetNodes can be top level elements."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|PolicyElementNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|index
init|=
name|children
operator|.
name|indexOf
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return;
block|}
name|children
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|setModified
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nodeRemoved
argument_list|(
name|node
argument_list|,
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|containsId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
for|for
control|(
specifier|final
name|AbstractPolicyNode
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
name|child
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
name|children
operator|.
name|size
argument_list|()
operator|+
literal|1
return|;
comment|//+1 for the target
block|}
specifier|public
name|XACMLTreeNode
name|getChild
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
name|index
operator|==
literal|0
operator|)
condition|?
name|getTarget
argument_list|()
else|:
operator|(
name|XACMLTreeNode
operator|)
name|children
operator|.
name|get
argument_list|(
name|index
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|int
name|indexOfChild
parameter_list|(
name|Object
name|child
parameter_list|)
block|{
if|if
condition|(
name|child
operator|==
name|getTarget
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|int
name|ret
init|=
name|children
operator|.
name|indexOf
argument_list|(
name|child
argument_list|)
decl_stmt|;
return|return
operator|(
name|ret
operator|>=
literal|0
operator|)
condition|?
name|ret
operator|+
literal|1
else|:
operator|-
literal|1
return|;
block|}
specifier|public
name|boolean
name|isModified
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|isModified
argument_list|(
name|deep
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|deep
condition|)
block|{
for|for
control|(
specifier|final
name|PolicyElementNode
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
name|child
operator|.
name|isModified
argument_list|(
literal|true
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|revert
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
name|children
operator|=
name|originalChildren
expr_stmt|;
if|if
condition|(
name|deep
condition|)
block|{
for|for
control|(
specifier|final
name|PolicyElementNode
name|child
range|:
name|children
control|)
name|child
operator|.
name|revert
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|revert
argument_list|(
name|deep
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
name|originalChildren
operator|=
name|children
expr_stmt|;
if|if
condition|(
name|deep
condition|)
block|{
for|for
control|(
specifier|final
name|PolicyElementNode
name|child
range|:
name|children
control|)
name|child
operator|.
name|commit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|commit
argument_list|(
name|deep
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

