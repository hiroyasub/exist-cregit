begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ByteConversion
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|ByteBuffer
block|{
name|byte
index|[]
name|buffer
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
specifier|public
name|ByteBuffer
parameter_list|(
name|int
name|initialSize
parameter_list|)
block|{
name|this
argument_list|(
name|initialSize
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ByteBuffer
parameter_list|(
name|int
name|initialSize
parameter_list|,
name|int
name|initialLen
parameter_list|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|initialSize
index|]
expr_stmt|;
name|len
operator|=
name|initialLen
expr_stmt|;
block|}
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|len
operator|+
literal|4
operator|>
name|buffer
operator|.
name|length
condition|)
name|resize
argument_list|()
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|i
argument_list|,
name|buffer
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|+=
literal|4
expr_stmt|;
block|}
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|l
parameter_list|)
block|{
if|if
condition|(
name|len
operator|+
literal|8
operator|>
name|buffer
operator|.
name|length
condition|)
name|resize
argument_list|()
expr_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|l
argument_list|,
name|buffer
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|+=
literal|8
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|len
return|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|byte
index|[]
name|newBuf
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newBuf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
specifier|private
name|void
name|resize
parameter_list|()
block|{
name|byte
index|[]
name|old
init|=
name|buffer
decl_stmt|;
name|buffer
operator|=
operator|new
name|byte
index|[
name|old
operator|.
name|length
operator|*
literal|2
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|old
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|old
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

