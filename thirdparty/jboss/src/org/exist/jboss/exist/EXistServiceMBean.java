begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|jboss
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|system
operator|.
name|ServiceMBean
import|;
end_import

begin_comment
comment|/**  * This are the managed operations and attributes for the EXist service  *  * @author Per Nyfelt  */
end_comment

begin_interface
specifier|public
interface|interface
name|EXistServiceMBean
extends|extends
name|ServiceMBean
block|{
specifier|public
name|String
name|getStatus
parameter_list|()
function_decl|;
specifier|public
name|String
name|getEXistHome
parameter_list|()
function_decl|;
specifier|public
name|void
name|setEXistHome
parameter_list|(
name|String
name|existHome
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

