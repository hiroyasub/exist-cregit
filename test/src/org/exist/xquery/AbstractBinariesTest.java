begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|Consumer2E
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for accessing binaries using XQuery via various APIs.  *  * @see<a href="https://github.com/eXist-db/exist/issues/790">Binary streaming is broken</a>  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractBinariesTest
parameter_list|<
name|T
parameter_list|,
name|U
parameter_list|,
name|E
extends|extends
name|Exception
parameter_list|>
block|{
specifier|protected
specifier|static
specifier|final
name|XmldbURI
name|TEST_COLLECTION
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"BinariesTest"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|BIN1_FILENAME
init|=
literal|"1.bin"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|byte
index|[]
name|BIN1_CONTENT
init|=
literal|"1234567890"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|storeBinaryFile
argument_list|(
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|BIN1_FILENAME
argument_list|)
argument_list|,
name|BIN1_CONTENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|removeCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-1}      */
annotation|@
name|Test
specifier|public
name|void
name|serializeBinary
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace util = \"http://exist-db.org/xquery/util\";\n"
operator|+
literal|"util:binary-doc('"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|BIN1_FILENAME
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"')"
decl_stmt|;
specifier|final
name|QueryResultAccessor
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|resultsAccessor
init|=
name|executeXQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|resultsAccessor
operator|.
name|accept
argument_list|(
name|results
lambda|->
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|size
argument_list|(
name|results
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|U
name|item
init|=
name|item
argument_list|(
name|results
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isBinaryType
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|BIN1_CONTENT
argument_list|,
name|getBytes
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-3}      */
annotation|@
name|Test
specifier|public
name|void
name|readBinary
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|randomData
argument_list|(
literal|1024
operator|*
literal|1024
operator|*
literal|10
argument_list|)
decl_stmt|;
comment|// 10KB
specifier|final
name|Path
name|tmpFile
init|=
name|createTemporaryFile
argument_list|(
name|data
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"import module namespace file = \"http://exist-db.org/xquery/file\";\n"
operator|+
literal|"file:read-binary('"
operator|+
name|tmpFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"')"
decl_stmt|;
specifier|final
name|QueryResultAccessor
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|resultsAccessor
init|=
name|executeXQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|resultsAccessor
operator|.
name|accept
argument_list|(
name|results
lambda|->
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|size
argument_list|(
name|results
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|U
name|item
init|=
name|item
argument_list|(
name|results
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isBinaryType
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|getBytes
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@see https://github.com/eXist-db/exist/issues/790#error-case-4}      */
annotation|@
name|Test
specifier|public
name|void
name|readAndWriteBinary
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|randomData
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
comment|// 1MB
specifier|final
name|Path
name|tmpInFile
init|=
name|createTemporaryFile
argument_list|(
name|data
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|tmpOutFile
init|=
name|temporaryFolder
operator|.
name|newFile
argument_list|()
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"import module namespace file = \"http://exist-db.org/xquery/file\";\n"
operator|+
literal|"let $bin := file:read-binary('"
operator|+
name|tmpInFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"')\n"
operator|+
literal|"return\n"
operator|+
literal|"    file:serialize-binary($bin, '"
operator|+
name|tmpOutFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"')"
decl_stmt|;
specifier|final
name|QueryResultAccessor
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|resultsAccessor
init|=
name|executeXQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|resultsAccessor
operator|.
name|accept
argument_list|(
name|results
lambda|->
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|size
argument_list|(
name|results
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|U
name|item
init|=
name|item
argument_list|(
name|results
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isBooleanType
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|getBoolean
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|tmpInFile
argument_list|)
argument_list|,
name|Files
operator|.
name|readAllBytes
argument_list|(
name|tmpOutFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|byte
index|[]
name|randomData
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
specifier|final
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|protected
name|Path
name|createTemporaryFile
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|f
init|=
name|temporaryFolder
operator|.
name|newFile
argument_list|()
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|f
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|FunctionalInterface
interface|interface
name|QueryResultAccessor
parameter_list|<
name|T
parameter_list|,
name|E
extends|extends
name|Exception
parameter_list|>
extends|extends
name|Consumer2E
argument_list|<
name|Consumer2E
argument_list|<
name|T
argument_list|,
name|AssertionError
argument_list|,
name|E
argument_list|>
argument_list|,
name|AssertionError
argument_list|,
name|E
argument_list|>
block|{     }
specifier|protected
specifier|abstract
name|void
name|storeBinaryFile
parameter_list|(
specifier|final
name|XmldbURI
name|filePath
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|void
name|removeCollection
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|QueryResultAccessor
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|executeXQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|long
name|size
parameter_list|(
name|T
name|results
parameter_list|)
throws|throws
name|E
function_decl|;
specifier|protected
specifier|abstract
name|U
name|item
parameter_list|(
name|T
name|results
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|E
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|isBinaryType
parameter_list|(
name|U
name|item
parameter_list|)
throws|throws
name|E
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|isBooleanType
parameter_list|(
name|U
name|item
parameter_list|)
throws|throws
name|E
function_decl|;
specifier|protected
specifier|abstract
name|byte
index|[]
name|getBytes
parameter_list|(
name|U
name|item
parameter_list|)
throws|throws
name|E
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|getBoolean
parameter_list|(
name|U
name|item
parameter_list|)
throws|throws
name|E
function_decl|;
block|}
end_class

end_unit

