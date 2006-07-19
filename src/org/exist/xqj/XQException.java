begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqj
package|;
end_package

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQException
extends|extends
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQException
block|{
comment|/** 	 * @param message 	 */
specifier|public
name|XQException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/** 	 * @param message 	 * @param cause 	 * @param vendorCode 	 * @param nextException 	 */
specifier|public
name|XQException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|String
name|vendorCode
parameter_list|,
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQException
name|nextException
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|,
name|vendorCode
argument_list|,
name|nextException
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
block|}
end_class

end_unit

