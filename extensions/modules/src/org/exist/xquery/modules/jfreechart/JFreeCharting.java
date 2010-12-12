begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|jfreechart
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|ResponseWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
operator|.
name|Serializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
operator|.
name|node
operator|.
name|NodeInputStream
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
name|NodeValue
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
name|Variable
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
name|response
operator|.
name|ResponseModule
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
name|JavaObjectValue
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|Base64BinaryValueType
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
name|BinaryValueFromInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jfree
operator|.
name|chart
operator|.
name|ChartUtilities
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jfree
operator|.
name|chart
operator|.
name|JFreeChart
import|;
end_import

begin_comment
comment|/**  * JFreechart extension functions.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  * @author Andrzej Taramina (andrzej@chaeron.com)  */
end_comment

begin_class
specifier|public
class|class
name|JFreeCharting
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
name|JFreeCharting
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|function1Txt
init|=
literal|"Render chart using JFreechart. Check documentation on "
operator|+
literal|"http://www.jfree.org/jfreechart/ for details about chart types, "
operator|+
literal|"parameters and data structures."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|function2Txt
init|=
name|function1Txt
operator|+
literal|" Output is directly streamed into the servlet output stream."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|chartText
init|=
literal|"The type of chart to render.  Supported chart types: "
operator|+
literal|"LineChart, LineChart3D, "
operator|+
literal|"MultiplePieChart, MultiplePieChart3D, PieChart, PieChart3D, "
operator|+
literal|"RingChart, StackedAreaChart, StackedBarChart, StackedBarChart3D, "
operator|+
literal|"WaterfallChart."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|parametersText
init|=
literal|"The configuration for the chart.  The "
operator|+
literal|"configuration should be supplied as follows:<configuration>"
operator|+
literal|"<param1>Value1</param1><param2>Value2</param2>/<configuration>.  "
operator|+
literal|"Supported parameters: width height title categoryAxisLabel timeAxisLabel "
operator|+
literal|"valueAxisLabel domainAxisLabel rangeAxisLabel pieSectionLabel pieSectionNumberFormat pieSectionPercentFormat orientation "
operator|+
literal|"titleColor chartBackgroundColor plotBackgroundColor rangeLowerBound rangeUpperrBound categoryItemLabelGeneratorClass seriesColors sectionColors sectionColorsDelimiter "
operator|+
literal|"categoryAxisColor valueAxisColortimeAxisColor "
operator|+
literal|"order legend tooltips urls."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"render"
argument_list|,
name|JFreeChartModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|JFreeChartModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|function1Txt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"chart-type"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|chartText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"configuration"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|parametersText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"data"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The CategoryDataset or PieDataset, supplied as JFreechart XML."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the generated PNG image file"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"stream-render"
argument_list|,
name|JFreeChartModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|JFreeChartModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|function2Txt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"chart-type"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|chartText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"configuration"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|parametersText
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"data"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The CategoryDataset or PieDataset, supplied as JFreechart XML."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|JFreeCharting
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
annotation|@
name|Override
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
comment|//was an image and a mime-type speficifed
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
operator|||
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
try|try
block|{
comment|// Get chart type
name|String
name|chartType
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// Get configuration
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|parse
argument_list|(
operator|(
operator|(
name|NodeValue
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
operator|)
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get datastream
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|NodeInputStream
argument_list|(
name|serializer
argument_list|,
name|node
argument_list|)
decl_stmt|;
comment|// get chart
name|JFreeChart
name|chart
init|=
literal|null
decl_stmt|;
try|try
block|{
name|chart
operator|=
name|JFreeChartFactory
operator|.
name|createJFreeChart
argument_list|(
name|chartType
argument_list|,
name|config
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|// Verify if chart is present
if|if
condition|(
name|chart
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unable to create chart '"
operator|+
name|chartType
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|// Render output
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"render"
argument_list|)
condition|)
block|{
name|byte
index|[]
name|image
init|=
name|writePNG
argument_list|(
name|config
argument_list|,
name|chart
argument_list|)
decl_stmt|;
return|return
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|image
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|ResponseWrapper
name|response
init|=
name|getResponseWrapper
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|writePNGtoResponse
argument_list|(
name|config
argument_list|,
name|response
argument_list|,
name|chart
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|/**      *  Get HTTP response wrapper which provides access to the servler      * outputstream.      *      * @throws XPathException Thrown when something bad happens.      */
specifier|private
name|ResponseWrapper
name|getResponseWrapper
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
name|ResponseModule
name|myModule
init|=
operator|(
name|ResponseModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
comment|// response object is read from global variable $response
name|Variable
name|respVar
init|=
name|myModule
operator|.
name|resolveVariable
argument_list|(
name|ResponseModule
operator|.
name|RESPONSE_VAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|respVar
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"No response object found in the current XQuery context."
argument_list|)
throw|;
block|}
if|if
condition|(
name|respVar
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|JAVA_OBJECT
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Variable $response is not bound to an Java object."
argument_list|)
throw|;
block|}
name|JavaObjectValue
name|respValue
init|=
operator|(
name|JavaObjectValue
operator|)
name|respVar
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
literal|"org.exist.http.servlets.HttpResponseWrapper"
operator|.
name|equals
argument_list|(
name|respValue
operator|.
name|getObject
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|signatures
index|[
literal|1
index|]
operator|.
name|toString
argument_list|()
operator|+
literal|" can only be used within the EXistServlet or XQueryServlet"
argument_list|)
throw|;
block|}
name|ResponseWrapper
name|response
init|=
operator|(
name|ResponseWrapper
operator|)
name|respValue
operator|.
name|getObject
argument_list|()
decl_stmt|;
return|return
name|response
return|;
block|}
comment|/**      *  Writes chart to response wrapper as PNG image.      *      * @throws XPathException Thrown when an IO exception is thrown,      */
specifier|private
name|void
name|writePNGtoResponse
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|ResponseWrapper
name|response
parameter_list|,
name|JFreeChart
name|chart
parameter_list|)
throws|throws
name|XPathException
block|{
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"image/png"
argument_list|)
expr_stmt|;
name|os
operator|=
name|response
operator|.
name|getOutputStream
argument_list|()
expr_stmt|;
name|ChartUtilities
operator|.
name|writeChartAsPNG
argument_list|(
name|os
argument_list|,
name|chart
argument_list|,
name|config
operator|.
name|getImageWidth
argument_list|()
argument_list|,
name|config
operator|.
name|getImageHeight
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"IO issue while serializing image. "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// Ignore
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|byte
index|[]
name|writePNG
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|JFreeChart
name|chart
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ChartUtilities
operator|.
name|writeChartAsPNG
argument_list|(
name|os
argument_list|,
name|chart
argument_list|,
name|config
operator|.
name|getImageWidth
argument_list|()
argument_list|,
name|config
operator|.
name|getImageHeight
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|os
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

