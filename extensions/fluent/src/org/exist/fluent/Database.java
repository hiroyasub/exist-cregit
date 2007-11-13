begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
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
name|*
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
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|*
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
name|security
operator|.
name|xacml
operator|.
name|AccessContext
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
name|*
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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  *<p>The global entry point to an embedded instance of the<a href='http://exist-db.org'>eXist</a>database.  * The static methods on this class control the lifecycle of the database connection.  It follows that  * there can be only one embedded database running in the JVM (or rather one per classloader, but  * that would probably be a bit confusing).  To gain access to the contents of the database, you  * need to acquire a handle instance by logging in.  All operations performed based on that instance  * will be executed using the permissions of the user associated with that instance.  You can have  * any number of instances (including multiple ones for the same user), but cannot mix resources  * obtained from different instances.  There is no need to explicitly release instances.</p>  *   *<p>Here's a short example of how to start up the database, perform a query, and shut down:  *<pre> Database.startup(new File("conf.xml"));  * Database db = Database.login("admin", null);  * for (String name : db.getFolder("/").query().all("//user/@name").values())  *   System.out.println("user: " + name);  * Database.shutdown();</pre></p>  *   * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  * @version $Revision: 1.26 $ ($Date: 2006/09/04 06:09:05 $)  */
end_comment

begin_class
specifier|public
class|class
name|Database
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Database
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** 	 * Start up the database, configured using the given config file.  This method must be 	 * called precisely once before making use of any facilities offered in this package. 	 * 	 * @param configFile the config file that specifies the database to use 	 * @throws IllegalStateException if the database has already been started 	 */
specifier|public
specifier|static
name|void
name|startup
parameter_list|(
name|File
name|configFile
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|dbName
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"database already started"
argument_list|)
throw|;
name|configFile
operator|=
name|configFile
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|configFile
operator|.
name|getName
argument_list|()
argument_list|,
name|configFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
name|dbName
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|dbName
argument_list|)
expr_stmt|;
name|txManager
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|ListenerManager
operator|.
name|configureTriggerDispatcher
argument_list|(
operator|new
name|Database
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Shut down the database connection.  If the database is not started, do nothing. 	 */
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|pool
operator|=
literal|null
expr_stmt|;
block|}
comment|/** 	 * Ensure the database is started.  If the database is not started, start it with the 	 * given config file.  If it is already started, make sure it was started with the same 	 * config file. 	 *  	 * @param configFile the config file that specifies the database to use 	 * @throws IllegalStateException if the database was already started with a different config file 	 */
specifier|public
specifier|static
name|void
name|ensureStarted
parameter_list|(
name|File
name|configFile
parameter_list|)
block|{
if|if
condition|(
name|isStarted
argument_list|()
condition|)
block|{
name|String
name|currentPath
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getConfigFilePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|configFile
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|File
argument_list|(
name|currentPath
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"database already started with different configuration "
operator|+
name|currentPath
argument_list|)
throw|;
block|}
else|else
block|{
name|startup
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Return whether the database has been started and is currently running. 	 * 	 * @return<code>true</code> if the database has been started with any configuration file 	 */
specifier|public
specifier|static
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|dbName
argument_list|)
return|;
block|}
comment|/** 	 * Flush the contents of the database to disk.  This ensures that all transactions are written out 	 * and the state of the database is synced.  It shouldn't be necessary any more with the newly 	 * implemented transaction recovery and this method will probably be deprecated in the future. 	 */
specifier|public
specifier|static
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|dbName
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"database not started"
argument_list|)
throw|;
try|try
block|{
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|sync
argument_list|(
name|Sync
operator|.
name|MAJOR_SYNC
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Login to obtain access to the database.  The password should be passed in the clear. 	 * If a user does not have a password set, you can pass in any value including<code>null</code>. 	 * Note that all newly created databases have a user<code>admin</code> with no password set. 	 * 	 * @param username the username of the user being logged in 	 * @param password the password corresponding to that user name, or<code>null</code> if none 	 * @return an instance of the database configured for access by the given user 	 * @throws DatabaseException if the user could not be logged in 	 */
specifier|public
specifier|static
name|Database
name|login
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
operator|||
operator|!
name|user
operator|.
name|validate
argument_list|(
name|password
argument_list|)
condition|)
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"invalid user credentials"
argument_list|)
throw|;
return|return
operator|new
name|Database
argument_list|(
name|user
argument_list|)
return|;
block|}
comment|/** 	 * Remove the given listener from all trigger points on all sources. 	 * 	 * @param listener the listener to remove 	 */
specifier|public
specifier|static
name|void
name|remove
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|ListenerManager
operator|.
name|INSTANCE
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|ROOT_PREFIX
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|equals
argument_list|(
name|ROOT_PREFIX
argument_list|)
condition|?
literal|"/"
else|:
name|path
operator|.
name|substring
argument_list|(
name|Database
operator|.
name|ROOT_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
specifier|private
specifier|static
name|String
name|dbName
init|=
literal|"exist"
decl_stmt|;
specifier|static
specifier|final
name|String
name|ROOT_PREFIX
init|=
literal|"/db"
decl_stmt|;
comment|// should match the root prefix in NativeBroker
specifier|private
specifier|static
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|static
name|TransactionManager
name|txManager
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Transaction
argument_list|>
name|localTransaction
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Transaction
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|WeakHashMap
argument_list|<
name|NativeBroker
argument_list|,
name|Boolean
argument_list|>
name|instrumentedBrokers
init|=
operator|new
name|WeakHashMap
argument_list|<
name|NativeBroker
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|User
name|user
decl_stmt|;
specifier|private
specifier|final
name|NamespaceMap
name|namespaceBindings
decl_stmt|;
name|String
name|defaultExportEncoding
init|=
literal|"UTF-8"
decl_stmt|;
name|Database
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|namespaceBindings
operator|=
operator|new
name|NamespaceMap
argument_list|()
expr_stmt|;
block|}
name|Database
parameter_list|(
name|Database
name|parent
parameter_list|,
name|NamespaceMap
name|namespaceBindings
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|parent
operator|.
name|user
expr_stmt|;
name|this
operator|.
name|namespaceBindings
operator|=
name|namespaceBindings
operator|.
name|extend
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Set the default character encoding to be used when exporting XML files from the database. 	 * If not explicitly set, it defaults to UTF-8. 	 * 	 * @param encoding 	 */
specifier|public
name|void
name|setDefaultExportEncoding
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
name|defaultExportEncoding
operator|=
name|encoding
expr_stmt|;
block|}
name|DBBroker
name|acquireBroker
parameter_list|()
block|{
try|try
block|{
name|NativeBroker
name|broker
init|=
operator|(
name|NativeBroker
operator|)
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|instrumentedBrokers
operator|.
name|get
argument_list|(
name|broker
argument_list|)
operator|==
literal|null
condition|)
block|{
name|broker
operator|.
name|addContentLoadingObserver
argument_list|(
name|contentObserver
argument_list|)
expr_stmt|;
name|instrumentedBrokers
operator|.
name|put
argument_list|(
name|broker
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
return|return
name|broker
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|void
name|releaseBroker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Return the namespace bindings for this database instance.  They will be inherited by 	 * all resources derived from this instance. 	 * 	 * @return the namespace bindings for this database instance 	 */
specifier|public
name|NamespaceMap
name|namespaceBindings
parameter_list|()
block|{
return|return
name|namespaceBindings
return|;
block|}
specifier|private
name|Sequence
name|adoptInternal
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|DBBroker
name|broker
init|=
name|acquireBroker
argument_list|()
decl_stmt|;
try|try
block|{
name|XQueryContext
name|context
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|INTERNAL_PREFIX_LOOKUP
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareNamespaces
argument_list|(
name|namespaceBindings
operator|.
name|getCombinedMap
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setBackwardsCompatibility
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
operator|new
name|DocumentSet
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|o
argument_list|,
name|context
argument_list|,
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ItemList
name|adopt
parameter_list|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|node
parameter_list|)
block|{
comment|// this works for DocumentFragments too, they'll be automatically expanded
return|return
operator|new
name|ItemList
argument_list|(
name|adoptInternal
argument_list|(
name|node
argument_list|)
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** 	 * Get the document for the given absolute path.  Namespace bindings will be inherited 	 * from this database. 	 * 	 * @param path the absolute path of the desired document 	 * @return the document at the given path 	 * @throws DatabaseException if the document is not found or something else goes wrong 	 */
specifier|public
name|Document
name|getDocument
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"empty document path"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"document path not absolute"
argument_list|)
throw|;
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"document path ends with '/'"
argument_list|)
throw|;
name|int
name|i
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
assert|assert
name|i
operator|!=
operator|-
literal|1
assert|;
return|return
name|getFolder
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
operator|.
name|documents
argument_list|()
operator|.
name|get
argument_list|(
name|path
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * Get the folder for the given path.  Namespace mappings will be inherited from this 	 * database. 	 *  	 * @param path the address of the desired collection 	 * @return a collection bound to the given path 	 * @throws DatabaseException if the path does not identify a valid collection 	 */
specifier|public
name|Folder
name|getFolder
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|Folder
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** 	 * Create the folder for the given path.  Namespace mappings will be inherited from this 	 * database.  If the folder does not exist, it is created along with all required ancestors. 	 *  	 * @param path the address of the desired collection 	 * @return a collection bound to the given path 	 */
specifier|public
name|Folder
name|createFolder
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|Folder
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** 	 * Return a query service that runs queries over the given list of resources. 	 * The resources can be of different kinds, and come from different locations in the 	 * folder hierarchy.  The service will inherit the database's namespace bindings, 	 * rather than the bindings of any given context resource. 	 * 	 * @param context the arbitrary collection of database objects over which to query 	 * @return a query service over the given resources 	 */
specifier|public
name|QueryService
name|query
parameter_list|(
name|Resource
modifier|...
name|context
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * Return a query service that runs queries over the given list of resources. 	 * The resources can be of different kinds, and come from different locations in the 	 * folder hierarchy.  The service will inherit the database's namespace bindings, 	 * rather than the bindings of any given context resource. 	 * 	 * @param context the arbitrary collection of database objects over which to query; 	 * 	the collection is not copied, and the collection's contents are re-read every time the query is performed 	 * @return a query service over the given resources 	 */
specifier|public
name|QueryService
name|query
parameter_list|(
specifier|final
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|?
extends|extends
name|Resource
argument_list|>
name|context
parameter_list|)
block|{
return|return
operator|new
name|QueryService
argument_list|(
name|getFolder
argument_list|(
literal|"/"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
name|void
name|prepareContext
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|docs
operator|=
operator|new
name|DocumentSet
argument_list|()
expr_stmt|;
name|base
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|Resource
name|res
range|:
name|context
control|)
block|{
name|QueryService
name|qs
init|=
name|res
operator|.
name|query
argument_list|()
decl_stmt|;
if|if
condition|(
name|qs
operator|.
name|docs
operator|!=
literal|null
condition|)
name|docs
operator|.
name|addAll
argument_list|(
name|qs
operator|.
name|docs
argument_list|)
expr_stmt|;
if|if
condition|(
name|qs
operator|.
name|base
operator|!=
literal|null
condition|)
try|try
block|{
name|base
operator|.
name|addAll
argument_list|(
name|qs
operator|.
name|base
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"unexpected item type conflict"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|;
block|}
name|void
name|checkSame
parameter_list|(
name|Resource
name|o
parameter_list|)
block|{
comment|// allow other resource to be a NULL, as those are safe and database-neutral
if|if
condition|(
operator|!
operator|(
name|o
operator|.
name|database
argument_list|()
operator|==
literal|null
operator|||
name|o
operator|.
name|database
argument_list|()
operator|.
name|user
operator|==
name|this
operator|.
name|user
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot combine objects from two database instances in one operation"
argument_list|)
throw|;
block|}
specifier|private
specifier|static
specifier|final
name|WeakMultiValueHashMap
argument_list|<
name|String
argument_list|,
name|StaleMarker
argument_list|>
name|staleMap
init|=
operator|new
name|WeakMultiValueHashMap
argument_list|<
name|String
argument_list|,
name|StaleMarker
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|void
name|stale
parameter_list|(
name|String
name|key
parameter_list|)
block|{
synchronized|synchronized
init|(
name|staleMap
init|)
block|{
for|for
control|(
name|StaleMarker
name|value
range|:
name|staleMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
control|)
name|value
operator|.
name|mark
argument_list|()
expr_stmt|;
name|staleMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|trackStale
parameter_list|(
name|String
name|key
parameter_list|,
name|StaleMarker
name|value
parameter_list|)
block|{
name|staleMap
operator|.
name|put
argument_list|(
name|normalizePath
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|ContentLoadingObserver
name|contentObserver
init|=
operator|new
name|ContentLoadingObserver
argument_list|()
block|{
specifier|public
name|void
name|dropIndex
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|stale
argument_list|(
name|normalizePath
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|dropIndex
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|ReadOnlyException
block|{
name|stale
argument_list|(
name|normalizePath
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeNode
parameter_list|(
name|StoredNode
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|stale
argument_list|(
name|normalizePath
argument_list|(
operator|(
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
operator|+
literal|"#"
operator|+
name|node
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
block|{
block|}
specifier|public
name|void
name|storeAttribute
parameter_list|(
name|AttrImpl
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|int
name|indexingHint
parameter_list|,
name|RangeIndexSpec
name|spec
parameter_list|,
name|boolean
name|remove
parameter_list|)
block|{
block|}
specifier|public
name|void
name|storeText
parameter_list|(
name|TextImpl
name|node
parameter_list|,
name|NodePath
name|currentPath
parameter_list|,
name|int
name|indexingHint
parameter_list|)
block|{
block|}
specifier|public
name|void
name|sync
parameter_list|()
block|{
block|}
specifier|public
name|void
name|printStatistics
parameter_list|()
block|{
block|}
specifier|public
name|boolean
name|close
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
specifier|public
name|void
name|closeAndRemove
parameter_list|()
block|{
comment|// TODO:  do nothing OK here?  indexes just got wiped and recreated, and this listener
comment|// was removed...
block|}
block|}
decl_stmt|;
comment|/** 	 * Return a transaction for use with database operations.  If a transaction is already in progress 	 * then join it, otherwise begin a new one.  If a transaction is joined, calling<code>commit</code> 	 * or<code>abort</code> on the returned instance will have no effect; only the outermost  	 * transaction object can do this. 	 * 	 * @return a transaction object 	 */
specifier|static
name|Transaction
name|requireTransaction
parameter_list|()
block|{
name|Transaction
name|t
init|=
name|localTransaction
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|t
operator|==
literal|null
condition|?
operator|new
name|Transaction
argument_list|(
name|txManager
argument_list|)
else|:
operator|new
name|Transaction
argument_list|(
name|t
operator|.
name|tx
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|WeakMultiValueHashMap
argument_list|<
name|Long
argument_list|,
name|NodeProxy
argument_list|>
name|nodes
init|=
operator|new
name|WeakMultiValueHashMap
argument_list|<
name|Long
argument_list|,
name|NodeProxy
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|ADDRESS_MASK
init|=
literal|0xFFFFFFFF0000FFFFL
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|NodeIndexListener
name|indexChangeListener
init|=
operator|new
name|NodeIndexListener
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"hiding"
argument_list|)
specifier|private
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.exist.fluent.Database.indexChangeListener"
argument_list|)
decl_stmt|;
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
name|int
name|numUpdated
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeProxy
name|target
range|:
name|nodes
operator|.
name|get
argument_list|(
name|node
operator|.
name|getInternalAddress
argument_list|()
operator|&
name|ADDRESS_MASK
argument_list|)
control|)
block|{
name|target
operator|.
name|setNodeId
argument_list|(
name|node
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|numUpdated
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"change nodeid at "
operator|+
name|StorageAddress
operator|.
name|toString
argument_list|(
name|node
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
operator|+
literal|" to "
operator|+
name|node
operator|.
name|getNodeId
argument_list|()
operator|+
literal|"; updated "
operator|+
name|numUpdated
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|static
name|void
name|trackNode
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|proxy
operator|.
name|getNodeType
argument_list|()
operator|==
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
return|return;
comment|// no need to track document nodes as they don't change gids
if|if
condition|(
name|proxy
operator|.
name|getInternalAddress
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|StoredNode
name|node
init|=
operator|(
name|StoredNode
operator|)
name|proxy
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"can't load node for proxy, doc="
operator|+
name|proxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|lastSegment
argument_list|()
operator|+
literal|", nodeid="
operator|+
name|proxy
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|proxy
operator|.
name|setInternalAddress
argument_list|(
name|node
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|proxy
operator|.
name|getInternalAddress
argument_list|()
operator|!=
operator|-
literal|1
assert|;
block|}
name|proxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|setIndexListener
argument_list|(
name|indexChangeListener
argument_list|)
expr_stmt|;
comment|// this may cause duplicates in the list; try to avoid them by design,
comment|// or it might become a performance hit
name|nodes
operator|.
name|put
argument_list|(
name|proxy
operator|.
name|getInternalAddress
argument_list|()
operator|&
name|ADDRESS_MASK
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|emptyIterator
parameter_list|()
block|{
return|return
name|EMPTY_ITERATOR
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
specifier|final
name|Iterator
name|EMPTY_ITERATOR
init|=
operator|new
name|Iterator
argument_list|()
block|{
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
specifier|final
name|Iterable
name|EMPTY_ITERABLE
init|=
operator|new
name|Iterable
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
name|EMPTY_ITERATOR
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

