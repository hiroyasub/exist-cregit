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
name|ArraySet
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
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getDependencies
argument_list|()
return|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#analyze(org.exist.xquery.Expression)      */
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
name|flags
operator||=
name|IN_PREDICATE
expr_stmt|;
comment|// set flag to signal subexpression that we are in a predicate
name|flags
operator|&=
operator|~
name|IN_WHERE_CLAUSE
expr_stmt|;
comment|// remove where clause flag
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
return|return;
name|super
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|int
name|type
init|=
name|inner
operator|.
name|returnsType
argument_list|()
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
name|type
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
operator|.
name|getDependencies
argument_list|()
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
name|type
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
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
name|flags
operator||=
name|SINGLE_STEP_EXECUTION
expr_stmt|;
comment|// need to re-analyze:
name|super
operator|.
name|analyze
argument_list|(
name|parent
argument_list|,
name|flags
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
comment|// just to be sure: change mode to boolean if the predicate expression returns a number
comment|//TODO : the code, likely to be correct, implements the exact contrary
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
name|NUMBER
argument_list|)
operator|&&
name|executionMode
operator|==
name|BOOLEAN
condition|)
block|{
name|executionMode
operator|=
name|POSITIONAL
expr_stmt|;
block|}
comment|//            if (!(contextSequence instanceof VirtualNodeSet)&&
comment|//            		Type.subTypeOf(contextSequence.getItemType(), Type.ATOMIC))
comment|//            	executionMode = BOOLEAN;
switch|switch
condition|(
name|executionMode
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
literal|"selectByNodeSet"
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
literal|"evalBoolean"
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
literal|"selectByPosition"
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
name|executionMode
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
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|context
operator|.
name|setContextPosition
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|Sequence
name|innerSeq
decl_stmt|;
name|innerSeq
operator|=
name|inner
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
expr_stmt|;
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
name|NodeProxy
name|current
decl_stmt|;
name|ContextItem
name|contextNode
decl_stmt|;
name|NodeProxy
name|next
decl_stmt|;
name|DocumentImpl
name|lastDoc
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|,
name|sizeHint
init|=
operator|-
literal|1
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
name|count
operator|++
control|)
block|{
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastDoc
operator|==
literal|null
operator|||
name|current
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|current
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
name|contextNode
operator|=
name|current
operator|.
name|getContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|contextNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Internal evaluation error: context node is missing for node "
operator|+
name|current
operator|.
name|getGID
argument_list|()
operator|+
literal|" !"
argument_list|)
throw|;
block|}
while|while
condition|(
name|contextNode
operator|!=
literal|null
condition|)
block|{
name|next
operator|=
name|contextNode
operator|.
name|getNode
argument_list|()
expr_stmt|;
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
name|current
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
name|contextNode
operator|=
name|contextNode
operator|.
name|getNextItem
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
operator|&&
name|outerSequence
operator|!=
literal|null
operator|&&
name|outerSequence
operator|.
name|getLength
argument_list|()
operator|>
literal|0
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
name|boolean
name|reverseAxis
init|=
name|isReverseAxis
argument_list|(
name|mode
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|reverseAxis
operator|||
name|mode
operator|==
name|Constants
operator|.
name|FOLLOWING_SIBLING_AXIS
operator|||
name|mode
operator|==
name|Constants
operator|.
name|FOLLOWING_AXIS
operator|||
name|mode
operator|==
name|Constants
operator|.
name|SELF_AXIS
operator|)
condition|)
block|{
name|outerSequence
operator|.
name|clearContext
argument_list|()
expr_stmt|;
name|Sequence
name|ancestors
init|=
name|contextSet
operator|.
name|selectAncestorDescendant
argument_list|(
name|outerSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ArraySet
name|temp
init|=
operator|new
name|ArraySet
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
name|getNextItem
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
name|getUnsorted
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
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception for the else condition ?
block|}
block|}
comment|//TODO : understand why we find other forward axes than the 3 ones above here
block|}
else|else
block|{
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
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|item
decl_stmt|;
name|Sequence
name|temp
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|Constants
operator|.
name|FOLLOWING_SIBLING_AXIS
case|:
name|temp
operator|=
name|contextSet
operator|.
name|selectSiblings
argument_list|(
name|p
argument_list|,
name|NodeSet
operator|.
name|FOLLOWING
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
name|selectSiblings
argument_list|(
name|p
argument_list|,
name|NodeSet
operator|.
name|PRECEDING
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
break|break;
case|case
name|Constants
operator|.
name|PARENT_AXIS
case|:
name|temp
operator|=
name|p
operator|.
name|getParents
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
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
literal|false
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
literal|false
argument_list|)
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
break|break;
comment|//TODO : explain this default given what is said above !!!
default|default:
name|temp
operator|=
name|contextSet
operator|.
name|selectAncestorDescendant
argument_list|(
name|p
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
block|}
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
if|if
condition|(
name|pos
operator|<
name|temp
operator|.
name|getLength
argument_list|()
operator|&&
name|pos
operator|>
operator|-
literal|1
condition|)
name|result
operator|.
name|add
argument_list|(
name|temp
operator|.
name|itemAt
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception for the else condition ?
block|}
block|}
block|}
return|return
name|result
return|;
block|}
else|else
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
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
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
decl_stmt|;
if|if
condition|(
name|pos
operator|>
literal|0
operator|&&
name|pos
operator|<=
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
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//Other positions are ignored
block|}
return|return
name|result
return|;
block|}
block|}
comment|//TODO : move this to a dedicated Axis class -pb
specifier|public
specifier|final
specifier|static
name|boolean
name|isReverseAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|UNKNOWN_AXIS
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Tested unknown axis"
argument_list|)
throw|;
return|return
operator|(
name|axis
operator|<
name|Constants
operator|.
name|CHILD_AXIS
operator|)
return|;
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
comment|//TODO : does this actually do anything ?
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

