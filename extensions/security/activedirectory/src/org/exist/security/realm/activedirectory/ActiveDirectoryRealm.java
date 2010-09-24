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
name|activedirectory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Attribute
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
name|Attributes
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
name|AccountImpl
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
name|realm
operator|.
name|ldap
operator|.
name|LDAPRealm
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
name|realm
operator|.
name|ldap
operator|.
name|LdapContextFactory
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
comment|//TODO: id = ActiveDirectory
specifier|public
class|class
name|ActiveDirectoryRealm
extends|extends
name|LDAPRealm
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
literal|"ActiveDirectory"
decl_stmt|;
specifier|public
name|ActiveDirectoryRealm
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
name|ContextFactory
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.security.Realm#getId() 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.security.Realm#authenticate(java.lang.String, 	 * java.lang.Object) 	 */
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
name|String
name|returnedAtts
index|[]
init|=
block|{
literal|"sn"
block|,
literal|"givenName"
block|,
literal|"mail"
block|}
decl_stmt|;
name|String
name|searchFilter
init|=
literal|"(&(objectClass=user)(sAMAccountName="
operator|+
name|username
operator|+
literal|"))"
decl_stmt|;
comment|// Create the search controls
name|SearchControls
name|searchCtls
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|searchCtls
operator|.
name|setReturningAttributes
argument_list|(
name|returnedAtts
argument_list|)
expr_stmt|;
comment|// Specify the search scope
name|searchCtls
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|SUBTREE_SCOPE
argument_list|)
expr_stmt|;
name|LdapContext
name|ctxGC
init|=
literal|null
decl_stmt|;
name|boolean
name|ldapUser
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ctxGC
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
comment|// Search objects in GC using filters
name|NamingEnumeration
argument_list|<
name|SearchResult
argument_list|>
name|answer
init|=
name|ctxGC
operator|.
name|search
argument_list|(
operator|(
operator|(
name|ContextFactory
operator|)
name|ensureContextFactory
argument_list|()
operator|)
operator|.
name|getSearchBase
argument_list|()
argument_list|,
name|searchFilter
argument_list|,
name|searchCtls
argument_list|)
decl_stmt|;
while|while
condition|(
name|answer
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|SearchResult
name|sr
init|=
name|answer
operator|.
name|next
argument_list|()
decl_stmt|;
name|Attributes
name|attrs
init|=
name|sr
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|amap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|attrs
operator|!=
literal|null
condition|)
block|{
name|amap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|NamingEnumeration
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|ne
init|=
name|attrs
operator|.
name|getAll
argument_list|()
decl_stmt|;
while|while
condition|(
name|ne
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|Attribute
name|attr
init|=
name|ne
operator|.
name|next
argument_list|()
decl_stmt|;
name|amap
operator|.
name|put
argument_list|(
name|attr
operator|.
name|getID
argument_list|()
argument_list|,
name|attr
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ldapUser
operator|=
literal|true
expr_stmt|;
block|}
name|ne
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|ldapUser
condition|)
block|{
try|try
block|{
name|AbstractAccount
name|account
init|=
operator|(
name|AbstractAccount
operator|)
name|getAccount
argument_list|(
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
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
name|username
argument_list|)
expr_stmt|;
comment|//TODO: addAccount(account);
block|}
return|return
operator|new
name|SubjectAccreditedImpl
argument_list|(
name|account
argument_list|,
name|ctxGC
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
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
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

