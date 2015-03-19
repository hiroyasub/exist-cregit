begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
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
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|common
operator|.
name|XmlRpcHttpRequestConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|server
operator|.
name|RequestProcessorFactoryFactory
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
name|security
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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

begin_comment
comment|/**  * Factory creates a new handler for each XMLRPC request. For eXist, the handler is implemented  * by class {@link org.exist.xmlrpc.RpcConnection}. The factory is needed to make sure that each  * RpcConnection is properly initialized.  */
end_comment

begin_class
specifier|public
class|class
name|XmldbRequestProcessorFactory
implements|implements
name|RequestProcessorFactoryFactory
operator|.
name|RequestProcessorFactory
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
name|XmldbRequestProcessorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|CHECK_INTERVAL
init|=
literal|2000
decl_stmt|;
specifier|protected
name|boolean
name|useDefaultUser
init|=
literal|true
decl_stmt|;
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|int
name|connections
init|=
literal|0
decl_stmt|;
specifier|protected
name|long
name|lastCheck
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|protected
name|QueryResultCache
name|resultSets
init|=
operator|new
name|QueryResultCache
argument_list|()
decl_stmt|;
comment|/** id of the database registred against the BrokerPool */
specifier|protected
name|String
name|databaseId
init|=
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
decl_stmt|;
specifier|public
name|XmldbRequestProcessorFactory
parameter_list|(
specifier|final
name|String
name|databaseId
parameter_list|,
specifier|final
name|boolean
name|useDefaultUser
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
operator|.
name|useDefaultUser
operator|=
name|useDefaultUser
expr_stmt|;
if|if
condition|(
name|databaseId
operator|!=
literal|null
operator|&&
operator|!
name|databaseId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|databaseId
operator|=
name|databaseId
expr_stmt|;
block|}
name|brokerPool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|this
operator|.
name|databaseId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getRequestProcessor
parameter_list|(
specifier|final
name|XmlRpcRequest
name|pRequest
parameter_list|)
throws|throws
name|XmlRpcException
block|{
name|checkResultSets
argument_list|()
expr_stmt|;
specifier|final
name|XmlRpcHttpRequestConfig
name|config
init|=
operator|(
name|XmlRpcHttpRequestConfig
operator|)
name|pRequest
operator|.
name|getConfig
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|user
init|=
name|authenticate
argument_list|(
name|config
operator|.
name|getBasicUserName
argument_list|()
argument_list|,
name|config
operator|.
name|getBasicPassword
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RpcConnection
argument_list|(
name|this
argument_list|,
name|user
argument_list|)
return|;
block|}
specifier|protected
name|Subject
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|XmlRpcException
block|{
comment|// assume guest user if no user is specified
comment|// set a password for admin to permit this
if|if
condition|(
name|username
operator|==
literal|null
condition|)
block|{
name|username
operator|=
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
name|password
operator|=
name|username
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|useDefaultUser
operator|&&
name|username
operator|.
name|equalsIgnoreCase
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"The user "
operator|+
name|SecurityManager
operator|.
name|GUEST_USER
operator|+
literal|" is prohibited from logging in through XML-RPC."
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XmlRpcException
argument_list|(
literal|0
argument_list|,
name|message
argument_list|)
throw|;
block|}
comment|// check user
try|try
block|{
return|return
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XmlRpcException
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|brokerPool
return|;
block|}
specifier|protected
name|void
name|checkResultSets
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastCheck
operator|>
name|CHECK_INTERVAL
condition|)
block|{
name|resultSets
operator|.
name|checkTimestamps
argument_list|()
expr_stmt|;
name|lastCheck
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
name|BrokerPool
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"shutdown failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

