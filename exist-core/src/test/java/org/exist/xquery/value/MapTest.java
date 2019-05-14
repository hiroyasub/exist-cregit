begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|xquery
operator|.
name|ErrorCodes
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|MapTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|server
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
name|effectiveBooleanValue
parameter_list|()
block|{
try|try
block|{
specifier|final
name|XQueryService
name|queryService
init|=
operator|(
name|XQueryService
operator|)
name|server
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|queryService
operator|.
name|query
argument_list|(
literal|"fn:boolean(map{})"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|XPathException
condition|)
block|{
specifier|final
name|XPathException
name|xpe
init|=
operator|(
name|XPathException
operator|)
name|cause
decl_stmt|;
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|FORG0006
argument_list|,
name|xpe
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|fail
argument_list|(
literal|"effectiveBooleanValue of a map should cause the error FORG0006"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

