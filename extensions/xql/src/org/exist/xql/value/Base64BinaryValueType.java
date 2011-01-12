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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64OutputStream
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|Base64BinaryValueType
extends|extends
name|BinaryValueType
argument_list|<
name|Base64OutputStream
argument_list|>
block|{
specifier|public
name|Base64BinaryValueType
parameter_list|()
block|{
name|super
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Base64OutputStream
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

