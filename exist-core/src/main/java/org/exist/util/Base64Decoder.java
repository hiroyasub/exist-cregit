begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * The contents of this [class] are subject to the Netscape Public  * License Version 1.1 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of  * the License at http://www.mozilla.org/NPL/  *  * Software distributed under the License is distributed on an "AS  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or  * implied. See the License for the specific language governing  * rights and limitations under the License.  *  * The Original Code is mozilla.org code.  *  * The Initial Developer of the Original Code is Netscape  * Communications Corporation.  Portions created by Netscape are  * Copyright (C) 1999 Netscape Communications Corporation. All  * Rights Reserved.  *  * Contributor(s):  */
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
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
import|;
end_import

begin_comment
comment|/**  * Base 64 text to byte decoder. To produce the binary  array from  * base 64 encoding call {@link #translate} for each sequence of  * characters and {@link #getByteArray} to mark closure of the  * character stream and retrieve the binary contents.  *  * @author Based on code from the Mozilla Directory SDK  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Base64Decoder
block|{
specifier|private
name|FastByteArrayOutputStream
name|out
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|private
name|byte
name|token
index|[]
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
comment|// input buffer
specifier|private
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
comment|// output buffer
specifier|private
name|int
name|token_length
init|=
literal|0
decl_stmt|;
comment|// input buffer length
specifier|private
specifier|static
specifier|final
name|byte
name|NUL
init|=
literal|127
decl_stmt|;
comment|// must be out of range 0-64
specifier|private
specifier|static
specifier|final
name|byte
name|EOF
init|=
literal|126
decl_stmt|;
comment|// must be out of range 0-64
specifier|private
specifier|static
specifier|final
name|byte
name|SP
init|=
literal|125
decl_stmt|;
comment|// must be out of range 0-64
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|map
init|=
block|{
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   x00 - x07
name|NUL
block|,
name|SP
block|,
name|SP
block|,
name|NUL
block|,
name|NUL
block|,
name|SP
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   x08 - x0F
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   x10 - x17
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   x18 - x1F
name|SP
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   x20 - x2F   !"#$%&'
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
literal|62
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
literal|63
block|,
comment|//   050-057  ()*+,-./
literal|52
block|,
literal|53
block|,
literal|54
block|,
literal|55
block|,
literal|56
block|,
literal|57
block|,
literal|58
block|,
literal|59
block|,
comment|//   060-067  01234567
literal|60
block|,
literal|61
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|EOF
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   070-077  89:;<=>?
name|NUL
block|,
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
comment|//   100-107  @ABCDEFG
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|14
block|,
comment|//   110-117  HIJKLMNO
literal|15
block|,
literal|16
block|,
literal|17
block|,
literal|18
block|,
literal|19
block|,
literal|20
block|,
literal|21
block|,
literal|22
block|,
comment|//   120-127  PQRSTUVW
literal|23
block|,
literal|24
block|,
literal|25
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   130-137  XYZ[\]^_
name|NUL
block|,
literal|26
block|,
literal|27
block|,
literal|28
block|,
literal|29
block|,
literal|30
block|,
literal|31
block|,
literal|32
block|,
comment|//   140-147  `abcdefg
literal|33
block|,
literal|34
block|,
literal|35
block|,
literal|36
block|,
literal|37
block|,
literal|38
block|,
literal|39
block|,
literal|40
block|,
comment|//   150-157  hijklmno
literal|41
block|,
literal|42
block|,
literal|43
block|,
literal|44
block|,
literal|45
block|,
literal|46
block|,
literal|47
block|,
literal|48
block|,
comment|//   160-167  pqrstuvw
literal|49
block|,
literal|50
block|,
literal|51
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   170-177  xyz{|}~
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   200-207
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   210-217
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   220-227
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   230-237
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   240-247
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   250-257
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   260-267
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   270-277
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   300-307
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   310-317
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   320-327
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   330-337
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   340-347
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   350-357
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   360-367
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
name|NUL
block|,
comment|//   370-377
block|}
decl_stmt|;
comment|// Fast routine that assumes full 4-char tokens with no '=' in them.
comment|//
specifier|private
name|void
name|decode_token
parameter_list|()
block|{
specifier|final
name|int
name|num
init|=
operator|(
operator|(
name|token
index|[
literal|0
index|]
operator|<<
literal|18
operator|)
operator||
operator|(
name|token
index|[
literal|1
index|]
operator|<<
literal|12
operator|)
operator||
operator|(
name|token
index|[
literal|2
index|]
operator|<<
literal|6
operator|)
operator||
operator|(
name|token
index|[
literal|3
index|]
operator|)
operator|)
decl_stmt|;
name|bytes
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xFF
operator|&
operator|(
name|num
operator|>>
literal|16
operator|)
operator|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xFF
operator|&
operator|(
name|num
operator|>>
literal|8
operator|)
operator|)
expr_stmt|;
name|bytes
index|[
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xFF
operator|&
name|num
operator|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
comment|// Hairier routine that deals with the final token, which can have fewer
comment|// than four characters, and that might be padded with '='.
comment|//
specifier|private
name|void
name|decode_final_token
parameter_list|()
block|{
name|byte
name|b0
init|=
name|token
index|[
literal|0
index|]
decl_stmt|;
name|byte
name|b1
init|=
name|token
index|[
literal|1
index|]
decl_stmt|;
name|byte
name|b2
init|=
name|token
index|[
literal|2
index|]
decl_stmt|;
name|byte
name|b3
init|=
name|token
index|[
literal|3
index|]
decl_stmt|;
name|int
name|eq_count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|b0
operator|==
name|EOF
condition|)
block|{
name|b0
operator|=
literal|0
expr_stmt|;
name|eq_count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|b1
operator|==
name|EOF
condition|)
block|{
name|b1
operator|=
literal|0
expr_stmt|;
name|eq_count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|b2
operator|==
name|EOF
condition|)
block|{
name|b2
operator|=
literal|0
expr_stmt|;
name|eq_count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|b3
operator|==
name|EOF
condition|)
block|{
name|b3
operator|=
literal|0
expr_stmt|;
name|eq_count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|eq_count
operator|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The number of '=' signs at the end of a base64 value must not exceed 2"
argument_list|)
throw|;
block|}
if|if
condition|(
name|eq_count
operator|==
literal|2
operator|&&
operator|(
name|b1
operator|&
literal|0x0F
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"In base64, if the value ends with '==' then the last character must be one of [AQgw]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|eq_count
operator|==
literal|1
operator|&&
operator|(
name|b2
operator|&
literal|0x03
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"In base64, if the value ends with '=' then the last character must be one of [AEIMQUYcgkosw048]"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|num
init|=
operator|(
operator|(
name|b0
operator|<<
literal|18
operator|)
operator||
operator|(
name|b1
operator|<<
literal|12
operator|)
operator||
operator|(
name|b2
operator|<<
literal|6
operator|)
operator||
operator|(
name|b3
operator|)
operator|)
decl_stmt|;
comment|// eq_count will be 0, 1, or 2.
comment|// No "=" padding means 4 bytes mapped to 3, the normal case,
comment|//        not handled in this routine.
comment|// "xxx=" means 3 bytes mapped to 2.
comment|// "xx==" means 2 bytes mapped to 1.
comment|// "x===" can't happen, because "x" would then be encoding
comment|//        only 6 bits, not 8, the minimum possible.
name|out
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
operator|(
name|num
operator|>>
literal|16
operator|)
argument_list|)
expr_stmt|;
comment|// byte 1, count = 0 or 1 or 2
if|if
condition|(
name|eq_count
operator|<=
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
operator|(
operator|(
name|num
operator|>>
literal|8
operator|)
operator|&
literal|0xFF
operator|)
argument_list|)
expr_stmt|;
comment|// byte 2, count = 0 or 1
if|if
condition|(
name|eq_count
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
operator|(
name|num
operator|&
literal|0xFF
operator|)
argument_list|)
expr_stmt|;
comment|// byte 3, count = 0
block|}
block|}
block|}
comment|/**      * Decode the base 64 string into a byte array (which can subsequently be accessed using getByteArray()      * @param str the base 64 string      * @throws IllegalArgumentException if the base64 string is incorrectly formatted      */
specifier|public
specifier|final
name|void
name|translate
parameter_list|(
name|CharSequence
name|str
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
comment|// already saw eof marker?
block|{
return|return;
block|}
specifier|final
name|int
name|length
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|lengthAtEOF
decl_stmt|;
name|int
name|found_eq
init|=
literal|0
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
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|>
literal|127
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"non-ASCII character in Base64 value (at offset "
operator|+
name|i
operator|+
literal|')'
argument_list|)
throw|;
block|}
name|byte
name|t
init|=
name|map
index|[
name|c
index|]
decl_stmt|;
if|if
condition|(
name|t
operator|==
name|NUL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid character '"
operator|+
name|c
operator|+
literal|"' in Base64 value (at offset "
operator|+
name|i
operator|+
literal|')'
argument_list|)
throw|;
block|}
if|if
condition|(
name|found_eq
operator|>
literal|0
operator|&&
name|t
operator|!=
name|EOF
operator|&&
name|t
operator|!=
name|SP
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"In Base64, an '=' character can appear only at the end"
argument_list|)
throw|;
block|}
if|if
condition|(
name|t
operator|==
name|EOF
condition|)
block|{
if|if
condition|(
name|found_eq
operator|>
literal|0
condition|)
block|{
name|found_eq
operator|++
expr_stmt|;
if|if
condition|(
name|found_eq
operator|>
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Base64 value can contain at most two '=' characters"
argument_list|)
throw|;
block|}
name|token_length
operator|=
operator|(
name|token_length
operator|+
literal|1
operator|)
operator|%
literal|4
expr_stmt|;
block|}
else|else
block|{
name|found_eq
operator|=
literal|1
expr_stmt|;
name|lengthAtEOF
operator|=
name|token_length
expr_stmt|;
name|eof
argument_list|()
expr_stmt|;
name|token_length
operator|=
operator|(
name|lengthAtEOF
operator|+
literal|1
operator|)
operator|%
literal|4
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|t
operator|!=
name|SP
condition|)
block|{
name|token
index|[
name|token_length
operator|++
index|]
operator|=
name|t
expr_stmt|;
if|if
condition|(
name|token_length
operator|==
literal|4
condition|)
block|{
if|if
condition|(
name|found_eq
operator|==
literal|0
condition|)
block|{
name|decode_token
argument_list|()
expr_stmt|;
block|}
name|token_length
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|token_length
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Base64 input must be a multiple of four characters"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|eof
parameter_list|()
block|{
if|if
condition|(
name|token
operator|!=
literal|null
operator|&&
name|token_length
operator|!=
literal|0
condition|)
block|{
while|while
condition|(
name|token_length
operator|<
literal|4
condition|)
block|{
name|token
index|[
name|token_length
operator|++
index|]
operator|=
name|EOF
expr_stmt|;
block|}
name|decode_final_token
argument_list|()
expr_stmt|;
block|}
name|token_length
operator|=
literal|0
expr_stmt|;
name|token
operator|=
operator|new
name|byte
index|[
literal|4
index|]
expr_stmt|;
name|bytes
operator|=
operator|new
name|byte
index|[
literal|3
index|]
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|getByteArray
parameter_list|()
block|{
name|eof
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
