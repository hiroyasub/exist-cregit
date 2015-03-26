begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|User
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
name|Resource
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
name|Service
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
comment|/**  * An eXist-specific service which provides methods to manage users and  * permissions.  *  * @author Wolfgang Meier<meier@ifs.tu-darmstadt.de>  * @author Modified by {Marco.Tampucci, Massimo.Martinelli} @isti.cnr.it  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|UserManagementService
extends|extends
name|Service
block|{
comment|/**      *  Get the name of this service      *      *@return    The name      */
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      *  Get the version of this service      *      *@return    The version value      */
annotation|@
name|Override
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/** 	 * Set permissions for the specified collection. 	 *  	 * @param child 	 * @param perm 	 * @throws XMLDBException 	 */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Collection
name|child
parameter_list|,
name|Permission
name|perm
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Collection
name|child
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|mode
parameter_list|,
name|List
argument_list|<
name|ACEAider
argument_list|>
name|aces
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Set permissions for the specified resource. 	 *  	 * @param resource 	 * @param perm 	 * @throws XMLDBException 	 */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|Permission
name|perm
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|mode
parameter_list|,
name|List
argument_list|<
name|ACEAider
argument_list|>
name|aces
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|//public void setPermissions(Collection collection, String owner, String group, int mode) throws XMLDBException;
comment|//public void setPermissions(Resource resource, String owner, String group, int mode) throws XMLDBException;
comment|/**      * Change owner gid of the current collection.      *      * @param  group               The group      * @throws XMLDBException      */
specifier|public
name|void
name|chgrp
parameter_list|(
name|String
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Change owner uid of the current collection.      *      * @param  u                   The user      * @throws XMLDBException      */
specifier|public
name|void
name|chown
parameter_list|(
name|Account
name|u
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Change owner uid and gid of the current collection.      *      * @param  u                   The user      * @param  group               The group      * @throws XMLDBException      */
specifier|public
name|void
name|chown
parameter_list|(
name|Account
name|u
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Change owner gid of the specified resource.      *      * @param  res                 The resource      * @param  group               The group      * @throws XMLDBException      */
specifier|public
name|void
name|chgrp
parameter_list|(
name|Resource
name|res
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Change owner uid of the specified resource.      *      * @param  res                 The resource      * @param  u                   The user      * @throws XMLDBException      */
specifier|public
name|void
name|chown
parameter_list|(
name|Resource
name|res
parameter_list|,
name|Account
name|u
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Change owner uid and gid of the specified resource.      *      * @param  res                 The resource      * @param  u                   The user      * @param  group               The group      * @throws XMLDBException      */
specifier|public
name|void
name|chown
parameter_list|(
name|Resource
name|res
parameter_list|,
name|Account
name|u
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Change permissions for the specified resource.      *      * Permissions are specified in a string according to the      * following format:      *       *<pre>[user|group|other]=[+|-][read|write|update]</pre>      *       * For example, to grant all permissions to the group and      * deny everything to others:      *       * group=+write,+read,+update,other=-read      *       * The changes are applied to the permissions currently      * active for this resource.      *       *@param  resource            Description of the Parameter      *@param  modeStr             Description of the Parameter      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|chmod
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|modeStr
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Change permissions for the current collection      *      *@param  modeStr             String describing the permissions to      * grant or deny.      *@exception  XMLDBException      *       */
specifier|public
name|void
name|chmod
parameter_list|(
name|String
name|modeStr
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|chmod
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Change permissions for the specified resource.      *       */
specifier|public
name|void
name|chmod
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Lock the specified resource for the specified user.      *       * A locked resource cannot be changed by other users (except      * users in group DBA) until the lock is released. Users with admin      * privileges can always change a resource.      *       * @param res      * @param u      * @throws XMLDBException      */
specifier|public
name|void
name|lockResource
parameter_list|(
name|Resource
name|res
parameter_list|,
name|Account
name|u
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Check if the resource has a user lock.      *       * Returns the name of the owner of the lock or null      * if no lock has been set on the resource.      *       * @param res      * @return Name of the owner of the lock      * @throws XMLDBException      */
specifier|public
name|String
name|hasUserLock
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Unlock the specified resource.      *       * The current user has to be same who locked the resource.      * Exception: admin users can always unlock a resource.      *       * @param res      * @throws XMLDBException      */
specifier|public
name|void
name|unlockResource
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Add a new account to the database      *      *@param  account             The feature to be added to the Account      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|addAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Update existing account information      *      * @param  account             Description of the Parameter      * @exception  XMLDBException  Description of the Exception      */
specifier|public
name|void
name|updateAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Update existing group information      *      * @param  group The group to update      * @throws XMLDBException if the group could not be updated      */
specifier|public
name|void
name|updateGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get a account record from the database      *      *@param  name                Description of the Parameter      *@return                     The user value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Account
name|getAccount
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|addAccountToGroup
parameter_list|(
name|String
name|accountName
parameter_list|,
name|String
name|groupName
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|addGroupManager
parameter_list|(
name|String
name|manager
parameter_list|,
name|String
name|groupName
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|removeGroupManager
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|manager
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Retrieve a list of all existing accounts.      *      *@return                     The accounts value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Account
index|[]
name|getAccounts
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|Group
name|getGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Retrieve a list of all existing groups. 	 *  	 * Please note: new groups are created automatically if a new group 	 * is assigned to a user. You can't add or remove them. 	 *  	 * @return List of all existing groups. 	 * @throws XMLDBException 	 */
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get a property defined by this service.      *      * @param  property            Description of the Parameter      * @return                     The property value      * @exception  XMLDBException  Description of the Exception      */
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Set a property for this service.      *      * @param  property            The new property value      * @param  value               The new property value      * @exception  XMLDBException  Description of the Exception      */
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Set the current collection for this service      *      *@param  collection          The new collection value      *@exception  XMLDBException  Description of the Exception      */
annotation|@
name|Override
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get permissions for the specified collections      *      *@param  coll                Description of the Parameter      *@return                     The permissions value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Permission
name|getPermissions
parameter_list|(
name|Collection
name|coll
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get the permissions of the sub-collection      */
specifier|public
name|Permission
name|getSubCollectionPermissions
parameter_list|(
name|Collection
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get the permissions of the sub-resource      */
specifier|public
name|Permission
name|getSubResourcePermissions
parameter_list|(
name|Collection
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|Date
name|getSubCollectionCreationTime
parameter_list|(
name|Collection
name|parent
parameter_list|,
name|String
name|string
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Get permissions for the specified resource      *      *@param  res                 Description of the Parameter      *@return                     The permissions value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Permission
name|getPermissions
parameter_list|(
name|Resource
name|res
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get permissions for all resources contained in the current      * collection. Returns a list of permissions in the same order      * as Collection.listResources().      *       * @return Permission[]      * @throws XMLDBException      */
specifier|public
name|Permission
index|[]
name|listResourcePermissions
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get permissions for all child collections contained in the current      * collection. Returns a list of permissions in the same order      * as Collection.listChildCollections().      *       * @return Permission[]      * @throws XMLDBException      */
specifier|public
name|Permission
index|[]
name|listCollectionPermissions
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      *  Delete a user from the database      *      *@param  account                User      *@exception  XMLDBException      */
specifier|public
name|void
name|removeAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|removeGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 *  Update the specified user without update user's password 	 *  Method added by {Marco.Tampucci, Massimo.Martinelli} @isti.cnr.it 	 * 	 *@param  user                Description of the Parameter 	 *@exception  XMLDBException  Description of the Exception 	 */
specifier|public
name|void
name|addUserGroup
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|removeGroupMember
parameter_list|(
specifier|final
name|String
name|group
parameter_list|,
specifier|final
name|String
name|account
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|addGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Deprecated
comment|//it'll removed after 1.6
specifier|public
name|void
name|addUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Deprecated
comment|//it'll removed after 1.6
specifier|public
name|void
name|updateUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Deprecated
comment|//it'll removed after 1.6
specifier|public
name|User
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Deprecated
comment|//it'll removed after 1.6
specifier|public
name|User
index|[]
name|getUsers
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Deprecated
comment|//it'll removed after 1.6
specifier|public
name|void
name|removeUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
annotation|@
name|Deprecated
comment|//it'll removed after 1.6
specifier|public
name|void
name|lockResource
parameter_list|(
name|Resource
name|res
parameter_list|,
name|User
name|u
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|String
index|[]
name|getGroupMembers
parameter_list|(
name|String
name|groupName
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

