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
name|AssumptionViolatedException
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
name|ExtTestAssumptionFailedFunction
extends|extends
name|JUnitIntegrationFunction
block|{
specifier|public
name|ExtTestAssumptionFailedFunction
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
literal|"ext-test-assumption-failed-function"
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
literal|"error detail of the test"
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
name|assumption
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
name|AssumptionViolatedException
name|assumptionFailureReason
init|=
name|assumptionMapAsAssumptionViolationException
argument_list|(
name|assumption
argument_list|)
decl_stmt|;
comment|// NOTE: We remove the StackTrace, because it is not useful to have a Java Stack Trace pointing into the XML XQuery Test Suite code
name|assumptionFailureReason
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
name|fireTestAssumptionFailed
argument_list|(
operator|new
name|Failure
argument_list|(
name|description
argument_list|,
name|assumptionFailureReason
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
specifier|public
name|AssumptionViolatedException
name|assumptionMapAsAssumptionViolationException
parameter_list|(
specifier|final
name|MapType
name|assumptionMap
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|seqName
init|=
name|assumptionMap
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
if|if
condition|(
name|seqName
operator|!=
literal|null
operator|&&
operator|!
name|seqName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|name
operator|=
name|seqName
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
name|name
operator|=
literal|""
expr_stmt|;
block|}
specifier|final
name|Sequence
name|seqValue
init|=
name|assumptionMap
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
specifier|final
name|String
name|value
decl_stmt|;
if|if
condition|(
name|seqValue
operator|!=
literal|null
operator|&&
operator|!
name|seqValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|value
operator|=
name|seqValue
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
name|value
operator|=
literal|""
expr_stmt|;
block|}
return|return
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"Assumption %"
operator|+
name|name
operator|+
literal|" does not hold for: "
operator|+
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit
