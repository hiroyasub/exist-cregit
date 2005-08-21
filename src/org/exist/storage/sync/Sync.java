begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|sync
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

begin_comment
comment|/**  * This class is registered with the {@link org.exist.storage.sync.SyncDaemon}.  * It will periodically trigger a cache sync to write cached pages to disk.   */
end_comment

begin_class
specifier|public
class|class
name|Sync
implements|implements
name|Runnable
block|{
specifier|public
specifier|final
specifier|static
name|int
name|MINOR_SYNC
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MAJOR_SYNC
init|=
literal|1
decl_stmt|;
specifier|private
name|long
name|majorSyncPeriod
decl_stmt|;
specifier|private
name|long
name|lastMajorSync
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|Sync
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|long
name|majorSyncPeriod
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|majorSyncPeriod
operator|=
name|majorSyncPeriod
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastMajorSync
operator|>
name|majorSyncPeriod
condition|)
block|{
name|pool
operator|.
name|triggerSync
argument_list|(
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
name|lastMajorSync
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|pool
operator|.
name|triggerSync
argument_list|(
name|MINOR_SYNC
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|restart
parameter_list|()
block|{
name|lastMajorSync
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

