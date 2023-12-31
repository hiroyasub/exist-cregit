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
name|collections
operator|.
name|triggers
package|;
end_package

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
name|CollectionConfiguration
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
name|DefaultDocumentSet
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
name|DocumentSet
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
name|*
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
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|diff
operator|.
name|Diff
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
name|Iterator
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|HistoryTriggerTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|TEST_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test-history-trigger"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|TEST_CONFIG_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
operator|.
name|append
argument_list|(
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">\n"
operator|+
literal|"<triggers>\n"
operator|+
literal|"<trigger class=\"org.exist.collections.triggers.HistoryTrigger\"/>\n"
operator|+
literal|"</triggers>\n"
operator|+
literal|"</collection>"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
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
comment|// create and store the collection.xconf for the test collection
name|Collection
name|configCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_CONFIG_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|configCollection
argument_list|)
expr_stmt|;
specifier|final
name|IndexInfo
name|indexInfo
init|=
name|configCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|CollectionConfiguration
operator|.
name|DEFAULT_COLLECTION_CONFIG_FILE_URI
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
decl_stmt|;
name|configCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
comment|// create the test collection
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
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
name|removeCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_CONFIG_COLLECTION_URI
argument_list|)
expr_stmt|;
name|removeCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
expr_stmt|;
name|removeCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|HistoryTrigger
operator|.
name|DEFAULT_ROOT_PATH
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeCollection
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
name|XmldbURI
name|collectionUri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
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
block|}
comment|/**      * Ensure that we can store a document and then overwrite it      * when the {@link HistoryTrigger} is enabled on the Collection      *      * @see<a href="https://github.com/eXist-db/exist/issues/139">History trigger fails #139</a>      */
annotation|@
name|Test
specifier|public
name|void
name|storeAndOverwriteByCopy
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|XmldbURI
name|testDoc1Name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test_store-and-overwrite-by-copy.xml"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|testDoc1Content
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<hello>12345</hello>"
decl_stmt|;
specifier|final
name|XmldbURI
name|testDoc2Name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"other.xml"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|testDoc2Content
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<other>thing</other>"
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
comment|// store the first document
name|storeInTestCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|testDoc1Name
argument_list|,
name|testDoc1Content
argument_list|)
expr_stmt|;
comment|// store the second document
name|storeInTestCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|testDoc2Name
argument_list|,
name|testDoc2Content
argument_list|)
expr_stmt|;
comment|// overwrite the first document by copying the second over it (and make sure we don't get a StackOverflow exception)
try|try
init|(
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TEST_COLLECTION_URI
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|;
init|)
block|{
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDoc2
init|=
name|testCollection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|testDoc2Name
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|lockedDoc2
argument_list|)
expr_stmt|;
comment|// copy doc2 over doc1
name|broker
operator|.
name|copyResource
argument_list|(
name|transaction
argument_list|,
name|lockedDoc2
operator|.
name|getDocument
argument_list|()
argument_list|,
name|testCollection
argument_list|,
name|testDoc1Name
argument_list|)
expr_stmt|;
comment|// NOTE: early release of Collection lock inline with Asymmetrical Locking scheme
name|testCollection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check that a copy of the original document was made
name|checkHistoryOfOriginal
argument_list|(
name|brokerPool
argument_list|,
name|testDoc1Name
argument_list|,
name|testDoc1Content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeAndOverwrite
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|XmldbURI
name|testDocName
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test_store-and-overwrite.xml"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|testDocContent
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<hello>world</hello>"
decl_stmt|;
specifier|final
name|String
name|testDoc2Content
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<and>another thing</and>"
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
comment|// store the document
name|storeInTestCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|testDocName
argument_list|,
name|testDocContent
argument_list|)
expr_stmt|;
comment|// overwrite the document (and make sure we don't get a StackOverflow exception)
name|storeInTestCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|testDocName
argument_list|,
name|testDoc2Content
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check that a copy of the original document was made
name|checkHistoryOfOriginal
argument_list|(
name|brokerPool
argument_list|,
name|testDocName
argument_list|,
name|testDocContent
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeInTestCollection
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|XmldbURI
name|docName
parameter_list|,
specifier|final
name|String
name|docContent
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|EXistException
throws|,
name|IOException
block|{
try|try
init|(
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TEST_COLLECTION_URI
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
specifier|final
name|IndexInfo
name|indexInfo
init|=
name|testCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|,
name|docContent
argument_list|)
decl_stmt|;
name|testCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|docContent
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkHistoryOfOriginal
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|XmldbURI
name|originalDocName
parameter_list|,
specifier|final
name|String
name|orginalDocContent
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
block|{
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
try|try
init|(
specifier|final
name|Collection
name|historyCollection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|HistoryTrigger
operator|.
name|DEFAULT_ROOT_PATH
operator|.
name|append
argument_list|(
name|TEST_COLLECTION_URI
argument_list|)
operator|.
name|append
argument_list|(
name|originalDocName
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|;
init|)
block|{
name|assertNotNull
argument_list|(
name|historyCollection
argument_list|)
expr_stmt|;
specifier|final
name|DocumentSet
name|documentSet
init|=
name|historyCollection
operator|.
name|getDocuments
argument_list|(
name|broker
argument_list|,
operator|new
name|DefaultDocumentSet
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|documentSet
operator|.
name|getDocumentCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|it
init|=
name|documentSet
operator|.
name|getDocumentIterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|Diff
name|diff
init|=
name|DiffBuilder
operator|.
name|compare
argument_list|(
name|Input
operator|.
name|from
argument_list|(
name|orginalDocContent
argument_list|)
argument_list|)
operator|.
name|withTest
argument_list|(
name|Input
operator|.
name|from
argument_list|(
name|doc
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|diff
operator|.
name|hasDifferences
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
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
block|}
end_class

end_unit

