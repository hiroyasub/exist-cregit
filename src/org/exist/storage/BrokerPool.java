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
name|Lock
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
name|ReentrantReadWriteLock
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
comment|/**  * This class controls all available instances of the database.  * Use it to configure, start and stop database instances. You may  * have multiple instances defined, each using its own configuration,  * database directory etc.. To define multiple instances, pass an  * identification string to the static method configure() and use  * getInstance(id) to retrieve an instance.  *   *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
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
specifier|private
specifier|static
name|long
name|timeOut
init|=
literal|30000L
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|registerShutdownHook
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|ShutdownThread
name|shutdownThread
init|=
operator|new
name|ShutdownThread
argument_list|()
decl_stmt|;
comment|//	size of the internal buffer for collection objects
specifier|public
specifier|static
specifier|final
name|int
name|COLLECTION_BUFFER_SIZE
init|=
literal|128
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_INSTANCE
init|=
literal|"exist"
decl_stmt|;
comment|/** 	 * Should a shutdown hook be registered with the JVM? If set to true, method 	 * {@link #configure(String, int, int, Configuration)} will register a shutdown thread 	 * which takes care to shut down the database if the application receives a kill or term 	 * signal. However, this is unnecessary if the calling application has already registered 	 * a shutdown hook. 	 *   	 * @param register 	 */
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
name|registerShutdownHook
operator|=
name|register
expr_stmt|;
block|}
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
comment|/** 	 *  Configure a new BrokerPool instance. Call this before calling getInstance(). 	 * 	 *@param  id The name to identify this database instance. You may have more 	 *	than one instance with different configurations. 	 *@param  minBrokers Minimum number of database brokers to start during initialization. 	 *@param  maxBrokers Maximum number of database brokers available to handle requests. 	 *@param  config The configuration object used by this instance. 	 *@exception  EXistException thrown if initialization fails. 	 */
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
literal|"' ..."
argument_list|)
expr_stmt|;
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
name|instances
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|instance
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|registerShutdownHook
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"registering shutdown hook"
argument_list|)
expr_stmt|;
try|try
block|{
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|shutdownThread
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
name|debug
argument_list|(
literal|"shutdown hook already registered"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
name|LOG
operator|.
name|warn
argument_list|(
literal|"instance with id "
operator|+
name|id
operator|+
literal|" already configured"
argument_list|)
expr_stmt|;
block|}
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
return|return
literal|false
return|;
return|return
name|instance
operator|.
name|isInstanceConfigured
argument_list|()
return|;
block|}
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
comment|/** 	 *  Singleton method. Get the BrokerPool for a specified database instance. 	 * 	 *@return        The instance. 	 *@exception  EXistException  thrown if the instance has not been configured. 	 */
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
return|return
name|instance
return|;
else|else
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"instance with id "
operator|+
name|id
operator|+
literal|" has not been configured yet"
argument_list|)
throw|;
block|}
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
comment|/** 	 *  Shutdown running brokers. After calling this method, the BrokerPool is 	 *  no longer configured. You have to configure it again by calling 	 *  configure(). 	 */
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
operator|!=
literal|null
condition|)
block|{
name|instance
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"instance with id "
operator|+
literal|" is not available"
argument_list|)
throw|;
block|}
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
name|instance
operator|.
name|shutdown
argument_list|(
name|killed
argument_list|)
expr_stmt|;
block|}
name|instances
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|int
name|max
init|=
literal|15
decl_stmt|;
specifier|private
name|int
name|min
init|=
literal|1
decl_stmt|;
specifier|protected
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
specifier|private
name|ArrayList
name|active
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|int
name|brokers
init|=
literal|0
decl_stmt|;
specifier|private
name|Stack
name|pool
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|threads
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|String
name|instanceId
decl_stmt|;
specifier|private
name|boolean
name|syncRequired
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|syncEvent
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|initializing
init|=
literal|true
decl_stmt|;
comment|/** 	 * The security manager for this database instance. 	 */
specifier|private
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
name|secManager
init|=
literal|null
decl_stmt|;
comment|/** 	 * SyncDaemon is a daemon thread which periodically triggers a cache sync. 	 */
specifier|private
name|SyncDaemon
name|syncDaemon
decl_stmt|;
comment|/** 	 * ShutdownListener will be notified when the database instance shuts down. 	 */
specifier|private
name|ShutdownListener
name|shutdownListener
init|=
literal|null
decl_stmt|;
comment|/** 	 * The global pool for compiled XQuery expressions. 	 */
specifier|private
name|XQueryPool
name|xqueryCache
decl_stmt|;
comment|/** 	 * The global collection cache. 	 */
specifier|protected
name|CollectionCache
name|collectionsCache
decl_stmt|;
comment|/** 	 * Global pool for SAX XMLReader instances. 	 */
specifier|protected
name|XMLReaderPool
name|xmlReaderPool
decl_stmt|;
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
comment|/** 	 *  Constructor for the BrokerPool object 	 * 	 *@exception  EXistException  Description of the Exception 	 */
specifier|public
name|BrokerPool
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
name|instanceId
operator|=
name|id
expr_stmt|;
name|min
operator|=
name|minBrokers
expr_stmt|;
name|max
operator|=
name|maxBrokers
expr_stmt|;
name|Integer
name|minInt
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.min"
argument_list|)
decl_stmt|;
name|Integer
name|maxInt
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.max"
argument_list|)
decl_stmt|;
name|Long
name|syncInt
init|=
operator|(
name|Long
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"db-connection.pool.sync-period"
argument_list|)
decl_stmt|;
if|if
condition|(
name|minInt
operator|!=
literal|null
condition|)
name|min
operator|=
name|minInt
operator|.
name|intValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxInt
operator|!=
literal|null
condition|)
name|max
operator|=
name|maxInt
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|long
name|syncPeriod
init|=
literal|120000
decl_stmt|;
if|if
condition|(
name|syncInt
operator|!=
literal|null
condition|)
name|syncPeriod
operator|=
name|syncInt
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"min = "
operator|+
name|min
operator|+
literal|"; max = "
operator|+
name|max
operator|+
literal|"; sync = "
operator|+
name|syncPeriod
argument_list|)
expr_stmt|;
name|syncDaemon
operator|=
operator|new
name|SyncDaemon
argument_list|()
expr_stmt|;
if|if
condition|(
name|syncPeriod
operator|>
literal|0
condition|)
name|syncDaemon
operator|.
name|executePeriodically
argument_list|(
literal|1000
argument_list|,
operator|new
name|Sync
argument_list|(
name|this
argument_list|,
name|syncPeriod
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|=
name|config
expr_stmt|;
name|xqueryCache
operator|=
operator|new
name|XQueryPool
argument_list|()
expr_stmt|;
name|collectionsCache
operator|=
operator|new
name|CollectionCache
argument_list|(
name|COLLECTION_BUFFER_SIZE
argument_list|)
expr_stmt|;
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
name|initialize
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  Number of active Brokers in this pool. 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|int
name|active
parameter_list|()
block|{
return|return
name|active
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 *  Number of available Brokers for the current database instance. 	 */
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
name|pool
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 * Returns the configuration object for this database 	 * instance. 	 */
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/** 	 * Returns the global collections cache. Collection objects 	 * are shared within one database instance. 	 *  	 * @return 	 */
specifier|public
name|CollectionCache
name|getCollectionsCache
parameter_list|()
block|{
return|return
name|collectionsCache
return|;
block|}
specifier|public
name|XMLReaderPool
name|getParserPool
parameter_list|()
block|{
return|return
name|xmlReaderPool
return|;
block|}
specifier|protected
name|DBBroker
name|createBroker
parameter_list|()
throws|throws
name|EXistException
block|{
name|DBBroker
name|broker
init|=
name|BrokerFactory
operator|.
name|getInstance
argument_list|(
name|this
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|//Thread.dumpStack();
name|LOG
operator|.
name|debug
argument_list|(
literal|"database "
operator|+
name|instanceId
operator|+
literal|": creating new instance of "
operator|+
name|broker
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|push
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|active
operator|.
name|add
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|brokers
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
name|brokers
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|/** 	 *  Get a DBBroker instance from the pool. 	 */
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
literal|"database instance is not available"
argument_list|)
throw|;
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|threads
operator|.
name|get
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
comment|// the thread already holds a reference to a broker object
name|broker
operator|.
name|incReferenceCount
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|pool
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|brokers
operator|<
name|max
condition|)
name|createBroker
argument_list|()
expr_stmt|;
else|else
while|while
condition|(
name|pool
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"waiting for broker instance to become available"
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
name|pool
operator|.
name|pop
argument_list|()
expr_stmt|;
name|threads
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
comment|/** 	 * Get a DBBroker instance and set its current user to user. 	 *   	 * @param user 	 * @return 	 * @throws EXistException 	 */
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
comment|/** 	 *  Returns the security manager responsible for this pool 	 * 	 *@return    The securityManager value 	 */
specifier|public
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
name|getSecurityManager
parameter_list|()
block|{
return|return
name|secManager
return|;
block|}
comment|/** 	 * Reload the security manager. This method is called whenever the 	 * users.xml file has been changed. 	 *  	 * @param broker 	 */
specifier|public
name|void
name|reloadSecurityManager
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"reloading security manager"
argument_list|)
expr_stmt|;
name|secManager
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
block|}
specifier|public
name|SyncDaemon
name|getSyncDaemon
parameter_list|()
block|{
return|return
name|syncDaemon
return|;
block|}
comment|/** 	 *  Initialize the current instance. 	 * 	 *@exception  EXistException  Description of the Exception 	 */
specifier|protected
name|void
name|initialize
parameter_list|()
throws|throws
name|EXistException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"initializing database "
operator|+
name|instanceId
argument_list|)
expr_stmt|;
name|initializing
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|min
condition|;
name|i
operator|++
control|)
name|createBroker
argument_list|()
expr_stmt|;
name|initializing
operator|=
literal|false
expr_stmt|;
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|pool
operator|.
name|peek
argument_list|()
decl_stmt|;
name|secManager
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"database engine "
operator|+
name|instanceId
operator|+
literal|" initialized."
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|isInitializing
parameter_list|()
block|{
return|return
name|initializing
return|;
block|}
comment|/** 	 *  Release a DBBroker instance into the pool. 	 *	If all active instances are in the pool (i.e. 	 * 	the database is currently not used), release 	 *  will call sync() to flush unwritten buffers  	 *  to the disk.  	 *  	 *@param  broker  Description of the Parameter 	 */
specifier|public
name|void
name|release
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
return|return;
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
comment|// broker still has references. Keep it
return|return;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|threads
operator|.
name|remove
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
name|pool
operator|.
name|push
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|syncRequired
operator|&&
name|threads
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
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
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Write buffers to disk. release() calls this 	 * method after a specified period of time 	 * to flush buffers. 	 *  	 * @param broker 	 */
specifier|public
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
block|}
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
comment|/**  Shutdown all brokers. */
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|(
name|boolean
name|killed
parameter_list|)
block|{
name|syncDaemon
operator|.
name|shutDown
argument_list|()
expr_stmt|;
while|while
condition|(
name|threads
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
try|try
block|{
name|this
operator|.
name|wait
argument_list|(
literal|2000
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"calling shutdown ..."
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|pool
operator|.
name|peek
argument_list|()
decl_stmt|;
name|broker
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"shutdown!"
argument_list|)
expr_stmt|;
name|conf
operator|=
literal|null
expr_stmt|;
name|instances
operator|.
name|remove
argument_list|(
name|instanceId
argument_list|)
expr_stmt|;
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
name|shutdownThread
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
comment|/** 		 *  Returns maximum of concurrent Brokers. 		 * 		 *@return    The max value 		 */
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|instanceId
return|;
block|}
comment|/** 	 *  Has this BrokerPool been configured? 	 * 	 *@return    The configured value 	 */
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
specifier|public
name|void
name|triggerSync
parameter_list|(
name|int
name|event
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|pool
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|pool
operator|.
name|size
argument_list|()
operator|==
name|brokers
condition|)
block|{
name|DBBroker
name|broker
init|=
operator|(
name|DBBroker
operator|)
name|pool
operator|.
name|peek
argument_list|()
decl_stmt|;
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
name|syncEvent
operator|=
name|event
expr_stmt|;
name|syncRequired
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|registerShutdownListener
parameter_list|(
name|ShutdownListener
name|listener
parameter_list|)
block|{
name|shutdownListener
operator|=
name|listener
expr_stmt|;
block|}
comment|/** 	 * Returns the global XQuery pool for this database instance. 	 *  	 * @return 	 */
specifier|public
name|XQueryPool
name|getXQueryPool
parameter_list|()
block|{
return|return
name|xqueryCache
return|;
block|}
comment|/** 	 * Returns the global update lock for this database instance. 	 * This lock is used by XUpdate operations to avoid that 	 * concurrent XUpdate requests modify the database until all 	 * document locks have been correctly set. 	 *   	 * @return 	 */
specifier|public
name|Lock
name|getGlobalUpdateLock
parameter_list|()
block|{
return|return
name|globalXUpdateLock
return|;
block|}
specifier|protected
specifier|static
class|class
name|ShutdownThread
extends|extends
name|Thread
block|{
comment|/**  Constructor for the ShutdownThread object */
specifier|public
name|ShutdownThread
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**  Main processing method for the ShutdownThread object */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"shutdown forced"
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
block|}
end_class

end_unit

