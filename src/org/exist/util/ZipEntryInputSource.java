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
name|ZipEntry
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
name|ZipFile
import|;
end_import

begin_comment
comment|/**  * This class extends InputSource to be able to deal with  * ZipEntry objects from ZIP compressed files. Its main  * feature is that each time {@link #getByteStream()} is  * called, a new uncompressed stream is created from the  * ZipEntry in the compressed ZIP file.  * This is very useful for eXist, which works in two steps:  * validation and insertion.  *   * @author jmfernandez  *  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ZipEntryInputSource
extends|extends
name|EXistInputSource
block|{
specifier|private
name|ZipEntry
name|zipEntry
decl_stmt|;
specifier|private
name|ZipFile
name|zipFile
decl_stmt|;
specifier|public
name|ZipEntryInputSource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|zipEntry
operator|=
literal|null
expr_stmt|;
name|zipFile
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|ZipEntryInputSource
parameter_list|(
name|ZipFile
name|zipFile
parameter_list|,
name|ZipEntry
name|zipEntry
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setZipEntry
argument_list|(
name|zipFile
argument_list|,
name|zipEntry
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setZipEntry
parameter_list|(
name|ZipFile
name|zipFile
parameter_list|,
name|ZipEntry
name|zipEntry
parameter_list|)
block|{
name|this
operator|.
name|zipFile
operator|=
name|zipFile
expr_stmt|;
name|this
operator|.
name|zipEntry
operator|=
name|zipEntry
expr_stmt|;
block|}
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
if|if
condition|(
name|zipFile
operator|!=
literal|null
operator|&&
name|zipEntry
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|retval
operator|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zipEntry
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
block|}
return|return
name|retval
return|;
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
specifier|public
name|long
name|getByteStreamLength
parameter_list|()
block|{
name|long
name|retval
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|zipEntry
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|zipEntry
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
return|return
name|zipFile
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|zipEntry
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Nothing to close
block|}
block|}
end_class

end_unit

