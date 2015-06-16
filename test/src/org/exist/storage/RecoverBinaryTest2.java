begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|*
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
name|assertNotNull
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

begin_class
specifier|public
class|class
name|RecoverBinaryTest2
block|{
specifier|private
specifier|static
name|String
name|directory
init|=
literal|"webapp/resources"
decl_stmt|;
comment|//private static File dir = new File(directory);
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
name|tearDown
argument_list|()
expr_stmt|;
name|read2
argument_list|()
expr_stmt|;
block|}
comment|//@Test
specifier|public
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
name|root
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
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|test2
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|storeFiles
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|test2
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
block|}
comment|//@Test
specifier|public
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
name|LockException
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
init|)
block|{
specifier|final
name|Collection
name|test2
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|test2
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
block|}
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
try|try
init|(
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
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|storeFiles
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|test2
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
block|}
block|}
comment|//@Test
specifier|public
name|void
name|read2
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
init|)
block|{
specifier|final
name|Collection
name|test2
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|test2
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
try|try
init|(
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
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|test1
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
name|test1
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
block|}
block|}
specifier|private
name|void
name|storeFiles
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Collection
name|test2
parameter_list|)
throws|throws
name|IOException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
comment|// Get files in directory
specifier|final
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
specifier|final
name|File
name|files
index|[]
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Check directory '"
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'."
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// store some documents.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
specifier|final
name|File
name|f
range|:
name|files
control|)
block|{
name|assertNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
specifier|final
name|XmldbURI
name|uri
init|=
name|test2
operator|.
name|getURI
argument_list|()
operator|.
name|append
argument_list|(
name|j
operator|+
literal|"_"
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
init|)
block|{
specifier|final
name|BinaryDocument
name|doc
init|=
name|test2
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|uri
argument_list|,
name|is
argument_list|,
name|MimeType
operator|.
name|BINARY_TYPE
operator|.
name|getName
argument_list|()
argument_list|,
name|f
operator|.
name|length
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
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
block|}
end_class

end_unit

