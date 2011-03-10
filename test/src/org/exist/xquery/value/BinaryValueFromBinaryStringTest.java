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
name|Hex
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Base64
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|assertArrayEquals
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
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|BinaryValueFromBinaryStringTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|getInputStream
parameter_list|()
throws|throws
name|XPathException
throws|,
name|IOException
block|{
specifier|final
name|String
name|testData
init|=
literal|"test data"
decl_stmt|;
specifier|final
name|String
name|base64TestData
init|=
name|Base64
operator|.
name|encodeBase64String
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|BinaryValue
name|binaryValue
init|=
operator|new
name|BinaryValueFromBinaryString
argument_list|(
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
name|base64TestData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|binaryValue
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|()
argument_list|,
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cast_base64_to_hexBinary
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|testData
init|=
literal|"testdata"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|BinaryValue
name|binaryValue
init|=
operator|new
name|BinaryValueFromBinaryString
argument_list|(
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
name|Base64
operator|.
name|encodeBase64String
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicValue
name|result
init|=
name|binaryValue
operator|.
name|convertTo
argument_list|(
operator|new
name|HexBinaryValueType
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|result
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cast_hexBinary_to_base64
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|testData
init|=
literal|"testdata"
decl_stmt|;
specifier|final
name|String
name|expectedResult
init|=
name|Base64
operator|.
name|encodeBase64String
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|BinaryValue
name|binaryValue
init|=
operator|new
name|BinaryValueFromBinaryString
argument_list|(
operator|new
name|HexBinaryValueType
argument_list|()
argument_list|,
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicValue
name|result
init|=
name|binaryValue
operator|.
name|convertTo
argument_list|(
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|result
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

