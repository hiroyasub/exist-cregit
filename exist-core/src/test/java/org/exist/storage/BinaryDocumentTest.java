begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
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
name|LockException
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
name|ClassRule
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
name|util
operator|.
name|Optional
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|BinaryDocumentTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|removeCollection
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|LockException
throws|,
name|EXistException
block|{
specifier|final
name|XmldbURI
name|testCollectionUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/remove-collection-test"
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|thingUri
init|=
name|testCollectionUri
operator|.
name|append
argument_list|(
literal|"thing"
argument_list|)
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
comment|// create a collection
specifier|final
name|Collection
name|thingCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|thingUri
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|thingCollection
argument_list|)
expr_stmt|;
comment|// add a binary document to the collection
specifier|final
name|byte
index|[]
name|binaryData1
init|=
literal|"binary-file1"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
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
name|binaryData1
argument_list|)
init|)
block|{
name|thingCollection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"file1.bin"
argument_list|)
argument_list|,
name|is
argument_list|,
literal|"application/octet-stream"
argument_list|,
name|binaryData1
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// remove the collection
name|assertTrue
argument_list|(
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|thingCollection
argument_list|)
argument_list|)
expr_stmt|;
comment|// try and store a binary doc with the same name as the thing collection (should succeed)
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|testCollectionUri
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|binaryData2
init|=
literal|"binary-file2"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
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
name|binaryData2
argument_list|)
init|)
block|{
name|testCollection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"thing"
argument_list|)
argument_list|,
name|is
argument_list|,
literal|"application/octet-stream"
argument_list|,
name|binaryData2
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|overwriteCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|LockException
block|{
specifier|final
name|XmldbURI
name|testCollectionUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/overwrite-collection-test"
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|thingUri
init|=
name|testCollectionUri
operator|.
name|append
argument_list|(
literal|"thing"
argument_list|)
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
comment|// create a collection
specifier|final
name|Collection
name|thingCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|thingUri
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|thingCollection
argument_list|)
expr_stmt|;
comment|// attempt to create a binary document with the same uri as the thingCollection (should fail)
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|testCollectionUri
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|binaryData
init|=
literal|"binary-file"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
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
name|binaryData
argument_list|)
init|)
block|{
try|try
block|{
name|testCollection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|thingUri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|is
argument_list|,
literal|"application/octet-stream"
argument_list|,
name|binaryData
operator|.
name|length
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have been able to overwrite Collection with Binary Document"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"The collection '"
operator|+
name|testCollectionUri
operator|.
name|getRawCollectionPath
argument_list|()
operator|+
literal|"' already has a sub-collection named '"
operator|+
name|thingUri
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"', you cannot create a Document with the same name as an existing collection."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

