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
name|File
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
operator|.
name|MapMode
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
name|io
operator|.
name|ByteBufferAccessor
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
name|io
operator|.
name|ByteBufferInputStream
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

begin_comment
comment|/**  * Representation of an XSD binary value e.g. (xs:base64Binary or xs:hexBinary)  * whose source is backed by a File  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|BinaryValueFromFile
extends|extends
name|BinaryValue
block|{
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|FileChannel
name|channel
decl_stmt|;
specifier|private
specifier|final
name|MappedByteBuffer
name|buf
decl_stmt|;
specifier|protected
name|BinaryValueFromFile
parameter_list|(
name|BinaryValueManager
name|manager
parameter_list|,
name|BinaryValueType
name|binaryValueType
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|manager
argument_list|,
name|binaryValueType
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|channel
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"r"
argument_list|)
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|this
operator|.
name|buf
operator|=
name|channel
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|channel
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|BinaryValueFromFile
name|getInstance
parameter_list|(
name|BinaryValueManager
name|manager
parameter_list|,
name|BinaryValueType
name|binaryValueType
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|XPathException
block|{
name|BinaryValueFromFile
name|binaryFile
init|=
operator|new
name|BinaryValueFromFile
argument_list|(
name|manager
argument_list|,
name|binaryValueType
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|manager
operator|.
name|registerBinaryValueInstance
argument_list|(
name|binaryFile
argument_list|)
expr_stmt|;
return|return
name|binaryFile
return|;
block|}
annotation|@
name|Override
specifier|public
name|BinaryValue
name|convertTo
parameter_list|(
name|BinaryValueType
name|binaryValueType
parameter_list|)
throws|throws
name|XPathException
block|{
name|BinaryValueFromFile
name|binaryFile
init|=
operator|new
name|BinaryValueFromFile
argument_list|(
name|getManager
argument_list|()
argument_list|,
name|binaryValueType
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|getManager
argument_list|()
operator|.
name|registerBinaryValueInstance
argument_list|(
name|binaryFile
argument_list|)
expr_stmt|;
return|return
name|binaryFile
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|streamBinaryTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|channel
operator|.
name|isOpen
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Underlying channel has been closed"
argument_list|)
throw|;
block|}
try|try
block|{
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|READ_BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
name|buf
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|int
name|remaining
init|=
name|buf
operator|.
name|remaining
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|<
name|READ_BUFFER_SIZE
condition|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|remaining
index|]
expr_stmt|;
block|}
name|buf
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|//reset the buf
name|buf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
return|return
operator|new
name|ByteBufferInputStream
argument_list|(
operator|new
name|ByteBufferAccessor
argument_list|()
block|{
specifier|private
name|ByteBuffer
name|roBuf
decl_stmt|;
annotation|@
name|Override
specifier|public
name|ByteBuffer
name|getBuffer
parameter_list|()
block|{
if|if
condition|(
name|roBuf
operator|==
literal|null
condition|)
block|{
name|roBuf
operator|=
name|buf
operator|.
name|asReadOnlyBuffer
argument_list|()
expr_stmt|;
block|}
return|return
name|roBuf
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

