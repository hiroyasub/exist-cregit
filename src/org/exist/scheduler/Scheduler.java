begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2006 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|scheduler
package|;
end_package

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
name|util
operator|.
name|Calendar
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
name|Map
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
name|quartz
operator|.
name|CronTrigger
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
name|JobDetail
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|SimpleTrigger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|SchedulerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|SchedulerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|impl
operator|.
name|StdSchedulerFactory
import|;
end_import

begin_comment
comment|/**  * A Scheduler to trigger System and User defined jobs  *  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|Scheduler
block|{
comment|//the scheduler
specifier|private
name|org
operator|.
name|quartz
operator|.
name|Scheduler
name|scheduler
init|=
literal|null
decl_stmt|;
comment|//the brokerpool for this scheduler
specifier|private
name|BrokerPool
name|brokerpool
init|=
literal|null
decl_stmt|;
comment|/** 	 * Create and Start a new Scheduler 	 *  	 * @param brokerpool	The brokerpool for which this scheduler is intended 	 */
specifier|public
name|Scheduler
parameter_list|(
name|BrokerPool
name|brokerpool
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
operator|.
name|brokerpool
operator|=
name|brokerpool
expr_stmt|;
try|try
block|{
name|SchedulerFactory
name|schedulerFactory
init|=
operator|new
name|StdSchedulerFactory
argument_list|()
decl_stmt|;
name|scheduler
operator|=
name|schedulerFactory
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unable to create Scheduler"
argument_list|,
name|se
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Shutdown the running Scheduler 	 */
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
name|scheduler
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
comment|//TODO: something here!?!
block|}
block|}
comment|/** 	 * @param period	The period, in milliseconds. 	 * @param job 	The job to trigger after each period 	 * @param startNow	true if the cycle should start with execution 	 * of the task now. Otherwise, the cycle starts with a delay of 	 *<code>period</code> milliseconds. 	 *  	 * @return	true if thejob was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createPeriodicJob
parameter_list|(
name|long
name|period
parameter_list|,
name|Job
name|job
parameter_list|,
name|boolean
name|startNow
parameter_list|)
block|{
return|return
name|createPeriodicJob
argument_list|(
name|period
argument_list|,
name|job
argument_list|,
name|startNow
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** 	 * @param period	The period, in milliseconds. 	 * @param job 	The job to trigger after each period 	 * @param startNow	true if the cycle should start with execution 	 * of the task now. Otherwise, the cycle starts with a delay of 	 *<code>period</code> milliseconds. 	 * @param params	Any parameters to pass to the job 	 *  	 * @return	true if thejob was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createPeriodicJob
parameter_list|(
name|long
name|period
parameter_list|,
name|Job
name|job
parameter_list|,
name|boolean
name|startNow
parameter_list|,
name|Map
name|params
parameter_list|)
block|{
comment|//Create the job details
name|JobDetail
name|jobDetail
init|=
operator|new
name|JobDetail
argument_list|(
name|job
operator|.
name|getName
argument_list|()
argument_list|,
name|job
operator|.
name|getGroup
argument_list|()
argument_list|,
name|job
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|//Setup the jobs's data map
name|JobDataMap
name|jobDataMap
init|=
name|jobDetail
operator|.
name|getJobDataMap
argument_list|()
decl_stmt|;
name|setupJobDataMap
argument_list|(
name|job
argument_list|,
name|jobDataMap
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|//setup a trigger for the job, millisecond based
name|SimpleTrigger
name|trigger
init|=
operator|new
name|SimpleTrigger
argument_list|()
decl_stmt|;
name|trigger
operator|.
name|setRepeatInterval
argument_list|(
name|period
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|setRepeatCount
argument_list|(
name|SimpleTrigger
operator|.
name|REPEAT_INDEFINITELY
argument_list|)
expr_stmt|;
comment|//when should the trigger start
if|if
condition|(
name|startNow
condition|)
block|{
comment|//start now
name|trigger
operator|.
name|setStartTime
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//start after period
name|Calendar
name|start
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|start
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
operator|(
name|int
operator|)
name|period
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|setStartTime
argument_list|(
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//set the trigger's name
name|trigger
operator|.
name|setName
argument_list|(
name|job
operator|.
name|getName
argument_list|()
operator|+
literal|" Trigger"
argument_list|)
expr_stmt|;
comment|//schedule the job
try|try
block|{
name|scheduler
operator|.
name|scheduleJob
argument_list|(
name|jobDetail
argument_list|,
name|trigger
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
comment|//Failed to schedule Job
return|return
literal|false
return|;
block|}
comment|//Succesfully scheduled Job
return|return
literal|true
return|;
block|}
comment|/** 	 * @param cronExpression	The Cron scheduling expression 	 * @param job 	The job to trigger after each period 	 *  	 * @return	true if thejob was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createCronJob
parameter_list|(
name|String
name|cronExpression
parameter_list|,
name|Job
name|job
parameter_list|)
block|{
return|return
name|createCronJob
argument_list|(
name|cronExpression
argument_list|,
name|job
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** 	 * @param cronExpression	The Cron scheduling expression 	 * @param job 	The job to trigger after each period 	 * @param params	Any parameters to pass to the job 	 *  	 * @return	true if thejob was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createCronJob
parameter_list|(
name|String
name|cronExpression
parameter_list|,
name|Job
name|job
parameter_list|,
name|Map
name|params
parameter_list|)
block|{
comment|//Create the job details
name|JobDetail
name|jobDetail
init|=
operator|new
name|JobDetail
argument_list|(
name|job
operator|.
name|getName
argument_list|()
argument_list|,
name|job
operator|.
name|getGroup
argument_list|()
argument_list|,
name|job
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|//Setup the jobs's data map
name|JobDataMap
name|jobDataMap
init|=
name|jobDetail
operator|.
name|getJobDataMap
argument_list|()
decl_stmt|;
name|setupJobDataMap
argument_list|(
name|job
argument_list|,
name|jobDataMap
argument_list|,
name|params
argument_list|)
expr_stmt|;
try|try
block|{
comment|//setup a trigger for the job, cron based
name|CronTrigger
name|trigger
init|=
operator|new
name|CronTrigger
argument_list|(
name|job
operator|.
name|getName
argument_list|()
operator|+
literal|" Trigger"
argument_list|,
name|job
operator|.
name|getGroup
argument_list|()
argument_list|,
name|cronExpression
argument_list|)
decl_stmt|;
comment|//schedule the job
name|scheduler
operator|.
name|scheduleJob
argument_list|(
name|jobDetail
argument_list|,
name|trigger
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|//Failed to schedule Job
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
comment|//Failed to schedule Job
return|return
literal|false
return|;
block|}
comment|//Succesfully scheduled Job
return|return
literal|true
return|;
block|}
comment|/** 	 * Sets up the Job's Data Map 	 *  	 * @param job	The Job 	 * @param jobDataMap	The Job's Data Map 	 * @param params	Any parameters for the job 	 */
specifier|private
name|void
name|setupJobDataMap
parameter_list|(
name|Job
name|job
parameter_list|,
name|JobDataMap
name|jobDataMap
parameter_list|,
name|Map
name|params
parameter_list|)
block|{
comment|//if this is a system job, store the brokerpool in the job's data map
name|jobDataMap
operator|.
name|put
argument_list|(
literal|"brokerpool"
argument_list|,
name|brokerpool
argument_list|)
expr_stmt|;
comment|//if this is a system task job, store the systemtask in the job's data map
if|if
condition|(
name|job
operator|instanceof
name|SystemTaskJob
condition|)
block|{
name|jobDataMap
operator|.
name|put
argument_list|(
literal|"systemtask"
argument_list|,
operator|(
operator|(
name|SystemTaskJob
operator|)
name|job
operator|)
operator|.
name|getSystemTask
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//if this is a users xquery job, store the xquery resource and user in the job's data map
if|if
condition|(
name|job
operator|instanceof
name|UserXQueryJob
condition|)
block|{
name|jobDataMap
operator|.
name|put
argument_list|(
literal|"xqueryresource"
argument_list|,
operator|(
operator|(
name|UserXQueryJob
operator|)
name|job
operator|)
operator|.
name|getXQueryResource
argument_list|()
argument_list|)
expr_stmt|;
name|jobDataMap
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
operator|(
operator|(
name|UserXQueryJob
operator|)
name|job
operator|)
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//copy any parameters into the job's data map
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|jobDataMap
operator|.
name|put
argument_list|(
literal|"params"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

