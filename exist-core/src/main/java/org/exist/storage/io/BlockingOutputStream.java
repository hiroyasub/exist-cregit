begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: BlockingOutputStream.java 223 2007-04-21 22:13:05Z dizzzz $  */
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
comment|/**  * Output stream adapter for a BlockingInputStream.  *  * @author Chris Offerman  */
end_comment

begin_class
specifier|public
class|class
name|BlockingOutputStream
extends|extends
name|OutputStream
block|{
specifier|private
name|BlockingInputStream
name|bis
decl_stmt|;
comment|/** Create a new BlockingOutputStream adapter.      *      *@param stream  The BlockingInputStream to adapt.      */
specifier|public
name|BlockingOutputStream
parameter_list|(
name|BlockingInputStream
name|stream
parameter_list|)
block|{
name|bis
operator|=
name|stream
expr_stmt|;
block|}
comment|/**      * BlockingInputStream of this BlockingOutputStream.      */
specifier|public
name|BlockingInputStream
name|getInputStream
parameter_list|()
block|{
return|return
name|bis
return|;
block|}
comment|/**      * Writes the specified byte to this output stream. The general       * contract for<code>write</code> is that one byte is written       * to the output stream. The byte to be written is the eight       * low-order bits of the argument<code>b</code>. The 24       * high-order bits of<code>b</code> are ignored.      *       *       * @param b   the<code>byte</code>.      * @throws ExistIOException  if an I/O error occurs. In particular,       *             an<code>ExistIOException</code> may be thrown if the       *             output stream has been closed.      */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|bis
operator|.
name|writeOutputStream
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes<code>len</code> bytes from the specified byte array       * starting at offset<code>off</code> to this output stream.       * The general contract for<code>write(b, off, len)</code> is that       * some of the bytes in the array<code>b</code> are written to the       * output stream in order; element<code>b[off]</code> is the first       * byte written and<code>b[off+len-1]</code> is the last byte written       * by this operation.      *       *       * @param b     the data.      * @param off   the start offset in the data.      * @param len   the number of bytes to write.      * @throws IOException  if an I/O error occurs. In particular,       *             an<code>IOException</code> is thrown if the output       *             stream is closed.      */
annotation|@
name|Override
specifier|public
name|void
name|write
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
name|bis
operator|.
name|writeOutputStream
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**      * Closes this output stream.      * A closed stream cannot perform output operations and cannot be reopened.      *<p>      * This method blocks its caller until the corresponding input stream is      * closed or an exception occurs.      *       * @throws IOException  if an I/O error occurs.      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|bis
operator|.
name|closeOutputStream
argument_list|()
expr_stmt|;
block|}
comment|/**      * Closes this output stream, specifying that an exception has occurred.      * This will cause all consumer calls to be unblocked and throw an      * IOException with this exception as its cause.      *<code>BlockingInputStream</code> specific method.      * @throws IOException  if an I/O error occurs.      */
specifier|public
name|void
name|close
parameter_list|(
name|Exception
name|ex
parameter_list|)
throws|throws
name|IOException
block|{
name|bis
operator|.
name|closeOutputStream
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
comment|/**      * Flushes this output stream and forces any buffered output bytes       * to be written out.      *<p>      * This methods blocks its caller until all buffered bytes are actually      * read by the consuming threads.      *       *       * @throws IOException  if an I/O error occurs.      */
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|bis
operator|.
name|flushOutputStream
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
