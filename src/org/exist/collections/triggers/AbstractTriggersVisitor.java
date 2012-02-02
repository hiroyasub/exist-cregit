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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
specifier|abstract
class|class
name|AbstractTriggersVisitor
parameter_list|<
name|T
extends|extends
name|Trigger
parameter_list|,
name|P
extends|extends
name|AbstractTriggerProxies
parameter_list|>
implements|implements
name|TriggersVisitor
block|{
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|P
name|proxies
decl_stmt|;
specifier|private
name|List
argument_list|<
name|T
argument_list|>
name|triggers
decl_stmt|;
specifier|public
name|AbstractTriggersVisitor
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|P
name|proxies
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|proxies
operator|=
name|proxies
expr_stmt|;
block|}
comment|/**      * lazy instantiated      */
specifier|protected
name|List
argument_list|<
name|T
argument_list|>
name|getTriggers
parameter_list|()
throws|throws
name|TriggerException
block|{
if|if
condition|(
name|triggers
operator|==
literal|null
condition|)
block|{
name|triggers
operator|=
name|proxies
operator|.
name|instantiateTriggers
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|triggers
return|;
block|}
block|}
end_class

end_unit
