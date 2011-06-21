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
name|CharacterDataImpl
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
comment|/**      * Undefined mode      */
specifier|public
specifier|final
specifier|static
name|int
name|UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Mode for storing nodes of a document      */
specifier|public
specifier|final
specifier|static
name|int
name|STORE
init|=
literal|0
decl_stmt|;
comment|/**      * Mode for removing all the nodes of a document      */
specifier|public
specifier|final
specifier|static
name|int
name|REMOVE_ALL_NODES
init|=
literal|1
decl_stmt|;
comment|/**      * Mode for removing some nodes of a document      */
specifier|public
specifier|final
specifier|static
name|int
name|REMOVE_SOME_NODES
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|REMOVE_BINARY
init|=
literal|3
decl_stmt|;
comment|/**      * Retunrs the IndexWorker that owns this listener.      *       * @return the IndexWorker      */
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
name|CharacterDataImpl
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
block|}
end_interface

end_unit

