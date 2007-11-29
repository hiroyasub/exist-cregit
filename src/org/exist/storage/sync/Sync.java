begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|Map
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
name|JobException
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
name|UserJavaJob
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

begin_comment
comment|/**  * It will periodically trigger a cache sync to write  * cached pages to disk.   */
end_comment

begin_class
specifier|public
class|class
name|Sync
extends|extends
name|UserJavaJob
block|{
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
specifier|final
specifier|static
name|int
name|MINOR_SYNC
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MAJOR_SYNC
init|=
literal|1
decl_stmt|;
specifier|public
name|Sync
parameter_list|()
block|{
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|JOB_NAME
return|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Map
name|params
parameter_list|)
throws|throws
name|JobException
block|{
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
name|triggerSync
argument_list|(
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pool
operator|.
name|triggerSync
argument_list|(
name|MINOR_SYNC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

