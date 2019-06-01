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
name|MemTreeBuilder
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
name|XMLNames
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Dynamic constructor for processing instruction nodes.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DynamicPIConstructor
extends|extends
name|NodeConstructor
block|{
specifier|private
name|Expression
name|name
decl_stmt|;
specifier|private
name|Expression
name|content
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|DynamicPIConstructor
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
name|nameExpr
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|nameExpr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContentExpr
parameter_list|(
name|Expression
name|contentExpr
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|contentExpr
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.AnalyzeContextInfo)      */
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
name|name
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|content
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
name|name
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
comment|//TODO : get rid of getLength()
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
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"The name expression should evaluate to a single value"
argument_list|)
throw|;
block|}
specifier|final
name|Item
name|nameItem
init|=
name|nameSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|nameItem
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|STRING
operator|||
name|nameItem
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NCNAME
operator|||
name|nameItem
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
operator|)
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
literal|"The name expression should evaluate to a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|+
literal|" or a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|NCNAME
argument_list|)
operator|+
literal|" or a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|UNTYPED_ATOMIC
argument_list|)
operator|+
literal|". Got: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|nameItem
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|XMLNames
operator|.
name|isNCName
argument_list|(
name|nameSeq
operator|.
name|getStringValue
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
name|XQDY0041
argument_list|,
name|nameSeq
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"' is not a valid processing instruction name"
argument_list|,
name|nameSeq
argument_list|)
throw|;
block|}
if|if
condition|(
name|nameSeq
operator|.
name|getStringValue
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"XML"
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
name|XQDY0064
argument_list|,
name|nameSeq
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"' is not a valid processing instruction name"
argument_list|,
name|nameSeq
argument_list|)
throw|;
block|}
name|String
name|contentString
decl_stmt|;
specifier|final
name|Sequence
name|contentSeq
init|=
name|content
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
name|contentSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|contentString
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
name|contentSeq
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
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
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
block|}
while|while
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|Character
operator|.
name|isWhitespace
argument_list|(
name|buf
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
name|buf
operator|.
name|deleteCharAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|contentString
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|contentString
operator|.
name|indexOf
argument_list|(
literal|"?>"
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
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
name|XQDY0026
argument_list|,
name|contentString
operator|+
literal|"' is not a valid processing intruction content"
argument_list|,
name|contentSeq
argument_list|)
throw|;
block|}
specifier|final
name|int
name|nodeNo
init|=
name|builder
operator|.
name|processingInstruction
argument_list|(
name|nameSeq
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|contentString
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNo
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
literal|"processing-instruction {"
argument_list|)
expr_stmt|;
name|name
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
literal|"} {"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|content
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
literal|"processing-instruction {"
operator|+
name|name
operator|.
name|toString
argument_list|()
operator|+
literal|"} {"
operator|+
name|content
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
name|name
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|content
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
