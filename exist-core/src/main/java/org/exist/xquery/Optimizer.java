begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|QName
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
name|ArrayConstructor
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
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|AtomicValue
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
name|java
operator|.
name|util
operator|.
name|*
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
name|LogManager
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
comment|// check if filtered expression can be simplified:
comment|// handles expressions like //foo/(baz)[...]
if|if
condition|(
name|filtered
operator|.
name|getExpression
argument_list|()
operator|instanceof
name|LocationStep
condition|)
block|{
comment|// single location step: simplify by directly attaching it to the parent path expression
specifier|final
name|LocationStep
name|step
init|=
operator|(
name|LocationStep
operator|)
name|filtered
operator|.
name|getExpression
argument_list|()
decl_stmt|;
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
name|parent
operator|instanceof
name|RewritableExpression
condition|)
block|{
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
specifier|final
name|boolean
name|optimizable
init|=
name|hasOptimizable
argument_list|(
name|preds
argument_list|)
decl_stmt|;
if|if
condition|(
name|optimizable
condition|)
block|{
comment|// copy predicates
for|for
control|(
name|Predicate
name|pred
range|:
name|preds
control|)
block|{
name|step
operator|.
name|addPredicate
argument_list|(
name|pred
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|RewritableExpression
operator|)
name|parent
operator|)
operator|.
name|replace
argument_list|(
name|filtered
argument_list|,
name|step
argument_list|)
expr_stmt|;
name|step
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|visitLocationStep
argument_list|(
name|step
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|// check if there are any predicates which could be optimized
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
specifier|final
name|boolean
name|optimize
init|=
name|hasOptimizable
argument_list|(
name|preds
argument_list|)
decl_stmt|;
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
specifier|private
name|boolean
name|hasOptimizable
parameter_list|(
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
parameter_list|)
block|{
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
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
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
name|simplifyPath
argument_list|(
name|and
operator|.
name|getRight
argument_list|()
argument_list|)
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
name|simplifyPath
argument_list|(
name|and
operator|.
name|getLeft
argument_list|()
argument_list|)
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
comment|/**      * Check if a global variable can be inlined, usually if it      * references a literal value or sequence thereof.      *      * @param ref the variable reference      */
annotation|@
name|Override
specifier|public
name|void
name|visitVariableReference
parameter_list|(
name|VariableReference
name|ref
parameter_list|)
block|{
specifier|final
name|String
name|ns
init|=
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|ns
operator|!=
literal|null
operator|&&
name|ns
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|ns
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|!=
literal|null
operator|&&
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|VariableDeclaration
argument_list|>
name|vars
init|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getVariableDeclarations
argument_list|()
decl_stmt|;
for|for
control|(
name|VariableDeclaration
name|var
range|:
name|vars
control|)
block|{
if|if
condition|(
name|var
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|var
operator|.
name|getExpression
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|var
operator|.
name|getExpression
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
specifier|final
name|Expression
name|expression
init|=
name|simplifyPath
argument_list|(
name|var
operator|.
name|getExpression
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|InlineableVisitor
name|visitor
init|=
operator|new
name|InlineableVisitor
argument_list|()
decl_stmt|;
name|expression
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
if|if
condition|(
name|visitor
operator|.
name|isInlineable
argument_list|()
condition|)
block|{
specifier|final
name|Expression
name|parent
init|=
name|ref
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|instanceof
name|RewritableExpression
condition|)
block|{
comment|//                                System.out.println(ref.getSource().toString() + " line " + ref.getLine() + ": " +
comment|//                                        "inlining " +
comment|//                                        "variable "+ ref.getName());
operator|(
operator|(
name|RewritableExpression
operator|)
name|parent
operator|)
operator|.
name|replace
argument_list|(
name|ref
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
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
specifier|private
name|Expression
name|simplifyPath
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|expression
operator|instanceof
name|PathExpr
operator|)
condition|)
block|{
return|return
name|expression
return|;
block|}
specifier|final
name|PathExpr
name|path
init|=
operator|(
name|PathExpr
operator|)
name|expression
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|getLength
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
name|path
return|;
block|}
return|return
name|path
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
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
comment|/**      * Traverses an expression subtree to check if it could be inlined.      */
specifier|static
class|class
name|InlineableVisitor
extends|extends
name|DefaultExpressionVisitor
block|{
specifier|private
name|boolean
name|inlineable
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|isInlineable
parameter_list|()
block|{
return|return
name|inlineable
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|LiteralValue
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|expr
operator|instanceof
name|Atomize
operator|||
name|expr
operator|instanceof
name|DynamicCardinalityCheck
operator|||
name|expr
operator|instanceof
name|DynamicNameCheck
operator|||
name|expr
operator|instanceof
name|DynamicTypeCheck
operator|||
name|expr
operator|instanceof
name|UntypedValueCheck
operator|||
name|expr
operator|instanceof
name|ConcatExpr
operator|||
name|expr
operator|instanceof
name|ArrayConstructor
condition|)
block|{
name|expr
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expr
parameter_list|)
block|{
comment|// continue to check for numeric operators and other simple constructs,
comment|// abort for all other path expressions with length> 1
if|if
condition|(
name|expr
operator|instanceof
name|OpNumeric
operator|||
name|expr
operator|instanceof
name|SequenceConstructor
operator|||
name|expr
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|super
operator|.
name|visitPathExpr
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitUserFunction
parameter_list|(
name|UserDefinedFunction
name|function
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitBuiltinFunction
parameter_list|(
name|Function
name|function
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitFunctionCall
parameter_list|(
name|FunctionCall
name|call
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitForExpression
parameter_list|(
name|ForExpr
name|forExpr
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitLetExpression
parameter_list|(
name|LetExpr
name|letExpr
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitOrderByClause
parameter_list|(
name|OrderByClause
name|orderBy
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitGroupByClause
parameter_list|(
name|GroupByClause
name|groupBy
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitWhereClause
parameter_list|(
name|WhereClause
name|where
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitConditional
parameter_list|(
name|ConditionalExpression
name|conditional
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
block|{
name|super
operator|.
name|visitPredicate
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitDocumentConstructor
parameter_list|(
name|DocumentConstructor
name|constructor
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitElementConstructor
parameter_list|(
name|ElementConstructor
name|constructor
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitTextConstructor
parameter_list|(
name|DynamicTextConstructor
name|constructor
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitAttribConstructor
parameter_list|(
name|AttributeConstructor
name|constructor
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitAttribConstructor
parameter_list|(
name|DynamicAttributeConstructor
name|constructor
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitUnionExpr
parameter_list|(
name|Union
name|union
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitIntersectionExpr
parameter_list|(
name|Intersect
name|intersect
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitVariableDeclaration
parameter_list|(
name|VariableDeclaration
name|decl
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitTryCatch
parameter_list|(
name|TryCatchExpression
name|tryCatch
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitCastExpr
parameter_list|(
name|CastExpression
name|expression
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
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
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitAndExpr
parameter_list|(
name|OpAnd
name|and
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitOrExpr
parameter_list|(
name|OpOr
name|or
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitFilteredExpr
parameter_list|(
name|FilteredExpression
name|filtered
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visitVariableReference
parameter_list|(
name|VariableReference
name|ref
parameter_list|)
block|{
name|inlineable
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

