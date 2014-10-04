begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|INode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|NodePath
import|;
end_import

begin_comment
comment|//TODO do we really need to extend Visitable any more?
end_comment

begin_interface
specifier|public
interface|interface
name|IStoredNode
extends|extends
name|INode
argument_list|<
name|DocumentImpl
argument_list|>
extends|,
name|NodeHandle
extends|,
name|Visitable
block|{
comment|//<editor-fold desc="serialization">
comment|/**      * Serialize the state of this node      * into a byte array.      *       * @return A byte array containing the      * serialization of the node      */
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
function_decl|;
comment|//public static StoredNode deserialize(byte[] data, int start, int len);
comment|//IStoredNode deserialize(); //TODO perhaps use package protected method?
comment|//</editor-fold>
comment|/**      * Set the Document that this node belongs to      *       * Counterpart to @see org.exist.dom.INode#getOwnerDocument()      *       * @param doc The document that this node belongs to      */
specifier|public
name|void
name|setOwnerDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|//<editor-fold desc="temp">
comment|//TODO see StoredNode.getParentStoredNode and StoredNode.getParentNode, should be able to remove in favour of getParentNode() in future.
specifier|public
name|IStoredNode
name|getParentStoredNode
parameter_list|()
function_decl|;
comment|//</editor-fold>
comment|/**      * Returns a count of the number of children      *       */
specifier|public
name|int
name|getChildCount
parameter_list|()
function_decl|;
comment|//TODO also available in memtree.ElementImpl - consider moving to org.exist.dom.INode (also this is only really used for ElementImpl and DocumentImpl)
comment|/**      * Returns true if the node was modified recently and nodes      * were inserted at the start or in the middle of its children.      *      * @return true when node the node is 'dirty'      */
comment|//public boolean isDirty();
comment|//TODO can we not detect this?
comment|/**      * Set the node to dirty to indicated      * that nodes were inserted at the start      * or in the middle of its children.      */
specifier|public
name|void
name|setDirty
parameter_list|(
name|boolean
name|dirty
parameter_list|)
function_decl|;
specifier|public
name|NodePath
name|getPath
parameter_list|()
function_decl|;
specifier|public
name|NodePath
name|getPath
parameter_list|(
name|NodePath
name|parentPath
parameter_list|)
function_decl|;
comment|//TODO seems to be ElementImpl specific see StoredNode
comment|//TODO clean this up
comment|/**      * @see org.exist.dom.persistent.StoredNode#reset()      * this seems to do two things      * clear the state, and then return the object      * to NodePool - all a bit of a mess really!      *       * org.exist.Indexer seems to borrow and return to the pool      * org.exist.memtree.DOMIndexer only seems to borrow nodes      * org.exist.serializers.NativeSerializer only seems to return nodes      * org.exist.dom.persistent.*Impl#deserialize(...) seem to have support for pooling      *   yet this is set to false in the invoking code!      */
specifier|public
name|void
name|release
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

