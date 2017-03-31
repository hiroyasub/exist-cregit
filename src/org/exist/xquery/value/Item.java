begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|numbering
operator|.
name|NodeId
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
name|xquery
operator|.
name|XPathException
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * This class represents an item in a sequence as defined by the XPath 2.0 specification.  * Every item is either an {@link org.exist.xquery.value.AtomicValue atomic value} or  * a {@link org.exist.dom.persistent.NodeProxy node}.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Item
block|{
comment|/** 	 * Return the type of this item according to the type constants defined in class 	 * {@link Type}. 	 *  	 */
name|int
name|getType
parameter_list|()
function_decl|;
comment|/** 	 * Return the string value of this item (see the definition of string value in XPath). 	 *  	 */
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Convert this item into a sequence, containing only the item. 	 *   	 */
name|Sequence
name|toSequence
parameter_list|()
function_decl|;
comment|/**      * Clean up any resources used by the items in this sequence.      */
name|void
name|destroy
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
function_decl|;
comment|/** 	 * Convert this item into an atomic value, whose type corresponds to 	 * the specified target type. requiredType should be one of the type 	 * constants defined in {@link Type}. An {@link XPathException} is thrown 	 * if the conversion is impossible. 	 *  	 * @param requiredType 	 * @throws XPathException 	 */
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
function_decl|;
name|void
name|toSAX
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|SAXException
function_decl|;
name|void
name|copyTo
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentBuilderReceiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
function_decl|;
name|int
name|conversionPreference
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|javaClass
parameter_list|)
function_decl|;
parameter_list|<
name|T
parameter_list|>
name|T
name|toJavaObject
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|target
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**          * Nodes may implement this method to be informed of storage address          * and node id changes after updates.          *          * @see org.exist.storage.UpdateListener          *          * @param oldNodeId          * @param newNode          */
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|NodeHandle
name|newNode
parameter_list|)
function_decl|;
comment|//TODO why is this here, it only pertains to Peristent nodes and NOT also in-memory nodes
block|}
end_interface

end_unit

