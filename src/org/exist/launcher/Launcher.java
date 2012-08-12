begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|awt
operator|.
name|event
operator|.
name|ActionEvent
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
name|ActionListener
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
name|WindowAdapter
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
name|WindowEvent
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observer
import|;
end_import

begin_comment
comment|/**  * A launcher for the eXist-db server integrated with the desktop.  * Shows a splash screen during startup and registers a tray icon  * in the system bar.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Launcher
implements|implements
name|Observer
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
name|Launcher
operator|.
name|class
argument_list|)
decl_stmt|;
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
comment|/* Turn off metal's use of bold fonts */
name|UIManager
operator|.
name|put
argument_list|(
literal|"swing.boldMetal"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
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
operator|!
name|SystemTray
operator|.
name|isSupported
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Not supported"
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|home
init|=
name|getJettyHome
argument_list|()
decl_stmt|;
name|captureConsole
argument_list|()
expr_stmt|;
name|splash
operator|=
operator|new
name|SplashScreen
argument_list|()
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
name|Launcher
operator|.
name|this
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
name|Launcher
operator|.
name|this
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
literal|"An error occurred during startup. Please check the logs."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Image
name|image
init|=
operator|new
name|ImageIcon
argument_list|(
literal|"icon.png"
argument_list|,
literal|"eXist-db Logo"
argument_list|)
operator|.
name|getImage
argument_list|()
decl_stmt|;
name|trayIcon
operator|=
operator|new
name|TrayIcon
argument_list|(
name|image
argument_list|)
expr_stmt|;
specifier|final
name|SystemTray
name|tray
init|=
name|SystemTray
operator|.
name|getSystemTray
argument_list|()
decl_stmt|;
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
literal|"Start"
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
name|Launcher
operator|.
name|this
argument_list|)
expr_stmt|;
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
literal|"Stop"
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
literal|"Open Browser"
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
literal|"/exist/"
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
literal|"Failed to open URL"
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
literal|"/exist/eXide/"
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
literal|"Failed to open URL"
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
literal|"exist.log"
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
literal|"Quit (stop db)"
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
name|jetty
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
name|trayIcon
operator|.
name|setPopupMenu
argument_list|(
name|popup
argument_list|)
expr_stmt|;
try|try
block|{
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Tray icon could not be added"
argument_list|)
expr_stmt|;
return|return;
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
name|splash
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|JettyStart
operator|.
name|SIGNAL_STARTED
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
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
name|splash
operator|.
name|setStatus
argument_list|(
literal|"Server started!"
argument_list|)
expr_stmt|;
name|splash
operator|.
name|setVisible
argument_list|(
literal|false
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
name|splash
operator|=
literal|null
expr_stmt|;
block|}
if|else if
condition|(
name|BrokerPool
operator|.
name|SIGNAL_STARTUP
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|splash
operator|.
name|setStatus
argument_list|(
literal|"Starting eXist-db ..."
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|BrokerPool
operator|.
name|SIGNAL_WRITABLE
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|splash
operator|.
name|setStatus
argument_list|(
literal|"eXist-db is up. Waiting for web server ..."
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|JettyStart
operator|.
name|SIGNAL_ERROR
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|splash
operator|.
name|setStatus
argument_list|(
literal|"An error occurred! Please check the logs."
argument_list|)
expr_stmt|;
name|showMessageAndExit
argument_list|(
literal|"Error Occurred"
argument_list|,
literal|"An error occurred during startup. Please check the logs."
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
specifier|private
name|void
name|showMessageAndExit
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|message
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
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
specifier|static
name|PrintStream
name|createLoggingProxy
parameter_list|(
specifier|final
name|PrintStream
name|realStream
parameter_list|)
block|{
return|return
operator|new
name|PrintStream
argument_list|(
name|realStream
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|print
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|realStream
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
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

