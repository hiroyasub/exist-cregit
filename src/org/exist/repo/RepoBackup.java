begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|repo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|NativeBroker
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
operator|.
name|LockMode
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
name|io
operator|.
name|TemporaryFileManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|IOException
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
name|Files
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_comment
comment|/**  * Utility methods for backing up and restoring the expath file system repository.  */
end_comment

begin_class
specifier|public
class|class
name|RepoBackup
block|{
specifier|public
specifier|final
specifier|static
name|String
name|REPO_ARCHIVE
init|=
literal|"expathrepo.zip"
decl_stmt|;
specifier|public
specifier|static
name|Path
name|backup
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TemporaryFileManager
name|temporaryFileManager
init|=
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|tempFile
init|=
name|temporaryFileManager
operator|.
name|getTemporaryFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|ZipOutputStream
name|os
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|tempFile
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|Path
name|directory
init|=
name|ExistRepository
operator|.
name|getRepositoryDir
argument_list|(
name|broker
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|zipDir
argument_list|(
name|directory
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|os
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|tempFile
return|;
block|}
specifier|public
specifier|static
name|void
name|restore
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|IOException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|XmldbURI
name|docPath
init|=
name|XmldbURI
operator|.
name|createInternal
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|REPO_ARCHIVE
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|docPath
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|docPath
operator|+
literal|" is not a binary resource"
argument_list|)
throw|;
block|}
specifier|final
name|Path
name|file
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getCollectionBinaryFileFsPath
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|directory
init|=
name|ExistRepository
operator|.
name|getRepositoryDir
argument_list|(
name|broker
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|unzip
argument_list|(
name|file
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Zip up a directory path      *      * @param directory      * @param zos      * @param path      * @throws IOException      */
specifier|public
specifier|static
name|void
name|zipDir
parameter_list|(
specifier|final
name|Path
name|directory
parameter_list|,
specifier|final
name|ZipOutputStream
name|zos
parameter_list|,
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get a listing of the directory content
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|dirList
init|=
name|FileUtils
operator|.
name|list
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// loop through dirList, and zip the files
for|for
control|(
specifier|final
name|Path
name|f
range|:
name|dirList
control|)
block|{
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|zipDir
argument_list|(
name|f
argument_list|,
name|zos
argument_list|,
name|path
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
operator|+
literal|"/"
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|ZipEntry
name|anEntry
init|=
operator|new
name|ZipEntry
argument_list|(
name|path
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|anEntry
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|f
argument_list|,
name|zos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/***      * Extract zipfile to outdir with complete directory structure.      *      * @param zipfile Input .zip file      * @param outdir Output directory      */
specifier|public
specifier|static
name|void
name|unzip
parameter_list|(
specifier|final
name|Path
name|zipfile
parameter_list|,
specifier|final
name|Path
name|outdir
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|ZipInputStream
name|zin
init|=
operator|new
name|ZipInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|zipfile
argument_list|)
argument_list|)
init|)
block|{
name|ZipEntry
name|entry
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|zin
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|out
init|=
name|outdir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|out
operator|.
name|startsWith
argument_list|(
name|outdir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Detected archive exit attack! zipFile="
operator|+
name|zipfile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|", entry="
operator|+
name|name
operator|+
literal|", outdir="
operator|+
name|outdir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|outdir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|String
name|dir
init|=
name|dirpart
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|outdir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//extract file
name|Files
operator|.
name|copy
argument_list|(
name|zin
argument_list|,
name|outdir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|dirpart
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
name|int
name|s
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
operator|-
literal|1
condition|?
literal|null
else|:
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit

