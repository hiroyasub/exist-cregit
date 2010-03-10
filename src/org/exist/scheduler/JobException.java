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
name|quartz
operator|.
name|JobExecutionException
import|;
end_import

begin_comment
comment|/**  * Exception class can be thrown by implementations of org.exist.scheduler.Job.  *  *<p>Also provides a mechanism for cleaning up a job after failed execution</p>  *  * @author  Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|JobException
extends|extends
name|Exception
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1567438994821964637L
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|JOB_ABORT
init|=
literal|0
decl_stmt|;
comment|//Abort this job, but continue scheduling
specifier|public
specifier|final
specifier|static
name|int
name|JOB_ABORT_THIS
init|=
literal|1
decl_stmt|;
comment|//Abort this job and cancel this trigger
specifier|public
specifier|final
specifier|static
name|int
name|JOB_ABORT_ALL
init|=
literal|2
decl_stmt|;
comment|//Abort this job and cancel all triggers
specifier|public
specifier|final
specifier|static
name|int
name|JOB_REFIRE
init|=
literal|3
decl_stmt|;
comment|//Refire this job now
specifier|private
name|int
name|action
init|=
name|JOB_ABORT
decl_stmt|;
specifier|private
name|String
name|message
decl_stmt|;
specifier|public
name|JobException
parameter_list|(
name|int
name|action
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**      * Should be called after this exception is caught it cleans up the job, with regards to the scheduler.      *      *<p>Jobs may be removed, refired immediately or left for their next execution</p>      *      * @throws  JobExecutionException  DOCUMENT ME!      */
specifier|public
name|void
name|cleanupJob
parameter_list|()
throws|throws
name|JobExecutionException
block|{
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|JOB_REFIRE
case|:
block|{
throw|throw
operator|(
operator|new
name|JobExecutionException
argument_list|(
name|message
argument_list|,
literal|true
argument_list|)
operator|)
throw|;
block|}
case|case
name|JOB_ABORT_THIS
case|:
block|{
name|JobExecutionException
name|jat
init|=
operator|new
name|JobExecutionException
argument_list|(
name|message
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|jat
operator|.
name|setUnscheduleFiringTrigger
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|jat
operator|)
throw|;
block|}
case|case
name|JOB_ABORT_ALL
case|:
block|{
name|JobExecutionException
name|jaa
init|=
operator|new
name|JobExecutionException
argument_list|(
name|message
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
operator|(
name|jaa
operator|)
throw|;
block|}
case|case
name|JOB_ABORT
case|:
default|default:
block|{
throw|throw
operator|(
operator|new
name|JobExecutionException
argument_list|(
name|message
argument_list|,
literal|false
argument_list|)
operator|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

