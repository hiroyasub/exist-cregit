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
name|persistent
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
name|security
operator|.
name|PermissionDeniedException
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
name|AnalyzeContextInfo
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
name|CompiledXQuery
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Built-in function fn:lang().  *  */
end_comment

begin_class
specifier|public
class|class
name|FunLang
extends|extends
name|Function
block|{
specifier|public
specifier|static
name|String
name|queryString
init|=
literal|"(ancestor-or-self::*/@xml:lang)[position() = last()]"
decl_stmt|;
specifier|public
name|CompiledXQuery
name|query
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_1_PARAM
init|=
literal|"Tests whether the language of the context item "
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_2_PARAMS
init|=
literal|"Tests whether the language of $node "
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION_BOTH
init|=
literal|"as specified by xml:lang attributes is the "
operator|+
literal|"same as, or is a sublanguage of, the language specified by $lang. The "
operator|+
literal|"behavior of the function if the second argument is omitted is exactly the "
operator|+
literal|"same as if the context item (.) had been passed as the second argument. The "
operator|+
literal|"language of the argument node, or the context item if the second argument is "
operator|+
literal|"omitted, is determined by the value of the xml:lang attribute on the node, "
operator|+
literal|"or, if the node has no such attribute, by the value of the xml:lang attribute "
operator|+
literal|"on the nearest ancestor of the node that has an xml:lang attribute. If there "
operator|+
literal|"is no such ancestor, then the function returns false().\n\n"
operator|+
literal|"The following errors may be raised: if the context item is undefined [err:XPDY0002]XP; "
operator|+
literal|"if the context item is not a node [err:XPTY0004]XP.\n\n"
operator|+
literal|"If $lang is the empty sequence it is interpreted as the zero-length string."
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
literal|"lang"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_1_PARAM
operator|+
name|FUNCTION_DESCRIPTION_BOTH
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"lang"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The language code"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"true if the language code matches, false otherwise"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"lang"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION_2_PARAMS
operator|+
name|FUNCTION_DESCRIPTION_BOTH
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"lang"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The language code"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The node"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"true if the language code matches, false otherwise"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunLang
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
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
try|try
block|{
name|query
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXQueryService
argument_list|()
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|contextSequence
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
expr_stmt|;
block|}
if|if
condition|(
name|contextSequence
operator|==
literal|null
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
name|XPDY0002
argument_list|,
literal|"Undefined context item"
argument_list|)
throw|;
block|}
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
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
literal|"Context item is not a node"
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|String
name|lang
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
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|langValue
init|=
literal|null
decl_stmt|;
name|Sequence
name|seq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|.
name|hasOne
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|contextSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
condition|)
block|{
name|langValue
operator|=
name|item
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|seq
operator|=
name|query
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seq
operator|!=
literal|null
operator|&&
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
if|else if
condition|(
name|langValue
operator|!=
literal|null
operator|||
operator|(
name|seq
operator|!=
literal|null
operator|&&
name|seq
operator|.
name|hasOne
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|seq
operator|!=
literal|null
condition|)
block|{
name|langValue
operator|=
name|seq
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
name|boolean
name|include
init|=
name|lang
operator|.
name|equalsIgnoreCase
argument_list|(
name|langValue
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|include
condition|)
block|{
specifier|final
name|int
name|hyphen
init|=
name|langValue
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
if|if
condition|(
name|hyphen
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|langValue
operator|=
name|langValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hyphen
argument_list|)
expr_stmt|;
name|include
operator|=
name|lang
operator|.
name|equalsIgnoreCase
argument_list|(
name|langValue
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|=
operator|new
name|BooleanValue
argument_list|(
name|include
argument_list|)
expr_stmt|;
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
name|XPTY0004
argument_list|,
literal|"Sequence returned more than one item !"
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

