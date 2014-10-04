begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|xmldb
operator|.
name|LocalCollection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|UserManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|functions
operator|.
name|securitymanager
operator|.
name|AccountManagementFunction
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
name|AnyURIValue
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * @author wolf  * @author Luigi P. Bai, finder@users.sf.net, 2004  *   */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|XMLDBCreateUser
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
name|XMLDBCreateUser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"create-user"
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
literal|"Create a new user, $user-id, in the database. "
operator|+
name|XMLDBModule
operator|.
name|NEED_PRIV_USER
operator|+
literal|" $user-id is the username, $password is the password, "
operator|+
literal|"$groups is the sequence of group memberships. "
operator|+
literal|"The first group in the sequence is the primary group."
operator|+
literal|"$home-collection-uri is the home collection URI."
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"user-id"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The user-id"
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
literal|"The password"
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
name|ONE_OR_MORE
argument_list|,
literal|"The group memberships"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"home-collection-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The home collection URI"
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
literal|"$home-collection-uri has no effect since 2.0. You should use the sm:create-account function from the SecurityManager module instead."
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"create-user"
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
literal|"Create a new user, $user-id, in the database. "
operator|+
name|XMLDBModule
operator|.
name|NEED_PRIV_USER
operator|+
literal|" $user-id is the username, $password is the password, "
operator|+
literal|"$groups is the sequence of group memberships. "
operator|+
literal|"The first group in the sequence is the primary group."
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"user-id"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The user-id"
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
literal|"The password"
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
name|ONE_OR_MORE
argument_list|,
literal|"The group memberships"
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
name|AccountManagementFunction
operator|.
name|FNS_CREATE_ACCOUNT_WITH_PERSONAL_GROUP_WITH_METADATA
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|XMLDBCreateUser
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xquery.Expression#eval(org.exist.dom.persistent.DocumentSet, 	 * org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
specifier|final
name|XPathException
name|xPathException
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling user '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be a DBA to call this function."
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
specifier|final
name|String
name|user
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
name|pass
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
literal|"Attempting to create user "
operator|+
name|user
argument_list|)
expr_stmt|;
specifier|final
name|UserAider
name|userObj
init|=
operator|new
name|UserAider
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|userObj
operator|.
name|setPassword
argument_list|(
name|pass
argument_list|)
expr_stmt|;
comment|// changed by wolf: the first group is always the primary group, so we
comment|// don't need
comment|// an additional argument
specifier|final
name|Sequence
name|groups
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|groups
operator|.
name|getItemCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|len
condition|;
name|x
operator|++
control|)
block|{
name|userObj
operator|.
name|addGroup
argument_list|(
name|groups
operator|.
name|itemAt
argument_list|(
name|x
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
operator|new
name|LocalCollection
argument_list|(
name|context
operator|.
name|getSubject
argument_list|()
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|,
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|addAccount
argument_list|(
name|userObj
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to create user: "
operator|+
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Failed to create user: "
operator|+
name|user
argument_list|,
name|xe
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to create new user '"
operator|+
name|user
operator|+
literal|"' by "
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|xe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|collection
condition|)
try|try
block|{
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

