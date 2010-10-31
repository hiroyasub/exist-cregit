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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Iterator
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
name|dom
operator|.
name|DocumentImpl
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
name|internal
operator|.
name|GroupImpl
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
name|security
operator|.
name|utils
operator|.
name|Utils
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
name|storage
operator|.
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
specifier|public
specifier|abstract
class|class
name|AbstractRealm
implements|implements
name|Realm
implements|,
name|Configurable
block|{
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Account
argument_list|>
name|usersByName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Account
argument_list|>
argument_list|(
literal|65
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|groupsByName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
argument_list|(
literal|65
argument_list|)
decl_stmt|;
specifier|private
name|SecurityManager
name|sm
decl_stmt|;
specifier|protected
name|Configuration
name|configuration
decl_stmt|;
specifier|protected
name|Collection
name|collectionRealm
init|=
literal|null
decl_stmt|;
specifier|protected
name|Collection
name|collectionAccounts
init|=
literal|null
decl_stmt|;
specifier|protected
name|Collection
name|collectionGroups
init|=
literal|null
decl_stmt|;
specifier|protected
name|Collection
name|collectionRemovedAccounts
init|=
literal|null
decl_stmt|;
specifier|protected
name|Collection
name|collectionRemovedGroups
init|=
literal|null
decl_stmt|;
specifier|public
name|AbstractRealm
parameter_list|(
name|SecurityManager
name|sm
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|sm
operator|=
name|sm
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
name|Database
name|getDatabase
parameter_list|()
block|{
return|return
name|getSecurityManager
argument_list|()
operator|.
name|getDatabase
argument_list|()
return|;
block|}
specifier|protected
name|SecurityManager
name|getSecurityManager
parameter_list|()
block|{
return|return
name|sm
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
name|XmldbURI
name|realmCollectionURL
init|=
name|SecurityManager
operator|.
name|SECURITY_COLLETION_URI
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|BrokerPool
name|pool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collectionRealm
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|realmCollectionURL
argument_list|)
expr_stmt|;
name|collectionAccounts
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"accounts"
argument_list|)
argument_list|)
expr_stmt|;
name|collectionGroups
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"groups"
argument_list|)
argument_list|)
expr_stmt|;
name|collectionRemovedAccounts
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"accounts"
argument_list|)
operator|.
name|append
argument_list|(
literal|"removed"
argument_list|)
argument_list|)
expr_stmt|;
name|collectionRemovedGroups
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"groups"
argument_list|)
operator|.
name|append
argument_list|(
literal|"removed"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionRealm
operator|==
literal|null
operator|||
name|collectionAccounts
operator|==
literal|null
operator|||
name|collectionGroups
operator|==
literal|null
operator|||
name|collectionRemovedAccounts
operator|==
literal|null
operator|||
name|collectionRemovedGroups
operator|==
literal|null
condition|)
block|{
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|collectionRealm
operator|==
literal|null
condition|)
name|collectionRealm
operator|=
name|Utils
operator|.
name|createCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|realmCollectionURL
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionAccounts
operator|==
literal|null
condition|)
name|collectionAccounts
operator|=
name|Utils
operator|.
name|createCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"accounts"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionGroups
operator|==
literal|null
condition|)
name|collectionGroups
operator|=
name|Utils
operator|.
name|createCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"groups"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionRemovedAccounts
operator|==
literal|null
condition|)
name|collectionRemovedAccounts
operator|=
name|Utils
operator|.
name|createCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"accounts"
argument_list|)
operator|.
name|append
argument_list|(
literal|"removed"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionRemovedGroups
operator|==
literal|null
condition|)
name|collectionRemovedGroups
operator|=
name|Utils
operator|.
name|createCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|realmCollectionURL
operator|.
name|append
argument_list|(
literal|"groups"
argument_list|)
operator|.
name|append
argument_list|(
literal|"removed"
argument_list|)
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|//load groups information
if|if
condition|(
name|collectionGroups
operator|!=
literal|null
operator|&&
name|collectionGroups
operator|.
name|getDocumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collectionGroups
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Configuration
name|conf
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|conf
operator|.
name|getProperty
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|groupsByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//                      Group group = instantiateGroup(this, conf);
name|GroupImpl
name|group
init|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|getSecurityManager
argument_list|()
operator|.
name|addGroup
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|groupsByName
operator|.
name|put
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//load marked for remove groups information
if|if
condition|(
name|collectionRemovedGroups
operator|!=
literal|null
operator|&&
name|collectionRemovedGroups
operator|.
name|getDocumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collectionRemovedGroups
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Configuration
name|conf
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|id
init|=
name|conf
operator|.
name|getPropertyInteger
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
operator|!
name|getSecurityManager
argument_list|()
operator|.
name|hasGroup
argument_list|(
name|id
argument_list|)
condition|)
block|{
comment|//	            		G group = instantiateGroup(this, conf, true);
name|GroupImpl
name|group
init|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|group
operator|.
name|removed
operator|=
literal|true
expr_stmt|;
comment|//	            		getSecurityManager().addGroup(group.getId(), group);
block|}
block|}
block|}
comment|//load accounts information
if|if
condition|(
name|collectionAccounts
operator|!=
literal|null
operator|&&
name|collectionAccounts
operator|.
name|getDocumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collectionAccounts
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Configuration
name|conf
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|conf
operator|.
name|getProperty
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|usersByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//                        A account = instantiateAccount(this, conf);
name|Account
name|account
init|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|//		            	getSecurityManager().addUser(account.getId(), account);
name|usersByName
operator|.
name|put
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//load marked for remove accounts information
if|if
condition|(
name|collectionRemovedAccounts
operator|!=
literal|null
operator|&&
name|collectionRemovedAccounts
operator|.
name|getDocumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collectionRemovedAccounts
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Configuration
name|conf
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|id
init|=
name|conf
operator|.
name|getPropertyInteger
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
operator|!
name|getSecurityManager
argument_list|()
operator|.
name|hasUser
argument_list|(
name|id
argument_list|)
condition|)
block|{
comment|//                        A account = instantiateAccount(this, conf, true);
name|AccountImpl
name|account
init|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|account
operator|.
name|removed
operator|=
literal|true
expr_stmt|;
name|getSecurityManager
argument_list|()
operator|.
name|addUser
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//set collections
for|for
control|(
name|Group
name|group
range|:
name|groupsByName
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|group
operator|.
name|getId
argument_list|()
operator|>
literal|0
condition|)
operator|(
operator|(
name|AbstractPrincipal
operator|)
name|group
operator|)
operator|.
name|setCollection
argument_list|(
name|broker
argument_list|,
name|collectionGroups
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Account
name|account
range|:
name|usersByName
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|account
operator|.
name|getId
argument_list|()
operator|>
literal|0
condition|)
operator|(
operator|(
name|AbstractPrincipal
operator|)
name|account
operator|)
operator|.
name|setCollection
argument_list|(
name|broker
argument_list|,
name|collectionAccounts
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
block|{
name|configuration
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|//Accounts management methods
specifier|public
specifier|final
specifier|synchronized
name|Account
name|registerAccount
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
if|if
condition|(
name|usersByName
operator|.
name|containsKey
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Account "
operator|+
name|account
operator|.
name|getName
argument_list|()
operator|+
literal|" exist."
argument_list|)
throw|;
name|usersByName
operator|.
name|put
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|,
name|account
argument_list|)
expr_stmt|;
return|return
name|account
return|;
block|}
annotation|@
name|Override
specifier|public
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
return|return
name|usersByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|hasAccount
parameter_list|(
name|String
name|accountName
parameter_list|)
block|{
return|return
name|usersByName
operator|.
name|containsKey
argument_list|(
name|accountName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|hasAccount
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
return|return
name|usersByName
operator|.
name|containsKey
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Account
argument_list|>
name|getAccounts
parameter_list|()
block|{
return|return
name|usersByName
operator|.
name|values
argument_list|()
return|;
block|}
comment|//Groups management methods
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|groupsByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|boolean
name|hasGroup
parameter_list|(
name|Group
name|role
parameter_list|)
block|{
return|return
name|groupsByName
operator|.
name|containsKey
argument_list|(
name|role
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
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
return|return
name|groupsByName
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Group
argument_list|>
name|getRoles
parameter_list|()
block|{
return|return
name|groupsByName
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
specifier|synchronized
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Group
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groupsByName
operator|.
name|values
argument_list|()
return|;
block|}
comment|//configuration methods
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
comment|//collections related methods
specifier|protected
name|Collection
name|getCollection
parameter_list|()
block|{
return|return
name|collectionRealm
return|;
block|}
specifier|private
name|Group
name|_addGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
if|if
condition|(
name|groupsByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group "
operator|+
name|name
operator|+
literal|" exist."
argument_list|)
throw|;
name|Group
name|group
init|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
name|sm
operator|.
name|getNextGroupId
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
comment|//            Group group = instantiateGroup(this, getSecurityManager().getNextGroupId(), name);
name|getSecurityManager
argument_list|()
operator|.
name|addGroup
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|groupsByName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
return|return
name|group
return|;
block|}
specifier|private
name|Group
name|_addGroup
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
if|if
condition|(
name|groupsByName
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group "
operator|+
name|name
operator|+
literal|" exist."
argument_list|)
throw|;
if|if
condition|(
name|getSecurityManager
argument_list|()
operator|.
name|hasGroup
argument_list|(
name|id
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group id "
operator|+
name|id
operator|+
literal|" allready used."
argument_list|)
throw|;
name|Group
name|group
init|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
decl_stmt|;
comment|//      G group = instantiateGroup(this, id, name);
name|getSecurityManager
argument_list|()
operator|.
name|addGroup
argument_list|(
name|id
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|groupsByName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
return|return
name|group
return|;
block|}
specifier|public
specifier|synchronized
name|Group
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|Group
name|created_group
init|=
name|_addGroup
argument_list|(
name|name
argument_list|)
decl_stmt|;
operator|(
operator|(
name|AbstractPrincipal
operator|)
name|created_group
operator|)
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|created_group
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
return|return
name|addGroup
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
throws|,
name|ConfigurationException
block|{
if|if
condition|(
name|account
operator|.
name|getRealmId
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"Account's realmId is null."
argument_list|)
throw|;
if|if
condition|(
operator|!
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getRealmId
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"Account from different realm"
argument_list|)
throw|;
return|return
name|getSecurityManager
argument_list|()
operator|.
name|addAccount
argument_list|(
name|account
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|getDatabase
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Account
name|user
init|=
name|broker
operator|.
name|getUser
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|account
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|user
operator|.
name|hasDbaRole
argument_list|()
operator|)
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|" you are not allowed to change '"
operator|+
name|account
operator|.
name|getName
argument_list|()
operator|+
literal|"' account"
argument_list|)
throw|;
name|Account
name|updatingAccount
init|=
name|getAccount
argument_list|(
name|invokingUser
argument_list|,
name|account
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatingAccount
operator|==
literal|null
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
comment|//XXX: different exception
literal|"account "
operator|+
name|account
operator|.
name|getName
argument_list|()
operator|+
literal|" does not exist"
argument_list|)
throw|;
comment|//check: add account to group
name|String
index|[]
name|groups
init|=
name|account
operator|.
name|getGroups
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|updatingAccount
operator|.
name|hasGroup
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
operator|)
condition|)
block|{
name|updatingAccount
operator|.
name|addGroup
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|//check: remove account from group
name|groups
operator|=
name|updatingAccount
operator|.
name|getGroups
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|account
operator|.
name|hasGroup
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|hasDbaRole
argument_list|()
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"not allowed to change group memberships"
argument_list|)
throw|;
name|updatingAccount
operator|.
name|remGroup
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|updatingAccount
operator|.
name|setPassword
argument_list|(
name|account
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AbstractPrincipal
operator|)
name|updatingAccount
operator|)
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|getDatabase
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Group
name|getExternalGroup
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|getSecurityManager
argument_list|()
operator|.
name|getGroup
argument_list|(
name|invokingUser
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

