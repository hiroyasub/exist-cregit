begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
comment|/**  * SecurityManager is responsible for managing users and groups.  *   * There's only one SecurityManager for each database instance, which  * may be obtained by {@link BrokerPool#getSecurityManager()}.  *   * Users and groups are stored in the system collection, in document  * users.xml. While it is possible to edit this file by hand, it  * may lead to unexpected results, since SecurityManager reads   * users.xml only during database startup and shutdown.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SecurityManager
block|{
specifier|public
specifier|final
specifier|static
name|String
name|ACL_FILE
init|=
literal|"users.xml"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|ACL_FILE_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|ACL_FILE
argument_list|)
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
specifier|public
specifier|final
specifier|static
name|User
name|SYSTEM_USER
init|=
operator|new
name|UserImpl
argument_list|(
name|DBA_USER
argument_list|,
literal|null
argument_list|,
name|DBA_GROUP
argument_list|)
decl_stmt|;
comment|//TODO: add uid = 0 ?
specifier|public
specifier|final
specifier|static
name|User
name|GUEST
init|=
operator|new
name|UserImpl
argument_list|(
name|GUEST_USER
argument_list|,
literal|null
argument_list|,
name|GUEST_GROUP
argument_list|)
decl_stmt|;
comment|//TODO: add uid = 1 ?
name|void
name|attach
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|DBBroker
name|sysBroker
parameter_list|)
function_decl|;
name|boolean
name|isXACMLEnabled
parameter_list|()
function_decl|;
name|ExistPDP
name|getPDP
parameter_list|()
function_decl|;
name|void
name|deleteUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
name|void
name|deleteUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
name|UserImpl
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|User
name|getUser
parameter_list|(
name|int
name|uid
parameter_list|)
function_decl|;
name|User
index|[]
name|getUsers
parameter_list|()
function_decl|;
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
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
name|String
index|[]
name|getGroups
parameter_list|()
function_decl|;
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|User
name|user
parameter_list|)
function_decl|;
name|boolean
name|hasUser
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|// TODO: this should be addUser
name|void
name|setUser
parameter_list|(
name|User
name|user
parameter_list|)
function_decl|;
name|int
name|getResourceDefaultPerms
parameter_list|()
function_decl|;
name|int
name|getCollectionDefaultPerms
parameter_list|()
function_decl|;
specifier|public
name|User
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
name|User
name|authenticate
parameter_list|(
name|Realm
name|realm
parameter_list|,
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
name|User
name|getSystemAccount
parameter_list|()
function_decl|;
specifier|public
name|User
name|getGuestAccount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

