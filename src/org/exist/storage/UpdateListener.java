begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|StoredNode
import|;
end_import

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

begin_interface
specifier|public
interface|interface
name|UpdateListener
block|{
comment|// Notification types:
comment|/** Notification type: a document was added to a collection */
specifier|public
specifier|final
specifier|static
name|int
name|ADD
init|=
literal|0
decl_stmt|;
comment|/** Notification type: a document has been updated */
specifier|public
specifier|final
specifier|static
name|int
name|UPDATE
init|=
literal|1
decl_stmt|;
comment|/** Notification type: a document was removed */
specifier|public
specifier|final
specifier|static
name|int
name|REMOVE
init|=
literal|2
decl_stmt|;
comment|/** 	 * Called whenever a document is updated within the database. 	 * Parameter event specifies the event type, i.e. one of {@link #ADD}, {@link #UPDATE}  	 * or {@link #REMOVE}. 	 *  	 * @param document 	 * @param event 	 */
specifier|public
name|void
name|documentUpdated
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|event
parameter_list|)
function_decl|;
comment|/**      * nodeMoved is called after a defragmentation run occurred for a document during which      * the address and the nodeId of a node may have changed. Defragmentation      * may only occur after a node update.      *      * @param oldNodeId      * @param newNode      */
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|StoredNode
name|newNode
parameter_list|)
function_decl|;
specifier|public
name|void
name|debug
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

