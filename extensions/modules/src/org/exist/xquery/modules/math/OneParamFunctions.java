begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|math
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
name|DoubleValue
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
name|NumericValue
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
comment|/**  * Class containing math functions that accept one parameter.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|OneParamFunctions
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|OneParamFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"abs"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the absolute value of a number."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Value to return the absolute value of"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"acos"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the arccosine value of a number in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"asin"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the arcsine value of a number in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"atan"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the arctangent value of a number in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"ceil"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the smallest (closest to negative infinity) value that is not less than the argument and is equal to a mathematical integer."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"cos"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns e (the base of natural logarithms) raised to a power."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"exp"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the absolute value of a number."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"floor"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the largest (closest to positive infinity) value that is not greater than the argument and is equal to a mathematical integer."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"log"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the natural logarithm of a number."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"round"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the double value that is closest to a integer."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sin"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the sine of the number in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"sqrt"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the square root of a number."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"square root of $x"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"tan"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the tangent of the number passed as an argument in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"x"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"degrees"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Converts angle in radians to degrees."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"radians"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"degrees"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"degrees"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"radians"
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Converts angle in degrees to radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"degrees"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"radians"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"radians"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|OneParamFunctions
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
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
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|MathModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
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
name|Sequence
name|seq
init|=
name|args
index|[
literal|0
index|]
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|NumericValue
name|value
init|=
operator|(
name|NumericValue
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|double
name|calcValue
init|=
literal|0
decl_stmt|;
name|String
name|functionName
init|=
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"abs"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"acos"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|acos
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"asin"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|asin
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"atan"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|atan
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"ceil"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|ceil
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"cos"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"exp"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|exp
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"floor"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|floor
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"log"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|log
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"round"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|rint
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"sin"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|sin
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"sqrt"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|sqrt
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"tan"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|tan
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"degrees"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|toDegrees
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"radians"
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|calcValue
operator|=
name|Math
operator|.
name|toRadians
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Function "
operator|+
name|functionName
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
name|result
operator|=
operator|new
name|DoubleValue
argument_list|(
name|calcValue
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|MathModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

