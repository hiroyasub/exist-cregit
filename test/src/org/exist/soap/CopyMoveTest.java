begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

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
name|rmi
operator|.
name|RemoteException
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
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
import|;
end_import

begin_class
specifier|public
class|class
name|CopyMoveTest
extends|extends
name|TestCase
block|{
specifier|static
name|Main
name|mn
init|=
literal|null
decl_stmt|;
name|String
name|testColl
init|=
literal|"/db/test"
decl_stmt|;
name|Query
name|query
decl_stmt|;
name|Admin
name|admin
decl_stmt|;
name|String
name|q_session
decl_stmt|;
name|String
name|a_session
decl_stmt|;
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
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mn
operator|==
literal|null
condition|)
block|{
name|mn
operator|=
operator|new
name|Main
argument_list|(
literal|"jetty"
argument_list|)
expr_stmt|;
name|mn
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
block|}
comment|//super.setUp();
name|QueryService
name|service
init|=
operator|new
name|QueryServiceLocator
argument_list|()
decl_stmt|;
name|query
operator|=
name|service
operator|.
name|getQuery
argument_list|(
operator|new
name|URL
argument_list|(
name|XQueryTest
operator|.
name|query_url
argument_list|)
argument_list|)
expr_stmt|;
name|q_session
operator|=
name|query
operator|.
name|connect
argument_list|(
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|AdminService
name|aservice
init|=
operator|new
name|AdminServiceLocator
argument_list|()
decl_stmt|;
name|admin
operator|=
name|aservice
operator|.
name|getAdmin
argument_list|(
operator|new
name|URL
argument_list|(
name|XQueryTest
operator|.
name|admin_url
argument_list|)
argument_list|)
expr_stmt|;
name|a_session
operator|=
name|admin
operator|.
name|connect
argument_list|(
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
try|try
block|{
name|query
operator|.
name|disconnect
argument_list|(
name|q_session
argument_list|)
expr_stmt|;
name|admin
operator|.
name|disconnect
argument_list|(
name|a_session
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|rex
parameter_list|)
block|{
name|rex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//mn.shutdownDB();
block|}
specifier|private
name|void
name|setupTestCollection
parameter_list|()
throws|throws
name|RemoteException
block|{
name|admin
operator|.
name|removeCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|admin
operator|.
name|createCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupTestCollections
parameter_list|()
throws|throws
name|RemoteException
block|{
name|String
name|collA
init|=
name|testColl
operator|+
literal|"/testA"
decl_stmt|;
name|admin
operator|.
name|removeCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|admin
operator|.
name|createCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|admin
operator|.
name|createCollection
argument_list|(
name|a_session
argument_list|,
name|collA
argument_list|)
expr_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|a_session
argument_list|,
literal|"<sample/>"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|collA
operator|+
literal|"/docA"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|a_session
argument_list|,
literal|"<sample/>"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|collA
operator|+
literal|"/docB"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCopyResourceChangeName
parameter_list|()
throws|throws
name|RemoteException
block|{
name|setupTestCollection
argument_list|()
expr_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|a_session
argument_list|,
literal|"<sample/>"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|testColl
operator|+
literal|"/original"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|admin
operator|.
name|copyResource
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/original"
argument_list|,
name|testColl
argument_list|,
literal|"duplicate"
argument_list|)
expr_stmt|;
name|String
index|[]
name|resources
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
operator|.
name|getResources
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|resources
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resources
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"duplicate"
argument_list|)
operator|||
name|resources
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"duplicate"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|query
operator|.
name|getResource
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/duplicate"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMoveResource
parameter_list|()
throws|throws
name|RemoteException
block|{
name|setupTestCollection
argument_list|()
expr_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|a_session
argument_list|,
literal|"<sample/>"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|testColl
operator|+
literal|"/original"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|admin
operator|.
name|moveResource
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/original"
argument_list|,
name|testColl
argument_list|,
literal|"duplicate"
argument_list|)
expr_stmt|;
name|String
index|[]
name|resources
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
operator|.
name|getResources
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resources
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resources
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"duplicate"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCopyCollectionChangeName
parameter_list|()
throws|throws
name|RemoteException
block|{
name|setupTestCollections
argument_list|()
expr_stmt|;
name|admin
operator|.
name|copyCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/testA"
argument_list|,
name|testColl
argument_list|,
literal|"testAcopy"
argument_list|)
expr_stmt|;
name|String
index|[]
name|collections
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
operator|.
name|getCollections
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|collections
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|String
index|[]
name|resources
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/testAcopy"
argument_list|)
operator|.
name|getResources
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|resources
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMoveCollection
parameter_list|()
throws|throws
name|RemoteException
block|{
name|setupTestCollections
argument_list|()
expr_stmt|;
name|admin
operator|.
name|moveCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/testA"
argument_list|,
name|testColl
argument_list|,
literal|"testAcopy"
argument_list|)
expr_stmt|;
name|String
index|[]
name|collections
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
argument_list|)
operator|.
name|getCollections
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|collections
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|collections
index|[
literal|0
index|]
argument_list|,
literal|"testAcopy"
argument_list|)
expr_stmt|;
name|String
index|[]
name|resources
init|=
name|query
operator|.
name|listCollection
argument_list|(
name|a_session
argument_list|,
name|testColl
operator|+
literal|"/testAcopy"
argument_list|)
operator|.
name|getResources
argument_list|()
operator|.
name|getElements
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|resources
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveThisEmptyTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|//        assertEquals(1,1);
block|}
block|}
end_class

end_unit

