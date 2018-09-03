begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

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

begin_comment
comment|/**  *  * @author ljo  */
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
name|FunLangTest
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
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testFnLangWithContext
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|resourceSet
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"let $doc-frag := "
operator|+
literal|"<desclist xml:lang=\"en\">"
operator|+
literal|"<desc xml:lang=\"en-US\" n=\"1\">"
operator|+
literal|"<line>The first line of the description.</line>"
operator|+
literal|"</desc>"
operator|+
literal|"<desc xml:lang=\"fr\" n=\"2\">"
operator|+
literal|"<line>La premi&#232;re ligne de la dÃ©scription.</line>"
operator|+
literal|"</desc>"
operator|+
literal|"</desclist>"
operator|+
literal|"return $doc-frag//desc[lang(\"en-US\")]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resourceSet
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<desc xml:lang=\"en-US\" n=\"1\">\n<line>The first line of the description.</line>\n</desc>"
argument_list|,
name|resourceSet
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
annotation|@
name|Test
specifier|public
name|void
name|testFnLangWithArgument
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|resourceSet
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"let $doc-frag := "
operator|+
literal|"<desclist xml:lang=\"en\">"
operator|+
literal|"<desc xml:lang=\"en-US\" n=\"1\">"
operator|+
literal|"<line>The first line of the description.</line>"
operator|+
literal|"</desc>"
operator|+
literal|"<desc xml:lang=\"fr\" n=\"2\">"
operator|+
literal|"<line>La premi&#232;re ligne de la dÃ©scription.</line>"
operator|+
literal|"</desc>"
operator|+
literal|"</desclist>"
operator|+
literal|"return lang(\"en-US\", $doc-frag//desc[@n eq \"2\"])"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resourceSet
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|resourceSet
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
annotation|@
name|Test
specifier|public
name|void
name|testFnLangWithAttributeArgument
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|resourceSet
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"let $doc-frag := "
operator|+
literal|"<desclist xml:lang=\"en\">"
operator|+
literal|"<desc xml:lang=\"en-US\" n=\"1\">"
operator|+
literal|"<line>The first line of the description.</line>"
operator|+
literal|"</desc>"
operator|+
literal|"<desc xml:lang=\"fr\" n=\"2\">"
operator|+
literal|"<line>La premi&#232;re ligne de la dÃ©scription.</line>"
operator|+
literal|"</desc>"
operator|+
literal|"</desclist>"
operator|+
literal|"return lang(\"en-US\", $doc-frag//desc/@n[. eq \"1\"])"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resourceSet
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|resourceSet
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
block|}
end_class

end_unit

