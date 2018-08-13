begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|sort
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
name|backup
operator|.
name|RawDataBackup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|AbstractIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|RawBackupSupport
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
name|btree
operator|.
name|DBException
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
name|index
operator|.
name|BTreeStore
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
name|LockManager
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
name|ManagedLock
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
name|util
operator|.
name|LockException
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
name|io
operator|.
name|OutputStream
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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * SortIndex helps to improve the performance of 'order by' expressions in XQuery.  * The index simply maps node ids to an integer index, which corresponds to the position  * of the node in the pre-ordered set.  *<p>  * The creation and maintenance of the index is handled by the user. XQuery functions  * are provided to create, delete and query an index.  *<p>  * Every sort index has an id by which it is identified and distinguished from other indexes  * on the same node set.  */
end_comment

begin_class
specifier|public
class|class
name|SortIndex
extends|extends
name|AbstractIndex
implements|implements
name|RawBackupSupport
block|{
specifier|public
specifier|static
specifier|final
name|String
name|ID
init|=
name|SortIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"sort.dbx"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|SORT_INDEX_ID
init|=
literal|0x10
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SortIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BTreeStore
name|btree
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
specifier|final
name|Path
name|file
init|=
name|getDataDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|FILE_NAME
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating '"
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
try|try
block|{
name|btree
operator|=
operator|new
name|BTreeStore
argument_list|(
name|pool
argument_list|,
name|SORT_INDEX_ID
argument_list|,
literal|false
argument_list|,
name|file
argument_list|,
name|pool
operator|.
name|getCacheManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to initialize structural index: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|DBException
block|{
name|btree
operator|.
name|close
argument_list|()
expr_stmt|;
name|btree
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
block|{
if|if
condition|(
name|btree
operator|==
literal|null
condition|)
return|return;
specifier|final
name|LockManager
name|lockManager
init|=
name|pool
operator|.
name|getLockManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReentrantLock
argument_list|>
name|btreeLock
init|=
name|lockManager
operator|.
name|acquireBtreeWriteLock
argument_list|(
name|btree
operator|.
name|getLockName
argument_list|()
argument_list|)
init|)
block|{
name|btree
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to acquire lock for '"
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|btree
operator|.
name|getFile
argument_list|()
argument_list|)
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception ? -pb
block|}
catch|catch
parameter_list|(
specifier|final
name|DBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//TODO : throw an exception ? -pb
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
block|{
name|btree
operator|.
name|closeAndRemove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexWorker
name|getWorker
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
operator|new
name|SortIndexWorker
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkIndex
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|backupToArchive
parameter_list|(
specifier|final
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|OutputStream
name|os
init|=
name|backup
operator|.
name|newEntry
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|btree
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
name|btree
operator|.
name|backupToStream
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|backup
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

