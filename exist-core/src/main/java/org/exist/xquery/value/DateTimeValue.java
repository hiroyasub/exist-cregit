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
name|XMLGregorianCalendar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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

begin_comment
comment|/**  * Represents a value of type xs:dateTime.  *  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|DateTimeValue
extends|extends
name|AbstractDateTimeValue
block|{
specifier|public
name|DateTimeValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
operator|new
name|GregorianCalendar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|normalize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DateTimeValue
parameter_list|(
name|XMLGregorianCalendar
name|calendar
parameter_list|)
block|{
name|super
argument_list|(
name|fillCalendar
argument_list|(
name|cloneXMLGregorianCalendar
argument_list|(
name|calendar
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|normalize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DateTimeValue
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|super
argument_list|(
name|dateToXMLGregorianCalendar
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
name|normalize
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
name|super
argument_list|(
name|dateTime
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|calendar
operator|.
name|getXMLSchemaType
argument_list|()
operator|!=
name|DatatypeConstants
operator|.
name|DATETIME
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"xs:dateTime instance must have all fields set"
argument_list|)
throw|;
block|}
name|normalize
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|XMLGregorianCalendar
name|dateToXMLGregorianCalendar
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
specifier|final
name|GregorianCalendar
name|gc
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setTime
argument_list|(
name|date
argument_list|)
expr_stmt|;
specifier|final
name|XMLGregorianCalendar
name|xgc
init|=
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|gc
argument_list|)
decl_stmt|;
name|xgc
operator|.
name|normalize
argument_list|()
expr_stmt|;
return|return
name|xgc
return|;
block|}
specifier|private
specifier|static
name|XMLGregorianCalendar
name|fillCalendar
parameter_list|(
name|XMLGregorianCalendar
name|calendar
parameter_list|)
block|{
if|if
condition|(
name|calendar
operator|.
name|getHour
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
block|{
name|calendar
operator|.
name|setHour
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|calendar
operator|.
name|getMinute
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
block|{
name|calendar
operator|.
name|setMinute
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|calendar
operator|.
name|getSecond
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
block|{
name|calendar
operator|.
name|setSecond
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|calendar
operator|.
name|getMillisecond
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
block|{
name|calendar
operator|.
name|setMillisecond
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|calendar
return|;
block|}
specifier|protected
name|void
name|normalize
parameter_list|()
block|{
if|if
condition|(
name|calendar
operator|.
name|getHour
argument_list|()
operator|==
literal|24
operator|&&
name|calendar
operator|.
name|getMinute
argument_list|()
operator|==
literal|0
operator|&&
name|calendar
operator|.
name|getSecond
argument_list|()
operator|==
literal|0
condition|)
block|{
name|calendar
operator|.
name|setHour
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|TimeUtils
operator|.
name|ONE_DAY
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|AbstractDateTimeValue
name|createSameKind
parameter_list|(
name|XMLGregorianCalendar
name|cal
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DateTimeValue
argument_list|(
name|cal
argument_list|)
return|;
block|}
specifier|protected
name|QName
name|getXMLSchemaType
parameter_list|()
block|{
return|return
name|DatatypeConstants
operator|.
name|DATETIME
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
name|DATE_TIME
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
argument_list|)
return|;
case|case
name|Type
operator|.
name|GYEAR
case|:
return|return
operator|new
name|GYearValue
argument_list|(
name|calendar
argument_list|)
return|;
case|case
name|Type
operator|.
name|GYEARMONTH
case|:
return|return
operator|new
name|GYearMonthValue
argument_list|(
name|calendar
argument_list|)
return|;
case|case
name|Type
operator|.
name|GMONTHDAY
case|:
return|return
operator|new
name|GMonthDayValue
argument_list|(
name|calendar
argument_list|)
return|;
case|case
name|Type
operator|.
name|GDAY
case|:
return|return
operator|new
name|GDayValue
argument_list|(
name|calendar
argument_list|)
return|;
case|case
name|Type
operator|.
name|GMONTH
case|:
return|return
operator|new
name|GMonthValue
argument_list|(
name|calendar
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
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
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
name|DATE_TIME
case|:
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|getTimeInMillis
argument_list|()
operator|-
operator|(
operator|(
name|DateTimeValue
operator|)
name|other
operator|)
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
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Operand to minus should be of type xs:dateTime, xdt:dayTimeDuration or xdt:yearMonthDuration; got: "
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
name|Date
name|getDate
parameter_list|()
block|{
return|return
name|calendar
operator|.
name|toGregorianCalendar
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit

