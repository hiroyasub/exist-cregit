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
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|SyntaxException
import|;
end_import

begin_interface
specifier|public
interface|interface
name|Permission
block|{
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_PERM
init|=
literal|0755
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|READ
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|WRITE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UPDATE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|USER_STRING
init|=
literal|"user"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GROUP_STRING
init|=
literal|"group"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OTHER_STRING
init|=
literal|"other"
decl_stmt|;
specifier|public
name|int
name|getGroupPermissions
parameter_list|()
function_decl|;
comment|/**      * Gets the user who owns this resource      *      * @return The owner value      */
specifier|public
name|User
name|getOwner
parameter_list|()
function_decl|;
comment|/**      * Gets the group       *      * @return The ownerGroup value      */
specifier|public
name|Group
name|getOwnerGroup
parameter_list|()
function_decl|;
comment|/**      * Get the permissions      *      * @return The permissions value      */
specifier|public
name|int
name|getPermissions
parameter_list|()
function_decl|;
comment|/**      * Get the active permissions for others      *      * @return The publicPermissions value      */
specifier|public
name|int
name|getPublicPermissions
parameter_list|()
function_decl|;
comment|/**      * Get the active permissions for the owner      *      * @return The userPermissions value      */
specifier|public
name|int
name|getUserPermissions
parameter_list|()
function_decl|;
comment|/**      * Read the Permission from an input stream      *      * @param  istream          Description of the Parameter      * @exception  IOException  Description of the Exception      * @deprecated use one on implementation level      */
specifier|public
name|void
name|read
parameter_list|(
name|DataInput
name|istream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Set the owner group      *      * @param  group  The group value      */
specifier|public
name|void
name|setGroup
parameter_list|(
name|Group
name|group
parameter_list|)
function_decl|;
comment|/**      * Set the owner group      *      * @param  name The group's name      */
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Sets permissions for group      *      * @param  perm  The new groupPermissions value      */
specifier|public
name|void
name|setGroupPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
function_decl|;
comment|/**      * Set the owner passed as User object      *      * @param  user  The new owner value      */
specifier|public
name|void
name|setOwner
parameter_list|(
name|User
name|user
parameter_list|)
function_decl|;
comment|/**      * Set the owner      *      * @param  user  The new owner value      */
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**      *  Set permissions using a string. The string has the      * following syntax:      *       * [user|group|other]=[+|-][read|write|update]      *       * For example, to set read and write permissions for the group, but      * not for others:      *       * group=+read,+write,other=-read,-write      *       * The new settings are or'ed with the existing settings.      *       *@param  str                  The new permissions      *@exception  SyntaxException  Description of the Exception      */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|SyntaxException
function_decl|;
comment|/**      *  Set permissions      *      *@param  perm  The new permissions value      */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
function_decl|;
comment|/**      *  Set permissions for others      *      *@param  perm  The new publicPermissions value      */
specifier|public
name|void
name|setPublicPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
function_decl|;
comment|/**      *  Set permissions for the owner      *      *@param  perm  The new userPermissions value      */
specifier|public
name|void
name|setUserPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
function_decl|;
comment|/**      *  Format permissions       *      *@return    Description of the Return Value      */
specifier|public
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**      *  Check  if user has the requested permissions for this resource.      *      *@param  user  The user      *@param  perm  The requested permissions      *@return       true if user has the requested permissions      */
specifier|public
name|boolean
name|validate
parameter_list|(
name|User
name|user
parameter_list|,
name|int
name|perm
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

