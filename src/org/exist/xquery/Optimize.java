begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  \$Id\$  */
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
name|VirtualNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|ElementIndex
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
name|ExtFulltext
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

begin_class
specifier|public
class|class
name|Optimize
extends|extends
name|Pragma
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|OPTIMIZE_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"optimize"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TimerPragma
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|Optimizable
name|optimizable
decl_stmt|;
specifier|private
name|Expression
name|innerExpr
decl_stmt|;
specifier|private
name|LocationStep
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|public
name|Optimize
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|QName
name|pragmaName
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|pragmaName
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
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
name|boolean
name|optimize
init|=
name|optimizable
operator|!=
literal|null
operator|&&
name|optimizable
operator|.
name|canOptimize
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|optimize
condition|)
block|{
name|NodeSet
name|contextSet
init|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|NodeSet
name|selection
init|=
name|optimizable
operator|.
name|preSelect
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: pre-selection: "
operator|+
name|selection
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeSet
name|ancestors
decl_stmt|;
if|if
condition|(
name|contextStep
operator|==
literal|null
condition|)
block|{
name|ancestors
operator|=
name|selection
operator|.
name|selectAncestorDescendant
argument_list|(
name|contextSet
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
literal|true
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|innerExpr
operator|.
name|eval
argument_list|(
name|ancestors
argument_list|)
return|;
block|}
else|else
block|{
name|NodeSelector
name|selector
decl_stmt|;
name|selector
operator|=
operator|new
name|AncestorSelector
argument_list|(
name|selection
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ElementIndex
name|index
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getElementIndex
argument_list|()
decl_stmt|;
name|QName
name|ancestorQN
init|=
name|contextStep
operator|.
name|getTest
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|optimizable
operator|.
name|optimizeOnSelf
argument_list|()
condition|)
block|{
name|ancestors
operator|=
name|selection
expr_stmt|;
block|}
else|else
name|ancestors
operator|=
name|index
operator|.
name|findElementsByTagName
argument_list|(
name|ancestorQN
operator|.
name|getNameType
argument_list|()
argument_list|,
name|selection
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|ancestorQN
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|contextStep
operator|.
name|setPreloadNodeSets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|contextStep
operator|.
name|setPreloadedData
argument_list|(
name|ancestors
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|ancestors
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: context after optimize: "
operator|+
name|ancestors
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|contextSequence
operator|=
name|filterDocuments
argument_list|(
name|contextSet
argument_list|,
name|ancestors
argument_list|)
expr_stmt|;
name|Sequence
name|result
init|=
name|innerExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: inner expr took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: Cannot optimize expression."
argument_list|)
expr_stmt|;
return|return
name|innerExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
block|}
specifier|private
name|Sequence
name|filterDocuments
parameter_list|(
name|NodeSet
name|contextSet
parameter_list|,
name|NodeSet
name|ancestors
parameter_list|)
block|{
if|if
condition|(
name|contextSet
operator|instanceof
name|VirtualNodeSet
condition|)
return|return
name|contextSet
return|;
return|return
name|contextSet
operator|.
name|filterDocuments
argument_list|(
name|ancestors
argument_list|)
return|;
block|}
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
name|innerExpr
operator|=
name|expression
expr_stmt|;
name|innerExpr
operator|.
name|accept
argument_list|(
operator|new
name|BasicExpressionVisitor
argument_list|()
block|{
specifier|public
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expression
parameter_list|)
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
name|expression
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|next
init|=
name|expression
operator|.
name|getExpression
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|next
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|super
operator|.
name|visit
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitFtExpression
parameter_list|(
name|ExtFulltext
name|fulltext
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: found optimizable: "
operator|+
name|fulltext
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|optimizable
operator|=
name|fulltext
expr_stmt|;
block|}
specifier|public
name|void
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
block|{
name|predicate
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|BasicExpressionVisitor
operator|.
name|findFirstStep
argument_list|(
name|innerExpr
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextStep
operator|!=
literal|null
operator|&&
name|contextStep
operator|.
name|getTest
argument_list|()
operator|.
name|isWildcardTest
argument_list|()
condition|)
name|contextStep
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: context step: "
operator|+
name|contextStep
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
block|}
end_class

end_unit

