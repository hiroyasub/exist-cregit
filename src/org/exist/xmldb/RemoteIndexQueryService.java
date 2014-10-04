begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  *     * Copyright (C) 2001-04 Wolfgang M. Meier wolfgang@exist-db.org  *   * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|List
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
implements|implements
name|IndexQueryService
block|{
specifier|protected
name|XmlRpcClient
name|rpcClient
init|=
literal|null
decl_stmt|;
specifier|protected
name|RemoteCollection
name|parent
decl_stmt|;
specifier|public
name|RemoteIndexQueryService
parameter_list|(
name|XmlRpcClient
name|client
parameter_list|,
name|RemoteCollection
name|parent
parameter_list|)
block|{
name|this
operator|.
name|rpcClient
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
comment|/** @see org.exist.xmldb.IndexQueryService#reindexCollection() */
specifier|public
name|void
name|reindexCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|reindexCollection
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** (non-Javadoc)      * @deprecated Use XmldbURI version instead      * @see org.exist.xmldb.IndexQueryService#reindexCollection(java.lang.String)      */
specifier|public
name|void
name|reindexCollection
parameter_list|(
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
comment|/* (non-Javadoc)          * @see org.exist.xmldb.IndexQueryService#reindexCollection(java.lang.String)          */
specifier|public
name|void
name|reindexCollection
parameter_list|(
name|XmldbURI
name|collectionPath
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
block|}
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
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
try|try
block|{
name|rpcClient
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
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#getIndexedElements(boolean) 	 */
specifier|public
name|Occurrences
index|[]
name|getIndexedElements
parameter_list|(
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
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|parent
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
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"getIndexedElements"
argument_list|,
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Occurrences
name|occurrences
index|[]
init|=
operator|new
name|Occurrences
index|[
name|result
operator|.
name|length
index|]
decl_stmt|;
name|Object
index|[]
name|row
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|occurrences
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|row
operator|=
operator|(
name|Object
index|[]
operator|)
name|result
index|[
name|i
index|]
expr_stmt|;
specifier|final
name|QName
name|qname
init|=
operator|new
name|QName
argument_list|(
operator|(
name|String
operator|)
name|row
index|[
literal|0
index|]
argument_list|,
operator|(
name|String
operator|)
name|row
index|[
literal|1
index|]
argument_list|,
operator|(
name|String
operator|)
name|row
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|occurrences
index|[
name|i
index|]
operator|=
operator|new
name|Occurrences
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|occurrences
index|[
name|i
index|]
operator|.
name|addOccurrences
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|row
index|[
literal|3
index|]
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|occurrences
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
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#scanIndexTerms(java.lang.String, java.lang.String, boolean) 	 */
specifier|public
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
name|String
name|start
parameter_list|,
name|String
name|end
parameter_list|,
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
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|parent
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
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"scanIndexTerms"
argument_list|,
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Occurrences
name|occurrences
index|[]
init|=
operator|new
name|Occurrences
index|[
name|result
operator|.
name|length
index|]
decl_stmt|;
name|Object
index|[]
name|row
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|occurrences
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|row
operator|=
operator|(
name|Object
index|[]
operator|)
name|result
index|[
name|i
index|]
expr_stmt|;
name|occurrences
index|[
name|i
index|]
operator|=
operator|new
name|Occurrences
argument_list|(
operator|(
name|String
operator|)
name|row
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|occurrences
index|[
name|i
index|]
operator|.
name|addOccurrences
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|row
index|[
literal|1
index|]
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|occurrences
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
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#scanIndexTerms(java.lang.String, java.lang.String, java.lang.String) 	 */
specifier|public
name|Occurrences
index|[]
name|scanIndexTerms
parameter_list|(
name|String
name|xpath
parameter_list|,
name|String
name|start
parameter_list|,
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
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
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
name|rpcClient
operator|.
name|execute
argument_list|(
literal|"scanIndexTerms"
argument_list|,
name|params
argument_list|)
decl_stmt|;
specifier|final
name|Occurrences
name|occurrences
index|[]
init|=
operator|new
name|Occurrences
index|[
name|result
operator|.
name|length
index|]
decl_stmt|;
name|Object
index|[]
name|row
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|occurrences
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|row
operator|=
operator|(
name|Object
index|[]
operator|)
name|result
index|[
name|i
index|]
expr_stmt|;
name|occurrences
index|[
name|i
index|]
operator|=
operator|new
name|Occurrences
argument_list|(
operator|(
name|String
operator|)
name|row
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|occurrences
index|[
name|i
index|]
operator|.
name|addOccurrences
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|row
index|[
literal|1
index|]
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|occurrences
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
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Service#getName() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Service#getVersion() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Service#setCollection(org.xmldb.api.base.Collection) 	 */
specifier|public
name|void
name|setCollection
parameter_list|(
name|Collection
name|col
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
name|col
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Configurable#getProperty(java.lang.String) 	 */
specifier|public
name|String
name|getProperty
parameter_list|(
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
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.base.Configurable#setProperty(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.IndexQueryService#configureCollection(java.lang.String) 	 */
specifier|public
name|void
name|configureCollection
parameter_list|(
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
name|parent
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
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|1
argument_list|)
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
name|rpcClient
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

