begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|com
operator|.
name|ibm
operator|.
name|icu
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
name|Constants
operator|.
name|Comparison
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
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|XMLGregorianCalendar
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

begin_comment
comment|/**  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|abstract
class|class
name|OrderedDurationValue
extends|extends
name|DurationValue
block|{
name|OrderedDurationValue
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
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|Comparison
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
name|other
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|r
init|=
name|compareTo
argument_list|(
name|collator
argument_list|,
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|operator
operator|!=
name|Comparison
operator|.
name|EQ
operator|&&
name|operator
operator|!=
name|Comparison
operator|.
name|NEQ
condition|)
block|{
if|if
condition|(
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DURATION
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
literal|"cannot compare unordered "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to "
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
if|if
condition|(
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DURATION
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
literal|"cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to unordered "
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
if|if
condition|(
name|Type
operator|.
name|getCommonSuperType
argument_list|(
name|getType
argument_list|()
argument_list|,
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|==
name|Type
operator|.
name|DURATION
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
literal|"cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to "
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
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|EQ
case|:
return|return
name|r
operator|==
name|DatatypeConstants
operator|.
name|EQUAL
return|;
case|case
name|NEQ
case|:
return|return
name|r
operator|!=
name|DatatypeConstants
operator|.
name|EQUAL
return|;
case|case
name|LT
case|:
return|return
name|r
operator|==
name|DatatypeConstants
operator|.
name|LESSER
return|;
case|case
name|LTEQ
case|:
return|return
name|r
operator|==
name|DatatypeConstants
operator|.
name|LESSER
operator|||
name|r
operator|==
name|DatatypeConstants
operator|.
name|EQUAL
return|;
case|case
name|GT
case|:
return|return
name|r
operator|==
name|DatatypeConstants
operator|.
name|GREATER
return|;
case|case
name|GTEQ
case|:
return|return
name|r
operator|==
name|DatatypeConstants
operator|.
name|GREATER
operator|||
name|r
operator|==
name|DatatypeConstants
operator|.
name|EQUAL
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown operator type in comparison"
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
name|other
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Constants
operator|.
name|INFERIOR
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
name|DURATION
argument_list|)
condition|)
block|{
comment|//Take care : this method doesn't seem to take ms into account
specifier|final
name|int
name|r
init|=
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
decl_stmt|;
comment|//compare fractional seconds to work around the JDK standard behaviour
if|if
condition|(
name|r
operator|==
name|DatatypeConstants
operator|.
name|EQUAL
operator|&&
name|duration
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
operator|!=
literal|null
operator|&&
operator|(
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|duration
operator|)
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|(
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
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|BigDecimal
operator|)
operator|(
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|duration
operator|)
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
operator|)
argument_list|)
operator|==
name|DatatypeConstants
operator|.
name|EQUAL
condition|)
block|{
return|return
name|Constants
operator|.
name|EQUAL
return|;
block|}
return|return
operator|(
operator|(
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
operator|)
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|BigDecimal
operator|)
operator|(
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|duration
operator|)
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
operator|)
argument_list|)
operator|)
operator|==
name|DatatypeConstants
operator|.
name|LESSER
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
if|if
condition|(
name|r
operator|==
name|DatatypeConstants
operator|.
name|INDETERMINATE
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"indeterminate order between totally ordered duration values "
operator|+
name|this
operator|+
literal|" and "
operator|+
name|other
argument_list|)
throw|;
block|}
return|return
name|r
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to "
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
operator|!=
name|getType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot obtain maximum across different non-numeric data types"
argument_list|)
throw|;
block|}
return|return
name|compareTo
argument_list|(
literal|null
argument_list|,
name|other
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
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
operator|!=
name|getType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot obtain minimum across different non-numeric data types"
argument_list|)
throw|;
block|}
return|return
name|compareTo
argument_list|(
literal|null
argument_list|,
name|other
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|other
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
name|DAY_TIME_DURATION
case|:
block|{
comment|//if (getType() != other.getType()) throw new IllegalArgumentException();	// not a match after all
specifier|final
name|Duration
name|a
init|=
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|b
init|=
operator|(
operator|(
name|OrderedDurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|result
init|=
name|createSameKind
argument_list|(
name|a
operator|.
name|add
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
comment|//TODO : move instantiation to the right place
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|result
argument_list|)
return|;
block|}
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
block|{
comment|//if (getType() != other.getType()) throw new IllegalArgumentException();	// not a match after all
specifier|final
name|Duration
name|a
init|=
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|b
init|=
operator|(
operator|(
name|OrderedDurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|result
init|=
name|createSameKind
argument_list|(
name|a
operator|.
name|add
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
comment|//TODO : move instantiation to the right place
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|result
argument_list|)
return|;
block|}
case|case
name|Type
operator|.
name|DURATION
case|:
block|{
comment|//if (getType() != other.getType()) throw new IllegalArgumentException();	// not a match after all
specifier|final
name|Duration
name|a
init|=
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|b
init|=
operator|(
operator|(
name|DurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|result
init|=
name|createSameKind
argument_list|(
name|a
operator|.
name|add
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
comment|//TODO : move instantiation to the right place
return|return
operator|new
name|DurationValue
argument_list|(
name|result
argument_list|)
return|;
block|}
case|case
name|Type
operator|.
name|TIME
case|:
case|case
name|Type
operator|.
name|DATE_TIME
case|:
case|case
name|Type
operator|.
name|DATE
case|:
specifier|final
name|AbstractDateTimeValue
name|date
init|=
operator|(
name|AbstractDateTimeValue
operator|)
name|other
decl_stmt|;
specifier|final
name|XMLGregorianCalendar
name|gc
init|=
operator|(
name|XMLGregorianCalendar
operator|)
name|date
operator|.
name|calendar
operator|.
name|clone
argument_list|()
decl_stmt|;
name|gc
operator|.
name|add
argument_list|(
name|duration
argument_list|)
expr_stmt|;
comment|//Shift one year
if|if
condition|(
name|gc
operator|.
name|getYear
argument_list|()
operator|<
literal|0
condition|)
block|{
name|gc
operator|.
name|setYear
argument_list|(
name|gc
operator|.
name|getYear
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|date
operator|.
name|createSameKind
argument_list|(
name|gc
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"cannot add "
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
literal|"('"
operator|+
name|other
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"') from "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"')"
argument_list|)
throw|;
block|}
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
name|DAY_TIME_DURATION
case|:
block|{
if|if
condition|(
name|getType
argument_list|()
operator|!=
name|other
operator|.
name|getType
argument_list|()
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
literal|"Tried to substract "
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
literal|"('"
operator|+
name|other
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"') from "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"')"
argument_list|)
throw|;
block|}
specifier|final
name|Duration
name|a
init|=
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|b
init|=
operator|(
operator|(
name|OrderedDurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|result
init|=
name|createSameKind
argument_list|(
name|a
operator|.
name|subtract
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|result
argument_list|)
return|;
block|}
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
block|{
if|if
condition|(
name|getType
argument_list|()
operator|!=
name|other
operator|.
name|getType
argument_list|()
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
literal|"Tried to substract "
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
literal|"('"
operator|+
name|other
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"') from "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"')"
argument_list|)
throw|;
block|}
specifier|final
name|Duration
name|a
init|=
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|b
init|=
operator|(
operator|(
name|OrderedDurationValue
operator|)
name|other
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
specifier|final
name|Duration
name|result
init|=
name|createSameKind
argument_list|(
name|a
operator|.
name|subtract
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/* 		case Type.TIME: 		case Type.DATE_TIME: 		case Type.DATE: 			AbstractDateTimeValue date = (AbstractDateTimeValue) other; 			XMLGregorianCalendar gc = (XMLGregorianCalendar) date.calendar.clone(); 			gc.substract(duration); 			return date.createSameKind(gc); 		*/
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Cannot substract "
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
literal|"('"
operator|+
name|other
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"') from "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"')"
argument_list|)
throw|;
block|}
comment|/* 		if(other.getType() == getType()) { 			return createSameKind(duration.subtract(((OrderedDurationValue)other).duration)); 		} 		throw new XPathException("Operand to minus should be of type " + Type.getTypeName(getType()) + "; got: " + 			Type.getTypeName(other.getType())); 		*/
block|}
comment|/**      * Convert the given value to a big decimal if it's a number, keeping as much precision      * as possible.      *      * @param x                      a value to convert to a big decimal      * @param exceptionMessagePrefix the beginning of the message to throw in an exception, will be suffixed with the type of the value given      * @return the big decimal equivalent of the value      * @throws XPathException if the value is not of a numeric type      */
specifier|protected
name|BigDecimal
name|numberToBigDecimal
parameter_list|(
name|ComputableValue
name|x
parameter_list|,
name|String
name|exceptionMessagePrefix
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|x
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|exceptionMessagePrefix
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|x
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|x
operator|)
operator|.
name|isInfinite
argument_list|()
operator|||
operator|(
operator|(
name|NumericValue
operator|)
name|x
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
name|XPTY0004
argument_list|,
literal|"Tried to convert '"
operator|+
name|x
operator|+
literal|"' to BigDecimal"
argument_list|)
throw|;
block|}
if|if
condition|(
name|x
operator|.
name|conversionPreference
argument_list|(
name|BigDecimal
operator|.
name|class
argument_list|)
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|x
operator|.
name|toJavaObject
argument_list|(
name|BigDecimal
operator|.
name|class
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|NumericValue
operator|)
name|x
operator|)
operator|.
name|getDouble
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

