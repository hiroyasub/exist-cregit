begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
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
comment|/**  * @author Adam Retter<adam.retter@googlemail.com>  */
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
name|MemtreeInXQuery
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|pi_attributes
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xquery
init|=
literal|"let $doc := document{\n"
operator|+
literal|"    processing-instruction{\"ok\"}{\"ok\"},\n"
operator|+
literal|"<root/>\n"
operator|+
literal|"}\n"
operator|+
literal|"return count($doc//processing-instruction()/@*)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|xquery
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
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pi_children
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xquery
init|=
literal|"let $doc := document{\n"
operator|+
literal|"    processing-instruction{\"ok\"}{\"ok\"},\n"
operator|+
literal|"<root/>\n"
operator|+
literal|"}\n"
operator|+
literal|"return count($doc//processing-instruction()/node())"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|xquery
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
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pi_descendantAttributes
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xquery
init|=
literal|"let $doc := document{\n"
operator|+
literal|"    processing-instruction{\"ok\"}{\"ok\"},\n"
operator|+
literal|"<root/>\n"
operator|+
literal|"}\n"
operator|+
literal|"return count($doc//processing-instruction()//@*)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|xquery
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
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|attr_attributes
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xquery
init|=
literal|"let $doc := document {\n"
operator|+
literal|"    element a {\n"
operator|+
literal|"        attribute x { \"y\" }\n"
operator|+
literal|"    }\n"
operator|+
literal|"} return\n"
operator|+
literal|"    count($doc/a/@x/@y)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|xquery
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
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|attr_children
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|xquery
init|=
literal|"let $doc := document {\n"
operator|+
literal|"    element a {\n"
operator|+
literal|"        attribute x { \"y\" }\n"
operator|+
literal|"    }\n"
operator|+
literal|"} return\n"
operator|+
literal|"    count($doc/a/@x/node())"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|xquery
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
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

