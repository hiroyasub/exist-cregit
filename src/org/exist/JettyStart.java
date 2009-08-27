begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observer
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
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|cluster
operator|.
name|ClusterComunication
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterException
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
name|SingleInstanceConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|XmlLibraryChecker
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
name|DatabaseImpl
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|system
operator|.
name|GetVersion
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|http
operator|.
name|HttpContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|http
operator|.
name|HttpListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|MultiException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_comment
comment|/**  * This class provides a main method to start Jetty with eXist. It registers shutdown  * handlers to cleanly shut down the database and the webserver.  * If database is NATIVE-CLUSTER, Clustercomunication is configured and started.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|JettyStart
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JettyStart
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|JettyStart
name|start
init|=
operator|new
name|JettyStart
argument_list|()
decl_stmt|;
name|start
operator|.
name|run
argument_list|(
name|args
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JettyStart
parameter_list|()
block|{
comment|// Additional checks XML libs @@@@
name|XmlLibraryChecker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Observer
name|observer
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"No configuration file specified!"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|shutdownHookOption
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.register-shutdown-hook"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|boolean
name|registerShutdownHook
init|=
name|shutdownHookOption
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
name|Properties
name|sysProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|sysProperties
operator|.
name|load
argument_list|(
name|GetVersion
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/exist/system.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// configure database
name|logger
operator|.
name|info
argument_list|(
literal|"Configuring eXist from "
operator|+
name|SingleInstanceConfiguration
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Running with Java "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|,
literal|"(unknown java.version)"
argument_list|)
operator|+
literal|" ["
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vendor"
argument_list|,
literal|"(unknown java.vendor)"
argument_list|)
operator|+
literal|" ("
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.name"
argument_list|,
literal|"(unknown java.vm.name)"
argument_list|)
operator|+
literal|") in "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|,
literal|"(unknown java.home)"
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|String
name|msg
decl_stmt|;
name|msg
operator|=
literal|"[eXist Version : "
operator|+
name|sysProperties
operator|.
name|get
argument_list|(
literal|"product-version"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
literal|"[eXist Build : "
operator|+
name|sysProperties
operator|.
name|get
argument_list|(
literal|"product-build"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
literal|"[eXist Home : "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
literal|"[SVN Revision : "
operator|+
name|sysProperties
operator|.
name|get
argument_list|(
literal|"svn-revision"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
literal|"[Operating System : "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.version"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
literal|"[jetty.home : "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.home"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|=
literal|"[log4j.configuration : "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"log4j.configuration"
argument_list|)
operator|+
literal|"]"
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
try|try
block|{
comment|// we register our own shutdown hook
name|BrokerPool
operator|.
name|setRegisterShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// configure the database instance
name|SingleInstanceConfiguration
name|config
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
name|config
operator|=
operator|new
name|SingleInstanceConfiguration
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
else|else
name|config
operator|=
operator|new
name|SingleInstanceConfiguration
argument_list|()
expr_stmt|;
if|if
condition|(
name|observer
operator|!=
literal|null
condition|)
name|BrokerPool
operator|.
name|registerStatusObserver
argument_list|(
name|observer
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|// register the XMLDB driver
name|Database
name|xmldb
init|=
operator|new
name|DatabaseImpl
argument_list|()
decl_stmt|;
name|xmldb
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|xmldb
argument_list|)
expr_stmt|;
name|configureCluster
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"configuration error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// start Jetty
specifier|final
name|Server
name|server
decl_stmt|;
name|int
name|port
init|=
literal|8080
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|Server
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|registerShutdownListener
argument_list|(
operator|new
name|ShutdownListenerImpl
argument_list|(
name|server
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|HttpListener
index|[]
name|listeners
init|=
name|server
operator|.
name|getListeners
argument_list|()
decl_stmt|;
name|StringBuilder
name|ports
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|listeners
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|HttpListener
name|listener
range|:
name|listeners
control|)
block|{
name|port
operator|=
name|listener
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|ports
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ports
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
name|HttpContext
index|[]
name|contexts
init|=
name|server
operator|.
name|getContexts
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"----------------------------------------------------------------"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"eXist-db has started on port"
operator|+
name|ports
operator|+
literal|". Configured contexts:"
argument_list|)
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
name|contexts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
name|contexts
index|[
name|i
index|]
operator|.
name|getContextPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"----------------------------------------------------------------"
argument_list|)
expr_stmt|;
if|if
condition|(
name|registerShutdownHook
condition|)
block|{
comment|// register a shutdown hook for the server
name|Thread
name|hook
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|setName
argument_list|(
literal|"Shutdown"
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|server
operator|.
name|stop
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
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|hook
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MultiException
name|e
parameter_list|)
block|{
name|boolean
name|hasBindException
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Object
name|t
range|:
name|e
operator|.
name|getExceptions
argument_list|()
control|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|java
operator|.
name|net
operator|.
name|BindException
condition|)
block|{
name|hasBindException
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"----------------------------------------------------------"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"ERROR: Could not start jetty, port "
operator|+
name|port
operator|+
literal|" is already in use.   "
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"----------------------------------------------------------"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If it is another error, print stacktrace
if|if
condition|(
operator|!
name|hasBindException
condition|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This class gets called after the database received a shutdown request. 	 * 	 * @author wolf 	 */
specifier|private
specifier|static
class|class
name|ShutdownListenerImpl
implements|implements
name|ShutdownListener
block|{
specifier|private
name|Server
name|server
decl_stmt|;
specifier|public
name|ShutdownListenerImpl
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|(
name|String
name|dbname
parameter_list|,
name|int
name|remainingInstances
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Database shutdown: stopping server in 1sec ..."
argument_list|)
expr_stmt|;
if|if
condition|(
name|remainingInstances
operator|==
literal|0
condition|)
block|{
comment|// give the webserver a 1s chance to complete open requests
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|TimerTask
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// stop the server
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ClusterComunication
name|cluster
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|configureCluster
parameter_list|(
name|Configuration
name|c
parameter_list|)
throws|throws
name|ClusterException
block|{
name|String
name|database
init|=
operator|(
name|String
operator|)
name|c
operator|.
name|getProperty
argument_list|(
literal|"database"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|database
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"NATIVE_CLUSTER"
argument_list|)
condition|)
return|return;
name|ClusterComunication
operator|.
name|configure
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

