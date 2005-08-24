begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001, Wolfgang M. Meier  * (meier@ifs.tu-darmstadt.de)  *   * This library is free software; you can redistribute it and/or modify it under  * the terms of the GNU Library General Public License as published by the Free  * Software Foundation; either version 2 of the License, or (at your option) any  * later version.  *   * This library is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more  * details.  *   * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|EOFException
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

begin_comment
comment|/**  * Implements VariableByteInput on top of a byte array.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|VariableByteArrayInput
extends|extends
name|AbstractVariableByteInput
block|{
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
specifier|private
name|int
name|end
decl_stmt|;
specifier|public
name|VariableByteArrayInput
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|VariableByteArrayInput
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|position
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|data
operator|.
name|length
expr_stmt|;
block|}
specifier|public
name|VariableByteArrayInput
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|offset
operator|+
name|length
expr_stmt|;
block|}
specifier|public
name|void
name|initialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
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
name|position
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|offset
operator|+
name|length
expr_stmt|;
block|}
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
throws|,
name|EOFException
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
return|return
name|data
index|[
name|position
operator|++
index|]
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see java.io.InputStream#read()      */
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
return|return
operator|-
literal|1
return|;
return|return
name|data
index|[
name|position
operator|++
index|]
operator|&
literal|0xFF
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see java.io.InputStream#available()      */
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|end
operator|-
name|position
return|;
block|}
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
name|byte
name|b
init|=
name|data
index|[
name|position
operator|++
index|]
decl_stmt|;
name|short
name|i
init|=
operator|(
name|short
operator|)
operator|(
name|b
operator|&
literal|0177
operator|)
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0200
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
name|b
operator|=
name|data
index|[
name|position
operator|++
index|]
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0177
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
name|byte
name|b
init|=
name|data
index|[
name|position
operator|++
index|]
decl_stmt|;
name|int
name|i
init|=
name|b
operator|&
literal|0177
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0200
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
name|b
operator|=
name|data
index|[
name|position
operator|++
index|]
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0177
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
specifier|public
name|int
name|readFixedInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|data
index|[
name|position
operator|++
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|position
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|position
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|position
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
return|;
block|}
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
name|byte
name|b
init|=
name|data
index|[
name|position
operator|++
index|]
decl_stmt|;
name|long
name|i
init|=
name|b
operator|&
literal|0177L
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0200
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
if|if
condition|(
name|position
operator|==
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
name|b
operator|=
name|data
index|[
name|position
operator|++
index|]
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0177L
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|more
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
do|do
block|{
name|more
operator|=
name|data
index|[
name|position
operator|++
index|]
expr_stmt|;
name|os
operator|.
name|buf
operator|.
name|append
argument_list|(
name|more
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|(
name|more
operator|&
literal|0x200
operator|)
operator|>
literal|0
condition|)
do|;
block|}
block|}
specifier|public
name|void
name|skip
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
name|position
operator|<
name|end
operator|&&
operator|(
name|data
index|[
name|position
operator|++
index|]
operator|&
literal|0200
operator|)
operator|>
literal|0
condition|)
empty_stmt|;
block|}
block|}
specifier|public
name|void
name|skipBytes
parameter_list|(
name|long
name|count
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
operator|&&
name|position
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|position
operator|++
expr_stmt|;
block|}
block|}
end_class

end_unit

