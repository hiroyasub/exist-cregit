begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-06 Wolfgang M. Meier  * meier@ifs.tu-darmstadt.de  * http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|Iterator
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
name|dom
operator|.
name|XMLUtil
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
literal|"lang"
argument_list|,
name|Function
operator|.
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
name|STRING
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunLang
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
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
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
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
literal|"lang"
argument_list|,
name|context
operator|.
name|getURIForPrefix
argument_list|(
literal|"xml"
argument_list|)
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
name|NodeSet
name|attribs
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getElementIndex
argument_list|()
operator|.
name|getAttributesByName
argument_list|(
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|qname
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeSet
name|temp
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|String
name|langValue
decl_stmt|;
name|int
name|hyphen
decl_stmt|;
name|boolean
name|include
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|attribs
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
name|include
operator|=
literal|false
expr_stmt|;
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|langValue
operator|=
name|p
operator|.
name|getNodeValue
argument_list|()
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
if|if
condition|(
operator|!
name|include
condition|)
block|{
name|hyphen
operator|=
name|langValue
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|include
condition|)
block|{
name|long
name|parentID
init|=
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentID
operator|!=
name|NodeProxy
operator|.
name|DOCUMENT_NODE_GID
condition|)
block|{
name|NodeProxy
name|parent
init|=
operator|new
name|NodeProxy
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|,
name|parentID
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
decl_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|temp
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|=
operator|(
operator|(
name|NodeSet
operator|)
name|contextSequence
operator|)
operator|.
name|selectAncestorDescendant
argument_list|(
name|temp
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|NodeSet
operator|)
name|result
operator|)
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|.
name|addContextNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
block|}
end_class

end_unit

