begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|replication
operator|.
name|jms
operator|.
name|subscribe
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
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
name|org
operator|.
name|exist
operator|.
name|replication
operator|.
name|shared
operator|.
name|JmsConnectionExceptionListener
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
name|StartupTrigger
import|;
end_import

begin_comment
comment|/**  * Startup Trigger to fire-up a message receiver. Typically this trigger is started by  * configuration in conf.xml  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|MessageReceiverStartupTrigger
implements|implements
name|StartupTrigger
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
name|MessageReceiverStartupTrigger
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Helper method to give resources back      */
specifier|private
name|void
name|closeSilent
parameter_list|(
name|Context
name|context
parameter_list|,
name|Connection
name|connection
parameter_list|,
name|Session
name|session
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing JMS session, connection and context"
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
comment|// Get from .xconf file, fill defaults when needed
name|SubscriberParameters
name|parameters
init|=
operator|new
name|SubscriberParameters
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|setSingleValueParameters
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
literal|null
decl_stmt|;
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Get parameters, fill defaults when needed
name|parameters
operator|.
name|processParameters
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting subscription of '"
operator|+
name|parameters
operator|.
name|getSubscriberName
argument_list|()
operator|+
literal|"' to '"
operator|+
name|parameters
operator|.
name|getTopic
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
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
name|parameters
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Setup listeners
name|JMSMessageListener
name|jmsListener
init|=
operator|new
name|JMSMessageListener
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|ExceptionListener
name|exceptionListener
init|=
operator|new
name|JmsConnectionExceptionListener
argument_list|()
decl_stmt|;
comment|// Setup context
name|Properties
name|contextProps
init|=
name|parameters
operator|.
name|getInitialContextProps
argument_list|()
decl_stmt|;
name|context
operator|=
operator|new
name|InitialContext
argument_list|(
name|contextProps
argument_list|)
expr_stmt|;
comment|// Lookup topic
name|Destination
name|destination
init|=
operator|(
name|Destination
operator|)
name|context
operator|.
name|lookup
argument_list|(
name|parameters
operator|.
name|getTopic
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|destination
operator|instanceof
name|Topic
operator|)
condition|)
block|{
name|String
name|errorText
init|=
literal|"'"
operator|+
name|parameters
operator|.
name|getTopic
argument_list|()
operator|+
literal|"' is not a Topic."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errorText
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
name|errorText
argument_list|)
throw|;
block|}
comment|// Lookup connection factory
name|ConnectionFactory
name|cf
init|=
operator|(
name|ConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
name|parameters
operator|.
name|getConnectionFactory
argument_list|()
argument_list|)
decl_stmt|;
comment|// Setup connection
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|()
expr_stmt|;
comment|// Register for exceptions
name|connection
operator|.
name|setExceptionListener
argument_list|(
name|exceptionListener
argument_list|)
expr_stmt|;
comment|// Set clientId
name|connection
operator|.
name|setClientID
argument_list|(
name|parameters
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO DW: should this be configurable?
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
comment|// Set durable messaging, when required
if|if
condition|(
name|parameters
operator|.
name|isDurable
argument_list|()
condition|)
block|{
comment|// Set subscriber
name|TopicSubscriber
name|topicSubscriber
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|parameters
operator|.
name|getSubscriberName
argument_list|()
argument_list|,
name|parameters
operator|.
name|getMessageSelector
argument_list|()
argument_list|,
name|parameters
operator|.
name|isNoLocal
argument_list|()
argument_list|)
decl_stmt|;
comment|// Register listeners
name|topicSubscriber
operator|.
name|setMessageListener
argument_list|(
name|jmsListener
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Create message consumer
name|MessageConsumer
name|messageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|,
name|parameters
operator|.
name|getMessageSelector
argument_list|()
argument_list|,
name|parameters
operator|.
name|isNoLocal
argument_list|()
argument_list|)
decl_stmt|;
comment|// Register listeners
name|messageConsumer
operator|.
name|setMessageListener
argument_list|(
name|jmsListener
argument_list|)
expr_stmt|;
block|}
comment|// Start it all
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Subscription was successful."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|t
parameter_list|)
block|{
comment|// Close all that has been opened. Always.
name|closeSilent
argument_list|(
name|context
argument_list|,
name|connection
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to start subscription: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
operator|+
literal|";  "
operator|+
name|parameters
operator|.
name|getReport
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

