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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|TarArchiveOutputStream
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
comment|/**  * Compresses a sequence of resources and/or collections into a Tar file  *   * @author Adam Retter<adam@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|TarFunction
extends|extends
name|AbstractCompressFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|TAR_FUNCTION_NAME
init|=
operator|new
name|QName
argument_list|(
literal|"tar"
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
specifier|private
specifier|final
specifier|static
name|String
name|TAR_FUNCTION_DESCRIPTION
init|=
literal|"Tars nodes, resources and collections."
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
name|TAR_FUNCTION_NAME
argument_list|,
name|TAR_FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SOURCES_PARAM
block|,
name|COLLECTION_HIERARCHY_PARAM
block|,             }
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|TAR_FUNCTION_NAME
argument_list|,
name|TAR_FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SOURCES_PARAM
block|,
name|COLLECTION_HIERARCHY_PARAM
block|,
name|STRIP_PREFIX_PARAM
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|TAR_FUNCTION_NAME
argument_list|,
name|TAR_FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SOURCES_PARAM
block|,
name|COLLECTION_HIERARCHY_PARAM
block|,
name|STRIP_PREFIX_PARAM
block|,
name|ENCODING_PARAM
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
name|ZERO_OR_MORE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|TarFunction
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
name|void
name|closeEntry
parameter_list|(
name|Object
name|os
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|TarArchiveOutputStream
operator|)
name|os
operator|)
operator|.
name|closeArchiveEntry
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|newEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|TarArchiveEntry
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|putEntry
parameter_list|(
name|Object
name|os
parameter_list|,
name|Object
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|TarArchiveOutputStream
operator|)
name|os
operator|)
operator|.
name|putArchiveEntry
argument_list|(
operator|(
name|TarArchiveEntry
operator|)
name|entry
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|OutputStream
name|stream
parameter_list|(
specifier|final
name|FastByteArrayOutputStream
name|baos
parameter_list|,
specifier|final
name|Charset
name|encoding
parameter_list|)
block|{
return|return
operator|new
name|TarArchiveOutputStream
argument_list|(
name|baos
argument_list|,
name|encoding
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

