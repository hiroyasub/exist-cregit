begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database * Copyright (C) 2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|securitymanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|dom
operator|.
name|QName
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
name|Subject
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|BasicFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Cardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|FindGroupFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnFindGroupsByGroupname
init|=
operator|new
name|QName
argument_list|(
literal|"find-groups-by-groupname"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnListGroups
init|=
operator|new
name|QName
argument_list|(
literal|"list-groups"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnFindGroupsWhereGroupnameContains
init|=
operator|new
name|QName
argument_list|(
literal|"find-groups-where-groupname-contains"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnGetUserGroups
init|=
operator|new
name|QName
argument_list|(
literal|"get-user-groups"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnGetUserPrimaryGroup
init|=
operator|new
name|QName
argument_list|(
literal|"get-user-primary-group"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnGroupExists
init|=
operator|new
name|QName
argument_list|(
literal|"group-exists"
argument_list|,
name|SecurityManagerModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SecurityManagerModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_LIST_GROUPS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnListGroups
argument_list|,
literal|"List all groups"
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The list of groups"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_FIND_GROUPS_BY_GROUPNAME
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnFindGroupsByGroupname
argument_list|,
literal|"Finds groups whoose group name starts with a matching string"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"starts-with"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The starting string against which to match group names"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The list of matching group names"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_FIND_GROUPS_WHERE_GROUPNAME_CONTANINS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnFindGroupsWhereGroupnameContains
argument_list|,
literal|"Finds groups whoose group name contains the string fragment"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"fragment"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The fragment against which to match group names"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The list of matching group names"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_GET_USER_GROUPS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnGetUserGroups
argument_list|,
literal|"Returns the sequence of groups that the user $user is a member of. You must be a DBA or logged in as the user for which you are trying to retrieve group details for."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"user"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The username to retrieve the group membership list for."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The users group memberships"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_GET_USER_PRIMARY_GROUP
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnGetUserPrimaryGroup
argument_list|,
literal|"Returns the primary group of the user $user. You must be a DBA or logged in as the user for which you are trying to retrieve group details for."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"user"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The username to retrieve the primary group of."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The users primary group"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_GROUP_EXISTS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnGroupExists
argument_list|,
literal|"Determines whether a user group exists."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the user group to check for existence."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true if the user group exists, false otherwise."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FindGroupFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|DBBroker
name|broker
init|=
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|currentUser
init|=
name|broker
operator|.
name|getCurrentSubject
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isCalledAs
argument_list|(
name|qnGetUserGroups
operator|.
name|getLocalPart
argument_list|()
argument_list|)
operator|&&
name|currentUser
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"You must be an authenticated user"
argument_list|)
throw|;
block|}
specifier|final
name|SecurityManager
name|securityManager
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnGetUserPrimaryGroup
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|username
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|securityManager
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnGroupExists
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|groupName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|result
operator|=
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|securityManager
operator|.
name|hasGroup
argument_list|(
name|groupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groupNames
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnListGroups
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|groupNames
operator|=
name|securityManager
operator|.
name|findAllGroupNames
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnFindGroupsByGroupname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|startsWith
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|groupNames
operator|=
name|securityManager
operator|.
name|findGroupnamesWhereGroupnameStarts
argument_list|(
name|startsWith
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnFindGroupsWhereGroupnameContains
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|fragment
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|groupNames
operator|=
name|securityManager
operator|.
name|findGroupnamesWhereGroupnameContains
argument_list|(
name|fragment
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnGetUserGroups
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|username
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentUser
operator|.
name|hasDbaRole
argument_list|()
operator|&&
operator|!
name|currentUser
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|username
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"You must be a DBA or enquiring about your own user account!"
argument_list|)
throw|;
block|}
specifier|final
name|Account
name|user
init|=
name|securityManager
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|groupNames
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|user
operator|.
name|getGroups
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unknown function"
argument_list|)
throw|;
block|}
comment|//order a-z
name|Collections
operator|.
name|sort
argument_list|(
name|groupNames
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|groupName
range|:
name|groupNames
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|groupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

