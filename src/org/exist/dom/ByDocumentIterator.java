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

begin_interface
specifier|public
interface|interface
name|ByDocumentIterator
block|{
specifier|public
name|void
name|nextDocument
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|hasNextNode
parameter_list|()
function_decl|;
specifier|public
name|NodeProxy
name|nextNode
parameter_list|()
function_decl|;
specifier|public
name|NodeProxy
name|peekNode
parameter_list|()
function_decl|;
specifier|public
name|void
name|setPosition
parameter_list|(
name|NodeProxy
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

