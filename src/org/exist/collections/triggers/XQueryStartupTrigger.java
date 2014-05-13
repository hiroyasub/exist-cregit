begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

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
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

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
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|security
operator|.
name|Permission
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
name|PermissionDeniedException
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
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|SourceFactory
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
name|StartupTrigger
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
name|lock
operator|.
name|Lock
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|XmldbURI
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
name|CompiledXQuery
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
name|XQuery
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
name|XQueryContext
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * Startup Trigger to fire XQuery scripts during database startup.  *  * Load scripts into /db/system/autostart as DBA.  *  *<pre>  * {@code  *<startup>  *<triggers>  *<trigger class="org.exist.collections.triggers.XQueryStartupTrigger"/>  *</triggers>  *</startup>  * }  *</pre>  *  * Due to security reasons individual scripts cannot be specified anymore.  *  *<pre>  * {@code  *<parameter name="xquery" value="/db/script1.xq"/>  *<parameter name="xquery" value="/db/script2.xq"/>  * }  *</pre>  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|XQueryStartupTrigger
implements|implements
name|StartupTrigger
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XQueryStartupTrigger
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XQUERY
init|=
literal|"xquery"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|AUTOSTART_COLLECTION
init|=
literal|"/db/system/autostart"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|XQUERY_EXTENSIONS
init|=
block|{
literal|".xq"
block|,
literal|".xquery"
block|,
literal|".xqy"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|REQUIRED_COLLECTION_MODE
init|=
name|Permission
operator|.
name|DEFAULT_SYSTEM_SECURITY_COLLECTION_PERM
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REQUIRED_MIMETYPE
init|=
literal|"application/xquery"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Startup Trigger for stored XQueries"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|getScriptsInStartupCollection
argument_list|(
name|broker
argument_list|)
control|)
block|{
name|executeQuery
argument_list|(
name|broker
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|//        for (String path : getParameters(params)) {
comment|//            executeQuery(broker, path);
comment|//        }
block|}
comment|/**      * List all xquery scripts in /db/system/autostart      *      * @param broker The exist-db broker      * @return List of xquery scripts      */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getScriptsInStartupCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
comment|// Return values
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|XmldbURI
name|uri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|AUTOSTART_COLLECTION
argument_list|)
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Collection '%s' not found."
argument_list|,
name|AUTOSTART_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|createAutostartCollection
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Scanning collection '%s'."
argument_list|,
name|AUTOSTART_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPermissionsOK
argument_list|(
name|collection
argument_list|)
condition|)
block|{
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|documents
init|=
name|collection
operator|.
name|iteratorNoLock
argument_list|(
name|broker
argument_list|)
decl_stmt|;
while|while
condition|(
name|documents
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocumentImpl
name|document
init|=
name|documents
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|docPath
init|=
name|document
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isPermissionsOK
argument_list|(
name|document
argument_list|)
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|endsWithAny
argument_list|(
name|docPath
argument_list|,
name|XQUERY_EXTENSIONS
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
operator|+
name|docPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Skipped document '%s', not an xquery script."
argument_list|,
name|docPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Document %s should be owned by DBA, mode %s, mimetype %s"
argument_list|,
name|docPath
argument_list|,
name|REQUIRED_COLLECTION_MODE
argument_list|,
name|REQUIRED_MIMETYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Collection %s should be owned by SYSTEM/DBA, mode %s."
argument_list|,
name|AUTOSTART_COLLECTION
argument_list|,
name|REQUIRED_COLLECTION_MODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Found %s xquery scripts in '%s'."
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|,
name|AUTOSTART_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Clean up resources
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|paths
return|;
block|}
comment|/**      * Verify that the permissions for a collection are SYSTEM/DBA/770      *      * @param collection The collection      * @return TRUE if the conditions are met, else FALSE      */
specifier|private
name|boolean
name|isPermissionsOK
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|Permission
name|perms
init|=
name|collection
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
return|return
operator|(
name|perms
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM
argument_list|)
operator|&&
name|perms
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
operator|&&
name|perms
operator|.
name|getMode
argument_list|()
operator|==
name|REQUIRED_COLLECTION_MODE
operator|)
return|;
block|}
comment|/**      * Verify that the owner of the document is DBA, the document is owned by the DBA group and that the permissions are      * set 0770, and the mimetype is set application/xquery.      *      * @param collection The document      * @return TRUE if the conditions are met, else FALSE      */
specifier|private
name|boolean
name|isPermissionsOK
parameter_list|(
name|DocumentImpl
name|document
parameter_list|)
block|{
name|Permission
name|perms
init|=
name|document
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
return|return
operator|(
name|perms
operator|.
name|getOwner
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
operator|&&
name|perms
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
operator|&&
name|perms
operator|.
name|getMode
argument_list|()
operator|==
name|REQUIRED_COLLECTION_MODE
operator|&&
name|document
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
operator|.
name|equals
argument_list|(
name|REQUIRED_MIMETYPE
argument_list|)
operator|)
return|;
block|}
comment|/**      * Get all XQuery paths from provided parameters in conf.xml      */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
comment|// Return values
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// The complete data map
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
argument_list|>
name|data
init|=
name|params
operator|.
name|entrySet
argument_list|()
decl_stmt|;
comment|// Iterate over all entries
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|data
control|)
block|{
comment|// only the 'xpath' parameter is used.
if|if
condition|(
name|XQUERY
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// Iterate over all values (object lists)
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|list
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Rewrite to URL in database
name|value
operator|=
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
operator|+
name|value
expr_stmt|;
comment|// Prevent double entries
if|if
condition|(
operator|!
name|paths
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Path '%s' should start with a '/'"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Found %s 'xquery' entries."
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|paths
return|;
block|}
comment|/**      * Execute xquery on path      *      * @param broker eXist database broker      * @param path path to query, formatted as xmldb:exist:///db/...      */
specifier|private
name|void
name|executeQuery
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|XQueryContext
name|context
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Get path to xquery
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"No Xquery found at '%s'"
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Setup xquery service
name|XQuery
name|service
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|context
operator|=
name|service
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|TRIGGER
argument_list|)
expr_stmt|;
comment|// Allow use of modules with relative paths
name|String
name|moduleLoadPath
init|=
name|StringUtils
operator|.
name|substringBeforeLast
argument_list|(
name|path
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|context
operator|.
name|setModuleLoadPath
argument_list|(
name|moduleLoadPath
argument_list|)
expr_stmt|;
comment|// Compile query
name|CompiledXQuery
name|compiledQuery
init|=
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Starting Xquery at '%s'"
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
comment|// Finish preparation
name|context
operator|.
name|prepareForExecution
argument_list|()
expr_stmt|;
comment|// Execute
name|Sequence
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Log results
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Result xquery: '%s'"
argument_list|,
name|result
operator|.
name|getStringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Dirty, catch it all
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"An error occured during preparation/execution of the xquery script %s: %s"
argument_list|,
name|path
argument_list|,
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|runCleanupTasks
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Create autostart collection when not existent      *      * @param broker The exist-db broker      */
specifier|private
name|void
name|createAutostartCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Creating %s"
argument_list|,
name|AUTOSTART_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|TransactionManager
name|txnManager
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|txnManager
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|XmldbURI
name|newCollection
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|AUTOSTART_COLLECTION
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Create collection
name|Collection
name|created
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|newCollection
argument_list|)
decl_stmt|;
comment|// Set ownership
name|Permission
name|perms
init|=
name|created
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
name|perms
operator|.
name|setOwner
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|perms
operator|.
name|setGroup
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
expr_stmt|;
name|perms
operator|.
name|setMode
argument_list|(
name|REQUIRED_COLLECTION_MODE
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|created
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Commit change
name|txnManager
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finished creation of collection"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|txnManager
operator|.
name|close
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

