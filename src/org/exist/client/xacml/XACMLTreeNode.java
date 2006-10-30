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
name|XACMLTreeNode
block|{
comment|/** 	 * Returns true if this element has been modified.  If  	 * deep is true, then this takes into account the status 	 * of any descendants. 	 *  	 * @param deep if descendants should be included 	 * @return if this element has been modified 	 */
name|boolean
name|isModified
parameter_list|(
name|boolean
name|deep
parameter_list|)
function_decl|;
comment|/** 	 * Sets the modification status of this node only. 	 *  	 * @param flag Whether this node's state is different 	 *  from its state immediately after the last commit. 	 *  	 */
name|void
name|setModified
parameter_list|(
name|boolean
name|flag
parameter_list|)
function_decl|;
comment|/** 	 * Reverts the state of this element to the last commit. 	 * If deep is true, then this includes any descendants. 	 *  	 * @param deep 	 */
name|void
name|revert
parameter_list|(
name|boolean
name|deep
parameter_list|)
function_decl|;
comment|/** 	 * Commits the state of this element.  This state 	 * will be the state reverted to when revert is called. 	 * If deep is true, then this includes any descendants. 	 *  	 * @param deep 	 */
name|void
name|commit
parameter_list|(
name|boolean
name|deep
parameter_list|)
function_decl|;
comment|/** 	 * Returns the parent of this node, or null if this is 	 * the root node. 	 *  	 * @return This node's parent 	 */
name|NodeContainer
name|getParent
parameter_list|()
function_decl|;
comment|/** 	 * Serializes this node to a<code>String</code> 	 *  	 * @param indent Whether or not the XML should be indented 	 * @return The string representation of this node 	 */
name|String
name|serialize
parameter_list|(
name|boolean
name|indent
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

