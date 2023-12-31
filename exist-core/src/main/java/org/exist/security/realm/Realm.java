begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|realm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|LifeCycle
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
name|SecurityManager
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
name|management
operator|.
name|AccountsManagement
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
name|management
operator|.
name|GroupsManagement
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Realm
extends|extends
name|AuthenticatingRealm
extends|,
name|AuthorizingRealm
extends|,
name|AccountsManagement
extends|,
name|GroupsManagement
extends|,
name|LifeCycle
block|{
name|String
name|getId
parameter_list|()
function_decl|;
name|Collection
argument_list|<
name|Account
argument_list|>
name|getAccounts
parameter_list|()
function_decl|;
name|Collection
argument_list|<
name|Group
argument_list|>
name|getGroups
parameter_list|()
function_decl|;
name|Database
name|getDatabase
parameter_list|()
function_decl|;
name|Group
name|getExternalGroup
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
function_decl|;
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
name|List
argument_list|<
name|String
argument_list|>
name|findAllGroupNames
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|findAllGroupMembers
parameter_list|(
specifier|final
name|String
name|groupName
parameter_list|)
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|findAllUserNames
parameter_list|()
function_decl|;
name|SecurityManager
name|getSecurityManager
parameter_list|()
function_decl|;
name|Collection
argument_list|<
name|?
extends|extends
name|String
argument_list|>
name|findGroupnamesWhereGroupnameStarts
parameter_list|(
name|String
name|startsWith
parameter_list|)
function_decl|;
name|Collection
argument_list|<
name|?
extends|extends
name|String
argument_list|>
name|findGroupnamesWhereGroupnameContains
parameter_list|(
name|String
name|fragment
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

