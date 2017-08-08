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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
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
name|dom
operator|.
name|persistent
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
name|dom
operator|.
name|memtree
operator|.
name|DocumentImpl
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
name|memtree
operator|.
name|NodeImpl
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
name|Constants
operator|.
name|Comparison
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
name|StringTokenizer
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

begin_comment
comment|/**  *  * @author wolf  * @author perig  *  */
end_comment

begin_class
specifier|public
class|class
name|FunId
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
name|FunId
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
literal|"id"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the sequence of element nodes that have an ID value "
operator|+
literal|"matching the value of one or more of the IDREF values supplied in $idrefs. "
operator|+
literal|"If none is matching or $idrefs is the empty sequence, returns the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"idrefs"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The IDREF sequence"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the elements with IDs  matching IDREFs from $idref-sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the sequence of element nodes that have an ID value "
operator|+
literal|"matching the value of one or more of the IDREF values supplied in $idrefs and is in the same document as $node-in-document. "
operator|+
literal|"If none is matching or $idrefs is the empty sequence, returns the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"idrefs"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The IDREF sequence"
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
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the elements with IDs matching IDREFs from $idrefs in the same document as $node-in-document"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * Constructor for FunId. 	 */
specifier|public
name|FunId
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
name|getArgumentCount
argument_list|()
operator|<
literal|1
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"function id requires one argument"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0017
argument_list|,
literal|"function id requires one argument"
argument_list|)
throw|;
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
name|boolean
name|processInMem
init|=
literal|false
decl_stmt|;
specifier|final
name|Expression
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|idval
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
name|idval
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|getArgumentCount
argument_list|()
operator|==
literal|1
operator|&&
name|contextSequence
operator|!=
literal|null
operator|&&
name|contextSequence
operator|.
name|isEmpty
argument_list|()
operator|)
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
specifier|final
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
block|{
name|logger
operator|.
name|error
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
operator|+
literal|" No node or context item for fn:id"
argument_list|)
expr_stmt|;
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
literal|"XPDY0002: no node or context item for fn:id"
argument_list|,
name|nodes
argument_list|)
throw|;
block|}
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
block|{
name|logger
operator|.
name|error
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
operator|+
literal|" fn:id() argument is not a node"
argument_list|)
expr_stmt|;
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
literal|"XPTY0004: fn:id() argument is not a node"
argument_list|,
name|nodes
argument_list|)
throw|;
block|}
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
block|{
name|processInMem
operator|=
literal|true
expr_stmt|;
block|}
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
name|getOwnerDocument
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
block|{
name|logger
operator|.
name|error
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
operator|+
literal|" No context item specified"
argument_list|)
expr_stmt|;
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
literal|"No context item specified"
argument_list|)
throw|;
block|}
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
block|{
name|logger
operator|.
name|error
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
operator|+
literal|" Context item is not a node"
argument_list|)
expr_stmt|;
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
argument_list|,
name|contextSequence
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
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
block|}
else|else
block|{
name|processInMem
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|processInMem
condition|)
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|idval
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
block|{
continue|continue;
block|}
if|if
condition|(
name|nextId
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
comment|// parse idrefs
specifier|final
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|nextId
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|nextId
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
block|{
name|getId
argument_list|(
name|result
argument_list|,
name|contextSequence
argument_list|,
name|nextId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getId
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
block|}
else|else
block|{
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
block|{
name|getId
argument_list|(
name|result
argument_list|,
name|contextSequence
argument_list|,
name|nextId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getId
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
specifier|private
name|void
name|getId
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
specifier|final
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
name|context
operator|.
name|getWatchDog
argument_list|()
argument_list|,
name|Comparison
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
name|ID
argument_list|)
argument_list|)
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
specifier|final
name|NodeProxy
name|n
range|:
name|attribs
control|)
block|{
name|p
operator|=
operator|new
name|NodeProxy
argument_list|(
name|n
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getNodeId
argument_list|()
operator|.
name|getParentId
argument_list|()
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|getId
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
specifier|final
name|Set
argument_list|<
name|DocumentImpl
argument_list|>
name|visitedDocs
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
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
specifier|final
name|NodeImpl
name|v
init|=
operator|(
name|NodeImpl
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|v
operator|.
name|getOwnerDocument
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
specifier|final
name|NodeImpl
name|elem
init|=
name|doc
operator|.
name|selectById
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|elem
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|elem
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
comment|// fn:id can operate on the entire context sequence at once - unless the
comment|// argument depends on the context item
return|return
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
block|}
block|}
end_class

end_unit

