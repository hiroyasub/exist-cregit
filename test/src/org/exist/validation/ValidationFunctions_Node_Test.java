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
name|ValidationFunctions_Node_Test
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
specifier|public
specifier|static
name|void
name|initLog4J
parameter_list|()
block|{
name|Layout
name|layout
init|=
operator|new
name|PatternLayout
argument_list|(
literal|"%d [%t] %-5p (%F [%M]:%L) - %m %n"
argument_list|)
decl_stmt|;
name|Appender
name|appender
init|=
operator|new
name|ConsoleAppender
argument_list|(
name|layout
argument_list|)
decl_stmt|;
name|BasicConfigurator
operator|.
name|configure
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
comment|// initialize driver
name|initLog4J
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start"
argument_list|)
expr_stmt|;
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
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|service
operator|=
operator|(
name|XPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|cmservice
operator|=
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
expr_stmt|;
name|Collection
name|col1
init|=
name|cmservice
operator|.
name|createCollection
argument_list|(
name|TestTools
operator|.
name|VALIDATION_HOME
argument_list|)
decl_stmt|;
name|Collection
name|col2
init|=
name|cmservice
operator|.
name|createCollection
argument_list|(
name|TestTools
operator|.
name|VALIDATION_XSD
argument_list|)
decl_stmt|;
name|Permission
name|permission
init|=
operator|new
name|UnixStylePermission
argument_list|(
literal|"guest"
argument_list|,
literal|"guest"
argument_list|,
literal|666
argument_list|)
decl_stmt|;
name|umservice
operator|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|umservice
operator|.
name|setPermissions
argument_list|(
name|col1
argument_list|,
name|permission
argument_list|)
expr_stmt|;
name|umservice
operator|.
name|setPermissions
argument_list|(
name|col2
argument_list|,
name|permission
argument_list|)
expr_stmt|;
name|String
name|addressbook
init|=
name|eXistHome
operator|+
literal|"/samples/validation/addressbook"
decl_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/addressbook.xsd"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_XSD
operator|+
literal|"/addressbook.xsd"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/catalog.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_XSD
operator|+
literal|"/catalog.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/addressbook_valid.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/addressbook_valid.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/addressbook_invalid.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/addressbook_invalid.xml"
argument_list|)
expr_stmt|;
block|}
comment|// ===========================================================
specifier|private
name|void
name|clearGrammarCache
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Clearing grammar cache"
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
name|storedNode
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"storedNode"
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|null
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"let $doc := doc('/db/validation/addressbook_valid.xml') "
operator|+
literal|"let $result := validation:validate( $doc, "
operator|+
literal|" xs:anyURI('/db/validation/xsd/addressbook.xsd') ) "
operator|+
literal|"return $result"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document as node"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"let $doc := doc('/db/validation/addressbook_invalid.xml') "
operator|+
literal|"let $result := validation:validate( $doc, "
operator|+
literal|" xs:anyURI('/db/validation/xsd/addressbook.xsd') ) "
operator|+
literal|"return $result"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document as node"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
name|constructedNode
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"constructedNode"
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|null
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"let $doc := "
operator|+
literal|"<addressBook xmlns=\"http://jmvanel.free.fr/xsd/addressBook\">"
operator|+
literal|"<owner><cname>John Punin</cname><email>puninj@cs.rpi.edu</email></owner>"
operator|+
literal|"<person><cname>Harrison Ford</cname><email>hford@famous.org</email></person>"
operator|+
literal|"<person><cname>Julia Roberts</cname><email>jr@pw.com</email></person>"
operator|+
literal|"</addressBook> "
operator|+
literal|"let $result := validation:validate( $doc, "
operator|+
literal|" xs:anyURI('/db/validation/xsd/addressbook.xsd') ) "
operator|+
literal|"return $result"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document as node"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"let $doc := "
operator|+
literal|"<addressBook xmlns=\"http://jmvanel.free.fr/xsd/addressBook\">"
operator|+
literal|"<owner1><cname>John Punin</cname><email>puninj@cs.rpi.edu</email></owner1>"
operator|+
literal|"<person><cname>Harrison Ford</cname><email>hford@famous.org</email></person>"
operator|+
literal|"<person><cname>Julia Roberts</cname><email>jr@pw.com</email></person>"
operator|+
literal|"</addressBook> "
operator|+
literal|"let $result := validation:validate( $doc, "
operator|+
literal|" xs:anyURI('/db/validation/xsd/addressbook.xsd') ) "
operator|+
literal|"return $result"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document as node"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
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
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

