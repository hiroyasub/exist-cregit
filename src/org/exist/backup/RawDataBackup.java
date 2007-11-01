begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: wolf  * Date: Nov 1, 2007  * Time: 6:51:30 PM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
package|;
end_package

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

begin_comment
comment|/**  * Callback interface, mainly used by the {@link org.exist.storage.DataBackup}  * system task to write the raw data files to an archive..  */
end_comment

begin_interface
specifier|public
interface|interface
name|RawDataBackup
block|{
specifier|public
name|OutputStream
name|newEntry
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
name|void
name|closeEntry
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

