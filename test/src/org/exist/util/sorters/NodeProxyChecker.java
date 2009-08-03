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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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

begin_comment
comment|/**  * check sort(NodeProxy)  *<p>  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  *   */
end_comment

begin_class
class|class
name|NodeProxyChecker
extends|extends
name|SortMethodChecker
block|{
name|NodeProxyChecker
parameter_list|(
name|SortingAlgorithmTester
name|sorter
parameter_list|)
block|{
name|super
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
block|}
name|NodeProxy
index|[]
name|a
decl_stmt|;
name|int
name|getLength
parameter_list|()
block|{
return|return
name|a
operator|.
name|length
return|;
block|}
comment|/** 	 * It asserts the ascending ordering of a NodeProxy array 	 */
name|void
name|check
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|lo
init|;
name|i
operator|<
name|hi
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
operator|(
operator|(
name|SortTestNodeProxy
operator|)
name|a
index|[
name|i
index|]
operator|)
operator|.
name|val
operator|<=
operator|(
operator|(
name|SortTestNodeProxy
operator|)
name|a
index|[
name|i
operator|+
literal|1
index|]
operator|)
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * It loads an input int array into the internal NodeProxy one 	 */
name|void
name|init
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
throws|throws
name|Exception
block|{
name|a
operator|=
operator|new
name|NodeProxy
index|[
name|values
operator|.
name|length
index|]
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
name|values
operator|.
name|length
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
operator|new
name|SortTestNodeProxy
argument_list|(
operator|-
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * This method invokes sort routine on selected sorter 	 */
name|void
name|sort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
block|{
name|sorter
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This method asserts single values 	 */
name|void
name|checkValue
parameter_list|(
name|int
name|idx
parameter_list|,
name|int
name|v
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"@"
operator|+
name|idx
argument_list|,
name|v
argument_list|,
operator|(
operator|(
name|SortTestNodeProxy
operator|)
name|a
index|[
name|idx
index|]
operator|)
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

