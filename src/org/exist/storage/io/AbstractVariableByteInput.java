begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-04, Wolfgang M.  * Meier (meier@ifs.tu-darmstadt.de)  *   * This library is free software; you can redistribute it and/or modify it under  * the terms of the GNU Library General Public License as published by the Free  * Software Foundation; either version 2 of the License, or (at your option) any  * later version.  *   * This library is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more  * details.  *   * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  *   * $Id$  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Abstract base class for implementations of VariableByteInput.  *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractVariableByteInput
implements|implements
name|VariableByteInput
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractVariableByteInput
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|AbstractVariableByteInput
parameter_list|()
block|{
comment|//Nothing to do
block|}
specifier|public
specifier|abstract
name|int
name|available
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|int
name|read
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|i
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
return|return
operator|(
name|byte
operator|)
name|i
return|;
block|}
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
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
name|b
operator|=
name|readByte
argument_list|()
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
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
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
name|b
operator|=
name|readByte
argument_list|()
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
name|int
name|readFixedInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|readByte
argument_list|()
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
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
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|long
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
name|b
operator|=
name|readByte
argument_list|()
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
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
throws|,
name|EOFException
block|{
name|int
name|len
init|=
name|readInt
argument_list|()
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|String
name|s
decl_stmt|;
try|try
block|{
name|s
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
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
name|s
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
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
operator|&&
name|available
argument_list|()
operator|>
literal|0
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
operator|(
name|readByte
argument_list|()
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
condition|;
name|i
operator|++
control|)
name|readByte
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
return|;
block|}
specifier|public
name|int
name|read
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
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|else if
condition|(
operator|(
name|off
operator|<
literal|0
operator|)
operator|||
operator|(
name|off
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
name|len
operator|<
literal|0
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|>
name|b
operator|.
name|length
operator|)
operator|||
operator|(
operator|(
name|off
operator|+
name|len
operator|)
operator|<
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
if|else if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|c
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|b
index|[
name|off
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
block|{
name|b
index|[
name|off
operator|+
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ee
parameter_list|)
block|{
comment|//Nothing to do
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
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|more
decl_stmt|;
do|do
block|{
name|more
operator|=
name|read
argument_list|()
expr_stmt|;
name|os
operator|.
name|buf
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|more
argument_list|)
expr_stmt|;
name|more
operator|&=
literal|0200
expr_stmt|;
block|}
do|while
condition|(
name|more
operator|>
literal|0
condition|)
do|;
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
name|int
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
name|read
argument_list|()
expr_stmt|;
name|os
operator|.
name|buf
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|more
argument_list|)
expr_stmt|;
name|more
operator|&=
literal|0200
expr_stmt|;
block|}
do|while
condition|(
name|more
operator|>
literal|0
condition|)
do|;
block|}
block|}
specifier|public
name|void
name|copyRaw
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
name|os
operator|.
name|buf
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.io.VariableByteInput#release()      */
specifier|public
name|void
name|release
parameter_list|()
block|{
comment|//Nothing to do
block|}
block|}
end_class

end_unit

