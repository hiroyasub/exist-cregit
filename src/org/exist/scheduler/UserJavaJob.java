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
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
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

begin_comment
comment|/**  * Class to represent a User's Java Job.  *  *<p>Should be extended by all classes wishing to schedule as a Job that perform user defined functionality</p>  *  * @author  Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|UserJavaJob
extends|extends
name|UserJob
block|{
comment|/**      * The execute method as called by the Quartz Scheduler.      *      * @param   jec  The execution context of the executing job      *      * @throws  JobExecutionException  if there was a problem with the job, this also describes to Quartz how to cleanup the job      */
specifier|public
specifier|final
name|void
name|execute
parameter_list|(
name|JobExecutionContext
name|jec
parameter_list|)
throws|throws
name|JobExecutionException
block|{
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
comment|//get the brokerpool from the data map
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
literal|"brokerpool"
argument_list|)
decl_stmt|;
comment|//get any parameters from the data map
name|Map
name|params
init|=
operator|(
name|Map
operator|)
name|jobDataMap
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
try|try
block|{
comment|//execute the job
name|execute
argument_list|(
name|pool
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JobException
name|je
parameter_list|)
block|{
comment|//cleanup the job
name|je
operator|.
name|cleanupJob
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Function that is executed by the Scheduler.      *      * @param   brokerpool  The BrokerPool for the Scheduler of this job      * @param   params      Any parameters passed to the job or null otherwise      *      * @throws  JobException  if there is a problem with the job. cleanupJob() should then be called, which will adjust the jobs scheduling      *                        appropriately      */
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|BrokerPool
name|brokerpool
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|params
parameter_list|)
throws|throws
name|JobException
function_decl|;
block|}
end_class

end_unit

