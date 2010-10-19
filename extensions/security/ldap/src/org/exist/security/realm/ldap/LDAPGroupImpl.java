begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|realm
operator|.
name|ldap
package|;
end_package

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
name|config
operator|.
name|ConfigurationException
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
name|annotation
operator|.
name|ConfigurationClass
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
name|AbstractGroup
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
name|AbstractRealm
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"group"
argument_list|)
specifier|public
class|class
name|LDAPGroupImpl
extends|extends
name|AbstractGroup
block|{
specifier|public
name|LDAPGroupImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LDAPGroupImpl
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
name|LDAPGroupImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|boolean
name|removed
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
argument_list|(
name|realm
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|removed
operator|=
name|removed
expr_stmt|;
block|}
name|LDAPGroupImpl
parameter_list|(
name|AbstractRealm
name|realm
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
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

