begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-04 Wolfgang M. Meier  * wolfgang@exist-db.org http://exist.sourceforge.net  *   * This program is free software; you can redistribute it and/or modify it under  * the terms of the GNU Lesser General Public License as published by the Free  * Software Foundation; either version 2 of the License, or (at your option) any  * later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more  * details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
package|;
end_package

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
name|EXistException
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
name|dom
operator|.
name|DocumentSet
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
name|NodeImpl
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
name|StoredNode
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
name|Permission
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
name|NotificationService
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
name|UpdateListener
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
name|xquery
operator|.
name|XPathException
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

begin_comment
comment|/**  * Implements an XUpdate insert-after or insert-before modification.  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Insert
extends|extends
name|Modification
block|{
specifier|public
specifier|final
specifier|static
name|int
name|INSERT_BEFORE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|INSERT_AFTER
init|=
literal|1
decl_stmt|;
specifier|private
name|int
name|mode
init|=
name|INSERT_BEFORE
decl_stmt|;
comment|/**      * Constructor for Insert.      *       * @param pool      * @param user      * @param selectStmt      */
specifier|public
name|Insert
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|,
name|Map
name|namespaces
parameter_list|,
name|Map
name|variables
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|selectStmt
argument_list|,
name|namespaces
argument_list|,
name|variables
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Insert
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|,
name|int
name|mode
parameter_list|,
name|Map
name|namespaces
parameter_list|,
name|Map
name|variables
parameter_list|)
block|{
name|this
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|selectStmt
argument_list|,
name|namespaces
argument_list|,
name|variables
argument_list|)
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
comment|/**      * @see org.exist.xupdate.Modification#process(org.exist.dom.DocumentSet)      */
specifier|public
name|long
name|process
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
name|NodeList
name|children
init|=
name|content
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|0
return|;
try|try
block|{
name|StoredNode
index|[]
name|ql
init|=
name|selectAndLock
argument_list|()
decl_stmt|;
name|IndexListener
name|listener
init|=
operator|new
name|IndexListener
argument_list|(
name|ql
argument_list|)
decl_stmt|;
name|NotificationService
name|notifier
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNotificationService
argument_list|()
decl_stmt|;
name|NodeImpl
name|node
decl_stmt|;
name|NodeImpl
name|parent
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
name|DocumentSet
name|modifiedDocs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|children
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|len
operator|+
literal|" nodes to insert"
argument_list|)
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
name|ql
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|ql
index|[
name|i
index|]
expr_stmt|;
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setIndexListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|doc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|broker
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|UPDATE
argument_list|)
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"permission to update document denied"
argument_list|)
throw|;
name|modifiedDocs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|parent
operator|=
operator|(
name|NodeImpl
operator|)
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|INSERT_BEFORE
case|:
name|parent
operator|.
name|insertBefore
argument_list|(
name|transaction
argument_list|,
name|children
argument_list|,
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|INSERT_AFTER
case|:
name|parent
operator|.
name|insertAfter
argument_list|(
name|transaction
argument_list|,
name|children
argument_list|,
name|node
argument_list|)
expr_stmt|;
break|break;
block|}
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|clearIndexListener
argument_list|()
expr_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|notifier
operator|.
name|notifyUpdate
argument_list|(
name|doc
argument_list|,
name|UpdateListener
operator|.
name|UPDATE
argument_list|)
expr_stmt|;
block|}
name|checkFragmentation
argument_list|(
name|transaction
argument_list|,
name|modifiedDocs
argument_list|)
expr_stmt|;
return|return
name|ql
operator|.
name|length
return|;
block|}
finally|finally
block|{
name|unlockDocuments
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see org.exist.xupdate.Modification#getName()      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
operator|(
name|mode
operator|==
name|INSERT_BEFORE
condition|?
literal|"insert-before"
else|:
literal|"insert-after"
operator|)
return|;
block|}
block|}
end_class

end_unit

