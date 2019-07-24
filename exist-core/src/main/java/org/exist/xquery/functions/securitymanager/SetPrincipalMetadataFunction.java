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
comment|/**  *  * @author<a href="mailto:adam@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|SetPrincipalMetadataFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnSetAccountMetadata
init|=
operator|new
name|QName
argument_list|(
literal|"set-account-metadata"
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
name|qnSetGroupMetadata
init|=
operator|new
name|QName
argument_list|(
literal|"set-group-metadata"
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
name|FNS_SET_ACCOUNT_METADATA
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnSetAccountMetadata
argument_list|,
literal|"Sets a metadata attribute value for an account"
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
literal|"The username of the account to set metadata for."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"attribute"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The metadata attribute key."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The metadata value,"
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
name|FNS_SET_GROUP_METADATA
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnSetGroupMetadata
argument_list|,
literal|"Sets a metadata attribute value for a group"
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
literal|"The name of the group to set metadata for."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"attribute"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The metadata attribute key."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The metadata value,"
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
name|SetPrincipalMetadataFunction
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
name|strPrincipal
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
name|String
name|metadataAttributeNamespace
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Principal
name|principal
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnSetAccountMetadata
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
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
name|getUsername
argument_list|()
operator|.
name|equals
argument_list|(
name|strPrincipal
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
operator|new
name|PermissionDeniedException
argument_list|(
literal|"You must have suitable access rights to modify the users metadata."
argument_list|)
argument_list|)
throw|;
block|}
name|principal
operator|=
name|securityManager
operator|.
name|getAccount
argument_list|(
name|strPrincipal
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnSetGroupMetadata
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
comment|//check for a valid group metadata key
name|boolean
name|valid
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|SchemaType
name|groupMetadataKey
range|:
name|GetPrincipalMetadataFunction
operator|.
name|GROUP_METADATA_KEYS
control|)
block|{
if|if
condition|(
name|groupMetadataKey
operator|.
name|getNamespace
argument_list|()
operator|.
name|equals
argument_list|(
name|metadataAttributeNamespace
argument_list|)
condition|)
block|{
name|valid
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|valid
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The metadata attribute key '"
operator|+
name|metadataAttributeNamespace
operator|+
literal|"' is not valid on a group."
argument_list|)
throw|;
block|}
specifier|final
name|Group
name|group
init|=
name|securityManager
operator|.
name|getGroup
argument_list|(
name|strPrincipal
argument_list|)
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
name|group
operator|.
name|isManager
argument_list|(
name|currentUser
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
operator|new
name|PermissionDeniedException
argument_list|(
literal|"You must have suitable access rights to modify the groups metadata."
argument_list|)
argument_list|)
throw|;
block|}
name|principal
operator|=
name|group
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
name|setAccountMetadata
argument_list|(
name|securityManager
argument_list|,
name|principal
argument_list|,
name|metadataAttributeNamespace
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|void
name|setAccountMetadata
parameter_list|(
specifier|final
name|SecurityManager
name|securityManager
parameter_list|,
specifier|final
name|Principal
name|principal
parameter_list|,
specifier|final
name|String
name|metadataAttributeNamespace
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
name|SchemaType
name|schemaType
init|=
name|AXSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|metadataAttributeNamespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaType
operator|==
literal|null
condition|)
block|{
name|schemaType
operator|=
name|EXistSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|metadataAttributeNamespace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|schemaType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown metadata attribute key: "
operator|+
name|metadataAttributeNamespace
argument_list|)
throw|;
block|}
name|principal
operator|.
name|setMetadataValue
argument_list|(
name|schemaType
argument_list|,
name|value
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|principal
operator|instanceof
name|Account
condition|)
block|{
name|securityManager
operator|.
name|updateAccount
argument_list|(
operator|(
name|Account
operator|)
name|principal
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|principal
operator|instanceof
name|Group
condition|)
block|{
name|securityManager
operator|.
name|updateGroup
argument_list|(
operator|(
name|Group
operator|)
name|principal
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

