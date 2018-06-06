begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|aider
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
name|security
operator|.
name|*
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

begin_comment
comment|/**  * Unix style permission details.  *   * @author Adam Retter<adam@exist-db.org>  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|UnixStylePermissionAider
extends|extends
name|AbstractUnixStylePermission
implements|implements
name|PermissionAider
block|{
comment|//owner, default to DBA
specifier|private
name|Account
name|owner
decl_stmt|;
specifier|private
name|Group
name|ownerGroup
decl_stmt|;
specifier|private
name|int
name|mode
decl_stmt|;
specifier|public
name|UnixStylePermissionAider
parameter_list|()
block|{
name|owner
operator|=
operator|new
name|UserAider
argument_list|(
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a Permission with given mode      *      * @param  mode  The mode      */
specifier|public
name|UnixStylePermissionAider
parameter_list|(
specifier|final
name|int
name|mode
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
comment|/**      * Construct a permission with given user, group and mode      *      * @param  user      * @param  group      * @param  mode      */
specifier|public
name|UnixStylePermissionAider
parameter_list|(
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|group
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
operator|new
name|UserAider
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|this
operator|.
name|ownerGroup
operator|=
operator|new
name|GroupAider
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSetGid
parameter_list|()
block|{
return|return
operator|(
operator|(
name|mode
operator|>>>
literal|9
operator|)
operator|&
name|SET_GID
operator|)
operator|==
name|SET_GID
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSetUid
parameter_list|()
block|{
return|return
operator|(
operator|(
name|mode
operator|>>>
literal|9
operator|)
operator|&
name|SET_UID
operator|)
operator|==
name|SET_UID
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSticky
parameter_list|()
block|{
return|return
operator|(
operator|(
name|mode
operator|>>>
literal|9
operator|)
operator|&
name|STICKY
operator|)
operator|==
name|STICKY
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSetUid
parameter_list|(
specifier|final
name|boolean
name|setUid
parameter_list|)
block|{
if|if
condition|(
name|setUid
condition|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
operator||
operator|(
name|SET_UID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mode
operator|=
name|mode
operator|&
operator|(
operator|~
operator|(
name|SET_UID
operator|<<
literal|9
operator|)
operator|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSetGid
parameter_list|(
specifier|final
name|boolean
name|setGid
parameter_list|)
block|{
if|if
condition|(
name|setGid
condition|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
operator||
operator|(
name|SET_GID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mode
operator|=
name|mode
operator|&
operator|(
operator|~
operator|(
name|SET_GID
operator|<<
literal|9
operator|)
operator|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSticky
parameter_list|(
specifier|final
name|boolean
name|sticky
parameter_list|)
block|{
if|if
condition|(
name|sticky
condition|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
operator||
operator|(
name|STICKY
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mode
operator|=
name|mode
operator|&
operator|(
operator|~
operator|(
name|STICKY
operator|<<
literal|9
operator|)
operator|)
expr_stmt|;
block|}
block|}
comment|/**      * Get the active mode for group      *      * @return The group mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getGroupMode
parameter_list|()
block|{
return|return
operator|(
name|mode
operator|&
literal|0x38
operator|)
operator|>>
literal|3
return|;
block|}
comment|/**      * Gets the user who owns this resource      *      * @return    The owner value      */
annotation|@
name|Override
specifier|public
name|Account
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**      * Gets the group       *      * @return    The ownerGroup value      */
annotation|@
name|Override
specifier|public
name|Group
name|getGroup
parameter_list|()
block|{
return|return
name|ownerGroup
return|;
block|}
comment|/**      * Get the mode      *      * @return    The mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
comment|/**      * Get the active mode for others      *      * @return    The other mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getOtherMode
parameter_list|()
block|{
return|return
name|mode
operator|&
literal|0x7
return|;
block|}
comment|/**      * Get the active mode for the owner      *      * @return    The user mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getOwnerMode
parameter_list|()
block|{
return|return
operator|(
name|mode
operator|&
literal|0x1c0
operator|)
operator|>>
literal|6
return|;
block|}
comment|/**      * Set the owner group      *      * @param  group  The group value      */
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
specifier|final
name|Group
name|group
parameter_list|)
block|{
name|this
operator|.
name|ownerGroup
operator|=
name|group
expr_stmt|;
block|}
comment|/**      * Set the owner group      *      * @param  group  The group name      */
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
specifier|final
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|ownerGroup
operator|=
operator|new
name|GroupAider
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroupFrom
parameter_list|(
name|Permission
name|other
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|this
operator|.
name|ownerGroup
operator|=
operator|new
name|GroupAider
argument_list|(
name|other
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Sets mode for group      *      *@param  groupMode  The new group mode value      */
annotation|@
name|Override
specifier|public
name|void
name|setGroupMode
parameter_list|(
specifier|final
name|int
name|groupMode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator||=
operator|(
name|groupMode
operator|<<
literal|3
operator|)
expr_stmt|;
block|}
comment|/**      *  Set the owner passed as User object      *      *@param  user  The new owner value      */
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
specifier|final
name|Account
name|user
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|user
expr_stmt|;
block|}
comment|/**      *  Set the owner      *      *@param  user  The new owner value      */
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
specifier|final
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
operator|new
name|UserAider
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Set mode      *      *@param  mode  The new mode value      */
annotation|@
name|Override
specifier|public
name|void
name|setMode
parameter_list|(
specifier|final
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
comment|/**      *  Set mode for others      *      *@param  otherMode  The new public mode value      */
annotation|@
name|Override
specifier|public
name|void
name|setOtherMode
parameter_list|(
specifier|final
name|int
name|otherMode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator||=
name|otherMode
expr_stmt|;
block|}
comment|/**      *  Set mode for the owner      *      *@param  ownerMode  The new owner mode value      */
annotation|@
name|Override
specifier|public
name|void
name|setOwnerMode
parameter_list|(
specifier|final
name|int
name|ownerMode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator||=
operator|(
name|ownerMode
operator|<<
literal|6
operator|)
expr_stmt|;
block|}
comment|/**      *  Format mode      *      *@return    Description of the Return Value      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|char
name|ch
index|[]
init|=
operator|new
name|char
index|[]
block|{
operator|(
name|mode
operator|&
operator|(
name|READ
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|READ_CHAR
block|,
operator|(
name|mode
operator|&
operator|(
name|WRITE
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|WRITE_CHAR
block|,
operator|(
name|mode
operator|&
operator|(
name|SET_UID
operator|<<
literal|9
operator|)
operator|)
operator|==
literal|0
condition|?
operator|(
operator|(
name|mode
operator|&
operator|(
name|EXECUTE
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|EXECUTE_CHAR
operator|)
else|:
operator|(
operator|(
name|mode
operator|&
operator|(
name|EXECUTE
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
condition|?
name|SETUID_CHAR_NO_EXEC
else|:
name|SETUID_CHAR
operator|)
block|,
operator|(
name|mode
operator|&
operator|(
name|READ
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|READ_CHAR
block|,
operator|(
name|mode
operator|&
operator|(
name|WRITE
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|WRITE_CHAR
block|,
operator|(
name|mode
operator|&
operator|(
name|SET_GID
operator|<<
literal|9
operator|)
operator|)
operator|==
literal|0
condition|?
operator|(
operator|(
name|mode
operator|&
operator|(
name|EXECUTE
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|EXECUTE_CHAR
operator|)
else|:
operator|(
operator|(
name|mode
operator|&
operator|(
name|EXECUTE
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
condition|?
name|SETGID_CHAR_NO_EXEC
else|:
name|SETGID_CHAR
operator|)
block|,
operator|(
name|mode
operator|&
name|READ
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|READ_CHAR
block|,
operator|(
name|mode
operator|&
name|WRITE
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|WRITE_CHAR
block|,
operator|(
name|mode
operator|&
operator|(
name|STICKY
operator|<<
literal|9
operator|)
operator|)
operator|==
literal|0
condition|?
operator|(
operator|(
name|mode
operator|&
name|EXECUTE
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|EXECUTE_CHAR
operator|)
else|:
operator|(
operator|(
name|mode
operator|&
name|EXECUTE
operator|)
operator|==
literal|0
condition|?
name|STICKY_CHAR_NO_EXEC
else|:
name|STICKY_CHAR
operator|)
block|}
decl_stmt|;
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|ch
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|UnixStylePermissionAider
name|fromString
parameter_list|(
specifier|final
name|String
name|modeStr
parameter_list|)
throws|throws
name|SyntaxException
block|{
if|if
condition|(
name|modeStr
operator|==
literal|null
operator|||
operator|!
operator|(
name|modeStr
operator|.
name|length
argument_list|()
operator|==
literal|9
operator|||
name|modeStr
operator|.
name|length
argument_list|()
operator|==
literal|12
operator|)
condition|)
block|{
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Invalid Permission String '"
operator|+
name|modeStr
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|int
name|mode
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|modeStr
operator|.
name|length
argument_list|()
condition|;
name|i
operator|=
name|i
operator|+
literal|3
control|)
block|{
for|for
control|(
specifier|final
name|char
name|c
range|:
name|modeStr
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|3
argument_list|)
operator|.
name|toCharArray
argument_list|()
control|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
name|READ_CHAR
case|:
name|mode
operator||=
operator|(
name|READ
operator|<<
operator|(
literal|6
operator|-
name|i
operator|)
operator|)
expr_stmt|;
break|break;
case|case
name|WRITE_CHAR
case|:
name|mode
operator||=
operator|(
name|WRITE
operator|<<
operator|(
literal|6
operator|-
name|i
operator|)
operator|)
expr_stmt|;
break|break;
case|case
name|EXECUTE_CHAR
case|:
name|mode
operator||=
operator|(
name|EXECUTE
operator|<<
operator|(
literal|6
operator|-
name|i
operator|)
operator|)
expr_stmt|;
break|break;
case|case
name|SETUID_CHAR
operator||
name|SETGID_CHAR
case|:
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|mode
operator||=
operator|(
name|SET_UID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
if|else if
condition|(
name|i
operator|==
literal|3
condition|)
block|{
name|mode
operator||=
operator|(
name|SET_GID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
name|mode
operator||=
operator|(
name|EXECUTE
operator|<<
operator|(
literal|6
operator|-
name|i
operator|)
operator|)
expr_stmt|;
break|break;
case|case
name|SETUID_CHAR_NO_EXEC
operator||
name|SETGID_CHAR_NO_EXEC
case|:
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|mode
operator||=
operator|(
name|SET_UID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
if|else if
condition|(
name|i
operator|==
literal|3
condition|)
block|{
name|mode
operator||=
operator|(
name|SET_GID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
break|break;
case|case
name|STICKY_CHAR
case|:
name|mode
operator||=
operator|(
name|STICKY
operator|<<
literal|9
operator|)
expr_stmt|;
name|mode
operator||=
operator|(
name|EXECUTE
operator|<<
operator|(
literal|6
operator|-
name|i
operator|)
operator|)
expr_stmt|;
break|break;
case|case
name|STICKY_CHAR_NO_EXEC
case|:
name|mode
operator||=
operator|(
name|STICKY
operator|<<
literal|9
operator|)
expr_stmt|;
break|break;
case|case
name|UNSET_CHAR
case|:
break|break;
default|default:
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Unknown char '"
operator|+
name|c
operator|+
literal|"' in mode string '"
operator|+
name|modeStr
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
operator|new
name|UnixStylePermissionAider
argument_list|(
name|mode
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validate
parameter_list|(
specifier|final
name|Subject
name|user
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Validation of Permission Aider is unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
name|ownerGroup
operator|=
operator|new
name|GroupAider
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
name|owner
operator|=
operator|new
name|UserAider
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|VariableByteOutputStream
name|ostream
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Serialization of permission Aider is unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|read
parameter_list|(
specifier|final
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"De-Serialization of permission Aider is unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectDBA
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectOwner
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectInGroup
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectInGroup
parameter_list|(
specifier|final
name|int
name|groupId
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Permission
name|copy
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

