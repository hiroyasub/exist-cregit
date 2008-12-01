begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|service
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
name|exist
operator|.
name|validation
operator|.
name|Validator
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
name|RemoteCollection
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
name|List
import|;
end_import

begin_comment
comment|/**  *  XML validation service for eXist database.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|RemoteValidationService
implements|implements
name|ValidationService
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RemoteValidationService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XmlRpcClient
name|client
init|=
literal|null
decl_stmt|;
specifier|private
name|RemoteCollection
name|remoteCollection
init|=
literal|null
decl_stmt|;
specifier|private
name|Validator
name|validator
init|=
literal|null
decl_stmt|;
specifier|public
name|RemoteValidationService
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|XmlRpcClient
name|client
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Starting RemoteValidationService"
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|remoteCollection
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * Validate specified resource.      */
specifier|public
name|boolean
name|validateResource
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|validateResource
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|validateResource
parameter_list|(
name|String
name|documentPath
parameter_list|,
name|String
name|grammarPath
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|documentPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|documentPath
operator|=
literal|"xmldb:exist://"
operator|+
name|documentPath
expr_stmt|;
block|}
if|if
condition|(
name|grammarPath
operator|!=
literal|null
operator|&&
name|grammarPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|grammarPath
operator|=
literal|"xmldb:exist://"
operator|+
name|grammarPath
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Validating resource '"
operator|+
name|documentPath
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|boolean
name|documentIsValid
init|=
literal|false
decl_stmt|;
comment|//        documentPath = remoteCollection.getPathURI().resolveCollectionPath(documentPath);
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
name|documentPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|grammarPath
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|grammarPath
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Boolean
name|result
init|=
operator|(
name|Boolean
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"isValid"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|documentIsValid
operator|=
name|result
operator|.
name|booleanValue
argument_list|()
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
return|return
name|documentIsValid
return|;
block|}
comment|// ----------------------------------------------------------
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// left empty
block|}
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"ValidationService"
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
name|setProperty
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|str1
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// left empty
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// left empty
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

