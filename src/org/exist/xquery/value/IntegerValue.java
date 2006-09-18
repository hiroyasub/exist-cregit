begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|Constants
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
name|XPathException
import|;
end_import

begin_comment
comment|/** [Definition:]   integer is<i>derived</i> from decimal by fixing the value of<i>fractionDigits<i> to be 0.   * This results in the standard mathematical concept of the integer numbers.   * The<i>value space</i> of integer is the infinite set {...,-2,-1,0,1,2,...}.   * The<i>base type</i> of integer is decimal.  * cf http://www.w3.org/TR/xmlschema-2/#integer   */
end_comment

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
specifier|static
specifier|final
name|BigInteger
name|ZERO_BIGINTEGER
init|=
operator|new
name|BigInteger
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|ONE_BIGINTEGER
init|=
operator|new
name|BigInteger
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|MINUS_ONE_BIGINTEGER
init|=
operator|new
name|BigInteger
argument_list|(
literal|"-1"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|LARGEST_LONG
init|=
operator|new
name|BigInteger
argument_list|(
literal|"9223372036854775808"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|SMALLEST_LONG
init|=
name|LARGEST_LONG
operator|.
name|negate
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|LARGEST_INT
init|=
operator|new
name|BigInteger
argument_list|(
literal|"4294967296"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|SMALLEST_INT
init|=
name|LARGEST_INT
operator|.
name|negate
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|LARGEST_SHORT
init|=
operator|new
name|BigInteger
argument_list|(
literal|"65536"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|SMALLEST_SHORT
init|=
name|LARGEST_SHORT
operator|.
name|negate
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|LARGEST_BYTE
init|=
operator|new
name|BigInteger
argument_list|(
literal|"256"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BigInteger
name|SMALLEST_BYTE
init|=
name|LARGEST_BYTE
operator|.
name|negate
argument_list|()
decl_stmt|;
specifier|private
name|BigInteger
name|value
decl_stmt|;
comment|// 	private long value;
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
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|// new BigInteger(value);
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
operator|new
name|BigInteger
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
comment|// Long.parseLong(stringValue);
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
literal|"' to an integer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
comment|//			}
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
operator|new
name|BigInteger
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
comment|// Long.parseLong(stringValue);
if|if
condition|(
operator|!
operator|(
name|checkType
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: can not convert '"
operator|+
name|stringValue
operator|+
literal|"' to "
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
literal|"' to an integer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * @param value 	 * @param requiredType 	 */
specifier|public
name|IntegerValue
parameter_list|(
name|BigInteger
name|value
parameter_list|,
name|int
name|requiredType
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|type
operator|=
name|requiredType
expr_stmt|;
block|}
comment|/** 	 * @param integer 	 */
specifier|public
name|IntegerValue
parameter_list|(
name|BigInteger
name|integer
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|integer
expr_stmt|;
block|}
comment|/** 	 * @param value2 	 * @param type2 	 * @throws XPathException 	 */
specifier|private
name|boolean
name|checkType
parameter_list|(
name|BigInteger
name|value2
parameter_list|,
name|int
name|type2
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
comment|// jmv: add test since now long is not the default implementation anymore:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|SMALLEST_LONG
argument_list|)
operator|!=
name|Constants
operator|.
name|INFERIOR
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_LONG
argument_list|)
operator|!=
name|Constants
operator|.
name|SUPERIOR
return|;
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
name|POSITIVE_INTEGER
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|ZERO_BIGINTEGER
argument_list|)
operator|==
literal|1
return|;
comment|//>0
case|case
name|Type
operator|.
name|NON_NEGATIVE_INTEGER
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|MINUS_ONE_BIGINTEGER
argument_list|)
operator|==
literal|1
return|;
comment|//> -1
case|case
name|Type
operator|.
name|NEGATIVE_INTEGER
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|ZERO_BIGINTEGER
argument_list|)
operator|==
operator|-
literal|1
return|;
comment|//<0
case|case
name|Type
operator|.
name|NON_POSITIVE_INTEGER
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|ONE_BIGINTEGER
argument_list|)
operator|==
operator|-
literal|1
return|;
comment|//<1
case|case
name|Type
operator|.
name|INT
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|SMALLEST_INT
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_INT
argument_list|)
operator|==
operator|-
literal|1
return|;
case|case
name|Type
operator|.
name|SHORT
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|SMALLEST_SHORT
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_SHORT
argument_list|)
operator|==
operator|-
literal|1
return|;
case|case
name|Type
operator|.
name|BYTE
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|SMALLEST_BYTE
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_BYTE
argument_list|)
operator|==
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
operator|.
name|compareTo
argument_list|(
name|MINUS_ONE_BIGINTEGER
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_LONG
argument_list|)
operator|==
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
operator|.
name|compareTo
argument_list|(
name|MINUS_ONE_BIGINTEGER
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_INT
argument_list|)
operator|==
operator|-
literal|1
return|;
case|case
name|Type
operator|.
name|UNSIGNED_SHORT
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|MINUS_ONE_BIGINTEGER
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_SHORT
argument_list|)
operator|==
operator|-
literal|1
return|;
case|case
name|Type
operator|.
name|UNSIGNED_BYTE
case|:
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|MINUS_ONE_BIGINTEGER
argument_list|)
operator|==
literal|1
operator|&&
name|value
operator|.
name|compareTo
argument_list|(
name|LARGEST_BYTE
argument_list|)
operator|==
operator|-
literal|1
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
operator|-
literal|4294967295L
operator|&&
name|value
operator|<=
literal|4294967295L
return|;
case|case
name|Type
operator|.
name|SHORT
case|:
return|return
name|value
operator|>=
operator|-
literal|65535
operator|&&
name|value
operator|<=
literal|65535
return|;
case|case
name|Type
operator|.
name|BYTE
case|:
return|return
name|value
operator|>=
operator|-
literal|255
operator|&&
name|value
operator|<=
literal|255
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
literal|4294967295L
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
literal|65535
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
literal|255
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
comment|// jmv>= 0;
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#getType() 	 */
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
operator|.
name|longValue
argument_list|()
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
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
comment|// Long.toString(value);
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#convertTo(int) 	 */
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
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Type
operator|.
name|UNTYPED_ATOMIC
case|:
return|return
operator|new
name|UntypedAtomicValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
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
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|FLOAT
case|:
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|.
name|floatValue
argument_list|()
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
operator|.
name|compareTo
argument_list|(
name|ZERO_BIGINTEGER
argument_list|)
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
literal|"cannot convert '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|this
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|value
operator|+
literal|")' "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#getInt() 	 */
specifier|public
name|int
name|getInt
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|intValue
argument_list|()
return|;
comment|// (int) value;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#getLong() 	 */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#getDouble() 	 */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|doubleValue
argument_list|()
return|;
comment|// (double) value;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|value
operator|.
name|compareTo
argument_list|(
name|ZERO_BIGINTEGER
argument_list|)
operator|==
literal|0
operator|)
condition|?
literal|false
else|:
literal|true
return|;
comment|// value != 0;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#ceiling() 	 */
specifier|public
name|NumericValue
name|ceiling
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#floor() 	 */
specifier|public
name|NumericValue
name|floor
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#round() 	 */
specifier|public
name|NumericValue
name|round
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#round(org.exist.xquery.IntegerValue) 	 */
specifier|public
name|NumericValue
name|round
parameter_list|(
name|IntegerValue
name|precision
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|precision
operator|.
name|getInt
argument_list|()
operator|<=
literal|0
condition|)
return|return
operator|(
name|IntegerValue
operator|)
operator|(
operator|(
name|DecimalValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|)
operator|)
operator|.
name|round
argument_list|(
name|precision
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
else|else
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#minus(org.exist.xquery.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|minus
parameter_list|(
name|ComputableValue
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
name|INTEGER
argument_list|)
condition|)
comment|// return new IntegerValue(value - ((IntegerValue) other).value, type);
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|subtract
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#plus(org.exist.xquery.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|plus
parameter_list|(
name|ComputableValue
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
name|INTEGER
argument_list|)
condition|)
comment|// return new IntegerValue(value + ((IntegerValue) other).value, type);
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|add
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#mult(org.exist.xquery.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|mult
parameter_list|(
name|ComputableValue
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
name|INTEGER
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|multiply
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
if|else if
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
name|DURATION
argument_list|)
condition|)
return|return
name|other
operator|.
name|mult
argument_list|(
name|this
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
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
comment|/** The div operator performs floating-point division according to IEEE 754. 	 * @see org.exist.xquery.value.NumericValue#idiv(org.exist.xquery.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|div
parameter_list|(
name|ComputableValue
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
if|if
condition|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
operator|==
name|BigInteger
operator|.
name|ZERO
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FOAR0001 : division by zero"
argument_list|)
throw|;
comment|//http://www.w3.org/TR/xpath20/#mapping : numeric; but xs:decimal if both operands are xs:integer
name|BigDecimal
name|d
init|=
operator|new
name|BigDecimal
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|BigDecimal
name|od
init|=
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
decl_stmt|;
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
literal|18
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|d
operator|.
name|scale
argument_list|()
argument_list|,
name|od
operator|.
name|scale
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DecimalValue
argument_list|(
name|d
operator|.
name|divide
argument_list|(
name|od
argument_list|,
name|scale
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_DOWN
argument_list|)
argument_list|)
return|;
block|}
else|else
comment|//TODO : review type promotion
return|return
operator|(
operator|(
name|ComputableValue
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
name|IntegerValue
name|idiv
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
name|ComputableValue
name|result
init|=
name|div
argument_list|(
name|other
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|result
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#mod(org.exist.xquery.value.NumericValue) 	 */
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
name|INTEGER
argument_list|)
condition|)
block|{
comment|// long ov = ((IntegerValue) other).value.longValue();
name|BigInteger
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
operator|!
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
comment|// if (ov == 0)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"division by zero"
argument_list|)
throw|;
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|remainder
argument_list|(
name|ov
argument_list|)
argument_list|,
name|type
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#unaryMinus() 	 */
specifier|public
name|NumericValue
name|negate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|negate
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#abs() 	 */
specifier|public
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
block|{
comment|// return new IntegerValue(Math.abs(value), type);
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|abs
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#max(org.exist.xquery.value.AtomicValue) 	 */
specifier|public
name|AtomicValue
name|max
parameter_list|(
name|Collator
name|collator
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
name|INTEGER
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|max
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
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
name|max
argument_list|(
name|collator
argument_list|,
name|other
argument_list|)
return|;
block|}
specifier|public
name|AtomicValue
name|min
parameter_list|(
name|Collator
name|collator
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
name|INTEGER
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|min
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
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
name|min
argument_list|(
name|collator
argument_list|,
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class) 	 */
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|IntegerValue
operator|.
name|class
argument_list|)
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|javaClass
operator|==
name|Long
operator|.
name|class
operator|||
name|javaClass
operator|==
name|long
operator|.
name|class
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|javaClass
operator|==
name|Integer
operator|.
name|class
operator|||
name|javaClass
operator|==
name|int
operator|.
name|class
condition|)
return|return
literal|2
return|;
if|if
condition|(
name|javaClass
operator|==
name|Short
operator|.
name|class
operator|||
name|javaClass
operator|==
name|short
operator|.
name|class
condition|)
return|return
literal|3
return|;
if|if
condition|(
name|javaClass
operator|==
name|Byte
operator|.
name|class
operator|||
name|javaClass
operator|==
name|byte
operator|.
name|class
condition|)
return|return
literal|4
return|;
if|if
condition|(
name|javaClass
operator|==
name|Double
operator|.
name|class
operator|||
name|javaClass
operator|==
name|double
operator|.
name|class
condition|)
return|return
literal|5
return|;
if|if
condition|(
name|javaClass
operator|==
name|Float
operator|.
name|class
operator|||
name|javaClass
operator|==
name|float
operator|.
name|class
condition|)
return|return
literal|6
return|;
if|if
condition|(
name|javaClass
operator|==
name|String
operator|.
name|class
condition|)
return|return
literal|7
return|;
if|if
condition|(
name|javaClass
operator|==
name|Boolean
operator|.
name|class
operator|||
name|javaClass
operator|==
name|boolean
operator|.
name|class
condition|)
return|return
literal|8
return|;
if|if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
return|return
literal|20
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class) 	 */
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
name|target
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|IntegerValue
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|else if
condition|(
name|target
operator|==
name|Long
operator|.
name|class
operator|||
name|target
operator|==
name|long
operator|.
name|class
condition|)
comment|// ?? jmv: return new Long(value);
return|return
operator|new
name|Long
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|target
operator|==
name|Integer
operator|.
name|class
operator|||
name|target
operator|==
name|int
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|INT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Integer
argument_list|(
operator|(
name|int
operator|)
name|v
operator|.
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Short
operator|.
name|class
operator|||
name|target
operator|==
name|short
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|SHORT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
name|v
operator|.
name|value
operator|.
name|shortValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Byte
operator|.
name|class
operator|||
name|target
operator|==
name|byte
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|BYTE
argument_list|)
decl_stmt|;
return|return
operator|new
name|Byte
argument_list|(
operator|(
name|byte
operator|)
name|v
operator|.
name|value
operator|.
name|byteValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Double
operator|.
name|class
operator|||
name|target
operator|==
name|double
operator|.
name|class
condition|)
block|{
name|DoubleValue
name|v
init|=
operator|(
name|DoubleValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
return|return
operator|new
name|Double
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Float
operator|.
name|class
operator|||
name|target
operator|==
name|float
operator|.
name|class
condition|)
block|{
name|FloatValue
name|v
init|=
operator|(
name|FloatValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|FLOAT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Float
argument_list|(
name|v
operator|.
name|value
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Boolean
operator|.
name|class
operator|||
name|target
operator|==
name|boolean
operator|.
name|class
condition|)
return|return
operator|new
name|BooleanValue
argument_list|(
name|effectiveBooleanValue
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|target
operator|==
name|String
operator|.
name|class
condition|)
comment|// return Long.toString(value);
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
if|else if
condition|(
name|target
operator|==
name|Object
operator|.
name|class
condition|)
return|return
name|value
return|;
comment|// Long(value);
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to Java object of type "
operator|+
name|target
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)      * @see java.lang.Comparable#compareTo(java.lang.Object)      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|AtomicValue
name|other
init|=
operator|(
name|AtomicValue
operator|)
name|o
decl_stmt|;
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
name|INTEGER
argument_list|)
condition|)
return|return
name|value
operator|.
name|compareTo
argument_list|(
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
name|getType
argument_list|()
operator|>
name|other
operator|.
name|getType
argument_list|()
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

