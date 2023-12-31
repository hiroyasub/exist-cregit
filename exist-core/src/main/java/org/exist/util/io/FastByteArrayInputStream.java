begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

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
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
import|;
end_import

begin_comment
comment|/**  * This is a replacement for {@link java.io.ByteArrayInputStream}  * which removes the synchronization overhead for non-concurrent  * access; as such this class is not thread-safe.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|FastByteArrayInputStream
extends|extends
name|InputStream
block|{
specifier|public
specifier|static
specifier|final
name|int
name|END_OF_STREAM
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * The underlying data buffer.      */
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
comment|/**      * End Of Data.      *      * Similar to data.length,      * i.e. the last readable offset + 1.      */
specifier|private
specifier|final
name|int
name|eod
decl_stmt|;
comment|/**      * Current offset in the data buffer.      */
specifier|private
name|int
name|offset
decl_stmt|;
comment|/**      * The current mark (if any).      */
specifier|private
name|int
name|markedOffset
decl_stmt|;
specifier|public
name|FastByteArrayInputStream
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|eod
operator|=
name|data
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|markedOffset
operator|=
name|this
operator|.
name|offset
expr_stmt|;
block|}
specifier|public
name|FastByteArrayInputStream
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offset cannot be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|min
argument_list|(
name|offset
argument_list|,
name|data
operator|.
name|length
operator|>
literal|0
condition|?
name|data
operator|.
name|length
else|:
name|offset
argument_list|)
expr_stmt|;
name|this
operator|.
name|eod
operator|=
name|data
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|markedOffset
operator|=
name|this
operator|.
name|offset
expr_stmt|;
block|}
specifier|public
name|FastByteArrayInputStream
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offset cannot be negative"
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length cannot be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|min
argument_list|(
name|offset
argument_list|,
name|data
operator|.
name|length
operator|>
literal|0
condition|?
name|data
operator|.
name|length
else|:
name|offset
argument_list|)
expr_stmt|;
name|this
operator|.
name|eod
operator|=
name|min
argument_list|(
name|this
operator|.
name|offset
operator|+
name|length
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|markedOffset
operator|=
name|this
operator|.
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
name|offset
operator|<
name|eod
condition|?
name|eod
operator|-
name|offset
else|:
literal|0
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
return|return
name|offset
operator|<
name|eod
condition|?
name|data
index|[
name|offset
operator|++
index|]
operator|&
literal|0xff
else|:
name|END_OF_STREAM
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
operator|<
literal|0
operator|||
name|off
operator|+
name|len
operator|>
name|b
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
if|if
condition|(
name|offset
operator|>=
name|eod
condition|)
block|{
return|return
name|END_OF_STREAM
return|;
block|}
name|int
name|actualLen
init|=
name|eod
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|actualLen
condition|)
block|{
name|actualLen
operator|=
name|len
expr_stmt|;
block|}
if|if
condition|(
name|actualLen
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|actualLen
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|actualLen
expr_stmt|;
return|return
name|actualLen
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
parameter_list|)
block|{
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Skipping backward is not supported"
argument_list|)
throw|;
block|}
name|long
name|actualSkip
init|=
name|eod
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|actualSkip
condition|)
block|{
name|actualSkip
operator|=
name|n
expr_stmt|;
block|}
name|offset
operator|+=
name|actualSkip
expr_stmt|;
return|return
name|actualSkip
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
specifier|final
name|int
name|readlimit
parameter_list|)
block|{
name|this
operator|.
name|markedOffset
operator|=
name|this
operator|.
name|offset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|offset
operator|=
name|this
operator|.
name|markedOffset
expr_stmt|;
block|}
block|}
end_class

end_unit

