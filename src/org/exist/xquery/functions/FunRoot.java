begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|StoredNode
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
name|SequenceIterator
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FunRoot
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
literal|"root"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the root of the tree to which the context node belongs. This will usually, "
operator|+
literal|"but not necessarily, be a document node."
argument_list|,
operator|new
name|SequenceType
index|[
literal|0
index|]
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
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
literal|"root"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the root of the tree to which $arg belongs. This will usually, "
operator|+
literal|"but not necessarily, be a document node."
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/**      * @param context      * @param signature      */
specifier|public
name|FunRoot
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
comment|/* (non-Javadoc)          * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)          */
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
name|Sequence
name|seq
decl_stmt|;
name|Sequence
name|result
decl_stmt|;
name|Item
name|item
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
name|contextSequence
operator|==
literal|null
operator|||
name|contextSequence
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
comment|//If we have one argumment, we take it into account
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|>
literal|0
condition|)
name|seq
operator|=
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
expr_stmt|;
comment|//Otherwise, we take the context sequence and we iterate over it
else|else
name|seq
operator|=
name|contextSequence
expr_stmt|;
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|(
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FOTY0011: item is not a node; got '"
operator|+
name|item
operator|+
literal|"'"
argument_list|)
throw|;
name|NodeProxy
name|p
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|item
expr_stmt|;
else|else
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|item
operator|.
name|toSequence
argument_list|()
operator|.
name|toNodeSet
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
name|doc
init|=
name|p
operator|.
name|getDocument
argument_list|()
decl_stmt|;
comment|//Filter out the temporary nodes wrapper element
comment|//WARNING : currently, we assume that *all* the nodes are wrapped in the same document
if|if
condition|(
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|isTempCollection
argument_list|()
condition|)
block|{
name|StoredNode
name|trueRoot
init|=
operator|(
name|StoredNode
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
operator|.
name|getChildNodes
argument_list|()
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|trueRoot
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getDocument
argument_list|()
argument_list|)
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
block|}
end_class

end_unit

