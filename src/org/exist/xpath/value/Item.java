begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|XPathException
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

begin_comment
comment|/**  * This class represents an item in a sequence as defined by the XPath 2.0 specification.  * Every item is either an {@link org.exist.xpath.value.AtomicValue atomic value} or  * a {@link org.exist.dom.NodeProxy node}.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Item
block|{
comment|/** 	 * Return the type of this item according to the type constants defined in class 	 * {@link Type}. 	 *  	 * @return 	 */
specifier|public
name|int
name|getType
parameter_list|()
function_decl|;
comment|/** 	 * Return the string value of this item (see the definition of string value in XPath). 	 *  	 * @return 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Convert this item into a sequence, containing only the item. 	 *   	 * @return 	 */
specifier|public
name|Sequence
name|toSequence
parameter_list|()
function_decl|;
comment|/** 	 * Convert this item into an atomic value, whose type corresponds to 	 * the specified target type. requiredType should be one of the type 	 * constants defined in {@link Type}. An {@link XPathException} is thrown 	 * if the conversion is impossible. 	 *  	 * @param requiredType 	 * @return 	 * @throws XPathException 	 */
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|toSAX
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
function_decl|;
block|}
end_interface

end_unit

