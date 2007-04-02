begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
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
name|util
operator|.
name|ExpressionDumper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_comment
comment|/**  * Analyzes the query and marks optimizable expressions for the query engine.  * This class just searches for potentially optimizable expressions in the query tree and  * encloses those expressions with an (#exist:optimize#) pragma. The real optimization  * work is not done by this class but by the pragma (see {@link org.exist.xquery.Optimize}).  * The pragma may also decide that the optimization is not applicable and just execute  * the expression without any optimization.  *  * Currently, the optimizer is disabled by default. To enable it, set attribute enable-query-rewriting  * to yes in conf.xml:  *  *&lt;xquery enable-java-binding="no" enable-query-rewriting="yes"&gt;...  *   * To enable/disable the optimizer for a single query, use an option:  *  *<pre>declare option exist:optimize "enable=yes|no";</pre>  *  */
end_comment

begin_class
specifier|public
class|class
name|Optimizer
extends|extends
name|BasicExpressionVisitor
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Optimizer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|Optimizer
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
block|}
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
name|boolean
name|optimize
init|=
literal|false
decl_stmt|;
comment|// only location steps with predicates can be optimized:
if|if
condition|(
name|locationStep
operator|.
name|hasPredicates
argument_list|()
condition|)
block|{
name|List
name|preds
init|=
name|locationStep
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
comment|// walk through the predicates attached to the current location step.
comment|// try to find a predicate containing an expression which is an instance
comment|// of Optimizable.
for|for
control|(
name|Iterator
name|i
init|=
name|preds
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
name|Predicate
name|pred
init|=
operator|(
name|Predicate
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|FindOptimizable
name|find
init|=
operator|new
name|FindOptimizable
argument_list|()
decl_stmt|;
name|pred
operator|.
name|accept
argument_list|(
name|find
argument_list|)
expr_stmt|;
if|if
condition|(
name|find
operator|.
name|getOptimizables
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|optimize
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|optimize
condition|)
block|{
comment|// we found at least one Optimizable. Rewrite the whole expression and
comment|// enclose it in an (#exist:optimize#) pragma.
name|Expression
name|parent
init|=
name|locationStep
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|parent
operator|instanceof
name|PathExpr
operator|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Parent expression of step is not a PathExpr: "
operator|+
name|parent
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"Rewriting expression: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|locationStep
argument_list|)
argument_list|)
expr_stmt|;
name|PathExpr
name|path
init|=
operator|(
name|PathExpr
operator|)
name|parent
decl_stmt|;
try|try
block|{
comment|// Create the pragma
name|ExtensionExpression
name|extension
init|=
operator|new
name|ExtensionExpression
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|extension
operator|.
name|addPragma
argument_list|(
operator|new
name|Optimize
argument_list|(
name|context
argument_list|,
name|Optimize
operator|.
name|OPTIMIZE_PRAGMA
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|extension
operator|.
name|setExpression
argument_list|(
name|locationStep
argument_list|)
expr_stmt|;
comment|// Replace the old expression with the pragma
name|path
operator|.
name|replaceExpression
argument_list|(
name|locationStep
argument_list|,
name|extension
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to optimize expression: "
operator|+
name|locationStep
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|visitForExpression
parameter_list|(
name|ForExpr
name|forExpr
parameter_list|)
block|{
name|forExpr
operator|.
name|getInputSequence
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Expression
name|where
init|=
name|forExpr
operator|.
name|getWhereExpression
argument_list|()
decl_stmt|;
if|if
condition|(
name|where
operator|!=
literal|null
condition|)
name|where
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|forExpr
operator|.
name|getReturnExpression
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitLetExpression
parameter_list|(
name|LetExpr
name|letExpr
parameter_list|)
block|{
name|letExpr
operator|.
name|getInputSequence
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Expression
name|where
init|=
name|letExpr
operator|.
name|getWhereExpression
argument_list|()
decl_stmt|;
if|if
condition|(
name|where
operator|!=
literal|null
condition|)
name|where
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|letExpr
operator|.
name|getReturnExpression
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * Try to find an expression object implementing interface Optimizable.      */
specifier|private
class|class
name|FindOptimizable
extends|extends
name|BasicExpressionVisitor
block|{
name|List
name|optimizables
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|List
name|getOptimizables
parameter_list|()
block|{
return|return
name|optimizables
return|;
block|}
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
name|visitFtExpression
parameter_list|(
name|ExtFulltext
name|fulltext
parameter_list|)
block|{
name|optimizables
operator|.
name|add
argument_list|(
name|fulltext
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|comparison
parameter_list|)
block|{
name|optimizables
operator|.
name|add
argument_list|(
name|comparison
argument_list|)
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
specifier|public
name|void
name|visitFunction
parameter_list|(
name|Function
name|function
parameter_list|)
block|{
if|if
condition|(
name|function
operator|instanceof
name|Optimizable
condition|)
block|{
name|optimizables
operator|.
name|add
argument_list|(
name|function
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

