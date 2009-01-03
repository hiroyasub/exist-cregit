begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2008 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ElementImpl
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
name|xquery
operator|.
name|BasicFunction
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
name|AnyURIValue
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

begin_class
specifier|public
class|class
name|FunNamespaceURIForPrefix
extends|extends
name|BasicFunction
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
literal|"namespace-uri-for-prefix"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the namespace URI of one of the in-scope namespaces for $b, identified by its namespace prefix. "
operator|+
literal|"If $b has an in-scope namespace whose namespace prefix is equal to $a, it returns the namespace "
operator|+
literal|"URI of that namespace. If $b is the zero-length string or the empty sequence, "
operator|+
literal|"it returns the namespace URI of the default (unnamed) namespace. Otherwise, "
operator|+
literal|"it returns the empty sequence."
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunNamespaceURIForPrefix
parameter_list|(
name|XQueryContext
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
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
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
block|}
name|String
name|prefix
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|prefix
operator|=
literal|""
expr_stmt|;
else|else
name|prefix
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|String
name|namespace
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
name|namespace
operator|=
name|Namespaces
operator|.
name|XML_NS
expr_stmt|;
block|}
else|else
block|{
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Map
name|prefixes
init|=
operator|new
name|HashMap
argument_list|()
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
name|PERSISTENT_NODE
condition|)
block|{
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|node
decl_stmt|;
name|NodeSet
name|ancestors
init|=
name|proxy
operator|.
name|getAncestors
argument_list|(
name|contextId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|ancestors
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|proxy
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|FunInScopePrefixes
operator|.
name|collectNamespacePrefixes
argument_list|(
operator|(
name|ElementImpl
operator|)
name|proxy
operator|.
name|getNode
argument_list|()
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// In-memory node
name|NodeImpl
name|next
init|=
operator|(
name|NodeImpl
operator|)
name|node
decl_stmt|;
do|do
block|{
name|FunInScopePrefixes
operator|.
name|collectNamespacePrefixes
argument_list|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
operator|)
name|next
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|next
operator|=
operator|(
name|NodeImpl
operator|)
name|next
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
do|;
block|}
name|namespace
operator|=
operator|(
name|String
operator|)
name|prefixes
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|namespace
operator|==
literal|null
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
name|result
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|namespace
argument_list|)
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
block|}
end_class

end_unit

