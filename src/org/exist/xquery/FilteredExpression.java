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
name|memtree
operator|.
name|NodeImpl
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
name|MemoryNodeSet
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
name|NodeValue
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * FilteredExpression represents a primary expression with a predicate. Examples:  * for $i in (1 to 10)[$i mod 2 = 0], $a[1], (doc("test.xml")//section)[2]. Other predicate  * expressions are handled by class {@link org.exist.xquery.LocationStep}.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FilteredExpression
extends|extends
name|AbstractExpression
block|{
specifier|final
specifier|protected
name|Expression
name|expression
decl_stmt|;
specifier|protected
name|boolean
name|abbreviated
init|=
literal|false
decl_stmt|;
specifier|final
specifier|protected
name|List
name|predicates
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|private
name|Expression
name|parent
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FilteredExpression
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|void
name|addPredicate
parameter_list|(
name|Predicate
name|pred
parameter_list|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
name|getPredicates
parameter_list|()
block|{
return|return
name|predicates
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
if|if
condition|(
name|expression
operator|instanceof
name|PathExpr
condition|)
return|return
operator|(
operator|(
name|PathExpr
operator|)
name|expression
operator|)
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
return|;
return|return
name|expression
return|;
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
name|parent
operator|=
name|contextInfo
operator|.
name|getParent
argument_list|()
expr_stmt|;
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
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|pred
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
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
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|Sequence
name|result
decl_stmt|;
name|Sequence
name|seq
init|=
name|expression
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
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|Predicate
name|pred
init|=
operator|(
name|Predicate
operator|)
name|predicates
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// If the current step is an // abbreviated step, we have to treat the predicate
comment|// specially to get the context position right. //a[1] translates to /descendant-or-self::node()/a[1],
comment|// so we need to return the 1st a from any parent of a.
comment|//
comment|// If the predicate is known to return a node set, no special treatment is required.
if|if
condition|(
name|abbreviated
operator|&&
operator|(
name|pred
operator|.
name|getExecutionMode
argument_list|()
operator|!=
name|Predicate
operator|.
name|NODE
operator|||
operator|!
name|seq
operator|.
name|isPersistentSet
argument_list|()
operator|)
condition|)
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
if|if
condition|(
name|seq
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
name|NodeSet
name|contextSet
init|=
name|seq
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|Sequence
name|outerSequence
init|=
name|contextSet
operator|.
name|getParents
argument_list|(
name|getExpressionId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|outerSequence
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
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Sequence
name|newContextSeq
init|=
name|contextSet
operator|.
name|selectParentChild
argument_list|(
operator|(
name|NodeSet
operator|)
name|node
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|getExpressionId
argument_list|()
argument_list|)
decl_stmt|;
name|Sequence
name|temp
init|=
name|processPredicate
argument_list|(
name|outerSequence
argument_list|,
name|newContextSeq
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|MemoryNodeSet
name|nodes
init|=
name|seq
operator|.
name|toMemNodeSet
argument_list|()
decl_stmt|;
name|Sequence
name|outerSequence
init|=
name|nodes
operator|.
name|getParents
argument_list|(
operator|new
name|AnyNodeTest
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|outerSequence
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
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Sequence
name|newSet
init|=
name|nodes
operator|.
name|getChildrenForParent
argument_list|(
operator|(
name|NodeImpl
operator|)
name|node
argument_list|)
decl_stmt|;
name|Sequence
name|temp
init|=
name|processPredicate
argument_list|(
name|outerSequence
argument_list|,
name|newSet
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
name|result
operator|=
name|processPredicate
argument_list|(
name|contextSequence
argument_list|,
name|seq
argument_list|)
expr_stmt|;
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
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|processPredicate
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|seq
operator|=
name|pred
operator|.
name|evalPredicate
argument_list|(
name|contextSequence
argument_list|,
name|seq
argument_list|,
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)     */
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
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
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
name|result
operator|.
name|append
argument_list|(
name|expression
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|result
operator|.
name|append
argument_list|(
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|expression
operator|.
name|returnsType
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#resetState() 	 */
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
name|expression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|pred
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#setPrimaryAxis(int) 	 */
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
name|void
name|setAbbreviated
parameter_list|(
name|boolean
name|abbrev
parameter_list|)
block|{
name|abbreviated
operator|=
name|abbrev
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|int
name|deps
init|=
name|Dependency
operator|.
name|CONTEXT_SET
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|predicates
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
name|deps
operator||=
operator|(
operator|(
name|Predicate
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
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
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitFilteredExpr
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Expression
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
block|}
end_class

end_unit

