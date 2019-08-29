begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|EXistException
import|;
end_import

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
name|InputStreamUtil
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
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SystemExportFiltersTest
block|{
specifier|private
specifier|static
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
name|String
name|XML1
init|=
literal|"<test attr=\"test\"/>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML1_BACKUP
init|=
literal|"<test attr=\"test\">test</test>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
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
specifier|final
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
specifier|final
name|String
name|XML3
init|=
literal|"<!DOCTYPE html><html></html>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML3_PROPER
init|=
literal|"<!DOCTYPE html>\n<html/>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BINARY
init|=
literal|"test"
decl_stmt|;
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
name|BeforeClass
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
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
block|{
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
name|txn
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
name|createCollection
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
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
name|txn
argument_list|,
name|broker
argument_list|,
name|test
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|storeXMLDocument
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|test
argument_list|,
name|doc01uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML1
argument_list|)
expr_stmt|;
name|storeXMLDocument
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|test
argument_list|,
name|doc02uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|storeXMLDocument
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|test
argument_list|,
name|doc03uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML3
argument_list|)
expr_stmt|;
name|test
operator|.
name|addBinaryResource
argument_list|(
name|txn
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
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|exportImport
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
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
name|List
argument_list|<
name|String
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|FilterForBackup
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|SystemExport
operator|.
name|CONFIG_FILTERS
argument_list|,
name|filters
argument_list|)
expr_stmt|;
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
name|boolean
name|direct
init|=
literal|true
decl_stmt|;
specifier|final
name|SystemExport
name|sysexport
init|=
operator|new
name|SystemExport
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|direct
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|backupDir
init|=
name|tempFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|file
operator|=
name|sysexport
operator|.
name|export
argument_list|(
name|backupDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
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
name|TestUtils
operator|.
name|cleanupDB
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
name|file
argument_list|,
name|listener
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
name|XML1_BACKUP
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
name|doc
operator|=
name|getDoc
argument_list|(
name|broker
argument_list|,
name|test
argument_list|,
name|doc11uri
operator|.
name|lastSegment
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|instanceof
name|BinaryDocument
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|transaction
argument_list|,
operator|(
operator|(
name|BinaryDocument
operator|)
name|doc
operator|)
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
name|BINARY
argument_list|,
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
block|}
name|transaction
operator|.
name|commit
argument_list|()
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
name|getCurrentSubject
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
specifier|private
specifier|static
name|Collection
name|createCollection
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|Collection
name|col
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
return|return
name|col
return|;
block|}
specifier|private
specifier|static
name|DocumentImpl
name|storeXMLDocument
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|col
parameter_list|,
name|XmldbURI
name|name
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|LockException
throws|,
name|SAXException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
block|{
name|IndexInfo
name|info
init|=
name|col
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|name
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|col
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|info
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
end_class

end_unit

