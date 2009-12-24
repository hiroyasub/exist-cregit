begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id:  */
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
comment|/**  *  Manages the permissions assigned to a ressource. This includes  *  the user who owns the ressource, the owner group and the permissions  *  for user, group and others. Permissions are encoded in a single byte  *  according to common unix conventions.  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|UnixStylePermission
implements|implements
name|Permission
block|{
comment|//default Unix style permissions for a resource
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_PERM
init|=
literal|0755
decl_stmt|;
comment|//default UnixStylePermission
specifier|public
specifier|final
specifier|static
name|Permission
name|SYSTEM_DEFAULT
init|=
operator|new
name|UnixStylePermission
argument_list|(
name|DEFAULT_PERM
argument_list|)
decl_stmt|;
comment|//owner, default to DBA
specifier|private
name|String
name|owner
init|=
name|SecurityManager
operator|.
name|DBA_USER
decl_stmt|;
specifier|private
name|String
name|ownerGroup
init|=
name|SecurityManager
operator|.
name|DBA_GROUP
decl_stmt|;
comment|//permissions
specifier|private
name|int
name|permissions
init|=
name|DEFAULT_PERM
decl_stmt|;
specifier|public
name|UnixStylePermission
parameter_list|()
block|{
block|}
comment|/**      *  Construct a Permission with given permissions       *      *@param  permissions  Description of the Parameter      */
specifier|public
name|UnixStylePermission
parameter_list|(
name|int
name|permissions
parameter_list|)
block|{
name|this
operator|.
name|permissions
operator|=
name|permissions
expr_stmt|;
block|}
comment|/**      *  Construct a permission with given user, group and      *  permissions      *      *@param  user         Description of the Parameter      *@param  group        Description of the Parameter      *@param  permissions  Description of the Parameter      */
specifier|public
name|UnixStylePermission
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|permissions
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|ownerGroup
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|permissions
operator|=
name|permissions
expr_stmt|;
block|}
comment|/**      *  Get the active permissions for group      *      *@return    The groupPermissions value      */
specifier|public
name|int
name|getGroupPermissions
parameter_list|()
block|{
return|return
operator|(
name|permissions
operator|&
literal|0x38
operator|)
operator|>>
literal|3
return|;
block|}
comment|/**      *  Gets the user who owns this resource      *      *@return    The owner value      */
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**      *  Gets the group       *      *@return    The ownerGroup value      */
specifier|public
name|String
name|getOwnerGroup
parameter_list|()
block|{
return|return
name|ownerGroup
return|;
block|}
comment|/**      *  Get the permissions      *      *@return    The permissions value      */
specifier|public
name|int
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
comment|/**      *  Get the active permissions for others      *      *@return    The publicPermissions value      */
specifier|public
name|int
name|getPublicPermissions
parameter_list|()
block|{
return|return
name|permissions
operator|&
literal|0x7
return|;
block|}
comment|/**      *  Get the active permissions for the owner      *      *@return    The userPermissions value      */
specifier|public
name|int
name|getUserPermissions
parameter_list|()
block|{
return|return
operator|(
name|permissions
operator|&
literal|0x1c0
operator|)
operator|>>
literal|6
return|;
block|}
comment|/**      *  Read the Permission from an input stream      *      *@param  istream          Description of the Parameter      *@exception  IOException  Description of the Exception      */
specifier|public
name|void
name|read
parameter_list|(
name|DataInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|owner
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|ownerGroup
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|permissions
operator|=
name|istream
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Set the owner group      *      *@param  group  The new group value      */
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
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
comment|/**      *  Sets permissions for group      *      *@param  perm  The new groupPermissions value      */
specifier|public
name|void
name|setGroupPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
name|permissions
operator|=
name|permissions
operator||
operator|(
name|perm
operator|<<
literal|3
operator|)
expr_stmt|;
block|}
comment|/**      *  Set the owner passed as User object      *      *@param  user  The new owner value      */
specifier|public
name|void
name|setOwner
parameter_list|(
name|User
name|user
parameter_list|)
block|{
comment|// FIXME: assume guest identity if user gets lost due to a database corruption
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|owner
operator|=
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
block|}
else|else
name|this
operator|.
name|owner
operator|=
name|user
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|//this.ownerGroup = user.getPrimaryGroup();
block|}
comment|/**      *  Set the owner      *      *@param  user  The new owner value      */
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
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
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|str
argument_list|,
literal|",= "
argument_list|)
decl_stmt|;
name|String
name|token
decl_stmt|;
name|int
name|shift
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|equalsIgnoreCase
argument_list|(
name|USER_STRING
argument_list|)
condition|)
name|shift
operator|=
literal|6
expr_stmt|;
if|else if
condition|(
name|token
operator|.
name|equalsIgnoreCase
argument_list|(
name|GROUP_STRING
argument_list|)
condition|)
name|shift
operator|=
literal|3
expr_stmt|;
if|else if
condition|(
name|token
operator|.
name|equalsIgnoreCase
argument_list|(
name|OTHER_STRING
argument_list|)
condition|)
name|shift
operator|=
literal|0
expr_stmt|;
else|else
block|{
name|char
name|modifier
init|=
name|token
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|modifier
operator|==
literal|'+'
operator|||
name|modifier
operator|==
literal|'-'
operator|)
condition|)
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"expected modifier +|-"
argument_list|)
throw|;
else|else
name|token
operator|=
name|token
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"'read', 'write' or 'update' "
operator|+
literal|"expected in permission string"
argument_list|)
throw|;
name|int
name|perm
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"read"
argument_list|)
condition|)
name|perm
operator|=
name|READ
expr_stmt|;
if|else if
condition|(
name|token
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"write"
argument_list|)
condition|)
name|perm
operator|=
name|WRITE
expr_stmt|;
else|else
name|perm
operator|=
name|UPDATE
expr_stmt|;
switch|switch
condition|(
name|modifier
condition|)
block|{
case|case
literal|'+'
case|:
name|permissions
operator|=
name|permissions
operator||
operator|(
name|perm
operator|<<
name|shift
operator|)
expr_stmt|;
break|break;
default|default:
name|permissions
operator|=
name|permissions
operator|&
operator|(
operator|~
operator|(
name|perm
operator|<<
name|shift
operator|)
operator|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/**      *  Set permissions      *      *@param  perm  The new permissions value      */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
name|this
operator|.
name|permissions
operator|=
name|perm
expr_stmt|;
block|}
comment|/**      *  Set permissions for others      *      *@param  perm  The new publicPermissions value      */
specifier|public
name|void
name|setPublicPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
name|permissions
operator|=
name|permissions
operator||
name|perm
expr_stmt|;
block|}
comment|/**      *  Set permissions for the owner      *      *@param  perm  The new userPermissions value      */
specifier|public
name|void
name|setUserPermissions
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
name|permissions
operator|=
name|permissions
operator||
operator|(
name|perm
operator|<<
literal|6
operator|)
expr_stmt|;
block|}
comment|/**      *  Format permissions       *      *@return    Description of the Return Value      */
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
block|{
operator|(
name|permissions
operator|&
operator|(
name|READ
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'r'
block|,
operator|(
name|permissions
operator|&
operator|(
name|WRITE
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'w'
block|,
operator|(
name|permissions
operator|&
operator|(
name|UPDATE
operator|<<
literal|6
operator|)
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'u'
block|,
operator|(
name|permissions
operator|&
operator|(
name|READ
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'r'
block|,
operator|(
name|permissions
operator|&
operator|(
name|WRITE
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'w'
block|,
operator|(
name|permissions
operator|&
operator|(
name|UPDATE
operator|<<
literal|3
operator|)
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'u'
block|,
operator|(
name|permissions
operator|&
name|READ
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'r'
block|,
operator|(
name|permissions
operator|&
name|WRITE
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'w'
block|,
operator|(
name|permissions
operator|&
name|UPDATE
operator|)
operator|==
literal|0
operator|?
literal|'-'
operator|:
literal|'u'
block|}
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|ch
argument_list|)
return|;
block|}
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
block|{
comment|// group dba has full access
if|if
condition|(
name|user
operator|.
name|hasDbaRole
argument_list|()
condition|)
return|return
literal|true
return|;
comment|// check if the user owns this resource
if|if
condition|(
name|user
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|owner
argument_list|)
condition|)
return|return
name|validateUser
argument_list|(
name|perm
argument_list|)
return|;
comment|// check groups
name|String
index|[]
name|groups
init|=
name|user
operator|.
name|getGroups
argument_list|()
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
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|groups
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|ownerGroup
argument_list|)
condition|)
return|return
name|validateGroup
argument_list|(
name|perm
argument_list|)
return|;
block|}
comment|// finally, check public access rights
return|return
name|validatePublic
argument_list|(
name|perm
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|boolean
name|validateGroup
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
name|perm
operator|=
name|perm
operator|<<
literal|3
expr_stmt|;
return|return
operator|(
name|permissions
operator|&
name|perm
operator|)
operator|==
name|perm
return|;
block|}
specifier|private
specifier|final
name|boolean
name|validatePublic
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
return|return
operator|(
name|permissions
operator|&
name|perm
operator|)
operator|==
name|perm
return|;
block|}
specifier|private
specifier|final
name|boolean
name|validateUser
parameter_list|(
name|int
name|perm
parameter_list|)
block|{
name|perm
operator|=
name|perm
operator|<<
literal|6
expr_stmt|;
return|return
operator|(
name|permissions
operator|&
name|perm
operator|)
operator|==
name|perm
return|;
block|}
block|}
end_class

end_unit

