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
name|LockedDocument
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
name|EXistCollectionManagementService
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
name|DatabaseImpl
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
name|base
operator|.
name|XMLDBException
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|StringReader
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
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|storeAndRead
parameter_list|()
throws|throws
name|IllegalAccessException
throws|,
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|InstantiationException
throws|,
name|SAXException
throws|,
name|XMLDBException
throws|,
name|EXistException
throws|,
name|ClassNotFoundException
throws|,
name|LockException
throws|,
name|ParserConfigurationException
throws|,
name|XPathException
throws|,
name|IOException
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
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|restartDb
argument_list|()
expr_stmt|;
name|read
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeAndReadXmldb
parameter_list|()
throws|throws
name|IllegalAccessException
throws|,
name|DatabaseConfigurationException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
throws|,
name|EXistException
throws|,
name|ClassNotFoundException
throws|,
name|IOException
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
name|startDb
argument_list|()
decl_stmt|;
name|xmldbStore
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|restartDb
argument_list|()
expr_stmt|;
name|xmldbRead
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
name|IllegalAccessException
throws|,
name|DatabaseConfigurationException
throws|,
name|InstantiationException
throws|,
name|ClassNotFoundException
throws|,
name|XMLDBException
throws|,
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
name|ParserConfigurationException
throws|,
name|XPathException
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
specifier|final
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
specifier|final
name|XUpdateProcessor
name|proc
init|=
operator|new
name|XUpdateProcessor
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|proc
argument_list|)
expr_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|Modification
name|modifications
index|[]
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|Modification
name|modifications
index|[]
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|Modification
name|modifications
index|[]
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:remove select=\"/products/product[last()]\"/>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
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
specifier|final
name|Modification
name|modifications
index|[]
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|Modification
name|modifications
index|[]
init|=
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
decl_stmt|;
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
name|String
name|xupdate
init|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:rename select=\"/products/product/description\">descript</xu:rename>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
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
name|Modification
name|modifications
index|[]
init|=
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
decl_stmt|;
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
name|IllegalAccessException
throws|,
name|DatabaseConfigurationException
throws|,
name|InstantiationException
throws|,
name|ClassNotFoundException
throws|,
name|XMLDBException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
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
init|;
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
name|TestConstants
operator|.
name|TEST_XML_URI
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|;
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
literal|"/test/test2/test.xml' should not be null"
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
block|}
block|}
specifier|private
name|void
name|xmldbStore
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|DatabaseConfigurationException
throws|,
name|InstantiationException
throws|,
name|ClassNotFoundException
throws|,
name|XMLDBException
throws|,
name|EXistException
block|{
specifier|final
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
specifier|final
name|EXistCollectionManagementService
name|mgr
init|=
operator|(
name|EXistCollectionManagementService
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
block|{
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
block|}
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
block|{
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
block|}
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:remove select=\"/products/product[last()]\"/>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
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
specifier|final
name|String
name|xupdate
init|=
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
decl_stmt|;
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
name|String
name|xupdate
init|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:rename select=\"/products/product/description\">descript</xu:rename>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
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
specifier|private
name|void
name|xmldbRead
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
name|EXistCollectionManagementService
name|mgr
init|=
operator|(
name|EXistCollectionManagementService
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
throws|,
name|XMLDBException
block|{
name|existEmbeddedServer
operator|.
name|startDb
argument_list|()
expr_stmt|;
specifier|final
name|Database
name|database
init|=
operator|new
name|DatabaseImpl
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
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
return|;
block|}
specifier|private
name|BrokerPool
name|restartDb
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|EXistException
block|{
name|existEmbeddedServer
operator|.
name|restart
argument_list|(
literal|false
argument_list|)
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
