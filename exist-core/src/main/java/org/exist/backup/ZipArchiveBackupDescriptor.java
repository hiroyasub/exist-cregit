begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|RepoBackup
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
name|EXistInputSource
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
name|ZipEntryInputSource
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|InputStream
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|ZipFile
import|;
end_import

begin_class
specifier|public
class|class
name|ZipArchiveBackupDescriptor
extends|extends
name|AbstractBackupDescriptor
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|()
decl_stmt|;
specifier|protected
name|ZipFile
name|archive
decl_stmt|;
specifier|protected
name|ZipEntry
name|descriptor
decl_stmt|;
specifier|protected
name|String
name|base
decl_stmt|;
specifier|protected
specifier|final
name|String
name|blob
init|=
literal|"blob/"
decl_stmt|;
specifier|public
name|ZipArchiveBackupDescriptor
parameter_list|(
specifier|final
name|Path
name|fileArchive
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Count number of files
name|countFileEntries
argument_list|(
name|fileArchive
argument_list|)
expr_stmt|;
name|archive
operator|=
operator|new
name|ZipFile
argument_list|(
name|fileArchive
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
comment|//is it full backup?
name|base
operator|=
literal|"db/"
expr_stmt|;
name|descriptor
operator|=
name|archive
operator|.
name|getEntry
argument_list|(
name|base
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|descriptor
operator|==
literal|null
operator|)
operator|||
name|descriptor
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|base
operator|=
literal|null
expr_stmt|;
comment|//looking for highest collection
comment|//TODO: better to put some information on top?
name|ZipEntry
name|item
init|=
literal|null
decl_stmt|;
specifier|final
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|zipEnum
init|=
name|archive
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|zipEnum
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|item
operator|=
name|zipEnum
operator|.
name|nextElement
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|item
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|item
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|base
operator|==
literal|null
operator|)
operator|||
operator|(
name|base
operator|.
name|length
argument_list|()
operator|>
name|item
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
name|descriptor
operator|=
name|item
expr_stmt|;
name|base
operator|=
name|item
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|base
operator|=
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|base
operator|.
name|length
argument_list|()
operator|-
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|descriptor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Archive "
operator|+
name|fileArchive
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" is not a valid eXist backup archive"
argument_list|)
throw|;
block|}
specifier|final
name|Path
name|fakeDbRoot
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"/db"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fakeDbRoot
operator|.
name|resolve
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|base
argument_list|)
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|startsWith
argument_list|(
name|fakeDbRoot
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Detected archive exit attack! zipFile="
operator|+
name|fileArchive
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
block|}
specifier|private
name|ZipArchiveBackupDescriptor
parameter_list|(
specifier|final
name|ZipFile
name|archive
parameter_list|,
specifier|final
name|String
name|base
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|this
operator|.
name|archive
operator|=
name|archive
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|descriptor
operator|=
name|archive
operator|.
name|getEntry
argument_list|(
name|base
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|descriptor
operator|==
literal|null
operator|)
operator|||
name|descriptor
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|archive
operator|.
name|getName
argument_list|()
operator|+
literal|" is a bit corrupted ("
operator|+
name|base
operator|+
literal|" descriptor not found): not a valid eXist backup archive"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|BackupDescriptor
name|getChildBackupDescriptor
parameter_list|(
specifier|final
name|String
name|describedItem
parameter_list|)
block|{
name|BackupDescriptor
name|bd
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bd
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|archive
argument_list|,
name|base
operator|+
name|describedItem
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
comment|// DoNothing(R)
block|}
return|return
name|bd
return|;
block|}
annotation|@
name|Override
specifier|public
name|BackupDescriptor
name|getBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
if|if
condition|(
operator|(
operator|!
name|describedItem
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|describedItem
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|)
block|{
name|describedItem
operator|=
name|describedItem
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|describedItem
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|describedItem
operator|=
name|describedItem
operator|+
literal|'/'
expr_stmt|;
block|}
name|BackupDescriptor
name|bd
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bd
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|archive
argument_list|,
name|describedItem
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// DoNothing(R)
block|}
return|return
name|bd
return|;
block|}
annotation|@
name|Override
specifier|public
name|EXistInputSource
name|getInputSource
parameter_list|()
block|{
return|return
operator|new
name|ZipEntryInputSource
argument_list|(
name|archive
argument_list|,
name|descriptor
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EXistInputSource
name|getInputSource
parameter_list|(
specifier|final
name|String
name|describedItem
parameter_list|)
block|{
specifier|final
name|ZipEntry
name|ze
init|=
name|archive
operator|.
name|getEntry
argument_list|(
name|base
operator|+
name|describedItem
argument_list|)
decl_stmt|;
name|EXistInputSource
name|retval
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|ze
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|ze
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|retval
operator|=
operator|new
name|ZipEntryInputSource
argument_list|(
name|archive
argument_list|,
name|ze
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
annotation|@
name|Override
specifier|public
name|EXistInputSource
name|getBlobInputSource
parameter_list|(
specifier|final
name|String
name|blobId
parameter_list|)
block|{
specifier|final
name|ZipEntry
name|ze
init|=
name|archive
operator|.
name|getEntry
argument_list|(
name|blob
operator|+
name|blobId
argument_list|)
decl_stmt|;
name|EXistInputSource
name|retval
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|ze
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|ze
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|retval
operator|=
operator|new
name|ZipEntryInputSource
argument_list|(
name|archive
argument_list|,
name|ze
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
return|return
name|archive
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|descriptor
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSymbolicPath
parameter_list|(
specifier|final
name|String
name|describedItem
parameter_list|,
specifier|final
name|boolean
name|isChildDescriptor
parameter_list|)
block|{
name|String
name|retval
init|=
name|archive
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|base
operator|+
name|describedItem
decl_stmt|;
if|if
condition|(
name|isChildDescriptor
condition|)
block|{
name|retval
operator|+=
literal|"/"
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
annotation|@
name|Override
specifier|public
name|Properties
name|getProperties
parameter_list|()
throws|throws
name|IOException
block|{
name|Properties
name|properties
init|=
literal|null
decl_stmt|;
specifier|final
name|ZipEntry
name|ze
init|=
name|archive
operator|.
name|getEntry
argument_list|(
name|BACKUP_PROPERTIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ze
operator|!=
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|archive
operator|.
name|getInputStream
argument_list|(
name|ze
argument_list|)
init|)
block|{
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|properties
return|;
block|}
annotation|@
name|Override
specifier|public
name|Path
name|getRepoBackup
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ZipEntry
name|ze
init|=
name|archive
operator|.
name|getEntry
argument_list|(
name|RepoBackup
operator|.
name|REPO_ARCHIVE
argument_list|)
decl_stmt|;
if|if
condition|(
name|ze
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|temp
init|=
name|temporaryFileManager
operator|.
name|getTemporaryFile
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|archive
operator|.
name|getInputStream
argument_list|(
name|ze
argument_list|)
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|temp
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
block|}
return|return
name|temp
return|;
block|}
annotation|@
name|Override
specifier|public
name|Path
name|getParentDir
parameter_list|()
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
name|archive
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|FileUtils
operator|.
name|fileName
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|archive
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|countFileEntries
parameter_list|(
specifier|final
name|Path
name|fileArchive
parameter_list|)
block|{
try|try
init|(
specifier|final
name|ZipFile
name|zipFile
init|=
operator|new
name|ZipFile
argument_list|(
name|fileArchive
operator|.
name|toFile
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|entries
init|=
name|zipFile
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|ZipEntry
name|zipEntry
init|=
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|zipEntry
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|zipEntry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|COLLECTION_DESCRIPTOR
argument_list|)
operator|&&
operator|!
name|zipEntry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"backup.properties"
argument_list|)
condition|)
block|{
name|numberOfFiles
operator|++
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to count number of files in {}."
argument_list|,
name|fileArchive
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
