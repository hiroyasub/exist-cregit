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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
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

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|AbstractAccountTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|addGroup_calls_assertCanModifyGroup
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|NoSuchMethodException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|Database
name|mockDatabase
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Database
operator|.
name|class
argument_list|)
decl_stmt|;
name|Subject
name|mockSubject
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
name|Group
name|mockGroup
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|Account
name|partialMockAccount
init|=
name|EasyMock
operator|.
name|createMockBuilder
argument_list|(
name|AbstractAccount
operator|.
name|class
argument_list|)
operator|.
name|withConstructor
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|withArgs
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
literal|"testAccount"
argument_list|)
operator|.
name|addMockedMethod
argument_list|(
name|AbstractGroup
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"_addManager"
argument_list|,
name|Account
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|createMock
argument_list|()
decl_stmt|;
comment|//expectations
name|expect
argument_list|(
name|mockRealm
operator|.
name|getDatabase
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockDatabase
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockDatabase
operator|.
name|getSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockSubject
argument_list|)
expr_stmt|;
name|mockGroup
operator|.
name|assertCanModifyGroup
argument_list|(
name|mockSubject
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockRealm
argument_list|,
name|mockDatabase
argument_list|,
name|mockGroup
argument_list|,
name|partialMockAccount
argument_list|)
expr_stmt|;
comment|//test
name|partialMockAccount
operator|.
name|addGroup
argument_list|(
name|mockGroup
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRealm
argument_list|,
name|mockDatabase
argument_list|,
name|mockGroup
argument_list|,
name|partialMockAccount
argument_list|)
expr_stmt|;
comment|//TODO calls on assert from AbstractAccountXQuerty
block|}
annotation|@
name|Test
specifier|public
name|void
name|remGroup_calls_assertCanModifyGroupForEachGroup
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|NoSuchMethodException
throws|,
name|ConfigurationException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|Database
name|mockDatabase
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Database
operator|.
name|class
argument_list|)
decl_stmt|;
name|Subject
name|mockSubject
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
name|Group
name|mockGroup
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|groupName
init|=
literal|"testGroup"
decl_stmt|;
name|TestableAbstractAccount
name|partialMockAccount
init|=
operator|new
name|TestableAbstractAccount
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
literal|"testGroup"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<
name|Group
argument_list|>
argument_list|()
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
name|mockGroup
argument_list|)
expr_stmt|;
name|partialMockAccount
operator|.
name|setInternalGroups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
comment|//expectations
name|expect
argument_list|(
name|mockRealm
operator|.
name|getDatabase
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockDatabase
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockDatabase
operator|.
name|getSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockSubject
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockGroup
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
name|mockGroup
operator|.
name|assertCanModifyGroup
argument_list|(
name|mockSubject
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockRealm
argument_list|,
name|mockDatabase
argument_list|,
name|mockGroup
argument_list|)
expr_stmt|;
comment|//test
name|partialMockAccount
operator|.
name|remGroup
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockRealm
argument_list|,
name|mockDatabase
argument_list|,
name|mockGroup
argument_list|)
expr_stmt|;
comment|//TODO calls on assert from AbstractAccountXQuerty
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
name|assertCanModifyAccount_fails_when_user_is_null
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|TestableAbstractAccount
name|account
init|=
operator|new
name|TestableAbstractAccount
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
literal|"testAccount"
argument_list|)
decl_stmt|;
name|account
operator|.
name|assertCanModifyAccount
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|assertCanModifyAccount_succeeds_when_user_is_dba
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|Account
name|mockAccount
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Account
operator|.
name|class
argument_list|)
decl_stmt|;
name|TestableAbstractAccount
name|account
init|=
operator|new
name|TestableAbstractAccount
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
literal|"testAccount"
argument_list|)
decl_stmt|;
comment|//expectations
name|expect
argument_list|(
name|mockAccount
operator|.
name|hasDbaRole
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
comment|//test
name|account
operator|.
name|assertCanModifyAccount
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockAccount
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
name|assertCanModifyAccount_fails_when_user_is_not_dba
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|Account
name|mockAccount
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Account
operator|.
name|class
argument_list|)
decl_stmt|;
name|TestableAbstractAccount
name|account
init|=
operator|new
name|TestableAbstractAccount
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
literal|"testAccount"
argument_list|)
decl_stmt|;
comment|//expectations
name|expect
argument_list|(
name|mockAccount
operator|.
name|hasDbaRole
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockAccount
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"test"
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
comment|//test
name|account
operator|.
name|assertCanModifyAccount
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|assertCanModifyAccount_succeeds_when_user_is_same
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|Account
name|mockAccount
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Account
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|accountName
init|=
literal|"testAccount"
decl_stmt|;
name|TestableAbstractAccount
name|account
init|=
operator|new
name|TestableAbstractAccount
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
comment|//expectations
name|expect
argument_list|(
name|mockAccount
operator|.
name|hasDbaRole
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockAccount
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|accountName
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
comment|//test
name|account
operator|.
name|assertCanModifyAccount
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockAccount
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
name|assertCanModifyAccount_fails_when_user_is_not_same
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|ConfigurationException
block|{
name|AbstractRealm
name|mockRealm
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|)
decl_stmt|;
name|Account
name|mockAccount
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Account
operator|.
name|class
argument_list|)
decl_stmt|;
name|TestableAbstractAccount
name|account
init|=
operator|new
name|TestableAbstractAccount
argument_list|(
name|mockRealm
argument_list|,
literal|1
argument_list|,
literal|"testAccount"
argument_list|)
decl_stmt|;
comment|//expectations
name|expect
argument_list|(
name|mockAccount
operator|.
name|hasDbaRole
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockAccount
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|"otherAccount"
argument_list|)
operator|.
name|times
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
comment|//test
name|account
operator|.
name|assertCanModifyAccount
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockAccount
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|TestableAbstractAccount
extends|extends
name|AbstractAccount
block|{
specifier|public
name|TestableAbstractAccount
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setInternalGroups
parameter_list|(
name|List
argument_list|<
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDigestPassword
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCredential
parameter_list|(
name|Credential
name|credential
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

