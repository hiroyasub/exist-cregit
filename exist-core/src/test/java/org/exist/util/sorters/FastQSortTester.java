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
name|persistent
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
name|FastQSort
import|;
end_import

begin_comment
comment|/**  * Interface to the quicksort methods.  *  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  * @see FastQSort  *   */
end_comment

begin_class
class|class
name|FastQSortTester
extends|extends
name|SortingAlgorithmTester
block|{
specifier|public
parameter_list|<
name|C
parameter_list|>
name|void
name|invokeSort
parameter_list|(
name|C
name|a
index|[]
parameter_list|,
name|Comparator
argument_list|<
name|C
argument_list|>
name|c
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|c
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|C
argument_list|>
parameter_list|>
name|void
name|invokeSort
parameter_list|(
name|C
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
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
specifier|public
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|C
argument_list|>
parameter_list|>
name|void
name|sort
parameter_list|(
name|C
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
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
specifier|public
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|C
argument_list|>
parameter_list|>
name|void
name|sort
parameter_list|(
name|C
index|[]
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|int
index|[]
name|b
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|C
parameter_list|>
name|void
name|sort
parameter_list|(
name|C
index|[]
name|a
parameter_list|,
name|Comparator
argument_list|<
name|C
argument_list|>
name|c
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|c
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|C
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|C
argument_list|>
parameter_list|>
name|void
name|sort
parameter_list|(
name|List
argument_list|<
name|C
argument_list|>
name|a
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
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
specifier|public
name|void
name|sort
parameter_list|(
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|NodeProxy
index|[]
name|a
parameter_list|)
throws|throws
name|Exception
block|{
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
specifier|public
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
index|[]
name|b
parameter_list|)
throws|throws
name|Exception
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
specifier|public
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
throws|throws
name|Exception
block|{
name|FastQSort
operator|.
name|sortByNodeId
argument_list|(
name|a
argument_list|,
name|lo
argument_list|,
name|hi
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

