begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|security
operator|.
name|Principal
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

begin_interface
specifier|public
interface|interface
name|User
extends|extends
name|Principal
block|{
specifier|public
specifier|final
specifier|static
name|int
name|PLAIN_ENCODING
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SIMPLE_MD5_ENCODING
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MD5_ENCODING
init|=
literal|2
decl_stmt|;
comment|/** 	 *  Add the user to a group 	 * 	 *@param  group  The feature to be added to the Group attribute 	 */
specifier|public
name|void
name|addGroup
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
comment|/** 	 *  Remove the user to a group 	 *  Added by {Marco.Tampucci and Massimo.Martinelli}@isti.cnr.it   	 * 	 *@param  group  The feature to be removed to the Group attribute 	 */
specifier|public
name|void
name|remGroup
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
specifier|public
name|void
name|setGroups
parameter_list|(
name|String
index|[]
name|groups
parameter_list|)
function_decl|;
comment|/** 	 *  Get all groups this user belongs to 	 * 	 *@return    The groups value 	 */
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasDbaRole
parameter_list|()
function_decl|;
specifier|public
name|int
name|getUID
parameter_list|()
function_decl|;
comment|/** 	 *  Get the primary group this user belongs to 	 * 	 *@return    The primaryGroup value 	 */
specifier|public
name|String
name|getPrimaryGroup
parameter_list|()
function_decl|;
comment|/** 	 *  Is the user a member of group? 	 * 	 *@param  group  Description of the Parameter 	 *@return        Description of the Return Value 	 */
specifier|public
name|boolean
name|hasGroup
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
comment|/** 	 *  Sets the password attribute of the User object 	 * 	 * @param  passwd  The new password value 	 */
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
function_decl|;
specifier|public
name|void
name|setHome
parameter_list|(
name|XmldbURI
name|homeCollection
parameter_list|)
function_decl|;
specifier|public
name|XmldbURI
name|getHome
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|Object
name|credentials
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isAuthenticated
parameter_list|()
function_decl|;
specifier|public
name|Realm
name|getRealm
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

