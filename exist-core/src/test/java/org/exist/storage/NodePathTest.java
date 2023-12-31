begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|NodePathTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|basicPaths
parameter_list|()
block|{
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d/e"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/a/b"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/a/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|wildcards
parameter_list|()
block|{
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a//c"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"//c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/c/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b//*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"//c/d"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
operator|.
name|match
argument_list|(
operator|new
name|NodePath
argument_list|(
literal|null
argument_list|,
literal|"/a/b/c/c/d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

