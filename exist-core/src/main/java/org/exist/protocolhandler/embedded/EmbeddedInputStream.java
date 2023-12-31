begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: EmbeddedInputStream.java 223 2007-04-21 22:13:05Z dizzzz $  */
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
name|lazy
operator|.
name|LazyValE
import|;
end_import

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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
name|dom
operator|.
name|persistent
operator|.
name|LockedDocument
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializers
operator|.
name|Serializer
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
name|CloseNotifyingInputStream
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

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Read document from embedded database as an InputStream.  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  * @author Dannes Wessels  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|EmbeddedInputStream
extends|extends
name|InputStream
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
name|EmbeddedInputStream
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
name|InputStream
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
name|EmbeddedInputStream
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
name|EmbeddedInputStream
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
name|InputStream
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
specifier|final
name|XmldbURI
name|path
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
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
try|try
init|(
specifier|final
name|LockedDocument
name|lockedResource
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|lockedResource
operator|==
literal|null
condition|)
block|{
comment|// Test for collection
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
name|path
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
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
comment|// No collection, no document
return|return
name|Left
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|url
operator|.
name|getPath
argument_list|()
operator|+
literal|" not found."
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// Collection
return|return
name|Left
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|url
operator|.
name|getPath
argument_list|()
operator|+
literal|" is a collection."
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
else|else
block|{
specifier|final
name|DocumentImpl
name|resource
init|=
name|lockedResource
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
block|{
specifier|final
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Preserve doctype
name|serializer
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|OUTPUT_DOCTYPE
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
comment|// serialize the XML to a temporary file
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
try|try
init|(
specifier|final
name|Writer
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|tempFile
argument_list|,
name|UTF_8
argument_list|,
name|StandardOpenOption
operator|.
name|TRUNCATE_EXISTING
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
init|)
block|{
name|serializer
operator|.
name|serialize
argument_list|(
name|resource
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: the temp file will be returned to the manager when the InputStream is closed
return|return
name|Right
argument_list|(
operator|new
name|CloseNotifyingInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|tempFile
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|tempFileManager
operator|.
name|returnTemporaryFile
argument_list|(
name|tempFile
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|==
name|BinaryDocument
operator|.
name|BINARY_FILE
condition|)
block|{
return|return
name|Right
argument_list|(
name|broker
operator|.
name|getBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|resource
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Left
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Unknown resource type "
operator|+
name|url
operator|.
name|getPath
argument_list|()
operator|+
literal|": "
operator|+
name|resource
operator|.
name|getResourceType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
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
literal|"End document download"
argument_list|)
expr_stmt|;
block|}
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
return|return
name|Left
argument_list|(
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
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
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
return|return
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
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
return|return
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
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
return|return
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|skip
argument_list|(
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
return|return
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|markSupported
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
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
specifier|final
name|int
name|readlimit
parameter_list|)
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
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|mark
argument_list|(
name|readlimit
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
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
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
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|read
argument_list|()
return|;
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
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|underlyingStream
operator|.
name|get
argument_list|()
operator|.
name|available
argument_list|()
return|;
block|}
block|}
end_class

end_unit

