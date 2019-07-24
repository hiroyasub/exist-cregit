begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|Service
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
comment|/**  * Provides additional methods related to eXist's indexing system.  *  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexQueryService
extends|extends
name|Service
block|{
name|void
name|configureCollection
parameter_list|(
name|String
name|configData
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the current collection, i.e. the collection from which      * this service has been retrieved.      *      * @throws XMLDBException if the operation fails.      */
name|void
name|reindexCollection
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the documemy in the current collection, i.e. the collection from which      * this service has been retrieved.      *      * @param name The name of the document      *      * @throws XMLDBException if the operation fails.      */
name|void
name|reindexDocument
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the collection specified by its path.      *      * @param collectionPath the collection path to reindex.      * @throws XMLDBException if the operation fails.      * @deprecated Use XmldbURI version instead      */
annotation|@
name|Deprecated
name|void
name|reindexCollection
parameter_list|(
name|String
name|collectionPath
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the collection specified by its path.      *      * @param collectionPath the collection path to reindex.      * @throws XMLDBException if the operation fails.      */
name|void
name|reindexCollection
parameter_list|(
name|XmldbURI
name|collectionPath
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Returns frequency statistics on all elements and attributes contained in the      * structure index for the current collection.      *      * @param inclusive true if we are inclusive.      * @return the occurences.      * @throws XMLDBException if the operation fails.      */
name|Occurrences
index|[]
name|getIndexedElements
parameter_list|(
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

