begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|DBBroker
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
name|ComputableValue
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
name|IntegerValue
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
name|Type
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
specifier|protected
name|int
name|operator
init|=
name|Constants
operator|.
name|PLUS
decl_stmt|;
specifier|protected
name|int
name|returnType
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
name|XQueryContext
name|context
parameter_list|,
name|int
name|operator
parameter_list|)
block|{
name|super
argument_list|(
name|context
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
name|XQueryContext
name|context
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
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|returnType
operator|=
name|Type
operator|.
name|ATOMIC
expr_stmt|;
name|int
name|ltype
init|=
name|left
operator|.
name|returnsType
argument_list|()
decl_stmt|;
name|int
name|rtype
init|=
name|right
operator|.
name|returnsType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
name|left
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|ltype
operator|=
name|Type
operator|.
name|ATOMIC
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
name|right
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|right
argument_list|)
expr_stmt|;
name|rtype
operator|=
name|Type
operator|.
name|ATOMIC
expr_stmt|;
block|}
comment|// check for date and time operands
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|TIME
argument_list|)
condition|)
block|{
name|returnType
operator|=
name|ltype
expr_stmt|;
comment|// select best return type for numeric operands
block|}
else|else
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
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
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
name|ltype
operator|>
name|rtype
condition|)
block|{
name|right
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|ltype
argument_list|,
name|right
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|ltype
expr_stmt|;
block|}
if|else if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|left
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|rtype
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|rtype
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
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
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|left
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|rtype
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|rtype
expr_stmt|;
block|}
if|else if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|right
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|ltype
argument_list|,
name|right
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|ltype
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// if we still have no return type, use the return type of the left expression
if|if
condition|(
name|returnType
operator|==
name|Type
operator|.
name|ATOMIC
condition|)
name|returnType
operator|=
name|left
operator|.
name|returnsType
argument_list|()
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
name|returnType
return|;
block|}
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|Sequence
name|lseq
init|=
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|lseq
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|Sequence
name|rseq
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|rseq
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|Item
name|lvalue
init|=
name|lseq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|,
name|rvalue
init|=
name|rseq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// runtime type checks:
if|if
condition|(
operator|!
operator|(
name|lvalue
operator|instanceof
name|ComputableValue
operator|)
condition|)
name|lvalue
operator|=
name|operator
operator|==
name|Constants
operator|.
name|IDIV
condition|?
name|lvalue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
else|:
name|lvalue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|rvalue
operator|instanceof
name|ComputableValue
operator|)
condition|)
name|rvalue
operator|=
name|operator
operator|==
name|Constants
operator|.
name|IDIV
condition|?
name|rvalue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
else|:
name|rvalue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|int
name|ltype
init|=
name|lvalue
operator|.
name|getType
argument_list|()
decl_stmt|,
name|rtype
init|=
name|rvalue
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|rvalue
operator|=
name|rvalue
operator|.
name|convertTo
argument_list|(
name|ltype
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ltype
operator|>
name|rtype
condition|)
block|{
name|rvalue
operator|=
name|rvalue
operator|.
name|convertTo
argument_list|(
name|ltype
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|lvalue
operator|=
name|lvalue
operator|.
name|convertTo
argument_list|(
name|rtype
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|lvalue
operator|=
name|lvalue
operator|.
name|convertTo
argument_list|(
name|rtype
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|lvalue
operator|=
name|lvalue
operator|.
name|convertTo
argument_list|(
name|rtype
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|rtype
operator|>
name|ltype
condition|)
block|{
name|rvalue
operator|=
name|rvalue
operator|.
name|convertTo
argument_list|(
name|ltype
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|operator
operator|==
name|Constants
operator|.
name|IDIV
condition|)
return|return
operator|(
operator|(
name|IntegerValue
operator|)
name|lvalue
operator|)
operator|.
name|idiv
argument_list|(
operator|(
name|NumericValue
operator|)
name|rvalue
argument_list|)
return|;
else|else
return|return
name|applyOperator
argument_list|(
operator|(
name|ComputableValue
operator|)
name|lvalue
argument_list|,
operator|(
name|ComputableValue
operator|)
name|rvalue
argument_list|)
return|;
block|}
specifier|public
name|ComputableValue
name|applyOperator
parameter_list|(
name|ComputableValue
name|left
parameter_list|,
name|ComputableValue
name|right
parameter_list|)
throws|throws
name|XPathException
block|{
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
operator|.
name|minus
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|PLUS
case|:
return|return
name|left
operator|.
name|plus
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|MULT
case|:
return|return
name|left
operator|.
name|mult
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|DIV
case|:
return|return
name|left
operator|.
name|div
argument_list|(
name|right
argument_list|)
return|;
case|case
name|Constants
operator|.
name|MOD
case|:
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|left
operator|)
operator|.
name|mod
argument_list|(
operator|(
name|NumericValue
operator|)
name|right
argument_list|)
return|;
default|default :
return|return
literal|null
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
literal|' '
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
literal|' '
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

