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
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|FileInputSource
import|;
end_import

begin_class
specifier|public
class|class
name|FileSystemBackupDescriptor
extends|extends
name|AbstractBackupDescriptor
block|{
specifier|protected
name|File
name|descriptor
decl_stmt|;
specifier|public
name|FileSystemBackupDescriptor
parameter_list|(
name|File
name|theDesc
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
if|if
condition|(
operator|!
name|theDesc
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
operator|||
operator|!
name|theDesc
operator|.
name|isFile
argument_list|()
operator|||
operator|!
name|theDesc
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|theDesc
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" is not a valid collection descriptor"
argument_list|)
throw|;
block|}
name|descriptor
operator|=
name|theDesc
expr_stmt|;
block|}
specifier|public
name|BackupDescriptor
name|getChildBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
name|File
name|child
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|descriptor
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|describedItem
argument_list|)
argument_list|,
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
decl_stmt|;
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
name|FileSystemBackupDescriptor
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
specifier|public
name|BackupDescriptor
name|getBackupDescriptor
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
name|String
name|topDir
init|=
name|descriptor
operator|.
name|getParentFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|subDir
init|=
name|topDir
operator|+
name|describedItem
decl_stmt|;
name|String
name|desc
init|=
name|subDir
operator|+
literal|'/'
operator|+
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
decl_stmt|;
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
name|FileSystemBackupDescriptor
argument_list|(
operator|new
name|File
argument_list|(
name|desc
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
specifier|public
name|EXistInputSource
name|getInputSource
parameter_list|()
block|{
return|return
operator|new
name|FileInputSource
argument_list|(
name|descriptor
argument_list|)
return|;
block|}
specifier|public
name|EXistInputSource
name|getInputSource
parameter_list|(
name|String
name|describedItem
parameter_list|)
block|{
name|File
name|child
init|=
operator|new
name|File
argument_list|(
name|descriptor
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|describedItem
argument_list|)
decl_stmt|;
name|EXistInputSource
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isFile
argument_list|()
operator|&&
name|child
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|is
operator|=
operator|new
name|FileInputSource
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
specifier|public
name|String
name|getSymbolicPath
parameter_list|()
block|{
return|return
name|descriptor
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|public
name|String
name|getSymbolicPath
parameter_list|(
name|String
name|describedItem
parameter_list|,
name|boolean
name|isChildDescriptor
parameter_list|)
block|{
name|File
name|resbase
init|=
operator|new
name|File
argument_list|(
name|descriptor
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|describedItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|isChildDescriptor
condition|)
name|resbase
operator|=
operator|new
name|File
argument_list|(
name|resbase
argument_list|,
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
expr_stmt|;
return|return
name|resbase
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|public
name|Properties
name|getProperties
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|dir
init|=
name|descriptor
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|File
name|parentDir
init|=
name|dir
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentDir
operator|!=
literal|null
condition|)
block|{
name|File
name|propFile
init|=
operator|new
name|File
argument_list|(
name|parentDir
argument_list|,
name|BACKUP_PROPERTIES
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// do nothing, return null
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|File
name|getParentDir
parameter_list|()
block|{
return|return
name|descriptor
operator|.
name|getParentFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|descriptor
operator|.
name|getParentFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

