begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|system
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|List
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|MemTreeBuilder
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
name|ScheduledJobInfo
operator|.
name|TriggerState
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
name|xquery
operator|.
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XPathException
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
name|value
operator|.
name|FunctionReturnSequenceType
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
name|value
operator|.
name|NodeValue
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|Type
import|;
end_import

begin_class
specifier|public
class|class
name|GetScheduledJobs
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|GetScheduledJobs
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TODAY_TIMESTAMP
init|=
literal|"HH:mm:ss.SSS Z"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DATE_TIME_FORMAT
init|=
literal|"yyyy-MM-dd HH:mm:ss.SSS Z"
decl_stmt|;
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
name|SystemModule
operator|.
name|NAMESPACE_URI
decl_stmt|;
specifier|final
specifier|static
name|String
name|PREFIX
init|=
name|SystemModule
operator|.
name|PREFIX
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-scheduled-jobs"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Get a list of scheduled jobs (dba role only)."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"a node containing the list of scheduled jobs"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetScheduledJobs
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
specifier|final
name|XPathException
name|xPathException
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling user '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be a DBA to get the list of scheduled jobs"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":get-scheduled-jobs"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jobs"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"brokerPool = "
operator|+
name|brokerPool
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
specifier|final
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|Scheduler
name|existScheduler
init|=
name|brokerPool
operator|.
name|getScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|existScheduler
operator|!=
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|ScheduledJobInfo
argument_list|>
name|scheduledJobsInfo
init|=
name|existScheduler
operator|.
name|getScheduledJobs
argument_list|()
decl_stmt|;
specifier|final
name|ScheduledJobInfo
index|[]
name|executingJobsInfo
init|=
name|existScheduler
operator|.
name|getExecutingJobs
argument_list|()
decl_stmt|;
if|if
condition|(
name|scheduledJobsInfo
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|ScheduledJobInfo
name|scheduledJobInfo
range|:
name|scheduledJobsInfo
control|)
block|{
name|addRow
argument_list|(
name|scheduledJobInfo
argument_list|,
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|executingJobsInfo
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|executingJobsInfo
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addRow
argument_list|(
name|executingJobsInfo
index|[
name|i
index|]
argument_list|,
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
operator|(
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
operator|)
return|;
block|}
specifier|private
name|void
name|addRow
parameter_list|(
name|ScheduledJobInfo
name|scheduledJobInfo
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|,
name|boolean
name|isRunning
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Entring addRow"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|scheduledJobInfo
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|group
init|=
name|scheduledJobInfo
operator|.
name|getGroup
argument_list|()
decl_stmt|;
specifier|final
name|String
name|triggerName
init|=
name|scheduledJobInfo
operator|.
name|getTriggerName
argument_list|()
decl_stmt|;
specifier|final
name|Date
name|startTime
init|=
name|scheduledJobInfo
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
specifier|final
name|Date
name|endTime
init|=
name|scheduledJobInfo
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
specifier|final
name|Date
name|fireTime
init|=
name|scheduledJobInfo
operator|.
name|getPreviousFireTime
argument_list|()
decl_stmt|;
specifier|final
name|Date
name|nextFireTime
init|=
name|scheduledJobInfo
operator|.
name|getNextFireTime
argument_list|()
decl_stmt|;
specifier|final
name|Date
name|finalFireTime
init|=
name|scheduledJobInfo
operator|.
name|getFinalFireTime
argument_list|()
decl_stmt|;
specifier|final
name|String
name|triggerExpression
init|=
name|scheduledJobInfo
operator|.
name|getTriggerExpression
argument_list|()
decl_stmt|;
specifier|final
name|TriggerState
name|triggerState
init|=
name|scheduledJobInfo
operator|.
name|getTriggerState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"job"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"group"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"triggerName"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|triggerName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"startTime"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dateText
argument_list|(
name|startTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"endTime"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dateText
argument_list|(
name|endTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"fireTime"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dateText
argument_list|(
name|fireTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"nextFireTime"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dateText
argument_list|(
name|nextFireTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"finalFireTime"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dateText
argument_list|(
name|finalFireTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"triggerExpression"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|triggerExpression
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"triggerState"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|triggerState
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"running"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|(
name|isRunning
operator|)
condition|?
literal|"RUNNING"
else|:
literal|"SCHEDULED"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Exiting addRow"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|dateText
parameter_list|(
name|Date
name|aDate
parameter_list|)
block|{
name|String
name|returnValue
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|aDate
operator|!=
literal|null
condition|)
block|{
name|String
name|formatString
init|=
name|DATE_TIME_FORMAT
decl_stmt|;
if|if
condition|(
name|isToday
argument_list|(
name|aDate
argument_list|)
condition|)
block|{
name|formatString
operator|=
name|TODAY_TIMESTAMP
expr_stmt|;
block|}
specifier|final
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|formatString
argument_list|)
decl_stmt|;
name|returnValue
operator|=
name|format
operator|.
name|format
argument_list|(
name|aDate
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|returnValue
operator|)
return|;
block|}
specifier|private
name|boolean
name|isToday
parameter_list|(
name|Date
name|aDate
parameter_list|)
block|{
specifier|final
name|Calendar
name|aCal1
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|aCal1
operator|.
name|setTime
argument_list|(
name|aDate
argument_list|)
expr_stmt|;
specifier|final
name|Calendar
name|aCal2
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|aCal1
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|)
operator|==
name|aCal2
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|)
operator|)
operator|&&
operator|(
name|aCal1
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
operator|==
name|aCal2
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|)
operator|)
operator|&&
operator|(
name|aCal1
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|==
name|aCal2
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|)
operator|)
condition|)
block|{
return|return
operator|(
literal|true
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
literal|false
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

