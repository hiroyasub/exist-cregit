begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|test
package|;
end_package

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
name|StorageAddress
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
name|security
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
name|security
operator|.
name|xacml
operator|.
name|AccessContext
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
name|Sequence
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
name|NodeProxy
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
name|StoredNode
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
name|Node
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
name|Attr
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
name|Text
import|;
end_import

begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_class
specifier|public
class|class
name|DLNStorageTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
name|String
name|TEST_COLLECTION
init|=
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
decl_stmt|;
specifier|private
specifier|static
name|String
name|TEST_XML
init|=
literal|"<test>\n"
operator|+
literal|"<para>My first paragraph.</para>\n"
operator|+
literal|"<!-- A comment -->\n"
operator|+
literal|"<para>This one contains a<a href=\"#\">link</a>.</para>\n"
operator|+
literal|"<?echo \"A processing instruction\"?>\n"
operator|+
literal|"<para>Another<b>paragraph</b>.</para>\n"
operator|+
literal|"</test>"
decl_stmt|;
specifier|public
name|void
name|testNodeStorage
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
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
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|SYSTEM_USER
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
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
comment|// test element ids
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"doc('/db/test/test_string.xml')/test/para"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|comment
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|comment
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.1"
argument_list|)
expr_stmt|;
name|comment
operator|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|comment
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.3"
argument_list|)
expr_stmt|;
name|comment
operator|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|comment
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.5"
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"doc('/db/test/test_string.xml')/test//a"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|a
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.3.2"
argument_list|,
name|a
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test attribute id
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"doc('/db/test/test_string.xml')/test//a/@href"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|href
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StorageAddress
operator|.
name|toString
argument_list|(
name|href
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.3.2.1"
argument_list|,
name|href
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test Attr deserialization
name|Attr
name|attr
init|=
operator|(
name|Attr
operator|)
name|href
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StorageAddress
operator|.
name|toString
argument_list|(
operator|(
operator|(
name|StoredNode
operator|)
name|attr
operator|)
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// test Attr fields
name|assertEquals
argument_list|(
name|attr
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"href"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
argument_list|,
literal|"href"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
comment|// test DOMFile.getNodeValue()
name|assertEquals
argument_list|(
name|href
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
comment|// test text node
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
literal|"doc('/db/test/test_string.xml')/test//b/text()"
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|text
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.5.2.1"
argument_list|,
name|text
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test DOMFile.getNodeValue()
name|assertEquals
argument_list|(
name|text
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"paragraph"
argument_list|)
expr_stmt|;
comment|// test Text deserialization
name|Text
name|node
init|=
operator|(
name|Text
operator|)
name|text
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|,
literal|"paragraph"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|node
operator|.
name|getData
argument_list|()
argument_list|,
literal|"paragraph"
argument_list|)
expr_stmt|;
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
name|void
name|setUp
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
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
name|org
operator|.
name|exist
operator|.
name|security
operator|.
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
name|test
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION
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
name|IndexInfo
name|info
init|=
name|test
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
literal|"test_string.xml"
argument_list|,
name|TEST_XML
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction commited ..."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
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
specifier|protected
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

