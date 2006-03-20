begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|Iterator
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
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|xacml
operator|.
name|ExistPDP
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
name|LockException
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
name|util
operator|.
name|hashtable
operator|.
name|Int2ObjectHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * SecurityManager is responsible for managing users and groups.  *   * There's only one SecurityManager for each database instance, which  * may be obtained by {@link BrokerPool#getSecurityManager()}.  *   * Users and groups are stored in the system collection, in document  * users.xml. While it is possible to edit this file by hand, it  * may lead to unexpected results, since SecurityManager reads   * users.xml only during database startup and shutdown.  */
end_comment

begin_class
specifier|public
class|class
name|XMLSecurityManager
implements|implements
name|SecurityManager
block|{
specifier|public
specifier|final
specifier|static
name|String
name|DBA_GROUP
init|=
literal|"dba"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DBA_USER
init|=
literal|"admin"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_GROUP
init|=
literal|"guest"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_USER
init|=
literal|"guest"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|User
name|SYSTEM_USER
init|=
operator|new
name|User
argument_list|(
name|DBA_USER
argument_list|,
literal|null
argument_list|,
name|DBA_GROUP
argument_list|)
decl_stmt|;
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
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|Int2ObjectHashMap
name|groups
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|65
argument_list|)
decl_stmt|;
specifier|private
name|Int2ObjectHashMap
name|users
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|65
argument_list|)
decl_stmt|;
specifier|private
name|int
name|nextUserId
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|nextGroupId
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|defCollectionPermissions
init|=
name|Permission
operator|.
name|DEFAULT_PERM
decl_stmt|;
specifier|private
name|int
name|defResourcePermissions
init|=
name|Permission
operator|.
name|DEFAULT_PERM
decl_stmt|;
specifier|private
name|ExistPDP
name|pdp
decl_stmt|;
specifier|public
name|XMLSecurityManager
parameter_list|()
block|{
block|}
comment|/** 	 * Initialize the security manager. 	 *  	 * Checks if the file users.xml exists in the system collection of the database. 	 * If not, it is created with two default users: admin and guest. 	 *   	 * @param pool 	 * @param sysBroker 	 */
specifier|public
name|void
name|attach
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|DBBroker
name|sysBroker
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
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
name|DBBroker
name|broker
init|=
name|sysBroker
decl_stmt|;
try|try
block|{
name|Collection
name|sysCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|DBBroker
operator|.
name|SYSTEM_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|sysCollection
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
name|sysCollection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|DBBroker
operator|.
name|SYSTEM_COLLECTION
argument_list|)
expr_stmt|;
name|sysCollection
operator|.
name|setPermissions
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|sysCollection
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
name|Document
name|acl
init|=
name|sysCollection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|ACL_FILE
argument_list|)
decl_stmt|;
name|Element
name|docElement
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
name|docElement
operator|=
name|acl
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|docElement
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating system users"
argument_list|)
expr_stmt|;
name|User
name|user
init|=
operator|new
name|User
argument_list|(
name|DBA_USER
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|user
operator|.
name|addGroup
argument_list|(
name|DBA_GROUP
argument_list|)
expr_stmt|;
name|user
operator|.
name|setUID
argument_list|(
operator|++
name|nextUserId
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getUID
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|user
operator|=
operator|new
name|User
argument_list|(
name|GUEST_USER
argument_list|,
name|GUEST_USER
argument_list|,
name|GUEST_GROUP
argument_list|)
expr_stmt|;
name|user
operator|.
name|setUID
argument_list|(
operator|++
name|nextUserId
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getUID
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|addGroup
argument_list|(
name|DBA_GROUP
argument_list|)
expr_stmt|;
name|addGroup
argument_list|(
name|GUEST_GROUP
argument_list|)
expr_stmt|;
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|save
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
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading acl"
argument_list|)
expr_stmt|;
name|Element
name|root
init|=
name|acl
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|String
name|version
init|=
name|root
operator|.
name|getAttribute
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
name|int
name|major
init|=
literal|0
decl_stmt|;
name|int
name|minor
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|numbers
init|=
name|version
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|major
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numbers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|minor
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numbers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|NodeList
name|nl
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|node
decl_stmt|;
name|Element
name|next
decl_stmt|;
name|User
name|user
decl_stmt|;
name|NodeList
name|ul
decl_stmt|;
name|String
name|lastId
decl_stmt|;
name|Group
name|group
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
continue|continue;
name|next
operator|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"users"
argument_list|)
condition|)
block|{
name|lastId
operator|=
name|next
operator|.
name|getAttribute
argument_list|(
literal|"last-id"
argument_list|)
expr_stmt|;
try|try
block|{
name|nextUserId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lastId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
name|ul
operator|=
name|next
operator|.
name|getChildNodes
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ul
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|=
name|ul
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|node
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"user"
argument_list|)
condition|)
block|{
name|user
operator|=
operator|new
name|User
argument_list|(
name|major
argument_list|,
name|minor
argument_list|,
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getUID
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|next
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"groups"
argument_list|)
condition|)
block|{
name|lastId
operator|=
name|next
operator|.
name|getAttribute
argument_list|(
literal|"last-id"
argument_list|)
expr_stmt|;
try|try
block|{
name|nextGroupId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lastId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
name|ul
operator|=
name|next
operator|.
name|getChildNodes
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ul
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|=
name|ul
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|node
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"group"
argument_list|)
condition|)
block|{
name|group
operator|=
operator|new
name|Group
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
name|groups
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
block|}
block|}
block|}
block|}
block|}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading acl failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// read default collection and resource permissions
name|Integer
name|defOpt
init|=
operator|(
name|Integer
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"indexer.permissions.collection"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defOpt
operator|!=
literal|null
condition|)
name|defCollectionPermissions
operator|=
name|defOpt
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|defOpt
operator|=
operator|(
name|Integer
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"indexer.permissions.resource"
argument_list|)
expr_stmt|;
if|if
condition|(
name|defOpt
operator|!=
literal|null
condition|)
name|defResourcePermissions
operator|=
name|defOpt
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|Boolean
name|enableXACML
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
literal|"xacml.enable"
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableXACML
operator|!=
literal|null
operator|&&
name|enableXACML
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|pdp
operator|=
operator|new
name|ExistPDP
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"XACML enabled"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isXACMLEnabled
parameter_list|()
block|{
return|return
name|pdp
operator|!=
literal|null
return|;
block|}
specifier|public
name|ExistPDP
name|getPDP
parameter_list|()
block|{
return|return
name|pdp
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|deleteUser
argument_list|(
name|getUser
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|deleteUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
return|return;
name|user
operator|=
operator|(
name|User
operator|)
name|users
operator|.
name|remove
argument_list|(
name|user
operator|.
name|getUID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"user "
operator|+
name|user
operator|.
name|getName
argument_list|()
operator|+
literal|" removed"
argument_list|)
expr_stmt|;
else|else
name|LOG
operator|.
name|debug
argument_list|(
literal|"user not found"
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
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
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|save
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|pool
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
name|User
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|User
name|user
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|users
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|user
operator|=
operator|(
name|User
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|user
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|user
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"user "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
specifier|synchronized
name|User
name|getUser
parameter_list|(
name|int
name|uid
parameter_list|)
block|{
specifier|final
name|User
name|user
init|=
operator|(
name|User
operator|)
name|users
operator|.
name|get
argument_list|(
name|uid
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"user with uid "
operator|+
name|uid
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
return|return
name|user
return|;
block|}
specifier|public
specifier|synchronized
name|User
index|[]
name|getUsers
parameter_list|()
block|{
name|User
name|u
index|[]
init|=
operator|new
name|User
index|[
name|users
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|users
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
name|u
index|[
name|j
index|]
operator|=
operator|(
name|User
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|u
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Group
name|group
init|=
operator|new
name|Group
argument_list|(
name|name
argument_list|,
operator|++
name|nextGroupId
argument_list|)
decl_stmt|;
name|groups
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
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Group
name|group
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|groups
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|group
operator|=
operator|(
name|Group
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
specifier|synchronized
name|Group
name|getGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Group
name|group
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|groups
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|group
operator|=
operator|(
name|Group
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|group
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|synchronized
name|Group
name|getGroup
parameter_list|(
name|int
name|gid
parameter_list|)
block|{
return|return
operator|(
name|Group
operator|)
name|groups
operator|.
name|get
argument_list|(
name|gid
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|String
index|[]
name|getGroups
parameter_list|()
block|{
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Group
name|group
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|groups
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|group
operator|=
operator|(
name|Group
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|list
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
name|String
index|[]
name|gl
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|gl
argument_list|)
expr_stmt|;
return|return
name|gl
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|User
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|hasDbaRole
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|User
name|user
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|users
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|user
operator|=
operator|(
name|User
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|user
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|save
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|)
throws|throws
name|EXistException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing acl file"
argument_list|)
expr_stmt|;
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
literal|"<auth version='1.0'>"
argument_list|)
expr_stmt|;
comment|// save groups
name|buf
operator|.
name|append
argument_list|(
literal|"<groups last-id=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|nextGroupId
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|groups
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Group
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
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
literal|"<users last-id=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|nextUserId
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|users
operator|.
name|valueIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|User
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
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
try|try
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|getUser
argument_list|(
name|DBA_USER
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
name|sysCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|DBBroker
operator|.
name|SYSTEM_COLLECTION
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
name|ACL_FILE
argument_list|,
name|data
argument_list|)
decl_stmt|;
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
name|doc
operator|.
name|setPermissions
argument_list|(
literal|0770
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
name|SAXException
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
catch|catch
parameter_list|(
name|PermissionDeniedException
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
catch|catch
parameter_list|(
name|TriggerException
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
catch|catch
parameter_list|(
name|LockException
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
specifier|public
specifier|synchronized
name|void
name|setUser
parameter_list|(
name|User
name|user
parameter_list|)
block|{
if|if
condition|(
name|user
operator|.
name|getUID
argument_list|()
operator|<
literal|0
condition|)
name|user
operator|.
name|setUID
argument_list|(
operator|++
name|nextUserId
argument_list|)
expr_stmt|;
name|users
operator|.
name|put
argument_list|(
name|user
operator|.
name|getUID
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|String
index|[]
name|groups
init|=
name|user
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
name|hasGroup
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
condition|)
name|addGroup
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|save
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|createUserHome
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|user
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"error while creating user"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"error while creating home collection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getResourceDefaultPerms
parameter_list|()
block|{
return|return
name|defResourcePermissions
return|;
block|}
specifier|public
name|int
name|getCollectionDefaultPerms
parameter_list|()
block|{
return|return
name|defCollectionPermissions
return|;
block|}
specifier|private
name|void
name|createUserHome
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|User
name|user
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
if|if
condition|(
name|user
operator|.
name|getHome
argument_list|()
operator|==
literal|null
condition|)
return|return;
name|broker
operator|.
name|setUser
argument_list|(
name|getUser
argument_list|(
name|DBA_USER
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
name|home
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|user
operator|.
name|getHome
argument_list|()
argument_list|)
decl_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|home
operator|.
name|getPermissions
argument_list|()
operator|.
name|setGroup
argument_list|(
name|user
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|home
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

