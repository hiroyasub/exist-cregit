begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionConfigurationManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|CollectionTrigger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|DocumentTrigger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|Debuggee
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
name|SymbolTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeIdFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|PluginsManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|scheduler
operator|.
name|Scheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|CacheManager
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
name|MetaStorage
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
name|NotificationService
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
name|ProcessMonitor
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
name|blob
operator|.
name|BlobStore
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
name|journal
operator|.
name|JournalManager
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
name|txn
operator|.
name|TransactionManager
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
name|Configuration
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
name|PerformanceStats
import|;
end_import

begin_comment
comment|/**  * Database controller, all operation synchronized by this instance. (singleton)  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|Database
block|{
comment|// TODO: javadocs
specifier|public
name|String
name|getId
parameter_list|()
function_decl|;
name|ThreadGroup
name|getThreadGroup
parameter_list|()
function_decl|;
comment|/**      *       * @return SecurityManager      */
specifier|public
name|SecurityManager
name|getSecurityManager
parameter_list|()
function_decl|;
comment|/**      *       * @return IndexManager      */
specifier|public
name|IndexManager
name|getIndexManager
parameter_list|()
function_decl|;
comment|/**      *       * @return TransactionManager      */
specifier|public
name|TransactionManager
name|getTransactionManager
parameter_list|()
function_decl|;
comment|/**      * Get the database Journal Manager.      *      * @return the Journal Manager      */
name|Optional
argument_list|<
name|JournalManager
argument_list|>
name|getJournalManager
parameter_list|()
function_decl|;
comment|/**      *       * @return CacheManager      */
specifier|public
name|CacheManager
name|getCacheManager
parameter_list|()
function_decl|;
comment|/**      *       * @return Scheduler      */
specifier|public
name|Scheduler
name|getScheduler
parameter_list|()
function_decl|;
comment|/** 	 *  	 */
specifier|public
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|// TODO: remove 'throws EXistException'?
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
throws|throws
name|EXistException
function_decl|;
specifier|public
name|DBBroker
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
function_decl|;
comment|/*      * @Deprecated ?       *       * try {       *     broker = database.authenticate(account, credentials);      *       *     broker1 = database.get();       *     broker2 = database.get();       *     ...       *     brokerN = database.get();      *       * } finally {       *     database.release(broker);      * }      */
specifier|public
name|DBBroker
name|get
parameter_list|(
name|Optional
argument_list|<
name|Subject
argument_list|>
name|subject
parameter_list|)
throws|throws
name|EXistException
function_decl|;
specifier|public
name|DBBroker
name|getActiveBroker
parameter_list|()
function_decl|;
comment|// throws EXistException;
comment|/**      * Returns the number of brokers currently serving requests for the database      * instance.      *       * @return The brokers count      */
specifier|public
name|int
name|countActiveBrokers
parameter_list|()
function_decl|;
comment|/**      *       * @return Debuggee      */
specifier|public
name|Debuggee
name|getDebuggee
parameter_list|()
function_decl|;
specifier|public
name|PerformanceStats
name|getPerformanceStats
parameter_list|()
function_decl|;
comment|// old configuration
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
function_decl|;
specifier|public
name|NodeIdFactory
name|getNodeFactory
parameter_list|()
function_decl|;
specifier|public
name|Path
name|getStoragePlace
parameter_list|()
function_decl|;
specifier|public
name|CollectionConfigurationManager
name|getConfigurationManager
parameter_list|()
function_decl|;
comment|/**      * Master document triggers.      */
specifier|public
name|Collection
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|DocumentTrigger
argument_list|>
argument_list|>
name|getDocumentTriggers
parameter_list|()
function_decl|;
comment|// public DocumentTrigger getDocumentTrigger();
comment|/**      * Master Collection triggers.      */
specifier|public
name|Collection
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
argument_list|>
name|getCollectionTriggers
parameter_list|()
function_decl|;
comment|// public CollectionTrigger getCollectionTrigger();
specifier|public
name|void
name|registerDocumentTrigger
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|DocumentTrigger
argument_list|>
name|clazz
parameter_list|)
function_decl|;
specifier|public
name|void
name|registerCollectionTrigger
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
name|clazz
parameter_list|)
function_decl|;
specifier|public
name|ProcessMonitor
name|getProcessMonitor
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
function_decl|;
specifier|public
name|NotificationService
name|getNotificationService
parameter_list|()
function_decl|;
specifier|public
name|PluginsManager
name|getPluginsManager
parameter_list|()
function_decl|;
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
function_decl|;
specifier|public
name|SymbolTable
name|getSymbols
parameter_list|()
function_decl|;
specifier|public
name|MetaStorage
name|getMetaStorage
parameter_list|()
function_decl|;
name|void
name|addStatusObserver
parameter_list|(
specifier|final
name|Observer
name|statusObserver
parameter_list|)
function_decl|;
name|boolean
name|removeStatusObserver
parameter_list|(
specifier|final
name|Observer
name|statusObserver
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

