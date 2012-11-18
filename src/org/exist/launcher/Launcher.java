begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|exist
operator|.
name|jetty
operator|.
name|JettyStart
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
name|imageio
operator|.
name|ImageIO
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
name|awt
operator|.
name|event
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
name|image
operator|.
name|BufferedImage
import|;
end_import

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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/**  * A launcher for the eXist-db server integrated with the desktop.  * Shows a splash screen during startup and registers a tray icon  * in the system bar.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Launcher
extends|extends
name|Observable
block|{
specifier|private
name|MenuItem
name|stopItem
decl_stmt|;
specifier|private
name|MenuItem
name|startItem
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|os
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// Switch to native look and feel except for Linux (ugly)
if|if
condition|(
operator|!
name|os
operator|.
name|equals
argument_list|(
literal|"Linux"
argument_list|)
condition|)
block|{
name|String
name|nativeLF
init|=
name|UIManager
operator|.
name|getSystemLookAndFeelClassName
argument_list|()
decl_stmt|;
try|try
block|{
name|UIManager
operator|.
name|setLookAndFeel
argument_list|(
name|nativeLF
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// can be safely ignored
block|}
block|}
comment|/* Turn off metal's use of bold fonts */
comment|//UIManager.put("swing.boldMetal", Boolean.FALSE);
comment|//Schedule a job for the event-dispatching thread:
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
operator|new
name|Launcher
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SystemTray
name|tray
init|=
literal|null
decl_stmt|;
specifier|private
name|TrayIcon
name|trayIcon
init|=
literal|null
decl_stmt|;
specifier|private
name|SplashScreen
name|splash
decl_stmt|;
specifier|private
name|JettyStart
name|jetty
decl_stmt|;
specifier|private
name|UtilityPanel
name|utilityPanel
decl_stmt|;
specifier|public
name|Launcher
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|SystemTray
operator|.
name|isSupported
argument_list|()
condition|)
block|{
name|tray
operator|=
name|SystemTray
operator|.
name|getSystemTray
argument_list|()
expr_stmt|;
block|}
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|utilityPanel
operator|=
operator|new
name|UtilityPanel
argument_list|(
name|Launcher
operator|.
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isSystemTraySupported
argument_list|()
condition|)
name|utilityPanel
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|captureConsole
argument_list|()
expr_stmt|;
specifier|final
name|String
name|home
init|=
name|getJettyHome
argument_list|()
decl_stmt|;
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
name|initSystemTray
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|splash
operator|=
operator|new
name|SplashScreen
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|splash
operator|.
name|addWindowListener
argument_list|(
operator|new
name|WindowAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|windowOpened
parameter_list|(
name|WindowEvent
name|windowEvent
parameter_list|)
block|{
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|jetty
operator|=
operator|new
name|JettyStart
argument_list|()
expr_stmt|;
name|jetty
operator|.
name|addObserver
argument_list|(
name|splash
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|home
block|}
argument_list|,
name|splash
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|showMessageAndExit
argument_list|(
literal|"Error Occurred"
argument_list|,
literal|"An error occurred during eXist-db startup. Please check the logs."
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSystemTraySupported
parameter_list|()
block|{
return|return
name|tray
operator|!=
literal|null
return|;
block|}
specifier|private
name|void
name|initSystemTray
parameter_list|(
name|String
name|home
parameter_list|)
block|{
name|Dimension
name|iconDim
init|=
name|tray
operator|.
name|getTrayIconSize
argument_list|()
decl_stmt|;
name|BufferedImage
name|image
init|=
literal|null
decl_stmt|;
try|try
block|{
name|image
operator|=
name|ImageIO
operator|.
name|read
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"icon32.png"
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
name|showMessageAndExit
argument_list|(
literal|"Launcher failed"
argument_list|,
literal|"Failed to read system tray icon."
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|trayIcon
operator|=
operator|new
name|TrayIcon
argument_list|(
name|image
operator|.
name|getScaledInstance
argument_list|(
name|iconDim
operator|.
name|width
argument_list|,
name|iconDim
operator|.
name|height
argument_list|,
name|Image
operator|.
name|SCALE_SMOOTH
argument_list|)
argument_list|,
literal|"eXist-db Launcher"
argument_list|)
expr_stmt|;
specifier|final
name|JDialog
name|hiddenFrame
init|=
operator|new
name|JDialog
argument_list|()
decl_stmt|;
name|hiddenFrame
operator|.
name|setUndecorated
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|PopupMenu
name|popup
init|=
name|createMenu
argument_list|(
name|home
argument_list|)
decl_stmt|;
name|trayIcon
operator|.
name|setPopupMenu
argument_list|(
name|popup
argument_list|)
expr_stmt|;
name|trayIcon
operator|.
name|addMouseListener
argument_list|(
operator|new
name|MouseAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|mouseClicked
parameter_list|(
name|MouseEvent
name|mouseEvent
parameter_list|)
block|{
if|if
condition|(
name|mouseEvent
operator|.
name|getButton
argument_list|()
operator|==
name|MouseEvent
operator|.
name|BUTTON1
condition|)
block|{
name|hiddenFrame
operator|.
name|add
argument_list|(
name|popup
argument_list|)
expr_stmt|;
name|popup
operator|.
name|show
argument_list|(
name|hiddenFrame
argument_list|,
name|mouseEvent
operator|.
name|getXOnScreen
argument_list|()
argument_list|,
name|mouseEvent
operator|.
name|getYOnScreen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|hiddenFrame
operator|.
name|setResizable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|hiddenFrame
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tray
operator|.
name|add
argument_list|(
name|trayIcon
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AWTException
name|e
parameter_list|)
block|{
return|return;
block|}
name|trayIcon
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Right click for menu"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PopupMenu
name|createMenu
parameter_list|(
specifier|final
name|String
name|home
parameter_list|)
block|{
name|PopupMenu
name|popup
init|=
operator|new
name|PopupMenu
argument_list|()
decl_stmt|;
name|startItem
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Start server"
argument_list|)
expr_stmt|;
name|startItem
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|startItem
argument_list|)
expr_stmt|;
name|startItem
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
if|if
condition|(
name|jetty
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Server already started"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|WARNING
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jetty
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
name|home
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|jetty
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|stopItem
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|startItem
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|trayIcon
operator|.
name|setToolTip
argument_list|(
literal|"eXist-db server running on port "
operator|+
name|jetty
operator|.
name|getPrimaryPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|stopItem
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Stop server"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|stopItem
argument_list|)
expr_stmt|;
name|stopItem
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|jetty
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|stopItem
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|startItem
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|trayIcon
operator|.
name|setToolTip
argument_list|(
literal|"eXist-db stopped"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|popup
operator|.
name|addSeparator
argument_list|()
expr_stmt|;
name|MenuItem
name|toolbar
init|=
operator|new
name|MenuItem
argument_list|(
literal|"Show Tool Window"
argument_list|)
decl_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|toolbar
argument_list|)
expr_stmt|;
name|toolbar
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|utilityPanel
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|MenuItem
name|item
decl_stmt|;
if|if
condition|(
name|Desktop
operator|.
name|isDesktopSupported
argument_list|()
condition|)
block|{
name|popup
operator|.
name|addSeparator
argument_list|()
expr_stmt|;
specifier|final
name|Desktop
name|desktop
init|=
name|Desktop
operator|.
name|getDesktop
argument_list|()
decl_stmt|;
if|if
condition|(
name|desktop
operator|.
name|isSupported
argument_list|(
name|Desktop
operator|.
name|Action
operator|.
name|BROWSE
argument_list|)
condition|)
block|{
name|item
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Open dashboard"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|item
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|dashboard
argument_list|(
name|desktop
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|item
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Open eXide"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|item
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|eXide
argument_list|(
name|desktop
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|item
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Open Java Admin Client"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|item
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|client
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|desktop
operator|.
name|isSupported
argument_list|(
name|Desktop
operator|.
name|Action
operator|.
name|OPEN
argument_list|)
condition|)
block|{
name|popup
operator|.
name|addSeparator
argument_list|()
expr_stmt|;
name|item
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Open exist.log"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|item
operator|.
name|addActionListener
argument_list|(
operator|new
name|LogActionListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|popup
operator|.
name|addSeparator
argument_list|()
expr_stmt|;
name|item
operator|=
operator|new
name|MenuItem
argument_list|(
literal|"Quit (and stop server)"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|item
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|popup
return|;
block|}
specifier|protected
name|void
name|shutdown
parameter_list|()
block|{
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Shutting down ..."
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|jetty
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|tray
operator|!=
literal|null
condition|)
name|tray
operator|.
name|remove
argument_list|(
name|trayIcon
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|dashboard
parameter_list|(
name|Desktop
name|desktop
parameter_list|)
block|{
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Opening dashboard in browser ..."
argument_list|)
expr_stmt|;
try|try
block|{
name|URI
name|url
init|=
operator|new
name|URI
argument_list|(
literal|"http://localhost:"
operator|+
name|jetty
operator|.
name|getPrimaryPort
argument_list|()
operator|+
literal|"/exist/apps/dashboard/"
argument_list|)
decl_stmt|;
name|desktop
operator|.
name|browse
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Failed to open URL"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Unable to launch browser"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Failed to open URL"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Unable to launch browser"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|eXide
parameter_list|(
name|Desktop
name|desktop
parameter_list|)
block|{
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Opening dashboard in browser ..."
argument_list|)
expr_stmt|;
try|try
block|{
name|URI
name|url
init|=
operator|new
name|URI
argument_list|(
literal|"http://localhost:"
operator|+
name|jetty
operator|.
name|getPrimaryPort
argument_list|()
operator|+
literal|"/exist/apps/eXide/"
argument_list|)
decl_stmt|;
name|desktop
operator|.
name|browse
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Failed to open URL"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Unable to launch browser"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Failed to open URL"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|utilityPanel
operator|.
name|setStatus
argument_list|(
literal|"Unable to launch browser"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|client
parameter_list|()
block|{
name|LauncherWrapper
name|wrapper
init|=
operator|new
name|LauncherWrapper
argument_list|(
literal|"client"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|launch
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|signalStarted
parameter_list|()
block|{
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
block|{
name|trayIcon
operator|.
name|setToolTip
argument_list|(
literal|"eXist-db server running on port "
operator|+
name|jetty
operator|.
name|getPrimaryPort
argument_list|()
argument_list|)
expr_stmt|;
name|startItem
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stopItem
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|signalShutdown
parameter_list|()
block|{
if|if
condition|(
name|isSystemTraySupported
argument_list|()
condition|)
block|{
name|trayIcon
operator|.
name|setToolTip
argument_list|(
literal|"eXist-db server stopped"
argument_list|)
expr_stmt|;
name|startItem
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|stopItem
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getJettyHome
parameter_list|()
block|{
name|String
name|jettyProperty
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.home"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jettyProperty
operator|==
literal|null
condition|)
block|{
name|File
name|home
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|jettyHome
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"tools"
argument_list|)
argument_list|,
literal|"jetty"
argument_list|)
decl_stmt|;
name|jettyProperty
operator|=
name|jettyHome
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jetty.home"
argument_list|,
name|jettyProperty
argument_list|)
expr_stmt|;
block|}
name|File
name|standaloneFile
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|jettyProperty
argument_list|,
literal|"etc"
argument_list|)
argument_list|,
literal|"jetty.xml"
argument_list|)
decl_stmt|;
return|return
name|standaloneFile
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
specifier|protected
name|void
name|showMessageAndExit
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|message
parameter_list|,
name|boolean
name|logs
parameter_list|)
block|{
name|JPanel
name|panel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|panel
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|label
operator|.
name|setHorizontalAlignment
argument_list|(
name|SwingConstants
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
if|if
condition|(
name|logs
condition|)
block|{
name|JButton
name|displayLogs
init|=
operator|new
name|JButton
argument_list|(
literal|"View Log"
argument_list|)
decl_stmt|;
name|displayLogs
operator|.
name|addActionListener
argument_list|(
operator|new
name|LogActionListener
argument_list|()
argument_list|)
expr_stmt|;
name|label
operator|.
name|setHorizontalAlignment
argument_list|(
name|SwingConstants
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|displayLogs
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
block|}
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|splash
argument_list|,
name|panel
argument_list|,
name|title
argument_list|,
name|JOptionPane
operator|.
name|WARNING_MESSAGE
argument_list|)
expr_stmt|;
comment|//System.exit(1);
block|}
comment|/**      * Ensure that stdout and stderr messages are also printed      * to the logs.      */
specifier|private
name|void
name|captureConsole
parameter_list|()
block|{
name|System
operator|.
name|setOut
argument_list|(
name|createLoggingProxy
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|createLoggingProxy
argument_list|(
name|System
operator|.
name|err
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PrintStream
name|createLoggingProxy
parameter_list|(
specifier|final
name|PrintStream
name|realStream
parameter_list|)
block|{
name|OutputStream
name|out
init|=
operator|new
name|OutputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|realStream
operator|.
name|write
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|String
operator|.
name|valueOf
argument_list|(
operator|(
name|char
operator|)
name|i
argument_list|)
decl_stmt|;
name|Launcher
operator|.
name|this
operator|.
name|setChanged
argument_list|()
expr_stmt|;
name|Launcher
operator|.
name|this
operator|.
name|notifyObservers
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|realStream
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|Launcher
operator|.
name|this
operator|.
name|setChanged
argument_list|()
expr_stmt|;
name|Launcher
operator|.
name|this
operator|.
name|notifyObservers
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|realStream
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|Launcher
operator|.
name|this
operator|.
name|setChanged
argument_list|()
expr_stmt|;
name|Launcher
operator|.
name|this
operator|.
name|notifyObservers
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
return|;
block|}
specifier|private
class|class
name|LogActionListener
implements|implements
name|ActionListener
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|actionEvent
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Desktop
operator|.
name|isDesktopSupported
argument_list|()
condition|)
return|return;
name|Desktop
name|desktop
init|=
name|Desktop
operator|.
name|getDesktop
argument_list|()
decl_stmt|;
name|File
name|home
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|logFile
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
literal|"webapp/WEB-INF/logs/exist.log"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|logFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Log file not found"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|ERROR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|desktop
operator|.
name|open
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|trayIcon
operator|.
name|displayMessage
argument_list|(
literal|null
argument_list|,
literal|"Failed to open log file"
argument_list|,
name|TrayIcon
operator|.
name|MessageType
operator|.
name|ERROR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

