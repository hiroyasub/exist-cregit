begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

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
name|TestUtils
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
name|PermissionDeniedException
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
name|UserAider
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
name|Configuration
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Tests for startup triggers.  */
end_comment

begin_class
specifier|public
class|class
name|StartupTriggerTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|USER
init|=
literal|"testuser1"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PASSWORD
init|=
literal|"testpass"
decl_stmt|;
comment|/**      * Check if startup trigger has access to security manager.      */
annotation|@
name|Test
specifier|public
name|void
name|createUser
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
throws|,
name|IOException
block|{
specifier|final
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Configuration
operator|.
name|StartupTriggerConfig
argument_list|>
name|startupTriggers
init|=
operator|(
name|List
argument_list|<
name|Configuration
operator|.
name|StartupTriggerConfig
argument_list|>
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_STARTUP_TRIGGERS
argument_list|)
decl_stmt|;
name|startupTriggers
operator|.
name|add
argument_list|(
operator|new
name|Configuration
operator|.
name|StartupTriggerConfig
argument_list|(
name|TestStartupTrigger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|TestStartupTrigger
implements|implements
name|StartupTrigger
block|{
specifier|public
name|TestStartupTrigger
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|DBBroker
name|sysBroker
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
specifier|final
name|SecurityManager
name|secman
init|=
name|sysBroker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|secman
operator|.
name|hasAccount
argument_list|(
name|USER
argument_list|)
condition|)
block|{
specifier|final
name|UserAider
name|aider
init|=
operator|new
name|UserAider
argument_list|(
name|USER
argument_list|)
decl_stmt|;
name|aider
operator|.
name|setPassword
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
try|try
block|{
name|secman
operator|.
name|addAccount
argument_list|(
name|aider
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
decl||
name|EXistException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|secman
operator|.
name|hasAccount
argument_list|(
name|USER
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|secman
operator|.
name|deleteAccount
argument_list|(
name|USER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
decl||
name|EXistException
name|e
parameter_list|)
block|{
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
block|}
block|}
end_class

end_unit

