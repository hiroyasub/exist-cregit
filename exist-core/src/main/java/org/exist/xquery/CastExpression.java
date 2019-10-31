begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|QName
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * CastExpression represents cast expressions as well as all type   * constructors.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CastExpression
extends|extends
name|AbstractExpression
block|{
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|private
name|int
name|cardinality
init|=
name|Cardinality
operator|.
name|EXACTLY_ONE
decl_stmt|;
specifier|private
specifier|final
name|int
name|requiredType
decl_stmt|;
comment|/** 	 * Constructor. When calling {@link #eval(Sequence, Item)}  	 * the passed expression will be cast into the required type and cardinality. 	 *  	 * @param context current context      * @param expr expression to cast      * @param requiredType the {@link Type} expected      * @param cardinality the {@link Cardinality} expected 	 */
specifier|public
name|CastExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expr
parameter_list|,
name|int
name|requiredType
parameter_list|,
name|int
name|cardinality
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredType
operator|=
name|requiredType
expr_stmt|;
name|this
operator|.
name|cardinality
operator|=
name|cardinality
expr_stmt|;
name|setExpression
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Expression
name|getInnerExpression
parameter_list|()
block|{
return|return
name|expression
return|;
block|}
specifier|public
name|void
name|setExpression
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|expression
operator|=
name|expr
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|setStaticReturnType
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
block|{
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Should be handled by the parser
if|if
condition|(
name|requiredType
operator|==
name|Type
operator|.
name|ATOMIC
operator|||
operator|(
name|requiredType
operator|==
name|Type
operator|.
name|NOTATION
operator|&&
name|expression
operator|.
name|returnsType
argument_list|()
operator|!=
name|Type
operator|.
name|NOTATION
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0080
argument_list|,
literal|"cannot cast to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|requiredType
operator|==
name|Type
operator|.
name|ANY_SIMPLE_TYPE
operator|||
name|expression
operator|.
name|returnsType
argument_list|()
operator|==
name|Type
operator|.
name|ANY_SIMPLE_TYPE
operator|||
name|requiredType
operator|==
name|Type
operator|.
name|UNTYPED
operator|||
name|expression
operator|.
name|returnsType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0051
argument_list|,
literal|"cannot cast to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
name|Sequence
name|result
decl_stmt|;
specifier|final
name|Sequence
name|seq
init|=
name|Atomize
operator|.
name|atomize
argument_list|(
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|cardinality
operator|&
name|Cardinality
operator|.
name|ZERO
operator|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Type error: empty sequence is not allowed here"
argument_list|)
throw|;
block|}
else|else
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|Item
name|item
init|=
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|hasMany
argument_list|()
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"cardinality error: sequence with more than one item is not allowed here"
argument_list|)
throw|;
block|}
try|try
block|{
comment|// casting to QName needs special treatment
if|if
condition|(
name|requiredType
operator|==
name|Type
operator|.
name|QNAME
condition|)
block|{
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|QNAME
condition|)
block|{
name|result
operator|=
name|item
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATOMIC
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Cannot cast "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to xs:QName"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setLocation
argument_list|(
name|e
operator|.
name|getLine
argument_list|()
argument_list|,
name|e
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|expression
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" cast as "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|expression
operator|.
name|toString
argument_list|()
operator|+
literal|" cast as "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|requiredType
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getDependencies
argument_list|()
operator||
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|ZERO_OR_ONE
return|;
block|}
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|super
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
name|expression
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|expression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitCastExpr
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Function
name|toFunction
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|typeName
init|=
name|Type
operator|.
name|getTypeName
argument_list|(
name|CastExpression
operator|.
name|this
operator|.
name|requiredType
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|QName
name|qname
init|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|typeName
argument_list|)
decl_stmt|;
specifier|final
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qname
argument_list|)
decl_stmt|;
specifier|final
name|SequenceType
name|argType
init|=
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
decl_stmt|;
name|signature
operator|.
name|setArgumentTypes
argument_list|(
operator|new
name|SequenceType
index|[]
block|{
name|argType
block|}
argument_list|)
expr_stmt|;
name|signature
operator|.
name|setReturnType
argument_list|(
operator|new
name|SequenceType
argument_list|(
name|CastExpression
operator|.
name|this
operator|.
name|requiredType
argument_list|,
name|CastExpression
operator|.
name|this
operator|.
name|cardinality
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|FunctionWrapper
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|typeName
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|FunctionWrapper
extends|extends
name|Function
block|{
specifier|protected
name|FunctionWrapper
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
operator|new
name|Function
operator|.
name|Placeholder
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|setArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|CastExpression
operator|.
name|this
operator|.
name|setExpression
argument_list|(
name|arguments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
return|return
name|CastExpression
operator|.
name|this
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

