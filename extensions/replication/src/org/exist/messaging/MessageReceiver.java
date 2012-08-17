begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|messaging
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|messaging
operator|.
name|configuration
operator|.
name|JmsMessagingConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionReference
import|;
end_import

begin_comment
comment|/**  *  * @author wessels  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageReceiver
block|{
specifier|public
name|NodeImpl
name|receive
parameter_list|(
name|JmsMessagingConfiguration
name|jmc
parameter_list|,
name|FunctionReference
name|ref
parameter_list|)
throws|throws
name|XPathException
function_decl|;
block|}
end_interface

end_unit

