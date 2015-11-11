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
name|PermissionDeniedException
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
name|Type
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|AccountStatusFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnIsAccountEnabled
init|=
operator|new
name|QName
argument_list|(
literal|"is-account-enabled"
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
name|qnSetAccountEnabled
init|=
operator|new
name|QName
argument_list|(
literal|"set-account-enabled"
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
name|FNS_IS_ACCOUNT_ENABLED
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnIsAccountEnabled
argument_list|,
literal|"Determines whether a user account is enabled. You must be a DBA, or you must be enquiring about your own user account."
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
literal|"The username of the account to check the status for."
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
literal|"true if the account is enabled, false otherwise."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_SET_ACCOUNT_ENABLED
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnSetAccountEnabled
argument_list|,
literal|"Enabled or disables a users account. You must be a DBA to enable or disable an account."
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
literal|"The username of the account to enable or disable."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"enabled"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true to enable the account, false to disable the account."
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
name|AccountStatusFunction
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
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnIsAccountEnabled
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
literal|"You must be a DBA or be enquiring about your own account!"
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
return|return
operator|new
name|BooleanValue
argument_list|(
name|account
operator|.
name|isEnabled
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnSetAccountEnabled
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
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"You must be a DBA to change the status of an account!"
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|enable
init|=
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
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
name|setEnabled
argument_list|(
name|enable
argument_list|)
expr_stmt|;
try|try
block|{
name|account
operator|.
name|save
argument_list|(
name|broker
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
name|ce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ce
argument_list|)
throw|;
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
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
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
literal|"Unknown function"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

