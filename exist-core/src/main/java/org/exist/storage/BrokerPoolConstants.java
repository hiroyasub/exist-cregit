begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2003-2016 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_comment
comment|/**  * Just static Constants used by {@link BrokerPool}  *  * We keep these here to reduce the visual  * complexity of the BrokerPool class  */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerPoolConstants
block|{
comment|//on-start, ready, go
comment|/*** initializing sub-components */
name|String
name|SIGNAL_STARTUP
init|=
literal|"startup"
decl_stmt|;
comment|/*** ready for recovery&amp; read-only operations */
name|String
name|SIGNAL_READINESS
init|=
literal|"ready"
decl_stmt|;
comment|/*** ready for writable operations */
name|String
name|SIGNAL_WRITABLE
init|=
literal|"writable"
decl_stmt|;
comment|/*** ready for writable operations */
name|String
name|SIGNAL_STARTED
init|=
literal|"started"
decl_stmt|;
comment|/*** running shutdown sequence */
name|String
name|SIGNAL_SHUTDOWN
init|=
literal|"shutdown"
decl_stmt|;
comment|/*** recovery aborted, db stopped */
name|String
name|SIGNAL_ABORTED
init|=
literal|"aborted"
decl_stmt|;
name|String
name|CONFIGURATION_CONNECTION_ELEMENT_NAME
init|=
literal|"db-connection"
decl_stmt|;
name|String
name|CONFIGURATION_STARTUP_ELEMENT_NAME
init|=
literal|"startup"
decl_stmt|;
name|String
name|CONFIGURATION_POOL_ELEMENT_NAME
init|=
literal|"pool"
decl_stmt|;
name|String
name|CONFIGURATION_SECURITY_ELEMENT_NAME
init|=
literal|"security"
decl_stmt|;
name|String
name|CONFIGURATION_RECOVERY_ELEMENT_NAME
init|=
literal|"recovery"
decl_stmt|;
name|String
name|DISK_SPACE_MIN_ATTRIBUTE
init|=
literal|"minDiskSpace"
decl_stmt|;
name|String
name|DATA_DIR_ATTRIBUTE
init|=
literal|"files"
decl_stmt|;
comment|//TODO : move elsewhere ?
name|String
name|RECOVERY_ENABLED_ATTRIBUTE
init|=
literal|"enabled"
decl_stmt|;
name|String
name|RECOVERY_POST_RECOVERY_CHECK
init|=
literal|"consistency-check"
decl_stmt|;
comment|//TODO : move elsewhere ?
name|String
name|COLLECTION_CACHE_SIZE_ATTRIBUTE
init|=
literal|"collectionCacheSize"
decl_stmt|;
name|String
name|MIN_CONNECTIONS_ATTRIBUTE
init|=
literal|"min"
decl_stmt|;
name|String
name|MAX_CONNECTIONS_ATTRIBUTE
init|=
literal|"max"
decl_stmt|;
name|String
name|SYNC_PERIOD_ATTRIBUTE
init|=
literal|"sync-period"
decl_stmt|;
name|String
name|SHUTDOWN_DELAY_ATTRIBUTE
init|=
literal|"wait-before-shutdown"
decl_stmt|;
name|String
name|NODES_BUFFER_ATTRIBUTE
init|=
literal|"nodesBuffer"
decl_stmt|;
comment|//Various configuration property keys (set by the configuration manager)
name|String
name|PROPERTY_STARTUP_TRIGGERS
init|=
literal|"startup.triggers"
decl_stmt|;
name|String
name|PROPERTY_DATA_DIR
init|=
literal|"db-connection.data-dir"
decl_stmt|;
name|String
name|PROPERTY_MIN_CONNECTIONS
init|=
literal|"db-connection.pool.min"
decl_stmt|;
name|String
name|PROPERTY_MAX_CONNECTIONS
init|=
literal|"db-connection.pool.max"
decl_stmt|;
name|String
name|PROPERTY_SYNC_PERIOD
init|=
literal|"db-connection.pool.sync-period"
decl_stmt|;
name|String
name|PROPERTY_SHUTDOWN_DELAY
init|=
literal|"wait-before-shutdown"
decl_stmt|;
name|String
name|DISK_SPACE_MIN_PROPERTY
init|=
literal|"db-connection.diskSpaceMin"
decl_stmt|;
comment|//TODO : move elsewhere ?
name|String
name|PROPERTY_COLLECTION_CACHE_SIZE
init|=
literal|"db-connection.collection-cache-size"
decl_stmt|;
comment|//TODO : move elsewhere ? Get fully qualified class name ?
name|String
name|DEFAULT_SECURITY_CLASS
init|=
literal|"org.exist.security.internal.SecurityManagerImpl"
decl_stmt|;
name|String
name|PROPERTY_SECURITY_CLASS
init|=
literal|"db-connection.security.class"
decl_stmt|;
name|String
name|PROPERTY_RECOVERY_ENABLED
init|=
literal|"db-connection.recovery.enabled"
decl_stmt|;
name|String
name|PROPERTY_RECOVERY_CHECK
init|=
literal|"db-connection.recovery.consistency-check"
decl_stmt|;
name|String
name|PROPERTY_SYSTEM_TASK_CONFIG
init|=
literal|"db-connection.system-task-config"
decl_stmt|;
name|String
name|PROPERTY_NODES_BUFFER
init|=
literal|"db-connection.nodes-buffer"
decl_stmt|;
name|String
name|PROPERTY_EXPORT_ONLY
init|=
literal|"db-connection.emergency"
decl_stmt|;
name|String
name|PROPERTY_RECOVERY_GROUP_COMMIT
init|=
literal|"db-connection.recovery.group-commit"
decl_stmt|;
name|String
name|RECOVERY_GROUP_COMMIT_ATTRIBUTE
init|=
literal|"group-commit"
decl_stmt|;
name|String
name|PROPERTY_RECOVERY_FORCE_RESTART
init|=
literal|"db-connection.recovery.force-restart"
decl_stmt|;
name|String
name|RECOVERY_FORCE_RESTART_ATTRIBUTE
init|=
literal|"force-restart"
decl_stmt|;
name|String
name|DOC_ID_MODE_ATTRIBUTE
init|=
literal|"doc-ids"
decl_stmt|;
name|String
name|DOC_ID_MODE_PROPERTY
init|=
literal|"db-connection.doc-ids.mode"
decl_stmt|;
name|String
name|PROPERTY_PAGE_SIZE
init|=
literal|"db-connection.page-size"
decl_stmt|;
comment|/**      * Default values      */
name|long
name|DEFAULT_SYNCH_PERIOD
init|=
literal|120000
decl_stmt|;
name|long
name|DEFAULT_MAX_SHUTDOWN_WAIT
init|=
literal|45000
decl_stmt|;
comment|//TODO : move this default setting to org.exist.collections.CollectionCache ?
name|int
name|DEFAULT_COLLECTION_BUFFER_SIZE
init|=
literal|64
decl_stmt|;
name|int
name|DEFAULT_PAGE_SIZE
init|=
literal|4096
decl_stmt|;
name|short
name|DEFAULT_DISK_SPACE_MIN
init|=
literal|64
decl_stmt|;
comment|// 64 MB
block|}
end_interface

end_unit

