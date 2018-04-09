begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|QNameValue
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

begin_comment
comment|/**  * Represents a dynamic attribute constructor. The implementation differs from  * AttributeConstructor as the evaluation is not controlled by the surrounding   * element. The attribute name as well as its value are only determined at evaluation time,  * not at compile time.  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DynamicAttributeConstructor
extends|extends
name|NodeConstructor
block|{
specifier|private
name|Expression
name|qnameExpr
decl_stmt|;
specifier|private
name|Expression
name|valueExpr
decl_stmt|;
specifier|private
name|boolean
name|replaceAttribute
init|=
literal|false
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|DynamicAttributeConstructor
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
name|setNameExpr
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|qnameExpr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Expression
name|getNameExpr
parameter_list|()
block|{
return|return
name|this
operator|.
name|qnameExpr
return|;
block|}
specifier|public
name|void
name|setContentExpr
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|valueExpr
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|Expression
name|getContentExpr
parameter_list|()
block|{
return|return
name|this
operator|.
name|valueExpr
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|qnameExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|valueExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
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
name|newDocumentContext
condition|)
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
block|}
name|NodeImpl
name|node
decl_stmt|;
try|try
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setReplaceAttributeFlag
argument_list|(
name|replaceAttribute
argument_list|)
expr_stmt|;
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|nameSeq
init|=
name|qnameExpr
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
operator|!
name|nameSeq
operator|.
name|hasOne
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"The name expression should evaluate to a single value"
argument_list|)
throw|;
block|}
specifier|final
name|Item
name|qnItem
init|=
name|nameSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QName
name|qn
decl_stmt|;
if|if
condition|(
name|qnItem
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|QNAME
condition|)
block|{
name|qn
operator|=
operator|(
operator|(
name|QNameValue
operator|)
name|qnItem
operator|)
operator|.
name|getQName
argument_list|()
expr_stmt|;
block|}
else|else
try|try
block|{
name|qn
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|nameSeq
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
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
literal|"'"
operator|+
name|nameSeq
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"' is not a valid attribute name"
argument_list|)
throw|;
block|}
comment|//Not in the specs but... makes sense
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidName
argument_list|(
name|qn
operator|.
name|getLocalPart
argument_list|()
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
literal|"'"
operator|+
name|qn
operator|.
name|getLocalPart
argument_list|()
operator|+
literal|"' is not a valid attribute name"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"xmlns"
operator|.
name|equals
argument_list|(
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|)
operator|&&
name|qn
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|isEmpty
argument_list|()
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
name|XQDY0044
argument_list|,
literal|"'"
operator|+
name|qn
operator|.
name|getLocalPart
argument_list|()
operator|+
literal|"' is not a valid attribute name"
argument_list|)
throw|;
block|}
name|String
name|value
decl_stmt|;
specifier|final
name|Sequence
name|valueSeq
init|=
name|valueExpr
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
name|valueSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|value
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|Atomize
operator|.
name|atomize
argument_list|(
name|valueSeq
argument_list|)
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
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
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
block|}
name|value
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|value
operator|=
name|DynamicAttributeConstructor
operator|.
name|normalize
argument_list|(
name|this
argument_list|,
name|qn
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|node
operator|=
literal|null
expr_stmt|;
try|try
block|{
specifier|final
name|int
name|nodeNr
init|=
name|builder
operator|.
name|addAttribute
argument_list|(
name|qn
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|node
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DOMException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0025
argument_list|,
literal|"element has more than one attribute '"
operator|+
name|qn
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|newDocumentContext
condition|)
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
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
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
specifier|public
specifier|static
name|String
name|normalize
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|QName
name|qn
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//normalize xml:id
if|if
condition|(
name|qn
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XML_ID_QNAME
argument_list|)
condition|)
block|{
name|value
operator|=
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|StringValue
operator|.
name|collapseWhitespace
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
literal|"attribute "
argument_list|)
expr_stmt|;
comment|//TODO : remove curly braces if Qname
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|qnameExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
comment|//TODO : handle empty value
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
name|valueExpr
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
comment|//TODO : remove curly braces if Qname
comment|//TODO : handle empty value
return|return
literal|"attribute "
operator|+
literal|"{"
operator|+
name|qnameExpr
operator|.
name|toString
argument_list|()
operator|+
literal|"} {"
operator|+
name|valueExpr
operator|.
name|toString
argument_list|()
operator|+
literal|"} "
return|;
block|}
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|qnameExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|valueExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
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
name|visitAttribConstructor
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setReplaceAttributeFlag
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|replaceAttribute
operator|=
name|flag
expr_stmt|;
block|}
block|}
end_class

end_unit

