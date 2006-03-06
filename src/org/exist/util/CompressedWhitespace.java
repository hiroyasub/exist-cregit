begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|FastStringBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_comment
comment|/**  * This class provides a compressed representation of a sequence of whitespace characters. The representation  * is a sequence of bytes: in each byte the top two bits indicate which whitespace character is used  * (x9, xA, xD, or x20) and the bottom six bits indicate the number of such characters. A zero byte is a filler.  * We don't compress the sequence if it would occupy more than 8 bytes, because that's the space we've got available  * in the TinyTree arrays.  */
end_comment

begin_class
specifier|public
class|class
name|CompressedWhitespace
implements|implements
name|CharSequence
block|{
specifier|private
specifier|static
name|char
index|[]
name|WHITE_CHARS
init|=
block|{
literal|0x09
block|,
literal|0x0A
block|,
literal|0x0D
block|,
literal|0x20
block|}
decl_stmt|;
specifier|private
name|long
name|value
decl_stmt|;
specifier|public
name|CompressedWhitespace
parameter_list|(
name|long
name|compressedValue
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|compressedValue
expr_stmt|;
block|}
comment|/**      * Attempt to compress a CharSequence      * @param in the CharSequence to be compressed      * @return the compressed sequence if it can be compressed; or the original CharSequence otherwise      */
specifier|public
specifier|static
name|CharSequence
name|compress
parameter_list|(
name|CharSequence
name|in
parameter_list|)
block|{
specifier|final
name|int
name|inlen
init|=
name|in
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|inlen
operator|==
literal|0
condition|)
block|{
return|return
name|in
return|;
block|}
name|int
name|runlength
init|=
literal|1
decl_stmt|;
name|int
name|outlength
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
name|inlen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|in
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
literal|"\t\n\r "
operator|)
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|inlen
operator|-
literal|1
operator|||
name|c
operator|!=
name|in
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|||
name|runlength
operator|==
literal|63
condition|)
block|{
name|runlength
operator|=
literal|1
expr_stmt|;
name|outlength
operator|++
expr_stmt|;
if|if
condition|(
name|outlength
operator|>
literal|8
condition|)
block|{
return|return
name|in
return|;
block|}
block|}
else|else
block|{
name|runlength
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
name|in
return|;
block|}
block|}
name|int
name|ix
init|=
literal|0
decl_stmt|;
name|runlength
operator|=
literal|1
expr_stmt|;
name|int
index|[]
name|out
init|=
operator|new
name|int
index|[
name|outlength
index|]
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
name|inlen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|in
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|inlen
operator|-
literal|1
operator|||
name|c
operator|!=
name|in
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|||
name|runlength
operator|==
literal|63
condition|)
block|{
name|int
name|code
init|=
operator|(
literal|"\t\n\r "
operator|)
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|out
index|[
name|ix
operator|++
index|]
operator|=
operator|(
name|code
operator|<<
literal|6
operator|)
operator||
name|runlength
expr_stmt|;
name|runlength
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|runlength
operator|++
expr_stmt|;
block|}
block|}
name|long
name|value
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
name|outlength
condition|;
name|i
operator|++
control|)
block|{
name|value
operator|=
operator|(
name|value
operator|<<
literal|8
operator|)
operator||
name|out
index|[
name|i
index|]
expr_stmt|;
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
operator|(
literal|8
operator|-
name|outlength
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|value
operator|=
operator|(
name|value
operator|<<
literal|8
operator|)
expr_stmt|;
block|}
return|return
operator|new
name|CompressedWhitespace
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**      * Uncompress the whitespace to a FastStringBuffer      * @param buffer the buffer to which the whitespace is to be appended. The parameter may be      * null, in which case a new buffer is created.      * @return the FastStringBuffer to which the whitespace has been appended. If a buffer was      * supplied in the argument, this will be the same buffer.      */
specifier|public
name|FastStringBuffer
name|uncompress
parameter_list|(
name|FastStringBuffer
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|FastStringBuffer
argument_list|(
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|val
init|=
name|value
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|56
init|;
name|s
operator|>=
literal|0
condition|;
name|s
operator|-=
literal|8
control|)
block|{
name|byte
name|b
init|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|val
operator|>>>
name|s
operator|)
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|char
name|c
init|=
name|WHITE_CHARS
index|[
name|b
operator|>>>
literal|6
operator|&
literal|0x3
index|]
decl_stmt|;
name|int
name|len
init|=
operator|(
name|b
operator|&
literal|0x3f
operator|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
return|;
block|}
specifier|public
name|long
name|getCompressedValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|int
name|length
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|long
name|val
init|=
name|value
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|56
init|;
name|s
operator|>=
literal|0
condition|;
name|s
operator|-=
literal|8
control|)
block|{
name|int
name|c
init|=
operator|(
name|int
operator|)
operator|(
operator|(
name|val
operator|>>>
name|s
operator|)
operator|&
literal|0x3f
operator|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|count
operator|+=
name|c
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**      * Returns the<code>char</code> value at the specified index.  An index ranges from zero      * to<tt>length() - 1</tt>.  The first<code>char</code> value of the sequence is at      * index zero, the next at index one, and so on, as for array      * indexing.</p>      *<p/>      *<p>If the<code>char</code> value specified by the index is a      *<a href="Character.html#unicode">surrogate</a>, the surrogate      * value is returned.      *      * @param index the index of the<code>char</code> value to be returned      * @return the specified<code>char</code> value      * @throws IndexOutOfBoundsException if the<tt>index</tt> argument is negative or not less than      *<tt>length()</tt>      */
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|long
name|val
init|=
name|value
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|56
init|;
name|s
operator|>=
literal|0
condition|;
name|s
operator|-=
literal|8
control|)
block|{
name|byte
name|b
init|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|val
operator|>>>
name|s
operator|)
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|count
operator|+=
operator|(
name|b
operator|&
literal|0x3f
operator|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
name|index
condition|)
block|{
return|return
name|WHITE_CHARS
index|[
name|b
operator|>>>
literal|6
operator|&
literal|0x3
index|]
return|;
block|}
block|}
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
name|index
operator|+
literal|""
argument_list|)
throw|;
block|}
comment|/**      * Returns a new<code>CharSequence</code> that is a subsequence of this sequence.      * The subsequence starts with the<code>char</code> value at the specified index and      * ends with the<code>char</code> value at index<tt>end - 1</tt>.  The length      * (in<code>char</code>s) of the      * returned sequence is<tt>end - start</tt>, so if<tt>start == end</tt>      * then an empty sequence is returned.</p>      *      * @param start the start index, inclusive      * @param end   the end index, exclusive      * @return the specified subsequence      * @throws IndexOutOfBoundsException if<tt>start</tt> or<tt>end</tt> are negative,      *                                   if<tt>end</tt> is greater than<tt>length()</tt>,      *                                   or if<tt>start</tt> is greater than<tt>end</tt>      */
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
name|uncompress
argument_list|(
literal|null
argument_list|)
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
comment|/**      * Indicates whether some other object is "equal to" this one.      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|CompressedWhitespace
condition|)
block|{
return|return
name|value
operator|==
operator|(
operator|(
name|CompressedWhitespace
operator|)
name|obj
operator|)
operator|.
name|value
return|;
block|}
return|return
name|uncompress
argument_list|(
literal|null
argument_list|)
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
comment|/**      * Returns a hash code value for the object.      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|uncompress
argument_list|(
literal|null
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Returns a string representation of the object.      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|uncompress
argument_list|(
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Write the value to a Writer      */
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
specifier|final
name|long
name|val
init|=
name|value
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|56
init|;
name|s
operator|>=
literal|0
condition|;
name|s
operator|-=
literal|8
control|)
block|{
specifier|final
name|byte
name|b
init|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|val
operator|>>>
name|s
operator|)
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
break|break;
block|}
specifier|final
name|char
name|c
init|=
name|WHITE_CHARS
index|[
name|b
operator|>>>
literal|6
operator|&
literal|0x3
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
operator|(
name|b
operator|&
literal|0x3f
operator|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Write the value to a Writer with escaping of special characters      */
specifier|public
name|void
name|writeEscape
parameter_list|(
name|boolean
index|[]
name|specialChars
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
specifier|final
name|long
name|val
init|=
name|value
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|56
init|;
name|s
operator|>=
literal|0
condition|;
name|s
operator|-=
literal|8
control|)
block|{
specifier|final
name|byte
name|b
init|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|val
operator|>>>
name|s
operator|)
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
break|break;
block|}
specifier|final
name|char
name|c
init|=
name|WHITE_CHARS
index|[
name|b
operator|>>>
literal|6
operator|&
literal|0x3
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
operator|(
name|b
operator|&
literal|0x3f
operator|)
decl_stmt|;
if|if
condition|(
name|specialChars
index|[
name|c
index|]
condition|)
block|{
name|String
name|e
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\n'
condition|)
block|{
name|e
operator|=
literal|"&#xA;"
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|==
literal|'\r'
condition|)
block|{
name|e
operator|=
literal|"&#xD;"
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|==
literal|'\t'
condition|)
block|{
name|e
operator|=
literal|"&#x9;"
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|CharSequence
name|c
init|=
name|compress
argument_list|(
literal|"\t\n\n\t\t\n      "
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|//
end_comment

begin_comment
comment|// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License. You may obtain a copy of the
end_comment

begin_comment
comment|// License at http://www.mozilla.org/MPL/
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Software distributed under the License is distributed on an "AS IS" basis,
end_comment

begin_comment
comment|// WITHOUT WARRANTY OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing rights and limitations under the License.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The Original Code is: all this file.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The Initial Developer of the Original Code is Michael H. Kay
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Contributor(s): none
end_comment

begin_comment
comment|//
end_comment

end_unit

