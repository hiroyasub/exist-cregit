begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
package|;
end_package

begin_import
import|import
name|java
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunMax
extends|extends
name|CollatingFunction
block|{
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
literal|"max"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Selects an item from the input sequence $a whose value "
operator|+
literal|"is greater than or equal to the value of every other item in the "
operator|+
literal|"input sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"max"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Selects an item from the input sequence $a whose value "
operator|+
literal|"is greater than or equal to the value of every other item in the "
operator|+
literal|"input sequence. The collation URI specified in $b will be used for "
operator|+
literal|"string comparisons."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|FunMax
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|result
decl_stmt|;
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
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|boolean
name|computableProcessing
init|=
literal|false
decl_stmt|;
comment|//TODO : test if a range index is defined *iff* it is compatible with the collator
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
name|SequenceIterator
name|iter
init|=
name|arg
operator|.
name|unorderedIterator
argument_list|()
decl_stmt|;
name|AtomicValue
name|max
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Cannot compare "
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
argument_list|)
throw|;
name|AtomicValue
name|value
init|=
name|item
operator|.
name|atomize
argument_list|()
decl_stmt|;
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
name|UNTYPED_ATOMIC
condition|)
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
if|if
condition|(
name|max
operator|==
literal|null
condition|)
name|max
operator|=
name|value
expr_stmt|;
else|else
block|{
if|if
condition|(
name|Type
operator|.
name|getCommonSuperType
argument_list|(
name|max
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
literal|"FORG0006: Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|max
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
name|UNTYPED_ATOMIC
condition|)
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
comment|//Numeric tests
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
comment|//Don't mix comparisons
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|max
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
literal|"FORG0006: Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|max
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
argument_list|)
throw|;
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
name|max
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
name|max
operator|=
name|FloatValue
operator|.
name|NaN
expr_stmt|;
else|else
name|max
operator|=
name|DoubleValue
operator|.
name|NaN
expr_stmt|;
comment|//although result will be NaN, we need to continue on order to type correctly
continue|continue;
block|}
else|else
name|max
operator|=
name|max
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
name|max
operator|instanceof
name|ComputableValue
operator|&&
name|value
operator|instanceof
name|ComputableValue
condition|)
block|{
comment|//Type value correctly
name|value
operator|=
name|value
operator|.
name|promote
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|max
operator|=
operator|(
name|ComputableValue
operator|)
name|max
operator|.
name|max
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|max
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
argument_list|)
throw|;
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
name|max
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
name|max
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
name|result
operator|=
name|max
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
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

