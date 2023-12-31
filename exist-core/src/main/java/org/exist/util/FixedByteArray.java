begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * FixedByteArray.java - Jun 3, 2003  *   * @author wolf  */
end_comment

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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_class
specifier|public
class|class
name|FixedByteArray
implements|implements
name|ByteArray
block|{
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|int
name|start
decl_stmt|;
specifier|private
name|int
name|len
decl_stmt|;
specifier|public
name|FixedByteArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
specifier|public
name|FixedByteArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
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
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#setLength(int) 	 */
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|len
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot modify fixed byte array"
argument_list|)
throw|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#copyTo(byte[], int) 	 */
specifier|public
name|void
name|copyTo
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#copyTo(int, byte[], int, int) 	 */
specifier|public
name|void
name|copyTo
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|byte
index|[]
name|newBuf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|start
operator|+
name|startOffset
argument_list|,
name|newBuf
argument_list|,
name|offset
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|ByteArray
name|other
parameter_list|)
block|{
name|other
operator|.
name|append
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
block|{
name|buf
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|buf
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|start
operator|+
name|startOffset
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#append(byte) 	 */
specifier|public
name|void
name|append
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot modify fixed byte array"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#append(byte[]) 	 */
specifier|public
name|void
name|append
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot modify fixed byte array"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#append(byte[], int, int) 	 */
specifier|public
name|void
name|append
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot modify fixed byte array"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#size() 	 */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|len
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#release() 	 */
specifier|public
name|void
name|release
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

