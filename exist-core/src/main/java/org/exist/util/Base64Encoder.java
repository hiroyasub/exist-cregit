begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *   * The contents of this [class] are subject to the Netscape Public  * License Version 1.1 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of  * the License at http://www.mozilla.org/NPL/  *  * Software distributed under the License is distributed on an "AS  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or  * implied. See the License for the specific language governing  * rights and limitations under the License.  *  * The Original Code is mozilla.org code.  *  * The Initial Developer of the Original Code is Netscape  * Communications Corporation.  Portions created by Netscape are  * Copyright (C) 1999 Netscape Communications Corporation. All  * Rights Reserved.  *  * Contributor(s):  */
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

begin_comment
comment|/**      * Byte to text encoder using base 64 encoding. To create a base 64      * encoding of a byte stream call {@link #translate} for every      * sequence of bytes and {@link #getCharArray} to mark closure of      * the byte stream and retrieve the text presentation.      *      * @author Based on code from the Mozilla Directory SDK      */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Base64Encoder
block|{
specifier|private
name|FastStringBuffer
name|out
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|256
argument_list|)
decl_stmt|;
specifier|private
name|int
name|buf
init|=
literal|0
decl_stmt|;
comment|// a 24-bit quantity
specifier|private
name|int
name|buf_bytes
init|=
literal|0
decl_stmt|;
comment|// how many octets are set in it
specifier|private
name|char
name|line
index|[]
init|=
operator|new
name|char
index|[
literal|74
index|]
decl_stmt|;
comment|// output buffer
specifier|private
name|int
name|line_length
init|=
literal|0
decl_stmt|;
comment|// output buffer fill pointer
comment|//static private final byte crlf[] = "\r\n".getBytes();
specifier|private
specifier|static
specifier|final
name|char
name|map
index|[]
init|=
block|{
literal|'A'
block|,
literal|'B'
block|,
literal|'C'
block|,
literal|'D'
block|,
literal|'E'
block|,
literal|'F'
block|,
literal|'G'
block|,
literal|'H'
block|,
comment|// 0-7
literal|'I'
block|,
literal|'J'
block|,
literal|'K'
block|,
literal|'L'
block|,
literal|'M'
block|,
literal|'N'
block|,
literal|'O'
block|,
literal|'P'
block|,
comment|// 8-15
literal|'Q'
block|,
literal|'R'
block|,
literal|'S'
block|,
literal|'T'
block|,
literal|'U'
block|,
literal|'V'
block|,
literal|'W'
block|,
literal|'X'
block|,
comment|// 16-23
literal|'Y'
block|,
literal|'Z'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|,
comment|// 24-31
literal|'g'
block|,
literal|'h'
block|,
literal|'i'
block|,
literal|'j'
block|,
literal|'k'
block|,
literal|'l'
block|,
literal|'m'
block|,
literal|'n'
block|,
comment|// 32-39
literal|'o'
block|,
literal|'p'
block|,
literal|'q'
block|,
literal|'r'
block|,
literal|'s'
block|,
literal|'t'
block|,
literal|'u'
block|,
literal|'v'
block|,
comment|// 40-47
literal|'w'
block|,
literal|'x'
block|,
literal|'y'
block|,
literal|'z'
block|,
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
comment|// 48-55
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'+'
block|,
literal|'/'
block|,
comment|// 56-63
block|}
decl_stmt|;
specifier|private
name|void
name|encode_token
parameter_list|()
block|{
specifier|final
name|int
name|i
init|=
name|line_length
decl_stmt|;
name|line
index|[
name|i
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
operator|(
name|buf
operator|>>
literal|18
operator|)
index|]
expr_stmt|;
comment|// sextet 1 (octet 1)
name|line
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
operator|(
name|buf
operator|>>
literal|12
operator|)
index|]
expr_stmt|;
comment|// sextet 2 (octet 1 and 2)
name|line
index|[
name|i
operator|+
literal|2
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
operator|(
name|buf
operator|>>
literal|6
operator|)
index|]
expr_stmt|;
comment|// sextet 3 (octet 2 and 3)
name|line
index|[
name|i
operator|+
literal|3
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
name|buf
index|]
expr_stmt|;
comment|// sextet 4 (octet 3)
name|line_length
operator|+=
literal|4
expr_stmt|;
name|buf
operator|=
literal|0
expr_stmt|;
name|buf_bytes
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
name|void
name|encode_partial_token
parameter_list|()
block|{
specifier|final
name|int
name|i
init|=
name|line_length
decl_stmt|;
name|line
index|[
name|i
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
operator|(
name|buf
operator|>>
literal|18
operator|)
index|]
expr_stmt|;
comment|// sextet 1 (octet 1)
name|line
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
operator|(
name|buf
operator|>>
literal|12
operator|)
index|]
expr_stmt|;
comment|// sextet 2 (octet 1 and 2)
if|if
condition|(
name|buf_bytes
operator|==
literal|1
condition|)
block|{
name|line
index|[
name|i
operator|+
literal|2
index|]
operator|=
literal|'='
expr_stmt|;
block|}
else|else
block|{
name|line
index|[
name|i
operator|+
literal|2
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
operator|(
name|buf
operator|>>
literal|6
operator|)
index|]
expr_stmt|;
block|}
comment|// sextet 3 (octet 2 and 3)
if|if
condition|(
name|buf_bytes
operator|<=
literal|2
condition|)
block|{
name|line
index|[
name|i
operator|+
literal|3
index|]
operator|=
literal|'='
expr_stmt|;
block|}
else|else
block|{
name|line
index|[
name|i
operator|+
literal|3
index|]
operator|=
name|map
index|[
literal|0x3F
operator|&
name|buf
index|]
expr_stmt|;
block|}
comment|// sextet 4 (octet 3)
name|line_length
operator|+=
literal|4
expr_stmt|;
name|buf
operator|=
literal|0
expr_stmt|;
name|buf_bytes
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
name|void
name|flush_line
parameter_list|()
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|,
literal|0
argument_list|,
name|line_length
argument_list|)
expr_stmt|;
name|line_length
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Given a sequence of input bytes, produces a sequence of output bytes      * using the base64 encoding.  If there are bytes in `out' already, the      * new bytes are appended, so the caller should do `out.setLength(0)'      * first if that's desired.      */
specifier|public
specifier|final
name|void
name|translate
parameter_list|(
name|byte
index|[]
name|in
parameter_list|)
block|{
specifier|final
name|int
name|in_length
init|=
name|in
operator|.
name|length
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
name|in_length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|buf_bytes
operator|==
literal|0
condition|)
block|{
name|buf
operator|=
operator|(
name|buf
operator|&
literal|0x00FFFF
operator|)
operator||
operator|(
name|in
index|[
name|i
index|]
operator|<<
literal|16
operator|)
expr_stmt|;
block|}
if|else if
condition|(
name|buf_bytes
operator|==
literal|1
condition|)
block|{
name|buf
operator|=
operator|(
name|buf
operator|&
literal|0xFF00FF
operator|)
operator||
operator|(
operator|(
name|in
index|[
name|i
index|]
operator|<<
literal|8
operator|)
operator|&
literal|0x00FFFF
operator|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|=
operator|(
name|buf
operator|&
literal|0xFFFF00
operator|)
operator||
operator|(
name|in
index|[
name|i
index|]
operator|&
literal|0x0000FF
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|++
name|buf_bytes
operator|)
operator|==
literal|3
condition|)
block|{
name|encode_token
argument_list|()
expr_stmt|;
if|if
condition|(
name|line_length
operator|>=
literal|72
condition|)
block|{
name|flush_line
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|==
operator|(
name|in_length
operator|-
literal|1
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|buf_bytes
operator|>
literal|0
operator|)
operator|&&
operator|(
name|buf_bytes
operator|<
literal|3
operator|)
condition|)
block|{
name|encode_partial_token
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|line_length
operator|>
literal|0
condition|)
block|{
name|flush_line
argument_list|()
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|line
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|line
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|char
index|[]
name|getCharArray
parameter_list|()
block|{
name|char
index|[]
name|ch
decl_stmt|;
if|if
condition|(
name|buf_bytes
operator|!=
literal|0
condition|)
block|{
name|encode_partial_token
argument_list|()
expr_stmt|;
block|}
name|flush_line
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|line
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|line
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
name|ch
operator|=
operator|new
name|char
index|[
name|out
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|out
operator|.
name|length
argument_list|()
argument_list|,
name|ch
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|ch
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|out
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
