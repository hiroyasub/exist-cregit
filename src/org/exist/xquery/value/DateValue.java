begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SimpleTimeZone
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|perl
operator|.
name|Perl5Util
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DateValue
extends|extends
name|AbstractDateTimeValue
block|{
comment|//	dateTime format is: [+|-]yyyy-mm-dd[([+|-]hh:mm | Z)]
specifier|private
specifier|static
specifier|final
name|String
name|regex
init|=
literal|"/(\\+|-)?(\\d{4})-([0-1]\\d)-(\\d{2})(.*)/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|tzre
init|=
literal|"/(\\+|-)?([0-1]\\d):(\\d{2})/"
decl_stmt|;
specifier|public
name|DateValue
parameter_list|()
block|{
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|()
expr_stmt|;
name|tzOffset
operator|=
operator|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|ZONE_OFFSET
argument_list|)
operator|+
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DST_OFFSET
argument_list|)
operator|)
operator|/
literal|60000
expr_stmt|;
name|date
operator|=
name|calendar
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DateValue
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|XPathException
block|{
name|Perl5Util
name|util
init|=
operator|new
name|Perl5Util
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|util
operator|.
name|match
argument_list|(
name|regex
argument_list|,
name|dateString
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|dateString
operator|+
literal|" cannot be cast into an xs:date"
argument_list|)
throw|;
name|String
name|part
init|=
name|util
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|era
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|part
operator|!=
literal|null
operator|&&
name|part
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
name|era
operator|=
operator|-
literal|1
expr_stmt|;
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|int
name|year
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|part
argument_list|)
operator|*
name|era
decl_stmt|;
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|int
name|month
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|part
argument_list|)
decl_stmt|;
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|int
name|day
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|part
argument_list|)
decl_stmt|;
name|tzOffset
operator|=
literal|0
expr_stmt|;
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|5
argument_list|)
expr_stmt|;
if|if
condition|(
name|part
operator|!=
literal|null
operator|&&
name|part
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|part
operator|.
name|equals
argument_list|(
literal|"Z"
argument_list|)
condition|)
block|{
name|explicitTimeZone
operator|=
literal|true
expr_stmt|;
name|tzOffset
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|util
operator|.
name|match
argument_list|(
name|tzre
argument_list|,
name|part
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: error in  timezone: "
operator|+
name|part
argument_list|)
throw|;
name|explicitTimeZone
operator|=
literal|true
expr_stmt|;
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|tzOffset
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|part
argument_list|)
operator|*
literal|60
expr_stmt|;
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|3
argument_list|)
expr_stmt|;
if|if
condition|(
name|part
operator|!=
literal|null
condition|)
block|{
name|int
name|tzminute
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|part
argument_list|)
decl_stmt|;
name|tzOffset
operator|+=
name|tzminute
expr_stmt|;
block|}
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|part
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
name|tzOffset
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
block|}
name|SimpleTimeZone
name|zone
init|=
operator|new
name|SimpleTimeZone
argument_list|(
name|tzOffset
operator|*
literal|60000
argument_list|,
literal|"LLL"
argument_list|)
decl_stmt|;
if|if
condition|(
name|explicitTimeZone
condition|)
block|{
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|(
name|zone
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|()
expr_stmt|;
name|tzOffset
operator|=
operator|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|ZONE_OFFSET
argument_list|)
operator|+
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DST_OFFSET
argument_list|)
operator|)
operator|/
literal|60000
expr_stmt|;
block|}
name|calendar
operator|.
name|setLenient
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|explicitTimeZone
condition|)
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|ZONE_OFFSET
argument_list|,
name|tzOffset
operator|*
literal|60000
argument_list|)
expr_stmt|;
try|try
block|{
name|date
operator|=
name|calendar
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|dateString
operator|+
literal|" cannot be cast into an xs:date"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|DateValue
parameter_list|(
name|Calendar
name|cal
parameter_list|,
name|int
name|timezone
parameter_list|)
block|{
name|tzOffset
operator|=
name|timezone
expr_stmt|;
name|explicitTimeZone
operator|=
literal|true
expr_stmt|;
name|SimpleTimeZone
name|zone
init|=
operator|new
name|SimpleTimeZone
argument_list|(
name|tzOffset
operator|*
literal|60000
argument_list|,
literal|"LLL"
argument_list|)
decl_stmt|;
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|(
name|zone
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setLenient
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
argument_list|,
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
argument_list|,
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|ZONE_OFFSET
argument_list|,
name|tzOffset
operator|*
literal|60000
argument_list|)
expr_stmt|;
name|date
operator|=
name|calendar
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DateValue
parameter_list|(
name|long
name|milliseconds
parameter_list|,
name|int
name|timezone
parameter_list|)
block|{
name|tzOffset
operator|=
name|timezone
expr_stmt|;
name|explicitTimeZone
operator|=
literal|true
expr_stmt|;
name|SimpleTimeZone
name|zone
init|=
operator|new
name|SimpleTimeZone
argument_list|(
name|tzOffset
operator|*
literal|60000
argument_list|,
literal|"LLL"
argument_list|)
decl_stmt|;
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|(
name|zone
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setLenient
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setTimeInMillis
argument_list|(
name|milliseconds
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|ZONE_OFFSET
argument_list|,
name|tzOffset
operator|*
literal|60000
argument_list|)
expr_stmt|;
name|date
operator|=
name|calendar
operator|.
name|getTime
argument_list|()
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
name|DATE
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
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|year
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|year
operator|<
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|year
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
name|formatString
argument_list|(
name|buf
argument_list|,
name|year
argument_list|,
operator|(
name|year
operator|>
literal|9999
condition|?
operator|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
operator|+
literal|""
operator|)
operator|.
name|length
argument_list|()
else|:
literal|4
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|formatString
argument_list|(
name|buf
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|+
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|formatString
argument_list|(
name|buf
argument_list|,
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|tzOffset
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'Z'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|tzOffset
operator|<
literal|0
condition|?
literal|"-"
else|:
literal|"+"
operator|)
argument_list|)
expr_stmt|;
name|int
name|tzo
init|=
name|tzOffset
decl_stmt|;
if|if
condition|(
name|tzo
operator|<
literal|0
condition|)
name|tzo
operator|=
operator|-
name|tzo
expr_stmt|;
name|int
name|tzhours
init|=
name|tzo
operator|/
literal|60
decl_stmt|;
name|formatString
argument_list|(
name|buf
argument_list|,
name|tzhours
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|int
name|tzminutes
init|=
name|tzo
operator|%
literal|60
decl_stmt|;
name|formatString
argument_list|(
name|buf
argument_list|,
name|tzminutes
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|DateValue
name|adjustToTimezone
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|Date
name|date
init|=
name|calendar
operator|.
name|getTime
argument_list|()
decl_stmt|;
return|return
operator|new
name|DateValue
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|offset
argument_list|)
return|;
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
name|DATE
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
name|DATE_TIME
case|:
return|return
operator|new
name|DateTimeValue
argument_list|(
name|calendar
argument_list|,
name|tzOffset
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
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot cast xs:date to "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#compareTo(int, org.exist.xquery.value.AtomicValue) 	 */
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
if|if
condition|(
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DATE
condition|)
block|{
name|int
name|cmp
init|=
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|)
operator|.
name|date
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
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown operator type in comparison"
argument_list|)
throw|;
block|}
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare xs:date to "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#compareTo(org.exist.xquery.value.AtomicValue) 	 */
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
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DATE
condition|)
block|{
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|)
operator|.
name|date
argument_list|)
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare xs:date to "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#max(org.exist.xquery.value.AtomicValue) 	 */
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
name|DATE
condition|)
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|)
operator|.
name|date
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
return|;
else|else
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
operator|)
operator|.
name|date
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#min(org.exist.xquery.value.AtomicValue) 	 */
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
name|DATE
condition|)
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|)
operator|.
name|date
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|other
return|;
else|else
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
operator|)
operator|.
name|date
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.ComputableValue#minus(org.exist.xquery.value.NumericValue) 	 */
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
name|DATE
case|:
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
operator|-
operator|(
operator|(
name|DateValue
operator|)
name|other
operator|)
operator|.
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
operator|(
operator|(
name|YearMonthDurationValue
operator|)
name|other
operator|)
operator|.
name|negate
argument_list|()
operator|.
name|plus
argument_list|(
name|this
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
return|return
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|other
operator|)
operator|.
name|negate
argument_list|()
operator|.
name|plus
argument_list|(
name|this
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Operand to minus should be of type xdt:yearMonthDuration or xdt:dayTimeDuration; got: "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.ComputableValue#plus(org.exist.xquery.value.NumericValue) 	 */
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
name|YEAR_MONTH_DURATION
case|:
return|return
name|other
operator|.
name|plus
argument_list|(
name|this
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
return|return
name|other
operator|.
name|plus
argument_list|(
name|this
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Operand to plus should be of type xdt:yearMonthDuration or xdt:dayTimeDuration; got: "
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
name|DateValue
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
name|Date
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
name|DateValue
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
name|Date
operator|.
name|class
condition|)
return|return
name|calendar
operator|.
name|getTime
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
name|this
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

