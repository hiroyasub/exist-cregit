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
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
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

begin_comment
comment|/**  * Interface for a class which provides  * services to a BrokerPool instance  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerPoolService
block|{
comment|/**      * Configure this service      *      * By default there is nothing to configure.      *      * @param configuration BrokerPool configuration      *      * @throws BrokerPoolServiceException if an error occurs when configuring the service      */
specifier|default
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|//nothing to configure
block|}
comment|/**      * Prepare this service      *      * Prepare is called before the BrokerPool enters      * system (single user) mode. As yet there are still      * no brokers      *      * @param brokerPool The BrokerPool instance that is being prepared      *      * @throws BrokerPoolServiceException if an error occurs when preparing the service      */
specifier|default
name|void
name|prepare
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|//nothing to prepare
block|}
comment|/**      * Start any part of this service that should happen during      * system (single-user) mode.      *      * As this point the database is not generally available      * and the only system broker is passed to this function      *      * @param systemBroker The system mode broker      * @param transaction The transaction for the system service      *      * @throws BrokerPoolServiceException if an error occurs when starting the system service      */
specifier|default
name|void
name|startSystem
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|// nothing to start
block|}
comment|/**      * Start any part of this service that should happen at the      * end of system (single-user) mode and directly before multi-user      * mode      *      * As this point the database is not generally available,      * {@link #startSystem(DBBroker, Txn)} has already been called      * for all services, any reindexing and recovery has completed      * but there is still only a system broker which is passed to this      * function      *      * @param systemBroker The system mode broker      * @param transaction The transaction for the pre-multi-user system service      *      * @throws BrokerPoolServiceException if an error occurs when starting the pre-multi-user system service      */
specifier|default
name|void
name|startPreMultiUserSystem
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|//nothing to start
block|}
comment|/**      * Start any part of this service that should happen at the      * start of multi-user mode      *      * As this point the database is generally available,      * {@link #startPreMultiUserSystem(DBBroker, Txn)} has already been called      * for all services. You may be competing with other services and/or      * users for database access      *      * @param brokerPool The multi-user available broker pool instance      *      * @throws BrokerPoolServiceException if an error occurs when starting the multi-user service      */
specifier|default
name|void
name|startMultiUser
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|//nothing to start
block|}
comment|/**      * Stop this service.      *      * By default there is nothing to stop      *      * As this point the database is not generally available      * and the only system broker is passed to this function      *      * @param systemBroker The system mode broker      *      * @throws BrokerPoolServiceException if an error occurs when stopping the service      */
specifier|default
name|void
name|stop
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|//nothing to actually stop
block|}
comment|/**      * Shutdown this service.      *      * By default there is nothing to shutdown      */
specifier|default
name|void
name|shutdown
parameter_list|()
block|{
comment|//nothing to actually shutdown
block|}
block|}
end_interface

end_unit

