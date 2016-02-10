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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ComboBoxModel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|DefaultComboBoxModel
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
name|DialogWithResponse
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
name|InteractiveClient
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
name|ACLPermission
operator|.
name|ACE_ACCESS_TYPE
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
name|ACLPermission
operator|.
name|ACE_TARGET
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
name|Account
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
name|Permission
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
name|ACEAider
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
name|AccessControlEntryDialog
extends|extends
name|javax
operator|.
name|swing
operator|.
name|JFrame
implements|implements
name|DialogWithResponse
argument_list|<
name|ACEAider
argument_list|>
block|{
specifier|private
specifier|final
name|UserManagementService
name|userManagementService
decl_stmt|;
specifier|private
name|DefaultTableModel
name|permissionTableModel
init|=
literal|null
decl_stmt|;
specifier|private
name|DefaultComboBoxModel
name|usernameModel
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allUsernames
decl_stmt|;
specifier|private
name|DefaultComboBoxModel
name|groupNameModel
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allGroupNames
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|DialogCompleteWithResponse
argument_list|<
name|ACEAider
argument_list|>
argument_list|>
name|dialogCompleteWithResponseCallbacks
init|=
operator|new
name|ArrayList
argument_list|<
name|DialogCompleteWithResponse
argument_list|<
name|ACEAider
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Creates new form AccessControlEntryDialog      */
specifier|public
name|AccessControlEntryDialog
parameter_list|(
specifier|final
name|UserManagementService
name|userManagementService
parameter_list|,
specifier|final
name|String
name|title
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|userManagementService
operator|=
name|userManagementService
expr_stmt|;
name|this
operator|.
name|setIconImage
argument_list|(
name|InteractiveClient
operator|.
name|getExistIcon
argument_list|(
name|getClass
argument_list|()
argument_list|)
operator|.
name|getImage
argument_list|()
argument_list|)
expr_stmt|;
name|allUsernames
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Account
name|account
range|:
name|userManagementService
operator|.
name|getAccounts
argument_list|()
control|)
block|{
name|allUsernames
operator|.
name|add
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|allGroupNames
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|groupName
range|:
name|userManagementService
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|allGroupNames
operator|.
name|add
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
block|}
name|initComponents
argument_list|()
expr_stmt|;
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DefaultTableModel
name|getPermissionTableModel
parameter_list|()
block|{
if|if
condition|(
name|permissionTableModel
operator|==
literal|null
condition|)
block|{
name|permissionTableModel
operator|=
operator|new
name|DefaultTableModel
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
operator|new
name|Object
index|[]
block|{
literal|false
block|,
literal|false
block|,
literal|false
block|}
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Read"
block|,
literal|"Write"
block|,
literal|"Execute"
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Class
name|getColumnClass
parameter_list|(
name|int
name|columnIndex
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|class
return|;
block|}
annotation|@
name|Override
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
block|}
expr_stmt|;
block|}
return|return
name|permissionTableModel
return|;
block|}
specifier|private
name|ComboBoxModel
name|getUsernameModel
parameter_list|()
block|{
if|if
condition|(
name|usernameModel
operator|==
literal|null
condition|)
block|{
name|usernameModel
operator|=
operator|new
name|DefaultComboBoxModel
argument_list|()
expr_stmt|;
name|usernameModel
operator|.
name|addElement
argument_list|(
literal|""
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|username
range|:
name|allUsernames
control|)
block|{
name|usernameModel
operator|.
name|addElement
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|usernameModel
return|;
block|}
specifier|private
name|ComboBoxModel
name|getGroupNameModel
parameter_list|()
block|{
if|if
condition|(
name|groupNameModel
operator|==
literal|null
condition|)
block|{
name|groupNameModel
operator|=
operator|new
name|DefaultComboBoxModel
argument_list|()
expr_stmt|;
name|groupNameModel
operator|.
name|addElement
argument_list|(
literal|""
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|groupName
range|:
name|allGroupNames
control|)
block|{
name|groupNameModel
operator|.
name|addElement
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|groupNameModel
return|;
block|}
specifier|private
name|boolean
name|isValidUsername
parameter_list|(
specifier|final
name|String
name|username
parameter_list|)
block|{
return|return
name|allUsernames
operator|.
name|contains
argument_list|(
name|username
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isValidGroupName
parameter_list|(
specifier|final
name|String
name|groupName
parameter_list|)
block|{
return|return
name|allGroupNames
operator|.
name|contains
argument_list|(
name|groupName
argument_list|)
return|;
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
name|lblTarget
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
argument_list|()
expr_stmt|;
name|cmbTarget
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JComboBox
argument_list|()
expr_stmt|;
name|lblUsername
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
argument_list|()
expr_stmt|;
name|cmbUsername
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JComboBox
argument_list|()
expr_stmt|;
name|AutoCompletion
operator|.
name|enable
argument_list|(
name|cmbUsername
argument_list|)
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
name|cmbGroupName
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JComboBox
argument_list|()
expr_stmt|;
name|AutoCompletion
operator|.
name|enable
argument_list|(
name|cmbGroupName
argument_list|)
expr_stmt|;
name|lblAccess
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
argument_list|()
expr_stmt|;
name|cmbAccess
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JComboBox
argument_list|()
expr_stmt|;
name|lblPermission
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JLabel
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
name|tblPermission
operator|=
operator|new
name|javax
operator|.
name|swing
operator|.
name|JTable
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
name|setDefaultCloseOperation
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|WindowConstants
operator|.
name|EXIT_ON_CLOSE
argument_list|)
expr_stmt|;
name|lblTarget
operator|.
name|setText
argument_list|(
literal|"Target:"
argument_list|)
expr_stmt|;
name|cmbTarget
operator|.
name|setModel
argument_list|(
operator|new
name|javax
operator|.
name|swing
operator|.
name|DefaultComboBoxModel
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"USER"
block|,
literal|"GROUP"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|cmbTarget
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|cmbTargetActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|lblUsername
operator|.
name|setText
argument_list|(
literal|"Username:"
argument_list|)
expr_stmt|;
name|cmbUsername
operator|.
name|setEditable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cmbUsername
operator|.
name|setModel
argument_list|(
name|getUsernameModel
argument_list|()
argument_list|)
expr_stmt|;
name|cmbUsername
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|cmbUsernameActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|lblGroupName
operator|.
name|setText
argument_list|(
literal|"Group:"
argument_list|)
expr_stmt|;
name|cmbGroupName
operator|.
name|setEditable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cmbGroupName
operator|.
name|setModel
argument_list|(
name|getGroupNameModel
argument_list|()
argument_list|)
expr_stmt|;
name|cmbGroupName
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cmbGroupName
operator|.
name|addActionListener
argument_list|(
name|evt
lambda|->
name|cmbGroupNameActionPerformed
argument_list|(
name|evt
argument_list|)
argument_list|)
expr_stmt|;
name|lblAccess
operator|.
name|setText
argument_list|(
literal|"Access:"
argument_list|)
expr_stmt|;
name|cmbAccess
operator|.
name|setModel
argument_list|(
operator|new
name|javax
operator|.
name|swing
operator|.
name|DefaultComboBoxModel
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"ALLOWED"
block|,
literal|"DENIED"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|lblPermission
operator|.
name|setText
argument_list|(
literal|"Permission"
argument_list|)
expr_stmt|;
name|tblPermission
operator|.
name|setModel
argument_list|(
name|getPermissionTableModel
argument_list|()
argument_list|)
expr_stmt|;
name|tblPermission
operator|.
name|setRowSelectionAllowed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|jScrollPane1
operator|.
name|setViewportView
argument_list|(
name|tblPermission
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
name|addGap
argument_list|(
literal|25
argument_list|,
literal|25
argument_list|,
literal|25
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
literal|345
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
name|addComponent
argument_list|(
name|lblPermission
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
name|addComponent
argument_list|(
name|lblUsername
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblTarget
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblGroupName
argument_list|)
operator|.
name|addComponent
argument_list|(
name|lblAccess
argument_list|)
argument_list|)
operator|.
name|addGap
argument_list|(
literal|28
argument_list|,
literal|28
argument_list|,
literal|28
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
name|cmbAccess
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
argument_list|,
literal|false
argument_list|)
operator|.
name|addComponent
argument_list|(
name|cmbTarget
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
name|cmbUsername
argument_list|,
literal|0
argument_list|,
literal|257
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|cmbGroupName
argument_list|,
literal|0
argument_list|,
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|DEFAULT_SIZE
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
argument_list|(
literal|24
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
name|jSeparator1
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
argument_list|)
argument_list|)
operator|.
name|addContainerGap
argument_list|()
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
literal|17
argument_list|,
literal|17
argument_list|,
literal|17
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
name|lblTarget
argument_list|)
operator|.
name|addComponent
argument_list|(
name|cmbTarget
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
name|lblUsername
argument_list|)
operator|.
name|addComponent
argument_list|(
name|cmbUsername
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
name|lblGroupName
argument_list|)
operator|.
name|addComponent
argument_list|(
name|cmbGroupName
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
name|cmbAccess
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
name|lblAccess
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
name|lblPermission
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
literal|45
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
name|UNRELATED
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
literal|10
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
name|BASELINE
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnCreate
argument_list|)
operator|.
name|addComponent
argument_list|(
name|btnClose
argument_list|)
argument_list|)
operator|.
name|addContainerGap
argument_list|(
name|javax
operator|.
name|swing
operator|.
name|GroupLayout
operator|.
name|DEFAULT_SIZE
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
block|}
comment|//</editor-fold>//GEN-END:initComponents
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
specifier|final
name|ACE_TARGET
name|target
init|=
name|ACE_TARGET
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|cmbTarget
operator|.
name|getSelectedItem
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|who
decl_stmt|;
if|if
condition|(
name|target
operator|==
name|ACE_TARGET
operator|.
name|USER
condition|)
block|{
name|who
operator|=
operator|(
name|String
operator|)
name|cmbUsername
operator|.
name|getSelectedItem
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isValidUsername
argument_list|(
name|who
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
name|who
operator|=
operator|(
name|String
operator|)
name|cmbGroupName
operator|.
name|getSelectedItem
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isValidGroupName
argument_list|(
name|who
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
specifier|final
name|ACE_ACCESS_TYPE
name|accessType
init|=
name|ACE_ACCESS_TYPE
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|cmbAccess
operator|.
name|getSelectedItem
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|mode
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|Boolean
operator|)
name|tblPermission
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|mode
operator||=
name|Permission
operator|.
name|READ
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|Boolean
operator|)
name|tblPermission
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
condition|)
block|{
name|mode
operator||=
name|Permission
operator|.
name|WRITE
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|Boolean
operator|)
name|tblPermission
operator|.
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
name|mode
operator||=
name|Permission
operator|.
name|EXECUTE
expr_stmt|;
block|}
specifier|final
name|ACEAider
name|ace
init|=
operator|new
name|ACEAider
argument_list|(
name|accessType
argument_list|,
name|target
argument_list|,
name|who
argument_list|,
name|mode
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|DialogCompleteWithResponse
argument_list|<
name|ACEAider
argument_list|>
name|callback
range|:
name|getDialogCompleteWithResponseCallbacks
argument_list|()
control|)
block|{
name|callback
operator|.
name|complete
argument_list|(
name|ace
argument_list|)
expr_stmt|;
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
comment|//GEN-LAST:event_btnCreateActionPerformed
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
specifier|private
name|void
name|cmbTargetActionPerformed
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
comment|//GEN-FIRST:event_cmbTargetActionPerformed
specifier|final
name|ACE_TARGET
name|aceTarget
init|=
name|ACE_TARGET
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|cmbTarget
operator|.
name|getSelectedItem
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|aceTarget
condition|)
block|{
case|case
name|USER
case|:
name|cmbGroupName
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cmbUsername
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|GROUP
case|:
name|cmbUsername
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cmbGroupName
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|//GEN-LAST:event_cmbTargetActionPerformed
specifier|private
name|void
name|cmbUsernameActionPerformed
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
comment|//GEN-FIRST:event_cmbUsernameActionPerformed
specifier|final
name|String
name|currentUsername
init|=
operator|(
name|String
operator|)
name|cmbUsername
operator|.
name|getSelectedItem
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isValid
init|=
name|isValidUsername
argument_list|(
name|currentUsername
argument_list|)
decl_stmt|;
name|btnCreate
operator|.
name|setEnabled
argument_list|(
name|isValid
argument_list|)
expr_stmt|;
block|}
comment|//GEN-LAST:event_cmbUsernameActionPerformed
specifier|private
name|void
name|cmbGroupNameActionPerformed
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
comment|//GEN-FIRST:event_cmbGroupNameActionPerformed
specifier|final
name|String
name|currentGroupName
init|=
operator|(
name|String
operator|)
name|cmbGroupName
operator|.
name|getSelectedItem
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isValid
init|=
name|isValidGroupName
argument_list|(
name|currentGroupName
argument_list|)
decl_stmt|;
name|btnCreate
operator|.
name|setEnabled
argument_list|(
name|isValid
argument_list|)
expr_stmt|;
block|}
comment|//GEN-LAST:event_cmbGroupNameActionPerformed
comment|// Variables declaration - do not modify//GEN-BEGIN:variables
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JButton
name|btnClose
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JButton
name|btnCreate
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JComboBox
name|cmbAccess
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JComboBox
name|cmbGroupName
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JComboBox
name|cmbTarget
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JComboBox
name|cmbUsername
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
name|jScrollPane1
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JSeparator
name|jSeparator1
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblAccess
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblGroupName
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblPermission
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblTarget
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JLabel
name|lblUsername
decl_stmt|;
specifier|private
name|javax
operator|.
name|swing
operator|.
name|JTable
name|tblPermission
decl_stmt|;
comment|// End of variables declaration//GEN-END:variables
specifier|private
name|List
argument_list|<
name|DialogCompleteWithResponse
argument_list|<
name|ACEAider
argument_list|>
argument_list|>
name|getDialogCompleteWithResponseCallbacks
parameter_list|()
block|{
return|return
name|dialogCompleteWithResponseCallbacks
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addDialogCompleteWithResponseCallback
parameter_list|(
specifier|final
name|DialogCompleteWithResponse
argument_list|<
name|ACEAider
argument_list|>
name|dialogCompleteWithResponseCallback
parameter_list|)
block|{
name|getDialogCompleteWithResponseCallbacks
argument_list|()
operator|.
name|add
argument_list|(
name|dialogCompleteWithResponseCallback
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

