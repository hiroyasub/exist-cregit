begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|TestUtils
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
name|DatabaseInstanceManager
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
name|AfterClass
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
name|junit
operator|.
name|BeforeClass
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|modules
operator|.
name|CollectionManagementService
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
name|XMLResource
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
name|XQueryService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * Test proper configuration of triggers in collection.xconf, in particular if there's  * only a configuration for the parent collection, but not the child. The trigger should  * be created with the correct base collection.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TriggerConfigTest
block|{
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|LinkedList
argument_list|<
name|String
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|LinkedList
argument_list|<
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|LinkedList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/db/triggers"
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/db/triggers/sub1"
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/db/triggers/sub1/sub2"
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_CONFIG
init|=
literal|"<exist:collection xmlns:exist='http://exist-db.org/collection-config/1.0'>"
operator|+
literal|"<exist:triggers>"
operator|+
literal|"<exist:trigger class='org.exist.collections.triggers.TestTrigger'/>"
operator|+
literal|"</exist:triggers>"
operator|+
literal|"</exist:collection>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_COLLECTION_CONFIG
init|=
literal|"<exist:collection xmlns:exist='http://exist-db.org/collection-config/1.0'>"
operator|+
literal|"</exist:collection>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT_CONTENT
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
name|BASE_URI
init|=
literal|"xmldb:exist://"
decl_stmt|;
specifier|private
name|String
name|testCollection
decl_stmt|;
specifier|public
name|TriggerConfigTest
parameter_list|(
name|String
name|testCollection
parameter_list|)
block|{
name|this
operator|.
name|testCollection
operator|=
name|testCollection
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|storeDocument
parameter_list|()
block|{
try|try
block|{
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
name|IndexQueryService
name|iqs
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
name|iqs
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
name|DOCUMENT_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|printMessages
argument_list|()
expr_stmt|;
name|XQueryService
name|qs
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|qs
operator|.
name|queryResource
argument_list|(
literal|"messages.xml"
argument_list|,
literal|"string(//event[last()]/@collection)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testCollection
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
annotation|@
name|Test
specifier|public
name|void
name|removeDocument
parameter_list|()
block|{
try|try
block|{
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
name|IndexQueryService
name|iqs
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
name|iqs
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
name|DOCUMENT_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|root
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XQueryService
name|qs
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|qs
operator|.
name|queryResource
argument_list|(
literal|"messages.xml"
argument_list|,
literal|"string(//event[last()]/@collection)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testCollection
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
annotation|@
name|Test
specifier|public
name|void
name|removeTriggers
parameter_list|()
block|{
try|try
block|{
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
name|IndexQueryService
name|iqs
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
name|iqs
operator|.
name|configureCollection
argument_list|(
name|EMPTY_COLLECTION_CONFIG
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
name|DOCUMENT_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XQueryService
name|qs
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|qs
operator|.
name|query
argument_list|(
literal|"if (doc-available('"
operator|+
name|testCollection
operator|+
literal|"/messages.xml')) then doc('"
operator|+
name|testCollection
operator|+
literal|"/messages.xml')/events/event[@id = 'STORE-DOCUMENT'] else ()"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No trigger should have fired. Configuration was removed"
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
annotation|@
name|Test
specifier|public
name|void
name|updateTriggers
parameter_list|()
block|{
try|try
block|{
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
name|IndexQueryService
name|iqs
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
name|iqs
operator|.
name|configureCollection
argument_list|(
name|EMPTY_COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|Collection
name|configCol
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
literal|null
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|configCol
operator|.
name|createResource
argument_list|(
literal|"collection.xconf"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|configCol
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
name|resource
operator|.
name|setContent
argument_list|(
name|DOCUMENT_CONTENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XQueryService
name|qs
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|qs
operator|.
name|query
argument_list|(
literal|"if (doc-available('"
operator|+
name|testCollection
operator|+
literal|"/messages.xml')) then doc('"
operator|+
name|testCollection
operator|+
literal|"/messages.xml')/events/event[@id = 'STORE-DOCUMENT']/string(@collection) else ()"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testCollection
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
specifier|private
name|void
name|printMessages
parameter_list|()
block|{
try|try
block|{
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
name|XMLResource
name|messages
init|=
operator|(
name|XMLResource
operator|)
name|root
operator|.
name|getResource
argument_list|(
literal|"messages.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|messages
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
annotation|@
name|After
specifier|public
name|void
name|cleanDB
parameter_list|()
block|{
try|try
block|{
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
literal|null
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
catch|catch
parameter_list|(
name|XMLDBException
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
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
parameter_list|()
block|{
comment|// initialize XML:DB driver
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
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
name|CollectionManagementService
name|mgmt
init|=
operator|(
name|CollectionManagementService
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
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
try|try
block|{
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
name|DatabaseInstanceManager
name|mgr
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
block|}
end_class

end_unit

