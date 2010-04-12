begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

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
name|apache
operator|.
name|log4j
operator|.
name|Appender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|BasicConfigurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|ConsoleAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Layout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|PatternLayout
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
name|Permission
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
name|UnixStylePermission
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
name|util
operator|.
name|ConfigurationHelper
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
name|UserManagementService
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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  *  Set of Tests for validation:validate($a) and validation:validate($a, $b)  * regaring validatin using XSD's.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ValidationFunctions_XSD_Test
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ValidationFunctions_XSD_Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|eXistHome
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|CollectionManagementService
name|cmservice
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|UserManagementService
name|umservice
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|XPathQueryService
name|service
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Database
name|database
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|noTest
parameter_list|()
block|{
block|}
comment|//    public static void initLog4J(){
comment|//        Layout layout = new PatternLayout("%d [%t] %-5p (%F [%M]:%L) - %m %n");
comment|//        Appender appender=new ConsoleAppender(layout);
comment|//        BasicConfigurator.configure(appender);
comment|//    }
comment|//
comment|//    @BeforeClass
comment|//    public static void setUp() throws Exception {
comment|//
comment|//        // initialize driver
comment|//        initLog4J();
comment|//
comment|//        logger.info("setUp");
comment|//
comment|//        Class<?> cl = Class.forName("org.exist.xmldb.DatabaseImpl");
comment|//        database = (Database) cl.newInstance();
comment|//        database.setProperty("create-database", "true");
comment|//        DatabaseManager.registerDatabase(database);
comment|//        root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "guest", "guest");
comment|//        service = (XPathQueryService) root.getService( "XQueryService", "1.0" );
comment|//
comment|//        cmservice = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
comment|//        Collection col1 = cmservice.createCollection(TestTools.VALIDATION_HOME);
comment|//        Collection col2 = cmservice.createCollection(TestTools.VALIDATION_XSD);
comment|//
comment|//        Permission permission = new UnixStylePermission("guest", "guest", 666);
comment|//
comment|//        umservice = (UserManagementService) root.getService("UserManagementService", "1.0");
comment|//        umservice.setPermissions(col1, permission);
comment|//        umservice.setPermissions(col2, permission);
comment|//
comment|//        String addressbook = eXistHome + "/samples/validation/addressbook";
comment|//
comment|//        TestTools.insertDocumentToURL(addressbook + "/addressbook.xsd",
comment|//                "xmldb:exist://" + TestTools.VALIDATION_XSD + "/addressbook.xsd");
comment|//        TestTools.insertDocumentToURL(addressbook + "/catalog.xml",
comment|//                "xmldb:exist://" + TestTools.VALIDATION_XSD + "/catalog.xml");
comment|//
comment|//        TestTools.insertDocumentToURL(addressbook + "/addressbook_valid.xml",
comment|//                "xmldb:exist://" + TestTools.VALIDATION_HOME + "/addressbook_valid.xml");
comment|//        TestTools.insertDocumentToURL(addressbook + "/addressbook_invalid.xml",
comment|//                "xmldb:exist://" + TestTools.VALIDATION_HOME + "/addressbook_invalid.xml");
comment|//    }
comment|//
comment|//    // ===========================================================
comment|//
comment|//    private void clearGrammarCache() {
comment|//        logger.info("Clearing grammar cache");
comment|//        @SuppressWarnings("unused")
comment|//		ResourceSet result = null;
comment|//        try {
comment|//            result = service.query("validation:clear-grammar-cache()");
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testXSD_NotInSystemCatalog() {
comment|//
comment|//        logger.info("start");
comment|//
comment|//        clearGrammarCache();
comment|//
comment|//        ResourceSet result = null;
comment|//        String r = null;
comment|//        try {
comment|//            // XSD for addressbook_valid.xml is *not* registered in system catalog.
comment|//            // result should be "document is invalid"
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals( "addressbook_valid.xml not in systemcatalog", "false", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testXSD_SpecifiedCatalog() {
comment|//
comment|//        logger.info("start");
comment|//
comment|//        clearGrammarCache();
comment|//
comment|//        ResourceSet result = null;
comment|//        String r = null;
comment|//        try {
comment|//            logger.info("Test1");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml'), "
comment|//                +" xs:anyURI('/db/validation/xsd/catalog.xml') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("valid document", "true", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test2");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_invalid.xml'), "
comment|//                +" xs:anyURI('/db/validation/xsd/catalog.xml') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals( "invalid document", "false", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test3");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml'), "
comment|//                +" xs:anyURI('/db/validation/dtd/catalog.xml') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("wrong catalog", "false", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test4");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_invalid.xml'),"
comment|//                +" xs:anyURI('/db/validation/dtd/catalog.xml') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("wrong catalog, invalid document", "false", r );
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testXSD_SpecifiedGrammar() {
comment|//
comment|//        logger.info("start");
comment|//
comment|//        clearGrammarCache();
comment|//
comment|//        ResourceSet result = null;
comment|//        String r = null;
comment|//        try {
comment|//            logger.info("Test1");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml'), "
comment|//                +" xs:anyURI('/db/validation/xsd/addressbook.xsd') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("valid document", "true", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test2");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_invalid.xml'), "
comment|//                +" xs:anyURI('/db/validation/xsd/addressbook.xsd') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals( "invalid document", "false", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testXSD_SearchedGrammar() {
comment|//
comment|//        logger.info("start");
comment|//
comment|//        clearGrammarCache();
comment|//
comment|//        ResourceSet result = null;
comment|//        String r = null;
comment|//        try {
comment|//
comment|//            logger.info("Test1");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml'), "
comment|//                +" xs:anyURI('/db/validation/xsd/') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("valid document", "true", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test2");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml'), "
comment|//                +" xs:anyURI('/db/validation/dtd/') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals( "valid document, not found", "false", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test3");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_valid.xml'), "
comment|//                +" xs:anyURI('/db/validation/') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("valid document", "true", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//            logger.info("Test4");
comment|//            result = service.query(
comment|//                "validation:validate( xs:anyURI('/db/validation/addressbook_invalid.xml') ,"
comment|//                +" xs:anyURI('/db/validation/') )");
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals( "invalid document", "false", r );
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    // DTDs
comment|//
comment|//    @AfterClass
comment|//    public static void stop() throws Exception {
comment|//
comment|//        logger.info("stop");
comment|//
comment|//        root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "admin", null);
comment|//
comment|//        DatabaseManager.deregisterDatabase(database);
comment|//        DatabaseInstanceManager dim =
comment|//            (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
comment|//        dim.shutdown();
comment|//
comment|//    }
block|}
end_class

end_unit

