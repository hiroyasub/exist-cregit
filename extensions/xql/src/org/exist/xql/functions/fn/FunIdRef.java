begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|DefaultDocumentSet
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
name|MutableDocumentSet
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
name|util
operator|.
name|XMLChar
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  *  * @author perig  * @author piotr kaminski  *  */
end_comment

begin_class
specifier|public
class|class
name|FunIdRef
extends|extends
name|Function
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FunIdRef
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"idref"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the sequence of element or attributes nodes with an IDREF value matching the "
operator|+
literal|"value of one or more of the ID values supplied in $ids. "
operator|+
literal|"If none is matching or $ids is the empty sequence, returns the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"ids"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The ID sequence"
argument_list|)
block|,             }
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
literal|"the elements with matching IDREF values from IDs in $ids"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"idref"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the sequence of element or attributes nodes with an IDREF value matching the "
operator|+
literal|"value of one or more of the ID values supplied in $ids. "
operator|+
literal|"If none is matching or $ids is the empty sequence, returns the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"ids"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The ID sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-in-document"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The node in document"
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
literal|"the elements with matching IDREF values from IDs in $ids in the same document as $node-in-document"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * Constructor for FunId. 	 */
specifier|public
name|FunIdRef
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
comment|/** 	 * @see org.exist.xquery.Expression#eval(Sequence, Item) 	 */
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
name|getArgumentCount
argument_list|()
operator|<
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"function id requires one argument"
argument_list|)
throw|;
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
name|boolean
name|processInMem
init|=
literal|false
decl_stmt|;
name|Expression
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Sequence
name|idrefval
init|=
name|arg
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|idrefval
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
else|else
block|{
name|String
name|nextId
decl_stmt|;
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
comment|// second argument should be a node, whose owner document will be
comment|// searched for the id
name|Sequence
name|nodes
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPDY0002: no node or context item for fn:idref"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|nodes
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
name|this
argument_list|,
literal|"XPTY0004: fn:idref() argument is not a node"
argument_list|)
throw|;
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|nodes
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
comment|//TODO : how to enforce this ?
comment|//If $node, or the context item if the second argument is omitted,
comment|//is a node in a tree whose root is not a document node [err:FODC0001] is raised                    processInMem = true;
name|processInMem
operator|=
literal|true
expr_stmt|;
else|else
block|{
name|MutableDocumentSet
name|ndocs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|ndocs
operator|.
name|add
argument_list|(
operator|(
operator|(
name|NodeProxy
operator|)
name|node
operator|)
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|=
name|ndocs
expr_stmt|;
block|}
name|contextSequence
operator|=
name|node
expr_stmt|;
block|}
if|else if
condition|(
name|contextSequence
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPDY0002: no context item specified"
argument_list|)
throw|;
if|else if
condition|(
operator|!
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
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: context item is not a node"
argument_list|)
throw|;
else|else
block|{
if|if
condition|(
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
name|docs
operator|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
else|else
name|processInMem
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|processInMem
condition|)
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
else|else
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|idrefval
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|nextId
operator|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextId
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
if|if
condition|(
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|nextId
argument_list|)
condition|)
block|{
if|if
condition|(
name|processInMem
condition|)
name|getIdRef
argument_list|(
name|result
argument_list|,
name|contextSequence
argument_list|,
name|nextId
argument_list|)
expr_stmt|;
else|else
name|getIdRef
argument_list|(
operator|(
name|NodeSet
operator|)
name|result
argument_list|,
name|docs
argument_list|,
name|nextId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
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
specifier|private
name|void
name|getIdRef
parameter_list|(
name|NodeSet
name|result
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeSet
name|attribs
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getValueIndex
argument_list|()
operator|.
name|find
argument_list|(
name|Constants
operator|.
name|EQ
argument_list|,
name|docs
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
operator|new
name|StringValue
argument_list|(
name|id
argument_list|,
name|Type
operator|.
name|IDREF
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeProxy
name|n
range|:
name|attribs
control|)
block|{
name|n
operator|.
name|setNodeType
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|getIdRef
parameter_list|(
name|Sequence
name|result
parameter_list|,
name|Sequence
name|seq
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|XPathException
block|{
name|Set
argument_list|<
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
argument_list|>
name|visitedDocs
init|=
operator|new
name|TreeSet
argument_list|<
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
argument_list|>
argument_list|()
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
control|)
block|{
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
name|v
init|=
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
name|doc
init|=
name|v
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|visitedDocs
operator|.
name|contains
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
name|node
init|=
name|doc
operator|.
name|selectByIdref
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
name|result
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|visitedDocs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

