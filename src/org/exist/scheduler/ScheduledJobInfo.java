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
name|util
operator|.
name|Date
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
name|Scheduler
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
name|SimpleTrigger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|quartz
operator|.
name|Trigger
import|;
end_import

begin_comment
comment|/**  * Information about a Scheduled Job  *  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|ScheduledJobInfo
block|{
specifier|private
name|Scheduler
name|scheduler
init|=
literal|null
decl_stmt|;
specifier|private
name|Trigger
name|trigger
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRIGGER_STATE_ERROR
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRIGGER_STATE_NONE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRIGGER_STATE_NORMAL
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRIGGER_STATE_PAUSED
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRIGGER_STATE_BLOCKED
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRIGGER_STATE_COMPLETE
init|=
literal|4
decl_stmt|;
specifier|public
name|ScheduledJobInfo
parameter_list|(
name|Scheduler
name|scheduler
parameter_list|,
name|Trigger
name|trigger
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|trigger
operator|=
name|trigger
expr_stmt|;
block|}
comment|/** 	 * Get the Job's Name 	 *  	 * @return the Job's Name 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getJobName
argument_list|()
return|;
block|}
comment|/** 	 * Get the Job's Group 	 *  	 * @return the Job's Group 	 */
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getJobGroup
argument_list|()
return|;
block|}
comment|/** 	 * Get the Name of the Job's Trigger 	 *  	 * @return the Name of the Job's Trigger 	 */
specifier|public
name|String
name|getTriggerName
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/** 	 * Get the Start time of the Job 	 *  	 * @return the Start time of the Job 	 */
specifier|public
name|Date
name|getStartTime
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getStartTime
argument_list|()
return|;
block|}
comment|/** 	 * Get the End time of the Job 	 *  	 * @return the End time of the Job, or null of the job is Scheduled forever 	 */
specifier|public
name|Date
name|getEndTime
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getEndTime
argument_list|()
return|;
block|}
comment|/** 	 * Get the Previous Fired time of the Job 	 *  	 * @return the time the Job was Previously Fired, or null if the job hasnt fired yet 	 */
specifier|public
name|Date
name|getPreviousFireTime
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getPreviousFireTime
argument_list|()
return|;
block|}
comment|/** 	 * Get the Time the Job will Next be Fired 	 *  	 * @return the time the Job will Next be Fired, or null if the job wont fire again 	 */
specifier|public
name|Date
name|getNextFireTime
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getNextFireTime
argument_list|()
return|;
block|}
comment|/** 	 * Get the Final Time the Job will be Fired 	 *  	 * @return the time the Job will be Fired for the Final time, or null if the job is Scheduled forever 	 */
specifier|public
name|Date
name|getFinalFireTime
parameter_list|()
block|{
return|return
name|trigger
operator|.
name|getFinalFireTime
argument_list|()
return|;
block|}
comment|/** 	 * Get the Expression that was used to configure the Triggers firing pattern 	 *  	 * @return The expression that was used to configure the Triggers firing pattern 	 */
specifier|public
name|String
name|getTriggerExpression
parameter_list|()
block|{
if|if
condition|(
name|trigger
operator|instanceof
name|CronTrigger
condition|)
block|{
return|return
operator|(
operator|(
name|CronTrigger
operator|)
name|trigger
operator|)
operator|.
name|getCronExpression
argument_list|()
return|;
block|}
if|else if
condition|(
name|trigger
operator|instanceof
name|SimpleTrigger
condition|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|SimpleTrigger
operator|)
name|trigger
operator|)
operator|.
name|getRepeatInterval
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * Get the State of the Job's Trigger 	 *  	 * @return the TRIGGER_STATE_* 	 */
specifier|public
name|int
name|getTriggerState
parameter_list|()
block|{
try|try
block|{
switch|switch
condition|(
name|scheduler
operator|.
name|getTriggerState
argument_list|(
name|trigger
operator|.
name|getName
argument_list|()
argument_list|,
name|trigger
operator|.
name|getGroup
argument_list|()
argument_list|)
condition|)
block|{
case|case
name|Trigger
operator|.
name|STATE_ERROR
case|:
return|return
name|TRIGGER_STATE_ERROR
return|;
case|case
name|Trigger
operator|.
name|STATE_NONE
case|:
return|return
name|TRIGGER_STATE_NONE
return|;
case|case
name|Trigger
operator|.
name|STATE_NORMAL
case|:
return|return
name|TRIGGER_STATE_NORMAL
return|;
case|case
name|Trigger
operator|.
name|STATE_PAUSED
case|:
return|return
name|TRIGGER_STATE_PAUSED
return|;
case|case
name|Trigger
operator|.
name|STATE_BLOCKED
case|:
return|return
name|TRIGGER_STATE_BLOCKED
return|;
case|case
name|Trigger
operator|.
name|STATE_COMPLETE
case|:
return|return
name|TRIGGER_STATE_COMPLETE
return|;
default|default:
return|return
name|TRIGGER_STATE_ERROR
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SchedulerException
name|se
parameter_list|)
block|{
return|return
name|TRIGGER_STATE_ERROR
return|;
block|}
block|}
block|}
end_class

end_unit

