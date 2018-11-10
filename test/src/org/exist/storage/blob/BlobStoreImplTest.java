begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2018 Adam Retter  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|blob
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
name|Try
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
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
name|FileUtils
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
name|crypto
operator|.
name|digest
operator|.
name|DigestInputStream
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
name|crypto
operator|.
name|digest
operator|.
name|DigestType
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
name|crypto
operator|.
name|digest
operator|.
name|MessageDigest
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
name|crypto
operator|.
name|digest
operator|.
name|StreamableDigest
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|io
operator|.
name|FastByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|io
operator|.
name|InputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Try
operator|.
name|TaggedTryUnchecked
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple
operator|.
name|Tuple
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bouncycastle
operator|.
name|util
operator|.
name|Arrays
operator|.
name|reverse
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
name|createNiceMock
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
name|assertNotNull
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
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|BlobStoreImplTest
block|{
specifier|private
specifier|static
specifier|final
name|DigestType
name|DIGEST_TYPE
init|=
name|DigestType
operator|.
name|BLAKE_256
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|BlobStore
name|newBlobStore
parameter_list|(
specifier|final
name|Path
name|blobDbx
parameter_list|,
specifier|final
name|Path
name|blobDir
parameter_list|)
block|{
specifier|final
name|Database
name|database
init|=
name|createNiceMock
argument_list|(
name|Database
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|database
operator|.
name|getThreadGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|database
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"BlobStoreTest"
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|database
argument_list|)
expr_stmt|;
return|return
operator|new
name|BlobStoreImpl
argument_list|(
name|database
argument_list|,
name|blobDbx
argument_list|,
name|blobDir
argument_list|,
name|DIGEST_TYPE
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addUnique
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
argument_list|>
name|testFiles
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile
range|:
name|testFiles
control|)
block|{
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|// should be 1 entry per unique test file in the blob.dbx, each entry is the digest and then the reference count
specifier|final
name|long
name|expectedBlobDbxLen
init|=
name|calculateBlobStoreSize
argument_list|(
name|testFiles
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|actualBlobDbxLen
init|=
name|Files
operator|.
name|size
argument_list|(
name|blobDbx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedBlobDbxLen
argument_list|,
name|actualBlobDbxLen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addDuplicates
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile1
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile2
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
expr_stmt|;
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
expr_stmt|;
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
expr_stmt|;
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
expr_stmt|;
block|}
comment|// should be 1 entry per unique test file in the blob.dbx, each entry is the digest and then the reference count
comment|// i.e. only 2 entries!
specifier|final
name|long
name|expectedBlobDbxLen
init|=
name|calculateBlobStoreSize
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|long
name|actualBlobDbxLen
init|=
name|Files
operator|.
name|size
argument_list|(
name|blobDbx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedBlobDbxLen
argument_list|,
name|actualBlobDbxLen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNonExistent
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
comment|// store a blob
specifier|final
name|BlobId
name|storedId
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile
argument_list|)
decl_stmt|;
specifier|final
name|BlobId
name|nonExistent
init|=
operator|new
name|BlobId
argument_list|(
name|reverse
argument_list|(
name|storedId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// try and retrieve a blob by id that does not exist
name|assertNull
argument_list|(
name|blobStore
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|nonExistent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|get
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile1
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile2
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
specifier|final
name|BlobId
name|testFileId1
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
decl_stmt|;
specifier|final
name|BlobId
name|testFileId2
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
decl_stmt|;
name|getAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFileId1
argument_list|,
name|testFile1
argument_list|)
expr_stmt|;
name|getAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFileId2
argument_list|,
name|testFile2
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|with
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile1
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile2
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
specifier|final
name|BlobId
name|testFileId1
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
decl_stmt|;
specifier|final
name|BlobId
name|testFileId2
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
decl_stmt|;
specifier|final
name|MessageDigest
name|gotTestFile1Digest
init|=
name|blobStore
operator|.
name|with
argument_list|(
literal|null
argument_list|,
name|testFileId1
argument_list|,
name|BlobStoreImplTest
operator|::
name|digest
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|testFile1
operator|.
name|_2
operator|.
name|getValue
argument_list|()
argument_list|,
name|gotTestFile1Digest
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|MessageDigest
name|gotTestFile2Digest
init|=
name|blobStore
operator|.
name|with
argument_list|(
literal|null
argument_list|,
name|testFileId2
argument_list|,
name|BlobStoreImplTest
operator|::
name|digest
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|testFile2
operator|.
name|_2
operator|.
name|getValue
argument_list|()
argument_list|,
name|gotTestFile2Digest
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeUnique
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
argument_list|>
name|testFiles
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|,
name|generateTestFile
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|BlobId
argument_list|>
name|addedBlobIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile
range|:
name|testFiles
control|)
block|{
name|addedBlobIds
operator|.
name|add
argument_list|(
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// remove each added blob
for|for
control|(
specifier|final
name|BlobId
name|addedBlobId
range|:
name|addedBlobIds
control|)
block|{
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|addedBlobId
argument_list|)
expr_stmt|;
block|}
comment|// check that each blob was removed
for|for
control|(
specifier|final
name|BlobId
name|addedBlobId
range|:
name|addedBlobIds
control|)
block|{
name|assertNull
argument_list|(
name|blobStore
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|addedBlobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeDuplicates
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile1
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile2
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
name|BlobId
name|testFile1Id
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
decl_stmt|;
name|BlobId
name|testFile2Id
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
decl_stmt|;
comment|// add again to increase reference count
name|testFile1Id
operator|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
expr_stmt|;
name|testFile2Id
operator|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
expr_stmt|;
comment|// remove second reference
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|testFile1Id
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|testFile2Id
argument_list|)
expr_stmt|;
comment|// should still exist with one more reference
name|getAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1Id
argument_list|,
name|testFile1
argument_list|)
expr_stmt|;
name|getAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2Id
argument_list|,
name|testFile2
argument_list|)
expr_stmt|;
comment|// remove first reference
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|testFile1Id
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|testFile2Id
argument_list|)
expr_stmt|;
comment|// should no longer exist as all references were removed
name|assertNull
argument_list|(
name|blobStore
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|testFile1Id
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|blobStore
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|testFile2Id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|compactPersistentReferences
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile1
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile2
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile3
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
specifier|final
name|BlobId
name|testFile1Id
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
decl_stmt|;
specifier|final
name|BlobId
name|testFile2Id
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile2
argument_list|)
decl_stmt|;
name|BlobId
name|testFile3Id
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile3
argument_list|)
decl_stmt|;
comment|// add a second reference for testFile3
name|testFile3Id
operator|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile3
argument_list|)
expr_stmt|;
comment|// remove testFile2
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|testFile2Id
argument_list|)
expr_stmt|;
comment|// remove one of the two references to testFile3
name|blobStore
operator|.
name|remove
argument_list|(
literal|null
argument_list|,
name|testFile3Id
argument_list|)
expr_stmt|;
block|}
comment|// should be 1 entry per unique test file in the blob.dbx, each entry is the digest and then the reference count
comment|// i.e. only 3 entries!
name|long
name|expectedBlobDbxLen
init|=
name|calculateBlobStoreSize
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|long
name|actualBlobDbxLen
init|=
name|Files
operator|.
name|size
argument_list|(
name|blobDbx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedBlobDbxLen
argument_list|,
name|actualBlobDbxLen
argument_list|)
expr_stmt|;
comment|// reopen and close the blob store, this should call {@link BlobStoreImpl#compactPersistentReferences}
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
comment|// after compaction, should only be 2 entries, because testFile2 was removed. testFile3 is still present as it has one reference remaining
name|expectedBlobDbxLen
operator|=
name|calculateBlobStoreSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|actualBlobDbxLen
operator|=
name|Files
operator|.
name|size
argument_list|(
name|blobDbx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedBlobDbxLen
argument_list|,
name|actualBlobDbxLen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|blobDbx
init|=
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"blob.dbx"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|blobDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|testFile1
init|=
name|generateTestFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BlobStore
name|blobStore
init|=
name|newBlobStore
argument_list|(
name|blobDbx
argument_list|,
name|blobDir
argument_list|)
init|)
block|{
name|blobStore
operator|.
name|open
argument_list|()
expr_stmt|;
specifier|final
name|BlobId
name|testFile1Id
init|=
name|addAndVerify
argument_list|(
name|blobStore
argument_list|,
name|testFile1
argument_list|)
decl_stmt|;
comment|// attempt copy
try|try
init|(
specifier|final
name|InputStream
name|src
init|=
name|blobStore
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|testFile1Id
argument_list|)
init|)
block|{
specifier|final
name|Tuple2
argument_list|<
name|BlobId
argument_list|,
name|Long
argument_list|>
name|copiedTestFileId
init|=
name|blobStore
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|testFile1Id
operator|.
name|getId
argument_list|()
argument_list|,
name|copiedTestFileId
operator|.
name|_1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|long
name|calculateBlobStoreSize
parameter_list|(
specifier|final
name|int
name|numRecords
parameter_list|)
block|{
return|return
name|BlobStoreImpl
operator|.
name|BLOB_STORE_HEADER_LEN
operator|+
operator|(
name|numRecords
operator|*
operator|(
name|DIGEST_TYPE
operator|.
name|getDigestLengthBytes
argument_list|()
operator|+
name|BlobStoreImpl
operator|.
name|REFERENCE_COUNT_LEN
operator|)
operator|)
return|;
block|}
specifier|private
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|generateTestFile
parameter_list|()
block|{
comment|// generate random data
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|1024
index|]
decl_stmt|;
comment|// 1MB
name|random
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// get the checksum of the random data
specifier|final
name|StreamableDigest
name|streamableDigest
init|=
name|DIGEST_TYPE
operator|.
name|newStreamableDigest
argument_list|()
decl_stmt|;
name|streamableDigest
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
specifier|final
name|MessageDigest
name|expectedDataDigest
init|=
name|streamableDigest
operator|.
name|copyMessageDigest
argument_list|()
decl_stmt|;
return|return
name|Tuple
argument_list|(
name|data
argument_list|,
name|expectedDataDigest
argument_list|)
return|;
block|}
specifier|private
name|BlobId
name|addAndVerify
parameter_list|(
specifier|final
name|BlobStore
name|blobStore
parameter_list|,
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Tuple2
argument_list|<
name|BlobId
argument_list|,
name|Long
argument_list|>
name|actualBlob
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|blob
operator|.
name|_1
argument_list|)
init|)
block|{
name|actualBlob
operator|=
name|blobStore
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|actualBlob
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|blob
operator|.
name|_2
operator|.
name|getValue
argument_list|()
argument_list|,
name|actualBlob
operator|.
name|_1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|actualBlob
operator|.
name|_1
return|;
block|}
specifier|private
name|void
name|getAndVerify
parameter_list|(
specifier|final
name|BlobStore
name|blobStore
parameter_list|,
specifier|final
name|BlobId
name|blobId
parameter_list|,
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|expectedBlob
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|blobStore
operator|.
name|get
argument_list|(
literal|null
argument_list|,
name|blobId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
specifier|final
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|actualBlob
init|=
name|readAll
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedBlob
operator|.
name|_1
argument_list|,
name|actualBlob
operator|.
name|_1
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedBlob
operator|.
name|_2
operator|.
name|getValue
argument_list|()
argument_list|,
name|actualBlob
operator|.
name|_2
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Tuple2
argument_list|<
name|byte
index|[]
argument_list|,
name|MessageDigest
argument_list|>
name|readAll
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StreamableDigest
name|streamableDigest
init|=
name|DIGEST_TYPE
operator|.
name|newStreamableDigest
argument_list|()
decl_stmt|;
name|is
operator|=
operator|new
name|DigestInputStream
argument_list|(
name|is
argument_list|,
name|streamableDigest
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|os
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|os
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
return|return
name|Tuple
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|streamableDigest
operator|.
name|copyMessageDigest
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|Try
argument_list|<
name|MessageDigest
argument_list|,
name|IOException
argument_list|>
name|digest
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
specifier|final
name|StreamableDigest
name|streamableDigest
init|=
name|DIGEST_TYPE
operator|.
name|newStreamableDigest
argument_list|()
decl_stmt|;
return|return
name|TaggedTryUnchecked
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|FileUtils
operator|.
name|digest
argument_list|(
name|path
argument_list|,
name|streamableDigest
argument_list|)
expr_stmt|;
return|return
name|streamableDigest
operator|.
name|copyMessageDigest
argument_list|()
return|;
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

