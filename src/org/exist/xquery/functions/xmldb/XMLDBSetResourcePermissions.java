begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  Modifications Copyright (C) 2004 Luigi P. Bai  *  finder@users.sf.net  *  Licensed as below under the LGPL.  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    */
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
name|security
operator|.
name|PermissionFactory
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
name|User
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

begin_class
specifier|public
class|class
name|XMLDBSetResourcePermissions
extends|extends
name|XMLDBAbstractCollectionManipulator
block|{
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
literal|"set-resource-permissions"
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
literal|"Sets the permissions of the specified resource. $a is the collection, which can be specified "
operator|+
literal|"as a simple collection path or an XMLDB URI. $b denotes the resource to"
operator|+
literal|"change. $c specifies the user which will become the owner of the resource, $d the group. "
operator|+
literal|"The final argument contains the permissions, specified as an xs:integer value. "
operator|+
literal|"PLEASE REMEMBER that 0755 is 7*64+5*8+5, NOT decimal 755."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|, 			}
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
name|XMLDBSetResourcePermissions
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
comment|/* (non-Javadoc)  * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)  *  */
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
name|Resource
name|res
init|=
name|collection
operator|.
name|getResource
argument_list|(
operator|new
name|AnyURIValue
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|toString
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
name|String
name|user
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|group
init|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|mode
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|4
index|]
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|user
operator|||
literal|0
operator|==
name|user
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Needs a valid user name, not: "
operator|+
name|user
argument_list|)
throw|;
if|if
condition|(
literal|null
operator|==
name|group
operator|||
literal|0
operator|==
name|group
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Needs a valid group name, not: "
operator|+
name|group
argument_list|)
throw|;
comment|// Must actually get a User object for the Permission...
name|Permission
name|p
init|=
name|PermissionFactory
operator|.
name|getPermission
argument_list|(
name|user
argument_list|,
name|group
argument_list|,
name|mode
argument_list|)
decl_stmt|;
name|User
name|u
init|=
name|ums
operator|.
name|getUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|u
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Needs a valid user name, not: "
operator|+
name|user
argument_list|)
throw|;
name|p
operator|.
name|setOwner
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|ums
operator|.
name|setPermissions
argument_list|(
name|res
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
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
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Unable to change resource permissions"
argument_list|,
name|xe
argument_list|)
throw|;
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

