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
comment|/**  *  * @author perig  * @author ljo  *  */
end_comment

begin_class
specifier|public
class|class
name|FunCompare
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
literal|"compare"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the collatable comparison between $string-1 and $string-2, using $collation-uri. "
operator|+
literal|"-1 if $string-1 is inferior to $string-2, 0 if $string-1 is equal "
operator|+
literal|"to $string-2, 1 if $string-1 is superior to $string-2. "
operator|+
literal|"If either comparand is the empty sequence, the empty sequence is "
operator|+
literal|"returned. "
operator|+
literal|"Please remember to specify the collation in the context or use, "
operator|+
literal|"the three argument version if you don't want the system default."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string-1"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The first string"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string-2"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The second string"
argument_list|)
block|}
argument_list|,
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
literal|"-1 if $string-1 is inferior to $string-2, "
operator|+
literal|"0 if $string-1 is equal to $string-2, "
operator|+
literal|"1 if $string-1 is superior to $string-2. "
operator|+
literal|"If either comparand is the empty sequence, the empty sequence is returned."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"compare"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the collatable comparison between $string-1 and $string-2, using $collation-uri. "
operator|+
literal|"-1 if $string-1 is inferior to $string-2, 0 if $string-1 is equal "
operator|+
literal|"to $string-2, 1 if $string-1 is superior to $string-2. "
operator|+
literal|"If either comparand is the empty sequence, the empty sequence is returned. "
operator|+
name|THIRD_REL_COLLATION_ARG_EXAMPLE
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string-1"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The first string"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string-2"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The second string"
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
literal|"The relative collation URI"
argument_list|)
block|}
argument_list|,
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
literal|"-1 if $string-1 is inferior to $string-2, "
operator|+
literal|"0 if $string-1 is equal to $string-2, "
operator|+
literal|"1 if $string-1 is superior to $string-2. "
operator|+
literal|"If either comparand is the empty sequence, the empty sequence is returned."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunCompare
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
specifier|final
name|Sequence
name|seq1
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
specifier|final
name|Sequence
name|seq2
init|=
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
decl_stmt|;
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|seq1
operator|.
name|isEmpty
argument_list|()
operator|||
name|seq2
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
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|int
name|comparison
init|=
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|seq1
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|seq2
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparison
operator|==
name|Constants
operator|.
name|EQUAL
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|Constants
operator|.
name|EQUAL
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|comparison
operator|<
literal|0
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|Constants
operator|.
name|INFERIOR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|Constants
operator|.
name|SUPERIOR
argument_list|)
expr_stmt|;
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
