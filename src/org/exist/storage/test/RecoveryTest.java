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
operator|.
name|test
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|security
operator|.
name|SecurityManager
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
name|NativeBroker
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
name|LockException
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
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|RecoveryTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|directory
init|=
literal|"/home/wolf/xml/shakespeare"
decl_stmt|;
specifier|private
specifier|static
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
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
specifier|public
name|void
name|testStore
parameter_list|()
throws|throws
name|Exception
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
name|startDB
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
literal|"/db/test"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|Collection
name|test
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
literal|"/db/test/test2"
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|File
name|files
index|[]
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|File
name|f
decl_stmt|;
name|IndexInfo
name|info
decl_stmt|;
name|BinaryDocument
name|doc
init|=
name|test
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
literal|"binary.txt"
argument_list|,
literal|"Some text data"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// store some documents. Will be replaced below
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|f
operator|=
name|files
index|[
name|i
index|]
expr_stmt|;
try|try
block|{
name|info
operator|=
name|test
operator|.
name|validate
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|f
operator|.
name|getName
argument_list|()
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
name|test
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
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error found while parsing document: "
operator|+
name|f
operator|.
name|getName
argument_list|()
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|f
operator|=
name|files
index|[
name|i
index|]
expr_stmt|;
try|try
block|{
name|info
operator|=
name|test
operator|.
name|validate
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|f
operator|.
name|getName
argument_list|()
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
name|test
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
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error found while parsing document: "
operator|+
name|f
operator|.
name|getName
argument_list|()
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
name|info
operator|=
name|test
operator|.
name|validate
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
literal|"test_string.xml"
argument_list|,
name|TEST_XML
argument_list|)
expr_stmt|;
name|test
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
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// remove last document
name|test
operator|.
name|removeDocument
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|files
index|[
name|files
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
comment|// the following transaction will not be committed. It will thus be rolled back by recovery
name|transaction
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|test
operator|.
name|removeDocument
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|files
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|test
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
name|transact
operator|.
name|getLogManager
argument_list|()
operator|.
name|flushToLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|domDb
operator|.
name|dump
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testRead
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testRead() ...\n"
argument_list|)
expr_stmt|;
name|File
name|files
index|[]
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
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
decl_stmt|;
name|String
name|data
decl_stmt|;
name|doc
operator|=
name|broker
operator|.
name|openDocument
argument_list|(
literal|"/db/test/test2/hamlet.xml"
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Document /db/test/test2/hamlet.xml should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|data
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|data
argument_list|)
expr_stmt|;
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
name|doc
operator|=
name|broker
operator|.
name|openDocument
argument_list|(
literal|"/db/test/test2/test_string.xml"
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Document /db/test/test2/test_string.xml should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|data
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|data
argument_list|)
expr_stmt|;
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
name|doc
operator|=
name|broker
operator|.
name|openDocument
argument_list|(
literal|"/db/test/test2/"
operator|+
name|files
index|[
name|files
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Document /db/test/test2/"
operator|+
name|files
index|[
name|files
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|" should not exist anymore"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"//SPEECH[LINE&= 'king']"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found: "
operator|+
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
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
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|next
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BinaryDocument
name|binDoc
init|=
operator|(
name|BinaryDocument
operator|)
name|broker
operator|.
name|openDocument
argument_list|(
literal|"/db/test/test2/binary.txt"
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Binary document is null"
argument_list|,
name|binDoc
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|String
argument_list|(
name|broker
operator|.
name|getBinaryResourceData
argument_list|(
name|binDoc
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|data
argument_list|)
expr_stmt|;
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
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|domDb
operator|.
name|dump
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|openCollection
argument_list|(
literal|"/db/test"
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|registerLock
argument_list|(
name|root
operator|.
name|getLock
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|home
decl_stmt|,
name|file
init|=
literal|"conf.xml"
decl_stmt|;
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
expr_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|file
argument_list|,
name|home
argument_list|)
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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

