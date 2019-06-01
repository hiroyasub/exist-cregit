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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  * Test case to check methods that use comparators.  *<p>  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ComparatorChecker
extends|extends
name|SortMethodChecker
block|{
enum|enum
name|SortOrder
block|{
name|ASCENDING
block|,
name|DESCENDING
block|,
name|UNSTABLE
block|,
name|RANDOM
block|}
empty_stmt|;
name|ComparatorChecker
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
specifier|abstract
name|void
name|check
parameter_list|(
name|SortOrder
name|sortOrder
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|abstract
name|void
name|sort
parameter_list|(
name|SortOrder
name|sortOrder
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|sort
parameter_list|(
name|SortOrder
name|sortOrder
parameter_list|)
throws|throws
name|Exception
block|{
name|sort
argument_list|(
name|sortOrder
argument_list|,
literal|0
argument_list|,
name|getLength
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|void
name|check
parameter_list|(
name|SortOrder
name|sortOrder
parameter_list|)
throws|throws
name|Exception
block|{
name|check
argument_list|(
name|sortOrder
argument_list|,
literal|0
argument_list|,
name|getLength
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
