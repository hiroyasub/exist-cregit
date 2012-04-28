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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|ConstructorArgs
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
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|AccountImpl
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
name|replay
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
name|Configuration
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
name|SecurityManagerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|AccountTest
block|{
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testGroupFallback
parameter_list|()
throws|throws
name|NoSuchMethodException
throws|,
name|PermissionDeniedException
block|{
comment|//        final String mockRealmId = "mock";
specifier|final
name|String
name|testAccountName
init|=
literal|"testUser"
decl_stmt|;
specifier|final
name|String
name|testGroupName
init|=
literal|"testGroup"
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
name|SecurityManagerImpl
name|mockSecurityManager
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|SecurityManagerImpl
operator|.
name|class
argument_list|,
operator|new
name|ConstructorArgs
argument_list|(
name|SecurityManagerImpl
operator|.
name|class
operator|.
name|getConstructor
argument_list|(
name|Database
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Object
index|[]
block|{
name|mockDatabase
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|mockConfiguration
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
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
argument_list|,
operator|new
name|ConstructorArgs
argument_list|(
name|AbstractRealm
operator|.
name|class
operator|.
name|getDeclaredConstructor
argument_list|(
name|SecurityManager
operator|.
name|class
argument_list|,
name|Configuration
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Object
index|[]
block|{
name|mockSecurityManager
block|,
name|mockConfiguration
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|AccountImpl
name|mockAccountImpl
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|AccountImpl
operator|.
name|class
argument_list|,
operator|new
name|ConstructorArgs
argument_list|(
name|AccountImpl
operator|.
name|class
operator|.
name|getDeclaredConstructor
argument_list|(
name|AbstractRealm
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
operator|new
name|Object
index|[]
block|{
name|mockRealm
block|,
name|testAccountName
block|}
argument_list|)
argument_list|,
operator|new
name|Method
index|[]
block|{
name|AccountImpl
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getRealm"
argument_list|)
block|,
name|AccountImpl
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"addGroup"
argument_list|,
name|Group
operator|.
name|class
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|mockAccountImpl
operator|.
name|getRealm
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockRealm
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockRealm
operator|.
name|getGroup
argument_list|(
literal|null
argument_list|,
name|testGroupName
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//expect(mockAccountImpl.getRealm()).andReturn(mockRealm);
comment|//expect(mockRealm.getSecurityManager()).andReturn(mockSecurityManager);
name|replay
argument_list|()
expr_stmt|;
name|mockAccountImpl
operator|.
name|addGroup
argument_list|(
name|testGroupName
argument_list|)
expr_stmt|;
name|verify
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

