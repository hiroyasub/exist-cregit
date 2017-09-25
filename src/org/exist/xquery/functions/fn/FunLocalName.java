begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-2009,  the eXist team  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.   *  * $Id$  */
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
name|NodeValue
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Built-in function fn:local-name().  */
end_comment

begin_class
specifier|public
class|class
name|FunLocalName
extends|extends
name|Function
block|{
specifier|private
specifier|static
specifier|final
name|String
name|FUNCTION_DESCRIPTION
init|=
literal|"Returns the local part of the name of $arg as an xs:string that "
operator|+
literal|"will either be the zero-length string or will have the lexical form of an xs:NCName.\n\n"
operator|+
literal|"If the argument is omitted, it defaults to the context item (.). "
operator|+
literal|"The behavior of the function if the argument is omitted is exactly "
operator|+
literal|"the same as if the context item had been passed as the argument.\n\n"
operator|+
literal|"The following errors may be raised: if the context item is undefined "
operator|+
literal|"[err:XPDY0002]XP; if the context item is not a node [err:XPTY0004]XP.\n\n"
operator|+
literal|"If the argument is supplied and is the empty sequence, the function "
operator|+
literal|"returns the zero-length string.\n\n"
operator|+
literal|"If the target node has no name (that is, if it is a document node, a "
operator|+
literal|"comment, or a text node), the function returns the zero-length string.\n\n"
operator|+
literal|"Otherwise, the value returned will be the local part of the expanded-QName "
operator|+
literal|"of the target node (as determined by the dm:node-name accessor in Section "
operator|+
literal|"5.11 node-name AccessorDM. This will be an xs:string whose lexical form is an xs:NCName."
decl_stmt|;
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
literal|"local-name"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
name|FUNCTION_DESCRIPTION
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the local name"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"local-name"
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
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The node to retrieve the local name from"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the local name"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunLocalName
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
specifier|final
name|Item
name|item
decl_stmt|;
comment|// check if the node is passed as an argument or should be taken from
comment|// the context sequence
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
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
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|item
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
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
name|item
operator|=
name|contextSequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|item
operator|==
literal|null
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
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
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
literal|"item is not a node; got '"
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
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|//TODO : how to improve performance ?
specifier|final
name|Node
name|n
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
specifier|final
name|String
name|localName
init|=
name|n
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|localName
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|localName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|StringValue
operator|.
name|EMPTY_STRING
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

