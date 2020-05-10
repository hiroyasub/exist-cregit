begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|util
operator|.
name|Collations
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
name|ErrorCodes
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
name|AtomicValue
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
name|DoubleValue
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
name|DurationValue
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
name|FloatValue
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
name|QNameValue
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
name|SequenceIterator
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
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
class|class
name|FunMin
extends|extends
name|CollatingFunction
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_COMMON_1
init|=
literal|"Selects an item from the input sequence $arg whose value is "
operator|+
literal|"less than or equal to the value of every other item in the "
operator|+
literal|"input sequence. If there are two or more such items, then "
operator|+
literal|"the specific item whose value is returned is implementation dependent.\n\n"
operator|+
literal|"The following rules are applied to the input sequence:\n\n"
operator|+
literal|"- Values of type xs:untypedAtomic in $arg are cast to xs:double.\n"
operator|+
literal|"- Numeric and xs:anyURI values are converted to the least common "
operator|+
literal|"type that supports the 'le' operator by a combination of type promotion "
operator|+
literal|"and subtype substitution. See Section B.1 Type PromotionXP and "
operator|+
literal|"Section B.2 Operator MappingXP.\n\n"
operator|+
literal|"The items in the resulting sequence may be reordered in an arbitrary "
operator|+
literal|"order. The resulting sequence is referred to below as the converted "
operator|+
literal|"sequence. This function returns an item from the converted sequence "
operator|+
literal|"rather than the input sequence.\n\n"
operator|+
literal|"If the converted sequence is empty, the empty sequence is returned.\n\n"
operator|+
literal|"All items in $arg must be numeric or derived from a single base type "
operator|+
literal|"for which the 'le' operator is defined. In addition, the values in the "
operator|+
literal|"sequence must have a total order. If date/time values do not have a "
operator|+
literal|"timezone, they are considered to have the implicit timezone provided "
operator|+
literal|"by the dynamic context for the purpose of comparison. Duration values "
operator|+
literal|"must either all be xs:yearMonthDuration values or must all be "
operator|+
literal|"xs:dayTimeDuration values.\n\n"
operator|+
literal|"If any of these conditions is not met, a type error is raised [err:FORG0006].\n\n"
operator|+
literal|"If the converted sequence contains the value NaN, the value NaN is returned.\n\n"
operator|+
literal|"If the items in the value of $arg are of type xs:string or types derived "
operator|+
literal|"by restriction from xs:string, then the determination of the item with "
operator|+
literal|"the smallest value is made according to the collation that is used. "
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_2_PARAM
init|=
literal|"If the type of the items in $arg is not xs:string and $collation is "
operator|+
literal|"specified, the collation is ignored.\n\n"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_COMMON_2
init|=
literal|"The collation used by the invocation of this function is determined "
operator|+
literal|"according to the rules in 7.3.1 Collations."
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
literal|"min"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_COMMON_1
operator|+
name|FUNCTION_DESCRIPTION_COMMON_2
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The input sequence"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the minimum value"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"min"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_COMMON_1
operator|+
name|FUNCTION_DESCRIPTION_2_PARAM
operator|+
name|FUNCTION_DESCRIPTION_COMMON_2
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The input sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collation-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The collation URI"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the minimum value"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunMin
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
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
name|boolean
name|computableProcessing
init|=
literal|false
decl_stmt|;
name|Sequence
name|result
decl_stmt|;
specifier|final
name|Sequence
name|arg
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
name|arg
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
comment|//TODO : test if a range index is defined *iff* it is compatible with the collator
specifier|final
name|Collator
name|collator
init|=
name|getCollator
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|SequenceIterator
name|iter
init|=
name|arg
operator|.
name|unorderedIterator
argument_list|()
decl_stmt|;
name|AtomicValue
name|min
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iter
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|QNameValue
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
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
argument_list|,
name|arg
argument_list|)
throw|;
block|}
name|AtomicValue
name|value
init|=
name|item
operator|.
name|atomize
argument_list|()
decl_stmt|;
comment|//Duration values must either all be xs:yearMonthDuration values or must all be xs:dayTimeDuration values.
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|DURATION
argument_list|)
condition|)
block|{
name|value
operator|=
operator|(
operator|(
name|DurationValue
operator|)
name|value
operator|)
operator|.
name|wrap
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|YEAR_MONTH_DURATION
condition|)
block|{
if|if
condition|(
name|min
operator|!=
literal|null
operator|&&
name|min
operator|.
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|YEAR_MONTH_DURATION
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DAY_TIME_DURATION
condition|)
block|{
if|if
condition|(
name|min
operator|!=
literal|null
operator|&&
name|min
operator|.
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|DAY_TIME_DURATION
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
throw|;
block|}
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
throw|;
block|}
comment|//Any value of type xdt:untypedAtomic is cast to xs:double
block|}
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|min
operator|==
literal|null
condition|)
block|{
name|min
operator|=
name|value
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Type
operator|.
name|getCommonSuperType
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|,
name|value
operator|.
name|getType
argument_list|()
argument_list|)
operator|==
name|Type
operator|.
name|ATOMIC
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
throw|;
block|}
comment|//Any value of type xdt:untypedAtomic is cast to xs:double
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATOMIC
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
block|}
comment|//Numeric tests
if|if
condition|(
name|Type
operator|.
name|subTypeOfUnion
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
comment|//Don't mix comparisons
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOfUnion
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|min
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|value
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
block|{
comment|//Type NaN correctly
name|value
operator|=
name|value
operator|.
name|promote
argument_list|(
name|min
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|FLOAT
condition|)
block|{
name|min
operator|=
name|FloatValue
operator|.
name|NaN
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
name|DoubleValue
operator|.
name|NaN
expr_stmt|;
block|}
comment|//although result will be NaN, we need to continue on order to type correctly
continue|continue;
block|}
name|min
operator|=
name|min
operator|.
name|promote
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|//Ugly test
if|if
condition|(
name|value
operator|instanceof
name|ComputableValue
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|min
operator|instanceof
name|ComputableValue
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|min
argument_list|)
throw|;
block|}
comment|//Type value correctly
name|value
operator|=
name|value
operator|.
name|promote
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|min
operator|=
name|min
operator|.
name|min
argument_list|(
name|collator
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|computableProcessing
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|computableProcessing
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
name|FORG0006
argument_list|,
literal|"Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|min
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
throw|;
block|}
if|if
condition|(
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|min
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
name|min
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
name|result
operator|=
name|min
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

