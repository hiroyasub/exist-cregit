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
name|storage
operator|.
name|DBBroker
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
name|XPathQueryService
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

begin_comment
comment|/**  *  Switch validation mode yes/no/auto per collection and validate.  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|CollectionConfigurationValidationModeTest
block|{
name|String
name|valid
init|=
literal|"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://jmvanel.free.fr/xsd/addressBook\" elementFormDefault=\"qualified\">"
operator|+
literal|"<xsd:attribute name=\"uselessAttribute\" type=\"xsd:string\"/>"
operator|+
literal|"<xsd:complexType name=\"record\">"
operator|+
literal|"<xsd:sequence>"
operator|+
literal|"<xsd:element name=\"cname\" type=\"xsd:string\"/>"
operator|+
literal|"<xsd:element name=\"email\" type=\"xsd:string\"/>"
operator|+
literal|"</xsd:sequence>"
operator|+
literal|"</xsd:complexType>"
operator|+
literal|"<xsd:element name=\"addressBook\">"
operator|+
literal|"<xsd:complexType>"
operator|+
literal|"<xsd:sequence>"
operator|+
literal|"<xsd:element name=\"owner\" type=\"record\"/>"
operator|+
literal|"<xsd:element name=\"person\" type=\"record\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>"
operator|+
literal|"</xsd:sequence>"
operator|+
literal|"</xsd:complexType>"
operator|+
literal|"</xsd:element>"
operator|+
literal|"</xsd:schema>"
decl_stmt|;
name|String
name|invalid
init|=
literal|"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://jmvanel.free.fr/xsd/addressBook\" elementFormDefault=\"qualified\">"
operator|+
literal|"<xsd:attribute name=\"uselessAttribute\" type=\"xsd:string\"/>"
operator|+
literal|"<xsd:complexType name=\"record\">"
operator|+
literal|"<xsd:sequence>"
operator|+
literal|"<xsd:elementa name=\"cname\" type=\"xsd:string\"/>"
operator|+
literal|"<xsd:elementb name=\"email\" type=\"xsd:string\"/>"
operator|+
literal|"</xsd:sequence>"
operator|+
literal|"</xsd:complexType>"
operator|+
literal|"<xsd:element name=\"addressBook\">"
operator|+
literal|"<xsd:complexType>"
operator|+
literal|"<xsd:sequence>"
operator|+
literal|"<xsd:element name=\"owner\" type=\"record\"/>"
operator|+
literal|"<xsd:element name=\"person\" type=\"record\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>"
operator|+
literal|"</xsd:sequence>"
operator|+
literal|"</xsd:complexType>"
operator|+
literal|"</xsd:element>"
operator|+
literal|"</xsd:schema>"
decl_stmt|;
name|String
name|anonymous
init|=
literal|"<schema elementFormDefault=\"qualified\">"
operator|+
literal|"<attribute name=\"uselessAttribute\" type=\"string\"/>"
operator|+
literal|"<complexType name=\"record\">"
operator|+
literal|"<sequence>"
operator|+
literal|"<elementa name=\"cname\" type=\"string\"/>"
operator|+
literal|"<elementb name=\"email\" type=\"string\"/>"
operator|+
literal|"</sequence>"
operator|+
literal|"</complexType>"
operator|+
literal|"<element name=\"addressBook\">"
operator|+
literal|"<complexType>"
operator|+
literal|"<sequence>"
operator|+
literal|"<element name=\"owner\" type=\"record\"/>"
operator|+
literal|"<element name=\"person\" type=\"record\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>"
operator|+
literal|"</sequence>"
operator|+
literal|"</complexType>"
operator|+
literal|"</element>"
operator|+
literal|"</schema>"
decl_stmt|;
name|String
name|different
init|=
literal|"<asd:schema xmlns:asd=\"http://www.w3.org/2001/XMLSchemaschema\" targetNamespace=\"http://jmvanel.free.fr/xsd/addressBookbook\" elementFormDefault=\"qualified\">"
operator|+
literal|"<asd:attribute name=\"uselessAttribute\" type=\"asd:string\"/>"
operator|+
literal|"<asd:complexType name=\"record\">"
operator|+
literal|"<asd:sequence>"
operator|+
literal|"<asd:element name=\"cname\" type=\"asd:string\"/>"
operator|+
literal|"<asd:element name=\"email\" type=\"asd:string\"/>"
operator|+
literal|"</asd:sequence>"
operator|+
literal|"</asd:complexType>"
operator|+
literal|"<asd:element name=\"addressBook\">"
operator|+
literal|"<asd:complexType>"
operator|+
literal|"<asd:sequence>"
operator|+
literal|"<asd:element name=\"owner\" type=\"record\"/>"
operator|+
literal|"<asd:element name=\"person\" type=\"record\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>"
operator|+
literal|"</asd:sequence>"
operator|+
literal|"</asd:complexType>"
operator|+
literal|"</asd:element>"
operator|+
literal|"</asd:schema>"
decl_stmt|;
name|String
name|xconf_yes
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\"><validation mode=\"yes\"/></collection>"
decl_stmt|;
name|String
name|xconf_no
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\"><validation mode=\"no\"/></collection>"
decl_stmt|;
name|String
name|xconf_auto
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\"><validation mode=\"auto\"/></collection>"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|CollectionConfigurationValidationModeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XPathQueryService
name|xpqservice
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
specifier|private
specifier|static
name|CollectionManagementService
name|cmservice
init|=
literal|null
decl_stmt|;
specifier|public
name|CollectionConfigurationValidationModeTest
parameter_list|()
block|{
block|}
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
name|setUpClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initLog4J
argument_list|()
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
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|xpqservice
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
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Clear grammar cache"
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
name|xpqservice
operator|.
name|query
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Clear grammar cache"
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
name|xpqservice
operator|.
name|query
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
block|}
specifier|private
name|void
name|createCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"createCollection="
operator|+
name|collection
argument_list|)
expr_stmt|;
name|Collection
name|testCollection
init|=
name|cmservice
operator|.
name|createCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|testCollection
operator|=
name|cmservice
operator|.
name|createCollection
argument_list|(
literal|"/db/system/config"
operator|+
name|collection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeCollectionXconf
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|document
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"storeCollectionXconf="
operator|+
name|collection
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|xpqservice
operator|.
name|query
argument_list|(
literal|"xmldb:store(\""
operator|+
name|collection
operator|+
literal|"\", \"collection.xconf\", "
operator|+
name|document
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Store xconf"
argument_list|,
name|collection
operator|+
literal|"/collection.xconf"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeDocument
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|document
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"storeDocument="
operator|+
name|collection
operator|+
literal|" "
operator|+
name|name
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|xpqservice
operator|.
name|query
argument_list|(
literal|"xmldb:store(\""
operator|+
name|collection
operator|+
literal|"\", \""
operator|+
name|name
operator|+
literal|"\", "
operator|+
name|document
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Store doc"
argument_list|,
name|collection
operator|+
literal|"/"
operator|+
name|name
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertModeFalse
parameter_list|()
block|{
try|try
block|{
name|createCollection
argument_list|(
literal|"/db/false"
argument_list|)
expr_stmt|;
name|storeCollectionXconf
argument_list|(
literal|"/db/system/config/db/false"
argument_list|,
name|xconf_no
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// namespace provided, valid document; should pass
name|storeDocument
argument_list|(
literal|"/db/false"
argument_list|,
literal|"valid.xml"
argument_list|,
name|valid
argument_list|)
expr_stmt|;
comment|// namespace provided, invalid document; should pass
name|storeDocument
argument_list|(
literal|"/db/false"
argument_list|,
literal|"invalid.xml"
argument_list|,
name|invalid
argument_list|)
expr_stmt|;
comment|// no namespace provided, should pass
name|storeDocument
argument_list|(
literal|"/db/false"
argument_list|,
literal|"anonymous.xml"
argument_list|,
name|anonymous
argument_list|)
expr_stmt|;
comment|// non resolvable namespace provided, should pass
name|storeDocument
argument_list|(
literal|"/db/false"
argument_list|,
literal|"different.xml"
argument_list|,
name|different
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
name|ex
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
name|insertModeTrue
parameter_list|()
block|{
comment|// namespace provided, valid document; should pass
try|try
block|{
name|createCollection
argument_list|(
literal|"/db/true"
argument_list|)
expr_stmt|;
name|storeCollectionXconf
argument_list|(
literal|"/db/system/config/db/true"
argument_list|,
name|xconf_yes
argument_list|)
expr_stmt|;
name|storeDocument
argument_list|(
literal|"/db/true"
argument_list|,
literal|"valid.xml"
argument_list|,
name|valid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// namespace provided, invalid document; should fail
try|try
block|{
name|storeDocument
argument_list|(
literal|"/db/true"
argument_list|,
literal|"invalid.xml"
argument_list|,
name|invalid
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|String
name|msg
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|contains
argument_list|(
literal|"cvc-complex-type.2.4.a: Invalid content was found"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no namespace provided; should fail
try|try
block|{
name|storeDocument
argument_list|(
literal|"/db/true"
argument_list|,
literal|"anonymous.xml"
argument_list|,
name|anonymous
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|String
name|msg
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|contains
argument_list|(
literal|"Cannot find the declaration of element 'schema'."
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|// non resolvable namespace provided, should fail
try|try
block|{
name|storeDocument
argument_list|(
literal|"/db/true"
argument_list|,
literal|"different.xml"
argument_list|,
name|different
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|String
name|msg
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|contains
argument_list|(
literal|"Cannot find the declaration of element 'asd:schema'."
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertModeAuto
parameter_list|()
block|{
comment|// namespace provided, valid document; should pass
try|try
block|{
name|createCollection
argument_list|(
literal|"/db/auto"
argument_list|)
expr_stmt|;
name|storeCollectionXconf
argument_list|(
literal|"/db/system/config/db/auto"
argument_list|,
name|xconf_auto
argument_list|)
expr_stmt|;
name|storeDocument
argument_list|(
literal|"/db/auto"
argument_list|,
literal|"valid.xml"
argument_list|,
name|valid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// namespace provided, invalid document, should fail
try|try
block|{
name|storeDocument
argument_list|(
literal|"/db/auto"
argument_list|,
literal|"invalid.xml"
argument_list|,
name|invalid
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|String
name|msg
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|contains
argument_list|(
literal|"cvc-complex-type.2.4.a: Invalid content was found"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no namespace reference, should pass
try|try
block|{
name|storeDocument
argument_list|(
literal|"/db/auto"
argument_list|,
literal|"anonymous.xml"
argument_list|,
name|anonymous
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|String
name|msg
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|contains
argument_list|(
literal|"Cannot find the declaration of element 'schema'."
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|// non resolvable namespace provided, should fail
try|try
block|{
name|storeDocument
argument_list|(
literal|"/db/auto"
argument_list|,
literal|"different.xml"
argument_list|,
name|different
argument_list|)
expr_stmt|;
comment|//            fail("I expected a failure here. to be checked by DIZZZZ");
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|String
name|msg
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

