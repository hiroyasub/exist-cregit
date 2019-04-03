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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|*
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
name|*
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
name|*
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

begin_class
specifier|public
class|class
name|RemoveRootCollectionTest
block|{
specifier|private
name|DBBroker
name|broker
decl_stmt|;
name|Collection
name|root
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|removeEmptyRootCollection
parameter_list|()
throws|throws
name|Exception
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
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|getChildCollectionCount
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|getDocumentCount
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeRootCollectionWithChildCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|addChildToRoot
argument_list|()
expr_stmt|;
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
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|getChildCollectionCount
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|getDocumentCount
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|removeRootCollectionWithDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|addDocumentToRoot
argument_list|()
expr_stmt|;
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
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|getChildCollectionCount
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|getDocumentCount
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
specifier|public
specifier|final
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
name|startDB
parameter_list|()
throws|throws
name|Exception
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
name|root
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopDB
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
block|}
specifier|private
name|void
name|addDocumentToRoot
parameter_list|()
throws|throws
name|Exception
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
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|TestUtils
operator|.
name|resolveShakespeareSample
argument_list|(
literal|"hamlet.xml"
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
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
literal|"hamlet.xml"
argument_list|)
argument_list|,
name|is
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
name|is
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addChildToRoot
parameter_list|()
throws|throws
name|Exception
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
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|child
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"child"
argument_list|)
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

