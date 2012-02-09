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
import|import static
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionRequired
operator|.
name|IS_DBA
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionRequired
operator|.
name|IS_OWNER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionRequired
operator|.
name|IS_MEMBER
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
name|RealmImpl
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

begin_comment
comment|/**  * Manages the permissions assigned to a resource. This includes  * the user who owns the resource, the owner group and the permissions  * for owner, group and others.  *  * Permissions are encoded into a 52 bit vector with the following convention -  *  * [userId(20),setUid(1),userMode(rwx)(3),groupId(20),setGid(1),groupMode(rwx)(3),sticky(1),otherMode(rwx)(3)]  * @see UnixStylePermission.encodeAsBitVector(int, int, int) for more details  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|UnixStylePermission
extends|extends
name|AbstractUnixStylePermission
implements|implements
name|Permission
block|{
specifier|protected
name|SecurityManager
name|sm
decl_stmt|;
specifier|protected
name|long
name|vector
init|=
name|encodeAsBitVector
argument_list|(
name|RealmImpl
operator|.
name|SYSTEM_ACCOUNT_ID
argument_list|,
name|RealmImpl
operator|.
name|DBA_GROUP_ID
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|UnixStylePermission
parameter_list|(
name|SecurityManager
name|sm
parameter_list|)
block|{
if|if
condition|(
name|sm
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Security manager can't be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|sm
operator|=
name|sm
expr_stmt|;
block|}
comment|/**      * Construct a permission with given user, group and permissions      *      * @param  invokingUser Description of the Parameter      * @param  sm           Description of the Parameter      * @param  user         Description of the Parameter      * @param  group        Description of the Parameter      * @param  mode  Description of the Parameter      */
specifier|public
name|UnixStylePermission
parameter_list|(
name|SecurityManager
name|sm
parameter_list|,
name|int
name|ownerId
parameter_list|,
name|int
name|groupId
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|this
argument_list|(
name|sm
argument_list|)
expr_stmt|;
name|this
operator|.
name|vector
operator|=
name|encodeAsBitVector
argument_list|(
name|ownerId
argument_list|,
name|groupId
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Gets the user who owns this resource      *      * @return The owner value      */
annotation|@
name|Override
specifier|public
name|Account
name|getOwner
parameter_list|()
block|{
return|return
name|sm
operator|.
name|getAccount
argument_list|(
name|getOwnerId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|int
name|getOwnerId
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
name|vector
operator|>>>
literal|32
operator|)
return|;
block|}
comment|/**      *  Set the owner passed as User object      *      *@param  account  The new owner value      */
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|Account
name|account
parameter_list|)
block|{
comment|//assume SYSTEM identity if user gets lost due to a database corruption - WTF???
comment|//TODO this should eventually be replaced with a PermissionDeniedException
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|account
operator|=
name|sm
operator|.
name|getSystemSubject
argument_list|()
expr_stmt|;
block|}
name|int
name|accountId
init|=
name|account
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|accountId
operator|!=
name|getOwnerId
argument_list|()
condition|)
block|{
name|setOwnerId
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|Account
name|account
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|id
argument_list|)
decl_stmt|;
comment|//assume SYSTEM identity if user gets lost due to a database corruption - WTF???
comment|//TODO this should eventually be replaced with a PermissionDeniedException
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|account
operator|=
name|sm
operator|.
name|getSystemSubject
argument_list|()
expr_stmt|;
block|}
name|int
name|accountId
init|=
name|account
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|accountId
operator|!=
name|getOwnerId
argument_list|()
condition|)
block|{
name|setOwnerId
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Set the owner      *      *@param  name  The new owner value      */
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Account
name|account
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|invokingUser
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
name|int
name|accountId
init|=
name|account
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|accountId
operator|!=
name|getOwnerId
argument_list|()
condition|)
block|{
name|setOwnerId
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|setOwner
argument_list|(
literal|null
argument_list|,
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
name|Account
name|user
parameter_list|)
block|{
name|setOwner
argument_list|(
literal|null
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|setOwner
argument_list|(
literal|null
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
argument_list|)
specifier|private
name|void
name|setOwnerId
parameter_list|(
name|int
name|ownerId
parameter_list|)
block|{
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|long
operator|)
name|ownerId
operator|<<
literal|32
operator|)
operator||
comment|//left shift new ownerId into position
operator|(
name|vector
operator|&
literal|4294967295L
operator|)
expr_stmt|;
comment|//extract everything from current permission except ownerId
block|}
comment|/**      *  Gets the group       *      *@return    The group value      */
annotation|@
name|Override
specifier|public
name|Group
name|getGroup
parameter_list|()
block|{
return|return
name|sm
operator|.
name|getGroup
argument_list|(
name|getGroupId
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|int
name|getGroupId
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
operator|(
name|vector
operator|>>>
literal|8
operator|)
operator|&
literal|1048575
operator|)
return|;
block|}
comment|/**      *  Set the owner group      *      *@param  groupName  The new group value      */
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|String
name|groupName
parameter_list|)
block|{
name|Group
name|group
init|=
name|sm
operator|.
name|getGroup
argument_list|(
name|invokingUser
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|setGroupId
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|Group
name|group
parameter_list|)
block|{
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|setGroupId
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|Group
name|group
init|=
name|sm
operator|.
name|getGroup
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|group
operator|=
name|sm
operator|.
name|getDBAGroup
argument_list|()
expr_stmt|;
comment|//TODO is this needed?
block|}
name|setGroupId
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|setGroup
argument_list|(
literal|null
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
name|Group
name|group
parameter_list|)
block|{
name|setGroup
argument_list|(
literal|null
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|setGroup
argument_list|(
literal|null
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
specifier|private
name|void
name|setGroupId
parameter_list|(
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_MEMBER
argument_list|)
name|int
name|groupId
parameter_list|)
block|{
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|28
operator|)
operator|<<
literal|28
operator|)
operator||
comment|//current ownerId and ownerMode, mask rest
operator|(
name|groupId
operator|<<
literal|8
operator|)
operator||
comment|//left shift new groupId into positon
operator|(
name|vector
operator|&
literal|255
operator|)
expr_stmt|;
comment|//current groupMode and otherMode
block|}
comment|/**      *  Get the mode      *      *@return    The mode      */
annotation|@
name|Override
specifier|public
name|int
name|getMode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
operator|(
operator|(
operator|(
name|vector
operator|>>>
literal|31
operator|)
operator|&
literal|1
operator|)
operator|<<
literal|11
operator|)
operator||
operator|(
operator|(
operator|(
name|vector
operator|>>>
literal|7
operator|)
operator|&
literal|1
operator|)
operator|<<
literal|10
operator|)
operator||
operator|(
operator|(
operator|(
name|vector
operator|>>>
literal|3
operator|)
operator|&
literal|1
operator|)
operator|<<
literal|9
operator|)
operator||
comment|//setUid | setGid | sticky
operator|(
operator|(
operator|(
operator|(
name|vector
operator|>>>
literal|28
operator|)
operator|&
literal|7
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
operator|(
operator|(
name|vector
operator|>>>
literal|4
operator|)
operator|&
literal|7
operator|)
operator|<<
literal|3
operator|)
operator||
operator|(
name|vector
operator|&
literal|7
operator|)
operator|)
operator|)
return|;
comment|//userPerm | groupPerm | otherPerm
block|}
comment|/**      *  Set the mode      *      *@param  mode  The new mode value      */
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|final
specifier|public
name|void
name|setMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|32
operator|)
operator|<<
literal|32
operator|)
operator||
comment|//left shift current ownerId into position
operator|(
operator|(
name|long
operator|)
operator|(
operator|(
name|mode
operator|>>>
literal|11
operator|)
operator|&
literal|1
operator|)
operator|<<
literal|31
operator|)
operator||
comment|//left shift setuid into position
operator|(
operator|(
operator|(
operator|(
name|mode
operator|>>>
literal|6
operator|)
operator|&
literal|7
operator|)
operator|)
operator|<<
literal|28
operator|)
operator||
comment|//left shift new ownerMode into position
operator|(
operator|(
operator|(
name|vector
operator|>>>
literal|8
operator|)
operator|&
literal|1048575
operator|)
operator|<<
literal|8
operator|)
operator||
comment|//left shift current groupId into position
operator|(
operator|(
operator|(
name|mode
operator|>>>
literal|10
operator|)
operator|&
literal|1
operator|)
operator|<<
literal|7
operator|)
operator||
comment|//left shift setgid into position
operator|(
operator|(
operator|(
name|mode
operator|>>>
literal|3
operator|)
operator|&
literal|7
operator|)
operator|<<
literal|4
operator|)
operator||
comment|//left shift new groupMode into position
operator|(
operator|(
operator|(
name|mode
operator|>>>
literal|9
operator|)
operator|&
literal|1
operator|)
operator|<<
literal|3
operator|)
operator||
comment|//left shift sticky into position
operator|(
name|mode
operator|&
literal|7
operator|)
expr_stmt|;
comment|//new otherMode
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
name|vector
operator|>>>
literal|31
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
return|;
block|}
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setSetUid
parameter_list|(
name|boolean
name|setUid
parameter_list|)
block|{
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|32
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|setUid
condition|?
literal|1
else|:
literal|0
operator|)
operator||
operator|(
name|vector
operator|&
literal|2147483647
operator|)
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
name|vector
operator|>>>
literal|7
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
return|;
block|}
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setSetGid
parameter_list|(
name|boolean
name|setGid
parameter_list|)
block|{
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|8
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|setGid
condition|?
literal|1
else|:
literal|0
operator|)
operator||
operator|(
name|vector
operator|&
literal|127
operator|)
expr_stmt|;
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
name|vector
operator|>>>
literal|3
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
return|;
block|}
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setSticky
parameter_list|(
name|boolean
name|sticky
parameter_list|)
block|{
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|4
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|sticky
condition|?
literal|1
else|:
literal|0
operator|)
operator||
operator|(
name|vector
operator|&
literal|7
operator|)
expr_stmt|;
block|}
comment|/**      *  Get the active mode for the owner      *      *@return    The mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getOwnerMode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
operator|(
name|vector
operator|>>>
literal|28
operator|)
operator|&
literal|7
operator|)
return|;
block|}
comment|/**      *  Set mode for the owner      *      *@param  mode  The new owner mode value      */
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setOwnerMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|mode
operator|=
name|mode
operator|&
literal|7
expr_stmt|;
comment|//ensure its only 3 bits
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|31
operator|)
operator|<<
literal|31
operator|)
operator||
comment|//left shift current ownerId and setuid into position
operator|(
name|mode
operator|<<
literal|28
operator|)
operator||
comment|//left shift new ownerMode into position
operator|(
name|vector
operator|&
literal|268435455
operator|)
expr_stmt|;
comment|//extract everything else from current permission except ownerId and ownerMode
block|}
comment|/**      *  Get the mode for group      *      *@return    The mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getGroupMode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
operator|(
name|vector
operator|>>>
literal|4
operator|)
operator|&
literal|7
operator|)
return|;
block|}
comment|/**      *  Sets mode for group      *      *@param  mode  The new mode value      */
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setGroupMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|mode
operator|=
name|mode
operator|&
literal|7
expr_stmt|;
comment|//ensure its only 3 bits
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|7
operator|)
operator|<<
literal|7
operator|)
operator||
comment|//left shift current ownerId, setuid, ownerMode, groupId and setgid into position
operator|(
name|mode
operator|<<
literal|4
operator|)
operator||
comment|//left shift new groupMode into position
operator|(
name|vector
operator|&
literal|15
operator|)
expr_stmt|;
comment|//current sticky and otherMode
block|}
comment|/**      *  Get the mode for others      *      *@return    The mode value      */
annotation|@
name|Override
specifier|public
name|int
name|getOtherMode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
name|vector
operator|&
literal|7
operator|)
return|;
block|}
comment|/**      *  Set mode for others      *      *@param  mode  The new other mode value      */
annotation|@
name|PermissionRequired
argument_list|(
name|user
operator|=
name|IS_DBA
operator||
name|IS_OWNER
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setOtherMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|mode
operator|=
name|mode
operator|&
literal|7
expr_stmt|;
comment|//ensure its only 3 bits
name|this
operator|.
name|vector
operator|=
operator|(
operator|(
name|vector
operator|>>>
literal|3
operator|)
operator|<<
literal|3
operator|)
operator||
comment|//left shift current ownerId, ownerMode, groupId and groupMode into position
name|mode
expr_stmt|;
comment|//new otherMode
block|}
comment|/**      *  Format mode      *      *@return the mode formatted as a string e.g. 'rwxrwxrwx'      */
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
name|vector
operator|&
operator|(
name|READ
operator|<<
literal|28
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
name|vector
operator|&
operator|(
name|WRITE
operator|<<
literal|28
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
name|vector
operator|&
operator|(
literal|1L
operator|<<
literal|31
operator|)
operator|)
operator|==
literal|0
condition|?
operator|(
operator|(
name|vector
operator|&
operator|(
name|EXECUTE
operator|<<
literal|28
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
name|SETUID_CHAR
block|,
operator|(
name|vector
operator|&
operator|(
name|READ
operator|<<
literal|4
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
name|vector
operator|&
operator|(
name|WRITE
operator|<<
literal|4
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
name|vector
operator|&
operator|(
literal|1
operator|<<
literal|7
operator|)
operator|)
operator|==
literal|0
condition|?
operator|(
operator|(
name|vector
operator|&
operator|(
name|EXECUTE
operator|<<
literal|4
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
name|SETGID_CHAR
block|,
operator|(
name|vector
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
name|vector
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
name|vector
operator|&
operator|(
literal|1
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
condition|?
operator|(
operator|(
name|vector
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
name|STICKY_CHAR
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
comment|/**      *  Check  if user has the requested mode for this resource.      *      *@param  user  The user      *@param  mode  The requested mode      *@return       true if user has the requested mode      */
annotation|@
name|Override
specifier|public
name|boolean
name|validate
parameter_list|(
name|Subject
name|user
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
comment|//group dba has full access
if|if
condition|(
name|user
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|//check owner
if|if
condition|(
name|user
operator|.
name|getId
argument_list|()
operator|==
operator|(
name|vector
operator|>>>
literal|32
operator|)
condition|)
block|{
comment|//check owner
return|return
operator|(
name|mode
operator|&
operator|(
operator|(
name|vector
operator|>>>
literal|28
operator|)
operator|&
literal|7
operator|)
operator|)
operator|==
name|mode
return|;
comment|//check owner mode
block|}
comment|//check group
name|int
name|userGroupIds
index|[]
init|=
name|user
operator|.
name|getGroupIds
argument_list|()
decl_stmt|;
name|int
name|groupId
init|=
operator|(
name|int
operator|)
operator|(
operator|(
name|vector
operator|>>>
literal|8
operator|)
operator|&
literal|1048575
operator|)
decl_stmt|;
for|for
control|(
name|int
name|userGroupId
range|:
name|userGroupIds
control|)
block|{
if|if
condition|(
name|userGroupId
operator|==
name|groupId
condition|)
block|{
return|return
operator|(
name|mode
operator|&
operator|(
operator|(
name|vector
operator|>>>
literal|4
operator|)
operator|&
literal|7
operator|)
operator|)
operator|==
name|mode
return|;
block|}
block|}
comment|//check other
if|if
condition|(
operator|(
name|mode
operator|&
operator|(
name|vector
operator|&
literal|7
operator|)
operator|)
operator|==
name|mode
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
comment|/*         if((permissionCheck& vector) == permissionCheck) { //check user and user mode             return true;    //matched both the username and the mode         } else if(((permissionCheck>>> 29)& (vector>>> 29)) == permissionCheck>>> 29) {             ///prevents fall-through, i.e. if the username matches and the mode didnt then stop comparrisons             return false;         }          //check group         int userGroupIds[] = user.getGroupIds();         for(int userGroupId : userGroupIds) {             permissionCheck = encodeAsBitVector(0, userGroupId, mode<< 3);             if((permissionCheck& vector) == permissionCheck) { //check group and group mode                 return true;             } else if((((permissionCheck>> 6)& 1048575)& ((vector>> 6)& 1048575)) == ((permissionCheck>> 6)& 1048575)) {                 ///prevents fall-through, i.e. if the grouname matches and the mode didnt then stop comparrisons                 return false;             }         }                  //check other         permissionCheck = encodeAsBitVector(0, 0, mode); //check other mode         if((permissionCheck& vector) == permissionCheck) {             return true;         }                  return false;*/
block|}
annotation|@
name|Override
specifier|public
name|void
name|read
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|vector
operator|=
name|istream
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|ostream
operator|.
name|writeLong
argument_list|(
name|vector
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|final
name|long
name|getVector
parameter_list|()
block|{
return|return
name|vector
return|;
block|}
comment|/**      * should return max of 52 bits - e.g. The maximum numeric value - 4503599627370495      * exact encoding is [userId(20),setUid(1),userMode(rwx)(3),groupId(20),setGid(1),groupMode(rwx)(3),sticky(1),otherMode(rwx)(3)]      */
specifier|protected
specifier|final
name|long
name|encodeAsBitVector
parameter_list|(
name|int
name|userId
parameter_list|,
name|int
name|groupId
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
comment|//makes sure mode is only 12 bits max - TODO maybe error if not 10 bits
name|mode
operator|=
name|mode
operator|&
literal|4095
expr_stmt|;
comment|//makes sure userId is only 20 bits max - TODO maybe error if not 20 bits
name|userId
operator|=
name|userId
operator|&
literal|1048575
expr_stmt|;
comment|//makes sure groupId is only 20 bits max - TODO maybe error if not 20 bits
name|groupId
operator|=
name|groupId
operator|&
literal|1048575
expr_stmt|;
name|int
name|setUid
init|=
operator|(
name|mode
operator|>>>
literal|11
operator|)
operator|&
literal|1
decl_stmt|;
name|int
name|setGid
init|=
operator|(
name|mode
operator|>>>
literal|10
operator|)
operator|&
literal|1
decl_stmt|;
name|int
name|sticky
init|=
operator|(
name|mode
operator|>>>
literal|9
operator|)
operator|&
literal|1
decl_stmt|;
name|int
name|userPerm
init|=
operator|(
name|mode
operator|>>>
literal|6
operator|)
operator|&
literal|7
decl_stmt|;
name|int
name|groupPerm
init|=
operator|(
name|mode
operator|>>>
literal|3
operator|)
operator|&
literal|7
decl_stmt|;
name|int
name|otherPerm
init|=
name|mode
operator|&
literal|7
decl_stmt|;
return|return
operator|(
operator|(
name|long
operator|)
name|userId
operator|<<
literal|32
operator|)
operator||
operator|(
operator|(
name|long
operator|)
name|setUid
operator|<<
literal|31
operator|)
operator||
operator|(
name|userPerm
operator|<<
literal|28
operator|)
operator||
operator|(
name|groupId
operator|<<
literal|8
operator|)
operator||
operator|(
name|setGid
operator|<<
literal|7
operator|)
operator||
operator|(
name|groupPerm
operator|<<
literal|4
operator|)
operator||
operator|(
name|sticky
operator|<<
literal|3
operator|)
operator||
name|otherPerm
return|;
block|}
specifier|protected
name|Subject
name|getCurrentSubject
parameter_list|()
block|{
return|return
name|sm
operator|.
name|getDatabase
argument_list|()
operator|.
name|getSubject
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectDBA
parameter_list|()
block|{
return|return
name|getCurrentSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectOwner
parameter_list|()
block|{
return|return
name|getCurrentSubject
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
name|getOwnerId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectInGroup
parameter_list|()
block|{
specifier|final
name|int
name|groupId
init|=
name|getGroupId
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|currentSubjectGroupId
range|:
name|getCurrentSubject
argument_list|()
operator|.
name|getGroupIds
argument_list|()
control|)
block|{
if|if
condition|(
name|groupId
operator|==
name|currentSubjectGroupId
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
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
for|for
control|(
name|int
name|currentSubjectGroupId
range|:
name|getCurrentSubject
argument_list|()
operator|.
name|getGroupIds
argument_list|()
control|)
block|{
if|if
condition|(
name|currentSubjectGroupId
operator|==
name|groupId
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

