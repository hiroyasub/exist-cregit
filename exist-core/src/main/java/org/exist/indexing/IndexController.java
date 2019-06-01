begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

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
name|TextImpl
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
name|AbstractCharacterData
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
name|IStoredNode
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
name|MetaStorage
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
name|MetaStreamListener
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
name|DatabaseConfigurationException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Internally used to dispatch an operation to each of the  * registered indexes. An IndexController instance can be  * retrieved via {@link org.exist.storage.DBBroker#getIndexController()}.  */
end_comment

begin_class
specifier|public
class|class
name|IndexController
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexWorker
argument_list|>
name|indexWorkers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|StreamListener
name|listener
init|=
literal|null
decl_stmt|;
specifier|private
name|DocumentImpl
name|currentDoc
init|=
literal|null
decl_stmt|;
specifier|private
name|ReindexMode
name|currentMode
init|=
name|ReindexMode
operator|.
name|UNKNOWN
decl_stmt|;
specifier|private
name|boolean
name|reindexing
decl_stmt|;
specifier|public
name|IndexController
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
specifier|final
name|List
argument_list|<
name|IndexWorker
argument_list|>
name|workers
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getIndexManager
argument_list|()
operator|.
name|getWorkers
argument_list|(
name|broker
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|worker
range|:
name|workers
control|)
block|{
name|indexWorkers
operator|.
name|put
argument_list|(
name|worker
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|worker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Configures all index workers registered with the db instance.      *       * @param configNodes lists the top-level child nodes below the&lt;index&gt; element in collection.xconf      * @param namespaces the active prefix/namespace map      * @return an arbitrary configuration object to be kept for this index in the collection configuration      * @throws DatabaseConfigurationException if a configuration error occurs      */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configure
parameter_list|(
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
throws|throws
name|DatabaseConfigurationException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|indexWorker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|Object
name|conf
init|=
name|indexWorker
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|configNodes
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|indexWorker
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
comment|/**      * Returns an {@link org.exist.indexing.IndexWorker} instance corresponding      * to the specified type of index in indexId. The indexId should be the same one      * as returned by {@link org.exist.indexing.IndexWorker#getIndexId()}.      *       * @param indexId The id of the index      * @return instance of index worker      */
specifier|public
name|IndexWorker
name|getWorkerByIndexId
parameter_list|(
specifier|final
name|String
name|indexId
parameter_list|)
block|{
return|return
name|indexWorkers
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
return|;
block|}
comment|/**      * Returns an {@link org.exist.indexing.IndexWorker} instance corresponding      * to the specified index named by indexName. The indexName should be the same one      * as returned by {@link org.exist.indexing.IndexWorker#getIndexName()}.      *       * @param indexName The name of the index      * @return instance of index worker      */
specifier|public
name|IndexWorker
name|getWorkerByIndexName
parameter_list|(
specifier|final
name|String
name|indexName
parameter_list|)
block|{
for|for
control|(
specifier|final
name|IndexWorker
name|worker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|indexName
operator|.
name|equals
argument_list|(
name|worker
operator|.
name|getIndexName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|worker
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Sets the document for the next operation.      *       * @param doc the document      *      * @deprecated use getStreamListener(DocumentImpl, ReindexMode)      */
annotation|@
name|Deprecated
specifier|public
name|void
name|setDocument
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
if|if
condition|(
name|currentDoc
operator|!=
name|doc
condition|)
block|{
comment|//Reset listener
name|listener
operator|=
literal|null
expr_stmt|;
block|}
name|currentDoc
operator|=
name|doc
expr_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|indexWorker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
name|indexWorker
operator|.
name|setDocument
argument_list|(
name|currentDoc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the the mode for the next operation.      *       * @param mode the mode, one of {@link ReindexMode#UNKNOWN}, {@link ReindexMode#STORE},      * {@link ReindexMode#REMOVE_SOME_NODES} or {@link ReindexMode#REMOVE_ALL_NODES}.      *      * @deprecated use getStreamListener(DocumentImpl, ReindexMode)      */
annotation|@
name|Deprecated
specifier|public
name|void
name|setMode
parameter_list|(
specifier|final
name|ReindexMode
name|mode
parameter_list|)
block|{
if|if
condition|(
name|currentMode
operator|!=
name|mode
condition|)
block|{
comment|//Reset listener
name|listener
operator|=
literal|null
expr_stmt|;
block|}
name|currentMode
operator|=
name|mode
expr_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|indexWorker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
name|indexWorker
operator|.
name|setMode
argument_list|(
name|currentMode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the document for the next operation.      *       * @return the document      */
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
comment|/**      * Returns the mode for the next operation.      *       * @return the document      */
specifier|public
name|ReindexMode
name|getMode
parameter_list|()
block|{
return|return
name|currentMode
return|;
block|}
comment|/**      * Sets the document and the mode for the next operation.      *       * @param doc the document      * @param mode the mode, one of {@link ReindexMode#UNKNOWN}, {@link ReindexMode#STORE},      * {@link ReindexMode#REMOVE_SOME_NODES} or {@link ReindexMode#REMOVE_ALL_NODES}.      *      * @deprecated use getStreamListener(DocumentImpl, ReindexMode)      */
annotation|@
name|Deprecated
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
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|setMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/**      * Flushes all index workers.      */
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|indexWorkers
operator|.
name|values
argument_list|()
operator|.
name|forEach
argument_list|(
name|IndexWorker
operator|::
name|flush
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove all indexes defined on the specified collection.      *      * @param collection the collection to remove      * @param broker the broker that will perform the operation      */
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
throws|throws
name|PermissionDeniedException
block|{
for|for
control|(
specifier|final
name|IndexWorker
name|indexWorker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
name|indexWorker
operator|.
name|removeCollection
argument_list|(
name|collection
argument_list|,
name|broker
argument_list|,
name|reindex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Re-index all nodes below the specified root node, using the given mode.      *      * @param transaction the current transaction      * @param reindexRoot the node from which reindexing should occur      * @param mode the mode, one of {@link ReindexMode#UNKNOWN}, {@link ReindexMode#STORE},      * {@link ReindexMode#REMOVE_SOME_NODES} or {@link ReindexMode#REMOVE_ALL_NODES}.      */
specifier|public
name|void
name|reindex
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|IStoredNode
argument_list|<
name|?
extends|extends
name|IStoredNode
argument_list|>
name|reindexRoot
parameter_list|,
specifier|final
name|ReindexMode
name|mode
parameter_list|)
block|{
if|if
condition|(
name|reindexRoot
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|setReindexing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|IStoredNode
argument_list|<
name|?
extends|extends
name|IStoredNode
argument_list|>
name|node
init|=
name|broker
operator|.
name|objectWith
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|reindexRoot
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|reindexRoot
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|listener
operator|=
name|getStreamListener
argument_list|(
name|node
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|listener
operator|.
name|startIndexDocument
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
try|try
block|{
name|IndexUtils
operator|.
name|scanNode
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|node
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|listener
operator|.
name|endIndexDocument
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|setReindexing
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isReindexing
parameter_list|()
block|{
return|return
name|reindexing
return|;
block|}
specifier|private
name|void
name|setReindexing
parameter_list|(
specifier|final
name|boolean
name|reindexing
parameter_list|)
block|{
name|this
operator|.
name|reindexing
operator|=
name|reindexing
expr_stmt|;
block|}
comment|/**      * When adding or removing nodes to or from the document tree, it might become      * necessary to re-index some parts of the tree, in particular if indexes are defined      * on mixed content nodes. This method will return the top-most root.      *      * @param node the node to be modified.      * @param path the NodePath of the node      * @return the top-most root node to be re-indexed      */
specifier|public
name|IStoredNode
name|getReindexRoot
parameter_list|(
specifier|final
name|IStoredNode
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|boolean
name|insert
parameter_list|)
block|{
return|return
name|getReindexRoot
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
name|insert
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * When adding or removing nodes to or from the document tree, it might become      * necessary to re-index some parts of the tree, in particular if indexes are defined      * on mixed content nodes. This method will return the top-most root.      *      * @param node the node to be modified.      * @param path path the NodePath of the node      * @param includeSelf if set to true, the current node itself will be included in the check      * @return the top-most root node to be re-indexed      */
specifier|public
name|IStoredNode
name|getReindexRoot
parameter_list|(
specifier|final
name|IStoredNode
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
name|IStoredNode
name|next
decl_stmt|;
name|IStoredNode
name|top
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|indexWorker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
name|next
operator|=
name|indexWorker
operator|.
name|getReindexRoot
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
name|insert
argument_list|,
name|includeSelf
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
operator|(
name|top
operator|==
literal|null
operator|||
name|top
operator|.
name|getNodeId
argument_list|()
operator|.
name|isDescendantOf
argument_list|(
name|next
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|top
operator|=
name|next
expr_stmt|;
block|}
block|}
if|if
condition|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|top
operator|=
name|node
expr_stmt|;
block|}
return|return
name|top
return|;
block|}
specifier|public
name|StreamListener
name|getStreamListener
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
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|setMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
return|return
name|getStreamListener
argument_list|()
return|;
block|}
comment|/**      * Returns a chain of {@link org.exist.indexing.StreamListener}, one      * for each index configured on the current document for the current mode.      * Note that the chain is reinitialized when the operating mode changes.      * That allows workers to return different {@link org.exist.indexing.StreamListener}      * for each mode.      *      * @return the first listener in the chain of StreamListeners      */
specifier|public
name|StreamListener
name|getStreamListener
parameter_list|()
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|StreamListener
name|next
init|=
name|listener
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
comment|// wolf: setDocument() should have been called before
comment|// next.getWorker().setDocument(currentDoc, currentMode);
name|next
operator|=
name|next
operator|.
name|getNextInChain
argument_list|()
expr_stmt|;
block|}
return|return
name|listener
return|;
block|}
name|StreamListener
name|first
init|=
literal|null
decl_stmt|;
name|StreamListener
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|worker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
comment|// wolf: setDocument() should have been called before
comment|//worker.setDocument(currentDoc, currentMode);
specifier|final
name|StreamListener
name|current
init|=
name|worker
operator|.
name|getListener
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|current
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|previous
operator|.
name|setNextInChain
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|previous
operator|=
name|current
expr_stmt|;
block|}
block|}
name|listener
operator|=
name|first
expr_stmt|;
return|return
name|listener
return|;
block|}
comment|/**      * Helper method: index a single node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|indexNode
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|IStoredNode
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
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
name|listener
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
operator|(
name|ElementImpl
operator|)
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
name|listener
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
operator|(
name|AbstractCharacterData
operator|)
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|listener
operator|.
name|attribute
argument_list|(
name|transaction
argument_list|,
operator|(
name|AttrImpl
operator|)
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**      * Helper method: indexing is starting for a document      *      * @param transaction the current transaction      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|startIndexDocument
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|startIndexDocument
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Helper method: index a single element node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
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
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Helper method: dispatch a single endElement event to the specified listener.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives index events      */
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
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|endElement
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Helper method: index a single attribute node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|attribute
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|AttrImpl
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|attribute
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Helper method: index a single text node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|TextImpl
name|node
parameter_list|,
specifier|final
name|NodePath
name|path
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Helper method: indexing has finished for a document      *      * @param transaction the current transaction      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|endIndexDocument
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|StreamListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|endIndexDocument
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the match listener for this node.      *       * @param proxy a proxy to the node.      * @return the MatchListener       */
specifier|public
name|MatchListener
name|getMatchListener
parameter_list|(
specifier|final
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|MatchListener
name|first
init|=
literal|null
decl_stmt|;
name|MatchListener
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|worker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|MatchListener
name|current
init|=
name|worker
operator|.
name|getMatchListener
argument_list|(
name|broker
argument_list|,
name|proxy
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|current
expr_stmt|;
block|}
else|else
block|{
name|previous
operator|.
name|setNextInChain
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|current
expr_stmt|;
block|}
block|}
return|return
name|first
return|;
block|}
specifier|public
name|List
argument_list|<
name|QueryRewriter
argument_list|>
name|getQueryRewriters
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|QueryRewriter
argument_list|>
name|rewriters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|IndexWorker
name|indexWorker
range|:
name|indexWorkers
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|QueryRewriter
name|rewriter
init|=
name|indexWorker
operator|.
name|getQueryRewriter
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewriter
operator|!=
literal|null
condition|)
block|{
name|rewriters
operator|.
name|add
argument_list|(
name|rewriter
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rewriters
return|;
block|}
specifier|public
name|void
name|streamMetas
parameter_list|(
specifier|final
name|MetaStreamListener
name|listener
parameter_list|)
block|{
specifier|final
name|MetaStorage
name|ms
init|=
name|broker
operator|.
name|getDatabase
argument_list|()
operator|.
name|getMetaStorage
argument_list|()
decl_stmt|;
if|if
condition|(
name|ms
operator|!=
literal|null
condition|)
block|{
name|ms
operator|.
name|streamMetas
argument_list|(
name|currentDoc
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
