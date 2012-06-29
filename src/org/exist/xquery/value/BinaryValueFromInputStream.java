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
name|org
operator|.
name|apache
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
name|CachingFilterInputStream
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
name|FilterInputStreamCache
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
name|FilterInputStreamCacheFactory
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
name|FilterInputStreamCacheFactory
operator|.
name|FilterInputStreamCacheConfiguration
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
comment|/**  * Representation of an XSD binary value e.g. (xs:base64Binary or xs:hexBinary)  * whose source is backed by an InputStream  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|BinaryValueFromInputStream
extends|extends
name|BinaryValue
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|BinaryValueFromInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CachingFilterInputStream
name|is
decl_stmt|;
specifier|private
name|FilterInputStreamCache
name|cache
decl_stmt|;
specifier|protected
name|BinaryValueFromInputStream
parameter_list|(
specifier|final
name|BinaryValueManager
name|manager
parameter_list|,
name|BinaryValueType
name|binaryValueType
parameter_list|,
name|InputStream
name|is
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
name|cache
operator|=
name|FilterInputStreamCacheFactory
operator|.
name|getCacheInstance
argument_list|(
operator|new
name|FilterInputStreamCacheConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getCacheClass
parameter_list|()
block|{
return|return
name|manager
operator|.
name|getCacheClass
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|is
operator|=
operator|new
name|CachingFilterInputStream
argument_list|(
name|cache
argument_list|,
name|is
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
argument_list|)
throw|;
block|}
comment|//mark the start of the stream so that we can re-read again as required
name|this
operator|.
name|is
operator|.
name|mark
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|BinaryValueFromInputStream
name|getInstance
parameter_list|(
name|BinaryValueManager
name|manager
parameter_list|,
name|BinaryValueType
name|binaryValueType
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|XPathException
block|{
name|BinaryValueFromInputStream
name|binaryInputStream
init|=
operator|new
name|BinaryValueFromInputStream
argument_list|(
name|manager
argument_list|,
name|binaryValueType
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|manager
operator|.
name|registerBinaryValueInstance
argument_list|(
name|binaryInputStream
argument_list|)
expr_stmt|;
return|return
name|binaryInputStream
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
name|BinaryValueFromInputStream
name|binaryInputStream
init|=
operator|new
name|BinaryValueFromInputStream
argument_list|(
name|getManager
argument_list|()
argument_list|,
name|binaryValueType
argument_list|,
operator|new
name|CachingFilterInputStream
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|getManager
argument_list|()
operator|.
name|registerBinaryValueInstance
argument_list|(
name|binaryInputStream
argument_list|)
expr_stmt|;
return|return
name|binaryInputStream
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
try|try
block|{
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
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
operator|(
name|read
operator|=
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|//reset the buf
try|try
block|{
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to reset stream: "
operator|+
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
name|CachingFilterInputStream
argument_list|(
name|is
argument_list|)
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
try|try
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|invalidate
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

