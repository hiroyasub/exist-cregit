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
name|io
operator|.
name|output
operator|.
name|CloseShieldOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|FastByteArrayOutputStream
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
name|*
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Representation of an XSD binary value e.g. (xs:base64Binary or xs:hexBinary)  * whose source is backed by a pre-encoded String.  *  * Note - BinaryValueFromBinaryString is a special case of BinaryValue  * where the value is already encoded.  *  * @author<a href="mailto:adam@existsolutions.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|BinaryValueFromBinaryString
extends|extends
name|BinaryValue
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|BinaryValueFromBinaryString
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
specifier|public
name|BinaryValueFromBinaryString
parameter_list|(
name|BinaryValueType
name|binaryValueType
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|binaryValueType
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|binaryValueType
operator|.
name|verifyAndFormatString
argument_list|(
name|value
argument_list|)
expr_stmt|;
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
comment|//TODO temporary approach, consider implementing a TranscodingBinaryValueFromBinaryString(BinaryValueFromBinaryString) class
comment|//that only does the transncoding lazily
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
name|FilterOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//transcode
name|fos
operator|=
name|binaryValueType
operator|.
name|getEncoder
argument_list|(
name|baos
argument_list|)
expr_stmt|;
name|streamBinaryTo
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to close stream: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to close stream: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|BinaryValueFromBinaryString
argument_list|(
name|binaryValueType
argument_list|,
name|baos
operator|.
name|toString
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
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
comment|//we need to create a safe output stream that cannot be closed
specifier|final
name|OutputStream
name|safeOutputStream
init|=
operator|new
name|CloseShieldOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
comment|//get the decoder
specifier|final
name|FilterOutputStream
name|fos
init|=
name|getBinaryValueType
argument_list|()
operator|.
name|getDecoder
argument_list|(
name|safeOutputStream
argument_list|)
decl_stmt|;
comment|//write with the decoder
specifier|final
name|byte
name|data
index|[]
init|=
name|value
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|//we do have to close the decoders output stream though
comment|//to ensure that all bytes have been written, this is
comment|//particularly nessecary for Apache Commons Codec stream encoders
try|try
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to close stream: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|streamTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
comment|//write
specifier|final
name|byte
name|data
index|[]
init|=
name|value
operator|.
name|getBytes
argument_list|()
decl_stmt|;
comment|//TODO consider a more efficient approach for writing large strings
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
comment|//TODO consider a more efficient approach for writting large strings
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|streamBinaryTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to get read only buffer: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
return|return
name|baos
operator|.
name|toFastByteInputStream
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
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
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|incrementSharedReferences
parameter_list|()
block|{
comment|// we don't need reference counting, as there is nothing to cleanup when all references are returned
block|}
block|}
end_class

end_unit

