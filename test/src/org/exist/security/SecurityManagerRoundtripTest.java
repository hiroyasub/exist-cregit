begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
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
name|UserManagementService
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
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|security
operator|.
name|internal
operator|.
name|aider
operator|.
name|GroupAider
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
name|internal
operator|.
name|aider
operator|.
name|UserAider
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
name|After
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
name|assertEquals
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com.com>  */
end_comment

begin_comment
comment|/**  * Ensures that security manager data, accounts, groups (and associations)  * are correctly persisted across database restarts  */
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
name|SecurityManagerRoundtripTest
block|{
specifier|private
name|JettyStart
name|server
decl_stmt|;
specifier|private
specifier|final
name|String
name|baseUri
decl_stmt|;
specifier|public
name|SecurityManagerRoundtripTest
parameter_list|(
name|String
name|baseUri
parameter_list|)
block|{
name|this
operator|.
name|baseUri
operator|=
name|baseUri
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|LinkedList
argument_list|<
name|String
index|[]
argument_list|>
name|instances
parameter_list|()
block|{
name|LinkedList
argument_list|<
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|LinkedList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"xmldb:exist://"
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"xmldb:exist://localhost:8088/xmlrpc"
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|startServer
parameter_list|()
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
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
name|After
specifier|public
name|void
name|stopServer
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shutdown standalone server..."
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|checkGroupMembership
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|PermissionDeniedException
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseUri
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|group1Name
init|=
literal|"testGroup1"
decl_stmt|;
specifier|final
name|String
name|group2Name
init|=
literal|"testGroup2"
decl_stmt|;
specifier|final
name|String
name|userName
init|=
literal|"testUser"
decl_stmt|;
name|Group
name|group1
init|=
operator|new
name|GroupAider
argument_list|(
name|group1Name
argument_list|)
decl_stmt|;
name|Group
name|group2
init|=
operator|new
name|GroupAider
argument_list|(
name|group2Name
argument_list|)
decl_stmt|;
name|Account
name|user
init|=
operator|new
name|UserAider
argument_list|(
name|userName
argument_list|,
name|group1
argument_list|)
decl_stmt|;
try|try
block|{
name|ums
operator|.
name|addGroup
argument_list|(
name|group1
argument_list|)
expr_stmt|;
name|ums
operator|.
name|addGroup
argument_list|(
name|group2
argument_list|)
expr_stmt|;
name|ums
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|ums
operator|.
name|getAccount
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|user
operator|.
name|addGroup
argument_list|(
name|group2
argument_list|)
expr_stmt|;
name|ums
operator|.
name|updateAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|/*** RESTART THE SERVER ***/
name|stopServer
argument_list|()
expr_stmt|;
name|startServer
argument_list|()
expr_stmt|;
comment|/**************************/
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseUri
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ums
operator|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|user
operator|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"testUser"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|Group
name|defaultGroup
init|=
name|user
operator|.
name|getDefaultGroup
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|defaultGroup
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group1Name
argument_list|,
name|defaultGroup
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|groups
index|[]
init|=
name|user
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|groups
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group1Name
argument_list|,
name|groups
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group2Name
argument_list|,
name|groups
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//cleanup
try|try
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|group1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
try|try
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|group2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
try|try
block|{
name|ums
operator|.
name|removeAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
block|}
block|}
end_class

end_unit

