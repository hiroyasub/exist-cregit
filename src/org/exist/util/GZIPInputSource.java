begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|InputStream
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_comment
comment|/**  * This class extends InputSource to be able to deal with  * GZIP compressed files. Its main feature is that each time  * {@link #getByteStream()} is called, a new uncompressed  * stream is created from the compressed GZIP file.  * This is very useful for eXist, which works in two steps:  * validation and insertion.  *   * @author JosÃ© MarÃ­a FernÃ¡ndez (jmfg@users.sourceforge.net)  *  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|GZIPInputSource
extends|extends
name|EXistInputSource
block|{
specifier|private
name|File
name|file
decl_stmt|;
specifier|private
name|InputStream
name|inputStream
decl_stmt|;
specifier|private
name|long
name|streamLength
decl_stmt|;
comment|/** 	 * Empty constructor 	 */
specifier|public
name|GZIPInputSource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|file
operator|=
literal|null
expr_stmt|;
name|inputStream
operator|=
literal|null
expr_stmt|;
name|streamLength
operator|=
operator|-
literal|1L
expr_stmt|;
block|}
comment|/** 	 * Constructor which with gzip-file 	 * @param gzipFile The gzip file. 	 */
specifier|public
name|GZIPInputSource
parameter_list|(
name|File
name|gzipFile
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|file
operator|=
name|gzipFile
expr_stmt|;
block|}
comment|/** 	 * This method was re-implemented to open a 	 * new GZIPInputStream each time it is called. 	 * @return 	 * If the file was set, and it could be opened, and it was 	 * a correct gzip file, a GZIPInputStream object. 	 * null, otherwise. 	 */
specifier|public
name|InputStream
name|getByteStream
parameter_list|()
block|{
name|InputStream
name|retval
init|=
literal|null
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|retval
operator|=
name|inputStream
operator|=
operator|new
name|GZIPInputStream
argument_list|(
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
comment|// No way to notify :-(
block|}
return|return
name|retval
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|inputStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore if the stream is already closed
block|}
finally|finally
block|{
name|inputStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided  	 */
specifier|public
name|void
name|setByteStream
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
comment|// Nothing, so collateral effects are avoided!
block|}
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided  	 */
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
comment|// Nothing, so collateral effects are avoided!
block|}
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided  	 */
specifier|public
name|void
name|setSystemId
parameter_list|(
name|String
name|systemId
parameter_list|)
block|{
comment|// Nothing, so collateral effects are avoided!
block|}
specifier|public
name|long
name|getByteStreamLength
parameter_list|()
block|{
if|if
condition|(
name|streamLength
operator|==
operator|-
literal|1L
condition|)
block|{
name|InputStream
name|str
init|=
name|getByteStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|long
name|retval
init|=
literal|0
decl_stmt|;
name|int
name|readed
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|readed
operator|=
name|str
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|retval
operator|+=
name|readed
expr_stmt|;
block|}
name|streamLength
operator|=
name|retval
expr_stmt|;
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
comment|// DoNothing(R)
block|}
block|}
return|return
name|streamLength
return|;
block|}
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
return|return
name|file
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

