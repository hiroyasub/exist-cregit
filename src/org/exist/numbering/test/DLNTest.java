begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|DLN
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
specifier|public
class|class
name|DLNTest
extends|extends
name|TestCase
block|{
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|DLNTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|TestItem
implements|implements
name|Comparable
block|{
name|int
name|id
decl_stmt|;
name|NodeId
name|dln
decl_stmt|;
specifier|public
name|TestItem
parameter_list|(
name|int
name|id
parameter_list|,
name|DLN
name|dln
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|dln
operator|=
name|dln
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|dln
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|TestItem
operator|)
name|other
operator|)
operator|.
name|dln
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|int
name|ITEMS_TO_TEST
init|=
literal|100000
decl_stmt|;
specifier|public
name|void
name|testSingleId
parameter_list|()
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testSingleId: generating "
operator|+
name|ITEMS_TO_TEST
operator|+
literal|" random ids --------"
argument_list|)
expr_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|TestItem
name|items
index|[]
init|=
operator|new
name|TestItem
index|[
name|ITEMS_TO_TEST
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
name|ITEMS_TO_TEST
condition|;
name|i
operator|++
control|)
block|{
name|int
name|next
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|5000000
argument_list|)
decl_stmt|;
name|DLN
name|dln
init|=
operator|new
name|DLN
argument_list|()
decl_stmt|;
name|dln
operator|.
name|setLevelId
argument_list|(
literal|0
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|items
index|[
name|i
index|]
operator|=
operator|new
name|TestItem
argument_list|(
name|next
argument_list|,
name|dln
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ generation took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ sorting id set ------"
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|items
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ sort took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ testing id set ------"
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
name|ITEMS_TO_TEST
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Item: "
operator|+
name|i
argument_list|,
name|items
index|[
name|i
index|]
operator|.
name|id
argument_list|,
operator|(
operator|(
name|DLN
operator|)
name|items
index|[
name|i
index|]
operator|.
name|dln
operator|)
operator|.
name|getLevelId
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|ITEMS_TO_TEST
condition|)
name|assertTrue
argument_list|(
name|items
index|[
name|i
index|]
operator|.
name|id
operator|<=
name|items
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|assertTrue
argument_list|(
name|items
index|[
name|i
index|]
operator|.
name|id
operator|>=
name|items
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|id
argument_list|)
expr_stmt|;
comment|//            System.out.println(items[i].toBitString());
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testSingleId: PASSED --------"
argument_list|)
expr_stmt|;
name|Runtime
name|rt
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Memory: total: "
operator|+
operator|(
name|rt
operator|.
name|totalMemory
argument_list|()
operator|/
literal|1024
operator|)
operator|+
literal|"; free: "
operator|+
operator|(
name|rt
operator|.
name|freeMemory
argument_list|()
operator|/
literal|1024
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSort
parameter_list|()
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testSort: generating "
operator|+
name|ITEMS_TO_TEST
operator|+
literal|" random ids --------"
argument_list|)
expr_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|DLN
name|items
index|[]
init|=
operator|new
name|DLN
index|[
name|ITEMS_TO_TEST
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
name|ITEMS_TO_TEST
condition|;
name|i
operator|++
control|)
block|{
name|int
name|next
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|5000000
argument_list|)
decl_stmt|;
name|DLN
name|dln
init|=
operator|new
name|DLN
argument_list|()
decl_stmt|;
name|dln
operator|.
name|setLevelId
argument_list|(
literal|0
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|items
index|[
name|i
index|]
operator|=
name|dln
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ generation took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ sorting id set ------"
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|items
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ sort took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testSortId: PASSED --------"
argument_list|)
expr_stmt|;
name|Runtime
name|rt
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Memory: total: "
operator|+
operator|(
name|rt
operator|.
name|totalMemory
argument_list|()
operator|/
literal|1024
operator|)
operator|+
literal|"; free: "
operator|+
operator|(
name|rt
operator|.
name|freeMemory
argument_list|()
operator|/
literal|1024
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreate
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ testCreate ------"
argument_list|)
expr_stmt|;
name|DLN
name|dln
init|=
operator|new
name|DLN
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|500000
condition|;
name|i
operator|++
control|)
block|{
name|dln
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|500000
argument_list|,
name|dln
operator|.
name|getLevelId
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|toBitString
argument_list|()
operator|+
literal|" = "
operator|+
name|dln
operator|.
name|getLevelId
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testCreate: PASSED --------"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLevelIds
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ testLevelIds ------"
argument_list|)
expr_stmt|;
name|DLN
name|dln
init|=
operator|new
name|DLN
argument_list|(
literal|"1.33.56.2.98.1.27"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.33.56.2.98.1.27"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.56.4.33.30.11.9.40.3.2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.56.4.33.30.11.9.40.3.2"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.8000656.40.3.2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.8000656.40.3.2"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.1"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID after increment: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.2"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.72"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.72"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.7.3/1.34"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.7.3/1.34"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.7.3.1/34"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.7.3.1/34"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dln
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID after increment: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.7.3.1/35"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testing DLN.incrementLevelId --------"
argument_list|)
expr_stmt|;
name|int
index|[]
name|id0
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|33
block|,
literal|56
block|,
literal|2
block|,
literal|98
block|,
literal|1
block|,
literal|27
block|}
decl_stmt|;
name|dln
operator|=
operator|new
name|DLN
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
name|id0
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|dln
operator|.
name|addLevelId
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|id0
index|[
name|i
index|]
condition|;
name|j
operator|++
control|)
name|dln
operator|.
name|incrementLevelId
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ID: "
operator|+
name|dln
operator|.
name|debug
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.33.56.2.98.1.27"
argument_list|,
name|dln
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|dln
operator|.
name|getLevelCount
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------- testLevelIds: PASSED --------"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRelations
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ testLevelRelations ------"
argument_list|)
expr_stmt|;
name|DLN
name|root
init|=
operator|new
name|DLN
argument_list|(
literal|"1.3"
argument_list|)
decl_stmt|;
name|DLN
name|descendant
init|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendant: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing getParentId: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|equals
argument_list|(
name|descendant
operator|.
name|getParentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.2.5.6"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOrSelf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOrSelfOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.4"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendant: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendant: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOrSelf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOrSelfOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.2.5.6"
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.2.5.6.7777"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing getParentId: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|equals
argument_list|(
name|descendant
operator|.
name|getParentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.2.5.6.7777.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1"
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.6.6.66"
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.6.6.65.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.6.6.66"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1/1"
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1/1.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Parent of "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|descendant
operator|.
name|getParentId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|equals
argument_list|(
name|descendant
operator|.
name|getParentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|descendant
operator|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1/1.2.2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isChildOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|descendant
operator|.
name|isChildOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing isDescendantOf: "
operator|+
name|descendant
operator|+
literal|" -> "
operator|+
name|root
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|descendant
operator|.
name|isDescendantOf
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|NodeId
name|left
init|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1"
argument_list|)
decl_stmt|;
name|NodeId
name|dln
init|=
operator|new
name|DLN
argument_list|(
literal|"1.3.1/1"
argument_list|)
decl_stmt|;
name|NodeId
name|right
init|=
operator|new
name|DLN
argument_list|(
literal|"1.3.2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dln
operator|.
name|compareTo
argument_list|(
name|right
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dln
operator|.
name|compareTo
argument_list|(
name|left
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|left
operator|.
name|compareTo
argument_list|(
name|dln
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|right
operator|.
name|compareTo
argument_list|(
name|dln
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|left
operator|.
name|compareTo
argument_list|(
name|right
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------ testLevelRelations: PASSED ------"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

