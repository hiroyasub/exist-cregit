begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: ValidationFunctions_XSD_Test.java 5941 2007-05-29 20:27:59Z dizzzz $  */
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
name|ValidationFunctions_Node_Test
block|{
annotation|@
name|Test
specifier|public
name|void
name|noTest
parameter_list|()
block|{
block|}
comment|//    private final static Logger logger = Logger.getLogger(ValidationFunctions_Node_Test.class);
comment|//
comment|//    private static String eXistHome = ConfigurationHelper.getExistHome().getAbsolutePath();
comment|//
comment|//    private static CollectionManagementService  cmservice = null;
comment|//    private static UserManagementService  umservice = null;
comment|//    private static XPathQueryService service;
comment|//    private static Collection root = null;
comment|//    private static Database database = null;
comment|//
comment|//
comment|//
comment|//
comment|//    public static void initLog4J() {
comment|//        Layout layout = new PatternLayout("%d [%t] %-5p (%F [%M]:%L) - %m %n");
comment|//        Appender appender = new ConsoleAppender(layout);
comment|//        BasicConfigurator.configure(appender);
comment|//    }
comment|//
comment|//    @BeforeClass
comment|//    public static void start() throws Exception {
comment|//
comment|//        // initialize driver
comment|//        initLog4J();
comment|//        logger.info("start");
comment|//
comment|//        Class<?> cl = Class.forName("org.exist.xmldb.DatabaseImpl");
comment|//        database = (Database) cl.newInstance();
comment|//        database.setProperty("create-database", "true");
comment|//        DatabaseManager.registerDatabase(database);
comment|//        root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "guest", "guest");
comment|//        service = (XPathQueryService) root.getService("XQueryService", "1.0");
comment|//
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
comment|//    public void storedNode() {
comment|//
comment|//        logger.info("storedNode");
comment|//
comment|//        clearGrammarCache();
comment|//
comment|//        String query = null;
comment|//        ResourceSet result = null;
comment|//        String r = null;
comment|//
comment|//        try {
comment|//            logger.info("Test1");
comment|//            query = "let $doc := doc('/db/validation/addressbook_valid.xml') " +
comment|//                    "let $result := validation:validate( $doc, " +
comment|//                    " xs:anyURI('/db/validation/xsd/addressbook.xsd') ) " +
comment|//                    "return $result";
comment|//            result = service.query(query);
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("valid document as node", "true", r);
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//
comment|//        try {
comment|//            logger.info("Test2");
comment|//
comment|//            query = "let $doc := doc('/db/validation/addressbook_invalid.xml') " +
comment|//                    "let $result := validation:validate( $doc, " +
comment|//                    " xs:anyURI('/db/validation/xsd/addressbook.xsd') ) " +
comment|//                    "return $result";
comment|//            result = service.query(query);
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("invalid document as node", "false", r);
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
comment|//    public void constructedNode() {
comment|//
comment|//        logger.info("constructedNode");
comment|//
comment|//        clearGrammarCache();
comment|//
comment|//        String query = null;
comment|//        ResourceSet result = null;
comment|//        String r = null;
comment|//
comment|//        try {
comment|//            logger.info("Test1");
comment|//
comment|//            query = "let $doc := " +
comment|//                    "<addressBook xmlns=\"http://jmvanel.free.fr/xsd/addressBook\">" +
comment|//                    "<owner><cname>John Punin</cname><email>puninj@cs.rpi.edu</email></owner>" +
comment|//                    "<person><cname>Harrison Ford</cname><email>hford@famous.org</email></person>" +
comment|//                    "<person><cname>Julia Roberts</cname><email>jr@pw.com</email></person>" +
comment|//                    "</addressBook> " +
comment|//                    "let $result := validation:validate( $doc, " +
comment|//                    " xs:anyURI('/db/validation/xsd/addressbook.xsd') ) " +
comment|//                    "return $result";
comment|//            result = service.query(query);
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("valid document as node", "true", r);
comment|//
comment|//            clearGrammarCache();
comment|//
comment|//        } catch (Exception e) {
comment|//            logger.error(e);
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
comment|//
comment|//        try {
comment|//            logger.info("Test2");
comment|//
comment|//            query = "let $doc := " +
comment|//                    "<addressBook xmlns=\"http://jmvanel.free.fr/xsd/addressBook\">" +
comment|//                    "<owner1><cname>John Punin</cname><email>puninj@cs.rpi.edu</email></owner1>" +
comment|//                    "<person><cname>Harrison Ford</cname><email>hford@famous.org</email></person>" +
comment|//                    "<person><cname>Julia Roberts</cname><email>jr@pw.com</email></person>" +
comment|//                    "</addressBook> " +
comment|//                    "let $result := validation:validate( $doc, " +
comment|//                    " xs:anyURI('/db/validation/xsd/addressbook.xsd') ) " +
comment|//                    "return $result";
comment|//            result = service.query(query);
comment|//            r = (String) result.getResource(0).getContent();
comment|//            assertEquals("invalid document as node", "false", r);
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
comment|//    @AfterClass
comment|//    public static void shutdown() throws Exception {
comment|//
comment|//        logger.info("shutdown");
comment|//
comment|//
comment|//        root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "admin", null);
comment|//
comment|//
comment|//        DatabaseManager.deregisterDatabase(database);
comment|//        DatabaseInstanceManager dim =
comment|//                (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
comment|//        dim.shutdown();
comment|//
comment|//    }
block|}
end_class

end_unit

