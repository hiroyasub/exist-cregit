begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|Dependency
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
name|Function
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
name|Profiler
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
name|AbstractDateTimeValue
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
name|FunctionReturnSequenceType
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
name|FunctionParameterSequenceType
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
comment|/**  *  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|FunGetDateComponent
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FunGetDateComponent
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|DATE_01_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"date"
argument_list|,
name|Type
operator|.
name|DATE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The date as xs:date"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|TIME_01_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"time"
argument_list|,
name|Type
operator|.
name|TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The time as xs:time"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|DATE_TIME_01_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"date-time"
argument_list|,
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The date-time as xs:dateTime"
argument_list|)
decl_stmt|;
comment|// ----- fromDate
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnDayFromDate
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"day-from-date"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 1 and 31, both inclusive, representing "
operator|+
literal|"the day component in the localized value of $date."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the day component from $date"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnMonthFromDate
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"month-from-date"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 1 and 12, both inclusive, representing the month "
operator|+
literal|"component in the localized value of $date."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the month component from $date"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnYearFromDate
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"year-from-date"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the year in the localized value of $date. The value may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the year component from $date"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnTimezoneFromDate
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"timezone-from-date"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the timezone component of $date if any. If $date has a timezone component, then the result is an xs:dayTimeDuration that indicates deviation from UTC; its value may range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence."
operator|+
literal|"If $date is the empty sequence, returns the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the timezone component from $date"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ----- fromTime
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnHoursFromTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hours-from-time"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 0 and 23, both inclusive, representing the "
operator|+
literal|"value of the hours component in the localized value of $time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the hours component from $time"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnMinutesFromTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"minutes-from-time"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer value between 0 to 59, both inclusive, representing the value of "
operator|+
literal|"the minutes component in the localized value of $time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the minutes component from $time"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnSecondsFromTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"seconds-from-time"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:decimal value between 0 and 60.999..., both inclusive, representing the "
operator|+
literal|"seconds and fractional seconds in the localized value of $date. Note that the value can be "
operator|+
literal|"greater than 60 seconds to accommodate occasional leap seconds used to keep human time "
operator|+
literal|"synchronized with the rotation of the planet."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the seconds component from $time"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnTimezoneFromTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"timezone-from-time"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the timezone component of $time if any. If $time has a timezone component, "
operator|+
literal|"then the result is an xdt:dayTimeDuration that indicates deviation from UTC; its value may "
operator|+
literal|"range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the timezone component from $time"
argument_list|)
argument_list|)
decl_stmt|;
comment|// ----- fromDateTime
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnDayFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"day-from-dateTime"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 1 and 31, both inclusive, representing "
operator|+
literal|"the day component in the localized value of $date-time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the day component from $date-time"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnMonthFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"month-from-dateTime"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 1 and 12, both inclusive, representing the month "
operator|+
literal|"component in the localized value of $date-time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the month component from $date-time"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnYearFromDateTime
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"year-from-dateTime"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the year in the localized value of $date-time. The value may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the year component from $date-time"
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
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer between 0 and 23, both inclusive, representing the "
operator|+
literal|"value of the hours component in the localized value of $date-time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the hours component from $date-time"
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
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer value between 0 to 59, both inclusive, representing the value of "
operator|+
literal|"the minutes component in the localized value of $date-time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the minutes component from $date-time"
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
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:decimal value between 0 and 60.999..., both inclusive, representing the "
operator|+
literal|"seconds and fractional seconds in the localized value of $date-time. Note that the value can be "
operator|+
literal|"greater than 60 seconds to accommodate occasional leap seconds used to keep human time "
operator|+
literal|"synchronized with the rotation of the planet."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the seconds component from $date-time"
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
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the timezone component of $date-time if any. If $date-time has a timezone component, "
operator|+
literal|"then the result is an xdt:dayTimeDuration that indicates deviation from UTC; its value may "
operator|+
literal|"range from +14:00 to -14:00 hours, both inclusive. Otherwise, the result is the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DATE_TIME_01_PARAM
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the timezone component from $date-time"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunGetDateComponent
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
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
block|}
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Sequence
name|arg
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|AbstractDateTimeValue
name|date
init|=
operator|(
name|AbstractDateTimeValue
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
operator|||
name|isCalledAs
argument_list|(
literal|"day-from-date"
argument_list|)
condition|)
block|{
name|result
operator|=
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
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"month-from-dateTime"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"month-from-date"
argument_list|)
condition|)
block|{
name|result
operator|=
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
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"year-from-dateTime"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"year-from-date"
argument_list|)
condition|)
block|{
name|result
operator|=
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
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"hours-from-dateTime"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"hours-from-time"
argument_list|)
condition|)
block|{
name|result
operator|=
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
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"minutes-from-dateTime"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"minutes-from-time"
argument_list|)
condition|)
block|{
name|result
operator|=
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
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"seconds-from-dateTime"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"seconds-from-time"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|date
operator|.
name|calendar
operator|.
name|getSecond
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|)
expr_stmt|;
if|if
condition|(
name|date
operator|.
name|calendar
operator|.
name|getFractionalSecond
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|(
operator|(
name|DecimalValue
operator|)
name|result
operator|)
operator|.
name|plus
argument_list|(
operator|new
name|DecimalValue
argument_list|(
name|date
operator|.
name|calendar
operator|.
name|getFractionalSecond
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"timezone-from-dateTime"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"timezone-from-date"
argument_list|)
operator|||
name|isCalledAs
argument_list|(
literal|"timezone-from-time"
argument_list|)
condition|)
block|{
name|result
operator|=
name|date
operator|.
name|getTimezone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"can't handle function "
operator|+
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Error
argument_list|(
literal|"can't handle function "
operator|+
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

