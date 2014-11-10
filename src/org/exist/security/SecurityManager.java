begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configurable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
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
name|xacml
operator|.
name|ExistPDP
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
name|BrokerPool
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

begin_comment
comment|/**  * SecurityManager is responsible for managing users and groups.  *   * There's only one SecurityManager for each database instance, which  * may be obtained by {@link BrokerPool#getSecurityManager()}.  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|SecurityManager
extends|extends
name|Configurable
block|{
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|SECURITY_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"security"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|CONFIG_FILE_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"config.xml"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|ACCOUNTS_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"accounts"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|GROUPS_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"groups"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|REMOVED_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"removed"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SYSTEM
init|=
literal|"SYSTEM"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DBA_GROUP
init|=
literal|"dba"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DBA_USER
init|=
literal|"admin"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_GROUP
init|=
literal|"guest"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GUEST_USER
init|=
literal|"guest"
decl_stmt|;
name|void
name|attach
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|DBBroker
name|sysBroker
parameter_list|)
throws|throws
name|EXistException
function_decl|;
specifier|public
name|Database
name|getDatabase
parameter_list|()
function_decl|;
name|boolean
name|isXACMLEnabled
parameter_list|()
function_decl|;
name|ExistPDP
name|getPDP
parameter_list|()
function_decl|;
name|Account
name|getAccount
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
name|boolean
name|hasAccount
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Account
name|addAccount
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|Account
name|addAccount
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|boolean
name|deleteAccount
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|boolean
name|deleteAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|boolean
name|updateAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|boolean
name|updateGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|Account
name|getAccount
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Group
name|addGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
annotation|@
name|Deprecated
name|void
name|addGroup
parameter_list|(
name|String
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
function_decl|;
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|boolean
name|hasGroup
parameter_list|(
name|Group
name|group
parameter_list|)
function_decl|;
name|Group
name|getGroup
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Group
name|getGroup
parameter_list|(
name|int
name|gid
parameter_list|)
function_decl|;
name|boolean
name|deleteGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|Account
name|user
parameter_list|)
function_decl|;
specifier|public
name|Subject
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
function_decl|;
specifier|public
name|Subject
name|getSystemSubject
parameter_list|()
function_decl|;
specifier|public
name|Subject
name|getGuestSubject
parameter_list|()
function_decl|;
specifier|public
name|Group
name|getDBAGroup
parameter_list|()
function_decl|;
specifier|public
name|List
argument_list|<
name|Account
argument_list|>
name|getGroupMembers
parameter_list|(
name|String
name|groupName
parameter_list|)
function_decl|;
annotation|@
name|Deprecated
comment|//use realm's method
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Account
argument_list|>
name|getUsers
parameter_list|()
function_decl|;
annotation|@
name|Deprecated
comment|//use realm's method
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Group
argument_list|>
name|getGroups
parameter_list|()
function_decl|;
comment|//session manager part
name|void
name|registerSession
parameter_list|(
name|Session
name|session
parameter_list|)
function_decl|;
name|Subject
name|getSubjectBySessionId
parameter_list|(
name|String
name|sessionid
parameter_list|)
function_decl|;
name|void
name|addGroup
parameter_list|(
name|int
name|id
parameter_list|,
name|Group
name|group
parameter_list|)
function_decl|;
name|void
name|addUser
parameter_list|(
name|int
name|id
parameter_list|,
name|Account
name|account
parameter_list|)
function_decl|;
name|boolean
name|hasGroup
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
name|boolean
name|hasUser
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
comment|/**     * Find users by their personal name     */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findUsernamesWhereNameStarts
parameter_list|(
name|String
name|startsWith
parameter_list|)
function_decl|;
comment|/**     * Find users by their username     */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findUsernamesWhereUsernameStarts
parameter_list|(
name|String
name|startsWith
parameter_list|)
function_decl|;
comment|/**     * Find all groups visible to the invokingUser     */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findAllGroupNames
parameter_list|()
function_decl|;
comment|/**     * Find all users visible to the invokingUser     */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findAllUserNames
parameter_list|()
function_decl|;
comment|/**     * Find groups by their group name     */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findGroupnamesWhereGroupnameStarts
parameter_list|(
name|String
name|startsWith
parameter_list|)
function_decl|;
comment|/**     * Find all members of a group     */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findAllGroupMembers
parameter_list|(
name|String
name|groupName
parameter_list|)
function_decl|;
comment|/**     * Process document, possible new sub-instance.     *       * @param document     * @throws ConfigurationException      */
name|void
name|processPramatter
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|ConfigurationException
function_decl|;
name|void
name|processPramatterBeforeSave
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|ConfigurationException
function_decl|;
comment|/**     * Particular web page for authentication.     *      * @return Authentication form location     */
specifier|public
name|String
name|getAuthenticationEntryPoint
parameter_list|()
function_decl|;
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findGroupnamesWhereGroupnameContains
parameter_list|(
name|String
name|fragment
parameter_list|)
function_decl|;
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|findUsernamesWhereNamePartStarts
parameter_list|(
name|String
name|startsWith
parameter_list|)
function_decl|;
name|Subject
name|getCurrentSubject
parameter_list|()
function_decl|;
comment|/**     * A receiver that is given the id of     * a security principal     */
specifier|public
interface|interface
name|PrincipalIdReceiver
block|{
comment|/**        * Callback function which received a Principal id        *        * @param id The id of the principal        */
specifier|public
name|void
name|allocate
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
function_decl|;
block|}
comment|/**     * Pre-allocates a new account id     *     * @param receiver A receiver that will receive the new account id     */
specifier|public
name|void
name|preAllocateAccountId
parameter_list|(
name|PrincipalIdReceiver
name|receiver
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
comment|/**     * Pre-allocates a new group id     *     * @param receiver A receiver that will receive the new group id     */
specifier|public
name|void
name|preAllocateGroupId
parameter_list|(
name|PrincipalIdReceiver
name|receiver
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
function_decl|;
block|}
end_interface

end_unit

