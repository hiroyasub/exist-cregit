begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|InputVerifier
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
name|table
operator|.
name|DefaultTableModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|DialogCompleteWithResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|HighlightedTableCellRenderer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|EXistSchemaType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Group
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|aider
operator|.
name|GroupAider
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
name|UserManagementService
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
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|GroupDialog
extends|extends
name|javax
operator|.
name|swing
operator|.
name|JFrame
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2291775874309563932L
decl_stmt|;
specifier|private
specifier|final
name|Pattern
name|PTN_GROUPNAME
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[a-zA-Z0-9\\-\\._@]{3,}"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UserManagementService
name|userManagementService
decl_stmt|;
specifier|private
specifier|final
name|String
name|currentUser
decl_stmt|;
specifier|private
name|DefaultTableModel
name|groupMembersTableModel
init|=
literal|null
decl_stmt|;
comment|/**      * Creates new form GroupDialog      */
specifier|public
name|GroupDialog
parameter_list|(
specifier|final
name|UserManagementService
name|userManagementService
parameter_list|,
specifier|final
name|String
name|currentUser
parameter_list|)
block|{
name|this
operator|.
name|userManagementService
operator|=
name|userManagementService
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|initComponents
argument_list|()
expr_stmt|;
name|tblGroupMembers
operator|.
name|setDefaultRenderer
argument_list|(
name|Object
operator|.
name|class
argument_list|,
operator|new
name|HighlightedTableCellRenderer
argument_list|()
argument_list|)
expr_stmt|;
name|addSelfAsManager
argument_list|()
expr_stmt|;
block|}
specifier|public
name|UserManagementService
name|getUserManagementService
parameter_list|()
block|{
return|return
name|userManagementService
return|;
block|}
specifier|protected
specifier|final
name|DefaultTableModel
name|getGroupMembersTableModel
parameter_list|()
block|{
if|if
condition|(
name|groupMembersTableModel
operator|==
literal|null
condition|)
block|{
name|groupMembersTableModel
operator|=
operator|new
name|ReadOnlyDefaultTableModel
argument_list|(
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Username"
block|,
literal|"Group Manager"
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getColumnClass
parameter_list|(
name|int
name|columnIndex
parameter_list|)
block|{
if|if
condition|(
name|columnIndex
operator|==
literal|1
condition|)
block|{
return|return
name|Boolean
operator|.
name|class
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getColumnClass
argument_list|(
name|columnIndex
argument_list|)
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
return|return
name|groupMembersTableModel
return|;
block|}
specifier|protected
name|void
name|addSelfAsManager
parameter_list|()
block|{
name|getGroupMembersTableModel
argument_list|()
operator|.
name|addRow
argument_list|(
operator|new
name|Object
index|[]
block|{
name|currentUser
block|,
name|Boolean
operator|.
name|TRUE
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method is called from within the constructor to initialize the form.      * WARNING: Do NOT modify this code. The content of this method is always      * regenerated by the Form Editor.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|//<editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
specifier|private
name|void
name|initComponents
parameter_list|()
block|{
name|pmGroupMembers
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JPopupMenu
argument_list|()
expr_stmt|;
name|miAddGroupMember
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JMenuItem
argument_list|()
expr_stmt|;
name|micbGroupMemberManager
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JCheckBoxMenuItem
argument_list|()
expr_stmt|;
name|miRemoveGroupMember
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JMenuItem
argument_list|()
expr_stmt|;
name|lblGroupName
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
argument_list|()
expr_stmt|;
name|txtGroupName
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JTextField
argument_list|()
expr_stmt|;
name|lblDescription
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
argument_list|()
expr_stmt|;
name|txtDescription
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JTextField
argument_list|()
expr_stmt|;
name|jSeparator1
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JSeparator
argument_list|()
expr_stmt|;
name|jScrollPane1
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
argument_list|()
expr_stmt|;
name|tblGroupMembers
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JTable
argument_list|()
expr_stmt|;
name|lblGroupMembers
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
argument_list|()
expr_stmt|;
name|jSeparator2
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JSeparator
argument_list|()
expr_stmt|;
name|btnCreate
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JButton
argument_list|()
expr_stmt|;
name|btnClose
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JButton
argument_list|()
expr_stmt|;
name|btnAddMember
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JButton
argument_list|()
expr_stmt|;
name|miAddGroupMember
operator|.
name|setText
argument_list|(
literal|"Add Group Member..."
argument_list|)
expr_stmt|;
name|miAddGroupMember
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|miAddGroupMemberActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|pmGroupMembers
operator|.
name|add
argument_list|(
name|miAddGroupMember
argument_list|)
expr_stmt|;
name|micbGroupMemberManager
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|micbGroupMemberManager
operator|.
name|setText
argument_list|(
literal|"Group Manager"
argument_list|)
expr_stmt|;
name|micbGroupMemberManager
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|micbGroupMemberManagerActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|pmGroupMembers
operator|.
name|add
argument_list|(
name|micbGroupMemberManager
argument_list|)
expr_stmt|;
name|miRemoveGroupMember
operator|.
name|setText
argument_list|(
literal|"Remove Group Member"
argument_list|)
expr_stmt|;
name|miRemoveGroupMember
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|miRemoveGroupMemberActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|pmGroupMembers
operator|.
name|add
argument_list|(
name|miRemoveGroupMember
argument_list|)
expr_stmt|;
name|setDefaultCloseOperation
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|WindowConstants
operator|.
name|DISPOSE_ON_CLOSE
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
literal|"New Group"
argument_list|)
expr_stmt|;
name|setPreferredSize
argument_list|(
operator|new
name|java
operator|.
name|awt
operator|.
name|Dimension
argument_list|(
literal|446
argument_list|,
literal|385
argument_list|)
argument_list|)
expr_stmt|;
name|lblGroupName
operator|.
name|setText
argument_list|(
literal|"Group name:"
argument_list|)
expr_stmt|;
name|txtGroupName
operator|.
name|setInputVerifier
argument_list|(
name|getGroupNameInputVerifier
argument_list|()
argument_list|)
expr_stmt|;
name|lblDescription
operator|.
name|setText
argument_list|(
literal|"Description:"
argument_list|)
expr_stmt|;
name|tblGroupMembers
operator|.
name|setModel
argument_list|(
name|getGroupMembersTableModel
argument_list|()
argument_list|)
expr_stmt|;
name|tblGroupMembers
operator|.
name|setAutoCreateRowSorter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tblGroupMembers
operator|.
name|setComponentPopupMenu
argument_list|(
name|pmGroupMembers
argument_list|)
expr_stmt|;
name|tblGroupMembers
operator|.
name|addMouseListener
argument_list|(
operator|new
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|MouseAdapter
argument_list|()
block|{
specifier|public
name|void
name|mouseClicked
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|MouseEvent
name|evt
parameter_list|)
block|{
name|tblGroupMembersMouseClicked
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|jScrollPane1
operator|.
name|setViewportView
argument_list|(
name|tblGroupMembers
argument_list|)
expr_stmt|;
name|lblGroupMembers
operator|.
name|setText
argument_list|(
literal|"Group Members:"
argument_list|)
expr_stmt|;
name|btnCreate
operator|.
name|setText
argument_list|(
literal|"Create"
argument_list|)
expr_stmt|;
name|btnCreate
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|btnCreateActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|btnClose
operator|.
name|setText
argument_list|(
literal|"Close"
argument_list|)
expr_stmt|;
name|btnClose
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|btnCloseActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|btnAddMember
operator|.
name|setText
argument_list|(
literal|"Add Group Member..."
argument_list|)
expr_stmt|;
name|btnAddMember
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|btnAddMemberActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
name|layout
init|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
argument_list|(
name|getContentPane
argument_list|()
argument_list|)
decl_stmt|;
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
name|layout
argument_list|)
expr_stmt|;
name|layout
operator|.
name|setHorizontalGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|LEADING
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|LEADING
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addGap
argument_list|(
literal|26
argument_list|,
literal|26
argument_list|,
literal|26
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|LEADING
argument_list|)
operator|.
name|addGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|TRAILING
argument_list|,
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addComponent
argument_list|(
name|lblGroupName
argument_list|)
operator|.
name|addGap
argument_list|(
literal|18
argument_list|,
literal|18
argument_list|,
literal|18
argument_list|)
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addComponent
argument_list|(
name|lblDescription
argument_list|)
operator|.
name|addGap
argument_list|(
literal|21
argument_list|,
literal|21
argument_list|,
literal|21
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|TRAILING
argument_list|,
literal|false
argument_list|)
operator|.
name|addComponent
argument_list|(
name|txtDescription
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|DEFAULT_SIZE
argument_list|,
literal|279
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|txtGroupName
argument_list|)
argument_list|)
operator|.
name|addGap
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addContainerGap
argument_list|()
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|LEADING
argument_list|)
operator|.
name|addComponent
argument_list|(
name|jSeparator2
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|TRAILING
argument_list|)
operator|.
name|addComponent
argument_list|(
name|jScrollPane1
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|TRAILING
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|,
literal|0
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|jSeparator1
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addGap
argument_list|(
literal|6
argument_list|,
literal|6
argument_list|,
literal|6
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblGroupMembers
argument_list|)
operator|.
name|addGap
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addContainerGap
argument_list|()
argument_list|)
operator|.
name|addGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|TRAILING
argument_list|,
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addGap
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnClose
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnCreate
argument_list|)
operator|.
name|addGap
argument_list|(
literal|16
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|)
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addContainerGap
argument_list|()
operator|.
name|addComponent
argument_list|(
name|btnAddMember
argument_list|)
operator|.
name|addContainerGap
argument_list|(
literal|251
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|layout
operator|.
name|setVerticalGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|LEADING
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createSequentialGroup
argument_list|()
operator|.
name|addGap
argument_list|(
literal|20
argument_list|,
literal|20
argument_list|,
literal|20
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|BASELINE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblGroupName
argument_list|)
operator|.
name|addComponent
argument_list|(
name|txtGroupName
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|DEFAULT_SIZE
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|)
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|BASELINE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|txtDescription
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|DEFAULT_SIZE
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblDescription
argument_list|)
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addComponent
argument_list|(
name|jSeparator1
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|DEFAULT_SIZE
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblGroupMembers
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addComponent
argument_list|(
name|jScrollPane1
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|,
literal|131
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|,
literal|18
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnAddMember
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addComponent
argument_list|(
name|jSeparator2
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|,
literal|12
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|PREFERRED_SIZE
argument_list|)
operator|.
name|addPreferredGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|LayoutStyle
operator|.
name|ComponentPlacement
operator|.
name|RELATED
argument_list|)
operator|.
name|addGroup
argument_list|(
name|layout
operator|.
name|createParallelGroup
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|Alignment
operator|.
name|LEADING
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnClose
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnCreate
argument_list|)
argument_list|)
operator|.
name|addContainerGap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
block|}
comment|//</editor-fold>//GEN-END:initComponents
specifier|private
name|InputVerifier
name|getGroupNameInputVerifier
parameter_list|()
block|{
return|return
operator|new
name|RegExpInputVerifier
argument_list|(
name|PTN_GROUPNAME
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isValidGroupName
parameter_list|()
block|{
if|if
condition|(
name|PTN_GROUPNAME
operator|.
name|matcher
argument_list|(
name|txtGroupName
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Group Name must be at least 3 characters ("
operator|+
name|PTN_GROUPNAME
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|isValidGroupDetails
parameter_list|()
block|{
return|return
name|isValidGroupName
argument_list|()
return|;
block|}
specifier|private
name|void
name|btnCreateActionPerformed
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_btnCreateActionPerformed
if|if
condition|(
operator|!
name|isValidGroupDetails
argument_list|()
condition|)
block|{
return|return;
block|}
comment|//create the user
name|createGroup
argument_list|()
expr_stmt|;
comment|//close the dialog
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|//GEN-LAST:event_btnCreateActionPerformed
specifier|protected
name|void
name|createGroup
parameter_list|()
block|{
comment|//1 - create the group
name|Group
name|group
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|GroupAider
name|groupAider
init|=
operator|new
name|GroupAider
argument_list|(
name|txtGroupName
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
name|groupAider
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
name|txtDescription
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|getUserManagementService
argument_list|()
operator|.
name|addGroup
argument_list|(
name|groupAider
argument_list|)
expr_stmt|;
comment|//get the created group
name|group
operator|=
name|getUserManagementService
argument_list|()
operator|.
name|getGroup
argument_list|(
name|txtGroupName
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xmldbe
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Could not create group '"
operator|+
name|txtGroupName
operator|.
name|getText
argument_list|()
operator|+
literal|"': "
operator|+
name|xmldbe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Create Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//2 - add the users to the group and set managers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getRowCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|member
init|=
operator|(
name|String
operator|)
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getValueAt
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|getUserManagementService
argument_list|()
operator|.
name|addAccountToGroup
argument_list|(
name|member
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|isManager
init|=
operator|(
name|Boolean
operator|)
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getValueAt
argument_list|(
name|i
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|isManager
condition|)
block|{
name|getUserManagementService
argument_list|()
operator|.
name|addGroupManager
argument_list|(
name|member
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xmldbe
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Could not add user '"
operator|+
name|member
operator|+
literal|"' to group '"
operator|+
name|group
operator|.
name|getName
argument_list|()
operator|+
literal|"': "
operator|+
name|xmldbe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Create Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|btnCloseActionPerformed
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_btnCloseActionPerformed
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|//GEN-LAST:event_btnCloseActionPerformed
specifier|protected
name|boolean
name|canModifyGroupMembers
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|boolean
name|canModifySelectedGroupMember
parameter_list|()
block|{
return|return
name|tblGroupMembers
operator|.
name|getSelectedRow
argument_list|()
operator|>
operator|-
literal|1
return|;
block|}
specifier|private
name|void
name|tblGroupMembersMouseClicked
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|MouseEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_tblGroupMembersMouseClicked
name|micbGroupMemberManager
operator|.
name|setEnabled
argument_list|(
name|canModifySelectedGroupMember
argument_list|()
argument_list|)
expr_stmt|;
name|micbGroupMemberManager
operator|.
name|setState
argument_list|(
name|isSelectedMemberManager
argument_list|()
argument_list|)
expr_stmt|;
name|miRemoveGroupMember
operator|.
name|setEnabled
argument_list|(
name|canModifySelectedGroupMember
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//GEN-LAST:event_tblGroupMembersMouseClicked
specifier|private
name|void
name|micbGroupMemberManagerActionPerformed
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_micbGroupMemberManagerActionPerformed
name|getGroupMembersTableModel
argument_list|()
operator|.
name|setValueAt
argument_list|(
operator|!
name|isSelectedMemberManager
argument_list|()
argument_list|,
name|tblGroupMembers
operator|.
name|getSelectedRow
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//GEN-LAST:event_micbGroupMemberManagerActionPerformed
specifier|private
name|void
name|miRemoveGroupMemberActionPerformed
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_miRemoveGroupMemberActionPerformed
specifier|final
name|int
name|row
init|=
name|tblGroupMembers
operator|.
name|getSelectedRow
argument_list|()
decl_stmt|;
name|getGroupMembersTableModel
argument_list|()
operator|.
name|removeRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
comment|//GEN-LAST:event_miRemoveGroupMemberActionPerformed
specifier|private
name|void
name|btnAddMemberActionPerformed
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_btnAddMemberActionPerformed
name|showFindUserForm
argument_list|()
expr_stmt|;
block|}
comment|//GEN-LAST:event_btnAddMemberActionPerformed
specifier|private
name|void
name|miAddGroupMemberActionPerformed
parameter_list|(
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
name|evt
parameter_list|)
block|{
comment|//GEN-FIRST:event_miAddGroupMemberActionPerformed
name|showFindUserForm
argument_list|()
expr_stmt|;
block|}
comment|//GEN-LAST:event_miAddGroupMemberActionPerformed
specifier|protected
name|String
name|getSelectedMember
parameter_list|()
block|{
specifier|final
name|int
name|row
init|=
name|tblGroupMembers
operator|.
name|getSelectedRow
argument_list|()
decl_stmt|;
return|return
operator|(
name|String
operator|)
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getValueAt
argument_list|(
name|row
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|isSelectedMemberManager
parameter_list|()
block|{
specifier|final
name|int
name|row
init|=
name|tblGroupMembers
operator|.
name|getSelectedRow
argument_list|()
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getValueAt
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
return|;
block|}
specifier|private
name|void
name|showFindUserForm
parameter_list|()
block|{
specifier|final
name|DialogCompleteWithResponse
argument_list|<
name|String
argument_list|>
name|callback
init|=
name|username
lambda|->
block|{
if|if
condition|(
operator|!
name|groupMembersContains
argument_list|(
name|username
argument_list|)
condition|)
block|{
name|getGroupMembersTableModel
argument_list|()
operator|.
name|addRow
argument_list|(
operator|new
name|Object
index|[]
block|{
name|username
operator|,
literal|false
block|}
block_content|)
empty_stmt|;
block|}
block|}
empty_stmt|;
try|try
block|{
specifier|final
name|FindUserForm
name|findUserForm
init|=
operator|new
name|FindUserForm
argument_list|(
name|getUserManagementService
argument_list|()
argument_list|)
decl_stmt|;
name|findUserForm
operator|.
name|addDialogCompleteWithResponseCallback
argument_list|(
name|callback
argument_list|)
expr_stmt|;
name|findUserForm
operator|.
name|setTitle
argument_list|(
literal|"Add User to Group..."
argument_list|)
expr_stmt|;
name|findUserForm
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xmldbe
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Could not retrieve list of users: "
operator|+
name|xmldbe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Add Member Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
end_class

begin_function
specifier|private
name|boolean
name|groupMembersContains
parameter_list|(
specifier|final
name|String
name|username
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getRowCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|member
init|=
operator|(
name|String
operator|)
name|getGroupMembersTableModel
argument_list|()
operator|.
name|getValueAt
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|member
operator|.
name|equals
argument_list|(
name|username
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
end_function

begin_function
specifier|protected
name|String
name|getCurrentUser
parameter_list|()
block|{
return|return
name|currentUser
return|;
block|}
end_function

begin_comment
comment|// Variables declaration - do not modify//GEN-BEGIN:variables
end_comment

begin_decl_stmt
specifier|protected
name|javax
operator|.
name|swing
operator|.
name|JButton
name|btnAddMember
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JButton
name|btnClose
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|protected
name|javax
operator|.
name|swing
operator|.
name|JButton
name|btnCreate
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
name|jScrollPane1
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JSeparator
name|jSeparator1
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JSeparator
name|jSeparator2
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblDescription
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblGroupMembers
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblGroupName
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|protected
name|javax
operator|.
name|swing
operator|.
name|JMenuItem
name|miAddGroupMember
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JMenuItem
name|miRemoveGroupMember
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JCheckBoxMenuItem
name|micbGroupMemberManager
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JPopupMenu
name|pmGroupMembers
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|protected
name|javax
operator|.
name|swing
operator|.
name|JTable
name|tblGroupMembers
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|protected
name|javax
operator|.
name|swing
operator|.
name|JTextField
name|txtDescription
decl_stmt|;
end_decl_stmt

begin_decl_stmt
specifier|protected
name|javax
operator|.
name|swing
operator|.
name|JTextField
name|txtGroupName
decl_stmt|;
end_decl_stmt

begin_comment
comment|// End of variables declaration//GEN-END:variables
end_comment

unit|}
end_unit

