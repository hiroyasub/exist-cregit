begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|Receiver
import|;
end_import

begin_comment
comment|/**  * Highlight matches in query results. Indexes can implement  * this interface to filter the output produced by the serializer  * when serializing query results. See  * {@link org.exist.indexing.IndexWorker#getMatchListener(org.exist.storage.DBBroker,  * org.exist.dom.NodeProxy)}.  * The interface basically extends {@link org.exist.util.serializer.Receiver}. The  * additional methods are used to chain multiple MatchListeners. Implementations should  * forward all events to the next receiver in the chain (if there is one).  * Class {@link org.exist.indexing.AbstractMatchListener} provides default implementations  * for all methods.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MatchListener
extends|extends
name|Receiver
block|{
comment|/**      * Register the next receiver in the chain. All      * events should be forwarded to this.      *      * @param next the next receiver in the chain.      */
name|void
name|setNextInChain
parameter_list|(
name|Receiver
name|next
parameter_list|)
function_decl|;
comment|/**      * Returns the next receiver in the chain.      * @return the next receiver      */
name|Receiver
name|getNextInChain
parameter_list|()
function_decl|;
comment|/**      * Walks the chain and returns the final receiver.      * @return the last receiver in the chain      */
name|Receiver
name|getLastInChain
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

