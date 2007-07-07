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
comment|/**  * Created by IntelliJ IDEA.  * User: wolf  * Date: Jun 9, 2007  * Time: 8:51:10 PM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CacheMBean
block|{
specifier|public
name|String
name|getType
parameter_list|()
function_decl|;
specifier|public
name|int
name|getSize
parameter_list|()
function_decl|;
specifier|public
name|int
name|getUsed
parameter_list|()
function_decl|;
specifier|public
name|int
name|getHits
parameter_list|()
function_decl|;
specifier|public
name|int
name|getFails
parameter_list|()
function_decl|;
specifier|public
name|String
name|getFileName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

