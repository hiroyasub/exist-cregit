begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|QName
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
name|ReadOnlyException
import|;
end_import

begin_comment
comment|/** Receives callback event during document(s) loading;  * implemented by several classes that generate various indices;  * Observer Design Pattern: role Observer;   * the class @link org.exist.storage.NativeBroker is the subjet (alias observable). */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentLoadingObserver
block|{
comment|/** store and index given element */
specifier|public
specifier|abstract
name|void
name|storeElement
parameter_list|(
name|int
name|xpathType
parameter_list|,
name|ElementImpl
name|node
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
comment|/** store and index given attribute */
specifier|public
specifier|abstract
name|void
name|storeAttribute
parameter_list|(
name|RangeIndexSpec
name|spec
parameter_list|,
name|AttrImpl
name|node
parameter_list|)
function_decl|;
comment|/** Add an index entry for the given QName and NodeProxy.      * Added entries are written to the list of pending entries. Call      * {@link #flush()} to flush all pending entries.      */
specifier|public
name|void
name|addRow
parameter_list|(
name|QName
name|qname
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
function_decl|;
comment|/** set the current document; generally called before calling an operation */
specifier|public
specifier|abstract
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
function_decl|;
comment|/** writes the pending items, for the current document's collection */
specifier|public
specifier|abstract
name|void
name|flush
parameter_list|()
function_decl|;
comment|/** triggers a cache sync, i.e. forces to write out all cached pages.	 	 sync() is called from time to time by the background sync daemon. */
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|()
function_decl|;
comment|/** 	 * Drop all index entries for the given collection. 	 *  	 * @param collection 	 */
specifier|public
specifier|abstract
name|void
name|dropIndex
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 * Drop all index entries for the given document. 	 *  	 * @param doc 	 * @throws ReadOnlyException 	 */
specifier|public
specifier|abstract
name|void
name|dropIndex
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|ReadOnlyException
function_decl|;
comment|/**      * Reindexes all pending items for the specified document. Similar to the normal index process,      * items to be reindexed are added to the internal pending list via methods       * {@link #addRow(QName, NodeProxy)}, {@link #storeElement(int, ElementImpl, String),      * and {@link #storeAttribute(RangeIndexSpec, AttrImpl)}. Method reindex then scans this      * list and updates the items in the index to reflect the reindexed document.      *       * @param oldDoc the document to be reindexed.      * @param node if != null, only nodes being descendants of the specified node will be      * reindexed. Other nodes are not touched. This is used for a partial reindex.      */
specifier|public
specifier|abstract
name|void
name|reindex
parameter_list|(
name|DocumentImpl
name|oldDoc
parameter_list|,
name|NodeImpl
name|node
parameter_list|)
function_decl|;
comment|/** remove all pending modifications, for the current document. */
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

