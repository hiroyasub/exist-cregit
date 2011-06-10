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
operator|.
name|internal
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
name|security
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
name|AbstractPrincipal
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
name|ConfigurationException
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
class|class
name|RealmImpl
extends|extends
name|AbstractRealm
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
specifier|public
specifier|final
specifier|static
name|int
name|SYSTEM_ACCOUNT_ID
init|=
literal|1048575
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ADMIN_ACCOUNT_ID
init|=
literal|1048574
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|GUEST_ACCOUNT_ID
init|=
literal|1048573
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNKNOWN_ACCOUNT_ID
init|=
literal|1048572
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DBA_GROUP_ID
init|=
literal|1048575
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|GUEST_GROUP_ID
init|=
literal|1048574
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNKNOWN_GROUP_ID
init|=
literal|1048573
decl_stmt|;
specifier|protected
specifier|final
name|AccountImpl
name|ACCOUNT_SYSTEM
decl_stmt|;
specifier|protected
specifier|final
name|AccountImpl
name|ACCOUNT_GUEST
decl_stmt|;
specifier|protected
specifier|final
name|GroupImpl
name|GROUP_DBA
decl_stmt|;
specifier|protected
specifier|final
name|GroupImpl
name|GROUP_GUEST
decl_stmt|;
specifier|protected
specifier|final
name|AccountImpl
name|ACCOUNT_UNKNOWN
decl_stmt|;
specifier|protected
specifier|final
name|GroupImpl
name|GROUP_UNKNOWN
decl_stmt|;
comment|//@ConfigurationFieldAsElement("allow-guest-authentication")
specifier|public
name|boolean
name|allowGuestAuthentication
init|=
literal|true
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
name|GROUP_UNKNOWN
operator|=
operator|new
name|GroupImpl
argument_list|(
name|this
argument_list|,
name|UNKNOWN_GROUP_ID
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ACCOUNT_UNKNOWN
operator|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
name|UNKNOWN_ACCOUNT_ID
argument_list|,
literal|""
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|,
name|GROUP_UNKNOWN
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
name|DBA_GROUP_ID
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
name|SYSTEM_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|SYSTEM
argument_list|,
literal|""
argument_list|,
name|GROUP_DBA
argument_list|,
literal|true
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
name|usersByName
operator|.
name|put
argument_list|(
name|ACCOUNT_SYSTEM
operator|.
name|getName
argument_list|()
argument_list|,
name|ACCOUNT_SYSTEM
argument_list|)
expr_stmt|;
comment|//Administrator account
name|AccountImpl
name|ACCOUNT_ADMIN
init|=
operator|new
name|AccountImpl
argument_list|(
name|this
argument_list|,
name|ADMIN_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|,
literal|""
argument_list|,
name|GROUP_DBA
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
name|GUEST_GROUP_ID
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
name|GUEST_ACCOUNT_ID
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|,
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
comment|//XXX: GROUP_DBA._addManager(ACCOUNT_ADMIN);
comment|//XXX: GROUP_GUEST._addManager(ACCOUNT_ADMIN);
name|sm
operator|.
name|lastUserId
operator|=
literal|10
expr_stmt|;
name|sm
operator|.
name|lastGroupId
operator|=
literal|10
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
specifier|synchronized
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
name|getSubject
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
name|setRemoved
argument_list|(
literal|true
argument_list|)
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
name|getSecurityManager
argument_list|()
operator|.
name|addUser
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
name|getDatabase
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Subject
name|subject
init|=
name|broker
operator|.
name|getSubject
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|subject
operator|.
name|hasDbaRole
argument_list|()
operator|)
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|" you ["
operator|+
name|subject
operator|.
name|getName
argument_list|()
operator|+
literal|"] are not allowed to delete '"
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
name|setRemoved
argument_list|(
literal|true
argument_list|)
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
name|getSecurityManager
argument_list|()
operator|.
name|addGroup
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
name|account
init|=
name|getAccount
argument_list|(
literal|null
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
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
literal|"Acount '"
operator|+
name|accountName
operator|+
literal|"' not found."
argument_list|)
throw|;
if|if
condition|(
literal|"SYSTEM"
operator|.
name|equals
argument_list|(
name|accountName
argument_list|)
operator|||
operator|(
operator|!
name|allowGuestAuthentication
operator|&&
literal|"guest"
operator|.
name|equals
argument_list|(
name|accountName
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|ACCOUNT_NOT_FOUND
argument_list|,
literal|"Acount '"
operator|+
name|accountName
operator|+
literal|"' can not be used."
argument_list|)
throw|;
if|if
condition|(
operator|!
name|account
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|ACCOUNT_LOCKED
argument_list|,
literal|"Acount '"
operator|+
name|accountName
operator|+
literal|"' is dissabled."
argument_list|)
throw|;
block|}
name|Subject
name|subject
init|=
operator|new
name|SubjectImpl
argument_list|(
operator|(
name|AccountImpl
operator|)
name|account
argument_list|,
name|credentials
argument_list|)
decl_stmt|;
if|if
condition|(
name|subject
operator|.
name|isAuthenticated
argument_list|()
condition|)
block|{
return|return
name|subject
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findUsernamesWhereNameStarts
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|startsWith
parameter_list|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
comment|//at present exist users cannot have personal name details
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findUsernamesWhereUsernameStarts
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|startsWith
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|userNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|userName
range|:
name|usersByName
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|userName
operator|.
name|startsWith
argument_list|(
name|startsWith
argument_list|)
condition|)
block|{
name|userNames
operator|.
name|add
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|userNames
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findAllGroupNames
parameter_list|(
name|Subject
name|invokingUser
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groupNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Group
name|group
range|:
name|getGroups
argument_list|()
control|)
block|{
name|groupNames
operator|.
name|add
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|groupNames
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findAllGroupMembers
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|groupName
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groupMembers
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
name|account
range|:
name|getAccounts
argument_list|()
control|)
block|{
if|if
condition|(
name|account
operator|.
name|hasGroup
argument_list|(
name|groupName
argument_list|)
condition|)
block|{
name|groupMembers
operator|.
name|add
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|groupMembers
return|;
block|}
comment|//    @Override
comment|//    public GroupImpl instantiateGroup(AbstractRealm realm, Configuration config) throws ConfigurationException {
comment|//        return new GroupImpl(realm, config);
comment|//    }
comment|//
comment|//    @Override
comment|//    public AccountImpl instantiateAccount(AbstractRealm realm, Configuration config) throws ConfigurationException {
comment|//        return new AccountImpl(realm, config);
comment|//    }
comment|//
comment|//    @Override
comment|//    public GroupImpl instantiateGroup(AbstractRealm realm, Configuration config, boolean removed) throws ConfigurationException {
comment|//        return new GroupImpl(realm, config, true);
comment|//    }
comment|//
comment|//    @Override
comment|//    public AccountImpl instantiateAccount(AbstractRealm realm, Configuration config, boolean removed) throws ConfigurationException {
comment|//        return new AccountImpl(realm, config, true);
comment|//    }
comment|//
comment|//    @Override
comment|//    public GroupImpl instantiateGroup(AbstractRealm realm, int id, String name) throws ConfigurationException {
comment|//        return new GroupImpl(realm, id, name);
comment|//    }
comment|//
comment|//    @Override
comment|//    public GroupImpl instantiateGroup(AbstractRealm realm, String name) throws ConfigurationException {
comment|//        return new GroupImpl(realm, name);
comment|//    }
comment|//
comment|//    @Override
comment|//    public AccountImpl instantiateAccount(AbstractRealm realm, int id, Account from_account) throws ConfigurationException, PermissionDeniedException {
comment|//        return new AccountImpl(realm, id, from_account);
comment|//    }
comment|//
comment|//    @Override
comment|//    public AccountImpl instantiateAccount(AbstractRealm realm, String username) throws ConfigurationException {
comment|//        return new AccountImpl(realm, username);
comment|//    }
block|}
end_class

end_unit

