begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
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
name|CollectionManagementServiceImpl
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
name|RemoteCollectionManagementService
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
name|XMLResource
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_class
specifier|public
class|class
name|CopyMoveTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|CopyMoveTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CopyMoveTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCopyResourceChangeName
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|c
init|=
name|setupTestCollection
argument_list|()
decl_stmt|;
try|try
block|{
name|XMLResource
name|original
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|"original"
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|original
operator|.
name|setContent
argument_list|(
literal|"<sample/>"
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|CollectionManagementServiceImpl
name|cms
init|=
operator|(
name|CollectionManagementServiceImpl
operator|)
name|c
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cms
operator|.
name|copyResource
argument_list|(
literal|"original"
argument_list|,
literal|""
argument_list|,
literal|"duplicate"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|c
operator|.
name|getResourceCount
argument_list|()
argument_list|)
expr_stmt|;
name|XMLResource
name|duplicate
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|getResource
argument_list|(
literal|"duplicate"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|duplicate
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|duplicate
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeCollection
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testQueryCopiedResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|c
init|=
name|setupTestCollection
argument_list|()
decl_stmt|;
try|try
block|{
name|XMLResource
name|original
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
literal|"original"
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|original
operator|.
name|setContent
argument_list|(
literal|"<sample/>"
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|CollectionManagementServiceImpl
name|cms
init|=
operator|(
name|CollectionManagementServiceImpl
operator|)
name|c
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cms
operator|.
name|copyResource
argument_list|(
literal|"original"
argument_list|,
literal|""
argument_list|,
literal|"duplicate"
argument_list|)
expr_stmt|;
name|XMLResource
name|duplicate
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|getResource
argument_list|(
literal|"duplicate"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|duplicate
argument_list|)
expr_stmt|;
name|XPathQueryService
name|xq
init|=
operator|(
name|XPathQueryService
operator|)
name|c
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|rs
init|=
name|xq
operator|.
name|queryResource
argument_list|(
literal|"duplicate"
argument_list|,
literal|"/sample"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeCollection
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Collection
name|setupTestCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|rootcms
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
name|c
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
name|c
operator|!=
literal|null
condition|)
name|rootcms
operator|.
name|removeCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|rootcms
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
comment|// initialize driver
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
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
block|}
block|}
specifier|private
name|void
name|closeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
literal|null
operator|!=
name|collection
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
end_class

end_unit

