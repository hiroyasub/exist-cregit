begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|util
operator|.
name|FileUtils
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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_class
specifier|public
class|class
name|BackupDirectory
block|{
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|BackupDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX_FULL_BACKUP_FILE
init|=
literal|"full"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX_INC_BACKUP_FILE
init|=
literal|"inc"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|FILE_REGEX
init|=
literal|"("
operator|+
name|PREFIX_FULL_BACKUP_FILE
operator|+
literal|"|"
operator|+
name|PREFIX_INC_BACKUP_FILE
operator|+
literal|")(\\d{8}-\\d{4}).*"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DATE_FORMAT_PICTURE
init|=
literal|"yyyyMMdd-HHmm"
decl_stmt|;
specifier|private
specifier|final
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FORMAT_PICTURE
argument_list|)
decl_stmt|;
specifier|private
name|Path
name|dir
decl_stmt|;
specifier|private
name|Matcher
name|matcher
decl_stmt|;
specifier|public
name|BackupDirectory
parameter_list|(
specifier|final
name|String
name|dirPath
parameter_list|)
block|{
name|this
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dirPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BackupDirectory
parameter_list|(
specifier|final
name|Path
name|directory
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|directory
expr_stmt|;
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|FILE_REGEX
argument_list|)
decl_stmt|;
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Path
name|createBackup
parameter_list|(
name|boolean
name|incremental
parameter_list|,
name|boolean
name|zip
parameter_list|)
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|Path
name|file
decl_stmt|;
do|do
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|incremental
condition|?
name|PREFIX_INC_BACKUP_FILE
else|:
name|PREFIX_FULL_BACKUP_FILE
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|++
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
operator|.
name|append
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zip
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|".zip"
argument_list|)
expr_stmt|;
block|}
name|file
operator|=
name|dir
operator|.
name|resolve
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
do|;
return|return
operator|(
name|file
operator|)
return|;
block|}
specifier|public
name|BackupDescriptor
name|lastBackupFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|files
init|=
name|FileUtils
operator|.
name|list
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Path
name|newest
init|=
literal|null
decl_stmt|;
name|Date
name|newestDate
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|file
range|:
name|files
control|)
block|{
name|matcher
operator|.
name|reset
argument_list|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
specifier|final
name|String
name|dateTime
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|Date
name|date
init|=
name|dateFormat
operator|.
name|parse
argument_list|(
name|dateTime
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|newestDate
operator|==
literal|null
operator|)
operator|||
name|date
operator|.
name|after
argument_list|(
name|newestDate
argument_list|)
condition|)
block|{
name|newestDate
operator|=
name|date
expr_stmt|;
name|newest
operator|=
name|file
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ParseException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
name|BackupDescriptor
name|descriptor
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|newest
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|newest
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|descriptor
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|newest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|descriptor
operator|=
operator|new
name|FileSystemBackupDescriptor
argument_list|(
name|newest
operator|.
name|resolve
argument_list|(
literal|"db"
argument_list|)
operator|.
name|resolve
argument_list|(
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|(
name|descriptor
operator|)
return|;
block|}
block|}
end_class

end_unit

