begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|functions
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
operator|.
name|StaticContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|ProcessingInstruction
import|;
end_import

begin_comment
comment|/**  * xpath-library function: string(object)  *  */
end_comment

begin_class
specifier|public
class|class
name|FunName
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
name|BUILTIN_FUNCTION_NS
argument_list|)
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
name|FunName
parameter_list|(
name|StaticContext
name|context
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
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|Node
name|n
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|NodeSet
name|result
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
name|n
operator|=
name|result
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|contextSequence
operator|.
name|getLength
argument_list|()
operator|>
literal|0
operator|&&
name|contextSequence
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|NODE
condition|)
name|n
operator|=
operator|(
operator|(
name|NodeSet
operator|)
name|contextSequence
operator|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
operator|new
name|StringValue
argument_list|(
name|n
operator|.
name|getNodeName
argument_list|()
argument_list|)
return|;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
return|return
operator|new
name|StringValue
argument_list|(
operator|(
operator|(
name|ProcessingInstruction
operator|)
name|n
operator|)
operator|.
name|getTarget
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|StringValue
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|StringValue
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

