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
name|util
operator|.
name|serializer
operator|.
name|XQuerySerializer
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
name|Item
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
name|StringValue
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
name|junit
operator|.
name|ComparisonFailure
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|ExtTestFailureFunction
extends|extends
name|JUnitIntegrationFunction
block|{
specifier|public
name|ExtTestFailureFunction
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
literal|"ext-test-failure-function"
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
name|param
argument_list|(
literal|"expected"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
literal|"expected result of the test"
argument_list|)
argument_list|,
name|param
argument_list|(
literal|"actual"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
literal|"actual result of the test"
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
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|MapType
name|expected
init|=
operator|(
name|MapType
operator|)
name|arg2
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|arg3
init|=
name|getCurrentArguments
argument_list|()
index|[
literal|2
index|]
decl_stmt|;
specifier|final
name|MapType
name|actual
init|=
operator|(
name|MapType
operator|)
name|arg3
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
name|AssertionError
name|failureReason
init|=
operator|new
name|ComparisonFailure
argument_list|(
literal|""
argument_list|,
name|expectedToString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|actualToString
argument_list|(
name|actual
argument_list|)
argument_list|)
decl_stmt|;
comment|// NOTE: We remove the StackTrace, because it is not useful to have a Java Stack Trace pointing into the XML XQuery Test Suite code
name|failureReason
operator|.
name|setStackTrace
argument_list|(
operator|new
name|StackTraceElement
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|notifier
operator|.
name|fireTestFailure
argument_list|(
operator|new
name|Failure
argument_list|(
name|description
argument_list|,
name|failureReason
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
decl||
name|SAXException
decl||
name|IOException
decl||
name|IllegalStateException
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
name|String
name|expectedToString
parameter_list|(
specifier|final
name|MapType
name|expected
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|Sequence
name|seqExpectedValue
init|=
name|expected
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|seqExpectedValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|seqToString
argument_list|(
name|seqExpectedValue
argument_list|)
return|;
block|}
specifier|final
name|Sequence
name|seqExpectedXPath
init|=
name|expected
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"xpath"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|seqExpectedXPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"XPath: "
operator|+
name|seqToString
argument_list|(
name|seqExpectedXPath
argument_list|)
return|;
block|}
specifier|final
name|Sequence
name|seqExpectedError
init|=
name|expected
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"error"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|seqExpectedError
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"Error: "
operator|+
name|seqToString
argument_list|(
name|seqExpectedError
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not extract expected value"
argument_list|)
throw|;
block|}
specifier|private
name|String
name|actualToString
parameter_list|(
specifier|final
name|MapType
name|actual
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|Sequence
name|seqActualError
init|=
name|actual
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"error"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|seqActualError
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|errorMapToString
argument_list|(
name|seqActualError
argument_list|)
return|;
block|}
specifier|final
name|Sequence
name|seqActualResult
init|=
name|actual
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"result"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|seqActualResult
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|seqToString
argument_list|(
name|seqActualResult
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
comment|// empty-sequence()
block|}
block|}
specifier|private
name|String
name|seqToString
parameter_list|(
specifier|final
name|Sequence
name|seq
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|SAXException
block|{
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|XQuerySerializer
name|xquerySerializer
init|=
operator|new
name|XQuerySerializer
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|xquerySerializer
operator|.
name|serialize
argument_list|(
name|seq
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
name|String
name|errorMapToString
parameter_list|(
specifier|final
name|Sequence
name|seqErrorMap
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|SAXException
block|{
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"adaptive"
argument_list|)
expr_stmt|;
specifier|final
name|XQuerySerializer
name|xquerySerializer
init|=
operator|new
name|XQuerySerializer
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|properties
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|xquerySerializer
operator|.
name|serialize
argument_list|(
name|seqErrorMap
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

