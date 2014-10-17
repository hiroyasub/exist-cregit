begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|persistent
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
name|persistent
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
name|persistent
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
comment|/**  * Implements an XUpate append statement.  *   * Note: appending an attribute that is already present in  * an element will overwrite the old attribute value.  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Append
extends|extends
name|Modification
block|{
specifier|private
name|int
name|child
decl_stmt|;
comment|/** 	 * Constructor for Append. 	 * @param selectStmt 	 */
specifier|public
name|Append
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
name|String
name|childAttr
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
if|if
condition|(
name|childAttr
operator|==
literal|null
operator|||
literal|"last()"
operator|.
name|equals
argument_list|(
name|childAttr
argument_list|)
condition|)
block|{
name|child
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|child
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|childAttr
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* 	 * @see org.exist.xupdate.Modification#process() 	 */
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
throws|,
name|TriggerException
block|{
specifier|final
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
block|{
return|return
literal|0
return|;
block|}
try|try
block|{
specifier|final
name|StoredNode
name|ql
index|[]
init|=
name|selectAndLock
argument_list|(
name|transaction
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
name|StoredNode
name|node
init|=
name|ql
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
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
name|getSubject
argument_list|()
argument_list|,
name|Permission
operator|.
name|WRITE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"User '"
operator|+
name|broker
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' does not have permission to write to the document '"
operator|+
name|doc
operator|.
name|getDocumentURI
argument_list|()
operator|+
literal|"'!"
argument_list|)
throw|;
block|}
name|node
operator|.
name|appendChildren
argument_list|(
name|transaction
argument_list|,
name|children
argument_list|,
name|child
argument_list|)
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
name|modifiedDocuments
operator|.
name|add
argument_list|(
name|doc
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
name|modifiedDocuments
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
comment|// release all acquired locks
name|unlockDocuments
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"append"
return|;
block|}
block|}
end_class

end_unit

