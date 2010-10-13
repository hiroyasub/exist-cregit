begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

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
name|NamingEnumeration
import|;
end_import

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
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchControls
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|ldap
operator|.
name|LdapContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|annotation
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
name|AuthenticationException
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
name|Subject
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
name|internal
operator|.
name|AbstractAccount
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
name|SecurityManagerImpl
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
name|SubjectAccreditedImpl
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
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"realm"
argument_list|)
comment|//TODO: id = LDAP
specifier|public
class|class
name|LDAPRealm
extends|extends
name|AbstractRealm
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LDAPRealm
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|String
name|ID
init|=
literal|"LDAP"
decl_stmt|;
specifier|protected
name|LdapContextFactory
name|ldapContextFactory
init|=
literal|null
decl_stmt|;
specifier|public
name|LDAPRealm
parameter_list|(
name|SecurityManagerImpl
name|sm
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|sm
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|LdapContextFactory
name|ensureContextFactory
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|ldapContextFactory
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No LdapContextFactory specified - creating a default instance."
argument_list|)
expr_stmt|;
block|}
name|LdapContextFactory
name|factory
init|=
operator|new
name|LdapContextFactory
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|this
operator|.
name|ldapContextFactory
operator|=
name|factory
expr_stmt|;
block|}
return|return
name|this
operator|.
name|ldapContextFactory
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startUp
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
operator|.
name|startUp
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Subject
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
block|{
comment|// Binds using the username and password provided by the user.
name|LdapContext
name|ctx
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ctx
operator|=
name|ensureContextFactory
argument_list|()
operator|.
name|getLdapContext
argument_list|(
name|username
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|credentials
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|UNNOWN_EXCEPTION
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|LdapUtils
operator|.
name|closeContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
name|AbstractAccount
name|account
init|=
operator|(
name|AbstractAccount
operator|)
name|getAccount
argument_list|(
literal|null
argument_list|,
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|account
operator|=
operator|(
name|AbstractAccount
operator|)
name|createAccountInDatabase
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AuthenticatedLdapSubjectAccreditedImpl
argument_list|(
name|account
argument_list|,
name|ctx
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|credentials
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Account
name|createAccountInDatabase
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|AuthenticationException
block|{
name|Subject
name|currentSubject
init|=
name|getDatabase
argument_list|()
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|getDatabase
argument_list|()
operator|.
name|setSubject
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sm
operator|.
name|addAccount
argument_list|(
operator|new
name|UserAider
argument_list|(
name|ID
argument_list|,
name|username
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|UNNOWN_EXCEPTION
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|getDatabase
argument_list|()
operator|.
name|setSubject
argument_list|(
name|currentSubject
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Group
name|createGroupInDatabase
parameter_list|(
name|String
name|groupname
parameter_list|)
throws|throws
name|AuthenticationException
block|{
name|Subject
name|currentSubject
init|=
name|getDatabase
argument_list|()
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
block|{
name|getDatabase
argument_list|()
operator|.
name|setSubject
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sm
operator|.
name|addGroup
argument_list|(
operator|new
name|GroupAider
argument_list|(
name|ID
argument_list|,
name|groupname
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|UNNOWN_EXCEPTION
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|getDatabase
argument_list|()
operator|.
name|setSubject
argument_list|(
name|currentSubject
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|Account
name|getAccount
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|//first attempt to get the cached account
name|Account
name|acct
init|=
name|super
operator|.
name|getAccount
argument_list|(
name|invokingUser
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|acct
operator|!=
literal|null
condition|)
block|{
return|return
name|acct
return|;
block|}
else|else
block|{
if|if
condition|(
name|invokingUser
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|invokingUser
operator|instanceof
name|AuthenticatedLdapSubjectAccreditedImpl
condition|)
block|{
comment|//if the account is not cached, we should try and find it in LDAP and cache it if it exists
name|LdapContext
name|ctx
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ctx
operator|=
name|ensureContextFactory
argument_list|()
operator|.
name|getLdapContext
argument_list|(
name|invokingUser
operator|.
name|getUsername
argument_list|()
argument_list|,
operator|(
operator|(
name|AuthenticatedLdapSubjectAccreditedImpl
operator|)
name|invokingUser
operator|)
operator|.
name|getAuthenticatedCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|SearchResult
name|ldapUser
init|=
name|findUserByAccountName
argument_list|(
name|ctx
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ldapUser
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|//found a user from ldap so cache them and return
try|try
block|{
return|return
name|createAccountInDatabase
argument_list|(
name|name
argument_list|)
return|;
comment|//registerAccount(acct); //TODO do we need this
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ae
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|UNNOWN_EXCEPTION
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|LdapUtils
operator|.
name|closeContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|Group
name|getGroup
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Group
name|grp
init|=
name|groupsByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|grp
operator|!=
literal|null
condition|)
block|{
return|return
name|grp
return|;
block|}
else|else
block|{
if|if
condition|(
name|invokingUser
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|invokingUser
operator|instanceof
name|AuthenticatedLdapSubjectAccreditedImpl
condition|)
block|{
comment|//if the group is not cached, we should try and find it in LDAP and cache it if it exists
name|LdapContext
name|ctx
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ctx
operator|=
name|ensureContextFactory
argument_list|()
operator|.
name|getLdapContext
argument_list|(
name|invokingUser
operator|.
name|getUsername
argument_list|()
argument_list|,
operator|(
operator|(
name|AuthenticatedLdapSubjectAccreditedImpl
operator|)
name|invokingUser
operator|)
operator|.
name|getAuthenticatedCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|SearchResult
name|ldapGroup
init|=
name|findGroupByGroupName
argument_list|(
name|ctx
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ldapGroup
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|//found a user from ldap so cache them and return
try|try
block|{
return|return
name|createGroupInDatabase
argument_list|(
name|name
argument_list|)
return|;
comment|//registerGroup(grp); //TODO do we need to do this?
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ae
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|UNNOWN_EXCEPTION
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|LdapUtils
operator|.
name|closeContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|SearchResult
name|findUserByAccountName
parameter_list|(
name|DirContext
name|ctx
parameter_list|,
name|String
name|accountName
parameter_list|)
throws|throws
name|NamingException
block|{
name|String
name|userName
init|=
name|accountName
decl_stmt|;
if|if
condition|(
name|userName
operator|.
name|indexOf
argument_list|(
literal|"@"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|userName
operator|=
name|userName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|userName
operator|.
name|indexOf
argument_list|(
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|searchFilter
init|=
literal|"(&(objectClass=user)(sAMAccountName="
operator|+
name|userName
operator|+
literal|"))"
decl_stmt|;
name|SearchControls
name|searchControls
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|searchControls
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|SUBTREE_SCOPE
argument_list|)
expr_stmt|;
comment|//TODO dont hardcode the search base!
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|results
init|=
name|ctx
operator|.
name|search
argument_list|(
name|ensureContextFactory
argument_list|()
operator|.
name|getBase
argument_list|()
argument_list|,
name|searchFilter
argument_list|,
name|searchControls
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|SearchResult
name|searchResult
init|=
operator|(
name|SearchResult
operator|)
name|results
operator|.
name|nextElement
argument_list|()
decl_stmt|;
comment|//make sure there is not another item available, there should be only 1 match
if|if
condition|(
name|results
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Matched multiple users for the accountName: "
operator|+
name|accountName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|searchResult
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|SearchResult
name|findGroupByGroupName
parameter_list|(
name|DirContext
name|ctx
parameter_list|,
name|String
name|groupName
parameter_list|)
throws|throws
name|NamingException
block|{
name|String
name|searchFilter
init|=
literal|"(&(objectClass=group)(sAMAccountName="
operator|+
name|groupName
operator|+
literal|"))"
decl_stmt|;
name|SearchControls
name|searchControls
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|searchControls
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|SUBTREE_SCOPE
argument_list|)
expr_stmt|;
comment|//TODO dont hardcode the search base!
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|results
init|=
name|ctx
operator|.
name|search
argument_list|(
name|ensureContextFactory
argument_list|()
operator|.
name|getBase
argument_list|()
argument_list|,
name|searchFilter
argument_list|,
name|searchControls
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|SearchResult
name|searchResult
init|=
operator|(
name|SearchResult
operator|)
name|results
operator|.
name|nextElement
argument_list|()
decl_stmt|;
comment|//make sure there is not another item available, there should be only 1 match
if|if
condition|(
name|results
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Matched multiple groups for the groupName: "
operator|+
name|groupName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|searchResult
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|// configurable methods
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
annotation|@
name|Override
specifier|public
name|Account
name|addAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|updateAccount
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteAccount
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
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
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|updateGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
specifier|private
specifier|final
class|class
name|AuthenticatedLdapSubjectAccreditedImpl
extends|extends
name|SubjectAccreditedImpl
block|{
specifier|private
specifier|final
name|String
name|authenticatedCredentials
decl_stmt|;
specifier|private
name|AuthenticatedLdapSubjectAccreditedImpl
parameter_list|(
name|AbstractAccount
name|account
parameter_list|,
name|LdapContext
name|ctx
parameter_list|,
name|String
name|authenticatedCredentials
parameter_list|)
block|{
name|super
argument_list|(
name|account
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|authenticatedCredentials
operator|=
name|authenticatedCredentials
expr_stmt|;
block|}
specifier|private
name|String
name|getAuthenticatedCredentials
parameter_list|()
block|{
return|return
name|authenticatedCredentials
return|;
block|}
block|}
block|}
end_class

end_unit

