begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|start
package|;
end_package

begin_class
specifier|public
class|class
name|StartException
extends|extends
name|Exception
block|{
specifier|public
name|int
name|getErrorCode
parameter_list|()
block|{
return|return
name|errorCode
return|;
block|}
specifier|private
specifier|final
name|int
name|errorCode
decl_stmt|;
specifier|public
name|StartException
parameter_list|(
specifier|final
name|int
name|errorCode
parameter_list|)
block|{
name|this
operator|.
name|errorCode
operator|=
name|errorCode
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
literal|"Error code: "
operator|+
name|getErrorCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

