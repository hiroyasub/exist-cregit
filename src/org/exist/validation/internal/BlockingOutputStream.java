begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
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
comment|/**  *<code>BlockingOutputStream</code> is a combination of an output stream and  * an input stream, connected through a (circular) buffer in memory.  * It is intended for coupling producer threads to consumer threads via a  * (byte) stream.  * When the buffer is full producer threads will be blocked until the buffer  * has some free space again. When the buffer is empty the consumer threads will  * be blocked until some bytes are available again.  *   */
end_comment

begin_class
specifier|public
class|class
name|BlockingOutputStream
extends|extends
name|OutputStream
block|{
specifier|private
specifier|final
specifier|static
name|int
name|EOS
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|CAPACITY
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
name|CAPACITY
operator|+
literal|1
decl_stmt|;
specifier|private
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|SIZE
index|]
decl_stmt|;
comment|// Circular queue.
specifier|private
name|int
name|head
decl_stmt|;
comment|// First full buffer position.
specifier|private
name|int
name|tail
decl_stmt|;
comment|// First empty buffer position.
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|/* InputStream methods */
comment|/**      * Reads the next byte of data from the input stream. The value byte is      * returned as an<code>int</code> in the range<code>0</code> to      *<code>255</code>. If no byte is available because the end of the stream      * has been reached, the value<code>-1</code> is returned. This method      * blocks until input data is available, the end of the stream is detected,      * or an exception is thrown.      *      * @return     the next byte of data, or<code>-1</code> if the end of the      *             stream is reached.      * @throws     IOException  if an I/O error occurs.      */
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|bb
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
return|return
operator|(
name|read
argument_list|(
name|bb
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|==
name|EOS
operator|)
condition|?
name|EOS
else|:
name|bb
index|[
literal|0
index|]
return|;
block|}
comment|/**      * Reads up to<code>len</code> bytes of data from the input stream into      * an array of bytes.  An attempt is made to read as many as      *<code>len</code> bytes, but a smaller number may be read.      * The number of bytes actually read is returned as an integer.      *      *<p> This method blocks until input data is available, end of file is      * detected, or an exception is thrown.      *      * @param      b     the buffer into which the data is read.      * @param      off   the start offset in array<code>b</code>      *                   at which the data is written.      * @param      len   the maximum number of bytes to read.      * @return     the total number of bytes read into the buffer, or      *<code>-1</code> if there is no more data because the end of      *             the stream has been reached.      * @throws     IOException  if an I/O error occurs.      * @throws     NullPointerException  if<code>b</code> is<code>null</code>.      */
specifier|public
specifier|synchronized
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
name|count
init|=
name|EOS
decl_stmt|;
try|try
block|{
while|while
condition|(
name|empty
argument_list|()
operator|&&
operator|!
name|closed
condition|)
name|wait
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|count
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|available
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count1
init|=
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|availablePart1
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|head
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|count1
argument_list|)
expr_stmt|;
name|int
name|count2
init|=
name|count
operator|-
name|count1
decl_stmt|;
if|if
condition|(
name|count2
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|off
operator|+
name|count1
argument_list|,
name|count2
argument_list|)
expr_stmt|;
block|}
name|head
operator|=
name|next
argument_list|(
name|head
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|empty
argument_list|()
condition|)
name|head
operator|=
name|tail
operator|=
literal|0
expr_stmt|;
comment|// Reset to optimal situation.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//throw new DaMaIOException("Read operation interrupted.", e);
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Read operation interrupted."
operator|+
name|e
argument_list|)
throw|;
block|}
return|return
name|count
return|;
block|}
comment|/**      * Closes this input stream and releases any system resources associated      * with the stream.      *      * @throws     IOException  if an I/O error occurs.      */
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/**      * The number of bytes that can be read (or skipped over) from      * this input stream without blocking by the next caller of a method for      * this input stream.      *      * @return     the number of bytes that can be read from this input stream      *             without blocking.      * @throws     IOException  if an I/O error occurs.      */
specifier|public
specifier|synchronized
name|int
name|available
parameter_list|()
block|{
return|return
operator|(
name|tail
operator|-
name|head
operator|+
name|SIZE
operator|)
operator|%
name|SIZE
return|;
block|}
specifier|private
name|int
name|availablePart1
parameter_list|()
block|{
return|return
operator|(
name|tail
operator|>=
name|head
operator|)
condition|?
name|tail
operator|-
name|head
else|:
name|SIZE
operator|-
name|head
return|;
block|}
specifier|private
name|int
name|availablePart2
parameter_list|()
block|{
return|return
operator|(
name|tail
operator|>=
name|head
operator|)
condition|?
literal|0
else|:
name|tail
return|;
block|}
comment|/* OutputStream methods */
comment|/**      * Writes the specified byte to this output stream. The general       * contract for<code>write</code> is that one byte is written       * to the output stream. The byte to be written is the eight       * low-order bits of the argument<code>b</code>. The 24       * high-order bits of<code>b</code> are ignored.      *      * @param      b   the<code>byte</code>.      * @throws     IOException  if an I/O error occurs. In particular,       *             an<code>IOException</code> may be thrown if the       *             output stream has been closed.      */
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|bb
index|[]
init|=
block|{
operator|(
name|byte
operator|)
name|b
block|}
decl_stmt|;
name|write
argument_list|(
name|bb
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes<code>len</code> bytes from the specified byte array       * starting at offset<code>off</code> to this output stream.       * The general contract for<code>write(b, off, len)</code> is that       * some of the bytes in the array<code>b</code> are written to the       * output stream in order; element<code>b[off]</code> is the first       * byte written and<code>b[off+len-1]</code> is the last byte written       * by this operation.      *      * @param      b     the data.      * @param      off   the start offset in the data.      * @param      len   the number of bytes to write.      * @throws     IOException  if an I/O error occurs. In particular,       *             an<code>IOException</code> is thrown if the output       *             stream is closed.      */
specifier|public
specifier|synchronized
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
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|count
decl_stmt|;
try|try
block|{
while|while
condition|(
name|full
argument_list|()
operator|&&
operator|!
name|closed
condition|)
name|wait
argument_list|()
expr_stmt|;
if|if
condition|(
name|closed
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Writing to closed stream"
argument_list|)
throw|;
name|count
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|free
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count1
init|=
name|Math
operator|.
name|min
argument_list|(
name|count
argument_list|,
name|freePart1
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buffer
argument_list|,
name|tail
argument_list|,
name|count1
argument_list|)
expr_stmt|;
name|int
name|count2
init|=
name|count
operator|-
name|count1
decl_stmt|;
if|if
condition|(
name|count2
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|count1
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|count2
argument_list|)
expr_stmt|;
block|}
name|tail
operator|=
name|next
argument_list|(
name|tail
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Write operation interrupted."
operator|+
name|e
argument_list|)
throw|;
block|}
name|off
operator|+=
name|count
expr_stmt|;
name|len
operator|-=
name|count
expr_stmt|;
block|}
block|}
comment|/**      * Equivalent of the<code>close()</code> method of an output stream.      * Renamed to solve the name clash with the<code>close()</code> method      * of the input stream also implemented by this class.      * Closes this output stream and releases any system resources       * associated with this stream. A closed stream cannot perform       * output operations and cannot be reopened.      *<p>      * This method blocks its caller until all bytes remaining in the buffer      * are read from the buffer by the receiving threads or an exception occurs.      *      * @throws     IOException  if an I/O error occurs.      */
specifier|public
specifier|synchronized
name|void
name|closeOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
while|while
condition|(
operator|!
name|empty
argument_list|()
operator|&&
operator|!
name|closed
condition|)
name|wait
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|empty
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Closing non empty stream."
argument_list|)
throw|;
name|closed
operator|=
literal|true
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Close OutputStream operation interrupted."
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Flushes this output stream and forces any buffered output bytes       * to be written out.      *<p>      * This methods blocks its caller until all buffered bytes are actually      * read by the consuming threads.      *      * @throws     IOException  if an I/O error occurs.      */
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
while|while
condition|(
operator|!
name|empty
argument_list|()
operator|&&
operator|!
name|closed
condition|)
name|wait
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|empty
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Flushing non empty closed stream."
argument_list|)
throw|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Flush operation interrupted."
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * The number of bytes that can be written to      * this output stream without blocking by the next caller of a method for      * this output stream.      *      * @return     the number of bytes that can be written to this output stream      *             without blocking.      * @throws     IOException  if an I/O error occurs.      */
specifier|public
specifier|synchronized
name|int
name|free
parameter_list|()
block|{
name|int
name|prevhead
init|=
name|prev
argument_list|(
name|head
argument_list|)
decl_stmt|;
return|return
operator|(
name|prevhead
operator|-
name|tail
operator|+
name|SIZE
operator|)
operator|%
name|SIZE
return|;
block|}
specifier|private
name|int
name|freePart1
parameter_list|()
block|{
name|int
name|prevhead
init|=
name|prev
argument_list|(
name|head
argument_list|)
decl_stmt|;
return|return
operator|(
name|prevhead
operator|>=
name|tail
operator|)
condition|?
name|prevhead
operator|-
name|tail
else|:
name|SIZE
operator|-
name|tail
return|;
block|}
specifier|private
name|int
name|freePart2
parameter_list|()
block|{
name|int
name|prevhead
init|=
name|prev
argument_list|(
name|head
argument_list|)
decl_stmt|;
return|return
operator|(
name|prevhead
operator|>=
name|tail
operator|)
condition|?
literal|0
else|:
name|prevhead
return|;
block|}
comment|/* Buffer management methods */
specifier|private
name|boolean
name|empty
parameter_list|()
block|{
return|return
name|head
operator|==
name|tail
return|;
block|}
specifier|private
name|boolean
name|full
parameter_list|()
block|{
return|return
name|next
argument_list|(
name|tail
argument_list|)
operator|==
name|head
return|;
block|}
specifier|private
specifier|static
name|int
name|next
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|next
argument_list|(
name|pos
argument_list|,
literal|1
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|next
parameter_list|(
name|int
name|pos
parameter_list|,
name|int
name|incr
parameter_list|)
block|{
return|return
operator|(
name|pos
operator|+
name|incr
operator|)
operator|%
name|SIZE
return|;
block|}
specifier|private
specifier|static
name|int
name|prev
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|prev
argument_list|(
name|pos
argument_list|,
literal|1
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|prev
parameter_list|(
name|int
name|pos
parameter_list|,
name|int
name|decr
parameter_list|)
block|{
return|return
operator|(
name|pos
operator|-
name|decr
operator|+
name|SIZE
operator|)
operator|%
name|SIZE
return|;
block|}
block|}
end_class

end_unit

