begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Request Replayer  *  * Release under the BSD License  *  * Copyright (c) 2006, Adam retter<adam.retter@devon.gov.uk>  * All rights reserved.  *   * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:  * 		Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.  *  	Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.  *  	Neither the name of the Devon Portal Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.  *    *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS  *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE  *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR  *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR  *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,  *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;  *  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF  *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR  *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF  *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|requestlog
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|GridBagConstraints
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|GridBagLayout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Insets
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
name|WindowEvent
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
name|DataOutputStream
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JButton
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JFileChooser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JFrame
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JLabel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JPanel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTextArea
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTextField
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
name|TitledBorder
import|;
end_import

begin_comment
comment|/** Webapplication Descriptor  *   * Request Replayer Simple Swing GUI Application  * Opens request replay log's as generated by eXist's web-application Descriptor when enabled  * and can send the request's back to eXist.  * Useful for load testing and checking memory leaks.  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-02-28  * @version 1.6  */
end_comment

begin_class
specifier|public
class|class
name|RequestReplayer
extends|extends
name|JFrame
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/** Handle to the Request Log File */
specifier|private
name|File
name|requestLogFile
init|=
literal|null
decl_stmt|;
comment|//Dialog Controls
specifier|private
name|JTextField
name|txtReplayFilename
init|=
literal|null
decl_stmt|;
specifier|private
name|JLabel
name|lblRequestCount
init|=
literal|null
decl_stmt|;
specifier|private
name|JTextField
name|txtIterations
init|=
literal|null
decl_stmt|;
specifier|private
name|JTextField
name|txtAlternateHost
init|=
literal|null
decl_stmt|;
specifier|private
name|JButton
name|btnStart
init|=
literal|null
decl_stmt|;
specifier|private
name|JTextArea
name|txtStatus
init|=
literal|null
decl_stmt|;
comment|/** 	 * Entry point of the program 	 *  	 * @param args		array of parameters passed in from where the program is executed 	*/
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
name|String
name|fileName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
name|fileName
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
comment|//Instantiate oursel
comment|// RequestReplayer rr =
operator|new
name|RequestReplayer
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Default Constructor 	 * @param fileName  	 */
specifier|public
name|RequestReplayer
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
name|requestLogFile
operator|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * JDialog Window Event Handler 	 *  	 * @param e		The event 	 */
specifier|protected
name|void
name|processWindowEvent
parameter_list|(
name|WindowEvent
name|e
parameter_list|)
block|{
comment|//Close Window Event
if|if
condition|(
name|e
operator|.
name|getID
argument_list|()
operator|==
name|WindowEvent
operator|.
name|WINDOW_CLOSING
condition|)
block|{
name|this
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//why do we need this? shouldnt the above line do the job?
block|}
block|}
comment|/** 	 * Initalise Dialog 	 */
specifier|private
name|void
name|initialize
parameter_list|()
block|{
name|setupGUI
argument_list|()
expr_stmt|;
name|this
operator|.
name|setSize
argument_list|(
literal|600
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|this
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *Setup the Dialog's GUI  	 */
specifier|private
name|void
name|setupGUI
parameter_list|()
block|{
name|this
operator|.
name|setTitle
argument_list|(
literal|"eXist Request Replayer"
argument_list|)
expr_stmt|;
comment|//Dialog Content Panel
name|JPanel
name|cnt
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|GridBagLayout
name|grid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|cnt
operator|.
name|setLayout
argument_list|(
name|grid
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContentPane
argument_list|(
name|cnt
argument_list|)
expr_stmt|;
comment|//Constraints for layout
name|GridBagConstraints
name|c
init|=
operator|new
name|GridBagConstraints
argument_list|()
decl_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//Panel to hold controls relating to the request log file
name|JPanel
name|panelFile
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|panelFile
operator|.
name|setBorder
argument_list|(
operator|new
name|TitledBorder
argument_list|(
literal|"Request Log"
argument_list|)
argument_list|)
expr_stmt|;
name|GridBagLayout
name|panelFileGrid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|panelFile
operator|.
name|setLayout
argument_list|(
name|panelFileGrid
argument_list|)
expr_stmt|;
comment|//filename label
name|JLabel
name|lblLogFile
init|=
operator|new
name|JLabel
argument_list|(
literal|"Filename:"
argument_list|)
decl_stmt|;
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
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelFileGrid
operator|.
name|setConstraints
argument_list|(
name|lblLogFile
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelFile
operator|.
name|add
argument_list|(
name|lblLogFile
argument_list|)
expr_stmt|;
comment|// filename field
name|String
name|fileNameInfield
init|=
literal|"/usr/local/eXist/request-replay-log.txt"
decl_stmt|;
if|if
condition|(
name|requestLogFile
operator|!=
literal|null
condition|)
name|fileNameInfield
operator|=
name|requestLogFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|txtReplayFilename
operator|=
operator|new
name|JTextField
argument_list|(
name|fileNameInfield
argument_list|,
literal|24
argument_list|)
expr_stmt|;
name|txtReplayFilename
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|txtReplayFilename
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
name|requestLogFile
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
comment|//Show a dialog to choose the log file
name|chooseFile
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelFileGrid
operator|.
name|setConstraints
argument_list|(
name|txtReplayFilename
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelFile
operator|.
name|add
argument_list|(
name|txtReplayFilename
argument_list|)
expr_stmt|;
comment|//filename choose button
name|JButton
name|btnChooseFile
init|=
operator|new
name|JButton
argument_list|(
literal|"Choose..."
argument_list|)
decl_stmt|;
name|btnChooseFile
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
name|chooseFile
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelFileGrid
operator|.
name|setConstraints
argument_list|(
name|btnChooseFile
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelFile
operator|.
name|add
argument_list|(
name|btnChooseFile
argument_list|)
expr_stmt|;
comment|//Records count labels
name|JLabel
name|lblRequestCountText
init|=
operator|new
name|JLabel
argument_list|(
literal|"Request Count: "
argument_list|)
decl_stmt|;
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
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelFileGrid
operator|.
name|setConstraints
argument_list|(
name|lblRequestCountText
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelFile
operator|.
name|add
argument_list|(
name|lblRequestCountText
argument_list|)
expr_stmt|;
name|lblRequestCount
operator|=
operator|new
name|JLabel
argument_list|(
operator|new
name|Integer
argument_list|(
name|countRequestRecords
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelFileGrid
operator|.
name|setConstraints
argument_list|(
name|lblRequestCount
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelFile
operator|.
name|add
argument_list|(
name|lblRequestCount
argument_list|)
expr_stmt|;
comment|//Add the Request file panel to the main content
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
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|panelFile
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|cnt
operator|.
name|add
argument_list|(
name|panelFile
argument_list|)
expr_stmt|;
comment|//Panel to hold controls relating to the replaying of the requests
name|JPanel
name|panelReplay
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|panelReplay
operator|.
name|setBorder
argument_list|(
operator|new
name|TitledBorder
argument_list|(
literal|"Replay"
argument_list|)
argument_list|)
expr_stmt|;
name|GridBagLayout
name|panelReplayGrid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|panelReplay
operator|.
name|setLayout
argument_list|(
name|panelReplayGrid
argument_list|)
expr_stmt|;
comment|//Iterations Label
name|JLabel
name|lblIterations
init|=
operator|new
name|JLabel
argument_list|(
literal|"Iterations:"
argument_list|)
decl_stmt|;
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
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelReplayGrid
operator|.
name|setConstraints
argument_list|(
name|lblIterations
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelReplay
operator|.
name|add
argument_list|(
name|lblIterations
argument_list|)
expr_stmt|;
comment|//Iterations field
name|txtIterations
operator|=
operator|new
name|JTextField
argument_list|(
literal|"1"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelReplayGrid
operator|.
name|setConstraints
argument_list|(
name|txtIterations
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelReplay
operator|.
name|add
argument_list|(
name|txtIterations
argument_list|)
expr_stmt|;
comment|//Alternate Host Label
name|JLabel
name|lblAlternateHost
init|=
operator|new
name|JLabel
argument_list|(
literal|"Alternate Host:"
argument_list|)
decl_stmt|;
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
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelReplayGrid
operator|.
name|setConstraints
argument_list|(
name|lblAlternateHost
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelReplay
operator|.
name|add
argument_list|(
name|lblAlternateHost
argument_list|)
expr_stmt|;
comment|//Alternate Host Field
name|txtAlternateHost
operator|=
operator|new
name|JTextField
argument_list|(
literal|24
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelReplayGrid
operator|.
name|setConstraints
argument_list|(
name|txtAlternateHost
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelReplay
operator|.
name|add
argument_list|(
name|txtAlternateHost
argument_list|)
expr_stmt|;
comment|//Start Button
name|btnStart
operator|=
operator|new
name|JButton
argument_list|(
literal|"Start"
argument_list|)
expr_stmt|;
name|btnStart
operator|.
name|setMnemonic
argument_list|(
literal|'S'
argument_list|)
expr_stmt|;
name|btnStart
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|doReplay
argument_list|()
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//TODO: Could change the above method so iterations happen simultaneously in seperate threads!
comment|//Disable the Start Button (Only one Thread running at a time!)
name|btnStart
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
name|GridBagConstraints
operator|.
name|CENTER
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|CENTER
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|panelReplayGrid
operator|.
name|setConstraints
argument_list|(
name|btnStart
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelReplay
operator|.
name|add
argument_list|(
name|btnStart
argument_list|)
expr_stmt|;
comment|//Add the Replay panel to the main content
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
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|panelReplay
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|cnt
operator|.
name|add
argument_list|(
name|panelReplay
argument_list|)
expr_stmt|;
comment|//Panel to hold controls for status of replaying of the requests
name|JPanel
name|panelStatus
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|panelStatus
operator|.
name|setBorder
argument_list|(
operator|new
name|TitledBorder
argument_list|(
literal|"Status"
argument_list|)
argument_list|)
expr_stmt|;
name|GridBagLayout
name|panelStatusGrid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|panelStatus
operator|.
name|setLayout
argument_list|(
name|panelStatusGrid
argument_list|)
expr_stmt|;
comment|//Status text area with vertical scroll
name|txtStatus
operator|=
operator|new
name|JTextArea
argument_list|(
literal|10
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|txtStatus
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|JScrollPane
name|scrollStatus
init|=
operator|new
name|JScrollPane
argument_list|(
name|txtStatus
argument_list|,
name|JScrollPane
operator|.
name|VERTICAL_SCROLLBAR_ALWAYS
argument_list|,
name|JScrollPane
operator|.
name|HORIZONTAL_SCROLLBAR_NEVER
argument_list|)
decl_stmt|;
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
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|CENTER
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|panelStatusGrid
operator|.
name|setConstraints
argument_list|(
name|scrollStatus
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelStatus
operator|.
name|add
argument_list|(
name|scrollStatus
argument_list|)
expr_stmt|;
comment|//Add the Status panel to the main content
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
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|panelStatus
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|cnt
operator|.
name|add
argument_list|(
name|panelStatus
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Counts the number of request records in the request replay log file 	 *  	 *  @return		The number of request records in the request replay log file 	 */
specifier|private
name|int
name|countRequestRecords
parameter_list|()
block|{
comment|//Count of records
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|//if there is no file handle try and setup a handle for the user specified file
if|if
condition|(
name|requestLogFile
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|requestLogFile
operator|=
operator|new
name|File
argument_list|(
name|txtReplayFilename
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid path for Request file"
argument_list|)
expr_stmt|;
return|return
operator|(
literal|0
operator|)
return|;
block|}
block|}
comment|//Iterate through the file, incrementing the count for each record found
comment|//records start with a line starting with "Date: "
try|try
block|{
name|BufferedReader
name|bufRead
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|requestLogFile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|bufRead
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|"Date:"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|line
operator|=
name|bufRead
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|bufRead
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Request file not found"
argument_list|)
expr_stmt|;
return|return
operator|(
literal|0
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An I/O Exception occured whilst reading the Request file"
argument_list|)
expr_stmt|;
return|return
operator|(
name|count
operator|)
return|;
block|}
return|return
operator|(
name|count
operator|)
return|;
block|}
comment|/** 	 * Event for when the "Choose..." button is clicked, displays a simple 	 * file chooser dialog  	 * @param   	 */
specifier|private
name|void
name|chooseFile
parameter_list|()
block|{
name|JFileChooser
name|fileChooser
init|=
operator|new
name|JFileChooser
argument_list|()
decl_stmt|;
name|fileChooser
operator|.
name|setApproveButtonText
argument_list|(
literal|"Open"
argument_list|)
expr_stmt|;
name|fileChooser
operator|.
name|setApproveButtonMnemonic
argument_list|(
literal|'O'
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestLogFile
operator|!=
literal|null
condition|)
block|{
name|fileChooser
operator|.
name|setCurrentDirectory
argument_list|(
name|requestLogFile
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|fileChooser
operator|.
name|ensureFileIsVisible
argument_list|(
name|requestLogFile
argument_list|)
expr_stmt|;
block|}
name|int
name|retval
init|=
name|fileChooser
operator|.
name|showDialog
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|retval
operator|==
name|JFileChooser
operator|.
name|APPROVE_OPTION
condition|)
block|{
name|requestLogFile
operator|=
name|fileChooser
operator|.
name|getSelectedFile
argument_list|()
expr_stmt|;
name|txtReplayFilename
operator|.
name|setText
argument_list|(
name|requestLogFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|lblRequestCount
operator|.
name|setText
argument_list|(
operator|new
name|Integer
argument_list|(
name|countRequestRecords
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Function that takes each request from the log file 	 * and sends it back to the server  	 */
specifier|private
name|void
name|doReplay
parameter_list|()
block|{
name|RandomAccessFile
name|raFile
init|=
literal|null
decl_stmt|;
comment|//Random Access to Log File
name|String
name|line
init|=
literal|null
decl_stmt|;
comment|//Holds a single line from the File
name|long
name|offset
init|=
literal|0
decl_stmt|;
comment|//Offset used for moving back up the file (for preserving carriage returns in a POST request)
comment|//clear the status
name|txtStatus
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|txtStatus
operator|.
name|setCaretPosition
argument_list|(
name|txtStatus
operator|.
name|getDocument
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|//repeat as specified by the user in the iteration text field
name|int
name|iterations
init|=
operator|new
name|Integer
argument_list|(
name|txtIterations
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
comment|//try and open the file
name|raFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|requestLogFile
argument_list|,
literal|"r"
argument_list|)
expr_stmt|;
comment|//loop through the file line by line
while|while
condition|(
operator|(
name|line
operator|=
name|raFile
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|/* 					 * Each Request record start's with a line in the file starting "Date: " 					 * and each record end's with two empty lines in the file (two carriage returns). 					 * */
comment|//Is this the start of a record
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|"Date:"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
comment|//Yes, process the Record
name|StringBuffer
name|bufRequest
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
comment|//buffer to hold each request record from the file
name|String
name|server
init|=
literal|null
decl_stmt|;
comment|//server to send the request to
name|int
name|port
init|=
literal|80
decl_stmt|;
comment|//server port to send the request to
comment|//Update Status for user
name|txtStatus
operator|.
name|append
argument_list|(
literal|"Iteration: "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|", Sending Request from "
operator|+
name|line
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|txtStatus
operator|.
name|setCaretPosition
argument_list|(
name|txtStatus
operator|.
name|getDocument
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|//Loop through each line of the file that is part of this record
while|while
condition|(
operator|(
name|line
operator|=
name|raFile
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|//is this an empty line (i.e. carriage return)
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|//NO, not an empty line (i.e. carriage return)
comment|//Store the line in the buffer
name|bufRequest
operator|.
name|append
argument_list|(
name|line
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
comment|//host for request
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|"Host:"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
comment|//has the user specified an alternate host?
if|if
condition|(
name|txtAlternateHost
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//get host from request
name|String
name|host
init|=
name|line
operator|.
name|substring
argument_list|(
operator|new
name|String
argument_list|(
literal|"Host: "
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|server
operator|=
name|host
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|host
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
argument_list|)
expr_stmt|;
name|port
operator|=
operator|new
name|Integer
argument_list|(
name|host
operator|.
name|substring
argument_list|(
name|host
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//get user specified alternate host
name|server
operator|=
name|txtAlternateHost
operator|.
name|getText
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|txtAlternateHost
operator|.
name|getText
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
argument_list|)
expr_stmt|;
name|port
operator|=
operator|new
name|Integer
argument_list|(
name|txtAlternateHost
operator|.
name|getText
argument_list|()
operator|.
name|substring
argument_list|(
name|txtAlternateHost
operator|.
name|getText
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|//YES, an empty line (i.e. carriage return)
name|offset
operator|=
name|raFile
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|//get the position in case this isnt the end of record indicator, then we can roll back
comment|//we have had an empty line, is it followed by another empty line?
comment|//if so this indicates the end of this record, so break out of the inner loop and read the next record
if|if
condition|(
name|raFile
operator|.
name|readLine
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|raFile
operator|.
name|length
argument_list|()
operator|==
name|offset
condition|)
block|{
comment|//do request
try|try
block|{
comment|//Connect a socket to the server
name|Socket
name|socReq
init|=
operator|new
name|Socket
argument_list|(
name|server
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|OutputStream
name|socReqOut
init|=
name|socReq
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|socReqOut
argument_list|)
decl_stmt|;
comment|//Write Request to the socket
name|os
operator|.
name|writeBytes
argument_list|(
name|bufRequest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|socReqOut
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//Close the socket
name|socReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An I/O Exception occured whilst writting a Request to the server"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// wait 200 milliseconds before sending next request
try|try
block|{
name|wait
argument_list|(
literal|200
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
block|}
comment|//break out of this inner while loop, i.e. next record
break|break;
block|}
else|else
block|{
comment|//wasnt the end of record marker so reset file position
name|raFile
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
comment|//Also Store the carriage return that we checked for in the buffer,
comment|//as this is part of the Request data not the end of file marker
name|bufRequest
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|//close the file
name|raFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An I/O Exception occured whilst reading the Request file"
argument_list|)
expr_stmt|;
comment|//We have errored so enable the start button so the user can do it again if they want to!
name|btnStart
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|//We have finished so enable the start button so the user can do it again if they want to!
name|btnStart
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

