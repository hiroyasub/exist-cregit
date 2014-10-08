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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  * Interface for Nodes in eXist  * used for both persistent and  * in-memory nodes.  *   * @param<T> The type of the persistent  * or in-memory document  *   * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|INode
parameter_list|<
name|T
extends|extends
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
parameter_list|>
extends|extends
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
extends|,
name|INodeHandle
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Get the qualified name of the Node      *       * @return The qualified name of the Node      */
specifier|public
name|QName
name|getQName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

