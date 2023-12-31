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

begin_comment
comment|/**  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractDescendantOrSelfNodeKindTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|final
specifier|static
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
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_DOCUMENT
init|=
literal|"<doc xml:id=\"x\">\n"
operator|+
literal|"<?xml-stylesheet type=\"text/xsl\" href=\"test\"?>\n"
operator|+
literal|"<a>\n"
operator|+
literal|"<b x=\"1\">text<e>text</e>text</b>\n"
operator|+
literal|"</a>\n"
operator|+
literal|"<a>\n"
operator|+
literal|"<c><!--comment-->\n"
operator|+
literal|"<d xmlns=\"x\" y=\"2\" z=\"3\">text</d>\n"
operator|+
literal|"</c>\n"
operator|+
literal|"</a>\n"
operator|+
literal|"</doc>"
decl_stmt|;
specifier|protected
specifier|abstract
name|ResourceSet
name|executeQueryOnDoc
parameter_list|(
specifier|final
name|String
name|docQuery
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Test
specifier|public
name|void
name|documentNodeCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//document-node())"
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//node())"
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
name|assertEquals
argument_list|(
literal|23
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|elementCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//element())"
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
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|textCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//text())"
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
name|assertEquals
argument_list|(
literal|14
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|attributeCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//attribute())"
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
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commentCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//comment())"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|processingInstructionCount
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|executeQueryOnDoc
argument_list|(
literal|"count($doc//processing-instruction())"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

