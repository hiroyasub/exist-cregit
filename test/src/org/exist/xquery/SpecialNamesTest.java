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
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|SpecialNamesTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
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
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
block|{
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
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
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
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
literal|"test"
argument_list|)
expr_stmt|;
name|testCollection
operator|=
literal|null
expr_stmt|;
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
block|{
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
block|}
else|else
block|{
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
block|}
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
block|{
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
block|}
else|else
block|{
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
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|attributes
parameter_list|()
throws|throws
name|XMLDBException
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
literal|"<foo amp='x' lt='x' gt='x' apos='x' quot='x'/>"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// TODO: could check result
block|}
block|}
end_class

end_unit

