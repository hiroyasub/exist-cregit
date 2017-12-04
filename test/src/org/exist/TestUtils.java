begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

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
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
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
name|util
operator|.
name|FileUtils
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
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
name|Files
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
name|Iterator
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
name|stream
operator|.
name|Stream
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
comment|/**  * Utility functions for working with tests  */
end_comment

begin_class
specifier|public
class|class
name|TestUtils
block|{
comment|/**      * Default Admin username used in tests      */
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_DB_USER
init|=
literal|"admin"
decl_stmt|;
comment|/**      * Default Admin password used in tests      */
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_DB_PWD
init|=
literal|""
decl_stmt|;
comment|/**      * Default Guest username used in tests      */
specifier|public
specifier|static
specifier|final
name|String
name|GUEST_DB_USER
init|=
literal|"guest"
decl_stmt|;
comment|/**      * Default Guest password used in tests      */
specifier|public
specifier|static
specifier|final
name|String
name|GUEST_DB_PWD
init|=
literal|"guest"
decl_stmt|;
comment|/**      * Removes all sub-collections of /db      * except for /db/system      */
specifier|public
specifier|static
name|void
name|cleanupDB
parameter_list|()
block|{
try|try
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
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
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
comment|// Remove all collections below the /db root, except /db/system
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
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return;
block|}
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|root
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|root
operator|.
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc
operator|.
name|getURI
argument_list|()
operator|.
name|lastSegment
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|i
init|=
name|root
operator|.
name|collectionIterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|XmldbURI
name|childName
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|childName
operator|.
name|equals
argument_list|(
literal|"system"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Collection
name|childColl
init|=
literal|null
decl_stmt|;
try|try
block|{
name|childColl
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
name|childName
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|childColl
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|childColl
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|childColl
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
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
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Remove /db/system/config/db and all collection configurations with it
name|Collection
name|dbConfig
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dbConfig
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|XmldbURI
operator|.
name|CONFIG_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"/db"
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbConfig
operator|==
literal|null
condition|)
block|{
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return;
block|}
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|dbConfig
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dbConfig
operator|!=
literal|null
condition|)
block|{
name|dbConfig
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Deletes all data files from the eXist data files directory      */
specifier|public
specifier|static
name|void
name|cleanupDataDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|DatabaseConfigurationException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|data
init|=
operator|(
name|Path
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Stream
argument_list|<
name|Path
argument_list|>
name|dataFiles
init|=
name|Files
operator|.
name|list
argument_list|(
name|data
argument_list|)
init|)
block|{
name|dataFiles
operator|.
name|filter
argument_list|(
name|path
lambda|->
operator|!
operator|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
literal|"RECOVERY"
argument_list|)
operator|||
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
literal|"README"
argument_list|)
operator|||
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
operator|.
name|equals
argument_list|(
literal|".DO_NOT_DELETE"
argument_list|)
operator|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|FileUtils
operator|::
name|deleteQuietly
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reads the content of a file      *      * @param directory The directory to read from      * @param filename the filename in the directory to read from      *      * @return The content of the file      */
specifier|public
specifier|static
name|byte
index|[]
name|readFile
parameter_list|(
specifier|final
name|Path
name|directory
parameter_list|,
specifier|final
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFile
argument_list|(
name|directory
operator|.
name|resolve
argument_list|(
name|filename
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Reads the content of a file      *      * @param file the file to read from      *      * @return The content of the file      */
specifier|public
specifier|static
name|byte
index|[]
name|readFile
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|Files
operator|.
name|isReadable
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**      * Get the EXIST_HOME directory      *      * @return The absolute path to the EXIST_HOME folder      *   or {@link Optional#empty()}      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|Path
argument_list|>
name|getEXistHome
parameter_list|()
block|{
return|return
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
operator|.
name|map
argument_list|(
name|Path
operator|::
name|toAbsolutePath
argument_list|)
return|;
block|}
comment|/**      * Get a file from within the EXIST_HOME directory      *      * @param fileName Just the name of the file.      *      * @return The path if it exists      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|Path
argument_list|>
name|getExistHomeFile
parameter_list|(
specifier|final
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|path
init|=
name|getEXistHome
argument_list|()
operator|.
name|orElseGet
argument_list|(
parameter_list|()
lambda|->
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|resolve
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
comment|/**      * Reads the content of the sample hamlet.xml      *      * @return The content of the file      */
specifier|public
specifier|static
name|byte
index|[]
name|readHamletSampleXml
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readFile
argument_list|(
name|resolveHamletSample
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Reads the content of the sample r_and_j.xml      *      * @return The content of the file      */
specifier|public
specifier|static
name|byte
index|[]
name|readRomeoAndJulietSampleXml
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readFile
argument_list|(
name|resolveRomeoAndJulietSample
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Reads the content of the sample file      *      * @param sampleRelativePath The path of the sample file relative to the samples directory      *      * @return The content of the file      */
specifier|public
specifier|static
name|byte
index|[]
name|readSample
parameter_list|(
specifier|final
name|String
name|sampleRelativePath
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|file
init|=
name|resolveSample
argument_list|(
name|sampleRelativePath
argument_list|)
decl_stmt|;
return|return
name|readFile
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**      * Resolve the path of a sample file      *      * @param relativePath The path of the sample file relative to the samples directory      *      * @return The absolute path to the sample file      */
specifier|public
specifier|static
name|Path
name|resolveSample
parameter_list|(
specifier|final
name|String
name|relativePath
parameter_list|)
block|{
specifier|final
name|Path
name|samples
init|=
name|FileUtils
operator|.
name|resolve
argument_list|(
name|getEXistHome
argument_list|()
argument_list|,
literal|"samples"
argument_list|)
decl_stmt|;
return|return
name|samples
operator|.
name|resolve
argument_list|(
name|relativePath
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Shakespeare samples      *      * @return The path to the Shakespeare samples      */
specifier|public
specifier|static
name|Path
name|shakespeareSamples
parameter_list|()
block|{
return|return
name|resolveSample
argument_list|(
literal|"shakespeare"
argument_list|)
return|;
block|}
comment|/**      * Resolve the path of a Shakespeare sample file      *      * @param relativePath The path of the Shakespeare sample file relative to the Shakespeare samples directory      *      * @return The absolute path to the sample file      */
specifier|public
specifier|static
name|Path
name|resolveShakespeareSample
parameter_list|(
specifier|final
name|String
name|relativePath
parameter_list|)
block|{
return|return
name|shakespeareSamples
argument_list|()
operator|.
name|resolve
argument_list|(
name|relativePath
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Shakespeare Hamlet sample      *      * @return The path to the Shakespeare Hamlet sample      */
specifier|public
specifier|static
name|Path
name|resolveHamletSample
parameter_list|()
block|{
return|return
name|resolveShakespeareSample
argument_list|(
literal|"hamlet.xml"
argument_list|)
return|;
block|}
comment|/**      * Gets the path of the Shakespeare Romeo and Juliet sample      *      * @return The path to the Shakespeare Romeo and Juliet sample      */
specifier|public
specifier|static
name|Path
name|resolveRomeoAndJulietSample
parameter_list|()
block|{
return|return
name|resolveShakespeareSample
argument_list|(
literal|"r_and_j.xml"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

