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
name|exist
operator|.
name|xmldb
operator|.
name|EXistResource
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
name|TransformTest
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
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_COLLECTION_NAME
init|=
literal|"transform-test"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
comment|/**      * Tests relative path resolution when parsing stylesheets in      * the transform:transform function.      */
annotation|@
name|Test
specifier|public
name|void
name|transform
parameter_list|()
throws|throws
name|XMLDBException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|String
name|imports
init|=
literal|"import module namespace transform='http://exist-db.org/xquery/transform';\n"
decl_stmt|;
name|String
name|query
init|=
literal|"import module namespace transform='http://exist-db.org/xquery/transform';\n"
operator|+
literal|"let $xml :=<empty />,\n"
operator|+
literal|"	$xsl := 'xmldb:exist:///db/"
operator|+
name|TEST_COLLECTION_NAME
operator|+
literal|"/xsl1/1.xsl'\n"
operator|+
literal|"return transform:transform($xml, $xsl, ())"
decl_stmt|;
name|String
name|result
init|=
name|execQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
literal|"<doc>"
operator|+
literal|"<p>Start Template 1</p>"
operator|+
literal|"<p>Start Template 2</p>"
operator|+
literal|"<p>Template 3</p>"
operator|+
literal|"<p>End Template 2</p>"
operator|+
literal|"<p>Template 3</p>"
operator|+
literal|"<p>End Template 1</p>"
operator|+
literal|"</doc>"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|execQuery
parameter_list|(
name|String
name|query
parameter_list|)
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
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|addXMLDocument
parameter_list|(
name|Collection
name|c
parameter_list|,
name|String
name|doc
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|Resource
name|r
init|=
name|c
operator|.
name|createResource
argument_list|(
name|id
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|r
operator|.
name|setContent
argument_list|(
name|doc
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|r
operator|)
operator|.
name|setMimeType
argument_list|(
literal|"application/xml"
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|r
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
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
block|{
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
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|service
operator|=
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
expr_stmt|;
name|Collection
name|xsl1
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"xsl1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|xsl1
argument_list|)
expr_stmt|;
name|Collection
name|xsl3
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"xsl3"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|xsl3
argument_list|)
expr_stmt|;
name|service
operator|=
operator|(
name|CollectionManagementService
operator|)
name|xsl1
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|Collection
name|xsl2
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"xsl2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|xsl2
argument_list|)
expr_stmt|;
name|String
name|doc1
init|=
literal|"<?xml version='1.0' encoding='UTF-8'?>\n"
operator|+
literal|"<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>\n"
operator|+
literal|"<xsl:import href='xsl2/2.xsl' />\n"
operator|+
literal|"<xsl:template match='/'>\n"
operator|+
literal|"<doc>"
operator|+
literal|"<p>Start Template 1</p>"
operator|+
literal|"<xsl:call-template name='template-2' />"
operator|+
literal|"<xsl:call-template name='template-3' />"
operator|+
literal|"<p>End Template 1</p>"
operator|+
literal|"</doc>"
operator|+
literal|"</xsl:template>"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
name|String
name|doc2
init|=
literal|"<?xml version='1.0' encoding='UTF-8'?>\n"
operator|+
literal|"<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>\n"
operator|+
literal|"<xsl:import href='../../xsl3/3.xsl' />\n"
operator|+
literal|"<xsl:template name='template-2'>\n"
operator|+
literal|"<p>Start Template 2</p>"
operator|+
literal|"<xsl:call-template name='template-3' />"
operator|+
literal|"<p>End Template 2</p>"
operator|+
literal|"</xsl:template>"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
name|String
name|doc3
init|=
literal|"<?xml version='1.0' encoding='UTF-8'?>\n"
operator|+
literal|"<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>\n"
operator|+
literal|"<xsl:template name='template-3'>\n"
operator|+
literal|"<p>Template 3</p>"
operator|+
literal|"</xsl:template>"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
name|addXMLDocument
argument_list|(
name|xsl1
argument_list|,
name|doc1
argument_list|,
literal|"1.xsl"
argument_list|)
expr_stmt|;
name|addXMLDocument
argument_list|(
name|xsl2
argument_list|,
name|doc2
argument_list|,
literal|"2.xsl"
argument_list|)
expr_stmt|;
name|addXMLDocument
argument_list|(
name|xsl3
argument_list|,
name|doc3
argument_list|,
literal|"3.xsl"
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

