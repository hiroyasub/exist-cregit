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
comment|/**  * CascadingException.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|CascadingException
extends|extends
name|Exception
implements|implements
name|CascadingThrowable
block|{
name|Throwable
name|m_throwable
decl_stmt|;
comment|/** 	 * Constructor for CascadingException. 	 */
specifier|public
name|CascadingException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Constructor for CascadingException. 	 * @param message 	 */
specifier|public
name|CascadingException
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
block|}
comment|/** 	 * Constructor for CascadingException. 	 * @param message 	 * @param cause 	 */
specifier|public
name|CascadingException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|m_throwable
operator|=
name|cause
expr_stmt|;
block|}
comment|/** 	 * Constructor for CascadingException. 	 * @param cause 	 */
specifier|public
name|CascadingException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|m_throwable
operator|=
name|cause
expr_stmt|;
block|}
comment|/**      * Return the root cause.      *       * @see java.lang.Throwable#getCause()      */
specifier|public
name|Throwable
name|getCause
parameter_list|()
block|{
return|return
name|m_throwable
return|;
block|}
block|}
end_class

end_unit

