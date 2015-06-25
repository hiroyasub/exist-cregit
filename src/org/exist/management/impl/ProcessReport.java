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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
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
name|ProcessReportMBean
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ProcessReport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|pItemNames
init|=
block|{
literal|"id"
block|,
literal|"action"
block|,
literal|"info"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|pItemDescriptions
init|=
block|{
literal|"Process ID"
block|,
literal|"Description of the current action"
block|,
literal|"Additional info provided by thread"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|pIndexNames
init|=
block|{
literal|"id"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qItemNames
init|=
block|{
literal|"id"
block|,
literal|"sourceType"
block|,
literal|"sourceKey"
block|,
literal|"terminating"
block|,
literal|"requestURI"
block|,
literal|"thread"
block|,
literal|"elapsed"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qItemDescriptions
init|=
block|{
literal|"XQuery ID"
block|,
literal|"Type of the query source"
block|,
literal|"Description of the source"
block|,
literal|"Is query terminating?"
block|,
literal|"The URI by which the query was called (if any)"
block|,
literal|"The thread running this query"
block|,
literal|"The time in milliseconds since the query was started"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qIndexNames
init|=
block|{
literal|"id"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qhItemNames
init|=
block|{
literal|"idx"
block|,
literal|"sourceKey"
block|,
literal|"recentInvocationCount"
block|,
literal|"mostRecentExecutionTime"
block|,
literal|"mostRecentExecutionDuration"
block|,
literal|"requestURI"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qhItemDescriptions
init|=
block|{
literal|"Index of the query in the history"
block|,
literal|"Description of the source"
block|,
literal|"Recent invocation count"
block|,
literal|"Most recent query invocation start time"
block|,
literal|"Most recent query invocation duration"
block|,
literal|"The URI by which the query was called (if any)"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qhIndexNames
init|=
block|{
literal|"idx"
block|}
decl_stmt|;
specifier|private
name|ProcessMonitor
name|processMonitor
decl_stmt|;
specifier|private
name|Scheduler
name|scheduler
decl_stmt|;
specifier|public
name|ProcessReport
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|processMonitor
operator|=
name|pool
operator|.
name|getProcessMonitor
argument_list|()
expr_stmt|;
name|scheduler
operator|=
name|pool
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getScheduledJobs
parameter_list|()
block|{
specifier|final
name|OpenType
argument_list|<
name|?
argument_list|>
index|[]
name|itemTypes
init|=
block|{
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|}
decl_stmt|;
name|CompositeType
name|infoType
decl_stmt|;
try|try
block|{
name|infoType
operator|=
operator|new
name|CompositeType
argument_list|(
literal|"scheduledJobs"
argument_list|,
literal|"Lists currently scheduled jobs in eXist"
argument_list|,
name|pItemNames
argument_list|,
name|pItemDescriptions
argument_list|,
name|itemTypes
argument_list|)
expr_stmt|;
specifier|final
name|TabularType
name|tabularType
init|=
operator|new
name|TabularType
argument_list|(
literal|"jobList"
argument_list|,
literal|"List of currently scheduled jobs"
argument_list|,
name|infoType
argument_list|,
name|pIndexNames
argument_list|)
decl_stmt|;
specifier|final
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
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
specifier|final
name|Object
index|[]
name|itemValues
init|=
block|{
name|job
operator|.
name|getName
argument_list|()
block|,
name|job
operator|.
name|getGroup
argument_list|()
block|,
name|job
operator|.
name|getTriggerExpression
argument_list|()
block|}
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|infoType
argument_list|,
name|pItemNames
argument_list|,
name|itemValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|OpenDataException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getRunningJobs
parameter_list|()
block|{
specifier|final
name|OpenType
argument_list|<
name|?
argument_list|>
index|[]
name|itemTypes
init|=
block|{
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|}
decl_stmt|;
name|CompositeType
name|infoType
decl_stmt|;
try|try
block|{
name|infoType
operator|=
operator|new
name|CompositeType
argument_list|(
literal|"runningJobs"
argument_list|,
literal|"Lists currently running jobs in eXist"
argument_list|,
name|pItemNames
argument_list|,
name|pItemDescriptions
argument_list|,
name|itemTypes
argument_list|)
expr_stmt|;
specifier|final
name|TabularType
name|tabularType
init|=
operator|new
name|TabularType
argument_list|(
literal|"jobList"
argument_list|,
literal|"List of currently running jobs"
argument_list|,
name|infoType
argument_list|,
name|pIndexNames
argument_list|)
decl_stmt|;
specifier|final
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
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
name|ProcessMonitor
operator|.
name|JobInfo
name|job
range|:
name|jobs
control|)
block|{
specifier|final
name|Object
index|[]
name|itemValues
init|=
block|{
name|job
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
block|,
name|job
operator|.
name|getAction
argument_list|()
block|,
name|job
operator|.
name|getAddInfo
argument_list|()
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|infoType
argument_list|,
name|pItemNames
argument_list|,
name|itemValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|OpenDataException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getRunningQueries
parameter_list|()
block|{
specifier|final
name|OpenType
argument_list|<
name|?
argument_list|>
index|[]
name|itemTypes
init|=
block|{
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|BOOLEAN
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|LONG
block|}
decl_stmt|;
name|CompositeType
name|infoType
decl_stmt|;
try|try
block|{
name|infoType
operator|=
operator|new
name|CompositeType
argument_list|(
literal|"runningQueries"
argument_list|,
literal|"Lists currently running XQueries"
argument_list|,
name|qItemNames
argument_list|,
name|qItemDescriptions
argument_list|,
name|itemTypes
argument_list|)
expr_stmt|;
specifier|final
name|TabularType
name|tabularType
init|=
operator|new
name|TabularType
argument_list|(
literal|"queryList"
argument_list|,
literal|"List of currently running XQueries"
argument_list|,
name|infoType
argument_list|,
name|qIndexNames
argument_list|)
decl_stmt|;
specifier|final
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
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
specifier|final
name|Object
index|[]
name|itemValues
init|=
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
block|,
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|getXacmlSource
argument_list|()
operator|.
name|getType
argument_list|()
block|,
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|getXacmlSource
argument_list|()
operator|.
name|getKey
argument_list|()
block|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|watchdog
operator|.
name|isTerminating
argument_list|()
argument_list|)
block|,
name|requestURI
block|,
name|watchdog
operator|.
name|getRunningThread
argument_list|()
block|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|watchdog
operator|.
name|getStartTime
argument_list|()
block|}
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|infoType
argument_list|,
name|qItemNames
argument_list|,
name|itemValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|OpenDataException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
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
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|killQuery
parameter_list|(
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
name|TabularData
name|getRecentQueryHistory
parameter_list|()
block|{
specifier|final
name|OpenType
argument_list|<
name|?
argument_list|>
index|[]
name|itemTypes
init|=
block|{
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|STRING
block|}
decl_stmt|;
name|CompositeType
name|infoType
decl_stmt|;
try|try
block|{
name|infoType
operator|=
operator|new
name|CompositeType
argument_list|(
literal|"recentQueryHistory"
argument_list|,
literal|"Lists recently completed XQueries"
argument_list|,
name|qhItemNames
argument_list|,
name|qhItemDescriptions
argument_list|,
name|itemTypes
argument_list|)
expr_stmt|;
specifier|final
name|TabularType
name|tabularType
init|=
operator|new
name|TabularType
argument_list|(
literal|"queryList"
argument_list|,
literal|"List of recently completed XQueries"
argument_list|,
name|infoType
argument_list|,
name|qhIndexNames
argument_list|)
decl_stmt|;
specifier|final
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
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
specifier|final
name|Object
index|[]
name|itemValues
init|=
block|{
name|i
operator|++
block|,
name|queryHistory
operator|.
name|getSource
argument_list|()
block|,
name|queryHistory
operator|.
name|getInvocationCount
argument_list|()
block|,
name|queryHistory
operator|.
name|getMostRecentExecutionTime
argument_list|()
block|,
name|queryHistory
operator|.
name|getMostRecentExecutionDuration
argument_list|()
block|,
name|queryHistory
operator|.
name|getRequestURI
argument_list|()
block|}
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|infoType
argument_list|,
name|qhItemNames
argument_list|,
name|itemValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|OpenDataException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
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
return|return
literal|null
return|;
block|}
comment|/**      * Sets the time span (in milliseconds) for which the stats for an executed query should      * be kept in the recent query history.      *      * @param time      */
annotation|@
name|Override
specifier|public
name|void
name|setHistoryTimespan
parameter_list|(
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
comment|/**      * Configure all settings related to recent query history.      *      * @param minTimeRecorded The minimum duration of a query (in milliseconds) to be added to the query history      *                        (see {@link ProcessMonitor#setMinTime(long)}).      * @param historyTimespan The max duration (in milliseconds) for which queries are tracked in the query history      *                        (see {@link ProcessMonitor#setHistoryTimespan(long)}).      * @param trackURI Set to true if the class should attempt to determine the HTTP URI through which the query was triggered      *                 (see {@link ProcessMonitor#setHistoryTimespan(long)}).      */
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|long
name|minTimeRecorded
parameter_list|,
name|long
name|historyTimespan
parameter_list|,
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

