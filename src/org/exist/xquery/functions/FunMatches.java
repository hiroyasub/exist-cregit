begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|NodeProxy
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
name|storage
operator|.
name|NativeValueIndex
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
name|Atomize
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
name|DynamicCardinalityCheck
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
name|DynamicTypeCheck
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
name|Expression
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
name|util
operator|.
name|Error
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
name|RegexTranslator
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
name|RegexTranslator
operator|.
name|RegexSyntaxException
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
name|BooleanValue
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
comment|/**  * Implements the fn:matches() function.  *   * Based on the jakarta ORO package for regular expression support.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunMatches
extends|extends
name|Function
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
literal|"matches"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns true if the first argument string matches the regular expression specified "
operator|+
literal|"by the second argument. This function is optimized internally if a range index of type xs:string "
operator|+
literal|"is defined on the nodes passed to the first argument."
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
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
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"matches"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns true if the first argument string matches the regular expression specified "
operator|+
literal|"by the second argument. This function is optimized internally if a range index of type xs:string "
operator|+
literal|"is defined on the nodes passed to the first argument."
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
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
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|protected
name|Matcher
name|matcher
init|=
literal|null
decl_stmt|;
specifier|protected
name|Pattern
name|pat
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FunMatches
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Function#setArguments(java.util.List) 	 */
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|Expression
name|arg
init|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|arg
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"1"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|arg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|arg
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|arg
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
name|arg
operator|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|arg
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"2"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|arg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|arg
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|arg
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
if|if
condition|(
name|arguments
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|arg
operator|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|arg
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"3"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|arg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|arg
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|arg
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
specifier|final
name|Expression
name|stringArg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|patternArg
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|stringArg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
operator|(
name|stringArg
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
operator|&&
operator|(
name|patternArg
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
else|else
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
if|if
condition|(
name|inPredicate
operator|&&
operator|(
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
block|{
comment|/* If one argument is a node set we directly 			 * return the matching nodes from the context set. This works 			 * only inside predicates. 			 */
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|// In all other cases, we return boolean
return|return
name|Type
operator|.
name|BOOLEAN
return|;
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//  call analyze for each argument
name|inPredicate
operator|=
operator|(
name|flags
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
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
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|Sequence
name|result
decl_stmt|;
name|Sequence
name|input
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
name|input
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
if|else if
condition|(
name|inPredicate
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|getDependencies
argument_list|()
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
argument_list|()
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
name|OPTIMIZATION_FLAGS
argument_list|,
literal|""
argument_list|,
literal|"Index evaluation"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evalWithIndex
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
argument_list|()
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
name|OPTIMIZATION_FLAGS
argument_list|,
literal|""
argument_list|,
literal|"Generic evaluation"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evalGeneric
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|input
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
comment|/**      * @param contextSequence      * @param contextItem      * @param stringArg      * @return 	 * @throws XPathException      */
specifier|private
name|Sequence
name|evalWithIndex
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
name|input
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
name|boolean
name|caseSensitive
init|=
literal|true
decl_stmt|;
name|int
name|flags
init|=
literal|0
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
name|String
name|flagsArg
init|=
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
name|getStringValue
argument_list|()
decl_stmt|;
name|caseSensitive
operator|=
operator|(
name|flagsArg
operator|.
name|indexOf
argument_list|(
literal|'i'
argument_list|)
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
operator|)
expr_stmt|;
name|flags
operator|=
name|parseFlags
argument_list|(
name|flagsArg
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|result
decl_stmt|;
name|String
name|pattern
init|=
name|translateRegexp
argument_list|(
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
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|NodeSet
name|nodes
init|=
name|input
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
comment|// get the type of a possible index
name|int
name|indexType
init|=
name|nodes
operator|.
name|getIndexType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|indexType
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|DocumentSet
name|docs
init|=
name|nodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
try|try
block|{
name|NativeValueIndex
name|index
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getValueIndex
argument_list|()
decl_stmt|;
comment|//TODO : check index' case compatibility with flags' one ? -pb
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
argument_list|()
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
name|OPTIMIZATIONS
argument_list|,
literal|"Using index "
operator|+
name|index
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Regex: "
operator|+
name|pattern
argument_list|)
expr_stmt|;
name|result
operator|=
name|index
operator|.
name|match
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
name|pattern
argument_list|,
name|DBBroker
operator|.
name|MATCH_REGEXP
argument_list|,
name|flags
argument_list|,
name|caseSensitive
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|nodes
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|node
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
argument_list|(
name|node
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|pattern
argument_list|,
name|flags
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|node
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
comment|/** 	 * Translates the regular expression from XPath2 syntax to java regex 	 * syntax. 	 *  	 * @param pattern 	 * @return 	 * @throws XPathException 	 */
specifier|protected
name|String
name|translateRegexp
parameter_list|(
name|String
name|pattern
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// convert pattern to Java regex syntax
try|try
block|{
name|pattern
operator|=
name|RegexTranslator
operator|.
name|translate
argument_list|(
name|pattern
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RegexSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Conversion from XPath2 to Java regular expression "
operator|+
literal|"syntax failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|pattern
return|;
block|}
comment|/**      * @param contextSequence      * @param contextItem      * @param stringArg      * @return      * @throws XPathException      */
specifier|private
name|Sequence
name|evalGeneric
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
name|stringArg
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|string
init|=
name|stringArg
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
name|translateRegexp
argument_list|(
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
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|flags
init|=
literal|0
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
name|flags
operator|=
name|parseFlags
argument_list|(
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
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|match
argument_list|(
name|string
argument_list|,
name|pattern
argument_list|,
name|flags
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @param string      * @param pattern      * @param flags      * @return      * @throws XPathException      */
specifier|private
name|boolean
name|match
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|pattern
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|pat
operator|==
literal|null
operator|||
operator|(
operator|!
name|pattern
operator|.
name|equals
argument_list|(
name|pat
operator|.
name|pattern
argument_list|()
argument_list|)
operator|)
operator|||
name|flags
operator|!=
name|pat
operator|.
name|flags
argument_list|()
condition|)
block|{
name|pat
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|pattern
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|pat
operator|.
name|matcher
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|.
name|reset
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
return|return
literal|true
return|;
else|else
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid regular expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|final
specifier|static
name|int
name|parseFlags
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|flags
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'m'
case|:
name|flags
operator||=
name|Pattern
operator|.
name|MULTILINE
expr_stmt|;
break|break;
case|case
literal|'i'
case|:
name|flags
operator|=
name|flags
operator||
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|UNICODE_CASE
expr_stmt|;
break|break;
case|case
literal|'x'
case|:
name|flags
operator||=
name|Pattern
operator|.
name|COMMENTS
expr_stmt|;
break|break;
case|case
literal|'s'
case|:
name|flags
operator||=
name|Pattern
operator|.
name|DOTALL
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid regular expression flag: "
operator|+
name|ch
argument_list|)
throw|;
block|}
block|}
return|return
name|flags
return|;
block|}
block|}
end_class

end_unit

