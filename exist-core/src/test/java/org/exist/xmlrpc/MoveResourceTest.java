begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClientConfigImpl
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
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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
name|HashMap
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
name|Map
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
name|samples
operator|.
name|Samples
operator|.
name|SAMPLES
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
comment|/**  * Test for deadlocks when moving resources from one collection to another. Uses  * two threads: one stores a document, then moves it to another collection.  * Based on XML-RPC. The second thread tries to execute a query via REST.  *  * Due to the complex move task, threads will deadlock almost immediately if  * something's wrong with collection locking.  */
end_comment

begin_class
specifier|public
class|class
name|MoveResourceTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DELAY
init|=
literal|50
decl_stmt|;
comment|// milliseconds
specifier|private
specifier|static
specifier|final
name|long
name|TIMEOUT
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// milliseconds
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
name|String
name|getXmlRpcUri
parameter_list|()
block|{
return|return
literal|"http://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/xmlrpc"
return|;
block|}
specifier|private
specifier|static
name|String
name|getRestUri
parameter_list|()
block|{
return|return
literal|"http://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMove
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
specifier|final
name|List
argument_list|<
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|MoveThread
argument_list|()
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|CheckThread
argument_list|()
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|CheckThread
argument_list|()
argument_list|)
expr_stmt|;
name|ExecutorService
name|service
init|=
literal|null
decl_stmt|;
try|try
block|{
name|service
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|tasks
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|final
name|AtomicInteger
name|id
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
literal|"MoveResourceTest.testMove-"
operator|+
name|id
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|CompletionService
argument_list|<
name|Boolean
argument_list|>
name|cs
init|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|forEach
argument_list|(
name|cs
operator|::
name|submit
argument_list|)
expr_stmt|;
comment|//wait for all tasks to complete
specifier|final
name|int
name|n
init|=
name|tasks
operator|.
name|size
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Boolean
name|result
init|=
name|cs
operator|.
name|poll
argument_list|(
name|TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|createCollection
parameter_list|(
name|XmlRpcClient
name|client
parameter_list|,
name|XmldbURI
name|collection
parameter_list|)
throws|throws
name|XmlRpcException
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collection
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|result
init|=
operator|(
name|Boolean
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|MoveThread
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|XmlRpcException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|romeoAndJuliet
init|=
name|readSample
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|XmldbURI
name|sourceColl
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"source"
operator|+
name|i
argument_list|)
decl_stmt|;
name|XmldbURI
name|targetColl1
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|XmldbURI
name|targetColl2
init|=
name|targetColl1
operator|.
name|append
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|XmldbURI
name|sourceResource
init|=
name|sourceColl
operator|.
name|append
argument_list|(
literal|"source.xml"
argument_list|)
decl_stmt|;
name|XmldbURI
name|targetResource
init|=
name|targetColl2
operator|.
name|append
argument_list|(
literal|"copied.xml"
argument_list|)
decl_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getXmlRpcClient
argument_list|()
decl_stmt|;
name|createCollection
argument_list|(
name|xmlrpc
argument_list|,
name|sourceColl
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
name|xmlrpc
argument_list|,
name|targetColl1
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
name|xmlrpc
argument_list|,
name|targetColl2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|romeoAndJuliet
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|sourceResource
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Boolean
name|result
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|sourceResource
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|targetColl2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"copied.xml"
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"moveResource"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"encoding"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"expand-xincludes"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"process-xsl-pi"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|targetResource
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"getDocument"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|data
operator|!=
literal|null
operator|&&
name|data
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
name|DELAY
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|sourceColl
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"removeCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|targetColl1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"removeCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|String
name|readSample
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getRomeoAndJulietSample
argument_list|()
init|)
block|{
return|return
name|InputStreamUtil
operator|.
name|readString
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
class|class
name|CheckThread
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|reqUrl
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/db?_query="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
literal|"collection('/db')//SPEECH[SPEAKER = 'JULIET']"
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
specifier|final
name|Request
name|request
init|=
name|Request
operator|.
name|Get
argument_list|(
name|reqUrl
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|HttpResponse
name|response
init|=
name|request
operator|.
name|execute
argument_list|()
operator|.
name|returnResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
name|DELAY
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|private
specifier|static
name|XmlRpcClient
name|getXmlRpcClient
parameter_list|()
throws|throws
name|MalformedURLException
block|{
specifier|final
name|XmlRpcClient
name|client
init|=
operator|new
name|XmlRpcClient
argument_list|()
decl_stmt|;
specifier|final
name|XmlRpcClientConfigImpl
name|config
init|=
operator|new
name|XmlRpcClientConfigImpl
argument_list|()
decl_stmt|;
name|config
operator|.
name|setEnabledForExtensions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setServerURL
argument_list|(
operator|new
name|URL
argument_list|(
name|getXmlRpcUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicUserName
argument_list|(
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicPassword
argument_list|(
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
expr_stmt|;
name|client
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

