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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|AbstractInternalModule
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
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * Module function definitions for xmldb module.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author Luigi P. Bai, finder@users.sf.net, 2004  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/xmldb"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"xmldb"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2004-09-12"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"pre eXist-1.0"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NEED_PRIV_USER
init|=
literal|"The XQuery owner must have appropriate privileges to do this, e.g. having DBA role."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NEED_PRIV_USER_NOT_CURRENT
init|=
literal|"The XQuery owner must have appropriate privileges to do this, e.g. having DBA role, and not being the owner of the currently running XQuery."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|REMEMBER_OCTAL_CALC
init|=
literal|"PLEASE REMEMBER that octal number 0755 is 7*64+5*8+5 i.e. 493 in decimal NOT 755. You can use util:base-to-integer(0755, 8) as argument for convenience."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|COLLECTION_URI
init|=
literal|"Collection URIs can be specified either as a simple collection path or an XMLDB URI."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ANY_URI
init|=
literal|"Resource URIs can be specified either as a simple collection path, an XMLDB URI or any URI."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCreateCollection
operator|.
name|signature
argument_list|,
name|XMLDBCreateCollection
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBRegisterDatabase
operator|.
name|signature
argument_list|,
name|XMLDBRegisterDatabase
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBStore
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBStore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBStore
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBStore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBLoadFromPattern
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBLoadFromPattern
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBLoadFromPattern
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBLoadFromPattern
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBLoadFromPattern
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|XMLDBLoadFromPattern
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBAuthenticate
operator|.
name|authenticateSignature
argument_list|,
name|XMLDBAuthenticate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBAuthenticate
operator|.
name|loginSignatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBAuthenticate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBAuthenticate
operator|.
name|loginSignatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBAuthenticate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetCurrentUser
operator|.
name|signature
argument_list|,
name|XMLDBGetCurrentUser
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetCurrentUserAttribute
operator|.
name|signature
argument_list|,
name|XMLDBGetCurrentUserAttribute
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetCurrentUserAttributeNames
operator|.
name|signature
argument_list|,
name|XMLDBGetCurrentUserAttributeNames
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBXUpdate
operator|.
name|signature
argument_list|,
name|XMLDBXUpdate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCopy
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBCopy
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCopy
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBCopy
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBMove
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBMove
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBMove
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBMove
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBRename
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBRename
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBRename
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBRename
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBRemove
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBRemove
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBRemove
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBRemove
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBHasLock
operator|.
name|signature
argument_list|,
name|XMLDBHasLock
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCreated
operator|.
name|lastModifiedSignature
argument_list|,
name|XMLDBCreated
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCreated
operator|.
name|createdSignatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBCreated
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCreated
operator|.
name|createdSignatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBCreated
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBPermissions
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBPermissions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBPermissions
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBPermissions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBSize
operator|.
name|signature
argument_list|,
name|XMLDBSize
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetUserOrGroup
operator|.
name|getGroupSignatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBGetUserOrGroup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetUserOrGroup
operator|.
name|getGroupSignatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBGetUserOrGroup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetUserOrGroup
operator|.
name|getOwnerSignatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBGetUserOrGroup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetUserOrGroup
operator|.
name|getOwnerSignatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBGetUserOrGroup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetChildCollections
operator|.
name|signature
argument_list|,
name|XMLDBGetChildCollections
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetChildResources
operator|.
name|signature
argument_list|,
name|XMLDBGetChildResources
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBSetCollectionPermissions
operator|.
name|signature
argument_list|,
name|XMLDBSetCollectionPermissions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBSetResourcePermissions
operator|.
name|signature
argument_list|,
name|XMLDBSetResourcePermissions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBUserAccess
operator|.
name|fnExistsUser
argument_list|,
name|XMLDBUserAccess
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBUserAccess
operator|.
name|fnUserGroups
argument_list|,
name|XMLDBUserAccess
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBUserAccess
operator|.
name|fnUserPrimaryGroup
argument_list|,
name|XMLDBUserAccess
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBUserAccess
operator|.
name|fnUserHome
argument_list|,
name|XMLDBUserAccess
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCreateUser
operator|.
name|signature
argument_list|,
name|XMLDBCreateUser
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBChangeUser
operator|.
name|signature
argument_list|,
name|XMLDBChangeUser
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBDeleteUser
operator|.
name|signature
argument_list|,
name|XMLDBDeleteUser
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCreateGroup
operator|.
name|signature
argument_list|,
name|XMLDBCreateGroup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBChmodCollection
operator|.
name|signature
argument_list|,
name|XMLDBChmodCollection
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBChmodResource
operator|.
name|signature
argument_list|,
name|XMLDBChmodResource
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCollectionAvailable
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBCollectionAvailable
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBPermissionsToString
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBPermissionsToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBPermissionsToString
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBPermissionsToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBIsAdmin
operator|.
name|signature
argument_list|,
name|XMLDBIsAdmin
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBIsAuthenticated
operator|.
name|signature
argument_list|,
name|XMLDBIsAuthenticated
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBURIFunctions
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBURIFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBURIFunctions
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBURIFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBURIFunctions
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|XMLDBURIFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBURIFunctions
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|XMLDBURIFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetMimeType
operator|.
name|signature
argument_list|,
name|XMLDBGetMimeType
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBDocument
operator|.
name|signature
argument_list|,
name|XMLDBDocument
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunXCollection
operator|.
name|signature
argument_list|,
name|FunXCollection
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBReindex
operator|.
name|signature
argument_list|,
name|XMLDBReindex
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBDefragment
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBDefragment
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBDefragment
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBDefragment
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBGetUsers
operator|.
name|signature
argument_list|,
name|XMLDBGetUsers
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
static|static
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|functions
argument_list|,
operator|new
name|FunctionComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XMLDBModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.Module#getDescription()          */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for database manipulation functions."
return|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.Module#getNamespaceURI()          */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.Module#getDefaultPrefix()          */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
block|}
end_class

end_unit

