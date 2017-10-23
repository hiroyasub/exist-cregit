begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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
name|Configuration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|fsm
operator|.
name|AtomicFSM
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|fsm
operator|.
name|FSM
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|fsm
operator|.
name|TransitionTable
operator|.
name|transitionTable
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|UnixStylePermission
operator|.
name|LOG
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
name|List
import|;
end_import

begin_comment
comment|/**  * This class simply maintains a list of {@link BrokerPoolService}  * and provides methods to {@BrokerPool} to manage the lifecycle of  * those services.  *  * This class should only be accessed from {@link BrokerPool}  * and the order of method invocation (service state change)  * is significant and must follow the order:  *  *      register -> configure -> prepare ->  *          system -> pre-multi-user -> multi-user  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
class|class
name|BrokerPoolServicesManager
block|{
specifier|private
enum|enum
name|ManagerState
block|{
name|REGISTRATION
block|,
name|CONFIGURATION
block|,
name|PREPARATION
block|,
name|SYSTEM
block|,
name|PRE_MULTI_USER
block|,
name|MULTI_USER
block|,
name|STOPPING
block|,
name|SHUTTING_DOWN
block|}
specifier|private
enum|enum
name|ManagerEvent
block|{
name|CONFIGURE
block|,
name|PREPARE
block|,
name|ENTER_SYSTEM_MODE
block|,
name|PREPARE_ENTER_MULTI_USER_MODE
block|,
name|ENTER_MULTI_USER_MODE
block|,
name|STOP
block|,
name|SHUTDOWN
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|FSM
argument_list|<
name|ManagerState
argument_list|,
name|ManagerEvent
argument_list|>
name|states
init|=
operator|new
name|AtomicFSM
argument_list|<>
argument_list|(
name|ManagerState
operator|.
name|REGISTRATION
argument_list|,
name|transitionTable
argument_list|(
name|ManagerState
operator|.
name|class
argument_list|,
name|ManagerEvent
operator|.
name|class
argument_list|)
operator|.
name|when
argument_list|(
name|ManagerState
operator|.
name|REGISTRATION
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|CONFIGURE
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|CONFIGURATION
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|PREPARE
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|PREPARATION
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|ENTER_SYSTEM_MODE
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|SYSTEM
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|PREPARE_ENTER_MULTI_USER_MODE
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|PRE_MULTI_USER
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|ENTER_MULTI_USER_MODE
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|MULTI_USER
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|STOP
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|STOPPING
argument_list|)
operator|.
name|on
argument_list|(
name|ManagerEvent
operator|.
name|SHUTDOWN
argument_list|)
operator|.
name|switchTo
argument_list|(
name|ManagerState
operator|.
name|SHUTTING_DOWN
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BrokerPoolService
argument_list|>
name|brokerPoolServices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Register a Service to be managed      *      * Note all services must be registered before any service is configured      * failure to do so will result in an {@link IllegalStateException}      *      * @param brokerPoolService The service to be managed      *      * @return The service after it has been registered      *      * @throws IllegalStateException Thrown if there is an attempt to register a service      * after any other service has been configured.      */
parameter_list|<
name|T
extends|extends
name|BrokerPoolService
parameter_list|>
name|T
name|register
parameter_list|(
specifier|final
name|T
name|brokerPoolService
parameter_list|)
block|{
specifier|final
name|ManagerState
name|currentState
init|=
name|states
operator|.
name|getCurrentState
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentState
operator|!=
name|ManagerState
operator|.
name|REGISTRATION
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Services may only be registered during the registration state. Current state is: "
operator|+
name|currentState
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
name|brokerPoolServices
operator|.
name|add
argument_list|(
name|brokerPoolService
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Registered service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
return|return
name|brokerPoolService
return|;
block|}
comment|/**      * Configures the Services      *      * Expected to be called from {@link BrokerPool#initialize()}      *      * @param configuration The database configuration (i.e. conf.xml)      *      * @throws BrokerPoolServiceException if any service causes an error during configuration      *      * @throws IllegalStateException Thrown if there is an attempt to configure a service      * after any other service has been prepared.      */
name|void
name|configureServices
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|CONFIGURE
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|BrokerPoolService
name|brokerPoolService
range|:
name|brokerPoolServices
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Configuring service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|brokerPoolService
operator|.
name|configure
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Prepare the Services for system (single user) mode      *      * Prepare is called before the BrokerPool enters      * system (single user) mode. As yet there are still      * no brokers!      *      * @throws BrokerPoolServiceException if any service causes an error during preparation      *      * @throws IllegalStateException Thrown if there is an attempt to prepare a service      * after any other service has entered start system service.      */
name|void
name|prepareServices
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|PREPARE
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|BrokerPoolService
name|brokerPoolService
range|:
name|brokerPoolServices
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Preparing service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|brokerPoolService
operator|.
name|prepare
argument_list|(
name|brokerPool
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Starts any services which should be started directly after      * the database enters system mode, but before any system mode      * operations are performed.      *      * At this point the broker pool is in system (single user) mode      * and not generally available for access, only a single      * system broker is available.      *      * @param systemBroker The System Broker which is available for      *   services to use to access the database      *      * @throws BrokerPoolServiceException if any service causes an error during starting the system mode      *      * @throws IllegalStateException Thrown if there is an attempt to start a service      * after any other service has entered the start pre-multi-user system mode.      */
name|void
name|startSystemServices
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|ENTER_SYSTEM_MODE
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|BrokerPoolService
name|brokerPoolService
range|:
name|brokerPoolServices
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Notifying service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" of start system..."
argument_list|)
expr_stmt|;
block|}
name|brokerPoolService
operator|.
name|startSystem
argument_list|(
name|systemBroker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Starts any services which should be started directly after      * the database finishes system mode operations, but before      * entering multi-user mode      *      * At this point the broker pool is still in system (single user) mode      * and not generally available for access, only a single      * system broker is available.      *      * @param systemBroker The System Broker which is available for      *   services to use to access the database      *      * @throws BrokerPoolServiceException if any service causes an error during starting the pre-multi-user mode      *      * @throws IllegalStateException Thrown if there is an attempt to start pre-multi-user system a service      * after any other service has entered multi-user.      */
name|void
name|startPreMultiUserSystemServices
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|PREPARE_ENTER_MULTI_USER_MODE
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|BrokerPoolService
name|brokerPoolService
range|:
name|brokerPoolServices
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Notifying service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" of start pre-multi-user..."
argument_list|)
expr_stmt|;
block|}
name|brokerPoolService
operator|.
name|startPreMultiUserSystem
argument_list|(
name|systemBroker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Starts any services which should be started once the database      * enters multi-user mode      *      * @param brokerPool The broker pool instance      *      * @throws BrokerPoolServiceException if any service causes an error during starting multi-user mode      *      * @throws IllegalStateException Thrown if there is an attempt to start multi-user a service      * before we have completed pre-multi-user mode      */
name|void
name|startMultiUserServices
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|ENTER_MULTI_USER_MODE
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|BrokerPoolService
name|brokerPoolService
range|:
name|brokerPoolServices
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Notifying service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" of start multi-user..."
argument_list|)
expr_stmt|;
block|}
name|brokerPoolService
operator|.
name|startMultiUser
argument_list|(
name|brokerPool
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Stops any services which were previously started.      *      * At this point the broker pool is likely back in system (single user) mode      * and not generally available for access, only a single      * system broker is available.      *      * @param systemBroker The System Broker which is available for      *   services to use to access the database      *      * @throws BrokerPoolServiceException if any service causes an error when stopping      *      * @throws IllegalStateException Thrown if there is an attempt to stop a service      * before we have completed starting multi-user mode      */
name|void
name|stopServices
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServicesManagerException
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|STOP
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|BrokerPoolServiceException
argument_list|>
name|serviceExceptions
init|=
literal|null
decl_stmt|;
comment|// we stop in the reverse order to starting up
for|for
control|(
name|int
name|i
init|=
name|brokerPoolServices
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|BrokerPoolService
name|brokerPoolService
init|=
name|brokerPoolServices
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Stopping service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|brokerPoolService
operator|.
name|stop
argument_list|(
name|systemBroker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BrokerPoolServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|serviceExceptions
operator|==
literal|null
condition|)
block|{
name|serviceExceptions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|serviceExceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|serviceExceptions
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|BrokerPoolServicesManagerException
argument_list|(
name|serviceExceptions
argument_list|)
throw|;
block|}
block|}
comment|/**      * Shutdown any services which were previously configured.      *      * @throws IllegalStateException Thrown if there is an attempt to shutdown a service      * before we have completed stopping services      */
name|void
name|shutdown
parameter_list|()
block|{
name|states
operator|.
name|process
argument_list|(
name|ManagerEvent
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
comment|// we shutdown in the reverse order to starting up
for|for
control|(
name|int
name|i
init|=
name|brokerPoolServices
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|BrokerPoolService
name|brokerPoolService
init|=
name|brokerPoolServices
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Shutting down service: "
operator|+
name|brokerPoolService
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|brokerPoolService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

