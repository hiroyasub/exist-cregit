begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|contentextraction
package|;
end_package

begin_comment
comment|/**  * @author Dulip Withanage<dulip.withanage@gmail.com>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|ContentExtractionException
extends|extends
name|Exception
block|{
specifier|public
name|ContentExtractionException
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
block|}
end_class

end_unit

