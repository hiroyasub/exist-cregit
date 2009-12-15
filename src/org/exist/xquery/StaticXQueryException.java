begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_class
specifier|public
class|class
name|StaticXQueryException
extends|extends
name|XPathException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8229758099980343418L
decl_stmt|;
specifier|public
name|StaticXQueryException
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
specifier|public
name|StaticXQueryException
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StaticXQueryException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StaticXQueryException
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
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StaticXQueryException
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

