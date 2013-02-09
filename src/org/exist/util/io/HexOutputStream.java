begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2012, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
name|FilterOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|DecoderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Hex
import|;
end_import

begin_comment
comment|/**  * Hexadecimal encoding OutputStream  *  * Based on org.apache.commons.codec.binary.Base64OutputStream  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|HexOutputStream
extends|extends
name|FilterOutputStream
block|{
specifier|private
specifier|final
name|Hex
name|hex
init|=
operator|new
name|Hex
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|doEncode
init|=
literal|false
decl_stmt|;
comment|/**      * Creates a HexOutputStream such that all data written is Hex-encoded to the original provided OutputStream.      *      * @param out      *            OutputStream to wrap.      */
specifier|public
name|HexOutputStream
parameter_list|(
specifier|final
name|OutputStream
name|out
parameter_list|,
specifier|final
name|boolean
name|doEncode
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|doEncode
operator|=
name|doEncode
expr_stmt|;
block|}
comment|/**      * Writes the specified<code>byte</code> to this output stream.      *      * @param i      *            source byte      * @throws IOException      *             if an I/O error occurs.      */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|singleByte
index|[]
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|i
block|}
decl_stmt|;
name|write
argument_list|(
name|singleByte
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes<code>len</code> bytes from the specified<code>b</code> array starting at<code>offset</code> to this      * output stream.      *      * @param b      *            source byte array      * @param offset      *            where to start reading the bytes      * @param len      *            maximum number of bytes to write      *      * @throws IOException      *             if an I/O error occurs.      * @throws NullPointerException      *             if the byte array parameter is null      * @throws IndexOutOfBoundsException      *             if offset, len or buffer size are invalid      */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
name|b
index|[]
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
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
name|offset
operator|<
literal|0
operator|||
name|len
operator|<
literal|0
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
name|offset
operator|>
name|b
operator|.
name|length
operator|||
name|offset
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
if|else if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
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
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|b
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
if|if
condition|(
name|doEncode
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|hex
operator|.
name|encode
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|hex
operator|.
name|decode
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DecoderException
name|de
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to decode: "
operator|+
name|de
operator|.
name|getMessage
argument_list|()
argument_list|,
name|de
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

