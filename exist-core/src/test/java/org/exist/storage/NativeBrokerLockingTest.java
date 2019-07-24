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
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|ThreadSafe
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
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|storage
operator|.
name|lock
operator|.
name|Lock
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|lock
operator|.
name|LockTable
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
name|lock
operator|.
name|LockTable
operator|.
name|LockEventListener
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
name|txn
operator|.
name|Txn
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
name|ExistEmbeddedServer
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
name|LockException
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
name|Rule
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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

begin_comment
comment|/**  * Tests to check that the acquire/release lease lifetimes  * of various locks used by {@link NativeBroker} functions  * are symmetrical  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|NativeBrokerLockingTest
block|{
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|TEST_COLLECTION
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|COLLECTION_A
init|=
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
literal|"colA"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|COLLECTION_B
init|=
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
literal|"colB"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|TRACE_STACK_DEPTH
init|=
literal|5
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setupTestData
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|createCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|COLLECTION_A
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|COLLECTION_B
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Collection
name|createCollection
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|removeTestData
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
condition|)
block|{
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Unable to remove test collection"
argument_list|)
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|openCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
try|try
init|(
specifier|final
name|Collection
name|collectionA
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|COLLECTION_A
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
comment|//no -op
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|openCollection_doesntExist
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
try|try
init|(
specifier|final
name|Collection
name|collectionNone
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|COLLECTION_A
operator|.
name|append
argument_list|(
literal|"none"
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
name|assertNull
argument_list|(
name|collectionNone
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
specifier|final
name|Collection
name|collectionA
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_B
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|collectionA
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getCollection_doesntExist
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
specifier|final
name|Collection
name|collectionNone
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_B
operator|.
name|append
argument_list|(
literal|"none"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|collectionNone
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getOrCreateCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
specifier|final
name|XmldbURI
name|collectionC
init|=
name|COLLECTION_B
operator|.
name|append
argument_list|(
literal|"colC"
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Collection
name|collectionA
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|collectionC
argument_list|)
init|)
block|{
comment|// no-op
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|moveCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|collectionA
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_A
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|collectionB
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_B
argument_list|)
decl_stmt|;
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
name|broker
operator|.
name|moveCollection
argument_list|(
name|transaction
argument_list|,
name|collectionA
argument_list|,
name|collectionB
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"colA"
argument_list|)
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyEmptyCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|collectionA
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_A
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|collectionB
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_B
argument_list|)
decl_stmt|;
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
name|broker
operator|.
name|copyCollection
argument_list|(
name|transaction
argument_list|,
name|collectionA
argument_list|,
name|collectionB
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"colA"
argument_list|)
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeEmptyCollection
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|LockTable
name|lockTable
init|=
name|brokerPool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|setTraceStackDepth
argument_list|(
name|TRACE_STACK_DEPTH
argument_list|)
expr_stmt|;
specifier|final
name|LockSymmetryListener
name|lockSymmetryListener
init|=
operator|new
name|LockSymmetryListener
argument_list|()
decl_stmt|;
name|boolean
name|registered
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|collectionA
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLECTION_A
argument_list|)
decl_stmt|;
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be registered
while|while
condition|(
operator|!
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
empty_stmt|;
name|registered
operator|=
literal|true
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|collectionA
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|registered
condition|)
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockSymmetryListener
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockSymmetryListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
name|assertTrue
argument_list|(
name|lockSymmetryListener
operator|.
name|isSymmetrical
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ThreadSafe
specifier|private
specifier|static
class|class
name|LockSymmetryListener
implements|implements
name|LockEventListener
block|{
specifier|private
specifier|final
name|Stack
argument_list|<
name|LockAction
argument_list|>
name|events
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Stack
argument_list|<
name|LockAction
argument_list|>
name|eventsAfterError
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|registered
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|error
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|// indicates if lock acquire/release is no longer symmetrical
annotation|@
name|Override
specifier|public
name|void
name|registered
parameter_list|()
block|{
name|registered
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregistered
parameter_list|()
block|{
name|registered
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRegistered
parameter_list|()
block|{
return|return
name|registered
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isSymmetrical
parameter_list|()
block|{
return|return
operator|!
name|error
operator|.
name|get
argument_list|()
operator|&&
name|events
operator|.
name|empty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|LockTable
operator|.
name|LockEventType
name|lockEventType
parameter_list|,
specifier|final
name|long
name|timestamp
parameter_list|,
specifier|final
name|long
name|groupId
parameter_list|,
specifier|final
name|LockTable
operator|.
name|Entry
name|entry
parameter_list|)
block|{
comment|// read count first to ensure memory visibility from volatile!
specifier|final
name|int
name|localCount
init|=
name|entry
operator|.
name|getCount
argument_list|()
decl_stmt|;
comment|// ignore sync events
specifier|final
name|StackTraceElement
index|[]
name|stackTrace
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getStackTraces
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|entry
operator|.
name|getStackTraces
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|stackTrace
operator|=
name|entry
operator|.
name|getStackTraces
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|reason
init|=
name|LockTable
operator|.
name|getSimpleStackReason
argument_list|(
name|stackTrace
argument_list|)
decl_stmt|;
if|if
condition|(
name|reason
operator|!=
literal|null
operator|&&
operator|(
name|reason
operator|.
name|equals
argument_list|(
literal|"sync"
argument_list|)
operator|||
name|reason
operator|.
name|equals
argument_list|(
literal|"notifySync"
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
name|stackTrace
operator|=
literal|null
expr_stmt|;
block|}
comment|//            System.out.println(LockTable.formatString(lockEventType, groupId, entry.getId(), entry.getLockType(),
comment|//                    entry.getLockMode(), entry.getOwner(), localCount, timestamp,
comment|//                    stackTrace));
specifier|final
name|LockAction
name|lockAction
init|=
operator|new
name|LockAction
argument_list|(
name|lockEventType
argument_list|,
name|groupId
argument_list|,
name|entry
operator|.
name|getId
argument_list|()
argument_list|,
name|entry
operator|.
name|getLockType
argument_list|()
argument_list|,
name|entry
operator|.
name|getLockMode
argument_list|()
argument_list|,
name|entry
operator|.
name|getOwner
argument_list|()
argument_list|,
name|localCount
argument_list|,
name|timestamp
argument_list|,
name|stackTrace
argument_list|)
decl_stmt|;
if|if
condition|(
name|error
operator|.
name|get
argument_list|()
condition|)
block|{
name|eventsAfterError
operator|.
name|push
argument_list|(
name|lockAction
argument_list|)
expr_stmt|;
return|return;
block|}
switch|switch
condition|(
name|lockEventType
condition|)
block|{
case|case
name|Attempt
case|:
name|events
operator|.
name|push
argument_list|(
name|lockAction
argument_list|)
expr_stmt|;
break|break;
case|case
name|Acquired
case|:
if|if
condition|(
name|isAcquireAfterAttempt
argument_list|(
name|lockAction
argument_list|)
condition|)
block|{
comment|//OK
name|events
operator|.
name|push
argument_list|(
name|lockAction
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//error
name|error
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Released
case|:
if|if
condition|(
name|isSymmetricalRelease
argument_list|(
name|lockAction
argument_list|)
condition|)
block|{
specifier|final
name|LockAction
name|acquired
init|=
name|events
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAcquireAfterAttempt
argument_list|(
name|acquired
argument_list|)
condition|)
block|{
specifier|final
name|LockAction
name|attempt
init|=
name|events
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
comment|//OK
break|break;
block|}
block|}
block|}
comment|//error
name|error
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|lockEventType
operator|+
literal|" should not happen!"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|isAcquireAfterAttempt
parameter_list|(
specifier|final
name|LockAction
name|current
parameter_list|)
block|{
specifier|final
name|LockAction
name|previous
init|=
name|events
operator|.
name|peek
argument_list|()
decl_stmt|;
return|return
name|previous
operator|.
name|lockEventType
operator|==
name|LockTable
operator|.
name|LockEventType
operator|.
name|Attempt
operator|&&
name|isRelated
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isSymmetricalRelease
parameter_list|(
specifier|final
name|LockAction
name|current
parameter_list|)
block|{
specifier|final
name|LockAction
name|previous
init|=
name|events
operator|.
name|peek
argument_list|()
decl_stmt|;
return|return
name|previous
operator|.
name|lockEventType
operator|==
name|LockTable
operator|.
name|LockEventType
operator|.
name|Acquired
operator|&&
name|isRelated
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isRelated
parameter_list|(
specifier|final
name|LockAction
name|lockAction1
parameter_list|,
specifier|final
name|LockAction
name|lockAction2
parameter_list|)
block|{
return|return
name|lockAction1
operator|.
name|lockType
operator|.
name|equals
argument_list|(
name|lockAction2
operator|.
name|lockType
argument_list|)
operator|&&
name|lockAction1
operator|.
name|id
operator|.
name|equals
argument_list|(
name|lockAction2
operator|.
name|id
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|LockAction
block|{
specifier|public
specifier|final
name|LockTable
operator|.
name|LockEventType
name|lockEventType
decl_stmt|;
specifier|public
specifier|final
name|long
name|groupId
decl_stmt|;
specifier|public
specifier|final
name|String
name|id
decl_stmt|;
specifier|public
specifier|final
name|Lock
operator|.
name|LockType
name|lockType
decl_stmt|;
specifier|public
specifier|final
name|Lock
operator|.
name|LockMode
name|mode
decl_stmt|;
specifier|public
specifier|final
name|String
name|threadName
decl_stmt|;
specifier|public
specifier|final
name|int
name|count
decl_stmt|;
comment|/**          * System#nanoTime()          */
specifier|public
specifier|final
name|long
name|timestamp
decl_stmt|;
annotation|@
name|Nullable
specifier|public
specifier|final
name|StackTraceElement
index|[]
name|stackTrace
decl_stmt|;
specifier|private
name|LockAction
parameter_list|(
specifier|final
name|LockTable
operator|.
name|LockEventType
name|lockEventType
parameter_list|,
specifier|final
name|long
name|groupId
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|Lock
operator|.
name|LockType
name|lockType
parameter_list|,
specifier|final
name|Lock
operator|.
name|LockMode
name|mode
parameter_list|,
specifier|final
name|String
name|threadName
parameter_list|,
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|long
name|timestamp
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|StackTraceElement
index|[]
name|stackTrace
parameter_list|)
block|{
name|this
operator|.
name|lockEventType
operator|=
name|lockEventType
expr_stmt|;
name|this
operator|.
name|groupId
operator|=
name|groupId
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|lockType
operator|=
name|lockType
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
name|this
operator|.
name|threadName
operator|=
name|threadName
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|stackTrace
operator|=
name|stackTrace
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

