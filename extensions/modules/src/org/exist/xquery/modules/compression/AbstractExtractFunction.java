begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|FunctionCall
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
name|modules
operator|.
name|ModuleUtils
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
name|AnyURIValue
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
name|Base64Binary
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
name|FunctionReference
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractExtractFunction
extends|extends
name|BasicFunction
block|{
specifier|private
name|FunctionCall
name|entryFilterFunction
init|=
literal|null
decl_stmt|;
specifier|private
name|FunctionCall
name|entryDataFunction
init|=
literal|null
decl_stmt|;
specifier|private
name|Sequence
name|contextSequence
decl_stmt|;
specifier|public
name|AbstractExtractFunction
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
name|this
operator|.
name|contextSequence
operator|=
name|contextSequence
expr_stmt|;
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
comment|//get the entry-filter function and check its types
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|1
index|]
operator|instanceof
name|FunctionReference
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"No entry-filter function provided."
argument_list|)
throw|;
name|FunctionReference
name|entryFilterFunctionRef
init|=
operator|(
name|FunctionReference
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
decl_stmt|;
name|entryFilterFunction
operator|=
name|entryFilterFunctionRef
operator|.
name|getFunctionCall
argument_list|()
expr_stmt|;
name|FunctionSignature
name|entryFilterFunctionSig
init|=
name|entryFilterFunction
operator|.
name|getSignature
argument_list|()
decl_stmt|;
if|if
condition|(
name|entryFilterFunctionSig
operator|.
name|getArgumentCount
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"entry-filter function must take at least 2 arguments."
argument_list|)
throw|;
name|SequenceType
index|[]
name|argTypes
init|=
name|entryFilterFunctionSig
operator|.
name|getArgumentTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|argTypes
index|[
literal|0
index|]
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|EXACTLY_ONE
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|argTypes
index|[
literal|0
index|]
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|||
name|argTypes
index|[
literal|1
index|]
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|EXACTLY_ONE
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|argTypes
index|[
literal|1
index|]
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|entryFilterFunctionSig
operator|.
name|getReturnType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"entry-filter function does not match the expected function signature."
argument_list|)
throw|;
comment|//get the entry-data function and check its types
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|2
index|]
operator|instanceof
name|FunctionReference
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"No entry-data function provided."
argument_list|)
throw|;
name|FunctionReference
name|entryDataFunctionRef
init|=
operator|(
name|FunctionReference
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
name|entryDataFunction
operator|=
name|entryDataFunctionRef
operator|.
name|getFunctionCall
argument_list|()
expr_stmt|;
name|FunctionSignature
name|entryDataFunctionSig
init|=
name|entryDataFunction
operator|.
name|getSignature
argument_list|()
decl_stmt|;
if|if
condition|(
name|entryDataFunctionSig
operator|.
name|getArgumentCount
argument_list|()
operator|<
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"entry-data function must take at least 3 arguments"
argument_list|)
throw|;
name|argTypes
operator|=
name|entryDataFunctionSig
operator|.
name|getArgumentTypes
argument_list|()
expr_stmt|;
if|if
condition|(
name|argTypes
index|[
literal|0
index|]
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|EXACTLY_ONE
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|argTypes
index|[
literal|0
index|]
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|||
name|argTypes
index|[
literal|1
index|]
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|EXACTLY_ONE
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|argTypes
index|[
literal|1
index|]
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|||
name|argTypes
index|[
literal|2
index|]
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|ZERO_OR_ONE
operator|||
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|argTypes
index|[
literal|2
index|]
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"entry-data function does not match the expected function signature."
argument_list|)
throw|;
name|Base64Binary
name|compressedData
init|=
operator|(
operator|(
name|Base64Binary
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
operator|)
decl_stmt|;
return|return
name|processCompressedData
argument_list|(
name|compressedData
argument_list|)
return|;
block|}
comment|/**      * Processes a compressed archive      *      * @param compressedData the compressed data to extract      * @return Sequence of results      */
specifier|protected
specifier|abstract
name|Sequence
name|processCompressedData
parameter_list|(
name|Base64Binary
name|compressedData
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Processes a compressed entry from an archive      *      * @param name The name of the entry      * @param isDirectory true if the entry is a directory, false otherwise      * @param is An InputStream for reading the uncompressed data of the entry      */
specifier|protected
name|Sequence
name|processCompressedEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isDirectory
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
name|String
name|dataType
init|=
name|isDirectory
condition|?
literal|"folder"
else|:
literal|"resource"
decl_stmt|;
comment|//call the entry-filter function
name|Sequence
name|filterParams
index|[]
init|=
operator|new
name|Sequence
index|[
literal|2
index|]
decl_stmt|;
name|filterParams
index|[
literal|0
index|]
operator|=
operator|new
name|AnyURIValue
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|filterParams
index|[
literal|1
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|dataType
argument_list|)
expr_stmt|;
name|Sequence
name|entryFilterFunctionResult
init|=
name|entryFilterFunction
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
name|filterParams
argument_list|)
decl_stmt|;
if|if
condition|(
name|BooleanValue
operator|.
name|FALSE
operator|==
name|entryFilterFunctionResult
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
name|Sequence
name|uncompressedData
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
comment|//copy the input data
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|entryData
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
comment|//try and parse as xml, fall back to binary
try|try
block|{
name|uncompressedData
operator|=
name|ModuleUtils
operator|.
name|streamToXML
argument_list|(
name|context
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|entryData
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
if|if
condition|(
name|entryData
operator|.
name|length
operator|>
literal|0
condition|)
name|uncompressedData
operator|=
operator|new
name|Base64Binary
argument_list|(
name|entryData
argument_list|)
expr_stmt|;
block|}
comment|//call the entry-data function
name|Sequence
name|dataParams
index|[]
init|=
operator|new
name|Sequence
index|[
literal|3
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|filterParams
argument_list|,
literal|0
argument_list|,
name|dataParams
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|dataParams
index|[
literal|2
index|]
operator|=
name|uncompressedData
expr_stmt|;
name|Sequence
name|entryDataFunctionResult
init|=
name|entryDataFunction
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
name|dataParams
argument_list|)
decl_stmt|;
return|return
name|entryDataFunctionResult
return|;
block|}
block|}
block|}
end_class

end_unit

