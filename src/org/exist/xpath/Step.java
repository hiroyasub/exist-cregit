begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *   *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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
specifier|abstract
class|class
name|Step
extends|extends
name|AbstractExpression
block|{
specifier|protected
name|int
name|axis
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|protected
name|ArrayList
name|predicates
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|NodeTest
name|test
decl_stmt|;
specifier|protected
name|boolean
name|inPredicate
init|=
literal|false
decl_stmt|;
specifier|public
name|Step
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|axis
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|axis
operator|=
name|axis
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
specifier|public
name|Step
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|axis
parameter_list|,
name|NodeTest
name|test
parameter_list|)
block|{
name|this
argument_list|(
name|pool
argument_list|,
name|axis
argument_list|)
expr_stmt|;
name|this
operator|.
name|test
operator|=
name|test
expr_stmt|;
block|}
specifier|public
name|void
name|addPredicate
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|predicates
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
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
throws|throws
name|XPathException
function_decl|;
specifier|public
name|int
name|getAxis
parameter_list|()
block|{
return|return
name|axis
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
if|if
condition|(
name|axis
operator|>
operator|-
literal|1
condition|)
name|buf
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|AXISSPECIFIERS
index|[
name|axis
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"::"
argument_list|)
expr_stmt|;
if|if
condition|(
name|test
operator|!=
literal|null
condition|)
name|buf
operator|.
name|append
argument_list|(
name|test
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
if|if
condition|(
name|predicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
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
name|buf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
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
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
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
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
name|DocumentSet
name|out_docs
init|=
name|in_docs
decl_stmt|;
if|if
condition|(
name|predicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
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
name|out_docs
operator|=
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
name|preselect
argument_list|(
name|out_docs
argument_list|)
expr_stmt|;
return|return
name|out_docs
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|TYPE_NODELIST
return|;
block|}
specifier|public
name|void
name|setAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|this
operator|.
name|axis
operator|=
name|axis
expr_stmt|;
block|}
specifier|public
name|void
name|setTest
parameter_list|(
name|NodeTest
name|test
parameter_list|)
block|{
name|this
operator|.
name|test
operator|=
name|test
expr_stmt|;
block|}
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
block|}
block|}
end_class

end_unit

