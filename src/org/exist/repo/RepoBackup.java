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
name|*
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
name|File
name|backup
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipOutputStream
name|os
init|=
literal|null
decl_stmt|;
name|File
name|tempFile
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|File
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
name|tempFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"expathrepo"
argument_list|,
literal|"zip"
argument_list|)
expr_stmt|;
name|os
operator|=
operator|new
name|ZipOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|)
argument_list|)
expr_stmt|;
name|zipDir
argument_list|(
name|directory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|os
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|Lock
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
name|File
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
name|File
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
name|Lock
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
name|String
name|directory
parameter_list|,
name|ZipOutputStream
name|zos
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|File
name|zipDir
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// get a listing of the directory content
specifier|final
name|String
index|[]
name|dirList
init|=
name|zipDir
operator|.
name|list
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
literal|2156
index|]
decl_stmt|;
name|int
name|bytesIn
init|=
literal|0
decl_stmt|;
comment|// loop through dirList, and zip the files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|zipDir
argument_list|,
name|dirList
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
specifier|final
name|String
name|filePath
init|=
name|f
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|zipDir
argument_list|(
name|filePath
argument_list|,
name|zos
argument_list|,
name|path
operator|+
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|ZipEntry
name|anEntry
init|=
operator|new
name|ZipEntry
argument_list|(
name|path
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
name|anEntry
argument_list|)
expr_stmt|;
name|bytesIn
operator|=
name|fis
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
while|while
condition|(
name|bytesIn
operator|!=
operator|-
literal|1
condition|)
block|{
name|zos
operator|.
name|write
argument_list|(
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|bytesIn
argument_list|)
expr_stmt|;
name|bytesIn
operator|=
name|fis
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/***      * Extract zipfile to outdir with complete directory structure.      *      * @param zipfile Input .zip file      * @param outdir Output directory      */
specifier|public
specifier|static
name|void
name|unzip
parameter_list|(
name|File
name|zipfile
parameter_list|,
name|File
name|outdir
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipInputStream
name|zin
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zin
operator|=
operator|new
name|ZipInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|zipfile
argument_list|)
argument_list|)
expr_stmt|;
name|ZipEntry
name|entry
decl_stmt|;
name|String
name|name
decl_stmt|,
name|dir
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
name|name
operator|=
name|entry
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|mkdirs
argument_list|(
name|outdir
argument_list|,
name|name
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|dir
operator|=
name|dirpart
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|mkdirs
argument_list|(
name|outdir
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
name|extractFile
argument_list|(
name|zin
argument_list|,
name|outdir
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|zin
operator|!=
literal|null
condition|)
try|try
block|{
name|zin
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|extractFile
parameter_list|(
name|ZipInputStream
name|in
parameter_list|,
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
specifier|final
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|count
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|mkdirs
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|path
parameter_list|)
block|{
specifier|final
name|File
name|d
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|d
operator|.
name|exists
argument_list|()
condition|)
block|{
name|d
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|dirpart
parameter_list|(
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

