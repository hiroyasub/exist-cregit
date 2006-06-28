begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|javax
operator|.
name|xml
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * XQJ interfaces reconstructed from version 0.5 documentation  */
end_comment

begin_interface
specifier|public
interface|interface
name|XQItem
extends|extends
name|XQItemAccessor
block|{
name|void
name|close
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|boolean
name|isClosed
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

