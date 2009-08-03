begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|sorters
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * TestCase - given a sort() method and an algorithm via a checker, do a variety  * of tests.  *<p>  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  *   */
end_comment

begin_class
specifier|public
class|class
name|SortTestCase
parameter_list|<
name|CH
extends|extends
name|SortMethodChecker
parameter_list|>
extends|extends
name|TestCase
block|{
specifier|protected
specifier|final
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|CH
name|checker
decl_stmt|;
specifier|protected
specifier|final
name|String
name|testSuite
decl_stmt|;
name|SortTestCase
parameter_list|(
name|CH
name|checker
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|testSuite
parameter_list|)
block|{
name|super
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|this
operator|.
name|checker
operator|=
name|checker
expr_stmt|;
name|this
operator|.
name|testSuite
operator|=
name|testSuite
expr_stmt|;
block|}
specifier|protected
name|int
index|[]
name|getRandomIntArray
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|sz
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
specifier|protected
name|int
index|[]
name|getConstantIntArray
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|sz
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
specifier|protected
name|int
index|[]
name|getAscendingIntArray
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|sz
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
specifier|protected
name|int
index|[]
name|getDescendingIntArray
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|sz
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|sz
operator|-
name|i
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
specifier|public
name|void
name|testSingleElement
parameter_list|()
throws|throws
name|Exception
block|{
name|checker
operator|.
name|init
argument_list|(
name|getConstantIntArray
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|checker
operator|.
name|init
argument_list|(
name|getRandomIntArray
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|()
expr_stmt|;
name|checker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testConstant
parameter_list|()
throws|throws
name|Exception
block|{
name|checker
operator|.
name|init
argument_list|(
name|getConstantIntArray
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|()
expr_stmt|;
name|checker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAscending
parameter_list|()
throws|throws
name|Exception
block|{
name|checker
operator|.
name|init
argument_list|(
name|getAscendingIntArray
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|()
expr_stmt|;
name|checker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDecending
parameter_list|()
throws|throws
name|Exception
block|{
name|checker
operator|.
name|init
argument_list|(
name|getDescendingIntArray
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|()
expr_stmt|;
name|checker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSortSubsection1
parameter_list|()
throws|throws
name|Exception
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
literal|1000
condition|;
name|i
operator|+=
literal|100
control|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
literal|1000
index|]
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
literal|1000
condition|;
name|ii
operator|++
control|)
block|{
name|a
index|[
name|ii
index|]
operator|=
operator|(
name|ii
operator|>=
name|i
operator|&&
name|ii
operator|<
name|i
operator|+
literal|100
operator|)
condition|?
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
else|:
literal|999
operator|-
name|ii
expr_stmt|;
block|}
name|checker
operator|.
name|init
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|99
argument_list|)
expr_stmt|;
name|checker
operator|.
name|check
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|99
argument_list|)
expr_stmt|;
comment|// check that the other values have not been disturbed
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|i
condition|;
name|ii
operator|++
control|)
block|{
name|checker
operator|.
name|checkValue
argument_list|(
name|ii
argument_list|,
literal|999
operator|-
name|ii
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|ii
init|=
name|i
operator|+
literal|100
init|;
name|ii
operator|<
literal|1000
condition|;
name|ii
operator|++
control|)
block|{
name|checker
operator|.
name|checkValue
argument_list|(
name|ii
argument_list|,
literal|999
operator|-
name|ii
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testSortSubsection2
parameter_list|()
throws|throws
name|Exception
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
literal|1000
condition|;
name|i
operator|+=
literal|100
control|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
literal|1000
index|]
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
literal|1000
condition|;
name|ii
operator|++
control|)
block|{
name|a
index|[
name|ii
index|]
operator|=
operator|(
name|ii
operator|>=
name|i
operator|&&
name|ii
operator|<
name|i
operator|+
literal|100
operator|)
condition|?
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
else|:
name|ii
expr_stmt|;
block|}
name|checker
operator|.
name|init
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|99
argument_list|)
expr_stmt|;
name|checker
operator|.
name|check
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|99
argument_list|)
expr_stmt|;
comment|// check that the other values have not been disturbed
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|i
condition|;
name|ii
operator|++
control|)
block|{
name|checker
operator|.
name|checkValue
argument_list|(
name|ii
argument_list|,
name|ii
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|ii
init|=
name|i
operator|+
literal|100
init|;
name|ii
operator|<
literal|1000
condition|;
name|ii
operator|++
control|)
block|{
name|checker
operator|.
name|checkValue
argument_list|(
name|ii
argument_list|,
name|ii
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testSortSubsection3
parameter_list|()
throws|throws
name|Exception
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
literal|1000
condition|;
name|i
operator|+=
literal|100
control|)
block|{
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
literal|1000
index|]
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
literal|1000
condition|;
name|ii
operator|++
control|)
block|{
name|a
index|[
name|ii
index|]
operator|=
operator|(
name|ii
operator|>=
name|i
operator|&&
name|ii
operator|<
name|i
operator|+
literal|100
operator|)
condition|?
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
else|:
operator|(
name|ii
operator|%
literal|7
operator|)
expr_stmt|;
block|}
name|checker
operator|.
name|init
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|checker
operator|.
name|sort
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|99
argument_list|)
expr_stmt|;
name|checker
operator|.
name|check
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|99
argument_list|)
expr_stmt|;
comment|// check that the other values have not been disturbed
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|i
condition|;
name|ii
operator|++
control|)
block|{
name|checker
operator|.
name|checkValue
argument_list|(
name|ii
argument_list|,
name|ii
operator|%
literal|7
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|ii
init|=
name|i
operator|+
literal|100
init|;
name|ii
operator|<
literal|1000
condition|;
name|ii
operator|++
control|)
block|{
name|checker
operator|.
name|checkValue
argument_list|(
name|ii
argument_list|,
name|ii
operator|%
literal|7
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

