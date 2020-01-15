begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|dom
operator|.
name|persistent
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
name|persistent
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
comment|/**  * PathExpr is just a sequence of XQuery/XPath expressions, which will be called  * step by step.  *  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  * @author perig  * @author ljo  */
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
implements|,
name|RewritableExpression
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|PathExpr
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|steps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|staticContext
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|inPredicate
init|=
literal|false
decl_stmt|;
specifier|protected
name|Expression
name|parent
decl_stmt|;
specifier|public
name|PathExpr
parameter_list|(
specifier|final
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
comment|/**      * Add an arbitrary expression to this object's list of child-expressions.      *      * @param expression An expression to add to this path      */
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Expression
name|expression
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add all the child-expressions from another PathExpr to this object's      * child-expressions.      *      * @param path A path to concatenate with this path      */
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|PathExpr
name|path
parameter_list|)
block|{
name|steps
operator|.
name|addAll
argument_list|(
name|path
operator|.
name|steps
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add another PathExpr to this object's expression list.      *      * @param path A path to add to this path      */
specifier|public
name|void
name|addPath
parameter_list|(
specifier|final
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
comment|/**      * Add a predicate expression to the list of expressions. The predicate is      * added to the last expression in the list.      *      * @param predicate A predicate to add to the path as the last expression      */
specifier|public
name|void
name|addPredicate
parameter_list|(
specifier|final
name|Predicate
name|predicate
parameter_list|)
block|{
if|if
condition|(
operator|!
name|steps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Expression
name|e
init|=
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
block|{
operator|(
operator|(
name|Step
operator|)
name|e
operator|)
operator|.
name|addPredicate
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* RewritableExpression API */
comment|/**      * Replace the given expression by a new expression.      *      * @param oldExpr the old expression      * @param newExpr the new expression to replace the old      */
annotation|@
name|Override
specifier|public
name|void
name|replace
parameter_list|(
specifier|final
name|Expression
name|oldExpr
parameter_list|,
specifier|final
name|Expression
name|newExpr
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|steps
operator|.
name|indexOf
argument_list|(
name|oldExpr
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
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
literal|"Expression not found when trying to replace: {}; in: {}"
argument_list|,
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|oldExpr
argument_list|)
argument_list|,
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|steps
operator|.
name|set
argument_list|(
name|idx
argument_list|,
name|newExpr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getPrevious
parameter_list|(
specifier|final
name|Expression
name|current
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|steps
operator|.
name|indexOf
argument_list|(
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|1
condition|)
block|{
return|return
name|steps
operator|.
name|get
argument_list|(
name|idx
operator|-
literal|1
argument_list|)
return|;
block|}
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
name|steps
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
specifier|final
name|Expression
name|oldExpr
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|steps
operator|.
name|indexOf
argument_list|(
name|oldExpr
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
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
literal|"Expression not found when trying to remove: {}; in: {}"
argument_list|,
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|oldExpr
argument_list|)
argument_list|,
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|steps
operator|.
name|remove
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
comment|/* END RewritableExpression API */
annotation|@
name|Override
specifier|public
name|Expression
name|getParent
parameter_list|()
block|{
return|return
name|this
operator|.
name|parent
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|parent
operator|=
name|contextInfo
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|inPredicate
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
expr_stmt|;
name|unordered
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|UNORDERED
operator|)
operator|>
literal|0
expr_stmt|;
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
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
specifier|final
name|Expression
name|expr
init|=
name|steps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|1
condition|)
block|{
comment|//take care : predicates in predicates are not marked as such ! -pb
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
comment|//Where clauses should be identified. TODO : pass bound variable's inputSequence ? -pb
if|if
condition|(
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_WHERE_CLAUSE
operator|)
operator|==
literal|0
condition|)
block|{
name|contextInfo
operator|.
name|setContextId
argument_list|(
name|Expression
operator|.
name|NO_CONTEXT_ID
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|>
literal|1
condition|)
block|{
name|contextInfo
operator|.
name|setContextStep
argument_list|(
name|steps
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|expr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
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
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
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
comment|//we will filter out nodes from the contextSequence
name|result
operator|=
name|contextSequence
expr_stmt|;
name|Sequence
name|currentContext
init|=
name|contextSequence
decl_stmt|;
name|DocumentSet
name|contextDocs
init|=
literal|null
decl_stmt|;
name|Expression
name|expr
init|=
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
specifier|final
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
comment|//TOUNDERSTAND : how null could be possible here ? -pb
if|if
condition|(
name|var
operator|!=
literal|null
condition|)
block|{
name|contextDocs
operator|=
name|var
operator|.
name|getContextDocs
argument_list|()
expr_stmt|;
block|}
block|}
comment|//contextDocs == null *is* significant
name|setContextDocSet
argument_list|(
name|contextDocs
argument_list|)
expr_stmt|;
comment|//To prevent processing nodes after atomic values...
comment|//TODO : let the parser do it ? -pb
name|boolean
name|gotAtomicResult
init|=
literal|false
decl_stmt|;
name|Expression
name|prev
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Expression
argument_list|>
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
name|prev
operator|=
name|expr
expr_stmt|;
name|expr
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|context
operator|.
name|getWatchDog
argument_list|()
operator|.
name|proceed
argument_list|(
name|expr
argument_list|)
expr_stmt|;
comment|//TODO : maybe this could be detected by the parser ? -pb
if|if
condition|(
name|gotAtomicResult
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|expr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
comment|//Ugly workaround to allow preceding *text* nodes.
operator|&&
operator|!
operator|(
name|expr
operator|instanceof
name|EnclosedExpr
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
name|XPTY0019
argument_list|,
literal|"left operand of '/' must be a node. Got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|result
operator|.
name|getItemType
argument_list|()
argument_list|)
operator|+
name|Cardinality
operator|.
name|toString
argument_list|(
name|result
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|//contextDocs == null *is* significant
name|expr
operator|.
name|setContextDocSet
argument_list|(
name|contextDocs
argument_list|)
expr_stmt|;
comment|// switch into single step mode if we are processing in-memory nodes only
specifier|final
name|boolean
name|inMemProcessing
init|=
name|currentContext
operator|!=
literal|null
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|currentContext
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
operator|!
name|currentContext
operator|.
name|isPersistentSet
argument_list|()
decl_stmt|;
comment|//DESIGN : first test the dependency then the result
specifier|final
name|int
name|exprDeps
init|=
name|expr
operator|.
name|getDependencies
argument_list|()
decl_stmt|;
if|if
condition|(
name|inMemProcessing
operator|||
operator|(
operator|(
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|exprDeps
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
operator|||
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|exprDeps
argument_list|,
name|Dependency
operator|.
name|CONTEXT_POSITION
argument_list|)
operator|)
operator|&&
comment|//A positional predicate will be evaluated one time
comment|//TODO : reconsider since that may be expensive (type evaluation)
operator|!
operator|(
name|this
operator|instanceof
name|Predicate
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|this
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|)
operator|&&
name|currentContext
operator|!=
literal|null
operator|&&
operator|!
name|currentContext
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|Sequence
name|exprResult
init|=
operator|new
name|ValueSequence
argument_list|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|expr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ValueSequence
operator|)
name|exprResult
operator|)
operator|.
name|keepUnOrdered
argument_list|(
name|unordered
argument_list|)
expr_stmt|;
comment|//Restore a position which may have been modified by inner expressions
name|int
name|p
init|=
name|context
operator|.
name|getContextPosition
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|seq
init|=
name|context
operator|.
name|getContextSequence
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|iterInner
init|=
name|currentContext
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
name|p
operator|++
control|)
block|{
name|context
operator|.
name|setContextSequencePosition
argument_list|(
name|p
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|context
operator|.
name|getWatchDog
argument_list|()
operator|.
name|proceed
argument_list|(
name|expr
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|current
init|=
name|iterInner
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|//0 or 1 item
if|if
condition|(
operator|!
name|currentContext
operator|.
name|hasMany
argument_list|()
condition|)
block|{
name|exprResult
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|currentContext
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|exprResult
operator|.
name|addAll
argument_list|(
name|expr
operator|.
name|eval
argument_list|(
name|currentContext
argument_list|,
name|current
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|=
name|exprResult
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|currentContext
argument_list|)
expr_stmt|;
block|}
comment|//TOUNDERSTAND : why did I have to write this test :-) ? -pb
comment|//it looks like an empty sequence could be considered as a sub-type of Type.NODE
comment|//well, no so stupid I think...
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
operator|!
operator|(
name|result
operator|instanceof
name|VirtualNodeSet
operator|)
operator|&&
operator|!
operator|(
name|expr
operator|instanceof
name|EnclosedExpr
operator|)
operator|&&
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|result
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|gotAtomicResult
operator|=
literal|true
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
operator|&&
name|getLastExpression
argument_list|()
operator|instanceof
name|Step
condition|)
block|{
comment|// remove duplicate nodes if this is a path
comment|// expression with more than one step
name|result
operator|.
name|removeDuplicates
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|staticContext
condition|)
block|{
name|currentContext
operator|=
name|result
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|allowMixedNodesInReturn
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|allowMixedNodesInReturn
operator|=
name|prev
operator|.
name|allowMixedNodesInReturn
argument_list|()
operator||
name|expr
operator|.
name|allowMixedNodesInReturn
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|allowMixedNodesInReturn
operator|=
name|expr
operator|.
name|allowMixedNodesInReturn
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|gotAtomicResult
operator|&&
name|result
operator|!=
literal|null
operator|&&
operator|!
name|allowMixedNodesInReturn
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|result
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
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
name|XPTY0018
argument_list|,
literal|"Cannot mix nodes and atomic values in the result of a path expression."
argument_list|)
throw|;
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
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get the expression.      *      * @param pos the position.      *      * @return the expression.      *      * @deprecated use {@link #getSubExpression(int)}      */
annotation|@
name|Deprecated
specifier|public
name|Expression
name|getExpression
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
return|return
name|steps
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|steps
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getSubExpression
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
return|return
name|steps
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
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
return|return
name|steps
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
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
comment|/**      * Get the length.      *      * @return the length of the path expression.      *      * @deprecated use {@link #getSubExpressionCount()}      */
annotation|@
name|Deprecated
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
annotation|@
name|Override
specifier|public
name|int
name|getSubExpressionCount
parameter_list|()
block|{
return|return
name|steps
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allowMixedNodesInReturn
parameter_list|()
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|allowMixedNodesInReturn
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|allowMixedNodesInReturn
argument_list|()
return|;
block|}
specifier|public
name|void
name|setUseStaticContext
parameter_list|(
specifier|final
name|boolean
name|staticContext
parameter_list|)
block|{
name|this
operator|.
name|staticContext
operator|=
name|staticContext
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitPathExpr
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
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
specifier|final
name|Iterator
argument_list|<
name|Expression
argument_list|>
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
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
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
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
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
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|Expression
name|next
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"()"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Expression
argument_list|>
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
block|{
name|result
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
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
block|{
name|result
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
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
block|{
name|result
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
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
block|{
comment|//Not so simple. ITEM should be re-tuned in some circumstances that have to be determined
return|return
name|Type
operator|.
name|NODE
return|;
block|}
return|return
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
operator|.
name|returnsType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getCardinality
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
block|{
return|return
name|Cardinality
operator|.
name|ZERO
return|;
block|}
return|return
operator|(
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
name|getCardinality
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|int
name|deps
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Expression
name|step
range|:
name|steps
control|)
block|{
name|deps
operator|=
name|deps
operator||
name|step
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
specifier|final
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
block|{
return|return;
block|}
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
block|{
return|return
literal|""
return|;
block|}
specifier|final
name|Expression
name|next
init|=
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
specifier|final
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
block|{
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
block|}
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLine
parameter_list|()
block|{
if|if
condition|(
name|line
operator|<
literal|0
operator|&&
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLine
argument_list|()
return|;
block|}
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
if|if
condition|(
name|column
operator|<
literal|0
operator|&&
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getColumn
argument_list|()
return|;
block|}
return|return
name|column
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
specifier|final
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
block|{
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
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
block|{
return|return
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrimaryAxis
argument_list|()
return|;
block|}
return|return
name|Constants
operator|.
name|UNKNOWN_AXIS
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
specifier|final
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
name|steps
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|resetState
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|)
block|{
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|setContext
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|Expression
name|simplify
parameter_list|()
block|{
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|simplify
argument_list|()
return|;
block|}
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

