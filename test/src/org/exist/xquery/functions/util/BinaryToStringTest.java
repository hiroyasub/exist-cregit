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
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
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
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
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
name|value
operator|.
name|BinaryValue
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
name|value
operator|.
name|StringValue
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
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|BinaryToStringTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|roundtrip
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|value
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
name|TestableBinaryToString
name|testable
init|=
operator|new
name|TestableBinaryToString
argument_list|(
operator|new
name|MockXQueryContext
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|BinaryValue
name|binary
init|=
name|testable
operator|.
name|stringToBinary
argument_list|(
name|value
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|StringValue
name|result
init|=
name|testable
operator|.
name|binaryToString
argument_list|(
name|binary
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|result
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|TestableBinaryToString
extends|extends
name|BinaryToString
block|{
specifier|public
name|TestableBinaryToString
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|StringValue
name|binaryToString
parameter_list|(
name|BinaryValue
name|binary
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|super
operator|.
name|binaryToString
argument_list|(
name|binary
argument_list|,
name|encoding
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BinaryValue
name|stringToBinary
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|super
operator|.
name|stringToBinary
argument_list|(
name|str
argument_list|,
name|encoding
argument_list|)
return|;
block|}
block|}
specifier|public
class|class
name|MockXQueryContext
extends|extends
name|XQueryContext
block|{
specifier|public
name|MockXQueryContext
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCacheClass
parameter_list|()
block|{
return|return
literal|"org.exist.util.io.FileFilterInputStreamCache"
return|;
comment|//return "org.exist.util.io.MemoryMappedFileFilterInputStreamCache";
comment|//return "org.exist.util.io.MemoryFilterInputStreamCache";
block|}
block|}
block|}
end_class

end_unit

