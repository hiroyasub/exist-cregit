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

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: wolf  * Date: Jun 10, 2007  * Time: 8:31:15 AM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_class
specifier|public
class|class
name|CacheManager
implements|implements
name|CacheManagerMBean
block|{
specifier|private
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
name|manager
operator|=
name|manager
expr_stmt|;
block|}
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

