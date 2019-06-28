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
name|persistent
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
name|dom
operator|.
name|persistent
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
comment|/**  * This interface represents a sequence as defined in the XPath 2.0 specification.  *  * A sequence is a sequence of items. Each item is either an atomic value or a  * node. A single item is also a sequence, containing only the item. The base classes for  * {@link org.exist.xquery.value.AtomicValue atomic values} and {@link org.exist.dom.persistent.NodeProxy  * nodes} thus implement the Sequence interface.  *  * Also, a {@link org.exist.dom.persistent.NodeSet node set} is a special type of sequence, where all  * items are of type node.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Sequence
block|{
comment|/**      * Constant representing an empty sequence, i.e. a sequence with no item.      */
name|Sequence
name|EMPTY_SEQUENCE
init|=
operator|new
name|EmptySequence
argument_list|()
decl_stmt|;
comment|/**      * The purpose of ordered and unordered flag is to set the ordering mode      * in the static context to ordered or unordered for a certain region in a query.      *      * @param flag      */
comment|//	public void keepUnOrdered(boolean flag);
comment|/**      * Add an item to the current sequence. An {@link XPathException} may be thrown      * if the item's type is incompatible with this type of sequence (e.g. if the sequence      * is a node set).      *      * The sequence may or may not allow duplicate values.      *      * @param item the item to add      * @throws XPathException if an error occurs      */
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Add all items of the other sequence to this item. An {@link XPathException} may      * be thrown if the type of the items in the other sequence is incompatible with      * the primary type of this sequence.      *      * @param other the other sequence      *      * @throws XPathException if an error occurs      */
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Return the primary type to which all items in this sequence belong. This is      * {@link org.exist.xquery.value.Type#NODE} for node sets, {@link Type#ITEM}      * for other sequences with mixed items.      *      * @return the primary type of the items in this sequence.      */
name|int
name|getItemType
parameter_list|()
function_decl|;
comment|/**      * Returns an iterator over all items in the sequence. The      * items are returned in document order where applicable.      *      * @return the iterator      *      * @throws XPathException if an error occurs      */
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Returns an iterator over all items in the sequence. The returned      * items may - but need not - to be in document order.      *      * @return the iterator      *      * @throws XPathException if an error occurs      */
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Returns the number of items contained in the sequence.      *      * NOTE: this is just a legacy convenience      *     for {@link #getItemCountLong()}.      *      * If the sequence has more items than {@link Integer#MAX_VALUE}      * then this function will likely return a negative value,      * at which point you should consider instead      * using {@link #getItemCountLong()}.      *      * @return The number of items in the sequence.      */
specifier|default
name|int
name|getItemCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|getItemCountLong
argument_list|()
return|;
block|}
comment|/**      * Returns the number of items contained in the sequence.      *      * Thought should be given before calling this method,      * as for some sequence types this can be a      *<strong>very</strong> expensive operation, whereas      * for others it may be almost free!      *      * @return The number of items in the sequence.      */
name|long
name|getItemCountLong
parameter_list|()
function_decl|;
comment|/**      * Returns whether the sequence is empty or not.      *      * @return<code>true</code> is the sequence is empty      */
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * Returns whether the sequence has just one item or not.      *      * @return<code>true</code> is the sequence has just one item      */
name|boolean
name|hasOne
parameter_list|()
function_decl|;
comment|/**      * Returns whether the sequence more than one item or not.      *      * @return<code>true</code> is the sequence more than one item      */
name|boolean
name|hasMany
parameter_list|()
function_decl|;
comment|/**      * Explicitly remove all duplicate nodes from this sequence.      */
name|void
name|removeDuplicates
parameter_list|()
function_decl|;
comment|/**      * Returns the cardinality of this sequence. The returned      * value is a combination of flags as defined in      * {@link org.exist.xquery.Cardinality}.      *      * @return the cardinality      *      * @see org.exist.xquery.Cardinality      */
name|int
name|getCardinality
parameter_list|()
function_decl|;
comment|/**      * Returns the item located at the specified position within      * this sequence. Items are counted beginning at 0.      *      * @param pos the position      * @return the item at the position      */
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
name|Sequence
name|tail
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Try to convert the sequence into an atomic value. The target type should be specified by      * using one of the constants defined in class {@link Type}. An {@link XPathException}      * is thrown if the conversion is impossible.      *      * @param requiredType one of the type constants defined in class {@link Type}      *      * @return the converted value or null      *      * @throws XPathException if an error occurs      */
annotation|@
name|Nullable
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Convert the sequence to a string.      *      * @return the string value      */
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Get the effective boolean value of this sequence. Will be false if the sequence is empty,      * true otherwise.      *      * @return the effective boolean value      *      * @throws XPathException if an error occurs      */
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Convert the sequence into a NodeSet. If the sequence contains items      * which are not nodes, an XPathException is thrown.      *      * @return the node set      *      * @throws XPathException if the sequence contains items which are not nodes.      */
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Convert the sequence into an in-memory node set. If the sequence contains      * items which are not nodes, an XPathException is thrown. For persistent      * node sets, this method will return null. Call {@link #isPersistentSet()} to check      * if the sequence is a persistent node set.      *      * @return the in memory node set      *      * @throws XPathException if the sequence contains items which are not nodes or is      *                        a persistent node set      */
name|MemoryNodeSet
name|toMemNodeSet
parameter_list|()
throws|throws
name|XPathException
function_decl|;
comment|/**      * Returns the set of documents from which the node items in this sequence      * have been selected. This is for internal use only.      *      * @return the document set      */
name|DocumentSet
name|getDocumentSet
parameter_list|()
function_decl|;
comment|/**      * Return an iterator on all collections referenced by documents      * contained in this sequence..      *      * @return the iterator      */
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|getCollectionIterator
parameter_list|()
function_decl|;
comment|/**      * Returns a preference indicator, indicating the preference of      * a value to be converted into the given Java class. Low numbers mean      * that the value can be easily converted into the given class.      *      * @param javaClass the java class      *      * @return the preference      */
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
comment|/**      * Convert the value into an instance of the specified      * Java class.      *      * @param target the target class      *      * @return the Java object.      *      * @throws XPathException if an error occurs      */
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
comment|/**      * Returns true if the sequence is the result of a previous operation      * and has been cached.      *      * @return true if the sequence has been cached      */
name|boolean
name|isCached
parameter_list|()
function_decl|;
comment|/**      * Indicates that the sequence is the result of a previous operation      * and has not been recomputed.      *      * @param cached true if the sequence should be cached      */
name|void
name|setIsCached
parameter_list|(
name|boolean
name|cached
parameter_list|)
function_decl|;
comment|/**      * For every item in the sequence, clear any context-dependant      * information that is stored during query processing. This      * feature is used for node sets, which may store information      * about their context node.      *      * @param contextId the context id      *      * @throws XPathException if an error occurs whilst clearing the context      */
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
function_decl|;
name|void
name|setSelfAsContext
parameter_list|(
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
function_decl|;
name|boolean
name|isPersistentSet
parameter_list|()
function_decl|;
comment|/**      * Node sets may implement this method to be informed of storage address      * and node id changes after updates.      *      * @param oldNodeId the old node id      * @param newNode the new node      */
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
comment|//TODO why is this here, it only pertains to Persistent nodes and NOT also in-memory nodes
name|boolean
name|isCacheable
parameter_list|()
function_decl|;
name|int
name|getState
parameter_list|()
function_decl|;
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
function_decl|;
comment|/**      * Clean up any resources used by the items in this sequence.      *      * @param context the XQuery context      * @param contextSequence the context sequence      */
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
block|}
end_interface

end_unit

