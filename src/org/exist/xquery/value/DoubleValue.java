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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_class
specifier|public
class|class
name|DoubleValue
extends|extends
name|NumericValue
block|{
specifier|public
specifier|final
specifier|static
name|DoubleValue
name|NaN
init|=
operator|new
name|DoubleValue
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|DoubleValue
name|ZERO
init|=
operator|new
name|DoubleValue
argument_list|(
literal|0.0E0
argument_list|)
decl_stmt|;
specifier|private
name|double
name|value
decl_stmt|;
specifier|public
name|DoubleValue
parameter_list|(
name|double
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
name|DoubleValue
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
name|Double
operator|.
name|parseDouble
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
literal|"cannot convert string '"
operator|+
name|stringValue
operator|+
literal|"' into a double"
argument_list|)
throw|;
block|}
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
name|DOUBLE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
comment|//	public String getStringValue() throws XPathException {
comment|//		return Double.toString(value);
comment|//	}
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
operator|&&
operator|(
name|value
operator|>=
operator|(
name|double
operator|)
operator|(
literal|1L
operator|<<
literal|53
operator|)
operator|||
operator|-
name|value
operator|>=
operator|(
name|double
operator|)
operator|(
literal|1L
operator|<<
literal|53
operator|)
operator|)
condition|)
block|{
return|return
operator|new
name|java
operator|.
name|math
operator|.
name|BigDecimal
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
name|String
name|s
init|=
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|2
argument_list|)
operator|==
literal|'.'
operator|&&
name|s
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
operator|==
literal|'0'
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"-0.0"
argument_list|)
condition|)
return|return
literal|"0"
return|;
return|return
name|s
return|;
block|}
name|int
name|e
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|'E'
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"Infinity"
argument_list|)
condition|)
block|{
return|return
literal|"INF"
return|;
block|}
if|else if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"-Infinity"
argument_list|)
condition|)
block|{
return|return
literal|"-INF"
return|;
block|}
comment|// For some reason, Double.toString() in Java can return strings such as "0.0040"
comment|// so we remove any trailing zeros
while|while
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
operator|==
literal|'0'
operator|&&
name|s
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|2
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
operator|--
name|len
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
name|int
name|exp
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|e
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|sign
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
name|sign
operator|=
literal|"-"
expr_stmt|;
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
operator|--
name|e
expr_stmt|;
block|}
else|else
name|sign
operator|=
literal|""
expr_stmt|;
name|int
name|nDigits
init|=
name|e
operator|-
literal|2
decl_stmt|;
if|if
condition|(
name|exp
operator|>=
name|nDigits
condition|)
block|{
return|return
name|sign
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
name|e
argument_list|)
operator|+
name|zeros
argument_list|(
name|exp
operator|-
name|nDigits
argument_list|)
return|;
block|}
if|else if
condition|(
name|exp
operator|>
literal|0
condition|)
block|{
return|return
name|sign
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|2
operator|+
name|exp
argument_list|)
operator|+
literal|"."
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|2
operator|+
name|exp
argument_list|,
name|e
argument_list|)
return|;
block|}
else|else
block|{
while|while
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|e
operator|-
literal|1
argument_list|)
operator|==
literal|'0'
condition|)
name|e
operator|--
expr_stmt|;
return|return
name|sign
operator|+
literal|"0."
operator|+
name|zeros
argument_list|(
operator|-
literal|1
operator|-
name|exp
argument_list|)
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
specifier|static
specifier|private
name|String
name|zeros
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|n
index|]
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
name|n
condition|;
name|i
operator|++
control|)
name|buf
index|[
name|i
index|]
operator|=
literal|'0'
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
specifier|public
name|double
name|getValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#isNaN() 	 */
specifier|public
name|boolean
name|isNaN
parameter_list|()
block|{
return|return
name|value
operator|==
name|Double
operator|.
name|NaN
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
name|NUMBER
case|:
case|case
name|Type
operator|.
name|DOUBLE
case|:
return|return
name|this
return|;
case|case
name|Type
operator|.
name|FLOAT
case|:
if|if
condition|(
name|value
argument_list|<
name|Float
operator|.
name|MIN_VALUE
operator|||
name|value
argument_list|>
name|Float
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value is out of range for type xs:float"
argument_list|)
throw|;
return|return
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
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
case|case
name|Type
operator|.
name|POSITIVE_INTEGER
case|:
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|,
name|requiredType
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
literal|0.0
operator|&&
name|value
operator|==
name|Double
operator|.
name|NaN
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
literal|"cannot convert double value '"
operator|+
name|value
operator|+
literal|"' into "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|!
operator|(
name|value
operator|==
literal|0
operator|||
name|value
operator|==
name|Double
operator|.
name|NaN
operator|)
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
name|value
return|;
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
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
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
operator|(
name|long
operator|)
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|double
name|val
parameter_list|)
block|{
name|value
operator|=
name|val
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#ceiling() 	 */
specifier|public
name|NumericValue
name|ceiling
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#floor() 	 */
specifier|public
name|NumericValue
name|floor
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|floor
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#round() 	 */
specifier|public
name|NumericValue
name|round
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#minus(org.exist.xpath.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|-
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|minus
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#plus(org.exist.xpath.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|+
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|plus
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#mult(org.exist.xpath.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|*
operator|(
operator|(
name|DoubleValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#div(org.exist.xpath.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|/
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|div
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|%
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|mod
argument_list|(
operator|(
name|NumericValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#negate() 	 */
specifier|public
name|NumericValue
name|negate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
operator|-
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#abs() 	 */
specifier|public
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#max(org.exist.xpath.value.AtomicValue) 	 */
specifier|public
name|AtomicValue
name|max
parameter_list|(
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
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
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|AtomicValue
name|min
parameter_list|(
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
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
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#conversionPreference(java.lang.Class) 	 */
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
name|DoubleValue
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
literal|3
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
literal|4
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
literal|5
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
literal|6
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
literal|1
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
literal|2
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#toJavaObject(java.lang.Class) 	 */
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
name|DoubleValue
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
return|return
operator|new
name|Double
argument_list|(
name|value
argument_list|)
return|;
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
return|return
operator|new
name|Float
argument_list|(
name|value
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
name|getValue
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
name|getValue
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
name|getValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|String
operator|.
name|class
condition|)
return|return
name|getStringValue
argument_list|()
return|;
if|else if
condition|(
name|target
operator|==
name|Boolean
operator|.
name|class
condition|)
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|effectiveBooleanValue
argument_list|()
argument_list|)
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
operator|new
name|Double
argument_list|(
name|value
argument_list|)
return|;
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
block|}
end_class

end_unit

