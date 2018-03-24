begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|ByteArrayOutputStream
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
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
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
name|XQueryPool
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
name|Txn
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedBinariesTest
extends|extends
name|AbstractBinariesTest
argument_list|<
name|Sequence
argument_list|,
name|Item
argument_list|,
name|IOException
argument_list|>
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|storeBinaryFile
parameter_list|(
specifier|final
name|XmldbURI
name|filePath
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|filePath
operator|.
name|removeLastSegment
argument_list|()
argument_list|)
decl_stmt|;
name|collection
operator|.
name|getLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
argument_list|)
init|)
block|{
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|filePath
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|is
argument_list|,
literal|"application/octet-stream"
argument_list|,
name|content
operator|.
name|length
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|collection
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeCollection
parameter_list|(
specifier|final
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|collectionUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|getLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|QueryResultAccessor
argument_list|<
name|Sequence
argument_list|,
name|IOException
argument_list|>
name|executeXQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQueryPool
name|pool
init|=
name|brokerPool
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xquery
init|=
name|brokerPool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|CompiledXQuery
name|existingCompiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|source
argument_list|)
decl_stmt|;
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|final
name|CompiledXQuery
name|compiled
decl_stmt|;
if|if
condition|(
name|existingCompiled
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|XQueryContext
argument_list|(
name|brokerPool
argument_list|)
expr_stmt|;
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|=
name|existingCompiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|compiled
operator|=
name|existingCompiled
expr_stmt|;
block|}
specifier|final
name|Sequence
name|results
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|consumer2E
lambda|->
block|{
try|try
block|{
comment|//                    context.runCleanupTasks();  //TODO(AR) shows the ordering issue with binary values (see comment below)
name|consumer2E
operator|.
name|accept
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//TODO(AR) performing #runCleanupTasks causes the stream to be closed, so if we do so before we are finished with the results, serialization fails.
name|context
operator|.
name|runCleanupTasks
argument_list|()
expr_stmt|;
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|source
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|long
name|size
parameter_list|(
specifier|final
name|Sequence
name|results
parameter_list|)
block|{
return|return
name|results
operator|.
name|getItemCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Item
name|item
parameter_list|(
specifier|final
name|Sequence
name|results
parameter_list|,
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
name|results
operator|.
name|itemAt
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isBinaryType
parameter_list|(
specifier|final
name|Item
name|item
parameter_list|)
block|{
return|return
name|Type
operator|.
name|BASE64_BINARY
operator|==
name|item
operator|.
name|getType
argument_list|()
operator|||
name|Type
operator|.
name|HEX_BINARY
operator|==
name|item
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isBooleanType
parameter_list|(
specifier|final
name|Item
name|item
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Type
operator|.
name|BOOLEAN
operator|==
name|item
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|getBytes
parameter_list|(
specifier|final
name|Item
name|item
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|item
operator|instanceof
name|Base64BinaryDocument
condition|)
block|{
specifier|final
name|Base64BinaryDocument
name|doc
init|=
operator|(
name|Base64BinaryDocument
operator|)
name|item
decl_stmt|;
try|try
init|(
specifier|final
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
init|)
block|{
name|doc
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
block|}
else|else
block|{
specifier|final
name|BinaryValueFromFile
name|file
init|=
operator|(
name|BinaryValueFromFile
operator|)
name|item
decl_stmt|;
try|try
init|(
specifier|final
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
init|)
block|{
name|file
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
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
specifier|final
name|Item
name|item
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|BooleanValue
operator|)
name|item
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

