begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|counter
package|;
end_package

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
name|xquery
operator|.
name|XPathException
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

begin_comment
comment|/**  * @author Jasper Linthorst (jasper.linthorst@gmail.com)  */
end_comment

begin_class
specifier|public
class|class
name|CounterTest
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
specifier|final
specifier|static
name|String
name|IMPORT
init|=
literal|"import module namespace counter=\""
operator|+
name|CounterModule
operator|.
name|NAMESPACE_URI
operator|+
literal|"\" "
operator|+
literal|"at \"java:org.exist.xquery.modules.counter.CounterModule\"; "
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|createAndDestroyCounter
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
name|String
name|query
init|=
name|IMPORT
operator|+
literal|"counter:create('jasper1')"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|query
operator|=
name|IMPORT
operator|+
literal|"counter:next-value('jasper1')"
expr_stmt|;
name|result
operator|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|query
operator|=
name|IMPORT
operator|+
literal|"counter:destroy('jasper1')"
expr_stmt|;
name|result
operator|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createAndInitAndDestroyCounter
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
name|String
name|query
init|=
name|IMPORT
operator|+
literal|"counter:create('jasper3',xs:long(1200))"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1200"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|query
operator|=
name|IMPORT
operator|+
literal|"counter:next-value('jasper3')"
expr_stmt|;
name|result
operator|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1201"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|query
operator|=
name|IMPORT
operator|+
literal|"counter:destroy('jasper3')"
expr_stmt|;
name|result
operator|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|r
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|threadedIncrement
parameter_list|()
throws|throws
name|XPathException
throws|,
name|InterruptedException
throws|,
name|XMLDBException
block|{
name|String
name|query
init|=
name|IMPORT
operator|+
literal|"counter:create('jasper2')"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
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
name|Thread
name|a
init|=
operator|new
name|IncrementThread
argument_list|()
decl_stmt|;
name|a
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|b
init|=
operator|new
name|IncrementThread
argument_list|()
decl_stmt|;
name|b
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|c
init|=
operator|new
name|IncrementThread
argument_list|()
decl_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|a
operator|.
name|join
argument_list|()
expr_stmt|;
name|b
operator|.
name|join
argument_list|()
expr_stmt|;
name|c
operator|.
name|join
argument_list|()
expr_stmt|;
name|query
operator|=
name|IMPORT
operator|+
literal|"counter:next-value('jasper2')"
expr_stmt|;
name|ResourceSet
name|valueAfter
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|query
operator|=
name|IMPORT
operator|+
literal|"counter:destroy('jasper2')"
expr_stmt|;
name|result
operator|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"601"
argument_list|,
operator|(
name|String
operator|)
name|valueAfter
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
specifier|static
class|class
name|IncrementThread
extends|extends
name|Thread
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|query
init|=
name|IMPORT
operator|+
literal|"counter:next-value('jasper2')"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
block|}
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
block|}
block|}
end_class

end_unit

