begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|TestDataGenerator
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
name|IndexInfo
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
name|EXistXPathQueryService
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
name|ResourceSet
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
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_comment
comment|/**  * Test deadlock detection and resolution.  *   * @author wolf  */
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
name|DeadlockTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DeadlockTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** pick a set of random collections to query */
specifier|private
specifier|static
specifier|final
name|int
name|TEST_RANDOM_COLLECTION
init|=
literal|0
decl_stmt|;
comment|/** pick a single collection to query */
specifier|private
specifier|static
specifier|final
name|int
name|TEST_SINGLE_COLLECTION
init|=
literal|1
decl_stmt|;
comment|/** query the root collection */
specifier|private
specifier|static
specifier|final
name|int
name|TEST_ALL_COLLECTIONS
init|=
literal|2
decl_stmt|;
comment|/** query a single document */
specifier|private
specifier|static
specifier|final
name|int
name|TEST_SINGLE_DOC
init|=
literal|3
decl_stmt|;
comment|/** apply a random mixture of the other modes */
specifier|private
specifier|static
specifier|final
name|int
name|TEST_MIXED
init|=
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|TEST_REMOVE
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DELAY
init|=
literal|7000
decl_stmt|;
comment|/** Use 4 test runs, querying different collections */
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
literal|"testRandomCollection"
block|,
name|TEST_RANDOM_COLLECTION
block|}
block|,
block|{
literal|"testSingleCollection"
block|,
name|TEST_SINGLE_COLLECTION
block|}
block|,
block|{
literal|"testAllCollections"
block|,
name|TEST_ALL_COLLECTIONS
block|}
block|,
block|{
literal|"testSingleDoc"
block|,
name|TEST_SINGLE_DOC
block|}
block|,
block|{
literal|"testMixed"
block|,
name|TEST_MIXED
block|}
block|,
block|{
literal|"testRemoved"
block|,
name|TEST_REMOVE
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|int
name|COLL_COUNT
init|=
literal|20
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|QUERY_COUNT
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DOC_COUNT
init|=
literal|70
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|REMOVE_COUNT
init|=
literal|50
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|N_THREADS
init|=
literal|40
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|generateXQ
init|=
literal|"declare function local:random-sequence($length as xs:integer, $G as map(xs:string, item())) {\n"
operator|+
literal|"  if ($length eq 0)\n"
operator|+
literal|"  then ()\n"
operator|+
literal|"  else ($G?number, local:random-sequence($length - 1, $G?next()))\n"
operator|+
literal|"};\n"
operator|+
literal|"let $rnd := fn:random-number-generator() return"
operator|+
literal|"<book id=\"{$filename}\" n=\"{$count}\">"
operator|+
literal|"<chapter xml:id=\"chapter{$count}\">"
operator|+
literal|"<title>{local:random-sequence(7, $rnd)}</title>"
operator|+
literal|"       {"
operator|+
literal|"           for $section in 1 to 8 return"
operator|+
literal|"<section id=\"sect{$section}\">"
operator|+
literal|"<title>{local:random-sequence(7, $rnd)}</title>"
operator|+
literal|"                   {"
operator|+
literal|"                       for $para in 1 to 10 return"
operator|+
literal|"<para>{local:random-sequence(120, $rnd)}</para>"
operator|+
literal|"                   }"
operator|+
literal|"</section>"
operator|+
literal|"       }"
operator|+
literal|"</chapter>"
operator|+
literal|"</book>"
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
annotation|@
name|Parameter
specifier|public
name|String
name|testName
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
specifier|public
name|int
name|mode
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
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
name|BeforeClass
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
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
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
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
comment|// initialize XML:DB driver
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
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
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|clearDB
parameter_list|()
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
literal|"xmldb:exist:///db/test"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
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
name|service
operator|.
name|removeCollection
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|runTasks
parameter_list|()
block|{
specifier|final
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|N_THREADS
argument_list|)
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|StoreTask
argument_list|(
literal|"store"
argument_list|,
name|COLL_COUNT
argument_list|,
name|DOC_COUNT
argument_list|)
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
name|DELAY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|QUERY_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|QueryTask
argument_list|(
name|COLL_COUNT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mode
operator|==
name|TEST_REMOVE
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|REMOVE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|RemoveDocumentTask
argument_list|(
name|COLL_COUNT
argument_list|,
name|DOC_COUNT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|terminated
init|=
literal|false
decl_stmt|;
try|try
block|{
name|terminated
operator|=
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|60
operator|*
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|assertTrue
argument_list|(
name|terminated
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|StoreTask
implements|implements
name|Runnable
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|int
name|docCount
decl_stmt|;
specifier|private
specifier|final
name|int
name|collectionCount
decl_stmt|;
specifier|public
name|StoreTask
parameter_list|(
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|int
name|collectionCount
parameter_list|,
specifier|final
name|int
name|docCount
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|collectionCount
operator|=
name|collectionCount
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
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
specifier|final
name|TestDataGenerator
name|generator
init|=
operator|new
name|TestDataGenerator
argument_list|(
literal|"xdb"
argument_list|,
name|docCount
argument_list|)
decl_stmt|;
name|Collection
name|coll
decl_stmt|;
name|int
name|fileCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collectionCount
condition|;
name|i
operator|++
control|)
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
name|coll
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|coll
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
name|Path
index|[]
name|files
init|=
name|generator
operator|.
name|generate
argument_list|(
name|broker
argument_list|,
name|coll
argument_list|,
name|generateXQ
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
operator|,
name|fileCount
operator|++
control|)
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
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|coll
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
literal|"test"
operator|+
name|fileCount
operator|+
literal|".xml"
argument_list|)
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|coll
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|is
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
name|generator
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//				fail(e.getMessage());
block|}
block|}
block|}
specifier|private
class|class
name|QueryTask
implements|implements
name|Runnable
block|{
specifier|private
name|int
name|collectionCount
decl_stmt|;
specifier|public
name|QueryTask
parameter_list|(
name|int
name|collectionCount
parameter_list|)
block|{
name|this
operator|.
name|collectionCount
operator|=
name|collectionCount
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|collection
init|=
literal|"/db"
decl_stmt|;
name|int
name|currentMode
init|=
name|mode
decl_stmt|;
if|if
condition|(
name|mode
operator|==
name|TEST_MIXED
operator|||
name|currentMode
operator|==
name|TEST_REMOVE
condition|)
name|currentMode
operator|=
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentMode
operator|==
name|TEST_SINGLE_COLLECTION
condition|)
block|{
name|int
name|collectionId
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|collectionCount
argument_list|)
decl_stmt|;
name|collection
operator|=
literal|"/db/test/"
operator|+
name|collectionId
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"collection('"
argument_list|)
operator|.
name|append
argument_list|(
name|collection
argument_list|)
operator|.
name|append
argument_list|(
literal|"')//chapter/section[@id = 'sect1']"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|currentMode
operator|==
name|TEST_RANDOM_COLLECTION
condition|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|collIds
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|int
name|r
decl_stmt|;
do|do
block|{
name|r
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|collectionCount
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|collIds
operator|.
name|contains
argument_list|(
name|r
argument_list|)
condition|)
do|;
name|collIds
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"collection('/db/test/"
argument_list|)
operator|.
name|append
argument_list|(
name|collIds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")//chapter/section[@id = 'sect1']"
argument_list|)
expr_stmt|;
name|collection
operator|=
literal|"/db/test"
expr_stmt|;
block|}
if|else if
condition|(
name|currentMode
operator|==
name|TEST_SINGLE_DOC
condition|)
block|{
name|int
name|collectionId
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|collectionCount
argument_list|)
decl_stmt|;
name|collection
operator|=
literal|"/db/test/"
operator|+
name|collectionId
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"doc('"
argument_list|)
operator|.
name|append
argument_list|(
name|collection
argument_list|)
operator|.
name|append
argument_list|(
literal|"/test1.xml')//chapter/section[@id = 'sect1']"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"//chapter/section[@id = 'sect1']"
argument_list|)
expr_stmt|;
block|}
name|String
name|query
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|collection
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|testCollection
operator|==
literal|null
condition|)
return|return;
name|EXistXPathQueryService
name|service
init|=
operator|(
name|EXistXPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|beginProtected
argument_list|()
expr_stmt|;
try|try
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|result
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|endProtected
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
block|}
block|}
specifier|private
class|class
name|RemoveDocumentTask
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|int
name|collectionCount
decl_stmt|;
specifier|private
specifier|final
name|int
name|documentCount
decl_stmt|;
specifier|public
name|RemoveDocumentTask
parameter_list|(
specifier|final
name|int
name|collectionCount
parameter_list|,
specifier|final
name|int
name|documentCount
parameter_list|)
block|{
name|this
operator|.
name|collectionCount
operator|=
name|collectionCount
expr_stmt|;
name|this
operator|.
name|documentCount
operator|=
name|documentCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
do|do
block|{
specifier|final
name|int
name|collectionId
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|collectionCount
argument_list|)
decl_stmt|;
specifier|final
name|String
name|collection
init|=
literal|"/db/test/"
operator|+
name|collectionId
decl_stmt|;
specifier|final
name|int
name|docId
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|documentCount
argument_list|)
operator|*
name|collectionId
decl_stmt|;
specifier|final
name|String
name|document
init|=
literal|"test"
operator|+
name|docId
operator|+
literal|".xml"
decl_stmt|;
try|try
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
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|collection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|resource
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
name|document
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|testCollection
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
block|}
do|while
condition|(
operator|!
name|removed
condition|)
do|;
block|}
block|}
block|}
end_class

end_unit

