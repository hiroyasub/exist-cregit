begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|map
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
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
name|ErrorCodes
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
name|ErrorCodes
operator|.
name|ErrorCode
import|;
end_import

begin_class
specifier|public
class|class
name|MapErrorCode
extends|extends
name|ErrorCodes
operator|.
name|ErrorCode
block|{
specifier|public
specifier|static
specifier|final
name|MapErrorCode
name|EXMPDY001
init|=
operator|new
name|MapErrorCode
argument_list|(
literal|"EXMPDY001"
argument_list|,
literal|"Key should be a single, atomic value"
argument_list|)
decl_stmt|;
specifier|public
name|MapErrorCode
parameter_list|(
name|String
name|code
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MapErrorCode
parameter_list|(
name|QName
name|errorQName
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|errorQName
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

