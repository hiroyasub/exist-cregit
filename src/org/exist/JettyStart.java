begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id:  */
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
name|File
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
comment|/**  * This class provides a main method to start Jetty with eXist. It registers shutdown  * handlers to cleanly shut down the database and the webserver.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|JettyStart
block|{
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
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JettyStart
parameter_list|()
block|{
block|}
specifier|public
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No configuration file specified!"
argument_list|)
expr_stmt|;
return|return;
block|}
name|boolean
name|registerShutdownHook
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"exist.register-shutdown-hook"
argument_list|)
decl_stmt|;
comment|// configure database
name|String
name|home
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Configuring eXist from "
operator|+
name|home
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"conf.xml"
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
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
literal|"conf.xml"
argument_list|,
name|home
argument_list|)
decl_stmt|;
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"configuration error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
comment|/** 	 * This class gets called after the database received a shutdown request. 	 *   	 * @author wolf 	 */
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
name|System
operator|.
name|err
operator|.
name|println
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
block|}
end_class

end_unit

