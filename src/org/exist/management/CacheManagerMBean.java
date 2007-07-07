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
comment|/**  * Created by IntelliJ IDEA.  * User: wolf  * Date: Jun 9, 2007  * Time: 10:33:21 PM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CacheManagerMBean
block|{
name|long
name|getMaxTotal
parameter_list|()
function_decl|;
name|long
name|getMaxSingle
parameter_list|()
function_decl|;
name|long
name|getCurrentSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

