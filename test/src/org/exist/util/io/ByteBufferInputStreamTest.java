begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
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
name|util
operator|.
name|Random
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|ByteBufferInputStreamTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|available
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testData
operator|.
name|length
argument_list|,
name|is
operator|.
name|available
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|availableIsZeroAfterClose
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|is
operator|.
name|available
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|availableAfterRead
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
comment|//read first 2 bytes
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|testData
operator|.
name|length
operator|-
literal|2
argument_list|,
name|is
operator|.
name|available
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readByteByByteCorrectAndThenReturnMinus1AtEndOfStream
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testData
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|testData
index|[
name|i
index|]
argument_list|,
name|is
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//ensure reading past the end of the stream returns -1
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|is
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readMultipleBytesCorrectAndThenReturnMinus1AtEndOfStream
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|readData
index|[]
init|=
operator|new
name|byte
index|[
name|testData
operator|.
name|length
index|]
decl_stmt|;
name|int
name|read
init|=
name|is
operator|.
name|read
argument_list|(
name|readData
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testData
operator|.
name|length
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testData
argument_list|,
name|readData
argument_list|)
expr_stmt|;
comment|//ensure reading past the end of the stream returns -1
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|is
operator|.
name|read
argument_list|(
name|readData
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readMultipleBytesPastAvailable
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|readData
index|[]
init|=
operator|new
name|byte
index|[
name|testData
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|int
name|read
init|=
name|is
operator|.
name|read
argument_list|(
name|readData
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testData
operator|.
name|length
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testData
argument_list|,
name|subArray
argument_list|(
name|readData
argument_list|,
name|testData
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|//bytes past the available should still be 0
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|}
argument_list|,
name|subArray
argument_list|(
name|readData
argument_list|,
name|testData
operator|.
name|length
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readMultipleBytesSpecificCorrectAndThenReturnMinus1AtEndOfStream
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|readData
index|[]
init|=
operator|new
name|byte
index|[
name|testData
operator|.
name|length
index|]
decl_stmt|;
name|is
operator|.
name|read
argument_list|(
name|readData
argument_list|,
literal|0
argument_list|,
name|testData
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testData
argument_list|,
name|readData
argument_list|)
expr_stmt|;
comment|//ensure reading past the end of the stream returns -1
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|is
operator|.
name|read
argument_list|(
name|readData
argument_list|,
literal|0
argument_list|,
name|testData
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readMultipleBytesSpecificPastAvailable
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|byte
name|readData
index|[]
init|=
operator|new
name|byte
index|[
name|testData
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|int
name|read
init|=
name|is
operator|.
name|read
argument_list|(
name|readData
argument_list|,
literal|0
argument_list|,
name|readData
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testData
operator|.
name|length
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testData
argument_list|,
name|subArray
argument_list|(
name|readData
argument_list|,
name|testData
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|//bytes past the available should still be 0
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|}
argument_list|,
name|subArray
argument_list|(
name|readData
argument_list|,
name|testData
operator|.
name|length
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|readSingleByteAfterCloseThrowsException
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//should throw IOException
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|readMultipleBytesAfterCloseThrowsException
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
name|readBuf
index|[]
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
comment|//should throw IOException
name|is
operator|.
name|read
argument_list|(
name|readBuf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|readMultipleBytesSpecificAfterCloseThrowsException
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|fail
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
name|readBuf
index|[]
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
comment|//should throw IOException
name|is
operator|.
name|read
argument_list|(
name|readBuf
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readMultipleBytesInLoop
parameter_list|()
throws|throws
name|IOException
block|{
comment|//generate 1KB of test data
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|byte
name|testData
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|testData
argument_list|)
expr_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
name|readBuf
index|[]
init|=
operator|new
name|byte
index|[
literal|56
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
name|readBuf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|assertLessThanOrEqual
argument_list|(
name|readBuf
operator|.
name|length
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|baos
operator|.
name|write
argument_list|(
name|readBuf
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
name|readMultipleBytesSpecificInLoop
parameter_list|()
throws|throws
name|IOException
block|{
comment|//generate 1KB of test data
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|byte
name|testData
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|testData
argument_list|)
expr_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
name|readBuf
index|[]
init|=
operator|new
name|byte
index|[
literal|56
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
name|readBuf
argument_list|,
literal|0
argument_list|,
name|readBuf
operator|.
name|length
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|assertLessThanOrEqual
argument_list|(
name|readBuf
operator|.
name|length
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|baos
operator|.
name|write
argument_list|(
name|readBuf
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
name|markReturnsTrue
parameter_list|()
block|{
specifier|final
name|byte
name|testData
index|[]
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|testData
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|TestableByteBufferAccessor
argument_list|(
name|buf
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|is
operator|.
name|markSupported
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|TestableByteBufferAccessor
implements|implements
name|ByteBufferAccessor
block|{
specifier|private
specifier|final
name|ByteBuffer
name|buf
decl_stmt|;
specifier|public
name|TestableByteBufferAccessor
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|buf
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuffer
name|getBuffer
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
block|}
specifier|private
name|byte
index|[]
name|subArray
parameter_list|(
name|byte
name|data
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|byte
name|newData
index|[]
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|newData
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|newData
return|;
block|}
specifier|private
name|byte
index|[]
name|subArray
parameter_list|(
name|byte
name|data
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|byte
name|newData
index|[]
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|newData
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|newData
return|;
block|}
specifier|private
specifier|static
name|void
name|assertLessThanOrEqual
parameter_list|(
name|int
name|expectedMax
parameter_list|,
name|int
name|actual
parameter_list|)
block|{
if|if
condition|(
name|actual
operator|>
name|expectedMax
condition|)
block|{
name|fail
argument_list|(
literal|"Expected actual value"
operator|+
name|actual
operator|+
literal|" to be less than or equal to "
operator|+
name|expectedMax
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

