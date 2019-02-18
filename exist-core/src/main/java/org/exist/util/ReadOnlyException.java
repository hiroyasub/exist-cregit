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

begin_class
specifier|public
class|class
name|ReadOnlyException
extends|extends
name|Exception
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7077941517830242672L
decl_stmt|;
comment|/** 	 * Constructor for ReadOnlyException. 	 */
specifier|public
name|ReadOnlyException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Constructor for ReadOnlyException. 	 * @param message 	 */
specifier|public
name|ReadOnlyException
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
block|}
end_class

end_unit

