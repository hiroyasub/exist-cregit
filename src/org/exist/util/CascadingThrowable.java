begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * CascadingThrowable.java  *   * @author Wolfgang Meier  */
end_comment

begin_interface
specifier|public
interface|interface
name|CascadingThrowable
block|{
comment|/**      * Returns the root cause of this throwable.      *       * @return Throwable      */
name|Throwable
name|getCause
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

