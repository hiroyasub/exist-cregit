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
name|xquery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Except
extends|extends
name|CombiningExpression
block|{
specifier|public
name|Except
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|PathExpr
name|left
parameter_list|,
name|PathExpr
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.CombiningExpression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|Sequence
name|lval
init|=
name|left
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|rval
init|=
name|right
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
name|rval
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|||
name|lval
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|lval
return|;
if|if
condition|(
operator|!
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|lval
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rval
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"except operand is not a node sequence"
argument_list|)
throw|;
if|if
condition|(
name|lval
operator|.
name|isPersistentSet
argument_list|()
operator|&&
name|rval
operator|.
name|isPersistentSet
argument_list|()
condition|)
return|return
name|lval
operator|.
name|toNodeSet
argument_list|()
operator|.
name|except
argument_list|(
name|rval
operator|.
name|toNodeSet
argument_list|()
argument_list|)
return|;
else|else
block|{
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Set
name|set
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|rval
operator|.
name|unorderedIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|set
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|lval
operator|.
name|unorderedIterator
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
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|set
operator|.
name|contains
argument_list|(
name|next
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
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
name|left
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" except "
argument_list|)
expr_stmt|;
name|right
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

