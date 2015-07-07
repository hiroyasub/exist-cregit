begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|DatabaseInstanceManager
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
name|XPathQueryServiceImpl
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
name|AfterClass
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
comment|/**  * Test concurrent access to collections.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrencyTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|N_THREADS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DOC_COUNT
init|=
literal|200
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|QUERY_COUNT
init|=
literal|20
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"/test/c"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REMOVE
init|=
literal|"declare namespace xdb=\"http://exist-db.org/xquery/xmldb\";\n"
operator|+
literal|"declare namespace util=\"http://exist-db.org/xquery/util\";"
operator|+
literal|"declare variable $start external; "
operator|+
literal|"let $dummy := util:log('DEBUG', ('Removing $start: ', $start, ' to ', $start + 9)) "
operator|+
literal|"for $i in $start to $start + 9 "
operator|+
literal|"let $resource := /test[@id = xs:integer($i)] "
operator|+
literal|"return"
operator|+
literal|"   xdb:remove(util:collection-name($resource), util:document-name($resource))"
decl_stmt|;
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
literal|1
init|;
name|i
operator|<=
literal|20
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
name|REMOVE
argument_list|,
name|i
operator|*
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|QUERY_COUNT
condition|;
name|j
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
argument_list|,
literal|0
argument_list|,
literal|true
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
block|}
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
name|String
name|query
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|boolean
name|protect
init|=
literal|false
decl_stmt|;
specifier|private
name|QueryTask
parameter_list|(
name|String
name|query
parameter_list|,
name|int
name|start
parameter_list|,
name|boolean
name|protect
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|protect
operator|=
name|protect
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
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
name|collection
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
name|XPathQueryServiceImpl
name|service
init|=
operator|(
name|XPathQueryServiceImpl
operator|)
name|collection
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
if|if
condition|(
name|start
operator|>
literal|0
condition|)
name|service
operator|.
name|declareVariable
argument_list|(
literal|"start"
argument_list|,
operator|new
name|Integer
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
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
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
parameter_list|()
block|{
comment|// initialize XML:DB driver
try|try
block|{
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
literal|"test"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|DOC_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Resource
name|r
init|=
name|test
operator|.
name|createResource
argument_list|(
literal|"test"
operator|+
name|i
operator|+
literal|".xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|String
name|XML
init|=
literal|"<test id='"
operator|+
name|i
operator|+
literal|"'>"
operator|+
literal|"<a>b</a>"
operator|+
literal|"<c>d</c>"
operator|+
literal|"</test>"
decl_stmt|;
name|r
operator|.
name|setContent
argument_list|(
name|XML
argument_list|)
expr_stmt|;
name|test
operator|.
name|storeResource
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
block|{
try|try
block|{
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
name|CollectionManagementService
name|cmgr
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
name|cmgr
operator|.
name|removeCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
comment|//            Collection configRoot = DatabaseManager.getCollection("xmldb:exist://" + CollectionConfigurationManager.CONFIG_COLLECTION,
comment|//                    "admin", null);
comment|//            cmgr = (CollectionManagementService) configRoot.getService("CollectionManagementService", "1.0");
comment|//            cmgr.removeCollection("db");
name|DatabaseInstanceManager
name|mgr
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|shutdown
argument_list|()
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
end_class

end_unit

