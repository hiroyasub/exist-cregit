begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id: BuiltinFunctions.java 9598 2009-07-31 05:45:57Z ixitar $  */
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
name|util
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
name|util
operator|.
name|Base64Decoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Base64Encoder
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
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_comment
comment|/**  * Base64 String conversion functions.  *  * @author  Andrzej Taramina (andrzej@chaeron.com)  */
end_comment

begin_class
specifier|public
class|class
name|Base64Functions
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
name|Base64Functions
operator|.
name|class
argument_list|)
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
literal|"base64-encode"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Encodes the given string as Base64"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string to be Base64 encoded"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the Base64 encoded output, with trailing newlines trimmed"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"base64-encode"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Encodes the given string as Base64"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The string to be Base64 encoded"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"trim"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Trim trailing newlines?"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the Base64 encoded output"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"base64-decode"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Decode the given Base64 encoded string back to clear text"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"string"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The Base64 string to be decoded"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the decoded output"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Base64Functions
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
name|Sequence
name|value
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|boolean
name|trim
init|=
literal|true
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|str
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|trim
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"base64-encode"
argument_list|)
condition|)
block|{
name|Base64Encoder
name|enc
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|enc
operator|.
name|translate
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|trim
condition|)
block|{
name|value
operator|=
operator|new
name|StringValue
argument_list|(
operator|new
name|String
argument_list|(
name|enc
operator|.
name|getCharArray
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
operator|new
name|StringValue
argument_list|(
operator|new
name|String
argument_list|(
name|enc
operator|.
name|getCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Base64Decoder
name|dec
init|=
operator|new
name|Base64Decoder
argument_list|()
decl_stmt|;
name|dec
operator|.
name|translate
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|value
operator|=
operator|new
name|StringValue
argument_list|(
operator|new
name|String
argument_list|(
name|dec
operator|.
name|getByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|value
operator|)
return|;
block|}
block|}
end_class

end_unit

