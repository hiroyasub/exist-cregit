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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|TestCase
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|aryEq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|createMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|verify
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

begin_comment
comment|/**  * @author<a href="mailto:patrick@reini.net">Patrick Reinhart</a>  */
end_comment

begin_class
specifier|public
class|class
name|OverflowToDiskStreamTest
block|{
specifier|private
name|MemoryContents
name|memoryContents
decl_stmt|;
specifier|private
name|OutputStreamSupplier
name|overflowStreamSupplier
decl_stmt|;
specifier|private
name|OverflowToDiskStream
name|overflowToDiskStream
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|memoryContents
operator|=
name|createMock
argument_list|(
name|MemoryContents
operator|.
name|class
argument_list|)
expr_stmt|;
name|overflowStreamSupplier
operator|=
name|createMock
argument_list|(
name|OutputStreamSupplier
operator|.
name|class
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|=
operator|new
name|OverflowToDiskStream
argument_list|(
literal|5
argument_list|,
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeSingleByte
parameter_list|()
throws|throws
name|IOException
block|{
name|TestOutputStream
name|testOutput
init|=
operator|new
name|TestOutputStream
argument_list|()
decl_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|writeAtEnd
argument_list|(
name|aryEq
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'1'
block|}
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|writeAtEnd
argument_list|(
name|aryEq
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'2'
block|}
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|writeAtEnd
argument_list|(
name|aryEq
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'3'
block|}
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|writeAtEnd
argument_list|(
name|aryEq
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'4'
block|}
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|writeAtEnd
argument_list|(
name|aryEq
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'5'
block|}
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|overflowStreamSupplier
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|testOutput
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|transferTo
argument_list|(
name|testOutput
argument_list|,
literal|0L
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
name|memoryContents
operator|.
name|reset
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
literal|'1'
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
literal|'2'
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
literal|'3'
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
literal|'4'
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
literal|'5'
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
literal|'6'
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
name|testOutput
operator|.
name|assertClosedContent
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'6'
block|}
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|replay
argument_list|(
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|replay
argument_list|(
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeByteArray
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[]
block|{
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
decl_stmt|;
name|TestOutputStream
name|testOutput
init|=
operator|new
name|TestOutputStream
argument_list|()
decl_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|writeAtEnd
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|overflowStreamSupplier
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|testOutput
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|memoryContents
operator|.
name|transferTo
argument_list|(
name|testOutput
argument_list|,
literal|0L
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|5L
argument_list|)
expr_stmt|;
name|memoryContents
operator|.
name|reset
argument_list|()
expr_stmt|;
name|replay
argument_list|(
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// should not trigger as still writing to memory
name|overflowToDiskStream
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|overflowToDiskStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|overflowToDiskStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|memoryContents
argument_list|,
name|overflowStreamSupplier
argument_list|)
expr_stmt|;
name|testOutput
operator|.
name|assertClosedContent
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|TestOutputStream
extends|extends
name|ByteArrayOutputStream
block|{
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|int
name|flushCount
decl_stmt|;
specifier|public
name|void
name|assertClosedContent
parameter_list|(
name|byte
index|[]
name|expected
parameter_list|,
name|int
name|expectedFlushes
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Stream not closed"
argument_list|,
name|closed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedFlushes
argument_list|,
name|flushCount
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flushCount
operator|++
expr_stmt|;
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

