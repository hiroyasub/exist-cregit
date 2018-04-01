begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|UnsupportedCharsetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|FileUtils
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
name|MimeTable
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
name|MimeType
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
name|FastByteArrayInputStream
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
name|xmldb
operator|.
name|EXistResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|LocalCollection
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
name|functions
operator|.
name|xmldb
operator|.
name|XMLDBAbstractCollectionManipulator
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
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
name|FunctionReference
name|entryFilterFunction
init|=
literal|null
decl_stmt|;
specifier|protected
name|Sequence
name|filterParam
init|=
literal|null
decl_stmt|;
specifier|private
name|FunctionReference
name|entryDataFunction
init|=
literal|null
decl_stmt|;
specifier|protected
name|Sequence
name|storeParam
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
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
name|entryFilterFunction
operator|=
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
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"entry-filter function must take at least 3 arguments."
argument_list|)
throw|;
name|filterParam
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
comment|//get the entry-data function and check its types
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
name|entryDataFunction
operator|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
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
name|storeParam
operator|=
name|args
index|[
literal|4
index|]
expr_stmt|;
try|try
block|{
specifier|final
name|Charset
name|encoding
decl_stmt|;
if|if
condition|(
operator|(
name|args
operator|.
name|length
operator|>=
literal|6
operator|)
operator|&&
operator|!
name|args
index|[
literal|5
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|encoding
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|args
index|[
literal|5
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|encoding
operator|=
name|StandardCharsets
operator|.
name|UTF_8
expr_stmt|;
block|}
name|BinaryValue
name|compressedData
init|=
operator|(
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
operator|)
decl_stmt|;
return|return
name|processCompressedData
argument_list|(
name|compressedData
argument_list|,
name|encoding
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|UnsupportedCharsetException
decl||
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Processes a compressed archive      *      * @param compressedData the compressed data to extract      * @return Sequence of results      */
specifier|protected
specifier|abstract
name|Sequence
name|processCompressedData
parameter_list|(
name|BinaryValue
name|compressedData
parameter_list|,
name|Charset
name|encoding
parameter_list|)
throws|throws
name|XPathException
throws|,
name|XMLDBException
function_decl|;
comment|/**      * Processes a compressed entry from an archive      *      * @param name The name of the entry      * @param isDirectory true if the entry is a directory, false otherwise      * @param is an InputStream for reading the uncompressed data of the entry      * @param filterParam is an additional param for entry filtering function        * @param storeParam is an additional param for entry storing function      * @throws XMLDBException       */
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
parameter_list|,
name|Sequence
name|filterParam
parameter_list|,
name|Sequence
name|storeParam
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|XMLDBException
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
literal|3
index|]
decl_stmt|;
name|filterParams
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
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
name|filterParams
index|[
literal|2
index|]
operator|=
name|filterParam
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
name|entryDataFunctionResult
decl_stmt|;
name|Sequence
name|uncompressedData
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
if|if
condition|(
name|entryDataFunction
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
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
name|storeParam
expr_stmt|;
name|entryDataFunctionResult
operator|=
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
expr_stmt|;
name|String
name|path
init|=
name|entryDataFunctionResult
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Collection
name|root
init|=
operator|new
name|LocalCollection
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
operator|new
name|AnyURIValue
argument_list|(
literal|"/db"
argument_list|)
operator|.
name|toXmldbURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isDirectory
condition|)
block|{
name|XMLDBAbstractCollectionManipulator
operator|.
name|createCollection
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Resource
name|resource
decl_stmt|;
name|Path
name|file
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|.
name|normalize
argument_list|()
decl_stmt|;
name|name
operator|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|path
operator|=
name|file
operator|.
name|getParent
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Collection
name|target
init|=
operator|(
name|path
operator|==
literal|null
operator|)
condition|?
name|root
else|:
name|XMLDBAbstractCollectionManipulator
operator|.
name|createCollection
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|//copy the input data
specifier|final
name|byte
index|[]
name|entryData
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|entryData
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|InputStream
name|bis
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|entryData
argument_list|)
init|)
block|{
name|NodeValue
name|content
init|=
name|ModuleUtils
operator|.
name|streamToXML
argument_list|(
name|context
argument_list|,
name|bis
argument_list|)
decl_stmt|;
name|resource
operator|=
name|target
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|ContentHandler
name|handler
init|=
operator|(
operator|(
name|XMLResource
operator|)
name|resource
operator|)
operator|.
name|setContentAsSAX
argument_list|()
decl_stmt|;
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|content
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|resource
operator|=
name|target
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
literal|"BinaryResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|entryData
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|setMimeType
argument_list|(
name|mime
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|target
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|//copy the input data
specifier|final
name|byte
index|[]
name|entryData
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|entryData
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
comment|//try and parse as xml, fall back to binary
try|try
init|(
specifier|final
name|InputStream
name|bis
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|entryData
argument_list|)
init|)
block|{
name|uncompressedData
operator|=
name|ModuleUtils
operator|.
name|streamToXML
argument_list|(
name|context
argument_list|,
name|bis
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
block|{
try|try
init|(
specifier|final
name|InputStream
name|bis
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|entryData
argument_list|)
init|)
block|{
name|uncompressedData
operator|=
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
name|bis
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//call the entry-data function
name|Sequence
name|dataParams
index|[]
init|=
operator|new
name|Sequence
index|[
literal|4
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
name|dataParams
index|[
literal|3
index|]
operator|=
name|storeParam
expr_stmt|;
name|entryDataFunctionResult
operator|=
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
expr_stmt|;
block|}
return|return
name|entryDataFunctionResult
return|;
block|}
block|}
block|}
end_class

end_unit

