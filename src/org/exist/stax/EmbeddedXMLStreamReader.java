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
name|storage
operator|.
name|dom
operator|.
name|RawNodeIterator
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

begin_comment
comment|/**  * Lazy implementation of a StAX {@link javax.xml.stream.XMLStreamReader}, which directly reads  * information from the persistent DOM. The class is optimized to support fast scanning of the DOM, where only  * a few selected node properties are requested. Node properties are extracted on demand. For example, the QName of  * an element will not be read unless {@link #getText()} is called.  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedXMLStreamReader
extends|extends
name|AbstractEmbeddedXMLStreamReader
argument_list|<
name|RawNodeIterator
argument_list|>
block|{
comment|/**      * Construct an EmbeddedXMLStreamReader.      *      * @param doc              the document to which the start node belongs.      * @param iterator         a RawNodeIterator positioned on the start node.      * @param origin           an optional NodeHandle whose nodeId should match the first node in the stream      *                         (or null if no need to check)      * @param reportAttributes if set to true, attributes will be reported as top-level events.      * @throws XMLStreamException      */
specifier|public
name|EmbeddedXMLStreamReader
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
specifier|final
name|RawNodeIterator
name|iterator
parameter_list|,
specifier|final
name|NodeHandle
name|origin
parameter_list|,
specifier|final
name|boolean
name|reportAttributes
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|,
name|iterator
argument_list|,
name|origin
argument_list|,
name|reportAttributes
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the (internal) address of the node at the cursor's current      * position.      *      * @return internal address of node      */
specifier|public
name|long
name|getCurrentPosition
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|currentAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|verifyOriginNodeId
parameter_list|()
throws|throws
name|XMLStreamException
block|{
if|if
condition|(
operator|!
name|nodeId
operator|.
name|equals
argument_list|(
name|origin
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|super
operator|.
name|verifyOriginNodeId
argument_list|()
expr_stmt|;
name|origin
operator|.
name|setInternalAddress
argument_list|(
name|iterator
operator|.
name|currentAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

