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
name|NodeSet
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

begin_comment
comment|/**  * This interface represents a sequence as defined in the XPath 2.0 specification.  *   * A sequence is a sequence of items. Each item is either an atomic value or a  * node. A single item is also a sequence, containing only the item. The base classes for   * {@link org.exist.xquery.value.AtomicValue atomic values} and {@link org.exist.dom.NodeProxy  * nodes} thus implement the Sequence interface.  *   * Also, a {@link org.exist.dom.NodeSet node set} is a special type of sequence, where all   * items are of type node.    */
end_comment

begin_interface
specifier|public
interface|interface
name|Sequence
block|{
comment|/** 	 * Constant representing an empty sequence, i.e. a sequence with no item. 	 */
specifier|public
specifier|final
specifier|static
name|Sequence
name|EMPTY_SEQUENCE
init|=
operator|new
name|EmptySequence
argument_list|()
decl_stmt|;
comment|/** 	 * Add an item to the current sequence. An {@link XPathException} may be thrown 	 * if the item's type is incompatible with this type of sequence (e.g. if the sequence 	 * is a node set). 	 *  	 * The sequence may or may not allow duplicate values. 	 *  	 * @param item 	 * @throws XPathException 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Add all items of the other sequence to this item. An {@link XPathException} may 	 * be thrown if the type of the items in the other sequence is incompatible with 	 * the primary type of this sequence. 	 *  	 * @param other 	 * @throws XPathException 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Return the primary type to which all items in this sequence belong. This is 	 * {@link org.exist.xquery.value.Type#NODE} for node sets, {@link Type#ITEM} 	 * for other sequences with mixed items. 	 *  	 * @return the primary type of the items in this sequence. 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
function_decl|;
comment|/** 	 * Returns an iterator over all items in the sequence. The 	 * items are returned in document order where applicable. 	 *  	 * @return 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
function_decl|;
comment|/** 	 * Returns an iterator over all items in the sequence. The returned 	 * items may - but need not - to be in document order. 	 *  	 * @return 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
function_decl|;
comment|/** 	 * Returns the number of items contained in the sequence. 	 * @return 	 */
specifier|public
name|int
name|getLength
parameter_list|()
function_decl|;
specifier|public
name|int
name|getCardinality
parameter_list|()
function_decl|;
comment|/** 	 * Returns the item located at the specified position within 	 * this sequence. Items are counted beginning at 0. 	 *  	 * @param pos 	 * @return 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/** 	 * Try to convert the sequence into an atomic value. The target type should be specified by 	 * using one of the constants defined in class {@link Type}. An {@link XPathException} 	 * is thrown if the conversion is impossible. 	 *  	 * @param requiredType one of the type constants defined in class {@link Type} 	 * @return 	 * @throws XPathException 	 */
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
comment|/** 	 * Convert the sequence to a string. 	 *  	 * @return 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Get the effective boolean value of this sequence. Will be false if the sequence is empty, 	 * true otherwise. 	 *  	 * @return 	 * @throws XPathException 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Convert the sequence into a NodeSet. If the sequence contains items 	 * which are not nodes, an XPathException is thrown. 	 * @return 	 * @throws XPathException if the sequence contains items which are not nodes. 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns a preference indicator, indicating the preference of 	 * a value to be converted into the given Java class. Low numbers mean 	 * that the value can be easily converted into the given class. 	 *  	 * @param javaClass 	 * @return 	 */
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
name|javaClass
parameter_list|)
function_decl|;
comment|/** 	 * Convert the value into an instance of the specified 	 * Java class. 	 *  	 * @param target 	 * @return 	 * @throws XPathException 	 */
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
name|target
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|setSelfAsContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

