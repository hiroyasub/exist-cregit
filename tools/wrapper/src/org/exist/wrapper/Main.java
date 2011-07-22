begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|wrapper
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Observable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Classpath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tanukisoftware
operator|.
name|wrapper
operator|.
name|WrapperListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tanukisoftware
operator|.
name|wrapper
operator|.
name|WrapperManager
import|;
end_import

begin_comment
comment|/**  * Implementation of WrapperListener for Tanuki's Java Service Wrapper.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Main
implements|implements
name|WrapperListener
implements|,
name|Observer
block|{
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|klazz
decl_stmt|;
specifier|private
name|Object
name|app
decl_stmt|;
specifier|private
name|Main
parameter_list|()
block|{
block|}
comment|/** 	 * Start the included Jetty server using reflection. The ClassLoader is set up through eXist's 	 * bootstrap loader, so the wrapper doesn't need to know all jars. 	 *  	 * The first argument passed to this method determines the run mode. It should 	 * be either "jetty" or "standalone". 	 *  	 * @see org.tanukisoftware.wrapper.WrapperListener#start(java.lang.String[]) 	 */
specifier|public
name|Integer
name|start
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"exist.register-shutdown-hook"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"jetty.home = "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.home"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
comment|// use the bootstrap loader to autodetect EXIST_HOME and
comment|// construct a correct classpath
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
name|loader
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|File
name|homeDir
init|=
name|loader
operator|.
name|detectHome
argument_list|()
decl_stmt|;
name|Classpath
name|classpath
init|=
name|loader
operator|.
name|constructClasspath
argument_list|(
name|homeDir
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ClassLoader
name|cl
init|=
name|classpath
operator|.
name|getClassLoader
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|cl
argument_list|)
expr_stmt|;
name|klazz
operator|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.exist.jetty.JettyStart"
argument_list|)
expr_stmt|;
comment|// find the run() method in the class
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|methodParamTypes
init|=
operator|new
name|Class
index|[
literal|2
index|]
decl_stmt|;
name|methodParamTypes
index|[
literal|0
index|]
operator|=
name|args
operator|.
name|getClass
argument_list|()
expr_stmt|;
name|methodParamTypes
index|[
literal|1
index|]
operator|=
name|Observer
operator|.
name|class
expr_stmt|;
name|Method
name|method
init|=
name|klazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"run"
argument_list|,
name|methodParamTypes
argument_list|)
decl_stmt|;
comment|// create a new instance and invoke the run() method
name|app
operator|=
name|klazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|String
index|[]
name|myArgs
init|=
operator|new
name|String
index|[
name|args
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|myArgs
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
name|Object
index|[]
name|params
init|=
operator|new
name|Object
index|[
literal|2
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
name|myArgs
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|this
expr_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|app
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
name|WrapperManager
operator|.
name|log
argument_list|(
name|WrapperManager
operator|.
name|WRAPPER_LOG_LEVEL_FATAL
argument_list|,
literal|"An error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.tanukisoftware.wrapper.WrapperListener#stop(int) 	 */
specifier|public
name|int
name|stop
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
comment|// wait up to 1 minute
name|WrapperManager
operator|.
name|signalStopping
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
try|try
block|{
name|Method
name|method
init|=
name|klazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"shutdown"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|app
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
return|return
name|exitCode
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.tanukisoftware.wrapper.WrapperListener#controlEvent(int) 	 */
specifier|public
name|void
name|controlEvent
parameter_list|(
name|int
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|==
name|WrapperManager
operator|.
name|WRAPPER_CTRL_HUP_EVENT
condition|)
block|{
try|try
block|{
name|Method
name|method
init|=
name|klazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"systemInfo"
argument_list|,
operator|new
name|Class
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|app
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
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
if|else if
condition|(
name|WrapperManager
operator|.
name|isControlledByNativeWrapper
argument_list|()
condition|)
block|{
comment|// the wrapper will take care of this event
block|}
else|else
block|{
if|if
condition|(
operator|(
name|event
operator|==
name|WrapperManager
operator|.
name|WRAPPER_CTRL_C_EVENT
operator|)
operator|||
operator|(
name|event
operator|==
name|WrapperManager
operator|.
name|WRAPPER_CTRL_CLOSE_EVENT
operator|)
operator|||
operator|(
name|event
operator|==
name|WrapperManager
operator|.
name|WRAPPER_CTRL_SHUTDOWN_EVENT
operator|)
condition|)
block|{
name|WrapperManager
operator|.
name|stop
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|update
parameter_list|(
name|Observable
name|o
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
if|if
condition|(
literal|"shutdown"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
name|WrapperManager
operator|.
name|signalStopping
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
else|else
name|WrapperManager
operator|.
name|signalStarting
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Main
name|main
init|=
operator|new
name|Main
argument_list|()
decl_stmt|;
name|WrapperManager
operator|.
name|start
argument_list|(
name|main
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

