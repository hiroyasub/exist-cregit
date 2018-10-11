begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2018 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|compression
package|;
end_package

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
name|util
operator|.
name|zip
operator|.
name|Deflater
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DeflaterOutputStream
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
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
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
name|BinaryValue
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
name|BooleanValue
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
comment|/**  * Deflate compression  *   * @author Olaf Schreck<olaf@existsolutions.com>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|DeflateFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|DEFLATE_FUNCTION_NAME
init|=
operator|new
name|QName
argument_list|(
literal|"deflate"
argument_list|,
name|CompressionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|CompressionModule
operator|.
name|PREFIX
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
name|DEFLATE_FUNCTION_NAME
argument_list|,
literal|"Deflate data (RFC 1950)"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"data"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The data to Deflate"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|DEFLATE_FUNCTION_NAME
argument_list|,
literal|"Deflate data (RFC 1951)"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"data"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The data to Deflate"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"raw"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"If true, create raw deflate data that is not wrapped inside zlib header and checksum."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|DeflateFunction
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
comment|// is there some data to Deflate?
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
name|BinaryValue
name|bin
init|=
operator|(
name|BinaryValue
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
name|boolean
name|rawflag
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
operator|&&
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
name|rawflag
operator|=
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
name|convertTo
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
name|Deflater
name|defl
init|=
operator|new
name|Deflater
argument_list|(
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Deflater
operator|.
name|DEFAULT_COMPRESSION
argument_list|,
name|rawflag
argument_list|)
decl_stmt|;
comment|// deflate the data
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|;
name|DeflaterOutputStream
name|dos
init|=
operator|new
name|DeflaterOutputStream
argument_list|(
name|baos
argument_list|,
name|defl
argument_list|)
init|)
block|{
name|bin
operator|.
name|streamBinaryTo
argument_list|(
name|dos
argument_list|)
expr_stmt|;
name|dos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|dos
operator|.
name|finish
argument_list|()
expr_stmt|;
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
name|baos
operator|.
name|toFastByteInputStream
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
