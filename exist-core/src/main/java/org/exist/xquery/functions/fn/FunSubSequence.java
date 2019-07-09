begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|persistent
operator|.
name|ExtArrayNodeSet
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
name|persistent
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
name|*
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Implements the fn:subsequence function.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunSubSequence
extends|extends
name|Function
block|{
specifier|public
specifier|static
specifier|final
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
literal|"subsequence"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns a subsequence of the items in $source-sequence, "
operator|+
literal|"items starting at the position, $starting-at, "
operator|+
literal|"up to the end of the sequence are included."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"source"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The source sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"starting-at"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The starting position in the $source"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the subsequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"subsequence"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns a subsequence of the items in $source, "
operator|+
literal|"starting at the position, $starting-at,  "
operator|+
literal|"including the number of items indicated by $length."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"source"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The source sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"starting-at"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The starting position in the $source"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"length"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The length of the subsequence"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the subsequence"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunSubSequence
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// statically check the argument list
name|checkArguments
argument_list|()
expr_stmt|;
comment|// call analyze for each argument
name|inPredicate
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
expr_stmt|;
name|unordered
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|UNORDERED
operator|)
operator|>
literal|0
expr_stmt|;
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|AnalyzeContextInfo
name|argContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|analyze
argument_list|(
name|argContextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|contextInfo
operator|.
name|setStaticReturnType
argument_list|(
name|argContextInfo
operator|.
name|getStaticReturnType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
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
specifier|final
name|Sequence
name|result
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
return|return
name|subsequence
argument_list|(
name|seq
argument_list|,
operator|(
operator|(
name|DoubleValue
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
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
operator|)
argument_list|,
name|getArgumentCount
argument_list|()
operator|!=
literal|3
condition|?
literal|null
else|:
operator|(
operator|(
name|DoubleValue
operator|)
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
operator|)
argument_list|)
return|;
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
comment|/**      * Creates a Subsequence from a sequence      *      * @param sequence the input sequence      * @param startLoc the starting location value as passed to {@code fn:subsequence}      * @param length the length value as passed to {@code fn:subsequence}, or null for all items      *      * @return the subsequence      */
specifier|public
specifier|static
name|Sequence
name|subsequence
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|,
specifier|final
name|DoubleValue
name|startLoc
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|DoubleValue
name|length
parameter_list|)
block|{
specifier|final
name|long
name|startArg
init|=
name|startLoc
operator|.
name|getLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|toExclusive
decl_stmt|;
if|if
condition|(
name|length
operator|!=
literal|null
condition|)
block|{
comment|/*                     From: https://www.w3.org/TR/xpath-functions-31/#func-subsequence                      $sourceSeq[fn:round($startingLoc) le position()                             and position() lt fn:round($startingLoc) + fn:round($length)]                  */
specifier|final
name|long
name|lengthArg
init|=
name|length
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|toExclusive
operator|=
name|startArg
operator|+
name|lengthArg
expr_stmt|;
block|}
else|else
block|{
comment|/*                     From: https://www.w3.org/TR/xpath-functions-31/#func-subsequence                      $sourceSeq[fn:round($startingLoc) le position()]                  */
name|toExclusive
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// we can't travel past Long.MAX_VALUE (...at the moment!)
block|}
comment|//TODO(AR) are there shortcuts where we can determine that the result is an empty-sequence from the args
comment|// we can't start before the first item
specifier|final
name|long
name|fromInclusive
decl_stmt|;
if|if
condition|(
name|startArg
operator|<=
literal|0
condition|)
block|{
name|fromInclusive
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|fromInclusive
operator|=
name|startArg
expr_stmt|;
block|}
return|return
operator|new
name|SubSequence
argument_list|(
name|fromInclusive
argument_list|,
name|toExclusive
argument_list|,
name|sequence
argument_list|)
return|;
block|}
block|}
end_class

end_unit

