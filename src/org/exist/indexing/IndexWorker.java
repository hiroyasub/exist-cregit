begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|DocumentImpl
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
name|DatabaseConfigurationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Provide concurrent access to the index structure. Implements the core operations on the index.  * The methods in this class are used in a multi-threaded environment. Every thread accessing the  * database will have exactly one IndexWorker for every index. {@link org.exist.indexing.Index#getWorker()}  * should thus return a new IndexWorker whenever it is  called. Implementations of IndexWorker have  * to take care of synchronizing access to shared resources.  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexWorker
block|{
comment|/**      * Returns an ID which uniquely identifies this index. This will usually be the class name.      * @return a unique ID identifying this index.      */
name|String
name|getIndexId
parameter_list|()
function_decl|;
comment|/**      * Read an index configuration from an collection.xconf configuration document.      *      * This method is called by the {@link org.exist.collections.CollectionConfiguration} while      * reading the collection.xconf configuration file for a given collection. The configNodes      * parameter lists all top-level child nodes below the&lt;index&gt; element in the      * collection.xconf. The IndexWorker should scan this list and handle those elements      * it understands.      *      * The returned Object will be stored in the collection configuration structure associated      * with each collection. It can later be retrieved from the collection configuration, e.g. to      * check if a given node should be indexed or not.      *      * @param configNodes      * @param namespaces      * @return      * @throws DatabaseConfigurationException      */
name|Object
name|configure
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
function_decl|;
comment|/**      * Flush the index. This method will be called when indexing a document. The implementation should      * immediately process all data it has buffered (if there is any), release as many memory resources      * as it can and prepare for being reused for a different job.      */
name|void
name|flush
parameter_list|()
function_decl|;
comment|/**      * Return a stream listener to index the specified document. There will never be more than one      * StreamListener being used per thread, so it is safe for the implementation to reuse a      * single StreamListener.      *      * @param document the document to be indexed.      * @return a StreamListener      */
name|StreamListener
name|getListener
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

