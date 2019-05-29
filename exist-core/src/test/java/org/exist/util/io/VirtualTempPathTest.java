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
name|After
import|;
end_import

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
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
import|;
end_import

begin_comment
comment|/**  * @author Patrick Reinhart<patrick@reini.net>  */
end_comment

begin_class
specifier|public
class|class
name|VirtualTempPathTest
block|{
specifier|private
name|TemporaryFileManager
name|temporaryFileManager
decl_stmt|;
specifier|private
name|VirtualTempPath
name|virtualTempPath
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|temporaryFileManager
operator|=
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|virtualTempPath
operator|=
operator|new
name|VirtualTempPath
argument_list|(
literal|2048
argument_list|,
name|temporaryFileManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|virtualTempPath
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newOutputStreamMemoryOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|OutputStream
name|out
init|=
name|virtualTempPath
operator|.
name|newOutputStream
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|OverflowToDiskStream
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
name|writeTestData
argument_list|(
name|out
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|virtualTempPath
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newOutputStreamNoMemoryBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|virtualTempPath
operator|=
operator|new
name|VirtualTempPath
argument_list|(
literal|0
argument_list|,
name|temporaryFileManager
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
name|virtualTempPath
operator|.
name|newOutputStream
argument_list|()
decl_stmt|;
name|assertNotEquals
argument_list|(
name|OverflowToDiskStream
operator|.
name|class
argument_list|,
name|out
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
name|writeTestData
argument_list|(
name|out
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|virtualTempPath
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newInputStreamUseEmptyInputStreamIfNotAlreadyWritten
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|virtualTempPath
operator|.
name|newInputStream
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ByteArrayInputStream
operator|.
name|class
argument_list|,
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newInputStreamAfterDataWrittenInMemory
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
name|writeTestData
argument_list|(
name|virtualTempPath
operator|.
name|newOutputStream
argument_list|()
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|virtualTempPath
operator|.
name|newInputStream
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|MemoryContentsInputStream
operator|.
name|class
argument_list|,
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|readAllBytes
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|virtualTempPath
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newInputStreamAfterDataWrittenToDisk
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
name|writeTestData
argument_list|(
name|virtualTempPath
operator|.
name|newOutputStream
argument_list|()
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|virtualTempPath
operator|.
name|newInputStream
argument_list|()
decl_stmt|;
name|assertNotEquals
argument_list|(
name|MemoryContentsInputStream
operator|.
name|class
argument_list|,
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|readAllBytes
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|buf
argument_list|,
name|virtualTempPath
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sizeNotYetWritten
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|virtualTempPath
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
name|sizeNWrittenInMemory
parameter_list|()
throws|throws
name|IOException
block|{
name|writeTestData
argument_list|(
name|virtualTempPath
operator|.
name|newOutputStream
argument_list|()
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123L
argument_list|,
name|virtualTempPath
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
name|sizeNWrittenToDisk
parameter_list|()
throws|throws
name|IOException
block|{
name|writeTestData
argument_list|(
name|virtualTempPath
operator|.
name|newOutputStream
argument_list|()
argument_list|,
literal|2123
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2123L
argument_list|,
name|virtualTempPath
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|byte
index|[]
name|readAllBytes
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|private
name|byte
index|[]
name|writeTestData
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|buf
index|[
name|size
operator|-
literal|1
index|]
operator|=
literal|'x'
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|buf
return|;
block|}
block|}
end_class

end_unit

