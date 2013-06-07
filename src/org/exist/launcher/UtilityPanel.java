begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2012 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|repo
operator|.
name|ExistRepository
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
name|ItemEvent
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
name|ItemListener
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
name|IOException
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

begin_class
specifier|public
class|class
name|UtilityPanel
extends|extends
name|JFrame
implements|implements
name|Observer
block|{
specifier|private
name|TextArea
name|messages
decl_stmt|;
specifier|private
name|JLabel
name|statusLabel
decl_stmt|;
specifier|private
name|JButton
name|dashboardButton
decl_stmt|;
specifier|private
name|JButton
name|eXideButton
decl_stmt|;
specifier|public
name|UtilityPanel
parameter_list|(
specifier|final
name|Launcher
name|launcher
parameter_list|,
name|boolean
name|hideOnStart
parameter_list|)
block|{
name|this
operator|.
name|setAlwaysOnTop
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
specifier|final
name|IOException
name|e
parameter_list|)
block|{
block|}
name|this
operator|.
name|setIconImage
argument_list|(
name|image
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|launcher
operator|.
name|isSystemTraySupported
argument_list|()
condition|)
block|{
name|setDefaultCloseOperation
argument_list|(
name|JFrame
operator|.
name|DO_NOTHING_ON_CLOSE
argument_list|)
expr_stmt|;
block|}
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
operator|new
name|GridBagLayout
argument_list|()
argument_list|)
expr_stmt|;
name|GridBagConstraints
name|c
init|=
operator|new
name|GridBagConstraints
argument_list|()
decl_stmt|;
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
name|JToolBar
name|toolbar
init|=
operator|new
name|JToolBar
argument_list|()
decl_stmt|;
name|toolbar
operator|.
name|setOpaque
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|toolbar
operator|.
name|setBorderPainted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//toolbar.setBackground(new Color(255, 255, 255, 255));
name|JButton
name|button
decl_stmt|;
if|if
condition|(
name|Desktop
operator|.
name|isDesktopSupported
argument_list|()
condition|)
block|{
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
name|dashboardButton
operator|=
name|createButton
argument_list|(
name|toolbar
argument_list|,
literal|"dashboard.png"
argument_list|,
literal|"Dashboard"
argument_list|)
expr_stmt|;
name|dashboardButton
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dashboardButton
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
name|launcher
operator|.
name|dashboard
argument_list|(
name|desktop
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|toolbar
operator|.
name|add
argument_list|(
name|dashboardButton
argument_list|)
expr_stmt|;
name|eXideButton
operator|=
name|createButton
argument_list|(
name|toolbar
argument_list|,
literal|"exide.png"
argument_list|,
literal|"eXide"
argument_list|)
expr_stmt|;
name|eXideButton
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|eXideButton
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
name|launcher
operator|.
name|eXide
argument_list|(
name|desktop
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|toolbar
operator|.
name|add
argument_list|(
name|eXideButton
argument_list|)
expr_stmt|;
block|}
block|}
name|button
operator|=
name|createButton
argument_list|(
name|toolbar
argument_list|,
literal|"browsing.png"
argument_list|,
literal|"Java Client"
argument_list|)
expr_stmt|;
name|button
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
name|launcher
operator|.
name|client
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|toolbar
operator|.
name|add
argument_list|(
name|button
argument_list|)
expr_stmt|;
name|button
operator|=
name|createButton
argument_list|(
name|toolbar
argument_list|,
literal|"shutdown.png"
argument_list|,
literal|"Shut Down"
argument_list|)
expr_stmt|;
name|button
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
name|launcher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|toolbar
operator|.
name|add
argument_list|(
name|button
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1.0
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|toolbar
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|statusLabel
operator|=
operator|new
name|JLabel
argument_list|(
literal|""
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
literal|"Dialog"
argument_list|,
name|Font
operator|.
name|PLAIN
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|200
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
comment|//statusLabel.setMinimumSize(new Dimension(200, 16));
if|if
condition|(
operator|!
name|launcher
operator|.
name|isSystemTraySupported
argument_list|()
condition|)
block|{
name|statusLabel
operator|.
name|setText
argument_list|(
literal|"System tray icon not supported."
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|gridy
operator|=
literal|1
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|statusLabel
argument_list|,
name|c
argument_list|)
expr_stmt|;
specifier|final
name|JCheckBox
name|showMessages
init|=
operator|new
name|JCheckBox
argument_list|(
literal|"Show console messages"
argument_list|)
decl_stmt|;
name|showMessages
operator|.
name|setHorizontalAlignment
argument_list|(
name|SwingConstants
operator|.
name|LEFT
argument_list|)
expr_stmt|;
name|showMessages
operator|.
name|setOpaque
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|showMessages
operator|.
name|addItemListener
argument_list|(
operator|new
name|ItemListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|itemStateChanged
parameter_list|(
name|ItemEvent
name|itemEvent
parameter_list|)
block|{
specifier|final
name|boolean
name|showMessages
init|=
name|itemEvent
operator|.
name|getStateChange
argument_list|()
operator|==
name|ItemEvent
operator|.
name|SELECTED
decl_stmt|;
if|if
condition|(
name|showMessages
condition|)
block|{
name|messages
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messages
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|UtilityPanel
operator|.
name|this
operator|.
name|pack
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|2
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|showMessages
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Font
name|messagesFont
init|=
operator|new
name|Font
argument_list|(
literal|"Monospaced"
argument_list|,
name|Font
operator|.
name|PLAIN
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|messages
operator|=
operator|new
name|TextArea
argument_list|()
expr_stmt|;
name|messages
operator|.
name|setBackground
argument_list|(
operator|new
name|Color
argument_list|(
literal|20
argument_list|,
literal|20
argument_list|,
literal|20
argument_list|,
literal|255
argument_list|)
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|800
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setForeground
argument_list|(
operator|new
name|Color
argument_list|(
literal|255
argument_list|,
literal|255
argument_list|,
literal|255
argument_list|)
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setFont
argument_list|(
name|messagesFont
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|3
expr_stmt|;
name|c
operator|.
name|weighty
operator|=
literal|1.0
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|BOTH
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|messages
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setMinimumSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|350
argument_list|,
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
specifier|final
name|Dimension
name|d
init|=
name|Toolkit
operator|.
name|getDefaultToolkit
argument_list|()
operator|.
name|getScreenSize
argument_list|()
decl_stmt|;
name|this
operator|.
name|setLocation
argument_list|(
name|d
operator|.
name|width
operator|-
name|this
operator|.
name|getWidth
argument_list|()
operator|-
literal|40
argument_list|,
literal|60
argument_list|)
expr_stmt|;
name|launcher
operator|.
name|addObserver
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hideOnStart
condition|)
block|{
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|toFront
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|JButton
name|createButton
parameter_list|(
name|JToolBar
name|toolbar
parameter_list|,
name|String
name|image
parameter_list|,
name|String
name|title
parameter_list|)
block|{
specifier|final
name|URL
name|imageURL
init|=
name|UtilityPanel
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|image
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
name|title
argument_list|)
decl_stmt|;
specifier|final
name|JButton
name|button
init|=
operator|new
name|JButton
argument_list|(
name|title
argument_list|,
name|icon
argument_list|)
decl_stmt|;
name|button
operator|.
name|setBorderPainted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|button
operator|.
name|setContentAreaFilled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|button
operator|.
name|setFocusPainted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|button
operator|.
name|setOpaque
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|button
operator|.
name|setHorizontalTextPosition
argument_list|(
name|SwingConstants
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|button
operator|.
name|setVerticalTextPosition
argument_list|(
name|SwingConstants
operator|.
name|BOTTOM
argument_list|)
expr_stmt|;
comment|//button.setBorder(new EmptyBorder(10, 10, 10, 10));
comment|//button.setBackground(new Color(255, 255, 255, 0));
return|return
name|button
return|;
block|}
specifier|protected
name|void
name|setStatus
parameter_list|(
specifier|final
name|String
name|message
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
name|message
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|Observable
name|observable
parameter_list|,
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|ExistRepository
operator|.
name|Notification
condition|)
block|{
specifier|final
name|ExistRepository
operator|.
name|Notification
name|notification
init|=
operator|(
name|ExistRepository
operator|.
name|Notification
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|notification
operator|.
name|getPackageURI
argument_list|()
operator|.
name|equals
argument_list|(
name|Launcher
operator|.
name|PACKAGE_DASHBOARD
argument_list|)
operator|&&
name|dashboardButton
operator|!=
literal|null
condition|)
block|{
name|dashboardButton
operator|.
name|setEnabled
argument_list|(
name|notification
operator|.
name|getAction
argument_list|()
operator|==
name|ExistRepository
operator|.
name|Action
operator|.
name|INSTALL
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|notification
operator|.
name|getPackageURI
argument_list|()
operator|.
name|equals
argument_list|(
name|Launcher
operator|.
name|PACKAGE_EXIDE
argument_list|)
operator|&&
name|eXideButton
operator|!=
literal|null
condition|)
block|{
name|eXideButton
operator|.
name|setEnabled
argument_list|(
name|notification
operator|.
name|getAction
argument_list|()
operator|==
name|ExistRepository
operator|.
name|Action
operator|.
name|INSTALL
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|messages
operator|.
name|append
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

