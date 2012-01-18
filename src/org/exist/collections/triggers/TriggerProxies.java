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

begin_interface
specifier|public
interface|interface
name|TriggerProxies
parameter_list|<
name|P
extends|extends
name|TriggerProxy
parameter_list|>
block|{
specifier|public
name|void
name|add
parameter_list|(
name|P
name|proxy
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|TriggersVisitor
name|instantiateVisitor
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

