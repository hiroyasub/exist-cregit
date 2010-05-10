begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|jetty
operator|.
name|JettyStart
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
name|CollectionImpl
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

begin_class
specifier|public
class|class
name|EntitiesTest
extends|extends
name|XMLTestCase
block|{
specifier|private
specifier|static
name|String
name|uri
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|public
specifier|static
name|void
name|setURI
parameter_list|(
name|String
name|collectionURI
parameter_list|)
block|{
name|uri
operator|=
name|collectionURI
expr_stmt|;
block|}
specifier|private
specifier|static
name|JettyStart
name|server
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|String
name|query
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://localhost"
argument_list|)
condition|)
name|initServer
argument_list|()
expr_stmt|;
try|try
block|{
comment|// initialize driver
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
name|uri
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
literal|"test"
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
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
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
block|}
block|}
specifier|private
name|void
name|initServer
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|new
name|JettyStart
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
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
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|CollectionImpl
operator|)
name|testCollection
operator|)
operator|.
name|isRemoteCollection
argument_list|()
condition|)
block|{
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|testCollection
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
name|testCollection
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tearDown PASSED"
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
comment|/** Helper that performs an XQuery and does JUnit assertion on result size.      * @see #queryResource(XQueryService, String, String, int, String)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|ResourceSet
name|queryResource
parameter_list|(
name|XQueryService
name|service
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|queryResource
argument_list|(
name|service
argument_list|,
name|resource
argument_list|,
name|query
argument_list|,
name|expected
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Helper that performs an XQuery and does JUnit assertion on result size.      * @param service XQuery service      * @param resource database resource (collection) to query      * @param query      * @param expected size of result      * @param message for JUnit      * @return a ResourceSet, allowing to do more assertions if necessary.      * @throws XMLDBException      */
specifier|private
name|ResourceSet
name|queryResource
parameter_list|(
name|XQueryService
name|service
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|queryResource
argument_list|(
name|resource
argument_list|,
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
name|query
argument_list|,
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** For queries without associated data */
specifier|private
name|ResourceSet
name|queryAndAssert
parameter_list|(
name|XQueryService
name|service
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
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
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** For queries without associated data */
specifier|private
name|XQueryService
name|getQueryService
parameter_list|()
throws|throws
name|XMLDBException
block|{
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
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|service
return|;
block|}
comment|/** stores XML String and get Query Service      * @param documentName to be stored in the DB      * @param content to be stored in the DB      * @return the XQuery Service      * @throws XMLDBException      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|XQueryService
name|storeXMLStringAndGetQueryService
parameter_list|(
name|String
name|documentName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|XMLDBException
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
name|documentName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|service
return|;
block|}
comment|/**      * @param result      * @throws XMLDBException      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|void
name|printResult
parameter_list|(
name|ResourceSet
name|result
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|ResourceIterator
name|i
init|=
name|result
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|Resource
name|r
init|=
name|i
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|r
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testAttributeConstructor
parameter_list|()
block|{
try|try
block|{
name|XQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ResourceSet
name|result
decl_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"<foo "
operator|+
literal|" ampEntity=\"{('&amp;')}\""
operator|+
literal|" string=\"{(string('&amp;'))}\""
operator|+
literal|" ltEntity=\"{('&lt;')}\""
operator|+
literal|" gtEntity=\"{('&gt;')}\""
operator|+
literal|" aposEntity=\"{('&apos;')}\""
operator|+
literal|" quotEntity=\"{('&quot;')}\""
operator|+
literal|"/>"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// TODO: could check result
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
specifier|public
name|void
name|testStringConstructor
parameter_list|()
block|{
try|try
block|{
name|XQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ResourceSet
name|result
decl_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"'&amp;'"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"'&lt;'"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"'&gt;'"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"'&apos;'"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"'&quot;'"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// TODO: could check result
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
specifier|public
name|void
name|testURIConstructor
parameter_list|()
block|{
try|try
block|{
name|XQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|ResourceSet
name|result
decl_stmt|;
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"xs:anyURI(\"index.xql?a=1&amp;b=2\")"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// TODO: could check result
name|result
operator|=
name|queryAndAssert
argument_list|(
name|service
argument_list|,
literal|"xs:anyURI('a') le xs:anyURI('b')"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
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
name|EntitiesTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

