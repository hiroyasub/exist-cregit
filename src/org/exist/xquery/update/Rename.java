begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|update
package|;
end_package

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
name|xquery
operator|.
name|Expression
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
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
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
name|util
operator|.
name|Error
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
name|util
operator|.
name|ExpressionDumper
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
name|util
operator|.
name|Messages
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
name|value
operator|.
name|Item
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
name|value
operator|.
name|QNameValue
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|Type
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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|Rename
extends|extends
name|Modification
block|{
comment|/** 	 * @param context 	 * @param select 	 * @param value 	 */
specifier|public
name|Rename
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|select
parameter_list|,
name|Expression
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|select
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|Sequence
name|inSeq
init|=
name|select
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|inSeq
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|inSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|UPDATE_SELECT_TYPE
argument_list|)
argument_list|)
throw|;
name|Sequence
name|contentSeq
init|=
name|value
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentSeq
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|UPDATE_EMPTY_CONTENT
argument_list|)
argument_list|)
throw|;
name|QName
name|newQName
decl_stmt|;
name|Item
name|item
init|=
name|contentSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|QNAME
condition|)
block|{
name|newQName
operator|=
operator|(
operator|(
name|QNameValue
operator|)
name|item
operator|)
operator|.
name|getQName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|newQName
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|TransactionManager
name|transact
init|=
name|context
operator|.
name|getBroker
argument_list|()
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
name|NodeImpl
index|[]
name|ql
init|=
name|selectAndLock
argument_list|(
name|inSeq
operator|.
name|toNodeSet
argument_list|()
argument_list|)
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
name|context
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
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
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
name|ElementImpl
name|newElem
init|=
operator|new
name|ElementImpl
argument_list|(
operator|(
name|ElementImpl
operator|)
name|node
argument_list|)
decl_stmt|;
name|newElem
operator|.
name|setNodeName
argument_list|(
name|newQName
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateChild
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|newElem
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|AttrImpl
name|newAttr
init|=
operator|new
name|AttrImpl
argument_list|(
operator|(
name|AttrImpl
operator|)
name|node
argument_list|)
decl_stmt|;
name|newAttr
operator|.
name|setNodeName
argument_list|(
name|newQName
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateChild
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|newAttr
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
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
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|storeDocument
argument_list|(
name|transaction
argument_list|,
name|doc
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
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
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|unlockDocuments
argument_list|()
expr_stmt|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper) 	 */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"update rename"
argument_list|)
operator|.
name|nl
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|select
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|" to "
argument_list|)
operator|.
name|nl
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|value
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|endIndent
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

