begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
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
name|EXistException
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
comment|/**  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
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
name|TxnTest
block|{
specifier|final
name|TransactionManagerTestHelper
name|helper
init|=
operator|new
name|TransactionManagerTestHelper
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|commitTransaction
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|COMMITTED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commitAndCloseTransaction
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
name|transaction
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|abortTransaction
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|ABORTED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|abortAndCloseTransaction
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|transaction
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|repeatedAbortOnlyAbortsTransactionOnce
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
comment|//call 3 times, abort count should be one!
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|ABORTED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|closeWithoutCommitAbortsTransaction
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|repeatedCloseWithoutCommitOnlyAbortsTransactionOnce
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
specifier|final
name|TransactionManager
name|transact
init|=
name|helper
operator|.
name|createTestableTransactionManager
argument_list|()
decl_stmt|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|CountingTxnListener
name|listener
init|=
operator|new
name|CountingTxnListener
argument_list|()
decl_stmt|;
name|transaction
operator|.
name|registerListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
comment|//call 3 times, abort count should be one!
name|transaction
operator|.
name|close
argument_list|()
expr_stmt|;
name|transaction
operator|.
name|close
argument_list|()
expr_stmt|;
name|transaction
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Txn
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|transaction
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listener
operator|.
name|getAbort
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|verifyMocks
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

