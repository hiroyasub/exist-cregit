begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|TimeValue
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  *  * @author perig  *  */
end_comment

begin_class
specifier|public
class|class
name|FunDateTime
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"dateTime"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Creates an xs:dateTime from an xs:date, $date, and an xs:time, $time."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
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
block|,
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
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the combined date and time as xs:dateTime"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunDateTime
parameter_list|(
name|XQueryContext
name|context
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
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|||
name|args
index|[
literal|1
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
if|else if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|hasMany
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Expected at most one xs:date"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
throw|;
block|}
if|else if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|hasMany
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Expected at most one xs:time"
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|DateValue
name|dv
init|=
operator|(
name|DateValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|TimeValue
name|tv
init|=
operator|(
name|TimeValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dv
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
name|tv
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
name|dv
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
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|EQ
argument_list|,
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|tv
operator|.
name|getTimezone
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
argument_list|)
condition|)
block|{
specifier|final
name|ValueSequence
name|argsSeq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|argsSeq
operator|.
name|add
argument_list|(
name|dv
argument_list|)
expr_stmt|;
name|argsSeq
operator|.
name|add
argument_list|(
name|tv
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FORG0008
argument_list|,
literal|"Operands have different timezones"
argument_list|,
name|argsSeq
argument_list|)
throw|;
block|}
block|}
block|}
name|String
name|dtv
init|=
operator|(
operator|(
name|DateTimeValue
operator|)
name|dv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|)
operator|)
operator|.
name|getTrimmedCalendar
argument_list|()
operator|.
name|toXMLFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|dv
operator|.
name|getTimezone
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|dtv
operator|=
name|dtv
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dtv
operator|.
name|length
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|DateTimeValue
argument_list|(
name|dtv
operator|+
name|tv
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"PT0S"
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|dv
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
argument_list|)
condition|)
block|{
name|dtv
operator|=
name|dtv
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dtv
operator|.
name|length
argument_list|()
operator|-
literal|9
argument_list|)
expr_stmt|;
if|if
condition|(
name|tv
operator|.
name|getTimezone
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|DateTimeValue
argument_list|(
name|dtv
operator|+
name|tv
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"Z"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|DateTimeValue
argument_list|(
name|dtv
operator|+
name|tv
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|tv
operator|.
name|getTimezone
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|tz
init|=
name|dtv
operator|.
name|substring
argument_list|(
literal|19
argument_list|)
decl_stmt|;
name|dtv
operator|=
name|dtv
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dtv
operator|.
name|length
argument_list|()
operator|-
literal|14
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|DateTimeValue
argument_list|(
name|dtv
operator|+
name|tv
operator|.
name|getStringValue
argument_list|()
operator|+
name|tz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dtv
operator|=
name|dtv
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dtv
operator|.
name|length
argument_list|()
operator|-
literal|14
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|DateTimeValue
argument_list|(
name|dtv
operator|+
name|tv
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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

