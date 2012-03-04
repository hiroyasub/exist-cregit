begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2010 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  * $Id$  */
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
name|DocumentSet
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
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractExpression
implements|implements
name|Expression
block|{
specifier|private
name|int
name|expressionId
init|=
name|EXPRESSION_ID_INVALID
decl_stmt|;
specifier|protected
name|XQueryContext
name|context
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
specifier|protected
name|DocumentSet
name|contextDocSet
init|=
literal|null
decl_stmt|;
comment|/**      * Holds the context id for the context of this expression.      */
specifier|protected
name|int
name|contextId
init|=
name|Expression
operator|.
name|NO_CONTEXT_ID
decl_stmt|;
comment|/**      * The purpose of ordered and unordered flag is to set the ordering mode in       * the static context to ordered or unordered for a certain region in a query.       */
specifier|protected
name|boolean
name|unordered
init|=
literal|false
decl_stmt|;
specifier|public
name|AbstractExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|expressionId
operator|=
name|context
operator|.
name|nextExpressionId
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getExpressionId
parameter_list|()
block|{
return|return
name|expressionId
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContextId
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|this
operator|.
name|contextId
operator|=
name|contextId
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getContextId
parameter_list|()
block|{
return|return
name|contextId
return|;
block|}
annotation|@
name|Override
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
annotation|@
name|Override
specifier|public
specifier|abstract
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
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#returnsType()      */
annotation|@
name|Override
specifier|public
specifier|abstract
name|int
name|returnsType
parameter_list|()
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#resetState()      */
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|contextDocSet
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|boolean
name|needsReset
parameter_list|()
block|{
comment|// always return true unless a subclass overwrites this
return|return
literal|true
return|;
block|}
comment|/**      * The default cardinality is {@link Cardinality#EXACTLY_ONE}.      */
annotation|@
name|Override
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
comment|// default cardinality
block|}
comment|/**      * Returns {@link Dependency#DEFAULT_DEPENDENCIES}.      *      * @see org.exist.xquery.Expression#getDependencies()      */
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|DEFAULT_DEPENDENCIES
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|UNKNOWN_AXIS
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#setContextDocSet(org.exist.dom.DocumentSet)      */
annotation|@
name|Override
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|this
operator|.
name|contextDocSet
operator|=
name|contextSet
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentSet
name|getContextDocSet
parameter_list|()
block|{
return|return
name|contextDocSet
return|;
block|}
annotation|@
name|Override
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
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|column
return|;
block|}
annotation|@
name|Override
specifier|public
name|XACMLSource
name|getSource
parameter_list|()
block|{
return|return
name|context
operator|.
name|getSource
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSubExpressionCount
parameter_list|()
block|{
comment|//default value
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getSubExpression
parameter_list|(
name|int
name|index
parameter_list|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Index: "
operator|+
name|index
operator|+
literal|", Size: "
operator|+
name|getSubExpressionCount
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
comment|//default
return|return
literal|false
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

