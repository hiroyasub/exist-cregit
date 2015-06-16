begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|triggers
operator|.
name|TriggerException
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
name|test
operator|.
name|TestConstants
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
name|Configuration
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
name|DatabaseConfigurationException
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
name|LockException
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  *  0 byte binary files cannot be retrieved from database. This test  * displays the error.  *  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|ResourceTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|EMPTY_BINARY_FILE
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|DOCUMENT_NAME_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"empty.txt"
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
block|{
specifier|final
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
name|BrokerPool
operator|.
name|getInstance
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeAndRead
parameter_list|()
throws|throws
name|TriggerException
throws|,
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|EXistException
block|{
name|store
argument_list|()
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|read
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|()
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|LockException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
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
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
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
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|final
name|BinaryDocument
name|doc
init|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|DOCUMENT_NAME_URI
argument_list|,
name|EMPTY_BINARY_FILE
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"text/text"
argument_list|)
decl_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|read
parameter_list|()
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|;
init|)
block|{
specifier|final
name|XmldbURI
name|docPath
init|=
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|DOCUMENT_NAME_URI
argument_list|)
decl_stmt|;
name|BinaryDocument
name|binDoc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|binDoc
operator|=
operator|(
name|BinaryDocument
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|docPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
comment|// if document is not present, null is returned
if|if
condition|(
name|binDoc
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Binary document '"
operator|+
name|docPath
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|binDoc
argument_list|)
init|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|broker
operator|.
name|getBinaryResourceSize
argument_list|(
name|binDoc
argument_list|)
index|]
expr_stmt|;
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|binDoc
operator|!=
literal|null
condition|)
block|{
name|binDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|collection
operator|.
name|removeBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|binDoc
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|store2
parameter_list|()
throws|throws
name|TriggerException
throws|,
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|EXistException
block|{
name|store
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|XmldbURI
name|docPath
init|=
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|DOCUMENT_NAME_URI
argument_list|)
decl_stmt|;
name|BinaryDocument
name|binDoc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|binDoc
operator|=
operator|(
name|BinaryDocument
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|docPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
comment|// if document is not present, null is returned
if|if
condition|(
name|binDoc
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Binary document '"
operator|+
name|docPath
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|binDoc
argument_list|)
init|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|broker
operator|.
name|getBinaryResourceSize
argument_list|(
name|binDoc
argument_list|)
index|]
expr_stmt|;
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|binDoc
operator|!=
literal|null
condition|)
block|{
name|binDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

