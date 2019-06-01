begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright 2004 The eXist Team  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id$  */
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
comment|/**  * Utility methods to have indexed access for insertion and deletion  * of array items.  *   * Based on original code from dbXML.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayUtils
block|{
comment|/**      * Delete an integer.      *       * @param vals array of integers      * @param idx index of integer to delete      * @return the array without the deleted integer      */
specifier|public
specifier|static
name|int
index|[]
name|deleteArrayInt
parameter_list|(
name|int
index|[]
name|vals
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|newVals
init|=
operator|new
name|int
index|[
name|vals
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newVals
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idx
operator|<
name|newVals
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|newVals
argument_list|,
name|idx
argument_list|,
name|newVals
operator|.
name|length
operator|-
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|newVals
return|;
block|}
comment|/**      * Delete a long.      *       * @param vals array of longs      * @param idx index of long to delete      * @return the array without the deleted long      */
specifier|public
specifier|static
name|long
index|[]
name|deleteArrayLong
parameter_list|(
name|long
index|[]
name|vals
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|newVals
init|=
operator|new
name|long
index|[
name|vals
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newVals
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idx
operator|<
name|newVals
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|newVals
argument_list|,
name|idx
argument_list|,
name|newVals
operator|.
name|length
operator|-
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|newVals
return|;
block|}
comment|/**      * Delete a short.      *       * @param vals array of shorts      * @param idx index of short to delete      * @return the array without the deleted short      */
specifier|public
specifier|static
name|short
index|[]
name|deleteArrayShort
parameter_list|(
name|short
index|[]
name|vals
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
specifier|final
name|short
index|[]
name|newVals
init|=
operator|new
name|short
index|[
name|vals
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newVals
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|idx
operator|<
name|newVals
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|newVals
argument_list|,
name|idx
argument_list|,
name|newVals
operator|.
name|length
operator|-
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|newVals
return|;
block|}
comment|/**      * Insert a integer.      *       * @param vals array of integers      * @param val integer to insert      * @param idx index of insertion      * @return the array with added integer      */
specifier|public
specifier|static
name|int
index|[]
name|insertArrayInt
parameter_list|(
name|int
index|[]
name|vals
parameter_list|,
name|int
name|val
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|newVals
init|=
operator|new
name|int
index|[
name|vals
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newVals
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
name|newVals
index|[
name|idx
index|]
operator|=
name|val
expr_stmt|;
if|if
condition|(
name|idx
operator|<
name|vals
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
name|idx
argument_list|,
name|newVals
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|vals
operator|.
name|length
operator|-
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|newVals
return|;
block|}
comment|/**      * Insert a long.      *       * @param vals array of longs      * @param val long to insert      * @param idx index of insertion      * @return the array with added long      */
specifier|public
specifier|static
name|long
index|[]
name|insertArrayLong
parameter_list|(
name|long
index|[]
name|vals
parameter_list|,
name|long
name|val
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|newVals
init|=
operator|new
name|long
index|[
name|vals
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newVals
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
name|newVals
index|[
name|idx
index|]
operator|=
name|val
expr_stmt|;
if|if
condition|(
name|idx
operator|<
name|vals
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
name|idx
argument_list|,
name|newVals
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|vals
operator|.
name|length
operator|-
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|newVals
return|;
block|}
comment|/**      * Insert a short.      *       * @param vals array of shorts      * @param val short to insert      * @param idx index of insertion      * @return the array with added short      */
specifier|public
specifier|static
name|short
index|[]
name|insertArrayShort
parameter_list|(
name|short
index|[]
name|vals
parameter_list|,
name|short
name|val
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
specifier|final
name|short
index|[]
name|newVals
init|=
operator|new
name|short
index|[
name|vals
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
literal|0
argument_list|,
name|newVals
argument_list|,
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
name|newVals
index|[
name|idx
index|]
operator|=
name|val
expr_stmt|;
if|if
condition|(
name|idx
operator|<
name|vals
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|vals
argument_list|,
name|idx
argument_list|,
name|newVals
argument_list|,
name|idx
operator|+
literal|1
argument_list|,
name|vals
operator|.
name|length
operator|-
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|newVals
return|;
block|}
comment|/**      * Searches the specified array of ints for the specified value using the      * binary search algorithm.  The array<strong>must</strong> be sorted (as      * by the<tt>sort</tt> method, above) prior to making this call.  If it      * is not sorted, the results are undefined.  If the array contains      * multiple elements with the specified value, there is no guarantee which      * one will be found.      *      * @param a the array to be searched.      * @param key the value to be searched for.      * @return index of the search key, if it is contained in the list;      *         otherwise,<tt>(-(<i>insertion point</i>) - 1)</tt>.  The      *<i>insertion point</i> is defined as the point at which the      *         key would be inserted into the list: the index of the first      *         element greater than the key, or<tt>list.size()</tt>, if all      *         elements in the list are less than the specified key.  Note      *         that this guarantees that the return value will be&gt;= 0 if      *         and only if the key is found.      */
specifier|public
specifier|static
name|int
name|binarySearch
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
name|key
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
name|size
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>
literal|1
decl_stmt|;
specifier|final
name|int
name|midVal
init|=
name|a
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|midVal
operator|<
name|key
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
if|else if
condition|(
name|midVal
operator|>
name|key
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
block|}
comment|// key found
block|}
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
comment|// key not found.
block|}
block|}
end_class

end_unit
