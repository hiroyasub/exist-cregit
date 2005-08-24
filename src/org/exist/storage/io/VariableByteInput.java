begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
comment|/**  * Interface for reading variable byte encoded values.  *   * Variable byte encoding offers a good compression ratio if the stored  * values are rather small, i.e. much smaller than the possible maximum for  * the given type.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|VariableByteInput
block|{
comment|/**      * Read a single byte and return as an int value.      *       * @return the byte value as int or -1 if no more bytes are available.      * @throws IOException      */
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Fill the provided byte array with data from the input.      *       * @param data      * @return      * @throws IOException      */
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
function_decl|;
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
function_decl|;
comment|/**      * Returns a value> 0 if more bytes can be read      * from the input.      *       * @return      * @throws IOException      */
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Read a single byte. Throws EOFException if no      * more bytes are available.      *       * @return      * @throws IOException      */
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Read a short value in variable byte encoding.      *       * @return      * @throws IOException      */
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Read an integer value in variable byte encoding.      *       * @return      * @throws IOException      */
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|int
name|readFixedInt
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Read a long value in variable byte encoding.      *       * @return      * @throws IOException      */
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
throws|,
name|EOFException
function_decl|;
comment|/**      * Read the following count numeric values from the input      * and drop them.      *       * @param count      * @throws IOException      */
specifier|public
name|void
name|skip
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
name|void
name|skipBytes
parameter_list|(
name|long
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Copy the next numeric value from the input to the      * specified output stream.      *       * @param os      * @throws IOException      */
specifier|public
name|void
name|copyTo
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Copy the count next numeric values from the input to      * the specified output stream.      *       * @param os      * @param count      * @throws IOException      */
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
function_decl|;
block|}
end_interface

end_unit

