begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|*
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
name|value
operator|.
name|Item
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * Base interface implemented by all classes which are part  * of an XQuery/XPath expression. The main method is   * {@link #eval(StaticContext, DocumentSet, Sequence, Item)}. Please  * read the description there.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Expression
block|{
comment|/** 	 * Evaluate the expression represented by this object. 	 * 	 * Depending on the context in which this expression is executed, 	 * either the context sequence, the context item or both of them may  	 * be set. An implementing class should know how to handle this. 	 *  	 * The general contract is as follows: if the {@link Dependency#CONTEXT_ITEM} 	 * bit is set in the bit field returned by {@link #getDependencies()}, the eval method will 	 * be called once for every item in the context sequence. The<b>contextItem</b> 	 * parameter will be set to the current item. Otherwise, the eval method will only be called 	 * once for the whole context sequence and<b>contextItem</b> will be null. 	 *  	 * eXist tries to process the entire context set in one, single step whenever 	 * possible. Thus, most classes only expect context to contain a list of  	 * nodes which represents the current context of the expression.  	 *  	 * The position() function in XPath is an example for an expression, 	 * which requires both, context sequence and context item to be set. 	 * 	 * The context sequence might be a node set, a sequence of atomic values or a single 	 * node or atomic value.  	 *  	 * @param docs the set of documents all nodes belong to. 	 * @param contextSequence the current context sequence. 	 * @param contextItem a single item, taken from context. This defines the item, 	 * the expression should work on. 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Evaluate the expression represented by this object. 	 * 	 * An overloaded method which just passes the context sequence depending on the 	 * expression context. 	 * 	 * @param docs the set of documents all nodes belong to. 	 * @param contextSet the node-set which defines the current context node-set. 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * The static return type of the expression. 	 * 	 * This method should return one of the type constants defined in class  	 * {@link org.exist.xpath.value.Type}. If the return type cannot be determined 	 * statically, return Type.ITEM. 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
function_decl|;
comment|/** 	 * The expected cardinality of the return value of the expression. 	 *  	 * Should return a bit mask with bits set as defined in class {@link Cardinality}. 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
function_decl|;
comment|/** 	 * Returns a set of bit-flags, indicating some of the parameters 	 * on which this expression depends. The flags are defined in 	 * {@link Dependency}. 	 *  	 * @return 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
function_decl|;
comment|/** 	 * Called to inform an expression that it should reset to its initial state.  	 *  	 * All cached data in the expression object should be dropped. For example, 	 * the document() function calls this method whenever the input document 	 * set has changed. 	 */
specifier|public
name|void
name|resetState
parameter_list|()
function_decl|;
comment|/** 	 * This method is called to inform the expression object that 	 * it is executed inside an XPath predicate (or in a where clause). 	 *  	 * @param inPredicate 	 */
specifier|public
name|void
name|setInPredicate
parameter_list|(
name|boolean
name|inPredicate
parameter_list|)
function_decl|;
comment|/** 	 * Return a readable representation of this expression. 	 * 	 * This method is called whenever the xpath-query should be 	 * displayed to the user. 	 */
specifier|public
name|String
name|pprint
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

