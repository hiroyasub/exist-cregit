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
name|dom
operator|.
name|persistent
operator|.
name|NodeHandle
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_interface
specifier|public
interface|interface
name|IEmbeddedXMLStreamReader
extends|extends
name|ExtendedXMLStreamReader
block|{
comment|/**      * Reposition the stream reader to another start node.      *      * NOTE: This maybe in a different document!      *      * @param broker the database broker.      * @param node the new start node.      * @param reportAttributes if set to true, attributes will be reported as top-level events.      *      * @throws java.io.IOException if an error occurs whilst repositioning the stream      */
name|void
name|reposition
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|NodeHandle
name|node
parameter_list|,
specifier|final
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
comment|/**      * Iterates over each node until      * the filter returns false      *      * @param filter the filter      *      * @throws XMLStreamException if an error occurs whilst iterating.      */
name|void
name|filter
parameter_list|(
name|StreamFilter
name|filter
parameter_list|)
throws|throws
name|XMLStreamException
function_decl|;
comment|/**      * Get the Node Type      * as used in the persistent      * DOM.      *      * Types are defined in {@link org.exist.storage.Signatures}      *      * @return the node type      */
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

