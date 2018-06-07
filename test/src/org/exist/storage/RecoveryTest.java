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
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|List
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
name|TestUtils
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
name|btree
operator|.
name|BTreeException
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
name|dom
operator|.
name|DOMFile
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
operator|.
name|LockMode
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
name|XQuery
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
name|Item
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
name|NodeValue
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
name|SequenceIterator
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
comment|/**  * Test recovery after a forced database corruption.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|RecoveryTest
block|{
comment|// we don't use @ClassRule/@Rule as we want to force corruption in some tests
specifier|private
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
specifier|private
specifier|static
name|Path
name|dir
init|=
name|TestUtils
operator|.
name|shakespeareSamples
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|String
name|TEST_XML
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<test>"
operator|+
literal|"<title>Hello</title>"
operator|+
literal|"<para>Hello World!</para>"
operator|+
literal|"</test>"
decl_stmt|;
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
throws|,
name|BTreeException
throws|,
name|XPathException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDb
argument_list|()
decl_stmt|;
name|store
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|stopDb
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|startDb
argument_list|()
expr_stmt|;
name|read
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|(
specifier|final
name|BrokerPool
name|pool
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
init|;
init|)
block|{
name|Collection
name|test2
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|files
decl_stmt|;
name|BinaryDocument
name|doc
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
name|test2
operator|=
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
name|files
operator|=
name|FileUtils
operator|.
name|list
argument_list|(
name|dir
argument_list|,
name|XMLFilenameFilter
operator|.
name|asPredicate
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|doc
operator|=
name|test2
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|TestConstants
operator|.
name|TEST_BINARY_URI
argument_list|,
literal|"Some text data"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// store some documents. Will be replaced below
for|for
control|(
specifier|final
name|Path
name|f
range|:
name|files
control|)
block|{
try|try
block|{
specifier|final
name|IndexInfo
name|info
init|=
name|test2
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
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test2
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
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Error found while parsing document: "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// replace some documents
for|for
control|(
specifier|final
name|Path
name|f
range|:
name|files
control|)
block|{
try|try
block|{
specifier|final
name|IndexInfo
name|info
init|=
name|test2
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
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test2
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
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Error found while parsing document: "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|IndexInfo
name|info
init|=
name|test2
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
literal|"test_string.xml"
argument_list|)
argument_list|,
name|TEST_XML
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
comment|//TODO : unlock the collection here ?
name|test2
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|TEST_XML
argument_list|)
expr_stmt|;
comment|// remove last document
name|test2
operator|.
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
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
comment|// the following transaction will not be committed. It will thus be rolled back by recovery
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|test2
operator|.
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|files
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|test2
operator|.
name|removeBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
comment|//DO NOT COMMIT TRANSACTION
name|pool
operator|.
name|getJournalManager
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//DOMFile domDb = ((NativeBroker)broker).getDOMFile();
comment|//assertNotNull(domDb);
comment|//Writer writer = new StringWriter();
comment|//domDb.dump(writer);
comment|//System.out.println(writer.toString());
block|}
block|}
specifier|private
name|void
name|read
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|XPathException
throws|,
name|IOException
throws|,
name|BTreeException
throws|,
name|LockException
block|{
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
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDoc
init|=
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
literal|"test/test2/hamlet.xml"
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Document '"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/test2/hamlet.xml' should not be null"
argument_list|,
name|lockedDoc
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
name|lockedDoc
operator|.
name|getDocument
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDoc
init|=
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
literal|"test/test2/test_string.xml"
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Document '"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/test2/test_string.xml' should not be null"
argument_list|,
name|lockedDoc
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
name|lockedDoc
operator|.
name|getDocument
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
init|=
name|FileUtils
operator|.
name|list
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|files
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
operator|.
name|append
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNull
argument_list|(
literal|"Document '"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/test2/'"
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|files
operator|.
name|get
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|+
literal|" should not exist anymore"
argument_list|,
name|lockedDoc
argument_list|)
expr_stmt|;
block|}
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"//SPEECH[ft:query(LINE, 'king')]"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|next
argument_list|)
decl_stmt|;
block|}
try|try
init|(
specifier|final
name|LockedDocument
name|lockedBinDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
operator|.
name|append
argument_list|(
name|TestConstants
operator|.
name|TEST_BINARY_URI
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Binary document is null"
argument_list|,
name|lockedBinDoc
argument_list|)
expr_stmt|;
specifier|final
name|BinaryDocument
name|binDoc
init|=
operator|(
name|BinaryDocument
operator|)
name|lockedBinDoc
operator|.
name|getDocument
argument_list|()
decl_stmt|;
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
specifier|final
name|byte
index|[]
name|bdata
init|=
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
decl_stmt|;
name|is
operator|.
name|read
argument_list|(
name|bdata
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
operator|new
name|String
argument_list|(
name|bdata
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|DOMFile
name|domDb
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getDOMFile
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|domDb
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
name|domDb
operator|.
name|dump
argument_list|(
name|writer
argument_list|)
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
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|root
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|acquireCollectionLock
argument_list|(
parameter_list|()
lambda|->
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getLockManager
argument_list|()
operator|.
name|acquireCollectionWriteLock
argument_list|(
name|root
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
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
name|BrokerPool
name|startDb
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|existEmbeddedServer
operator|.
name|startDb
argument_list|()
expr_stmt|;
return|return
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopDb
parameter_list|()
block|{
name|existEmbeddedServer
operator|.
name|stopDb
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

