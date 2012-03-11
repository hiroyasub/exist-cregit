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
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
operator|.
name|DBException
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * Represents an arbitrary index structure that can be used by eXist. This is the  * main interface to be registered with the database instance. It provides methods  * to configure, open and close the index. These methods will be called by the main  * database instance during startup/shutdown. They don't need to be synchronized.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Index
block|{
comment|/**      * Returns an id which uniquely identifies this index.  This is usually the class name.       * @return a unique name identifying this index.      */
name|String
name|getIndexId
parameter_list|()
function_decl|;
comment|/**      * Returns a human-readable name which uniquely identifies this index. This is configured by the user      * @return a unique name identifying this index.      */
name|String
name|getIndexName
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link org.exist.storage.BrokerPool} on with this Index operates.      *       * @return the broker pool      */
name|BrokerPool
name|getBrokerPool
parameter_list|()
function_decl|;
comment|/**      * Configure the index and all resources associated with it. This method      * is called while the database instance is initializing and receives the      *<pre>&lt;module id="foo" class="bar"/&gt;</pre>      * section of the configuration file.      *      * @param pool the BrokerPool representing the current database instance.      * @param dataDir the main data directory where eXist stores its files (if relevant).      * @param config the module element which configures this index, as found in conf.xml      * @throws DatabaseConfigurationException      */
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
function_decl|;
comment|/**      * Opens the index for writing and reading. Will be called during initialization, but also      * if the database has to be restarted.      *      * @throws DatabaseConfigurationException      */
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
function_decl|;
comment|/**      * Closes the index and all associated resources.      *      * @throws DBException      */
name|void
name|close
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/**      * Sync the index. This method should make sure that all index contents are written to disk.      * It will be called during checkpoint events and the system relies on the index to materialize      * all data.      *      * @throws DBException      */
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/**      * Closes the index and removes it completely, including all resources and files      * associated to it. This method is called during database repair before the      * db contents are re-indexed.      */
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
function_decl|;
comment|/**      * Returns a new IndexWorker, which is used to access the index in a multi-threaded      * environment.      *      * Every database instance has a number of      * {@link org.exist.storage.DBBroker} objects. All operations on the db      * have to go through one of these brokers. Each DBBroker retrieves an      * IndexWorker for every index by calling this method.      *      * @param broker The DBBroker that owns this worker      * @return a new IndexWorker that can be used for concurrent access to the index.      */
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
comment|/**      * Convenience method that allows to check index consistency.      *       * @param broker the broker that will perform the operation.      * @return whether or not the index is in a consistent state.       * The definition of "consistency" is left to the user.      */
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

