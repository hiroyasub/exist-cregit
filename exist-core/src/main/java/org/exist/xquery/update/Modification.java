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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|ints
operator|.
name|Int2ObjectMap
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|ints
operator|.
name|Int2ObjectOpenHashMap
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
name|ManagedLocks
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
name|DocumentTrigger
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
name|DocumentTriggers
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
name|DefaultDocumentSet
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
name|MutableDocumentSet
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
name|NodeProxy
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
name|dom
operator|.
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|NodeHandle
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
name|lock
operator|.
name|LockManager
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
name|ManagedDocumentLock
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
name|serializers
operator|.
name|Serializer
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
name|*
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
name|NodeValue
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
name|SequenceIterator
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
name|Attr
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
name|DOMException
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
name|Node
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Modification
extends|extends
name|AbstractExpression
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Modification
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Expression
name|select
decl_stmt|;
specifier|protected
specifier|final
name|Expression
name|value
decl_stmt|;
specifier|protected
name|ManagedLocks
argument_list|<
name|ManagedDocumentLock
argument_list|>
name|lockedDocumentsLocks
decl_stmt|;
specifier|protected
name|MutableDocumentSet
name|modifiedDocuments
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Int2ObjectMap
argument_list|<
name|DocumentTrigger
argument_list|>
name|triggers
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|Modification
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|select
operator|=
name|select
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|triggers
operator|=
operator|new
name|Int2ObjectOpenHashMap
argument_list|<>
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|EMPTY
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|EMPTY
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#resetState()      */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|select
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|value
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|select
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|value
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression, int)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|addFlag
argument_list|(
name|IN_UPDATE
argument_list|)
expr_stmt|;
name|select
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|value
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Acquire a lock on all documents processed by this modification.      * We have to avoid that node positions change during the      * operation.      *      * @param nodes      *      * @throws LockException      * @throws TriggerException      */
specifier|protected
name|StoredNode
index|[]
name|selectAndLock
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|Sequence
name|nodes
parameter_list|)
throws|throws
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
throws|,
name|TriggerException
block|{
specifier|final
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
name|globalLock
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getGlobalUpdateLock
argument_list|()
decl_stmt|;
name|globalLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|DocumentSet
name|lockedDocuments
init|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
comment|// acquire a lock on all documents
comment|// we have to avoid that node positions change
comment|// during the modification
name|lockedDocumentsLocks
operator|=
name|lockedDocuments
operator|.
name|lock
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|StoredNode
name|ql
index|[]
init|=
operator|new
name|StoredNode
index|[
name|nodes
operator|.
name|getItemCount
argument_list|()
index|]
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
name|Item
name|item
init|=
name|nodes
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XQuery update expressions can only be applied to nodes. Got: "
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|NodeValue
name|nv
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
if|if
condition|(
name|nv
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XQuery update expressions can not be applied to in-memory nodes."
argument_list|)
throw|;
block|}
specifier|final
name|Node
name|n
init|=
name|nv
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Updating the document object is not allowed."
argument_list|)
throw|;
block|}
name|ql
index|[
name|i
index|]
operator|=
operator|(
name|StoredNode
operator|)
name|n
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|ql
index|[
name|i
index|]
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
comment|//prepare Trigger
name|prepareTrigger
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|ql
return|;
block|}
finally|finally
block|{
name|globalLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Sequence
name|deepCopy
parameter_list|(
name|Sequence
name|inSeq
parameter_list|)
throws|throws
name|XPathException
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
specifier|final
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setReceiver
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Sequence
name|out
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|inSeq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
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
name|DOCUMENT
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
specifier|final
name|NodeHandle
name|root
init|=
operator|(
name|NodeHandle
operator|)
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|item
operator|=
operator|new
name|NodeProxy
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|=
operator|(
name|Item
operator|)
operator|(
operator|(
name|Document
operator|)
name|item
operator|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
specifier|final
name|int
name|last
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
specifier|final
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|item
decl_stmt|;
name|serializer
operator|.
name|toReceiver
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
block|{
name|item
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastAttr
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|item
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|last
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|item
operator|)
operator|.
name|deepCopy
argument_list|()
expr_stmt|;
block|}
block|}
name|out
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
decl||
name|DOMException
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
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|finishTriggers
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|TriggerException
block|{
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|iterator
init|=
name|modifiedDocuments
operator|.
name|getDocumentIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|context
operator|.
name|addModifiedDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|finishTrigger
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|triggers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Release all acquired document locks.      */
specifier|protected
name|void
name|unlockDocuments
parameter_list|()
block|{
if|if
condition|(
name|lockedDocumentsLocks
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|modifiedDocuments
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//unlock documents
name|lockedDocumentsLocks
operator|.
name|close
argument_list|()
expr_stmt|;
name|lockedDocumentsLocks
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|checkFragmentation
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|EXistException
throws|,
name|LockException
block|{
name|int
name|fragmentationLimit
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|Object
name|property
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|DBBroker
operator|.
name|PROPERTY_XUPDATE_FRAGMENTATION_FACTOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|fragmentationLimit
operator|=
operator|(
name|Integer
operator|)
name|property
expr_stmt|;
block|}
name|checkFragmentation
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|fragmentationLimit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check if any of the modified documents needs defragmentation.      *      * Defragmentation will take place if the number of split pages in the      * document exceeds the limit defined in the configuration file.      *      * @param docs      */
specifier|public
specifier|static
name|void
name|checkFragmentation
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|int
name|splitCount
parameter_list|)
throws|throws
name|EXistException
throws|,
name|LockException
block|{
specifier|final
name|DBBroker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
specifier|final
name|LockManager
name|lockManager
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getLockManager
argument_list|()
decl_stmt|;
comment|//if there is no batch update transaction, start a new individual transaction
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|broker
operator|.
name|continueOrBeginTransaction
argument_list|()
init|)
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|docs
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|DocumentImpl
name|next
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|getMetadata
argument_list|()
operator|.
name|getSplitCount
argument_list|()
operator|>
name|splitCount
condition|)
block|{
try|try
init|(
specifier|final
name|ManagedDocumentLock
name|nextLock
init|=
name|lockManager
operator|.
name|acquireDocumentWriteLock
argument_list|(
name|next
operator|.
name|getURI
argument_list|()
argument_list|)
init|)
block|{
name|broker
operator|.
name|defragXMLResource
argument_list|(
name|transaction
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
block|}
name|broker
operator|.
name|checkXMLResourceConsistency
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Fires the prepare function for the UPDATE_DOCUMENT_EVENT trigger for the Document doc      *      * @param transaction	The transaction      * @param doc	The document to trigger for      *      * @throws TriggerException      */
specifier|private
name|void
name|prepareTrigger
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|TriggerException
block|{
specifier|final
name|Collection
name|col
init|=
name|doc
operator|.
name|getCollection
argument_list|()
decl_stmt|;
specifier|final
name|DBBroker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
specifier|final
name|DocumentTrigger
name|trigger
init|=
operator|new
name|DocumentTriggers
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|col
argument_list|)
decl_stmt|;
comment|//prepare the trigger
name|trigger
operator|.
name|beforeUpdateDocument
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|triggers
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|,
name|trigger
argument_list|)
expr_stmt|;
block|}
comment|/** Fires the finish function for UPDATE_DOCUMENT_EVENT for the documents trigger      *      * @param transaction	The transaction      * @param doc	The document to trigger for      *      * @throws TriggerException      */
specifier|private
name|void
name|finishTrigger
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//finish the trigger
specifier|final
name|DocumentTrigger
name|trigger
init|=
name|triggers
operator|.
name|get
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|trigger
operator|!=
literal|null
condition|)
block|{
name|trigger
operator|.
name|afterUpdateDocument
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Gets the Transaction to use for the update (can be batch or individual)      *      * @return The transaction      */
specifier|protected
name|Txn
name|getTransaction
parameter_list|()
block|{
return|return
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|continueOrBeginTransaction
argument_list|()
return|;
block|}
comment|/**      * Get's the parent of the node.      *      * @param node The node of which to retrieve the parent.      *      * @return the parent node, or null if not available      */
specifier|protected
annotation|@
name|Nullable
name|Node
name|getParent
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|Attr
condition|)
block|{
return|return
operator|(
operator|(
name|Attr
operator|)
name|node
operator|)
operator|.
name|getOwnerElement
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|node
operator|.
name|getParentNode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

