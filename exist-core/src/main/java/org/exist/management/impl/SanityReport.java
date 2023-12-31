begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-10 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|*
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|TaskStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
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
name|ConsistencyCheckTask
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
name|DBBroker
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
name|storage
operator|.
name|XQueryPool
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
name|CompiledXQuery
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
name|XQuery
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

begin_class
specifier|public
class|class
name|SanityReport
extends|extends
name|NotificationBroadcasterSupport
implements|implements
name|SanityReportMXBean
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
name|SanityReport
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STATUS_OK
init|=
literal|"OK"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STATUS_FAIL
init|=
literal|"FAIL"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringSource
name|TEST_XQUERY
init|=
operator|new
name|StringSource
argument_list|(
literal|"<r>{current-dateTime()}</r>"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PING_WAITING
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PING_ERROR
init|=
operator|-
literal|2
decl_stmt|;
specifier|private
specifier|static
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|NO_ERRORS
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|seqNum
init|=
literal|0
decl_stmt|;
specifier|private
name|Date
name|actualCheckStart
init|=
literal|null
decl_stmt|;
specifier|private
name|Date
name|lastCheckStart
init|=
literal|null
decl_stmt|;
specifier|private
name|Date
name|lastCheckEnd
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|lastActionInfo
init|=
literal|"nothing done"
decl_stmt|;
specifier|private
name|long
name|lastPingRespTime
init|=
literal|0
decl_stmt|;
specifier|private
name|String
name|output
init|=
literal|""
decl_stmt|;
specifier|private
name|TaskStatus
name|taskstatus
init|=
operator|new
name|TaskStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|NEVER_RUN
argument_list|)
decl_stmt|;
specifier|private
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
name|NO_ERRORS
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|SanityReport
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getAllInstancesQuery
parameter_list|()
block|{
return|return
literal|"org.exist.management."
operator|+
literal|'*'
operator|+
literal|":type=SanityReport"
return|;
block|}
specifier|public
specifier|static
name|ObjectName
name|getName
parameter_list|(
specifier|final
name|String
name|instanceId
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
return|return
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instanceId
operator|+
literal|".tasks:type=SanityReport"
argument_list|)
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
name|getName
argument_list|(
name|pool
operator|.
name|getId
argument_list|()
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
name|pool
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|MBeanNotificationInfo
index|[]
name|getNotificationInfo
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|types
init|=
operator|new
name|String
index|[]
block|{
name|AttributeChangeNotification
operator|.
name|ATTRIBUTE_CHANGE
block|}
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|AttributeChangeNotification
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|description
init|=
literal|"The status attribute of this MBean has changed"
decl_stmt|;
specifier|final
name|MBeanNotificationInfo
name|info
init|=
operator|new
name|MBeanNotificationInfo
argument_list|(
name|types
argument_list|,
name|name
argument_list|,
name|description
argument_list|)
decl_stmt|;
return|return
operator|new
name|MBeanNotificationInfo
index|[]
block|{
name|info
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|getLastCheckEnd
parameter_list|()
block|{
return|return
name|lastCheckEnd
return|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|getLastCheckStart
parameter_list|()
block|{
return|return
name|lastCheckStart
return|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|getActualCheckStart
parameter_list|()
block|{
return|return
name|actualCheckStart
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|taskstatus
operator|.
name|getStatusString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastActionInfo
parameter_list|()
block|{
return|return
name|lastActionInfo
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPingTime
parameter_list|()
block|{
return|return
name|lastPingRespTime
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Error
argument_list|>
name|getErrors
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Error
argument_list|>
name|errorList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ErrorReport
name|error
range|:
name|errors
control|)
block|{
name|errorList
operator|.
name|add
argument_list|(
operator|new
name|Error
argument_list|(
name|error
operator|.
name|getErrcodeString
argument_list|()
argument_list|,
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|errorList
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|triggerCheck
parameter_list|(
name|String
name|output
parameter_list|,
name|String
name|backup
parameter_list|,
name|String
name|incremental
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
specifier|final
name|SystemTask
name|task
init|=
operator|new
name|ConsistencyCheckTask
argument_list|()
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
name|parseParameter
argument_list|(
name|output
argument_list|,
name|backup
argument_list|,
name|incremental
argument_list|)
decl_stmt|;
name|task
operator|.
name|configure
argument_list|(
name|pool
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|pool
operator|.
name|triggerSystemTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|existException
parameter_list|)
block|{
name|taskstatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|STOPPED_ERROR
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|errors
operator|.
name|add
argument_list|(
operator|new
name|ErrorReport
argument_list|(
name|ErrorReport
operator|.
name|CONFIGURATION_FAILD
argument_list|,
name|existException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|existException
argument_list|)
argument_list|)
expr_stmt|;
name|taskstatus
operator|.
name|setReason
argument_list|(
name|errors
argument_list|)
expr_stmt|;
name|changeStatus
argument_list|(
name|taskstatus
argument_list|)
expr_stmt|;
name|taskstatus
operator|.
name|setStatusChangeTime
argument_list|()
expr_stmt|;
name|taskstatus
operator|.
name|setReason
argument_list|(
name|existException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to trigger db sanity check: "
operator|+
name|existException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|existException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|ping
parameter_list|(
name|boolean
name|checkQueryEngine
parameter_list|)
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|lastPingRespTime
operator|=
operator|-
literal|1
expr_stmt|;
name|lastActionInfo
operator|=
literal|"Ping"
expr_stmt|;
name|taskstatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|PING_WAIT
argument_list|)
expr_stmt|;
comment|// try to acquire a broker. If the db is deadlocked or not responsive,
comment|// this will block forever.
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getGuestSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
if|if
condition|(
name|checkQueryEngine
condition|)
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|XQueryPool
name|xqPool
init|=
name|pool
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|xqPool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|TEST_XQUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|TEST_XQUERY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compiled
operator|.
name|getContext
argument_list|()
operator|.
name|prepareForReuse
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|compiled
operator|.
name|getContext
argument_list|()
operator|.
name|runCleanupTasks
argument_list|()
expr_stmt|;
name|xqPool
operator|.
name|returnCompiledXQuery
argument_list|(
name|TEST_XQUERY
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|lastPingRespTime
operator|=
operator|-
literal|2
expr_stmt|;
name|taskstatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|PING_ERROR
argument_list|)
expr_stmt|;
name|taskstatus
operator|.
name|setStatusChangeTime
argument_list|()
expr_stmt|;
name|taskstatus
operator|.
name|setReason
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|changeStatus
argument_list|(
name|taskstatus
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lastPingRespTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
name|taskstatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|PING_OK
argument_list|)
expr_stmt|;
name|taskstatus
operator|.
name|setStatusChangeTime
argument_list|()
expr_stmt|;
name|taskstatus
operator|.
name|setReason
argument_list|(
literal|"ping response time: "
operator|+
name|lastPingRespTime
argument_list|)
expr_stmt|;
name|changeStatus
argument_list|(
name|taskstatus
argument_list|)
expr_stmt|;
block|}
return|return
name|lastPingRespTime
return|;
block|}
specifier|private
name|Properties
name|parseParameter
parameter_list|(
name|String
name|output
parameter_list|,
name|String
name|backup
parameter_list|,
name|String
name|incremental
parameter_list|)
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|doBackup
init|=
name|backup
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"YES"
argument_list|)
decl_stmt|;
comment|// This should be simplified
if|if
condition|(
name|backup
operator|!=
literal|null
operator|&&
operator|(
name|doBackup
operator|)
operator|||
name|backup
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
literal|"backup"
argument_list|,
name|backup
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|incremental
operator|!=
literal|null
operator|&&
operator|(
name|incremental
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"YES"
argument_list|)
operator|||
name|incremental
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
operator|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
literal|"incremental"
argument_list|,
name|incremental
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
literal|"output"
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|put
argument_list|(
literal|"backup"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
specifier|protected
name|void
name|updateErrors
parameter_list|(
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errorList
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|errorList
operator|==
literal|null
operator|||
name|errorList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|taskstatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|STOPPED_OK
argument_list|)
expr_stmt|;
name|this
operator|.
name|errors
operator|=
name|NO_ERRORS
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|errors
operator|=
name|errorList
expr_stmt|;
name|taskstatus
operator|.
name|setStatus
argument_list|(
name|TaskStatus
operator|.
name|Status
operator|.
name|STOPPED_ERROR
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
specifier|protected
name|void
name|changeStatus
parameter_list|(
name|TaskStatus
name|status
parameter_list|)
block|{
name|status
operator|.
name|setStatusChangeTime
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|status
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|INIT
case|:
name|actualCheckStart
operator|=
name|status
operator|.
name|getStatusChangeTime
argument_list|()
expr_stmt|;
break|break;
case|case
name|STOPPED_ERROR
case|:
case|case
name|STOPPED_OK
case|:
name|lastCheckStart
operator|=
name|actualCheckStart
expr_stmt|;
name|actualCheckStart
operator|=
literal|null
expr_stmt|;
name|lastCheckEnd
operator|=
name|status
operator|.
name|getStatusChangeTime
argument_list|()
expr_stmt|;
if|if
condition|(
name|status
operator|.
name|getReason
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|errors
operator|=
operator|(
name|List
argument_list|<
name|ErrorReport
argument_list|>
operator|)
name|status
operator|.
name|getReason
argument_list|()
expr_stmt|;
block|}
name|lastActionInfo
operator|=
name|taskstatus
operator|.
name|toString
argument_list|()
operator|+
literal|" to ["
operator|+
name|output
operator|+
literal|"] ended with status ["
operator|+
name|status
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
expr_stmt|;
break|break;
default|default:
break|break;
block|}
specifier|final
name|TaskStatus
name|oldState
init|=
name|taskstatus
decl_stmt|;
try|try
block|{
name|taskstatus
operator|=
name|status
expr_stmt|;
specifier|final
name|Notification
name|event
init|=
operator|new
name|AttributeChangeNotification
argument_list|(
name|this
argument_list|,
name|seqNum
operator|++
argument_list|,
name|taskstatus
operator|.
name|getStatusChangeTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
literal|"Status change"
argument_list|,
literal|"status"
argument_list|,
literal|"String"
argument_list|,
name|oldState
operator|.
name|toString
argument_list|()
argument_list|,
name|taskstatus
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|event
operator|.
name|setUserData
argument_list|(
name|taskstatus
operator|.
name|getCompositeData
argument_list|()
argument_list|)
expr_stmt|;
name|sendNotification
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
specifier|protected
name|void
name|updateStatus
parameter_list|(
name|int
name|percentage
parameter_list|)
block|{
try|try
block|{
specifier|final
name|int
name|oldPercentage
init|=
name|taskstatus
operator|.
name|getPercentage
argument_list|()
decl_stmt|;
name|taskstatus
operator|.
name|setPercentage
argument_list|(
name|percentage
argument_list|)
expr_stmt|;
specifier|final
name|Notification
name|event
init|=
operator|new
name|AttributeChangeNotification
argument_list|(
name|this
argument_list|,
name|seqNum
operator|++
argument_list|,
name|taskstatus
operator|.
name|getStatusChangeTime
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
literal|"Work percentage change"
argument_list|,
literal|"status"
argument_list|,
literal|"int"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|oldPercentage
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|taskstatus
operator|.
name|getPercentage
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|event
operator|.
name|setUserData
argument_list|(
name|taskstatus
operator|.
name|getCompositeData
argument_list|()
argument_list|)
expr_stmt|;
name|sendNotification
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
end_class

end_unit

