begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|FastQSort
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
name|Expression
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
name|OrderSpec
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|OrderedValueSequence
extends|extends
name|AbstractSequence
block|{
specifier|private
name|OrderSpec
name|orderSpecs
index|[]
decl_stmt|;
specifier|private
name|Entry
index|[]
name|items
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|private
name|DocumentSet
name|docs
decl_stmt|;
specifier|public
name|OrderedValueSequence
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|OrderSpec
name|orderSpecs
index|[]
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|orderSpecs
operator|=
name|orderSpecs
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|items
operator|=
operator|new
name|Entry
index|[
name|size
index|]
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ATOMIC
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
return|return
operator|new
name|OrderedValueSequenceIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
operator|(
name|items
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|items
operator|.
name|length
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#add(org.exist.xpath.value.Item) 	 */
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
name|items
index|[
name|count
operator|++
index|]
operator|=
operator|new
name|Entry
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AbstractSequence#addAll(org.exist.xpath.value.Sequence) 	 */
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Item
name|next
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|other
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
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|other
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|add
argument_list|(
name|other
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sort
parameter_list|()
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|items
argument_list|,
literal|0
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#itemAt(int) 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|items
operator|!=
literal|null
operator|&&
name|pos
operator|>
operator|-
literal|1
operator|&&
name|pos
operator|<
name|count
condition|)
return|return
name|items
index|[
name|pos
index|]
operator|.
name|item
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Operation not supported"
argument_list|)
throw|;
block|}
specifier|private
class|class
name|Entry
implements|implements
name|Comparable
block|{
name|Item
name|item
decl_stmt|;
name|AtomicValue
name|values
index|[]
decl_stmt|;
specifier|public
name|Entry
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|item
operator|=
name|item
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
name|orderSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Sequence
name|seq
init|=
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getSortExpression
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|AtomicValue
operator|.
name|EMPTY_VALUE
expr_stmt|;
if|if
condition|(
name|seq
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|seq
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expected a single value for order expression "
operator|+
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getSortExpression
argument_list|()
operator|.
name|pprint
argument_list|()
operator|+
literal|" ; found: "
operator|+
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 		 * @see java.lang.Comparable#compareTo(java.lang.Object) 		 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Entry
name|other
init|=
operator|(
name|Entry
operator|)
name|o
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
name|values
index|[
name|i
index|]
expr_stmt|;
name|b
operator|=
name|other
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
operator|-
literal|1
expr_stmt|;
else|else
name|cmp
operator|=
literal|1
expr_stmt|;
block|}
if|else if
condition|(
name|b
operator|==
name|AtomicValue
operator|.
name|EMPTY_VALUE
operator|&&
name|a
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
literal|1
expr_stmt|;
else|else
name|cmp
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
name|cmp
operator|=
name|a
operator|.
name|compareTo
argument_list|(
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
literal|0
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
name|OrderedValueSequenceIterator
implements|implements
name|SequenceIterator
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.SequenceIterator#hasNext() 		 */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|count
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<
name|count
condition|)
return|return
name|items
index|[
name|pos
operator|++
index|]
operator|.
name|item
return|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

