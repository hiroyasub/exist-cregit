begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2011-2013 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|sync
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileStore
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
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|JobDescription
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
name|SystemTask
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

begin_class
specifier|public
class|class
name|SyncTask
implements|implements
name|SystemTask
block|{
specifier|private
specifier|final
specifier|static
name|String
name|JOB_NAME
init|=
literal|"Sync"
decl_stmt|;
specifier|public
specifier|static
name|String
name|getJobName
parameter_list|()
block|{
return|return
name|JOB_NAME
return|;
block|}
specifier|public
specifier|static
name|String
name|getJobGroup
parameter_list|()
block|{
return|return
name|JobDescription
operator|.
name|EXIST_INTERNAL_GROUP
return|;
block|}
specifier|private
name|Path
name|dataDir
decl_stmt|;
specifier|private
name|long
name|diskSpaceMin
init|=
literal|64
operator|*
literal|1024L
operator|*
literal|1024L
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|afterCheckpoint
parameter_list|()
block|{
comment|// a checkpoint is created by the MAJOR_SYNC event
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|EXistException
block|{
specifier|final
name|Integer
name|min
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|DISK_SPACE_MIN_PROPERTY
argument_list|)
decl_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
block|{
name|diskSpaceMin
operator|=
name|min
operator|*
literal|1024L
operator|*
literal|1024L
expr_stmt|;
block|}
comment|// fixme! - Shouldn't it be data dir AND journal dir we check
comment|// rather than EXIST_HOME? /ljo
name|dataDir
operator|=
operator|(
name|Path
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using DATA_DIR: "
operator|+
name|dataDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|". Minimal disk space required for database "
operator|+
literal|"to continue operations: "
operator|+
operator|(
name|diskSpaceMin
operator|/
literal|1024
operator|/
literal|1024
operator|)
operator|+
literal|"mb"
argument_list|)
expr_stmt|;
specifier|final
name|long
name|space
init|=
name|FileUtils
operator|.
name|measureFileStore
argument_list|(
name|dataDir
argument_list|,
name|FileStore
operator|::
name|getUsableSpace
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Usable space on partition containing DATA_DIR: "
operator|+
name|dataDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
operator|(
name|space
operator|/
literal|1024
operator|/
literal|1024
operator|)
operator|+
literal|"mb"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|checkDiskSpace
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Partition containing DATA_DIR: "
operator|+
name|dataDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" is running out of disk space. "
operator|+
literal|"Switching eXist-db to read only to prevent data loss!"
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|pool
operator|.
name|getLastMajorSync
argument_list|()
operator|>
name|pool
operator|.
name|getMajorSyncPeriod
argument_list|()
condition|)
block|{
name|pool
operator|.
name|sync
argument_list|(
name|broker
argument_list|,
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pool
operator|.
name|sync
argument_list|(
name|broker
argument_list|,
name|Sync
operator|.
name|MINOR_SYNC
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|checkDiskSpace
parameter_list|()
block|{
specifier|final
name|long
name|space
init|=
name|FileUtils
operator|.
name|measureFileStore
argument_list|(
name|dataDir
argument_list|,
name|FileStore
operator|::
name|getUsableSpace
argument_list|)
decl_stmt|;
comment|//LOG.info("Usable space on partition containing DATA_DIR: " + dataDir.getAbsolutePath() + ": " + (space / 1024 / 1024) + "mb");
return|return
name|space
operator|>
name|diskSpaceMin
return|;
block|}
block|}
end_class

end_unit

