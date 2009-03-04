begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Implementation of BackupWriter that writes to the file system.  */
end_comment

begin_class
specifier|public
class|class
name|FileSystemWriter
implements|implements
name|BackupWriter
block|{
specifier|private
name|File
name|currentDir
decl_stmt|;
specifier|private
name|File
name|currentContents
decl_stmt|;
specifier|private
name|Writer
name|currentContentsOut
decl_stmt|;
specifier|private
name|OutputStream
name|currentOut
decl_stmt|;
specifier|private
name|boolean
name|dataWritten
init|=
literal|false
decl_stmt|;
specifier|public
name|FileSystemWriter
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileSystemWriter
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|//removing "path"
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|currentDir
operator|=
name|file
expr_stmt|;
block|}
specifier|public
name|void
name|newCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataWritten
operator|=
literal|true
expr_stmt|;
name|currentDir
operator|=
name|file
expr_stmt|;
block|}
specifier|public
name|void
name|closeCollection
parameter_list|()
block|{
name|currentDir
operator|=
name|currentDir
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
block|}
specifier|public
name|Writer
name|newContents
parameter_list|()
throws|throws
name|IOException
block|{
name|currentContents
operator|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
literal|"__contents__.xml"
argument_list|)
expr_stmt|;
name|currentContentsOut
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|currentContents
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|dataWritten
operator|=
literal|true
expr_stmt|;
return|return
name|currentContentsOut
return|;
block|}
specifier|public
name|void
name|closeContents
parameter_list|()
throws|throws
name|IOException
block|{
name|currentContentsOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|OutputStream
name|newEntry
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|currentOut
operator|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|dataWritten
operator|=
literal|true
expr_stmt|;
return|return
name|currentOut
return|;
block|}
specifier|public
name|void
name|closeEntry
parameter_list|()
throws|throws
name|IOException
block|{
name|currentOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataWritten
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Backup properties need to be set before any backup data is written"
argument_list|)
throw|;
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
literal|"backup.properties"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|propFile
argument_list|)
decl_stmt|;
name|properties
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"Backup properties"
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

