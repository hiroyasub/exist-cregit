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
name|org
operator|.
name|apache
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
name|EXistException
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
name|SecurityManager
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
name|User
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
name|JobExecutionContext
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
name|SimpleTrigger
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
name|ArrayList
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
name|Iterator
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_comment
comment|/**  * A Scheduler to trigger Startup, System and User defined jobs  *  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|Scheduler
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"scheduler"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_JOB_ELEMENT_NAME
init|=
literal|"job"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TYPE_ATTRIBUTE
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_CLASS_ATTRIBUTE
init|=
literal|"class"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_XQUERY_ATTRIBUTE
init|=
literal|"xquery"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_CRON_TRIGGER_ATTRIBUTE
init|=
literal|"cron-trigger"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_PERIOD_ATTRIBUTE
init|=
literal|"period"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_DELAY_ATTRIBUTE
init|=
literal|"delay"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_REPEAT_ATTRIBUTE
init|=
literal|"repeat"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_JOB_PARAMETER_ELEMENT_NAME
init|=
literal|"parameter"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_SCHEDULER_JOBS
init|=
literal|"scheduler.jobs"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TYPE_USER
init|=
literal|"user"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TYPE_STARTUP
init|=
literal|"startup"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TYPE_SYSTEM
init|=
literal|"system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|JOB_NAME_ATTRIBUTE
init|=
literal|"name"
decl_stmt|;
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
comment|//startup jobs
specifier|private
name|Vector
name|startupJobs
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerpool
init|=
literal|null
decl_stmt|;
specifier|private
name|Configuration
name|config
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Scheduler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//Logger
comment|/** 	 * Create and Start a new Scheduler 	 *  	 * @param brokerpool	The brokerpool for which this scheduler is intended 	 */
specifier|public
name|Scheduler
parameter_list|(
name|BrokerPool
name|brokerpool
parameter_list|,
name|Configuration
name|config
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
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
try|try
block|{
comment|//load the properties for quartz
name|InputStream
name|is
init|=
name|Scheduler
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"quartz.properties"
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Failed to load scheduler settings from org/exist/scheduler/quartz.properties"
argument_list|)
throw|;
block|}
name|properties
operator|.
name|setProperty
argument_list|(
name|StdSchedulerFactory
operator|.
name|PROP_SCHED_INSTANCE_NAME
argument_list|,
name|brokerpool
operator|.
name|getId
argument_list|()
operator|+
literal|"_QuartzScheduler"
argument_list|)
expr_stmt|;
name|SchedulerFactory
name|schedulerFactory
init|=
operator|new
name|StdSchedulerFactory
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|scheduler
operator|=
name|schedulerFactory
operator|.
name|getScheduler
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
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|setupConfiguredJobs
argument_list|()
expr_stmt|;
name|executeStartupJobs
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
name|LOG
operator|.
name|error
argument_list|(
name|se
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Shutdown the running Scheduler 	 *  	 * Asynchronous method. use isShutdown() to determine if the 	 * Scheduler has Shutdown 	 */
specifier|public
name|void
name|shutdown
parameter_list|(
name|boolean
name|waitForJobsToComplete
parameter_list|)
block|{
try|try
block|{
name|scheduler
operator|.
name|shutdown
argument_list|(
name|waitForJobsToComplete
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|se
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isShutdown
parameter_list|()
block|{
try|try
block|{
return|return
name|scheduler
operator|.
name|isShutdown
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|se
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/** 	 * Creates a startup job 	 *  	 * @param job The job to trigger at startup  	 * @param params Any parameters to pass to the job 	 */
specifier|private
name|void
name|createStartupJob
parameter_list|(
name|UserJob
name|job
parameter_list|,
name|Properties
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
comment|//Setup the job's data map
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
comment|//create the minimum quartz supporting classes to execute a job
name|SimpleTrigger
name|trig
init|=
operator|new
name|SimpleTrigger
argument_list|()
decl_stmt|;
name|trig
operator|.
name|setJobDataMap
argument_list|(
name|jobDataMap
argument_list|)
expr_stmt|;
name|JobExecutionContext
name|jec
init|=
operator|new
name|JobExecutionContext
argument_list|(
literal|null
argument_list|,
operator|new
name|org
operator|.
name|quartz
operator|.
name|spi
operator|.
name|TriggerFiredBundle
argument_list|(
name|jobDetail
argument_list|,
name|trig
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|startupJobs
operator|.
name|add
argument_list|(
name|jec
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Executes all startup jobs 	 */
specifier|public
name|void
name|executeStartupJobs
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|itStartupJob
init|=
name|startupJobs
operator|.
name|iterator
argument_list|()
init|;
name|itStartupJob
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|JobExecutionContext
name|jec
init|=
operator|(
name|JobExecutionContext
operator|)
name|itStartupJob
operator|.
name|next
argument_list|()
decl_stmt|;
name|org
operator|.
name|quartz
operator|.
name|Job
name|j
init|=
name|jec
operator|.
name|getJobInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Running startup job '"
operator|+
name|jec
operator|.
name|getJobDetail
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
try|try
block|{
comment|//execute the job
name|j
operator|.
name|execute
argument_list|(
name|jec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to run startup job '"
operator|+
name|jec
operator|.
name|getJobDetail
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|se
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * @param period	The period, in milliseconds. 	 * @param job 	The job to trigger after each period 	 * @param delay<= 0, start now, otherwise start in specified number of milliseconds 	 *  	 *  	 * @return	true if the job was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createPeriodicJob
parameter_list|(
name|long
name|period
parameter_list|,
name|JobDescription
name|job
parameter_list|,
name|long
name|delay
parameter_list|)
block|{
return|return
name|createPeriodicJob
argument_list|(
name|period
argument_list|,
name|job
argument_list|,
name|delay
argument_list|,
literal|null
argument_list|,
name|SimpleTrigger
operator|.
name|REPEAT_INDEFINITELY
argument_list|)
return|;
block|}
comment|/** 	 * @param period	The period, in milliseconds. 	 * @param job 	The job to trigger after each period 	 * @param delay<= 0, start now, otherwise start in specified number of milliseconds 	 * @param params	Any parameters to pass to the job 	 *  	 * @return	true if the job was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createPeriodicJob
parameter_list|(
name|long
name|period
parameter_list|,
name|JobDescription
name|job
parameter_list|,
name|long
name|delay
parameter_list|,
name|Properties
name|params
parameter_list|)
block|{
return|return
name|createPeriodicJob
argument_list|(
name|period
argument_list|,
name|job
argument_list|,
name|delay
argument_list|,
name|params
argument_list|,
name|SimpleTrigger
operator|.
name|REPEAT_INDEFINITELY
argument_list|)
return|;
block|}
comment|/** 	 * @param period	The period, in milliseconds. 	 * @param job 	The job to trigger after each period 	 * @param delay<= 0, start now, otherwise start in specified number of milliseconds 	 * @param params	Any parameters to pass to the job 	 * @param repeatCount	Number of times to repeat this job. 	 *  	 * @return	true if the job was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createPeriodicJob
parameter_list|(
name|long
name|period
parameter_list|,
name|JobDescription
name|job
parameter_list|,
name|long
name|delay
parameter_list|,
name|Properties
name|params
parameter_list|,
name|int
name|repeatCount
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
comment|//Setup the job's data map
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
name|repeatCount
argument_list|)
expr_stmt|;
comment|//when should the trigger start
if|if
condition|(
name|delay
operator|<=
literal|0
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
name|delay
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to schedule periodic job '"
operator|+
name|job
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|//Successfully scheduled Job
return|return
literal|true
return|;
block|}
comment|/** 	 * @param cronExpression	The Cron scheduling expression 	 * @param job 	The job to trigger after each period 	 *  	 * @return	true if the job was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createCronJob
parameter_list|(
name|String
name|cronExpression
parameter_list|,
name|JobDescription
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
comment|/** 	 * @param cronExpression	The Cron scheduling expression 	 * @param job 	The job to trigger after each period 	 * @param params	Any parameters to pass to the job 	 *  	 * @return	true if the job was successfully scheduled, false otherwise 	 */
specifier|public
name|boolean
name|createCronJob
parameter_list|(
name|String
name|cronExpression
parameter_list|,
name|JobDescription
name|job
parameter_list|,
name|Properties
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
comment|//Setup the job's data map
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
comment|//setup a trigger for the job, Cron based
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to schedule cron job '"
operator|+
name|job
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|pe
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to schedule cron job '"
operator|+
name|job
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|//Successfully scheduled Job
return|return
literal|true
return|;
block|}
comment|/** 	 * Removes a Job from the Scheduler 	 *  	 * @param jobName	The name of the Job 	 * @param jobGroup The group that the Job was Scheduled in 	 *  	 * @return true if the job was deleted, false otherwise 	 */
specifier|public
name|boolean
name|deleteJob
parameter_list|(
name|String
name|jobName
parameter_list|,
name|String
name|jobGroup
parameter_list|)
block|{
try|try
block|{
return|return
name|scheduler
operator|.
name|deleteJob
argument_list|(
name|jobName
argument_list|,
name|jobGroup
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to delete job '"
operator|+
name|jobName
operator|+
literal|"'"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/** 	 * Pauses a Job with the Scheduler 	 *  	 * @param jobName	The name of the Job 	 * @param jobGroup The group that the Job was Scheduled in 	 */
specifier|public
name|boolean
name|pauseJob
parameter_list|(
name|String
name|jobName
parameter_list|,
name|String
name|jobGroup
parameter_list|)
block|{
try|try
block|{
name|scheduler
operator|.
name|pauseJob
argument_list|(
name|jobName
argument_list|,
name|jobGroup
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to pause job '"
operator|+
name|jobName
operator|+
literal|"'"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/** 	 * Resume a Job with the Scheduler 	 *  	 * @param jobName	The name of the Job 	 * @param jobGroup The group that the Job was Scheduled in 	 */
specifier|public
name|boolean
name|resumeJob
parameter_list|(
name|String
name|jobName
parameter_list|,
name|String
name|jobGroup
parameter_list|)
block|{
try|try
block|{
name|scheduler
operator|.
name|resumeJob
argument_list|(
name|jobName
argument_list|,
name|jobGroup
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to resume job '"
operator|+
name|jobName
operator|+
literal|"'"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/** 	 * Gets the names of the Job groups 	 *  	 * @return String array of the Job group names 	 */
specifier|public
name|String
index|[]
name|getJobGroupNames
parameter_list|()
block|{
try|try
block|{
return|return
name|scheduler
operator|.
name|getJobGroupNames
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get job group names"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/** 	 * Gets information about currently Scheduled Jobs 	 *  	 * @return An array of ScheduledJobInfo 	 */
specifier|public
name|ScheduledJobInfo
index|[]
name|getScheduledJobs
parameter_list|()
block|{
name|ArrayList
name|jobs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
comment|//get the trigger groups
name|String
index|[]
name|trigGroups
init|=
name|scheduler
operator|.
name|getTriggerGroupNames
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|tg
init|=
literal|0
init|;
name|tg
operator|<
name|trigGroups
operator|.
name|length
condition|;
name|tg
operator|++
control|)
block|{
comment|//get the trigger names for the trigger group
name|String
index|[]
name|trigNames
init|=
name|scheduler
operator|.
name|getTriggerNames
argument_list|(
name|trigGroups
index|[
name|tg
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|tn
init|=
literal|0
init|;
name|tn
operator|<
name|trigNames
operator|.
name|length
condition|;
name|tn
operator|++
control|)
block|{
comment|//add information about the job to the result
name|jobs
operator|.
name|add
argument_list|(
operator|new
name|ScheduledJobInfo
argument_list|(
name|scheduler
argument_list|,
name|scheduler
operator|.
name|getTrigger
argument_list|(
name|trigNames
index|[
name|tn
index|]
argument_list|,
name|trigGroups
index|[
name|tg
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get scheduled jobs"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|//copy the array list to a correctly typed array
name|Object
index|[]
name|oJobsArray
init|=
name|jobs
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|ScheduledJobInfo
index|[]
name|jobsArray
init|=
operator|new
name|ScheduledJobInfo
index|[
name|oJobsArray
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|oJobsArray
argument_list|,
literal|0
argument_list|,
name|jobsArray
argument_list|,
literal|0
argument_list|,
name|oJobsArray
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|jobsArray
return|;
block|}
comment|/** 	 * Gets information about currently Executing Jobs 	 *  	 * @return An array of ScheduledJobInfo 	 */
specifier|public
name|ScheduledJobInfo
index|[]
name|getExecutingJobs
parameter_list|()
block|{
name|List
name|executingJobs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|executingJobs
operator|=
name|scheduler
operator|.
name|getCurrentlyExecutingJobs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get executing jobs"
argument_list|,
name|se
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ScheduledJobInfo
index|[]
name|jobs
init|=
operator|new
name|ScheduledJobInfo
index|[
name|executingJobs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|executingJobs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JobExecutionContext
name|jec
init|=
operator|(
name|JobExecutionContext
operator|)
name|executingJobs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|jobs
index|[
name|i
index|]
operator|=
operator|new
name|ScheduledJobInfo
argument_list|(
name|scheduler
argument_list|,
name|jec
operator|.
name|getTrigger
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|jobs
return|;
block|}
comment|/** 	 * Set's up all the jobs that are listed in conf.xml and loaded 	 * through org.exist.util.Configuration 	 */
specifier|public
name|void
name|setupConfiguredJobs
parameter_list|()
block|{
name|Configuration
operator|.
name|JobConfig
name|jobList
index|[]
init|=
operator|(
name|Configuration
operator|.
name|JobConfig
index|[]
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|Scheduler
operator|.
name|PROPERTY_SCHEDULER_JOBS
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobList
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|jobList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Configuration
operator|.
name|JobConfig
name|jobConfig
init|=
name|jobList
index|[
name|i
index|]
decl_stmt|;
name|JobDescription
name|job
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|jobConfig
operator|.
name|getResourceName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"/db/"
argument_list|)
condition|)
block|{
if|if
condition|(
name|jobConfig
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|JOB_TYPE_SYSTEM
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"System jobs may only be written in Java"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//create an XQuery job
name|User
name|guestUser
init|=
name|brokerpool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
decl_stmt|;
name|job
operator|=
operator|new
name|UserXQueryJob
argument_list|(
name|jobConfig
operator|.
name|getJobName
argument_list|()
argument_list|,
name|jobConfig
operator|.
name|getResourceName
argument_list|()
argument_list|,
name|guestUser
argument_list|)
expr_stmt|;
try|try
block|{
comment|// check if a job with the same name is already registered
if|if
condition|(
name|scheduler
operator|.
name|getJobDetail
argument_list|(
name|job
operator|.
name|getName
argument_list|()
argument_list|,
name|UserJob
operator|.
name|JOB_GROUP
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// yes, try to make the job's name unique
operator|(
operator|(
name|UserXQueryJob
operator|)
name|job
operator|)
operator|.
name|setName
argument_list|(
name|job
operator|.
name|getName
argument_list|()
operator|+
name|job
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|//create a Java job
try|try
block|{
name|Class
name|jobClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|jobConfig
operator|.
name|getResourceName
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|jobObject
init|=
name|jobClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|jobConfig
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|JOB_TYPE_SYSTEM
argument_list|)
condition|)
block|{
if|if
condition|(
name|jobObject
operator|instanceof
name|SystemTask
condition|)
block|{
name|SystemTask
name|task
init|=
operator|(
name|SystemTask
operator|)
name|jobObject
decl_stmt|;
name|task
operator|.
name|configure
argument_list|(
name|config
argument_list|,
name|jobConfig
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|=
operator|new
name|SystemTaskJob
argument_list|(
name|jobConfig
operator|.
name|getJobName
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"System jobs must extend SystemTask"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|job
operator|=
operator|(
name|JobDescription
operator|)
name|jobObject
expr_stmt|;
if|if
condition|(
name|jobConfig
operator|.
name|getJobName
argument_list|()
operator|!=
literal|null
condition|)
name|job
operator|.
name|setName
argument_list|(
name|jobConfig
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to schedule '"
operator|+
name|jobConfig
operator|.
name|getType
argument_list|()
operator|+
literal|"' job "
operator|+
name|jobConfig
operator|.
name|getResourceName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|//if there is a job, schedule it
if|if
condition|(
name|job
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|jobConfig
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|JOB_TYPE_STARTUP
argument_list|)
condition|)
block|{
comment|//startup job - one off execution - no period, delay or repeat
name|createStartupJob
argument_list|(
operator|(
name|UserJob
operator|)
name|job
argument_list|,
name|jobConfig
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//timed job
comment|//trigger is Cron or period?
if|if
condition|(
name|jobConfig
operator|.
name|getSchedule
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
comment|//schedule job with Cron trigger
name|createCronJob
argument_list|(
name|jobConfig
operator|.
name|getSchedule
argument_list|()
argument_list|,
name|job
argument_list|,
name|jobConfig
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//schedule job with periodic trigger
name|createPeriodicJob
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|jobConfig
operator|.
name|getSchedule
argument_list|()
argument_list|)
argument_list|,
name|job
argument_list|,
name|jobConfig
operator|.
name|getDelay
argument_list|()
argument_list|,
name|jobConfig
operator|.
name|getParameters
argument_list|()
argument_list|,
name|jobConfig
operator|.
name|getRepeat
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/** 	 * Sets up the Job's Data Map 	 *  	 * @param job	The Job 	 * @param jobDataMap	The Job's Data Map 	 * @param params	Any parameters for the job 	 */
specifier|private
name|void
name|setupJobDataMap
parameter_list|(
name|JobDescription
name|job
parameter_list|,
name|JobDataMap
name|jobDataMap
parameter_list|,
name|Properties
name|params
parameter_list|)
block|{
comment|//if this is a system job, store the BrokerPool in the job's data map
name|jobDataMap
operator|.
name|put
argument_list|(
literal|"brokerpool"
argument_list|,
name|brokerpool
argument_list|)
expr_stmt|;
comment|//if this is a system task job, store the SystemTask in the job's data map
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
comment|//if this is a users XQuery job, store the XQuery resource and user in the job's data map
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

