begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|securitymanager
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * eXist Security Manager Module Extension  *  * An extension module for interacting with eXist-db Security Manager  *  * @author Adam Retter<adam@existsolutions.com>  *  * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[], java.util.Map)   */
end_comment

begin_class
specifier|public
class|class
name|SecurityManagerModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/securitymanager"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"sm"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-2.0"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESCRIPTION
init|=
literal|"Module for interacting with the Security Manager"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|AccountManagementFunction
operator|.
name|FNS_CREATE_ACCOUNT
argument_list|,
name|AccountManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountManagementFunction
operator|.
name|FNS_CREATE_ACCOUNT_WITH_METADATA
argument_list|,
name|AccountManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountManagementFunction
operator|.
name|FNS_CREATE_ACCOUNT_WITH_PERSONAL_GROUP
argument_list|,
name|AccountManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountManagementFunction
operator|.
name|FNS_CREATE_ACCOUNT_WITH_PERSONAL_GROUP_WITH_METADATA
argument_list|,
name|AccountManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountManagementFunction
operator|.
name|FNS_REMOVE_ACCOUNT
argument_list|,
name|AccountManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountManagementFunction
operator|.
name|FNS_PASSWD
argument_list|,
name|AccountManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindUserFunction
operator|.
name|FNS_FIND_USERS_BY_USERNAME
argument_list|,
name|FindUserFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindUserFunction
operator|.
name|FNS_FIND_USERS_BY_NAME
argument_list|,
name|FindUserFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindUserFunction
operator|.
name|FNS_FIND_USERS_BY_NAME_PART
argument_list|,
name|FindUserFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindUserFunction
operator|.
name|FNS_LIST_USERS
argument_list|,
name|FindUserFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindUserFunction
operator|.
name|FNS_USER_EXISTS
argument_list|,
name|FindUserFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UMaskFunction
operator|.
name|FNS_GET_UMASK
argument_list|,
name|UMaskFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UMaskFunction
operator|.
name|FNS_SET_UMASK
argument_list|,
name|UMaskFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetPrincipalMetadataFunction
operator|.
name|FNS_GET_ALL_ACCOUNT_METADATA_KEYS
argument_list|,
name|GetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetPrincipalMetadataFunction
operator|.
name|FNS_GET_ACCOUNT_METADATA_KEYS
argument_list|,
name|GetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetPrincipalMetadataFunction
operator|.
name|FNS_GET_ACCOUNT_METADATA
argument_list|,
name|GetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetPrincipalMetadataFunction
operator|.
name|FNS_GET_ALL_GROUP_METADATA_KEYS
argument_list|,
name|GetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetPrincipalMetadataFunction
operator|.
name|FNS_GET_GROUP_METADATA_KEYS
argument_list|,
name|GetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetPrincipalMetadataFunction
operator|.
name|FNS_GET_GROUP_METADATA
argument_list|,
name|GetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetPrincipalMetadataFunction
operator|.
name|FNS_SET_ACCOUNT_METADATA
argument_list|,
name|SetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetPrincipalMetadataFunction
operator|.
name|FNS_SET_GROUP_METADATA
argument_list|,
name|SetPrincipalMetadataFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountStatusFunction
operator|.
name|FNS_IS_ACCOUNT_ENABLED
argument_list|,
name|AccountStatusFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AccountStatusFunction
operator|.
name|FNS_SET_ACCOUNT_ENABLED
argument_list|,
name|AccountStatusFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupManagementFunction
operator|.
name|FNS_CREATE_GROUP
argument_list|,
name|GroupManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupManagementFunction
operator|.
name|FNS_CREATE_GROUP_WITH_METADATA
argument_list|,
name|GroupManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupManagementFunction
operator|.
name|FNS_CREATE_GROUP_WITH_MANAGERS_WITH_METADATA
argument_list|,
name|GroupManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupManagementFunction
operator|.
name|FNS_REMOVE_GROUP
argument_list|,
name|GroupManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupManagementFunction
operator|.
name|FNS_DELETE_GROUP
argument_list|,
name|GroupManagementFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_ADD_GROUP_MEMBER
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_REMOVE_GROUP_MEMBER
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_ADD_GROUP_MANAGER
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_REMOVE_GROUP_MANAGER
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_GET_GROUP_MANAGERS
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_GET_GROUP_MEMBERS
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_IS_DBA
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GroupMembershipFunction
operator|.
name|FNS_SET_USER_PRIMARY_GROUP
argument_list|,
name|GroupMembershipFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_LIST_GROUPS
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_FIND_GROUPS_BY_GROUPNAME
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_FIND_GROUPS_WHERE_GROUPNAME_CONTANINS
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_GET_USER_GROUPS
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_GET_GROUPS
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_GET_USER_PRIMARY_GROUP
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FindGroupFunction
operator|.
name|FNS_GROUP_EXISTS
argument_list|,
name|FindGroupFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_GET_PERMISSIONS
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_ADD_USER_ACE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_ADD_GROUP_ACE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_INSERT_USER_ACE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_INSERT_GROUP_ACE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_MODIFY_ACE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_REMOVE_ACE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_CLEAR_ACL
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_CHMOD
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_CHOWN
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_CHGRP
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_MODE_TO_OCTAL
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_OCTAL_TO_MODE
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
comment|//<editor-fold desc="Functions on the broker/context current user">
operator|new
name|FunctionDef
argument_list|(
name|PermissionsFunction
operator|.
name|FNS_HAS_ACCESS
argument_list|,
name|PermissionsFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IsAuthenticatedFunction
operator|.
name|FNS_IS_AUTHENTICATED
argument_list|,
name|IsAuthenticatedFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IsAuthenticatedFunction
operator|.
name|FNS_IS_EXTERNALLY_AUTHENTICATED
argument_list|,
name|IsAuthenticatedFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IdFunction
operator|.
name|FNS_ID
argument_list|,
name|IdFunction
operator|.
name|class
argument_list|)
comment|//</editor-fold>
block|}
decl_stmt|;
specifier|public
name|SecurityManagerModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|DESCRIPTION
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
block|}
end_class

end_unit

