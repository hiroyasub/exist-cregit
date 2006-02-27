begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-05 The eXist Project  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
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
name|SwapVals
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
name|HeapSort
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
name|InsertionSort
import|;
end_import

begin_comment
comment|/** 	This class implements a version  	of the Introspective Sort Algorithm. 	 	Reference: David R. Musser 	"Introspective Sorting and Selection Algorithms" 	Software--Practice and Experience, (8): 983-993 (1997)  	The implementation is mainly inspired 	on the article describing the algorithm, 	but also in the work of Michael 	Maniscalco in C++. It is also slightly 	based on the previous implementation of 	FastQSort in eXist. 	 	http://www.cs.rpi.edu/~musser/ 	http://www.cs.rpi.edu/~musser/gp/introsort.ps 	http://www.michael-maniscalco.com/sorting.htm 	 	@author Jose Maria Fernandez */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FastQSort
block|{
specifier|private
specifier|final
specifier|static
name|int
name|M
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|double
name|LOG2
init|=
name|Math
operator|.
name|log
argument_list|(
literal|2.0
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|void
name|IntroSort
parameter_list|(
name|Comparable
name|a
index|[]
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|r
parameter_list|,
name|int
name|maxdepth
parameter_list|)
comment|//----------------------------------------------------
block|{
while|while
condition|(
operator|(
name|r
operator|-
name|l
operator|)
operator|>
name|M
condition|)
block|{
if|if
condition|(
name|maxdepth
operator|<=
literal|0
condition|)
block|{
name|HeapSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
operator|(
name|l
operator|+
name|r
operator|)
operator|/
literal|2
decl_stmt|;
name|int
name|j
decl_stmt|;
name|Comparable
name|partionElement
decl_stmt|;
comment|// Arbitrarily establishing partition element as the midpoint of
comment|// the array.
if|if
condition|(
name|a
index|[
name|l
index|]
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
if|if
condition|(
name|a
index|[
name|l
index|]
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|r
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|r
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|partionElement
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
comment|// loop through the array until indices cross
name|i
operator|=
name|l
operator|+
literal|1
expr_stmt|;
name|j
operator|=
name|r
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|i
operator|<=
name|j
condition|)
block|{
comment|// find the first element that is greater than or equal to
comment|// the partionElement starting from the leftIndex.
while|while
condition|(
operator|(
name|i
operator|<
name|r
operator|)
operator|&&
operator|(
name|partionElement
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
operator|>
literal|0
operator|)
condition|)
operator|++
name|i
expr_stmt|;
comment|// find an element that is smaller than or equal to
comment|// the partionElement starting from the rightIndex.
while|while
condition|(
operator|(
name|j
operator|>
name|l
operator|)
operator|&&
operator|(
name|partionElement
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|j
index|]
argument_list|)
operator|<
literal|0
operator|)
condition|)
operator|--
name|j
expr_stmt|;
comment|// if the indexes have not crossed, swap
if|if
condition|(
name|i
operator|<=
name|j
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
operator|--
name|j
expr_stmt|;
block|}
block|}
comment|// If the right index has not reached the left side of array
comment|// must now sort the left partition.
if|if
condition|(
name|l
operator|<
name|j
condition|)
name|IntroSort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|j
argument_list|,
name|maxdepth
argument_list|)
expr_stmt|;
comment|// If the left index has not reached the right side of array
comment|// must now sort the right partition.
if|if
condition|(
name|i
operator|>=
name|r
condition|)
break|break;
name|l
operator|=
name|i
expr_stmt|;
block|}
name|InsertionSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|IntroSort
parameter_list|(
name|Object
name|a
index|[]
parameter_list|,
name|Comparator
name|comp
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|r
parameter_list|,
name|int
name|maxdepth
parameter_list|)
comment|//----------------------------------------------------
block|{
while|while
condition|(
operator|(
name|r
operator|-
name|l
operator|)
operator|>
name|M
condition|)
block|{
if|if
condition|(
name|maxdepth
operator|<=
literal|0
condition|)
block|{
name|HeapSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|comp
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
operator|(
name|l
operator|+
name|r
operator|)
operator|/
literal|2
decl_stmt|;
name|int
name|j
decl_stmt|;
name|Object
name|partionElement
decl_stmt|;
comment|// Arbitrarily establishing partition element as the midpoint of
comment|// the array.
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|a
index|[
name|l
index|]
argument_list|,
name|a
index|[
name|i
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|a
index|[
name|l
index|]
argument_list|,
name|a
index|[
name|r
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|comp
operator|.
name|compare
argument_list|(
name|a
index|[
name|i
index|]
argument_list|,
name|a
index|[
name|r
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|partionElement
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
comment|// loop through the array until indices cross
name|i
operator|=
name|l
operator|+
literal|1
expr_stmt|;
name|j
operator|=
name|r
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|i
operator|<=
name|j
condition|)
block|{
comment|// find the first element that is greater than or equal to
comment|// the partionElement starting from the leftIndex.
while|while
condition|(
operator|(
name|i
operator|<
name|r
operator|)
operator|&&
operator|(
name|comp
operator|.
name|compare
argument_list|(
name|partionElement
argument_list|,
name|a
index|[
name|i
index|]
argument_list|)
operator|>
literal|0
operator|)
condition|)
operator|++
name|i
expr_stmt|;
comment|// find an element that is smaller than or equal to
comment|// the partionElement starting from the rightIndex.
while|while
condition|(
operator|(
name|j
operator|>
name|l
operator|)
operator|&&
operator|(
name|comp
operator|.
name|compare
argument_list|(
name|partionElement
argument_list|,
name|a
index|[
name|j
index|]
argument_list|)
operator|<
literal|0
operator|)
condition|)
operator|--
name|j
expr_stmt|;
comment|// if the indexes have not crossed, swap
if|if
condition|(
name|i
operator|<=
name|j
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
operator|--
name|j
expr_stmt|;
block|}
block|}
comment|// If the right index has not reached the left side of array
comment|// must now sort the left partition.
if|if
condition|(
name|l
operator|<
name|j
condition|)
name|IntroSort
argument_list|(
name|a
argument_list|,
name|comp
argument_list|,
name|l
argument_list|,
name|j
argument_list|,
name|maxdepth
argument_list|)
expr_stmt|;
comment|// If the left index has not reached the right side of array
comment|// must now sort the right partition.
if|if
condition|(
name|i
operator|>=
name|r
condition|)
break|break;
name|l
operator|=
name|i
expr_stmt|;
block|}
name|InsertionSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|comp
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|IntroSort
parameter_list|(
name|NodeProxy
name|a
index|[]
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|r
parameter_list|,
name|int
name|maxdepth
parameter_list|)
comment|//----------------------------------------------------
block|{
while|while
condition|(
operator|(
name|r
operator|-
name|l
operator|)
operator|>
name|M
condition|)
block|{
if|if
condition|(
name|maxdepth
operator|<=
literal|0
condition|)
block|{
name|HeapSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
operator|(
name|l
operator|+
name|r
operator|)
operator|/
literal|2
decl_stmt|;
name|int
name|j
decl_stmt|;
name|NodeProxy
name|partionElement
decl_stmt|;
comment|// Arbitrarily establishing partition element as the midpoint of
comment|// the array.
if|if
condition|(
name|a
index|[
name|l
index|]
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
if|if
condition|(
name|a
index|[
name|l
index|]
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|r
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|r
index|]
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|partionElement
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
comment|// loop through the array until indices cross
name|i
operator|=
name|l
operator|+
literal|1
expr_stmt|;
name|j
operator|=
name|r
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|i
operator|<=
name|j
condition|)
block|{
comment|// find the first element that is greater than or equal to
comment|// the partionElement starting from the leftIndex.
while|while
condition|(
operator|(
name|i
operator|<
name|r
operator|)
operator|&&
operator|(
name|partionElement
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
operator|>
literal|0
operator|)
condition|)
operator|++
name|i
expr_stmt|;
comment|// find an element that is smaller than or equal to
comment|// the partionElement starting from the rightIndex.
while|while
condition|(
operator|(
name|j
operator|>
name|l
operator|)
operator|&&
operator|(
name|partionElement
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|j
index|]
argument_list|)
operator|<
literal|0
operator|)
condition|)
operator|--
name|j
expr_stmt|;
comment|// if the indexes have not crossed, swap
if|if
condition|(
name|i
operator|<=
name|j
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
operator|--
name|j
expr_stmt|;
block|}
block|}
comment|// If the right index has not reached the left side of array
comment|// must now sort the left partition.
if|if
condition|(
name|l
operator|<
name|j
condition|)
name|IntroSort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|j
argument_list|,
name|maxdepth
argument_list|)
expr_stmt|;
comment|// If the left index has not reached the right side of array
comment|// must now sort the right partition.
if|if
condition|(
name|i
operator|>=
name|r
condition|)
break|break;
name|l
operator|=
name|i
expr_stmt|;
block|}
name|InsertionSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|IntroSort
parameter_list|(
name|List
name|a
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|r
parameter_list|,
name|int
name|maxdepth
parameter_list|)
comment|//----------------------------------------------------
block|{
while|while
condition|(
operator|(
name|r
operator|-
name|l
operator|)
operator|>
name|M
condition|)
block|{
if|if
condition|(
name|maxdepth
operator|<=
literal|0
condition|)
block|{
name|HeapSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
operator|(
name|l
operator|+
name|r
operator|)
operator|/
literal|2
decl_stmt|;
name|int
name|j
decl_stmt|;
name|Object
name|partionElement
decl_stmt|;
comment|// Arbitrarily establishing partition element as the midpoint of
comment|// the array.
if|if
condition|(
operator|(
operator|(
name|Comparable
operator|)
name|a
operator|.
name|get
argument_list|(
name|l
argument_list|)
operator|)
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
if|if
condition|(
operator|(
operator|(
name|Comparable
operator|)
name|a
operator|.
name|get
argument_list|(
name|l
argument_list|)
operator|)
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|r
argument_list|)
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|(
name|Comparable
operator|)
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|r
argument_list|)
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|partionElement
operator|=
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// loop through the array until indices cross
name|i
operator|=
name|l
operator|+
literal|1
expr_stmt|;
name|j
operator|=
name|r
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|i
operator|<=
name|j
condition|)
block|{
comment|// find the first element that is greater than or equal to
comment|// the partionElement starting from the leftIndex.
while|while
condition|(
operator|(
name|i
operator|<
name|r
operator|)
operator|&&
operator|(
operator|(
operator|(
name|Comparable
operator|)
name|partionElement
operator|)
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|>
literal|0
operator|)
condition|)
operator|++
name|i
expr_stmt|;
comment|// find an element that is smaller than or equal to
comment|// the partionElement starting from the rightIndex.
while|while
condition|(
operator|(
name|j
operator|>
name|l
operator|)
operator|&&
operator|(
operator|(
operator|(
name|Comparable
operator|)
name|partionElement
operator|)
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
operator|<
literal|0
operator|)
condition|)
operator|--
name|j
expr_stmt|;
comment|// if the indexes have not crossed, swap
if|if
condition|(
name|i
operator|<=
name|j
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
operator|--
name|j
expr_stmt|;
block|}
block|}
comment|// If the right index has not reached the left side of array
comment|// must now sort the left partition.
if|if
condition|(
name|l
operator|<
name|j
condition|)
name|IntroSort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|j
argument_list|,
name|maxdepth
argument_list|)
expr_stmt|;
comment|// If the left index has not reached the right side of array
comment|// must now sort the right partition.
if|if
condition|(
name|i
operator|>=
name|r
condition|)
break|break;
name|l
operator|=
name|i
expr_stmt|;
block|}
name|InsertionSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|IntroSort
parameter_list|(
name|long
name|a
index|[]
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|r
parameter_list|,
name|Object
name|b
index|[]
parameter_list|,
name|int
name|maxdepth
parameter_list|)
comment|//----------------------------------------------------
block|{
while|while
condition|(
operator|(
name|r
operator|-
name|l
operator|)
operator|>
name|M
condition|)
block|{
if|if
condition|(
name|maxdepth
operator|<=
literal|0
condition|)
block|{
name|HeapSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|,
name|b
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
operator|(
name|l
operator|+
name|r
operator|)
operator|/
literal|2
decl_stmt|;
name|int
name|j
decl_stmt|;
name|long
name|partionElement
decl_stmt|;
comment|// Arbitrarily establishing partition element as the midpoint of
comment|// the array.
if|if
condition|(
name|a
index|[
name|l
index|]
operator|>
name|a
index|[
name|i
index|]
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|b
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
block|}
if|if
condition|(
name|a
index|[
name|l
index|]
operator|>
name|a
index|[
name|r
index|]
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|b
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
index|[
name|i
index|]
operator|>
name|a
index|[
name|r
index|]
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|b
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|partionElement
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
comment|// loop through the array until indices cross
name|i
operator|=
name|l
operator|+
literal|1
expr_stmt|;
name|j
operator|=
name|r
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|i
operator|<=
name|j
condition|)
block|{
comment|// find the first element that is greater than or equal to
comment|// the partionElement starting from the leftIndex.
while|while
condition|(
operator|(
name|i
operator|<
name|r
operator|)
operator|&&
operator|(
name|partionElement
operator|>
name|a
index|[
name|i
index|]
operator|)
condition|)
operator|++
name|i
expr_stmt|;
comment|// find an element that is smaller than or equal to
comment|// the partionElement starting from the rightIndex.
while|while
condition|(
operator|(
name|j
operator|>
name|l
operator|)
operator|&&
operator|(
name|partionElement
operator|<
name|a
index|[
name|j
index|]
operator|)
condition|)
operator|--
name|j
expr_stmt|;
comment|// if the indexes have not crossed, swap
if|if
condition|(
name|i
operator|<=
name|j
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|b
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
operator|--
name|j
expr_stmt|;
block|}
block|}
comment|// If the right index has not reached the left side of array
comment|// must now sort the left partition.
if|if
condition|(
name|l
operator|<
name|j
condition|)
name|IntroSort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|j
argument_list|,
name|b
argument_list|,
name|maxdepth
argument_list|)
expr_stmt|;
comment|// If the left index has not reached the right side of array
comment|// must now sort the right partition.
if|if
condition|(
name|i
operator|>=
name|r
condition|)
break|break;
name|l
operator|=
name|i
expr_stmt|;
block|}
name|InsertionSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|IntroSortByNodeId
parameter_list|(
name|NodeProxy
name|a
index|[]
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|r
parameter_list|,
name|int
name|maxdepth
parameter_list|)
comment|//----------------------------------------------------
block|{
while|while
condition|(
operator|(
name|r
operator|-
name|l
operator|)
operator|>
name|M
condition|)
block|{
if|if
condition|(
name|maxdepth
operator|<=
literal|0
condition|)
block|{
name|HeapSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
operator|(
name|l
operator|+
name|r
operator|)
operator|/
literal|2
decl_stmt|;
name|int
name|j
decl_stmt|;
name|NodeProxy
name|partionElement
decl_stmt|;
comment|// Arbitrarily establishing partition element as the midpoint of
comment|// the array.
if|if
condition|(
name|a
index|[
name|l
index|]
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|i
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// Tri-Median Methode!
if|if
condition|(
name|a
index|[
name|l
index|]
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|r
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
index|[
name|i
index|]
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|r
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|partionElement
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
comment|// loop through the array until indices cross
name|i
operator|=
name|l
operator|+
literal|1
expr_stmt|;
name|j
operator|=
name|r
operator|-
literal|1
expr_stmt|;
while|while
condition|(
name|i
operator|<=
name|j
condition|)
block|{
comment|// find the first element that is greater than or equal to
comment|// the partionElement starting from the leftIndex.
while|while
condition|(
operator|(
name|i
operator|<
name|r
operator|)
operator|&&
operator|(
name|partionElement
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|i
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
condition|)
operator|++
name|i
expr_stmt|;
comment|// find an element that is smaller than or equal to
comment|// the partionElement starting from the rightIndex.
while|while
condition|(
operator|(
name|j
operator|>
name|l
operator|)
operator|&&
operator|(
name|partionElement
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
index|[
name|j
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|<
literal|0
operator|)
condition|)
operator|--
name|j
expr_stmt|;
comment|// if the indexes have not crossed, swap
if|if
condition|(
name|i
operator|<=
name|j
condition|)
block|{
name|SwapVals
operator|.
name|swap
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|j
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
operator|--
name|j
expr_stmt|;
block|}
block|}
comment|// If the right index has not reached the left side of array
comment|// must now sort the left partition.
if|if
condition|(
name|l
operator|<
name|j
condition|)
name|IntroSortByNodeId
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|j
argument_list|,
name|maxdepth
argument_list|)
expr_stmt|;
comment|// If the left index has not reached the right side of array
comment|// must now sort the right partition.
if|if
condition|(
name|i
operator|>=
name|r
condition|)
break|break;
name|l
operator|=
name|i
expr_stmt|;
block|}
name|InsertionSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|Comparable
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
name|IntroSort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
literal|2
operator|*
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
operator|/
name|LOG2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|Object
index|[]
name|a
parameter_list|,
name|Comparator
name|c
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
name|IntroSort
argument_list|(
name|a
argument_list|,
name|c
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
literal|2
operator|*
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
operator|/
name|LOG2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|List
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
name|IntroSort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
literal|2
operator|*
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
operator|/
name|LOG2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|NodeProxy
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|lo
operator|==
name|hi
condition|)
return|return;
comment|// just one item, doesn't need sorting
name|IntroSort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
literal|2
operator|*
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
operator|/
name|LOG2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|sortByNodeId
parameter_list|(
name|NodeProxy
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
if|if
condition|(
name|lo
operator|==
name|hi
condition|)
return|return;
comment|// just one item, doesn't need sorting
name|IntroSortByNodeId
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
literal|2
operator|*
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
operator|/
name|LOG2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|sort
parameter_list|(
name|long
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|Object
name|b
index|[]
parameter_list|)
block|{
name|IntroSort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
name|b
argument_list|,
literal|2
operator|*
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|hi
operator|-
name|lo
operator|+
literal|1
argument_list|)
operator|/
name|LOG2
argument_list|)
argument_list|)
expr_stmt|;
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
throws|throws
name|Exception
block|{
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|String
index|[]
name|a
init|=
operator|new
name|String
index|[]
block|{
literal|"Rudi"
block|,
literal|"Herbert"
block|,
literal|"Anton"
block|,
literal|"Berta"
block|,
literal|"Olga"
block|,
literal|"Willi"
block|,
literal|"Heinz"
block|}
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|l
operator|.
name|add
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Ordering file "
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|java
operator|.
name|io
operator|.
name|BufferedReader
name|is
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|BufferedReader
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileReader
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|rr
decl_stmt|;
while|while
condition|(
operator|(
name|rr
operator|=
name|is
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|rr
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
name|long
name|a
decl_stmt|;
name|long
name|b
decl_stmt|;
name|a
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|sort
argument_list|(
name|l
argument_list|,
literal|0
argument_list|,
name|l
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|b
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Ellapsed time: "
operator|+
operator|(
name|b
operator|-
name|a
operator|)
operator|+
literal|" size: "
operator|+
name|l
operator|.
name|size
argument_list|()
argument_list|)
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
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

