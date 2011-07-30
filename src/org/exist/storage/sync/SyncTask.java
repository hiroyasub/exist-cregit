begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2011 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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

begin_class
specifier|public
class|class
name|SyncTask
implements|implements
name|SystemTask
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|final
specifier|static
name|String
name|JOB_GROUP
init|=
literal|"eXist.internal"
decl_stmt|;
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
name|JOB_GROUP
return|;
block|}
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
block|}
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
block|}
end_class

end_unit

