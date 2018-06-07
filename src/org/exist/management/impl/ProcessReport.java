begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
package|;
end_package

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
name|List
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
name|ScheduledJobInfo
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
name|Scheduler
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
name|ProcessMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryWatchDog
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|ProcessMonitor
operator|.
name|QueryHistory
import|;
end_import

begin_class
specifier|public
class|class
name|ProcessReport
implements|implements
name|ProcessReportMXBean
block|{
specifier|private
specifier|final
name|String
name|instanceId
decl_stmt|;
specifier|private
specifier|final
name|ProcessMonitor
name|processMonitor
decl_stmt|;
specifier|private
specifier|final
name|Scheduler
name|scheduler
decl_stmt|;
specifier|public
name|ProcessReport
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|instanceId
operator|=
name|pool
operator|.
name|getId
argument_list|()
expr_stmt|;
name|this
operator|.
name|processMonitor
operator|=
name|pool
operator|.
name|getProcessMonitor
argument_list|()
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|pool
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getAllInstancesQuery
parameter_list|()
block|{
return|return
name|getName
argument_list|(
literal|"*"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getName
parameter_list|(
specifier|final
name|String
name|instanceId
parameter_list|)
block|{
return|return
literal|"org.exist.management."
operator|+
name|instanceId
operator|+
literal|":type=ProcessReport"
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectName
name|getName
parameter_list|()
throws|throws
name|MalformedObjectNameException
block|{
return|return
operator|new
name|ObjectName
argument_list|(
name|getName
argument_list|(
name|instanceId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInstanceId
parameter_list|()
block|{
return|return
name|instanceId
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Job
argument_list|>
name|getScheduledJobs
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Job
argument_list|>
name|jobList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ScheduledJobInfo
argument_list|>
name|jobs
init|=
name|scheduler
operator|.
name|getScheduledJobs
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ScheduledJobInfo
name|job
range|:
name|jobs
control|)
block|{
name|jobList
operator|.
name|add
argument_list|(
operator|new
name|Job
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
name|getTriggerExpression
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|jobList
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Job
argument_list|>
name|getRunningJobs
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Job
argument_list|>
name|jobList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ProcessMonitor
operator|.
name|JobInfo
index|[]
name|jobs
init|=
name|processMonitor
operator|.
name|runningJobs
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ProcessMonitor
operator|.
name|JobInfo
name|job
range|:
name|jobs
control|)
block|{
name|jobList
operator|.
name|add
argument_list|(
operator|new
name|Job
argument_list|(
name|job
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|job
operator|.
name|getAction
argument_list|()
argument_list|,
name|job
operator|.
name|getAddInfo
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|jobList
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RunningQuery
argument_list|>
name|getRunningQueries
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RunningQuery
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|XQueryWatchDog
index|[]
name|watchdogs
init|=
name|processMonitor
operator|.
name|getRunningXQueries
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|XQueryWatchDog
name|watchdog
range|:
name|watchdogs
control|)
block|{
name|String
name|requestURI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|processMonitor
operator|.
name|getTrackRequestURI
argument_list|()
condition|)
block|{
name|requestURI
operator|=
name|ProcessMonitor
operator|.
name|getRequestURI
argument_list|(
name|watchdog
argument_list|)
expr_stmt|;
block|}
name|queries
operator|.
name|add
argument_list|(
operator|new
name|RunningQuery
argument_list|(
name|watchdog
argument_list|,
name|requestURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|queries
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|killQuery
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
specifier|final
name|XQueryWatchDog
index|[]
name|watchdogs
init|=
name|processMonitor
operator|.
name|getRunningXQueries
argument_list|()
decl_stmt|;
for|for
control|(
name|XQueryWatchDog
name|watchdog
range|:
name|watchdogs
control|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
name|watchdog
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
name|context
operator|.
name|hashCode
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|watchdog
operator|.
name|isTerminating
argument_list|()
condition|)
block|{
name|watchdog
operator|.
name|kill
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RecentQueryHistory
argument_list|>
name|getRecentQueryHistory
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RecentQueryHistory
argument_list|>
name|history
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|QueryHistory
index|[]
name|queryHistories
init|=
name|processMonitor
operator|.
name|getRecentQueryHistory
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|QueryHistory
name|queryHistory
range|:
name|queryHistories
control|)
block|{
name|history
operator|.
name|add
argument_list|(
operator|new
name|RecentQueryHistory
argument_list|(
name|i
operator|++
argument_list|,
name|queryHistory
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|history
return|;
block|}
comment|/**      * Sets the time span (in milliseconds) for which the stats for an executed query should      * be kept in the recent query history.      *      * @param time      */
annotation|@
name|Override
specifier|public
name|void
name|setHistoryTimespan
parameter_list|(
specifier|final
name|long
name|time
parameter_list|)
block|{
name|processMonitor
operator|.
name|setHistoryTimespan
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getHistoryTimespan
parameter_list|()
block|{
return|return
name|processMonitor
operator|.
name|getHistoryTimespan
argument_list|()
return|;
block|}
comment|/**      * Sets the minimum execution time of queries recorded in the recent query history.      * Queries faster than this are not recorded.      *      * @param time      */
annotation|@
name|Override
specifier|public
name|void
name|setMinTime
parameter_list|(
specifier|final
name|long
name|time
parameter_list|)
block|{
name|processMonitor
operator|.
name|setMinTime
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMinTime
parameter_list|()
block|{
return|return
name|processMonitor
operator|.
name|getMinTime
argument_list|()
return|;
block|}
comment|/**      * Enable request tracking: for every executed query, try to figure out which HTTP      * URL triggered it (if applicable). For performance reasons this is disabled by default,      * though the overhead should be small.      *      * @param track      */
annotation|@
name|Override
specifier|public
name|void
name|setTrackRequestURI
parameter_list|(
specifier|final
name|boolean
name|track
parameter_list|)
block|{
name|processMonitor
operator|.
name|setTrackRequestURI
argument_list|(
name|track
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getTrackRequestURI
parameter_list|()
block|{
return|return
name|processMonitor
operator|.
name|getTrackRequestURI
argument_list|()
return|;
block|}
comment|/**      * Configure all settings related to recent query history.      *      * @param minTimeRecorded The minimum duration of a query (in milliseconds) to be added to the query history      *                        (see {@link ProcessMonitor#setMinTime(long)}).      * @param historyTimespan The max duration (in milliseconds) for which queries are tracked in the query history      *                        (see {@link ProcessMonitor#setHistoryTimespan(long)}).      * @param trackURI        Set to true if the class should attempt to determine the HTTP URI through which the query was triggered      *                        (see {@link ProcessMonitor#setHistoryTimespan(long)}).      */
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|long
name|minTimeRecorded
parameter_list|,
specifier|final
name|long
name|historyTimespan
parameter_list|,
specifier|final
name|boolean
name|trackURI
parameter_list|)
block|{
name|processMonitor
operator|.
name|setMinTime
argument_list|(
name|minTimeRecorded
argument_list|)
expr_stmt|;
name|processMonitor
operator|.
name|setHistoryTimespan
argument_list|(
name|historyTimespan
argument_list|)
expr_stmt|;
name|processMonitor
operator|.
name|setTrackRequestURI
argument_list|(
name|trackURI
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

