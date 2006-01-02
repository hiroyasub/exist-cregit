begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  $Id$ */
end_comment

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
comment|/** Applies Null Object Design Pattern  * @author Jean-Marc Vanel - http:///jmvanel.free.fr */
end_comment

begin_class
specifier|public
class|class
name|NullNodeIndexListener
implements|implements
name|NodeIndexListener
block|{
comment|/** Singleton */
specifier|public
specifier|static
specifier|final
name|NodeIndexListener
name|INSTANCE
init|=
operator|new
name|NullNodeIndexListener
argument_list|()
decl_stmt|;
comment|/** @see org.exist.dom.NodeIndexListener#nodeChanged(org.exist.dom.NodeImpl) */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
block|}
comment|/** @see org.exist.dom.NodeIndexListener#nodeChanged(long, long) */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|long
name|oldAddress
parameter_list|,
name|long
name|newAddress
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

