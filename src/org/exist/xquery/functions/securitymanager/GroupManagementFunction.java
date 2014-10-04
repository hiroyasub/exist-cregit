begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist-db SecurityManager Module Extension  *  Copyright (C) 2013 Adam Retter<adam@existsolutions.com>  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|persistent
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|GroupManagementFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnCreateGroup
init|=
operator|new
name|QName
argument_list|(
literal|"create-group"
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
name|qnRemoveGroup
init|=
operator|new
name|QName
argument_list|(
literal|"remove-group"
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
name|qnDeleteGroup
init|=
operator|new
name|QName
argument_list|(
literal|"delete-group"
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
name|FNS_CREATE_GROUP
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateGroup
argument_list|,
literal|"Creates a User Group. The current user will be set as the group's manager."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the group to create."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_CREATE_GROUP_WITH_METADATA
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateGroup
argument_list|,
literal|"Creates a User Group. The current user will be set as the group's manager."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the group to create."
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
literal|"A description of the group."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_CREATE_GROUP_WITH_MANAGERS_WITH_METADATA
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnCreateGroup
argument_list|,
literal|"Creates a User Group. The current user will be set as a manager of the group in addition to the specified managers."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the group to create."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"managers"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The usernames of users that will be a manager of this group."
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
literal|"A description of the group."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REMOVE_GROUP
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRemoveGroup
argument_list|,
literal|"Remove a User Group. Any resources owned by the group will be moved to the 'guest' group."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The group-id to delete"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_DELETE_GROUP
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnDeleteGroup
argument_list|,
literal|"Removes a User Group. Any resources owned by the group will be moved to the 'guest' group."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"group-id"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The group-id to delete"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|FNS_REMOVE_GROUP
argument_list|)
decl_stmt|;
comment|//TODO implement later
comment|/* public final static FunctionSignature FNS_DELETE_GROUP_WITH_SUCCESSOR = new FunctionSignature(         qnRemoveGroup         "Deletes an existing group identified by $group-id, any resources owned by the group will be moved to the group indicated by $successor-group-id.",         new SequenceType[]{             new FunctionParameterSequenceType("group-id", Type.STRING, Cardinality.EXACTLY_ONE, "The group-id to delete"),             new FunctionParameterSequenceType("successor-group-id", Type.STRING, Cardinality.EXACTLY_ONE, "The group-id that should take over ownership of any resources")         },         new SequenceType(Type.ITEM, Cardinality.EMPTY)     ); */
specifier|public
name|GroupManagementFunction
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
name|SecurityManager
name|securityManager
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|currentSubject
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSubject
argument_list|()
decl_stmt|;
try|try
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
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnCreateGroup
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|securityManager
operator|.
name|hasGroup
argument_list|(
name|groupName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The group with name "
operator|+
name|groupName
operator|+
literal|" already exists."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|currentSubject
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Only DBA users may create a user group."
argument_list|)
throw|;
block|}
specifier|final
name|Group
name|group
init|=
operator|new
name|GroupAider
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
name|group
operator|.
name|addManager
argument_list|(
name|currentSubject
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
comment|//set group managers
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|groupManagers
init|=
name|getGroupManagers
argument_list|(
name|securityManager
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|group
operator|.
name|addManagers
argument_list|(
name|groupManagers
argument_list|)
expr_stmt|;
block|}
comment|//set metadata
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|group
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
name|securityManager
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnRemoveGroup
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|||
name|isCalledAs
argument_list|(
name|qnDeleteGroup
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|securityManager
operator|.
name|hasGroup
argument_list|(
name|groupName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The group with name "
operator|+
name|groupName
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
specifier|final
name|Group
name|successorGroup
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
specifier|final
name|String
name|successorGroupName
init|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|currentSubject
operator|.
name|hasGroup
argument_list|(
name|successorGroupName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"You must be a member of the group for which permissions should be inherited by"
argument_list|)
throw|;
block|}
name|successorGroup
operator|=
name|securityManager
operator|.
name|getGroup
argument_list|(
name|successorGroupName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|successorGroup
operator|=
name|securityManager
operator|.
name|getGroup
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|securityManager
operator|.
name|deleteGroup
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
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
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
block|}
specifier|private
name|List
argument_list|<
name|Account
argument_list|>
name|getGroupManagers
parameter_list|(
specifier|final
name|SecurityManager
name|securityManager
parameter_list|,
specifier|final
name|Sequence
name|seq
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|managers
init|=
operator|new
name|ArrayList
argument_list|<
name|Account
argument_list|>
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
name|seq
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Account
name|account
init|=
name|securityManager
operator|.
name|getAccount
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|managers
operator|.
name|add
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
return|return
name|managers
return|;
block|}
block|}
end_class

end_unit

