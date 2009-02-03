begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2006 The eXist team  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc.,  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  *  *  Some modifications Copyright (C) 2004 Luigi P. Bai  *  finder@users.sf.net  */
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
name|XMLDBCollectionExists
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMLDBCollectionExists
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBCollectionExists
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMLDBCollectionExists
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMLDBPermissionsToString
operator|.
name|signature
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
name|FunDocument
operator|.
name|signature
argument_list|,
name|FunDocument
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
literal|"Database manipulation functions"
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
block|}
end_class

end_unit

