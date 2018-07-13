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
name|Configurable
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
name|config
operator|.
name|Configurator
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

begin_comment
comment|/**  * @author aretter  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"whitelist"
argument_list|)
specifier|public
class|class
name|LDAPPrincipalWhiteList
extends|extends
name|AbstractLDAPPrincipalRestrictionList
implements|implements
name|Configurable
block|{
specifier|public
name|LDAPPrincipalWhiteList
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|//it require, because class's fields initializing after super constructor
if|if
condition|(
name|this
operator|.
name|configuration
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

