begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|test
operator|.
name|ExistWebServer
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
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
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|diff
operator|.
name|Diff
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
name|Source
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|assertFalse
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
name|SerializationTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PORT_PLACEHOLDER
init|=
literal|"${PORT}"
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"local"
block|,
literal|"xmldb:exist://"
block|}
block|,
block|{
literal|"remote"
block|,
literal|"xmldb:exist://localhost:"
operator|+
name|PORT_PLACEHOLDER
operator|+
literal|"/xmlrpc"
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|String
name|apiName
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
name|value
operator|=
literal|1
argument_list|)
specifier|public
name|String
name|baseUri
decl_stmt|;
specifier|private
specifier|final
name|String
name|getBaseUri
parameter_list|()
block|{
return|return
name|baseUri
operator|.
name|replace
argument_list|(
name|PORT_PLACEHOLDER
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|existWebServer
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|String
name|EOL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_COLLECTION_NAME
init|=
literal|"test"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_DOC_NAME
init|=
literal|"defaultns.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML
init|=
literal|"<root xmlns=\"http://foo.com\">"
operator|+
literal|"<entry>1</entry>"
operator|+
literal|"<entry>2</entry>"
operator|+
literal|"</root>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_EXPECTED1
init|=
literal|"<exist:result xmlns:exist=\""
operator|+
name|Namespaces
operator|.
name|EXIST_NS
operator|+
literal|"\" hitCount=\"2\">"
operator|+
name|EOL
operator|+
literal|"<entry xmlns=\"http://foo.com\">1</entry>"
operator|+
name|EOL
operator|+
literal|"<entry xmlns=\"http://foo.com\">2</entry>"
operator|+
name|EOL
operator|+
literal|"</exist:result>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_EXPECTED2
init|=
literal|"<exist:result xmlns:exist=\""
operator|+
name|Namespaces
operator|.
name|EXIST_NS
operator|+
literal|"\" hitCount=\"1\">"
operator|+
name|EOL
operator|+
literal|"<c:Site xmlns:c=\"urn:content\" xmlns=\"urn:content\">"
operator|+
name|EOL
operator|+
literal|"<config xmlns=\"urn:config\">123</config>"
operator|+
name|EOL
operator|+
literal|"<serverconfig xmlns=\"urn:config\">123</serverconfig>"
operator|+
name|EOL
operator|+
literal|"</c:Site>"
operator|+
name|EOL
operator|+
literal|"</exist:result>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML_UPDATED_EXPECTED
init|=
literal|"<root xmlns=\"http://foo.com\">"
operator|+
name|EOL
operator|+
literal|"<entry>1</entry>"
operator|+
name|EOL
operator|+
literal|"<entry>2</entry>"
operator|+
name|EOL
operator|+
literal|"<entry xmlns=\"\" xml:id=\"aargh\"/>"
operator|+
name|EOL
operator|+
literal|"</root>"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|wrappedNsTest1
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"declare namespace foo=\"http://foo.com\"; //foo:entry"
argument_list|)
decl_stmt|;
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
specifier|final
name|Resource
name|resource
init|=
name|result
operator|.
name|getMembersAsResource
argument_list|()
decl_stmt|;
name|assertXMLEquals
argument_list|(
name|XML_EXPECTED1
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|wrappedNsTest2
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"declare namespace config='urn:config'; "
operator|+
literal|"declare namespace c='urn:content'; "
operator|+
literal|"declare variable $config :=<config xmlns='urn:config'>123</config>; "
operator|+
literal|"declare variable $serverConfig :=<serverconfig xmlns='urn:config'>123</serverconfig>; "
operator|+
literal|"<c:Site xmlns='urn:content' xmlns:c='urn:content'> "
operator|+
literal|"{($config,$serverConfig)} "
operator|+
literal|"</c:Site>"
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
specifier|final
name|Resource
name|resource
init|=
name|result
operator|.
name|getMembersAsResource
argument_list|()
decl_stmt|;
name|assertXMLEquals
argument_list|(
name|XML_EXPECTED2
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xqueryUpdateNsTest
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"xquery version \"1.0\";"
operator|+
name|EOL
operator|+
literal|"declare namespace foo=\"http://foo.com\";"
operator|+
name|EOL
operator|+
literal|"let $in-memory :="
operator|+
name|EOL
operator|+
name|XML
operator|+
name|EOL
operator|+
literal|"let $on-disk := doc('/db/"
operator|+
name|TEST_COLLECTION_NAME
operator|+
literal|'/'
operator|+
name|XML_DOC_NAME
operator|+
literal|"')"
operator|+
name|EOL
operator|+
literal|"let $new-node :=<entry xml:id='aargh'/>"
operator|+
name|EOL
operator|+
literal|"let $update := update insert $new-node into $on-disk/foo:root"
operator|+
name|EOL
operator|+
literal|"return"
operator|+
name|EOL
operator|+
literal|"    ("
operator|+
name|EOL
operator|+
literal|"        $in-memory,"
operator|+
name|EOL
operator|+
literal|"        $on-disk"
operator|+
name|EOL
operator|+
literal|"    )"
operator|+
name|EOL
argument_list|)
decl_stmt|;
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
specifier|final
name|Resource
name|inMemoryResource
init|=
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertXMLEquals
argument_list|(
name|XML
argument_list|,
name|inMemoryResource
argument_list|)
expr_stmt|;
specifier|final
name|Resource
name|onDiskResource
init|=
name|result
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertXMLEquals
argument_list|(
name|XML_UPDATED_EXPECTED
argument_list|,
name|onDiskResource
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertXMLEquals
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|,
specifier|final
name|Resource
name|actual
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Source
name|srcExpected
init|=
name|Input
operator|.
name|fromString
argument_list|(
name|expected
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Source
name|srcActual
init|=
name|Input
operator|.
name|fromString
argument_list|(
name|actual
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Diff
name|diff
init|=
name|DiffBuilder
operator|.
name|compare
argument_list|(
name|srcExpected
argument_list|)
operator|.
name|withTest
argument_list|(
name|srcActual
argument_list|)
operator|.
name|checkForIdentical
argument_list|()
operator|.
name|ignoreWhitespace
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|diff
operator|.
name|hasDifferences
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
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
name|getBaseUri
argument_list|()
operator|+
literal|"/db"
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
specifier|final
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
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|XML_DOC_NAME
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|XML
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
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
name|getBaseUri
argument_list|()
operator|+
literal|"/db"
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
decl_stmt|;
specifier|final
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
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
name|testCollection
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit
