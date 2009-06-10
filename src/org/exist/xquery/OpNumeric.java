begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|ComputableValue
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
name|NumericValue
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
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * numeric operation on two operands by +, -, *, div, mod etc..  *  */
end_comment

begin_class
specifier|public
class|class
name|OpNumeric
extends|extends
name|BinaryOp
block|{
specifier|protected
specifier|final
name|int
name|operator
decl_stmt|;
specifier|protected
name|int
name|returnType
init|=
name|Type
operator|.
name|ATOMIC
decl_stmt|;
specifier|protected
name|NodeSet
name|temp
init|=
literal|null
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|OpNumeric
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|operator
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
block|}
specifier|public
name|OpNumeric
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|int
name|operator
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|left
operator|=
name|atomizeIfNecessary
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|right
operator|=
name|atomizeIfNecessary
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|int
name|ltype
init|=
name|left
operator|.
name|returnsType
argument_list|()
decl_stmt|;
name|int
name|rtype
init|=
name|right
operator|.
name|returnsType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
name|ltype
operator|>
name|rtype
condition|)
block|{
name|right
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|ltype
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|left
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|rtype
argument_list|,
name|left
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operator
operator|==
name|Constants
operator|.
name|DIV
operator|&&
name|ltype
operator|==
name|Type
operator|.
name|INTEGER
operator|&&
name|rtype
operator|==
name|Type
operator|.
name|INTEGER
condition|)
block|{
name|returnType
operator|=
name|Type
operator|.
name|DECIMAL
expr_stmt|;
block|}
if|else if
condition|(
name|operator
operator|==
name|Constants
operator|.
name|IDIV
condition|)
block|{
name|returnType
operator|=
name|Type
operator|.
name|INTEGER
expr_stmt|;
block|}
else|else
block|{
name|returnType
operator|=
name|Math
operator|.
name|max
argument_list|(
name|ltype
argument_list|,
name|rtype
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
name|ltype
operator|=
name|Type
operator|.
name|NUMBER
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
name|rtype
operator|=
name|Type
operator|.
name|NUMBER
expr_stmt|;
name|OpEntry
name|entry
init|=
operator|(
name|OpEntry
operator|)
name|OP_TYPES
operator|.
name|get
argument_list|(
operator|new
name|OpEntry
argument_list|(
name|operator
argument_list|,
name|ltype
argument_list|,
name|rtype
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
name|returnType
operator|=
name|entry
operator|.
name|typeResult
expr_stmt|;
block|}
name|add
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Expression
name|atomizeIfNecessary
parameter_list|(
name|Expression
name|x
parameter_list|)
block|{
return|return
name|Type
operator|.
name|subTypeOf
argument_list|(
name|x
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|?
name|x
else|:
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|x
argument_list|)
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|returnType
return|;
block|}
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
name|super
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
name|returnType
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
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
name|Sequence
name|lseq
init|=
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|rseq
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|lseq
operator|.
name|hasMany
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: too many operands at the left of "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
throw|;
if|if
condition|(
name|rseq
operator|.
name|hasMany
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: too many operands at the right of "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
throw|;
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|rseq
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
if|else if
condition|(
name|lseq
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|Item
name|lvalue
init|=
name|lseq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Item
name|rvalue
init|=
name|rseq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|lvalue
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
operator|||
name|lvalue
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATOMIC
condition|)
name|lvalue
operator|=
name|lvalue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
expr_stmt|;
if|if
condition|(
name|rvalue
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
operator|||
name|rvalue
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATOMIC
condition|)
name|rvalue
operator|=
name|rvalue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|lvalue
operator|instanceof
name|ComputableValue
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|lvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|lvalue
operator|+
literal|")' can not be an operand for "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
throw|;
if|if
condition|(
operator|!
operator|(
name|rvalue
operator|instanceof
name|ComputableValue
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|rvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|rvalue
operator|+
literal|")' can not be an operand for "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
throw|;
comment|//TODO : move to implementations
if|if
condition|(
name|operator
operator|==
name|Constants
operator|.
name|IDIV
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|lvalue
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|lvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|lvalue
operator|+
literal|")' can not be an operand for "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
throw|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rvalue
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|rvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|rvalue
operator|+
literal|")' can not be an operand for "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
throw|;
comment|//If the divisor is (positive or negative) zero, then an error is raised [err:FOAR0001]
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|rvalue
operator|)
operator|.
name|isZero
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FOAR0001: division by zero"
argument_list|)
throw|;
comment|//If either operand is NaN then an error is raised [err:FOAR0002].
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|lvalue
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FOAR0002: division of "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|lvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|lvalue
operator|+
literal|")'"
argument_list|)
throw|;
comment|//If either operand is NaN then an error is raised [err:FOAR0002].
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|rvalue
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FOAR0002: division of "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|rvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|rvalue
operator|+
literal|")'"
argument_list|)
throw|;
comment|//If $arg1 is INF or -INF then an error is raised [err:FOAR0002].
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|lvalue
operator|)
operator|.
name|isInfinite
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FOAR0002: division of "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|lvalue
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|lvalue
operator|+
literal|")'"
argument_list|)
throw|;
name|result
operator|=
operator|(
operator|(
name|NumericValue
operator|)
name|lvalue
operator|)
operator|.
name|idiv
argument_list|(
operator|(
name|NumericValue
operator|)
name|rvalue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|applyOperator
argument_list|(
operator|(
name|ComputableValue
operator|)
name|lvalue
argument_list|,
operator|(
name|ComputableValue
operator|)
name|rvalue
argument_list|)
expr_stmt|;
block|}
comment|//TODO : type-checks on MOD operator : maybe the same ones than above -pb
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setLocation
argument_list|(
name|line
argument_list|,
name|column
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
comment|//Sets the return type if not already set
if|if
condition|(
name|returnType
operator|==
name|Type
operator|.
name|ATOMIC
condition|)
comment|//TODO : refine previously set type ? -pb
name|returnType
operator|=
name|result
operator|.
name|getItemType
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|ComputableValue
name|applyOperator
parameter_list|(
name|ComputableValue
name|left
parameter_list|,
name|ComputableValue
name|right
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|Constants
operator|.
name|MINUS
case|:
return|return
name|left
operator|.
name|minus
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|PLUS
case|:
return|return
name|left
operator|.
name|plus
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|MULT
case|:
return|return
name|left
operator|.
name|mult
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|DIV
case|:
return|return
name|left
operator|.
name|div
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|MOD
case|:
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|left
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|left
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|left
operator|+
literal|")' is not numeric"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|right
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|right
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|right
operator|+
literal|")' is not numeric"
argument_list|)
throw|;
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|left
operator|)
operator|.
name|mod
argument_list|(
operator|(
name|NumericValue
operator|)
name|right
argument_list|)
return|;
block|}
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown numeric operator "
operator|+
name|operator
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|getLeft
argument_list|()
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
literal|' '
argument_list|)
operator|.
name|display
argument_list|(
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
operator|.
name|display
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|getRight
argument_list|()
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getLeft
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// excerpt from operator mapping table in XQuery 1.0 section B.2
comment|// http://www.w3.org/TR/xquery/#mapping
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|OP_TABLE
init|=
block|{
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DATE
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DATE
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DATE
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DATE
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|TIME
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|TIME
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|TIME
block|,
name|Type
operator|.
name|TIME
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Constants
operator|.
name|PLUS
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DATE
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DATE
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DATE
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|TIME
block|,
name|Type
operator|.
name|TIME
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|TIME
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|TIME
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DATE_TIME
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Constants
operator|.
name|MINUS
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|MULT
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Constants
operator|.
name|MULT
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Constants
operator|.
name|MULT
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Constants
operator|.
name|MULT
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|MULT
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|IDIV
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|INTEGER
block|,
name|Constants
operator|.
name|DIV
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
comment|// except for integer -> decimal
name|Constants
operator|.
name|DIV
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Constants
operator|.
name|DIV
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Constants
operator|.
name|DIV
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
block|,
name|Type
operator|.
name|DECIMAL
block|,
name|Constants
operator|.
name|DIV
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DAY_TIME_DURATION
block|,
name|Type
operator|.
name|DECIMAL
block|,
name|Constants
operator|.
name|MOD
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,
name|Type
operator|.
name|NUMBER
block|,     }
decl_stmt|;
specifier|private
specifier|static
class|class
name|OpEntry
implements|implements
name|Comparable
block|{
specifier|public
specifier|final
name|int
name|op
decl_stmt|,
name|typeA
decl_stmt|,
name|typeB
decl_stmt|,
name|typeResult
decl_stmt|;
specifier|public
name|OpEntry
parameter_list|(
name|int
name|op
parameter_list|,
name|int
name|typeA
parameter_list|,
name|int
name|typeB
parameter_list|)
block|{
name|this
argument_list|(
name|op
argument_list|,
name|typeA
argument_list|,
name|typeB
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OpEntry
parameter_list|(
name|int
name|op
parameter_list|,
name|int
name|typeA
parameter_list|,
name|int
name|typeB
parameter_list|,
name|int
name|typeResult
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|typeA
operator|=
name|typeA
expr_stmt|;
name|this
operator|.
name|typeB
operator|=
name|typeB
expr_stmt|;
name|this
operator|.
name|typeResult
operator|=
name|typeResult
expr_stmt|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|OpEntry
name|that
init|=
operator|(
name|OpEntry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|op
operator|!=
name|that
operator|.
name|op
condition|)
return|return
name|this
operator|.
name|op
operator|-
name|that
operator|.
name|op
return|;
if|else if
condition|(
name|this
operator|.
name|typeA
operator|!=
name|that
operator|.
name|typeA
condition|)
return|return
name|this
operator|.
name|typeA
operator|-
name|that
operator|.
name|typeA
return|;
if|else if
condition|(
name|this
operator|.
name|typeB
operator|!=
name|that
operator|.
name|typeB
condition|)
return|return
name|this
operator|.
name|typeB
operator|-
name|that
operator|.
name|typeB
return|;
else|else
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
try|try
block|{
name|OpEntry
name|that
init|=
operator|(
name|OpEntry
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|op
operator|==
name|that
operator|.
name|op
operator|&&
name|this
operator|.
name|typeA
operator|==
name|that
operator|.
name|typeA
operator|&&
name|this
operator|.
name|typeB
operator|==
name|that
operator|.
name|typeB
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// TODO: implement hashcode, if needed
block|}
specifier|private
specifier|static
specifier|final
name|Map
name|OP_TYPES
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|OP_TABLE
operator|.
name|length
condition|;
name|i
operator|+=
literal|4
control|)
block|{
name|OpEntry
name|entry
init|=
operator|new
name|OpEntry
argument_list|(
name|OP_TABLE
index|[
name|i
index|]
argument_list|,
name|OP_TABLE
index|[
name|i
operator|+
literal|1
index|]
argument_list|,
name|OP_TABLE
index|[
name|i
operator|+
literal|2
index|]
argument_list|,
name|OP_TABLE
index|[
name|i
operator|+
literal|3
index|]
argument_list|)
decl_stmt|;
name|OP_TYPES
operator|.
name|put
argument_list|(
name|entry
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

