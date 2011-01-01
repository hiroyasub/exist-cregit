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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Interface for Cache Implementations for use by the CachingFilterInputStream  *  * @author Adam Retter<adam.retter@googlemail.com>  * @version 1.0  */
end_comment

begin_interface
specifier|public
interface|interface
name|FilterInputStreamCache
block|{
comment|/**      * Writes len bytes from the specified byte array starting at offset off to the cache.      * The general contract for write(b, off, len) is that some of the bytes in the array b      * are written to the output stream in order; element b[off] is the first byte written      * and b[off+len-1] is the last byte written by this operation.      *      * If b is null, a NullPointerException is thrown.      *      * If off is negative, or len is negative, or off+len is greater than the length of the array b, then an IndexOutOfBoundsException is thrown.      *      * @param b the data.      * @param off the start offset in the data.      * @param len - the number of bytes to write.      *      * @throws IOException - if an I/O error occurs. In particular, an IOException is thrown if the cache is invalidated.      */
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes the specified byte to the cache.      * The general contract for write is that one byte is written to the cache.      *      * @param i - the byte.      *      * @throws IOException if an I/O error occurs. In particular, an IOException may be thrown if cache is invalidated.      */
specifier|public
name|void
name|write
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Gets the length of the cache      *      * @return The length of the cache      */
specifier|public
name|int
name|getLength
parameter_list|()
function_decl|;
comment|/**      * Retrieves the byte at offset off from the cache      *      * @param off The offset to read from      * @return The byte read from the offset      *      * @Throws IOException if an I/O error occurs. In particular, an IOException may be thrown if cache is invalidated.      */
specifier|public
name|byte
name|get
parameter_list|(
name|int
name|off
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Copies data from the cache to a buffer      *      * @param cacheOffset The offset in the cache to start copying data from      * @param b The buffer to write to      * @param off The offset in the buffer b at which to start writing      * @param len The length of data to copy      *      * @Throws IOException if an I/O error occurs. In particular, an IOException may be thrown if cache is invalidated.      */
specifier|public
name|void
name|copyTo
parameter_list|(
name|int
name|cacheOffset
parameter_list|,
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Invalidates the cache      *      * Destroys the cache and releases any underlying resources      *      * @Throws IOException if an I/O error occurs. In particular, an IOException may be thrown if cache is already invalidated.      */
specifier|public
name|void
name|invalidate
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

