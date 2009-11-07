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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveEntry
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
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveInputStream
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

begin_comment
comment|/**  * Extracts files and folders from a Tar file  *   * @author Adam Retter<adam@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|UnTarFunction
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
literal|"untar"
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
literal|"UnTar all the resources/folders from the provided data by calling user defined functions "
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
literal|"tar-data"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The tar file data"
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
literal|"A user defined function for filtering resources from the tar file. The function takes 3 parameters e.g. user:untar-entry-filter($path as xs:string, $data-type as xs:string, $param as item()*) as xs:boolean. $type may be 'resource' or 'folder'. $param is a sequence with any additional parameters, for example a list of extracted files.If the return type is true() it indicates the entry should be processed and passed to the entry-data function, else the resource is skipped."
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
literal|"A user defined function for storing an extracted resource from the tar file. The function takes 4 parameters e.g. user:untar-entry-data($path as xs:string, $data-type as xs:string, $data as item()?, $param as item()*). $type may be 'resource' or 'folder'. $param is a sequence with any additional parameters"
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
block|}
decl_stmt|;
specifier|public
name|UnTarFunction
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
name|Base64Binary
name|compressedData
parameter_list|)
throws|throws
name|XPathException
block|{
name|TarArchiveInputStream
name|tis
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tis
operator|=
operator|new
name|TarArchiveInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|compressedData
operator|.
name|getBinaryData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TarArchiveEntry
name|entry
init|=
literal|null
decl_stmt|;
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
name|tis
operator|.
name|getNextTarEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
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
name|tis
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
block|}
return|return
name|results
return|;
block|}
catch|catch
parameter_list|(
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
finally|finally
block|{
if|if
condition|(
name|tis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

