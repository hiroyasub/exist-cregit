begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|realm
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

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransformationContext
block|{
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAdditionalGroups
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

