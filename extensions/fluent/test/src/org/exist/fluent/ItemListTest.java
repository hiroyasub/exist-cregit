begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ItemListTest
extends|extends
name|DatabaseTestCase
block|{
annotation|@
name|Test
specifier|public
name|void
name|equals1
parameter_list|()
block|{
name|ItemList
name|list1
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
decl_stmt|,
name|list2
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|list1
operator|.
name|equals
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|list2
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
name|equals2
parameter_list|()
block|{
name|ItemList
name|list1
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
decl_stmt|,
name|list2
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 4)"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|list1
operator|.
name|equals
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
comment|// can't assert anything about their hashcodes
block|}
annotation|@
name|Test
specifier|public
name|void
name|equals3
parameter_list|()
block|{
name|ItemList
name|list1
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
decl_stmt|,
name|list2
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2)"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|list1
operator|.
name|equals
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
comment|// can't assert anything about their hashcodes
block|}
annotation|@
name|Test
specifier|public
name|void
name|equals4
parameter_list|()
block|{
name|ItemList
name|list1
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2)"
argument_list|)
decl_stmt|,
name|list2
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|list1
operator|.
name|equals
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
comment|// can't assert anything about their hashcodes
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodesEquals1
parameter_list|()
block|{
name|ItemList
operator|.
name|NodesFacet
name|list1
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
operator|.
name|nodes
argument_list|()
decl_stmt|,
name|list2
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|list1
operator|.
name|equals
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|list2
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
name|valuesEquals1
parameter_list|()
block|{
name|ItemList
operator|.
name|ValuesFacet
name|list1
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|,
name|list2
init|=
name|db
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"(1, 2, 3)"
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|list1
operator|.
name|equals
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|list1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|list2
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
name|convertToSequence
parameter_list|()
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/top"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|build
argument_list|(
name|Name
operator|.
name|create
argument_list|(
name|db
argument_list|,
literal|"test"
argument_list|)
argument_list|)
operator|.
name|elem
argument_list|(
literal|"a"
argument_list|)
operator|.
name|elem
argument_list|(
literal|"b"
argument_list|)
operator|.
name|elem
argument_list|(
literal|"c"
argument_list|)
operator|.
name|end
argument_list|(
literal|"c"
argument_list|)
operator|.
name|end
argument_list|(
literal|"b"
argument_list|)
operator|.
name|elem
argument_list|(
literal|"d"
argument_list|)
operator|.
name|elem
argument_list|(
literal|"c"
argument_list|)
operator|.
name|end
argument_list|(
literal|"c"
argument_list|)
operator|.
name|end
argument_list|(
literal|"d"
argument_list|)
operator|.
name|elem
argument_list|(
literal|"c"
argument_list|)
operator|.
name|end
argument_list|(
literal|"c"
argument_list|)
operator|.
name|end
argument_list|(
literal|"a"
argument_list|)
operator|.
name|commit
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"//c"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ItemList
name|res
init|=
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"//(b|d)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"$_1//c"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|res
block|}
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DatabaseException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|stale1
parameter_list|()
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/top"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|(
name|db
argument_list|)
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo><bar1/><bar2/></foo>"
argument_list|)
argument_list|)
decl_stmt|;
name|ItemList
name|list
init|=
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"/foo/*"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"//bar1"
argument_list|)
operator|.
name|deleteAllNodes
argument_list|()
expr_stmt|;
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"$_1"
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|stale2
parameter_list|()
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/top"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|(
name|db
argument_list|)
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo><bar1/><bar2/></foo>"
argument_list|)
argument_list|)
decl_stmt|;
name|ItemList
name|list
init|=
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"/foo/*"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"//bar1"
argument_list|)
operator|.
name|deleteAllNodes
argument_list|()
expr_stmt|;
name|list
operator|.
name|removeDeletedNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"$_1"
argument_list|,
name|list
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteAllNodes1
parameter_list|()
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/top"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|(
name|db
argument_list|)
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo><bar><bar/></bar></foo>"
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"//bar"
argument_list|)
operator|.
name|deleteAllNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<foo/>"
argument_list|,
name|doc
operator|.
name|contentsAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteAllNodes2
parameter_list|()
block|{
name|XMLDocument
name|doc
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/top"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|generate
argument_list|(
name|db
argument_list|)
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<bar><bar><bar/></bar></bar>"
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|query
argument_list|()
operator|.
name|all
argument_list|(
literal|"//bar"
argument_list|)
operator|.
name|deleteAllNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|db
operator|.
name|getFolder
argument_list|(
literal|"/top"
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

