begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|AbstractCharacterData
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

begin_comment
comment|/**  * Callback interface which receives index events. StreamListeners are chained;  * events should be forwarded to the next listener in the chain (if there is any).  */
end_comment

begin_interface
specifier|public
interface|interface
name|StreamListener
block|{
enum|enum
name|ReindexMode
block|{
comment|/**          * Undefined mode          */
name|UNKNOWN
block|,
comment|/**          * Mode for storing nodes of a document          */
name|STORE
block|,
comment|/**          * Mode for replacing the nodes of a document with another document          * this is really a group mode, which is later followed by {@link #REMOVE_ALL_NODES}          * and then {@link #STORE}          */
name|REPLACE_DOCUMENT
block|,
comment|/**          * Mode for removing all the nodes of a document          */
name|REMOVE_ALL_NODES
block|,
comment|/**          * Mode for removing some nodes of a document          */
name|REMOVE_SOME_NODES
block|,
comment|/**          * Mode for removing a binary document          */
name|REMOVE_BINARY
block|}
comment|/**      * Returns the IndexWorker that owns this listener.      *       * @return the IndexWorker      */
name|IndexWorker
name|getWorker
parameter_list|()
function_decl|;
comment|/**      * Set the next stream listener in the chain. Events should always be forwarded      * to the next listener.      *      * @param listener the next listener in the chain.      */
name|void
name|setNextInChain
parameter_list|(
name|StreamListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Returns the next stream listener in the chain. This should usually be the one      * that was passed in from {@link #setNextInChain(StreamListener)}.      *      * @return the next listener in the chain.      */
name|StreamListener
name|getNextInChain
parameter_list|()
function_decl|;
comment|/**      * Starting to replace a document      *      * After which the sequence of {@link #startIndexDocument(Txn)} / events / {@link #endIndexDocument(Txn)}      * will be called twice, first where the index mode will be {@link ReindexMode#REMOVE_ALL_NODES}      * and second where the index mode will be {@link ReindexMode#STORE}      * this is then finished by {@link #endReplaceDocument(Txn)}      *      * This can be used in conjunction with {@link #endReplaceDocument(Txn)} in indexes      * which support differential updates      *      * @param transaction The current executing transaction      */
name|void
name|startReplaceDocument
parameter_list|(
name|Txn
name|transaction
parameter_list|)
function_decl|;
comment|/**      * Finished replacing a document      *      * See {@link #startReplaceDocument(Txn)} for details      *      * @param transaction The current executing transaction      */
name|void
name|endReplaceDocument
parameter_list|(
name|Txn
name|transaction
parameter_list|)
function_decl|;
comment|/**      * Starting to index a document      *      * @param transaction the current transaction      */
name|void
name|startIndexDocument
parameter_list|(
name|Txn
name|transaction
parameter_list|)
function_decl|;
comment|/**      * Processed the opening tag of an element.      *      * @param transaction the current transaction      * @param element the element which has been stored to the db      * @param path the current node path      */
name|void
name|startElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
function_decl|;
comment|/**      * An attribute has been stored.      *      * @param transaction the current transaction      * @param attrib the attribute which has been stored to the db      * @param path the current node path      */
name|void
name|attribute
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|AttrImpl
name|attrib
parameter_list|,
name|NodePath
name|path
parameter_list|)
function_decl|;
comment|/**      * A text node has been stored.      * @param transaction the current transaction      * @param text the text node which has been stored to the db.      * @param path the current node path      */
name|void
name|characters
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|AbstractCharacterData
name|text
parameter_list|,
name|NodePath
name|path
parameter_list|)
function_decl|;
comment|/**      * Processed the closing tag of an element.      *      * @param transaction the current transaction      * @param element the element which has been stored to the db      * @param path the current node path      */
name|void
name|endElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
function_decl|;
comment|/**      * Finishing storing a document      *      * @param transaction the current transaction      */
name|void
name|endIndexDocument
parameter_list|(
name|Txn
name|transaction
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

