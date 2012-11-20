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
name|AXSchemaType
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
name|EditUserDialog
extends|extends
name|UserDialog
implements|implements
name|DialogWithResponse
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|9097018734007436201L
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|HIDDEN_PASSWORD_CONST
init|=
literal|"password"
decl_stmt|;
specifier|private
specifier|final
name|Account
name|account
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|DialogCompleteWithResponse
argument_list|<
name|String
argument_list|>
argument_list|>
name|dialogCompleteWithResponseCallbacks
init|=
operator|new
name|ArrayList
argument_list|<
name|DialogCompleteWithResponse
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|EditUserDialog
parameter_list|(
specifier|final
name|UserManagementService
name|userManagementService
parameter_list|,
specifier|final
name|Account
name|account
parameter_list|)
block|{
name|super
argument_list|(
name|userManagementService
argument_list|)
expr_stmt|;
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
name|setFormPropertiesFromAccount
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setFormPropertiesFromAccount
parameter_list|()
block|{
name|setTitle
argument_list|(
literal|"Edit User: "
operator|+
name|getAccount
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
name|txtUsername
operator|.
name|setText
argument_list|(
name|getAccount
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|txtUsername
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|txtFullName
operator|.
name|setText
argument_list|(
name|getAccount
argument_list|()
operator|.
name|getMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
argument_list|)
argument_list|)
expr_stmt|;
name|txtDescription
operator|.
name|setText
argument_list|(
name|getAccount
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
name|txtPassword
operator|.
name|setText
argument_list|(
name|HIDDEN_PASSWORD_CONST
argument_list|)
expr_stmt|;
name|txtPasswordConfirm
operator|.
name|setText
argument_list|(
name|HIDDEN_PASSWORD_CONST
argument_list|)
expr_stmt|;
name|cbDisabled
operator|.
name|setSelected
argument_list|(
operator|!
name|getAccount
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|spnUmask
operator|.
name|setValue
argument_list|(
name|UmaskSpinnerModel
operator|.
name|intToOctalUmask
argument_list|(
name|getAccount
argument_list|()
operator|.
name|getUserMask
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cbPersonalGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|group
range|:
name|getAccount
argument_list|()
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|getMemberOfGroupsListModel
argument_list|()
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|getAvailableGroupsListModel
argument_list|()
operator|.
name|removeElement
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createUser
parameter_list|()
block|{
comment|//dont create a user, update instead!
name|updateUser
argument_list|()
expr_stmt|;
comment|//return updated password
for|for
control|(
specifier|final
name|DialogCompleteWithResponse
argument_list|<
name|String
argument_list|>
name|callback
range|:
name|getDialogCompleteWithResponseCallbacks
argument_list|()
control|)
block|{
comment|//only fire if password changed
if|if
condition|(
name|isPasswordChanged
argument_list|()
condition|)
block|{
name|callback
operator|.
name|complete
argument_list|(
name|txtPassword
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|updateUser
parameter_list|()
block|{
try|try
block|{
name|setAccountFromFormProperties
argument_list|()
expr_stmt|;
name|getUserManagementService
argument_list|()
operator|.
name|updateAccount
argument_list|(
name|getAccount
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
literal|"Could not update user '"
operator|+
name|txtUsername
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
literal|"Edit User Error"
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
literal|"Could not update user '"
operator|+
name|txtUsername
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
literal|"Edit User Error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isPasswordChanged
parameter_list|()
block|{
specifier|final
name|String
name|password
init|=
operator|new
name|String
argument_list|(
name|txtPassword
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|!
name|password
operator|.
name|equals
argument_list|(
name|HIDDEN_PASSWORD_CONST
argument_list|)
return|;
block|}
specifier|private
name|void
name|setAccountFromFormProperties
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
name|getAccount
argument_list|()
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
argument_list|,
name|txtFullName
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|getAccount
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
if|if
condition|(
name|isPasswordChanged
argument_list|()
condition|)
block|{
specifier|final
name|String
name|password
init|=
operator|new
name|String
argument_list|(
name|txtPassword
operator|.
name|getPassword
argument_list|()
argument_list|)
decl_stmt|;
name|getAccount
argument_list|()
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
name|getAccount
argument_list|()
operator|.
name|setEnabled
argument_list|(
operator|!
name|cbDisabled
operator|.
name|isSelected
argument_list|()
argument_list|)
expr_stmt|;
name|getAccount
argument_list|()
operator|.
name|setUserMask
argument_list|(
name|UmaskSpinnerModel
operator|.
name|octalUmaskToInt
argument_list|(
operator|(
name|String
operator|)
name|spnUmask
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//get the current groups of the user
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|currentGroups
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
name|getAccount
argument_list|()
operator|.
name|getGroups
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//get the new groups of the user to be set
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|memberOfGroups
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
name|getMemberOfGroupsListModel
argument_list|()
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|memberOfGroups
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|getMemberOfGroupsListModel
argument_list|()
operator|.
name|getElementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//groups to remove
for|for
control|(
specifier|final
name|String
name|currentGroup
range|:
name|currentGroups
control|)
block|{
if|if
condition|(
operator|!
name|memberOfGroups
operator|.
name|contains
argument_list|(
name|currentGroup
argument_list|)
condition|)
block|{
name|account
operator|.
name|remGroup
argument_list|(
name|currentGroup
argument_list|)
expr_stmt|;
block|}
block|}
comment|//groups to add
for|for
control|(
specifier|final
name|String
name|memberOfGroup
range|:
name|memberOfGroups
control|)
block|{
if|if
condition|(
operator|!
name|currentGroups
operator|.
name|contains
argument_list|(
name|memberOfGroup
argument_list|)
condition|)
block|{
name|account
operator|.
name|addGroup
argument_list|(
name|memberOfGroup
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|Account
name|getAccount
parameter_list|()
block|{
return|return
name|account
return|;
block|}
specifier|private
name|List
argument_list|<
name|DialogCompleteWithResponse
argument_list|<
name|String
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
name|String
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

