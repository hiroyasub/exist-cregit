begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|File
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
name|util
operator|.
name|Optional
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
name|assertNull
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

begin_comment
comment|/**  * @author wolf  *   */
end_comment

begin_class
specifier|public
class|class
name|CopyResourceTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|storeAndRead
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|EXistException
block|{
specifier|final
name|String
name|testCollectionName
init|=
literal|"copyResource"
decl_stmt|;
specifier|final
name|String
name|subCollection
init|=
literal|"storeAndRead"
decl_stmt|;
name|store
argument_list|(
name|testCollectionName
argument_list|,
name|subCollection
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|read
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeAndReadAborted
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|EXistException
block|{
specifier|final
name|String
name|testCollectionName
init|=
literal|"copyResource"
decl_stmt|;
specifier|final
name|String
name|subCollection
init|=
literal|"storeAndReadAborted"
decl_stmt|;
name|storeAborted
argument_list|(
name|testCollectionName
argument_list|,
name|subCollection
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|readAborted
argument_list|(
name|testCollectionName
argument_list|,
name|subCollection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|(
specifier|final
name|String
name|testCollectionName
parameter_list|,
specifier|final
name|String
name|subCollection
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|DatabaseConfigurationException
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
name|Optional
operator|.
name|of
argument_list|(
name|pool
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
name|Collection
name|testCollection
decl_stmt|;
name|IndexInfo
name|info
decl_stmt|;
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
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
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
name|testCollection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|subTestCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|append
argument_list|(
name|subCollection
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|subTestCollection
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|subTestCollection
argument_list|)
expr_stmt|;
specifier|final
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/shakespeare/r_and_j.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|info
operator|=
name|subTestCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|subTestCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|,
literal|false
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
name|broker
operator|.
name|copyResource
argument_list|(
name|transaction
argument_list|,
name|info
operator|.
name|getDocument
argument_list|()
argument_list|,
name|testCollection
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
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
name|read
parameter_list|(
specifier|final
name|String
name|testCollectionName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
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
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
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
name|Optional
operator|.
name|of
argument_list|(
name|pool
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
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|append
argument_list|(
literal|"new_test.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
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
block|}
block|}
specifier|private
name|void
name|storeAborted
parameter_list|(
specifier|final
name|String
name|testCollectionName
parameter_list|,
specifier|final
name|String
name|subCollection
parameter_list|)
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
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
name|Optional
operator|.
name|of
argument_list|(
name|pool
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
name|Collection
name|testCollection
decl_stmt|;
name|IndexInfo
name|info
decl_stmt|;
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
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
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
name|testCollection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
argument_list|)
expr_stmt|;
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
name|subTestCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|append
argument_list|(
name|subCollection
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|subTestCollection
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|subTestCollection
argument_list|)
expr_stmt|;
specifier|final
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/shakespeare/r_and_j.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|info
operator|=
name|subTestCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test2.xml"
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|subTestCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|,
literal|false
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
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|broker
operator|.
name|copyResource
argument_list|(
name|transaction
argument_list|,
name|info
operator|.
name|getDocument
argument_list|()
argument_list|,
name|testCollection
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"new_test2.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
comment|//DO NOT COMMIT TRANSACTION
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|getJournal
argument_list|()
operator|.
name|flushToLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|readAborted
parameter_list|(
specifier|final
name|String
name|testCollectionName
parameter_list|,
specifier|final
name|String
name|subCollection
parameter_list|)
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
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
name|Optional
operator|.
name|of
argument_list|(
name|pool
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
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|append
argument_list|(
name|subCollection
argument_list|)
operator|.
name|append
argument_list|(
literal|"test2.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
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
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
operator|.
name|append
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|append
argument_list|(
literal|"new_test2.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Document should not exist"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
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

