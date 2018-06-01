begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|util
operator|.
name|zip
operator|.
name|ZipEntry
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
name|ZipInputStream
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
name|ValueSequence
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

begin_comment
comment|/**  * Extracts files and folders from a Zip file  *  * @author Adam Retter<adam@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|UnZipFunction
extends|extends
name|AbstractExtractFunction
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
literal|"unzip"
argument_list|,
name|CompressionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|CompressionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"UnZip all the resources/folders from the provided data by calling user defined functions "
operator|+
literal|"to determine what and how to store the resources/folders"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"zip-data"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The zip file data"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-filter"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A user defined function for filtering resources from the zip file. The function takes 3 parameters e.g. "
operator|+
literal|"user:unzip-entry-filter($path as xs:string, $data-type as xs:string, $param as item()*) as xs:boolean. "
operator|+
literal|"$data-type may be 'resource' or 'folder'. $param is a sequence with any additional parameters, "
operator|+
literal|"for example a list of extracted files. If the return type is true() it indicates the entry "
operator|+
literal|"should be processed and passed to the entry-data function, else the resource is skipped. "
operator|+
literal|"If you wish to extract all resources you can use the provided compression:no-filter#3 function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-filter-param"
argument_list|,
name|Type
operator|.
name|ANY_TYPE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A sequence with an additional parameters for filtering function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-data"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A user defined function for storing an extracted resource from the zip file. The function takes 4 parameters e.g. "
operator|+
literal|"user:unzip-entry-data($path as xs:string, $data-type as xs:string, $data as item()?, $param as item()*). "
operator|+
literal|"Or a user defined function which returns path for storing an extracted resource from the tar file. The function takes 3 parameters e.g. "
operator|+
literal|"user:entry-path($path as xs:string, $data-type as xs:string, $param as item()*) as xs:anyURI. "
operator|+
literal|"$data-type may be 'resource' or 'folder'. $param is a sequence with any additional parameters. "
operator|+
literal|"Functions for storing the entries to a folder on the filesystem or a collection in the database "
operator|+
literal|"provided by compression:fs-store-entry4($dest) and compression:db-store-entry4($dest)."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-data-param"
argument_list|,
name|Type
operator|.
name|ANY_TYPE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A sequence with an additional parameters for storing function."
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"unzip"
argument_list|,
name|CompressionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|CompressionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"UnZip all the resources/folders from the provided data by calling user defined functions "
operator|+
literal|"to determine what and how to store the resources/folders"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"zip-data"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The zip file data"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-filter"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A user defined function for filtering resources from the zip file. The function takes 3 parameters e.g. "
operator|+
literal|"user:unzip-entry-filter($path as xs:string, $data-type as xs:string, $param as item()*) as xs:boolean. "
operator|+
literal|"$data-type may be 'resource' or 'folder'. $param is a sequence with any additional parameters, "
operator|+
literal|"for example a list of extracted files. If the return type is true() it indicates the entry "
operator|+
literal|"should be processed and passed to the entry-data function, else the resource is skipped. "
operator|+
literal|"If you wish to extract all resources you can use the provided compression:no-filter#3 function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-filter-param"
argument_list|,
name|Type
operator|.
name|ANY_TYPE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A sequence with an additional parameters for filtering function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-data"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A user defined function for storing an extracted resource from the zip file. The function takes 4 parameters e.g. "
operator|+
literal|"user:unzip-entry-data($path as xs:string, $data-type as xs:string, $data as item()?, $param as item()*). "
operator|+
literal|"Or a user defined function which returns path for storing an extracted resource from the tar file. The function takes 3 parameters e.g. "
operator|+
literal|"user:entry-path($path as xs:string, $data-type as xs:string, $param as item()*) as xs:anyURI. "
operator|+
literal|"$data-type may be 'resource' or 'folder'. $param is a sequence with any additional parameters."
operator|+
literal|"Functions for storing the entries to a folder on the filesystem or a collection in the database "
operator|+
literal|"provided by compression:fs-store-entry4($dest) and compression:db-store-entry4($dest)."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"entry-data-param"
argument_list|,
name|Type
operator|.
name|ANY_TYPE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A sequence with an additional parameters for storing function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoding"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The encoding to be used during uncompressing eg: UTF8 or Cp437 from https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html"
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|UnZipFunction
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
specifier|protected
name|Sequence
name|processCompressedData
parameter_list|(
specifier|final
name|BinaryValue
name|compressedData
parameter_list|,
specifier|final
name|Charset
name|encoding
parameter_list|)
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
try|try
init|(
specifier|final
name|ZipInputStream
name|zis
init|=
operator|new
name|ZipInputStream
argument_list|(
name|compressedData
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|encoding
argument_list|)
init|)
block|{
name|ZipEntry
name|entry
init|=
literal|null
decl_stmt|;
specifier|final
name|Sequence
name|results
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|zis
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Sequence
name|processCompressedEntryResults
init|=
name|processCompressedEntry
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|zis
argument_list|,
name|filterParam
argument_list|,
name|storeParam
argument_list|)
decl_stmt|;
name|results
operator|.
name|addAll
argument_list|(
name|processCompressedEntryResults
argument_list|)
expr_stmt|;
name|zis
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
specifier|final
name|StackTraceElement
name|trace
index|[]
init|=
name|e
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|trace
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
if|if
condition|(
name|trace
index|[
literal|0
index|]
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"java.lang.StringCoding"
argument_list|)
operator|&&
name|trace
index|[
literal|0
index|]
operator|.
name|getMethodName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"throwMalformed"
argument_list|)
condition|)
block|{
comment|// handle errors from invalid encoding in the zip file (JDK 10)
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
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

