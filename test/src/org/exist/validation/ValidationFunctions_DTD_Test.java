begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|util
operator|.
name|LockException
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
name|BasicConfigurator
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
name|security
operator|.
name|Subject
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
name|util
operator|.
name|XMLReaderObjectFactory
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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  *  Set of Tests for validation:validate($a) and validation:validate($a, $b)  * regaring validatin using DTD's.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ValidationFunctions_DTD_Test
block|{
specifier|private
specifier|static
name|Configuration
name|config
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testValidationFunctionsDTD"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ADMIN_UID
init|=
literal|"admin"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ADMIN_PWD
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|GUEST_UID
init|=
literal|"guest"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALIDATION_HOME_COLLECTION_URI
init|=
literal|"/db/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME_COLLECTION
decl_stmt|;
specifier|private
specifier|static
name|XPathQueryService
name|service
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALIDATION_DTD_COLLECTION_URI
init|=
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_DTD_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALIDATION_XSD_COLLECTION_URI
init|=
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_XSD_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALIDATION_TMP_COLLECTION_URI
init|=
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP_COLLECTION
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|validateUsingSystemCatalog
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|// DTD for hamlet_valid.xml is registered in system catalog.
comment|// result should be "document is valid"
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|VALIDATION_TMP_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml'))"
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
literal|"hamlet_valid.xml in systemcatalog"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedCatalog_test1
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml') ,"
operator|+
literal|" xs:anyURI('"
operator|+
name|VALIDATION_DTD_COLLECTION_URI
operator|+
literal|"/catalog.xml'))"
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
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedCatalog_test2
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_invalid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_DTD_COLLECTION_URI
operator|+
literal|"/catalog.xml'))"
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
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedCatalog_test3
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_XSD_COLLECTION_URI
operator|+
literal|"/catalog.xml'))"
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
literal|"wrong catalog"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedCatalog_test4
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_invalid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_XSD_COLLECTION_URI
operator|+
literal|"/catalog.xml'))"
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
literal|"wrong catalog, invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedGrammar_dtd_forValidDoc
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_DTD_COLLECTION_URI
operator|+
literal|"/hamlet.dtd'))"
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
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedGrammar_dtd_forInvalidDoc
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_invalid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_DTD_COLLECTION_URI
operator|+
literal|"/hamlet.dtd') )"
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
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|searchedGrammar_valid_dtd
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_DTD_COLLECTION_URI
operator|+
literal|"/'))"
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
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|searchedGrammar_valid_xsd
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml'), xs:anyURI('"
operator|+
name|VALIDATION_XSD_COLLECTION_URI
operator|+
literal|"/') )"
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
literal|"valid document, not found"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|searchedGrammar_valid
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_valid.xml'), xs:anyURI('/db/'))"
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
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|searchedGrammar_invalid
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate(xs:anyURI('"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/hamlet_invalid.xml'), xs:anyURI('/db/'))"
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
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|clearGrammarCache
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|service
operator|.
name|query
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startup
parameter_list|()
throws|throws
name|Exception
block|{
name|BasicConfigurator
operator|.
name|configure
argument_list|()
expr_stmt|;
name|config
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"auto"
argument_list|)
expr_stmt|;
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
comment|//create the collections we need for these tests
name|createTestCollections
argument_list|()
expr_stmt|;
comment|//create the documents we need for the tests
name|createTestDocuments
argument_list|()
expr_stmt|;
comment|//get xmldb xpath query service
name|service
operator|=
name|getXPathService
argument_list|()
expr_stmt|;
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
name|removeTestCollections
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|XPathQueryService
name|getXPathService
parameter_list|()
throws|throws
name|Exception
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
return|return
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
return|;
block|}
specifier|private
specifier|static
name|void
name|createTestCollections
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Subject
name|admin
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
comment|/** create nessecary collections if they dont exist */
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
argument_list|)
argument_list|)
decl_stmt|;
name|testCollection
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|GUEST_UID
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|col
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_DTD_COLLECTION
argument_list|)
argument_list|)
decl_stmt|;
name|col
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|GUEST_UID
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|col
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_XSD_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|col
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|GUEST_UID
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|col
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|col
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|GUEST_UID
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|transact
operator|!=
literal|null
operator|&&
name|txn
operator|!=
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|createTestDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|getGuestSubject
argument_list|()
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
comment|/** create necessary documents  */
comment|//hamlet
name|String
name|sb
init|=
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|getHamlet
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|=
name|sb
operator|.
name|replaceAll
argument_list|(
literal|"\\Q<!\\E.*DOCTYPE.*\\Q-->\\E"
argument_list|,
literal|"<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \"play.dtd\">"
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|tmpCol
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP_COLLECTION
argument_list|)
argument_list|)
decl_stmt|;
name|storeDocument
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|tmpCol
argument_list|,
literal|"hamlet_valid.xml"
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|tmpCol
argument_list|)
expr_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|dtdCol
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_DTD_COLLECTION
argument_list|)
argument_list|)
decl_stmt|;
name|storeTextDocument
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|dtdCol
argument_list|,
literal|"hamlet.dtd"
argument_list|,
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|loadSample
argument_list|(
literal|"validation/dtd/hamlet.dtd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|storeDocument
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|dtdCol
argument_list|,
literal|"catalog.xml"
argument_list|,
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|loadSample
argument_list|(
literal|"validation/dtd/catalog.xml"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|dtdCol
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|homeCol
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
argument_list|)
argument_list|)
decl_stmt|;
name|storeDocument
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|homeCol
argument_list|,
literal|"hamlet_valid.xml"
argument_list|,
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|loadSample
argument_list|(
literal|"validation/dtd/hamlet_valid.xml"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|storeDocument
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|homeCol
argument_list|,
literal|"hamlet_invalid.xml"
argument_list|,
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|loadSample
argument_list|(
literal|"validation/dtd/hamlet_invalid.xml"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|homeCol
argument_list|)
expr_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|transact
operator|!=
literal|null
operator|&&
name|txn
operator|!=
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|storeDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docUri
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|storeTextDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|TriggerException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docUri
argument_list|,
name|data
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|removeTestCollections
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Subject
name|admin
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
argument_list|)
argument_list|)
decl_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|txn
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|transact
operator|!=
literal|null
operator|&&
name|txn
operator|!=
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*      @Before     public void setUp() throws Exception {          logger.info("setUp");          Class<?> cl = Class.forName("org.exist.xmldb.DatabaseImpl");         database = (Database) cl.newInstance();         database.setProperty("create-database", "true");         DatabaseManager.registerDatabase(database);         root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "admin", null);         service = (XPathQueryService) root.getService( "XQueryService", "1.0" );          try {             File file = new File(eXistHome, "samples/shakespeare/hamlet.xml");             InputStream fis = new FileInputStream(file);             ByteArrayOutputStream baos = new ByteArrayOutputStream();             TestTools.copyStream(fis, baos);             fis.close();              String sb = new String(baos.toByteArray());             sb=sb.replaceAll("\\Q<!\\E.*DOCTYPE.*\\Q-->\\E",                 "<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \"play.dtd\">" );             InputStream is = new ByteArrayInputStream(sb.getBytes());              // -----              URL url = new URL("xmldb:exist://" + TestTools.VALIDATION_TMP + "/hamlet_valid.xml");             URLConnection connection = url.openConnection();             OutputStream os = connection.getOutputStream();              TestTools.copyStream(is, os);              is.close();             os.close();          } catch (Exception ex) {             ex.printStackTrace();             logger.error(ex);             fail(ex.getMessage());         }          try{             config.setProperty(XMLReaderObjectFactory.PROPERTY_VALIDATION_MODE, "no");              String hamlet = eXistHome + "/samples/validation/dtd";              TestTools.insertDocumentToURL(hamlet+"/hamlet.dtd",                 "xmldb:exist://"+TestTools.VALIDATION_DTD+"/hamlet.dtd");             TestTools.insertDocumentToURL(hamlet+"/catalog.xml",                 "xmldb:exist://"+TestTools.VALIDATION_DTD+"/catalog.xml");              TestTools.insertDocumentToURL(hamlet+"/hamlet_valid.xml",                 "xmldb:exist://"+TestTools.VALIDATION_HOME+"/hamlet_valid.xml");             TestTools.insertDocumentToURL(hamlet+"/hamlet_invalid.xml",                 "xmldb:exist://"+TestTools.VALIDATION_HOME+"/hamlet_invalid.xml");              config.setProperty(XMLReaderObjectFactory.PROPERTY_VALIDATION_MODE, "yes");          } catch (Exception ex) {             ex.printStackTrace();             logger.error(ex);             fail(ex.getMessage());         }     }      @AfterClass     public static void shutdown() throws Exception {          logger.info("shutdown");                  DatabaseManager.deregisterDatabase(database);         DatabaseInstanceManager dim =             (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");         dim.shutdown();              }*/
block|}
end_class

end_unit

