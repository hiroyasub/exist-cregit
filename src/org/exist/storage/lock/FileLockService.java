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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|BrokerPoolService
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
name|BrokerPoolServiceException
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
name|ReadOnlyException
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
name|Optional
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
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * A Simple Service wrapper for {@link FileLock}  */
end_comment

begin_class
specifier|public
class|class
name|FileLockService
implements|implements
name|BrokerPoolService
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FileLockService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|lockFileName
decl_stmt|;
specifier|private
specifier|final
name|String
name|confDirPropName
decl_stmt|;
specifier|private
specifier|final
name|String
name|defaultDirName
decl_stmt|;
specifier|private
name|Path
name|dataDir
decl_stmt|;
specifier|private
name|boolean
name|writable
decl_stmt|;
specifier|private
name|AtomicReference
argument_list|<
name|FileLock
argument_list|>
name|dataLock
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|FileLockService
parameter_list|(
specifier|final
name|String
name|lockFileName
parameter_list|,
specifier|final
name|String
name|confDirPropName
parameter_list|,
specifier|final
name|String
name|defaultDirName
parameter_list|)
block|{
name|this
operator|.
name|lockFileName
operator|=
name|lockFileName
expr_stmt|;
name|this
operator|.
name|confDirPropName
operator|=
name|confDirPropName
expr_stmt|;
name|this
operator|.
name|defaultDirName
operator|=
name|defaultDirName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|dataDir
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
operator|(
name|Path
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|confDirPropName
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|defaultDirName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
try|try
block|{
comment|//TODO : shall we force the creation ? use a parameter to decide ?
name|LOG
operator|.
name|info
argument_list|(
literal|"Data directory '"
operator|+
name|dataDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"' does not exist. Creating one ..."
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SecurityException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BrokerPoolServiceException
argument_list|(
literal|"Cannot create data directory '"
operator|+
name|dataDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|//Save it for further use.
name|configuration
operator|.
name|setProperty
argument_list|(
name|confDirPropName
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isWritable
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot write to data directory: "
operator|+
name|dataDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writable
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|writable
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|// try to acquire lock on the data dir
specifier|final
name|FileLock
name|fileLock
init|=
operator|new
name|FileLock
argument_list|(
name|brokerPool
argument_list|,
name|dataDir
operator|.
name|resolve
argument_list|(
name|lockFileName
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|dataLock
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|fileLock
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|boolean
name|locked
init|=
name|fileLock
operator|.
name|tryLock
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|locked
condition|)
block|{
throw|throw
operator|new
name|BrokerPoolServiceException
argument_list|(
operator|new
name|EXistException
argument_list|(
literal|"The directory seems to be locked by another "
operator|+
literal|"database instance. Found a valid lock file: "
operator|+
name|fileLock
operator|.
name|getFile
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ReadOnlyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|writable
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|writable
condition|)
block|{
name|brokerPool
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Is this directory managed by this File Lock read-only?      *      * @return true if the directory is read-only      */
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
operator|!
name|writable
return|;
block|}
specifier|public
name|Path
name|getFile
parameter_list|()
block|{
specifier|final
name|FileLock
name|fileLock
init|=
name|dataLock
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileLock
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|fileLock
operator|.
name|getFile
argument_list|()
return|;
block|}
block|}
comment|//TODO(AR) instead we should implement a BrokerPoolService#shutdown() and BrokerPoolServicesManager#shutdown()
specifier|public
name|void
name|release
parameter_list|()
block|{
specifier|final
name|FileLock
name|fileLock
init|=
name|dataLock
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileLock
operator|!=
literal|null
condition|)
block|{
name|fileLock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
name|dataLock
operator|.
name|compareAndSet
argument_list|(
name|fileLock
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

