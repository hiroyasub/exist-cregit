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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_class
specifier|public
class|class
name|FileInputSource
extends|extends
name|EXistInputSource
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
name|FileInputSource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Optional
argument_list|<
name|Path
argument_list|>
name|file
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
specifier|private
name|Optional
argument_list|<
name|InputStream
argument_list|>
name|inputStream
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
comment|/** 	 * Constructor which calls {@link #setFile(Path)} 	 * @param file 	 * The file passed to {@link #setFile(Path)} 	 */
specifier|public
name|FileInputSource
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * If a file source has been set, the File 	 * object used for that is returned 	 * 	 * @return The Path object. 	 */
specifier|public
name|Path
name|getFile
parameter_list|()
block|{
return|return
name|file
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/** 	 * This method sets the File object used to get 	 * the uncompressed stream of data 	 * 	 * @param file The Path object pointing to the file. 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
specifier|public
name|void
name|setFile
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|reOpen
argument_list|()
expr_stmt|;
comment|// Remember: super.setSystemId must be used instead of local implementation
name|super
operator|.
name|setSystemId
argument_list|(
name|this
operator|.
name|file
operator|.
name|map
argument_list|(
name|f
lambda|->
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This method was re-implemented to open a 	 * new InputStream each time it is called. 	 * 	 * @return If the file was set, and it could be opened, an InputStream object. 	 * null, otherwise. 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|InputStream
name|getByteStream
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// close any open stream first
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
name|this
operator|.
name|inputStream
operator|=
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|file
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reOpen
argument_list|()
expr_stmt|;
return|return
name|inputStream
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|void
name|setByteStream
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// Nothing, so collateral effects are avoided!
block|}
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|void
name|setCharacterStream
parameter_list|(
specifier|final
name|Reader
name|r
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// Nothing, so collateral effects are avoided!
block|}
comment|/** 	 * This method now does nothing, so collateral 	 * effects from superclass with this one are avoided 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|void
name|setSystemId
parameter_list|(
specifier|final
name|String
name|systemId
parameter_list|)
block|{
name|assertOpen
argument_list|()
expr_stmt|;
comment|// Nothing, so collateral effects are avoided!
block|}
comment|/** 	 * @see EXistInputSource#getByteStreamLength() 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|long
name|getByteStreamLength
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
return|return
name|Files
operator|.
name|size
argument_list|(
name|file
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/** 	 * @see EXistInputSource#getSymbolicPath() 	 * 	 * @throws IllegalStateException if the InputSource was previously closed 	 */
annotation|@
name|Override
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
name|assertOpen
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|map
argument_list|(
name|Path
operator|::
name|toAbsolutePath
argument_list|)
operator|.
name|map
argument_list|(
name|Path
operator|::
name|toString
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isClosed
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
name|inputStream
operator|.
name|isPresent
argument_list|()
condition|)
block|{
try|try
block|{
name|inputStream
operator|.
name|get
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|inputStream
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
