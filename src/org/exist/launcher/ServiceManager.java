begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|launcher
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|SystemUtils
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
name|util
operator|.
name|ConfigurationHelper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
specifier|public
class|class
name|ServiceManager
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
name|ServiceManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Launcher
name|launcher
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|wrapperProperties
decl_stmt|;
specifier|private
name|boolean
name|canUseServices
decl_stmt|;
specifier|private
name|boolean
name|inServiceInstall
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isInstalled
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isRunning
init|=
literal|false
decl_stmt|;
specifier|public
name|ServiceManager
parameter_list|(
name|Launcher
name|launcher
parameter_list|)
block|{
name|this
operator|.
name|launcher
operator|=
name|launcher
expr_stmt|;
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_WINDOWS
condition|)
block|{
name|canUseServices
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|isRoot
argument_list|(
parameter_list|(
name|root
parameter_list|)
lambda|->
name|canUseServices
operator|=
name|root
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|eXistHome
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|wrapperConfig
decl_stmt|;
if|if
condition|(
name|eXistHome
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|wrapperConfig
operator|=
name|eXistHome
operator|.
name|get
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"tools/yajsw/conf/wrapper.conf"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|wrapperConfig
operator|=
name|Paths
operator|.
name|get
argument_list|(
literal|"tools/yajsw/conf/wrapper.conf"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
literal|"wrapper.config"
argument_list|,
name|wrapperConfig
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wrapperProperties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|wrapperProperties
operator|.
name|setProperty
argument_list|(
literal|"wrapper.working.dir"
argument_list|,
name|eXistHome
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wrapperProperties
operator|.
name|setProperty
argument_list|(
literal|"wrapper.config"
argument_list|,
name|wrapperConfig
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|installedAsService
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isInstallingService
parameter_list|()
block|{
return|return
name|inServiceInstall
return|;
block|}
specifier|public
name|boolean
name|isInstalled
parameter_list|()
block|{
return|return
name|isInstalled
return|;
block|}
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|isRunning
return|;
block|}
specifier|public
name|boolean
name|canUseServices
parameter_list|()
block|{
return|return
name|canUseServices
return|;
block|}
specifier|private
name|void
name|installedAsService
parameter_list|()
block|{
if|if
condition|(
name|canUseServices
condition|)
block|{
specifier|final
name|String
name|cmd
decl_stmt|;
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_UNIX
condition|)
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/queryDaemon.sh"
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/queryService.bat"
expr_stmt|;
block|}
name|runWrapperCmd
argument_list|(
name|cmd
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|code
operator|==
literal|0
condition|)
block|{
name|Pattern
name|statusRegex
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Installed\\s*:\\s*(.*)$|^Running\\s*:\\s*(.*)$"
argument_list|,
name|Pattern
operator|.
name|MULTILINE
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|statusRegex
operator|.
name|matcher
argument_list|(
name|output
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|isInstalled
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|isRunning
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"isInstalled: "
operator|+
name|isInstalled
operator|+
literal|"; isRunning: "
operator|+
name|isRunning
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|installAsService
parameter_list|()
block|{
name|launcher
operator|.
name|showTrayMessage
argument_list|(
literal|"Installing service and starting eXistdb ..."
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|inServiceInstall
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|canUseServices
condition|)
block|{
specifier|final
name|String
name|cmd
decl_stmt|;
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_UNIX
condition|)
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/installDaemon.sh"
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/installService.bat"
expr_stmt|;
block|}
name|runWrapperCmd
argument_list|(
name|cmd
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|code
operator|==
literal|0
condition|)
block|{
name|isInstalled
operator|=
literal|true
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
name|launcher
operator|.
name|showTrayMessage
argument_list|(
literal|"Service installed and started"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
literal|null
argument_list|,
literal|"Failed to install service. "
argument_list|,
literal|"Install Service Failed"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
name|isInstalled
operator|=
literal|false
expr_stmt|;
name|isRunning
operator|=
literal|false
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|inServiceInstall
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|boolean
name|uninstall
parameter_list|()
block|{
if|if
condition|(
name|isInstalled
condition|)
block|{
specifier|final
name|String
name|cmd
decl_stmt|;
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_MAC
condition|)
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/uninstallDaemon.sh"
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/uninstallService.bat"
expr_stmt|;
block|}
name|runWrapperCmd
argument_list|(
name|cmd
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|code
operator|==
literal|0
condition|)
block|{
name|isInstalled
operator|=
literal|false
expr_stmt|;
name|isRunning
operator|=
literal|false
expr_stmt|;
name|launcher
operator|.
name|showTrayMessage
argument_list|(
literal|"Service stopped and uninstalled"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
literal|null
argument_list|,
literal|"Failed to uninstall service. "
argument_list|,
literal|"Uninstalling Service Failed"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
name|isInstalled
operator|=
literal|true
expr_stmt|;
name|isRunning
operator|=
literal|true
expr_stmt|;
block|}
name|launcher
operator|.
name|setServiceState
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
specifier|final
name|String
name|cmd
decl_stmt|;
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_MAC
condition|)
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/startDaemon.sh"
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/startService.bat"
expr_stmt|;
block|}
name|runWrapperCmd
argument_list|(
name|cmd
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|code
operator|==
literal|0
condition|)
block|{
name|isRunning
operator|=
literal|true
expr_stmt|;
name|launcher
operator|.
name|showTrayMessage
argument_list|(
literal|"Service started"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
literal|null
argument_list|,
literal|"Failed to start service. "
argument_list|,
literal|"Starting Service Failed"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
name|isRunning
operator|=
literal|false
expr_stmt|;
block|}
name|launcher
operator|.
name|setServiceState
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|isRunning
return|;
block|}
specifier|protected
name|boolean
name|stop
parameter_list|()
block|{
if|if
condition|(
name|isRunning
condition|)
block|{
specifier|final
name|String
name|cmd
decl_stmt|;
if|if
condition|(
name|SystemUtils
operator|.
name|IS_OS_MAC
condition|)
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/stopDaemon.sh"
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
literal|"tools/yajsw/bin/stopService.bat"
expr_stmt|;
block|}
name|runWrapperCmd
argument_list|(
name|cmd
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|code
operator|==
literal|0
condition|)
block|{
name|isRunning
operator|=
literal|false
expr_stmt|;
name|launcher
operator|.
name|showTrayMessage
argument_list|(
literal|"Service stopped"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
literal|null
argument_list|,
literal|"Failed to stop service. "
argument_list|,
literal|"Stopping Service Failed"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
name|isRunning
operator|=
literal|true
expr_stmt|;
block|}
name|launcher
operator|.
name|setServiceState
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|isRoot
parameter_list|(
name|Consumer
argument_list|<
name|Boolean
argument_list|>
name|consumer
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-u"
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|args
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
name|consumer
operator|.
name|accept
argument_list|(
literal|"0"
operator|.
name|equals
argument_list|(
name|output
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|showServicesConsole
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"cmd"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"/c"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"services.msc"
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|args
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|runWrapperCmd
parameter_list|(
specifier|final
name|String
name|cmd
parameter_list|,
specifier|final
name|BiConsumer
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|consumer
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|args
argument_list|,
parameter_list|(
name|code
parameter_list|,
name|output
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|accept
argument_list|(
name|code
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
name|BiConsumer
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|consumer
parameter_list|)
block|{
specifier|final
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|args
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|home
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|pb
operator|.
name|directory
argument_list|(
name|home
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
name|pb
operator|.
name|inheritIO
argument_list|()
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|Process
name|process
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|exitValue
init|=
name|process
operator|.
name|waitFor
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|exitValue
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Error Running Process"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

