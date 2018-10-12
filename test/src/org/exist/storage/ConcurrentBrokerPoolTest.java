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
name|LockedDocument
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
name|journal
operator|.
name|Journal
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
name|List
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
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

begin_comment
comment|/**  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentBrokerPoolTest
block|{
specifier|private
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"concurrentBrokerPoolTest"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|threadNum
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|MAX_CONCURRENT_THREADS
init|=
literal|6
decl_stmt|;
comment|/**      * Tests storing documents across multiple db instances within the same JVM in parallel.      *      * Creates n tasks which are distributed over {@code MAX_CONCURRENT_THREADS} threads.      *      * Within the same JVM, each task:      *   1. Gets a new BrokerPool instance from the global BrokerPools      *   2. With the BrokerPool instance:      *     2.1 starts the instance      *     2.2 stores a document into the instance's /db collection      *     2.2 stops the instance      *   3. Returns the instance to the global BrokerPools      */
annotation|@
name|Test
specifier|public
name|void
name|multiInstanceStore
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
specifier|final
name|ThreadFactory
name|threadFactory
init|=
name|runnable
lambda|->
operator|new
name|Thread
argument_list|(
name|threadGroup
argument_list|,
name|runnable
argument_list|,
literal|"leaseStoreRelease-"
operator|+
name|threadNum
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|MAX_CONCURRENT_THREADS
argument_list|,
name|threadFactory
argument_list|)
decl_stmt|;
comment|// the number of instances to use
specifier|final
name|int
name|instances
init|=
literal|20
decl_stmt|;
comment|// setup store data tasks
specifier|final
name|List
argument_list|<
name|Callable
argument_list|<
name|Tuple2
argument_list|<
name|Path
argument_list|,
name|UUID
argument_list|>
argument_list|>
argument_list|>
name|tasks
init|=
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|instances
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
operator|new
name|StoreInstance
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
comment|// store data
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|Tuple2
argument_list|<
name|Path
argument_list|,
name|UUID
argument_list|>
argument_list|>
argument_list|>
name|futures
init|=
name|executorService
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// validate stored data
for|for
control|(
specifier|final
name|Future
argument_list|<
name|Tuple2
argument_list|<
name|Path
argument_list|,
name|UUID
argument_list|>
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|validateStoredDoc
argument_list|(
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|validateStoredDoc
parameter_list|(
specifier|final
name|Tuple2
argument_list|<
name|Path
argument_list|,
name|UUID
argument_list|>
name|pathUuid
parameter_list|)
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|Path
name|dataDir
init|=
name|pathUuid
operator|.
name|_1
decl_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|dataDir
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|UUID
name|uuid
init|=
name|pathUuid
operator|.
name|_2
decl_stmt|;
specifier|final
name|Properties
name|config
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
name|Journal
operator|.
name|PROPERTY_RECOVERY_JOURNAL_DIR
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
specifier|final
name|ExistEmbeddedServer
name|server
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|"validate-"
operator|+
name|uuid
operator|.
name|toString
argument_list|()
argument_list|,
name|getConfigFile
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
name|config
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|server
operator|.
name|startDb
argument_list|()
expr_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|server
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|LockedDocument
name|doc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|DB
operator|.
name|append
argument_list|(
name|docName
argument_list|(
name|uuid
argument_list|)
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|doc
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
name|docContent
argument_list|(
name|uuid
argument_list|)
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
name|fromNode
argument_list|(
name|doc
operator|.
name|getDocument
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
name|expected
argument_list|)
operator|.
name|withTest
argument_list|(
name|actual
argument_list|)
operator|.
name|checkForSimilar
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// ASSERT
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
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|stopDb
argument_list|()
expr_stmt|;
comment|// clear temp files
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|XmldbURI
name|docName
parameter_list|(
specifier|final
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|XmldbURI
operator|.
name|create
argument_list|(
name|uuid
operator|.
name|toString
argument_list|()
operator|+
literal|".xml"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|docContent
parameter_list|(
specifier|final
name|UUID
name|uuid
parameter_list|)
block|{
return|return
literal|"<uuid>"
operator|+
name|uuid
operator|.
name|toString
argument_list|()
operator|+
literal|"</uuid>"
return|;
block|}
specifier|private
specifier|static
name|Path
name|getConfigFile
parameter_list|(
specifier|final
name|Class
name|instance
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|instance
operator|.
name|getResource
argument_list|(
literal|"ConcurrentBrokerPoolTest.conf.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|StoreInstance
implements|implements
name|Callable
argument_list|<
name|Tuple2
argument_list|<
name|Path
argument_list|,
name|UUID
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Tuple2
argument_list|<
name|Path
argument_list|,
name|UUID
argument_list|>
name|call
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ExistEmbeddedServer
name|server
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|"store-"
operator|+
name|uuid
operator|.
name|toString
argument_list|()
argument_list|,
name|getConfigFile
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|server
operator|.
name|startDb
argument_list|()
expr_stmt|;
try|try
block|{
name|store
argument_list|(
name|server
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Tuple
argument_list|(
name|server
operator|.
name|getTemporaryStorage
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|uuid
argument_list|)
return|;
block|}
finally|finally
block|{
name|server
operator|.
name|stopDb
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// NOTE: false flag ensures we don't delete the temporary storage!
block|}
block|}
specifier|private
name|void
name|store
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|IOException
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|XmldbURI
operator|.
name|DB
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
specifier|final
name|String
name|docContent
init|=
name|docContent
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|indexInfo
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|(
name|uuid
argument_list|)
argument_list|,
name|docContent
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|docContent
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

