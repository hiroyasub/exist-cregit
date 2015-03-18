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
name|MutableDocumentSet
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
name|xmldb
operator|.
name|CollectionManagementServiceImpl
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
name|xupdate
operator|.
name|Modification
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xupdate
operator|.
name|XUpdateProcessor
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
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XUpdateQueryService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_comment
comment|/**  * Tests recovery of XUpdate operations.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|UpdateRecoverTest
block|{
specifier|private
specifier|static
name|String
name|TEST_XML
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<products>"
operator|+
literal|"<product id=\"0\">"
operator|+
literal|"<description>Milk</description>"
operator|+
literal|"<price>22.50</price>"
operator|+
literal|"</product>"
operator|+
literal|"</products>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|storeAndRead
parameter_list|()
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
annotation|@
name|Test
specifier|public
name|void
name|storeAndReadXmldb
parameter_list|()
block|{
name|xmldbStore
argument_list|()
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|xmldbRead
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|store
parameter_list|()
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
init|)
block|{
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
name|info
operator|=
name|test2
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|TestConstants
operator|.
name|TEST_XML_URI
argument_list|,
name|TEST_XML
argument_list|)
expr_stmt|;
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
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|info
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|XUpdateProcessor
name|proc
init|=
operator|new
name|XUpdateProcessor
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|String
name|xupdate
decl_stmt|;
name|Modification
name|modifications
index|[]
decl_stmt|;
comment|// insert some nodes
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:insert-before select=\"/products/product[1]\">"
operator|+
literal|"<product>"
operator|+
literal|"<description>Product "
operator|+
name|i
operator|+
literal|"</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|2.5
operator|)
operator|+
literal|"</price>"
operator|+
literal|"<stock>"
operator|+
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|"</stock>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:insert-before>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// add attribute
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products/product["
operator|+
name|i
operator|+
literal|"]\">"
operator|+
literal|"<xu:attribute name=\"id\">"
operator|+
name|i
operator|+
literal|"</xu:attribute>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// replace some
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:replace select=\"/products/product["
operator|+
name|i
operator|+
literal|"]\">"
operator|+
literal|"<product id=\""
operator|+
name|i
operator|+
literal|"\">"
operator|+
literal|"<description>Replaced product</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|0.75
operator|)
operator|+
literal|"</price>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:replace>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|long
name|mods
init|=
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
decl_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// remove some
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:remove select=\"/products/product[last()]\"/>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products\">"
operator|+
literal|"<product>"
operator|+
literal|"<xu:attribute name=\"id\"><xu:value-of select=\"count(/products/product) + 1\"/></xu:attribute>"
operator|+
literal|"<description>Product "
operator|+
name|i
operator|+
literal|"</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|2.5
operator|)
operator|+
literal|"</price>"
operator|+
literal|"<stock>"
operator|+
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|"</stock>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// rename element "description" to "descript"
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:rename select=\"/products/product/description\">descript</xu:rename>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// update attribute values
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:update select=\"/products/product["
operator|+
name|i
operator|+
literal|"]/@id\">"
operator|+
name|i
operator|+
literal|"u</xu:update>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|long
name|mods
init|=
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
decl_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// append new element to records
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products/product["
operator|+
name|i
operator|+
literal|"]\">"
operator|+
literal|"<date><xu:value-of select=\"current-dateTime()\"/></date>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// update element content
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:update select=\"/products/product["
operator|+
name|i
operator|+
literal|"]/price\">19.99</xu:update>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|long
name|mods
init|=
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
decl_stmt|;
name|proc
operator|.
name|reset
argument_list|()
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
block|}
specifier|private
name|void
name|read
parameter_list|()
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
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|startDB
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
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
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
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
name|assertNotNull
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|DocumentImpl
name|doc
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
name|TEST_XML_URI
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Document '"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/test2/test.xml' should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|xmldbStore
parameter_list|()
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
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|startDB
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|CollectionManagementServiceImpl
name|mgr
init|=
operator|(
name|CollectionManagementServiceImpl
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|test
init|=
name|root
operator|.
name|getChildCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|==
literal|null
condition|)
name|test
operator|=
name|mgr
operator|.
name|createCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|test2
init|=
name|test
operator|.
name|getChildCollection
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test2
operator|==
literal|null
condition|)
name|test2
operator|=
name|mgr
operator|.
name|createCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
name|test2
operator|.
name|createResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|TEST_XML
argument_list|)
expr_stmt|;
name|test2
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|test2
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|String
name|xupdate
decl_stmt|;
comment|// insert some nodes
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:insert-before select=\"/products/product[1]\">"
operator|+
literal|"<product>"
operator|+
literal|"<description>Product "
operator|+
name|i
operator|+
literal|"</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|2.5
operator|)
operator|+
literal|"</price>"
operator|+
literal|"<stock>"
operator|+
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|"</stock>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:insert-before>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|// add attribute
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products/product["
operator|+
name|i
operator|+
literal|"]\">"
operator|+
literal|"<xu:attribute name=\"id\">"
operator|+
name|i
operator|+
literal|"</xu:attribute>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|// replace some
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:replace select=\"/products/product["
operator|+
name|i
operator|+
literal|"]\">"
operator|+
literal|"<product id=\""
operator|+
name|i
operator|+
literal|"\">"
operator|+
literal|"<description>Replaced product</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|0.75
operator|)
operator|+
literal|"</price>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:replace>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|// remove some
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:remove select=\"/products/product[last()]\"/>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products\">"
operator|+
literal|"<product>"
operator|+
literal|"<xu:attribute name=\"id\"><xu:value-of select=\"count(/products/product) + 1\"/></xu:attribute>"
operator|+
literal|"<description>Product "
operator|+
name|i
operator|+
literal|"</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|2.5
operator|)
operator|+
literal|"</price>"
operator|+
literal|"<stock>"
operator|+
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|"</stock>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|// rename element "description" to "descript"
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:rename select=\"/products/product/description\">descript</xu:rename>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
comment|// update attribute values
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:update select=\"/products/product["
operator|+
name|i
operator|+
literal|"]/@id\">"
operator|+
name|i
operator|+
literal|"u</xu:update>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|// append new element to records
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products/product["
operator|+
name|i
operator|+
literal|"]\">"
operator|+
literal|"<date><xu:value-of select=\"current-dateTime()\"/></date>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|// update element content
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:update select=\"/products/product["
operator|+
name|i
operator|+
literal|"]/price\">19.99</xu:update>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|service
operator|.
name|updateResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
block|}
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
block|}
specifier|private
name|void
name|xmldbRead
parameter_list|()
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|test2
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
name|test2
operator|.
name|getResource
argument_list|(
literal|"test_xmldb.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|CollectionManagementServiceImpl
name|mgr
init|=
operator|(
name|CollectionManagementServiceImpl
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|removeCollection
argument_list|(
literal|"test"
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
block|}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
block|{
try|try
block|{
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
comment|// initialize driver
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
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

