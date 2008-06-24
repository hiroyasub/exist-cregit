begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
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
name|Iterator
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
name|TextImpl
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
comment|/**  * Internally used to dispatch an operation to each of the  * registered indexes. An IndexController instance can be  * retrieved via {@link org.exist.storage.DBBroker#getIndexController()}.  *   */
end_comment

begin_class
specifier|public
class|class
name|IndexController
block|{
specifier|protected
name|Map
name|indexWorkers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
decl_stmt|;
specifier|protected
name|StreamListener
name|listener
init|=
literal|null
decl_stmt|;
specifier|protected
name|DocumentImpl
name|currentDoc
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|currentMode
init|=
name|StreamListener
operator|.
name|UNKNOWN
decl_stmt|;
specifier|public
name|IndexController
parameter_list|(
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
name|IndexWorker
index|[]
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|workers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexWorkers
operator|.
name|put
argument_list|(
name|workers
index|[
name|i
index|]
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|workers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * TODO: temporary method to plug in fulltext index.      * Remove once new fulltext index module is ready.      *       * @param worker      */
specifier|public
name|void
name|addIndexWorker
parameter_list|(
name|IndexWorker
name|worker
parameter_list|)
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
comment|/**      * Configures all index workers registered with the db instance.      *       * @param configNodes lists the top-level child nodes below the&lt;index&gt; element in collection.xconf      * @param namespaces the active prefix/namespace map      * @return an arbitrary configuration object to be kept for this index in the collection configuration      * @throws DatabaseConfigurationException if a configuration error occurs      */
specifier|public
name|Map
name|configure
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|IndexWorker
name|indexWorker
decl_stmt|;
name|Object
name|conf
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|conf
operator|=
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
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
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
return|return
name|map
return|;
block|}
comment|/**      * Returns an {@link org.exist.indexing.IndexWorker} instance corresponding      * to the specified type of index in indexId. The indexId should be the same one      * as returned by {@link org.exist.indexing.IndexWorker#getIndexId()}.      *       * @param indexId      * @return instance of index worker      */
specifier|public
name|IndexWorker
name|getWorkerByIndexId
parameter_list|(
name|String
name|indexId
parameter_list|)
block|{
return|return
operator|(
name|IndexWorker
operator|)
name|indexWorkers
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
return|;
block|}
comment|/**      * Returns an {@link org.exist.indexing.IndexWorker} instance corresponding      * to the specified index named by indexName. The indexName should be the same one      * as returned by {@link org.exist.indexing.IndexWorker#getIndexName()}.      *       * @param indexName      * @return instance of index worker      */
specifier|public
name|IndexWorker
name|getWorkerByIndexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|IndexWorker
name|worker
init|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
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
return|return
name|worker
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Sets the document for the next operation.      *       * @param doc the document      */
specifier|public
name|void
name|setDocument
parameter_list|(
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
comment|//Reset listener
name|listener
operator|=
literal|null
expr_stmt|;
name|currentDoc
operator|=
name|doc
expr_stmt|;
name|IndexWorker
name|indexWorker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|indexWorker
operator|.
name|setDocument
argument_list|(
name|currentDoc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the the mode for the next operation.      *       * @param mode the mode, one of {@link StreamListener#UNKNOWN}, {@link StreamListener#STORE},       * {@link StreamListener#REMOVE_SOME_NODES} or {@link StreamListener#REMOVE_ALL_NODES}.      */
specifier|public
name|void
name|setMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
if|if
condition|(
name|currentMode
operator|!=
name|mode
condition|)
comment|//Reset listener
name|listener
operator|=
literal|null
expr_stmt|;
name|currentMode
operator|=
name|mode
expr_stmt|;
name|IndexWorker
name|indexWorker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
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
name|int
name|getMode
parameter_list|()
block|{
return|return
name|currentMode
return|;
block|}
comment|/**      * Sets the document and the mode for the next operation.      *       * @param doc the document      * @param mode the mode, one of {@link StreamListener#UNKNOWN}, {@link StreamListener#STORE},       * {@link StreamListener#REMOVE_SOME_NODES} or {@link StreamListener#REMOVE_ALL_NODES}.      */
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
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
name|IndexWorker
name|indexWorker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|indexWorker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Remove all indexes defined on the specified collection.      *      * @param collection the collection to remove      * @param broker the broker that will perform the operation      */
specifier|public
name|void
name|removeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
block|{
name|IndexWorker
name|indexWorker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|indexWorker
operator|.
name|removeCollection
argument_list|(
name|collection
argument_list|,
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reindex all nodes below the specified root node, using the given mode.      *      * @param transaction the current transaction      * @param reindexRoot the node from which reindexing should occur      * @param mode the mode, one of {@link StreamListener#UNKNOWN}, {@link StreamListener#STORE},       * {@link StreamListener#REMOVE_SOME_NODES} or {@link StreamListener#REMOVE_ALL_NODES}.      */
specifier|public
name|void
name|reindex
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|reindexRoot
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
if|if
condition|(
name|reindexRoot
operator|==
literal|null
condition|)
return|return;
name|reindexRoot
operator|=
name|reindexRoot
operator|.
name|getDocument
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|objectWith
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|reindexRoot
operator|.
name|getDocument
argument_list|()
argument_list|,
name|reindexRoot
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setDocument
argument_list|(
name|reindexRoot
operator|.
name|getDocument
argument_list|()
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|getStreamListener
argument_list|()
expr_stmt|;
name|IndexUtils
operator|.
name|scanNode
argument_list|(
name|transaction
argument_list|,
name|reindexRoot
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * When adding or removing nodes to or from the document tree, it might become      * necessary to reindex some parts of the tree, in particular if indexes are defined      * on mixed content nodes. This method will call      * {@link IndexWorker#getReindexRoot(org.exist.dom.StoredNode, org.exist.storage.NodePath, boolean)}      * on each configured index. It will then return the top-most root.      *      * @param node the node to be modified.      * @param path the NodePath of the node      * @return the top-most root node to be reindexed      */
specifier|public
name|StoredNode
name|getReindexRoot
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
return|return
name|getReindexRoot
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * When adding or removing nodes to or from the document tree, it might become      * necessary to reindex some parts of the tree, in particular if indexes are defined      * on mixed content nodes. This method will call      * {@link IndexWorker#getReindexRoot(org.exist.dom.StoredNode, org.exist.storage.NodePath, boolean)}      * on each configured index. It will then return the top-most root.      *      * @param node the node to be modified.      * @param path path the NodePath of the node      * @param includeSelf if set to true, the current node itself will be included in the check      * @return the top-most root node to be reindexed      */
specifier|public
name|StoredNode
name|getReindexRoot
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
name|IndexWorker
name|indexWorker
decl_stmt|;
name|StoredNode
name|next
decl_stmt|,
name|top
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
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
name|top
operator|=
name|next
expr_stmt|;
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
name|top
operator|=
name|node
expr_stmt|;
return|return
name|top
return|;
block|}
comment|/**      * Returns a chain of {@link org.exist.indexing.StreamListener}, one      * for each index configured on the current document for the current mode.      * Note that the chain is reinitialized when the operating mode changes.      * That allows workers to return different {@link org.exist.indexing.StreamListener}      * for each mode.        *      * @return the first listener in the chain of StreamListeners      */
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
comment|//                next.getWorker().setDocument(currentDoc, currentMode);
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
name|current
decl_stmt|,
name|previous
init|=
literal|null
decl_stmt|;
name|IndexWorker
name|worker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|worker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// wolf: setDocument() should have been called before
comment|//            worker.setDocument(currentDoc, currentMode);
name|current
operator|=
name|worker
operator|.
name|getListener
argument_list|()
expr_stmt|;
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
name|previous
operator|.
name|setNextInChain
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
name|previous
operator|=
name|current
expr_stmt|;
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
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
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
name|listener
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
operator|(
name|TextImpl
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
comment|/**      * Helper method: index a single element node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|startElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
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
comment|/**      * Helper method: dispatch a single endElement event to the specified listener.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives index events      */
specifier|public
name|void
name|endElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
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
comment|/**      * Helper method: index a single attribute node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|attribute
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|AttrImpl
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
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
comment|/**      * Helper method: index a single text node which has been added during an XUpdate or XQuery update expression.      *      * @param transaction the current transaction      * @param node the node to index      * @param path the node's NodePath      * @param listener the StreamListener which receives the index events      */
specifier|public
name|void
name|characters
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|TextImpl
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
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
comment|/**      * Returns the match listener for this node.      *       * @param proxy a proxy to the node.      * @return the MatchListener       */
specifier|public
name|MatchListener
name|getMatchListener
parameter_list|(
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
name|current
decl_stmt|,
name|previous
init|=
literal|null
decl_stmt|;
name|IndexWorker
name|worker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
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
name|worker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|current
operator|=
name|worker
operator|.
name|getMatchListener
argument_list|(
name|broker
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

