begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|io
operator|.
name|Writer
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
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|CompiledExpression
import|;
end_import

begin_comment
comment|/**  * PathExpr is just a sequence of XQuery/XPath expressions, which will be called  * step by step.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|PathExpr
extends|extends
name|AbstractExpression
implements|implements
name|CompiledXQuery
implements|,
name|CompiledExpression
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PathExpr
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|boolean
name|keepVirtual
init|=
literal|false
decl_stmt|;
specifier|protected
name|List
name|steps
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|inPredicate
init|=
literal|false
decl_stmt|;
specifier|public
name|PathExpr
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
comment|/**      * Add an arbitrary expression to this object's list of child-expressions.      *       * @param s      */
specifier|public
name|void
name|add
parameter_list|(
name|Expression
name|s
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add all the child-expressions from another PathExpr to this object's      * child-expressions.      *       * @param path      */
specifier|public
name|void
name|add
parameter_list|(
name|PathExpr
name|path
parameter_list|)
block|{
name|Expression
name|expr
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|path
operator|.
name|steps
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
name|expr
operator|=
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Add another PathExpr to this object's expression list.      *       * @param path      */
specifier|public
name|void
name|addPath
parameter_list|(
name|PathExpr
name|path
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a predicate expression to the list of expressions. The predicate is      * added to the last expression in the list.      *       * @param pred      */
specifier|public
name|void
name|addPredicate
parameter_list|(
name|Predicate
name|pred
parameter_list|)
block|{
name|Expression
name|e
init|=
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|Step
condition|)
operator|(
operator|(
name|Step
operator|)
name|e
operator|)
operator|.
name|addPredicate
argument_list|(
name|pred
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
name|inPredicate
operator|=
operator|(
name|flags
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|steps
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// if this is a sequence of steps, the IN_PREDICATE flag
comment|// is only passed to the first step, so it has to be removed
comment|// for subsequent steps
comment|//TODO : why not if (i> 0) then ??? -pb
comment|//We'd need a test case with more than 2 steps to demonstrate the feature
if|if
condition|(
name|i
operator|==
literal|1
condition|)
name|flags
operator|=
name|flags
operator|&
operator|(
operator|~
name|IN_PREDICATE
operator|)
expr_stmt|;
name|Expression
name|expr
init|=
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|expr
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|Sequence
name|r
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|r
operator|=
name|contextSequence
expr_stmt|;
name|DocumentSet
name|contextDocs
init|=
literal|null
decl_stmt|;
name|Expression
name|expr
init|=
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|VariableReference
condition|)
block|{
name|Variable
name|var
init|=
operator|(
operator|(
name|VariableReference
operator|)
name|expr
operator|)
operator|.
name|getVariable
argument_list|()
decl_stmt|;
if|if
condition|(
name|var
operator|!=
literal|null
condition|)
name|contextDocs
operator|=
name|var
operator|.
name|getContextDocs
argument_list|()
expr_stmt|;
block|}
name|Item
name|current
decl_stmt|;
name|Sequence
name|values
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|expr
operator|=
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|contextDocs
operator|!=
literal|null
condition|)
name|expr
operator|.
name|setContextDocSet
argument_list|(
name|contextDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|expr
operator|.
name|getDependencies
argument_list|()
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
if|if
condition|(
name|r
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
name|r
operator|=
name|expr
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
name|values
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|r
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
name|iterInner
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
name|values
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|r
argument_list|,
name|current
argument_list|)
expr_stmt|;
else|else
name|values
operator|.
name|addAll
argument_list|(
name|expr
operator|.
name|eval
argument_list|(
name|r
argument_list|,
name|current
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|values
expr_stmt|;
block|}
block|}
else|else
block|{
name|r
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
comment|// remove duplicate nodes if this is a path
comment|// expression with more than one step
name|r
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|getLastExpression
parameter_list|()
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|steps
operator|.
name|size
argument_list|()
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
comment|//        dumper.startIndent();
name|Expression
name|next
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//Open a first parenthesis
if|if
condition|(
name|next
operator|instanceof
name|LogicalOp
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|next
operator|instanceof
name|Step
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
else|else
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
name|next
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
comment|//Close the last parenthesis
if|if
condition|(
name|next
operator|instanceof
name|LogicalOp
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
comment|//        dumper.endIndent();
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Expression
name|next
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|next
operator|=
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Open a first parenthesis
if|if
condition|(
name|next
operator|instanceof
name|LogicalOp
condition|)
name|result
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|next
operator|instanceof
name|Step
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
else|else
name|result
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|result
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
comment|// Close the last parenthesis
if|if
condition|(
name|next
operator|instanceof
name|LogicalOp
condition|)
name|result
operator|.
name|append
argument_list|(
literal|')'
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
name|int
name|returnsType
parameter_list|()
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Type
operator|.
name|NODE
return|;
return|return
operator|(
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|)
operator|.
name|returnsType
argument_list|()
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.AbstractExpression#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|Expression
name|next
decl_stmt|;
name|int
name|deps
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|steps
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
name|next
operator|=
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|deps
operator|=
name|deps
operator||
name|next
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
block|}
return|return
name|deps
return|;
block|}
specifier|public
name|void
name|replaceLastExpression
parameter_list|(
name|Expression
name|s
parameter_list|)
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|steps
operator|.
name|set
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getLiteralValue
parameter_list|()
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|Expression
name|next
init|=
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|LiteralValue
condition|)
block|{
try|try
block|{
return|return
operator|(
operator|(
name|LiteralValue
operator|)
name|next
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//TODO : is there anything to do here ?
block|}
block|}
if|if
condition|(
name|next
operator|instanceof
name|PathExpr
condition|)
return|return
operator|(
operator|(
name|PathExpr
operator|)
name|next
operator|)
operator|.
name|getLiteralValue
argument_list|()
return|;
return|return
literal|""
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.AbstractExpression#getASTNode()      */
specifier|public
name|XQueryAST
name|getASTNode
parameter_list|()
block|{
name|XQueryAST
name|ast
init|=
name|super
operator|.
name|getASTNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|ast
operator|==
literal|null
operator|&&
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
return|return
operator|(
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getASTNode
argument_list|()
return|;
return|return
name|ast
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.AbstractExpression#setPrimaryAxis(int)      */
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
operator|(
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xquery.AbstractExpression#resetState()      */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|steps
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
operator|(
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.xmldb.CompiledExpression#reset()      */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|resetState
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.CompiledXQuery#isValid() 	 */
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|context
operator|.
name|checkModulesValid
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.CompiledXQuery#dump(java.io.Writer)      */
specifier|public
name|void
name|dump
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|ExpressionDumper
name|dumper
init|=
operator|new
name|ExpressionDumper
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

