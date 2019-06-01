begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|IndexQueryService
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
name|Collection
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
name|CollectionManagementService
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

begin_class
specifier|public
class|class
name|SAXTriggerTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT1_CONTENT
init|=
literal|"<test>"
operator|+
literal|"<item id='1'><price>5.6</price><stock>22</stock></item>"
operator|+
literal|"<item id='2'><price>7.4</price><stock>43</stock></item>"
operator|+
literal|"<item id='3'><price>18.4</price><stock>5</stock></item>"
operator|+
literal|"<item id='4'><price>65.54</price><stock>16</stock></item>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT2_CONTENT
init|=
literal|"<test>"
operator|+
literal|"<item id='1'><price>5.6</price><stock>22</stock></item>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT3_CONTENT
init|=
literal|"<test test=\"valueTest\">\n"
operator|+
literal|"<item id=\"1\" test=\"valueTest\">\n"
operator|+
literal|"<price test=\"valueTest\">5.6</price>\n"
operator|+
literal|"<stock test=\"valueTest\">22</stock>\n"
operator|+
literal|"</item>\n"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<exist:collection xmlns:exist='http://exist-db.org/collection-config/1.0'>"
operator|+
literal|"<exist:triggers>"
operator|+
literal|"<exist:trigger class='org.exist.collections.triggers.StoreTrigger'/>"
operator|+
literal|"</exist:triggers>"
operator|+
literal|"</exist:collection>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|BASE_URI
init|=
literal|"xmldb:exist://"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|testCollection
init|=
literal|"/db/triggers"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|EXistException
throws|,
name|XMLDBException
block|{
specifier|final
name|BrokerPool
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|db
operator|.
name|registerDocumentTrigger
argument_list|(
name|AnotherTrigger
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|BASE_URI
operator|+
name|testCollection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|resource
init|=
name|root
operator|.
name|createResource
argument_list|(
literal|"data.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|DOCUMENT1_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|AnotherTrigger
operator|.
name|createDocumentEvents
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|26
argument_list|,
name|AnotherTrigger
operator|.
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOCUMENT1_CONTENT
argument_list|,
name|AnotherTrigger
operator|.
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saxEventModifications
parameter_list|()
throws|throws
name|EXistException
throws|,
name|XMLDBException
block|{
specifier|final
name|BrokerPool
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|db
operator|.
name|registerDocumentTrigger
argument_list|(
name|StoreTrigger
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|BASE_URI
operator|+
name|testCollection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|root
operator|.
name|createResource
argument_list|(
literal|"data.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|DOCUMENT2_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|root
operator|.
name|createResource
argument_list|(
literal|"data.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOCUMENT3_CONTENT
argument_list|,
name|resource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saxEventModificationsAtXConf
parameter_list|()
throws|throws
name|EXistException
throws|,
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|BASE_URI
operator|+
name|testCollection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|root
operator|.
name|createResource
argument_list|(
literal|"data.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|DOCUMENT2_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|root
operator|.
name|createResource
argument_list|(
literal|"data.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOCUMENT3_CONTENT
argument_list|,
name|resource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanDB
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|config
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|BASE_URI
operator|+
literal|"/db/system/config"
operator|+
name|testCollection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|CollectionManagementService
name|mgmt
init|=
operator|(
name|CollectionManagementService
operator|)
name|config
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|mgmt
operator|.
name|removeCollection
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|BASE_URI
operator|+
name|testCollection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|root
operator|.
name|getResource
argument_list|(
literal|"messages.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|root
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
name|resource
operator|=
name|root
operator|.
name|getResource
argument_list|(
literal|"data.xml"
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|root
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|XMLDBException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
block|{
name|CollectionManagementService
name|mgmt
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|testCol
init|=
name|mgmt
operator|.
name|createCollection
argument_list|(
literal|"triggers"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|mgmt
operator|=
operator|(
name|CollectionManagementService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|testCol
operator|=
name|mgmt
operator|.
name|createCollection
argument_list|(
literal|"sub"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|LockException
throws|,
name|TriggerException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
