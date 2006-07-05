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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  * XQJ interfaces reconstructed from version 0.5 documentation  */
end_comment

begin_interface
specifier|public
interface|interface
name|XQPreparedExpression
extends|extends
name|XQDynamicContext
block|{
name|void
name|cancel
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|void
name|clearWarnings
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|void
name|close
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|XQResultSequence
name|executeQuery
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|QName
index|[]
name|getAllExternalVariables
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|QName
index|[]
name|getUnboundExternalVariables
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|int
name|getQueryTimeout
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|XQSequenceType
name|getStaticResultType
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|XQSequenceType
name|getStaticVariableType
parameter_list|(
name|QName
name|name
parameter_list|)
throws|throws
name|XQException
function_decl|;
name|XQWarning
name|getWarnings
parameter_list|()
throws|throws
name|XQException
function_decl|;
name|boolean
name|isClosed
parameter_list|()
function_decl|;
name|void
name|setQueryTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|XQException
function_decl|;
block|}
end_interface

end_unit

