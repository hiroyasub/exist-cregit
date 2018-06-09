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

begin_class
specifier|public
class|class
name|FunSum
extends|extends
name|Function
block|{
comment|//Used to detect overflows : currently not used.
specifier|private
name|boolean
name|gotInfinity
init|=
literal|false
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
literal|"sum"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns a value obtained by adding together the values in $arg. "
operator|+
literal|"If $arg is the the empty sequence the xs:double value 0.0e0 is returned."
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
literal|"The sequence of numbers to be summed up"
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
name|EXACTLY_ONE
argument_list|,
literal|"the sum of all numbers in $arg"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sum"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns a value obtained by adding together the values in $arg. "
operator|+
literal|"If $arg is the the empty sequence then $default is returned."
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
literal|"The sequence of numbers to be summed up"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"default"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The default value if $arg computes to the empty sequence"
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
literal|"the sum of all numbers in $arg"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunSum
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
specifier|final
name|Sequence
name|inner
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
name|inner
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//If $zero is not specified, then the value returned for an empty sequence is the xs:integer value 0
name|Sequence
name|zero
init|=
name|IntegerValue
operator|.
name|ZERO
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|zero
operator|=
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
expr_stmt|;
block|}
name|result
operator|=
name|zero
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|SequenceIterator
name|iter
init|=
name|inner
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|Item
name|item
init|=
name|iter
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|AtomicValue
name|value
init|=
name|item
operator|.
name|atomize
argument_list|()
decl_stmt|;
name|value
operator|=
name|check
argument_list|(
name|value
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Set the first value
name|ComputableValue
name|sum
init|=
operator|(
name|ComputableValue
operator|)
name|value
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|item
operator|=
name|iter
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|value
operator|=
name|item
operator|.
name|atomize
argument_list|()
expr_stmt|;
name|value
operator|=
name|check
argument_list|(
name|value
argument_list|,
name|sum
argument_list|)
expr_stmt|;
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
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|value
operator|)
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
name|gotInfinity
operator|=
literal|true
expr_stmt|;
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
name|sum
operator|=
name|DoubleValue
operator|.
name|NaN
expr_stmt|;
break|break;
block|}
block|}
name|sum
operator|=
operator|(
name|ComputableValue
operator|)
name|sum
operator|.
name|promote
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|//Aggregate next values
name|sum
operator|=
name|sum
operator|.
name|plus
argument_list|(
operator|(
name|ComputableValue
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|sum
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|gotInfinity
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|result
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|result
operator|)
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
comment|//Throw an overflow eception here since we get an infinity
comment|//whereas is hasn't been provided by the sequence
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
specifier|private
name|AtomicValue
name|check
parameter_list|(
name|AtomicValue
name|value
parameter_list|,
name|ComputableValue
name|sum
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|sum
operator|!=
literal|null
operator|&&
name|sum
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
name|sum
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
name|sum
operator|!=
literal|null
operator|&&
name|sum
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
name|sum
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
comment|//Any values of type xdt:untypedAtomic in the sequence $arg are cast to xs:double
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
operator|!
operator|(
name|value
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
name|XPTY0004
argument_list|,
literal|""
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
operator|+
literal|"("
operator|+
name|value
operator|+
literal|")' can not be an operand in a sum"
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

