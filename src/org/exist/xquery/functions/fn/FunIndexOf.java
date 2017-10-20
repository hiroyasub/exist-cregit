begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|xquery
operator|.
name|BasicFunction
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
operator|.
name|Comparison
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
operator|.
name|StringTruncationOperator
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
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|FunIndexOf
extends|extends
name|BasicFunction
block|{
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
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the sequence of positive integers giving the positions within the sequence"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|COLLATION_PARAM
init|=
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
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|SEARCH_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"search"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The search component"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|SEQ_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"source"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The source sequence"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION
init|=
literal|"Returns a sequence of positive integers giving the "
operator|+
literal|"positions within the sequence of atomic values $source "
operator|+
literal|"that are equal to $search.\n\n"
operator|+
literal|"The collation used by the invocation of this function "
operator|+
literal|"is determined according to the rules in 7.3.1 Collations. "
operator|+
literal|"The collation is used when string comparison is required.\n\n"
operator|+
literal|"The items in the sequence $source are compared with "
operator|+
literal|"$search under the rules for the 'eq' operator. Values of "
operator|+
literal|"type xs:untypedAtomic are compared as if they were of "
operator|+
literal|"type xs:string. Values that cannot be compared, i.e. "
operator|+
literal|"the 'eq' operator is not defined for their types, are "
operator|+
literal|"considered to be distinct. If an item compares equal, "
operator|+
literal|"then the position of that item in the sequence "
operator|+
literal|"$source is included in the result.\n\n"
operator|+
literal|"If the value of $source is the empty sequence, or "
operator|+
literal|"if no item in $source matches $search, then the "
operator|+
literal|"empty sequence is returned.\n\n"
operator|+
literal|"The first item in a sequence is at position 1, not position 0.\n\n"
operator|+
literal|"The result sequence is in ascending numeric order."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnIndexOf
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-of"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SEQ_PARAM
block|,
name|SEARCH_PARAM
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
literal|"index-of"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION
operator|+
literal|" "
operator|+
name|CollatingFunction
operator|.
name|THIRD_REL_COLLATION_ARG_EXAMPLE
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SEQ_PARAM
block|,
name|SEARCH_PARAM
block|,
name|COLLATION_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunIndexOf
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
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
block|}
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
specifier|final
name|AtomicValue
name|srch
init|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
decl_stmt|;
name|Collator
name|collator
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
specifier|final
name|String
name|collation
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|collator
operator|=
name|context
operator|.
name|getCollator
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collator
operator|=
name|context
operator|.
name|getDefaultCollator
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
name|int
name|j
init|=
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|0
index|]
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|AtomicValue
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|atomize
argument_list|()
decl_stmt|;
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
name|next
argument_list|,
name|srch
argument_list|,
name|StringTruncationOperator
operator|.
name|NONE
argument_list|,
name|Comparison
operator|.
name|EQ
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|j
argument_list|)
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
comment|//Ignore me : values can not be compared
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

