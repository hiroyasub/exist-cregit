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
name|Test
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
specifier|public
class|class
name|SystemExportImportTest
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
name|doc01uri
init|=
name|col1uri
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
name|col1uri
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
name|col1uri
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
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
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
comment|//check that it clean
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
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col1uri
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|col
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
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|getDoc
argument_list|(
name|broker
argument_list|,
name|col
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
name|col
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
name|col
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
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
specifier|public
name|Properties
name|contentsOutputProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
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
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|SAXException
block|{
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
comment|//@BeforeClass
specifier|public
specifier|static
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
name|doc01uri
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
name|XML1
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
name|doc02uri
argument_list|)
expr_stmt|;
name|info
operator|=
name|root
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
name|XML2
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
name|doc03uri
argument_list|)
expr_stmt|;
name|info
operator|=
name|root
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
name|XML3
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
name|doc11uri
argument_list|)
expr_stmt|;
name|root
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
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|commit
argument_list|(
name|transaction
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
throws|throws
name|ClassNotFoundException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
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
comment|//@AfterClass
specifier|public
specifier|static
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
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|EXistException
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
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|commit
argument_list|(
name|transaction
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

