begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|mockito
operator|.
name|Mockito
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
name|assertFalse
import|;
end_import

begin_class
specifier|public
class|class
name|LeasableTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|fromCloseable
parameter_list|()
block|{
name|AutoCloseable
name|autoCloseable
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AutoCloseable
operator|.
name|class
argument_list|)
decl_stmt|;
name|Leasable
argument_list|<
name|AutoCloseable
argument_list|>
name|leasable
init|=
name|Leasable
operator|.
name|fromCloseable
argument_list|(
name|autoCloseable
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|leasable
operator|.
name|isLeased
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|leasable
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|Leasable
argument_list|<
name|AutoCloseable
argument_list|>
operator|.
name|Lease
name|lease1
init|=
name|leasable
operator|.
name|lease
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|leasable
operator|.
name|isLeased
argument_list|()
argument_list|)
expr_stmt|;
name|Leasable
argument_list|<
name|AutoCloseable
argument_list|>
operator|.
name|Lease
name|lease2
init|=
name|leasable
operator|.
name|lease
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|lease1
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|lease1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|lease1
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|leasable
operator|.
name|isLeased
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|leasable
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|lease2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|leasable
operator|.
name|isLeased
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|leasable
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
