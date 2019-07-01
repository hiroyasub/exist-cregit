begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2003-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  * This class is to large extents copied from Saxon 2003-01-21 (version ?).  * See comment at the back about licensing for those parts.  *   *  $Id$  */
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
name|Serializable
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
comment|/**  * A simple implementation of a class similar to StringBuffer. Unlike  * StringBuffer it is not synchronized. It also offers the capability  * to remove unused space. (This class could possibly be replaced by  * StringBuilder in JDK 1.5, but using our own class gives more control.)  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FastStringBuffer
implements|implements
name|CharSequence
implements|,
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|504264698052799896L
decl_stmt|;
specifier|private
name|char
index|[]
name|array
decl_stmt|;
specifier|private
name|int
name|used
init|=
literal|0
decl_stmt|;
specifier|public
name|FastStringBuffer
parameter_list|(
name|int
name|initialSize
parameter_list|)
block|{
name|array
operator|=
operator|new
name|char
index|[
name|initialSize
index|]
expr_stmt|;
block|}
comment|/**      * Append the contents of a String to the buffer      * @param s the String to be appended      */
specifier|public
name|void
name|append
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|ensureCapacity
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|s
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|used
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * Append the contents of a CharSlice to the buffer      * @param s the String to be appended      */
specifier|public
name|void
name|append
parameter_list|(
name|CharSlice
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|ensureCapacity
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|s
operator|.
name|copyTo
argument_list|(
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|used
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * Append the contents of a FastStringBuffer to the buffer      * @param s the FastStringBuffer to be appended      */
specifier|public
name|void
name|append
parameter_list|(
name|FastStringBuffer
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|ensureCapacity
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|s
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|used
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * Append the contents of a StringBuffer to the buffer      * @param s the StringBuffer to be appended      */
specifier|public
name|void
name|append
parameter_list|(
name|StringBuffer
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|ensureCapacity
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|s
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|used
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * Append the contents of a general CharSequence to the buffer      * @param s the CharSequence to be appended      */
specifier|public
name|void
name|append
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
comment|// Although we provide variants of this method for different subtypes, Java decides which to use based
comment|// on the static type of the operand. We want to use the right method based on the dynamic type, to avoid
comment|// creating objects and copying strings unnecessarily. So we do a dynamic dispatch.
specifier|final
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|ensureCapacity
argument_list|(
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|instanceof
name|CharSlice
condition|)
block|{
operator|(
operator|(
name|CharSlice
operator|)
name|s
operator|)
operator|.
name|copyTo
argument_list|(
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|instanceof
name|String
condition|)
block|{
operator|(
operator|(
name|String
operator|)
name|s
operator|)
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|instanceof
name|FastStringBuffer
condition|)
block|{
operator|(
operator|(
name|FastStringBuffer
operator|)
name|s
operator|)
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|instanceof
name|CompressedWhitespace
condition|)
block|{
operator|(
operator|(
name|CompressedWhitespace
operator|)
name|s
operator|)
operator|.
name|uncompress
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
name|s
operator|.
name|toString
argument_list|()
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|array
argument_list|,
name|used
argument_list|)
expr_stmt|;
block|}
name|used
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * Append the contents of a character array to the buffer      * @param srcArray the array whose contents are to be added      * @param start the offset of the first character in the array to be copied      * @param length the number of characters to be copied      */
specifier|public
name|void
name|append
parameter_list|(
name|char
index|[]
name|srcArray
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|ensureCapacity
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|srcArray
argument_list|,
name|start
argument_list|,
name|array
argument_list|,
name|used
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|used
operator|+=
name|length
expr_stmt|;
block|}
comment|/**      * Append the entire contents of a character array to the buffer      * @param srcArray the array whose contents are to be added      */
specifier|public
name|void
name|append
parameter_list|(
name|char
index|[]
name|srcArray
parameter_list|)
block|{
specifier|final
name|int
name|length
init|=
name|srcArray
operator|.
name|length
decl_stmt|;
name|ensureCapacity
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|srcArray
argument_list|,
literal|0
argument_list|,
name|array
argument_list|,
name|used
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|used
operator|+=
name|length
expr_stmt|;
block|}
comment|/**      * Append a character to the buffer      * @param ch the character to be added      */
specifier|public
name|void
name|append
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
name|ensureCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|array
index|[
name|used
operator|++
index|]
operator|=
name|ch
expr_stmt|;
block|}
comment|/**      * Append a wide character to the buffer (as a surrogate pair if necessary).      *      * @param ch the character      */
specifier|public
name|void
name|appendWideChar
parameter_list|(
specifier|final
name|int
name|ch
parameter_list|)
block|{
if|if
condition|(
name|ch
operator|>
literal|0xffff
condition|)
block|{
name|append
argument_list|(
name|XMLChar
operator|.
name|highSurrogate
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|XMLChar
operator|.
name|lowSurrogate
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Prepend a wide character to the buffer (as a surrogate pair if necessary).      *      * @param ch the character      */
specifier|public
name|void
name|prependWideChar
parameter_list|(
name|int
name|ch
parameter_list|)
block|{
if|if
condition|(
name|ch
operator|>
literal|0xffff
condition|)
block|{
name|insertCharAt
argument_list|(
literal|0
argument_list|,
name|XMLChar
operator|.
name|lowSurrogate
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
name|insertCharAt
argument_list|(
literal|0
argument_list|,
name|XMLChar
operator|.
name|highSurrogate
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|insertCharAt
argument_list|(
literal|0
argument_list|,
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the length of this character sequence.  The length is the number      * of 16-bit<code>char</code>s in the sequence.      *      * @return the number of<code>char</code>s in this sequence      */
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|used
return|;
block|}
comment|/**      * Returns the<code>char</code> value at the specified index.  An index ranges from zero      * to<code>length() - 1</code>.  The first<code>char</code> value of the sequence is at      * index zero, the next at index one, and so on, as for array      * indexing.      *      * If the<code>char</code> value specified by the index is a      *<a href="Character.html#unicode">surrogate</a>, the surrogate      * value is returned.      *      * @param index the index of the<code>char</code> value to be returned      * @return the specified<code>char</code> value      * @throws IndexOutOfBoundsException if the<code>index</code> argument is negative or not less than      *<code>length()</code>      */
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|>=
name|used
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
return|return
name|array
index|[
name|index
index|]
return|;
block|}
comment|/**      * Returns a new<code>CharSequence</code> that is a subsequence of this sequence.      * The subsequence starts with the<code>char</code> value at the specified index and      * ends with the<code>char</code> value at index<code>end - 1</code>.  The length      * (in<code>char</code>s) of the      * returned sequence is<code>end - start</code>, so if<code>start == end</code>      * then an empty sequence is returned.      *      * @param start the start index, inclusive      * @param end   the end index, exclusive      * @return the specified subsequence      * @throws IndexOutOfBoundsException if<code>start</code> or<code>end</code> are negative,      *                                   if<code>end</code> is greater than<code>length()</code>,      *                                   or if<code>start</code> is greater than<code>end</code>      */
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
operator|new
name|CharSlice
argument_list|(
name|array
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
comment|/**      * Copies characters from this FastStringBuffer into the destination character      * array.      *      * The first character to be copied is at index<code>srcBegin</code>;      * the last character to be copied is at index<code>srcEnd-1</code>      * (thus the total number of characters to be copied is      *<code>srcEnd-srcBegin</code>). The characters are copied into the      * subarray of<code>dst</code> starting at index<code>dstBegin</code>      * and ending at index:      *<blockquote><pre>      *     dstbegin + (srcEnd-srcBegin) - 1      *</pre></blockquote>      *      * @param      srcBegin   index of the first character in the string      *                        to copy.      * @param      srcEnd     index after the last character in the string      *                        to copy.      * @param      dst        the destination array.      * @param      dstBegin   the start offset in the destination array.      * @exception IndexOutOfBoundsException If any of the following      *            is true:      *<ul><li><code>srcBegin</code> is negative.      *<li><code>srcBegin</code> is greater than<code>srcEnd</code>      *<li><code>srcEnd</code> is greater than the length of this      *                string      *<li><code>dstBegin</code> is negative      *<li><code>dstBegin+(srcEnd-srcBegin)</code> is larger than      *<code>dst.length</code></ul>      */
specifier|public
name|void
name|getChars
parameter_list|(
name|int
name|srcBegin
parameter_list|,
name|int
name|srcEnd
parameter_list|,
name|char
name|dst
index|[]
parameter_list|,
name|int
name|dstBegin
parameter_list|)
block|{
if|if
condition|(
name|srcBegin
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|(
name|srcBegin
argument_list|)
throw|;
block|}
if|if
condition|(
name|srcEnd
operator|>
name|used
condition|)
block|{
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|(
name|srcEnd
argument_list|)
throw|;
block|}
if|if
condition|(
name|srcBegin
operator|>
name|srcEnd
condition|)
block|{
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|(
name|srcEnd
operator|-
name|srcBegin
argument_list|)
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
name|srcBegin
argument_list|,
name|dst
argument_list|,
name|dstBegin
argument_list|,
name|srcEnd
operator|-
name|srcBegin
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the index of the first character equal to a given value      * @param ch the character to search for      * @return the position of the first occurrence, or -1 if not found      */
specifier|public
name|int
name|indexOf
parameter_list|(
name|char
name|ch
parameter_list|)
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
name|used
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|array
index|[
name|i
index|]
operator|==
name|ch
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * Convert contents of the FastStringBuffer to a string      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|condense
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
return|;
block|}
comment|/**      * Set the character at a particular offset      * @param index the index of the character to be set      * @param ch the new character to overwrite the existing character at that location      * @throws IndexOutOfBoundsException if {@code int< 0 || int>= length()}      */
specifier|public
name|void
name|setCharAt
parameter_list|(
name|int
name|index
parameter_list|,
name|char
name|ch
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>
name|used
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
name|array
index|[
name|index
index|]
operator|=
name|ch
expr_stmt|;
block|}
comment|/**      * Insert a character at a particular offset      * @param index the index of the character to be set      * @param ch the new character to insert at that location      * @throws IndexOutOfBoundsException if {@code int< 0 || int>= length()}      */
specifier|public
name|void
name|insertCharAt
parameter_list|(
name|int
name|index
parameter_list|,
name|char
name|ch
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>
name|used
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
name|ensureCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|used
init|;
name|i
operator|>
name|index
condition|;
name|i
operator|--
control|)
block|{
name|array
index|[
name|i
index|]
operator|=
name|array
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|used
operator|++
expr_stmt|;
name|array
index|[
name|index
index|]
operator|=
name|ch
expr_stmt|;
block|}
comment|/**      * Remove a character at a particular offset      * @param index the index of the character to be set      * @throws IndexOutOfBoundsException if {@code int< 0 || int>= length()}      */
specifier|public
name|void
name|removeCharAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>
name|used
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
name|used
operator|--
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
init|;
name|i
operator|<
name|used
condition|;
name|i
operator|++
control|)
block|{
name|array
index|[
name|i
index|]
operator|=
name|array
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
block|}
block|}
comment|/**      * Set the length. If this exceeds the current length, this method is a no-op.      * If this is less than the current length, characters beyond the specified point      * are deleted.      *      * @param length the new length      */
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|<
literal|0
operator|||
name|length
operator|>
name|used
condition|)
block|{
return|return;
block|}
name|used
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * Expand the character array if necessary to ensure capacity for appended data      *      * @param extra the extra capacity needed.      */
specifier|public
name|void
name|ensureCapacity
parameter_list|(
name|int
name|extra
parameter_list|)
block|{
if|if
condition|(
name|used
operator|+
name|extra
operator|>
name|array
operator|.
name|length
condition|)
block|{
name|int
name|newlen
init|=
name|array
operator|.
name|length
operator|*
literal|2
decl_stmt|;
if|if
condition|(
name|newlen
operator|<
name|used
operator|+
name|extra
condition|)
block|{
name|newlen
operator|=
name|used
operator|+
name|extra
operator|*
literal|2
expr_stmt|;
block|}
name|char
index|[]
name|array2
init|=
operator|new
name|char
index|[
name|newlen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|array2
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|array
operator|=
name|array2
expr_stmt|;
block|}
block|}
comment|/**      * Remove surplus space from the array. This doesn't reduce the array to the minimum      * possible size; it only reclaims space if it seems worth doing. Specifically, it      * contracts the array if the amount of wasted space is more than 256 characters, or      * more than half the allocated size.      *      * @return the character sequence.      */
specifier|public
name|CharSequence
name|condense
parameter_list|()
block|{
if|if
condition|(
name|array
operator|.
name|length
operator|-
name|used
operator|>
literal|256
operator|||
name|array
operator|.
name|length
operator|>
name|used
operator|*
literal|2
condition|)
block|{
name|char
index|[]
name|array2
init|=
operator|new
name|char
index|[
name|used
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|array2
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|array
operator|=
name|array2
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Write the value to a writer.      *      * @param writer the writer      *      * @throws java.io.IOException if an error occurs whilst writing      */
specifier|public
name|void
name|write
parameter_list|(
specifier|final
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
name|writer
operator|.
name|write
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
block|}
comment|/**      * Diagnostic print of the contents of a CharSequence.      *      * @param in the character sequence      *      * @return the diagnostic print      */
specifier|public
specifier|static
name|String
name|diagnosticPrint
parameter_list|(
name|CharSequence
name|in
parameter_list|)
block|{
specifier|final
name|FastStringBuffer
name|buff
init|=
operator|new
name|FastStringBuffer
argument_list|(
name|in
operator|.
name|length
argument_list|()
operator|*
literal|2
argument_list|)
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
name|in
operator|.
name|length
argument_list|()
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
name|c
operator|>
literal|32
operator|&&
name|c
operator|<
literal|127
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"\\u"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|12
init|;
name|d
operator|>=
literal|0
condition|;
name|d
operator|-=
literal|4
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"0123456789abcdef"
operator|.
name|charAt
argument_list|(
operator|(
name|c
operator|>>
name|d
operator|)
operator|&
literal|0xf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//Quick copies from old eXist's FastStringBuffer
comment|/**      *  Manefest constant: Suppress leading whitespace. This should be used when      *  normalize-to-SAX is called for the first chunk of a multi-chunk output,      *  or one following unsuppressed whitespace in a previous chunk.      *      *  see    #sendNormalizedSAXcharacters(char[],int,int,org.xml.sax.ContentHandler,int)      */
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_LEADING_WS
init|=
literal|0x01
decl_stmt|;
comment|/**      *  Manefest constant: Suppress trailing whitespace. This should be used      *  when normalize-to-SAX is called for the last chunk of a multi-chunk      *  output; it may have to be or'ed with SUPPRESS_LEADING_WS.      */
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_TRAILING_WS
init|=
literal|0x02
decl_stmt|;
comment|/**      *  Manefest constant: Suppress both leading and trailing whitespace. This      *  should be used when normalize-to-SAX is called for a complete string.      *  (I'm not wild about the name of this one. Ideas welcome.)      *      * see    sendNormalizedSAXcharacters(char[],int,int,org.xml.sax.ContentHandler,int)      */
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_BOTH
init|=
name|SUPPRESS_LEADING_WS
operator||
name|SUPPRESS_TRAILING_WS
decl_stmt|;
comment|/**      *  Gets the normalizedString attribute of the FastStringBuffer object      *      *@param  mode  Description of the Parameter      *@return       The normalizedString value      */
specifier|public
name|String
name|getNormalizedString
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
return|return
name|getNormalizedString
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|toString
argument_list|()
argument_list|)
argument_list|,
name|mode
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  Gets the normalizedString attribute of the FastStringBuffer object      *      *@param  sb    Description of the Parameter      *@param  mode  Description of the Parameter      *@return       The normalizedString value      */
specifier|public
name|StringBuffer
name|getNormalizedString
parameter_list|(
name|StringBuffer
name|sb
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
comment|//TODO : switch (mode)
return|return
operator|new
name|StringBuffer
argument_list|(
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
return|;
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

