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
name|jetty
operator|.
name|JettyStart
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Callable
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
name|CompletionService
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
name|ExecutionException
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
name|ExecutorCompletionService
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
comment|/**  * Test for deadlocks when moving resources from one collection to another. Uses  * two threads: one stores a document, then moves it to another collection.  * Based on XML-RPC. The second thread tries to execute a query via REST.  *  * Due to the complex move task, threads will deadlock almost immediately if  * something's wrong with collection locking.  */
end_comment

begin_class
specifier|public
class|class
name|MoveResourceTest
block|{
specifier|private
name|JettyStart
name|server
decl_stmt|;
comment|// jetty.port.standalone
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"http://localhost:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|,
literal|"8088"
argument_list|)
operator|+
literal|"/xmlrpc"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REST_URI
init|=
literal|"http://localhost:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|,
literal|"8088"
argument_list|)
decl_stmt|;
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
name|Void
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
specifier|final
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CompletionService
argument_list|<
name|Void
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
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|task
parameter_list|)
lambda|->
block|{
name|cs
operator|.
name|submit
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
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
name|cs
operator|.
name|take
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
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
name|IOException
throws|,
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
name|String
name|readData
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/shakespeare/r_and_j.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
name|char
index|[]
name|ch
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|ch
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
class|class
name|MoveThread
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|XmlRpcException
throws|,
name|InterruptedException
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
name|getClient
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
name|readData
argument_list|()
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
literal|250
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
literal|null
return|;
block|}
block|}
specifier|private
class|class
name|CheckThread
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|String
name|reqUrl
init|=
name|REST_URI
operator|+
literal|"/db?_query="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
literal|"collection('/db')//SPEECH[SPEAKER = 'JULIET']"
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
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|reqUrl
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|int
name|r
init|=
name|connect
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Server returned response code "
operator|+
name|r
argument_list|,
literal|200
argument_list|,
name|r
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|connect
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|readResponse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|readResponse
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
comment|//Don't worry about closing the server : the shutdownDB hook will do the job
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|new
name|JettyStart
argument_list|()
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|XmlRpcClient
name|getClient
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|XmlRpcClient
name|client
init|=
operator|new
name|XmlRpcClient
argument_list|()
decl_stmt|;
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
name|URI
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicUserName
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicPassword
argument_list|(
literal|""
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

