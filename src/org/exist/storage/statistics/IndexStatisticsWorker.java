begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2018 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|statistics
package|;
end_package

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
name|IStoredNode
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
name|NodeSet
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
name|indexing
operator|.
name|AbstractStreamListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|MatchListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|StreamListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|StreamListener
operator|.
name|ReindexMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|ExtendedXMLStreamReader
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
name|NativeBroker
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
name|NodePath
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
name|btree
operator|.
name|BTreeCallback
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
name|btree
operator|.
name|Value
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
name|index
operator|.
name|CollectionStore
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
name|io
operator|.
name|VariableByteInput
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
name|Occurrences
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
name|QueryRewriter
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
name|TerminatedException
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|IndexStatisticsWorker
implements|implements
name|IndexWorker
block|{
specifier|private
specifier|final
name|IndexStatistics
name|index
decl_stmt|;
specifier|private
specifier|final
name|StatisticsListener
name|listener
init|=
operator|new
name|StatisticsListener
argument_list|()
decl_stmt|;
specifier|private
name|DataGuide
name|perDocGuide
init|=
literal|null
decl_stmt|;
specifier|private
name|ReindexMode
name|mode
init|=
name|ReindexMode
operator|.
name|STORE
decl_stmt|;
specifier|private
name|DocumentImpl
name|currentDoc
init|=
literal|null
decl_stmt|;
specifier|public
name|IndexStatisticsWorker
parameter_list|(
specifier|final
name|IndexStatistics
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|index
operator|.
name|getIndexId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|index
operator|.
name|getIndexName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryRewriter
name|getQueryRewriter
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|configure
parameter_list|(
specifier|final
name|IndexController
name|controller
parameter_list|,
specifier|final
name|NodeList
name|configNodes
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|setDocument
argument_list|(
name|doc
argument_list|,
name|ReindexMode
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
specifier|final
name|ReindexMode
name|mode
parameter_list|)
block|{
name|this
operator|.
name|perDocGuide
operator|=
operator|new
name|DataGuide
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentDoc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMode
parameter_list|(
specifier|final
name|ReindexMode
name|mode
parameter_list|)
block|{
name|perDocGuide
operator|=
operator|new
name|DataGuide
argument_list|()
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|ReindexMode
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|IStoredNode
parameter_list|>
name|IStoredNode
name|getReindexRoot
parameter_list|(
specifier|final
name|IStoredNode
argument_list|<
name|T
argument_list|>
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|boolean
name|insert
parameter_list|,
specifier|final
name|boolean
name|includeSelf
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|StreamListener
name|getListener
parameter_list|()
block|{
if|if
condition|(
name|mode
operator|==
name|ReindexMode
operator|.
name|STORE
condition|)
block|{
return|return
name|listener
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|MatchListener
name|getMatchListener
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|NodeProxy
name|proxy
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|perDocGuide
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|mergeStats
argument_list|(
name|perDocGuide
argument_list|)
expr_stmt|;
comment|//            System.out.println(index.toString());
block|}
name|perDocGuide
operator|=
operator|new
name|DataGuide
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|updateIndex
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
name|perDocGuide
operator|=
operator|new
name|DataGuide
argument_list|()
expr_stmt|;
specifier|final
name|DocumentCallback
name|cb
init|=
operator|new
name|DocumentCallback
argument_list|(
name|broker
argument_list|)
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|getResourcesFailsafe
argument_list|(
name|cb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TerminatedException
name|e
parameter_list|)
block|{
comment|// thrown when the db shuts down. ignore.
block|}
name|index
operator|.
name|updateStats
argument_list|(
name|perDocGuide
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|updateDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
specifier|final
name|ElementImpl
name|root
init|=
operator|(
name|ElementImpl
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
specifier|final
name|int
name|rootLevel
init|=
name|root
operator|.
name|getNodeId
argument_list|()
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
specifier|final
name|Deque
argument_list|<
name|NodeStats
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ExtendedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|getXMLStreamReader
argument_list|(
name|root
argument_list|,
literal|false
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
for|for
control|(
specifier|final
name|NodeStats
name|next
range|:
name|stack
control|)
block|{
name|next
operator|.
name|incDepth
argument_list|()
expr_stmt|;
block|}
specifier|final
name|QName
name|qname
init|=
name|reader
operator|.
name|getQName
argument_list|()
decl_stmt|;
name|path
operator|.
name|addComponent
argument_list|(
name|qname
argument_list|)
expr_stmt|;
specifier|final
name|NodeStats
name|nodeStats
init|=
name|perDocGuide
operator|.
name|add
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|nodeStats
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|END_ELEMENT
case|:
name|path
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
specifier|final
name|NodeStats
name|stats
init|=
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|stats
operator|.
name|updateMaxDepth
argument_list|()
expr_stmt|;
specifier|final
name|NodeId
name|otherId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|otherLevel
init|=
name|otherId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherLevel
operator|==
name|rootLevel
condition|)
block|{
comment|// finished `root element...
break|break;
comment|// exit-while
block|}
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XMLStreamException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeCollection
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|boolean
name|reindex
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkIndex
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Occurrences
index|[]
name|scanIndex
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|DocumentSet
name|docs
parameter_list|,
specifier|final
name|NodeSet
name|contextSet
parameter_list|,
specifier|final
name|Map
name|hints
parameter_list|)
block|{
return|return
operator|new
name|Occurrences
index|[
literal|0
index|]
return|;
block|}
specifier|private
class|class
name|StatisticsListener
extends|extends
name|AbstractStreamListener
block|{
specifier|private
specifier|final
name|Deque
argument_list|<
name|NodeStats
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|ElementImpl
name|element
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|)
block|{
name|super
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|perDocGuide
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|NodeStats
name|next
range|:
name|stack
control|)
block|{
name|next
operator|.
name|incDepth
argument_list|()
expr_stmt|;
block|}
specifier|final
name|NodeStats
name|nodeStats
init|=
name|perDocGuide
operator|.
name|add
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|nodeStats
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|ElementImpl
name|element
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|)
block|{
name|super
operator|.
name|endElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|perDocGuide
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NodeStats
name|stats
init|=
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|stats
operator|.
name|updateMaxDepth
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexWorker
name|getWorker
parameter_list|()
block|{
return|return
name|IndexStatisticsWorker
operator|.
name|this
return|;
block|}
block|}
specifier|private
class|class
name|DocumentCallback
implements|implements
name|BTreeCallback
block|{
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|DocumentCallback
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|indexInfo
parameter_list|(
specifier|final
name|Value
name|key
parameter_list|,
specifier|final
name|long
name|pointer
parameter_list|)
throws|throws
name|TerminatedException
block|{
specifier|final
name|CollectionStore
name|store
init|=
operator|(
name|CollectionStore
operator|)
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getStorage
argument_list|(
name|NativeBroker
operator|.
name|COLLECTIONS_DBX_ID
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|byte
name|type
init|=
name|key
operator|.
name|data
argument_list|()
index|[
name|key
operator|.
name|start
argument_list|()
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|DocumentImpl
operator|.
name|LENGTH_DOCUMENT_TYPE
index|]
decl_stmt|;
specifier|final
name|VariableByteInput
name|istream
init|=
name|store
operator|.
name|getAsStream
argument_list|(
name|pointer
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
operator|new
name|DocumentImpl
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|doc
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
name|updateDocument
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|IndexStatistics
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"An error occurred while regenerating index statistics: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

