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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|Reference
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
name|ReferenceImpl
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
name|AXSchemaType
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
name|AbstractPrincipal
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
name|EXistSchemaType
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
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RealmImpl
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
name|INITIAL_LAST_ACCOUNT_ID
init|=
literal|10
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
specifier|public
specifier|final
specifier|static
name|int
name|INITIAL_LAST_GROUP_ID
init|=
literal|10
decl_stmt|;
specifier|protected
specifier|final
name|AccountImpl
name|ACCOUNT_SYSTEM
decl_stmt|;
specifier|protected
specifier|final
name|AccountImpl
name|ACCOUNT_UNKNOWN
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
name|GroupImpl
name|GROUP_UNKNOWN
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_ADMIN_PASSWORD
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_GUEST_PASSWORD
init|=
literal|"guest"
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
specifier|final
name|SecurityManagerImpl
name|sm
parameter_list|,
specifier|final
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
name|sm
operator|.
name|lastUserId
operator|=
name|INITIAL_LAST_ACCOUNT_ID
expr_stmt|;
comment|//TODO this is horrible!
name|sm
operator|.
name|lastGroupId
operator|=
name|INITIAL_LAST_GROUP_ID
expr_stmt|;
comment|//TODO this is horrible!
comment|//DBA group
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
name|GROUP_DBA
operator|.
name|setManagers
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
operator|new
name|ReferenceImpl
argument_list|<>
argument_list|(
name|sm
argument_list|,
literal|"getAccount"
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|GROUP_DBA
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"Database Administrators"
argument_list|)
expr_stmt|;
name|sm
operator|.
name|addGroup
argument_list|(
name|GROUP_DBA
operator|.
name|getId
argument_list|()
argument_list|,
name|GROUP_DBA
argument_list|)
expr_stmt|;
name|registerGroup
argument_list|(
name|GROUP_DBA
argument_list|)
expr_stmt|;
comment|//sm.groupsById.put(GROUP_DBA.getId(), GROUP_DBA);
comment|//groupsByName.put(GROUP_DBA.getName(), GROUP_DBA);
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
name|ACCOUNT_SYSTEM
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
argument_list|,
name|SecurityManager
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
name|ACCOUNT_SYSTEM
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"System Internals"
argument_list|)
expr_stmt|;
name|sm
operator|.
name|addUser
argument_list|(
name|ACCOUNT_SYSTEM
operator|.
name|getId
argument_list|()
argument_list|,
name|ACCOUNT_SYSTEM
argument_list|)
expr_stmt|;
name|registerAccount
argument_list|(
name|ACCOUNT_SYSTEM
argument_list|)
expr_stmt|;
comment|//sm.usersById.put(ACCOUNT_SYSTEM.getId(), ACCOUNT_SYSTEM);
comment|//usersByName.put(ACCOUNT_SYSTEM.getName(), ACCOUNT_SYSTEM);
comment|//guest group
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
name|GROUP_GUEST
operator|.
name|setManagers
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Reference
argument_list|<
name|SecurityManager
argument_list|,
name|Account
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
operator|new
name|ReferenceImpl
argument_list|<>
argument_list|(
name|sm
argument_list|,
literal|"getAccount"
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|GROUP_GUEST
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"Anonymous Users"
argument_list|)
expr_stmt|;
name|sm
operator|.
name|addGroup
argument_list|(
name|GROUP_GUEST
operator|.
name|getId
argument_list|()
argument_list|,
name|GROUP_GUEST
argument_list|)
expr_stmt|;
name|registerGroup
argument_list|(
name|GROUP_GUEST
argument_list|)
expr_stmt|;
comment|//sm.groupsById.put(GROUP_GUEST.getId(), GROUP_GUEST);
comment|//groupsByName.put(GROUP_GUEST.getName(), GROUP_GUEST);
comment|//unknown account and group
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
comment|//XXX: GROUP_DBA._addManager(ACCOUNT_ADMIN);
comment|//XXX: GROUP_GUEST._addManager(ACCOUNT_ADMIN);
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|super
operator|.
name|start
argument_list|(
name|broker
argument_list|)
expr_stmt|;
try|try
block|{
name|createAdminAndGuestIfNotExist
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
specifier|final
name|boolean
name|exportOnly
init|=
operator|(
name|Boolean
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_EXPORT_ONLY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|exportOnly
condition|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|createAdminAndGuestIfNotExist
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
comment|//Admin account
if|if
condition|(
name|getSecurityManager
argument_list|()
operator|.
name|getAccount
argument_list|(
name|ADMIN_ACCOUNT_ID
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//AccountImpl actAdmin = new AccountImpl(broker, this, ADMIN_ACCOUNT_ID, SecurityManager.DBA_USER, "", GROUP_DBA, true);
specifier|final
name|UserAider
name|actAdmin
init|=
operator|new
name|UserAider
argument_list|(
name|ADMIN_ACCOUNT_ID
argument_list|,
name|getId
argument_list|()
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
decl_stmt|;
name|actAdmin
operator|.
name|setPassword
argument_list|(
name|DEFAULT_ADMIN_PASSWORD
argument_list|)
expr_stmt|;
name|actAdmin
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
argument_list|,
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
expr_stmt|;
name|actAdmin
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"System Administrator"
argument_list|)
expr_stmt|;
name|actAdmin
operator|.
name|addGroup
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
expr_stmt|;
name|getSecurityManager
argument_list|()
operator|.
name|addAccount
argument_list|(
name|broker
argument_list|,
name|actAdmin
argument_list|)
expr_stmt|;
block|}
comment|//Guest account
if|if
condition|(
name|getSecurityManager
argument_list|()
operator|.
name|getAccount
argument_list|(
name|GUEST_ACCOUNT_ID
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//AccountImpl actGuest = new AccountImpl(broker, this, GUEST_ACCOUNT_ID, SecurityManager.GUEST_USER, SecurityManager.GUEST_USER, GROUP_GUEST, false);
specifier|final
name|UserAider
name|actGuest
init|=
operator|new
name|UserAider
argument_list|(
name|GUEST_ACCOUNT_ID
argument_list|,
name|getId
argument_list|()
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
decl_stmt|;
name|actGuest
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
argument_list|,
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
expr_stmt|;
name|actGuest
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"Anonymous User"
argument_list|)
expr_stmt|;
name|actGuest
operator|.
name|setPassword
argument_list|(
name|DEFAULT_GUEST_PASSWORD
argument_list|)
expr_stmt|;
name|actGuest
operator|.
name|addGroup
argument_list|(
name|SecurityManager
operator|.
name|GUEST_GROUP
argument_list|)
expr_stmt|;
name|getSecurityManager
argument_list|()
operator|.
name|addAccount
argument_list|(
name|broker
argument_list|,
name|actGuest
argument_list|)
expr_stmt|;
block|}
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
name|boolean
name|deleteAccount
parameter_list|(
specifier|final
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
block|{
return|return
literal|false
return|;
block|}
name|usersByName
operator|.
expr|<
name|PermissionDeniedException
operator|,
name|EXistException
operator|>
name|modify2E
argument_list|(
name|principalDb
lambda|->
block|{
specifier|final
name|AbstractAccount
name|remove_account
init|=
operator|(
name|AbstractAccount
operator|)
name|principalDb
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No such account exists!"
argument_list|)
throw|;
block|}
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getDatabase
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|Account
name|user
init|=
name|broker
operator|.
name|getCurrentSubject
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
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"You are not allowed to delete '"
operator|+
name|account
operator|.
name|getName
argument_list|()
operator|+
literal|"' user"
argument_list|)
throw|;
block|}
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
specifier|final
name|TransactionManager
name|transaction
init|=
name|getDatabase
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|principalDb
operator|.
name|remove
argument_list|(
name|remove_account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|deleteGroup
parameter_list|(
specifier|final
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
block|{
return|return
literal|false
return|;
block|}
name|groupsByName
operator|.
expr|<
name|PermissionDeniedException
operator|,
name|EXistException
operator|>
name|modify2E
argument_list|(
name|principalDb
lambda|->
block|{
specifier|final
name|AbstractPrincipal
name|remove_group
init|=
operator|(
name|AbstractPrincipal
operator|)
name|principalDb
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group does '"
operator|+
name|group
operator|.
name|getName
argument_list|()
operator|+
literal|"' not exist!"
argument_list|)
throw|;
block|}
specifier|final
name|DBBroker
name|broker
init|=
name|getDatabase
argument_list|()
operator|.
name|getActiveBroker
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|subject
init|=
name|broker
operator|.
name|getCurrentSubject
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Group
operator|)
name|remove_group
operator|)
operator|.
name|assertCanModifyGroup
argument_list|(
name|subject
argument_list|)
expr_stmt|;
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
specifier|final
name|TransactionManager
name|transaction
init|=
name|getDatabase
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|principalDb
operator|.
name|remove
argument_list|(
name|remove_group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
literal|true
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
specifier|final
name|Account
name|account
init|=
name|getAccount
argument_list|(
name|accountName
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|ACCOUNT_NOT_FOUND
argument_list|,
literal|"Account '"
operator|+
name|accountName
operator|+
literal|"' not found."
argument_list|)
throw|;
block|}
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
block|{
throw|throw
operator|new
name|AuthenticationException
argument_list|(
name|AuthenticationException
operator|.
name|ACCOUNT_NOT_FOUND
argument_list|,
literal|"Account '"
operator|+
name|accountName
operator|+
literal|"' can not be used."
argument_list|)
throw|;
block|}
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
literal|"Account '"
operator|+
name|accountName
operator|+
literal|"' is disabled."
argument_list|)
throw|;
block|}
specifier|final
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
operator|!
name|subject
operator|.
name|isAuthenticated
argument_list|()
condition|)
block|{
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
return|return
name|subject
return|;
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
specifier|final
name|String
name|prefix
parameter_list|)
block|{
return|return
name|usersByName
operator|.
name|read
argument_list|(
name|principalDb
lambda|->
name|principalDb
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|userName
lambda|->
name|userName
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findGroupnamesWhereGroupnameStarts
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
return|return
name|groupsByName
operator|.
name|read
argument_list|(
name|principalDb
lambda|->
name|principalDb
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|groupName
lambda|->
name|groupName
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|?
extends|extends
name|String
argument_list|>
name|findGroupnamesWhereGroupnameContains
parameter_list|(
specifier|final
name|String
name|fragment
parameter_list|)
block|{
return|return
name|groupsByName
operator|.
name|read
argument_list|(
name|principalDb
lambda|->
name|principalDb
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|groupName
lambda|->
name|groupName
operator|.
name|contains
argument_list|(
name|fragment
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
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
parameter_list|()
block|{
return|return
name|groupsByName
operator|.
name|read
argument_list|(
name|principalDb
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|principalDb
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findAllUserNames
parameter_list|()
block|{
return|return
name|usersByName
operator|.
name|read
argument_list|(
name|principalDb
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|principalDb
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
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
specifier|final
name|String
name|groupName
parameter_list|)
block|{
return|return
name|usersByName
operator|.
name|read
argument_list|(
name|principalDb
lambda|->
name|principalDb
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|account
lambda|->
name|account
operator|.
name|hasGroup
argument_list|(
name|groupName
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|account
lambda|->
name|account
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
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
specifier|final
name|String
name|startsWith
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
comment|//TODO at present exist users cannot have personal name details
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findUsernamesWhereNamePartStarts
parameter_list|(
specifier|final
name|String
name|startsWith
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
comment|//TODO at present exist users cannot have personal name details
block|}
block|}
end_class

end_unit

