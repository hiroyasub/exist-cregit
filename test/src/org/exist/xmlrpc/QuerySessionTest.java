begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|TestDataGenerator
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
name|Assert
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
name|*
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
name|XQueryService
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
name|util
operator|.
name|Random
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

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|QuerySessionTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|generateXQ
init|=
literal|"<book id=\"{$filename}\" n=\"{$count}\">"
operator|+
literal|"<chapter xml:id=\"chapter{$count}\">"
operator|+
literal|"<title>{pt:random-text(7)}</title>"
operator|+
literal|"       {"
operator|+
literal|"           for $section in 1 to 8 return"
operator|+
literal|"<section id=\"sect{$section}\">"
operator|+
literal|"<title>{pt:random-text(7)}</title>"
operator|+
literal|"                   {"
operator|+
literal|"                       for $para in 1 to 10 return"
operator|+
literal|"<para>{pt:random-text(40)}</para>"
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
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<fulltext default=\"all\" attributes=\"false\"/>"
operator|+
literal|"<create qname=\"@xml:id\" type=\"xs:string\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|QUERY
init|=
literal|"declare variable $n external;"
operator|+
literal|"//chapter[@xml:id = $n]"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|baseURI
init|=
literal|"xmldb:exist://localhost:8088/xmlrpc"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|N_THREADS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|DOC_COUNT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
name|JettyStart
name|server
decl_stmt|;
specifier|private
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|manualRelease
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|test
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
literal|"/db/rpctest"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
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
name|service
operator|.
name|query
argument_list|(
literal|"//chapter[@xml:id = 'chapter1']"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// clear should release the query result on the server
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// the result has been cleared already. we should get an exception here
name|Resource
name|members
init|=
name|result
operator|.
name|getMembersAsResource
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"members: "
operator|+
name|members
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
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
name|QUERY
argument_list|)
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
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|terminated
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|QueryTask
implements|implements
name|Runnable
block|{
specifier|private
name|String
name|query
decl_stmt|;
specifier|private
name|QueryTask
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Collection
name|test
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
literal|"/db/rpctest"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
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
name|int
name|n
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|DOC_COUNT
argument_list|)
operator|+
literal|1
decl_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"n"
argument_list|,
literal|"chapter"
operator|+
name|n
argument_list|)
expr_stmt|;
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|Assert
operator|.
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startServer
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|=
operator|new
name|JettyStart
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for server to start..."
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// initialize XML:DB driver
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|mgmt
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
name|Collection
name|test
init|=
name|mgmt
operator|.
name|createCollection
argument_list|(
literal|"rpctest"
argument_list|)
decl_stmt|;
name|IndexQueryService
name|idxs
init|=
operator|(
name|IndexQueryService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|idxs
operator|.
name|configureCollection
argument_list|(
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|createResource
argument_list|(
literal|"strings.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
operator|new
name|File
argument_list|(
literal|"samples/shakespeare/macbeth.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|test
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|TestDataGenerator
name|generator
init|=
operator|new
name|TestDataGenerator
argument_list|(
literal|"xdb"
argument_list|,
name|DOC_COUNT
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|generator
operator|.
name|generate
argument_list|(
name|test
argument_list|,
name|generateXQ
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|resource
operator|=
name|test
operator|.
name|createResource
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|test
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|stopServer
parameter_list|()
block|{
comment|//waiting to finish 10s
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
block|}
try|try
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|mgmt
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
name|mgmt
operator|.
name|removeCollection
argument_list|(
literal|"rpctest"
argument_list|)
expr_stmt|;
name|Collection
name|config
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseURI
operator|+
literal|"/db/system/config/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|mgmt
operator|=
operator|(
name|CollectionManagementService
operator|)
name|config
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|mgmt
operator|.
name|removeCollection
argument_list|(
literal|"rpctest"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

