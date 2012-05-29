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
name|Account
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
name|Group
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
name|AbstractRealm
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

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"account"
argument_list|)
specifier|public
class|class
name|LDAPAccountImpl
extends|extends
name|AccountImpl
block|{
specifier|public
name|LDAPAccountImpl
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
name|LDAPAccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|AccountImpl
name|from_user
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|from_user
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LDAPAccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
parameter_list|,
name|Account
name|from_user
parameter_list|)
throws|throws
name|ConfigurationException
throws|,
name|PermissionDeniedException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|id
argument_list|,
name|from_user
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LDAPAccountImpl
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
specifier|public
name|LDAPAccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|password
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
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
name|LDAPAccountImpl
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
name|super
argument_list|(
name|realm
argument_list|,
name|config
argument_list|,
name|removed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|group
operator|instanceof
name|LDAPGroupImpl
condition|)
block|{
comment|//TODO
comment|//we dont support writes to LDAP yet!
return|return
literal|null
return|;
block|}
else|else
block|{
comment|//adds an LDAP User to a group from a different Realm
return|return
name|super
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|Group
name|group
init|=
name|getRealm
argument_list|()
operator|.
name|getGroup
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|//allow LDAP users to have groups from other realms
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
comment|//if the group is not present in this realm, look externally
name|group
operator|=
name|getRealm
argument_list|()
operator|.
name|getExternalGroup
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|addGroup
argument_list|(
name|group
argument_list|)
return|;
block|}
block|}
end_class

end_unit

