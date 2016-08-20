begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Created by aretter on 20/08/2016.  */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerPoolService
block|{
comment|/**      * Configure this service      *      * By default there is nothing to configure.      *      * @param configuration BrokerPool configuration      */
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
comment|/**      * Prepare this service      *      * Prepare is called before the BrokerPool enters      * system (single user) mode. As yet there are still      * no brokers      *      * @param brokerPool The BrokerPool instance that is being prepared      */
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
comment|/**      * Start any part of this service that should happen during      * system (single-user) mode.      *      * As this point the database is not generally available      * and the only system broker is passed to this function      */
specifier|default
name|void
name|startSystem
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|// nothing to start
block|}
comment|/**      * Start any part of this service that should happen at the      * end of system (single-user) mode and directly before multi-user      * mode      *      * As this point the database is not generally available,      * {@link #startSystem(DBBroker)} has already been called      * for all services, any reindexing and recovery has completed      * but there is still only a system broker which is passed to this      * function      */
specifier|default
name|void
name|startTrailingSystem
parameter_list|(
specifier|final
name|DBBroker
name|systemBroker
parameter_list|)
throws|throws
name|BrokerPoolServiceException
block|{
comment|//nothing to start
block|}
comment|/**      * Stop this service      *      * By default there is nothing to stop      *      * @param brokerPool The BrokerPool instance that is stopping      */
specifier|default
name|void
name|stop
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
block|{
comment|//nothing to actually stop
block|}
block|}
end_interface

end_unit

