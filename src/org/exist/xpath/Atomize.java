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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Atomize
extends|extends
name|AbstractExpression
block|{
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|public
name|Atomize
parameter_list|(
name|StaticContext
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.xpath.StaticContext, org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
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
name|seq
init|=
name|expression
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Item
name|next
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
name|seq
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
name|result
operator|.
name|add
argument_list|(
name|next
operator|.
name|atomize
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#preselect(org.exist.dom.DocumentSet, org.exist.xpath.StaticContext) 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|expression
operator|.
name|preselect
argument_list|(
name|in_docs
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#pprint() 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|"#atomize("
operator|+
name|expression
operator|.
name|pprint
argument_list|()
operator|+
literal|')'
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ATOMIC
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getDependencies
argument_list|()
return|;
block|}
block|}
end_class

end_unit

