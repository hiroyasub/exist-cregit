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

begin_comment
comment|/**  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|DayTimeDurationValue
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
name|DayTimeDurationValue
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
name|duration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|YEARS
argument_list|)
operator|||
name|duration
operator|.
name|isSet
argument_list|(
name|DatatypeConstants
operator|.
name|MONTHS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"the value '"
operator|+
name|duration
operator|+
literal|"' is not an xdt:dayTimeDuration since it specifies year or month values"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|DayTimeDurationValue
parameter_list|(
name|long
name|millis
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
argument_list|(
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newDurationDayTime
argument_list|(
name|millis
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DayTimeDurationValue
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
argument_list|(
name|createDurationDayTime
argument_list|(
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|str
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Duration
name|createDurationDayTime
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
name|newDurationDayTime
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
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
name|DAY_TIME_DURATION
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
name|DurationValue
name|wrap
parameter_list|()
block|{
return|return
name|this
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
name|DAY_TIME_DURATION
return|;
block|}
specifier|public
name|double
name|getValue
parameter_list|()
block|{
name|double
name|value
init|=
name|duration
operator|.
name|getDays
argument_list|()
decl_stmt|;
name|value
operator|=
name|value
operator|*
literal|24
operator|+
name|duration
operator|.
name|getHours
argument_list|()
expr_stmt|;
name|value
operator|=
name|value
operator|*
literal|60
operator|+
name|duration
operator|.
name|getMinutes
argument_list|()
expr_stmt|;
specifier|final
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
name|value
operator|=
name|value
operator|*
literal|60
operator|+
operator|(
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
operator|)
expr_stmt|;
return|return
name|value
operator|*
name|duration
operator|.
name|getSign
argument_list|()
return|;
block|}
specifier|public
name|long
name|getValueInMilliseconds
parameter_list|()
block|{
return|return
operator|(
name|long
operator|)
operator|(
name|getValue
argument_list|()
operator|*
literal|1000
operator|)
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
name|String
name|getStringValue
parameter_list|()
block|{
specifier|final
name|Duration
name|canonicalDuration
init|=
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|d
init|=
name|canonicalDuration
operator|.
name|getDays
argument_list|()
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|canonicalDuration
operator|.
name|getHours
argument_list|()
decl_stmt|;
specifier|final
name|int
name|m
init|=
name|canonicalDuration
operator|.
name|getMinutes
argument_list|()
decl_stmt|;
name|Number
name|s
init|=
name|canonicalDuration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|s
operator|=
literal|0
expr_stmt|;
block|}
comment|//Copied from Saxon 8.6.1
specifier|final
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
name|canonicalDuration
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
name|d
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|d
operator|+
literal|"D"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|d
operator|==
literal|0
operator|||
name|h
operator|!=
literal|0
operator|||
name|m
operator|!=
literal|0
operator|||
name|s
operator|.
name|intValue
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'T'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|h
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|h
operator|+
literal|"H"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|m
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|m
operator|+
literal|"M"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|s
operator|.
name|intValue
argument_list|()
operator|!=
literal|0
operator|)
operator|||
operator|(
name|d
operator|==
literal|0
operator|&&
name|m
operator|==
literal|0
operator|&&
name|h
operator|==
literal|0
operator|)
condition|)
block|{
comment|//TODO : ugly -> factorize
comment|//sb.append(Integer.toString(s.intValue()));
comment|//double ms = s.doubleValue() - s.intValue();
comment|//if (ms != 0.0) {
comment|//	sb.append(".");
comment|//	sb.append(Double.toString(ms).substring(2));
comment|//}
comment|//0 is a dummy parameter
name|FloatingPointConverter
operator|.
name|appendFloat
argument_list|(
name|sb
argument_list|,
name|s
operator|.
name|floatValue
argument_list|()
argument_list|)
operator|.
name|getNormalizedString
argument_list|(
name|FastStringBuffer
operator|.
name|SUPPRESS_BOTH
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"S"
argument_list|)
expr_stmt|;
comment|/*             if (micros == 0) {                 sb.append(s + "S");             } else {                 long ms = (s * 1000000) + micros;                 String mss = ms + "";                 if (s == 0) {                     mss = "0000000" + mss;                     mss = mss.substring(mss.length()-7);                 }                 sb.append(mss.substring(0, mss.length()-6));                 sb.append('.');                 int lastSigDigit = mss.length()-1;                 while (mss.charAt(lastSigDigit) == '0') {                     lastSigDigit--;                 }                 sb.append(mss.substring(mss.length()-6, lastSigDigit+1));                 sb.append('S');             }             */
block|}
comment|//End of copy
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
name|DAY_TIME_DURATION
case|:
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|getCanonicalDuration
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|STRING
case|:
block|{
specifier|final
name|DayTimeDurationValue
name|dtdv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
name|getCanonicalDuration
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|dtdv
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
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
literal|null
argument_list|,
literal|null
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
name|DAYS
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
name|HOURS
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
name|MINUTES
argument_list|)
argument_list|,
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
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|YearMonthDurationValue
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
block|{
specifier|final
name|DayTimeDurationValue
name|dtdv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
name|getCanonicalDuration
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|UntypedAtomicValue
argument_list|(
name|dtdv
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
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
name|DayTimeDurationValue
argument_list|(
name|dur
argument_list|)
return|;
block|}
comment|/* 	public ComputableValue plus(ComputableValue other) throws XPathException { 		try { 			return super.plus(other); 		} catch (IllegalArgumentException e) { 				throw new XPathException("Operand to plus should be of type xdt:dayTimeDuration, xs:time, " + 					"xs:date or xs:dateTime; got: " + 					Type.getTypeName(other.getType())); 		} 	} 	*/
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
specifier|final
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
specifier|final
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
specifier|final
name|DayTimeDurationValue
name|product
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
name|duration
operator|.
name|multiply
argument_list|(
name|factor
operator|.
name|abs
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isFactorNegative
condition|)
block|{
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|product
operator|.
name|negate
argument_list|()
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|product
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
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
name|DAY_TIME_DURATION
condition|)
block|{
specifier|final
name|DecimalValue
name|a
init|=
operator|new
name|DecimalValue
argument_list|(
name|secondsValueSigned
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DecimalValue
name|b
init|=
operator|new
name|DecimalValue
argument_list|(
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|other
operator|)
operator|.
name|secondsValueSigned
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|DecimalValue
argument_list|(
name|a
operator|.
name|value
operator|.
name|divide
argument_list|(
name|b
operator|.
name|value
argument_list|,
literal|20
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_UP
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
comment|//If $arg2 is positive or negative infinity, the result is a zero-length duration
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
name|DayTimeDurationValue
argument_list|(
literal|"PT0S"
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
literal|"Division by zero"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|BigDecimal
name|divisor
init|=
name|numberToBigDecimal
argument_list|(
name|other
argument_list|,
literal|"Operand to div should be of xdt:dayTimeDuration or numeric type; got: "
argument_list|)
decl_stmt|;
specifier|final
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
specifier|final
name|BigDecimal
name|secondsValueSigned
init|=
name|secondsValueSigned
argument_list|()
decl_stmt|;
specifier|final
name|DayTimeDurationValue
name|quotient
init|=
name|fromDecimalSeconds
argument_list|(
name|secondsValueSigned
operator|.
name|divide
argument_list|(
name|divisor
operator|.
name|abs
argument_list|()
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|3
argument_list|,
name|secondsValueSigned
operator|.
name|scale
argument_list|()
argument_list|)
argument_list|,
name|divisor
operator|.
name|scale
argument_list|()
argument_list|)
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_UP
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isDivisorNegative
condition|)
block|{
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|quotient
operator|.
name|negate
argument_list|()
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|quotient
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|DayTimeDurationValue
name|fromDecimalSeconds
parameter_list|(
name|BigDecimal
name|x
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|x
operator|.
name|signum
argument_list|()
operator|>=
literal|0
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
name|x
operator|.
name|abs
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

