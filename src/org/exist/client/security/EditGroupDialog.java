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
name|Arrays
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
name|JOptionPane
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
name|PermissionDeniedException
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
name|EditGroupDialog
extends|extends
name|GroupDialog
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|9092253443709031810L
decl_stmt|;
specifier|private
specifier|final
name|Group
name|group
decl_stmt|;
specifier|public
name|EditGroupDialog
parameter_list|(
specifier|final
name|UserManagementService
name|userManagementService
parameter_list|,
specifier|final
name|String
name|currentUser
parameter_list|,
specifier|final
name|Group
name|group
parameter_list|)
block|{
name|super
argument_list|(
name|userManagementService
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|setFormPropertiesFromGroup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addSelfAsManager
parameter_list|()
block|{
block|}
specifier|private
name|void
name|setFormPropertiesFromGroup
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Edit Group: "
operator|+
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|btnCreate
operator|.
name|setText
argument_list|(
literal|"Save"
argument_list|)
expr_stmt|;
name|txtGroupName
operator|.
name|setText
argument_list|(
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|txtGroupName
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|txtDescription
operator|.
name|setText
argument_list|(
name|getGroup
argument_list|()
operator|.
name|getMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
comment|//display existing group members and managers
try|try
block|{
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|groupManagers
init|=
name|group
operator|.
name|getManagers
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|groupMembers
init|=
name|getUserManagementService
argument_list|()
operator|.
name|getGroupMembers
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|groupMembers
argument_list|)
expr_stmt|;
comment|//order the members a-z
for|for
control|(
specifier|final
name|String
name|groupMember
range|:
name|groupMembers
control|)
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
name|groupMember
block|,
name|isGroupManager
argument_list|(
name|groupManagers
argument_list|,
name|groupMember
argument_list|)
block|}
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
literal|"Could not get group members: "
operator|+
name|xmldbe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Edit Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Could not get group members: "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Edit Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
comment|//enable additions to the group?
name|miAddGroupMember
operator|.
name|setEnabled
argument_list|(
name|canModifyGroupMembers
argument_list|()
argument_list|)
expr_stmt|;
name|btnAddMember
operator|.
name|setEnabled
argument_list|(
name|canModifyGroupMembers
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createGroup
parameter_list|()
block|{
comment|//dont create a group update instead!
name|updateGroup
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|updateGroup
parameter_list|()
block|{
try|try
block|{
name|updateGroupMembers
argument_list|()
expr_stmt|;
name|setGroupFromFormProperties
argument_list|()
expr_stmt|;
name|getUserManagementService
argument_list|()
operator|.
name|updateGroup
argument_list|(
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Could not update group '"
operator|+
name|txtGroupName
operator|.
name|getText
argument_list|()
operator|+
literal|"': "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Edit Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
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
literal|"Could not update group '"
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
literal|"Edit Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setGroupFromFormProperties
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|XMLDBException
block|{
name|getGroup
argument_list|()
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
comment|//set managers
comment|//1) remove all managers
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|currentManagers
init|=
name|getGroup
argument_list|()
operator|.
name|getManagers
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Account
name|currentManager
range|:
name|currentManagers
control|)
block|{
name|getUserManagementService
argument_list|()
operator|.
name|removeGroupManager
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|currentManager
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//2) only add those in this dialog
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupManagers
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
specifier|final
name|String
name|manager
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
name|getUserManagementService
argument_list|()
operator|.
name|addGroupManager
argument_list|(
name|manager
argument_list|,
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|updateGroupMembers
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|currentGroupMembers
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|getUserManagementService
argument_list|()
operator|.
name|getGroupMembers
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|groupMembers
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
name|groupMembers
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
comment|//members to remove
for|for
control|(
specifier|final
name|String
name|currentGroupMember
range|:
name|currentGroupMembers
control|)
block|{
if|if
condition|(
operator|!
name|groupMembers
operator|.
name|contains
argument_list|(
name|currentGroupMember
argument_list|)
condition|)
block|{
name|getUserManagementService
argument_list|()
operator|.
name|removeGroupMember
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|currentGroupMember
argument_list|)
expr_stmt|;
block|}
block|}
comment|//members to add
for|for
control|(
specifier|final
name|String
name|groupMember
range|:
name|groupMembers
control|)
block|{
if|if
condition|(
operator|!
name|currentGroupMembers
operator|.
name|contains
argument_list|(
name|groupMember
argument_list|)
condition|)
block|{
name|getUserManagementService
argument_list|()
operator|.
name|addAccountToGroup
argument_list|(
name|groupMember
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|Group
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canModifyGroupMembers
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|getUserManagementService
argument_list|()
operator|.
name|getAccount
argument_list|(
name|getCurrentUser
argument_list|()
argument_list|)
operator|.
name|hasDbaRole
argument_list|()
operator|||
name|isGroupManager
argument_list|(
name|group
operator|.
name|getManagers
argument_list|()
argument_list|,
name|getCurrentUser
argument_list|()
argument_list|)
operator|)
return|;
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
literal|"Could not establish user "
operator|+
name|getCurrentUser
argument_list|()
operator|+
literal|"'s group permissions: "
operator|+
name|xmldbe
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Edit Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Could not establish user "
operator|+
name|getCurrentUser
argument_list|()
operator|+
literal|"'s group permissions: "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Edit Group Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canModifySelectedGroupMember
parameter_list|()
block|{
specifier|final
name|boolean
name|groupMemberSelected
init|=
name|tblGroupMembers
operator|.
name|getSelectedRow
argument_list|()
operator|>
operator|-
literal|1
decl_stmt|;
return|return
name|groupMemberSelected
operator|&&
operator|(
operator|!
operator|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
operator|&&
operator|(
name|getSelectedMember
argument_list|()
operator|.
name|equals
argument_list|(
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
operator|||
name|getSelectedMember
argument_list|()
operator|.
name|equals
argument_list|(
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|SYSTEM
argument_list|)
operator|)
operator|)
operator|)
operator|&&
operator|(
operator|!
operator|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|GUEST_GROUP
argument_list|)
operator|&&
name|getSelectedMember
argument_list|()
operator|.
name|equals
argument_list|(
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
operator|)
operator|)
return|;
block|}
specifier|private
name|boolean
name|isGroupManager
parameter_list|(
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|groupManagers
parameter_list|,
specifier|final
name|String
name|groupMember
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Account
name|groupManager
range|:
name|groupManagers
control|)
block|{
if|if
condition|(
name|groupManager
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|groupMember
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
block|}
end_class

end_unit
