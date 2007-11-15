begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
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
name|javax
operator|.
name|swing
operator|.
name|BorderFactory
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
name|JProgressBar
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
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|ElementIndex
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
name|TextSearchEngine
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
name|ProgressIndicator
import|;
end_import

begin_class
class|class
name|UploadDialog
extends|extends
name|JFrame
block|{
name|JTextField
name|currentFile
decl_stmt|;
name|JTextField
name|currentDir
decl_stmt|;
name|JLabel
name|currentSize
decl_stmt|;
name|JTextArea
name|messages
decl_stmt|;
name|JProgressBar
name|progress
decl_stmt|;
name|JProgressBar
name|byDirProgress
decl_stmt|;
name|boolean
name|cancelled
init|=
literal|false
decl_stmt|;
specifier|final
name|JButton
name|closeBtn
decl_stmt|;
specifier|public
name|UploadDialog
parameter_list|()
block|{
name|super
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.0"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|GridBagLayout
name|grid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
name|grid
argument_list|)
expr_stmt|;
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
literal|5
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.1"
argument_list|)
argument_list|)
decl_stmt|;
comment|//$NON-NLS-1$
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
name|c
operator|.
name|weightx
operator|=
literal|0
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|byDirProgress
operator|=
operator|new
name|JProgressBar
argument_list|()
expr_stmt|;
name|byDirProgress
operator|.
name|setStringPainted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setString
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.2"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|byDirProgress
operator|.
name|setIndeterminate
argument_list|(
literal|true
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
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|byDirProgress
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|byDirProgress
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.3"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
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
name|c
operator|.
name|weightx
operator|=
literal|0
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|currentDir
operator|=
operator|new
name|JTextField
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|currentDir
operator|.
name|setEditable
argument_list|(
literal|false
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
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|currentDir
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|currentDir
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.4"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
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
name|c
operator|.
name|weightx
operator|=
literal|0
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|currentFile
operator|=
operator|new
name|JTextField
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|currentFile
operator|.
name|setEditable
argument_list|(
literal|false
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
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|currentFile
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|currentFile
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.5"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
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
literal|3
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
name|c
operator|.
name|weightx
operator|=
literal|0
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|currentSize
operator|=
operator|new
name|JLabel
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.6"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
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
literal|3
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|currentSize
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|currentSize
argument_list|)
expr_stmt|;
name|JLabel
name|status
init|=
operator|new
name|JLabel
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.7"
argument_list|)
argument_list|)
decl_stmt|;
comment|//$NON-NLS-1$
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
literal|4
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
name|c
operator|.
name|weightx
operator|=
literal|0
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|status
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|progress
operator|=
operator|new
name|JProgressBar
argument_list|()
expr_stmt|;
name|progress
operator|.
name|setIndeterminate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|progress
operator|.
name|setStringPainted
argument_list|(
literal|true
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
literal|4
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|progress
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|messages
operator|=
operator|new
name|JTextArea
argument_list|(
literal|5
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|messages
operator|.
name|setLineWrap
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JScrollPane
name|scroll
init|=
operator|new
name|JScrollPane
argument_list|(
name|messages
argument_list|,
name|JScrollPane
operator|.
name|VERTICAL_SCROLLBAR_ALWAYS
argument_list|,
name|JScrollPane
operator|.
name|HORIZONTAL_SCROLLBAR_AS_NEEDED
argument_list|)
decl_stmt|;
name|scroll
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createTitledBorder
argument_list|(
literal|"Messages"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
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
literal|5
expr_stmt|;
name|c
operator|.
name|gridwidth
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
name|BOTH
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|weighty
operator|=
literal|1
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|scroll
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|scroll
argument_list|)
expr_stmt|;
name|closeBtn
operator|=
operator|new
name|JButton
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.9"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|closeBtn
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
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.20"
argument_list|)
operator|.
name|equals
argument_list|(
name|closeBtn
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
comment|//$NON-NLS-1$
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
else|else
block|{
name|cancelled
operator|=
literal|true
expr_stmt|;
name|closeBtn
operator|.
name|setText
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.11"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
block|}
block|}
block|}
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
literal|6
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|c
operator|.
name|weightx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|weighty
operator|=
literal|0
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|closeBtn
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|closeBtn
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Observer
name|getObserver
parameter_list|()
block|{
return|return
operator|new
name|UploadProgressObserver
argument_list|()
return|;
block|}
specifier|public
name|void
name|setCurrent
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|currentFile
operator|.
name|setText
argument_list|(
name|label
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCurrentDir
parameter_list|(
name|String
name|dir
parameter_list|)
block|{
name|currentDir
operator|.
name|setText
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCurrentSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>=
literal|1024
condition|)
name|currentSize
operator|.
name|setText
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|size
operator|/
literal|1024
argument_list|)
operator|+
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.12"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
else|else
name|currentSize
operator|.
name|setText
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTotalSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|byDirProgress
operator|.
name|setIndeterminate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setString
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setMinimum
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setMaximum
argument_list|(
operator|(
name|int
operator|)
operator|(
name|size
operator|/
literal|1024
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setStoredSize
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|byDirProgress
operator|.
name|setValue
argument_list|(
operator|(
name|int
operator|)
operator|(
name|count
operator|/
literal|1024
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|cancelled
return|;
block|}
specifier|public
name|void
name|uploadCompleted
parameter_list|()
block|{
name|closeBtn
operator|.
name|setText
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.13"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|progress
operator|.
name|setIndeterminate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|progress
operator|.
name|setValue
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|progress
operator|.
name|setString
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.14"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|byDirProgress
operator|.
name|setIndeterminate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setString
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|byDirProgress
operator|.
name|setValue
argument_list|(
name|byDirProgress
operator|.
name|getMaximum
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|showMessage
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|messages
operator|.
name|append
argument_list|(
name|msg
operator|+
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.15"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|messages
operator|.
name|setCaretPosition
argument_list|(
name|messages
operator|.
name|getDocument
argument_list|()
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|progress
operator|.
name|setString
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.16"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
name|progress
operator|.
name|setIndeterminate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
class|class
name|UploadProgressObserver
implements|implements
name|Observer
block|{
name|int
name|mode
init|=
literal|0
decl_stmt|;
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
name|progress
operator|.
name|setIndeterminate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProgressIndicator
name|ind
init|=
operator|(
name|ProgressIndicator
operator|)
name|arg
decl_stmt|;
name|progress
operator|.
name|setValue
argument_list|(
name|ind
operator|.
name|getPercentage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|TextSearchEngine
condition|)
name|progress
operator|.
name|setString
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.17"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
if|else if
condition|(
name|o
operator|instanceof
name|ElementIndex
condition|)
name|progress
operator|.
name|setString
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.18"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
else|else
name|progress
operator|.
name|setString
argument_list|(
name|Messages
operator|.
name|getString
argument_list|(
literal|"UploadDialog.19"
argument_list|)
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
block|}
block|}
block|}
end_class

end_unit

