begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2011 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|NodeImpl
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
name|ReferenceNode
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
name|ValueComparison
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
name|functions
operator|.
name|array
operator|.
name|ArrayType
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
name|functions
operator|.
name|map
operator|.
name|MapType
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
name|AtomicValue
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
name|NumericValue
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
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
comment|/**  * Implements the fn:deep-equal library function.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|FunDeepEqual
extends|extends
name|CollatingFunction
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
name|FunDeepEqual
operator|.
name|class
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
literal|"deep-equal"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns true() iff every item in $items-1 is deep-equal to the item "
operator|+
literal|"at the same position in $items-2, false() otherwise. "
operator|+
literal|"If both $items-1 and $items-2 are the empty sequence, returns true(). "
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"items-1"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The first item sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"items-2"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The second item sequence"
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
literal|"true() if the sequences are deep-equal, false() otherwise"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"deep-equal"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns true() iff every item in $items-1 is deep-equal to the item "
operator|+
literal|"at the same position in $items-2, false() otherwise. "
operator|+
literal|"If both $items-1 and $items-2 are the empty sequence, returns true(). "
operator|+
literal|"Comparison collation is specified by $collation-uri. "
operator|+
name|THIRD_REL_COLLATION_ARG_EXAMPLE
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"items-1"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The first item sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"items-2"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The second item sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collation-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The collation URI"
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
literal|"true() if the sequences are deep-equal, false() otherwise"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunDeepEqual
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
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator||
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
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
specifier|final
name|Sequence
index|[]
name|args
init|=
name|getArguments
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
specifier|final
name|Collator
name|collator
init|=
name|getCollator
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|deepEqualsSeq
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|,
name|collator
argument_list|)
argument_list|)
decl_stmt|;
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
specifier|public
specifier|static
name|boolean
name|deepEqualsSeq
parameter_list|(
name|Sequence
name|sa
parameter_list|,
name|Sequence
name|sb
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
specifier|final
name|int
name|length
init|=
name|sa
operator|.
name|getItemCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|!=
name|sb
operator|.
name|getItemCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|deepEquals
argument_list|(
name|sa
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|sb
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|collator
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|deepEquals
parameter_list|(
name|Item
name|a
parameter_list|,
name|Item
name|b
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|a
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
operator|||
name|b
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
if|if
condition|(
name|a
operator|.
name|getType
argument_list|()
operator|!=
name|b
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|ArrayType
name|ar
init|=
operator|(
name|ArrayType
operator|)
name|a
decl_stmt|;
specifier|final
name|ArrayType
name|br
init|=
operator|(
name|ArrayType
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|ar
operator|.
name|getSize
argument_list|()
operator|!=
name|br
operator|.
name|getSize
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ar
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|deepEqualsSeq
argument_list|(
name|ar
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|br
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|collator
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|a
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|MAP
operator|||
name|b
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|MAP
condition|)
block|{
if|if
condition|(
name|a
operator|.
name|getType
argument_list|()
operator|!=
name|b
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|MapType
name|amap
init|=
operator|(
name|MapType
operator|)
name|a
decl_stmt|;
specifier|final
name|MapType
name|bmap
init|=
operator|(
name|MapType
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|amap
operator|.
name|size
argument_list|()
operator|!=
name|bmap
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|aentry
range|:
name|amap
control|)
block|{
if|if
condition|(
operator|!
name|bmap
operator|.
name|contains
argument_list|(
name|aentry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|deepEqualsSeq
argument_list|(
name|aentry
operator|.
name|getValue
argument_list|()
argument_list|,
name|bmap
operator|.
name|get
argument_list|(
name|aentry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|collator
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|final
name|boolean
name|aAtomic
init|=
name|Type
operator|.
name|subTypeOf
argument_list|(
name|a
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|bAtomic
init|=
name|Type
operator|.
name|subTypeOf
argument_list|(
name|b
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
decl_stmt|;
if|if
condition|(
name|aAtomic
operator|||
name|bAtomic
condition|)
block|{
if|if
condition|(
operator|!
name|aAtomic
operator|||
operator|!
name|bAtomic
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
specifier|final
name|AtomicValue
name|av
init|=
operator|(
name|AtomicValue
operator|)
name|a
decl_stmt|;
specifier|final
name|AtomicValue
name|bv
init|=
operator|(
name|AtomicValue
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|av
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|bv
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
comment|//or if both values are NaN
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|a
operator|)
operator|.
name|isNaN
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|b
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|ValueComparison
operator|.
name|compareAtomic
argument_list|(
name|collator
argument_list|,
name|av
argument_list|,
name|bv
argument_list|,
name|Constants
operator|.
name|TRUNC_NONE
argument_list|,
name|Constants
operator|.
name|EQ
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|a
operator|.
name|getType
argument_list|()
operator|!=
name|b
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeValue
name|nva
init|=
operator|(
name|NodeValue
operator|)
name|a
decl_stmt|,
name|nvb
init|=
operator|(
name|NodeValue
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|nva
operator|==
name|nvb
condition|)
block|{
return|return
literal|true
return|;
block|}
try|try
block|{
comment|//Don't use this shortcut for in-memory nodes
comment|//since the symbol table is ignored.
if|if
condition|(
name|nva
operator|.
name|getImplementationType
argument_list|()
operator|!=
name|NodeValue
operator|.
name|IN_MEMORY_NODE
operator|&&
name|nva
operator|.
name|equals
argument_list|(
name|nvb
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// shortcut!
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
comment|// apparently incompatible values, do manual comparison
block|}
name|Node
name|na
decl_stmt|,
name|nb
decl_stmt|;
switch|switch
condition|(
name|a
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|DOCUMENT
case|:
comment|// NodeValue.getNode() doesn't seem to work for document nodes
name|na
operator|=
name|nva
operator|instanceof
name|Node
condition|?
operator|(
name|Node
operator|)
name|nva
else|:
operator|(
operator|(
name|NodeProxy
operator|)
name|nva
operator|)
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|nb
operator|=
name|nvb
operator|instanceof
name|Node
condition|?
operator|(
name|Node
operator|)
name|nvb
else|:
operator|(
operator|(
name|NodeProxy
operator|)
name|nvb
operator|)
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
return|return
name|compareContents
argument_list|(
name|na
argument_list|,
name|nb
argument_list|)
return|;
case|case
name|Type
operator|.
name|ELEMENT
case|:
name|na
operator|=
name|nva
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|nb
operator|=
name|nvb
operator|.
name|getNode
argument_list|()
expr_stmt|;
return|return
name|compareElements
argument_list|(
name|na
argument_list|,
name|nb
argument_list|)
return|;
case|case
name|Type
operator|.
name|ATTRIBUTE
case|:
name|na
operator|=
name|nva
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|nb
operator|=
name|nvb
operator|.
name|getNode
argument_list|()
expr_stmt|;
return|return
name|compareNames
argument_list|(
name|na
argument_list|,
name|nb
argument_list|)
operator|&&
name|safeEquals
argument_list|(
name|na
operator|.
name|getNodeValue
argument_list|()
argument_list|,
name|nb
operator|.
name|getNodeValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|PROCESSING_INSTRUCTION
case|:
case|case
name|Type
operator|.
name|NAMESPACE
case|:
name|na
operator|=
name|nva
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|nb
operator|=
name|nvb
operator|.
name|getNode
argument_list|()
expr_stmt|;
return|return
name|safeEquals
argument_list|(
name|na
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|nb
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|&&
name|safeEquals
argument_list|(
name|nva
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|nvb
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|TEXT
case|:
case|case
name|Type
operator|.
name|COMMENT
case|:
return|return
name|safeEquals
argument_list|(
name|nva
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|nvb
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected item type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|a
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|compareElements
parameter_list|(
name|Node
name|a
parameter_list|,
name|Node
name|b
parameter_list|)
block|{
return|return
name|compareNames
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
operator|&&
name|compareAttributes
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
operator|&&
name|compareContents
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|compareContents
parameter_list|(
name|Node
name|a
parameter_list|,
name|Node
name|b
parameter_list|)
block|{
name|a
operator|=
name|findNextTextOrElementNode
argument_list|(
name|a
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|=
name|findNextTextOrElementNode
argument_list|(
name|b
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
operator|(
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
operator|)
condition|)
block|{
specifier|final
name|int
name|nodeTypeA
init|=
name|getEffectiveNodeType
argument_list|(
name|a
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nodeTypeB
init|=
name|getEffectiveNodeType
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeTypeA
operator|!=
name|nodeTypeB
condition|)
block|{
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|nodeTypeA
condition|)
block|{
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
if|if
condition|(
name|a
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
operator|&&
name|b
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
if|if
condition|(
operator|!
name|safeEquals
argument_list|(
operator|(
operator|(
name|ReferenceNode
operator|)
name|a
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|,
operator|(
operator|(
name|ReferenceNode
operator|)
name|b
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|else if
condition|(
name|a
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
if|if
condition|(
operator|!
name|safeEquals
argument_list|(
operator|(
operator|(
name|ReferenceNode
operator|)
name|a
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|,
name|b
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|else if
condition|(
name|b
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
if|if
condition|(
operator|!
name|safeEquals
argument_list|(
name|a
operator|.
name|getNodeValue
argument_list|()
argument_list|,
operator|(
operator|(
name|ReferenceNode
operator|)
name|b
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|safeEquals
argument_list|(
name|a
operator|.
name|getNodeValue
argument_list|()
argument_list|,
name|b
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
break|break;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
if|if
condition|(
operator|!
name|compareElements
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected node type "
operator|+
name|nodeTypeA
argument_list|)
throw|;
block|}
name|a
operator|=
name|findNextTextOrElementNode
argument_list|(
name|a
operator|.
name|getNextSibling
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|=
name|findNextTextOrElementNode
argument_list|(
name|b
operator|.
name|getNextSibling
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|a
operator|==
name|b
return|;
comment|// both null
block|}
specifier|private
specifier|static
name|Node
name|findNextTextOrElementNode
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|nodeType
init|=
name|getEffectiveNodeType
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeType
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|||
name|nodeType
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
return|return
name|n
return|;
block|}
name|n
operator|=
name|n
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|int
name|getEffectiveNodeType
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|int
name|nodeType
init|=
name|n
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeType
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
name|nodeType
operator|=
operator|(
operator|(
name|ReferenceNode
operator|)
name|n
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeType
return|;
block|}
specifier|private
specifier|static
name|boolean
name|compareAttributes
parameter_list|(
name|Node
name|a
parameter_list|,
name|Node
name|b
parameter_list|)
block|{
specifier|final
name|NamedNodeMap
name|nnma
init|=
name|a
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
specifier|final
name|NamedNodeMap
name|nnmb
init|=
name|b
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|getAttrCount
argument_list|(
name|nnma
argument_list|)
operator|!=
name|getAttrCount
argument_list|(
name|nnmb
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nnma
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|ta
init|=
name|nnma
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Namespaces
operator|.
name|XMLNS_NS
operator|.
name|equals
argument_list|(
name|ta
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|Node
name|tb
init|=
name|ta
operator|.
name|getLocalName
argument_list|()
operator|==
literal|null
condition|?
name|nnmb
operator|.
name|getNamedItem
argument_list|(
name|ta
operator|.
name|getNodeName
argument_list|()
argument_list|)
else|:
name|nnmb
operator|.
name|getNamedItemNS
argument_list|(
name|ta
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|ta
operator|.
name|getLocalName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tb
operator|==
literal|null
operator|||
operator|!
name|safeEquals
argument_list|(
name|ta
operator|.
name|getNodeValue
argument_list|()
argument_list|,
name|tb
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Return the number of real attributes in the map. Filter out      * xmlns namespace attributes.      */
specifier|private
specifier|static
name|int
name|getAttrCount
parameter_list|(
name|NamedNodeMap
name|nnm
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nnm
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|n
init|=
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Namespaces
operator|.
name|XMLNS_NS
operator|.
name|equals
argument_list|(
name|n
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
specifier|private
specifier|static
name|boolean
name|compareNames
parameter_list|(
name|Node
name|a
parameter_list|,
name|Node
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|getLocalName
argument_list|()
operator|!=
literal|null
operator|||
name|b
operator|.
name|getLocalName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|safeEquals
argument_list|(
name|a
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|b
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|safeEquals
argument_list|(
name|a
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|b
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
return|return
name|safeEquals
argument_list|(
name|a
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|b
operator|.
name|getNodeName
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|safeEquals
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|a
operator|==
literal|null
condition|?
name|b
operator|==
literal|null
else|:
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
end_class

end_unit

