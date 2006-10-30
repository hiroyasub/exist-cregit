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

begin_interface
specifier|public
interface|interface
name|NodeContainer
extends|extends
name|NodeChangeListener
extends|,
name|XACMLTreeNode
block|{
comment|/** 	 * Gets the number of children of this element.  Children 	 * should include the target of this element, the condition 	 * of a rule, child policies or policy sets of a policy set, 	 * or child rules of a policy.   	 *  	 * @return the number of children 	 */
specifier|abstract
name|int
name|getChildCount
parameter_list|()
function_decl|;
comment|/** 	 * Gets the child at the specified index.  Children 	 * may include the target of this element, the condition of 	 * a rule, child policies or policy sets of a policy set, 	 * or child rules of a policy. 	 *  	 * @param index The child's position 	 * @return the child 	 */
specifier|abstract
name|XACMLTreeNode
name|getChild
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** 	 * Gets the index of a particular child.  This operates on 	 * reference equality, not equals. 	 *  	 * @param child The child to obtain the index of 	 * @return The child's index, or -1 if it is not a child of 	 *  this policy element 	 */
specifier|abstract
name|int
name|indexOfChild
parameter_list|(
name|Object
name|child
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

