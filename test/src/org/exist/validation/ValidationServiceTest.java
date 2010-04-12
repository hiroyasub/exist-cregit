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
name|storage
operator|.
name|io
operator|.
name|ExistIOException
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
name|validation
operator|.
name|service
operator|.
name|ValidationService
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
name|Assert
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
name|Ignore
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
comment|/**  *  Tests for the Validation Service, e.g. used by InteractiveClient  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ValidationServiceTest
block|{
comment|//    private final static String URI = "xmldb:exist://" + DBBroker.ROOT_COLLECTION;
comment|//    private final static String DRIVER = "org.exist.xmldb.DatabaseImpl";
comment|//    private static String eXistHome = ConfigurationHelper.getExistHome().getAbsolutePath();
comment|//    private static CollectionManagementService cmservice = null;
comment|//    private static UserManagementService umservice = null;
comment|//    private static Collection root = null;
comment|//    private static ValidationService validationService = null;
comment|//    private static XPathQueryService xqservice;
comment|//    private static Database database = null;
comment|//
comment|//    public static void initLog4J() {
comment|//        Layout layout = new PatternLayout("%d [%t] %-5p (%F [%M]:%L) - %m %n");
comment|//        Appender appender = new ConsoleAppender(layout);
comment|//        BasicConfigurator.configure(appender);
comment|//    }
comment|//
comment|//    @BeforeClass
comment|//    public static void init() {
comment|//        initLog4J();
comment|//
comment|//        try {
comment|//            System.out.println(">>> setUp");
comment|//            Class<?> cl = Class.forName(DRIVER);
comment|//            database = (Database) cl.newInstance();
comment|//            database.setProperty("create-database", "true");
comment|//            DatabaseManager.registerDatabase(database);
comment|//            root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "guest", "guest");
comment|//            Assert.assertNotNull("Could not connect to database.");
comment|//            validationService = getValidationService();
comment|//
comment|//            xqservice = (XPathQueryService) root.getService("XQueryService", "1.0");
comment|//
comment|//            cmservice = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
comment|//            Collection col1 = cmservice.createCollection("/db/validation");
comment|//
comment|//            Permission permission = new UnixStylePermission("guest", "guest", 666);
comment|//
comment|//            umservice = (UserManagementService) root.getService("UserManagementService", "1.0");
comment|//            umservice.setPermissions(col1, permission);
comment|//
comment|//            String addressbook = eXistHome + "/samples/validation/addressbook";
comment|//
comment|//            TestTools.insertDocumentToURL(addressbook + "/addressbook_valid.xml",
comment|//                "xmldb:exist:///db/validation/addressbook_valid.xsd");
comment|//
comment|//            TestTools.insertDocumentToURL(addressbook + "/addressbook_invalid.xml",
comment|//                "xmldb:exist:///db/validation/addressbook_invalid.xsd");
comment|//
comment|//            TestTools.insertDocumentToURL(addressbook + "/catalog.xml",
comment|//                "xmldb:exist:///db/validation/catalog_xsd.xml");
comment|//
comment|//            TestTools.insertDocumentToURL(addressbook + "/addressbook.xsd",
comment|//                "xmldb:exist:///db/validation/addressbook.xsd");
comment|//
comment|//            System.out.println("<<<\n");
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    private static ValidationService getValidationService() {
comment|//        try {
comment|//            return (ValidationService) root.getService("ValidationService", "1.0");
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//        return null;
comment|//    }
comment|//
comment|//    // ===========================================================
comment|//    @Before
comment|//    public void clearGrammarCache() throws XMLDBException {
comment|//        System.out.println("Clearing grammar cache");
comment|//        @SuppressWarnings("unused")
comment|//        ResourceSet result = xqservice.query("validation:clear-grammar-cache()");
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testGetName() {
comment|//        System.out.println("testGetName");
comment|//        try {
comment|//            Assert.assertEquals("ValidationService check", validationService.getName(), "ValidationService");
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testGetVersion() {
comment|//        System.out.println("testGetVersion");
comment|//        try {
comment|//            Assert.assertEquals("ValidationService check", validationService.getVersion(), "1.0");
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testXsdValidDocument() {
comment|//        System.out.println("testXsdValidDocument");
comment|//        try {
comment|//            Assert.assertFalse("system catalog", validationService.validateResource("/db/validation/addressbook_valid.xml"));
comment|////            Assert.assertTrue("specified catalog", validationService.validateResource("/db/validation/addressbook_valid.xml",
comment|////                    "/db/validation/catalog_xsd.xml"));
comment|////            Assert.assertTrue("specified grammar", validationService.validateResource("/db/validation/addressbook_valid.xml",
comment|////                    "xmldb:///db/validation/addressbook.xsd"));
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testXsdInvalidDocument() {
comment|//        System.out.println("testXsdInvalidDocument");
comment|//        try {
comment|//            Assert.assertFalse("system catalog", validationService.validateResource("/db/validation/addressbook_invalid.xml"));
comment|//            Assert.assertFalse("specified catalog", validationService.validateResource("/db/validation/addressbook_invalid.xml",
comment|//                    "/db/validation/xsd/catalog.xml"));
comment|//            Assert.assertFalse("specified grammar", validationService.validateResource("/db/validation/addressbook_invalid.xml",
comment|//                    "/db/validation/xsd/addressbook.xsd"));
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testNonexistingDocument() {
comment|//        System.out.println("testNonexistingDocument");
comment|//        try {
comment|//            Assert.assertFalse("non existing document", validationService.validateResource(DBBroker.ROOT_COLLECTION + "/foobar.xml"));
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Ignore
comment|//    @Test
comment|//    public void testDtdValidDocument() {
comment|//        System.out.println("testDtdValidDocument");
comment|//        try {
comment|//            Assert.assertFalse("system catalog", validationService.validateResource("/db/validation/hamlet_valid.xml"));
comment|//            Assert.assertTrue("specified catalog", validationService.validateResource("/db/validation/hamlet_valid.xml",
comment|//                    "/db/validation/dtd/catalog.xml"));
comment|////            Assert.assertTrue( "specified grammar", service.validateResource("/db/validation/hamlet_valid.xml",
comment|////                "/db/validation/dtd/hamlet.dtd") );
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Ignore("cannot specify dtd as second parameter")
comment|//    @Test
comment|//    public void testDtdValidDocument2() {
comment|//        System.out.println("testDtdValidDocument");
comment|//        try {
comment|//            Assert.assertTrue("specified grammar", validationService.validateResource("/db/validation/hamlet_valid.xml",
comment|//                    "/db/validation/dtd/hamlet.dtd"));
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testDtdInvalidDocument() {
comment|//        System.out.println("testDtdInvalidDocument");
comment|//        try {
comment|//            Assert.assertFalse("system catalog", validationService.validateResource("/db/grammar/hamlet_invalid.xml"));
comment|//
comment|//            Assert.assertFalse("specified catalog", validationService.validateResource("/db/validation/hamlet_invalid.xml",
comment|//                    "/db/validation/dtd/catalog.xml"));
comment|//
comment|////            Assert.assertFalse( "specified grammar", service.validateResource("/db/validation/hamlet_invalid.xml",
comment|////                "/db/validation/dtd/hamlet.dtd") );
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testNoDoctype() {
comment|//        System.out.println("testNoDoctype");
comment|//        try {
comment|//            Assert.assertFalse("system catalog", validationService.validateResource("/db/validation/hamlet_nodoctype.xml"));
comment|//
comment|//            Assert.assertFalse("specified catalog", validationService.validateResource("/db/validation/hamlet_nodoctype.xml",
comment|//                    "/db/validation/dtd/catalog.xml"));
comment|//
comment|////            Assert.assertFalse( "specified grammar", service.validateResource("/db/validation/hamlet_nodoctype.xml",
comment|////                "/db/validation/dtd/hamlet.dtd") );
comment|//
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            Assert.fail(e.getMessage());
comment|//        }
comment|//    }
comment|//
comment|//    @Test
comment|//    public void testWrongDoctype() {
comment|//        System.out.println("testWrongDoctype");
comment|//        try {
comment|//            Assert.assertFalse("system catalog", validationService.validateResource("/db/validation/hamlet_wrongdoctype.xml"));
comment|//
comment|//            Assert.assertFalse("specified catalog", validationService.validateResource("/db/validation/hamlet_wrongdoctype.xml",
comment|//                    "/db/validation/dtd/catalog.xml"));
comment|//
comment|////            Assert.assertFalse( "specified grammar", service.validateResource("/db/validation/hamlet_wrongdoctype.xml",
comment|////                "/db/validation/dtd/hamlet.dtd") );
comment|//
comment|//        } catch (Exception e) {
comment|//
comment|//            if (e instanceof ExistIOException) {
comment|//                e.getCause().printStackTrace();
comment|//                Assert.fail(e.getCause().getMessage());
comment|//            } else {
comment|//                e.printStackTrace();
comment|//                Assert.fail(e.getMessage());
comment|//            }
comment|//        }
comment|//    }
comment|//
comment|//    @AfterClass
comment|//    public static void shutdown() throws Exception {
comment|//
comment|//        System.out.println("shutdown");
comment|//
comment|//         root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "admin", null);
comment|//
comment|//        DatabaseManager.deregisterDatabase(database);
comment|//        DatabaseInstanceManager dim =
comment|//                (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
comment|//        dim.shutdown();
comment|//
comment|//    }
annotation|@
name|Test
specifier|public
name|void
name|noTest
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

