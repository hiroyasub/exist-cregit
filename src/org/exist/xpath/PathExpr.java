begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_class
specifier|public
class|class
name|PathExpr
extends|extends
name|AbstractExpression
block|{
specifier|protected
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PathExpr
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|keepVirtual
init|=
literal|false
decl_stmt|;
specifier|protected
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|protected
name|LinkedList
name|steps
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|inPredicate
init|=
literal|false
decl_stmt|;
specifier|public
name|PathExpr
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Expression
name|s
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|PathExpr
name|path
parameter_list|)
block|{
name|Expression
name|expr
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|path
operator|.
name|steps
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
name|expr
operator|=
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addPath
parameter_list|(
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
specifier|public
name|void
name|addPredicate
parameter_list|(
name|Predicate
name|pred
parameter_list|)
block|{
name|Expression
name|e
init|=
operator|(
name|Expression
operator|)
name|steps
operator|.
name|getLast
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|Step
condition|)
operator|(
operator|(
name|Step
operator|)
name|e
operator|)
operator|.
name|addPredicate
argument_list|(
name|pred
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Value
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|NodeProxy
name|contextNode
parameter_list|)
block|{
if|if
condition|(
name|docs
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|ValueNodeSet
argument_list|(
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
name|Value
name|r
decl_stmt|;
if|if
condition|(
name|contextSet
operator|!=
literal|null
condition|)
name|r
operator|=
operator|new
name|ValueNodeSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
else|else
name|r
operator|=
operator|new
name|ValueNodeSet
argument_list|(
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|NodeSet
name|set
decl_stmt|;
name|NodeProxy
name|current
decl_stmt|;
name|Expression
name|expr
decl_stmt|;
name|ValueSet
name|values
decl_stmt|;
for|for
control|(
name|Iterator
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
name|set
operator|=
operator|(
name|NodeSet
operator|)
name|r
operator|.
name|getNodeList
argument_list|()
expr_stmt|;
name|expr
operator|=
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|expr
operator|.
name|returnsType
argument_list|()
operator|!=
name|Constants
operator|.
name|TYPE_NODELIST
condition|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|Literal
operator|||
name|expr
operator|instanceof
name|IntNumber
condition|)
return|return
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|set
argument_list|,
literal|null
argument_list|)
return|;
name|values
operator|=
operator|new
name|ValueSet
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|iter2
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|iter2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|iter2
operator|.
name|next
argument_list|()
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|set
argument_list|,
name|current
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
name|r
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
name|docs
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
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
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
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
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|()
block|{
return|return
name|preselect
argument_list|(
name|docs
argument_list|)
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
block|{
name|DocumentSet
name|docs
init|=
name|in_docs
decl_stmt|;
if|if
condition|(
name|docs
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|docs
return|;
for|for
control|(
name|Iterator
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
name|docs
operator|=
operator|(
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|.
name|preselect
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
if|if
condition|(
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|!=
literal|null
condition|)
return|return
operator|(
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|returnsType
argument_list|()
return|;
return|return
name|Constants
operator|.
name|TYPE_NODELIST
return|;
block|}
specifier|public
name|void
name|setDocumentSet
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
specifier|public
name|void
name|setFirstExpression
parameter_list|(
name|Expression
name|s
parameter_list|)
block|{
name|steps
operator|.
name|addFirst
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#setInPredicate(boolean) 	 */
specifier|public
name|void
name|setInPredicate
parameter_list|(
name|boolean
name|inPredicate
parameter_list|)
block|{
name|this
operator|.
name|inPredicate
operator|=
name|inPredicate
expr_stmt|;
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
operator|(
operator|(
name|Expression
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

