begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2010 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|DocumentSet
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
name|parser
operator|.
name|XQueryAST
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
name|util
operator|.
name|ExpressionDumper
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
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
import|;
end_import

begin_comment
comment|/**  * Base interface implemented by all classes which are part  * of an XQuery/XPath expression. The main method is   * {@link #eval(Sequence, Item)}. Please  * read the description there.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Expression
block|{
comment|// Flags to be passed to analyze:
comment|/**      * Indicates that the query engine will call the expression once for every      * item in the context sequence. This is what you would expect to be the      * normal behaviour of an XQuery processor. However, eXist tries to process      * some types of expressions in one single step for the whole input sequence.      * So if the flag is not set, the expression is only called once.      */
specifier|public
specifier|final
specifier|static
name|int
name|SINGLE_STEP_EXECUTION
init|=
literal|1
decl_stmt|;
comment|/**      * Indicates that the expression is within a predicate or the where clause of      * a FLWOR.      */
specifier|public
specifier|final
specifier|static
name|int
name|IN_PREDICATE
init|=
literal|2
decl_stmt|;
comment|/**      * Indicates that the expression is within a where clause of a FLWOR. This      * flag will be set in addition to {@link #IN_PREDICATE}.      */
specifier|public
specifier|final
specifier|static
name|int
name|IN_WHERE_CLAUSE
init|=
literal|4
decl_stmt|;
comment|/**      * Indicates that the expression is used within an update statement. Subexpressions      * should not cache any relevant data as it may be subject to change.      */
specifier|public
specifier|final
specifier|static
name|int
name|IN_UPDATE
init|=
literal|8
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NEED_INDEX_INFO
init|=
literal|16
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|USE_TREE_TRAVERSAL
init|=
literal|32
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|POSITIONAL_PREDICATE
init|=
literal|64
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DOT_TEST
init|=
literal|128
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IN_NODE_CONSTRUCTOR
init|=
literal|256
decl_stmt|;
comment|/**      * Indicates that the expression will redirect subexpressions evaluation       * result to output stream after some manipulations.      */
specifier|public
specifier|final
specifier|static
name|int
name|NON_STREAMABLE
init|=
literal|512
decl_stmt|;
comment|/**      * Indicates that sequence .      */
specifier|public
specifier|final
specifier|static
name|int
name|UNORDERED
init|=
literal|1024
decl_stmt|;
comment|/**      * Indicates that no context id is supplied to an expression.      */
specifier|public
specifier|final
specifier|static
name|int
name|NO_CONTEXT_ID
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IGNORE_CONTEXT
init|=
operator|-
literal|2
decl_stmt|;
comment|/**      * Marks an invalid expression id.      */
specifier|public
specifier|final
specifier|static
name|int
name|EXPRESSION_ID_INVALID
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Returns an id which uniquely identifies this expression      * within the compiled expression tree of the query.      *       * @return unique id or {@link #EXPRESSION_ID_INVALID}      */
specifier|public
name|int
name|getExpressionId
parameter_list|()
function_decl|;
comment|/**      * Statically analyze the expression and its subexpressions.      *       * During the static analysis phase, the query engine can detect      * unknown variables and some type errors.      *       * @throws XPathException      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Evaluate the expression represented by this object.      *      * Depending on the context in which this expression is executed,      * either the context sequence, the context item or both of them may      * be set. An implementing class should know how to handle this.      *      * The general contract is as follows: if the {@link Dependency#CONTEXT_ITEM}      * bit is set in the bit field returned by {@link #getDependencies()}, the eval method will      * be called once for every item in the context sequence. The<b>contextItem</b>      * parameter will be set to the current item. Otherwise, the eval method will only be called      * once for the whole context sequence and<b>contextItem</b> will be null.      *      * eXist tries to process the entire context set in one, single step whenever      * possible. Thus, most classes only expect context to contain a list of      * nodes which represents the current context of the expression.      *      * The position() function in XPath is an example for an expression,      * which requires both, context sequence and context item to be set.      *      * The context sequence might be a node set, a sequence of atomic values or a single      * node or atomic value.      *      * @param contextSequence the current context sequence.      * @param contextItem a single item, taken from context. This defines the item,      * the expression should work on.      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Evaluate the expression represented by this object.      *      * An overloaded method which just passes the context sequence depending on the      * expression context.      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
function_decl|;
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
function_decl|;
comment|/**      * The static return type of the expression.      *      * This method should return one of the type constants defined in class      * {@link org.exist.xquery.value.Type}. If the return type cannot be determined      * statically, return Type.ITEM.      */
specifier|public
name|int
name|returnsType
parameter_list|()
function_decl|;
comment|/**      * The expected cardinality of the return value of the expression.      *      * Should return a bit mask with bits set as defined in class {@link Cardinality}.      */
specifier|public
name|int
name|getCardinality
parameter_list|()
function_decl|;
comment|/**      * Returns a set of bit-flags, indicating some of the parameters      * on which this expression depends. The flags are defined in      * {@link Dependency}.      *      * @return set of bit-flags      */
specifier|public
name|int
name|getDependencies
parameter_list|()
function_decl|;
specifier|public
name|Expression
name|simplify
parameter_list|()
function_decl|;
comment|/**      * Called to inform an expression that it should reset to its initial state.      *      * All cached data in the expression object should be dropped. For example,      * the xmldb:document() function calls this method whenever the input document      * set has changed.      * @param postOptimization      */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
function_decl|;
comment|/**      * Start traversing the expression tree using the specified {@link ExpressionVisitor}.      * @param visitor      */
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
function_decl|;
comment|/**      * Write a diagnostic dump of the expression to the passed      * {@link ExpressionDumper}.      *      * @param dumper the expression dumper to write to      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
function_decl|;
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
function_decl|;
specifier|public
name|int
name|getContextId
parameter_list|()
function_decl|;
specifier|public
name|DocumentSet
name|getContextDocSet
parameter_list|()
function_decl|;
specifier|public
name|void
name|setASTNode
parameter_list|(
name|XQueryAST
name|ast
parameter_list|)
function_decl|;
specifier|public
name|void
name|setLocation
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
function_decl|;
specifier|public
name|int
name|getLine
parameter_list|()
function_decl|;
specifier|public
name|int
name|getColumn
parameter_list|()
function_decl|;
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
function_decl|;
specifier|public
name|XACMLSource
name|getSource
parameter_list|()
function_decl|;
comment|//Expression is the part of tree, next methods allow to walk down the tree
specifier|public
name|int
name|getSubExpressionCount
parameter_list|()
function_decl|;
specifier|public
name|Expression
name|getSubExpression
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|//check will item process by the expression
specifier|public
name|Boolean
name|match
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|boolean
name|allowMixNodesInReturn
parameter_list|()
function_decl|;
specifier|public
name|Expression
name|getParent
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

