begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public  *  License along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  *  Handles predicate expressions.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_class
specifier|public
class|class
name|Predicate
extends|extends
name|PathExpr
block|{
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.PathExpr#getDependencies() 	 */
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
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//if(Type.subTypeOf(getExpression(0).returnsType(), Type.NODE)) {
return|return
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
comment|//} else {
comment|//	return Dependency.CONTEXT_ITEM + Dependency.CONTEXT_SET;
comment|//}
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
specifier|public
name|Sequence
name|evalPredicate
parameter_list|(
name|Sequence
name|outerSequence
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//long start = System.currentTimeMillis();
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
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|int
name|type
init|=
name|inner
operator|.
name|returnsType
argument_list|()
decl_stmt|;
comment|//LOG.debug("inner expr " + inner.pprint() + " returns " + Type.getTypeName(type));
comment|// Case 1: predicate expression returns a node set. Check the returned node set
comment|// against the context set and return all nodes from the context, for which the
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
name|contextSequence
argument_list|,
literal|null
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
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
name|doc
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|current
operator|.
name|doc
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
name|gid
operator|+
literal|"!"
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
operator|.
name|match
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
return|return
name|result
return|;
comment|// Case 2: predicate expression returns a boolean. Call the
comment|// predicate expression for each item in the context. Add the item
comment|// to the result if the predicate expression yields true.
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
name|BOOLEAN
argument_list|)
condition|)
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
name|context
operator|.
name|setContextPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
init|=
name|inner
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|item
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
comment|// Case 3: predicate expression returns a number. Call the predicate
comment|// expression once for each item in the context set.
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
name|ArraySet
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
init|=
name|contextSet
operator|.
name|selectAncestorDescendant
argument_list|(
name|p
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|)
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
name|v
operator|.
name|getInt
argument_list|()
operator|-
literal|1
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
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|contextSequence
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
else|else
name|LOG
operator|.
name|debug
argument_list|(
literal|"unable to determine return type of predicate expression"
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

