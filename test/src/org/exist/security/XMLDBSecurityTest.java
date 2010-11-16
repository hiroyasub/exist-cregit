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
name|UserManagementService
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
name|LinkedList
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
name|XMLDBSecurityTest
block|{
specifier|private
specifier|static
name|String
name|DB_DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|String
name|baseUri
decl_stmt|;
specifier|private
specifier|static
name|JettyStart
name|server
decl_stmt|;
specifier|public
name|XMLDBSecurityTest
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
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// fails since guest has no write permissions
specifier|public
name|void
name|worldCreateCollection
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|test
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
name|createCollection
argument_list|(
literal|"createdByGuest"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// fails since guest has no write permissions
specifier|public
name|void
name|worldAddResource
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|createResource
argument_list|(
literal|"createdByGuest"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
literal|"<testMe/>"
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
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// fails since guest has no write permissions
specifier|public
name|void
name|worldRemoveCollection
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
name|baseUri
operator|+
literal|"/db"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
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
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"securityTest1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// fails since guest has no write permissions
specifier|public
name|void
name|worldChmodCollection
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|ums
operator|.
name|chmod
argument_list|(
literal|0777
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// fails since guest has no write permissions
specifier|public
name|void
name|worldChmodResource
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|ums
operator|.
name|chmod
argument_list|(
name|resource
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// fails since guest has no write permissions
specifier|public
name|void
name|worldChownCollection
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Account
name|guest
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"guest"
argument_list|)
decl_stmt|;
comment|// make myself the owner ;-)
name|ums
operator|.
name|chown
argument_list|(
name|guest
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// only the owner or admin can chown a collection or resource
specifier|public
name|void
name|worldChownResource
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|Account
name|test2
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"guest"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|resource
argument_list|,
name|test2
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|groupCreateSubColl
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|newCol
init|=
name|cms
operator|.
name|createCollection
argument_list|(
literal|"createdByTest2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newCol
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
name|Test
specifier|public
name|void
name|groupCreateResource
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|createResource
argument_list|(
literal|"createdByTest2.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
literal|"<testMe/>"
argument_list|)
expr_stmt|;
name|test
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|test
operator|.
name|getResource
argument_list|(
literal|"createdByTest2.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<testMe/>"
argument_list|,
name|resource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
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
name|Test
specifier|public
name|void
name|groupRemoveCollection
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
name|baseUri
operator|+
literal|"/db"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
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
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"securityTest1"
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
name|Test
specifier|public
name|void
name|groupChmodCollection
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|ums
operator|.
name|chmod
argument_list|(
literal|07777
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"agsrwurwurwu"
argument_list|,
name|ums
operator|.
name|getPermissions
argument_list|(
name|test
argument_list|)
operator|.
name|toString
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
name|Test
specifier|public
name|void
name|groupChmodResource
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|ums
operator|.
name|chmod
argument_list|(
name|resource
argument_list|,
literal|0777
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
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// only the owner or admin can chown a collection or resource
specifier|public
name|void
name|groupChownCollection
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|Account
name|test2
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|test2
argument_list|,
literal|"users"
argument_list|)
expr_stmt|;
name|Permission
name|perms
init|=
name|ums
operator|.
name|getPermissions
argument_list|(
name|test
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test2"
argument_list|,
name|perms
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
comment|// only the owner or admin can chown a collection or resource
specifier|public
name|void
name|groupChownResource
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
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test2"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// grant myself all rights ;-)
name|Account
name|test2
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|resource
argument_list|,
name|test2
argument_list|,
literal|"users"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
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
name|Account
name|test1
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test1
operator|!=
literal|null
condition|)
name|ums
operator|.
name|removeAccount
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|Account
name|test2
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test2
operator|!=
literal|null
condition|)
name|ums
operator|.
name|removeAccount
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|Group
name|group
init|=
name|ums
operator|.
name|getGroup
argument_list|(
literal|"users"
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
name|ums
operator|.
name|removeGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|=
operator|new
name|GroupAider
argument_list|(
literal|"exist"
argument_list|,
literal|"users"
argument_list|)
expr_stmt|;
name|ums
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|UserAider
name|user
init|=
operator|new
name|UserAider
argument_list|(
literal|"test1"
argument_list|,
name|group
argument_list|)
decl_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ums
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|user
operator|=
operator|new
name|UserAider
argument_list|(
literal|"test2"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|ums
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// create a collection /db/securityTest as user "test1"
name|CollectionManagementService
name|cms
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
name|cms
operator|.
name|createCollection
argument_list|(
literal|"securityTest1"
argument_list|)
decl_stmt|;
name|ums
operator|=
operator|(
name|UserManagementService
operator|)
name|test
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
comment|// pass ownership to test1
name|test1
operator|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|test1
argument_list|,
literal|"users"
argument_list|)
expr_stmt|;
comment|// full permissions for user and group, none for world
name|ums
operator|.
name|chmod
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|test
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|baseUri
operator|+
literal|"/db/securityTest1"
argument_list|,
literal|"test1"
argument_list|,
literal|"test1"
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|test
operator|.
name|createResource
argument_list|(
literal|"test.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
literal|"<test/>"
argument_list|)
expr_stmt|;
name|test
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
name|resource
argument_list|,
literal|0770
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
name|cleanup
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
name|baseUri
operator|+
literal|"/db"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
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
if|if
condition|(
name|root
operator|.
name|getChildCollection
argument_list|(
literal|"securityTest1"
argument_list|)
operator|!=
literal|null
condition|)
name|cms
operator|.
name|removeCollection
argument_list|(
literal|"securityTest1"
argument_list|)
expr_stmt|;
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
name|Account
name|test1
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test1
operator|!=
literal|null
condition|)
name|ums
operator|.
name|removeAccount
argument_list|(
name|test1
argument_list|)
expr_stmt|;
name|Account
name|test2
init|=
name|ums
operator|.
name|getAccount
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test2
operator|!=
literal|null
condition|)
name|ums
operator|.
name|removeAccount
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|Group
name|group
init|=
name|ums
operator|.
name|getGroup
argument_list|(
literal|"users"
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
name|ums
operator|.
name|removeGroup
argument_list|(
name|group
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
name|BeforeClass
specifier|public
specifier|static
name|void
name|startServer
parameter_list|()
block|{
try|try
block|{
comment|//            Class<?> cl = Class.forName(DB_DRIVER);
comment|//            Database database = (Database) cl.newInstance();
comment|//            database.setProperty("create-database", "true");
comment|//            DatabaseManager.registerDatabase(database);
comment|//            Collection root = DatabaseManager.getCollection("xmldb:exist:///db", "admin", "");
comment|//            assertNotNull(root);
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
name|AfterClass
specifier|public
specifier|static
name|void
name|stopServer
parameter_list|()
block|{
comment|//        try {
comment|//         Collection root = DatabaseManager.getCollection("xmldb:exist:///db", "admin", "");
comment|//            DatabaseInstanceManager mgr =
comment|//                (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
comment|//            mgr.shutdown();
comment|//        } catch (XMLDBException e) {
comment|//            e.printStackTrace();
comment|//        }
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
block|}
end_class

end_unit

