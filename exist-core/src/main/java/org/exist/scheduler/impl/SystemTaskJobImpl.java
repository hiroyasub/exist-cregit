begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|SystemTaskJob
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
name|SystemTask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|JobDataMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|JobExecutionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|JobExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|StatefulJob
import|;
end_import

begin_comment
comment|/**  * Class to represent a SystemTask Job Can be used by SystemTasks to schedule themselves as job's.  *  * SystemTaskJobs may only have a Single Instance running in the scheduler at once, intersecting schedules will be queued.  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|SystemTaskJobImpl
implements|implements
name|SystemTaskJob
implements|,
name|StatefulJob
block|{
specifier|private
specifier|final
specifier|static
name|String
name|JOB_GROUP
init|=
literal|"eXist.System"
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|"SystemTask"
decl_stmt|;
specifier|private
name|SystemTask
name|task
init|=
literal|null
decl_stmt|;
comment|/**      * Default Constructor for Quartz.      */
specifier|public
name|SystemTaskJobImpl
parameter_list|()
block|{
block|}
comment|/**      * Constructor for Creating a new SystemTask Job.      *      * @param  jobName  DOCUMENT ME!      * @param  task     DOCUMENT ME!      */
specifier|public
name|SystemTaskJobImpl
parameter_list|(
specifier|final
name|String
name|jobName
parameter_list|,
specifier|final
name|SystemTask
name|task
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
if|if
condition|(
name|jobName
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|name
operator|+=
literal|": "
operator|+
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|name
operator|=
name|jobName
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|setName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|JOB_GROUP
return|;
block|}
comment|/**      * Returns the SystemTask for this Job.      *      * @return  The SystemTask for this Job      */
specifier|protected
name|SystemTask
name|getSystemTask
parameter_list|()
block|{
return|return
name|task
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|execute
parameter_list|(
specifier|final
name|JobExecutionContext
name|jec
parameter_list|)
throws|throws
name|JobExecutionException
block|{
specifier|final
name|JobDataMap
name|jobDataMap
init|=
name|jec
operator|.
name|getJobDetail
argument_list|()
operator|.
name|getJobDataMap
argument_list|()
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
operator|(
name|BrokerPool
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
name|DATABASE
argument_list|)
decl_stmt|;
specifier|final
name|SystemTask
name|task
init|=
operator|(
name|SystemTask
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
name|SYSTEM_TASK
argument_list|)
decl_stmt|;
comment|//if invalid arguments then abort
if|if
condition|(
operator|(
name|pool
operator|==
literal|null
operator|)
operator|||
operator|(
name|task
operator|==
literal|null
operator|)
condition|)
block|{
comment|//abort all triggers for this job
specifier|final
name|JobExecutionException
name|jaa
init|=
operator|new
name|JobExecutionException
argument_list|(
literal|"SystemTaskJob Failed: BrokerPool or SystemTask was null! Unscheduling SystemTask"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|jaa
operator|.
name|setUnscheduleAllTriggers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
name|jaa
throw|;
block|}
comment|//trigger the system task
name|pool
operator|.
name|triggerSystemTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

