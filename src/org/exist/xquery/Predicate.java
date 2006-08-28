begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public  *  License along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|exist
operator|.
name|dom
operator|.
name|ContextItem
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
name|DocumentImpl
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
name|NumericValue
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

begin_comment
comment|/**  *  Handles predicate expressions.  *  *@author     Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Predicate
extends|extends
name|PathExpr
block|{
specifier|private
specifier|final
specifier|static
name|int
name|NODE
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|BOOLEAN
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|POSITIONAL
init|=
literal|2
decl_stmt|;
specifier|private
name|CachedResult
name|cached
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|executionMode
init|=
name|BOOLEAN
decl_stmt|;
specifier|private
name|int
name|outerContextId
decl_stmt|;
specifier|private
name|boolean
name|innerExpressionDot
init|=
literal|false
decl_stmt|;
specifier|public
name|Predicate
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#getDependencies() 	 */
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
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|deps
operator|=
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|deps
operator|=
name|super
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|executionMode
operator|==
name|POSITIONAL
condition|)
comment|// in a positional predicate, remove the dependency on the context item
name|deps
operator|=
name|deps
operator|&
operator|~
name|Dependency
operator|.
name|CONTEXT_ITEM
expr_stmt|;
return|return
name|deps
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#analyze(org.exist.xquery.AnalyzeContextInfo)      */
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
name|addFlag
argument_list|(
name|IN_PREDICATE
argument_list|)
expr_stmt|;
comment|// set flag to signal subexpression that we are in a predicate
name|contextInfo
operator|.
name|removeFlag
argument_list|(
name|IN_WHERE_CLAUSE
argument_list|)
expr_stmt|;
comment|// remove where clause flag
name|contextInfo
operator|.
name|removeFlag
argument_list|(
name|DOT_TEST
argument_list|)
expr_stmt|;
name|outerContextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
name|contextInfo
operator|.
name|setContextId
argument_list|(
name|getExpressionId
argument_list|()
argument_list|)
expr_stmt|;
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|super
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|newContextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|DOT_TEST
operator|)
operator|==
name|DOT_TEST
condition|)
name|innerExpressionDot
operator|=
literal|true
expr_stmt|;
name|Expression
name|inner
init|=
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Case 1: predicate expression returns a node set.
comment|// Check the returned node set against the context set
comment|// and return all nodes from the context, for which the
comment|// predicate expression returns a non-empty sequence.
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|inner
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|inner
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
name|executionMode
operator|=
name|NODE
expr_stmt|;
else|else
name|executionMode
operator|=
name|BOOLEAN
expr_stmt|;
comment|// Case 2: predicate expression returns a number.
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|inner
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
operator|(
name|inner
operator|.
name|getCardinality
argument_list|()
operator|==
name|Cardinality
operator|.
name|EXACTLY_ONE
operator|||
name|inner
operator|.
name|getCardinality
argument_list|()
operator|==
name|Cardinality
operator|.
name|ZERO_OR_ONE
operator|)
condition|)
block|{
comment|//Just a hint : inner's cardinality may still be potential
name|executionMode
operator|=
name|POSITIONAL
expr_stmt|;
comment|// Case 3: predicate expression evaluates to a boolean.
block|}
else|else
name|executionMode
operator|=
name|BOOLEAN
expr_stmt|;
if|if
condition|(
name|executionMode
operator|==
name|BOOLEAN
condition|)
block|{
name|contextInfo
operator|.
name|addFlag
argument_list|(
name|SINGLE_STEP_EXECUTION
argument_list|)
expr_stmt|;
comment|// need to re-analyze:
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Sequence
name|evalPredicate
parameter_list|(
name|Sequence
name|outerSequence
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|int
name|mode
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
block|}
name|Sequence
name|result
decl_stmt|;
name|Expression
name|inner
init|=
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|inner
operator|==
literal|null
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|int
name|recomputedExecutionMode
init|=
name|executionMode
decl_stmt|;
comment|//Try to promote a boolean evaluation to a positionnal one
if|if
condition|(
name|executionMode
operator|==
name|BOOLEAN
condition|)
block|{
comment|//Atomic sequences will keep evaluating "." in BOOLEAN mode
if|if
condition|(
operator|!
name|innerExpressionDot
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
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
name|Sequence
name|innerSeq
init|=
name|inner
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
comment|//Only if we have an actual *singleton* of numeric items
if|if
condition|(
name|innerSeq
operator|.
name|hasOne
argument_list|()
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|innerSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|recomputedExecutionMode
operator|=
name|POSITIONAL
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|executionMode
operator|==
name|NODE
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
operator|&&
operator|!
operator|(
name|contextSequence
operator|instanceof
name|VirtualNodeSet
operator|)
condition|)
block|{
name|recomputedExecutionMode
operator|=
name|BOOLEAN
expr_stmt|;
block|}
comment|//(1,2,3)[xs:decimal(.)]
comment|//TODO : think and find *the* triggering condition that leads to boolean evaluation
comment|//ideally determine it at analysis time
if|if
condition|(
name|executionMode
operator|==
name|POSITIONAL
operator|&&
name|innerExpressionDot
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
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
name|recomputedExecutionMode
operator|=
name|BOOLEAN
expr_stmt|;
block|}
switch|switch
condition|(
name|recomputedExecutionMode
condition|)
block|{
case|case
name|NODE
case|:
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
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATION_FLAGS
argument_list|,
literal|"OPTIMIZATION CHOICE"
argument_list|,
literal|"Node selection"
argument_list|)
expr_stmt|;
name|result
operator|=
name|selectByNodeSet
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
break|break;
case|case
name|BOOLEAN
case|:
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
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATION_FLAGS
argument_list|,
literal|"OPTIMIZATION CHOICE"
argument_list|,
literal|"Boolean evaluation"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evalBoolean
argument_list|(
name|contextSequence
argument_list|,
name|inner
argument_list|)
expr_stmt|;
break|break;
case|case
name|POSITIONAL
case|:
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
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATION_FLAGS
argument_list|,
literal|"OPTIMIZATION CHOICE"
argument_list|,
literal|"Positional evaluation"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setEvalFlag
argument_list|(
name|XQueryContext
operator|.
name|IN_POSITIONAL_PREDICATE
argument_list|)
expr_stmt|;
name|result
operator|=
name|selectByPosition
argument_list|(
name|outerSequence
argument_list|,
name|contextSequence
argument_list|,
name|mode
argument_list|,
name|inner
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported execution mode: '"
operator|+
name|recomputedExecutionMode
operator|+
literal|"'"
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
comment|/** 	 * @param contextSequence 	 * @param inner 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|Sequence
name|evalBoolean
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Expression
name|inner
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|int
name|p
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|contextSequence
operator|.
name|iterate
argument_list|()
init|;
name|i
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
name|setContextPosition
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Sequence
name|innerSeq
init|=
name|inner
operator|.
name|eval
argument_list|(
name|item
operator|.
name|toSequence
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerSeq
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * @param contextSequence 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|Sequence
name|selectByNodeSet
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|ExtArrayNodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|NodeSet
name|contextSet
init|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|boolean
name|contextIsVirtual
init|=
name|contextSet
operator|instanceof
name|VirtualNodeSet
decl_stmt|;
comment|/* 		//Uncomment the lines below which are intended to work around a VirtualNodeSet bug 		//No need to say that performance can suffer ! 		NodeSet nodes; 		if (contextIsVirtual) { 			ArraySet copy = new ArraySet(contextSet.getLength()); 			for (Iterator i = contextSet.iterator(); i.hasNext();) { 				copy.add((Item)i.next()); 			} 			nodes =	super.eval(copy, null).toNodeSet(); 		} else 			nodes =	super.eval(contextSet, null).toNodeSet();			 		//End of work-around 		*/
comment|//Comment the line below if you have uncommented the lines above :-)
name|NodeSet
name|nodes
init|=
name|super
operator|.
name|eval
argument_list|(
name|contextSet
argument_list|,
literal|null
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
comment|/* if the predicate expression returns results from the cache 		 * we can also return the cached result.  		 */
if|if
condition|(
name|cached
operator|!=
literal|null
operator|&&
name|cached
operator|.
name|isValid
argument_list|(
name|contextSequence
argument_list|)
operator|&&
name|nodes
operator|.
name|isCached
argument_list|()
condition|)
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
name|OPTIMIZATIONS
argument_list|,
literal|"Using cached results"
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|cached
operator|.
name|getResult
argument_list|()
return|;
block|}
name|DocumentImpl
name|lastDoc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|nodes
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
name|NodeProxy
name|currentNode
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
if|if
condition|(
name|lastDoc
operator|==
literal|null
operator|||
name|currentNode
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|currentNode
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|nodes
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
name|ContextItem
name|contextItem
init|=
name|currentNode
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextItem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Internal evaluation error: context is missing for node "
operator|+
name|currentNode
operator|.
name|getNodeId
argument_list|()
operator|+
literal|" !"
argument_list|)
throw|;
block|}
comment|//TODO : review to consider transverse context
while|while
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|contextItem
operator|.
name|getContextId
argument_list|()
operator|==
name|getExpressionId
argument_list|()
condition|)
block|{
name|NodeProxy
name|next
init|=
name|contextItem
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextIsVirtual
operator|||
name|contextSet
operator|.
name|contains
argument_list|(
name|next
argument_list|)
condition|)
block|{
name|next
operator|.
name|addMatches
argument_list|(
name|currentNode
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|next
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
name|contextItem
operator|=
name|contextItem
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|contextSequence
operator|instanceof
name|NodeSet
condition|)
name|cached
operator|=
operator|new
name|CachedResult
argument_list|(
operator|(
name|NodeSet
operator|)
name|contextSequence
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** 	 * @param outerSequence 	 * @param contextSequence 	 * @param mode 	 * @param inner 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|Sequence
name|selectByPosition
parameter_list|(
name|Sequence
name|outerSequence
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|int
name|mode
parameter_list|,
name|Expression
name|inner
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|outerSequence
operator|!=
literal|null
operator|&&
operator|!
name|outerSequence
operator|.
name|isEmpty
argument_list|()
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
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
name|Sequence
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|NodeSet
name|contextSet
init|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|Constants
operator|.
name|CHILD_AXIS
case|:
case|case
name|Constants
operator|.
name|ATTRIBUTE_AXIS
case|:
case|case
name|Constants
operator|.
name|DESCENDANT_AXIS
case|:
case|case
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
case|:
case|case
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
case|:
block|{
name|NodeSet
name|outerNodeSet
decl_stmt|;
comment|//Ugly and costly processing of VirtualNodeSEts
comment|//TODO : CORRECT THIS !!!
if|if
condition|(
name|outerSequence
operator|instanceof
name|VirtualNodeSet
condition|)
block|{
name|outerNodeSet
operator|=
operator|new
name|ExtArrayNodeSet
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
name|outerSequence
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|outerNodeSet
operator|.
name|add
argument_list|(
name|outerSequence
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|outerNodeSet
operator|=
name|outerSequence
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
comment|//Comment the line below if you have uncommented the lines above :-)
comment|//TODO: in some cases, especially with in-memory nodes,
comment|//outerSequence.toNodeSet() will generate a document
comment|//which will be different from the one(s) in contextSet
comment|//ancestors will thus be empty :-(
if|if
condition|(
name|outerSequence
operator|instanceof
name|VirtualNodeSet
condition|)
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|outerSequence
operator|)
operator|.
name|realize
argument_list|()
expr_stmt|;
name|Sequence
name|ancestors
init|=
name|contextSet
operator|.
name|selectAncestorDescendant
argument_list|(
name|outerNodeSet
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
literal|true
argument_list|,
name|getExpressionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextSet
operator|.
name|getDocumentSet
argument_list|()
operator|.
name|intersection
argument_list|(
name|outerNodeSet
operator|.
name|getDocumentSet
argument_list|()
argument_list|)
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"contextSet and outerNodeSet don't share any document"
argument_list|)
expr_stmt|;
name|ExtArrayNodeSet
name|temp
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|ancestors
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
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|ContextItem
name|contextNode
init|=
name|p
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|temp
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|contextNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|contextNode
operator|.
name|getContextId
argument_list|()
operator|==
name|getExpressionId
argument_list|()
condition|)
name|temp
operator|.
name|add
argument_list|(
name|contextNode
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
name|contextNode
operator|=
name|contextNode
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
comment|//TODO : understand why we sort here...
name|temp
operator|.
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
name|Sequence
name|innerSeq
init|=
name|inner
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|j
init|=
name|innerSeq
operator|.
name|iterate
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NumericValue
name|v
init|=
operator|(
name|NumericValue
operator|)
name|j
operator|.
name|nextItem
argument_list|()
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
decl_stmt|;
comment|//... whereas we don't want a sorted array here
comment|//TODO : rename this method as getInDocumentOrder ? -pb
name|p
operator|=
name|temp
operator|.
name|get
argument_list|(
name|v
operator|.
name|getInt
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|clearContext
argument_list|(
name|Expression
operator|.
name|IGNORE_CONTEXT
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
comment|//TODO : does null make sense here ? Well... sometimes ;-)
block|}
block|}
break|break;
block|}
default|default:
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
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Sequence
name|temp
decl_stmt|;
name|boolean
name|reverseAxis
init|=
literal|true
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|Constants
operator|.
name|ANCESTOR_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectAncestors
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|Expression
operator|.
name|IGNORE_CONTEXT
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectAncestors
argument_list|(
name|p
argument_list|,
literal|true
argument_list|,
name|Expression
operator|.
name|IGNORE_CONTEXT
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|PARENT_AXIS
case|:
comment|//TODO : understand why the contextSet is not involved here
comment|//NodeProxy.getParent returns a *theoretical* parent
comment|//which is *not* guaranteed to be in the context set !
name|temp
operator|=
name|p
operator|.
name|getParents
argument_list|(
name|Expression
operator|.
name|NO_CONTEXT_ID
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|PRECEDING_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectPreceding
argument_list|(
name|p
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|PRECEDING_SIBLING_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectPrecedingSiblings
argument_list|(
name|p
argument_list|,
name|Expression
operator|.
name|IGNORE_CONTEXT
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|FOLLOWING_SIBLING_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectFollowingSiblings
argument_list|(
name|p
argument_list|,
name|Expression
operator|.
name|IGNORE_CONTEXT
argument_list|)
expr_stmt|;
name|reverseAxis
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|FOLLOWING_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectFollowing
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|reverseAxis
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|SELF_AXIS
case|:
name|temp
operator|=
name|p
expr_stmt|;
name|reverseAxis
operator|=
literal|false
expr_stmt|;
break|break;
default|default:
comment|//temp = contextSet.selectAncestorDescendant(p, NodeSet.DESCENDANT, false, false);
comment|//break;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Tested unknown axis"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|temp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//TODO : build a value sequence *one* time ? -pb
name|Sequence
name|innerSeq
init|=
name|inner
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|j
init|=
name|innerSeq
operator|.
name|iterate
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NumericValue
name|v
init|=
operator|(
name|NumericValue
operator|)
name|j
operator|.
name|nextItem
argument_list|()
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
operator|(
name|reverseAxis
condition|?
name|temp
operator|.
name|getLength
argument_list|()
operator|-
name|v
operator|.
name|getInt
argument_list|()
else|:
name|v
operator|.
name|getInt
argument_list|()
operator|-
literal|1
operator|)
decl_stmt|;
comment|//Other positions are ignored
if|if
condition|(
name|pos
operator|>=
literal|0
operator|&&
name|pos
operator|<
name|temp
operator|.
name|getLength
argument_list|()
condition|)
block|{
name|NodeProxy
name|t
init|=
operator|(
name|NodeProxy
operator|)
name|temp
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
comment|// for the current context: filter out those context items
comment|// not selected by the positional predicate
name|ContextItem
name|ctx
init|=
name|t
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|t
operator|.
name|clearContext
argument_list|(
name|Expression
operator|.
name|IGNORE_CONTEXT
argument_list|)
expr_stmt|;
while|while
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ctx
operator|.
name|getContextId
argument_list|()
operator|==
name|outerContextId
condition|)
block|{
if|if
condition|(
name|ctx
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
name|t
operator|.
name|addContextNode
argument_list|(
name|outerContextId
argument_list|,
name|ctx
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
name|t
operator|.
name|addContextNode
argument_list|(
name|ctx
operator|.
name|getContextId
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|ctx
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
else|else
block|{
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Sequence
name|innerSeq
init|=
name|inner
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|innerSeq
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
name|NumericValue
name|v
init|=
operator|(
name|NumericValue
operator|)
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|v
operator|.
name|getInt
argument_list|()
operator|-
literal|1
decl_stmt|;
comment|//Other positions are ignored
if|if
condition|(
name|pos
operator|>=
literal|0
operator|&&
name|pos
operator|<
name|contextSequence
operator|.
name|getLength
argument_list|()
condition|)
name|result
operator|.
name|add
argument_list|(
name|contextSequence
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|super
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|getLength
argument_list|()
operator|>
literal|0
condition|)
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|super
operator|.
name|resetState
argument_list|()
expr_stmt|;
name|cached
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

