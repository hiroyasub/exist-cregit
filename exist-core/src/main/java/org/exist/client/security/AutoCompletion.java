begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Public Domain Code  * Taken from: http://www.orbital-computer.de/JComboBox/  * Original Author: Thomas Bierhance  *   * Accompanying statement from Thomas:  *   * This work is hereby released into the Public Domain.  * To view a copy of the public domain dedication, visit  * http://creativecommons.org/licenses/publicdomain/  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|security
package|;
end_package

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
name|beans
operator|.
name|PropertyChangeEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyChangeListener
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
name|text
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|AutoCompletion
extends|extends
name|PlainDocument
block|{
name|JComboBox
name|comboBox
decl_stmt|;
name|ComboBoxModel
name|model
decl_stmt|;
name|JTextComponent
name|editor
decl_stmt|;
comment|// flag to indicate if setSelectedItem has been called
comment|// subsequent calls to remove/insertString should be ignored
name|boolean
name|selecting
init|=
literal|false
decl_stmt|;
name|boolean
name|hidePopupOnFocusLoss
decl_stmt|;
name|boolean
name|hitBackspace
init|=
literal|false
decl_stmt|;
name|boolean
name|hitBackspaceOnSelection
decl_stmt|;
name|KeyListener
name|editorKeyListener
decl_stmt|;
name|FocusListener
name|editorFocusListener
decl_stmt|;
specifier|public
name|AutoCompletion
parameter_list|(
specifier|final
name|JComboBox
name|comboBox
parameter_list|)
block|{
name|this
operator|.
name|comboBox
operator|=
name|comboBox
expr_stmt|;
name|model
operator|=
name|comboBox
operator|.
name|getModel
argument_list|()
expr_stmt|;
name|comboBox
operator|.
name|addActionListener
argument_list|(
name|e
lambda|->
block|{
if|if
condition|(
operator|!
name|selecting
condition|)
block|{
name|highlightCompletedText
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|comboBox
operator|.
name|addPropertyChangeListener
argument_list|(
name|e
lambda|->
block|{
if|if
condition|(
literal|"editor"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getPropertyName
argument_list|()
argument_list|)
condition|)
block|{
name|configureEditor
argument_list|(
operator|(
name|ComboBoxEditor
operator|)
name|e
operator|.
name|getNewValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"model"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getPropertyName
argument_list|()
argument_list|)
condition|)
block|{
name|model
operator|=
operator|(
name|ComboBoxModel
operator|)
name|e
operator|.
name|getNewValue
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|editorKeyListener
operator|=
operator|new
name|KeyAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|keyPressed
parameter_list|(
name|KeyEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|comboBox
operator|.
name|isDisplayable
argument_list|()
condition|)
block|{
name|comboBox
operator|.
name|setPopupVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|hitBackspace
operator|=
literal|false
expr_stmt|;
switch|switch
condition|(
name|e
operator|.
name|getKeyCode
argument_list|()
condition|)
block|{
comment|// determine if the pressed key is backspace (needed by the remove method)
case|case
name|KeyEvent
operator|.
name|VK_BACK_SPACE
case|:
name|hitBackspace
operator|=
literal|true
expr_stmt|;
name|hitBackspaceOnSelection
operator|=
name|editor
operator|.
name|getSelectionStart
argument_list|()
operator|!=
name|editor
operator|.
name|getSelectionEnd
argument_list|()
expr_stmt|;
break|break;
comment|// ignore delete key
case|case
name|KeyEvent
operator|.
name|VK_DELETE
case|:
name|e
operator|.
name|consume
argument_list|()
expr_stmt|;
name|comboBox
operator|.
name|getToolkit
argument_list|()
operator|.
name|beep
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
expr_stmt|;
comment|// Bug 5100422 on Java 1.5: Editable JComboBox won't hide popup when tabbing out
name|hidePopupOnFocusLoss
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.5"
argument_list|)
expr_stmt|;
comment|// Highlight whole text when gaining focus
name|editorFocusListener
operator|=
operator|new
name|FocusAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|focusGained
parameter_list|(
name|FocusEvent
name|e
parameter_list|)
block|{
name|highlightCompletedText
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|focusLost
parameter_list|(
name|FocusEvent
name|e
parameter_list|)
block|{
comment|// Workaround for Bug 5100422 - Hide Popup on focus loss
if|if
condition|(
name|hidePopupOnFocusLoss
condition|)
block|{
name|comboBox
operator|.
name|setPopupVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|configureEditor
argument_list|(
name|comboBox
operator|.
name|getEditor
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handle initially selected object
specifier|final
name|Object
name|selected
init|=
name|comboBox
operator|.
name|getSelectedItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|selected
operator|!=
literal|null
condition|)
block|{
name|setText
argument_list|(
name|selected
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|highlightCompletedText
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|enable
parameter_list|(
name|JComboBox
name|comboBox
parameter_list|)
block|{
comment|// has to be editable
name|comboBox
operator|.
name|setEditable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// change the editor's document
operator|new
name|AutoCompletion
argument_list|(
name|comboBox
argument_list|)
expr_stmt|;
block|}
name|void
name|configureEditor
parameter_list|(
name|ComboBoxEditor
name|newEditor
parameter_list|)
block|{
if|if
condition|(
name|editor
operator|!=
literal|null
condition|)
block|{
name|editor
operator|.
name|removeKeyListener
argument_list|(
name|editorKeyListener
argument_list|)
expr_stmt|;
name|editor
operator|.
name|removeFocusListener
argument_list|(
name|editorFocusListener
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newEditor
operator|!=
literal|null
condition|)
block|{
name|editor
operator|=
operator|(
name|JTextComponent
operator|)
name|newEditor
operator|.
name|getEditorComponent
argument_list|()
expr_stmt|;
name|editor
operator|.
name|addKeyListener
argument_list|(
name|editorKeyListener
argument_list|)
expr_stmt|;
name|editor
operator|.
name|addFocusListener
argument_list|(
name|editorFocusListener
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setDocument
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|(
name|int
name|offs
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|BadLocationException
block|{
comment|// return immediately when selecting an item
if|if
condition|(
name|selecting
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|hitBackspace
condition|)
block|{
comment|// user hit backspace => move the selection backwards
comment|// old item keeps being selected
if|if
condition|(
name|offs
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|hitBackspaceOnSelection
condition|)
block|{
name|offs
operator|--
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// User hit backspace with the cursor positioned on the start => beep
name|comboBox
operator|.
name|getToolkit
argument_list|()
operator|.
name|beep
argument_list|()
expr_stmt|;
comment|// when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
block|}
name|highlightCompletedText
argument_list|(
name|offs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|remove
argument_list|(
name|offs
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|insertString
parameter_list|(
name|int
name|offs
parameter_list|,
name|String
name|str
parameter_list|,
name|AttributeSet
name|a
parameter_list|)
throws|throws
name|BadLocationException
block|{
comment|// return immediately when selecting an item
if|if
condition|(
name|selecting
condition|)
block|{
return|return;
block|}
comment|// insert the string into the document
name|super
operator|.
name|insertString
argument_list|(
name|offs
argument_list|,
name|str
argument_list|,
name|a
argument_list|)
expr_stmt|;
comment|// lookup and select a matching item
name|Object
name|item
init|=
name|lookupItem
argument_list|(
name|getText
argument_list|(
literal|0
argument_list|,
name|getLength
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|setSelectedItem
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// keep old item selected if there is no match
name|item
operator|=
name|comboBox
operator|.
name|getSelectedItem
argument_list|()
expr_stmt|;
comment|// imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
name|offs
operator|=
name|offs
operator|-
name|str
operator|.
name|length
argument_list|()
expr_stmt|;
comment|// provide feedback to the user that his input has been received but can not be accepted
name|comboBox
operator|.
name|getToolkit
argument_list|()
operator|.
name|beep
argument_list|()
expr_stmt|;
comment|// when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
block|}
name|setText
argument_list|(
name|item
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// select the completed part
name|highlightCompletedText
argument_list|(
name|offs
operator|+
name|str
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
try|try
block|{
comment|// remove all text and insert the completed string
name|super
operator|.
name|remove
argument_list|(
literal|0
argument_list|,
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|insertString
argument_list|(
literal|0
argument_list|,
name|text
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BadLocationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|highlightCompletedText
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|editor
operator|.
name|setCaretPosition
argument_list|(
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|moveCaretPosition
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setSelectedItem
parameter_list|(
name|Object
name|item
parameter_list|)
block|{
name|selecting
operator|=
literal|true
expr_stmt|;
name|model
operator|.
name|setSelectedItem
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|selecting
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|Object
name|lookupItem
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
specifier|final
name|Object
name|selectedItem
init|=
name|model
operator|.
name|getSelectedItem
argument_list|()
decl_stmt|;
comment|// only search for a different item if the currently selected does not match
if|if
condition|(
name|selectedItem
operator|!=
literal|null
operator|&&
name|startsWithIgnoreCase
argument_list|(
name|selectedItem
operator|.
name|toString
argument_list|()
argument_list|,
name|pattern
argument_list|)
condition|)
block|{
return|return
name|selectedItem
return|;
block|}
else|else
block|{
comment|// iterate over all items
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|n
init|=
name|model
operator|.
name|getSize
argument_list|()
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Object
name|currentItem
init|=
name|model
operator|.
name|getElementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// current item starts with the pattern?
if|if
condition|(
name|currentItem
operator|!=
literal|null
operator|&&
name|startsWithIgnoreCase
argument_list|(
name|currentItem
operator|.
name|toString
argument_list|()
argument_list|,
name|pattern
argument_list|)
condition|)
block|{
return|return
name|currentItem
return|;
block|}
block|}
block|}
comment|// no item starts with the pattern => return null
return|return
literal|null
return|;
block|}
comment|// checks if str1 starts with str2 - ignores case
specifier|private
name|boolean
name|startsWithIgnoreCase
parameter_list|(
name|String
name|str1
parameter_list|,
name|String
name|str2
parameter_list|)
block|{
return|return
name|str1
operator|.
name|toUpperCase
argument_list|()
operator|.
name|startsWith
argument_list|(
name|str2
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
comment|/*     private static void createAndShowGUI() {         // the combo box (add/modify items if you like to)         final JComboBox comboBox = new JComboBox(new Object[] {"Ester", "Jordi", "Jordina", "Jorge", "Sergi"});         enable(comboBox);          // create and show a window containing the combo box         final JFrame frame = new JFrame();         frame.setDefaultCloseOperation(3);         frame.getContentPane().add(comboBox);         frame.pack(); frame.setVisible(true);     }               public static void main(String[] args) {         javax.swing.SwingUtilities.invokeLater(new Runnable() {             public void run() {                 createAndShowGUI();             }         });     }     */
block|}
end_class

end_unit
