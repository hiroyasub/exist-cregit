begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_comment
comment|/**  * Test case for mime-type mapping.  * Tests the distribution edition of mime-types.xml  * as well as variants that exploit the default mime type feature  *   * @author Peter Ciuffetti  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MimeTableTest
block|{
comment|/** 	 * This test checks the behavior of MimeTable.java 	 * with respect to the distribution version of mime-types.xml. 	 * The distribution version of mime-types.xml does not use the 	 * default mime type capability. 	 */
annotation|@
name|Test
specifier|public
name|void
name|testDistributionVersionOfMimeTypesXml
parameter_list|()
throws|throws
name|URISyntaxException
block|{
specifier|final
name|Path
name|mimeTypes
init|=
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"mime-types.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|MimeTable
name|mimeTable
init|=
operator|new
name|MimeTable
argument_list|(
name|mimeTypes
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime table not found"
argument_list|,
name|mimeTable
argument_list|)
expr_stmt|;
name|MimeType
name|mt
decl_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.xml"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"application/xml"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.html"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.html"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"text/html"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.jpg"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.jpg"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should return null mime type for file without extension"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"foo.bar"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Should return null mime type for file with extension not configured in mime-types.xml"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This test checks the behavior of the mime-types@default-resource-type attribute 	 * The test config assigns all resources to application/xml 	 */
annotation|@
name|Test
specifier|public
name|void
name|testWithDefaultResourceTypeFeature
parameter_list|()
throws|throws
name|URISyntaxException
block|{
specifier|final
name|Path
name|mimeTypes
init|=
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"mime-types-xml-default.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|MimeTable
name|mimeTable
init|=
operator|new
name|MimeTable
argument_list|(
name|mimeTypes
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime table not found"
argument_list|,
name|mimeTable
argument_list|)
expr_stmt|;
name|MimeType
name|mt
decl_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.xml"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"application/xml"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.html"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.html"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"application/xml"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.jpg"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.jpg"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"application/xml"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for foo"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"application/xml"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"foo.bar"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.jpg"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"application/xml"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|XML
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This test checks the behavior of the mime-types@default-mime-type attribute 	 * The test config assigns all resources to foo/bar (BINARY) 	 */
annotation|@
name|Test
specifier|public
name|void
name|testWithDefaultMimeTypeFeature
parameter_list|()
throws|throws
name|URISyntaxException
block|{
specifier|final
name|Path
name|mimeTypes
init|=
name|Paths
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"mime-types-foo-default.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|MimeTable
name|mimeTable
init|=
operator|new
name|MimeTable
argument_list|(
name|mimeTypes
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime table not found"
argument_list|,
name|mimeTable
argument_list|)
expr_stmt|;
name|MimeType
name|mt
decl_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.xml"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"foo/bar"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.html"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.html"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"foo/bar"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"test.jpg"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.jpg"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"foo/bar"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for foo"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"foo/bar"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|mt
operator|=
name|mimeTable
operator|.
name|getContentTypeFor
argument_list|(
literal|"foo.bar"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Mime type not found for test.jpg"
argument_list|,
name|mt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect mime type"
argument_list|,
literal|"foo/bar"
argument_list|,
name|mt
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect resource type"
argument_list|,
name|MimeType
operator|.
name|BINARY
argument_list|,
name|mt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

