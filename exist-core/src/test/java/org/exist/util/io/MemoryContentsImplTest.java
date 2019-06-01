begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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

begin_comment
comment|/**  * @author Patrick Reinhart<patrick@reini.net>  */
end_comment

begin_class
specifier|public
class|class
name|MemoryContentsImplTest
block|{
specifier|private
name|byte
index|[]
name|buf
decl_stmt|;
specifier|private
name|MemoryContents
name|contents
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|buf
operator|=
operator|new
name|byte
index|[]
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|}
expr_stmt|;
name|contents
operator|=
name|MemoryContentsImpl
operator|.
name|createWithInitialBlocks
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSize
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|contents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeAtEnd
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|contents
operator|.
name|writeAtEnd
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|contents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|contents
operator|.
name|writeAtEnd
argument_list|(
name|buf
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|contents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|write
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|contents
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|2L
argument_list|,
literal|0
argument_list|,
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|contents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|dst
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|contents
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|contents
operator|.
name|read
argument_list|(
name|dst
argument_list|,
literal|0L
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|transferTo
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|buf
operator|.
name|length
decl_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|contents
operator|.
name|writeAtEnd
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|contents
operator|.
name|transferTo
argument_list|(
name|out
argument_list|,
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|bigWriteAndRead
parameter_list|()
throws|throws
name|IOException
block|{
comment|// set up phase
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|length
init|=
literal|1024
operator|*
literal|1024
operator|+
literal|1024
decl_stmt|;
name|buf
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
comment|// test phase
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|contents
operator|.
name|writeAtEnd
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|contents
operator|.
name|transferTo
argument_list|(
name|out
argument_list|,
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
