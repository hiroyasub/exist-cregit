begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: EmbeddedOutputStream.java 223 2007-04-21 22:13:05Z dizzzz $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|embedded
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
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
name|Either
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
name|RunnableE
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
name|lazy
operator|.
name|LazyValE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
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
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
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
name|PermissionDeniedException
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
name|lock
operator|.
name|Lock
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
name|TransactionManager
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
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|*
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
name|CloseNotifyingOutputStream
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
name|TemporaryFileManager
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
name|XmldbURI
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
name|SAXException
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
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
operator|.
name|Left
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
operator|.
name|Right
import|;
end_import

begin_comment
comment|/**  * Write document to local database (embedded) using output stream.  *  * @author Adam Retter<adam@exist-db.org>  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedOutputStream
extends|extends
name|OutputStream
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|EmbeddedOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|XmldbURL
name|url
decl_stmt|;
specifier|private
specifier|final
name|LazyValE
argument_list|<
name|OutputStream
argument_list|,
name|IOException
argument_list|>
name|underlyingStream
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
comment|/**      * @param url Location of document in database.      *      * @throws IOException if there is a problem accessing the database instance.      */
specifier|public
name|EmbeddedOutputStream
parameter_list|(
specifier|final
name|XmldbURL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param brokerPool the database instance.      * @param url Location of document in database.      *      * @throws IOException if there is a problem accessing the database instance.      */
specifier|public
name|EmbeddedOutputStream
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|XmldbURL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|brokerPool
operator|==
literal|null
condition|?
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|url
operator|.
name|getInstanceName
argument_list|()
argument_list|)
else|:
name|brokerPool
decl_stmt|;
name|this
operator|.
name|underlyingStream
operator|=
operator|new
name|LazyValE
argument_list|<>
argument_list|(
parameter_list|()
lambda|->
name|openStream
argument_list|(
name|pool
argument_list|,
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Either
argument_list|<
name|IOException
argument_list|,
name|OutputStream
argument_list|>
name|openStream
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|XmldbURL
name|url
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Begin document download"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// get a temporary file
specifier|final
name|TemporaryFileManager
name|tempFileManager
init|=
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|tempFile
init|=
name|tempFileManager
operator|.
name|getTemporaryFile
argument_list|()
decl_stmt|;
specifier|final
name|OutputStream
name|osTemp
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|tempFile
argument_list|,
name|StandardOpenOption
operator|.
name|TRUNCATE_EXISTING
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
decl_stmt|;
comment|// upload the content of the temp file to the db when it is closed, then return the temp file
specifier|final
name|RunnableE
argument_list|<
name|IOException
argument_list|>
name|uploadOnClose
init|=
parameter_list|()
lambda|->
block|{
name|uploadToDb
argument_list|(
name|pool
argument_list|,
name|url
argument_list|,
name|tempFile
argument_list|)
expr_stmt|;
name|tempFileManager
operator|.
name|returnTemporaryFile
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
return|return
name|Right
argument_list|(
operator|new
name|CloseNotifyingOutputStream
argument_list|(
name|osTemp
argument_list|,
name|uploadOnClose
argument_list|)
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
return|return
name|Left
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|uploadToDb
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|XmldbURL
name|url
parameter_list|,
specifier|final
name|Path
name|tempFile
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|XmldbURI
name|collectionUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|url
operator|.
name|getCollection
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|documentUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|url
operator|.
name|getDocumentName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collectionUri
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|collectionUri
operator|.
name|toString
argument_list|()
operator|+
literal|" is not a collection."
argument_list|)
throw|;
block|}
if|if
condition|(
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|broker
argument_list|,
name|documentUri
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|documentUri
operator|.
name|toString
argument_list|()
operator|+
literal|" is a collection."
argument_list|)
throw|;
block|}
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
name|documentUri
argument_list|)
decl_stmt|;
name|String
name|contentType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
name|contentType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|mime
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
if|if
condition|(
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing XML resource"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|InputSource
name|inputsource
init|=
operator|new
name|FileInputSource
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|documentUri
argument_list|,
name|inputsource
argument_list|)
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|info
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|inputsource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing Binary resource"
argument_list|)
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|tempFile
argument_list|)
init|)
block|{
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|documentUri
argument_list|,
name|is
argument_list|,
name|contentType
argument_list|,
name|FileUtils
operator|.
name|sizeQuietly
argument_list|(
name|tempFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
decl||
name|PermissionDeniedException
decl||
name|LockException
decl||
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"End document upload"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The underlying stream is closed"
argument_list|)
throw|;
block|}
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The underlying stream is closed"
argument_list|)
throw|;
block|}
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The underlying stream is closed"
argument_list|)
throw|;
block|}
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
try|try
block|{
if|if
condition|(
name|underlyingStream
operator|.
name|isInitialized
argument_list|()
condition|)
block|{
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

