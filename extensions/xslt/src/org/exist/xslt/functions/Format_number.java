begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
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
name|java
operator|.
name|lang
operator|.
name|String
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_comment
comment|/**  * format-number($value as numeric?, $picture as xs:string) as xs:string   * format-number($value as numeric?, $picture as xs:string, $decimal-format-name as xs:string) as xs:string   *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Format_number
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|SequenceType
name|NUMBER_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"number"
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The number to format"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SequenceType
name|FORMAT_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"format"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The format pattern string.  Please see the JavaDoc for java.text.DecimalFormat to get the specifics of this format string."
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SequenceType
name|DECIMAL_FORMAT_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"decimalformat"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The decimal-format name must be a QName, which is expanded as described in [2.4 Qualified Names]. It is an error if the stylesheet does not contain a declaration of the decimal-format with the specified expanded-name."
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|FUNCTION_RETURN_TYPE
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"the formatted string"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT_NUMBER_DESCRIPTION
init|=
literal|"The format-number function converts its first argument to a string using the format pattern"
operator|+
literal|" string specified by the second argument and the decimal-format named by the third argument, or the default decimal-format, if there is no"
operator|+
literal|" third argument. The format pattern string is in the syntax specified by the JDK 1.1 DecimalFormat class. The format pattern string is in a"
operator|+
literal|" localized notation: the decimal-format determines what characters have a special meaning in the pattern (with the exception of the quote character,"
operator|+
literal|" which is not localized). The format pattern must not contain the currency sign (#x00A4); support for this feature was added after the initial release"
operator|+
literal|" of JDK 1.1. The decimal-format name must be a QName, which is expanded as described in [2.4 Qualified Names]. It is an error if the stylesheet does"
operator|+
literal|" not contain a declaration of the decimal-format with the specified expanded-name."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT_DECIMAL_NUMBER_DESCRIPTION
init|=
literal|"The format-number function converts its first argument to a string using the format pattern"
operator|+
literal|" string specified by the second argument and the decimal-format named by the third argument, or the default decimal-format, if there is no"
operator|+
literal|" third argument. The format pattern string is in the syntax specified by the JDK 1.1 DecimalFormat class. The format pattern string is in a"
operator|+
literal|" localized notation: the decimal-format determines what characters have a special meaning in the pattern (with the exception of the quote character,"
operator|+
literal|" which is not localized). The format pattern must not contain the currency sign (#x00A4); support for this feature was added after the initial release"
operator|+
literal|" of JDK 1.1. The decimal-format name must be a QName, which is expanded as described in [2.4 Qualified Names]. It is an error if the stylesheet does"
operator|+
literal|" not contain a declaration of the decimal-format with the specified expanded-name. NOTE: The decimalformat parameter is currently not implemented and is ignored."
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
literal|"format-number"
argument_list|,
name|XSLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XSLModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|FORMAT_NUMBER_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NUMBER_PARAMETER
block|,
name|FORMAT_PARAMETER
block|}
argument_list|,
name|FUNCTION_RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"format-number"
argument_list|,
name|XSLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XSLModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|FORMAT_DECIMAL_NUMBER_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NUMBER_PARAMETER
block|,
name|FORMAT_PARAMETER
block|,
name|DECIMAL_FORMAT_PARAMETER
block|}
argument_list|,
name|FUNCTION_RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|Format_number
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
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|NumericValue
name|numericValue
init|=
operator|(
name|NumericValue
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
try|try
block|{
name|String
name|value
init|=
operator|new
name|DecimalFormat
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|format
argument_list|(
name|numericValue
operator|.
name|getDouble
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

