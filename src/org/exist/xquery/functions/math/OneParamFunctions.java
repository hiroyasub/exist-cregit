begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
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
name|math
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
comment|//private static final Logger logger = Logger.getLogger(OneParamFunctions.class);
specifier|public
specifier|static
specifier|final
name|String
name|ACOS
init|=
literal|"acos"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ASIN
init|=
literal|"asin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ATAN
init|=
literal|"atan"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COS
init|=
literal|"cos"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXP
init|=
literal|"exp"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXP10
init|=
literal|"exp10"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOG
init|=
literal|"log"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOG10
init|=
literal|"log10"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SIN
init|=
literal|"sin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SQRT
init|=
literal|"sqrt"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TAN
init|=
literal|"tan"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_ACOS
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|ACOS
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the arc cosine of the argument, the result being in the range zero to +Ï radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the result"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_ASIN
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|ASIN
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the arc sine of the argument, the result being in the range -Ï/2 to +Ï/2 radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
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
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_ATAN
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|ATAN
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the arc tangent of the argument, the result being in the range -Ï/2 to +Ï/2 radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the result"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_COS
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|COS
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the cosine of the argument, expressed in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the cosine"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_EXP
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|EXP
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Calculates e (the Euler Constant) raised to the power of $arg"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"e (the Euler Constant) raised to the power of a value or expression"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_EXP10
init|=
operator|new
name|FunctionSignature
argument_list|(
comment|// NEW
operator|new
name|QName
argument_list|(
name|EXP10
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Calculates 10 raised to the power of $arg"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"e (the Euler Constant) raised to the power of a value or expression"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_LOG
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|LOG
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the natural logarithm of the argument."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the log"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_LOG10
init|=
operator|new
name|FunctionSignature
argument_list|(
comment|// NEW
operator|new
name|QName
argument_list|(
name|LOG10
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the base-ten logarithm of the argument."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the log"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_SIN
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SIN
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the sine of the argument, expressed in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the sine"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_SQRT
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|SQRT
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the non-negative square root of the argument."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The input number"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the square root of $x"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_TAN
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|TAN
argument_list|,
name|MathModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|,
literal|"Returns the tangent of the argument, expressed in radians."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The radians"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the tangent"
argument_list|)
argument_list|)
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
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
specifier|final
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
specifier|final
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
name|double
name|calcValue
init|=
literal|0
decl_stmt|;
specifier|final
name|String
name|functionName
init|=
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
decl_stmt|;
if|if
condition|(
name|ACOS
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
name|ASIN
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
name|ATAN
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
name|COS
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
name|EXP
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
name|EXP10
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
name|pow
argument_list|(
literal|10.0d
argument_list|,
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|LOG
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
name|LOG10
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
name|log10
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
name|SIN
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
name|SQRT
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
name|TAN
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
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

