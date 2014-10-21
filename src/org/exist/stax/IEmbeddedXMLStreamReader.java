begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|stax
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
name|IStoredNode
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
name|XMLString
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
name|StreamFilter
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

begin_interface
specifier|public
interface|interface
name|IEmbeddedXMLStreamReader
extends|extends
name|ExtendedXMLStreamReader
block|{
comment|/**      * Deserialize the node at the current position of the cursor and return      * it as a {@link org.exist.dom.persistent.IStoredNode}.      *      * @return the node at the current position.      */
name|IStoredNode
name|getNode
parameter_list|()
function_decl|;
comment|/**      * Returns the last node in document sequence that occurs before the      * current node. Usually used to find the last child before an END_ELEMENT      * event.      *      * @return the last node in document sequence before the current node      */
name|IStoredNode
name|getPreviousNode
parameter_list|()
function_decl|;
comment|/**      * Iterates over each node until      * the filter returns false      *      * @param filter      */
name|void
name|filter
parameter_list|(
name|StreamFilter
name|filter
parameter_list|)
throws|throws
name|XMLStreamException
function_decl|;
comment|/**      * Get the Node Type      * as used in the persistent      * DOM {@see org.exist.storage.Signatures}      */
name|short
name|getNodeType
parameter_list|()
function_decl|;
comment|/**      * Returns the current value of the parse event as an XMLString,      * this returns the string value of a CHARACTERS event,      * returns the value of a COMMENT, the replacement value      * the string value of a CDATA section or      * the string value for a SPACE event.      *      * @return the current text or the empty text      */
name|XMLString
name|getXMLText
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

