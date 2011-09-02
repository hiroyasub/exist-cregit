begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
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
name|collections
operator|.
name|Collection
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
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
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
name|Realm
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
name|BrokerPool
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
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
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
name|AbstractPrincipal
implements|implements
name|Principal
block|{
specifier|private
name|Realm
name|realm
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"name"
argument_list|)
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"id"
argument_list|)
specifier|protected
specifier|final
name|int
name|id
decl_stmt|;
comment|//XXX: this must be under org.exist.security.internal to make it protected
specifier|public
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
specifier|protected
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
specifier|public
name|AbstractPrincipal
parameter_list|(
name|Realm
name|realm
parameter_list|,
name|Collection
name|collection
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
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|BrokerPool
name|database
decl_stmt|;
try|try
block|{
name|database
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|database
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Configuration
name|_config_
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
operator|+
literal|".xml"
argument_list|)
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|database
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|AbstractPrincipal
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Realm
name|realm
parameter_list|,
name|Collection
name|collection
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
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
try|try
block|{
name|Configuration
name|_config_
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
operator|+
literal|".xml"
argument_list|)
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|AbstractPrincipal
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|Configuration
name|_config_
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|configuration
operator|.
name|getPropertyInteger
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|configuration
operator|.
name|getProperty
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|ConfigurationException
throws|,
name|PermissionDeniedException
block|{
if|if
condition|(
name|configuration
operator|!=
literal|null
condition|)
block|{
name|configuration
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|ConfigurationException
throws|,
name|PermissionDeniedException
block|{
if|if
condition|(
name|configuration
operator|!=
literal|null
condition|)
block|{
name|configuration
operator|.
name|save
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|Realm
name|getRealm
parameter_list|()
block|{
return|return
name|realm
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRealmId
parameter_list|()
block|{
return|return
name|realm
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
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
specifier|final
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
specifier|public
specifier|final
name|void
name|setCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|ConfigurationException
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|Configurator
operator|.
name|unregister
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|Configuration
name|_config_
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
operator|+
literal|".xml"
argument_list|)
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|void
name|setCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|ConfigurationException
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|Configurator
operator|.
name|unregister
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|Configuration
name|_config_
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Database
name|getDatabase
parameter_list|()
block|{
return|return
name|realm
operator|.
name|getDatabase
argument_list|()
return|;
block|}
specifier|public
name|void
name|setRemoved
parameter_list|(
name|boolean
name|removed
parameter_list|)
block|{
name|this
operator|.
name|removed
operator|=
name|removed
expr_stmt|;
block|}
block|}
end_class

end_unit

