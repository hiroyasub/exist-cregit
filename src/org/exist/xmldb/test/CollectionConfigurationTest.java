begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|CollectionConfiguration
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
name|CollectionConfigurationManager
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
name|XPathQueryService
import|;
end_import

begin_class
specifier|public
class|class
name|CollectionConfigurationTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testIndexConfiguration"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_COLLECTION_2
init|=
literal|"conf2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONF_COLL_PATH
init|=
name|CollectionConfigurationManager
operator|.
name|CONFIG_COLLECTION
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|coll1
init|=
name|CONF_COLL_PATH
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|coll2
init|=
name|coll1
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION_2
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
specifier|static
specifier|final
name|String
name|TEST_CONFIG_NAME_1
init|=
literal|"test1.xconf"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_CONFIG_NAME_2
init|=
literal|"test2.xconf"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT_CONTENT
init|=
literal|"<test>"
operator|+
literal|"<a>001</a>"
operator|+
literal|"<a>01</a>"
operator|+
literal|"<a>1</a>"
operator|+
literal|"<b>001</b>"
operator|+
literal|"<b>01</b>"
operator|+
literal|"<b>1</b>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
name|String
name|CONFIG1
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<create qname=\"a\" type=\"xs:integer\"/>"
operator|+
literal|"<create qname=\"b\" type=\"xs:string\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
specifier|protected
name|void
name|setUp
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
name|DRIVER
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
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
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
name|Collection
name|configColl
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
name|CONF_COLL_PATH
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|configColl
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"creating collection '"
operator|+
name|CONF_COLL_PATH
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|configColl
operator|=
name|cms
operator|.
name|createCollection
argument_list|(
name|CONF_COLL_PATH
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
specifier|protected
name|void
name|tearDown
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
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
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
comment|//Removes the collection config collection *manually*
name|service
operator|.
name|removeCollection
argument_list|(
name|CONF_COLL_PATH
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testCollectionConfigurationService1
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|//Configure collection automatically
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
name|CONFIG1
argument_list|)
expr_stmt|;
comment|//... then index document
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
comment|//3 numeric values
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup(xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//... but 1 string value
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup(xs:QName(\"b\"), \"1\" ) "
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
specifier|public
name|void
name|testCollectionConfigurationService2
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|// Add document....
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
comment|// ... then configure collection automatically
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
name|CONFIG1
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
comment|// No numeric values because we have no index
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// No string value because we have no index
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ...let's activate the index
name|idxConf
operator|.
name|reindexCollection
argument_list|()
expr_stmt|;
comment|// 3 numeric values
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ... but 1 string value
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
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
specifier|public
name|void
name|testCollectionConfigurationService3
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|//Configure collection *manually*
name|storeConfiguration
argument_list|(
name|CONF_COLL_PATH
argument_list|,
name|CollectionConfiguration
operator|.
name|DEFAULT_COLLECTION_CONFIG_FILE
argument_list|,
name|CONFIG1
argument_list|)
expr_stmt|;
comment|//... then index document
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
comment|//3 numeric values
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup(xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|//... but 1 string value
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup(xs:QName(\"b\"), \"1\" ) "
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
specifier|public
name|void
name|testCollectionConfigurationService4
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|// Add document....
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
comment|// ... then configure collection *manually*
name|storeConfiguration
argument_list|(
name|CONF_COLL_PATH
argument_list|,
name|CollectionConfiguration
operator|.
name|DEFAULT_COLLECTION_CONFIG_FILE
argument_list|,
name|CONFIG1
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
comment|// No numeric values because we have no index
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// No string value because we have no index
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ...let's activate the index
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
name|reindexCollection
argument_list|()
expr_stmt|;
comment|// 3 numeric values
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ... but 1 string value
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
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
specifier|public
name|void
name|testCollectionConfigurationService5
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|//Configure collection *manually*
name|String
name|configurationFileName
init|=
literal|"foo"
operator|+
name|CollectionConfiguration
operator|.
name|COLLECTION_CONFIG_SUFFIX
decl_stmt|;
name|storeConfiguration
argument_list|(
name|CONF_COLL_PATH
argument_list|,
name|configurationFileName
argument_list|,
name|CONFIG1
argument_list|)
expr_stmt|;
comment|// ... then configure collection automatically
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
name|CONFIG1
argument_list|)
expr_stmt|;
comment|// Add document....
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
comment|//our config file
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"xmldb:get-child-resources('"
operator|+
name|CollectionConfigurationManager
operator|.
name|CONFIG_COLLECTION
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"')"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|configurationFileName
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
comment|// 3 numeric values
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ... but 1 string value
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
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
specifier|public
name|void
name|testCollectionConfigurationService6
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
comment|// Add document....
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
comment|//... then configure collection *manually*
name|String
name|configurationFileName
init|=
literal|"foo"
operator|+
name|CollectionConfiguration
operator|.
name|COLLECTION_CONFIG_SUFFIX
decl_stmt|;
name|storeConfiguration
argument_list|(
name|CONF_COLL_PATH
argument_list|,
name|configurationFileName
argument_list|,
name|CONFIG1
argument_list|)
expr_stmt|;
comment|//... then configure collection automatically
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
name|CONFIG1
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
comment|//our config file
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"xmldb:get-child-resources('"
operator|+
name|CollectionConfigurationManager
operator|.
name|CONFIG_COLLECTION
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"')"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|configurationFileName
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
comment|// No numeric values because we have no index
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// No string value because we have no index
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ...let's activate the index
name|idxConf
operator|.
name|reindexCollection
argument_list|()
expr_stmt|;
comment|//WARNING : the code hereafter used to *not* work whereas
comment|//testCollectionConfigurationService4 did.
comment|//Adding confMgr.invalidateAll(getName()); in Collection.storeInternal solved the problem
comment|//Strange case that needs investigations... -pb
comment|// 3 numeric values
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"a\"), 1 ) "
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// ... but 1 string value
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"util:qname-index-lookup( xs:QName(\"b\"), \"1\" ) "
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
specifier|public
name|void
name|testMultipleConfigurations00
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations01
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations02
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations03
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations04
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations05
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations06
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations07
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations08
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations09
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations10
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations11
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations12
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations13
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll1
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations14
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleConfigurations15
parameter_list|()
block|{
name|checkStoreConf
argument_list|(
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
name|coll2
argument_list|,
name|TEST_CONFIG_NAME_2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkStoreConf
parameter_list|(
name|String
name|coll1
parameter_list|,
name|String
name|confName1
parameter_list|,
name|String
name|coll2
parameter_list|,
name|String
name|confName2
parameter_list|,
name|boolean
name|shouldSucceed
parameter_list|)
block|{
try|try
block|{
name|storeConfiguration
argument_list|(
name|coll1
argument_list|,
name|confName1
argument_list|,
name|CONFIG1
argument_list|)
expr_stmt|;
name|storeConfiguration
argument_list|(
name|coll2
argument_list|,
name|confName2
argument_list|,
name|CONFIG1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|shouldSucceed
condition|)
block|{
name|fail
argument_list|(
literal|"Should not have been able to store '"
operator|+
name|confName1
operator|+
literal|"' to '"
operator|+
name|coll1
operator|+
literal|"'\n\tand then '"
operator|+
name|confName2
operator|+
literal|"' to '"
operator|+
name|coll2
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
if|if
condition|(
name|shouldSucceed
condition|)
block|{
name|fail
argument_list|(
literal|"Should have been able to store '"
operator|+
name|confName1
operator|+
literal|"' to '"
operator|+
name|coll1
operator|+
literal|"'\n\tand then '"
operator|+
name|confName2
operator|+
literal|"' to '"
operator|+
name|coll2
operator|+
literal|"': "
operator|+
name|xe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|storeConfiguration
parameter_list|(
name|String
name|collPath
parameter_list|,
name|String
name|confName
parameter_list|,
name|String
name|confContent
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|fullCollPath
init|=
name|URI
operator|+
name|collPath
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing configuration '"
operator|+
name|confName
operator|+
literal|"' to '"
operator|+
name|collPath
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|Collection
name|configColl
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|fullCollPath
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|configColl
operator|==
literal|null
condition|)
block|{
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|configColl
operator|=
name|cms
operator|.
name|createCollection
argument_list|(
name|collPath
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|configColl
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
name|configColl
operator|.
name|createResource
argument_list|(
name|confName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|confContent
argument_list|)
expr_stmt|;
name|configColl
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|CollectionConfigurationTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

