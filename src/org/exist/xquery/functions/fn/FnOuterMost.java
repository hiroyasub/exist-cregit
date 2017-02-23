begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001- 2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|numbering
operator|.
name|NodeId
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_class
specifier|public
class|class
name|FnOuterMost
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_OUTERMOST
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"outermost"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns every node within the input sequence that has no ancestor that is itself a member of the input sequence; the nodes are returned in document order with duplicates eliminated."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The nodes to test"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The nodes that have no ancestor which is itself in the input sequence"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FnOuterMost
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
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|nodes
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|else if
condition|(
name|nodes
operator|.
name|hasOne
argument_list|()
condition|)
block|{
return|return
name|nodes
return|;
block|}
else|else
block|{
specifier|final
name|Sequence
name|results
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
init|=
name|getNodeIds
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|NodeId
argument_list|>
name|found
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|it
init|=
name|nodes
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|it
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|NodeValue
name|node
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
decl_stmt|;
specifier|final
name|NodeId
name|currentNodeId
init|=
name|node
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|found
operator|.
name|contains
argument_list|(
name|currentNodeId
argument_list|)
operator|&&
name|nodeIds
operator|.
name|parallelStream
argument_list|()
operator|.
name|noneMatch
argument_list|(
name|nodeId
lambda|->
name|currentNodeId
operator|.
name|isDescendantOf
argument_list|(
name|nodeId
argument_list|)
argument_list|)
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|node
argument_list|)
block|;
name|found
operator|.
name|add
argument_list|(
name|currentNodeId
argument_list|)
empty_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
block|}
end_class

begin_function
specifier|private
name|List
argument_list|<
name|NodeId
argument_list|>
name|getNodeIds
parameter_list|(
specifier|final
name|Sequence
name|nodes
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|it
init|=
name|nodes
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|it
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|NodeValue
name|node
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
decl_stmt|;
name|nodeIds
operator|.
name|add
argument_list|(
name|node
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeIds
return|;
block|}
end_function

unit|}
end_unit

