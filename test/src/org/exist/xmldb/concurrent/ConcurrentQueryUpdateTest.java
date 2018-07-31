begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|action
operator|.
name|XQueryUpdateAction
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

begin_class
specifier|public
class|class
name|ConcurrentQueryUpdateTest
extends|extends
name|ConcurrentTestBase
block|{
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
name|Collection
name|col
init|=
name|getTestCollection
argument_list|()
decl_stmt|;
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|col
operator|.
name|createResource
argument_list|(
literal|"testappend.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
literal|"<root><node id=\"1\"/></root>"
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|assertAdditional
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|col
init|=
name|getTestCollection
argument_list|()
decl_stmt|;
specifier|final
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"distinct-values(//node/@id)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|41
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|XMLResource
name|next
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
operator|(
name|long
operator|)
name|i
argument_list|)
decl_stmt|;
name|next
operator|.
name|getContent
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTestCollectionName
parameter_list|()
block|{
return|return
literal|"C1"
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Runner
argument_list|>
name|getRunners
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Runner
argument_list|(
operator|new
name|XQueryUpdateAction
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/C1"
argument_list|,
literal|"testappend.xml"
argument_list|)
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|Runner
argument_list|(
operator|new
name|XQueryUpdateAction
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/C1"
argument_list|,
literal|"testappend.xml"
argument_list|)
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

