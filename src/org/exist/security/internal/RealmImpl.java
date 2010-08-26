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
name|internal
package|;
end_package

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
name|collections
operator|.
name|IndexInfo
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
name|SecurityManager
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
name|UUIDGenerator
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
name|sync
operator|.
name|Sync
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
name|util
operator|.
name|MimeType
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
class|class
name|RealmImpl
extends|extends
name|AbstractRealm
implements|implements
name|Configurable
block|{
specifier|public
specifier|static
name|String
name|ID
init|=
literal|"exist"
decl_stmt|;
comment|//TODO: final "eXist-db";
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
name|Realm
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|public
name|void
name|setPasswordRealm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|ID
operator|=
name|value
expr_stmt|;
block|}
specifier|protected
specifier|final
specifier|static
name|String
name|ACL_FILE
init|=
literal|"users.xml"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|XmldbURI
name|ACL_FILE_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|ACL_FILE
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Account
name|ACCOUNT_SYSTEM
decl_stmt|;
specifier|protected
specifier|final
name|Account
name|ACCOUNT_GUEST
decl_stmt|;
specifier|protected
specifier|final
name|Group
name|GROUP_DBA
decl_stmt|;
specifier|protected
specifier|final
name|Group
name|GROUP_GUEST
decl_stmt|;
specifier|protected
specifier|final
name|Account
name|ACCOUNT_UNKNOW
decl_stmt|;
specifier|protected
specifier|final
name|Group
name|GROUP_UNKNOW
decl_stmt|;
specifier|protected
name|RealmImpl
parameter_list|(
name|SecurityManagerImpl
name|sm
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|ConfigurationException
block|{
comment|//, Configuration conf
name|super
argument_list|(
name|sm
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|//Build-in accounts
name|GROUP_UNKNOW
operator|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ACCOUNT_UNKNOW
operator|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
name|ACCOUNT_UNKNOW
operator|.
name|addGroup
argument_list|(
name|GROUP_UNKNOW
argument_list|)
expr_stmt|;
comment|//DBA group& account
name|GROUP_DBA
operator|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
expr_stmt|;
name|sm
operator|.
name|groupsById
operator|.
name|put
argument_list|(
name|GROUP_DBA
operator|.
name|getId
argument_list|()
argument_list|,
name|GROUP_DBA
argument_list|)
expr_stmt|;
name|groupsByName
operator|.
name|put
argument_list|(
name|GROUP_DBA
operator|.
name|getName
argument_list|()
argument_list|,
name|GROUP_DBA
argument_list|)
expr_stmt|;
comment|//System account
name|ACCOUNT_SYSTEM
operator|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
literal|"SYSTEM"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ACCOUNT_SYSTEM
operator|.
name|addGroup
argument_list|(
name|GROUP_DBA
argument_list|)
expr_stmt|;
name|sm
operator|.
name|usersById
operator|.
name|put
argument_list|(
name|ACCOUNT_SYSTEM
operator|.
name|getId
argument_list|()
argument_list|,
name|ACCOUNT_SYSTEM
argument_list|)
expr_stmt|;
comment|//usersByName.put(ACCOUNT_SYSTEM.getName(), ACCOUNT_SYSTEM);
comment|//Administrator account
name|AccountImpl
name|ACCOUNT_ADMIN
init|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|ACCOUNT_ADMIN
operator|.
name|addGroup
argument_list|(
name|GROUP_DBA
argument_list|)
expr_stmt|;
name|sm
operator|.
name|usersById
operator|.
name|put
argument_list|(
name|ACCOUNT_ADMIN
operator|.
name|getId
argument_list|()
argument_list|,
name|ACCOUNT_ADMIN
argument_list|)
expr_stmt|;
name|usersByName
operator|.
name|put
argument_list|(
name|ACCOUNT_ADMIN
operator|.
name|getName
argument_list|()
argument_list|,
name|ACCOUNT_ADMIN
argument_list|)
expr_stmt|;
comment|//Guest group& account
name|GROUP_GUEST
operator|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
literal|2
argument_list|,
name|SecurityManager
operator|.
name|GUEST_GROUP
argument_list|)
expr_stmt|;
name|sm
operator|.
name|groupsById
operator|.
name|put
argument_list|(
name|GROUP_GUEST
operator|.
name|getId
argument_list|()
argument_list|,
name|GROUP_GUEST
argument_list|)
expr_stmt|;
name|groupsByName
operator|.
name|put
argument_list|(
name|GROUP_GUEST
operator|.
name|getName
argument_list|()
argument_list|,
name|GROUP_GUEST
argument_list|)
expr_stmt|;
name|ACCOUNT_GUEST
operator|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
literal|2
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
expr_stmt|;
name|ACCOUNT_GUEST
operator|.
name|addGroup
argument_list|(
name|GROUP_GUEST
argument_list|)
expr_stmt|;
name|sm
operator|.
name|usersById
operator|.
name|put
argument_list|(
name|ACCOUNT_GUEST
operator|.
name|getId
argument_list|()
argument_list|,
name|ACCOUNT_GUEST
argument_list|)
expr_stmt|;
name|usersByName
operator|.
name|put
argument_list|(
name|ACCOUNT_GUEST
operator|.
name|getName
argument_list|()
argument_list|,
name|ACCOUNT_GUEST
argument_list|)
expr_stmt|;
name|sm
operator|.
name|lastUserId
operator|=
literal|3
expr_stmt|;
name|sm
operator|.
name|lastGroupId
operator|=
literal|3
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
name|sm
operator|.
name|groupsById
operator|.
name|put
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
name|sm
operator|.
name|groupsById
operator|.
name|containsKey
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
name|sm
operator|.
name|groupsById
operator|.
name|put
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
name|sm
operator|.
name|addAccount
argument_list|(
name|account
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|updateAccount
parameter_list|(
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
name|sm
operator|.
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
literal|"' user"
argument_list|)
throw|;
name|Account
name|updatingAccount
init|=
name|getAccount
argument_list|(
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
literal|"user "
operator|+
name|account
operator|.
name|getName
argument_list|()
operator|+
literal|" does not exist"
argument_list|)
throw|;
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
comment|//XXX: delete group from account information
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
name|sm
operator|.
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
specifier|public
specifier|synchronized
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
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|AbstractAccount
name|remove_account
init|=
operator|(
name|AbstractAccount
operator|)
name|usersByName
operator|.
name|get
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|remove_account
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|sm
operator|.
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
literal|" you are not allowed to delete '"
operator|+
name|account
operator|.
name|getName
argument_list|()
operator|+
literal|"' user"
argument_list|)
throw|;
name|remove_account
operator|.
name|removed
operator|=
literal|true
expr_stmt|;
name|remove_account
operator|.
name|setCollection
argument_list|(
name|broker
argument_list|,
name|collectionRemovedAccounts
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
operator|+
literal|".xml"
argument_list|)
argument_list|)
expr_stmt|;
name|TransactionManager
name|transaction
init|=
name|sm
operator|.
name|getDatabase
argument_list|()
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
name|txn
operator|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|collectionAccounts
operator|.
name|removeXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|remove_account
operator|.
name|getName
argument_list|()
operator|+
literal|".xml"
argument_list|)
argument_list|)
expr_stmt|;
name|transaction
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
name|transaction
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading configuration failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sm
operator|.
name|usersById
operator|.
name|put
argument_list|(
name|remove_account
operator|.
name|getId
argument_list|()
argument_list|,
name|remove_account
argument_list|)
expr_stmt|;
name|usersByName
operator|.
name|remove
argument_list|(
name|remove_account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|sm
operator|.
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
specifier|public
specifier|synchronized
name|boolean
name|updateGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//nothing to do: the name or id can't be changed
return|return
literal|false
return|;
block|}
specifier|public
specifier|synchronized
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
if|if
condition|(
name|group
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|AbstractPrincipal
name|remove_group
init|=
operator|(
name|AbstractPrincipal
operator|)
name|groupsByName
operator|.
name|get
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|remove_group
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|sm
operator|.
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
literal|" you are not allowed to delete '"
operator|+
name|remove_group
operator|.
name|getName
argument_list|()
operator|+
literal|"' group"
argument_list|)
throw|;
name|remove_group
operator|.
name|removed
operator|=
literal|true
expr_stmt|;
name|remove_group
operator|.
name|setCollection
argument_list|(
name|broker
argument_list|,
name|collectionRemovedGroups
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
operator|+
literal|".xml"
argument_list|)
argument_list|)
expr_stmt|;
name|TransactionManager
name|transaction
init|=
name|sm
operator|.
name|getDatabase
argument_list|()
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
name|txn
operator|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|collectionGroups
operator|.
name|removeXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|remove_group
operator|.
name|getName
argument_list|()
operator|+
literal|".xml"
argument_list|)
argument_list|)
expr_stmt|;
name|transaction
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
name|transaction
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading configuration failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sm
operator|.
name|groupsById
operator|.
name|put
argument_list|(
name|remove_group
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|Group
operator|)
name|remove_group
argument_list|)
expr_stmt|;
name|groupsByName
operator|.
name|remove
argument_list|(
name|remove_group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|sm
operator|.
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
specifier|synchronized
name|Subject
name|authenticate
parameter_list|(
name|String
name|accountName
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
block|{
name|Account
name|user
init|=
name|getAccount
argument_list|(
name|accountName
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|ACCOUNT_NOT_FOUND
argument_list|,
literal|"Acount "
operator|+
name|accountName
operator|+
literal|" not found"
argument_list|)
throw|;
name|Subject
name|newUser
init|=
operator|new
name|SubjectImpl
argument_list|(
operator|(
name|AccountImpl
operator|)
name|user
argument_list|,
name|credentials
argument_list|)
decl_stmt|;
if|if
condition|(
name|newUser
operator|.
name|isAuthenticated
argument_list|()
condition|)
return|return
name|newUser
return|;
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|WRONG_PASSWORD
argument_list|,
literal|"Wrong password for user ["
operator|+
name|accountName
operator|+
literal|"] "
argument_list|)
throw|;
block|}
specifier|private
name|void
name|__save
parameter_list|()
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
name|TransactionManager
name|transact
init|=
name|sm
operator|.
name|getDatabase
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|sm
operator|.
name|getDatabase
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|_save
argument_list|(
name|broker
argument_list|,
name|txn
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
name|EXistException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|sm
operator|.
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
specifier|private
specifier|synchronized
name|void
name|_save
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
comment|//LOG.debug("storing acl file");
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<!-- Central user configuration. Editing this document will cause the security "
operator|+
literal|"to reload and update its internal database. Please handle with care! -->"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<auth version='1.0'>"
argument_list|)
expr_stmt|;
comment|// save groups
name|buf
operator|.
name|append
argument_list|(
literal|"<!-- Please do not remove the guest and admin groups -->"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<groups last-id='"
operator|+
name|sm
operator|.
name|lastGroupId
operator|+
literal|"'>"
argument_list|)
expr_stmt|;
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
name|buf
operator|.
name|append
argument_list|(
name|group
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</groups>"
argument_list|)
expr_stmt|;
comment|//save users
name|buf
operator|.
name|append
argument_list|(
literal|"<!-- Please do not remove the admin user. -->"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<users last-id='"
operator|+
name|sm
operator|.
name|lastUserId
operator|+
literal|"'>"
argument_list|)
expr_stmt|;
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
name|buf
operator|.
name|append
argument_list|(
name|account
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</users>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</auth>"
argument_list|)
expr_stmt|;
comment|// store users.xml
comment|//broker.flush();
comment|//broker.sync(Sync.MAJOR_SYNC);
name|Subject
name|currentUser
init|=
name|broker
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|sm
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
name|sysCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|IndexInfo
name|info
init|=
name|sysCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|ACL_FILE_URI
argument_list|,
name|data
argument_list|)
decl_stmt|;
comment|//TODO : unlock the collection here ?
name|DocumentImpl
name|doc
init|=
name|info
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|MimeType
operator|.
name|XML_TYPE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setPermissions
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|sysCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|doc
operator|.
name|getCollection
argument_list|()
argument_list|)
expr_stmt|;
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
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|currentUser
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

