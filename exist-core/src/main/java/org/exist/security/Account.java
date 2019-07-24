begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *   * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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

begin_interface
specifier|public
interface|interface
name|Account
extends|extends
name|User
block|{
specifier|public
specifier|final
specifier|static
name|int
name|UNDEFINED_ID
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Set the primary group of the user      * If the user is not already in the group      * they will also be added      *      * @param group The primary group      * @throws PermissionDeniedException is user has not sufficient rights      */
name|void
name|setPrimaryGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
name|void
name|assertCanModifyAccount
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Get the umask of the user      *      * @return The umask as an integer      */
specifier|public
name|int
name|getUserMask
parameter_list|()
function_decl|;
comment|/**      * Set the umask of the user      *      * @param umask The umask as an integer      */
specifier|public
name|void
name|setUserMask
parameter_list|(
specifier|final
name|int
name|umask
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

