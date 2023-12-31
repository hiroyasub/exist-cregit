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
name|assertNull
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
name|SubSequenceTest
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
name|Parameterized
operator|.
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
literal|"0 until 10"
block|,
literal|0
block|,
literal|10
block|,
literal|0
block|}
block|,
block|{
literal|"1 until 10"
block|,
literal|1
block|,
literal|10
block|,
literal|99
block|}
block|,
block|{
literal|"1 until 11"
block|,
literal|1
block|,
literal|11
block|,
literal|99
block|}
block|,
block|{
literal|"10 until 20"
block|,
literal|10
block|,
literal|20
block|,
literal|99
block|}
block|,
block|{
literal|"11 until 20"
block|,
literal|11
block|,
literal|20
block|,
literal|98
block|}
block|,
block|{
literal|"11 until 21"
block|,
literal|11
block|,
literal|21
block|,
literal|80
block|}
block|,
block|{
literal|"89 until 99"
block|,
literal|89
block|,
literal|99
block|,
literal|98
block|}
block|,
block|{
literal|"90 until 99"
block|,
literal|90
block|,
literal|99
block|,
literal|99
block|}
block|,
block|{
literal|"90 until 100"
block|,
literal|90
block|,
literal|100
block|,
literal|99
block|}
block|,
block|{
literal|"99 until 109"
block|,
literal|99
block|,
literal|109
block|,
literal|99
block|}
block|,
block|{
literal|"100 until 109"
block|,
literal|100
block|,
literal|109
block|,
literal|5
block|}
block|,
block|{
literal|"100 until 110"
block|,
literal|100
block|,
literal|110
block|,
literal|0
block|}
block|,         }
argument_list|)
return|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|String
name|subSequenceStartEndName
decl_stmt|;
annotation|@
name|Parameterized
operator|.
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
name|Parameterized
operator|.
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
name|Parameterized
operator|.
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
name|itemAt_0
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertItemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|itemAt_1
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertItemAt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|itemAt_2
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertItemAt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|itemAt_8
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertItemAt
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|itemAt_9
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertItemAt
argument_list|(
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|itemAt_10
parameter_list|()
throws|throws
name|XPathException
block|{
name|assertItemAt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertItemAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|long
name|cleanFromInclusive
init|=
name|fromInclusive
operator|<
literal|1
condition|?
literal|1
else|:
name|fromInclusive
decl_stmt|;
name|long
name|length
init|=
name|toExclusive
operator|-
name|cleanFromInclusive
decl_stmt|;
if|if
condition|(
name|toExclusive
operator|>
name|RANGE_END
operator|+
literal|1
condition|)
block|{
name|length
operator|=
name|RANGE_END
operator|-
name|cleanFromInclusive
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|length
condition|)
block|{
specifier|final
name|long
name|expected
init|=
name|RANGE_START
operator|+
operator|(
name|cleanFromInclusive
operator|-
literal|1
operator|)
operator|+
name|pos
decl_stmt|;
specifier|final
name|long
name|actual
init|=
name|getSubsequence
argument_list|()
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|Long
operator|.
name|class
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|getSubsequence
argument_list|()
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

