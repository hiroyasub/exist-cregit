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
name|publish
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
name|MessageSender
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
name|TransportException
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
name|eXistMessage
import|;
end_import

begin_comment
comment|/**  * Specific class for sending a eXistMessage via JMS to a broker  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|JMSMessageSender
implements|implements
name|MessageSender
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
name|JMSMessageSender
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PublisherParameters
name|parameters
init|=
operator|new
name|PublisherParameters
argument_list|()
decl_stmt|;
comment|/**      * Constructor      *      * @param parameters Set of (Key,value) parameters for setting JMS routing      * instructions, like java.naming.* , destination and connection factory.      */
name|JMSMessageSender
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
name|parameters
operator|.
name|setMultiValueParameters
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Send {@link eXistMessage} to message broker.      *      * @param em The message that needs to be sent      * @throws TransportException Thrown when something bad happens.      */
specifier|public
name|void
name|sendMessage
parameter_list|(
name|eXistMessage
name|em
parameter_list|)
throws|throws
name|TransportException
block|{
comment|// Get from .xconf file, fill defaults when needed
name|parameters
operator|.
name|processParameters
argument_list|()
expr_stmt|;
name|parameters
operator|.
name|fillActiveMQbrokerDefaults
argument_list|()
expr_stmt|;
name|Properties
name|contextProps
init|=
name|parameters
operator|.
name|getInitialContextProps
argument_list|()
decl_stmt|;
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
comment|// Setup context
name|context
operator|=
operator|new
name|InitialContext
argument_list|(
name|contextProps
argument_list|)
expr_stmt|;
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
name|TransportException
argument_list|(
name|errorText
argument_list|)
throw|;
block|}
comment|// Create message
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// Set time-to-live is set
name|Long
name|timeToLive
init|=
name|parameters
operator|.
name|getTimeToLive
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeToLive
operator|!=
literal|null
condition|)
block|{
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
block|}
comment|// Set priority if set
name|Integer
name|priority
init|=
name|parameters
operator|.
name|getPriority
argument_list|()
decl_stmt|;
if|if
condition|(
name|priority
operator|!=
literal|null
condition|)
block|{
name|producer
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
comment|// Set payload when available
name|byte
index|[]
name|payload
init|=
name|em
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
comment|// check empty, collection!
block|}
comment|// Set eXist-db clustering specific details
name|message
operator|.
name|setStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_RESOURCE_OPERATION
argument_list|,
name|em
operator|.
name|getResourceOperation
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_RESOURCE_TYPE
argument_list|,
name|em
operator|.
name|getResourceType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_SOURCE_PATH
argument_list|,
name|em
operator|.
name|getResourcePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|em
operator|.
name|getDestinationPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_DESTINATION_PATH
argument_list|,
name|em
operator|.
name|getDestinationPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Set other details
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
init|=
name|em
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|item
range|:
name|metaData
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|metaData
operator|.
name|get
argument_list|(
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|message
operator|.
name|setStringProperty
argument_list|(
name|item
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
name|message
operator|.
name|setIntProperty
argument_list|(
name|item
argument_list|,
operator|(
name|Integer
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
name|message
operator|.
name|setLongProperty
argument_list|(
name|item
argument_list|,
operator|(
name|Long
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
operator|.
name|setStringProperty
argument_list|(
name|item
argument_list|,
literal|""
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Send message
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Close connection
comment|// DW: connection could be re-used?
comment|//connection.close();
name|LOG
operator|.
name|debug
argument_list|(
literal|"Message sent with id '"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|"'"
argument_list|)
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
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransportException
argument_list|(
literal|"Problem during communcation: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransportException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
comment|// I know, bad coding practice, really need it
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransportException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
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
block|}
block|}
block|}
end_class

end_unit

