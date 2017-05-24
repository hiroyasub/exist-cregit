begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-08 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|management
operator|.
name|Agent
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
name|util
operator|.
name|DatabaseConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceAlreadyExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanRegistrationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerFactory
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
name|NotCompliantMBeanException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  * Real implementation of interface {@link org.exist.management.Agent}  * which registers MBeans with the MBeanServer.  */
end_comment

begin_class
specifier|public
class|class
name|JMXAgent
implements|implements
name|Agent
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
name|JMXAgent
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|volatile
name|Agent
name|agent
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|Agent
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|agent
operator|==
literal|null
condition|)
block|{
name|agent
operator|=
operator|new
name|JMXAgent
argument_list|()
expr_stmt|;
block|}
return|return
name|agent
return|;
block|}
specifier|private
name|MBeanServer
name|server
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Stack
argument_list|<
name|ObjectName
argument_list|>
argument_list|>
name|registeredMBeans
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|ObjectName
argument_list|,
name|Object
argument_list|>
name|beanInstances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|JMXAgent
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating the JMX MBeanServer."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ArrayList
argument_list|<
name|MBeanServer
argument_list|>
name|servers
init|=
name|MBeanServerFactory
operator|.
name|findMBeanServer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|servers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|server
operator|=
name|servers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|server
operator|=
name|MBeanServerFactory
operator|.
name|createMBeanServer
argument_list|()
expr_stmt|;
block|}
comment|//        try {
comment|//            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:9999/server");
comment|//            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
comment|//            cs.start();
comment|//        } catch (IOException e) {
comment|//            LOG.warn("ERROR: failed to initialize JMX connector: " + e.getMessage(), e);
comment|//        }
name|registerSystemMBeans
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|registerSystemMBeans
parameter_list|()
block|{
try|try
block|{
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management:type=LockTable"
argument_list|)
decl_stmt|;
name|addMBean
argument_list|(
name|name
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
operator|.
name|LockTable
argument_list|()
argument_list|)
expr_stmt|;
name|name
operator|=
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management:type=SystemInfo"
argument_list|)
expr_stmt|;
name|addMBean
argument_list|(
name|name
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
operator|.
name|SystemInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedObjectNameException
decl||
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while registering cache mbean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|initDBInstance
parameter_list|(
specifier|final
name|BrokerPool
name|instance
parameter_list|)
block|{
try|try
block|{
name|addMBean
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|,
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|":type=Database"
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
operator|.
name|Database
argument_list|(
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|addMBean
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|,
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|".tasks:type=SanityReport"
argument_list|,
operator|new
name|SanityReport
argument_list|(
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|addMBean
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|,
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|":type=DiskUsage"
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|addMBean
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|,
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|":type=ProcessReport"
argument_list|,
operator|new
name|ProcessReport
argument_list|(
name|instance
argument_list|)
argument_list|)
expr_stmt|;
name|addMBean
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|,
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|":type=BinaryValues"
argument_list|,
operator|new
name|BinaryValues
argument_list|()
argument_list|)
expr_stmt|;
name|addMBean
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|,
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|":type=CollectionCache"
argument_list|,
operator|new
name|CollectionCache
argument_list|(
name|instance
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while registering database mbean."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|closeDBInstance
parameter_list|(
name|BrokerPool
name|instance
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Stack
argument_list|<
name|ObjectName
argument_list|>
name|stack
init|=
name|registeredMBeans
operator|.
name|get
argument_list|(
name|instance
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|ObjectName
name|on
init|=
operator|(
name|ObjectName
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"deregistering JMX MBean: "
operator|+
name|on
argument_list|)
expr_stmt|;
if|if
condition|(
name|server
operator|.
name|isRegistered
argument_list|(
name|on
argument_list|)
condition|)
block|{
name|server
operator|.
name|unregisterMBean
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InstanceNotFoundException
decl||
name|MBeanRegistrationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem found while unregistering JMX"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|addMBean
parameter_list|(
name|String
name|dbInstance
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|mbean
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
try|try
block|{
specifier|final
name|ObjectName
name|on
init|=
operator|new
name|ObjectName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|addMBean
argument_list|(
name|on
argument_list|,
name|mbean
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbInstance
operator|!=
literal|null
condition|)
block|{
name|Stack
argument_list|<
name|ObjectName
argument_list|>
name|stack
init|=
name|registeredMBeans
operator|.
name|get
argument_list|(
name|dbInstance
argument_list|)
decl_stmt|;
if|if
condition|(
name|stack
operator|==
literal|null
condition|)
block|{
name|stack
operator|=
operator|new
name|Stack
argument_list|<>
argument_list|()
expr_stmt|;
name|registeredMBeans
operator|.
name|put
argument_list|(
name|dbInstance
argument_list|,
name|stack
argument_list|)
expr_stmt|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
name|beanInstances
operator|.
name|put
argument_list|(
name|on
argument_list|,
name|mbean
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem registering mbean: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Exception while registering JMX mbean: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|addMBean
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|Object
name|mbean
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|server
operator|.
name|isRegistered
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|server
operator|.
name|registerMBean
argument_list|(
name|mbean
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InstanceAlreadyExistsException
decl||
name|MBeanRegistrationException
decl||
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem registering mbean: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Exception while registering JMX mbean: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|changeStatus
parameter_list|(
name|BrokerPool
name|instance
parameter_list|,
name|TaskStatus
name|actualStatus
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|".tasks:type=SanityReport"
argument_list|)
decl_stmt|;
specifier|final
name|SanityReport
name|report
init|=
operator|(
name|SanityReport
operator|)
name|beanInstances
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
block|{
name|report
operator|.
name|changeStatus
argument_list|(
name|actualStatus
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem calling mbean: "
operator|+
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
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|updateStatus
parameter_list|(
name|BrokerPool
name|instance
parameter_list|,
name|int
name|percentage
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ObjectName
name|name
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.exist.management."
operator|+
name|instance
operator|.
name|getId
argument_list|()
operator|+
literal|".tasks:type=SanityReport"
argument_list|)
decl_stmt|;
specifier|final
name|SanityReport
name|report
init|=
operator|(
name|SanityReport
operator|)
name|beanInstances
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
block|{
name|report
operator|.
name|updateStatus
argument_list|(
name|percentage
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem calling mbean: "
operator|+
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
end_class

end_unit

