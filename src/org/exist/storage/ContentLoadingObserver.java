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
name|*
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
name|DBException
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
comment|/** Receives callback event during document(s) loading and removal;  * implemented by several classes that generate various indices;  * Observer Design Pattern: role Observer;   * the class @link org.exist.storage.NativeBroker is the subject (alias observable).  *   * startElement() and endElement() bear the same names as the corresponding SAX events.    * However storeXXX() have no corresponding method in SAX.  *   * Note: when we will have more than one runtime switch , we will refactor   * fullTextIndexSwitch into an object */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentLoadingObserver
block|{
comment|/** store and index given attribute */
comment|//TODO : remove the RangeIndexSpec dependency ASAP
specifier|public
name|void
name|storeAttribute
parameter_list|(
name|AttrImpl
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|int
name|indexingHint
parameter_list|,
name|RangeIndexSpec
name|spec
parameter_list|,
name|boolean
name|remove
parameter_list|)
function_decl|;
comment|/** store and index given text node */
specifier|public
name|void
name|storeText
parameter_list|(
name|TextImpl
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|int
name|indexingHint
parameter_list|)
function_decl|;
comment|/** 	 * The given node is being removed from the database.  	 */
specifier|public
name|void
name|removeNode
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|String
name|content
parameter_list|)
function_decl|;
comment|/** set the current document; generally called before calling an operation */
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
function_decl|;
comment|/** 	 * Drop all index entries for the given collection. 	 *  	 * @param collection 	 */
specifier|public
name|void
name|dropIndex
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/** 	 * Drop all index entries for the given document. 	 *  	 * @param doc 	 * @throws ReadOnlyException 	 */
specifier|public
name|void
name|dropIndex
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|ReadOnlyException
function_decl|;
comment|/** remove all pending modifications, for the current document. */
specifier|public
name|void
name|remove
parameter_list|()
function_decl|;
comment|/* The following methods are rather related to file management : create a dedicated interface ? /*  	/** writes the pending items, for the current document's collection */
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/** triggers a cache sync, i.e. forces to write out all cached pages.	 	 sync() is called from time to time by the background sync daemon. */
specifier|public
name|void
name|sync
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|close
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
name|void
name|closeAndRemove
parameter_list|()
function_decl|;
specifier|public
name|void
name|printStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

