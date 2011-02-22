begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-08 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
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
name|FilenameFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|BrokerPool
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
name|journal
operator|.
name|Journal
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
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Class DiskUsage  *   * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|DiskUsage
implements|implements
name|DiskUsageMBean
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|Configuration
name|config
decl_stmt|;
specifier|public
name|DiskUsage
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|config
operator|=
name|pool
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
block|}
specifier|private
name|long
name|getSpace
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|method
parameter_list|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cls
init|=
name|dir
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Method
name|m
init|=
name|cls
operator|.
name|getMethod
argument_list|(
name|method
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Long
name|a
init|=
operator|(
name|Long
operator|)
name|m
operator|.
name|invoke
argument_list|(
name|dir
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|a
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
comment|// method not
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|String
name|getDataDirectory
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
return|;
block|}
specifier|public
name|String
name|getJournalDirectory
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|Journal
operator|.
name|PROPERTY_RECOVERY_JOURNAL_DIR
argument_list|)
return|;
block|}
specifier|public
name|long
name|getDataDirectoryTotalSpace
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getSpace
argument_list|(
name|dir
argument_list|,
literal|"getTotalSpace"
argument_list|)
return|;
block|}
specifier|public
name|long
name|getDataDirectoryFreeSpace
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getSpace
argument_list|(
name|dir
argument_list|,
literal|"getUsableSpace"
argument_list|)
return|;
block|}
specifier|public
name|long
name|getJournalDirectoryTotalSpace
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getJournalDirectory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getSpace
argument_list|(
name|dir
argument_list|,
literal|"getTotalSpace"
argument_list|)
return|;
block|}
specifier|public
name|long
name|getJournalDirectoryFreeSpace
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getJournalDirectory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getSpace
argument_list|(
name|dir
argument_list|,
literal|"getUsableSpace"
argument_list|)
return|;
block|}
specifier|public
name|long
name|getDataDirectoryUsedSpace
parameter_list|()
block|{
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|DbxFilenameFilter
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|totalSize
operator|+=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
return|return
name|totalSize
return|;
block|}
specifier|public
name|long
name|getJournalDirectoryUsedSpace
parameter_list|()
block|{
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getJournalDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|JournalFilenameFilter
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|totalSize
operator|+=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
return|return
name|totalSize
return|;
block|}
specifier|public
name|int
name|getJournalDirectoryNumberOfFiles
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getJournalDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|(
operator|new
name|JournalFilenameFilter
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|files
operator|.
name|length
return|;
block|}
block|}
end_class

begin_class
class|class
name|DbxFilenameFilter
implements|implements
name|FilenameFilter
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".dbx"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

begin_class
class|class
name|JournalFilenameFilter
implements|implements
name|FilenameFilter
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".log"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

