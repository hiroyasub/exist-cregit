begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * \$Id\$  */
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
name|parser
operator|.
name|XQueryAST
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
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
import|;
end_import

begin_class
specifier|public
class|class
name|DebuggableExpression
implements|implements
name|Expression
implements|,
name|RewritableExpression
block|{
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|protected
name|int
name|line
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|int
name|column
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|DebuggableExpression
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|this
operator|.
name|expression
operator|=
name|expression
operator|.
name|simplify
argument_list|()
expr_stmt|;
name|this
operator|.
name|line
operator|=
name|this
operator|.
name|expression
operator|.
name|getLine
argument_list|()
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|this
operator|.
name|expression
operator|.
name|getColumn
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getExpressionId
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getExpressionId
argument_list|()
return|;
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
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|contextInfo
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
try|try
block|{
name|expression
operator|.
name|getContext
argument_list|()
operator|.
name|expressionStart
argument_list|(
name|expression
argument_list|)
expr_stmt|;
return|return
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
finally|finally
block|{
name|expression
operator|.
name|getContext
argument_list|()
operator|.
name|expressionEnd
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|expression
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getPrimaryAxis
argument_list|()
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|expression
operator|.
name|returnsType
argument_list|()
return|;
block|}
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getCardinality
argument_list|()
return|;
block|}
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getDependencies
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
name|expression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|needsReset
parameter_list|()
block|{
return|return
literal|true
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
name|expression
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|expression
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|expression
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContextId
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|expression
operator|.
name|setContextId
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getContextId
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getContextId
argument_list|()
return|;
block|}
specifier|public
name|DocumentSet
name|getContextDocSet
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getContextDocSet
argument_list|()
return|;
block|}
specifier|public
name|void
name|setASTNode
parameter_list|(
name|XQueryAST
name|ast
parameter_list|)
block|{
if|if
condition|(
name|ast
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|ast
operator|.
name|getLine
argument_list|()
expr_stmt|;
name|column
operator|=
name|ast
operator|.
name|getColumn
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setLocation
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
block|{
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
block|}
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|column
return|;
block|}
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getContext
argument_list|()
return|;
block|}
specifier|public
name|XACMLSource
name|getSource
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getSource
argument_list|()
return|;
block|}
specifier|public
name|int
name|getSubExpressionCount
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getSubExpressionCount
argument_list|()
return|;
block|}
specifier|public
name|Expression
name|getSubExpression
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|expression
operator|.
name|getSubExpression
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|match
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|expression
operator|.
name|match
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|replace
parameter_list|(
name|Expression
name|oldExpr
parameter_list|,
name|Expression
name|newExpr
parameter_list|)
block|{
if|if
condition|(
name|oldExpr
operator|==
name|expression
condition|)
name|expression
operator|=
name|newExpr
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
name|Expression
name|oldExpr
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Method remove is not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getPrevious
parameter_list|(
name|Expression
name|current
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getFirst
parameter_list|()
block|{
return|return
name|expression
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowMixedNodesInReturn
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|expression
operator|.
name|toString
argument_list|()
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
annotation|@
name|Override
specifier|public
name|Expression
name|getParent
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

