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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|util
operator|.
name|DatabaseConfigurationException
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
comment|/**  * Ensures that security manager data, accounts, groups (and associations)  * are correctly persisted across database restarts  *  * @author<a href="mailto:adam@existsolutions.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSecurityManagerRoundtripTest
block|{
specifier|protected
specifier|abstract
name|Collection
name|getRoot
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|restartServer
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
function_decl|;
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
throws|,
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|getRoot
argument_list|()
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
name|restartServer
argument_list|()
expr_stmt|;
comment|/**************************/
name|ums
operator|=
operator|(
name|UserManagementService
operator|)
name|getRoot
argument_list|()
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
name|userName
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
specifier|final
name|Account
name|u1
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|u1
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeAccount
argument_list|(
name|u1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|g1
init|=
name|ums
operator|.
name|getGroup
argument_list|(
name|group1Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|g1
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|g1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|g2
init|=
name|ums
operator|.
name|getGroup
argument_list|(
name|group2Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|g2
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|g2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkPrimaryGroupRemainsDBA
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|getRoot
argument_list|()
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
name|ums
operator|.
name|getGroup
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
argument_list|)
decl_stmt|;
comment|//set users primary group as DBA
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
name|group1
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
name|restartServer
argument_list|()
expr_stmt|;
comment|/**************************/
name|ums
operator|=
operator|(
name|UserManagementService
operator|)
name|getRoot
argument_list|()
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
name|userName
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
name|SecurityManager
operator|.
name|DBA_GROUP
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
literal|3
argument_list|,
name|groups
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|,
name|groups
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group1Name
argument_list|,
name|groups
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group2Name
argument_list|,
name|groups
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//cleanup
specifier|final
name|Account
name|u1
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|u1
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeAccount
argument_list|(
name|u1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|g1
init|=
name|ums
operator|.
name|getGroup
argument_list|(
name|group1Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|g1
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|g1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|g2
init|=
name|ums
operator|.
name|getGroup
argument_list|(
name|group2Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|g2
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|g2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkPrimaryGroupStability
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|getRoot
argument_list|()
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
literal|"testGroupA"
decl_stmt|;
specifier|final
name|String
name|group2Name
init|=
literal|"testGroupB"
decl_stmt|;
specifier|final
name|String
name|userName
init|=
literal|"testUserA"
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
comment|//set users primary group as group1
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
name|group2Name
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
name|restartServer
argument_list|()
expr_stmt|;
comment|/**************************/
name|ums
operator|=
operator|(
name|UserManagementService
operator|)
name|getRoot
argument_list|()
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
name|userName
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
specifier|final
name|Account
name|u1
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|u1
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeAccount
argument_list|(
name|u1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|g1
init|=
name|ums
operator|.
name|getGroup
argument_list|(
name|group1Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|g1
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|g1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Group
name|g2
init|=
name|ums
operator|.
name|getGroup
argument_list|(
name|group2Name
argument_list|)
decl_stmt|;
if|if
condition|(
name|g2
operator|!=
literal|null
condition|)
block|{
name|ums
operator|.
name|removeGroup
argument_list|(
name|g2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

