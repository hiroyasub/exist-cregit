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
name|java
operator|.
name|io
operator|.
name|FilterOutputStream
import|;
end_import

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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  *  * @author<a href="mailto:adam@existsolutions.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|BinaryValueTypeTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|verifyAndFormat_does_trim
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|testValue
init|=
literal|" HELLO \r\n"
decl_stmt|;
name|BinaryValueType
name|binaryValueType
init|=
operator|new
name|TestableBinaryValueType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Base64OutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|result
init|=
name|binaryValueType
operator|.
name|verifyAndFormatString
argument_list|(
name|testValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testValue
operator|.
name|trim
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verifyAndFormat_replaces_whiteSpace
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|testValue
init|=
literal|"HELLO WO RLD"
decl_stmt|;
name|BinaryValueType
name|binaryValueType
init|=
operator|new
name|TestableBinaryValueType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Base64OutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|result
init|=
name|binaryValueType
operator|.
name|verifyAndFormatString
argument_list|(
name|testValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testValue
operator|.
name|replaceAll
argument_list|(
literal|"\\s"
argument_list|,
literal|""
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|TestableBinaryValueType
parameter_list|<
name|T
extends|extends
name|FilterOutputStream
parameter_list|>
extends|extends
name|BinaryValueType
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|TestableBinaryValueType
parameter_list|(
name|int
name|xqueryType
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|coder
parameter_list|)
block|{
name|super
argument_list|(
name|xqueryType
argument_list|,
name|coder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|verifyString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
annotation|@
name|Override
specifier|protected
name|String
name|formatString
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
return|;
block|}
block|}
block|}
end_class

end_unit

