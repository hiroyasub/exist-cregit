begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|util
operator|.
name|Occurrences
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

begin_class
specifier|public
class|class
name|RemoteIndexQueryService
extends|extends
name|AbstractRemote
implements|implements
name|IndexQueryService
block|{
specifier|private
specifier|final
name|XmlRpcClient
name|client
decl_stmt|;
specifier|public
name|RemoteIndexQueryService
parameter_list|(
specifier|final
name|XmlRpcClient
name|client
parameter_list|,
specifier|final
name|RemoteCollection
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"IndexQueryService"
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|reindexCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|reindexCollection
argument_list|(
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @deprecated {@link org.exist.xmldb.IndexQueryService#reindexCollection(org.exist.xmldb.XmldbURI)}      */
annotation|@
name|Deprecated
annotation|@
name|Override
specifier|public
name|void
name|reindexCollection
parameter_list|(
specifier|final
name|String
name|collectionPath
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|reindexCollection
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|collectionPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|reindexCollection
parameter_list|(
specifier|final
name|XmldbURI
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XmldbURI
name|collectionPath
init|=
name|resolve
argument_list|(
name|collection
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"reindexCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"xmlrpc error while doing reindexCollection: "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Occurrences
index|[]
name|getIndexedElements
parameter_list|(
specifier|final
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|inclusive
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|result
init|=
operator|(
name|Object
index|[]
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"getIndexedElements"
argument_list|,
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Stream
argument_list|<
name|Occurrences
argument_list|>
name|occurrences
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|result
argument_list|)
operator|.
name|map
argument_list|(
name|o
lambda|->
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
operator|.
name|map
argument_list|(
name|row
lambda|->
operator|new
name|Occurrences
argument_list|(
operator|new
name|QName
argument_list|(
name|row
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|row
index|[
literal|1
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|row
index|[
literal|2
index|]
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
operator|(
name|Integer
operator|)
name|row
index|[
literal|3
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|occurrences
operator|.
name|toArray
argument_list|(
name|size
lambda|->
operator|new
name|Occurrences
index|[
name|size
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"xmlrpc error while retrieving indexed elements"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
specifier|final
name|String
name|start
parameter_list|,
specifier|final
name|String
name|end
parameter_list|,
specifier|final
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|inclusive
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|result
init|=
operator|(
name|Object
index|[]
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"scanIndexTerms"
argument_list|,
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Stream
argument_list|<
name|Occurrences
argument_list|>
name|occurrences
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|result
argument_list|)
operator|.
name|map
argument_list|(
name|o
lambda|->
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
operator|.
name|map
argument_list|(
name|row
lambda|->
operator|new
name|Occurrences
argument_list|(
name|row
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|Integer
operator|)
name|row
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|occurrences
operator|.
name|toArray
argument_list|(
name|size
lambda|->
operator|new
name|Occurrences
index|[
name|size
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"xmlrpc error while retrieving indexed elements"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
specifier|final
name|String
name|xpath
parameter_list|,
specifier|final
name|String
name|start
parameter_list|,
specifier|final
name|String
name|end
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|xpath
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|end
argument_list|)
expr_stmt|;
specifier|final
name|Object
index|[]
name|result
init|=
operator|(
name|Object
index|[]
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"scanIndexTerms"
argument_list|,
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Stream
argument_list|<
name|Occurrences
argument_list|>
name|occurrences
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|result
argument_list|)
operator|.
name|map
argument_list|(
name|o
lambda|->
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
operator|.
name|map
argument_list|(
name|row
lambda|->
operator|new
name|Occurrences
argument_list|(
name|row
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|Integer
operator|)
name|row
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|occurrences
operator|.
name|toArray
argument_list|(
name|size
lambda|->
operator|new
name|Occurrences
index|[
name|size
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"xmlrpc error while retrieving indexed elements"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCollection
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
operator|.
name|collection
operator|=
operator|(
name|RemoteCollection
operator|)
name|collection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|configureCollection
parameter_list|(
specifier|final
name|String
name|configData
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|path
init|=
name|collection
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|configData
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|execute
argument_list|(
literal|"configureCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
literal|"xmlrpc error while doing reindexCollection: "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

