begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2010 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|xmldb
package|;
end_package

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
name|Group
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

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com>  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBAddUserToGroup
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XMLDBAddUserToGroup
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"add-user-to-group"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Add a user to a group. $user is the username. $group is the group name"
operator|+
name|XMLDBModule
operator|.
name|NEED_PRIV_USER
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
literal|"The user name"
argument_list|)
block|,
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
literal|"The group name"
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
literal|"true() or false() indicating the outcome of the operation"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * @param context      */
specifier|public
name|XMLDBAddUserToGroup
parameter_list|(
name|XQueryContext
name|context
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
comment|/*      * (non-Javadoc)      *      * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet,      *         org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|args
index|[]
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"guest"
argument_list|)
condition|)
block|{
name|XPathException
name|xPathException
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling account '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be an authenticated account to call this function."
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
name|String
name|userName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|groupName
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Attempting to add user '"
operator|+
name|userName
operator|+
literal|"' to group '"
operator|+
name|groupName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
try|try
block|{
name|SecurityManager
name|sm
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
name|Group
name|group
init|=
name|sm
operator|.
name|getGroup
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSubject
argument_list|()
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
name|Account
name|account
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSubject
argument_list|()
argument_list|,
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
name|account
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|sm
operator|.
name|updateAccount
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSubject
argument_list|()
argument_list|,
name|account
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not find account for username '"
operator|+
name|userName
operator|+
literal|"' in call to xmldb:add-user-to-group"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
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
literal|"Permission denied, calling account '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' don not authorize to call this function."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|exe
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to add user '"
operator|+
name|userName
operator|+
literal|"' to group '"
operator|+
name|groupName
operator|+
literal|"'"
argument_list|,
name|exe
argument_list|)
expr_stmt|;
block|}
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
block|}
end_class

end_unit

