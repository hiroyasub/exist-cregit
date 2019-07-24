begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:patrick@reini.net">Patrick Reinhart</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|MemoryContents
block|{
comment|/**      * Returns the amount of bytes held in the memory.      *      * @return the total amount of data      */
name|long
name|size
parameter_list|()
function_decl|;
comment|/**      * Reads the from the memory data into the given {@code dst} buffer.      *      * @param dst      the destination data buffer to write to      * @param position the position to start      * @param off      the offset within the target destination buffer      * @param len      the amount of bytes to write at maximum      * @return the amount of data written into the target buffer      * @throws IOException if the write of data failed      */
name|int
name|read
parameter_list|(
name|byte
index|[]
name|dst
parameter_list|,
name|long
name|position
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
comment|/**      * Writes the from the given {@code src} data buffer array into the memory.      *      * @param src the source data buffer to read from      * @param off the offset within the source data buffer      * @param len the total amount of bytes to read      * @return the amount of actually read bytes from the buffer      * @throws IOException if the read of data failed      */
name|int
name|writeAtEnd
parameter_list|(
name|byte
index|[]
name|src
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
comment|/**      * Writes the from the given {@code src} data buffer array into the memory.      *      * @param src      the source data buffer to read from      * @param position the position within the memory to start at      * @param off      the offset within the source data buffer      * @param len      the total amount of bytes to read      * @return the amount of actually read bytes from the buffer      * @throws IOException if the read of data failed      */
name|int
name|write
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|long
name|position
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
comment|/**      * Writes all available data from memory to the given {@code target} stream,      * starting at the {@code position}.      *      * @param target   the target stream to write to      * @param position the position to start read from      * @return the amount of bytes written to the target      * @throws IOException if the write of data failed      */
name|long
name|transferTo
parameter_list|(
name|OutputStream
name|target
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Resets the memory contents to have the same state as it would be if newly constructed.      */
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

