begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelParameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Cardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|RangeSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelParameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SubSequenceRangeTest
block|{
specifier|private
specifier|static
specifier|final
name|long
name|RANGE_START
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|RANGE_END
init|=
literal|99
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"0 until 1"
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
block|,
block|{
literal|"0 until 100"
block|,
literal|0
block|,
literal|100
block|,
literal|99
block|}
block|,
block|{
literal|"1 until 100"
block|,
literal|1
block|,
literal|100
block|,
literal|99
block|}
block|,
block|{
literal|"2 until 100"
block|,
literal|2
block|,
literal|100
block|,
literal|98
block|}
block|,
block|{
literal|"10 until 90"
block|,
literal|10
block|,
literal|90
block|,
literal|80
block|}
block|,
block|{
literal|"1 until 99"
block|,
literal|1
block|,
literal|99
block|,
literal|98
block|}
block|,
block|{
literal|"1 until 100"
block|,
literal|1
block|,
literal|100
block|,
literal|99
block|}
block|,
block|{
literal|"1 until 101"
block|,
literal|1
block|,
literal|101
block|,
literal|99
block|}
block|,
block|{
literal|"-1 until 110"
block|,
operator|-
literal|1
block|,
literal|110
block|,
literal|99
block|}
block|,
block|{
literal|"-4 until 6"
block|,
operator|-
literal|4
block|,
literal|6
block|,
literal|5
block|}
block|,
block|{
literal|"-4 until -7"
block|,
operator|-
literal|4
block|,
operator|-
literal|7
block|,
literal|0
block|}
block|,
block|{
literal|"99 until 100"
block|,
literal|99
block|,
literal|100
block|,
literal|1
block|}
block|,
block|{
literal|"100 until 101"
block|,
literal|100
block|,
literal|101
block|,
literal|0
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Parameter
specifier|public
name|String
name|subSequenceStartEndName
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
specifier|public
name|long
name|fromInclusive
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|2
argument_list|)
specifier|public
name|int
name|toExclusive
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|3
argument_list|)
specifier|public
name|int
name|expectedSubsequenceLength
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|RangeSequence
name|range
init|=
operator|new
name|RangeSequence
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|RANGE_START
argument_list|)
argument_list|,
operator|new
name|IntegerValue
argument_list|(
name|RANGE_END
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|SubSequence
name|getSubsequence
parameter_list|()
block|{
return|return
operator|new
name|SubSequence
argument_list|(
name|fromInclusive
argument_list|,
name|toExclusive
argument_list|,
name|range
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getItemCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|expectedSubsequenceLength
argument_list|,
name|getSubsequence
argument_list|()
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isEmpty
parameter_list|()
block|{
if|if
condition|(
name|expectedSubsequenceLength
operator|==
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|hasOne
parameter_list|()
block|{
if|if
condition|(
name|expectedSubsequenceLength
operator|==
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|hasOne
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|hasOne
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|hasMany
parameter_list|()
block|{
if|if
condition|(
name|expectedSubsequenceLength
operator|>
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|hasMany
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|hasMany
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getCardinality
parameter_list|()
block|{
specifier|final
name|Cardinality
name|expectedCardinality
decl_stmt|;
if|if
condition|(
name|expectedSubsequenceLength
operator|==
literal|0
condition|)
block|{
name|expectedCardinality
operator|=
name|Cardinality
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
if|else if
condition|(
name|expectedSubsequenceLength
operator|==
literal|1
condition|)
block|{
name|expectedCardinality
operator|=
name|Cardinality
operator|.
name|EXACTLY_ONE
expr_stmt|;
block|}
else|else
block|{
name|expectedCardinality
operator|=
name|Cardinality
operator|.
name|_MANY
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedCardinality
argument_list|,
name|getSubsequence
argument_list|()
operator|.
name|getCardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|iterate_loop
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|SequenceIterator
name|it
init|=
name|getSubsequence
argument_list|()
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedSubsequenceLength
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|iterate_skip_loop
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|SequenceIterator
name|it
init|=
name|getSubsequence
argument_list|()
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedSubsequenceLength
argument_list|,
name|it
operator|.
name|skippable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|skipped
init|=
name|it
operator|.
name|skip
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|skipped
operator|<=
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|long
name|remaining
init|=
name|expectedSubsequenceLength
operator|-
name|skipped
decl_stmt|;
name|assertEquals
argument_list|(
name|remaining
argument_list|,
name|it
operator|.
name|skippable
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|remaining
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|iterate_loop_skip_loop
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|SequenceIterator
name|it
init|=
name|getSubsequence
argument_list|()
operator|.
name|iterate
argument_list|()
decl_stmt|;
specifier|final
name|int
name|loopOneMax
init|=
literal|5
decl_stmt|;
name|int
name|count
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
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|i
operator|<
name|loopOneMax
condition|;
name|i
operator|++
control|)
block|{
name|it
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
specifier|final
name|long
name|expectedLoopOneConsumed
init|=
name|Math
operator|.
name|min
argument_list|(
name|loopOneMax
argument_list|,
name|expectedSubsequenceLength
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedLoopOneConsumed
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|long
name|remaining
init|=
name|expectedSubsequenceLength
operator|-
name|expectedLoopOneConsumed
decl_stmt|;
name|assertEquals
argument_list|(
name|remaining
argument_list|,
name|it
operator|.
name|skippable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|skipped
init|=
name|it
operator|.
name|skip
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|skipped
operator|<=
literal|3
argument_list|)
expr_stmt|;
name|remaining
operator|-=
name|skipped
expr_stmt|;
name|assertEquals
argument_list|(
name|remaining
argument_list|,
name|it
operator|.
name|skippable
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|it
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|remaining
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

