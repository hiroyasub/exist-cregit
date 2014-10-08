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
name|StringValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * format-time( $value  as xs:time?, $picture  as xs:string,   * 	$language  as xs:string?, $calendar  as xs:string?,   * 	$country  as xs:string?) as xs:string?   *   * format-time($value as xs:time?, $picture as xs:string) as xs:string?   *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Format_time
extends|extends
name|BasicFunction
block|{
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
literal|"format-time"
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
name|FormatFunctionConstants
operator|.
name|TIME_FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|FormatFunctionConstants
operator|.
name|TIME_PARAMETER
block|,
name|FormatFunctionConstants
operator|.
name|PICTURE_PARAMETER
block|,
name|FormatFunctionConstants
operator|.
name|LANGUAGE_PARAMETER
block|,
name|FormatFunctionConstants
operator|.
name|CALENDAR_PARAMETER
block|,
name|FormatFunctionConstants
operator|.
name|COUNTRY_PARAMETER
block|}
argument_list|,
name|FormatFunctionConstants
operator|.
name|RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"format-time"
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
name|FormatFunctionConstants
operator|.
name|TIME_FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|FormatFunctionConstants
operator|.
name|TIME_PARAMETER
block|,
name|FormatFunctionConstants
operator|.
name|PICTURE_PARAMETER
block|}
argument_list|,
name|FormatFunctionConstants
operator|.
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|Format_time
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
try|try
block|{
name|TimeValue
name|value
init|=
operator|(
name|TimeValue
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
name|String
name|picture
init|=
name|FormatFunctionConstants
operator|.
name|translate
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|language
init|=
operator|(
name|args
operator|.
name|length
operator|<=
literal|2
operator|||
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|calendar
init|=
operator|(
name|args
operator|.
name|length
operator|<=
literal|2
operator|||
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|country
init|=
operator|(
name|args
operator|.
name|length
operator|<=
literal|2
operator|||
name|args
index|[
literal|4
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|args
index|[
literal|4
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|format
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|language
operator|!=
literal|null
operator|||
name|country
operator|!=
literal|null
condition|)
block|{
name|Locale
name|locale
init|=
operator|(
name|country
operator|==
literal|null
operator|)
condition|?
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
else|:
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
decl_stmt|;
name|format
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|picture
argument_list|,
name|locale
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|format
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|picture
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StringValue
argument_list|(
name|format
operator|.
name|format
argument_list|(
name|value
operator|.
name|toJavaObject
argument_list|(
name|java
operator|.
name|util
operator|.
name|Date
operator|.
name|class
argument_list|)
argument_list|)
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

