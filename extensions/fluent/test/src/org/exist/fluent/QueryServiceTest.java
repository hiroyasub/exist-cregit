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
name|QueryServiceTest
extends|extends
name|DatabaseTestCase
block|{
annotation|@
name|Test
specifier|public
name|void
name|analyze1
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"zero-or-one(//blah)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"item()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze2
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"exactly-one(//blah)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ONE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"item()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze3
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"one-or-more(//blah)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"item()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze4
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"//blah"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"node()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze5
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"$blah"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[$blah]"
argument_list|,
name|qa
operator|.
name|requiredVariables
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

