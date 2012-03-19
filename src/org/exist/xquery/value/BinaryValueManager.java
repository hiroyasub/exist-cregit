begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_interface
specifier|public
interface|interface
name|BinaryValueManager
block|{
specifier|public
name|void
name|registerBinaryValueInstance
parameter_list|(
name|BinaryValue
name|binaryValue
parameter_list|)
function_decl|;
specifier|public
name|void
name|cleanupBinaryValueInstances
parameter_list|()
function_decl|;
specifier|public
name|String
name|getCacheClass
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

