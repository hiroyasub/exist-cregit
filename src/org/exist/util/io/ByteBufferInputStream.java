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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Implementation of an InputStream which reads from a ByteBuffer  *  * @version 1.0  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|ByteBufferInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
name|ByteBufferAccessor
name|bufAccessor
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|END_OF_STREAM
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|ByteBufferInputStream
parameter_list|(
name|ByteBufferAccessor
name|bufAccessor
parameter_list|)
block|{
name|this
operator|.
name|bufAccessor
operator|=
name|bufAccessor
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|available
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|available
operator|=
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|capacity
argument_list|()
operator|-
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
return|return
name|available
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|isClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|END_OF_STREAM
return|;
block|}
return|return
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|isClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|END_OF_STREAM
return|;
block|}
if|else if
condition|(
name|b
operator|.
name|length
operator|>
name|available
argument_list|()
condition|)
block|{
return|return
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|available
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|get
argument_list|(
name|b
argument_list|)
operator|.
name|position
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|isClosed
argument_list|()
expr_stmt|;
if|if
condition|(
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|END_OF_STREAM
return|;
block|}
if|else if
condition|(
name|b
operator|.
name|length
operator|>
name|available
argument_list|()
condition|)
block|{
name|len
operator|=
name|available
argument_list|()
expr_stmt|;
block|}
return|return
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
operator|.
name|position
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|mark
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|l
operator|>
name|available
argument_list|()
condition|)
block|{
name|l
operator|=
name|available
argument_list|()
expr_stmt|;
block|}
name|long
name|newPosition
init|=
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|position
argument_list|()
decl_stmt|;
name|newPosition
operator|+=
name|l
expr_stmt|;
try|try
block|{
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|position
argument_list|(
operator|(
name|int
operator|)
name|newPosition
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to skip "
operator|+
name|l
operator|+
literal|" bytes"
argument_list|,
name|iae
argument_list|)
throw|;
block|}
return|return
name|l
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
name|isClosed
argument_list|()
expr_stmt|;
name|bufAccessor
operator|.
name|getBuffer
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|void
name|isClosed
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The stream was previously closed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

