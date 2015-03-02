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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|Database
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
name|ConfigurationHelper
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|StoreBinaryTest
block|{
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|ensureCleanDatabase
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|home
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"webapp/WEB-INF/data"
argument_list|)
decl_stmt|;
name|File
name|dataFiles
index|[]
init|=
name|data
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|endsWith
argument_list|(
literal|".dbx"
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|".journal"
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|".log"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|dataFile
range|:
name|dataFiles
control|)
block|{
name|dataFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|subFolderName
range|:
operator|new
name|String
index|[]
block|{
literal|"journal"
block|,
literal|"fs"
block|,
literal|"sanity"
block|,
literal|"lucene"
block|}
control|)
block|{
name|File
name|subFolder
init|=
operator|new
name|File
argument_list|(
name|data
argument_list|,
name|subFolderName
argument_list|)
decl_stmt|;
if|if
condition|(
name|subFolder
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|subFolder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|check_MimeType_is_preserved
parameter_list|()
throws|throws
name|EXistException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|xqueryMimeType
init|=
literal|"application/xquery"
decl_stmt|;
specifier|final
name|String
name|xqueryFilename
init|=
literal|"script.xql"
decl_stmt|;
specifier|final
name|String
name|xquery
init|=
literal|"current-dateTime()"
decl_stmt|;
name|Main
name|database
init|=
name|startupDatabase
argument_list|()
decl_stmt|;
try|try
block|{
comment|//store the xquery document
name|BinaryDocument
name|binaryDoc
init|=
name|storeBinary
argument_list|(
name|xqueryFilename
argument_list|,
name|xquery
argument_list|,
name|xqueryMimeType
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|binaryDoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xqueryMimeType
argument_list|,
name|binaryDoc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
comment|//make a note of the binary documents uri
specifier|final
name|XmldbURI
name|binaryDocUri
init|=
name|binaryDoc
operator|.
name|getFileURI
argument_list|()
decl_stmt|;
comment|//restart the database
name|stopDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|database
operator|=
name|startupDatabase
argument_list|()
expr_stmt|;
comment|//retrieve the xquery document
name|binaryDoc
operator|=
name|getBinary
argument_list|(
name|binaryDocUri
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|binaryDoc
argument_list|)
expr_stmt|;
comment|//check the mimetype has been preserved across database restarts
name|assertEquals
argument_list|(
name|xqueryMimeType
argument_list|,
name|binaryDoc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Main
name|startupDatabase
parameter_list|()
block|{
name|Main
name|database
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
argument_list|(
literal|"jetty"
argument_list|)
decl_stmt|;
name|database
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
return|return
name|database
return|;
block|}
specifier|private
name|void
name|stopDatabase
parameter_list|(
name|Main
name|database
parameter_list|)
block|{
try|try
block|{
name|database
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do not fail. exceptions may occur at this point.
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|BinaryDocument
name|getBinary
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|EXistException
block|{
name|BinaryDocument
name|binaryDoc
init|=
literal|null
decl_stmt|;
name|Database
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
empty_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
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
name|binaryDoc
operator|=
operator|(
name|BinaryDocument
operator|)
name|root
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|uri
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
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|binaryDoc
return|;
block|}
specifier|private
name|BinaryDocument
name|storeBinary
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|data
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|EXistException
block|{
specifier|final
name|Database
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
empty_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|BinaryDocument
name|binaryDoc
init|=
literal|null
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
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
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|binaryDoc
operator|=
name|root
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
argument_list|,
name|data
operator|.
name|getBytes
argument_list|()
argument_list|,
name|mimeType
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
return|return
name|binaryDoc
return|;
block|}
block|}
end_class

end_unit

