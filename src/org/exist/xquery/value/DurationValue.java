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
comment|/**  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|DurationValue
extends|extends
name|ComputableValue
block|{
specifier|public
specifier|final
specifier|static
name|int
name|YEAR
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MONTH
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DAY
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|HOUR
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MINUTE
init|=
literal|4
decl_stmt|;
specifier|protected
specifier|final
name|Duration
name|duration
decl_stmt|;
specifier|private
name|Duration
name|canonicalDuration
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|BigInteger
name|TWELVE
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|12
argument_list|)
decl_stmt|,
name|TWENTY_FOUR
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|24
argument_list|)
decl_stmt|,
name|SIXTY
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|60
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|BigDecimal
name|SIXTY_DECIMAL
init|=
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|60
argument_list|)
decl_stmt|,
name|ZERO_DECIMAL
init|=
name|BigDecimal
operator|.
name|ZERO
decl_stmt|;
specifier|protected
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
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|ZERO_DECIMAL
argument_list|)
decl_stmt|;
comment|/** 	 * Create a new duration value of the most specific type allowed by the fields set in the given 	 * duration object.  If no fields are set, return a xs:dayTimeDuration. 	 * 	 * @param duration the duration to wrap 	 * @return a new instance of the most specific subclass of<code>DurationValue</code> 	 */
specifier|public
specifier|static
name|DurationValue
name|wrap
parameter_list|(
name|Duration
name|duration
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|duration
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|duration
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e2
parameter_list|)
block|{
return|return
operator|new
name|DurationValue
argument_list|(
name|duration
argument_list|)
return|;
block|}
block|}
block|}
specifier|public
name|DurationValue
parameter_list|(
name|Duration
name|duration
parameter_list|)
block|{
name|this
operator|.
name|duration
operator|=
name|duration
expr_stmt|;
block|}
specifier|public
name|DurationValue
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|this
operator|.
name|duration
operator|=
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDuration
argument_list|(
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"err:FORG0001: cannot construct "
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
name|str
operator|+
literal|"\""
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Duration
name|getCanonicalDuration
parameter_list|()
block|{
name|canonicalize
argument_list|()
expr_stmt|;
return|return
name|canonicalDuration
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
name|DURATION
return|;
block|}
specifier|protected
name|DurationValue
name|createSameKind
parameter_list|(
name|Duration
name|d
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DurationValue
argument_list|(
name|d
argument_list|)
return|;
block|}
specifier|public
name|DurationValue
name|negate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|createSameKind
argument_list|(
name|duration
operator|.
name|negate
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
name|canonicalize
argument_list|()
expr_stmt|;
return|return
name|canonicalDuration
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|BigInteger
name|nullIfZero
parameter_list|(
name|BigInteger
name|x
parameter_list|)
block|{
if|if
condition|(
name|BigInteger
operator|.
name|ZERO
operator|.
name|compareTo
argument_list|(
name|x
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
condition|)
name|x
operator|=
literal|null
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|private
specifier|static
name|BigInteger
name|zeroIfNull
parameter_list|(
name|BigInteger
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|==
literal|null
condition|)
name|x
operator|=
name|BigInteger
operator|.
name|ZERO
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|private
specifier|static
name|BigDecimal
name|nullIfZero
parameter_list|(
name|BigDecimal
name|x
parameter_list|)
block|{
if|if
condition|(
name|ZERO_DECIMAL
operator|.
name|compareTo
argument_list|(
name|x
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
condition|)
name|x
operator|=
literal|null
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|private
specifier|static
name|BigDecimal
name|zeroIfNull
parameter_list|(
name|BigDecimal
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|==
literal|null
condition|)
name|x
operator|=
name|ZERO_DECIMAL
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|private
name|void
name|canonicalize
parameter_list|()
block|{
if|if
condition|(
name|canonicalDuration
operator|!=
literal|null
condition|)
return|return;
name|BigInteger
name|years
decl_stmt|,
name|months
decl_stmt|,
name|days
decl_stmt|,
name|hours
decl_stmt|,
name|minutes
decl_stmt|;
name|BigDecimal
name|seconds
decl_stmt|;
name|BigInteger
index|[]
name|r
decl_stmt|;
name|r
operator|=
name|monthsValue
argument_list|()
operator|.
name|divideAndRemainder
argument_list|(
name|TWELVE
argument_list|)
expr_stmt|;
name|years
operator|=
name|nullIfZero
argument_list|(
name|r
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|months
operator|=
name|nullIfZero
argument_list|(
name|r
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// TODO: replace following segment with this for JDK 1.5
comment|//		BigDecimal[] rd = secondsValue().divideAndRemainder(SIXTY_DECIMAL);
comment|//		seconds = nullIfZero(rd[1]);
comment|//		r = rd[0].toBigInteger().divideAndRemainder(SIXTY);
comment|// segment to be replaced:
name|BigDecimal
name|secondsValue
init|=
name|secondsValue
argument_list|()
decl_stmt|;
name|BigDecimal
name|m
init|=
name|secondsValue
operator|.
name|divide
argument_list|(
name|SIXTY_DECIMAL
argument_list|,
literal|0
argument_list|,
name|BigDecimal
operator|.
name|ROUND_DOWN
argument_list|)
decl_stmt|;
name|seconds
operator|=
name|nullIfZero
argument_list|(
name|secondsValue
operator|.
name|subtract
argument_list|(
name|SIXTY_DECIMAL
operator|.
name|multiply
argument_list|(
name|m
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|m
operator|.
name|toBigInteger
argument_list|()
operator|.
name|divideAndRemainder
argument_list|(
name|SIXTY
argument_list|)
expr_stmt|;
name|minutes
operator|=
name|nullIfZero
argument_list|(
name|r
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|r
operator|=
name|r
index|[
literal|0
index|]
operator|.
name|divideAndRemainder
argument_list|(
name|TWENTY_FOUR
argument_list|)
expr_stmt|;
name|hours
operator|=
name|nullIfZero
argument_list|(
name|r
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|days
operator|=
name|nullIfZero
argument_list|(
name|r
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|years
operator|==
literal|null
operator|&&
name|months
operator|==
literal|null
operator|&&
name|days
operator|==
literal|null
operator|&&
name|hours
operator|==
literal|null
operator|&&
name|minutes
operator|==
literal|null
operator|&&
name|seconds
operator|==
literal|null
condition|)
block|{
name|canonicalDuration
operator|=
name|canonicalZeroDuration
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|canonicalDuration
operator|=
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
name|years
argument_list|,
name|months
argument_list|,
name|days
argument_list|,
name|hours
argument_list|,
name|minutes
argument_list|,
name|seconds
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|BigDecimal
name|secondsValue
parameter_list|()
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
name|zeroIfNull
argument_list|(
operator|(
name|BigInteger
operator|)
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|DAYS
argument_list|)
argument_list|)
operator|.
name|multiply
argument_list|(
name|TWENTY_FOUR
argument_list|)
operator|.
name|add
argument_list|(
name|zeroIfNull
argument_list|(
operator|(
name|BigInteger
operator|)
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|HOURS
argument_list|)
argument_list|)
argument_list|)
operator|.
name|multiply
argument_list|(
name|SIXTY
argument_list|)
operator|.
name|add
argument_list|(
name|zeroIfNull
argument_list|(
operator|(
name|BigInteger
operator|)
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|MINUTES
argument_list|)
argument_list|)
argument_list|)
operator|.
name|multiply
argument_list|(
name|SIXTY
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|zeroIfNull
argument_list|(
operator|(
name|BigDecimal
operator|)
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|BigDecimal
name|secondsValueSigned
parameter_list|()
block|{
name|BigDecimal
name|x
init|=
name|secondsValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|duration
operator|.
name|getSign
argument_list|()
operator|<
literal|0
condition|)
name|x
operator|=
name|x
operator|.
name|negate
argument_list|()
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|protected
name|BigInteger
name|monthsValue
parameter_list|()
block|{
return|return
name|zeroIfNull
argument_list|(
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
argument_list|)
operator|.
name|multiply
argument_list|(
name|TWELVE
argument_list|)
operator|.
name|add
argument_list|(
name|zeroIfNull
argument_list|(
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
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|BigInteger
name|monthsValueSigned
parameter_list|()
block|{
name|BigInteger
name|x
init|=
name|monthsValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|duration
operator|.
name|getSign
argument_list|()
operator|<
literal|0
condition|)
name|x
operator|=
name|x
operator|.
name|negate
argument_list|()
expr_stmt|;
return|return
name|x
return|;
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
name|getPart
parameter_list|(
name|int
name|part
parameter_list|)
block|{
name|int
name|r
decl_stmt|;
switch|switch
condition|(
name|part
condition|)
block|{
case|case
name|YEAR
case|:
name|r
operator|=
name|duration
operator|.
name|getYears
argument_list|()
expr_stmt|;
break|break;
case|case
name|MONTH
case|:
name|r
operator|=
name|duration
operator|.
name|getMonths
argument_list|()
expr_stmt|;
break|break;
case|case
name|DAY
case|:
name|r
operator|=
name|duration
operator|.
name|getDays
argument_list|()
expr_stmt|;
break|break;
case|case
name|HOUR
case|:
name|r
operator|=
name|duration
operator|.
name|getHours
argument_list|()
expr_stmt|;
break|break;
case|case
name|MINUTE
case|:
name|r
operator|=
name|duration
operator|.
name|getMinutes
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid argument to method getPart"
argument_list|)
throw|;
block|}
return|return
name|r
operator|*
name|duration
operator|.
name|getSign
argument_list|()
return|;
block|}
specifier|public
name|double
name|getSeconds
parameter_list|()
block|{
name|Number
name|n
init|=
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
return|return
name|n
operator|==
literal|null
condition|?
literal|0
else|:
name|n
operator|.
name|doubleValue
argument_list|()
operator|*
name|duration
operator|.
name|getSign
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
name|canonicalize
argument_list|()
expr_stmt|;
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
name|DURATION
case|:
return|return
operator|new
name|DurationValue
argument_list|(
name|canonicalDuration
argument_list|)
return|;
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
if|if
condition|(
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|YEARS
argument_list|)
operator|!=
literal|null
operator|||
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|MONTHS
argument_list|)
operator|!=
literal|null
condition|)
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
name|canonicalDuration
operator|.
name|getSign
argument_list|()
operator|>=
literal|0
argument_list|,
operator|(
name|BigInteger
operator|)
name|canonicalDuration
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
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|MONTHS
argument_list|)
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|YearMonthDurationValue
operator|.
name|CANONICAL_ZERO_DURATION
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
if|if
condition|(
name|canonicalDuration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|DAYS
argument_list|)
operator|||
name|canonicalDuration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|HOURS
argument_list|)
operator|||
name|canonicalDuration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|MINUTES
argument_list|)
operator|||
name|canonicalDuration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
condition|)
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDuration
argument_list|(
name|canonicalDuration
operator|.
name|getSign
argument_list|()
operator|>=
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|(
name|BigInteger
operator|)
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|DAYS
argument_list|)
argument_list|,
operator|(
name|BigInteger
operator|)
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|HOURS
argument_list|)
argument_list|,
operator|(
name|BigInteger
operator|)
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|MINUTES
argument_list|)
argument_list|,
operator|(
name|BigDecimal
operator|)
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|DayTimeDurationValue
operator|.
name|CANONICAL_ZERO_DURATION
argument_list|)
return|;
case|case
name|Type
operator|.
name|STRING
case|:
name|canonicalize
argument_list|()
expr_stmt|;
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
name|UNTYPED_ATOMIC
case|:
name|canonicalize
argument_list|()
expr_stmt|;
return|return
operator|new
name|UntypedAtomicValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot cast ' + Type.getTypeName(getType()) 'to "
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
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
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
name|EQ
case|:
block|{
if|if
condition|(
operator|!
operator|(
name|DurationValue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|other
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operand type: "
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
comment|//TODO : upgrade so that P365D is *not* equal to P1Y
name|boolean
name|r
init|=
name|duration
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|duration
argument_list|)
decl_stmt|;
comment|//confirm strict equality to work around the JDK standard behaviour
if|if
condition|(
name|r
condition|)
name|r
operator|=
name|r
operator|&
name|areReallyEqual
argument_list|(
name|getCanonicalDuration
argument_list|()
argument_list|,
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
case|case
name|Constants
operator|.
name|NEQ
case|:
block|{
if|if
condition|(
operator|!
operator|(
name|DurationValue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|other
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operand type: "
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
comment|//TODO : upgrade so that P365D is *not* equal to P1Y
name|boolean
name|r
init|=
name|duration
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|duration
argument_list|)
decl_stmt|;
comment|//confirm strict equality to work around the JDK standard behaviour
if|if
condition|(
name|r
condition|)
name|r
operator|=
name|r
operator|&
name|areReallyEqual
argument_list|(
name|getCanonicalDuration
argument_list|()
argument_list|,
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|!
name|r
return|;
block|}
case|case
name|Constants
operator|.
name|LT
case|:
case|case
name|Constants
operator|.
name|LTEQ
case|:
case|case
name|Constants
operator|.
name|GT
case|:
case|case
name|Constants
operator|.
name|GTEQ
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: "
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
literal|" type can not be ordered"
argument_list|)
throw|;
default|default :
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown comparison operator"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|compareTo
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
operator|!
operator|(
name|DurationValue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|other
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operand type: "
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
comment|//TODO : what to do with the collator ?
return|return
name|duration
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|duration
argument_list|)
return|;
block|}
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operation on "
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
argument_list|)
throw|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operation on "
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
argument_list|)
throw|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operation on "
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
argument_list|)
throw|;
block|}
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operation on "
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
argument_list|)
throw|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operation on "
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
argument_list|)
throw|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: invalid operation on "
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
argument_list|)
throw|;
block|}
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|Duration
operator|.
name|class
argument_list|)
condition|)
return|return
literal|1
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
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
name|getClass
argument_list|()
argument_list|)
condition|)
return|return
name|this
return|;
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|Duration
operator|.
name|class
argument_list|)
condition|)
return|return
name|duration
return|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"err:XPTY0004: cannot convert value of type "
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
literal|"err:FORG0006: value of type "
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
specifier|public
specifier|static
name|boolean
name|areReallyEqual
parameter_list|(
name|Duration
name|duration1
parameter_list|,
name|Duration
name|duration2
parameter_list|)
block|{
name|boolean
name|secondsEqual
init|=
name|zeroIfNull
argument_list|(
operator|(
name|BigDecimal
operator|)
name|duration1
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
argument_list|)
operator|.
name|compareTo
argument_list|(
name|zeroIfNull
argument_list|(
operator|(
name|BigDecimal
operator|)
name|duration2
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
argument_list|)
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
decl_stmt|;
return|return
name|secondsEqual
operator|&&
name|duration1
operator|.
name|getMinutes
argument_list|()
operator|==
name|duration2
operator|.
name|getMinutes
argument_list|()
operator|&&
name|duration1
operator|.
name|getHours
argument_list|()
operator|==
name|duration2
operator|.
name|getHours
argument_list|()
operator|&&
name|duration1
operator|.
name|getDays
argument_list|()
operator|==
name|duration2
operator|.
name|getDays
argument_list|()
operator|&&
name|duration1
operator|.
name|getMonths
argument_list|()
operator|==
name|duration2
operator|.
name|getMonths
argument_list|()
operator|&&
name|duration1
operator|.
name|getYears
argument_list|()
operator|==
name|duration2
operator|.
name|getYears
argument_list|()
return|;
block|}
block|}
end_class

end_unit

