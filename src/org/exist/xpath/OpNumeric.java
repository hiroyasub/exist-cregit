begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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
name|apache
operator|.
name|log4j
operator|.
name|Category
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
name|dom
operator|.
name|SingleNodeSet
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * numeric operation on two operands by +, -, *, div, mod etc..  *  */
end_comment

begin_class
specifier|public
class|class
name|OpNumeric
extends|extends
name|BinaryOp
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|OpEquals
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|operator
init|=
name|Constants
operator|.
name|PLUS
decl_stmt|;
specifier|protected
name|NodeSet
name|temp
init|=
literal|null
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|OpNumeric
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|operator
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
block|}
specifier|public
name|OpNumeric
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|int
name|operator
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|add
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|TYPE_NUM
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
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|in_docs
return|;
name|DocumentSet
name|out_docs
init|=
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|preselect
argument_list|(
name|in_docs
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
name|out_docs
operator|=
name|out_docs
operator|.
name|union
argument_list|(
name|getExpression
argument_list|(
name|i
argument_list|)
operator|.
name|preselect
argument_list|(
name|out_docs
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|out_docs
return|;
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
name|node
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
name|contextSet
operator|=
operator|new
name|SingleNodeSet
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|double
name|lvalue
init|=
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|)
operator|.
name|getNumericValue
argument_list|()
decl_stmt|;
name|double
name|rvalue
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|)
operator|.
name|getNumericValue
argument_list|()
decl_stmt|;
name|double
name|result
init|=
name|applyOperator
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueNumber
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|public
name|double
name|applyOperator
parameter_list|(
name|double
name|left
parameter_list|,
name|double
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|==
name|Double
operator|.
name|NaN
operator|||
name|right
operator|==
name|Double
operator|.
name|NaN
condition|)
return|return
name|Double
operator|.
name|NaN
return|;
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|Constants
operator|.
name|MINUS
case|:
return|return
name|left
operator|-
name|right
return|;
case|case
name|Constants
operator|.
name|PLUS
case|:
return|return
name|left
operator|+
name|right
return|;
case|case
name|Constants
operator|.
name|MULT
case|:
return|return
name|left
operator|*
name|right
return|;
case|case
name|Constants
operator|.
name|DIV
case|:
return|return
name|left
operator|/
name|right
return|;
case|case
name|Constants
operator|.
name|MOD
case|:
return|return
name|left
operator|%
name|right
return|;
default|default:
return|return
name|Double
operator|.
name|NaN
return|;
block|}
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
name|getLeft
argument_list|()
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getRight
argument_list|()
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

