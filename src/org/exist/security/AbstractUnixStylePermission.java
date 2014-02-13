begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2013 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id: UnixStylePermission.java 14571 2011-05-29 12:34:48Z deliriumsky $  */
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
comment|/**  * All code in this class must be side-effect free  * and not carry state, thus ensuring that thus class  * can be used in a local or remote scenario  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractUnixStylePermission
implements|implements
name|Permission
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractUnixStylePermission
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The symbolic mode is described by the following grammar:      *      * mode         ::= clause [, clause ...]      * clause       ::= [who ...] [action ...] action      * action       ::= op [perm ...]      * who          ::= a | u | g | o      * op           ::= + | - | =      * perm         ::= r | s | t | w | x      */
specifier|private
name|void
name|setUnixSymbolicMode
parameter_list|(
specifier|final
name|String
name|symbolicMode
parameter_list|)
throws|throws
name|SyntaxException
throws|,
name|PermissionDeniedException
block|{
comment|//TODO expand perm to full UNIX chmod i.e. perm ::= r | s | t | w | x | X | u | g | o
specifier|final
name|String
name|clauses
index|[]
init|=
name|symbolicMode
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|clause
range|:
name|clauses
control|)
block|{
specifier|final
name|String
name|whoPerm
index|[]
init|=
name|clause
operator|.
name|split
argument_list|(
literal|"[+\\-=]"
argument_list|)
decl_stmt|;
name|int
name|perm
init|=
literal|0
decl_stmt|;
name|boolean
name|uidgid
init|=
literal|false
decl_stmt|;
name|boolean
name|sticky
init|=
literal|false
decl_stmt|;
comment|//process the op first
for|for
control|(
specifier|final
name|char
name|c
range|:
name|whoPerm
index|[
literal|1
index|]
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
name|perm
operator||=
name|READ
expr_stmt|;
break|break;
case|case
name|WRITE_CHAR
case|:
name|perm
operator||=
name|WRITE
expr_stmt|;
break|break;
case|case
name|EXECUTE_CHAR
case|:
name|perm
operator||=
name|EXECUTE
expr_stmt|;
break|break;
case|case
name|SETUID_CHAR
case|:
name|uidgid
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|STICKY_CHAR
case|:
name|sticky
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Unrecognised mode char '"
operator|+
name|c
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|char
name|whoose
index|[]
decl_stmt|;
if|if
condition|(
name|whoPerm
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|whoose
operator|=
name|whoPerm
index|[
literal|0
index|]
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|whoose
operator|=
operator|new
name|char
index|[]
block|{
name|ALL_CHAR
block|}
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|char
name|c
range|:
name|whoose
control|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
name|ALL_CHAR
case|:
specifier|final
name|int
name|newMode
init|=
operator|(
name|perm
operator|<<
literal|6
operator|)
operator||
operator|(
name|perm
operator|<<
literal|3
operator|)
operator||
name|perm
operator||
operator|(
name|sticky
condition|?
operator|(
name|STICKY
operator|<<
literal|9
operator|)
else|:
literal|0
operator|)
operator||
operator|(
name|uidgid
condition|?
operator|(
operator|(
name|SET_UID
operator||
name|SET_GID
operator|)
operator|<<
literal|9
operator|)
else|:
literal|0
operator|)
decl_stmt|;
if|if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"+"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setMode
argument_list|(
name|getMode
argument_list|()
operator||
name|newMode
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setMode
argument_list|(
name|getMode
argument_list|()
operator|&
operator|~
name|newMode
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setMode
argument_list|(
name|newMode
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|USER_CHAR
case|:
if|if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"+"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setOwnerMode
argument_list|(
name|getOwnerMode
argument_list|()
operator||
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|uidgid
condition|)
block|{
name|setSetUid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setOwnerMode
argument_list|(
name|getOwnerMode
argument_list|()
operator|&
operator|~
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|uidgid
condition|)
block|{
name|setSetUid
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setOwnerMode
argument_list|(
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|uidgid
condition|)
block|{
name|setSetUid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|GROUP_CHAR
case|:
if|if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"+"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setGroupMode
argument_list|(
name|getGroupMode
argument_list|()
operator||
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|uidgid
condition|)
block|{
name|setSetGid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setGroupMode
argument_list|(
name|getGroupMode
argument_list|()
operator|&
operator|~
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|uidgid
condition|)
block|{
name|setSetGid
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setGroupMode
argument_list|(
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|uidgid
condition|)
block|{
name|setSetGid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|OTHER_CHAR
case|:
if|if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"+"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setOtherMode
argument_list|(
name|getOtherMode
argument_list|()
operator||
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|sticky
condition|)
block|{
name|setSticky
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"-"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setOtherMode
argument_list|(
name|getOtherMode
argument_list|()
operator|&
operator|~
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|sticky
condition|)
block|{
name|setSticky
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|clause
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|setOtherMode
argument_list|(
name|perm
argument_list|)
expr_stmt|;
if|if
condition|(
name|sticky
condition|)
block|{
name|setSticky
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Unrecognised mode char '"
operator|+
name|c
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
name|perm
operator|=
literal|0
expr_stmt|;
name|uidgid
operator|=
literal|false
expr_stmt|;
name|sticky
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      * Set mode using a string. The string has the      * following syntax:      *      * [user|group|other]=[+|-][read|write|execute]      *      * For example, to set read and write mode for the group, but      * not for others:      *      * group=+read,+write,other=-read,-write      *      * The new settings are or'ed with the existing settings.      *      *@param  existSymbolicMode                  The new mode      *@exception  SyntaxException  Description of the Exception      *      * @deprecated setUnixSymbolicMode should be used instead      */
annotation|@
name|Deprecated
specifier|private
name|void
name|setExistSymbolicMode
parameter_list|(
specifier|final
name|String
name|existSymbolicMode
parameter_list|)
throws|throws
name|SyntaxException
throws|,
name|PermissionDeniedException
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Permission modes should not be set using this format '"
operator|+
name|existSymbolicMode
operator|+
literal|"', consider using the UNIX symbolic mode instead"
argument_list|)
expr_stmt|;
name|int
name|shift
init|=
literal|0
decl_stmt|;
name|int
name|mode
init|=
name|getMode
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|s
range|:
name|existSymbolicMode
operator|.
name|toLowerCase
argument_list|()
operator|.
name|split
argument_list|(
literal|"=|,"
argument_list|)
control|)
block|{
if|if
condition|(
name|s
operator|.
name|equalsIgnoreCase
argument_list|(
name|USER_STRING
argument_list|)
condition|)
block|{
name|shift
operator|=
literal|6
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|equalsIgnoreCase
argument_list|(
name|GROUP_STRING
argument_list|)
condition|)
block|{
name|shift
operator|=
literal|3
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|equalsIgnoreCase
argument_list|(
name|OTHER_STRING
argument_list|)
condition|)
block|{
name|shift
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|int
name|perm
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
name|READ_STRING
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|perm
operator|=
name|READ
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
name|WRITE_STRING
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|perm
operator|=
name|WRITE
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
name|EXECUTE_STRING
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
name|perm
operator|=
name|EXECUTE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Unrecognised mode char '"
operator|+
name|s
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
name|mode
operator||=
operator|(
name|perm
operator|<<
name|shift
operator|)
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|mode
operator|&=
operator|(
operator|~
operator|(
name|perm
operator|<<
name|shift
operator|)
operator|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Unrecognised mode char '"
operator|+
name|s
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
name|setMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple symbolic mode is [rwxs-]{3}[rwxs-]{3}[rwxt-]{3}      */
specifier|private
name|void
name|setSimpleSymbolicMode
parameter_list|(
specifier|final
name|String
name|simpleSymbolicMode
parameter_list|)
throws|throws
name|SyntaxException
throws|,
name|PermissionDeniedException
block|{
name|setMode
argument_list|(
name|simpleSymbolicModeToInt
argument_list|(
name|simpleSymbolicMode
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|Pattern
name|unixSymbolicModePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"((?:[augo]*(?:[+\\-=](?:["
operator|+
name|READ_CHAR
operator|+
name|SETUID_CHAR
operator|+
name|STICKY_CHAR
operator|+
name|WRITE_CHAR
operator|+
name|EXECUTE_CHAR
operator|+
literal|"])+)+),?)+"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Matcher
name|unixSymbolicModeMatcher
init|=
name|unixSymbolicModePattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Pattern
name|existSymbolicModePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?:(?:"
operator|+
name|USER_STRING
operator|+
literal|"|"
operator|+
name|GROUP_STRING
operator|+
literal|"|"
operator|+
name|OTHER_STRING
operator|+
literal|")=(?:[+-](?:"
operator|+
name|READ_STRING
operator|+
literal|"|"
operator|+
name|WRITE_STRING
operator|+
literal|"|"
operator|+
name|EXECUTE_STRING
operator|+
literal|"),?)+)+"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Matcher
name|existSymbolicModeMatcher
init|=
name|existSymbolicModePattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Pattern
name|simpleSymbolicModePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"["
operator|+
name|READ_CHAR
operator|+
name|WRITE_CHAR
operator|+
name|EXECUTE_CHAR
operator|+
name|SETUID_CHAR
operator|+
name|UNSET_CHAR
operator|+
literal|"]{3}["
operator|+
name|READ_CHAR
operator|+
name|WRITE_CHAR
operator|+
name|EXECUTE_CHAR
operator|+
name|SETGID_CHAR
operator|+
name|UNSET_CHAR
operator|+
literal|"]{3}["
operator|+
name|READ_CHAR
operator|+
name|WRITE_CHAR
operator|+
name|EXECUTE_CHAR
operator|+
name|STICKY_CHAR
operator|+
name|UNSET_CHAR
operator|+
literal|"]{3}"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Matcher
name|simpleSymbolicModeMatcher
init|=
name|simpleSymbolicModePattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|/**      * Note: we don't need @PermissionRequired(user = IS_DBA | IS_OWNER) here      * because all of these methods delegate to the subclass implementation.      *      * @param modeStr The String representing a mode to set      *      * @throws org.exist.util.SyntaxException If the string syntax for the mode      * is not recognised. The following syntaxes are supported. Simple symbolic,      * Unix symbolic, eXist symbolic.      *       * @throws org.exist.security.PermissionDeniedException If you do not have      * permission to set the mode      */
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|setMode
parameter_list|(
specifier|final
name|String
name|modeStr
parameter_list|)
throws|throws
name|SyntaxException
throws|,
name|PermissionDeniedException
block|{
name|simpleSymbolicModeMatcher
operator|.
name|reset
argument_list|(
name|modeStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|simpleSymbolicModeMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setSimpleSymbolicMode
argument_list|(
name|modeStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unixSymbolicModeMatcher
operator|.
name|reset
argument_list|(
name|modeStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|unixSymbolicModeMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setUnixSymbolicMode
argument_list|(
name|modeStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|existSymbolicModeMatcher
operator|.
name|reset
argument_list|(
name|modeStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|existSymbolicModeMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|setExistSymbolicMode
argument_list|(
name|modeStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SyntaxException
argument_list|(
literal|"Unknown mode String: "
operator|+
name|modeStr
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|int
name|simpleSymbolicModeToInt
parameter_list|(
specifier|final
name|String
name|simpleModeStr
parameter_list|)
throws|throws
name|SyntaxException
block|{
name|int
name|mode
init|=
literal|0
decl_stmt|;
specifier|final
name|char
name|modeArray
index|[]
init|=
name|simpleModeStr
operator|.
name|toCharArray
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
name|modeArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|modeArray
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|shift
init|=
operator|(
name|i
operator|<
literal|3
condition|?
literal|6
else|:
operator|(
name|i
operator|<
literal|6
condition|?
literal|3
else|:
literal|0
operator|)
operator|)
decl_stmt|;
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
name|shift
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
name|shift
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
name|shift
operator|)
expr_stmt|;
break|break;
case|case
name|SETUID_CHAR
case|:
if|if
condition|(
name|i
operator|<
literal|3
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
else|else
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
name|STICKY
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
literal|"Unrecognised mode char '"
operator|+
name|c
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
return|return
name|mode
return|;
block|}
specifier|public
specifier|static
name|String
name|modeToSimpleSymbolicMode
parameter_list|(
specifier|final
name|int
name|mode
parameter_list|)
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
name|EXECUTE
operator|)
operator|==
literal|0
condition|?
name|UNSET_CHAR
else|:
name|EXECUTE_CHAR
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
comment|/**      * Utility function for external use      *      * @param types Types to convert to a string representation      * @return The string representation of the types      */
specifier|public
specifier|static
name|String
name|typesToString
parameter_list|(
specifier|final
name|int
name|types
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|types
operator|>>
literal|8
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|types
operator|>>
literal|8
operator|)
operator|&
name|READ
operator|)
operator|==
name|READ
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|READ_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|types
operator|>>
literal|8
operator|)
operator|&
name|WRITE
operator|)
operator|==
name|WRITE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|WRITE_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|types
operator|>>
literal|8
operator|)
operator|&
name|EXECUTE
operator|)
operator|==
name|EXECUTE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|EXECUTE_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|types
operator|>>
literal|4
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|types
operator|>>
literal|4
operator|)
operator|&
name|READ
operator|)
operator|==
name|READ
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|READ_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|types
operator|>>
literal|4
operator|)
operator|&
name|WRITE
operator|)
operator|==
name|WRITE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|WRITE_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|types
operator|>>
literal|4
operator|)
operator|&
name|EXECUTE
operator|)
operator|==
name|EXECUTE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|EXECUTE_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|types
operator|&
name|READ
operator|)
operator|==
name|READ
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|READ_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|types
operator|&
name|WRITE
operator|)
operator|==
name|WRITE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|WRITE_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|types
operator|&
name|EXECUTE
operator|)
operator|==
name|EXECUTE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|EXECUTE_CHAR
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|UNSET_CHAR
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

