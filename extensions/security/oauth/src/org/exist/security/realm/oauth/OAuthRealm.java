begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|oauth
package|;
end_package

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
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|SchemaType
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

begin_import
import|import
name|org
operator|.
name|scribe
operator|.
name|exceptions
operator|.
name|OAuthException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"realm"
argument_list|)
comment|//TODO: id = OAuth
specifier|public
class|class
name|OAuthRealm
extends|extends
name|AbstractRealm
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|OAuthRealm
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|OAUTH
init|=
literal|"OAuth"
decl_stmt|;
specifier|protected
specifier|static
name|OAuthRealm
name|_
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"id"
argument_list|)
specifier|public
specifier|final
specifier|static
name|String
name|ID
init|=
literal|"OAuth"
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"version"
argument_list|)
specifier|public
specifier|final
specifier|static
name|String
name|version
init|=
literal|"1.0"
decl_stmt|;
comment|//@ConfigurationReferenceBy("name")
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"service"
argument_list|)
annotation|@
name|ConfigurationFieldClassMask
argument_list|(
literal|"org.exist.security.realm.oauth.Service"
argument_list|)
name|List
argument_list|<
name|Service
argument_list|>
name|services
decl_stmt|;
specifier|private
name|Group
name|primaryGroup
init|=
literal|null
decl_stmt|;
specifier|public
name|OAuthRealm
parameter_list|(
specifier|final
name|SecurityManagerImpl
name|sm
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|sm
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|_
operator|=
name|this
expr_stmt|;
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
name|String
name|getId
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
specifier|private
specifier|synchronized
name|Group
name|getPrimaryGroup
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|primaryGroup
operator|==
literal|null
condition|)
block|{
name|primaryGroup
operator|=
name|getGroup
argument_list|(
name|OAUTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|primaryGroup
operator|==
literal|null
condition|)
try|try
block|{
name|primaryGroup
operator|=
name|executeAsSystemUser
argument_list|(
operator|new
name|Unit
argument_list|<
name|Group
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Group
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
return|return
name|addGroup
argument_list|(
operator|new
name|GroupAider
argument_list|(
name|ID
argument_list|,
name|OAUTH
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|primaryGroup
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"OAuth realm can not create primary group 'OAuth'."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|primaryGroup
return|;
block|}
annotation|@
name|Override
specifier|public
name|Subject
name|authenticate
parameter_list|(
specifier|final
name|String
name|accountName
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
block|{
comment|// Auto-generated method stub
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
throws|,
name|ConfigurationException
block|{
comment|// Auto-generated method stub
return|return
literal|false
return|;
block|}
specifier|protected
name|Account
name|createAccountInDatabase
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|Map
argument_list|<
name|SchemaType
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
throws|throws
name|AuthenticationException
block|{
try|try
block|{
return|return
name|executeAsSystemUser
argument_list|(
operator|new
name|Unit
argument_list|<
name|Account
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Account
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
comment|//create the user account
name|UserAider
name|userAider
init|=
operator|new
name|UserAider
argument_list|(
name|ID
argument_list|,
name|username
argument_list|,
name|getPrimaryGroup
argument_list|()
argument_list|)
decl_stmt|;
comment|//store any requested metadata
for|for
control|(
name|Entry
argument_list|<
name|SchemaType
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|metadata
operator|.
name|entrySet
argument_list|()
control|)
name|userAider
operator|.
name|setMetadataValue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Account
name|account
init|=
name|getSecurityManager
argument_list|()
operator|.
name|addAccount
argument_list|(
name|userAider
argument_list|)
decl_stmt|;
return|return
name|account
return|;
block|}
block|}
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
block|}
specifier|public
name|Service
name|getServiceBulderByPath
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Service
name|service
range|:
name|services
control|)
block|{
if|if
condition|(
name|service
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|service
return|;
block|}
block|}
throw|throw
operator|new
name|OAuthException
argument_list|(
literal|"Service no found by name '"
operator|+
name|name
operator|+
literal|"'."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

