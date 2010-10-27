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
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Context
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
name|ldap
operator|.
name|InitialLdapContext
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"context"
argument_list|)
specifier|public
class|class
name|LdapContextFactory
implements|implements
name|Configurable
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LdapContextFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|SUN_CONNECTION_POOLING_PROPERTY
init|=
literal|"com.sun.jndi.ldap.connect.pool"
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"authentication"
argument_list|)
specifier|protected
name|String
name|authentication
init|=
literal|"simple"
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"principalPattern"
argument_list|)
specifier|protected
name|String
name|principalPattern
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageFormat
name|principalPatternFormat
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"url"
argument_list|)
specifier|protected
name|String
name|url
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|contextFactoryClassName
init|=
literal|"com.sun.jndi.ldap.LdapCtxFactory"
decl_stmt|;
specifier|protected
name|String
name|systemUsername
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|systemPassword
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|usePooling
init|=
literal|true
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|additionalEnvironment
decl_stmt|;
specifier|private
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"search"
argument_list|)
specifier|private
name|LDAPSearchContext
name|search
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"transformation"
argument_list|)
specifier|private
name|LDAPTransformationContext
name|realmTransformation
decl_stmt|;
specifier|public
name|LdapContextFactory
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
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
if|if
condition|(
name|principalPattern
operator|!=
literal|null
condition|)
block|{
name|principalPatternFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|principalPattern
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|LdapContext
name|getSystemLdapContext
parameter_list|()
throws|throws
name|NamingException
block|{
return|return
name|getLdapContext
argument_list|(
name|systemUsername
argument_list|,
name|systemPassword
argument_list|)
return|;
block|}
specifier|public
name|LdapContext
name|getLdapContext
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|NamingException
block|{
if|if
condition|(
name|url
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"An LDAP URL must be specified of the form ldap://<hostname>:<port>"
argument_list|)
throw|;
if|if
condition|(
name|username
operator|!=
literal|null
operator|&&
name|principalPattern
operator|!=
literal|null
condition|)
block|{
name|username
operator|=
name|principalPatternFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|username
block|}
argument_list|)
expr_stmt|;
block|}
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|env
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_AUTHENTICATION
argument_list|,
name|authentication
argument_list|)
expr_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|contextFactoryClassName
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|url
argument_list|)
expr_stmt|;
comment|// the following is helpful in debugging errors
comment|//env.put("com.sun.jndi.ldap.trace.ber", System.err);
comment|// Only pool connections for system contexts
if|if
condition|(
name|usePooling
operator|&&
name|username
operator|!=
literal|null
operator|&&
name|username
operator|.
name|equals
argument_list|(
name|systemUsername
argument_list|)
condition|)
block|{
comment|// Enable connection pooling
name|env
operator|.
name|put
argument_list|(
name|SUN_CONNECTION_POOLING_PROPERTY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|additionalEnvironment
operator|!=
literal|null
condition|)
block|{
name|env
operator|.
name|putAll
argument_list|(
name|additionalEnvironment
argument_list|)
expr_stmt|;
block|}
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
literal|"Initializing LDAP context using URL ["
operator|+
name|url
operator|+
literal|"] and username ["
operator|+
name|username
operator|+
literal|"] "
operator|+
literal|"with pooling ["
operator|+
operator|(
name|usePooling
condition|?
literal|"enabled"
else|:
literal|"disabled"
operator|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InitialLdapContext
argument_list|(
name|env
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|LDAPSearchContext
name|getSearch
parameter_list|()
block|{
return|return
name|search
return|;
block|}
specifier|public
name|LDAPTransformationContext
name|getTransformationContext
parameter_list|()
block|{
return|return
name|realmTransformation
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
block|}
end_class

end_unit

