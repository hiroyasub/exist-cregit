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
name|NodeIndexListener
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
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
name|store
operator|.
name|StorageAddress
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
name|Lock
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
name|AbstractExpression
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
name|Cardinality
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|Modification
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Expression
name|select
decl_stmt|;
specifier|protected
name|Expression
name|value
decl_stmt|;
specifier|protected
name|DocumentSet
name|lockedDocuments
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param context 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#returnsType() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|select
operator|.
name|resetState
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
name|value
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression, int) 	 */
specifier|public
name|void
name|analyze
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
name|select
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
operator||
name|IN_UPDATE
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
name|value
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
operator||
name|IN_UPDATE
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Acquire a lock on all documents processed by this modification. 	 * We have to avoid that node positions change during the 	 * operation. 	 *  	 * @param nl 	 * @return 	 * @throws LockException 	 */
specifier|protected
name|NodeImpl
index|[]
name|selectAndLock
parameter_list|(
name|NodeSet
name|nodes
parameter_list|)
throws|throws
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
block|{
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
try|try
block|{
name|globalLock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|lockedDocuments
operator|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
comment|// acquire a lock on all documents
comment|// we have to avoid that node positions change
comment|// during the modification
name|lockedDocuments
operator|.
name|lock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NodeImpl
name|ql
index|[]
init|=
operator|new
name|NodeImpl
index|[
name|nodes
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
name|DocumentImpl
name|doc
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
name|ql
index|[
name|i
index|]
operator|=
operator|(
name|NodeImpl
operator|)
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|ql
index|[
name|i
index|]
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setBroker
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
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
name|release
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
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
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
name|Sequence
name|out
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
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
argument_list|)
expr_stmt|;
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
else|else
block|{
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|item
operator|)
operator|.
name|expand
argument_list|()
expr_stmt|;
comment|//                        System.out.println(context.getBroker().getSerializer().serialize((NodeValue)item));
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
name|SAXException
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
block|}
comment|/** 	 * Release all acquired document locks. 	 */
specifier|protected
name|void
name|unlockDocuments
parameter_list|()
block|{
if|if
condition|(
name|lockedDocuments
operator|==
literal|null
condition|)
return|return;
name|lockedDocuments
operator|.
name|unlock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Check if any of the modified documents needs defragmentation. 	 *  	 * Defragmentation will take place if the number of split pages in the 	 * document exceeds the limit defined in the configuration file. 	 *   	 * @param docs 	 */
specifier|protected
name|void
name|checkFragmentation
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|EXistException
block|{
name|DBBroker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|next
init|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|getSplitCount
argument_list|()
operator|>
name|broker
operator|.
name|getFragmentationLimit
argument_list|()
condition|)
name|broker
operator|.
name|defrag
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|broker
operator|.
name|consistencyCheck
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
specifier|static
class|class
name|IndexListener
implements|implements
name|NodeIndexListener
block|{
name|NodeImpl
index|[]
name|nodes
decl_stmt|;
specifier|public
name|IndexListener
parameter_list|(
name|NodeImpl
index|[]
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.dom.NodeIndexListener#nodeChanged(org.exist.dom.NodeImpl) 		 */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
name|long
name|address
init|=
name|node
operator|.
name|getInternalAddress
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|StorageAddress
operator|.
name|equals
argument_list|(
name|nodes
index|[
name|i
index|]
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|address
argument_list|)
condition|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

