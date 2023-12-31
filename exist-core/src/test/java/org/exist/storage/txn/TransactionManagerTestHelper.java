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
name|SecurityManager
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
name|Subject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|NativeBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|SystemTaskManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|JournalManager
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
name|easymock
operator|.
name|EasyMock
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|TransactionManagerTestHelper
block|{
name|BrokerPool
name|mockBrokerPool
init|=
literal|null
decl_stmt|;
name|NativeBroker
name|mockBroker
init|=
literal|null
decl_stmt|;
specifier|protected
name|TransactionManager
name|createTestableTransactionManager
parameter_list|(
specifier|final
name|boolean
name|expectTxnClose
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
throws|,
name|EXistException
block|{
name|mockBrokerPool
operator|=
name|createMock
argument_list|(
name|BrokerPool
operator|.
name|class
argument_list|)
expr_stmt|;
name|mockBroker
operator|=
name|createMock
argument_list|(
name|NativeBroker
operator|.
name|class
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockBrokerPool
operator|.
name|getBroker
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockBroker
argument_list|)
operator|.
name|atLeastOnce
argument_list|()
expr_stmt|;
name|mockBroker
operator|.
name|addCurrentTransaction
argument_list|(
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|atLeastOnce
argument_list|()
expr_stmt|;
if|if
condition|(
name|expectTxnClose
condition|)
block|{
name|mockBroker
operator|.
name|removeCurrentTransaction
argument_list|(
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|atLeastOnce
argument_list|()
expr_stmt|;
block|}
name|mockBroker
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectLastCall
argument_list|()
operator|.
name|atLeastOnce
argument_list|()
expr_stmt|;
specifier|final
name|SecurityManager
name|mockSecurityManager
init|=
name|createMock
argument_list|(
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Subject
name|mockSystemSubject
init|=
name|createMock
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|mockBrokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|mockSystemSubject
argument_list|)
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockBroker
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|mockBrokerPool
operator|.
name|getSecurityManager
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockSecurityManager
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockSystemSubject
argument_list|)
operator|.
name|anyTimes
argument_list|()
expr_stmt|;
specifier|final
name|JournalManager
name|mockJournalManager
init|=
name|createMock
argument_list|(
name|JournalManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|SystemTaskManager
name|mockTaskManager
init|=
name|createMock
argument_list|(
name|SystemTaskManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|replay
argument_list|(
name|mockBrokerPool
argument_list|,
name|mockBroker
argument_list|,
name|mockSecurityManager
argument_list|)
expr_stmt|;
return|return
operator|new
name|TransactionManager
argument_list|(
name|mockBrokerPool
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|mockJournalManager
argument_list|)
argument_list|,
name|mockTaskManager
argument_list|)
return|;
block|}
specifier|protected
name|void
name|verifyMocks
parameter_list|()
block|{
name|verify
argument_list|(
name|mockBrokerPool
argument_list|,
name|mockBroker
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

