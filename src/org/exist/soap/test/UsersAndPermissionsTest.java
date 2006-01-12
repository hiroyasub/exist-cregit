begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|test
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
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Admin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|AdminService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|AdminServiceLocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Permissions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QueryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QueryServiceLocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|UserDesc
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

begin_class
specifier|public
class|class
name|UsersAndPermissionsTest
extends|extends
name|TestCase
block|{
specifier|static
name|String
name|query_url
init|=
literal|"http://localhost:8080/exist/services/Query"
decl_stmt|;
specifier|static
name|String
name|admin_url
init|=
literal|"http://localhost:8080/exist/services/Admin"
decl_stmt|;
name|String
name|testUser
init|=
literal|"BertieBeetle"
decl_stmt|;
name|String
name|testPassword
init|=
literal|"srfg.hj7Ld-"
decl_stmt|;
name|String
name|testHome
init|=
literal|"/db/home/BertieBeetle"
decl_stmt|;
name|String
name|testGroup
init|=
literal|"BertiesGroup"
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
name|sessionId
decl_stmt|;
specifier|public
name|UsersAndPermissionsTest
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|query_url
argument_list|)
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
name|admin_url
argument_list|)
argument_list|)
expr_stmt|;
name|sessionId
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
name|admin
operator|.
name|disconnect
argument_list|(
name|sessionId
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
block|}
specifier|public
name|void
name|bugTestCreateUser
parameter_list|()
throws|throws
name|RemoteException
block|{
name|UserDesc
name|desc
decl_stmt|;
try|try
block|{
name|desc
operator|=
name|admin
operator|.
name|getUser
argument_list|(
name|sessionId
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Removing user "
operator|+
name|testUser
argument_list|)
expr_stmt|;
name|admin
operator|.
name|removeUser
argument_list|(
name|sessionId
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|rex
parameter_list|)
block|{
block|}
name|String
index|[]
name|testGroups
init|=
block|{
name|testGroup
block|}
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Creating user "
operator|+
name|testUser
argument_list|)
expr_stmt|;
name|admin
operator|.
name|setUser
argument_list|(
name|sessionId
argument_list|,
name|testUser
argument_list|,
name|testPassword
argument_list|,
operator|new
name|Strings
argument_list|(
name|testGroups
argument_list|)
argument_list|,
name|testHome
argument_list|)
expr_stmt|;
name|desc
operator|=
name|admin
operator|.
name|getUser
argument_list|(
name|sessionId
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|desc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"User: "
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" Groups: ("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|desc
operator|.
name|getGroups
argument_list|()
operator|.
name|getElements
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|desc
operator|.
name|getGroups
argument_list|()
operator|.
name|getElements
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|") Home: "
operator|+
name|desc
operator|.
name|getHome
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Creating test resource"
argument_list|)
expr_stmt|;
comment|// Create a test resource
name|admin
operator|.
name|removeCollection
argument_list|(
name|sessionId
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|admin
operator|.
name|createCollection
argument_list|(
name|sessionId
argument_list|,
name|testColl
argument_list|)
expr_stmt|;
name|String
name|res
init|=
name|testColl
operator|+
literal|"/original"
decl_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|sessionId
argument_list|,
literal|"<sample/>"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|,
name|res
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Permissions
name|perms
init|=
name|admin
operator|.
name|getPermissions
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Owner: "
operator|+
name|perms
operator|.
name|getOwner
argument_list|()
operator|+
literal|" Group: "
operator|+
name|perms
operator|.
name|getGroup
argument_list|()
operator|+
literal|" Access: "
operator|+
name|Integer
operator|.
name|toOctalString
argument_list|(
name|perms
operator|.
name|getPermissions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Modifying resource permissions"
argument_list|)
expr_stmt|;
name|admin
operator|.
name|setPermissions
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|,
name|testUser
argument_list|,
name|testGroup
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
name|Permissions
name|newperms
init|=
name|admin
operator|.
name|getPermissions
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Owner: "
operator|+
name|newperms
operator|.
name|getOwner
argument_list|()
operator|+
literal|" Group: "
operator|+
name|newperms
operator|.
name|getGroup
argument_list|()
operator|+
literal|" Access: "
operator|+
name|Integer
operator|.
name|toOctalString
argument_list|(
name|newperms
operator|.
name|getPermissions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newperms
operator|.
name|getOwner
argument_list|()
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newperms
operator|.
name|getGroup
argument_list|()
argument_list|,
name|testGroup
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newperms
operator|.
name|getPermissions
argument_list|()
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Restoring resource permissions"
argument_list|)
expr_stmt|;
name|admin
operator|.
name|setPermissions
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|,
name|perms
operator|.
name|getOwner
argument_list|()
argument_list|,
name|perms
operator|.
name|getGroup
argument_list|()
argument_list|,
name|perms
operator|.
name|getPermissions
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Locking resource"
argument_list|)
expr_stmt|;
name|admin
operator|.
name|lockResource
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|String
name|lockOwner
init|=
name|admin
operator|.
name|hasUserLock
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock owner : "
operator|+
name|lockOwner
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lockOwner
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Unlocking resource"
argument_list|)
expr_stmt|;
name|admin
operator|.
name|unlockResource
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Lock owner : "
operator|+
name|admin
operator|.
name|hasUserLock
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|)
argument_list|)
expr_stmt|;
name|perms
operator|=
name|admin
operator|.
name|getPermissions
argument_list|(
name|sessionId
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Owner: "
operator|+
name|perms
operator|.
name|getOwner
argument_list|()
operator|+
literal|" Group: "
operator|+
name|perms
operator|.
name|getGroup
argument_list|()
operator|+
literal|" Access: "
operator|+
name|Integer
operator|.
name|toOctalString
argument_list|(
name|perms
operator|.
name|getPermissions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"==> Removing user "
operator|+
name|testUser
argument_list|)
expr_stmt|;
name|admin
operator|.
name|removeUser
argument_list|(
name|sessionId
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
try|try
block|{
name|desc
operator|=
name|admin
operator|.
name|getUser
argument_list|(
name|sessionId
argument_list|,
name|testUser
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Remove of user "
operator|+
name|testUser
operator|+
literal|" failed"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|rex
parameter_list|)
block|{
block|}
block|}
block|}
end_class

end_unit

