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
comment|/**  * Represents a value of type xs:dateTime.  *   * TODO: This is in large parts copied from Saxon and needs to be revised.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DateTimeValue
extends|extends
name|AbstractDateTimeValue
block|{
comment|// dateTime format is: [+|-]yyyy-mm-ddThh:mm:ss[.fff*][([+|-]hh:mm | Z)]
specifier|private
specifier|static
specifier|final
name|String
name|regex
init|=
literal|"/(\\+|-)?(\\d{4})-([0-1]\\d)-(\\d{2})T([0-2]\\d):([0-6][0-9]):([0-6][0-9])(\\.(\\d{1,3}))?(.*)/"
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
name|DateTimeValue
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
name|DateTimeValue
parameter_list|(
name|GregorianCalendar
name|cal
parameter_list|,
name|int
name|timezone
parameter_list|)
block|{
name|calendar
operator|=
operator|(
name|GregorianCalendar
operator|)
name|cal
operator|.
name|clone
argument_list|()
expr_stmt|;
name|tzOffset
operator|=
name|timezone
expr_stmt|;
name|explicitTimeZone
operator|=
literal|true
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
name|DateTimeValue
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
specifier|public
name|DateTimeValue
parameter_list|(
name|String
name|dateTime
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
name|dateTime
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|dateTime
operator|+
literal|" cannot be cast into an xs:dateTime"
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
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|int
name|hour
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
literal|6
argument_list|)
expr_stmt|;
name|int
name|minutes
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
literal|7
argument_list|)
expr_stmt|;
name|int
name|seconds
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
literal|9
argument_list|)
expr_stmt|;
name|int
name|millis
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|part
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|part
operator|.
name|length
argument_list|()
operator|<
literal|3
condition|)
name|part
operator|+=
literal|"00"
expr_stmt|;
if|if
condition|(
name|part
operator|.
name|length
argument_list|()
operator|>
literal|3
condition|)
name|part
operator|=
name|part
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|millis
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
name|part
operator|=
name|util
operator|.
name|group
argument_list|(
literal|10
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
operator|&&
operator|(
operator|!
name|part
operator|.
name|equals
argument_list|(
literal|"Z"
argument_list|)
operator|)
condition|)
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
name|calendar
operator|=
operator|new
name|GregorianCalendar
argument_list|(
name|zone
argument_list|)
expr_stmt|;
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
name|hour
argument_list|,
name|minutes
argument_list|,
name|seconds
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
name|millis
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
name|dateTime
operator|+
literal|" cannot be cast into an xs:dateTime"
argument_list|)
throw|;
block|}
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
name|DATE_TIME
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
name|buf
operator|.
name|append
argument_list|(
literal|'T'
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
name|HOUR_OF_DAY
argument_list|)
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
name|MINUTE
argument_list|)
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
name|SECOND
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|int
name|millis
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
decl_stmt|;
if|if
condition|(
name|millis
operator|!=
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|String
name|m
init|=
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|)
operator|+
literal|""
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|length
argument_list|()
operator|<
literal|3
condition|)
name|m
operator|=
literal|"0"
operator|+
name|m
expr_stmt|;
while|while
condition|(
name|m
operator|.
name|endsWith
argument_list|(
literal|"0"
argument_list|)
condition|)
name|m
operator|=
name|m
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|m
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
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
name|DATE_TIME
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
name|DATE
case|:
return|return
operator|new
name|DateValue
argument_list|(
name|calendar
argument_list|,
name|tzOffset
argument_list|)
return|;
case|case
name|Type
operator|.
name|TIME
case|:
return|return
operator|new
name|TimeValue
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
literal|"Type error: cannot cast xs:dateTime to "
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
name|DATE_TIME
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
name|DateTimeValue
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
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare xs:dateTime to "
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
name|DATE_TIME
condition|)
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateTimeValue
operator|)
name|other
operator|)
operator|.
name|date
argument_list|)
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare xs:dateTime to "
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
name|DATE_TIME
condition|)
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateTimeValue
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
name|DateTimeValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DATE_TIME
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
name|DATE_TIME
condition|)
return|return
name|date
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DateTimeValue
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
name|DateTimeValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DATE_TIME
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.ComputableValue#minus(org.exist.xquery.value.ComputableValue) 	 */
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.ComputableValue#plus(org.exist.xquery.value.ComputableValue) 	 */
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.ComputableValue#mult(org.exist.xquery.value.ComputableValue) 	 */
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.ComputableValue#div(org.exist.xquery.value.ComputableValue) 	 */
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

