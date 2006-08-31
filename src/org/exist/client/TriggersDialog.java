begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  *  * Copyright (C) 2001-06 Wolfgang M. Meier wolfgang@exist-db.org  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  * $Id:$  */
end_comment

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
name|Component
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Dimension
import|;
end_import

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
name|awt
operator|.
name|event
operator|.
name|WindowListener
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
name|javax
operator|.
name|swing
operator|.
name|Box
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|DefaultCellEditor
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
name|JCheckBox
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JComboBox
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
name|JOptionPane
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
name|JTable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ListSelectionModel
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

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|AbstractTableModel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|JTableHeader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|TableCellRenderer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|TableColumn
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
name|DBBroker
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
name|XmldbURI
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
name|Collection
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
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * Dialog for viewing and editing Triggers in the Admin Client   *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-08-25  * @version 1.0  */
end_comment

begin_class
class|class
name|TriggersDialog
extends|extends
name|JFrame
block|{
specifier|private
name|CollectionXConf
name|cx
init|=
literal|null
decl_stmt|;
specifier|private
name|JComboBox
name|cmbCollections
decl_stmt|;
specifier|private
name|JTable
name|tblTriggers
decl_stmt|;
specifier|private
name|TriggersTableModel
name|triggersModel
decl_stmt|;
specifier|private
name|TableColumn
name|colStoreDocument
decl_stmt|;
specifier|private
name|TableColumn
name|colUpdateDocument
decl_stmt|;
specifier|private
name|TableColumn
name|colRemoveDocument
decl_stmt|;
specifier|private
name|TableColumn
name|colCreateCollection
decl_stmt|;
specifier|private
name|TableColumn
name|colRenameCollection
decl_stmt|;
specifier|private
name|TableColumn
name|colDeleteCollection
decl_stmt|;
specifier|private
name|InteractiveClient
name|client
decl_stmt|;
specifier|public
name|TriggersDialog
parameter_list|(
name|String
name|title
parameter_list|,
name|InteractiveClient
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|500
argument_list|,
literal|300
argument_list|)
argument_list|)
expr_stmt|;
comment|//capture the frame's close event
name|WindowListener
name|windowListener
init|=
operator|new
name|WindowAdapter
argument_list|()
block|{
specifier|public
name|void
name|windowClosing
parameter_list|(
name|WindowEvent
name|e
parameter_list|)
block|{
name|saveChanges
argument_list|()
expr_stmt|;
name|TriggersDialog
operator|.
name|this
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TriggersDialog
operator|.
name|this
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|this
operator|.
name|addWindowListener
argument_list|(
name|windowListener
argument_list|)
expr_stmt|;
comment|//draw the GUI
name|setupComponents
argument_list|()
expr_stmt|;
name|JTableHeader
name|tblTriggersHeader
init|=
name|tblTriggers
operator|.
name|getTableHeader
argument_list|()
decl_stmt|;
name|tblTriggersHeader
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
name|tblTriggersHeader
operator|.
name|getWidth
argument_list|()
argument_list|,
name|tblTriggersHeader
operator|.
name|getHeight
argument_list|()
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|//Get the indexes for the root collection
name|actionGetTriggers
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupComponents
parameter_list|()
block|{
comment|//Dialog Content Panel
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
comment|//Constraints for Layout
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
comment|//collection label
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
literal|"Collection"
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
comment|//get the collections but not system collections
name|ArrayList
name|alCollections
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|Collection
name|root
init|=
name|client
operator|.
name|getCollection
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
decl_stmt|;
name|ArrayList
name|alAllCollections
init|=
name|getCollections
argument_list|(
name|root
argument_list|,
operator|new
name|ArrayList
argument_list|()
argument_list|)
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
name|alAllCollections
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|alAllCollections
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
name|DBBroker
operator|.
name|CONFIG_COLLECTION
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|alCollections
operator|.
name|add
argument_list|(
name|alAllCollections
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
comment|//showErrorMessage(e.getMessage(), e);
return|return;
block|}
comment|//Create a combobox listing the collections
name|cmbCollections
operator|=
operator|new
name|JComboBox
argument_list|(
name|alCollections
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
name|cmbCollections
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
name|saveChanges
argument_list|()
expr_stmt|;
name|JComboBox
name|cb
init|=
operator|(
name|JComboBox
operator|)
name|e
operator|.
name|getSource
argument_list|()
decl_stmt|;
name|actionGetTriggers
argument_list|(
name|cb
operator|.
name|getSelectedItem
argument_list|()
operator|.
name|toString
argument_list|()
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
name|grid
operator|.
name|setConstraints
argument_list|(
name|cmbCollections
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|cmbCollections
argument_list|)
expr_stmt|;
comment|//Panel to hold controls relating to the Triggers Index
name|JPanel
name|panelTriggers
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|panelTriggers
operator|.
name|setBorder
argument_list|(
operator|new
name|TitledBorder
argument_list|(
literal|"Triggers"
argument_list|)
argument_list|)
expr_stmt|;
name|GridBagLayout
name|panelTriggersGrid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|panelTriggers
operator|.
name|setLayout
argument_list|(
name|panelTriggersGrid
argument_list|)
expr_stmt|;
comment|//Table to hold the Triggers with Sroll bar
name|triggersModel
operator|=
operator|new
name|TriggersTableModel
argument_list|()
expr_stmt|;
name|tblTriggers
operator|=
operator|new
name|JTable
argument_list|(
name|triggersModel
argument_list|)
expr_stmt|;
name|tblTriggers
operator|.
name|setAutoResizeMode
argument_list|(
name|JTable
operator|.
name|AUTO_RESIZE_NEXT_COLUMN
argument_list|)
expr_stmt|;
name|tblTriggers
operator|.
name|setSelectionMode
argument_list|(
name|ListSelectionModel
operator|.
name|SINGLE_SELECTION
argument_list|)
expr_stmt|;
name|colStoreDocument
operator|=
name|tblTriggers
operator|.
name|getColumnModel
argument_list|()
operator|.
name|getColumn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|colStoreDocument
operator|.
name|setCellEditor
argument_list|(
operator|new
name|CheckBoxCellEditor
argument_list|()
argument_list|)
expr_stmt|;
name|colStoreDocument
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CheckBoxCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|colUpdateDocument
operator|=
name|tblTriggers
operator|.
name|getColumnModel
argument_list|()
operator|.
name|getColumn
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|colUpdateDocument
operator|.
name|setCellEditor
argument_list|(
operator|new
name|CheckBoxCellEditor
argument_list|()
argument_list|)
expr_stmt|;
name|colUpdateDocument
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CheckBoxCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|colRemoveDocument
operator|=
name|tblTriggers
operator|.
name|getColumnModel
argument_list|()
operator|.
name|getColumn
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|colRemoveDocument
operator|.
name|setCellEditor
argument_list|(
operator|new
name|CheckBoxCellEditor
argument_list|()
argument_list|)
expr_stmt|;
name|colRemoveDocument
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CheckBoxCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|colCreateCollection
operator|=
name|tblTriggers
operator|.
name|getColumnModel
argument_list|()
operator|.
name|getColumn
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|colCreateCollection
operator|.
name|setCellEditor
argument_list|(
operator|new
name|CheckBoxCellEditor
argument_list|()
argument_list|)
expr_stmt|;
name|colCreateCollection
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CheckBoxCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|colRenameCollection
operator|=
name|tblTriggers
operator|.
name|getColumnModel
argument_list|()
operator|.
name|getColumn
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|colRenameCollection
operator|.
name|setCellEditor
argument_list|(
operator|new
name|CheckBoxCellEditor
argument_list|()
argument_list|)
expr_stmt|;
name|colRenameCollection
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CheckBoxCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|colDeleteCollection
operator|=
name|tblTriggers
operator|.
name|getColumnModel
argument_list|()
operator|.
name|getColumn
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|colDeleteCollection
operator|.
name|setCellEditor
argument_list|(
operator|new
name|CheckBoxCellEditor
argument_list|()
argument_list|)
expr_stmt|;
name|colDeleteCollection
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CheckBoxCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|JScrollPane
name|scrollFullTextIndexes
init|=
operator|new
name|JScrollPane
argument_list|(
name|tblTriggers
argument_list|)
decl_stmt|;
name|scrollFullTextIndexes
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|350
argument_list|,
literal|100
argument_list|)
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
name|CENTER
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|BOTH
expr_stmt|;
name|panelTriggersGrid
operator|.
name|setConstraints
argument_list|(
name|scrollFullTextIndexes
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelTriggers
operator|.
name|add
argument_list|(
name|scrollFullTextIndexes
argument_list|)
expr_stmt|;
comment|//Toolbar with add/delete buttons for Triggers
name|Box
name|triggersToolbarBox
init|=
name|Box
operator|.
name|createHorizontalBox
argument_list|()
decl_stmt|;
comment|//add button
name|JButton
name|btnAddTrigger
init|=
operator|new
name|JButton
argument_list|(
literal|"Add"
argument_list|)
decl_stmt|;
name|btnAddTrigger
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
name|actionAddTrigger
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|triggersToolbarBox
operator|.
name|add
argument_list|(
name|btnAddTrigger
argument_list|)
expr_stmt|;
comment|//delete button
name|JButton
name|btnDeleteTrigger
init|=
operator|new
name|JButton
argument_list|(
literal|"Delete"
argument_list|)
decl_stmt|;
name|btnDeleteTrigger
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
name|actionDeleteTrigger
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|triggersToolbarBox
operator|.
name|add
argument_list|(
name|btnDeleteTrigger
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
literal|4
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
name|CENTER
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|BOTH
expr_stmt|;
name|panelTriggersGrid
operator|.
name|setConstraints
argument_list|(
name|triggersToolbarBox
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|panelTriggers
operator|.
name|add
argument_list|(
name|triggersToolbarBox
argument_list|)
expr_stmt|;
comment|//add triggers panel to content frame
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
name|gridwidth
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
name|BOTH
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|panelTriggers
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|panelTriggers
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
block|}
comment|//if changes have been made, allows the user to save them
specifier|private
name|void
name|saveChanges
parameter_list|()
block|{
comment|//the collection has been changed
if|if
condition|(
name|cx
operator|.
name|hasChanged
argument_list|()
condition|)
block|{
comment|//ask the user if they would like to save the changes
name|int
name|result
init|=
name|JOptionPane
operator|.
name|showConfirmDialog
argument_list|(
name|getContentPane
argument_list|()
argument_list|,
literal|"The configuration for the collection has changed, would you like to save the changes?"
argument_list|,
literal|"Save Changes"
argument_list|,
name|JOptionPane
operator|.
name|YES_NO_OPTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|JOptionPane
operator|.
name|YES_OPTION
condition|)
block|{
comment|//save the collection.xconf changes
if|if
condition|(
name|cx
operator|.
name|Save
argument_list|()
condition|)
block|{
comment|//save ok
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|getContentPane
argument_list|()
argument_list|,
literal|"Your changes have been saved."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//save failed
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|getContentPane
argument_list|()
argument_list|,
literal|"Unable to save changes!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//THIS IS A COPY FROM ClientFrame
comment|//TODO: share this code between the two classes
specifier|private
name|ArrayList
name|getCollections
parameter_list|(
name|Collection
name|root
parameter_list|,
name|ArrayList
name|collectionsList
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collectionsList
operator|.
name|add
argument_list|(
operator|new
name|PrettyXmldbURI
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|root
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|childCollections
init|=
name|root
operator|.
name|listChildCollections
argument_list|()
decl_stmt|;
name|Collection
name|child
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
name|childCollections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|child
operator|=
name|root
operator|.
name|getChildCollection
argument_list|(
name|childCollections
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|getCollections
argument_list|(
name|child
argument_list|,
name|collectionsList
argument_list|)
expr_stmt|;
block|}
return|return
name|collectionsList
return|;
block|}
specifier|private
name|void
name|actionAddTrigger
parameter_list|()
block|{
name|triggersModel
operator|.
name|addRow
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|actionDeleteTrigger
parameter_list|()
block|{
name|int
name|iSelectedRow
init|=
name|tblTriggers
operator|.
name|getSelectedRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|iSelectedRow
operator|>
operator|-
literal|1
condition|)
block|{
name|triggersModel
operator|.
name|removeRow
argument_list|(
name|iSelectedRow
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Displays the indexes when a collection is selection
specifier|private
name|void
name|actionGetTriggers
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
try|try
block|{
name|cx
operator|=
operator|new
name|CollectionXConf
argument_list|(
name|collectionName
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|triggersModel
operator|.
name|fireTableDataChanged
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
comment|//TODO: CONSIDER whether CollectionXConf Should throw xmldb exception at all?
block|}
block|}
specifier|public
class|class
name|CheckBoxCellRenderer
extends|extends
name|JCheckBox
implements|implements
name|TableCellRenderer
block|{
specifier|public
name|CheckBoxCellRenderer
parameter_list|()
block|{
name|setHorizontalAlignment
argument_list|(
name|JLabel
operator|.
name|CENTER
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Component
name|getTableCellRendererComponent
parameter_list|(
name|JTable
name|table
parameter_list|,
name|Object
name|value
parameter_list|,
name|boolean
name|isSelected
parameter_list|,
name|boolean
name|hasFocus
parameter_list|,
name|int
name|row
parameter_list|,
name|int
name|column
parameter_list|)
block|{
if|if
condition|(
name|isSelected
condition|)
block|{
name|setForeground
argument_list|(
name|table
operator|.
name|getSelectionForeground
argument_list|()
argument_list|)
expr_stmt|;
comment|//super.setBackground(table.getSelectionBackground());
name|setBackground
argument_list|(
name|table
operator|.
name|getSelectionBackground
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setForeground
argument_list|(
name|table
operator|.
name|getForeground
argument_list|()
argument_list|)
expr_stmt|;
name|setBackground
argument_list|(
name|table
operator|.
name|getBackground
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Set the state
name|setSelected
argument_list|(
operator|(
name|value
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
operator|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
specifier|public
class|class
name|CheckBoxCellEditor
extends|extends
name|DefaultCellEditor
block|{
specifier|public
name|CheckBoxCellEditor
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|JCheckBox
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
class|class
name|TriggersTableModel
extends|extends
name|AbstractTableModel
block|{
specifier|private
specifier|final
name|String
index|[]
name|columnNames
init|=
operator|new
name|String
index|[]
block|{
literal|"class"
block|,
literal|"<html>Store<br>Document</html>"
block|,
literal|"<html>Update<br>Document</html>"
block|,
literal|"<html>Remove<br>Document</html>"
block|,
literal|"<html>Create<br>Collection</html>"
block|,
literal|"<html>Rename<br>Collection</html>"
block|,
literal|"<html>Delete<br>Collection</html>"
block|}
decl_stmt|;
specifier|public
name|TriggersTableModel
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|fireTableDataChanged
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		* @see javax.swing.table.TableModel#isCellEditable() 		*/
specifier|public
name|void
name|setValueAt
parameter_list|(
name|Object
name|aValue
parameter_list|,
name|int
name|rowIndex
parameter_list|,
name|int
name|columnIndex
parameter_list|)
block|{
name|String
name|triggerClass
init|=
literal|null
decl_stmt|;
name|boolean
name|STORE_DOCUMENT_EVENT
init|=
operator|(
operator|(
name|Boolean
operator|)
name|colStoreDocument
operator|.
name|getCellEditor
argument_list|()
operator|.
name|getCellEditorValue
argument_list|()
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|boolean
name|UPDATE_DOCUMENT_EVENT
init|=
operator|(
operator|(
name|Boolean
operator|)
name|colUpdateDocument
operator|.
name|getCellEditor
argument_list|()
operator|.
name|getCellEditorValue
argument_list|()
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|boolean
name|REMOVE_DOCUMENT_EVENT
init|=
operator|(
operator|(
name|Boolean
operator|)
name|colRemoveDocument
operator|.
name|getCellEditor
argument_list|()
operator|.
name|getCellEditorValue
argument_list|()
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|boolean
name|CREATE_COLLECTION_EVENT
init|=
operator|(
operator|(
name|Boolean
operator|)
name|colCreateCollection
operator|.
name|getCellEditor
argument_list|()
operator|.
name|getCellEditorValue
argument_list|()
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|boolean
name|RENAME_COLLECTION_EVENT
init|=
operator|(
operator|(
name|Boolean
operator|)
name|colRenameCollection
operator|.
name|getCellEditor
argument_list|()
operator|.
name|getCellEditorValue
argument_list|()
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|boolean
name|DELETE_COLLECTION_EVENT
init|=
operator|(
operator|(
name|Boolean
operator|)
name|colDeleteCollection
operator|.
name|getCellEditor
argument_list|()
operator|.
name|getCellEditorValue
argument_list|()
operator|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|columnIndex
operator|==
literal|0
condition|)
block|{
comment|//trigger class name has been updated
name|triggerClass
operator|=
operator|(
name|String
operator|)
name|aValue
expr_stmt|;
block|}
name|cx
operator|.
name|updateTrigger
argument_list|(
name|rowIndex
argument_list|,
name|triggerClass
argument_list|,
name|STORE_DOCUMENT_EVENT
argument_list|,
name|UPDATE_DOCUMENT_EVENT
argument_list|,
name|REMOVE_DOCUMENT_EVENT
argument_list|,
name|CREATE_COLLECTION_EVENT
argument_list|,
name|RENAME_COLLECTION_EVENT
argument_list|,
name|DELETE_COLLECTION_EVENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fireTableCellUpdated
argument_list|(
name|rowIndex
argument_list|,
name|columnIndex
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeRow
parameter_list|(
name|int
name|rowIndex
parameter_list|)
block|{
name|cx
operator|.
name|deleteTrigger
argument_list|(
name|rowIndex
argument_list|)
expr_stmt|;
name|fireTableRowsDeleted
argument_list|(
name|rowIndex
argument_list|,
name|rowIndex
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addRow
parameter_list|()
block|{
name|cx
operator|.
name|addTrigger
argument_list|(
literal|""
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fireTableRowsInserted
argument_list|(
name|getRowCount
argument_list|()
argument_list|,
name|getRowCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		* @see javax.swing.table.TableModel#isCellEditable() 		*/
specifier|public
name|boolean
name|isCellEditable
parameter_list|(
name|int
name|rowIndex
parameter_list|,
name|int
name|columnIndex
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 		* @see javax.swing.table.TableModel#getColumnCount() 		*/
specifier|public
name|int
name|getColumnCount
parameter_list|()
block|{
return|return
name|columnNames
operator|.
name|length
return|;
block|}
comment|/* (non-Javadoc) 		 * @see javax.swing.table.TableModel#getColumnName(int) 		 */
specifier|public
name|String
name|getColumnName
parameter_list|(
name|int
name|column
parameter_list|)
block|{
return|return
name|columnNames
index|[
name|column
index|]
return|;
block|}
comment|/* (non-Javadoc) 		 * @see javax.swing.table.TableModel#getRowCount() 		 */
specifier|public
name|int
name|getRowCount
parameter_list|()
block|{
return|return
name|cx
operator|!=
literal|null
condition|?
name|cx
operator|.
name|getTriggerCount
argument_list|()
else|:
literal|0
return|;
block|}
comment|/* (non-Javadoc) 		 * @see javax.swing.table.TableModel#getValueAt(int, int) 		 */
specifier|public
name|Object
name|getValueAt
parameter_list|(
name|int
name|rowIndex
parameter_list|,
name|int
name|columnIndex
parameter_list|)
block|{
switch|switch
condition|(
name|columnIndex
condition|)
block|{
comment|/* class */
case|case
literal|0
case|:
return|return
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getTriggerClass
argument_list|()
return|;
comment|/* events */
case|case
literal|1
case|:
comment|//store document
return|return
operator|new
name|Boolean
argument_list|(
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getStoreDocumentEvent
argument_list|()
argument_list|)
return|;
case|case
literal|2
case|:
comment|//update document
return|return
operator|new
name|Boolean
argument_list|(
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getUpdateDocumentEvent
argument_list|()
argument_list|)
return|;
case|case
literal|3
case|:
comment|//remove document
return|return
operator|new
name|Boolean
argument_list|(
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getRemoveDocumentEvent
argument_list|()
argument_list|)
return|;
case|case
literal|4
case|:
comment|//create collection
return|return
operator|new
name|Boolean
argument_list|(
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getCreateCollectionEvent
argument_list|()
argument_list|)
return|;
case|case
literal|5
case|:
comment|//rename collection
return|return
operator|new
name|Boolean
argument_list|(
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getRenameCollectionEvent
argument_list|()
argument_list|)
return|;
case|case
literal|6
case|:
comment|//delete collection
return|return
operator|new
name|Boolean
argument_list|(
name|cx
operator|.
name|getTrigger
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|getDeleteCollectionEvent
argument_list|()
argument_list|)
return|;
default|default :
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

