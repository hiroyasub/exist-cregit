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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|Base64InputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ConfigurationHelper
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|Base64BinaryValueTypeTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|verify_invalidBase64_fails
parameter_list|()
throws|throws
name|XPathException
block|{
name|TestableBase64BinaryValueType
name|base64Type
init|=
operator|new
name|TestableBase64BinaryValueType
argument_list|()
decl_stmt|;
name|base64Type
operator|.
name|verifyString
argument_list|(
literal|"=aaabbcd"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XPathException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|verify_invalidBase64_fails_2
parameter_list|()
throws|throws
name|XPathException
block|{
name|TestableBase64BinaryValueType
name|base64Type
init|=
operator|new
name|TestableBase64BinaryValueType
argument_list|()
decl_stmt|;
name|base64Type
operator|.
name|verifyString
argument_list|(
literal|"frfhforlksid745323=="
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verify_validBase64_passes
parameter_list|()
throws|throws
name|XPathException
block|{
name|TestableBase64BinaryValueType
name|base64Type
init|=
operator|new
name|TestableBase64BinaryValueType
argument_list|()
decl_stmt|;
name|base64Type
operator|.
name|verifyString
argument_list|(
literal|"aaabbcd="
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verify_validBase64_passes_2
parameter_list|()
throws|throws
name|XPathException
block|{
name|TestableBase64BinaryValueType
name|base64Type
init|=
operator|new
name|TestableBase64BinaryValueType
argument_list|()
decl_stmt|;
name|base64Type
operator|.
name|verifyString
argument_list|(
literal|"dGVzdCBkYXRh"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verify_validBase64_passes_3
parameter_list|()
throws|throws
name|XPathException
block|{
name|TestableBase64BinaryValueType
name|base64Type
init|=
operator|new
name|TestableBase64BinaryValueType
argument_list|()
decl_stmt|;
name|base64Type
operator|.
name|verifyString
argument_list|(
literal|"aaa a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|verify_validBase64_passes_large_string
parameter_list|()
throws|throws
name|XPathException
throws|,
name|IOException
block|{
name|File
name|home
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|binaryFile
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"webapp/logo.jpg"
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
literal|null
decl_stmt|;
name|String
name|base64data
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|Base64InputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|binaryFile
argument_list|)
argument_list|,
literal|true
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|baos
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
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
name|int
name|read
init|=
operator|-
literal|1
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
name|base64data
operator|=
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|baos
operator|!=
literal|null
condition|)
block|{
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|base64data
argument_list|)
expr_stmt|;
name|TestableBase64BinaryValueType
name|base64Type
init|=
operator|new
name|TestableBase64BinaryValueType
argument_list|()
decl_stmt|;
name|base64Type
operator|.
name|verifyString
argument_list|(
name|base64data
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|TestableBase64BinaryValueType
extends|extends
name|Base64BinaryValueType
block|{
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
name|super
operator|.
name|verifyString
argument_list|(
name|str
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|formatString
argument_list|(
name|str
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
