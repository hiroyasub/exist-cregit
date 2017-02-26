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
name|TestUtils
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
name|IndexInfo
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
name|util
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|List
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

begin_comment
comment|/**  * Test crash recovery after reindexing a collection.  */
end_comment

begin_class
specifier|public
class|class
name|ReindexTest
block|{
comment|// we don't use @ClassRule/@Rule as we want to force corruption in some tests
specifier|private
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Path
name|dir
init|=
name|TestUtils
operator|.
name|shakespeareSamples
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|reindexTests
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
throws|,
name|LockException
throws|,
name|TriggerException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDb
argument_list|()
decl_stmt|;
name|storeDocuments
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|stopDb
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|pool
operator|=
name|startDb
argument_list|()
expr_stmt|;
name|removeCollection
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|stopDb
argument_list|()
expr_stmt|;
name|restart
argument_list|()
expr_stmt|;
block|}
comment|/**      * Store some documents, reindex the collection and crash without commit.      */
specifier|public
name|void
name|storeDocuments
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|LockException
block|{
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
init|)
block|{
try|try
init|(
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
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|Collection
name|root
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
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
init|=
name|FileUtils
operator|.
name|list
argument_list|(
name|dir
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|f
range|:
name|files
control|)
block|{
specifier|final
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|==
literal|null
operator|||
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|IndexInfo
name|info
init|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|root
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error found while parsing document: "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|broker
operator|.
name|reindexCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getJournalManager
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Recover, remove the collection, then crash after commit.      */
specifier|public
name|void
name|removeCollection
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
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
init|;
init|)
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|Collection
name|root
init|=
literal|null
decl_stmt|;
try|try
block|{
name|root
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|acquireLock
argument_list|(
name|root
operator|.
name|getLock
argument_list|()
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getJournalManager
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|root
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
comment|/**      * Just recover.      */
specifier|public
name|void
name|restart
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDb
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
init|)
block|{
name|Collection
name|root
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Removed collection does still exist"
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|BrokerPool
name|startDb
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
name|existEmbeddedServer
operator|.
name|startDb
argument_list|()
expr_stmt|;
return|return
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopDb
parameter_list|()
block|{
name|existEmbeddedServer
operator|.
name|stopDb
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

