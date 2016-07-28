begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|persistent
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
name|Dependency
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
name|Profiler
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
name|XPathUtil
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
name|StringValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
comment|/**      * @param context      * @param select      * @param value      */
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
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
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
specifier|final
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
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
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
block|}
specifier|final
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
comment|//START trap Rename failure
comment|/* If we try and Rename a node at an invalid location,          * trap the error in a context variable,          * this is then accessible from xquery via. the context extension module - deliriumsky          * TODO: This trapping could be expanded further - basically where XPathException is thrown from thiss class          * TODO: Maybe we could provide more detailed messages in the trap, e.g. couldnt rename node `xyz` into `abc` becuase... this would be nicer for the end user of the xquery application           */
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
block|{
comment|//Indicate the failure to perform this update by adding it to the sequence in the context variable XQueryContext.XQUERY_CONTEXTVAR_XQUERY_UPDATE_ERROR
name|ValueSequence
name|prevUpdateErrors
init|=
literal|null
decl_stmt|;
specifier|final
name|XPathException
name|xpe
init|=
operator|new
name|XPathException
argument_list|(
name|this
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
decl_stmt|;
specifier|final
name|Object
name|ctxVarObj
init|=
name|context
operator|.
name|getXQueryContextVar
argument_list|(
name|XQueryContext
operator|.
name|XQUERY_CONTEXTVAR_XQUERY_UPDATE_ERROR
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctxVarObj
operator|==
literal|null
condition|)
block|{
name|prevUpdateErrors
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|prevUpdateErrors
operator|=
operator|(
name|ValueSequence
operator|)
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|ctxVarObj
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|prevUpdateErrors
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|setXQueryContextVar
argument_list|(
name|XQueryContext
operator|.
name|XQUERY_CONTEXTVAR_XQUERY_UPDATE_ERROR
argument_list|,
name|prevUpdateErrors
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//TODO: should we trap this instead of throwing an exception - deliriumsky?
throw|throw
name|xpe
throw|;
block|}
block|}
comment|//END trap Rename failure
if|if
condition|(
operator|!
name|inSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|QName
name|newQName
decl_stmt|;
specifier|final
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
comment|//start a transaction
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|getTransaction
argument_list|()
init|)
block|{
specifier|final
name|StoredNode
index|[]
name|ql
init|=
name|selectAndLock
argument_list|(
name|transaction
argument_list|,
name|inSeq
argument_list|)
decl_stmt|;
name|NodeImpl
name|parent
decl_stmt|;
specifier|final
name|NotificationService
name|notifier
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNotificationService
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|StoredNode
name|node
range|:
name|ql
control|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
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
name|context
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
name|context
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
comment|//update the document
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
specifier|final
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
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
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
specifier|final
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
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
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
name|this
argument_list|,
literal|"unsupported node-type"
argument_list|)
throw|;
block|}
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
name|context
operator|.
name|getBroker
argument_list|()
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
name|finishTriggers
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
comment|//commit the transaction
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|EXistException
decl||
name|LockException
decl||
name|TriggerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
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
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"update rename "
operator|+
name|select
operator|.
name|toString
argument_list|()
operator|+
literal|" as "
operator|+
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

