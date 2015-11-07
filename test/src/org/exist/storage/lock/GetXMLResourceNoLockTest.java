begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|BinaryDocument
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
name|TestUtils
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
name|test
operator|.
name|TestConstants
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
name|start
operator|.
name|Main
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
name|DBBroker
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
name|TransactionManager
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

begin_comment
comment|/**  *   * @author Patrick Bosek<patrick.bosek@jorsek.com>  *  */
end_comment

begin_class
specifier|public
class|class
name|GetXMLResourceNoLockTest
block|{
specifier|private
name|Path
name|dataDirBackup
decl_stmt|;
specifier|private
name|Main
name|database
decl_stmt|;
specifier|private
specifier|static
name|String
name|EMPTY_BINARY_FILE
init|=
literal|"What's an up dog?"
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|DOCUMENT_NAME_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"empty.txt"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testCollectionMaintainsLockWhenResourceIsSelectedNoLock
parameter_list|()
throws|throws
name|EXistException
throws|,
name|InterruptedException
block|{
name|storeTestResource
argument_list|()
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
try|try
block|{
name|XmldbURI
name|docPath
init|=
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|DOCUMENT_NAME_URI
argument_list|)
decl_stmt|;
name|BinaryDocument
name|binDoc
init|=
operator|(
name|BinaryDocument
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|docPath
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
comment|// if document is not present, null is returned
if|if
condition|(
name|binDoc
operator|==
literal|null
condition|)
name|fail
argument_list|(
literal|"Binary document '"
operator|+
name|docPath
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Collection does not have lock!"
argument_list|,
literal|true
argument_list|,
name|testCollection
operator|.
name|getLock
argument_list|()
operator|.
name|hasLock
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
name|testCollection
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Error opening document"
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|dataDirBackup
operator|=
name|TestUtils
operator|.
name|moveDataDirToTempAndCreateClean
argument_list|()
expr_stmt|;
name|database
operator|=
name|TestUtils
operator|.
name|startupDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|TestUtils
operator|.
name|stopDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|TestUtils
operator|.
name|moveDataDirBack
argument_list|(
name|dataDirBackup
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeTestResource
parameter_list|()
throws|throws
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
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
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|BinaryDocument
name|doc
init|=
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|DOCUMENT_NAME_URI
argument_list|,
name|EMPTY_BINARY_FILE
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"text/text"
argument_list|)
decl_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

