begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|DocumentTriggerProxies
extends|extends
name|AbstractTriggerProxies
argument_list|<
name|DocumentTrigger
argument_list|,
name|DocumentTriggerProxy
argument_list|,
name|DocumentTriggersVisitor
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|DocumentTriggersVisitor
name|instantiateVisitor
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
operator|new
name|DocumentTriggersVisitor
argument_list|(
name|broker
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

