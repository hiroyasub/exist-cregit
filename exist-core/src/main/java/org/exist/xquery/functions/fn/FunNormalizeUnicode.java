begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2006-2009, The eXist Project  * http://exist-db.org/  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *   * $Id$  */
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
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
import|import
name|java
operator|.
name|text
operator|.
name|Normalizer
import|;
end_import

begin_comment
comment|/**  * Implements fn:normalize-unicode()  *  * @author perig  *  */
end_comment

begin_class
specifier|public
class|class
name|FunNormalizeUnicode
extends|extends
name|Function
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FunNormalizeUnicode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_0_PARAM
init|=
literal|"Returns the value of the context item normalized according to the "
operator|+
literal|"nomalization form \"NFC\"\n\n"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_1_PARAM
init|=
literal|"Returns the value of $arg normalized according to the "
operator|+
literal|"normalization criteria for a normalization form identified "
operator|+
literal|"by the value of $normalization-form. The effective value of "
operator|+
literal|"the $normalization-form is computed by removing leading and "
operator|+
literal|"trailing blanks, if present, and converting to upper case.\n\n"
operator|+
literal|"If the value of $arg is the empty sequence, returns the zero-length string.\n\n"
operator|+
literal|"See [Character Model for the World Wide Web 1.0: Normalization] "
operator|+
literal|"for a description of the normalization forms.\n\n"
operator|+
literal|"- If the effective value of $normalization-form is \"NFC\", then the value "
operator|+
literal|"returned by the function is the value of $arg in Unicode Normalization Form C (NFC).\n"
operator|+
literal|"- If the effective value of $normalization-form is \"NFD\", then the value "
operator|+
literal|"returned by the function is the value of $arg in Unicode Normalization Form D (NFD).\n"
operator|+
literal|"- If the effective value of $normalization-form is \"NFKC\", then the value "
operator|+
literal|"returned by the function is the value of $arg in Unicode Normalization Form KC (NFKC).\n"
operator|+
literal|"- If the effective value of $normalization-form is \"NFKD\", then the value "
operator|+
literal|"returned by the function is the value of $arg in Unicode Normalization Form KD (NFKD).\n"
operator|+
literal|"- If the effective value of $normalization-form is \"FULLY-NORMALIZED\", then the value "
operator|+
literal|"returned by the function is the value of $arg in the fully normalized form.\n"
operator|+
literal|"- If the effective value of $normalization-form is the zero-length string, "
operator|+
literal|"no normalization is performed and $arg is returned.\n\n"
operator|+
literal|"Conforming implementations must support normalization form \"NFC\" and may "
operator|+
literal|"support normalization forms \"NFD\", \"NFKC\", \"NFKD\", \"FULLY-NORMALIZED\". "
operator|+
literal|"They may also support other normalization forms with implementation-defined semantics. "
operator|+
literal|"If the effective value of the $normalization-form is other than one of the values "
operator|+
literal|"supported by the implementation, then an error is raised [err:FOCH0003]."
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|ARG_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The unicode string to normalize"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|NF_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"normalization-form"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The normalization form"
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
name|ONE
argument_list|,
literal|"the normalized text"
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
literal|"normalize-unicode"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_0_PARAM
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|ARG_PARAM
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
literal|"normalize-unicode"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_1_PARAM
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|ARG_PARAM
block|,
name|NF_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunNormalizeUnicode
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
name|Sequence
name|result
decl_stmt|;
specifier|final
name|Sequence
name|s1
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|s1
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
name|String
name|newNormalizationForm
init|=
literal|"NFC"
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|1
condition|)
block|{
name|newNormalizationForm
operator|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
comment|//TODO : handle the "FULLY-NORMALIZED" string...
if|if
condition|(
name|newNormalizationForm
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|s1
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|Normalizer
operator|.
name|Form
name|form
init|=
name|Normalizer
operator|.
name|Form
operator|.
name|valueOf
argument_list|(
name|newNormalizationForm
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|Normalizer
operator|.
name|normalize
argument_list|(
name|s1
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|form
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
name|FOCH0003
argument_list|,
literal|"Unknown normalization form: "
operator|+
name|newNormalizationForm
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

