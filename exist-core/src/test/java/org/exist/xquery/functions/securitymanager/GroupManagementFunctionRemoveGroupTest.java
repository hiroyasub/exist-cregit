begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|securitymanager
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|Runnable3E
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
name|security
operator|.
name|*
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
name|SecurityManager
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
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|ExistEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
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
name|java
operator|.
name|util
operator|.
name|Optional
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
specifier|public
class|class
name|GroupManagementFunctionRemoveGroupTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER1_NAME
init|=
literal|"user1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER1_PWD
init|=
name|USER1_NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER2_NAME
init|=
literal|"user2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER2_PWD
init|=
name|USER2_NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OTHER_GROUP1_NAME
init|=
literal|"otherGroup"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OTHER_GROUP2_NAME
init|=
literal|"otherGroup2"
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|ExistEmbeddedServer
name|existWebServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PermissionDeniedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|cannotDeleteDbaGroup
parameter_list|()
throws|throws
name|XPathException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|extractPermissionDenied
argument_list|(
parameter_list|()
lambda|->
block|{
name|xqueryRemoveGroup
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PermissionDeniedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|cannotDeleteGuestGroup
parameter_list|()
throws|throws
name|XPathException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|extractPermissionDenied
argument_list|(
parameter_list|()
lambda|->
block|{
name|xqueryRemoveGroup
argument_list|(
name|SecurityManager
operator|.
name|GUEST_GROUP
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PermissionDeniedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|cannotDeleteUnknownGroup
parameter_list|()
throws|throws
name|XPathException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|extractPermissionDenied
argument_list|(
parameter_list|()
lambda|->
block|{
name|xqueryRemoveGroup
argument_list|(
name|SecurityManager
operator|.
name|UNKNOWN_GROUP
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteUsersSupplementalGroups
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|SecurityManager
name|sm
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
comment|// create user with personal group as primary group
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Account
name|user1
init|=
name|createUser
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|USER1_NAME
argument_list|,
name|USER1_PWD
argument_list|)
decl_stmt|;
specifier|final
name|Group
name|otherGroup1
init|=
name|createGroup
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|OTHER_GROUP1_NAME
argument_list|)
decl_stmt|;
name|addUserToGroup
argument_list|(
name|sm
argument_list|,
name|user1
argument_list|,
name|otherGroup1
argument_list|)
expr_stmt|;
specifier|final
name|Group
name|otherGroup2
init|=
name|createGroup
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|OTHER_GROUP2_NAME
argument_list|)
decl_stmt|;
name|addUserToGroup
argument_list|(
name|sm
argument_list|,
name|user1
argument_list|,
name|otherGroup2
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check that the user is as we expect
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Account
name|user1
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|USER1_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|USER1_NAME
argument_list|,
name|user1
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|user1Groups
init|=
name|user1
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|USER1_NAME
block|,
name|OTHER_GROUP1_NAME
block|,
name|OTHER_GROUP2_NAME
block|}
argument_list|,
name|user1Groups
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|user1Group
range|:
name|user1Groups
control|)
block|{
name|assertNotNull
argument_list|(
name|sm
operator|.
name|getGroup
argument_list|(
name|user1Group
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// attempt to remove the supplemental groups of the user
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|assertTrue
argument_list|(
name|sm
operator|.
name|deleteGroup
argument_list|(
name|OTHER_GROUP1_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sm
operator|.
name|deleteGroup
argument_list|(
name|OTHER_GROUP2_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check that the user no longer has the supplemental groups
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Account
name|user1
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|USER1_NAME
argument_list|)
decl_stmt|;
specifier|final
name|String
name|user1PrimaryGroup
init|=
name|user1
operator|.
name|getPrimaryGroup
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|USER1_NAME
argument_list|,
name|user1PrimaryGroup
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|user1Groups
init|=
name|user1
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|USER1_NAME
block|,
name|OTHER_GROUP1_NAME
block|,
name|OTHER_GROUP2_NAME
block|}
argument_list|,
name|user1Groups
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|user1Group
range|:
name|user1Groups
control|)
block|{
if|if
condition|(
name|user1PrimaryGroup
operator|.
name|equals
argument_list|(
name|user1Group
argument_list|)
condition|)
block|{
name|assertNotNull
argument_list|(
name|sm
operator|.
name|getGroup
argument_list|(
name|user1Group
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// cannot retrieve groups which have been deleted!
name|assertNull
argument_list|(
name|sm
operator|.
name|getGroup
argument_list|(
name|user1Group
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PermissionDeniedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|deleteUsersPersonalPrimaryGroup
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|SecurityManager
name|sm
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
comment|// create user with personal group as primary group
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|createUser
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|USER1_NAME
argument_list|,
name|USER1_PWD
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check that the user is as we expect
name|String
name|user1PrimaryGroup
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Account
name|user1
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|USER1_NAME
argument_list|)
decl_stmt|;
name|user1PrimaryGroup
operator|=
name|user1
operator|.
name|getPrimaryGroup
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|USER1_NAME
argument_list|,
name|user1PrimaryGroup
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|USER1_NAME
block|}
argument_list|,
name|user1
operator|.
name|getGroups
argument_list|()
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// attempt to remove the primary group of the user
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|sm
operator|.
name|deleteGroup
argument_list|(
name|user1PrimaryGroup
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have received: PermissionDeniedException: Account 'user1' still has 'user1' as their primary group!"
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteUsersSharingPersonalPrimaryGroup
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|SecurityManager
name|sm
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
comment|// create two users which share a primary group
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Group
name|otherGroup1
init|=
name|createGroup
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|OTHER_GROUP1_NAME
argument_list|)
decl_stmt|;
name|Account
name|user1
init|=
name|createUser
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|USER1_NAME
argument_list|,
name|USER1_PWD
argument_list|)
decl_stmt|;
name|addUserToGroup
argument_list|(
name|sm
argument_list|,
name|user1
argument_list|,
name|otherGroup1
argument_list|)
expr_stmt|;
name|setPrimaryGroup
argument_list|(
name|sm
argument_list|,
name|user1
argument_list|,
name|otherGroup1
argument_list|)
expr_stmt|;
specifier|final
name|Account
name|user2
init|=
name|createUser
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|USER2_NAME
argument_list|,
name|USER2_PWD
argument_list|)
decl_stmt|;
name|addUserToGroup
argument_list|(
name|sm
argument_list|,
name|user2
argument_list|,
name|otherGroup1
argument_list|)
expr_stmt|;
name|setPrimaryGroup
argument_list|(
name|sm
argument_list|,
name|user2
argument_list|,
name|otherGroup1
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check that the users are as we expect
name|String
name|primaryGroup
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Account
name|user1
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|USER1_NAME
argument_list|)
decl_stmt|;
name|primaryGroup
operator|=
name|user1
operator|.
name|getPrimaryGroup
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|OTHER_GROUP1_NAME
argument_list|,
name|primaryGroup
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|user1Groups
init|=
name|user1
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|OTHER_GROUP1_NAME
block|,
name|USER1_NAME
block|}
argument_list|,
name|user1Groups
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|user1Group
range|:
name|user1Groups
control|)
block|{
name|assertNotNull
argument_list|(
name|sm
operator|.
name|getGroup
argument_list|(
name|user1Group
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Account
name|user2
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|USER2_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|OTHER_GROUP1_NAME
argument_list|,
name|user2
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|user2Groups
init|=
name|user2
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|OTHER_GROUP1_NAME
block|,
name|USER2_NAME
block|}
argument_list|,
name|user2Groups
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|user2Group
range|:
name|user2Groups
control|)
block|{
name|assertNotNull
argument_list|(
name|sm
operator|.
name|getGroup
argument_list|(
name|user2Group
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// attempt to remove the primary group of the first user
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
block|{
name|sm
operator|.
name|deleteGroup
argument_list|(
name|primaryGroup
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have received: PermissionDeniedException: Account 'user1' still has 'otherGroup1' as their primary group!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// delete the first user
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|removeUser
argument_list|(
name|sm
argument_list|,
name|USER1_NAME
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// attempt to remove the primary group of the second user
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
block|{
name|sm
operator|.
name|deleteGroup
argument_list|(
name|primaryGroup
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have received: PermissionDeniedException: Account 'user2' still has 'otherGroup1' as their primary group!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// delete the second user
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|removeUser
argument_list|(
name|sm
argument_list|,
name|USER2_NAME
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// no users have the group as primary group, so now should be able to delete the group
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|sm
operator|.
name|deleteGroup
argument_list|(
name|primaryGroup
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Account
name|createUser
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|Group
name|userGroup
init|=
operator|new
name|GroupAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|sm
operator|.
name|addGroup
argument_list|(
name|broker
argument_list|,
name|userGroup
argument_list|)
expr_stmt|;
specifier|final
name|Account
name|user
init|=
operator|new
name|UserAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|user
operator|.
name|setPrimaryGroup
argument_list|(
name|userGroup
argument_list|)
expr_stmt|;
name|sm
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|userGroup
operator|=
name|sm
operator|.
name|getGroup
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|userGroup
operator|.
name|addManager
argument_list|(
name|sm
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
argument_list|)
expr_stmt|;
name|sm
operator|.
name|updateGroup
argument_list|(
name|userGroup
argument_list|)
expr_stmt|;
return|return
name|user
return|;
block|}
specifier|private
specifier|static
name|Group
name|createGroup
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|groupName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
specifier|final
name|Group
name|otherGroup
init|=
operator|new
name|GroupAider
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
return|return
name|sm
operator|.
name|addGroup
argument_list|(
name|broker
argument_list|,
name|otherGroup
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|addUserToGroup
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|Account
name|user
parameter_list|,
specifier|final
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|user
operator|.
name|addGroup
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sm
operator|.
name|updateAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|setPrimaryGroup
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|Account
name|user
parameter_list|,
specifier|final
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|user
operator|.
name|setPrimaryGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|sm
operator|.
name|updateAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|removeUser
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|username
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|sm
operator|.
name|deleteAccount
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|removeGroup
argument_list|(
name|sm
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|removeGroup
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|groupname
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|sm
operator|.
name|deleteGroup
argument_list|(
name|groupname
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Sequence
name|xqueryRemoveGroup
parameter_list|(
specifier|final
name|String
name|groupname
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"import module namespace sm = 'http://exist-db.org/xquery/securitymanager';\n"
operator|+
literal|"sm:remove-group('"
operator|+
name|groupname
operator|+
literal|"')"
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|extractPermissionDenied
parameter_list|(
specifier|final
name|Runnable3E
argument_list|<
name|XPathException
argument_list|,
name|PermissionDeniedException
argument_list|,
name|EXistException
argument_list|>
name|runnable
parameter_list|)
throws|throws
name|XPathException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
block|{
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|PermissionDeniedException
condition|)
block|{
throw|throw
operator|(
name|PermissionDeniedException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit
