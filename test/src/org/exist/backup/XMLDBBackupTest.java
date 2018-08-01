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
name|backup
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|ConsoleRestoreListener
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
name|ExistWebServer
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
name|Before
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|diff
operator|.
name|Diff
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
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
name|Path
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
name|assertNotNull
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|XMLDBBackupTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PORT_PLACEHOLDER
init|=
literal|"${PORT}"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"test-xmldb-backup-restore"
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|TemporaryFolder
name|tempFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"local"
block|,
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|toString
argument_list|()
block|}
block|,
block|{
literal|"remote"
block|,
literal|"xmldb:exist://localhost:"
operator|+
name|PORT_PLACEHOLDER
operator|+
literal|"/xmlrpc"
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|String
name|apiName
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
specifier|public
name|String
name|baseUri
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOC1_NAME
init|=
literal|"doc1.xml"
decl_stmt|;
specifier|private
specifier|final
name|String
name|doc1Content
init|=
literal|"<timestamp>"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"</timestamp>"
decl_stmt|;
specifier|private
specifier|final
name|String
name|getBaseUri
parameter_list|()
block|{
return|return
name|baseUri
operator|.
name|replace
argument_list|(
name|PORT_PLACEHOLDER
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|existWebServer
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|backupRestore
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|URISyntaxException
throws|,
name|ParserConfigurationException
block|{
specifier|final
name|XmldbURI
name|collectionUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|getBaseUri
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"/db"
argument_list|)
operator|.
name|append
argument_list|(
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
specifier|final
name|String
name|backupFilename
init|=
literal|"test-xmldb-backup-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|".zip"
decl_stmt|;
comment|// backup the collection
specifier|final
name|Path
name|backupFile
init|=
name|backup
argument_list|(
name|backupFilename
argument_list|,
name|collectionUri
argument_list|)
decl_stmt|;
comment|// delete the collection
name|deleteCollection
argument_list|(
name|collectionUri
argument_list|)
expr_stmt|;
comment|// restore the collection
name|restore
argument_list|(
name|backupFile
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|getBaseUri
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"/db"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check restore has restored the collection
specifier|final
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionUri
operator|.
name|toString
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
specifier|final
name|Resource
name|doc1
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
name|DOC1_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
specifier|final
name|Source
name|expected
init|=
name|Input
operator|.
name|fromString
argument_list|(
name|doc1Content
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|actual
init|=
name|Input
operator|.
name|fromString
argument_list|(
name|doc1
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Diff
name|diff
init|=
name|DiffBuilder
operator|.
name|compare
argument_list|(
name|actual
argument_list|)
operator|.
name|withTest
argument_list|(
name|expected
argument_list|)
operator|.
name|checkForIdentical
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|diff
operator|.
name|hasDifferences
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Path
name|backup
parameter_list|(
specifier|final
name|String
name|filename
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLDBException
throws|,
name|SAXException
block|{
specifier|final
name|Path
name|backupFile
init|=
name|tempFolder
operator|.
name|newFile
argument_list|(
name|filename
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|Backup
name|backup
init|=
operator|new
name|Backup
argument_list|(
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|,
name|backupFile
argument_list|,
name|collectionUri
argument_list|)
decl_stmt|;
name|backup
operator|.
name|backup
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|backupFile
return|;
block|}
specifier|private
name|void
name|restore
parameter_list|(
specifier|final
name|Path
name|backupFile
parameter_list|,
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|URISyntaxException
throws|,
name|ParserConfigurationException
throws|,
name|IOException
block|{
specifier|final
name|Restore
name|restore
init|=
operator|new
name|Restore
argument_list|()
decl_stmt|;
name|restore
operator|.
name|restore
argument_list|(
operator|new
name|ConsoleRestoreListener
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|,
literal|null
argument_list|,
name|backupFile
argument_list|,
name|collectionUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|deleteCollection
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|parent
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionUri
operator|.
name|removeLastSegment
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|colService
init|=
operator|(
name|CollectionManagementService
operator|)
name|parent
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|colService
operator|.
name|removeCollection
argument_list|(
name|collectionUri
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
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
name|getBaseUri
argument_list|()
operator|+
literal|"/db"
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
specifier|final
name|CollectionManagementService
name|colService
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
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
name|colService
operator|.
name|createCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
specifier|final
name|Resource
name|doc1
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
name|DOC1_NAME
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|doc1
operator|.
name|setContent
argument_list|(
name|doc1Content
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
