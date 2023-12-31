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
name|ConfigurationFieldAsElement
import|;
end_import

begin_comment
comment|/**  * @author aretter  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|""
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractLDAPPrincipalRestrictionList
implements|implements
name|Configurable
block|{
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"principal"
argument_list|)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|principals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
name|Configuration
name|configuration
decl_stmt|;
specifier|public
name|AbstractLDAPPrincipalRestrictionList
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|)
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
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
operator|(
name|configuration
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPrincipals
parameter_list|()
block|{
return|return
name|principals
return|;
block|}
specifier|public
name|void
name|addPrincipal
parameter_list|(
name|String
name|principal
parameter_list|)
block|{
name|this
operator|.
name|principals
operator|.
name|add
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

