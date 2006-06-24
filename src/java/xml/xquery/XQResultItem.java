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
name|XQResultItem
extends|extends
name|XQItem
extends|,
name|XQItemAccessor
block|{
name|void
name|clearWarnings
parameter_list|()
function_decl|;
name|XQConnection
name|getConnection
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|XQWarning
name|getWarnings
parameter_list|()
throws|throws
name|XQException
function_decl|;
block|}
end_interface

end_unit

