begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|Permission
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
name|PermissionsFunctions
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
name|IntegerValue
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
name|Resource
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author gvalentino  *  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBPermissions
extends|extends
name|XMLDBAbstractCollectionManipulator
block|{
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|ARG_COLLECTION
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The collection-uri"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|ARG_RESOURCE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The resource"
argument_list|)
decl_stmt|;
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
name|XMLDBPermissions
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
literal|"get-permissions"
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
literal|"Returns the permissions assigned to the collection $collection-uri. "
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|ARG_COLLECTION
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the collection permissions"
argument_list|)
argument_list|,
name|PermissionsFunctions
operator|.
name|FNS_GET_PERMISSIONS
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-permissions"
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
literal|"Returns the permissions assigned to the resource $resource "
operator|+
literal|"in collection $collection-uri. "
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|ARG_COLLECTION
block|,
name|ARG_RESOURCE
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the resource permissions"
argument_list|)
argument_list|,
name|PermissionsFunctions
operator|.
name|FNS_GET_PERMISSIONS
argument_list|)
block|}
decl_stmt|;
specifier|public
name|XMLDBPermissions
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|evalWithCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
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
try|try
block|{
specifier|final
name|Permission
name|perm
init|=
name|getPermissions
argument_list|(
name|collection
argument_list|,
name|args
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntegerValue
argument_list|(
name|perm
operator|.
name|getMode
argument_list|()
argument_list|,
name|Type
operator|.
name|INT
argument_list|)
return|;
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
literal|"Unable to retrieve resource permissions"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unable to retrieve resource permissions"
argument_list|,
name|xe
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * @param collection 	 * @param args 	 * @return permission 	 * @throws XMLDBException 	 * @throws XPathException 	 */
specifier|protected
name|Permission
name|getPermissions
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|XPathException
block|{
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
name|Permission
name|perm
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
specifier|final
name|Resource
name|res
init|=
name|collection
operator|.
name|getResource
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|perm
operator|=
name|ums
operator|.
name|getPermissions
argument_list|(
name|res
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
literal|"Unable to locate resource "
operator|+
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|perm
operator|=
name|ums
operator|.
name|getPermissions
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|perm
return|;
block|}
block|}
end_class

end_unit

