begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id: Account.java 12494 2010-08-21 12:40:10Z shabanovd $  */
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
name|Set
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
name|realm
operator|.
name|Realm
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
annotation|@
name|Deprecated
comment|//use Account
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
comment|/** 	 * Add the user to a group 	 * 	 * @param  group  The feature to be added to the Group attribute 	 */
specifier|public
name|Group
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** 	 * Add the user to a group 	 * 	 * @param  group  The feature to be added to the Group attribute 	 */
specifier|public
name|Group
name|addGroup
parameter_list|(
name|Group
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
comment|/** 	 *  Get the primary group this user belongs to 	 * 	 *@return    The primaryGroup value 	 */
specifier|public
name|String
name|getPrimaryGroup
parameter_list|()
function_decl|;
specifier|public
name|Group
name|getDefaultGroup
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
name|Realm
name|getRealm
parameter_list|()
function_decl|;
comment|/** 	 * Get the user's password 	 *  	 * @return Description of the Return Value 	 * @deprecated 	 */
specifier|public
name|String
name|getPassword
parameter_list|()
function_decl|;
annotation|@
name|Deprecated
specifier|public
name|String
name|getDigestPassword
parameter_list|()
function_decl|;
annotation|@
name|Deprecated
specifier|public
name|void
name|setGroups
parameter_list|(
name|String
index|[]
name|groups
parameter_list|)
function_decl|;
comment|/**      * Add a named attribute.      *      * @param name      * @param value      */
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Get the named attribute value.      *      * @param name The String that is the name of the attribute.      * @return The value associated with the name or null if no value is associated with the name.      */
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns the set of attributes names.      *      * @return the Set of attribute names.      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAttributeNames
parameter_list|()
function_decl|;
comment|/**      * Returns the person full name or account name.      *      * @return the person full name or account name      */
name|String
name|getUsername
parameter_list|()
function_decl|;
comment|/**      * Indicates whether the account has expired. Authentication on an expired account is not possible.      *      * @return<code>true</code> if the account is valid (ie non-expired),<code>false</code> if no longer valid (ie expired)      */
name|boolean
name|isAccountNonExpired
parameter_list|()
function_decl|;
comment|/**      * Indicates whether the account is locked or unlocked. Authentication on a locked account is not possible.      *      * @return<code>true</code> if the account is not locked,<code>false</code> otherwise      */
name|boolean
name|isAccountNonLocked
parameter_list|()
function_decl|;
comment|/**      * Indicates whether the account's credentials has expired. Expired credentials prevent authentication.      *      * @return<code>true</code> if the account's credentials are valid (ie non-expired),<code>false</code> if no longer valid (ie expired)      */
name|boolean
name|isCredentialsNonExpired
parameter_list|()
function_decl|;
comment|/**      * Indicates whether the account is enabled or disabled. Authentication on a disabled account is not possible.      *      * @return<code>true</code> if the account is enabled,<code>false</code> otherwise      */
name|boolean
name|isEnabled
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

