begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012-2014 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  */
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
name|storage
operator|.
name|BrokerPool
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
name|javax
operator|.
name|swing
operator|.
name|border
operator|.
name|EmptyBorder
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
name|net
operator|.
name|URL
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|SystemProperties
import|;
end_import

begin_comment
comment|/**  * Display a splash screen showing the eXist-db logo and a status line.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|SplashScreen
extends|extends
name|JFrame
implements|implements
name|Observer
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8449133653386075548L
decl_stmt|;
specifier|private
name|JLabel
name|statusLabel
decl_stmt|;
specifier|private
name|JLabel
name|versionLabel
decl_stmt|;
specifier|private
name|Launcher
name|launcher
decl_stmt|;
specifier|public
name|SplashScreen
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
name|setUndecorated
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setBackground
argument_list|(
operator|new
name|Color
argument_list|(
literal|255
argument_list|,
literal|255
argument_list|,
literal|255
argument_list|,
literal|255
argument_list|)
argument_list|)
expr_stmt|;
name|setAlwaysOnTop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setDefaultCloseOperation
argument_list|(
name|DO_NOTHING_ON_CLOSE
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|setBackground
argument_list|(
operator|new
name|Color
argument_list|(
literal|255
argument_list|,
literal|255
argument_list|,
literal|255
argument_list|,
literal|255
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|URL
name|imageURL
init|=
name|SplashScreen
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"logo.png"
argument_list|)
decl_stmt|;
specifier|final
name|ImageIcon
name|icon
init|=
operator|new
name|ImageIcon
argument_list|(
name|imageURL
argument_list|,
literal|"eXist-db Logo"
argument_list|)
decl_stmt|;
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
comment|// add the image label
specifier|final
name|JLabel
name|imageLabel
init|=
operator|new
name|JLabel
argument_list|()
decl_stmt|;
name|imageLabel
operator|.
name|setIcon
argument_list|(
name|icon
argument_list|)
expr_stmt|;
specifier|final
name|EmptyBorder
name|border
init|=
operator|new
name|EmptyBorder
argument_list|(
literal|20
argument_list|,
literal|20
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|imageLabel
operator|.
name|setBorder
argument_list|(
name|border
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|imageLabel
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
comment|// version label
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Version "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|SystemProperties
operator|.
name|getInstance
argument_list|()
operator|.
name|getSystemProperty
argument_list|(
literal|"product-version"
argument_list|,
literal|"unknown"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|SystemProperties
operator|.
name|getInstance
argument_list|()
operator|.
name|getSystemProperty
argument_list|(
literal|"git-commit"
argument_list|,
literal|""
argument_list|)
argument_list|)
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|SystemProperties
operator|.
name|getInstance
argument_list|()
operator|.
name|getSystemProperty
argument_list|(
literal|"git-commit"
argument_list|,
literal|"(unknown Git commit ID)"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|versionLabel
operator|=
operator|new
name|JLabel
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|SwingConstants
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|versionLabel
operator|.
name|setFont
argument_list|(
operator|new
name|Font
argument_list|(
name|versionLabel
operator|.
name|getFont
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|Font
operator|.
name|BOLD
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|versionLabel
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|versionLabel
operator|.
name|setBorder
argument_list|(
operator|new
name|EmptyBorder
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|versionLabel
operator|.
name|setSize
argument_list|(
operator|new
name|Dimension
argument_list|(
name|icon
operator|.
name|getIconWidth
argument_list|()
argument_list|,
literal|60
argument_list|)
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|versionLabel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
comment|// message label
name|statusLabel
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Launching ..."
argument_list|,
name|SwingConstants
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setFont
argument_list|(
operator|new
name|Font
argument_list|(
name|statusLabel
operator|.
name|getFont
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|Font
operator|.
name|PLAIN
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setBorder
argument_list|(
operator|new
name|EmptyBorder
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setSize
argument_list|(
operator|new
name|Dimension
argument_list|(
name|icon
operator|.
name|getIconWidth
argument_list|()
argument_list|,
literal|60
argument_list|)
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|statusLabel
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
comment|// show it
name|setSize
argument_list|(
operator|new
name|Dimension
argument_list|(
name|icon
operator|.
name|getIconWidth
argument_list|()
operator|+
literal|40
argument_list|,
name|icon
operator|.
name|getIconHeight
argument_list|()
operator|+
literal|50
argument_list|)
argument_list|)
expr_stmt|;
comment|//pack();
name|this
operator|.
name|setLocationRelativeTo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setStatus
parameter_list|(
specifier|final
name|String
name|status
parameter_list|)
block|{
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
name|statusLabel
operator|.
name|setText
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
name|launcher
operator|.
name|signalStarted
argument_list|()
expr_stmt|;
name|setStatus
argument_list|(
literal|"Server started!"
argument_list|)
expr_stmt|;
name|setVisible
argument_list|(
literal|false
argument_list|)
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
name|SIGNAL_ABORTED
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|showMessageAndExit
argument_list|(
literal|"Startup aborted"
argument_list|,
literal|"eXist-db detected an error during recovery. This may not be fatal, "
operator|+
literal|"but to avoid possible damage, the db will now stop. Please consider "
operator|+
literal|"running a consistency check via the export tool and create "
operator|+
literal|"a backup if problems are reported. The db should come up again if you restart "
operator|+
literal|"it."
argument_list|,
literal|true
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
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|showMessageAndExit
argument_list|(
literal|"Error Occurred"
argument_list|,
literal|"An error occurred during startup. Please check the logs."
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|BrokerPool
operator|.
name|SIGNAL_SHUTDOWN
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|launcher
operator|.
name|signalShutdown
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|setStatus
argument_list|(
name|arg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

