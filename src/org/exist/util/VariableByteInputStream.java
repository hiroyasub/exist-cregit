begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
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
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|InputStream
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

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    21. September 2002  */
end_comment

begin_class
specifier|public
class|class
name|VariableByteInputStream
block|{
specifier|private
name|InputStream
name|is
decl_stmt|;
comment|/** 	 *  Constructor for the VariableByteInputStream object 	 * 	 *@param  data  Description of the Parameter 	 */
specifier|public
name|VariableByteInputStream
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|is
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|VariableByteInputStream
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
name|is
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|VariableByteInputStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|is
operator|=
name|stream
expr_stmt|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|byte
operator|)
name|is
operator|.
name|read
argument_list|()
return|;
block|}
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
throws|,
name|EOFException
block|{
try|try
block|{
return|return
operator|(
name|short
operator|)
name|VariableByteCoding
operator|.
name|decode
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|EOFException
throws|,
name|IOException
block|{
try|try
block|{
return|return
operator|(
name|int
operator|)
name|VariableByteCoding
operator|.
name|decode
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|EOFException
throws|,
name|IOException
block|{
try|try
block|{
return|return
name|VariableByteCoding
operator|.
name|decode
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
block|}
specifier|public
name|long
name|readFixedLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|VariableByteCoding
operator|.
name|decodeFixed
argument_list|(
name|is
argument_list|)
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
name|is
operator|.
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
comment|/** 	 *  Description of the Method 	 * 	 *@param  count  Description of the Parameter 	 */
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
name|is
operator|.
name|available
argument_list|()
operator|>
literal|0
condition|;
name|i
operator|++
control|)
comment|//VariableByteCoding.decode(is);
name|VariableByteCoding
operator|.
name|skipNext
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|is
operator|.
name|available
argument_list|()
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
name|VariableByteCoding
operator|.
name|copyTo
argument_list|(
name|is
argument_list|,
name|os
operator|.
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

