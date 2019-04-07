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
name|indexing
operator|.
name|lucene
package|;
end_package

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
name|ThreadFactory
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
name|EXistXQueryService
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
name|IndexQueryService
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
name|BeforeClass
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
name|modules
operator|.
name|XMLResource
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
name|XUpdateQueryService
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

begin_class
specifier|public
class|class
name|ConcurrencyTest
block|{
specifier|private
specifier|static
specifier|final
name|long
name|TIMEOUT_TERMINATION
init|=
literal|1000
operator|*
literal|60
operator|*
literal|3
decl_stmt|;
comment|// 3 minutes (in milliseconds)
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
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|int
name|CONCURRENT_THREADS
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|*
literal|3
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|test
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_CONFIG1
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<lucene>"
operator|+
literal|"<text qname=\"LINE\"/>"
operator|+
literal|"<text qname=\"SPEAKER\"/>"
operator|+
literal|"</lucene>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|store
parameter_list|()
block|{
specifier|final
name|ExecutorService
name|executor
init|=
name|newFixedThreadPool
argument_list|(
name|CONCURRENT_THREADS
argument_list|,
literal|"store"
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
name|CONCURRENT_THREADS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|name
init|=
literal|"store-thread-"
operator|+
name|i
decl_stmt|;
specifier|final
name|Runnable
name|run
init|=
parameter_list|()
lambda|->
block|{
try|try
block|{
name|storeRemoveDocs
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
decl||
name|IOException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
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
block|}
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
name|run
argument_list|)
expr_stmt|;
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
name|TIMEOUT_TERMINATION
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//Nothing to do
block|}
name|assertTrue
argument_list|(
name|terminated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|update
parameter_list|()
block|{
specifier|final
name|ExecutorService
name|executor
init|=
name|newFixedThreadPool
argument_list|(
name|CONCURRENT_THREADS
argument_list|,
literal|"update"
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
name|CONCURRENT_THREADS
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|name
init|=
literal|"update-thread"
operator|+
name|i
decl_stmt|;
name|Runnable
name|run
init|=
parameter_list|()
lambda|->
block|{
try|try
block|{
name|xupdateDocs
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
decl||
name|IOException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
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
block|}
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
name|run
argument_list|)
expr_stmt|;
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
name|TIMEOUT_TERMINATION
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//Nothing to do
block|}
name|assertTrue
argument_list|(
name|terminated
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ExecutorService
name|newFixedThreadPool
parameter_list|(
specifier|final
name|int
name|nThreads
parameter_list|,
specifier|final
name|String
name|threadsBaseName
parameter_list|)
block|{
return|return
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|nThreads
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
specifier|final
name|Runnable
name|r
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
name|threadsBaseName
operator|+
literal|"-"
operator|+
name|counter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|void
name|storeRemoveDocs
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|storeDocs
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
specifier|final
name|EXistXQueryService
name|xqs
init|=
operator|(
name|EXistXQueryService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(LINE, 'king')]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|98
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(SPEAKER, 'juliet')]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|118
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|resources
init|=
name|test
operator|.
name|listResources
argument_list|()
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
name|resources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Resource
name|resource
init|=
name|test
operator|.
name|getResource
argument_list|(
name|resources
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|test
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(LINE, 'king')]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(SPEAKER, 'juliet')]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|xupdateDocs
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|storeDocs
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
specifier|final
name|EXistXQueryService
name|xqs
init|=
operator|(
name|EXistXQueryService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(SPEAKER, 'juliet')]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|118
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|xupdate
init|=
name|LuceneIndexTest
operator|.
name|XUPDATE_START
operator|+
literal|"<xu:remove select=\"//SPEECH[ft:query(SPEAKER, 'juliet')]\"/>"
operator|+
name|LuceneIndexTest
operator|.
name|XUPDATE_END
decl_stmt|;
specifier|final
name|XUpdateQueryService
name|xuqs
init|=
operator|(
name|XUpdateQueryService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|xuqs
operator|.
name|update
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
name|result
operator|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(SPEAKER, 'juliet')]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|xqs
operator|.
name|query
argument_list|(
literal|"//SPEECH[ft:query(LINE, 'king')]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|98
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeDocs
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|existEmbeddedServer
operator|.
name|createCollection
argument_list|(
name|test
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
specifier|final
name|IndexQueryService
name|iqs
init|=
operator|(
name|IndexQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|iqs
operator|.
name|configureCollection
argument_list|(
name|COLLECTION_CONFIG1
argument_list|)
expr_stmt|;
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
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
name|sampleName
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getShakespeareSample
argument_list|(
name|sampleName
argument_list|)
init|)
block|{
name|resource
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
block|}
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
block|{
name|test
operator|=
name|existEmbeddedServer
operator|.
name|createCollection
argument_list|(
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|LockException
throws|,
name|TriggerException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
block|{
name|test
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

