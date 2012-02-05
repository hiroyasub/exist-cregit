begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
comment|/**  * Provide concurrent access to the index structure. Implements the core operations on the index.  * The methods in this class are used in a multi-threaded environment. Every thread accessing the  * database will have exactly one IndexWorker for every index. {@link org.exist.indexing.Index#getWorker(DBBroker)}  * should thus return a new IndexWorker whenever it is  called. Implementations of IndexWorker have  * to take care of synchronizing access to shared resources.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexWorker
block|{
comment|/**      * A key to a QName {@link java.util.List} "hint" to be used when the index scans its index entries      */
specifier|public
specifier|static
specifier|final
name|String
name|VALUE_COUNT
init|=
literal|"value_count"
decl_stmt|;
comment|/**      * Returns an ID which uniquely identifies this worker's index.      * @return a unique name identifying this worker's index.      */
specifier|public
name|String
name|getIndexId
parameter_list|()
function_decl|;
comment|/**      * Returns a name which uniquely identifies this worker's index.      * @return a unique name identifying this worker's index.      */
specifier|public
name|String
name|getIndexName
parameter_list|()
function_decl|;
comment|/**      * Read an index configuration from an collection.xconf configuration document.      *      * This method is called by the {@link org.exist.collections.CollectionConfiguration} while      * reading the collection.xconf configuration file for a given collection. The configNodes      * parameter lists all top-level child nodes below the&lt;index&gt; element in the      * collection.xconf. The IndexWorker should scan this list and handle those elements      * it understands.      *      * The returned Object will be stored in the collection configuration structure associated      * with each collection. It can later be retrieved from the collection configuration, e.g. to      * check if a given node should be indexed or not.      *      * @param configNodes lists the top-level child nodes below the&lt;index&gt; element in collection.xconf      * @param namespaces the active prefix/namespace map      * @return an arbitrary configuration object to be kept for this index in the collection configuration      * @throws DatabaseConfigurationException if a configuration error occurs      */
name|Object
name|configure
parameter_list|(
name|IndexController
name|controller
parameter_list|,
name|NodeList
name|configNodes
parameter_list|,
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
function_decl|;
comment|/**      * Notify this worker to operate on the specified document.      *      * @param doc the document which is processed      */
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
comment|/**      * Notify this worker to operate on the specified document, using the mode      * given. Mode will be one of {@link StreamListener#UNKNOWN}, {@link StreamListener#STORE},       * {@link StreamListener#REMOVE_SOME_NODES} or {@link StreamListener#REMOVE_ALL_NODES}.      *      * @param doc the document which is processed      * @param mode the current operation mode      */
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|mode
parameter_list|)
function_decl|;
comment|/**      * Notify this worker to operate using the mode      * given. Mode will be one of {@link StreamListener#UNKNOWN}, {@link StreamListener#STORE},      * {@link StreamListener#REMOVE_SOME_NODES} or {@link StreamListener#REMOVE_ALL_NODES}.      *      * @param mode the current operation mode      */
name|void
name|setMode
parameter_list|(
name|int
name|mode
parameter_list|)
function_decl|;
comment|/**      * Returns the document for the next operation.      *       * @return the document      */
name|DocumentImpl
name|getDocument
parameter_list|()
function_decl|;
comment|/**      * Returns the mode for the next operation.      *       * @return the document      */
name|int
name|getMode
parameter_list|()
function_decl|;
comment|/**      * When adding or removing nodes to or from the document tree, it might become      * necessary to reindex some parts of the tree, in particular if indexes are defined      * on mixed content nodes. This method will call      * {@link IndexWorker#getReindexRoot(org.exist.dom.StoredNode, org.exist.storage.NodePath, boolean)}      * on each configured index. It will then return the top-most root.      *      * @param node the node to be modified.      * @param path path the NodePath of the node      * @param includeSelf if set to true, the current node itself will be included in the check      * @return the top-most root node to be reindexed      */
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
function_decl|;
comment|/**      * Return a stream listener to index the current document in the current mode.      * There will never be more than one StreamListener being used per thread, so it is safe      * for the implementation to reuse a single StreamListener.      *      * Parameter mode specifies the type of the current operation.      *      * @return a StreamListener      */
name|StreamListener
name|getListener
parameter_list|()
function_decl|;
comment|/**      * Returns a {@link org.exist.indexing.MatchListener}, which can be used to filter      * (and manipulate) the XML output generated by the serializer when serializing      * query results. The method should return null if the implementation is not interested      * in receiving serialization events.      *      * @param proxy the NodeProxy which is being serialized      * @return a MatchListener or null if the implementation does not want to receive      * serialization events      */
name|MatchListener
name|getMatchListener
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
comment|/**      * Flush the index. This method will be called when indexing a document. The implementation should      * immediately process all data it has buffered (if there is any), release as many memory resources      * as it can and prepare for being reused for a different job.      */
name|void
name|flush
parameter_list|()
function_decl|;
comment|/**      * Remove all indexes for the given collection, its subcollections and      * all resources..      *      * @param collection The collection to remove      * @param broker The broker that will perform the operation      */
name|void
name|removeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**       * Checking index could be delegated to a worker. Use this method to do so.      * @param broker The broker that will perform the operation      * @return Whether or not the index if in a suitable state      */
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
comment|/**       * Return<strong>aggregated</strong> (on a document count basis)       * index entries for the specified document set. Aggregation can only occur if      * the index entries can be compared, i.e. if the index implements       * {@link org.exist.indexing.OrderedValuesIndex}, otherwise each entry will be considered      * as a single occurence.      * @param context       * @param docs The documents to which the index entries belong      * @param contextSet      * @param hints Some "hints" for retrieving the index entries. See such hints in      * {@link org.exist.indexing.OrderedValuesIndex} and {@link org.exist.indexing.QNamedKeysIndex}.      * @return Occurrences objects that contain :      *<ol>      *<li>a<strong>string</strong> representation of the index entry. This may change in the future.</li>      *<li>the number of occurrences for the index entry over all the documents</li>      *<li>the list of the documents in which the index entry is</li>      *</ol>       */
specifier|public
name|Occurrences
index|[]
name|scanIndex
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|Map
name|hints
parameter_list|)
function_decl|;
comment|//TODO : a scanIndex() method that would return an unaggregated list of index entries ?
block|}
end_interface

end_unit

