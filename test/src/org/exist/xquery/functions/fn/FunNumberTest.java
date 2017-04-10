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
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|FunNumberTest
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
annotation|@
name|Test
specifier|public
name|void
name|testFnNumberWithContext
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
literal|"let $errors := "
operator|+
literal|"<report>"
operator|+
literal|"<message level=\"Error\" line=\"1191\" column=\"49\" repeat=\"96\"></message>"
operator|+
literal|"<message level=\"Error\" line=\"161740\" column=\"25\"></message>"
operator|+
literal|"<message level=\"Error\" line=\"162327\" column=\"92\" repeat=\"87\"></message>"
operator|+
literal|"<message level=\"Error\" line=\"255090\" column=\"25\">c</message>"
operator|+
literal|"<message level=\"Error\" line=\"255702\" column=\"414\" repeat=\"9\"></message>"
operator|+
literal|"</report>"
operator|+
literal|"return sum($errors//message/(@repeat/number(),1)[1])"
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
literal|"194"
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
name|testFnNumberWithArgument
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
literal|"let $errors := "
operator|+
literal|"<report>"
operator|+
literal|"<message level=\"Error\" line=\"1191\" column=\"49\" repeat=\"96\"></message>"
operator|+
literal|"<message level=\"Error\" line=\"161740\" column=\"25\"></message>"
operator|+
literal|"<message level=\"Error\" line=\"162327\" column=\"92\" repeat=\"87\"></message>"
operator|+
literal|"<message level=\"Error\" line=\"255090\" column=\"25\">c</message>"
operator|+
literal|"<message level=\"Error\" line=\"255702\" column=\"414\" repeat=\"9\"></message>"
operator|+
literal|"</report>"
operator|+
literal|"return sum($errors//message/(number(@repeat),1)[1])"
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
literal|"NaN"
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

