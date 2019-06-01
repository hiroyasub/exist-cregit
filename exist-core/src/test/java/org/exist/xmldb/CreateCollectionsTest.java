begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|net
operator|.
name|URISyntaxException
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Collections
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
name|security
operator|.
name|Account
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
name|ExistXmldbEmbeddedServer
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
name|InputStreamUtil
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
name|TestUtils
operator|.
name|*
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
name|assertNull
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
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|samples
operator|.
name|Samples
operator|.
name|SAMPLES
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_class
specifier|public
class|class
name|CreateCollectionsTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testCreateCollection"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//create a test collection
specifier|final
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|test
init|=
name|cms
operator|.
name|createCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// change ownership to guest
name|Account
name|guest
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|GUEST_DB_USER
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|guest
argument_list|,
name|guest
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//delete the test collection
specifier|final
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cms
operator|.
name|removeCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rootCollectionHasNoParent
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|ADMIN_DB_USER
argument_list|,
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"root collection has no parent"
argument_list|,
name|root
operator|.
name|getParentCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|collectionMustProvideAtLeastOneService
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|colTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|Service
index|[]
name|services
init|=
name|colTest
operator|.
name|getServices
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Collection must provide at least one Service"
argument_list|,
name|services
operator|!=
literal|null
operator|&&
name|services
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createCollection_hasNoSubCollections_andIsOpen
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|colTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|colTest
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Created Collection has zero child collections"
argument_list|,
literal|0
argument_list|,
name|testCollection
operator|.
name|getChildCollectionCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Created Collection state should be Open after creation"
argument_list|,
name|testCollection
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeSamplesShakespeare
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|Collection
name|colTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|colTest
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|storedResourceNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|filenames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//store the samples
for|for
control|(
specifier|final
name|String
name|sampleName
range|:
name|SAMPLES
operator|.
name|getShakespeareXmlSampleNames
argument_list|()
control|)
block|{
specifier|final
name|Resource
name|res
init|=
name|storeResourceFromFile
argument_list|(
name|SAMPLES
operator|.
name|getShakespeareSample
argument_list|(
name|sampleName
argument_list|)
argument_list|,
name|testCollection
argument_list|,
name|sampleName
argument_list|)
decl_stmt|;
name|storedResourceNames
operator|.
name|add
argument_list|(
name|res
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|filenames
operator|.
name|add
argument_list|(
name|sampleName
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|filenames
argument_list|,
name|storedResourceNames
argument_list|)
expr_stmt|;
comment|//get a list from the database of stored resource names
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|retrievedStoredResourceNames
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|testCollection
operator|.
name|listResources
argument_list|()
argument_list|)
decl_stmt|;
comment|//order of names from database may not be the order in which the files were loaded!
name|Collections
operator|.
name|sort
argument_list|(
name|filenames
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|retrievedStoredResourceNames
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|filenames
argument_list|,
name|retrievedStoredResourceNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeRemoveStoreResource
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|Collection
name|colTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|colTest
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|testFile
init|=
literal|"macbeth.xml"
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getMacbethSample
argument_list|()
init|)
block|{
name|storeResourceFromFile
argument_list|(
name|is
argument_list|,
name|testCollection
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
block|}
name|Resource
name|resMacbeth
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"getResource("
operator|+
name|testFile
operator|+
literal|"\")"
argument_list|,
name|resMacbeth
argument_list|)
expr_stmt|;
specifier|final
name|int
name|resourceCount
init|=
name|testCollection
operator|.
name|getResourceCount
argument_list|()
decl_stmt|;
name|testCollection
operator|.
name|removeResource
argument_list|(
name|resMacbeth
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"After removal resource count must decrease"
argument_list|,
name|resourceCount
operator|-
literal|1
argument_list|,
name|testCollection
operator|.
name|getResourceCount
argument_list|()
argument_list|)
expr_stmt|;
name|resMacbeth
operator|=
name|testCollection
operator|.
name|getResource
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|resMacbeth
argument_list|)
expr_stmt|;
comment|// restore the resource just removed
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getMacbethSample
argument_list|()
init|)
block|{
name|storeResourceFromFile
argument_list|(
name|is
argument_list|,
name|testCollection
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"After re-store resource count must increase"
argument_list|,
name|resourceCount
argument_list|,
name|testCollection
operator|.
name|getResourceCount
argument_list|()
argument_list|)
expr_stmt|;
name|resMacbeth
operator|=
name|testCollection
operator|.
name|getResource
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"getResource("
operator|+
name|testFile
operator|+
literal|"\")"
argument_list|,
name|resMacbeth
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeBinaryResource
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|Collection
name|colTest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|colTest
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|fLogo
init|=
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"org/exist/xquery/value/logo.jpg"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|storeBinaryResourceFromFile
argument_list|(
name|fLogo
argument_list|,
name|testCollection
argument_list|)
decl_stmt|;
name|Object
name|content
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"logo.jpg"
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|byte
index|[]
name|dataStored
init|=
operator|(
name|byte
index|[]
operator|)
name|content
decl_stmt|;
name|assertArrayEquals
argument_list|(
literal|"After storing binary resource, data out==data in"
argument_list|,
name|data
argument_list|,
name|dataStored
argument_list|)
expr_stmt|;
block|}
specifier|private
name|XMLResource
name|storeResourceFromFile
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|,
specifier|final
name|Collection
name|testCollection
parameter_list|,
specifier|final
name|String
name|fileName
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|fileName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"storeResourceFromFile"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|InputStreamUtil
operator|.
name|readString
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|private
name|byte
index|[]
name|storeBinaryResourceFromFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|Collection
name|testCollection
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
specifier|final
name|Resource
name|res
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"store binary Resource From File"
argument_list|,
name|res
argument_list|)
expr_stmt|;
comment|// Get an array of bytes from the file:
specifier|final
name|byte
index|[]
name|data
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleCreates
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|testCol
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cms
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
name|Collection
name|c1
init|=
name|testCol
operator|.
name|getChildCollection
argument_list|(
literal|"dummy1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setCollection
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"dummy2"
argument_list|)
expr_stmt|;
name|Collection
name|c2
init|=
name|c1
operator|.
name|getChildCollection
argument_list|(
literal|"dummy2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setCollection
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|cms
operator|.
name|createCollection
argument_list|(
literal|"dummy3"
argument_list|)
expr_stmt|;
name|Collection
name|c3
init|=
name|c2
operator|.
name|getChildCollection
argument_list|(
literal|"dummy3"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c3
argument_list|)
expr_stmt|;
name|cms
operator|.
name|setCollection
argument_list|(
name|testCol
argument_list|)
expr_stmt|;
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
name|c1
operator|=
name|testCol
operator|.
name|getChildCollection
argument_list|(
literal|"dummy1"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
