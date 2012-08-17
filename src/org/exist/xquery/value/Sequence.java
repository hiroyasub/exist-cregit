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
name|DocumentSet
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
name|NodeSet
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
name|StoredNode
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
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/** 	 * The purpose of ordered and unordered flag is to set the ordering mode  	 * in the static context to ordered or unordered for a certain region in a query.  	 *  	 * @param flag 	 */
comment|//	public void keepUnOrdered(boolean flag);
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
comment|/** 	 * Returns an iterator over all items in the sequence. The 	 * items are returned in document order where applicable. 	 *  	 * @throws XPathException TODO 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns an iterator over all items in the sequence. The returned 	 * items may - but need not - to be in document order. 	 *  	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns the number of items contained in the sequence.      * Call this method<strong>only</strong> when necessary,       * since it can be resource consuming.  	 *  	 * @return The number of items in the sequence 	 */
specifier|public
name|int
name|getItemCount
parameter_list|()
function_decl|;
comment|/** 	 * Returns whether the sequence is empty or not. 	 *  	 * @return<code>true</code> is the sequence is empty 	 */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * Returns whether the sequence has just one item or not.      *       * @return<code>true</code> is the sequence has just one item      */
specifier|public
name|boolean
name|hasOne
parameter_list|()
function_decl|;
comment|/**      * Returns whether the sequence more than one item or not.      *       * @return<code>true</code> is the sequence more than one item      */
specifier|public
name|boolean
name|hasMany
parameter_list|()
function_decl|;
comment|/** 	 * Explicitely remove all duplicate nodes from this sequence. 	 */
specifier|public
name|void
name|removeDuplicates
parameter_list|()
function_decl|;
comment|/** 	 * Returns the cardinality of this sequence. The returned 	 * value is a combination of flags as defined in 	 * {@link org.exist.xquery.Cardinality}. 	 *  	 * @see org.exist.xquery.Cardinality 	 *  	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
function_decl|;
comment|/** 	 * Returns the item located at the specified position within 	 * this sequence. Items are counted beginning at 0. 	 *  	 * @param pos 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
specifier|public
name|Sequence
name|tail
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Try to convert the sequence into an atomic value. The target type should be specified by 	 * using one of the constants defined in class {@link Type}. An {@link XPathException} 	 * is thrown if the conversion is impossible. 	 *  	 * @param requiredType one of the type constants defined in class {@link Type} 	 * @throws XPathException 	 */
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
comment|/** 	 * Convert the sequence to a string. 	 *  	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Get the effective boolean value of this sequence. Will be false if the sequence is empty, 	 * true otherwise. 	 *  	 * @throws XPathException 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Convert the sequence into a NodeSet. If the sequence contains items 	 * which are not nodes, an XPathException is thrown. 	 * @throws XPathException if the sequence contains items which are not nodes. 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Convert the sequence into an in-memory node set. If the sequence contains      * items which are not nodes, an XPathException is thrown. For persistent      * node sets, this method will return null. Call {@link #isPersistentSet()} to check      * if the sequence is a persistent node set.       *      * @throws XPathException if the sequence contains items which are not nodes or is      * a persistent node set      */
specifier|public
name|MemoryNodeSet
name|toMemNodeSet
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns the set of documents from which the node items in this sequence 	 * have been selected. This is for internal use only. 	 *  	 */
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
function_decl|;
comment|/**      * Return an iterator on all collections referenced by documents      * contained in this sequence..      */
specifier|public
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|getCollectionIterator
parameter_list|()
function_decl|;
comment|/** 	 * Returns a preference indicator, indicating the preference of 	 * a value to be converted into the given Java class. Low numbers mean 	 * that the value can be easily converted into the given class. 	 *  	 * @param javaClass 	 */
specifier|public
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
comment|/** 	 * Convert the value into an instance of the specified 	 * Java class. 	 *  	 * @param target 	 * @throws XPathException 	 */
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|target
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Returns true if the sequence is the result of a previous operation 	 * and has been cached. 	 *  	 */
specifier|public
name|boolean
name|isCached
parameter_list|()
function_decl|;
comment|/** 	 * Indicates that the sequence is the result of a previous operation 	 * and has not been recomputed. 	 *   	 * @param cached 	 */
specifier|public
name|void
name|setIsCached
parameter_list|(
name|boolean
name|cached
parameter_list|)
function_decl|;
comment|/** 	 * For every item in the sequence, clear any context-dependant 	 * information that is stored during query processing. This 	 * feature is used for node sets, which may store information 	 * about their context node. 	 */
specifier|public
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|setSelfAsContext
parameter_list|(
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
function_decl|;
comment|/**      * Node sets may implement this method to be informed of storage address      * and node id changes after updates.      *      * @see org.exist.storage.UpdateListener      *       * @param oldNodeId      * @param newNode      */
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|StoredNode
name|newNode
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isCacheable
parameter_list|()
function_decl|;
specifier|public
name|int
name|getState
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
function_decl|;
comment|/**      * Clean up any resources used by the items in this sequence.      */
name|void
name|destroy
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

