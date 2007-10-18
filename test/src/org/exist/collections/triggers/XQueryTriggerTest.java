begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-05 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. */
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
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLAssert
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
name|xmldb
operator|.
name|EXistResource
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|ResourceSet
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_comment
comment|/** class under test : {@link XQueryTrigger}  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|XQueryTriggerTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testXQueryTrigger"
decl_stmt|;
specifier|private
specifier|final
name|String
name|COLLECTION_CONFIG
init|=
literal|"<exist:collection xmlns:exist='http://exist-db.org/collection-config/1.0'>"
operator|+
literal|"<exist:triggers>"
operator|+
literal|"<exist:trigger event='store'"
operator|+
literal|"                    class='org.exist.collections.triggers.XQueryTrigger'>"
operator|+
literal|"<exist:parameter name='query' "
operator|+
literal|"			value=\"import module namespace log = 'log' at '"
operator|+
name|URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|MODULE_NAME
operator|+
literal|"';"
operator|+
literal|"log:log('trigger1')\" />"
operator|+
literal|"<exist:parameter name='bindingPrefix' value='log'/>"
operator|+
literal|"        />"
operator|+
literal|"</exist:trigger>"
operator|+
literal|"<exist:trigger event='update'"
operator|+
literal|"                    class='org.exist.collections.triggers.XQueryTrigger'>"
operator|+
literal|"<exist:parameter name='query' "
operator|+
literal|"			value=\"import module namespace log = 'log' at '"
operator|+
name|URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|MODULE_NAME
operator|+
literal|"';"
operator|+
literal|"log:log('trigger2')\" />"
operator|+
literal|"<exist:parameter name=\"bindingPrefix\" value=\"log\"/>"
operator|+
literal|"        />"
operator|+
literal|"</exist:trigger>"
operator|+
literal|"<exist:trigger event='remove'"
operator|+
literal|"                    class='org.exist.collections.triggers.XQueryTrigger'>"
operator|+
literal|"<exist:parameter name=\"query\" value=\"import module namespace log = 'log' at '"
operator|+
name|URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|MODULE_NAME
operator|+
literal|"';"
operator|+
literal|"log:log('trigger3')\"/>"
operator|+
literal|"<exist:parameter name='bindingPrefix' value='log' />"
operator|+
literal|"        />"
operator|+
literal|"</exist:trigger>"
operator|+
literal|"</exist:triggers>"
operator|+
literal|"</exist:collection>"
decl_stmt|;
specifier|private
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
name|DOCUMENT_NAME
init|=
literal|"test.xml"
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
comment|/** XUpdate document update specification */
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT_UPDATE
init|=
literal|"<xu:modifications xmlns:xu='http://www.xmldb.org/xupdate' version='1.0'>"
operator|+
literal|"<!-- special offer -->"
operator|+
literal|"<xu:update select='/test/item[@id = \"3\"]/price'>"
operator|+
literal|"15.2"
operator|+
literal|"</xu:update>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MODIFIED_DOCUMENT_CONTENT
init|=
name|DOCUMENT_CONTENT
operator|.
name|replaceAll
argument_list|(
literal|"<price>18.4</price>"
argument_list|,
literal|"<price>15.2</price>"
argument_list|)
decl_stmt|;
comment|/** "log" document that will be updated by the trigger */
specifier|private
specifier|final
specifier|static
name|String
name|LOG_NAME
init|=
literal|"XQueryTriggerLog.xml"
decl_stmt|;
comment|/** initial content of the "log" document */
specifier|private
specifier|final
specifier|static
name|String
name|EMPTY_LOG
init|=
literal|"<events/>"
decl_stmt|;
comment|/** XQuery module implementing the trigger under test */
specifier|private
specifier|final
specifier|static
name|String
name|MODULE_NAME
init|=
literal|"XQueryTriggerLogger.xqm"
decl_stmt|;
comment|/** XQuery module implementing the trigger under test;       * the log() XQuery function will add an<event> element inside<events> element */
specifier|private
specifier|final
specifier|static
name|String
name|MODULE
init|=
literal|"module namespace log='log'; "
operator|+
literal|"import module namespace xmldb='http://exist-db.org/xquery/xmldb'; "
operator|+
literal|"declare variable $log:eventType external;"
operator|+
literal|"declare variable $log:collectionName external;"
operator|+
literal|"declare variable $log:documentName external;"
operator|+
literal|"declare variable $log:triggerEvent external;"
operator|+
literal|"declare variable $log:document external;"
operator|+
literal|"declare function log:log($id as xs:string?) {"
operator|+
literal|"let $isLoggedIn := xmldb:login('"
operator|+
name|URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"', 'admin', '') return "
operator|+
literal|"xmldb:update("
operator|+
literal|"'"
operator|+
name|URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"', "
operator|+
literal|"<xu:modifications xmlns:xu='http://www.xmldb.org/xupdate' version='1.0'>"
operator|+
literal|"<xu:append select='/events'>"
operator|+
literal|"<xu:element name='event'>"
operator|+
literal|"<xu:attribute name='id'>{$id}</xu:attribute>"
operator|+
literal|"<xu:attribute name='time'>{current-dateTime()}</xu:attribute>"
operator|+
literal|"<xu:attribute name='type'>{$log:eventType}</xu:attribute>"
operator|+
literal|"<xu:element name='collectionName'>{$log:collectionName}</xu:element>"
operator|+
literal|"<xu:element name='documentName'>{$log:documentName}</xu:element>"
operator|+
literal|"<xu:element name='triggerEvent'>{$log:triggerEvent}</xu:element>"
operator|+
literal|"<xu:element name='document'>{$log:document}</xu:element>"
operator|+
literal|"</xu:element>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
operator|+
literal|")"
operator|+
literal|"};"
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|testCollection
decl_stmt|;
comment|/** just start the DB and create the test collection */
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
block|{
try|try
block|{
comment|// initialize driver
name|Class
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
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
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
name|testCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
catch|catch
parameter_list|(
name|InstantiationException
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
catch|catch
parameter_list|(
name|IllegalAccessException
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
name|AfterClass
specifier|public
specifier|static
name|void
name|shutdownDB
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
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
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
name|service
operator|.
name|removeCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|testCollection
operator|=
literal|null
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
comment|/** create "log" document that will be updated by the trigger,      * and store the XQuery module implementing the trigger under test */
annotation|@
name|Before
specifier|public
name|void
name|storePreliminaryDocuments
parameter_list|()
block|{
try|try
block|{
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|LOG_NAME
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|EMPTY_LOG
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|BinaryResource
name|module
init|=
operator|(
name|BinaryResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|MODULE_NAME
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|module
operator|)
operator|.
name|setMimeType
argument_list|(
literal|"application/xquery"
argument_list|)
expr_stmt|;
name|module
operator|.
name|setContent
argument_list|(
name|MODULE
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|module
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
comment|/** test a trigger fired by storing a Document  */
annotation|@
name|Test
specifier|public
name|void
name|storeDocument
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|// configure the Collection with the trigger under test
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|testCollection
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
comment|// this will fire the trigger
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|DOCUMENT_NAME
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|DOCUMENT_CONTENT
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// remove the trigger for the Collection under test
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|EMPTY_COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|//TODO : understand why it is necessary !
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger1']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger1'][collectionName = '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger1'][documentName = '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|DOCUMENT_NAME
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger1'][triggerEvent = 'STORE']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger1']/document/test"
argument_list|)
expr_stmt|;
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
name|assertXMLEqual
argument_list|(
name|DOCUMENT_CONTENT
argument_list|,
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|)
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
comment|/** test a trigger fired by a Document Update */
annotation|@
name|Test
specifier|public
name|void
name|updateDocument
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|testCollection
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
comment|//TODO : trigger UPDATE events !
name|XUpdateQueryService
name|update
init|=
operator|(
name|XUpdateQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|update
operator|.
name|updateResource
argument_list|(
name|DOCUMENT_NAME
argument_list|,
name|DOCUMENT_UPDATE
argument_list|)
expr_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|EMPTY_COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// this is necessary to compare with MODIFIED_DOCUMENT_CONTENT ; TODO better compare with XML diff tool
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger2']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger2'][collectionName = '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger2'][documentName = '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|DOCUMENT_NAME
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger2'][triggerEvent = 'UPDATE']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger2']/document/test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertXMLEqual
argument_list|(
name|DOCUMENT_CONTENT
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
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertXMLEqual
argument_list|(
name|MODIFIED_DOCUMENT_CONTENT
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
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
comment|/** test a trigger fired by a Document Delete */
annotation|@
name|Test
specifier|public
name|void
name|deleteDocument
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|testCollection
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
name|testCollection
operator|.
name|removeResource
argument_list|(
name|testCollection
operator|.
name|getResource
argument_list|(
name|DOCUMENT_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|EMPTY_COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger3']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger3'][collectionName = '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger3'][documentName = '"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|DOCUMENT_NAME
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger3'][triggerEvent = 'REMOVE']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : consistent URI !
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"/events/event[@id = 'trigger3']/document/test"
argument_list|)
expr_stmt|;
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
name|assertXMLEqual
argument_list|(
name|MODIFIED_DOCUMENT_CONTENT
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
operator|.
name|toString
argument_list|()
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
block|}
end_class

end_unit

