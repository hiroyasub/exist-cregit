begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|util
operator|.
name|FastStringBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|FloatingPointConverter
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
name|ErrorCodes
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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FloatValue
extends|extends
name|NumericValue
block|{
comment|// m Ã 2^e, where m is an integer whose absolute value is less than 2^24,
comment|// and e is an integer between -149 and 104, inclusive.
comment|// In addition also -INF, +INF and NaN.
specifier|public
specifier|final
specifier|static
name|FloatValue
name|NaN
init|=
operator|new
name|FloatValue
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FloatValue
name|POSITIVE_INFINITY
init|=
operator|new
name|FloatValue
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FloatValue
name|NEGATIVE_INFINITY
init|=
operator|new
name|FloatValue
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FloatValue
name|ZERO
init|=
operator|new
name|FloatValue
argument_list|(
literal|0.0E0f
argument_list|)
decl_stmt|;
specifier|protected
name|float
name|value
decl_stmt|;
specifier|public
name|FloatValue
parameter_list|(
name|float
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
name|FloatValue
parameter_list|(
name|String
name|stringValue
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|stringValue
operator|.
name|equals
argument_list|(
literal|"INF"
argument_list|)
condition|)
name|value
operator|=
name|Float
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
if|else if
condition|(
name|stringValue
operator|.
name|equals
argument_list|(
literal|"-INF"
argument_list|)
condition|)
name|value
operator|=
name|Float
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
if|else if
condition|(
name|stringValue
operator|.
name|equals
argument_list|(
literal|"NaN"
argument_list|)
condition|)
name|value
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
else|else
name|value
operator|=
name|Float
operator|.
name|parseFloat
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
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"cannot construct "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|this
operator|.
name|getItemType
argument_list|()
argument_list|)
operator|+
literal|" from \""
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/* 	public float getFloat() throws XPathException  { 		return value; 	} 	*/
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#getType()      */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|FLOAT
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
comment|/* 		if (value == Float.POSITIVE_INFINITY) 			return "INF";  		if (value == Float.NEGATIVE_INFINITY) 			return "-INF";		 		String s = String.valueOf(value); 		s = s.replaceAll("\\.0+$", ""); 		return s;	 		*/
name|FastStringBuffer
name|sb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|20
argument_list|)
decl_stmt|;
comment|//0 is a dummy parameter
name|FloatingPointConverter
operator|.
name|appendFloat
argument_list|(
name|sb
argument_list|,
name|value
argument_list|)
operator|.
name|getNormalizedString
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#isNaN() 	 */
specifier|public
name|boolean
name|isNaN
parameter_list|()
block|{
return|return
name|Float
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isInfinite
parameter_list|()
block|{
return|return
name|Float
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isZero
parameter_list|()
block|{
return|return
name|Float
operator|.
name|compare
argument_list|(
name|value
argument_list|,
literal|0f
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
return|;
block|}
specifier|public
name|boolean
name|isNegative
parameter_list|()
block|{
return|return
operator|(
name|Float
operator|.
name|compare
argument_list|(
name|value
argument_list|,
literal|0f
argument_list|)
operator|<
name|Constants
operator|.
name|EQUAL
operator|)
return|;
block|}
specifier|public
name|boolean
name|isPositive
parameter_list|()
block|{
return|return
operator|(
name|Float
operator|.
name|compare
argument_list|(
name|value
argument_list|,
literal|0f
argument_list|)
operator|>
name|Constants
operator|.
name|EQUAL
operator|)
return|;
block|}
specifier|public
name|boolean
name|hasFractionalPart
parameter_list|()
block|{
if|if
condition|(
name|isNaN
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|isInfinite
argument_list|()
condition|)
return|return
literal|false
return|;
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
operator|.
name|hasFractionalPart
argument_list|()
return|;
block|}
empty_stmt|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#convertTo(int) 	 */
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
name|FLOAT
case|:
return|return
name|this
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
case|case
name|Type
operator|.
name|POSITIVE_INTEGER
case|:
if|if
condition|(
operator|!
operator|(
name|Float
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
operator|||
name|Float
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|)
condition|)
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
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOCA0002
argument_list|,
literal|"cannot convert ' xs:float(\""
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"\")' to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
return|return
operator|(
name|value
operator|==
literal|0.0f
operator|||
name|Float
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
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
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"cannot cast '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|this
operator|.
name|getItemType
argument_list|()
argument_list|)
operator|+
literal|"(\""
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"\")' to "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#negate() 	 */
specifier|public
name|NumericValue
name|negate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
argument_list|(
operator|-
name|value
argument_list|)
return|;
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
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|value
argument_list|)
argument_list|)
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
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|value
argument_list|)
argument_list|)
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
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|||
name|Float
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
operator|||
name|value
operator|==
literal|0.0
condition|)
return|return
name|this
return|;
if|if
condition|(
name|value
operator|>
operator|-
literal|0.5
operator|&&
name|value
operator|<
literal|0.0
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
operator|-
literal|0.0
argument_list|)
return|;
if|if
condition|(
name|value
operator|>
name|Integer
operator|.
name|MIN_VALUE
operator|&&
name|value
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
comment|//too big return original value unchanged
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#round(org.exist.xquery.value.IntegerValue) 	 */
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
operator|==
literal|null
condition|)
return|return
name|round
argument_list|()
return|;
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|||
name|Float
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
operator|||
name|value
operator|==
literal|0.0
condition|)
return|return
name|this
return|;
comment|/* use the decimal rounding method */
return|return
operator|(
name|FloatValue
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
name|FLOAT
argument_list|)
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|-
operator|(
operator|(
name|FloatValue
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|+
operator|(
operator|(
name|FloatValue
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
switch|switch
condition|(
name|other
operator|.
name|getType
argument_list|()
condition|)
block|{
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
operator|*
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
name|other
operator|.
name|mult
argument_list|(
name|this
argument_list|)
return|;
default|default:
return|return
name|mult
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
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#div(org.exist.xquery.value.NumericValue) 	 */
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
name|NUMBER
argument_list|)
condition|)
block|{
comment|//Positive or negative zero divided by positive or negative zero returns NaN.
if|if
condition|(
name|this
operator|.
name|isZero
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isZero
argument_list|()
condition|)
return|return
name|NaN
return|;
comment|//A negative number divided by positive zero returns -INF.
if|if
condition|(
name|this
operator|.
name|isNegative
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isZero
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isPositive
argument_list|()
condition|)
return|return
name|NEGATIVE_INFINITY
return|;
comment|//A negative number divided by positive zero returns -INF.
if|if
condition|(
name|this
operator|.
name|isNegative
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isZero
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isNegative
argument_list|()
condition|)
return|return
name|POSITIVE_INFINITY
return|;
comment|//Division of Positive by negative zero returns -INF and INF, respectively.
if|if
condition|(
name|this
operator|.
name|isPositive
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isZero
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isNegative
argument_list|()
condition|)
return|return
name|NEGATIVE_INFINITY
return|;
if|if
condition|(
name|this
operator|.
name|isPositive
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isZero
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isPositive
argument_list|()
condition|)
return|return
name|POSITIVE_INFINITY
return|;
comment|//Also, INF or -INF divided by INF or -INF returns NaN.
if|if
condition|(
name|this
operator|.
name|isInfinite
argument_list|()
operator|&&
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isInfinite
argument_list|()
condition|)
return|return
name|NaN
return|;
block|}
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|/
operator|(
operator|(
name|FloatValue
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
comment|/* 		if (Type.subTypeOf(other.getType(), Type.FLOAT)) { 			float result = value / ((FloatValue) other).value; 			if (result == Float.NaN || result == Float.POSITIVE_INFINITY || result == Float.NEGATIVE_INFINITY) 				throw new XPathException("illegal arguments to idiv"); 			return new IntegerValue(new BigDecimal(result).toBigInteger(), Type.INTEGER); 		} 		throw new XPathException("idiv called with incompatible argument type: " + getType() + " vs " + other.getType()); 		*/
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|%
operator|(
operator|(
name|FloatValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#abs() 	 */
specifier|public
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|FloatValue
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
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|FloatValue
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
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
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
argument_list|<
name|?
argument_list|>
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|FloatValue
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
literal|2
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
literal|1
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
argument_list|<
name|?
argument_list|>
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
name|FloatValue
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
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|)
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
name|Integer
operator|.
name|valueOf
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
name|Short
operator|.
name|valueOf
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
name|Byte
operator|.
name|valueOf
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
name|FLOAT
argument_list|)
condition|)
return|return
name|Float
operator|.
name|compare
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|FloatValue
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
operator|<
name|other
operator|.
name|getType
argument_list|()
condition|?
name|Constants
operator|.
name|INFERIOR
else|:
name|Constants
operator|.
name|SUPERIOR
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

