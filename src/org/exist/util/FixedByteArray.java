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
specifier|public
name|FixedByteArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
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
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#copyTo(byte[], int, int) 	 */
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
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
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
literal|0
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.ByteArray#copyTo(int, byte[], int, int) 	 */
specifier|public
name|void
name|copyTo
parameter_list|(
name|int
name|start
parameter_list|,
name|byte
index|[]
name|newBuf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
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
name|newBuf
argument_list|,
name|offset
argument_list|,
name|len
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
name|data
operator|.
name|length
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

