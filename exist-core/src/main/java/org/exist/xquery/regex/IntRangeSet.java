begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|regex
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
name|util
operator|.
name|Arrays
import|;
end_import

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

begin_comment
comment|/**  * Set of int values. This implementation of IntSet uses a sorted array  * of integer ranges.  *   * Copied from Saxon-HE 9.2 package net.sf.saxon.regex.  *  * @author Michael Kay  */
end_comment

begin_class
specifier|public
class|class
name|IntRangeSet
implements|implements
name|Serializable
block|{
comment|// The array of start points, which will always be sorted
specifier|private
name|int
index|[]
name|startPoints
decl_stmt|;
comment|// The array of end points, which will always be sorted
specifier|private
name|int
index|[]
name|endPoints
decl_stmt|;
comment|// The number of elements of the above two arrays that are actually in use
specifier|private
name|int
name|used
init|=
literal|0
decl_stmt|;
comment|// Hashcode, evaluated lazily
specifier|private
name|int
name|hashCode
init|=
operator|-
literal|1
decl_stmt|;
comment|// The number of items in the set
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
comment|/**      *  Create an empty set      */
specifier|public
name|IntRangeSet
parameter_list|()
block|{
name|startPoints
operator|=
operator|new
name|int
index|[
literal|4
index|]
expr_stmt|;
name|endPoints
operator|=
operator|new
name|int
index|[
literal|4
index|]
expr_stmt|;
name|used
operator|=
literal|0
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
name|hashCode
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**      * Create one IntRangeSet as a copy of another      * @param input the IntRangeSet to be copied      */
specifier|public
name|IntRangeSet
parameter_list|(
name|IntRangeSet
name|input
parameter_list|)
block|{
name|startPoints
operator|=
operator|new
name|int
index|[
name|input
operator|.
name|used
index|]
expr_stmt|;
name|endPoints
operator|=
operator|new
name|int
index|[
name|input
operator|.
name|used
index|]
expr_stmt|;
name|used
operator|=
name|input
operator|.
name|used
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|input
operator|.
name|startPoints
argument_list|,
literal|0
argument_list|,
name|startPoints
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|input
operator|.
name|endPoints
argument_list|,
literal|0
argument_list|,
name|endPoints
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|hashCode
operator|=
name|input
operator|.
name|hashCode
expr_stmt|;
block|}
comment|/**      * Create an IntRangeSet given the start points and end points of the integer ranges.      * The two arrays must be the same length; each must be in ascending order; and the n'th end point      * must be greater than the n'th start point, and less than the n+1'th start point, for all n.      * @param startPoints the start points of the integer ranges      * @param endPoints the end points of the integer ranges      * @throws IllegalArgumentException if the two arrays are different lengths. Other error conditions      * in the input are not currently detected.      */
specifier|public
name|IntRangeSet
parameter_list|(
name|int
index|[]
name|startPoints
parameter_list|,
name|int
index|[]
name|endPoints
parameter_list|)
block|{
if|if
condition|(
name|startPoints
operator|.
name|length
operator|!=
name|endPoints
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Array lengths differ"
argument_list|)
throw|;
block|}
name|this
operator|.
name|startPoints
operator|=
name|startPoints
expr_stmt|;
name|this
operator|.
name|endPoints
operator|=
name|endPoints
expr_stmt|;
name|used
operator|=
name|startPoints
operator|.
name|length
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
name|used
condition|;
name|i
operator|++
control|)
block|{
name|size
operator|+=
operator|(
name|endPoints
index|[
name|i
index|]
operator|-
name|startPoints
index|[
name|i
index|]
operator|+
literal|1
operator|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|startPoints
operator|=
operator|new
name|int
index|[
literal|4
index|]
expr_stmt|;
name|endPoints
operator|=
operator|new
name|int
index|[
literal|4
index|]
expr_stmt|;
name|used
operator|=
literal|0
expr_stmt|;
name|hashCode
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
operator|==
literal|0
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|value
operator|<
name|startPoints
index|[
literal|0
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|j
init|=
name|used
decl_stmt|;
do|do
block|{
specifier|final
name|int
name|mid
init|=
name|i
operator|+
operator|(
name|j
operator|-
name|i
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|endPoints
index|[
name|mid
index|]
operator|<
name|value
condition|)
block|{
name|i
operator|=
name|Math
operator|.
name|max
argument_list|(
name|mid
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|startPoints
index|[
name|mid
index|]
operator|>
name|value
condition|)
block|{
name|j
operator|=
name|Math
operator|.
name|min
argument_list|(
name|mid
argument_list|,
name|j
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
do|while
condition|(
name|i
operator|!=
name|j
condition|)
do|;
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|remove
parameter_list|(
name|int
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
comment|/**      * Add an integer to the set      * @param value the integer to be added      * @return true if the integer was added, false if it was already present      */
specifier|public
name|boolean
name|add
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|hashCode
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|used
operator|==
literal|0
condition|)
block|{
name|ensureCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|startPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|value
expr_stmt|;
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|value
expr_stmt|;
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|value
operator|>
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
condition|)
block|{
if|if
condition|(
name|value
operator|==
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|+
literal|1
condition|)
block|{
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|++
expr_stmt|;
block|}
else|else
block|{
name|ensureCapacity
argument_list|(
name|used
operator|+
literal|1
argument_list|)
expr_stmt|;
name|startPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|value
expr_stmt|;
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|value
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|value
operator|<
name|startPoints
index|[
literal|0
index|]
condition|)
block|{
if|if
condition|(
name|value
operator|==
name|startPoints
index|[
literal|0
index|]
operator|-
literal|1
condition|)
block|{
name|startPoints
index|[
literal|0
index|]
operator|--
expr_stmt|;
block|}
else|else
block|{
name|ensureCapacity
argument_list|(
name|used
operator|+
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|startPoints
argument_list|,
literal|0
argument_list|,
name|startPoints
argument_list|,
literal|1
argument_list|,
name|used
operator|-
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|endPoints
argument_list|,
literal|0
argument_list|,
name|endPoints
argument_list|,
literal|1
argument_list|,
name|used
operator|-
literal|1
argument_list|)
expr_stmt|;
name|startPoints
index|[
literal|0
index|]
operator|=
name|value
expr_stmt|;
name|endPoints
index|[
literal|0
index|]
operator|=
name|value
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|j
init|=
name|used
decl_stmt|;
do|do
block|{
specifier|final
name|int
name|mid
init|=
name|i
operator|+
operator|(
name|j
operator|-
name|i
operator|)
operator|/
literal|2
decl_stmt|;
if|if
condition|(
name|endPoints
index|[
name|mid
index|]
operator|<
name|value
condition|)
block|{
name|i
operator|=
name|Math
operator|.
name|max
argument_list|(
name|mid
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|startPoints
index|[
name|mid
index|]
operator|>
name|value
condition|)
block|{
name|j
operator|=
name|Math
operator|.
name|min
argument_list|(
name|mid
argument_list|,
name|j
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
comment|// value is already present
block|}
block|}
do|while
condition|(
name|i
operator|!=
name|j
condition|)
do|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|endPoints
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
operator|==
name|value
condition|)
block|{
name|i
operator|--
expr_stmt|;
block|}
if|else if
condition|(
name|i
operator|<
name|used
operator|-
literal|1
operator|&&
name|startPoints
index|[
name|i
operator|+
literal|1
index|]
operator|-
literal|1
operator|==
name|value
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|endPoints
index|[
name|i
index|]
operator|+
literal|1
operator|==
name|value
condition|)
block|{
if|if
condition|(
name|value
operator|==
name|startPoints
index|[
name|i
operator|+
literal|1
index|]
operator|-
literal|1
condition|)
block|{
comment|// merge the two ranges
name|endPoints
index|[
name|i
index|]
operator|=
name|endPoints
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|startPoints
argument_list|,
name|i
operator|+
literal|2
argument_list|,
name|startPoints
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|used
operator|-
name|i
operator|-
literal|2
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|endPoints
argument_list|,
name|i
operator|+
literal|2
argument_list|,
name|endPoints
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|used
operator|-
name|i
operator|-
literal|2
argument_list|)
expr_stmt|;
name|used
operator|--
expr_stmt|;
block|}
else|else
block|{
name|endPoints
index|[
name|i
index|]
operator|++
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|startPoints
index|[
name|i
index|]
operator|-
literal|1
operator|==
name|value
condition|)
block|{
if|if
condition|(
name|value
operator|==
name|endPoints
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
condition|)
block|{
comment|// merge the two ranges
name|endPoints
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|endPoints
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|startPoints
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|startPoints
argument_list|,
name|i
argument_list|,
name|used
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|endPoints
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|endPoints
argument_list|,
name|i
argument_list|,
name|used
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|used
operator|--
expr_stmt|;
block|}
else|else
block|{
name|startPoints
index|[
name|i
index|]
operator|--
expr_stmt|;
block|}
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|value
operator|>
name|endPoints
index|[
name|i
index|]
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|ensureCapacity
argument_list|(
name|used
operator|+
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|startPoints
argument_list|,
name|i
argument_list|,
name|startPoints
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|used
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|endPoints
argument_list|,
name|i
argument_list|,
name|endPoints
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|used
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|err
parameter_list|)
block|{
name|err
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|startPoints
index|[
name|i
index|]
operator|=
name|value
expr_stmt|;
name|endPoints
index|[
name|i
index|]
operator|=
name|value
expr_stmt|;
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|(
name|int
name|n
parameter_list|)
block|{
if|if
condition|(
name|startPoints
operator|.
name|length
operator|<
name|n
condition|)
block|{
name|int
index|[]
name|s
init|=
operator|new
name|int
index|[
name|startPoints
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|int
index|[]
name|e
init|=
operator|new
name|int
index|[
name|startPoints
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|startPoints
argument_list|,
literal|0
argument_list|,
name|s
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|endPoints
argument_list|,
literal|0
argument_list|,
name|e
argument_list|,
literal|0
argument_list|,
name|used
argument_list|)
expr_stmt|;
name|startPoints
operator|=
name|s
expr_stmt|;
name|endPoints
operator|=
name|e
expr_stmt|;
block|}
name|used
operator|=
name|n
expr_stmt|;
block|}
comment|/**      * Get an iterator over the values      *      * @return value iterator      */
specifier|public
name|IntRangeSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|IntRangeSetIterator
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|FastStringBuffer
name|sb
init|=
operator|new
name|FastStringBuffer
argument_list|(
name|used
operator|*
literal|8
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
name|used
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|startPoints
index|[
name|i
index|]
operator|+
literal|"-"
operator|+
name|endPoints
index|[
name|i
index|]
operator|+
literal|","
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Test whether this set has exactly the same members as another set. Note that      * IntRangeSet values are<b>NOT</b> comparable with other implementations of IntSet      *      * @param other object to compare with      * @return true if other is an IntRangeSet and has the same members      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|IntRangeSet
condition|)
block|{
return|return
name|used
operator|==
operator|(
operator|(
name|IntRangeSet
operator|)
name|other
operator|)
operator|.
name|used
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|startPoints
argument_list|,
operator|(
operator|(
name|IntRangeSet
operator|)
name|other
operator|)
operator|.
name|startPoints
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|endPoints
argument_list|,
operator|(
operator|(
name|IntRangeSet
operator|)
name|other
operator|)
operator|.
name|endPoints
argument_list|)
return|;
block|}
return|return
name|containsAll
argument_list|(
operator|(
name|IntRangeSet
operator|)
name|other
argument_list|)
return|;
block|}
comment|/**      * Construct a hash key that supports the equals() test      *      * @return hash key      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Note, hashcodes are NOT the same as those used by IntHashSet and IntArraySet
if|if
condition|(
name|hashCode
operator|==
operator|-
literal|1
condition|)
block|{
name|int
name|h
init|=
literal|0x836a89f1
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
name|used
condition|;
name|i
operator|++
control|)
block|{
name|h
operator|^=
name|startPoints
index|[
name|i
index|]
operator|+
operator|(
name|endPoints
index|[
name|i
index|]
operator|<<
literal|3
operator|)
expr_stmt|;
block|}
name|hashCode
operator|=
name|h
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
comment|/**      * Test if this set is a superset of another set      *      * @param other the subset      * @return true if this is a superset of other      */
specifier|public
name|boolean
name|containsAll
parameter_list|(
name|IntRangeSet
name|other
parameter_list|)
block|{
specifier|final
name|IntRangeSetIterator
name|it
init|=
name|other
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|contains
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Add a range of integers to the set.      * This is optimized for the case where these are all greater than any existing integer      * in the set.      * @param low the low end of the new range      * @param high the high end of the new range      */
specifier|public
name|void
name|addRange
parameter_list|(
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
name|hashCode
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|used
operator|==
literal|0
condition|)
block|{
name|ensureCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|startPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|low
expr_stmt|;
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|high
expr_stmt|;
name|size
operator|+=
operator|(
name|high
operator|-
name|low
operator|+
literal|1
operator|)
expr_stmt|;
block|}
if|else if
condition|(
name|low
operator|>
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
condition|)
block|{
if|if
condition|(
name|low
operator|==
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|+
literal|1
condition|)
block|{
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|high
expr_stmt|;
block|}
else|else
block|{
name|ensureCapacity
argument_list|(
name|used
operator|+
literal|1
argument_list|)
expr_stmt|;
name|startPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|low
expr_stmt|;
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
operator|=
name|high
expr_stmt|;
block|}
name|size
operator|+=
operator|(
name|high
operator|-
name|low
operator|+
literal|1
operator|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
name|low
init|;
name|i
operator|<=
name|high
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Get the start points of the ranges      *      * @return array of start points      */
specifier|public
name|int
index|[]
name|getStartPoints
parameter_list|()
block|{
return|return
name|startPoints
return|;
block|}
comment|/**      * Get the end points of the ranges      *      * @return array of end points      */
specifier|public
name|int
index|[]
name|getEndPoints
parameter_list|()
block|{
return|return
name|endPoints
return|;
block|}
comment|/**      * Get the number of ranges actually in use      *      * @return number of ranges in use      */
specifier|public
name|int
name|getNumberOfRanges
parameter_list|()
block|{
return|return
name|used
return|;
block|}
comment|/**      * Iterator class      */
specifier|private
class|class
name|IntRangeSetIterator
implements|implements
name|Serializable
block|{
specifier|private
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|current
init|=
literal|0
decl_stmt|;
specifier|public
name|IntRangeSetIterator
parameter_list|()
block|{
name|i
operator|=
operator|-
literal|1
expr_stmt|;
name|current
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
return|return
name|size
operator|>
literal|0
return|;
block|}
else|else
block|{
return|return
name|current
operator|<
name|endPoints
index|[
name|used
operator|-
literal|1
index|]
return|;
block|}
block|}
specifier|public
name|int
name|next
parameter_list|()
block|{
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|i
operator|=
literal|0
expr_stmt|;
name|current
operator|=
name|startPoints
index|[
literal|0
index|]
expr_stmt|;
return|return
name|current
return|;
block|}
if|if
condition|(
name|current
operator|==
name|endPoints
index|[
name|i
index|]
condition|)
block|{
name|current
operator|=
name|startPoints
index|[
operator|++
name|i
index|]
expr_stmt|;
return|return
name|current
return|;
block|}
else|else
block|{
return|return
operator|++
name|current
return|;
block|}
block|}
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
comment|// The Initial Developer of the Original Code is Michael Kay.
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
comment|// Contributor(s): none.
end_comment

end_unit

