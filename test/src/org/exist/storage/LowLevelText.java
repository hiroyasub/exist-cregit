begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
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
name|CompiledXQuery
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
name|exist
operator|.
name|xquery
operator|.
name|XQuery
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
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Currently, tests for the {@link org.exist.storage.XQueryPool}  * with the {@link StringSource}.   */
end_comment

begin_class
specifier|public
class|class
name|LowLevelText
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_XQUERY_SOURCE
init|=
literal|"/test"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MY_TEST_INSTANCE
init|=
literal|"exist"
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|XQueryPool
name|pool
decl_stmt|;
specifier|private
name|StringSource
name|stringSource
decl_stmt|;
specifier|private
name|CompiledXQuery
name|preCompiledXQuery
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
throws|,
name|XPathException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
name|MY_TEST_INSTANCE
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|brokerPool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|MY_TEST_INSTANCE
argument_list|)
expr_stmt|;
comment|//BUG: need to be released!
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|XQueryPool
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|stringSource
operator|=
operator|new
name|StringSource
argument_list|(
name|TEST_XQUERY_SOURCE
argument_list|)
expr_stmt|;
name|XQuery
name|xquery
init|=
name|brokerPool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|preCompiledXQuery
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|stringSource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|borrowCompiledXQuery1
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
comment|// put the preCompiledXQuery in cache - NOTE: returnCompiledXQuery() is not a good name
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|stringSource
argument_list|,
name|preCompiledXQuery
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|stringSource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|borrowCompiledXQuery2
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|stringSource
argument_list|,
name|preCompiledXQuery
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|stringSource
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|stringSource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|borrowCompiledXQuery3
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|stringSource
argument_list|,
name|preCompiledXQuery
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|stringSource
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|stringSource
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|stringSource
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * test with a new StringSource object having same content 	 */
annotation|@
name|Test
specifier|public
name|void
name|borrowCompiledXQueryNewStringSource
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|stringSource
argument_list|,
name|preCompiledXQuery
argument_list|)
expr_stmt|;
name|StringSource
name|localStringSource
init|=
operator|new
name|StringSource
argument_list|(
name|TEST_XQUERY_SOURCE
argument_list|)
decl_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|localStringSource
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * test with a new StringSource object having same content 	 */
annotation|@
name|Test
specifier|public
name|void
name|borrowCompiledXQueryNewStringSource2
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|stringSource
argument_list|,
name|preCompiledXQuery
argument_list|)
expr_stmt|;
name|StringSource
name|localStringSource
init|=
operator|new
name|StringSource
argument_list|(
name|TEST_XQUERY_SOURCE
argument_list|)
decl_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|localStringSource
argument_list|)
expr_stmt|;
name|callAndTestBorrowCompiledXQuery
argument_list|(
name|localStringSource
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|callAndTestBorrowCompiledXQuery
parameter_list|(
name|StringSource
name|stringSourceArg
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|CompiledXQuery
name|compiledXQuery
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|stringSourceArg
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"borrowCompiledXQuery should retrieve something for this stringSource"
argument_list|,
name|compiledXQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"borrowCompiledXQuery should retrieve the preCompiled XQuery for this stringSource"
argument_list|,
name|compiledXQuery
argument_list|,
name|preCompiledXQuery
argument_list|)
expr_stmt|;
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|stringSourceArg
argument_list|,
name|compiledXQuery
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

