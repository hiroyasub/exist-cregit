begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Type
import|;
end_import

begin_comment
comment|/**  * XQuery if ... then ... else expression.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConditionalExpression
extends|extends
name|AbstractExpression
implements|implements
name|RewritableExpression
block|{
specifier|private
name|Expression
name|testExpr
decl_stmt|;
specifier|private
name|Expression
name|thenExpr
decl_stmt|;
specifier|private
name|Expression
name|elseExpr
decl_stmt|;
specifier|public
name|ConditionalExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|testExpr
parameter_list|,
name|Expression
name|thenExpr
parameter_list|,
name|Expression
name|elseExpr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|testExpr
operator|=
name|testExpr
operator|.
name|simplify
argument_list|()
expr_stmt|;
name|this
operator|.
name|thenExpr
operator|=
name|thenExpr
operator|.
name|simplify
argument_list|()
expr_stmt|;
name|this
operator|.
name|elseExpr
operator|=
name|elseExpr
operator|.
name|simplify
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
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
name|Expression
name|getTestExpr
parameter_list|()
block|{
return|return
name|testExpr
return|;
block|}
specifier|public
name|Expression
name|getThenExpr
parameter_list|()
block|{
return|return
name|thenExpr
return|;
block|}
specifier|public
name|Expression
name|getElseExpr
parameter_list|()
block|{
return|return
name|elseExpr
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|thenExpr
operator|.
name|getCardinality
argument_list|()
operator||
name|elseExpr
operator|.
name|getCardinality
argument_list|()
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
name|contextInfo
operator|.
name|setFlags
argument_list|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
operator|(
operator|~
name|IN_PREDICATE
operator|)
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|testExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|thenExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|elseExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|context
operator|.
name|expressionStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Sequence
name|testSeq
init|=
name|testExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|testSeq
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
block|{
return|return
name|thenExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|elseExpr
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
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getLine
argument_list|()
operator|==
literal|0
condition|)
name|e
operator|.
name|setLocation
argument_list|(
name|line
argument_list|,
name|column
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|expressionEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#preselect(org.exist.dom.DocumentSet) 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|in_docs
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
literal|"if ("
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|testExpr
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
literal|") then"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|thenExpr
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
literal|"else"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|elseExpr
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
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
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
literal|"if ( "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|testExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" ) then "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|thenExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" else "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|elseExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|getCommonSuperType
argument_list|(
name|thenExpr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|elseExpr
operator|.
name|returnsType
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
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
name|testExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|thenExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|elseExpr
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
name|visitConditional
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/* RewritableExpression API */
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
name|testExpr
operator|==
name|oldExpr
condition|)
name|testExpr
operator|=
name|newExpr
expr_stmt|;
if|else if
condition|(
name|thenExpr
operator|==
name|oldExpr
condition|)
name|thenExpr
operator|=
name|newExpr
expr_stmt|;
if|else if
condition|(
name|elseExpr
operator|==
name|oldExpr
condition|)
name|elseExpr
operator|=
name|newExpr
expr_stmt|;
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
literal|null
return|;
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|insertAfter
parameter_list|(
name|Expression
name|exprBefore
parameter_list|,
name|Expression
name|newExpr
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
comment|/* END RewritableExpression API */
block|}
end_class

end_unit

