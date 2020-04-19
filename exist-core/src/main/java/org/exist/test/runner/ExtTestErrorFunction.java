begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|runner
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
name|Annotation
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
name|functions
operator|.
name|map
operator|.
name|MapType
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunNotifier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|optParam
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|param
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|params
import|;
end_import

begin_class
specifier|public
class|class
name|ExtTestErrorFunction
extends|extends
name|JUnitIntegrationFunction
block|{
specifier|public
name|ExtTestErrorFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|String
name|parentName
parameter_list|,
specifier|final
name|RunNotifier
name|notifier
parameter_list|)
block|{
name|super
argument_list|(
literal|"ext-test-error-function"
argument_list|,
name|params
argument_list|(
name|param
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"name of the test"
argument_list|)
argument_list|,
name|optParam
argument_list|(
literal|"error"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
literal|"error detail of the test. e.g. map { \"code\": $err:code, \"description\": $err:description, \"value\": $err:value, \"module\": $err:module, \"line-number\": $err:line-number, \"column-number\": $err:column-number, \"additional\": $err:additional, \"xquery-stack-trace\": $exerr:xquery-stack-trace, \"java-stack-trace\": $exerr:java-stack-trace}"
argument_list|)
argument_list|)
argument_list|,
name|context
argument_list|,
name|parentName
argument_list|,
name|notifier
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|arg1
init|=
name|getCurrentArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|arg1
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|arg2
init|=
name|getCurrentArguments
argument_list|()
operator|.
name|length
operator|==
literal|2
condition|?
name|getCurrentArguments
argument_list|()
index|[
literal|1
index|]
else|:
literal|null
decl_stmt|;
specifier|final
name|MapType
name|error
init|=
name|arg2
operator|!=
literal|null
condition|?
operator|(
name|MapType
operator|)
name|arg2
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|Description
name|description
init|=
name|Description
operator|.
name|createTestDescription
argument_list|(
name|suiteName
argument_list|,
name|name
argument_list|,
operator|new
name|Annotation
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// notify JUnit
try|try
block|{
specifier|final
name|XPathException
name|errorReason
init|=
name|errorMapAsXPathException
argument_list|(
name|error
argument_list|)
decl_stmt|;
name|notifier
operator|.
name|fireTestFailure
argument_list|(
operator|new
name|Failure
argument_list|(
name|description
argument_list|,
name|errorReason
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
comment|//signal internal failure
name|notifier
operator|.
name|fireTestFailure
argument_list|(
operator|new
name|Failure
argument_list|(
name|description
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|XPathException
name|errorMapAsXPathException
parameter_list|(
specifier|final
name|MapType
name|errorMap
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|seqDescription
init|=
name|errorMap
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"description"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|description
decl_stmt|;
if|if
condition|(
name|seqDescription
operator|!=
literal|null
operator|&&
operator|!
name|seqDescription
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|description
operator|=
name|seqDescription
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|description
operator|=
literal|""
expr_stmt|;
block|}
specifier|final
name|Sequence
name|seqErrorCode
init|=
name|errorMap
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"code"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ErrorCodes
operator|.
name|ErrorCode
name|errorCode
decl_stmt|;
if|if
condition|(
name|seqErrorCode
operator|!=
literal|null
operator|&&
operator|!
name|seqErrorCode
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|errorCode
operator|=
operator|new
name|ErrorCodes
operator|.
name|ErrorCode
argument_list|(
operator|(
operator|(
name|QNameValue
operator|)
name|seqErrorCode
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getQName
argument_list|()
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|errorCode
operator|=
name|ErrorCodes
operator|.
name|ERROR
expr_stmt|;
block|}
specifier|final
name|Sequence
name|seqLineNumber
init|=
name|errorMap
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"line-number"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|lineNumber
decl_stmt|;
if|if
condition|(
name|seqLineNumber
operator|!=
literal|null
operator|&&
operator|!
name|seqLineNumber
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|lineNumber
operator|=
name|seqLineNumber
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|int
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lineNumber
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|Sequence
name|seqColumnNumber
init|=
name|errorMap
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"column-number"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|columnNumber
decl_stmt|;
if|if
condition|(
name|seqColumnNumber
operator|!=
literal|null
operator|&&
operator|!
name|seqColumnNumber
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|columnNumber
operator|=
name|seqColumnNumber
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|int
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|columnNumber
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|XPathException
name|xpe
init|=
operator|new
name|XPathException
argument_list|(
name|lineNumber
argument_list|,
name|columnNumber
argument_list|,
name|errorCode
argument_list|,
name|description
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|seqJavaStackTrace
init|=
name|errorMap
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"java-stack-trace"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|seqJavaStackTrace
operator|!=
literal|null
operator|&&
operator|!
name|seqJavaStackTrace
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|xpe
operator|.
name|setStackTrace
argument_list|(
name|convertStackTraceElements
argument_list|(
name|seqJavaStackTrace
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NullPointerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|xpe
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Pattern
name|PTN_CAUSED_BY
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Caused by:\\s([a-zA-Z0-9_$\\.]+)(?::\\s(.+))?"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|PTN_AT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"at\\s((?:[a-zA-Z0-9_$]+)(?:\\.[a-zA-Z0-9_$]+)*)\\.([a-zA-Z0-9_$-]+)\\(([a-zA-Z0-9_]+\\.java):([0-9]+)\\)"
argument_list|)
decl_stmt|;
specifier|protected
name|StackTraceElement
index|[]
name|convertStackTraceElements
parameter_list|(
specifier|final
name|Sequence
name|seqJavaStackTrace
parameter_list|)
throws|throws
name|XPathException
block|{
name|StackTraceElement
index|[]
name|traceElements
init|=
operator|new
name|StackTraceElement
index|[
name|seqJavaStackTrace
operator|.
name|getItemCount
argument_list|()
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|Matcher
name|matcherAt
init|=
name|PTN_AT
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|// index 0 is the first `Caused by: ...`
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|seqJavaStackTrace
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|item
init|=
name|seqJavaStackTrace
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|StackTraceElement
name|stackTraceElement
init|=
name|convertStackTraceElement
argument_list|(
name|matcherAt
argument_list|,
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|stackTraceElement
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|traceElements
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|stackTraceElement
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|seqJavaStackTrace
operator|.
name|getItemCount
argument_list|()
condition|)
block|{
name|traceElements
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|traceElements
argument_list|,
name|i
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
name|traceElements
return|;
block|}
specifier|private
annotation|@
name|Nullable
name|StackTraceElement
name|convertStackTraceElement
parameter_list|(
specifier|final
name|Matcher
name|matcherAt
parameter_list|,
specifier|final
name|String
name|s
parameter_list|)
block|{
name|matcherAt
operator|.
name|reset
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcherAt
operator|.
name|matches
argument_list|()
condition|)
block|{
specifier|final
name|String
name|declaringClass
init|=
name|matcherAt
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|methodName
init|=
name|matcherAt
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fileName
init|=
name|matcherAt
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|String
name|lineNumber
init|=
name|matcherAt
operator|.
name|group
argument_list|(
literal|4
argument_list|)
decl_stmt|;
return|return
operator|new
name|StackTraceElement
argument_list|(
name|declaringClass
argument_list|,
name|methodName
argument_list|,
name|fileName
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|lineNumber
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

