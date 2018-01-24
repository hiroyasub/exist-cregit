begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2015 The eXist Project  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
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
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelSuite
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
name|sorters
operator|.
name|SortComparatorTest
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
name|sorters
operator|.
name|SortTest
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
name|Suite
import|;
end_import

begin_comment
comment|/**  * Perform comprehensive testing of the eXist sort algorithms.  *<p>  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  *   */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelSuite
operator|.
name|class
argument_list|)
annotation|@
name|Suite
operator|.
name|SuiteClasses
argument_list|(
block|{
name|SortTest
operator|.
name|class
block|,
name|SortComparatorTest
operator|.
name|class
block|,
block|}
argument_list|)
specifier|public
class|class
name|SortTests
block|{ }
end_class

end_unit

