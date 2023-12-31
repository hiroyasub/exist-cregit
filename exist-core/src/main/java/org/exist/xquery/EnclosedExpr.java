begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|MemTreeBuilder
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
name|TextImpl
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
name|util
operator|.
name|ExpressionDumper
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
name|DOMException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Represents an enclosed expression<code>{expr}</code> inside element  * content. Enclosed expressions within attribute values are processed by  * {@link org.exist.xquery.AttributeConstructor}.  *    * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
class|class
name|EnclosedExpr
extends|extends
name|PathExpr
block|{
specifier|public
name|EnclosedExpr
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|removeFlag
argument_list|(
name|IN_NODE_CONSTRUCTOR
argument_list|)
expr_stmt|;
name|super
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#eval(org.exist.xquery.StaticContext,      * org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence)      */
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
comment|// evaluate the expression
name|Sequence
name|result
decl_stmt|;
name|context
operator|.
name|enterEnclosedExpr
argument_list|()
expr_stmt|;
try|try
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
name|result
operator|=
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
comment|// create the output
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|setCheckNS
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
comment|// flatten all arrays in the input sequence
name|result
operator|=
name|ArrayType
operator|.
name|flatten
argument_list|(
name|result
argument_list|)
expr_stmt|;
specifier|final
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|StringBuilder
name|buf
init|=
literal|null
decl_stmt|;
name|boolean
name|allowAttribs
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XQTY0105
argument_list|,
literal|"Enclosed expression contains function item"
argument_list|)
throw|;
comment|// if item is an atomic value, collect the string values of all
comment|// following atomic values and separate them by a space.
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|allowAttribs
operator|=
literal|false
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
comment|//If the item is a node, flush any collected character data and
comment|//copy the node to the target doc.
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
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
comment|//It is possible for a text node constructor to construct a text node containing a zero-length string.
comment|//However, if used in the content of a constructed element or document node,
comment|//such a text node will be deleted or merged with another text node.
if|if
condition|(
name|next
operator|instanceof
name|TextImpl
operator|&&
operator|(
operator|(
name|TextImpl
operator|)
name|next
operator|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
operator|&&
operator|!
name|allowAttribs
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
name|XQTY0024
argument_list|,
literal|"An attribute may not appear after another child node."
argument_list|)
throw|;
block|}
try|try
block|{
name|receiver
operator|.
name|setCheckNS
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|setCheckNS
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DOMException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|code
operator|==
name|DOMException
operator|.
name|NAMESPACE_ERR
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
name|XQDY0102
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|allowAttribs
operator|=
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
operator|||
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NAMESPACE
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
block|}
comment|// flush remaining character data
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"SAXException during serialization: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|context
operator|.
name|exitEnclosedExpr
argument_list|()
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|super
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{"
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitPathExpr
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowMixedNodesInReturn
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|simplify
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

