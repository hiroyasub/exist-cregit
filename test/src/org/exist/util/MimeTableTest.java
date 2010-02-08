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
name|io
operator|.
name|File
import|;
end_import

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
name|junit
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test case for mime-type mapping.  * Tests the distribution edition of mime-types.xml  * as well as variants that exploit the default mime type feature  *   * @author Peter Ciuffetti  */
end_comment

begin_class
specifier|public
class|class
name|MimeTableTest
block|{
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// MimeTable is a singleton
comment|// We use reflection here to null-out the 'instance' field
comment|// so subsequent tests that call getInstance() will re-load
comment|// the specified mime type config file
name|Field
name|field
init|=
name|MimeTable
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"instance"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
name|MimeTable
operator|.
name|getInstance
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This test checks the behavior of MimeTable.java 	 * with respect to the distribution version of mime-types.xml. 	 * The distribution version of mime-types.xml does not use the 	 * default mime type capability. 	 */
annotation|@
name|Test
specifier|public
name|void
name|testDistributionVersionOfMimeTypesXml
parameter_list|()
block|{
name|File
name|existDir
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|existDir
operator|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"mime-types.xml"
argument_list|)
decl_stmt|;
name|MimeTable
name|mimeTable
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|(
name|file
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
block|{
name|File
name|existDir
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|existDir
operator|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"./test/src/org/exist/util"
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
operator|+
literal|"/test/src/org/exist/util"
argument_list|)
expr_stmt|;
name|MimeTable
name|mimeTable
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|(
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"mime-types-xml-default.xml"
argument_list|)
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
block|{
name|File
name|existDir
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|existDir
operator|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"./test/src/org/exist/util"
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
operator|+
literal|"/test/src/org/exist/util"
argument_list|)
expr_stmt|;
name|MimeTable
name|mimeTable
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|(
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"mime-types-foo-default.xml"
argument_list|)
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

