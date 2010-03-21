begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_comment
comment|/**  * @author mdiggory  */
end_comment

begin_interface
specifier|public
interface|interface
name|XmldbPrincipal
extends|extends
name|Principal
block|{
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
annotation|@
name|Deprecated
specifier|public
name|String
name|getPassword
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasRole
parameter_list|(
name|String
name|role
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

