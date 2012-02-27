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

begin_comment
comment|/**  * This interface is used to report changes of the node id or the storage address  * of a node to classes which have to keep node sets up to date during processing.  * Used by the XUpdate classes to update the query result sets.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeIndexListener
block|{
comment|/**      * The internal id of a node has changed. The storage address is      * still the same, so one can find the changed node by comparing      * its storage address.      *       * @param node      */
name|void
name|nodeChanged
parameter_list|(
name|StoredNode
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

