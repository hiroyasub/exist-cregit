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

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|*
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @see<a href="https://www.w3.org/TR/xpath-functions-31/#func-tokenize">https://www.w3.org/TR/xpath-functions-31/#func-tokenize</a>  */
end_comment

begin_class
specifier|public
class|class
name|FunTokenize
extends|extends
name|FunMatches
block|{
specifier|private
specifier|static
specifier|final
name|QName
name|FS_TOKENIZE_NAME
init|=
operator|new
name|QName
argument_list|(
literal|"tokenize"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|FS_TOKENIZE_PARAM_INPUT
init|=
name|optParam
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"The input string"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|FS_TOKENIZE_PARAM_PATTERN
init|=
name|param
argument_list|(
literal|"pattern"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"The tokenization pattern"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FS_TOKENIZE
index|[]
init|=
name|functionSignatures
argument_list|(
name|FS_TOKENIZE_NAME
argument_list|,
literal|"Breaks the input string $input into a sequence of strings, "
argument_list|,
name|returnsOptMany
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
literal|"the token sequence"
argument_list|)
argument_list|,
name|arities
argument_list|(
name|arity
argument_list|(
name|FS_TOKENIZE_PARAM_INPUT
argument_list|)
argument_list|,
name|arity
argument_list|(
name|FS_TOKENIZE_PARAM_INPUT
argument_list|,
name|FS_TOKENIZE_PARAM_PATTERN
argument_list|)
argument_list|,
name|arity
argument_list|(
name|FS_TOKENIZE_PARAM_INPUT
argument_list|,
name|FS_TOKENIZE_PARAM_PATTERN
argument_list|,
name|param
argument_list|(
literal|"flags"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"The flags"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunTokenize
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
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
name|String
name|string
init|=
name|stringArg
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|string
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
literal|3
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
name|pattern
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|pattern
operator|=
literal|" "
expr_stmt|;
name|string
operator|=
name|FunNormalizeSpace
operator|.
name|normalize
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|hasLiteral
argument_list|(
name|flags
argument_list|)
condition|)
block|{
comment|// no need to change anything
name|pattern
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
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|boolean
name|ignoreWhitespace
init|=
name|hasIgnoreWhitespace
argument_list|(
name|flags
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|caseBlind
init|=
operator|!
name|hasCaseInsensitive
argument_list|(
name|flags
argument_list|)
decl_stmt|;
name|pattern
operator|=
name|translateRegexp
argument_list|(
name|this
argument_list|,
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
argument_list|,
name|ignoreWhitespace
argument_list|,
name|caseBlind
argument_list|)
expr_stmt|;
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
block|}
if|if
condition|(
name|pat
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
operator|.
name|matches
argument_list|()
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
name|FORX0003
argument_list|,
literal|"regular expression could match empty string"
argument_list|)
throw|;
block|}
specifier|final
name|String
index|[]
name|tokens
init|=
name|pat
operator|.
name|split
argument_list|(
name|string
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|token
range|:
name|tokens
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
operator|new
name|StringValue
argument_list|(
name|pattern
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
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

