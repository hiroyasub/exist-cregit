begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|index
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
name|btree
operator|.
name|Value
import|;
end_import

begin_interface
specifier|public
interface|interface
name|BFileCallback
block|{
specifier|public
name|void
name|info
parameter_list|(
name|Value
name|key
parameter_list|,
name|Value
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

