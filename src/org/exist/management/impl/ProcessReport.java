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
name|XQueryWatchDog
import|;
end_import

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
name|Logger
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
literal|"sourceKey"
block|,
literal|"recentInvocationCount"
block|,
literal|"mostRecentExecutionTime"
block|,
literal|"mostRecentExecutionDuration"
block|}
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qhItemDescriptions
init|=
block|{
literal|"Description of the source"
block|,
literal|"Recent invocation count"
block|,
literal|"Most recent query invocation start time"
block|,
literal|"Most recent query invocation duration"
block|,     }
decl_stmt|;
specifier|private
specifier|static
name|String
index|[]
name|qhIndexNames
init|=
block|{
literal|"sourceKey"
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
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
decl_stmt|;
name|ScheduledJobInfo
index|[]
name|jobs
init|=
name|scheduler
operator|.
name|getScheduledJobs
argument_list|()
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
name|jobs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Object
index|[]
name|itemValues
init|=
block|{
name|jobs
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
block|,
name|jobs
index|[
name|i
index|]
operator|.
name|getGroup
argument_list|()
block|,
name|jobs
index|[
name|i
index|]
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
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
decl_stmt|;
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|jobs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Object
index|[]
name|itemValues
init|=
block|{
name|jobs
index|[
name|i
index|]
operator|.
name|getThread
argument_list|()
operator|.
name|getName
argument_list|()
block|,
name|jobs
index|[
name|i
index|]
operator|.
name|getAction
argument_list|()
block|,
name|jobs
index|[
name|i
index|]
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
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
decl_stmt|;
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|watchdogs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Object
index|[]
name|itemValues
init|=
block|{
operator|new
name|Integer
argument_list|(
name|watchdogs
index|[
name|i
index|]
operator|.
name|getContext
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
block|,
name|watchdogs
index|[
name|i
index|]
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
name|watchdogs
index|[
name|i
index|]
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
name|watchdogs
index|[
name|i
index|]
operator|.
name|isTerminating
argument_list|()
argument_list|)
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
name|getRecentQueryHistory
parameter_list|()
block|{
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
name|INTEGER
block|,
name|SimpleType
operator|.
name|LONG
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
name|TabularDataSupport
name|data
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tabularType
argument_list|)
decl_stmt|;
name|QueryHistory
index|[]
name|queryHistories
init|=
name|processMonitor
operator|.
name|getRecentQueryHistory
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryHistory
name|queryHistory
range|:
name|queryHistories
control|)
block|{
name|Object
index|[]
name|itemValues
init|=
block|{
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
block|}
end_class

end_unit

