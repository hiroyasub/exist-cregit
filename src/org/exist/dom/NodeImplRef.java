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
comment|/**  * Holds a mutable reference to a NodeImpl, used to pass a node by reference.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|NodeImplRef
block|{
specifier|public
name|NodeImplRef
parameter_list|()
block|{
block|}
specifier|public
name|NodeImplRef
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
specifier|public
name|StoredNode
name|node
decl_stmt|;
block|}
end_class

end_unit

