begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|xquery
operator|.
name|value
operator|.
name|AtomicValue
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
name|StringValue
import|;
end_import

begin_comment
comment|/**  * Node constructor for attribute nodes.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|AttributeConstructor
extends|extends
name|NodeConstructor
block|{
name|String
name|qname
decl_stmt|;
name|List
name|contents
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|boolean
name|isNamespaceDecl
init|=
literal|false
decl_stmt|;
specifier|public
name|AttributeConstructor
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
name|isNamespaceDecl
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|addValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|contents
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addEnclosedExpr
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isNamespaceDecl
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"enclosed expressions are not allowed in namespace "
operator|+
literal|"declaration attributes"
argument_list|)
throw|;
name|contents
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getQName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
specifier|public
name|boolean
name|isNamespaceDeclaration
parameter_list|()
block|{
return|return
name|isNamespaceDecl
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Object
name|next
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contents
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
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|Expression
condition|)
name|evalEnclosedExpr
argument_list|(
operator|(
operator|(
name|Expression
operator|)
name|next
operator|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
argument_list|,
name|buf
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|StringValue
name|result
init|=
operator|new
name|StringValue
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|expand
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|void
name|evalEnclosedExpr
parameter_list|(
name|Sequence
name|seq
parameter_list|,
name|StringBuffer
name|buf
parameter_list|)
throws|throws
name|XPathException
block|{
name|Item
name|item
decl_stmt|;
name|AtomicValue
name|atomic
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
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|atomic
operator|=
name|item
operator|.
name|atomize
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|atomic
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * If this is a namespace declaration attribute, return 	 * the single string value of the attribute. 	 *  	 * @return 	 */
specifier|public
name|String
name|getLiteralValue
parameter_list|()
block|{
if|if
condition|(
name|contents
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
return|return
operator|(
name|String
operator|)
name|contents
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#pprint() 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|qname
argument_list|)
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|Object
name|next
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contents
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
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|Expression
condition|)
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Expression
operator|)
name|next
operator|)
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.NodeConstructor#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|Object
name|object
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contents
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
name|object
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|object
operator|instanceof
name|Expression
condition|)
operator|(
operator|(
name|Expression
operator|)
name|object
operator|)
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

