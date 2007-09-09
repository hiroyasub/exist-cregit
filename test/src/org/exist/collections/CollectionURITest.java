begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
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
name|*
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

begin_class
specifier|public
class|class
name|CollectionURITest
block|{
annotation|@
name|Test
specifier|public
name|void
name|append
parameter_list|()
block|{
name|CollectionURI
name|uri
init|=
operator|new
name|CollectionURI
argument_list|(
literal|"/db"
argument_list|)
decl_stmt|;
name|uri
operator|.
name|append
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
operator|new
name|CollectionURI
argument_list|(
literal|"/db/test1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/db/test1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|String
argument_list|(
literal|"/db/test1"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|uri
operator|.
name|append
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
operator|new
name|CollectionURI
argument_list|(
literal|"/db/test1/test2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/db/test1/test2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|String
argument_list|(
literal|"/db/test1/test2"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|CollectionURI
name|uri
init|=
operator|new
name|CollectionURI
argument_list|(
literal|"/db/test1/test2"
argument_list|)
decl_stmt|;
name|uri
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
operator|new
name|CollectionURI
argument_list|(
literal|"/db/test1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/db/test1"
argument_list|)
expr_stmt|;
name|uri
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
operator|new
name|CollectionURI
argument_list|(
literal|"/db"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/db"
argument_list|)
expr_stmt|;
name|uri
operator|.
name|append
argument_list|(
literal|"testMe"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
operator|new
name|CollectionURI
argument_list|(
literal|"/db/testMe"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/db/testMe"
argument_list|)
expr_stmt|;
name|uri
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
operator|new
name|CollectionURI
argument_list|(
literal|"/db"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/db"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

