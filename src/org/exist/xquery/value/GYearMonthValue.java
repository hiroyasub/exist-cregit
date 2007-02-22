begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|GregorianCalendar
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

begin_class
specifier|public
class|class
name|GYearMonthValue
extends|extends
name|AbstractDateTimeValue
block|{
specifier|public
name|GYearMonthValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|stripCalendar
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
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GYearMonthValue
parameter_list|(
name|XMLGregorianCalendar
name|calendar
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|stripCalendar
argument_list|(
operator|(
name|XMLGregorianCalendar
operator|)
name|calendar
operator|.
name|clone
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GYearMonthValue
parameter_list|(
name|String
name|timeValue
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|timeValue
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
name|GYEARMONTH
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"xs:gYearMonth instance must not have year, month or day fields set"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|XMLGregorianCalendar
name|stripCalendar
parameter_list|(
name|XMLGregorianCalendar
name|calendar
parameter_list|)
block|{
name|calendar
operator|=
operator|(
name|XMLGregorianCalendar
operator|)
name|calendar
operator|.
name|clone
argument_list|()
expr_stmt|;
name|calendar
operator|.
name|setDay
argument_list|(
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setHour
argument_list|(
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setMinute
argument_list|(
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setSecond
argument_list|(
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|setMillisecond
argument_list|(
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
argument_list|)
expr_stmt|;
return|return
name|calendar
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
name|GYEARMONTH
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
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot cast xs:time to "
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
name|GYearMonthValue
argument_list|(
name|cal
argument_list|)
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
name|GYEARMONTH
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
name|GYEARMONTH
return|;
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
name|getType
argument_list|()
operator|==
name|getType
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|getTimezone
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|other
operator|)
operator|.
name|getTimezone
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|getTimezone
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Constants
operator|.
name|EQ
argument_list|,
operator|(
name|DayTimeDurationValue
operator|)
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|other
operator|)
operator|.
name|getTimezone
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|DatatypeConstants
operator|.
name|LESSER
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|getTimezone
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"PT0S"
argument_list|)
condition|)
return|return
name|DatatypeConstants
operator|.
name|LESSER
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|other
operator|)
operator|.
name|getTimezone
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|DayTimeDurationValue
operator|)
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|other
operator|)
operator|.
name|getTimezone
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"PT0S"
argument_list|)
condition|)
return|return
name|DatatypeConstants
operator|.
name|LESSER
return|;
block|}
block|}
block|}
comment|// filling in missing timezones with local timezone, should be total order as per XPath 2.0 10.4
name|int
name|r
init|=
name|this
operator|.
name|getTrimmedCalendar
argument_list|()
operator|.
name|compare
argument_list|(
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|other
operator|)
operator|.
name|getTrimmedCalendar
argument_list|()
argument_list|)
decl_stmt|;
comment|//getImplicitCalendar().compare(((AbstractDateTimeValue) other).getImplicitCalendar());
if|if
condition|(
name|r
operator|==
name|DatatypeConstants
operator|.
name|INDETERMINATE
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"indeterminate order between "
operator|+
name|this
operator|+
literal|" and "
operator|+
name|other
argument_list|)
throw|;
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
literal|"Subtraction is not supported on values of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

