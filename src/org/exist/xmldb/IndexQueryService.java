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
comment|/**  * Provides additional methods related to eXist's indexing system.  *   * @author wolf  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexQueryService
extends|extends
name|Service
block|{
specifier|public
name|void
name|configureCollection
parameter_list|(
name|String
name|configData
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the current collection, i.e. the collection from which      * this service has been retrieved.      *       * @throws XMLDBException      */
specifier|public
name|void
name|reindexCollection
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the collection specified by its path.      *       * @param collectionPath      * @throws XMLDBException      * @deprecated Use XmldbURI version instead      */
specifier|public
name|void
name|reindexCollection
parameter_list|(
name|String
name|collectionPath
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Reindex the collection specified by its path.      *       * @param collectionPath      * @throws XMLDBException      */
specifier|public
name|void
name|reindexCollection
parameter_list|(
name|XmldbURI
name|collectionPath
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Returns frequency statistics on all elements and attributes contained in the      * structure index for the current collection.      *       * @param inclusive      * @throws XMLDBException      */
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
function_decl|;
comment|/** 	 * Queries the fulltext index to retrieve information on indexed words contained 	 * in the index for the current collection. Returns a list of {@link Occurrences} for all  	 * words contained in the index. If param end is null, all words starting with  	 * the string sequence param start are returned. Otherwise, the method  	 * returns all words that come after start and before end in lexical order. 	 *  	 * @param start 	 * @param end 	 * @param inclusive 	 * @throws XMLDBException 	 */
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
function_decl|;
comment|/**      * Queries the fulltext index to retrieve information on indexed words occurring within      * the set of nodes identified by a given XPath expression. Returns a list of {@link Occurrences} for all       * words contained in the index. If param end is null, all words starting with       * the string sequence param start are returned. Otherwise, the method       * returns all words that come after start and before end in lexical order.      *       *       * @param xpath       * @param start       * @param end       * @throws XMLDBException       */
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
function_decl|;
block|}
end_interface

end_unit

