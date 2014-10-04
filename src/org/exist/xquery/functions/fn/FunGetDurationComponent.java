begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|DurationValue
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
comment|/**  *  * @author wolf  * @author piotr kaminski  *  */
end_comment

begin_class
specifier|public
class|class
name|FunGetDurationComponent
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
name|FunGetDurationComponent
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|DAYTIME_DURA_01_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"duration"
argument_list|,
name|Type
operator|.
name|DAY_TIME_DURATION
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The duration as xs:dayTimeDuration"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|YEARMONTH_DURA_01_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"duration"
argument_list|,
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The duration as xs:yearMonthDuration"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnDaysFromDuration
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"days-from-duration"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the days component in the canonical lexical "
operator|+
literal|"representation of the value of $duration. The result may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DAYTIME_DURA_01_PARAM
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
literal|"the days component of $duration"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnHoursFromDuration
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hours-from-duration"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the hours component in the canonical lexical "
operator|+
literal|"representation of the value of $duration. The result may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DAYTIME_DURA_01_PARAM
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
literal|"the hours component of $duration"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnMinutesFromDuration
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"minutes-from-duration"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the minutes component in the canonical "
operator|+
literal|"lexical representation of the value of $duration. The result may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DAYTIME_DURA_01_PARAM
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
literal|"the minutes component of $duration"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnSecondsFromDuration
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"seconds-from-duration"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:decimal representing the seconds component in the canonical lexical "
operator|+
literal|"representation of the value of $duration. The result may be negative"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DAYTIME_DURA_01_PARAM
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
literal|"the seconds component of $duration"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnMonthsFromDuration
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"months-from-duration"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the months component in the canonical lexical "
operator|+
literal|"representation of the value of $duration. The result may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|YEARMONTH_DURA_01_PARAM
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
literal|"the months component of $duration"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|fnYearsFromDuration
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"years-from-duration"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns an xs:integer representing the years component in the canonical lexical "
operator|+
literal|"representation of the value of $duration. The result may be negative."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|YEARMONTH_DURA_01_PARAM
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
literal|"the years component of $duration"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunGetDurationComponent
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
name|DurationValue
name|duration
init|=
operator|new
name|DurationValue
argument_list|(
operator|(
operator|(
name|DurationValue
operator|)
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"days-from-duration"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|duration
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|DAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"hours-from-duration"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|duration
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|HOUR
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"minutes-from-duration"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|duration
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|MINUTE
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"seconds-from-duration"
argument_list|)
condition|)
block|{
if|if
condition|(
name|duration
operator|.
name|getCanonicalDuration
argument_list|()
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|DecimalValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|DecimalValue
argument_list|(
operator|(
name|BigDecimal
operator|)
name|duration
operator|.
name|getCanonicalDuration
argument_list|()
operator|.
name|getField
argument_list|(
name|DatatypeConstants
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|duration
operator|.
name|getCanonicalDuration
argument_list|()
operator|.
name|getSign
argument_list|()
operator|<
literal|0
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
name|negate
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"months-from-duration"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|duration
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|MONTH
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"years-from-duration"
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|duration
operator|.
name|getPart
argument_list|(
name|DurationValue
operator|.
name|YEAR
argument_list|)
argument_list|)
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

