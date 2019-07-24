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
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_class
annotation|@
name|NotThreadSafe
specifier|public
specifier|abstract
class|class
name|EXistInputSource
extends|extends
name|InputSource
implements|implements
name|Closeable
block|{
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
comment|/** 	 * Get the length of the byte stream. 	 * 	 * @return the length of the byte stream. 	 * 	 * @deprecated Should be avoided, trying to get the length of a stream may involve buffering 	 */
annotation|@
name|Deprecated
specifier|public
specifier|abstract
name|long
name|getByteStreamLength
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|String
name|getSymbolicPath
parameter_list|()
function_decl|;
comment|/** 	 * Determines if the InputSource was closed 	 * 	 * @return true if the InputSource was previously closed, false otherwise 	 */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/** 	 * Throws an exception if the InputSource is closed 	 * 	 * @throws IllegalStateException If the InputSource was previously closed 	 */
specifier|protected
name|void
name|assertOpen
parameter_list|()
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The InputSource has been closed"
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Re-Opens the InputSource by just 	 * setting the closed flag to false 	 */
specifier|protected
name|void
name|reOpen
parameter_list|()
block|{
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
block|}
comment|/** 	 * Just sets the status of the InputStream to closed 	 * 	 * Sub-classes that override this should call {@code super.close()} 	 */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

