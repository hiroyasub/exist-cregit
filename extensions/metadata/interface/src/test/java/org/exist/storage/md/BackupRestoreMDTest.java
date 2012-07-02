begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Restore
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
name|SystemExport
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
name|SystemImport
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
name|DefaultRestoreListener
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
name|RestoreListener
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
name|CollectionConfigurationManager
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
name|IndexInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|BinaryDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
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
name|BrokerPool
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
name|DBBroker
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
name|TransactionManager
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
name|TestConstants
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
name|Configuration
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
name|ConfigurationHelper
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
name|Database
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|BackupRestoreMDTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|col1uri
init|=
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|doc1uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test_string.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|doc2uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test.binary"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML
init|=
literal|"<test/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|BINARY
init|=
literal|"test"
decl_stmt|;
specifier|private
specifier|static
name|String
name|KEY1
init|=
literal|"key1"
decl_stmt|;
specifier|private
specifier|static
name|String
name|VALUE1
init|=
literal|"value1"
decl_stmt|;
specifier|private
specifier|static
name|String
name|KEY2
init|=
literal|"key2"
decl_stmt|;
specifier|private
specifier|static
name|String
name|VALUE2
init|=
literal|"value2"
decl_stmt|;
specifier|private
specifier|static
name|BrokerPool
name|pool
decl_stmt|;
comment|//@Test
specifier|public
name|void
name|test_01
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test_01"
argument_list|)
expr_stmt|;
name|startDB
argument_list|()
expr_stmt|;
name|MetaData
name|md
init|=
name|MetaData
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|String
name|docUUID
init|=
literal|null
decl_stmt|;
name|String
name|doc2UUID
init|=
literal|null
decl_stmt|;
name|String
name|key1UUID
init|=
literal|null
decl_stmt|;
name|String
name|key2UUID
init|=
literal|null
decl_stmt|;
name|String
name|key3UUID
init|=
literal|null
decl_stmt|;
name|File
name|file
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Metas
name|docMD
init|=
name|md
operator|.
name|getMetas
argument_list|(
name|doc1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docMD
argument_list|)
expr_stmt|;
name|docUUID
operator|=
name|docMD
operator|.
name|getUUID
argument_list|()
expr_stmt|;
comment|//set metas
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|docMD
operator|.
name|put
argument_list|(
name|KEY2
argument_list|,
name|VALUE2
argument_list|)
expr_stmt|;
name|Meta
name|meta
init|=
name|docMD
operator|.
name|get
argument_list|(
name|KEY1
argument_list|)
decl_stmt|;
name|key1UUID
operator|=
name|meta
operator|.
name|getUUID
argument_list|()
expr_stmt|;
name|meta
operator|=
name|docMD
operator|.
name|get
argument_list|(
name|KEY2
argument_list|)
expr_stmt|;
name|key2UUID
operator|=
name|meta
operator|.
name|getUUID
argument_list|()
expr_stmt|;
comment|//binary
name|docMD
operator|=
name|MetaData
operator|.
name|get
argument_list|()
operator|.
name|getMetas
argument_list|(
name|doc2uri
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|docMD
argument_list|)
expr_stmt|;
name|doc2UUID
operator|=
name|docMD
operator|.
name|getUUID
argument_list|()
expr_stmt|;
comment|//set metas
name|meta
operator|=
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|VALUE2
argument_list|)
expr_stmt|;
name|key3UUID
operator|=
name|meta
operator|.
name|getUUID
argument_list|()
expr_stmt|;
name|SystemExport
name|sysexport
init|=
operator|new
name|SystemExport
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|file
operator|=
name|sysexport
operator|.
name|export
argument_list|(
literal|"backup"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|clean
argument_list|()
expr_stmt|;
name|SystemImport
name|restore
init|=
operator|new
name|SystemImport
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|RestoreListener
name|listener
init|=
operator|new
name|DefaultRestoreListener
argument_list|()
decl_stmt|;
name|restore
operator|.
name|restore
argument_list|(
name|listener
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
name|file
argument_list|,
literal|"xmldb:exist://"
argument_list|)
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Metas
name|docMD
init|=
name|md
operator|.
name|getMetas
argument_list|(
name|doc1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docMD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|docUUID
argument_list|,
name|docMD
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|Meta
name|meta
init|=
name|docMD
operator|.
name|get
argument_list|(
name|KEY1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|VALUE1
argument_list|,
name|meta
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key1UUID
argument_list|,
name|meta
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|meta
operator|=
name|docMD
operator|.
name|get
argument_list|(
name|KEY2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|VALUE2
argument_list|,
name|meta
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key2UUID
argument_list|,
name|meta
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|//binary
name|docMD
operator|=
name|MetaData
operator|.
name|get
argument_list|()
operator|.
name|getMetas
argument_list|(
name|doc2uri
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|docMD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc2UUID
argument_list|,
name|docMD
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|meta
operator|=
name|docMD
operator|.
name|get
argument_list|(
name|KEY1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|VALUE2
argument_list|,
name|meta
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|key3UUID
argument_list|,
name|meta
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|DocumentImpl
name|getDoc
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|col
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|DocumentImpl
name|doc
init|=
name|col
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
comment|//@BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|transaction
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|confFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"conf.xml"
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|confFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getPluginsManager
argument_list|()
operator|.
name|addPlugin
argument_list|(
literal|"org.exist.storage.md.Plugin"
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
name|transaction
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|CollectionConfigurationManager
name|mgr
init|=
name|pool
operator|.
name|getConfigurationManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addConfiguration
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|root
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"store "
operator|+
name|doc1uri
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc1uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|root
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"store "
operator|+
name|doc2uri
argument_list|)
expr_stmt|;
name|BinaryDocument
name|doc
init|=
name|root
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc2uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|BINARY
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|rundb
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|rundb
parameter_list|()
block|{
try|try
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|//@AfterClass
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|clean
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pool
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"stoped"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|clean
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CLEANING..."
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|transaction
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
name|transaction
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CLEANED."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

