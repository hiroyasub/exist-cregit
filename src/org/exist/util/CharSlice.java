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
comment|/**  * This is an implementation of the JDK 1.4 CharSequence interface: it implements  * a CharSequence as a view of an array. The implementation relies on the array  * being immutable: as a minimum, the caller is required to ensure that the array  * contents will not change so long as the CharSlice remains in existence.  *  * This class should be more efficient than String because it avoids copying the  * characters unnecessarily.  *  * The methods in the class don't check their arguments. Incorrect arguments will  * generally result in exceptions from lower-level classes.  *  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CharSlice
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
literal|2668084569793755681L
decl_stmt|;
specifier|private
name|char
index|[]
name|array
decl_stmt|;
specifier|private
name|int
name|offset
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|public
name|CharSlice
parameter_list|(
name|char
index|[]
name|array
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|array
operator|.
name|length
expr_stmt|;
block|}
specifier|public
name|CharSlice
parameter_list|(
name|char
index|[]
name|array
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|length
expr_stmt|;
if|if
condition|(
name|start
operator|+
name|length
operator|>
name|array
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"start("
operator|+
name|start
operator|+
literal|") + length("
operator|+
name|length
operator|+
literal|")> size("
operator|+
name|array
operator|.
name|length
operator|+
literal|')'
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the length of this character sequence.  The length is the number      * of 16-bit Unicode characters in the sequence.</p>      *      * @return  the number of characters in this sequence      */
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**      * Set the length of this character sequence, without changing the array and start offset      * to which it is bound      */
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|count
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * Returns the character at the specified index.  An index ranges from zero      * to<tt>length() - 1</tt>.  The first character of the sequence is at      * index zero, the next at index one, and so on, as for array      * indexing.</p>      *      * @param   index   the index of the character to be returned      *      * @return  the specified character      *      * @throws  java.lang.IndexOutOfBoundsException      *          if the<tt>index</tt> argument is negative or not less than      *<tt>length()</tt>      */
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|array
index|[
name|offset
operator|+
name|index
index|]
return|;
block|}
comment|/**      * Returns a new character sequence that is a subsequence of this sequence.      * The subsequence starts with the character at the specified index and      * ends with the character at index<tt>end - 1</tt>.  The length of the      * returned sequence is<tt>end - start</tt>, so if<tt>start == end</tt>      * then an empty sequence is returned.</p>      *      * @param   start   the start index, inclusive      * @param   end     the end index, exclusive      *      * @return  the specified subsequence      *      * @throws  java.lang.IndexOutOfBoundsException      *          if<tt>start</tt> or<tt>end</tt> are negative,      *          if<tt>end</tt> is greater than<tt>length()</tt>,      *          or if<tt>start</tt> is greater than<tt>end</tt>      */
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
name|offset
operator|+
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
comment|/**      * Convert to a string      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|array
argument_list|,
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**      * Compare equality      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/**      * Generate a hash code      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Same algorithm as String#hashCode(), but not cached
name|int
name|end
init|=
name|offset
operator|+
name|count
decl_stmt|;
name|int
name|h
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|array
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
comment|/**      * Get the index of a specific character in the sequence. Returns -1 if not found.      * This method mimics {@link String#indexOf}      * @param c the character to be found      * @return the position of the first occurrence of that character, or -1 if not found.      */
specifier|public
name|int
name|indexOf
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|int
name|end
init|=
name|offset
operator|+
name|count
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
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
name|c
condition|)
block|{
return|return
name|i
operator|-
name|offset
return|;
block|}
empty_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * Returns a new character sequence that is a subsequence of this sequence.      * Unlike subSequence, this is guaranteed to return a String.      */
specifier|public
name|String
name|substring
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
name|String
argument_list|(
name|array
argument_list|,
name|offset
operator|+
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
comment|/**      * Append the contents to another array at a given offset. The caller is responsible      * for ensuring that sufficient space is available.      * @param destination the array to which the characters will be copied      * @param destOffset the offset in the target array where the copy will start      */
specifier|public
name|void
name|copyTo
parameter_list|(
name|char
index|[]
name|destination
parameter_list|,
name|int
name|destOffset
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
name|offset
argument_list|,
name|destination
argument_list|,
name|destOffset
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**      * Write the value to a writer      */
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
name|writer
operator|.
name|write
argument_list|(
name|array
argument_list|,
name|offset
argument_list|,
name|count
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

