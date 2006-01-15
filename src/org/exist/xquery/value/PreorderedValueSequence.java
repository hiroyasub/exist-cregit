begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|xquery
operator|.
name|Constants
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
name|Expression
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
name|OrderSpec
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * A sequence that sorts its items in the order specified by the order specs  * of an "order by" clause. Used by {@link org.exist.xquery.ForExpr}.  *   * For better performance, the whole input sequence is sorted in one single step.  * However, this only works if every order expression returns a result of type  * node.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|PreorderedValueSequence
extends|extends
name|AbstractSequence
block|{
specifier|private
name|OrderSpec
name|orderSpecs
index|[]
decl_stmt|;
specifier|private
name|OrderedNodeProxy
index|[]
name|nodes
decl_stmt|;
specifier|public
name|PreorderedValueSequence
parameter_list|(
name|OrderSpec
name|specs
index|[]
parameter_list|,
name|Sequence
name|input
parameter_list|,
name|int
name|contextId
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|orderSpecs
operator|=
name|specs
expr_stmt|;
name|nodes
operator|=
operator|new
name|OrderedNodeProxy
index|[
name|input
operator|.
name|getLength
argument_list|()
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|input
operator|.
name|unorderedIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
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
name|nodes
index|[
name|j
index|]
operator|=
operator|new
name|OrderedNodeProxy
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|nodes
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|processAll
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|processAll
parameter_list|()
throws|throws
name|XPathException
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
name|orderSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|expr
init|=
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getSortExpression
argument_list|()
decl_stmt|;
name|NodeSet
name|result
init|=
name|expr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|result
operator|.
name|iterator
argument_list|()
init|;
name|j
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
name|j
operator|.
name|next
argument_list|()
decl_stmt|;
name|ContextItem
name|context
init|=
name|p
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//TODO : review to consider transverse context
while|while
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getNode
argument_list|()
operator|instanceof
name|OrderedNodeProxy
condition|)
block|{
name|OrderedNodeProxy
name|cp
init|=
operator|(
name|OrderedNodeProxy
operator|)
name|context
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|cp
operator|.
name|values
index|[
name|i
index|]
operator|=
name|p
operator|.
name|atomize
argument_list|()
expr_stmt|;
block|}
name|context
operator|=
name|context
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
name|sort
argument_list|()
expr_stmt|;
return|return
operator|new
name|PreorderedValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
block|{
return|return
operator|new
name|PreorderedValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|nodes
operator|.
name|length
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#add(org.exist.xquery.value.Item) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AbstractSequence#itemAt(int) 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|nodes
index|[
name|pos
index|]
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#removeDuplicates()      */
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
comment|// TODO: is this ever relevant?
block|}
specifier|private
name|void
name|sort
parameter_list|()
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
operator|new
name|OrderedComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|OrderedComparator
implements|implements
name|Comparator
block|{
comment|/* (non-Javadoc) 		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) 		 */
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
specifier|final
name|OrderedNodeProxy
name|p1
init|=
operator|(
name|OrderedNodeProxy
operator|)
name|o1
decl_stmt|;
specifier|final
name|OrderedNodeProxy
name|p2
init|=
operator|(
name|OrderedNodeProxy
operator|)
name|o2
decl_stmt|;
name|int
name|cmp
init|=
literal|0
decl_stmt|;
name|AtomicValue
name|a
decl_stmt|,
name|b
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p1
operator|.
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|a
operator|=
name|p1
operator|.
name|values
index|[
name|i
index|]
expr_stmt|;
name|b
operator|=
name|p2
operator|.
name|values
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|a
operator|==
name|AtomicValue
operator|.
name|EMPTY_VALUE
operator|&&
name|b
operator|!=
name|AtomicValue
operator|.
name|EMPTY_VALUE
condition|)
block|{
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|EMPTY_LEAST
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
else|else
name|cmp
operator|=
name|Constants
operator|.
name|SUPERIOR
expr_stmt|;
block|}
if|else if
condition|(
name|a
operator|!=
name|AtomicValue
operator|.
name|EMPTY_VALUE
operator|&&
name|b
operator|==
name|AtomicValue
operator|.
name|EMPTY_VALUE
condition|)
block|{
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|EMPTY_LEAST
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|Constants
operator|.
name|SUPERIOR
expr_stmt|;
else|else
name|cmp
operator|=
name|Constants
operator|.
name|INFERIOR
expr_stmt|;
block|}
else|else
name|cmp
operator|=
name|a
operator|.
name|compareTo
argument_list|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getCollator
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
operator|&
name|OrderSpec
operator|.
name|DESCENDING_ORDER
operator|)
operator|!=
literal|0
condition|)
name|cmp
operator|=
name|cmp
operator|*
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
name|Constants
operator|.
name|EQUAL
condition|)
break|break;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
block|}
block|}
return|return
name|cmp
return|;
block|}
block|}
specifier|private
class|class
name|OrderedNodeProxy
extends|extends
name|NodeProxy
block|{
name|AtomicValue
index|[]
name|values
decl_stmt|;
specifier|public
name|OrderedNodeProxy
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
block|{
name|super
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|AtomicValue
index|[
name|orderSpecs
operator|.
name|length
index|]
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|values
index|[
name|i
index|]
operator|=
name|AtomicValue
operator|.
name|EMPTY_VALUE
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|PreorderedValueSequenceIterator
implements|implements
name|SequenceIterator
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|nodes
operator|.
name|length
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<
name|nodes
operator|.
name|length
condition|)
return|return
name|nodes
index|[
name|pos
operator|++
index|]
return|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

