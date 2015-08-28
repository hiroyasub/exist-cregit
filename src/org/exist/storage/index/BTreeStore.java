begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|index
package|;
end_package

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
name|DefaultCacheManager
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
name|BTree
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
name|ReentrantReadWriteLock
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_class
specifier|public
class|class
name|BTreeStore
extends|extends
name|BTree
block|{
specifier|public
specifier|final
specifier|static
name|short
name|FILE_FORMAT_VERSION_ID
init|=
literal|2
decl_stmt|;
specifier|protected
name|Lock
name|lock
init|=
literal|null
decl_stmt|;
specifier|public
name|BTreeStore
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|byte
name|fileId
parameter_list|,
name|boolean
name|transactional
parameter_list|,
specifier|final
name|Path
name|file
parameter_list|,
name|DefaultCacheManager
name|cacheManager
parameter_list|)
throws|throws
name|DBException
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|fileId
argument_list|,
name|transactional
argument_list|,
name|cacheManager
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
argument_list|()
condition|)
block|{
name|open
argument_list|(
name|FILE_FORMAT_VERSION_ID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating data file: "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|create
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|setSplitFactor
argument_list|(
literal|0.7
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Lock
name|getLock
parameter_list|()
block|{
return|return
name|lock
return|;
block|}
specifier|public
name|short
name|getFileVersion
parameter_list|()
block|{
return|return
name|FILE_FORMAT_VERSION_ID
return|;
block|}
block|}
end_class

end_unit

