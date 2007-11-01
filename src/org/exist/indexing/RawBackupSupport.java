begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Interface to be implemented by an index if it wants to add raw data  * files to a raw system backup. This features is mainly used by the  * {@link org.exist.storage.DataBackup} system task.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RawBackupSupport
block|{
name|void
name|backupToArchive
parameter_list|(
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

