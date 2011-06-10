begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|lock
operator|.
name|Lock
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
name|TransactionException
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_comment
comment|/**  * Instantiates an appropriate Permission class based on the current configuration  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|PermissionFactory
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
name|PermissionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|SecurityManager
name|sm
init|=
literal|null
decl_stmt|;
comment|//TODO The way this gets set is nasty AR
specifier|public
specifier|static
name|Permission
name|getPermission
parameter_list|()
block|{
name|Permission
name|permission
init|=
literal|null
decl_stmt|;
try|try
block|{
name|permission
operator|=
operator|new
name|SimpleACLPermission
argument_list|(
name|sm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while instantiating security permission class."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|permission
return|;
block|}
specifier|public
specifier|static
name|Permission
name|getPermission
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|Permission
name|permission
init|=
literal|null
decl_stmt|;
try|try
block|{
name|permission
operator|=
operator|new
name|SimpleACLPermission
argument_list|(
name|sm
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while instantiating security permission class."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|permission
return|;
block|}
specifier|public
specifier|static
name|Permission
name|getPermission
parameter_list|(
name|int
name|ownerId
parameter_list|,
name|int
name|groupId
parameter_list|)
block|{
comment|//TODO consider loading Permission.DEFAULT_PERM from conf.xml instead
return|return
operator|new
name|SimpleACLPermission
argument_list|(
name|sm
argument_list|,
name|ownerId
argument_list|,
name|groupId
argument_list|,
name|Permission
operator|.
name|DEFAULT_PERM
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Permission
name|getPermission
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|groupName
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|Permission
name|permission
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Account
name|owner
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|invokingUser
argument_list|,
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|owner
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"User was not found '"
operator|+
operator|(
name|userName
operator|==
literal|null
condition|?
literal|""
else|:
name|userName
operator|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|Group
name|group
init|=
name|sm
operator|.
name|getGroup
argument_list|(
name|invokingUser
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Group was not found '"
operator|+
operator|(
name|userName
operator|==
literal|null
condition|?
literal|""
else|:
name|groupName
operator|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|permission
operator|=
operator|new
name|SimpleACLPermission
argument_list|(
name|sm
argument_list|,
name|owner
operator|.
name|getId
argument_list|()
argument_list|,
name|group
operator|.
name|getId
argument_list|()
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while instantiating security permission class."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|permission
return|;
block|}
specifier|public
interface|interface
name|PermissionModifier
block|{
specifier|public
name|void
name|modify
parameter_list|(
name|Permission
name|permission
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
block|}
specifier|public
specifier|static
name|void
name|updatePermissions
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|pathUri
parameter_list|,
name|PermissionModifier
name|permissionModifier
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|pathUri
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Resource or collection '"
operator|+
name|pathUri
operator|.
name|toString
argument_list|()
operator|+
literal|"' does not exist."
argument_list|)
throw|;
block|}
name|Permission
name|permissions
init|=
name|doc
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
name|permissionModifier
operator|.
name|modify
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeXMLResource
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// keep the write lock in the transaction
name|transaction
operator|.
name|registerLock
argument_list|(
name|collection
operator|.
name|getLock
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|Permission
name|permissions
init|=
name|collection
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
name|permissionModifier
operator|.
name|modify
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission to modify permissions is denied for user '"
operator|+
name|broker
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' on '"
operator|+
name|pathUri
operator|.
name|toString
argument_list|()
operator|+
literal|"': "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission to modify permissions is denied for user '"
operator|+
name|broker
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' on '"
operator|+
name|pathUri
operator|.
name|toString
argument_list|()
operator|+
literal|"': "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission to modify permissions is denied for user '"
operator|+
name|broker
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' on '"
operator|+
name|pathUri
operator|.
name|toString
argument_list|()
operator|+
literal|"': "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|te
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission to modify permissions is denied for user '"
operator|+
name|broker
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' on '"
operator|+
name|pathUri
operator|.
name|toString
argument_list|()
operator|+
literal|"': "
operator|+
name|te
operator|.
name|getMessage
argument_list|()
argument_list|,
name|te
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|te
parameter_list|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission to modify permissions is denied for user '"
operator|+
name|broker
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' on '"
operator|+
name|pathUri
operator|.
name|toString
argument_list|()
operator|+
literal|"': "
operator|+
name|te
operator|.
name|getMessage
argument_list|()
argument_list|,
name|te
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

