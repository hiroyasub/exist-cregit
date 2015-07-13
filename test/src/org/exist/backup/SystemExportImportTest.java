begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|OutputKeys
import|;
end_import

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
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|LogRestoreListener
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
name|CollectionConfigurationException
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
name|dom
operator|.
name|persistent
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializers
operator|.
name|Serializer
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
name|util
operator|.
name|DatabaseConfigurationException
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
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameter
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
operator|.
name|Parameters
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
name|Database
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

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
name|SystemExportImportTest
block|{
annotation|@
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
literal|"direct"
block|,
literal|true
block|}
block|,
block|{
literal|"non-direct"
block|,
literal|false
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Parameter
specifier|public
name|String
name|apiName
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
specifier|public
name|boolean
name|direct
decl_stmt|;
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
name|doc01uri
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test1.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|doc02uri
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test2.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|doc03uri
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test3.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|doc11uri
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test.binary"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML1
init|=
literal|"<test attr=\"test\"/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML2
init|=
literal|"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n"
operator|+
literal|"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
operator|+
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\"></html>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML2_PROPER
init|=
literal|"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
operator|+
literal|"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
operator|+
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\"/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML3
init|=
literal|"<!DOCTYPE html><html></html>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML3_PROPER
init|=
literal|"<!DOCTYPE html>\n<html/>"
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
name|BrokerPool
name|pool
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|exportImport
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|Collection
name|test
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|)
expr_stmt|;
specifier|final
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
name|direct
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
name|clean
argument_list|()
expr_stmt|;
specifier|final
name|SystemImport
name|restore
init|=
operator|new
name|SystemImport
argument_list|(
name|pool
argument_list|)
decl_stmt|;
specifier|final
name|RestoreListener
name|listener
init|=
operator|new
name|LogRestoreListener
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|Collection
name|test
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|getDoc
argument_list|(
name|broker
argument_list|,
name|test
argument_list|,
name|doc01uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|XML1
argument_list|,
name|serializer
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|getDoc
argument_list|(
name|broker
argument_list|,
name|test
argument_list|,
name|doc02uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XML2_PROPER
argument_list|,
name|serializer
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|getDoc
argument_list|(
name|broker
argument_list|,
name|test
argument_list|,
name|doc03uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XML3_PROPER
argument_list|,
name|serializer
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|DocumentImpl
name|getDoc
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Collection
name|col
parameter_list|,
specifier|final
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
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
specifier|private
specifier|final
specifier|static
name|Properties
name|contentsOutputProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|contentsOutputProps
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|contentsOutputProps
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|OUTPUT_DOCTYPE
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|serializer
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setUser
argument_list|(
name|broker
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|contentsOutputProps
argument_list|)
expr_stmt|;
return|return
name|serializer
operator|.
name|serialize
argument_list|(
name|document
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|startDB
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|CollectionConfigurationException
throws|,
name|LockException
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
block|{
specifier|final
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
specifier|final
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
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
specifier|final
name|Collection
name|test
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
specifier|final
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
name|test
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|test
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc01uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|info
operator|=
name|test
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc02uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|info
operator|=
name|test
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc03uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML3
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|test
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc11uri
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
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|rundb
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|rundb
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
block|{
specifier|final
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
specifier|final
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
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|EXistException
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
block|}
specifier|private
name|void
name|clean
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|EXistException
block|{
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
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
specifier|final
name|Collection
name|test
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

