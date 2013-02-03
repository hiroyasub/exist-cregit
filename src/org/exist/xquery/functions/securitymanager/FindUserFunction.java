begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SecurityManager Extension  *  Copyright (C) 2010 Adam Retter<adam@existsolutions.com>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|BooleanValue
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|Sequence
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
name|SequenceType
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
name|StringValue
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
name|Type
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|FindUserFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnFindUsersByUsername
init|=
operator|new
name|QName
argument_list|(
literal|"find-users-by-username"
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
name|qnFindUsersByName
init|=
operator|new
name|QName
argument_list|(
literal|"find-users-by-name"
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
name|qnFindUsersByNamePart
init|=
operator|new
name|QName
argument_list|(
literal|"find-users-by-name-part"
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
name|qnListUsers
init|=
operator|new
name|QName
argument_list|(
literal|"list-users"
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
name|qnUserExists
init|=
operator|new
name|QName
argument_list|(
literal|"user-exists"
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
name|FNS_FIND_USERS_BY_USERNAME
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnFindUsersByUsername
argument_list|,
literal|"Finds users whoose username starts with a matching string"
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
literal|"The starting string against which to match usernames"
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
literal|"The list of matching usernames"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_FIND_USERS_BY_NAME
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnFindUsersByName
argument_list|,
literal|"Finds users whoose personal name starts with a matching string"
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
literal|"The starting string against which to match a personal name"
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
literal|"The list of matching usernames"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_FIND_USERS_BY_NAME_PART
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnFindUsersByNamePart
argument_list|,
literal|"Finds users whoose first name or last name starts with a matching string"
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
literal|"The starting string against which to match a first or last name"
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
literal|"The list of matching usernames"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_LIST_USERS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnListUsers
argument_list|,
literal|"List all users. You must be a DBA to enumerate all users, if you are not a DBA you will just get the username of the currently logged in user."
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
name|ONE_OR_MORE
argument_list|,
literal|"The list of users."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_USER_EXISTS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnUserExists
argument_list|,
literal|"Determines whether a user exists."
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
literal|"The username to check for existence."
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
literal|"true if the user account exists, false otherwise."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FindUserFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
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
name|Sequence
index|[]
name|args
parameter_list|,
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
name|getSubject
argument_list|()
decl_stmt|;
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
name|qnListUsers
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
if|if
condition|(
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
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addUserNamesToSequence
argument_list|(
name|securityManager
operator|.
name|findAllUserNames
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
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
literal|"You must be an authenticated user"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnUserExists
operator|.
name|getLocalName
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
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|securityManager
operator|.
name|hasAccount
argument_list|(
name|username
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
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
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|usernames
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnFindUsersByUsername
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|usernames
operator|=
name|securityManager
operator|.
name|findUsernamesWhereUsernameStarts
argument_list|(
name|startsWith
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnFindUsersByName
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|usernames
operator|=
name|securityManager
operator|.
name|findUsernamesWhereNameStarts
argument_list|(
name|startsWith
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnFindUsersByNamePart
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|usernames
operator|=
name|securityManager
operator|.
name|findUsernamesWhereNamePartStarts
argument_list|(
name|startsWith
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown function"
argument_list|)
throw|;
block|}
name|addUserNamesToSequence
argument_list|(
name|usernames
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|addUserNamesToSequence
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|userNames
parameter_list|,
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//order a-z
name|Collections
operator|.
name|sort
argument_list|(
name|userNames
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|userName
range|:
name|userNames
control|)
block|{
name|sequence
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

