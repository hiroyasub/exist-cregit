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
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|io
operator|.
name|InputStreamUtil
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|samples
operator|.
name|Samples
operator|.
name|SAMPLES
import|;
end_import

begin_class
specifier|public
class|class
name|ResourceSetTest
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testResourceSet"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
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
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getSample
argument_list|(
literal|"shakespeare/shakes.xsl"
argument_list|)
init|)
block|{
specifier|final
name|Resource
name|shakesRes
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"shakes.xsl"
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|shakesRes
operator|.
name|setContent
argument_list|(
name|InputStreamUtil
operator|.
name|readAll
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|shakesRes
argument_list|)
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getHamletSample
argument_list|()
init|)
block|{
specifier|final
name|Resource
name|hamletRes
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"hamlet.xml"
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|hamletRes
operator|.
name|setContent
argument_list|(
name|InputStreamUtil
operator|.
name|readAll
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|hamletRes
argument_list|)
expr_stmt|;
block|}
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
comment|//delete the test collection
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|testCollection
operator|.
name|getParentCollection
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
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|intersection1
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xpathPrefix
init|=
literal|"doc('/db/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/shakes.xsl')/*/*"
decl_stmt|;
specifier|final
name|String
name|query1
init|=
name|xpathPrefix
operator|+
literal|"[position()>= 5 ]"
decl_stmt|;
specifier|final
name|String
name|query2
init|=
name|xpathPrefix
operator|+
literal|"[position()<= 10]"
decl_stmt|;
specifier|final
name|int
name|expected
init|=
literal|87
decl_stmt|;
specifier|final
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
specifier|final
name|ResourceSet
name|result1
init|=
name|service
operator|.
name|query
argument_list|(
name|query1
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result2
init|=
name|service
operator|.
name|query
argument_list|(
name|query2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"size of intersection of "
operator|+
name|query1
operator|+
literal|" and "
operator|+
name|query2
operator|+
literal|" yields "
argument_list|,
name|expected
argument_list|,
name|ResourceSetHelper
operator|.
name|intersection
argument_list|(
name|result1
argument_list|,
name|result2
argument_list|)
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|intersection2
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xpathPrefix
init|=
literal|"doc('/db/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/hamlet.xml')//LINE"
decl_stmt|;
specifier|final
name|String
name|query1
init|=
name|xpathPrefix
operator|+
literal|"[fn:contains(. , 'funeral')]"
decl_stmt|;
comment|// count=4
specifier|final
name|String
name|query2
init|=
name|xpathPrefix
operator|+
literal|"[fn:contains(. , 'dirge')]"
decl_stmt|;
comment|// count=1, intersection=1
specifier|final
name|int
name|expected
init|=
literal|1
decl_stmt|;
specifier|final
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
specifier|final
name|ResourceSet
name|result1
init|=
name|service
operator|.
name|query
argument_list|(
name|query1
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result2
init|=
name|service
operator|.
name|query
argument_list|(
name|query2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"size of intersection of "
operator|+
name|query1
operator|+
literal|" and "
operator|+
name|query2
operator|+
literal|" yields "
argument_list|,
name|expected
argument_list|,
name|ResourceSetHelper
operator|.
name|intersection
argument_list|(
name|result1
argument_list|,
name|result2
argument_list|)
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

