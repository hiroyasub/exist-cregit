begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|IOException
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
name|io
operator|.
name|VariableByteInput
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
name|io
operator|.
name|VariableByteOutputStream
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
name|int
name|DEFAULT_COLLECTION_PERM
init|=
literal|0777
decl_stmt|;
name|int
name|DEFAULT_RESOURCE_PERM
init|=
literal|0666
decl_stmt|;
name|int
name|DEFAULT_UMASK
init|=
literal|022
decl_stmt|;
name|int
name|DEFAULT_SYSTEM_COLLECTION_PERM
init|=
literal|0755
decl_stmt|;
name|int
name|DEFAULT_SYSTSEM_RESOURCE_PERM
init|=
literal|0770
decl_stmt|;
name|int
name|DEFAULT_SYSTEM_ETC_COLLECTION_PERM
init|=
literal|0755
decl_stmt|;
name|int
name|DEFAULT_SYSTEM_SECURITY_COLLECTION_PERM
init|=
literal|0770
decl_stmt|;
name|int
name|DEFAULT_TEMPORARY_COLLECTION_PERM
init|=
literal|0771
decl_stmt|;
name|int
name|DEFAULT_TEMPORARY_DOCUMENT_PERM
init|=
literal|0771
decl_stmt|;
name|int
name|SET_UID
init|=
literal|04
decl_stmt|;
name|int
name|SET_GID
init|=
literal|02
decl_stmt|;
name|int
name|STICKY
init|=
literal|01
decl_stmt|;
name|int
name|READ
init|=
literal|04
decl_stmt|;
name|int
name|WRITE
init|=
literal|02
decl_stmt|;
name|int
name|EXECUTE
init|=
literal|01
decl_stmt|;
name|String
name|USER_STRING
init|=
literal|"user"
decl_stmt|;
name|String
name|GROUP_STRING
init|=
literal|"group"
decl_stmt|;
name|String
name|OTHER_STRING
init|=
literal|"other"
decl_stmt|;
name|String
name|READ_STRING
init|=
literal|"read"
decl_stmt|;
name|String
name|WRITE_STRING
init|=
literal|"write"
decl_stmt|;
name|String
name|EXECUTE_STRING
init|=
literal|"execute"
decl_stmt|;
name|char
name|SETUID_CHAR
init|=
literal|'s'
decl_stmt|;
name|char
name|SETUID_CHAR_NO_EXEC
init|=
literal|'S'
decl_stmt|;
name|char
name|SETGID_CHAR
init|=
literal|'s'
decl_stmt|;
name|char
name|SETGID_CHAR_NO_EXEC
init|=
literal|'S'
decl_stmt|;
name|char
name|STICKY_CHAR
init|=
literal|'t'
decl_stmt|;
name|char
name|STICKY_CHAR_NO_EXEC
init|=
literal|'T'
decl_stmt|;
name|char
name|READ_CHAR
init|=
literal|'r'
decl_stmt|;
name|char
name|WRITE_CHAR
init|=
literal|'w'
decl_stmt|;
name|char
name|EXECUTE_CHAR
init|=
literal|'x'
decl_stmt|;
name|char
name|UNSET_CHAR
init|=
literal|'-'
decl_stmt|;
name|char
name|ALL_CHAR
init|=
literal|'a'
decl_stmt|;
name|char
name|USER_CHAR
init|=
literal|'u'
decl_stmt|;
name|char
name|GROUP_CHAR
init|=
literal|'g'
decl_stmt|;
name|char
name|OTHER_CHAR
init|=
literal|'o'
decl_stmt|;
name|int
name|getGroupMode
parameter_list|()
function_decl|;
comment|/**      * Gets the user who owns this resource      *      * @return The owner value      */
name|Account
name|getOwner
parameter_list|()
function_decl|;
comment|/**      * Gets the group       *      * @return The group value      */
name|Group
name|getGroup
parameter_list|()
function_decl|;
comment|/**      * Get the mode      *      * @return The mode value      */
name|int
name|getMode
parameter_list|()
function_decl|;
comment|/**      * Get the active mode for others      *      * @return The mode value      */
name|int
name|getOtherMode
parameter_list|()
function_decl|;
comment|/**      * Get the active mode for the owner      *      * @return The mode value      */
name|int
name|getOwnerMode
parameter_list|()
function_decl|;
comment|/**      * Set the owner group by group id      *      * @param  id  The group id      */
name|void
name|setGroup
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set the owner group      *      * @param  group  The group value      */
name|void
name|setGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set the owner group      *      * @param  name The group's name      */
name|void
name|setGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set the owner group      *       * This is used to set the owner group      * of this permission to the same      * as the owner group of the<i>other</i>      * permission.      *       * This is typically used in setGID situations.      *       * @param other Another permissions object      */
name|void
name|setGroupFrom
parameter_list|(
name|Permission
name|other
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Sets mode for group      *      * @param  perm  The new group mode value      */
name|void
name|setGroupMode
parameter_list|(
name|int
name|perm
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set the owner passed as account id      *      * @param  id  The new owner id      */
name|void
name|setOwner
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set the owner passed as User object      *      * @param  user  The new owner value      */
name|void
name|setOwner
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set the owner      *      * @param  user  The new owner value      */
name|void
name|setOwner
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      * Set mode using a string.      *       * The string can either be in one of three formats:      *        * 1) Unix Symbolic format as given to 'chmod' on Unix/Linux      * 2) eXist Symbolic format as described in @see org.exist.security.AbstractUnixStylePermission#setExistSymbolicMode(java.lang.String)      * 3) Simple Symbolic format e.g. "rwxr-xr-x"      *       * The eXist symbolic format should be avoided      * in new applications as it is deprecated      *       * @param modeStr The new mode      * @exception  SyntaxException  Description of the Exception      */
name|void
name|setMode
parameter_list|(
name|String
name|modeStr
parameter_list|)
throws|throws
name|SyntaxException
throws|,
name|PermissionDeniedException
function_decl|;
comment|/**      *  Set mode      *      *@param  mode  The new mode value      */
name|void
name|setMode
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      *  Set mode for others      *      *@param  perm  The new mode value      */
name|void
name|setOtherMode
parameter_list|(
name|int
name|perm
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      *  Set mode for the owner      *      *@param  other  The new mode value      */
name|void
name|setOwnerMode
parameter_list|(
name|int
name|other
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
name|boolean
name|isSetUid
parameter_list|()
function_decl|;
name|boolean
name|isSetGid
parameter_list|()
function_decl|;
name|boolean
name|isSticky
parameter_list|()
function_decl|;
name|void
name|setSetUid
parameter_list|(
name|boolean
name|setUid
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
name|void
name|setSetGid
parameter_list|(
name|boolean
name|setGid
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
name|void
name|setSticky
parameter_list|(
name|boolean
name|sticky
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
comment|/**      *  Check  if user has the requested mode for this resource.      *      *@param  user  The user      *@param  mode  The requested mode      *@return       true if user has the requested mode      */
name|boolean
name|validate
parameter_list|(
name|Subject
name|user
parameter_list|,
name|int
name|mode
parameter_list|)
function_decl|;
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|void
name|read
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|boolean
name|isCurrentSubjectDBA
parameter_list|()
function_decl|;
name|boolean
name|isCurrentSubjectOwner
parameter_list|()
function_decl|;
name|boolean
name|isCurrentSubjectInGroup
parameter_list|()
function_decl|;
name|boolean
name|isCurrentSubjectInGroup
parameter_list|(
name|int
name|groupId
parameter_list|)
function_decl|;
name|boolean
name|isPosixChownRestricted
parameter_list|()
function_decl|;
name|Permission
name|copy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

