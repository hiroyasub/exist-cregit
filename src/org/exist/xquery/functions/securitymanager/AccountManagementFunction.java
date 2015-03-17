begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
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
name|security
operator|.
name|internal
operator|.
name|aider
operator|.
name|GroupAider
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
name|aider
operator|.
name|UserAider
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
name|*
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
name|Type
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|AccountManagementFunction
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|qnCreateAccount
init|=
operator|new
name|QName
argument_list|(
literal|"create-account"
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
name|QName
name|qnRemoveAccount
init|=
operator|new
name|QName
argument_list|(
literal|"remove-account"
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
name|QName
name|qnPasswd
init|=
operator|new
name|QName
argument_list|(
literal|"passwd"
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
name|FNS_CREATE_ACCOUNT
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateAccount
argument_list|,
literal|"Creates a User Account."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's username."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's password."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"primary-group"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The primary group of the user."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"groups"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Any supplementary groups of which the user should be a member."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_CREATE_ACCOUNT_WITH_METADATA
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateAccount
argument_list|,
literal|"Creates a User Account."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's username."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's password."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"primary-group"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The primary group of the user."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"groups"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Any supplementary groups of which the user should be a member."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"full-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full name of the user."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"description"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A description of the user."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_CREATE_ACCOUNT_WITH_PERSONAL_GROUP
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateAccount
argument_list|,
literal|"Creates a User Account and a personal group for that user. The personal group takes the same name as the user, and is set as the user's primary group."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's username."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's password."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"groups"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Any supplementary groups of which the user should be a member."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_CREATE_ACCOUNT_WITH_PERSONAL_GROUP_WITH_METADATA
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateAccount
argument_list|,
literal|"Creates a User Account and a personal group for that user. The personal group takes the same name as the user, and is set as the user's primary group."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's username."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's password."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"groups"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Any supplementary groups of which the user should be a member."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"full-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full name of the user."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"description"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A description of the user."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REMOVE_ACCOUNT
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRemoveAccount
argument_list|,
literal|"Removes a User Account. If the user has a personal group you are responsible for removing that separately through sm:remove-group. "
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's username."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_PASSWD
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnPasswd
argument_list|,
literal|"Changes the password of a User Account."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The User's username."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The new User's password."
argument_list|)
block|,             }
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|AccountManagementFunction
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
try|try
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnRemoveAccount
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
comment|/* remove account */
if|if
condition|(
operator|!
name|currentUser
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Only a DBA user may remove accounts."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|securityManager
operator|.
name|hasAccount
argument_list|(
name|username
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The user account with username "
operator|+
name|username
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
if|if
condition|(
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
literal|"You cannot remove yourself i.e. the currently logged in user."
argument_list|)
throw|;
block|}
name|securityManager
operator|.
name|deleteAccount
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|password
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnPasswd
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
comment|/* change password */
if|if
condition|(
operator|!
operator|(
name|currentUser
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|username
argument_list|)
operator|||
name|currentUser
operator|.
name|hasDbaRole
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"You may only change your own password, unless you are a DBA."
argument_list|)
throw|;
block|}
specifier|final
name|Account
name|account
init|=
name|securityManager
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|account
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|securityManager
operator|.
name|updateAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnCreateAccount
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
comment|/* create account */
if|if
condition|(
operator|!
name|currentUser
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"You must be a DBA to create a User Account."
argument_list|)
throw|;
block|}
if|if
condition|(
name|securityManager
operator|.
name|hasAccount
argument_list|(
name|username
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The user account with username "
operator|+
name|username
operator|+
literal|" already exists."
argument_list|)
throw|;
block|}
specifier|final
name|Account
name|user
init|=
operator|new
name|UserAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|>=
literal|5
condition|)
block|{
comment|//set metadata values if present
name|user
operator|.
name|setMetadataValue
argument_list|(
name|AXSchemaType
operator|.
name|FULLNAME
argument_list|,
name|args
index|[
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|-
literal|2
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|user
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
name|args
index|[
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|-
literal|1
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
index|[]
name|subGroups
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
operator|||
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|5
condition|)
block|{
comment|//create the personal group
specifier|final
name|Group
name|group
init|=
operator|new
name|GroupAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|group
operator|.
name|setMetadataValue
argument_list|(
name|EXistSchemaType
operator|.
name|DESCRIPTION
argument_list|,
literal|"Personal group for "
operator|+
name|username
argument_list|)
expr_stmt|;
name|group
operator|.
name|addManager
argument_list|(
name|currentUser
argument_list|)
expr_stmt|;
name|securityManager
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
comment|//add the personal group as the primary group
name|user
operator|.
name|addGroup
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|subGroups
operator|=
name|getGroups
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//add the primary group as the primary group
name|user
operator|.
name|addGroup
argument_list|(
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|subGroups
operator|=
name|getGroups
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subGroups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|user
operator|.
name|addGroup
argument_list|(
name|subGroups
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|//create the account
name|securityManager
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|//if we created a personal group, then add the new account as a manager of their personal group
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
operator|||
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|5
condition|)
block|{
specifier|final
name|Group
name|group
init|=
name|securityManager
operator|.
name|getGroup
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|group
operator|.
name|addManager
argument_list|(
name|securityManager
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
argument_list|)
expr_stmt|;
name|securityManager
operator|.
name|updateGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown function call: "
operator|+
name|getSignature
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ConfigurationException
name|ce
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ce
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|ee
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ee
argument_list|)
throw|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|String
index|[]
name|getGroups
parameter_list|(
specifier|final
name|Sequence
name|seq
parameter_list|)
block|{
specifier|final
name|String
name|groups
index|[]
init|=
operator|new
name|String
index|[
name|seq
operator|.
name|getItemCount
argument_list|()
index|]
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
name|seq
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groups
index|[
name|i
index|]
operator|=
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|groups
return|;
block|}
block|}
end_class

end_unit

