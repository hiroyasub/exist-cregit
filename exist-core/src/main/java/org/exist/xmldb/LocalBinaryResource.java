begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|CountingOutputStream
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
name|NullOutputStream
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
name|persistent
operator|.
name|BinaryDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|blob
operator|.
name|BlobId
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
name|EXistInputSource
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
name|crypto
operator|.
name|digest
operator|.
name|DigestType
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
name|crypto
operator|.
name|digest
operator|.
name|MessageDigest
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
name|w3c
operator|.
name|dom
operator|.
name|DocumentType
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
name|InputSource
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
name|ext
operator|.
name|LexicalHandler
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
name|ErrorCodes
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
name|BinaryResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|io
operator|.
name|FileOutputStream
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
name|Files
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
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|SupplierE
import|;
end_import

begin_class
specifier|public
class|class
name|LocalBinaryResource
extends|extends
name|AbstractEXistResource
implements|implements
name|ExtendedResource
implements|,
name|EXistBinaryResource
implements|,
name|EXistResource
block|{
specifier|protected
name|InputSource
name|inputSource
init|=
literal|null
decl_stmt|;
specifier|protected
name|Path
name|file
init|=
literal|null
decl_stmt|;
specifier|protected
name|byte
index|[]
name|rawData
init|=
literal|null
decl_stmt|;
specifier|private
name|BinaryValue
name|binaryValue
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isExternal
init|=
literal|false
decl_stmt|;
specifier|public
name|LocalBinaryResource
parameter_list|(
specifier|final
name|Subject
name|user
parameter_list|,
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|LocalCollection
name|collection
parameter_list|,
specifier|final
name|XmldbURI
name|docId
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
name|docId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getResourceType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|BinaryResource
operator|.
name|RESOURCE_TYPE
return|;
block|}
annotation|@
name|Override
specifier|public
name|BlobId
name|getBlobId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|read
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
operator|(
operator|(
name|BinaryDocument
operator|)
name|document
operator|)
operator|.
name|getBlobId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MessageDigest
name|getContentDigest
parameter_list|(
specifier|final
name|DigestType
name|digestType
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|read
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|broker
operator|.
name|getBinaryResourceContentDigest
argument_list|(
name|transaction
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|document
argument_list|,
name|digestType
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getExtendedContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getExtendedContent
argument_list|(
parameter_list|()
lambda|->
name|read
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|broker
operator|.
name|getBinaryResource
argument_list|(
operator|(
operator|(
name|BinaryDocument
operator|)
name|document
operator|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Similar to {@link org.exist.xmldb.ExtendedResource#getExtendedContent()}      * but useful for operations within the XML:DB Local API      * that are already working within a transaction      */
name|Object
name|getExtendedContent
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|getExtendedContent
argument_list|(
parameter_list|()
lambda|->
name|read
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|)
operator|.
name|apply
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker1
parameter_list|,
name|transaction1
parameter_list|)
lambda|->
name|broker1
operator|.
name|getBinaryResource
argument_list|(
operator|(
operator|(
name|BinaryDocument
operator|)
name|document
operator|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Object
name|getExtendedContent
parameter_list|(
specifier|final
name|SupplierE
argument_list|<
name|Object
argument_list|,
name|XMLDBException
argument_list|>
name|binaryResourceRead
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
return|return
name|file
return|;
block|}
if|if
condition|(
name|inputSource
operator|!=
literal|null
condition|)
block|{
return|return
name|inputSource
return|;
block|}
if|if
condition|(
name|rawData
operator|!=
literal|null
condition|)
block|{
return|return
name|rawData
return|;
block|}
if|if
condition|(
name|binaryValue
operator|!=
literal|null
condition|)
block|{
return|return
name|binaryValue
return|;
block|}
return|return
name|read
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|transaction
argument_list|,
operator|(
operator|(
name|BinaryDocument
operator|)
name|document
operator|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Object
name|res
init|=
name|getExtendedContent
argument_list|()
decl_stmt|;
return|return
name|getContent
argument_list|(
name|res
argument_list|)
return|;
block|}
comment|/**      * Similar to {@link org.exist.xmldb.LocalBinaryResource#getContent()}      * but useful for operations within the XML:DB Local API      * that are already working within a transaction      */
name|Object
name|getContent
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Object
name|res
init|=
name|getExtendedContent
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|)
decl_stmt|;
return|return
name|getContent
argument_list|(
name|res
argument_list|)
return|;
block|}
specifier|private
name|Object
name|getContent
parameter_list|(
specifier|final
name|Object
name|res
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|res
operator|instanceof
name|Path
condition|)
block|{
return|return
name|readFile
argument_list|(
operator|(
name|Path
operator|)
name|res
argument_list|)
return|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|java
operator|.
name|io
operator|.
name|File
condition|)
block|{
return|return
name|readFile
argument_list|(
operator|(
operator|(
name|java
operator|.
name|io
operator|.
name|File
operator|)
name|res
operator|)
operator|.
name|toPath
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|InputSource
condition|)
block|{
return|return
name|readFile
argument_list|(
operator|(
name|InputSource
operator|)
name|res
argument_list|)
return|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|byte
index|[]
condition|)
block|{
return|return
name|res
return|;
block|}
if|else if
condition|(
name|res
operator|instanceof
name|BinaryValue
condition|)
block|{
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
operator|(
operator|(
name|BinaryValue
operator|)
name|res
operator|)
operator|.
name|streamBinaryTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
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
if|else if
condition|(
name|res
operator|instanceof
name|InputStream
condition|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|(
name|InputStream
operator|)
name|res
init|)
block|{
return|return
name|readFile
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
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
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContent
parameter_list|(
specifier|final
name|Object
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|value
operator|instanceof
name|Path
condition|)
block|{
name|file
operator|=
operator|(
name|Path
operator|)
name|value
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|java
operator|.
name|io
operator|.
name|File
condition|)
block|{
name|file
operator|=
operator|(
operator|(
name|java
operator|.
name|io
operator|.
name|File
operator|)
name|value
operator|)
operator|.
name|toPath
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|InputSource
condition|)
block|{
name|inputSource
operator|=
operator|(
name|InputSource
operator|)
name|value
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|rawData
operator|=
operator|(
name|byte
index|[]
operator|)
name|value
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|rawData
operator|=
operator|(
operator|(
name|String
operator|)
name|value
operator|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|BinaryValue
condition|)
block|{
name|binaryValue
operator|=
operator|(
name|BinaryValue
operator|)
name|value
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"don't know how to handle value of type "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|isExternal
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getStreamContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getStreamContent
argument_list|(
parameter_list|()
lambda|->
name|read
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
name|broker
operator|.
name|getBinaryResource
argument_list|(
operator|(
operator|(
name|BinaryDocument
operator|)
name|document
operator|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Similar to {@link org.exist.xmldb.LocalBinaryResource#getStreamContent()}      * but useful for operations within the XML:DB Local API      * that are already working within a transaction      */
name|InputStream
name|getStreamContent
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|getStreamContent
argument_list|(
parameter_list|()
lambda|->
name|this
operator|.
block_content|<InputStream>read(broker
argument_list|,
name|transaction
argument_list|)
operator|.
name|apply
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker1
parameter_list|,
name|transaction1
parameter_list|)
lambda|->
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|transaction
argument_list|,
operator|(
operator|(
name|BinaryDocument
operator|)
name|document
operator|)
argument_list|)
argument_list|)
block_content|)
function|;
block|}
end_class

begin_function
specifier|private
name|InputStream
name|getStreamContent
parameter_list|(
specifier|final
name|SupplierE
argument_list|<
name|InputStream
argument_list|,
name|XMLDBException
argument_list|>
name|streamContentRead
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|InputStream
name|is
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
comment|// Cannot fire it :-(
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
if|else if
condition|(
name|inputSource
operator|!=
literal|null
condition|)
block|{
name|is
operator|=
name|inputSource
operator|.
name|getByteStream
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|rawData
operator|!=
literal|null
condition|)
block|{
name|is
operator|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|rawData
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|binaryValue
operator|!=
literal|null
condition|)
block|{
name|is
operator|=
name|binaryValue
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
name|streamContentRead
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|getContentIntoAFile
parameter_list|(
specifier|final
name|Path
name|tmpFile
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
init|(
specifier|final
name|OutputStream
name|bos
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|tmpFile
argument_list|)
init|)
block|{
name|getContentIntoAStream
argument_list|(
name|bos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while loading binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|getContentIntoAStream
parameter_list|(
specifier|final
name|OutputStream
name|os
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|read
argument_list|(
parameter_list|(
name|document
parameter_list|,
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|os
operator|instanceof
name|FileOutputStream
condition|)
block|{
try|try
init|(
specifier|final
name|OutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|os
argument_list|,
literal|65536
argument_list|)
init|)
block|{
name|broker
operator|.
name|readBinaryResource
argument_list|(
name|transaction
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|document
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|broker
operator|.
name|readBinaryResource
argument_list|(
name|transaction
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|document
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|long
name|getStreamLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|long
name|retval
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|FileUtils
operator|.
name|sizeQuietly
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|inputSource
operator|!=
literal|null
operator|&&
name|inputSource
operator|instanceof
name|EXistInputSource
condition|)
block|{
name|retval
operator|=
operator|(
operator|(
name|EXistInputSource
operator|)
name|inputSource
operator|)
operator|.
name|getByteStreamLength
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|rawData
operator|!=
literal|null
condition|)
block|{
name|retval
operator|=
name|rawData
operator|.
name|length
expr_stmt|;
block|}
if|else if
condition|(
name|binaryValue
operator|!=
literal|null
condition|)
block|{
try|try
init|(
specifier|final
name|CountingOutputStream
name|os
init|=
operator|new
name|CountingOutputStream
argument_list|(
operator|new
name|NullOutputStream
argument_list|()
argument_list|)
init|)
block|{
name|binaryValue
operator|.
name|streamBinaryTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|retval
operator|=
name|os
operator|.
name|getByteCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while obtaining length of binary value "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|retval
operator|=
name|getContentLength
argument_list|()
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
end_function

begin_function
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
return|return
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"file "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|" could not be found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
specifier|final
name|InputSource
name|inSrc
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|inSrc
operator|.
name|getByteStream
argument_list|()
init|)
block|{
return|return
name|readFile
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"Could not read InputSource"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|bos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"IO exception while reading file "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|DocumentType
name|getDocType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setDocType
parameter_list|(
specifier|final
name|DocumentType
name|doctype
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
specifier|final
name|LexicalHandler
name|handler
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|void
name|setProperties
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|)
block|{
block|}
end_function

begin_function
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
operator|!
name|isExternal
operator|&&
name|file
operator|!=
literal|null
condition|)
block|{
name|file
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|binaryValue
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|binaryValue
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"error while closing binary resource "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_function

unit|}
end_unit

