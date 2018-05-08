begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_class
specifier|public
class|class
name|CacheManager
implements|implements
name|CacheManagerMXBean
block|{
specifier|private
specifier|final
name|String
name|instanceId
decl_stmt|;
specifier|private
specifier|final
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|CacheManager
name|manager
decl_stmt|;
specifier|public
name|CacheManager
parameter_list|(
specifier|final
name|String
name|instanceId
parameter_list|,
specifier|final
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|CacheManager
name|manager
parameter_list|)
block|{
name|this
operator|.
name|instanceId
operator|=
name|instanceId
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getAllInstancesQuery
parameter_list|()
block|{
return|return
literal|"org.exist.management."
operator|+
literal|'*'
operator|+
literal|":type=CacheManager"
return|;
block|}
specifier|private
specifier|static
name|ObjectName
name|getName
parameter_list|(
specifier|final
name|String
name|instanceId
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
return|return
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instanceId
operator|+
literal|":type=CacheManager"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectName
name|getName
parameter_list|()
throws|throws
name|MalformedObjectNameException
block|{
return|return
name|getName
argument_list|(
name|instanceId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInstanceId
parameter_list|()
block|{
return|return
name|instanceId
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxTotal
parameter_list|()
block|{
return|return
name|manager
operator|.
name|getMaxTotal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxSingle
parameter_list|()
block|{
return|return
name|manager
operator|.
name|getMaxSingle
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCurrentSize
parameter_list|()
block|{
return|return
name|manager
operator|.
name|getCurrentSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

