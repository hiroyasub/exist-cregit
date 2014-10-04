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
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_comment
comment|/**  * Interface for handling Nodes in eXist  * used for both persistent and  * in-memory nodes.  *   * @param<T> The type of the persistent  * or in-memory document  *   * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|INodeHandle
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
block|{
comment|/**      * Get the ID of the Node      *       * @return The ID of the Node      */
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
comment|/**      * Get the type of the node      */
specifier|public
name|short
name|getNodeType
parameter_list|()
function_decl|;
comment|//TODO convert to enum? what about persistence of the enum id (if it is ever persisted?)?
comment|/**      * @see org.w3c.dom.Node#getOwnerDocument()      *       * @return The persistent Owner Document      */
specifier|public
name|T
name|getOwnerDocument
parameter_list|()
function_decl|;
comment|//TODO consider extracting T into "org.exist.dom.IDocument extends org.w3c.com.Document" and returning an IDocument here
block|}
end_interface

end_unit

