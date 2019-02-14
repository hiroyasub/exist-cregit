begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

begin_comment
comment|/**  * Notify a listener that a lock has been released.  */
end_comment

begin_interface
specifier|public
interface|interface
name|LockListener
block|{
name|void
name|lockReleased
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

