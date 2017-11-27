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
name|PatternSyntaxException
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
name|PatternFactory
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
name|StringValue
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
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|regex
operator|.
name|RegexUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunReplace
extends|extends
name|FunMatches
block|{
specifier|private
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_3_PARAM
init|=
literal|"The function returns the xs:string that is obtained by replacing each non-overlapping substring "
operator|+
literal|"of $input that matches the given $pattern with an occurrence of the $replacement string.\n\n"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_4_PARAM
init|=
literal|"The function returns the xs:string that is obtained by replacing each non-overlapping substring "
operator|+
literal|"of $input that matches the given $pattern with an occurrence of the $replacement string.\n\n"
operator|+
literal|"The $flags argument is interpreted in the same manner as for the fn:matches() function.\n\n"
operator|+
literal|"Calling the four argument version with the $flags argument set to a "
operator|+
literal|"zero-length string gives the same effect as using the three argument version.\n\n"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_COMMON
init|=
literal|"If $input is the empty sequence, it is interpreted as the zero-length string.\n\nIf two overlapping "
operator|+
literal|"substrings of $input both match the $pattern, then only the first one (that is, the one whose first "
operator|+
literal|"character comes first in the $input string) is replaced.\n\nWithin the $replacement string, a variable "
operator|+
literal|"$N may be used to refer to the substring captured by the Nth parenthesized sub-expression in the "
operator|+
literal|"regular expression. For each match of the pattern, these variables are assigned the value of the "
operator|+
literal|"content matched by the relevant sub-expression, and the modified replacement string is then "
operator|+
literal|"substituted for the characters in $input that matched the pattern. $0 refers to the substring "
operator|+
literal|"captured by the regular expression as a whole.\n\nMore specifically, the rules are as follows, "
operator|+
literal|"where S is the number of parenthesized sub-expressions in the regular expression, and N is the "
operator|+
literal|"decimal number formed by taking all the digits that consecutively follow the $ character:\n\n"
operator|+
literal|"1.  If N=0, then the variable is replaced by the substring matched by the regular expression as a whole.\n\n"
operator|+
literal|"2.  If 1<=N<=S, then the variable is replaced by the substring captured by the Nth parenthesized "
operator|+
literal|"sub-expression. If the Nth parenthesized sub-expression was not matched, then the variable "
operator|+
literal|"is replaced by the zero-length string.\n\n"
operator|+
literal|"3.  If S<N<=9, then the variable is replaced by the zero-length string.\n\n"
operator|+
literal|"4.  Otherwise (if N>S and N>9), the last digit of N is taken to be a literal character to be "
operator|+
literal|"included \"as is\" in the replacement string, and the rules are reapplied using the number N "
operator|+
literal|"formed by stripping off this last digit."
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|INPUT_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The input string"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PATTERN_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pattern"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The pattern to match"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|REPLACEMENT_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"replacement"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The string to replace the pattern with"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|FLAGS_ARG
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"flags"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The flags"
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the altered string"
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
literal|"replace"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_3_PARAM
operator|+
name|FUNCTION_DESCRIPTION_COMMON
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|INPUT_ARG
block|,
name|PATTERN_ARG
block|,
name|REPLACEMENT_ARG
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
literal|"replace"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_4_PARAM
operator|+
name|FUNCTION_DESCRIPTION_COMMON
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|INPUT_ARG
block|,
name|PATTERN_ARG
block|,
name|REPLACEMENT_ARG
block|,
name|FLAGS_ARG
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FunReplace
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
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|steps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Expression
name|arg
init|=
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
block|{
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
block|}
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
name|arg
operator|=
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
block|{
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
block|}
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
name|arg
operator|=
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
block|{
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
block|}
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
literal|4
condition|)
block|{
name|arg
operator|=
name|arguments
operator|.
name|get
argument_list|(
literal|3
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
literal|"4"
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
block|{
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
block|}
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
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
name|Sequence
name|result
decl_stmt|;
specifier|final
name|Sequence
name|stringArg
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
name|stringArg
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|StringValue
operator|.
name|EMPTY_STRING
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|flags
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|4
condition|)
block|{
name|flags
operator|=
name|parseFlags
argument_list|(
name|this
argument_list|,
name|getArgument
argument_list|(
literal|3
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
block|}
else|else
block|{
name|flags
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|String
name|string
init|=
name|stringArg
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|patternSeq
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
specifier|final
name|Sequence
name|replaceSeq
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
decl_stmt|;
name|String
name|replace
init|=
name|replaceSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|pattern
decl_stmt|;
if|if
condition|(
name|hasLiteral
argument_list|(
name|flags
argument_list|)
condition|)
block|{
comment|// no need to change anything in the pattern
name|pattern
operator|=
name|patternSeq
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
comment|// however, $ and \ now have no special significance
name|replace
operator|=
name|replace
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"\\\\"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$"
argument_list|,
literal|"\\$"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pattern
operator|=
name|translateRegexp
argument_list|(
name|this
argument_list|,
name|patternSeq
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|hasIgnoreWhitespace
argument_list|(
name|flags
argument_list|)
argument_list|,
name|hasCaseInsensitive
argument_list|(
name|flags
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//An error is raised [err:FORX0004] if the value of $replacement contains a "$" character that is not immediately followed by a digit 0-9 and not immediately preceded by a "\".
comment|//An error is raised [err:FORX0004] if the value of $replacement contains a "\" character that is not part of a "\\" pair, unless it is immediately followed by a "$" character.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|replace
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//Commented out : this seems to be a total non sense
comment|/*             	if (replace.charAt(i) == '$') {             		try {             			if (!(replace.charAt(i - 1) == '\\' || Character.isDigit(replace.charAt(i + 1))))             				throw new XPathException(this, "err:FORX0004 The value of $replacement contains a '$' character that is not immediately followed by a digit 0-9 and not immediately preceded by a '\\'.");             		//Handle index exceptions             		} catch (Exception e){             			throw new XPathException(this, "err:FORX0004 The value of $replacement contains a '$' character that is not immediately followed by a digit 0-9 and not immediately preceded by a '\\'.");             		}             	}             	*/
if|if
condition|(
name|replace
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
operator|(
name|replace
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'\\'
operator|||
name|replace
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'$'
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
name|FORX0004
argument_list|,
literal|"The value of $replacement contains a '\\' character that is not part of a '\\\\' pair, unless it is immediately followed by a '$' character."
argument_list|,
name|replaceSeq
argument_list|)
throw|;
block|}
name|i
operator|++
expr_stmt|;
comment|//Handle index exceptions
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FORX0004
argument_list|,
literal|"The value of $replacement contains a '\\' character that is not part of a '\\\\' pair, unless it is immediately followed by a '$' character."
argument_list|,
name|replaceSeq
argument_list|)
throw|;
block|}
block|}
block|}
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
name|PatternFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|getPattern
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
specifier|final
name|String
name|r
init|=
name|matcher
operator|.
name|replaceAll
argument_list|(
name|replace
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FORX0001
argument_list|,
literal|"Invalid regular expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|patternSeq
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FORX0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|patternSeq
argument_list|,
name|e
argument_list|)
throw|;
comment|//Some JVMs seem to raise this one
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FORX0004
argument_list|,
literal|"Invalid replace expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|replaceSeq
argument_list|,
name|e
argument_list|)
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

