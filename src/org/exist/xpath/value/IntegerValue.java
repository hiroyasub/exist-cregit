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
name|XPathException
import|;
end_import

begin_class
specifier|public
class|class
name|IntegerValue
extends|extends
name|NumericValue
block|{
specifier|public
specifier|final
specifier|static
name|IntegerValue
name|ZERO
init|=
operator|new
name|IntegerValue
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|long
name|value
decl_stmt|;
specifier|private
name|int
name|type
init|=
name|Type
operator|.
name|INTEGER
decl_stmt|;
specifier|public
name|IntegerValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|IntegerValue
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
operator|!
name|checkType
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value is not a valid integer for type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
block|}
specifier|public
name|IntegerValue
parameter_list|(
name|String
name|stringValue
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|value
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
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
literal|"failed to convert '"
operator|+
name|stringValue
operator|+
literal|"' to an integer"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|IntegerValue
parameter_list|(
name|String
name|stringValue
parameter_list|,
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|type
operator|=
name|requiredType
expr_stmt|;
try|try
block|{
name|value
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
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
literal|"failed to convert '"
operator|+
name|stringValue
operator|+
literal|"' to an integer"
argument_list|)
throw|;
block|}
name|checkType
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|boolean
name|checkType
parameter_list|(
name|long
name|value
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Type
operator|.
name|LONG
case|:
case|case
name|Type
operator|.
name|INTEGER
case|:
case|case
name|Type
operator|.
name|DECIMAL
case|:
return|return
literal|true
return|;
case|case
name|Type
operator|.
name|NON_POSITIVE_INTEGER
case|:
return|return
name|value
operator|<
literal|1
return|;
case|case
name|Type
operator|.
name|NEGATIVE_INTEGER
case|:
return|return
name|value
operator|<
literal|0
return|;
case|case
name|Type
operator|.
name|INT
case|:
return|return
name|value
operator|>=
name|Integer
operator|.
name|MIN_VALUE
operator|&&
name|value
operator|<=
name|Integer
operator|.
name|MAX_VALUE
return|;
case|case
name|Type
operator|.
name|SHORT
case|:
return|return
name|value
operator|>=
name|Short
operator|.
name|MIN_VALUE
operator|&&
name|value
operator|<=
name|Short
operator|.
name|MAX_VALUE
return|;
case|case
name|Type
operator|.
name|BYTE
case|:
return|return
name|value
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|value
operator|<=
name|Byte
operator|.
name|MAX_VALUE
return|;
case|case
name|Type
operator|.
name|NON_NEGATIVE_INTEGER
case|:
return|return
name|value
operator|>
operator|-
literal|1
return|;
case|case
name|Type
operator|.
name|UNSIGNED_LONG
case|:
return|return
name|value
operator|>
operator|-
literal|1
return|;
case|case
name|Type
operator|.
name|UNSIGNED_INT
case|:
return|return
name|value
operator|>
operator|-
literal|1
operator|&&
name|value
operator|<=
name|Integer
operator|.
name|MAX_VALUE
return|;
case|case
name|Type
operator|.
name|UNSIGNED_SHORT
case|:
return|return
name|value
operator|>
operator|-
literal|1
operator|&&
name|value
operator|<=
name|Short
operator|.
name|MAX_VALUE
return|;
case|case
name|Type
operator|.
name|UNSIGNED_BYTE
case|:
return|return
name|value
operator|>
operator|-
literal|1
operator|&&
name|value
operator|<=
name|Byte
operator|.
name|MAX_VALUE
return|;
case|case
name|Type
operator|.
name|POSITIVE_INTEGER
case|:
return|return
name|value
operator|>
literal|0
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown type: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
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
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
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
name|DECIMAL
case|:
case|case
name|Type
operator|.
name|NUMBER
case|:
case|case
name|Type
operator|.
name|INTEGER
case|:
case|case
name|Type
operator|.
name|LONG
case|:
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
return|return
name|this
return|;
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
case|case
name|Type
operator|.
name|POSITIVE_INTEGER
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
case|case
name|Type
operator|.
name|DOUBLE
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
name|STRING
case|:
return|return
operator|new
name|StringValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
return|return
operator|(
name|value
operator|==
literal|0
operator|)
condition|?
name|BooleanValue
operator|.
name|FALSE
else|:
name|BooleanValue
operator|.
name|TRUE
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert integer '"
operator|+
name|value
operator|+
literal|"' to "
operator|+
name|requiredType
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#getInt() 	 */
specifier|public
name|int
name|getInt
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#getLong() 	 */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#getDouble() 	 */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|double
operator|)
name|value
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
operator|!=
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#ceiling() 	 */
specifier|public
name|NumericValue
name|ceiling
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#floor() 	 */
specifier|public
name|NumericValue
name|floor
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#round() 	 */
specifier|public
name|NumericValue
name|round
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#minus(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|minus
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|IntegerValue
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|-
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|minus
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#plus(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|plus
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|IntegerValue
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|+
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|plus
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#mult(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|mult
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|IntegerValue
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|*
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|mult
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#div(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|div
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|IntegerValue
condition|)
block|{
name|long
name|ov
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|ov
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"division by zero"
argument_list|)
throw|;
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|/
name|ov
argument_list|)
return|;
block|}
else|else
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|div
argument_list|(
name|other
argument_list|)
return|;
block|}
specifier|public
name|NumericValue
name|idiv
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|IntegerValue
condition|)
block|{
name|long
name|ov
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|ov
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"division by zero"
argument_list|)
throw|;
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|/
name|ov
argument_list|)
return|;
block|}
else|else
return|return
operator|(
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|idiv
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#mod(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|mod
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|IntegerValue
condition|)
block|{
name|long
name|ov
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|ov
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"division by zero"
argument_list|)
throw|;
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|%
name|ov
argument_list|)
return|;
block|}
else|else
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|mod
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#unaryMinus() 	 */
specifier|public
name|NumericValue
name|negate
parameter_list|()
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
operator|-
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

