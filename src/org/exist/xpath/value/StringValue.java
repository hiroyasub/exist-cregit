begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|xpath
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
name|xpath
operator|.
name|XPathException
import|;
end_import

begin_class
specifier|public
class|class
name|StringValue
extends|extends
name|AtomicValue
block|{
specifier|public
specifier|final
specifier|static
name|StringValue
name|EMPTY_STRING
init|=
operator|new
name|StringValue
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|private
name|String
name|value
decl_stmt|;
specifier|public
name|StringValue
parameter_list|(
name|String
name|stringValue
parameter_list|)
block|{
name|value
operator|=
name|stringValue
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|STRING
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|==
literal|0
condition|?
name|this
else|:
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#convertTo(int) 	 */
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|requiredType
condition|)
block|{
case|case
name|Type
operator|.
name|ATOMIC
case|:
case|case
name|Type
operator|.
name|ITEM
case|:
case|case
name|Type
operator|.
name|STRING
case|:
return|return
name|this
return|;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert string '"
operator|+
name|value
operator|+
literal|"' to boolean"
argument_list|)
throw|;
case|case
name|Type
operator|.
name|FLOAT
case|:
case|case
name|Type
operator|.
name|DOUBLE
case|:
case|case
name|Type
operator|.
name|NUMBER
case|:
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
case|:
case|case
name|Type
operator|.
name|NON_POSITIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|NEGATIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|LONG
case|:
case|case
name|Type
operator|.
name|INT
case|:
case|case
name|Type
operator|.
name|SHORT
case|:
case|case
name|Type
operator|.
name|BYTE
case|:
case|case
name|Type
operator|.
name|NON_NEGATIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|UNSIGNED_LONG
case|:
case|case
name|Type
operator|.
name|UNSIGNED_INT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_SHORT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_BYTE
case|:
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
argument_list|,
name|requiredType
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert string '"
operator|+
name|value
operator|+
literal|"' to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#compareTo(int, org.exist.xpath.value.AtomicValue) 	 */
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|int
name|cmp
init|=
name|value
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|Constants
operator|.
name|EQ
case|:
return|return
name|cmp
operator|==
literal|0
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
name|cmp
operator|!=
literal|0
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
name|cmp
operator|<
literal|0
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
name|cmp
operator|<=
literal|0
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
name|cmp
operator|>
literal|0
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
name|cmp
operator|>=
literal|0
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot apply operand to string value"
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: operands are not comparable"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#compareTo(org.exist.xpath.value.AtomicValue) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#toString() 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|expand
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|seq
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuffer
name|entityRef
init|=
literal|null
decl_stmt|;
name|char
name|ch
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
name|seq
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|=
name|seq
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'&'
case|:
if|if
condition|(
name|entityRef
operator|==
literal|null
condition|)
name|entityRef
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
else|else
name|entityRef
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|seq
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|ch
operator|=
name|seq
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|!=
literal|';'
condition|)
name|entityRef
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
else|else
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|found
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|expandEntity
argument_list|(
name|entityRef
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
name|char
name|expandEntity
parameter_list|(
name|String
name|buf
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"amp"
argument_list|)
condition|)
return|return
literal|'&'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"lt"
argument_list|)
condition|)
return|return
literal|'<'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"gt"
argument_list|)
condition|)
return|return
literal|'>'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"quot"
argument_list|)
condition|)
return|return
literal|'"'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"apos"
argument_list|)
condition|)
return|return
literal|'\''
return|;
if|else if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|buf
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'#'
condition|)
return|return
name|expandCharRef
argument_list|(
name|buf
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown entity reference: "
operator|+
name|buf
argument_list|)
throw|;
block|}
specifier|private
specifier|final
specifier|static
name|char
name|expandCharRef
parameter_list|(
name|String
name|buf
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|buf
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'x'
condition|)
block|{
comment|// Hex
return|return
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|16
argument_list|)
return|;
block|}
else|else
return|return
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown character reference: "
operator|+
name|buf
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

