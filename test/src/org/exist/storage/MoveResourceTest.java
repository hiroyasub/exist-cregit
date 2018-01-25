begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Path
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|ExistEmbeddedServer
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
name|DatabaseImpl
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
name|exist
operator|.
name|xmldb
operator|.
name|EXistCollectionManagementService
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
name|AfterClass
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
name|FixMethodOrder
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
name|MethodSorters
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
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
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

begin_class
annotation|@
name|FixMethodOrder
argument_list|(
name|MethodSorters
operator|.
name|NAME_ASCENDING
argument_list|)
specifier|public
class|class
name|MoveResourceTest
block|{
comment|// we don't use @ClassRule/@Rule as we want to force corruption in some tests
specifier|private
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|storeAndRead
parameter_list|()
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
throws|,
name|DatabaseConfigurationException
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDb
argument_list|()
decl_stmt|;
name|store
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|stopDb
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|startDb
argument_list|()
expr_stmt|;
name|read
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeAndReadAborted
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDb
argument_list|()
decl_stmt|;
name|storeAborted
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|stopDb
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|startDb
argument_list|()
expr_stmt|;
name|readAborted
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeAndReadXmldb
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|EXistException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|ClassNotFoundException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDb
argument_list|()
decl_stmt|;
name|xmldbStore
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|stopDb
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|startDb
argument_list|()
expr_stmt|;
name|xmldbRead
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
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
name|transact
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
name|TestConstants
operator|.
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
name|Collection
name|test2
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test2
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|f
init|=
name|TestUtils
operator|.
name|resolveShakespeareSample
argument_list|(
literal|"r_and_j.xml"
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|test2
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|TestConstants
operator|.
name|TEST_XML_URI
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test2
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|test2
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|TestConstants
operator|.
name|TEST_XML_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|broker
operator|.
name|moveResource
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|,
name|test
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test.xml"
argument_list|)
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|read
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|LockException
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
init|)
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
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test/new_test.xml"
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|root
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|registerLock
argument_list|(
name|root
operator|.
name|getLock
argument_list|()
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
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
block|}
block|}
specifier|private
name|void
name|storeAborted
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
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
init|)
block|{
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|test2
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test2
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|f
init|=
name|TestUtils
operator|.
name|resolveShakespeareSample
argument_list|(
literal|"r_and_j.xml"
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|test2
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test2.xml"
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|test2
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
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
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|Collection
name|test2
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|)
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|test2
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test2.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|moveResource
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|,
name|test
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test2.xml"
argument_list|)
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
comment|//NOTE: do not commit the transaction
name|pool
operator|.
name|getJournalManager
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|readAborted
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|LockException
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
init|)
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
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
operator|.
name|append
argument_list|(
literal|"new_test2.xml"
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|root
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|registerLock
argument_list|(
name|root
operator|.
name|getLock
argument_list|()
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
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
block|}
block|}
specifier|private
name|void
name|xmldbStore
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
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
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|EXistCollectionManagementService
name|mgr
init|=
operator|(
name|EXistCollectionManagementService
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|test
init|=
name|root
operator|.
name|getChildCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|==
literal|null
condition|)
block|{
name|test
operator|=
name|mgr
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|test2
init|=
name|test
operator|.
name|getChildCollection
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test2
operator|==
literal|null
condition|)
block|{
name|test2
operator|=
name|mgr
operator|.
name|createCollection
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|f
init|=
name|TestUtils
operator|.
name|resolveShakespeareSample
argument_list|(
literal|"r_and_j.xml"
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|res
init|=
name|test2
operator|.
name|createResource
argument_list|(
literal|"test3.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|test2
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|moveResource
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test2/test3.xml"
argument_list|)
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test3.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|xmldbRead
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|test
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/test"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|res
init|=
name|test
operator|.
name|getResource
argument_list|(
literal|"new_test3.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|res
argument_list|)
expr_stmt|;
specifier|final
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
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
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|EXistCollectionManagementService
name|mgr
init|=
operator|(
name|EXistCollectionManagementService
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
name|mgr
operator|.
name|removeCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerPool
name|startDb
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
throws|,
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
block|{
name|existEmbeddedServer
operator|.
name|startDb
argument_list|()
expr_stmt|;
comment|// initialize driver
specifier|final
name|Database
name|database
init|=
operator|new
name|DatabaseImpl
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
return|return
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopDb
parameter_list|()
block|{
name|existEmbeddedServer
operator|.
name|stopDb
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|TestUtils
operator|.
name|cleanupDataDir
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

