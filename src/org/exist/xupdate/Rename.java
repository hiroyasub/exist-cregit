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
name|dom
operator|.
name|AttrImpl
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
name|ElementImpl
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
name|QName
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

begin_comment
comment|/**  * Implements an XUpdate rename operation.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Rename
extends|extends
name|Modification
block|{
comment|/**      * @param pool      * @param user      * @param selectStmt      */
specifier|public
name|Rename
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
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xupdate.Modification#process(org.exist.dom.DocumentSet)      */
specifier|public
name|long
name|process
parameter_list|()
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
name|int
name|modificationCount
init|=
literal|0
decl_stmt|;
try|try
block|{
name|NodeImpl
index|[]
name|ql
init|=
name|selectAndLock
argument_list|()
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|,
name|prevCollection
init|=
literal|null
decl_stmt|;
name|NodeImpl
name|node
decl_stmt|;
name|NodeImpl
name|parent
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
name|String
name|newName
init|=
name|children
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
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
name|collection
operator|=
name|doc
operator|.
name|getCollection
argument_list|()
expr_stmt|;
if|if
condition|(
name|prevCollection
operator|!=
literal|null
operator|&&
name|collection
operator|!=
name|prevCollection
condition|)
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|saveCollection
argument_list|(
name|prevCollection
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|collection
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
literal|"write access to collection denied; user="
operator|+
name|broker
operator|.
name|getUser
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
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
literal|"permission denied to update document"
argument_list|)
throw|;
name|doc
operator|.
name|setIndexListener
argument_list|(
name|listener
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
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|setNodeName
argument_list|(
operator|new
name|QName
argument_list|(
name|newName
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateChild
argument_list|(
name|node
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|modificationCount
operator|++
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
operator|(
operator|(
name|AttrImpl
operator|)
name|node
operator|)
operator|.
name|setNodeName
argument_list|(
operator|new
name|QName
argument_list|(
name|newName
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateChild
argument_list|(
name|node
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|modificationCount
operator|++
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"unsupported node-type"
argument_list|)
throw|;
block|}
name|doc
operator|.
name|clearIndexListener
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|prevCollection
operator|=
name|collection
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|saveCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|unlockDocuments
argument_list|()
expr_stmt|;
block|}
return|return
name|modificationCount
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xupdate.Modification#getName()      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"rename"
return|;
block|}
block|}
end_class

end_unit

