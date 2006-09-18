begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  Modifications Copyright (C) 2004 Luigi P. Bai  *  finder@users.sf.net  *  Licensed as below under the LGPL.  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBCreateUser
extends|extends
name|BasicFunction
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
literal|"Create a new user in the database. Arguments are: username, password, group memberships,"
operator|+
literal|"home collection."
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
name|ONE_OR_MORE
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
name|ZERO_OR_ONE
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
comment|/** 	 * @param context 	 */
specifier|public
name|XMLDBCreateUser
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, 	 *         org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|User
name|userObj
init|=
operator|new
name|User
argument_list|(
name|user
argument_list|,
name|pass
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to create user "
operator|+
name|user
argument_list|)
expr_stmt|;
comment|// changed by wolf: the first group is always the primary group, so we don't need
comment|// an additional argument
name|Sequence
name|groups
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|int
name|len
init|=
name|groups
operator|.
name|getLength
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
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Empty user collection"
argument_list|)
throw|;
try|try
block|{
name|userObj
operator|.
name|setHome
argument_list|(
operator|new
name|AnyURIValue
argument_list|(
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|toXmldbURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Invalid home collection URI"
argument_list|,
name|e
argument_list|)
throw|;
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
name|getUser
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
name|addUser
argument_list|(
name|userObj
argument_list|)
expr_stmt|;
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
literal|"Failed to create new user "
operator|+
name|user
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

