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
name|NodeProxy
import|;
end_import

begin_comment
comment|/**  * Interface to some sorting algorithm.  *<p>  * This work was undertaken as part of the development of the taxonomic  * repository at http://biodiversity.org.au . See<A  * href="ghw-at-anbg.gov.au">Greg&nbsp;Whitbread</A> for further details.  *   * @author pmurray@bigpond.com  * @author pmurray@anbg.gov.au  * @author https://sourceforge.net/users/paulmurray  * @author http://www.users.bigpond.com/pmurray  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SortingAlgorithmTester
block|{
specifier|abstract
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
function_decl|;
specifier|abstract
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
function_decl|;
specifier|abstract
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
function_decl|;
specifier|abstract
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
function_decl|;
specifier|abstract
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
function_decl|;
specifier|abstract
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
function_decl|;
comment|// This one must change its parameters so some Java compilers do not
comment|// get fooled
specifier|abstract
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
function_decl|;
specifier|abstract
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
function_decl|;
specifier|abstract
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
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|static
name|SortingAlgorithmTester
index|[]
name|allSorters
parameter_list|()
block|{
return|return
operator|new
name|SortingAlgorithmTester
index|[]
block|{
operator|new
name|InsertionSortTester
argument_list|()
block|,
operator|new
name|HeapSortTester
argument_list|()
block|,
operator|new
name|FastQSortTester
argument_list|()
block|,
comment|//			new HSortTester(),
block|}
return|;
block|}
block|}
end_class

end_unit

