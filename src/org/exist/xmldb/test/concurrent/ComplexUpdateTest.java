begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
operator|.
name|concurrent
package|;
end_package

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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ComplexUpdateTest
extends|extends
name|ConcurrentTestBase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<TEST><USER-SESSION-DATA version=\"1\"/></TEST>"
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ComplexUpdateTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param name 	 * @param uri 	 * @param testCollection 	 */
specifier|public
name|ComplexUpdateTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|URI
argument_list|,
literal|"complex"
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.test.concurrent.ConcurrentTestBase#setUp() 	 */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|getTestCollection
argument_list|()
operator|.
name|createResource
argument_list|(
literal|"R01.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|XML
argument_list|)
expr_stmt|;
name|getTestCollection
argument_list|()
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|getTestCollection
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|ComplexUpdateAction
argument_list|(
name|URI
operator|+
literal|"/complex"
argument_list|,
literal|"R01.xml"
argument_list|,
literal|10000
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

