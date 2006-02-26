begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DecimalValue
extends|extends
name|NumericValue
block|{
comment|//Copied from Saxon 8.6.1
specifier|private
specifier|static
specifier|final
name|int
name|DIVIDE_PRECISION
init|=
literal|18
decl_stmt|;
name|BigDecimal
name|value
decl_stmt|;
specifier|public
name|DecimalValue
parameter_list|(
name|BigDecimal
name|decimal
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|decimal
expr_stmt|;
name|loseTrailingZeros
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DecimalValue
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|value
operator|=
operator|new
name|BigDecimal
argument_list|(
name|str
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
literal|"Type error: "
operator|+
name|str
operator|+
literal|" cannot be cast to a decimal"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|DecimalValue
parameter_list|(
name|double
name|val
parameter_list|)
block|{
name|value
operator|=
operator|new
name|BigDecimal
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|DECIMAL
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
comment|/* 		String s = value.toString(); 		while (s.length()> 0&& s.indexOf('.') != Constants.STRING_NOT_FOUND&&  				s.charAt(s.length() - 1) == '0') { 			s = s.substring(0, s.length() - 1); 		} 		if (s.length()> 0&&  s.charAt(s.length() - 1 ) == '.') 			s = s.substring(0, s.length() - 1); 		return s; 		*/
comment|//Copied from Saxon 8.6.1
comment|// Can't use the plain BigDecimal#toString() under JDK 1.5 because this produces values like "1E-5".
comment|// JDK 1.5 offers BigDecimal#toPlainString() which might do the job directly
if|if
condition|(
name|value
operator|.
name|scale
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
name|boolean
name|negative
init|=
name|value
operator|.
name|signum
argument_list|()
operator|<
literal|0
decl_stmt|;
name|String
name|s
init|=
name|value
operator|.
name|abs
argument_list|()
operator|.
name|unscaledValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|scale
init|=
name|value
operator|.
name|scale
argument_list|()
decl_stmt|;
comment|//For some reason this leads to an out of memory error
comment|//FastStringBuffer sb = new FastStringBuffer(len+1);
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|negative
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scale
operator|>=
name|len
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"0."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|len
init|;
name|i
operator|<
name|scale
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
operator|-
name|scale
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|len
operator|-
name|scale
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//End of copy
block|}
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
name|DECIMAL
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
name|value
operator|.
name|longValue
argument_list|()
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
name|value
operator|.
name|signum
argument_list|()
operator|==
literal|0
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#effectiveBooleanValue() 	 */
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
name|signum
argument_list|()
operator|!=
literal|0
return|;
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
name|DecimalValue
argument_list|(
name|value
operator|.
name|negate
argument_list|()
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
name|DecimalValue
argument_list|(
name|value
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_CEILING
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
name|DecimalValue
argument_list|(
name|value
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_FLOOR
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
switch|switch
condition|(
name|value
operator|.
name|signum
argument_list|()
condition|)
block|{
case|case
operator|-
literal|1
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_DOWN
argument_list|)
argument_list|)
return|;
case|case
literal|0
case|:
return|return
name|this
return|;
case|case
literal|1
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_UP
argument_list|)
argument_list|)
return|;
default|default :
return|return
name|this
return|;
block|}
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
name|int
name|pre
init|=
name|precision
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|pre
operator|>=
literal|0
condition|)
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|setScale
argument_list|(
name|pre
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_EVEN
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|movePointRight
argument_list|(
name|pre
argument_list|)
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_EVEN
argument_list|)
operator|.
name|movePointLeft
argument_list|(
name|pre
argument_list|)
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
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|subtract
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
case|:
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
default|default:
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
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|add
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
case|:
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
default|default:
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
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|multiply
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
case|:
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
switch|switch
condition|(
name|other
operator|.
name|getType
argument_list|()
condition|)
block|{
comment|//case Type.DECIMAL:
comment|//return new DecimalValue(value.divide(((DecimalValue) other).value, BigDecimal.ROUND_HALF_UP));
case|case
name|Type
operator|.
name|INTEGER
case|:
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
default|default:
comment|//Copied from Saxon 8.6.1
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
name|DIVIDE_PRECISION
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|value
operator|.
name|scale
argument_list|()
argument_list|,
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
operator|.
name|scale
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BigDecimal
name|result
init|=
name|value
operator|.
name|divide
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|,
name|scale
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_DOWN
argument_list|)
decl_stmt|;
return|return
operator|new
name|DecimalValue
argument_list|(
name|result
argument_list|)
return|;
comment|//End of copy
block|}
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
comment|//Copied from Saxon 8.6.1
if|if
condition|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
operator|.
name|signum
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FOAR0001: division by zero"
argument_list|)
throw|;
name|BigInteger
name|quot
init|=
name|value
operator|.
name|divide
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|,
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_DOWN
argument_list|)
operator|.
name|toBigInteger
argument_list|()
decl_stmt|;
return|return
operator|new
name|IntegerValue
argument_list|(
name|quot
argument_list|)
return|;
comment|//End of copy
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DECIMAL
condition|)
block|{
name|BigDecimal
name|quotient
init|=
name|value
operator|.
name|divide
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|,
name|BigDecimal
operator|.
name|ROUND_DOWN
argument_list|)
decl_stmt|;
name|BigDecimal
name|remainder
init|=
name|value
operator|.
name|subtract
argument_list|(
name|quotient
operator|.
name|multiply
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DecimalValue
argument_list|(
name|remainder
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#abs(org.exist.xquery.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|abs
argument_list|()
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DECIMAL
condition|)
block|{
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|max
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|max
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DECIMAL
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DECIMAL
condition|)
block|{
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|min
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
operator|.
name|min
argument_list|(
operator|(
operator|(
name|DecimalValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|)
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
block|}
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
name|DecimalValue
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
name|BigDecimal
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
literal|4
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
literal|5
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
literal|6
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
literal|7
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
literal|3
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
literal|8
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
literal|9
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
name|DecimalValue
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
name|BigDecimal
operator|.
name|class
condition|)
return|return
name|value
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
operator|.
name|doubleValue
argument_list|()
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
operator|.
name|floatValue
argument_list|()
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
operator|new
name|Long
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
comment|//Copied from Saxon 8.6.1
comment|/**     * Remove insignificant trailing zeros (the Java BigDecimal class retains trailing zeros,     * but the XPath 2.0 xs:decimal type does not)     */
specifier|private
name|void
name|loseTrailingZeros
parameter_list|()
block|{
name|int
name|scale
init|=
name|value
operator|.
name|scale
argument_list|()
decl_stmt|;
if|if
condition|(
name|scale
operator|>
literal|0
condition|)
block|{
name|BigInteger
name|i
init|=
name|value
operator|.
name|unscaledValue
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BigInteger
index|[]
name|dr
init|=
name|i
operator|.
name|divideAndRemainder
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|dr
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|BigInteger
operator|.
name|ZERO
argument_list|)
condition|)
block|{
name|i
operator|=
name|dr
index|[
literal|0
index|]
expr_stmt|;
name|scale
operator|--
expr_stmt|;
if|if
condition|(
name|scale
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|scale
operator|!=
name|value
operator|.
name|scale
argument_list|()
condition|)
block|{
name|value
operator|=
operator|new
name|BigDecimal
argument_list|(
name|i
argument_list|,
name|scale
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//End of copy
block|}
end_class

end_unit

