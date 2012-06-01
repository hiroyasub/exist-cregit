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
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|DatatypeConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|Duration
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
comment|/**  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|YearMonthDurationValue
extends|extends
name|OrderedDurationValue
block|{
specifier|public
specifier|static
specifier|final
name|Duration
name|CANONICAL_ZERO_DURATION
init|=
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDuration
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
name|BigInteger
operator|.
name|ZERO
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|YearMonthDurationValue
parameter_list|(
name|Duration
name|duration
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|duration
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|duration
operator|.
name|equals
argument_list|(
name|DurationValue
operator|.
name|CANONICAL_ZERO_DURATION
argument_list|)
condition|)
block|{
if|if
condition|(
name|duration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|DAYS
argument_list|)
operator|||
name|duration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|HOURS
argument_list|)
operator|||
name|duration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|MINUTES
argument_list|)
operator|||
comment|//Always set !
comment|//!duration.getField(DatatypeConstants.SECONDS).equals(BigInteger.ZERO))
name|duration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"The value '"
operator|+
name|duration
operator|+
literal|"' is not an "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" since it specifies days, hours, minutes or seconds values"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|YearMonthDurationValue
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
argument_list|(
name|createDurationYearMonth
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Duration
name|createDurationYearMonth
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
return|return
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDurationYearMonth
argument_list|(
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|str
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|)
operator|+
literal|" from \""
operator|+
name|str
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|Duration
name|canonicalZeroDuration
parameter_list|()
block|{
return|return
name|CANONICAL_ZERO_DURATION
return|;
block|}
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|duration
operator|.
name|getSign
argument_list|()
operator|*
operator|(
name|duration
operator|.
name|getYears
argument_list|()
operator|*
literal|12
operator|+
name|duration
operator|.
name|getMonths
argument_list|()
operator|)
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|YEAR_MONTH_DURATION
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
name|FastStringBuffer
name|sb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|32
argument_list|)
decl_stmt|;
if|if
condition|(
name|getCanonicalDuration
argument_list|()
operator|.
name|getSign
argument_list|()
operator|<
literal|0
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
name|sb
operator|.
name|append
argument_list|(
literal|'P'
argument_list|)
expr_stmt|;
if|if
condition|(
name|getCanonicalDuration
argument_list|()
operator|.
name|getYears
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|getCanonicalDuration
argument_list|()
operator|.
name|getYears
argument_list|()
operator|+
literal|"Y"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getCanonicalDuration
argument_list|()
operator|.
name|getMonths
argument_list|()
operator|!=
literal|0
operator|||
name|getCanonicalDuration
argument_list|()
operator|.
name|getYears
argument_list|()
operator|==
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|getCanonicalDuration
argument_list|()
operator|.
name|getMonths
argument_list|()
operator|+
literal|"M"
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
name|ITEM
case|:
case|case
name|Type
operator|.
name|ATOMIC
case|:
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
name|this
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
name|DURATION
case|:
return|return
operator|new
name|DurationValue
argument_list|(
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDuration
argument_list|(
name|duration
operator|.
name|getSign
argument_list|()
operator|>=
literal|0
argument_list|,
operator|(
name|BigInteger
operator|)
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|YEARS
argument_list|)
argument_list|,
operator|(
name|BigInteger
operator|)
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|MONTHS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|DayTimeDurationValue
operator|.
name|CANONICAL_ZERO_DURATION
argument_list|)
return|;
comment|//case Type.DOUBLE:
comment|//return new DoubleValue(monthsValueSigned().doubleValue());
comment|//return new DoubleValue(Double.NaN);
comment|//case Type.DECIMAL:
comment|//return new DecimalValue(monthsValueSigned().doubleValue());
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
name|XPTY0004
argument_list|,
literal|"cannot cast 'xs:yearMonthDuration(\""
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
specifier|protected
name|DurationValue
name|createSameKind
parameter_list|(
name|Duration
name|dur
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|dur
argument_list|)
return|;
block|}
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
try|try
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
name|TIME
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
return|return
name|super
operator|.
name|plus
argument_list|(
name|other
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Operand to plus should be of type xdt:yearMonthDuration, xs:date, "
operator|+
literal|"or xs:dateTime; got: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
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
name|other
operator|instanceof
name|NumericValue
condition|)
block|{
comment|//If $arg2 is NaN an error is raised [err:FOCA0005]
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOCA0005
argument_list|,
literal|"Operand is not a number"
argument_list|)
throw|;
block|}
comment|//If $arg2 is positive or negative infinity, the result overflows
if|if
condition|(
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FODT0002
argument_list|,
literal|"Multiplication by infinity overflow"
argument_list|)
throw|;
block|}
block|}
name|BigDecimal
name|factor
init|=
name|numberToBigDecimal
argument_list|(
name|other
argument_list|,
literal|"Operand to mult should be of numeric type; got: "
argument_list|)
decl_stmt|;
name|boolean
name|isFactorNegative
init|=
name|factor
operator|.
name|signum
argument_list|()
operator|<
literal|0
decl_stmt|;
name|YearMonthDurationValue
name|product
init|=
name|fromDecimalMonths
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|monthsValueSigned
argument_list|()
argument_list|)
operator|.
name|multiply
argument_list|(
name|factor
operator|.
name|abs
argument_list|()
argument_list|)
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
operator|(
name|isFactorNegative
operator|)
condition|?
name|BigDecimal
operator|.
name|ROUND_HALF_DOWN
else|:
name|BigDecimal
operator|.
name|ROUND_HALF_UP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isFactorNegative
condition|)
return|return
name|product
operator|.
name|negate
argument_list|()
return|;
return|return
name|product
return|;
block|}
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
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|YEAR_MONTH_DURATION
condition|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|getValue
argument_list|()
argument_list|)
operator|.
name|div
argument_list|(
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|YearMonthDurationValue
operator|)
name|other
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|NumericValue
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOCA0005
argument_list|,
literal|"Operand is not a number"
argument_list|)
throw|;
block|}
if|if
condition|(
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
block|{
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
literal|"P0M"
argument_list|)
return|;
block|}
comment|//If $arg2 is positive or negative zero, the result overflows and is handled as discussed in 10.1.1 Limits and Precision
if|if
condition|(
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FODT0002
argument_list|,
literal|"Division by zero overflow"
argument_list|)
throw|;
block|}
block|}
name|BigDecimal
name|divisor
init|=
name|numberToBigDecimal
argument_list|(
name|other
argument_list|,
literal|"Can not divide xdt:yearMonthDuration by '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"'"
argument_list|)
decl_stmt|;
name|boolean
name|isDivisorNegative
init|=
name|divisor
operator|.
name|signum
argument_list|()
operator|<
literal|0
decl_stmt|;
name|YearMonthDurationValue
name|quotient
init|=
name|fromDecimalMonths
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|monthsValueSigned
argument_list|()
argument_list|)
operator|.
name|divide
argument_list|(
name|divisor
operator|.
name|abs
argument_list|()
argument_list|)
operator|.
name|setScale
argument_list|(
literal|0
argument_list|,
operator|(
name|isDivisorNegative
operator|)
condition|?
name|BigDecimal
operator|.
name|ROUND_DOWN
else|:
name|BigDecimal
operator|.
name|ROUND_UP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isDivisorNegative
condition|)
return|return
name|quotient
operator|.
name|negate
argument_list|()
return|;
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|quotient
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|YearMonthDurationValue
name|fromDecimalMonths
parameter_list|(
name|BigDecimal
name|x
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDurationYearMonth
argument_list|(
name|x
operator|.
name|signum
argument_list|()
operator|>=
literal|0
argument_list|,
literal|null
argument_list|,
name|x
operator|.
name|toBigInteger
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0006
argument_list|,
literal|"value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" has no boolean value."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

