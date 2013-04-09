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
name|javax
operator|.
name|naming
operator|.
name|NamingException
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
comment|/**  *  * @author aretter  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"search"
argument_list|)
specifier|public
class|class
name|LDAPSearchContext
implements|implements
name|Configurable
block|{
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"base"
argument_list|)
specifier|protected
name|String
name|base
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"default-username"
argument_list|)
specifier|protected
name|String
name|defaultUsername
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"default-password"
argument_list|)
specifier|protected
name|String
name|defaultPassword
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"account"
argument_list|)
specifier|protected
name|LDAPSearchAccount
name|searchAccount
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"group"
argument_list|)
specifier|protected
name|LDAPSearchGroup
name|searchGroup
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Configuration
name|configuration
decl_stmt|;
specifier|public
name|LDAPSearchContext
parameter_list|(
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
specifier|public
name|String
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
specifier|public
name|String
name|getAbsoluteBase
parameter_list|()
throws|throws
name|NamingException
block|{
if|if
condition|(
name|getBase
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|int
name|index
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|=
name|getBase
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"dc="
argument_list|)
operator|)
operator|>=
literal|0
condition|)
return|return
name|getBase
argument_list|()
operator|.
name|substring
argument_list|(
name|index
argument_list|)
return|;
if|if
condition|(
operator|(
name|index
operator|=
name|getBase
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DC="
argument_list|)
operator|)
operator|>=
literal|0
condition|)
return|return
name|getBase
argument_list|()
operator|.
name|substring
argument_list|(
name|index
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NamingException
argument_list|(
literal|"no 'base' defined"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|NamingException
argument_list|(
literal|"'base' have no 'dc=' or 'DC='"
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getDefaultUsername
parameter_list|()
block|{
return|return
name|defaultUsername
return|;
block|}
specifier|public
name|String
name|getDefaultPassword
parameter_list|()
block|{
return|return
name|defaultPassword
return|;
block|}
specifier|public
name|LDAPSearchAccount
name|getSearchAccount
parameter_list|()
block|{
return|return
name|searchAccount
return|;
block|}
specifier|public
name|LDAPSearchGroup
name|getSearchGroup
parameter_list|()
block|{
return|return
name|searchGroup
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
block|}
end_class

end_unit

