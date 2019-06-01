begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2005-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
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
name|Cardinality
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
name|Dependency
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
name|Function
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
name|FunctionSignature
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
name|Profiler
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|SequenceType
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
comment|/**  * Implements the fn:roud-half-to-even() function.  *  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|FunRoundHalfToEven
extends|extends
name|Function
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_1_PARAM
init|=
literal|"The value returned is the nearest (that is, numerically closest) "
operator|+
literal|"value to $arg that is a multiple of ten to the power of minus 0. "
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_2_PARAM
init|=
literal|"The value returned is the nearest (that is, numerically closest) "
operator|+
literal|"value to $arg that is a multiple of ten to the power of minus "
operator|+
literal|"$precision. "
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_COMMON
init|=
literal|"If two such values are equally near (e.g. if the "
operator|+
literal|"fractional part in $arg is exactly .500...), the function returns "
operator|+
literal|"the one whose least significant digit is even.\n\nIf the type of "
operator|+
literal|"$arg is one of the four numeric types xs:float, xs:double, "
operator|+
literal|"xs:decimal or xs:integer the type of the result is the same as "
operator|+
literal|"the type of $arg. If the type of $arg is a type derived from one "
operator|+
literal|"of the numeric types, the result is an instance of the "
operator|+
literal|"base numeric type.\n\n"
operator|+
literal|"The three argument version of the function with $precision = 0 "
operator|+
literal|"produces the same result as the two argument version.\n\n"
operator|+
literal|"For arguments of type xs:float and xs:double, if the argument is "
operator|+
literal|"NaN, positive or negative zero, or positive or negative infinity, "
operator|+
literal|"then the result is the same as the argument. In all other cases, "
operator|+
literal|"the argument is cast to xs:decimal, the function is applied to this "
operator|+
literal|"xs:decimal value, and the resulting xs:decimal is cast back to "
operator|+
literal|"xs:float or xs:double as appropriate to form the function result. "
operator|+
literal|"If the resulting xs:decimal value is zero, then positive or negative "
operator|+
literal|"zero is returned according to the sign of the original argument.\n\n"
operator|+
literal|"Note that the process of casting to xs:decimal "
operator|+
literal|"may result in an error [err:FOCA0001].\n\n"
operator|+
literal|"If $arg is of type xs:float or xs:double, rounding occurs on the "
operator|+
literal|"value of the mantissa computed with exponent = 0."
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|ARG_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The input number"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PRECISION_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"precision"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The precision factor"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|RETURN_TYPE
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the rounded value"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"round-half-to-even"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_1_PARAM
operator|+
name|FUNCTION_DESCRIPTION_COMMON
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|ARG_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"round-half-to-even"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_2_PARAM
operator|+
name|FUNCTION_DESCRIPTION_COMMON
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|ARG_PARAM
block|,
name|PRECISION_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunRoundHalfToEven
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signatures
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signatures
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|DOUBLE
return|;
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
name|Sequence
name|result
decl_stmt|;
name|IntegerValue
name|precision
init|=
literal|null
decl_stmt|;
specifier|final
name|Sequence
name|seq
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
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
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|>
literal|1
condition|)
block|{
name|precision
operator|=
operator|(
name|IntegerValue
operator|)
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
block|}
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
name|NumericValue
name|value
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|NumericValue
condition|)
block|{
name|value
operator|=
operator|(
name|NumericValue
operator|)
name|item
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
operator|(
name|NumericValue
operator|)
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|value
operator|.
name|round
argument_list|(
name|precision
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit
