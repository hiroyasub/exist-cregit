begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|Module
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
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
name|value
operator|.
name|DateTimeValue
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
name|value
operator|.
name|DateValue
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
name|value
operator|.
name|DayTimeDurationValue
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
name|value
operator|.
name|DecimalValue
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
name|value
operator|.
name|IntegerValue
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceType
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
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunGetDateTimeComponent
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnGetDayFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"day-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 1 and 31, both inclusive, representing "
operator|+
literal|"the day component in the localized value of $a."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnGetMonthFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"month-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 1 and 12, both inclusive, representing the month "
operator|+
literal|"component in the localized value of $a."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnGetYearFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"year-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the year in the localized value of $a. The value may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnHoursFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hours-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 0 and 23, both inclusive, representing the "
operator|+
literal|"value of the hours component in the localized value of $arg."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnMinutesFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"minutes-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer value between 0 to 59, both inclusive, representing the value of "
operator|+
literal|"the minutes component in the localized value of $arg."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnSecondsFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"seconds-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:decimal value between 0 and 60.999..., both inclusive, representing the "
operator|+
literal|"seconds and fractional seconds in the localized value of $arg. Note that the value can be "
operator|+
literal|"greater than 60 seconds to accommodate occasional leap seconds used to keep human time "
operator|+
literal|"synchronized with the rotation of the planet."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnTimezoneFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"timezone-from-dateTime"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the timezone component of $arg if any. If $arg has a timezone component, "
operator|+
literal|"then the result is an xdt:dayTimeDuration that indicates deviation from UTC; its value may "
operator|+
literal|"range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|FunGetDateTimeComponent
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|arg
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|DateTimeValue
name|date
init|=
operator|(
name|DateTimeValue
operator|)
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"day-from-dateTime"
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|DAY
argument_list|)
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"month-from-dateTime"
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|MONTH
argument_list|)
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"year-from-dateTime"
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|YEAR
argument_list|)
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"hours-from-dateTime"
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|HOUR
argument_list|)
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"minutes-from-dateTime"
argument_list|)
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|MINUTE
argument_list|)
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"seconds-from-dateTime"
argument_list|)
condition|)
block|{
name|long
name|millis
init|=
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|SECOND
argument_list|)
operator|*
literal|1000
operator|+
name|date
operator|.
name|getPart
argument_list|(
name|DateValue
operator|.
name|MILLISECOND
argument_list|)
decl_stmt|;
return|return
operator|new
name|DecimalValue
argument_list|(
name|millis
operator|/
literal|1000
argument_list|)
return|;
block|}
else|else
block|{
name|long
name|tzoffset
init|=
name|date
operator|.
name|getTimezoneOffset
argument_list|()
decl_stmt|;
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|tzoffset
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

