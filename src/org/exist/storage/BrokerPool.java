begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|File
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
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionConfigurationManager
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
name|PermissionDeniedException
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
name|User
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
name|ReentrantReadWriteLock
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
name|sync
operator|.
name|Sync
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
name|sync
operator|.
name|SyncDaemon
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
name|txn
operator|.
name|TransactionException
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
name|txn
operator|.
name|TransactionManager
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|XMLReaderObjectFactory
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
name|XMLReaderPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|ShutdownListener
import|;
end_import

begin_comment
comment|/**  * This class controls all available instances of the database.  * Use it to configure, start and stop database instances.   * You may have multiple instances defined, each using its own configuration.   * To define multiple instances, pass an identification string to {@link #configure(String, int, int, Configuration)}  * and use {@link #getInstance(String)} to retrieve an instance.  *  *@author  Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_comment
comment|//TODO : in the future, separate the design between the Map of DBInstances and their non static implementation
end_comment

begin_class
specifier|public
class|class
name|BrokerPool
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
name|BrokerPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|TreeMap
name|instances
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
comment|/** 	 * The id of a default database instance for those who are too lazy to provide parameters ;-).  	 */
comment|//TODO : rename as DEFAULT_DB_INSTANCE_ID ?
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_INSTANCE
init|=
literal|"exist"
decl_stmt|;
comment|//TODO : inline the class ? or... make it configurable ?
comment|// WM: inline. I don't think users need to be able to overwrite this.
comment|// They can register their own shutdown hooks any time.
specifier|private
specifier|final
specifier|static
name|Thread
name|shutdownHook
init|=
operator|new
name|Thread
argument_list|()
block|{
comment|/** 	     * Make sure that all instances are cleanly shut down. 	     */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing shutdown thread"
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|//TODO : make this defaut value configurable ? useless if we have a registerShutdownHook(Thread aThread) method (null = deregister)
specifier|private
specifier|static
name|boolean
name|registerShutdownHook
init|=
literal|true
decl_stmt|;
comment|/**      * Whether of not the JVM should run the shutdown thread. 	 * @param register<code>true</code> if the JVM should run the thread 	 */
comment|//TODO : rename as activateShutdownHook ? or registerShutdownHook(Thread aThread)
comment|// WM: it is probably not necessary to allow users to register their own hook. This method
comment|// is only used once, by class org.exist.JettyStart, which registers its own hook.
specifier|public
specifier|final
specifier|static
name|void
name|setRegisterShutdownHook
parameter_list|(
name|boolean
name|register
parameter_list|)
block|{
comment|/* 		 * TODO : call Runtime.getRuntime().removeShutdownHook or Runtime.getRuntime().registerShutdownHook  		 * depending of the value of register 		 * Since Java doesn't provide a convenient way to know if a shutdown hook has been registrered,  		 * we may have to catch IllegalArgumentException 		 */
comment|//TODO : check that the JVM is not shutting down
name|registerShutdownHook
operator|=
name|register
expr_stmt|;
block|}
comment|//TODO : make it non-static since every database instance may have its own policy.
comment|//TODO : make a defaut value that could be overwritten by the configuration
comment|// WM: this is only used by junit tests to test the recovery process.
comment|/**      * For testing only: triggers a database corruption by disabling the page caches. The effect is      * similar to a sudden power loss or the jvm being killed. The flag is used by some      * junit tests to test the recovery process.      */
specifier|public
specifier|static
name|boolean
name|FORCE_CORRUPTION
init|=
literal|false
decl_stmt|;
comment|/** 	 *  Creates and configures a default database instance and adds it to the pool.  	 *  Call this before calling {link #getInstance()}.  	 * If a default database instance already exists, the new configuration is ignored. 	 * @param minBrokers The minimum number of concurrent brokers for handling requests on the database instance. 	 * @param maxBrokers The maximum number of concurrent brokers for handling requests on the database instance. 	 * @param config The configuration object for the database instance 	 * @throws EXistException 	 *@exception  EXistException If the initialization fails.	 	 */
comment|//TODO : in the future, we should implement a Configurable interface
specifier|public
specifier|final
specifier|static
name|void
name|configure
parameter_list|(
name|int
name|minBrokers
parameter_list|,
name|int
name|maxBrokers
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|configure
argument_list|(
name|DEFAULT_INSTANCE
argument_list|,
name|minBrokers
argument_list|,
name|maxBrokers
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Creates and configures a database instance and adds it to the pool.  	 *  Call this before calling {link #getInstance()}.  	 * If a database instance with the same name already exists, the new configuration is ignored. 	 * @param id A<strong>unique</strong> name for the database instance.  	 * It is possible to have more than one database instance (with different configurations for example). 	 * @param minBrokers The minimum number of concurrent brokers for handling requests on the database instance. 	 * @param maxBrokers The maximum number of concurrent brokers for handling requests on the database instance. 	 * @param config The configuration object for the database instance 	 * @throws EXistException If the initialization fails.	 	 */
comment|//TODO : in the future, we should implement a Configurable interface
specifier|public
specifier|final
specifier|static
name|void
name|configure
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|minBrokers
parameter_list|,
name|int
name|maxBrokers
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
comment|//Check if there is a database instance in the pool with the same id
name|BrokerPool
name|instance
init|=
operator|(
name|BrokerPool
operator|)
name|instances
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"configuring database instance '"
operator|+
name|id
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
comment|//Create the instance
name|instance
operator|=
operator|new
name|BrokerPool
argument_list|(
name|id
argument_list|,
name|minBrokers
argument_list|,
name|maxBrokers
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|//Add it to the pool
name|instances
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|instance
argument_list|)
expr_stmt|;
comment|//We now have at leant an instance...
if|if
condition|(
name|instances
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|//... and a ShutdownHook may be interesting
if|if
condition|(
name|registerShutdownHook
condition|)
block|{
try|try
block|{
comment|//... currently an eXist-specific one. TODO : make it configurable ?
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|shutdownHook
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"shutdown hook registered"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"shutdown hook already registered"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//TODO : throw an exception here rather than silently ignore an *explicit* parameter ?
comment|// WM: maybe throw an exception. Users can check if a db is already configured.
block|}
else|else
name|LOG
operator|.
name|warn
argument_list|(
literal|"database instance '"
operator|+
name|id
operator|+
literal|"' is already configured"
argument_list|)
expr_stmt|;
block|}
comment|/** Returns whether or not the default database instance is configured. 	 * @return<code>true</code> if it is configured 	 */
comment|//TODO : in the future, we should implement a Configurable interface
specifier|public
specifier|final
specifier|static
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
name|isConfigured
argument_list|(
name|DEFAULT_INSTANCE
argument_list|)
return|;
block|}
comment|/** Returns whether or not a database instance is configured. 	 * @param id The name of the database instance 	 * @return<code>true</code> if it is configured 	 */
comment|//TODO : in the future, we should implement a Configurable interface
specifier|public
specifier|final
specifier|static
name|boolean
name|isConfigured
parameter_list|(
name|String
name|id
parameter_list|)
block|{
comment|//Check if there is a database instance in the pool with the same id
name|BrokerPool
name|instance
init|=
operator|(
name|BrokerPool
operator|)
name|instances
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
comment|//No : it *can't* be configured
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|//Yes : it *may* be configured
return|return
name|instance
operator|.
name|isInstanceConfigured
argument_list|()
return|;
block|}
comment|/**Returns a broker pool for the default database instance. 	 * @return The broker pool 	 * @throws EXistException If the database instance is not available (not created, stopped or not configured) 	 */
specifier|public
specifier|final
specifier|static
name|BrokerPool
name|getInstance
parameter_list|()
throws|throws
name|EXistException
block|{
return|return
name|getInstance
argument_list|(
name|DEFAULT_INSTANCE
argument_list|)
return|;
block|}
comment|/**Returns a broker pool for a database instance. 	 * @param id The name of the database instance 	 * @return The broker pool 	 * @throws EXistException If the instance is not available (not created, stopped or not configured) 	 */
specifier|public
specifier|final
specifier|static
name|BrokerPool
name|getInstance
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|EXistException
block|{
comment|//Check if there is a database instance in the pool with the same id
name|BrokerPool
name|instance
init|=
operator|(
name|BrokerPool
operator|)
name|instances
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|!=
literal|null
condition|)
comment|//TODO : call isConfigured(id) and throw an EXistException if relevant ?
return|return
name|instance
return|;
else|else
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"database instance '"
operator|+
name|id
operator|+
literal|"' is not available"
argument_list|)
throw|;
block|}
comment|/** Returns an iterator over the database instances. 	 * @return The iterator 	 */
specifier|public
specifier|final
specifier|static
name|Iterator
name|getInstances
parameter_list|()
block|{
return|return
name|instances
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** Stops the default database instance. After calling this method, it is 	 *  no longer configured. 	 * @throws EXistException If the default database instance is not available (not created, stopped or not configured)  	 */
specifier|public
specifier|final
specifier|static
name|void
name|stop
parameter_list|()
throws|throws
name|EXistException
block|{
name|stop
argument_list|(
name|DEFAULT_INSTANCE
argument_list|)
expr_stmt|;
block|}
comment|/** Stops the given database instance. After calling this method, it is 	 *  no longer configured. 	 * @param id The name of the database instance 	 * @throws EXistException If the database instance is not available (not created, stopped or not configured) 	 */
specifier|public
specifier|final
specifier|static
name|void
name|stop
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|EXistException
block|{
name|BrokerPool
name|instance
init|=
operator|(
name|BrokerPool
operator|)
name|instances
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"database instance '"
operator|+
name|id
operator|+
literal|"' is not available"
argument_list|)
throw|;
name|instance
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/** Stops all the database instances. After calling this method, the database instances are 	 *  no longer configured. 	 * @param killed<code>true</code> when invoked by an exiting JVM 	 */
specifier|public
specifier|final
specifier|static
name|void
name|stopAll
parameter_list|(
name|boolean
name|killed
parameter_list|)
block|{
comment|//Create a temporary vector
name|Vector
name|tmpInstances
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|instances
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
comment|//and feed it with the living database instances
name|tmpInstances
operator|.
name|add
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BrokerPool
name|instance
decl_stmt|;
comment|//Iterate over the living database instances
for|for
control|(
name|Iterator
name|i
init|=
name|tmpInstances
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|instance
operator|=
operator|(
name|BrokerPool
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|instance
operator|.
name|conf
operator|!=
literal|null
condition|)
comment|//Shut them down
name|instance
operator|.
name|shutdown
argument_list|(
name|killed
argument_list|)
expr_stmt|;
block|}
comment|//Clear the living instances container : they are all sentenced to death...
name|instances
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/* END OF STATIC IMPLEMENTATION */
comment|/** 	 * Default values 	 */
comment|//TODO : make them static when we have 2 classes
specifier|private
specifier|final
name|int
name|DEFAULT_MIN_BROKERS
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|int
name|DEFAULT_MAX_BROKERS
init|=
literal|15
decl_stmt|;
specifier|public
specifier|final
name|long
name|DEFAULT_SYNCH_PERIOD
init|=
literal|120000
decl_stmt|;
specifier|public
specifier|final
name|long
name|DEFAULT_MAX_SHUTDOWN_WAIT
init|=
literal|45000
decl_stmt|;
comment|//TODO : move this default setting to org.exist.collections.CollectionCache ?
specifier|public
specifier|final
name|int
name|DEFAULT_COLLECTION_BUFFER_SIZE
init|=
literal|128
decl_stmt|;
comment|/**      *<code>true</code> if the database instance is able to handle transactions.       */
specifier|private
name|boolean
name|transactionsEnabled
decl_stmt|;
comment|/** 	 * The name of the database instance 	 */
specifier|private
name|String
name|instanceId
decl_stmt|;
comment|/** 	 *<code>true</code> if the database instance is not yet initialized 	 */
comment|//TODO : let's be positive and rename it as initialized ?
specifier|private
name|boolean
name|initializing
init|=
literal|true
decl_stmt|;
comment|/** 	 * The number of brokers for the database instance  	 */
specifier|private
name|int
name|brokersCount
init|=
literal|0
decl_stmt|;
comment|/** 	 * The minimal number of brokers for the database instance  	 */
specifier|private
name|int
name|minBrokers
decl_stmt|;
comment|/** 	 * The maximal number of brokers for the database instance  	 */
specifier|private
name|int
name|maxBrokers
decl_stmt|;
comment|/** 	 * The number of inactive brokers for the database instance  	 */
specifier|private
name|Stack
name|inactiveBrokers
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
comment|/** 	 * The number of active brokers for the database instance  	 */
specifier|private
name|Map
name|activeBrokers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|/** The configuration object for the database instance      */
specifier|protected
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
comment|/** 	 *<code>true</code> if a cache synchronization event is scheduled 	 */
comment|//TODO : rename as syncScheduled ?
comment|//TODO : alternatively, delete this member and create a Sync.NOSYNC event
specifier|private
name|boolean
name|syncRequired
init|=
literal|false
decl_stmt|;
comment|/** 	 * The kind of scheduled cache synchronization event.  	 * One of {@link org.exist.storage.Sync#MINOR_SYNC} or {@link org.exist.storage.Sync#MINOR_SYNC} 	 */
specifier|private
name|int
name|syncEvent
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|checkpoint
init|=
literal|false
decl_stmt|;
comment|/**      *<code>true</code> if the database instance is running in read-only mode.      */
comment|//TODO : this should be computed by the DBrokers depending of their configuration/capabilities
comment|//TODO : for now, this member is used for recovery management
specifier|private
name|boolean
name|isReadOnly
decl_stmt|;
comment|/**      * The transaction manager of the database instance.      */
specifier|private
name|TransactionManager
name|transactionManager
init|=
literal|null
decl_stmt|;
comment|/** 	 * Delay (in ms) for running jobs to return when the database instance shuts down. 	 */
specifier|private
name|long
name|maxShutdownWait
decl_stmt|;
comment|/** 	 * The daemon which periodically triggers system tasks and cache synchronization on the database instance. 	 */
specifier|private
name|SyncDaemon
name|syncDaemon
decl_stmt|;
specifier|private
name|Sync
name|sync
decl_stmt|;
comment|/** 	 * The listener that is notified when the database instance shuts down. 	 */
specifier|private
name|ShutdownListener
name|shutdownListener
init|=
literal|null
decl_stmt|;
comment|/**      * The security manager of the database instance.       */
specifier|private
name|SecurityManager
name|securityManager
init|=
literal|null
decl_stmt|;
comment|/** 	 * The system maintenance tasks of the database instance. 	 */
comment|//TODO : maybe not the most appropriate container...
comment|// WM: yes, only used in initialization. Don't need a synchronized collection here
specifier|private
name|List
name|systemTasks
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|//TODO : remove when SystemTask has a getPeriodicity() method
specifier|private
name|Vector
name|systemTasksPeriods
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
comment|/** 	 * The pending system maintenance tasks of the database instance. 	 */
specifier|private
name|Stack
name|waitingSystemTasks
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
comment|/** 	 * The cache in which the database instance may store items. 	 */
specifier|private
name|CacheManager
name|cacheManager
decl_stmt|;
comment|/** 	 * The pool in which the database instance's<strong>compiled</strong> XQueries are stored. 	 */
specifier|private
name|XQueryPool
name|xQueryPool
decl_stmt|;
comment|/** 	 * The monitor in which the database instance's strong>running</strong> XQueries are managed. 	 */
specifier|private
name|XQueryMonitor
name|xQueryMonitor
decl_stmt|;
comment|/**      * The global manager for accessing collection configuration files from the database instance.      */
specifier|private
name|CollectionConfigurationManager
name|collectionConfigurationManager
init|=
literal|null
decl_stmt|;
comment|/**      * The cache in which the database instance's collections are stored.      */
specifier|protected
name|CollectionCache
name|collectionCache
decl_stmt|;
comment|/** 	 * The pool in which the database instance's readers are stored. 	 */
specifier|protected
name|XMLReaderPool
name|xmlReaderPool
decl_stmt|;
comment|//TODO : is another value possible ? If no, make it static
comment|// WM: no, we need one lock per database instance. Otherwise we would lock another database.
specifier|private
name|Lock
name|globalXUpdateLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|"xupdate"
argument_list|)
decl_stmt|;
comment|/** Creates and configures the database instance.  	 * @param instanceId A name for the database instance. 	 * @param minBrokers The minimum number of concurrent brokers for handling requests on the database instance. 	 * @param maxBrokers The maximum number of concurrent brokers for handling requests on the database instance. 	 * @param conf The configuration object for the database instance 	 * @throws EXistException If the initialization fails.     */
comment|//TODO : shouldn't this constructor be private ? as such it *must* remain under configure() control !
comment|//TODO : Then write a configure(int minBrokers, int maxBrokers, Configuration conf) method
comment|// WM: yes, could be private.
specifier|public
name|BrokerPool
parameter_list|(
name|String
name|instanceId
parameter_list|,
name|int
name|minBrokers
parameter_list|,
name|int
name|maxBrokers
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|EXistException
block|{
name|Integer
name|anInteger
decl_stmt|;
name|Long
name|aLong
decl_stmt|;
name|Boolean
name|aBoolean
decl_stmt|;
comment|//TODO : ensure that the instance name is unique ?
comment|//WM: needs to be done in the configure method.
name|this
operator|.
name|instanceId
operator|=
name|instanceId
expr_stmt|;
comment|//TODO : find a nice way to (re)set the default values
comment|//TODO : create static final members for configuration keys
name|this
operator|.
name|minBrokers
operator|=
name|DEFAULT_MIN_BROKERS
expr_stmt|;
name|this
operator|.
name|maxBrokers
operator|=
name|DEFAULT_MAX_BROKERS
expr_stmt|;
comment|//TODO : make a member of it ? or, better, use a SystemTask (see below)
name|long
name|syncPeriod
init|=
name|DEFAULT_SYNCH_PERIOD
decl_stmt|;
name|this
operator|.
name|maxShutdownWait
operator|=
name|DEFAULT_MAX_SHUTDOWN_WAIT
expr_stmt|;
comment|//TODO : read from configuration
name|this
operator|.
name|transactionsEnabled
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|minBrokers
operator|=
name|minBrokers
expr_stmt|;
name|this
operator|.
name|maxBrokers
operator|=
name|maxBrokers
expr_stmt|;
comment|/* 		 * strange enough, the settings provided by the constructor may be overriden 		 * by the ones *explicitely* provided by the constructor 		 * TODO : consider a private constructor BrokerPool(String instanceId) then configure(int minBrokers, int maxBrokers, Configuration config) 		 */
name|anInteger
operator|=
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.min"
argument_list|)
expr_stmt|;
if|if
condition|(
name|anInteger
operator|!=
literal|null
condition|)
name|this
operator|.
name|minBrokers
operator|=
name|anInteger
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|anInteger
operator|=
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.max"
argument_list|)
expr_stmt|;
if|if
condition|(
name|anInteger
operator|!=
literal|null
condition|)
name|this
operator|.
name|maxBrokers
operator|=
name|anInteger
operator|.
name|intValue
argument_list|()
expr_stmt|;
comment|//TODO : sanity check : minBrokers shall be lesser than or equal to maxBrokers
comment|//TODO : sanity check : minBrokers shall be positive
name|LOG
operator|.
name|info
argument_list|(
literal|"database instance '"
operator|+
name|instanceId
operator|+
literal|"' will have between "
operator|+
name|this
operator|.
name|minBrokers
operator|+
literal|" and "
operator|+
name|this
operator|.
name|maxBrokers
operator|+
literal|" brokers"
argument_list|)
expr_stmt|;
comment|//TODO : use the periodicity of a SystemTask (see below)
name|aLong
operator|=
operator|(
name|Long
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.sync-period"
argument_list|)
expr_stmt|;
if|if
condition|(
name|aLong
operator|!=
literal|null
condition|)
comment|/*this.*/
name|syncPeriod
operator|=
name|aLong
operator|.
name|longValue
argument_list|()
expr_stmt|;
comment|//TODO : sanity check : the synch period should be reasonible
name|LOG
operator|.
name|info
argument_list|(
literal|"database instance '"
operator|+
name|instanceId
operator|+
literal|"' will be synchronized every "
operator|+
comment|/*this.*/
name|syncPeriod
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
comment|//TODO : move this to initialize ?
name|syncDaemon
operator|=
operator|new
name|SyncDaemon
argument_list|()
expr_stmt|;
name|aLong
operator|=
operator|(
name|Long
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.shutdown-wait"
argument_list|)
expr_stmt|;
if|if
condition|(
name|aLong
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|maxShutdownWait
operator|=
name|aLong
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
comment|//TODO : sanity check : the shutdown period should be reasonible
name|LOG
operator|.
name|info
argument_list|(
literal|"database instance '"
operator|+
name|instanceId
operator|+
literal|"' will wait  "
operator|+
name|this
operator|.
name|maxShutdownWait
operator|+
literal|" ms during shutdown"
argument_list|)
expr_stmt|;
name|aBoolean
operator|=
operator|(
name|Boolean
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.recovery.enabled"
argument_list|)
expr_stmt|;
if|if
condition|(
name|aBoolean
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|transactionsEnabled
operator|=
name|aBoolean
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"database instance '"
operator|+
name|instanceId
operator|+
literal|"' is enabled for transactions : "
operator|+
name|this
operator|.
name|transactionsEnabled
argument_list|)
expr_stmt|;
comment|//How ugly : needs refactoring...
name|Configuration
operator|.
name|SystemTaskConfig
name|systemTasksConfigs
index|[]
init|=
operator|(
name|Configuration
operator|.
name|SystemTaskConfig
index|[]
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.system-task-config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|systemTasksConfigs
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
name|systemTasksConfigs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
argument_list|)
decl_stmt|;
name|SystemTask
name|task
init|=
operator|(
name|SystemTask
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|task
operator|instanceof
name|SystemTask
operator|)
condition|)
comment|//TODO : shall we ignore the exception ?
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"'"
operator|+
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' is not an instance of org.exist.storage.SystemTask"
argument_list|)
throw|;
name|task
operator|.
name|configure
argument_list|(
name|conf
argument_list|,
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
name|systemTasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
comment|//TODO : remove when SystemTask has a getPeriodicity() method
name|systemTasksPeriods
operator|.
name|add
argument_list|(
operator|new
name|Long
argument_list|(
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getPeriod
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"added system task instance '"
operator|+
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' to be executed every "
operator|+
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getPeriod
argument_list|()
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|//TODO : shall we ignore the exception ?
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"system task class '"
operator|+
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
operator|+
literal|"' not found"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
comment|//TODO : shall we ignore the exception ?
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"system task '"
operator|+
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
operator|+
literal|"' can not be instantiated"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
comment|//TODO : shall we ignore the exception ?
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"system task '"
operator|+
name|systemTasksConfigs
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
operator|+
literal|"' can not be accessed"
argument_list|)
throw|;
block|}
block|}
comment|//TODO : why not add a default Sync task here if there is no instanceof Sync in systemTasks ?
block|}
comment|//TODO : since we need one :-( (see above)
name|this
operator|.
name|isReadOnly
operator|=
operator|!
name|canReadDataDir
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//Configuration is valid, save it
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|//TODO : in the future, we should implement an Initializable interface
name|initialize
argument_list|()
expr_stmt|;
comment|//TODO : move this to initialize ?
if|if
condition|(
name|syncPeriod
operator|>
literal|0
condition|)
block|{
comment|//TODO : why not automatically register Sync in system tasks ?
name|sync
operator|=
operator|new
name|Sync
argument_list|(
name|this
argument_list|,
name|syncPeriod
argument_list|)
expr_stmt|;
name|syncDaemon
operator|.
name|executePeriodically
argument_list|(
literal|2500
argument_list|,
name|sync
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO : create a canReadJournalDir() method in the *relevant* class. The two directories may be different.
specifier|protected
name|boolean
name|canReadDataDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|EXistException
block|{
name|String
name|dataDir
init|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.data-dir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataDir
operator|==
literal|null
condition|)
name|dataDir
operator|=
literal|"data"
expr_stmt|;
comment|//TODO : DEFAULT_DATA_DIR
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
comment|//TODO : shall we force the creation ? use a parameter to decide ?
name|LOG
operator|.
name|info
argument_list|(
literal|"Data directory '"
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"' does not exist. Creating one ..."
argument_list|)
expr_stmt|;
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot create data directory '"
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'. Switching to read-only mode."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|//Save it for further use.
comment|//TODO : "data-dir" has sense for *native* brokers
name|conf
operator|.
name|setProperty
argument_list|(
literal|"db-connection.data-dir"
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|canWrite
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot write to data directory: "
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|". Switching to read-only mode."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**Initializes the database instance. 	 * @throws EXistException 	 */
specifier|protected
name|void
name|initialize
parameter_list|()
throws|throws
name|EXistException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"initializing database instance '"
operator|+
name|instanceId
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
comment|//Flag to indicate that we are initializing
name|initializing
operator|=
literal|true
expr_stmt|;
comment|//REFACTOR : construct then configure
name|cacheManager
operator|=
operator|new
name|CacheManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//REFACTOR : construct then configure
name|xQueryPool
operator|=
operator|new
name|XQueryPool
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//REFACTOR : construct then... configure
name|xQueryMonitor
operator|=
operator|new
name|XQueryMonitor
argument_list|()
expr_stmt|;
comment|//REFACTOR : construct then... configure
name|xmlReaderPool
operator|=
operator|new
name|XMLReaderPool
argument_list|(
operator|new
name|XMLReaderObjectFactory
argument_list|(
name|this
argument_list|)
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//REFACTOR : construct then... configure
name|collectionCache
operator|=
operator|new
name|CollectionCache
argument_list|(
name|this
argument_list|,
name|DEFAULT_COLLECTION_BUFFER_SIZE
argument_list|,
literal|0.9
argument_list|)
expr_stmt|;
comment|//REFACTOR : construct then... configure
comment|//TODO : journal directory *may* be different from "db-connection.data-dir"
name|transactionManager
operator|=
operator|new
name|TransactionManager
argument_list|(
name|this
argument_list|,
operator|new
name|File
argument_list|(
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.data-dir"
argument_list|)
argument_list|)
argument_list|,
name|isTransactional
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO : replace the following code by get()/release() statements ?
comment|// WM: I would rather tend to keep this broker reserved as a system broker.
comment|// create a first broker to initialize the security manager
name|createBroker
argument_list|()
expr_stmt|;
comment|//TODO : this broker is *not* marked as active and *might* be reused by another process ! Is it intended ?
comment|// at this stage, the database is still single-threaded, so reusing the broker later is not a problem.
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|inactiveBrokers
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|// run recovery
name|boolean
name|recovered
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|isTransactional
argument_list|()
condition|)
block|{
name|recovered
operator|=
name|transactionManager
operator|.
name|runRecovery
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|recovered
condition|)
block|{
name|Txn
name|txn
init|=
name|transactionManager
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
comment|//TODO : use a root collection final member
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
literal|"/db"
argument_list|)
expr_stmt|;
name|transactionManager
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|transactionManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//TODO : from there, rethink the sequence of calls.
comment|// WM: attention: a small change in the sequence of calls can break
comment|// either normal startup or recovery.
comment|// remove old temporary docs
name|broker
operator|.
name|cleanUpAll
argument_list|()
expr_stmt|;
comment|//create the security manager
comment|//TODO : why only the first broker has a security manager ? Global or attached to each broker ?
comment|// WM: there's only one security manager per BrokerPool, but it needs a DBBroker instance to read
comment|// the /db/system collection.
name|securityManager
operator|=
operator|new
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
argument_list|(
name|this
argument_list|,
name|broker
argument_list|)
expr_stmt|;
name|initializing
operator|=
literal|false
expr_stmt|;
comment|//TODO : other brokers don't have one. Don't know if they need one though...
comment|// WM: there's only one CollectionConfigurationManager per BrokerPool. The passed DBBroker
comment|// is needed to initialize/read the /db/system/config collection.
name|collectionConfigurationManager
operator|=
operator|new
name|CollectionConfigurationManager
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|recovered
condition|)
block|{
try|try
block|{
name|broker
operator|.
name|setUser
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|broker
operator|.
name|repair
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error during recovery: "
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
comment|//Create the minimal number of brokers required by the configuration
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|minBrokers
condition|;
name|i
operator|++
control|)
name|createBroker
argument_list|()
expr_stmt|;
comment|//Schedule the system tasks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|systemTasks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//TODO : remove first argument when SystemTask has a getPeriodicity() method
name|initSystemTask
argument_list|(
operator|(
name|Long
operator|)
name|systemTasksPeriods
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|,
operator|(
name|SystemTask
operator|)
name|systemTasks
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"database instance '"
operator|+
name|instanceId
operator|+
literal|"' initialized"
argument_list|)
expr_stmt|;
block|}
comment|//TODO : remove the period argument when SystemTask has a getPeriodicity() method
comment|//TODO : make it protected ?
specifier|private
name|void
name|initSystemTask
parameter_list|(
name|Long
name|period
parameter_list|,
name|SystemTask
name|task
parameter_list|)
throws|throws
name|EXistException
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduling system maintenance task "
operator|+
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" every "
operator|+
name|period
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|syncDaemon
operator|.
name|executePeriodically
argument_list|(
name|period
operator|.
name|longValue
argument_list|()
argument_list|,
operator|new
name|SystemTaskRunnable
argument_list|(
name|this
argument_list|,
name|task
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
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
name|EXistException
argument_list|(
literal|"Failed to initialize system maintenance task: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Whether or not the database instance is being initialized.  	 *  	 * @return<code>true</code> is the database instance is being initialized 	 */
comment|//	TODO : let's be positive and rename it as isInitialized ?
specifier|protected
name|boolean
name|isInitializing
parameter_list|()
block|{
return|return
name|initializing
return|;
block|}
comment|/** Returns the database instance's id.      * @return The id      */
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|instanceId
return|;
block|}
comment|/** 	 *  Returns the number of brokers currently serving requests for the database instance.  	 * 	 *@return The brokers count 	 */
comment|//TODO : rename as getActiveBrokers ?
specifier|public
name|int
name|active
parameter_list|()
block|{
return|return
name|activeBrokers
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 * Returns the number of inactive brokers for the database instance. 	 *@return The brokers count 	 */
comment|//TODO : rename as getInactiveBrokers ?
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
name|inactiveBrokers
operator|.
name|size
argument_list|()
return|;
block|}
comment|//TODO : getMin() method ?
comment|/** 	 *  Returns the maximal number of brokers for the database instance. 	 * 	 *@return The brokers count 	 */
comment|//TODO : rename as getMaxBrokers ?
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|maxBrokers
return|;
block|}
comment|/** 	 * Returns whether the database instance has been configured. 	 * 	 *@return<code>true</code> if the datbase instance is configured 	 */
specifier|public
specifier|final
name|boolean
name|isInstanceConfigured
parameter_list|()
block|{
return|return
name|conf
operator|!=
literal|null
return|;
block|}
comment|/** 	 * Returns the configuration object for the database instance. 	 *@return The configuration 	 */
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|//TODO : rename as setShutdwonListener ?
specifier|public
name|void
name|registerShutdownListener
parameter_list|(
name|ShutdownListener
name|listener
parameter_list|)
block|{
comment|//TODO : check that we are not shutting down
name|shutdownListener
operator|=
name|listener
expr_stmt|;
block|}
comment|/**      *  Returns the database instance's security manager      *      *@return    The security manager      */
specifier|public
name|SecurityManager
name|getSecurityManager
parameter_list|()
block|{
return|return
name|securityManager
return|;
block|}
comment|/** Returns the daemon which periodically executes system tasks, including cache synchronization, on the database instance.      * @return The daemon      */
specifier|public
name|SyncDaemon
name|getSyncDaemon
parameter_list|()
block|{
return|return
name|syncDaemon
return|;
block|}
comment|/**      * Returns whether transactions can be handled by the database instance.      *       * @return<code>true</code> if transactions can be handled      */
specifier|public
name|boolean
name|isTransactional
parameter_list|()
block|{
comment|//TODO : confusion between dataDir and a so-called "journalDir" !
return|return
operator|!
name|isReadOnly
operator|&&
name|transactionsEnabled
return|;
block|}
specifier|public
name|TransactionManager
name|getTransactionManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|transactionManager
return|;
block|}
comment|/**       * Returns a manager for accessing the database instance's collection configuration files.      * @return The manager      */
specifier|public
name|CollectionConfigurationManager
name|getConfigurationManager
parameter_list|()
block|{
return|return
name|collectionConfigurationManager
return|;
block|}
comment|/**      * Returns a cache in which the database instance's collections are stored.      *       * @return The cache 	 */
comment|//TODO : rename as getCollectionCache ?
specifier|public
name|CollectionCache
name|getCollectionsCache
parameter_list|()
block|{
return|return
name|collectionCache
return|;
block|}
comment|/**      * Returns a cache in which the database instance's may store items.      *       * @return The cache 	 */
specifier|public
name|CacheManager
name|getCacheManager
parameter_list|()
block|{
return|return
name|cacheManager
return|;
block|}
comment|/**      * Returns a pool in which the database instance's<strong>compiled</strong> XQueries are stored.      *       * @return The pool      */
specifier|public
name|XQueryPool
name|getXQueryPool
parameter_list|()
block|{
return|return
name|xQueryPool
return|;
block|}
comment|/**      * Returns a monitor in which the database instance's<strong>running</strong> XQueries are managed.      *       * @return The monitor      */
specifier|public
name|XQueryMonitor
name|getXQueryMonitor
parameter_list|()
block|{
return|return
name|xQueryMonitor
return|;
block|}
comment|/**      * Returns a pool in which the database instance's readers are stored.      *       * @return The pool 	 */
specifier|public
name|XMLReaderPool
name|getParserPool
parameter_list|()
block|{
return|return
name|xmlReaderPool
return|;
block|}
comment|/**      * Returns the global update lock for the database instance.      * This lock is used by XUpdate operations to avoid that      * concurrent XUpdate requests modify the database until all      * document locks have been correctly set.      *        * @return The global lock      */
comment|//TODO : rename as getUpdateLock ?
specifier|public
name|Lock
name|getGlobalUpdateLock
parameter_list|()
block|{
return|return
name|globalXUpdateLock
return|;
block|}
comment|/** Creates an inactive broker for the database instance.      * @return The broker      * @throws EXistException      */
specifier|protected
name|DBBroker
name|createBroker
parameter_list|()
throws|throws
name|EXistException
block|{
comment|//TODO : in the future, don't pass the whole configuration, just the part relevant to brokers
name|DBBroker
name|broker
init|=
name|BrokerFactory
operator|.
name|getInstance
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|inactiveBrokers
operator|.
name|push
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|brokersCount
operator|++
expr_stmt|;
name|broker
operator|.
name|setId
argument_list|(
name|broker
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'_'
operator|+
name|brokersCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"created broker '"
operator|+
name|broker
operator|.
name|getId
argument_list|()
operator|+
literal|" for database instance '"
operator|+
name|instanceId
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|/** Returns an active broker for the database instance. 	 * @return The broker 	 * @throws EXistException If the instance is not available (stopped or not configured) 	 */
comment|//TODO : rename as getBroker ? getInstance (when refactored) ?
specifier|public
name|DBBroker
name|get
parameter_list|()
throws|throws
name|EXistException
block|{
if|if
condition|(
operator|!
name|isInstanceConfigured
argument_list|()
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"database instance '"
operator|+
name|instanceId
operator|+
literal|"' is not available"
argument_list|)
throw|;
comment|//Try to get an active broker
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|activeBrokers
operator|.
name|get
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
decl_stmt|;
comment|//Use it...
comment|//TOUNDERSTAND (pb) : why not pop a broker from the inactive ones rather than maintaining reference counters ?
comment|// WM: a thread may call this more than once in the sequence of operations, i.e. calls to get/release can
comment|// be nested. Returning a new broker every time would lead to a deadlock condition if two threads have
comment|// to wait for a broker to become available. We thus use reference counts and return
comment|// the same broker instance for each thread.
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
comment|//increase its number of uses
name|broker
operator|.
name|incReferenceCount
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
comment|//TODO : share the code with what is below (including notifyAll) ?
comment|// WM: notifyAll is not necessary if we don't have to wait for a broker.
block|}
comment|//No active broker : get one ASAP
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//Are there any available brokers ?
if|if
condition|(
name|inactiveBrokers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//There are no available brokers. If allowed...
if|if
condition|(
name|brokersCount
operator|<
name|maxBrokers
condition|)
comment|//... create one
name|createBroker
argument_list|()
expr_stmt|;
else|else
comment|//... or wait until there is one available
while|while
condition|(
name|inactiveBrokers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"waiting for a broker to become available"
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
name|broker
operator|=
operator|(
name|DBBroker
operator|)
name|inactiveBrokers
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|//activate the broker
name|activeBrokers
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|incReferenceCount
argument_list|()
expr_stmt|;
comment|//Inform the other threads that we have a new-comer
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
block|}
comment|/** 	 * Returns an active broker for the database instance and sets its current user. 	 *   	 * @param user The user 	 * @return The broker 	 * @throws EXistException 	 */
comment|//TODO : rename as getBroker ? getInstance (when refactored) ?
specifier|public
name|DBBroker
name|get
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|EXistException
block|{
name|DBBroker
name|broker
init|=
name|get
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|/** 	 * Releases a broker for the database instance. If it is no more used, make if invactive. 	 * If there are pending system maintenance tasks, 	 * the method will block until these tasks have finished.  	 *  	 *@param  broker  The broker to be released 	 */
comment|//TODO : rename as releaseBroker ? releaseInstance (when refactored) ?
specifier|public
name|void
name|release
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
comment|//TODO : Is this test accurate ?
comment|// might be null as release() is often called within a finally block
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
return|return;
comment|//TOUNDERSTAND (pb) : why maintain reference counters rather than pushing the brokers to the stack ?
comment|//TODO : first check that the broker is active ! If not, return immediately.
name|broker
operator|.
name|decReferenceCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getReferenceCount
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//it is still in use and thus can't be marked as inactive
return|return;
block|}
comment|//Broker is no more used : inactivate it
synchronized|synchronized
init|(
name|this
init|)
block|{
name|activeBrokers
operator|.
name|remove
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
name|inactiveBrokers
operator|.
name|push
argument_list|(
name|broker
argument_list|)
expr_stmt|;
comment|//If the database is now idle, do some useful stuff
if|if
condition|(
name|activeBrokers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//TODO : use a "clean" dedicated method (we have some below) ?
if|if
condition|(
name|syncRequired
condition|)
block|{
comment|//Note that the broker is not yet really inactive ;-)
name|sync
argument_list|(
name|broker
argument_list|,
name|syncEvent
argument_list|)
expr_stmt|;
name|this
operator|.
name|syncRequired
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|checkpoint
operator|=
literal|false
expr_stmt|;
block|}
name|processWaitingTasks
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|//Inform the other threads that someone is gone
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Reloads the security manager of the database instance. This method is called for example when the 	 *<code>users.xml</code> file has been changed. 	 *  	 * @param A broker responsible for executing the job 	 */
comment|//TOUNDERSTAND (pb) : why do we need a broker here ? Why not get and release one when we're done?
comment|// WM: this is called from the Collection.store() methods to signal that /db/system/users.xml has changed.
comment|// A broker is already available in these methods, so we use it here.
specifier|public
name|void
name|reloadSecurityManager
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|securityManager
operator|=
operator|new
name|SecurityManager
argument_list|(
name|this
argument_list|,
name|broker
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Security manager reloaded"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Executes a waiting cache synchronization for the database instance. 	 * @param broker A broker responsible for executing the job  	 * @param syncEvent One of {@link org.exist.storage.Sync#MINOR_SYNC} or {@link org.exist.storage.Sync#MINOR_SYNC} 	 */
comment|//TODO : rename as runSync ? executeSync ?
comment|//TOUNDERSTAND (pb) : *not* synchronized, so... "executes" or, rather, "schedules" ? "executes" (WM)
comment|//TOUNDERSTAND (pb) : why do we need a broker here ? Why not get and release one when we're done ?
comment|// WM: the method will always be under control of the BrokerPool. It is guaranteed that no
comment|// other brokers are active when it is called. That's why we don't need to synchronize here.
comment|//TODO : make it protected ?
specifier|private
name|void
name|sync
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|int
name|syncEvent
parameter_list|)
block|{
name|broker
operator|.
name|sync
argument_list|(
name|syncEvent
argument_list|)
expr_stmt|;
comment|//TODO : strange that it is set *after* the sunc method has been called.
name|broker
operator|.
name|setUser
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|broker
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
if|if
condition|(
name|syncEvent
operator|==
name|Sync
operator|.
name|MAJOR_SYNC
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|FORCE_CORRUPTION
condition|)
name|transactionManager
operator|.
name|checkpoint
argument_list|(
name|checkpoint
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|cacheManager
operator|.
name|checkCaches
argument_list|()
expr_stmt|;
name|sync
operator|.
name|restart
argument_list|()
expr_stmt|;
block|}
else|else
name|cacheManager
operator|.
name|checkDistribution
argument_list|()
expr_stmt|;
comment|//TODO : touch this.syncEvent and syncRequired ?
block|}
comment|/** 	 * Schedules a cache synchronization for the database instance. If the database instance is idle, 	 * the cache synchronization will be run immediately. Otherwise, the task will be deffered  	 * until all running threads have returned. 	 * @param syncEvent One of {@link org.exist.storage.Sync#MINOR_SYNC} or {@link org.exist.storage.Sync#MINOR_SYNC}    	 */
specifier|public
name|void
name|triggerSync
parameter_list|(
name|int
name|syncEvent
parameter_list|)
block|{
comment|//TOUNDERSTAND (pb) : synchronized, so... "schedules" or, rather, "executes" ? "schedules" (WM)
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//Are there available brokers ?
comment|// TOUNDERSTAND (pb) : the trigger is ignored !
comment|// WM: yes, it seems wrong!!
comment|//			if(inactiveBrokers.size() == 0)
comment|//				return;
comment|//TODO : switch on syncEvent and throw an exception if it is inaccurate ?
comment|//Is the database instance idle ?
if|if
condition|(
name|inactiveBrokers
operator|.
name|size
argument_list|()
operator|==
name|brokersCount
condition|)
block|{
comment|//Borrow a broker
comment|//TODO : this broker is *not* marked as active and may be reused by another process !
comment|// No other brokers are running at this time, so there's no risk.
comment|//TODO : use get() then release the broker ?
comment|// No, might lead to a deadlock.
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|inactiveBrokers
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|//Do the synchonization job
name|sync
argument_list|(
name|broker
argument_list|,
name|syncEvent
argument_list|)
expr_stmt|;
name|syncRequired
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|//Put the synchonization job into the queue
comment|//TODO : check that we don't replace high priority Sync.MAJOR_SYNC by a lesser priority sync !
name|this
operator|.
name|syncEvent
operator|=
name|syncEvent
expr_stmt|;
name|syncRequired
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Executes a system maintenance task for the database instance. The database will be stopped      * during its execution (TODO : how ?).      * @param broker A broker responsible for executing the task       * @param task The task      */
comment|//TODO : rename as executeSystemTask ?
comment|//TOUNDERSTAND (pb) : *not* synchronized, so... "executes" or, rather, "schedules" ?
comment|// WM: no other brokers will be running when this method is called, so there's no need to synchronize.
comment|//TOUNDERSTAND (pb) : why do we need a broker here ? Why not get and release one when we're done ?
comment|// WM: get/release may lead to deadlock!
comment|//TODO : make it protected ?
specifier|private
name|void
name|runSystemTask
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|SystemTask
name|task
parameter_list|)
block|{
try|try
block|{
comment|//Flush everything
comment|//TOUNDERSTAND (pb) : are we sure that this sync will be executed (see comments above) ?
comment|// WM: tried to fix it
name|sync
argument_list|(
name|broker
argument_list|,
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running system maintenance task: "
operator|+
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|task
operator|.
name|execute
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"System maintenance task reported error: "
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
comment|/** 	 * Schedules a system maintenance task for the database instance. If the database is idle, 	 * the task will be run immediately. Otherwise, the task will be deffered  	 * until all running threads have returned.         * @param task The task      */
comment|//TOUNDERSTAND (pb) : synchronized, so... "schedules" or, rather, "executes" ?
specifier|public
name|void
name|triggerSystemTask
parameter_list|(
name|SystemTask
name|task
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//Are there available brokers ?
comment|// TOUNDERSTAND (pb) : the trigger is ignored !
comment|// WM: yes, commented out
comment|//    		if(inactiveBrokers.size() == 0)
comment|//    			return;
comment|//TODO : check task and throw an exception if inaccurate
comment|//Is the database instance idle ?
if|if
condition|(
name|inactiveBrokers
operator|.
name|size
argument_list|()
operator|==
name|brokersCount
condition|)
block|{
comment|//Borrow a broker
comment|//TODO : this broker is *not* marked as active and may be reused by another process !
comment|// WM: No other broker will be running at this point
comment|//TODO : use get() then release the broker ? WM: deadlock risk here!
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|inactiveBrokers
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|//Do the job
name|runSystemTask
argument_list|(
name|broker
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
else|else
comment|//Put the task into the queue
name|waitingSystemTasks
operator|.
name|push
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Executes waiting system maintenance tasks for the database instance.      * @param broker A broker responsible for executing the task      */
comment|//TOUNDERSTAND (pb) : *not* synchronized, so... "executes" or, rather, "schedules" ?
comment|//TOUNDERSTAND (pb) : why do we need a broker here ? Why not get and release one when we're done ?
comment|// WM: same as above: no other broker is active while we are calling this
comment|//TODO : make it protected ?
specifier|private
name|void
name|processWaitingTasks
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
while|while
condition|(
operator|!
name|waitingSystemTasks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SystemTask
name|task
init|=
operator|(
name|SystemTask
operator|)
name|waitingSystemTasks
operator|.
name|pop
argument_list|()
decl_stmt|;
name|runSystemTask
argument_list|(
name|broker
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Shuts downs the database instance 	 */
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
name|shutdown
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Shuts downs the database instance 	 * @param killed<code>true</code> when the JVM is (cleanly) exiting 	 */
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|(
name|boolean
name|killed
parameter_list|)
block|{
comment|//Notify all running tasks that we are shutting down
name|syncDaemon
operator|.
name|shutDown
argument_list|()
expr_stmt|;
comment|//Notify all running XQueries that we are shutting down
name|xQueryMonitor
operator|.
name|killAll
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|//TODO : close other objects using varying methods ? set them to null ?
comment|//cacheManager.something();
comment|//xQueryPool.something();
comment|//collectionConfigurationManager.something();
comment|//collectionCache.something();
comment|//xmlReaderPool.close();
name|long
name|waitStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|//Are there active brokers ?
while|while
condition|(
name|activeBrokers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
comment|//Wait until they become inactive...
name|this
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
comment|//...or force the shutdown
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|waitStart
operator|>
name|maxShutdownWait
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Not all threads returned. Forcing shutdown ..."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"calling shutdown ..."
argument_list|)
expr_stmt|;
comment|//TODO : replace the following code by get()/release() statements ?
comment|// WM: deadlock risk if not all brokers returned properly.
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|inactiveBrokers
operator|.
name|isEmpty
argument_list|()
condition|)
try|try
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not create instance for shutdown. Giving up."
argument_list|)
expr_stmt|;
block|}
else|else
comment|//TODO : this broker is *not* marked as active and may be reused by another process !
comment|//TODO : use get() then release the broker ?
comment|// WM: deadlock risk if not all brokers returned properly.
name|broker
operator|=
operator|(
name|DBBroker
operator|)
name|inactiveBrokers
operator|.
name|peek
argument_list|()
expr_stmt|;
comment|//TOUNDERSTAND (pb) : shutdown() is called on only *one* broker ?
comment|// WM: yes, the database files are shared, so only one broker is needed to close them for all
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
name|broker
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|//TODO : use dedicated method here, probably elsewhere ?
try|try
block|{
if|if
condition|(
operator|!
name|FORCE_CORRUPTION
condition|)
block|{
name|transactionManager
operator|.
name|checkpoint
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//TOUNDERSTAND (pb) : not called if FORCE_CORRUPTION is true ?
comment|// WM: yes, this will truncate the log file and thus make the tests more realistic ;-)
name|transactionManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|TransactionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//Invalidate the configuration
name|conf
operator|=
literal|null
expr_stmt|;
comment|//Clear the living instances container
name|instances
operator|.
name|remove
argument_list|(
name|instanceId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"shutdown complete !"
argument_list|)
expr_stmt|;
comment|//Last instance closes the house...
comment|//TOUNDERSTAND (pb) : !killed or, rather, killed ?
comment|// TODO: WM: check usage of killed!
if|if
condition|(
name|instances
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
operator|!
name|killed
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing shutdown hook"
argument_list|)
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|removeShutdownHook
argument_list|(
name|shutdownHook
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shutdownListener
operator|!=
literal|null
condition|)
name|shutdownListener
operator|.
name|shutdown
argument_list|(
name|instanceId
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TODO : move this elsewhere
specifier|public
name|void
name|triggerCheckpoint
parameter_list|()
block|{
if|if
condition|(
name|syncRequired
condition|)
return|return;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|syncEvent
operator|=
name|Sync
operator|.
name|MAJOR_SYNC
expr_stmt|;
name|syncRequired
operator|=
literal|true
expr_stmt|;
name|checkpoint
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** A wrapper class for executing a database instance's system task. 	 */
comment|//TODO : make it protected ?
specifier|private
specifier|static
class|class
name|SystemTaskRunnable
implements|implements
name|Runnable
block|{
name|SystemTask
name|task
decl_stmt|;
name|BrokerPool
name|pool
decl_stmt|;
comment|/** Creates a wrapper for executing a database instance's system task.          * @param pool The database instance          * @param task The system task          */
specifier|public
name|SystemTaskRunnable
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|SystemTask
name|task
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
block|}
comment|/** Runs the wrapper for executing a system task.          */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running system task '"
operator|+
name|task
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
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
block|}
block|}
end_class

end_unit

