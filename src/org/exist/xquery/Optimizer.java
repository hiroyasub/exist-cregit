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
name|exist
operator|.
name|xquery
operator|.
name|pragmas
operator|.
name|Optimize
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
name|xquery
operator|.
name|functions
operator|.
name|fn
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
name|List
import|;
end_import

begin_comment
comment|/**  * Analyzes the query and marks optimizable expressions for the query engine.  * This class just searches for potentially optimizable expressions in the query tree and  * encloses those expressions with an (#exist:optimize#) pragma. The real optimization  * work is not done by this class but by the pragma (see {@link org.exist.xquery.pragmas.Optimize}).  * The pragma may also decide that the optimization is not applicable and just execute  * the expression without any optimization.  *  * Currently, the optimizer is disabled by default. To enable it, set attribute enable-query-rewriting  * to yes in conf.xml:  *  *&lt;xquery enable-java-binding="no" enable-query-rewriting="yes"&gt;...  *   * To enable/disable the optimizer for a single query, use an option:  *  *<pre>declare option exist:optimize "enable=yes|no";</pre>  *  */
end_comment

begin_class
specifier|public
class|class
name|Optimizer
extends|extends
name|DefaultExpressionVisitor
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
specifier|private
name|int
name|predicates
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|hasOptimized
init|=
literal|false
decl_stmt|;
specifier|private
name|List
argument_list|<
name|QueryRewriter
argument_list|>
name|rewriters
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryRewriter
argument_list|>
argument_list|(
literal|5
argument_list|)
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
name|this
operator|.
name|rewriters
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getQueryRewriters
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasOptimized
parameter_list|()
block|{
return|return
name|hasOptimized
return|;
block|}
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
name|super
operator|.
name|visitLocationStep
argument_list|(
name|locationStep
argument_list|)
expr_stmt|;
comment|// check query rewriters if they want to rewrite the location step
name|Pragma
name|optimizePragma
init|=
literal|null
decl_stmt|;
for|for
control|(
name|QueryRewriter
name|rewriter
range|:
name|rewriters
control|)
block|{
try|try
block|{
name|optimizePragma
operator|=
name|rewriter
operator|.
name|rewriteLocationStep
argument_list|(
name|locationStep
argument_list|)
expr_stmt|;
if|if
condition|(
name|optimizePragma
operator|!=
literal|null
condition|)
block|{
comment|// expression was rewritten: return
name|hasOptimized
operator|=
literal|true
expr_stmt|;
break|break;
block|}
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
literal|"Exception called while rewriting location step: "
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
specifier|final
name|List
argument_list|<
name|Predicate
argument_list|>
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
specifier|final
name|Predicate
name|pred
range|:
name|preds
control|)
block|{
specifier|final
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
specifier|final
name|List
argument_list|<
name|Optimizable
argument_list|>
name|list
init|=
name|find
operator|.
name|getOptimizables
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|canOptimize
argument_list|(
name|list
argument_list|)
condition|)
block|{
name|optimize
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
specifier|final
name|Expression
name|parent
init|=
name|locationStep
operator|.
name|getParentExpression
argument_list|()
decl_stmt|;
if|if
condition|(
name|optimize
condition|)
block|{
comment|// we found at least one Optimizable. Rewrite the whole expression and
comment|// enclose it in an (#exist:optimize#) pragma.
if|if
condition|(
operator|!
operator|(
name|parent
operator|instanceof
name|RewritableExpression
operator|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Parent expression of step is not a PathExpr: "
operator|+
name|parent
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|hasOptimized
operator|=
literal|true
expr_stmt|;
specifier|final
name|RewritableExpression
name|path
init|=
operator|(
name|RewritableExpression
operator|)
name|parent
decl_stmt|;
try|try
block|{
comment|// Create the pragma
specifier|final
name|ExtensionExpression
name|extension
init|=
operator|new
name|ExtensionExpression
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|optimizePragma
operator|!=
literal|null
condition|)
block|{
name|extension
operator|.
name|addPragma
argument_list|(
name|optimizePragma
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
literal|false
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
name|replace
argument_list|(
name|locationStep
argument_list|,
name|extension
argument_list|)
expr_stmt|;
comment|// Check if there are additional steps before the optimizable expression and
comment|// rewrite them to use the ancestor axis. This will change //a/b//c[d = "D"] into
comment|// //c[d = "D"][ancestor::b/parent::a]
name|int
name|reverseAxis
init|=
name|reverseAxis
argument_list|(
name|locationStep
operator|.
name|getAxis
argument_list|()
argument_list|)
decl_stmt|;
name|Expression
name|previous
init|=
name|path
operator|.
name|getPrevious
argument_list|(
name|extension
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
operator|&&
name|reverseAxis
operator|!=
name|Constants
operator|.
name|UNKNOWN_AXIS
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Step
argument_list|>
name|prevSteps
init|=
operator|new
name|ArrayList
argument_list|<
name|Step
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|previous
operator|!=
literal|null
operator|&&
name|previous
operator|!=
name|path
operator|.
name|getFirst
argument_list|()
operator|&&
name|previous
operator|instanceof
name|Step
condition|)
block|{
specifier|final
name|Step
name|prevStep
init|=
operator|(
name|Step
operator|)
name|previous
decl_stmt|;
if|if
condition|(
name|prevStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|&&
operator|!
operator|(
name|path
operator|.
name|getPrevious
argument_list|(
name|prevStep
argument_list|)
operator|instanceof
name|LocationStep
operator|)
condition|)
block|{
comment|// Do not rewrite this step if it is the first step after a root step and
comment|// the axis is the child axis!
break|break;
block|}
name|reverseAxis
operator|=
name|reverseAxis
argument_list|(
name|prevStep
operator|.
name|getAxis
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverseAxis
operator|!=
name|Constants
operator|.
name|UNKNOWN_AXIS
operator|&&
operator|!
name|prevStep
operator|.
name|hasPredicates
argument_list|()
operator|&&
operator|!
name|prevStep
operator|.
name|getTest
argument_list|()
operator|.
name|isWildcardTest
argument_list|()
condition|)
block|{
name|prevSteps
operator|.
name|add
argument_list|(
name|prevStep
argument_list|)
expr_stmt|;
name|previous
operator|=
name|path
operator|.
name|getPrevious
argument_list|(
name|prevStep
argument_list|)
expr_stmt|;
name|path
operator|.
name|remove
argument_list|(
name|prevStep
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|prevSteps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|reverseAxis
operator|=
name|reverseAxis
argument_list|(
name|locationStep
operator|.
name|getAxis
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Predicate
name|predicate
init|=
operator|new
name|Predicate
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Step
name|expr
range|:
name|prevSteps
control|)
block|{
specifier|final
name|int
name|axis
init|=
name|expr
operator|.
name|getAxis
argument_list|()
decl_stmt|;
name|expr
operator|.
name|setAxis
argument_list|(
name|reverseAxis
argument_list|)
expr_stmt|;
name|reverseAxis
operator|=
name|reverseAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
name|predicate
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
name|locationStep
operator|.
name|setAxis
argument_list|(
name|Constants
operator|.
name|DESCENDANT_AXIS
argument_list|)
expr_stmt|;
name|locationStep
operator|.
name|addPredicate
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Rewritten expression: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|parent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
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
if|else if
condition|(
name|optimizePragma
operator|!=
literal|null
condition|)
block|{
specifier|final
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
name|optimizePragma
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
specifier|final
name|RewritableExpression
name|path
init|=
operator|(
name|RewritableExpression
operator|)
name|parent
decl_stmt|;
name|path
operator|.
name|replace
argument_list|(
name|locationStep
argument_list|,
name|extension
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visitFilteredExpr
parameter_list|(
name|FilteredExpression
name|filtered
parameter_list|)
block|{
name|super
operator|.
name|visitFilteredExpr
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
name|boolean
name|optimize
init|=
literal|false
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
init|=
name|filtered
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
comment|// walk through the predicates attached to the current location step.
comment|// try to find a predicate containing an expression which is an instance
comment|// of Optimizable.
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|preds
control|)
block|{
specifier|final
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
specifier|final
name|List
argument_list|<
name|Optimizable
argument_list|>
name|list
init|=
name|find
operator|.
name|getOptimizables
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|canOptimize
argument_list|(
name|list
argument_list|)
condition|)
block|{
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
specifier|final
name|Expression
name|parent
init|=
name|filtered
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
name|RewritableExpression
operator|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Parent expression: "
operator|+
name|parent
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" of step does not implement RewritableExpression"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
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
name|filtered
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hasOptimized
operator|=
literal|true
expr_stmt|;
specifier|final
name|RewritableExpression
name|path
init|=
operator|(
name|RewritableExpression
operator|)
name|parent
decl_stmt|;
try|try
block|{
comment|// Create the pragma
specifier|final
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
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|extension
operator|.
name|setExpression
argument_list|(
name|filtered
argument_list|)
expr_stmt|;
comment|// Replace the old expression with the pragma
name|path
operator|.
name|replace
argument_list|(
name|filtered
argument_list|,
name|extension
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
name|filtered
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
name|visitAndExpr
parameter_list|(
name|OpAnd
name|and
parameter_list|)
block|{
if|if
condition|(
name|predicates
operator|>
literal|0
condition|)
block|{
comment|// inside a filter expression, we can often replace a logical and with
comment|// a chain of filters, which can then be further optimized
name|Expression
name|parent
init|=
name|and
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
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Parent expression of boolean operator is not a PathExpr: "
operator|+
name|parent
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|PathExpr
name|path
decl_stmt|;
name|Predicate
name|predicate
decl_stmt|;
if|if
condition|(
name|parent
operator|instanceof
name|Predicate
condition|)
block|{
name|predicate
operator|=
operator|(
name|Predicate
operator|)
name|parent
expr_stmt|;
name|path
operator|=
name|predicate
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
operator|(
name|PathExpr
operator|)
name|parent
expr_stmt|;
name|parent
operator|=
name|path
operator|.
name|getParent
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|parent
operator|instanceof
name|Predicate
operator|)
operator|||
name|path
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Boolean operator is not a top-level expression in the predicate: "
operator|+
operator|(
name|parent
operator|==
literal|null
condition|?
literal|"?"
else|:
name|parent
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|predicate
operator|=
operator|(
name|Predicate
operator|)
name|parent
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Rewriting boolean expression: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|and
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|hasOptimized
operator|=
literal|true
expr_stmt|;
specifier|final
name|LocationStep
name|step
init|=
operator|(
name|LocationStep
operator|)
name|predicate
operator|.
name|getParent
argument_list|()
decl_stmt|;
specifier|final
name|Predicate
name|newPred
init|=
operator|new
name|Predicate
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|newPred
operator|.
name|add
argument_list|(
name|and
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
name|step
operator|.
name|insertPredicate
argument_list|(
name|predicate
argument_list|,
name|newPred
argument_list|)
expr_stmt|;
name|path
operator|.
name|replace
argument_list|(
name|and
argument_list|,
name|and
operator|.
name|getLeft
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|and
operator|.
name|isRewritable
argument_list|()
condition|)
block|{
name|and
operator|.
name|getLeft
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|and
operator|.
name|getRight
argument_list|()
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
name|visitOrExpr
parameter_list|(
name|OpOr
name|or
parameter_list|)
block|{
if|if
condition|(
name|or
operator|.
name|isRewritable
argument_list|()
condition|)
block|{
name|or
operator|.
name|getLeft
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|or
operator|.
name|getRight
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|comparison
parameter_list|)
block|{
comment|// Check if the left operand is a path expression ending in a
comment|// text() step. This step is unnecessary and makes it hard
comment|// to further optimize the expression. We thus try to remove
comment|// the extra text() step automatically.
comment|// TODO should insert a pragma instead of removing the step
comment|// we don't know at this point if there's an index to use
comment|//        Expression expr = comparison.getLeft();
comment|//        if (expr instanceof PathExpr) {
comment|//            PathExpr pathExpr = (PathExpr) expr;
comment|//            Expression last = pathExpr.getLastExpression();
comment|//            if (pathExpr.getLength()> 1&& last instanceof Step&& ((Step)last).getTest().getType() == Type.TEXT) {
comment|//                pathExpr.remove(last);
comment|//            }
comment|//        }
name|comparison
operator|.
name|getLeft
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|comparison
operator|.
name|getRight
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
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
block|{
operator|++
name|predicates
expr_stmt|;
name|super
operator|.
name|visitPredicate
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
operator|--
name|predicates
expr_stmt|;
block|}
specifier|private
name|boolean
name|canOptimize
parameter_list|(
name|List
argument_list|<
name|Optimizable
argument_list|>
name|list
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Optimizable
name|optimizable
range|:
name|list
control|)
block|{
specifier|final
name|int
name|axis
init|=
name|optimizable
operator|.
name|getOptimizeAxis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|SELF_AXIS
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|int
name|reverseAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
switch|switch
condition|(
name|axis
condition|)
block|{
case|case
name|Constants
operator|.
name|CHILD_AXIS
case|:
return|return
name|Constants
operator|.
name|PARENT_AXIS
return|;
case|case
name|Constants
operator|.
name|DESCENDANT_AXIS
case|:
return|return
name|Constants
operator|.
name|ANCESTOR_AXIS
return|;
case|case
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
case|:
return|return
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
return|;
block|}
return|return
name|Constants
operator|.
name|UNKNOWN_AXIS
return|;
block|}
comment|/**      * Try to find an expression object implementing interface Optimizable.      */
specifier|public
specifier|static
class|class
name|FindOptimizable
extends|extends
name|BasicExpressionVisitor
block|{
name|List
argument_list|<
name|Optimizable
argument_list|>
name|optimizables
init|=
operator|new
name|ArrayList
argument_list|<
name|Optimizable
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|List
argument_list|<
name|Optimizable
argument_list|>
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
specifier|final
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
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitBuiltinFunction
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
operator|(
name|Optimizable
operator|)
name|function
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

