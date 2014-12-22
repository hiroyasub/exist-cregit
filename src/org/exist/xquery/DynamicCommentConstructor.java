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
name|MemTreeBuilder
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

begin_comment
comment|/**  * Implements a dynamic comment constructor. Contrary to {@link org.exist.xquery.CommentConstructor},  * the character content of a DynamicCommentConstructor is determined only at evaluation time.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DynamicCommentConstructor
extends|extends
name|NodeConstructor
block|{
specifier|final
specifier|private
name|Expression
name|content
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|DynamicCommentConstructor
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|contentExpr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|content
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|contentExpr
argument_list|)
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
name|Sequence
name|result
decl_stmt|;
try|try
block|{
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
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
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
name|contentSeq
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|buf
operator|.
name|indexOf
argument_list|(
literal|"--"
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
operator|||
name|buf
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"-"
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
name|XQDY0072
argument_list|,
literal|"'"
operator|+
name|buf
operator|.
name|toString
argument_list|()
operator|+
literal|"' is not a valid comment"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|nodeNr
init|=
name|builder
operator|.
name|comment
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
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
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
literal|"comment {"
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
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"comment {"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|content
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
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

