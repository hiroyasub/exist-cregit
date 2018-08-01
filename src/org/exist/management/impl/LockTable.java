begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2017 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|lock
operator|.
name|Lock
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
name|lock
operator|.
name|Lock
operator|.
name|LockType
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
name|lock
operator|.
name|LockTable
operator|.
name|LockModeOwner
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
name|lock
operator|.
name|LockTableUtils
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
name|Map
import|;
end_import

begin_comment
comment|/**  * JMX MXBean for examining the LockTable  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|LockTable
implements|implements
name|LockTableMXBean
block|{
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|LockTable
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|brokerPool
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
literal|":type=LockTable"
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
name|pool
operator|.
name|getId
argument_list|()
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
name|pool
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|LockType
argument_list|,
name|Map
argument_list|<
name|Lock
operator|.
name|LockMode
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|getAcquired
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
operator|.
name|getAcquired
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|LockType
argument_list|,
name|List
argument_list|<
name|LockModeOwner
argument_list|>
argument_list|>
argument_list|>
name|getAttempting
parameter_list|()
block|{
return|return
name|pool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
operator|.
name|getAttempting
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dumpToConsole
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|LockTableUtils
operator|.
name|stateToString
argument_list|(
name|pool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|Logger
name|LOCK_LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|LockTable
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|dumpToLog
parameter_list|()
block|{
name|LOCK_LOG
operator|.
name|info
argument_list|(
name|LockTableUtils
operator|.
name|stateToString
argument_list|(
name|pool
operator|.
name|getLockManager
argument_list|()
operator|.
name|getLockTable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

