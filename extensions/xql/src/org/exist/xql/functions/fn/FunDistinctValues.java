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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|Constants
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
name|ValueComparison
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * Implements the fn:distinct-values standard library function.  *   * @author wolf  * @author perig  */
end_comment

begin_class
specifier|public
class|class
name|FunDistinctValues
extends|extends
name|CollatingFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FunDistinctValues
operator|.
name|class
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
literal|"distinct-values"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence where duplicate values of $atomic-values, based on value equality, "
operator|+
literal|"have been deleted."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"atomic-values"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The atomic values"
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
name|ZERO_OR_MORE
argument_list|,
literal|"the distinct values sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"distinct-values"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence where duplicate values of $atomic-values, based on value equality specified by collation $collation-uri, "
operator|+
literal|"have been deleted."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"atomic-values"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The atomic values"
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
name|ZERO_OR_MORE
argument_list|,
literal|"the distinct values sequence"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunDistinctValues
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ATOMIC
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
comment|/* 	public int getDependencies() { 		int deps = Dependency.CONTEXT_SET; 		if (getArgumentCount() == 1) 			deps |= getArgument(0).getDependencies(); 		return deps; 	} 	*/
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|TreeSet
argument_list|<
name|AtomicValue
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<
name|AtomicValue
argument_list|>
argument_list|(
operator|new
name|ValueComparator
argument_list|(
name|collator
argument_list|)
argument_list|)
decl_stmt|;
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Item
name|item
decl_stmt|;
name|AtomicValue
name|value
decl_stmt|;
name|boolean
name|hasAlreadyNaN
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|item
operator|=
name|i
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
if|if
condition|(
operator|!
name|set
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
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
name|isNaN
argument_list|()
condition|)
block|{
comment|//although NaN does not equal itself, if $arg contains multiple NaN values a single NaN is returned.
if|if
condition|(
operator|!
name|hasAlreadyNaN
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|hasAlreadyNaN
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|set
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
return|return
name|result
return|;
block|}
specifier|public
specifier|final
specifier|static
class|class
name|ValueComparator
implements|implements
name|Comparator
argument_list|<
name|AtomicValue
argument_list|>
block|{
name|Collator
name|collator
decl_stmt|;
specifier|public
name|ValueComparator
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) 		 */
specifier|public
name|int
name|compare
parameter_list|(
name|AtomicValue
name|o1
parameter_list|,
name|AtomicValue
name|o2
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|ValueComparison
operator|.
name|compareAtomic
argument_list|(
name|collator
argument_list|,
name|o1
argument_list|,
name|o2
argument_list|,
name|Constants
operator|.
name|TRUNC_NONE
argument_list|,
name|Constants
operator|.
name|EQ
argument_list|)
condition|)
return|return
name|Constants
operator|.
name|EQUAL
return|;
if|else if
condition|(
name|ValueComparison
operator|.
name|compareAtomic
argument_list|(
name|collator
argument_list|,
name|o1
argument_list|,
name|o2
argument_list|,
name|Constants
operator|.
name|TRUNC_NONE
argument_list|,
name|Constants
operator|.
name|LT
argument_list|)
condition|)
return|return
name|Constants
operator|.
name|INFERIOR
return|;
if|else if
condition|(
name|ValueComparison
operator|.
name|compareAtomic
argument_list|(
name|collator
argument_list|,
name|o1
argument_list|,
name|o2
argument_list|,
name|Constants
operator|.
name|TRUNC_NONE
argument_list|,
name|Constants
operator|.
name|GT
argument_list|)
condition|)
return|return
name|Constants
operator|.
name|SUPERIOR
return|;
comment|//Fallback
else|else
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|collator
argument_list|,
name|o2
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//throw new IllegalArgumentException("cannot compare values");
comment|//Values that cannot be compared, i.e. the eq operator is not defined for their types, are considered to be distinct
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

