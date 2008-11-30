begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2006 The eXist team  * http://exist-db.org  *    * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
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
name|xmlrpc
operator|.
name|XmlRpcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|ErrorCodes
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Date
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

begin_class
specifier|public
class|class
name|RemoteCollectionManagementService
implements|implements
name|CollectionManagementServiceImpl
block|{
specifier|protected
name|XmlRpcClient
name|client
decl_stmt|;
specifier|protected
name|RemoteCollection
name|parent
init|=
literal|null
decl_stmt|;
specifier|public
name|RemoteCollectionManagementService
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|XmlRpcClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|createCollection
argument_list|(
name|collName
argument_list|,
operator|(
name|Date
operator|)
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|XmldbURI
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|createCollection
argument_list|(
name|collName
argument_list|,
operator|(
name|Date
operator|)
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|collName
parameter_list|,
name|Date
name|created
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
return|return
name|createCollection
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|collName
argument_list|)
argument_list|,
name|created
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|XmldbURI
name|collName
parameter_list|,
name|Date
name|created
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|collName
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|collName
argument_list|)
expr_stmt|;
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|created
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
name|RemoteCollection
name|collection
init|=
operator|new
name|RemoteCollection
argument_list|(
name|client
argument_list|,
operator|(
name|RemoteCollection
operator|)
name|parent
argument_list|,
name|collName
argument_list|)
decl_stmt|;
name|parent
operator|.
name|addChildCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
comment|/**      *  Implements createCollection from interface CollectionManager. Gets      *  called by some applications based on Xindice.      *      *@param  path                Description of the Parameter      *@param  configuration       Description of the Parameter      *@return                     Description of the Return Value      *@exception  XMLDBException  Description of the Exception      */
specifier|public
name|Collection
name|createCollection
parameter_list|(
name|String
name|path
parameter_list|,
name|Document
name|configuration
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|createCollection
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"CollectionManagementService"
return|;
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|String
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|removeCollection
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|collName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|XmldbURI
name|collName
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|collName
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|collName
argument_list|)
expr_stmt|;
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"removeCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
name|parent
operator|.
name|removeChildCollection
argument_list|(
name|collName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|parent
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|parent
operator|=
operator|(
name|RemoteCollection
operator|)
name|parent
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
block|}
specifier|public
name|void
name|move
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|move
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|collectionPath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|destinationPath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|newName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xmldb.CollectionManagementServiceImpl#move(org.xmldb.api.base.Collection, org.xmldb.api.base.Collection, java.lang.String)      */
specifier|public
name|void
name|move
parameter_list|(
name|XmldbURI
name|collectionPath
parameter_list|,
name|XmldbURI
name|destinationPath
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collectionPath
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|collectionPath
argument_list|)
expr_stmt|;
name|destinationPath
operator|=
name|destinationPath
operator|==
literal|null
condition|?
name|collectionPath
operator|.
name|removeLastSegment
argument_list|()
else|:
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|newName
operator|=
name|collectionPath
operator|.
name|lastSegment
argument_list|()
expr_stmt|;
block|}
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collectionPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|destinationPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|newName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"moveCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|moveResource
parameter_list|(
name|String
name|resourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|moveResource
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|resourcePath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|destinationPath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|newName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|moveResource
parameter_list|(
name|XmldbURI
name|resourcePath
parameter_list|,
name|XmldbURI
name|destinationPath
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resourcePath
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|resourcePath
argument_list|)
expr_stmt|;
if|if
condition|(
name|destinationPath
operator|==
literal|null
condition|)
name|destinationPath
operator|=
name|resourcePath
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
else|else
name|destinationPath
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|newName
operator|=
name|resourcePath
operator|.
name|lastSegment
argument_list|()
expr_stmt|;
block|}
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|resourcePath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|destinationPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|newName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"moveResource"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|copy
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|copy
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|collectionPath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|destinationPath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|newName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.CollectionManagementServiceImpl#copy(java.lang.String, java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|copy
parameter_list|(
name|XmldbURI
name|collectionPath
parameter_list|,
name|XmldbURI
name|destinationPath
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collectionPath
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|collectionPath
argument_list|)
expr_stmt|;
name|destinationPath
operator|=
name|destinationPath
operator|==
literal|null
condition|?
name|collectionPath
operator|.
name|removeLastSegment
argument_list|()
else|:
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|newName
operator|=
name|collectionPath
operator|.
name|lastSegment
argument_list|()
expr_stmt|;
block|}
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collectionPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|destinationPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|newName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"copyCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|copyResource
parameter_list|(
name|String
name|resourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|copyResource
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|resourcePath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|destinationPath
argument_list|)
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|newName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|copyResource
parameter_list|(
name|XmldbURI
name|resourcePath
parameter_list|,
name|XmldbURI
name|destinationPath
parameter_list|,
name|XmldbURI
name|newName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|resourcePath
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|resourcePath
argument_list|)
expr_stmt|;
if|if
condition|(
name|destinationPath
operator|==
literal|null
condition|)
name|destinationPath
operator|=
name|resourcePath
operator|.
name|removeLastSegment
argument_list|()
expr_stmt|;
else|else
name|destinationPath
operator|=
name|parent
operator|.
name|getPathURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|destinationPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
condition|)
block|{
name|newName
operator|=
name|resourcePath
operator|.
name|lastSegment
argument_list|()
expr_stmt|;
block|}
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|resourcePath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|destinationPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|newName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"copyResource"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

