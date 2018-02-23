begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|dom
operator|.
name|persistent
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_class
specifier|public
class|class
name|FnHasChildren
extends|extends
name|Function
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|QN_HAS_CHILDREN
init|=
operator|new
name|QName
argument_list|(
literal|"has-children"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_HAS_CHILDREN_0
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_HAS_CHILDREN
argument_list|,
literal|"Returns true if the context item has one or more child nodes"
argument_list|,
name|FunctionSignature
operator|.
name|NO_ARGS
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
name|EXACTLY_ONE
argument_list|,
literal|"true if the context item has one of more child nodes, false otherwise"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_HAS_CHILDREN_1
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_HAS_CHILDREN
argument_list|,
literal|"Returns true if the supplied node has one or more child nodes"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
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
name|ZERO_OR_ONE
argument_list|,
literal|"The node to test"
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
name|EXACTLY_ONE
argument_list|,
literal|"true if $node has one of more child nodes, false otherwise"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FnHasChildren
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
specifier|final
name|NodeValue
name|node
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// default to the context item
if|if
condition|(
name|contextSequence
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|contextItem
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
literal|"Context item is absent"
argument_list|)
throw|;
block|}
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
name|contextSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
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
literal|"Context item is not a node()"
argument_list|)
throw|;
block|}
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|item
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Sequence
name|arg0
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
name|getArgumentCount
argument_list|()
operator|==
literal|1
operator|&&
name|arg0
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
else|else
block|{
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|arg0
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Node
name|w3cNode
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|NodeProxy
condition|)
block|{
name|w3cNode
operator|=
name|node
operator|.
name|getNode
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
condition|)
block|{
name|w3cNode
operator|=
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|node
operator|)
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
literal|"Context item is not a node()"
argument_list|)
throw|;
block|}
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|w3cNode
operator|.
name|hasChildNodes
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

