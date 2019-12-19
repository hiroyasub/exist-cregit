begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClientConfigImpl
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
name|FileUtils
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
name|Leasable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|RpcAPI
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|BasicFileAttributes
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|RemoteCollection
operator|.
name|MAX_UPLOAD_CHUNK
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteRestoreService
implements|implements
name|EXistRestoreService
block|{
specifier|private
specifier|final
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
name|leasableXmlRpcClient
decl_stmt|;
specifier|private
specifier|final
name|RemoteCallSite
name|remoteCallSite
decl_stmt|;
comment|/**      * Constructor for DatabaseInstanceManagerImpl.      *      * @param leasableXmlRpcClient the leasable instance of a the XML RPC client      * @param remoteCallSite the remote call site      */
specifier|public
name|RemoteRestoreService
parameter_list|(
specifier|final
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
name|leasableXmlRpcClient
parameter_list|,
specifier|final
name|RemoteCallSite
name|remoteCallSite
parameter_list|)
block|{
name|this
operator|.
name|leasableXmlRpcClient
operator|=
name|leasableXmlRpcClient
expr_stmt|;
name|this
operator|.
name|remoteCallSite
operator|=
name|remoteCallSite
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"RestoreService"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"1.0"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
specifier|final
name|String
name|backup
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|newAdminPassword
parameter_list|,
specifier|final
name|RestoreServiceTaskListener
name|restoreListener
parameter_list|,
specifier|final
name|boolean
name|overwriteApps
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Path
name|backupPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|backup
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|backupPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"Backup does not exist: "
operator|+
name|backupPath
argument_list|)
throw|;
block|}
specifier|final
name|String
name|remoteFileName
decl_stmt|;
specifier|final
name|String
name|backupFileName
init|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|backupPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|backupFileName
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|remoteFileName
operator|=
name|uploadBackupFile
argument_list|(
name|backupPath
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|backupPath
argument_list|)
condition|)
block|{
specifier|final
name|Path
name|tmpZipFile
init|=
name|zipBackupDir
argument_list|(
name|backupPath
argument_list|)
decl_stmt|;
try|try
block|{
name|remoteFileName
operator|=
name|uploadBackupFile
argument_list|(
name|tmpZipFile
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tmpZipFile
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|backupFileName
operator|.
name|equals
argument_list|(
literal|"__contents__.xml"
argument_list|)
condition|)
block|{
specifier|final
name|Path
name|tmpZipFile
init|=
name|zipBackupDir
argument_list|(
name|backupPath
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|remoteFileName
operator|=
name|uploadBackupFile
argument_list|(
name|tmpZipFile
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tmpZipFile
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"Backup does not appear to be an eXist-db backup"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|restoreTaskHandle
decl_stmt|;
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
name|newAdminPassword
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|remoteFileName
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|overwriteApps
argument_list|)
expr_stmt|;
name|restoreTaskHandle
operator|=
operator|(
name|String
operator|)
name|remoteCallSite
operator|.
name|execute
argument_list|(
literal|"restore"
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// has the admin password changed?
try|try
init|(
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
operator|.
name|Lease
name|xmlRpcClientLease
init|=
name|leasableXmlRpcClient
operator|.
name|lease
argument_list|()
init|)
block|{
specifier|final
name|XmlRpcClientConfigImpl
name|config
init|=
operator|(
name|XmlRpcClientConfigImpl
operator|)
name|xmlRpcClientLease
operator|.
name|get
argument_list|()
operator|.
name|getClientConfig
argument_list|()
decl_stmt|;
specifier|final
name|String
name|currentPassword
init|=
name|config
operator|.
name|getBasicPassword
argument_list|()
decl_stmt|;
if|if
condition|(
name|newAdminPassword
operator|!=
literal|null
operator|&&
operator|!
name|currentPassword
operator|.
name|equals
argument_list|(
name|newAdminPassword
argument_list|)
condition|)
block|{
name|config
operator|.
name|setBasicPassword
argument_list|(
name|newAdminPassword
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// restore interrupt status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// now we need to poll for results...
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
name|params
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|restoreTaskHandle
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|finished
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Object
index|[]
name|results
init|=
operator|(
name|Object
index|[]
operator|)
name|remoteCallSite
operator|.
name|execute
argument_list|(
literal|"getRestoreTaskEvents"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Object
name|result
range|:
name|results
control|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|result
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|String
name|event
range|:
name|events
control|)
block|{
comment|// dispatch event to the listener
switch|switch
condition|(
name|RpcAPI
operator|.
name|RestoreTaskEvent
operator|.
name|fromCode
argument_list|(
name|event
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
case|case
name|STARTED
case|:
name|restoreListener
operator|.
name|started
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|PROCESSING_DESCRIPTOR
case|:
name|restoreListener
operator|.
name|processingDescriptor
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CREATED_COLLECTION
case|:
name|restoreListener
operator|.
name|createdCollection
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|RESTORED_RESOURCE
case|:
name|restoreListener
operator|.
name|restoredResource
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|INFO
case|:
name|restoreListener
operator|.
name|info
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|WARN
case|:
name|restoreListener
operator|.
name|warn
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|ERROR
case|:
name|restoreListener
operator|.
name|error
argument_list|(
name|event
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FINISHED
case|:
name|restoreListener
operator|.
name|finished
argument_list|()
expr_stmt|;
name|finished
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|finished
condition|)
block|{
break|break;
comment|// exit the for loop! we are done...
block|}
block|}
comment|// before looping... sleep a bit, if we got zero events sleep longer as the server is likely busy restoring something large
if|if
condition|(
operator|!
name|finished
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|events
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// restore interrupt status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// stop looping on finished event or on exception
block|}
block|}
specifier|private
name|String
name|uploadBackupFile
parameter_list|(
specifier|final
name|Path
name|backupZipFile
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|String
name|fileName
init|=
literal|null
decl_stmt|;
specifier|final
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|Files
operator|.
name|size
argument_list|(
name|backupZipFile
argument_list|)
argument_list|,
name|MAX_UPLOAD_CHUNK
argument_list|)
index|]
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|backupZipFile
argument_list|)
init|)
block|{
name|int
name|len
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
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
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|fileName
operator|=
operator|(
name|String
operator|)
name|remoteCallSite
operator|.
name|execute
argument_list|(
literal|"upload"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fileName
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
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
literal|"Unable to upload backup file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Path
name|zipBackupDir
parameter_list|(
specifier|final
name|Path
name|dir
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
specifier|final
name|Path
name|zipFile
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"remote-restore-service"
argument_list|,
literal|"zip"
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|OutputStream
name|fos
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|zipFile
argument_list|)
init|;
specifier|final
name|ZipOutputStream
name|zos
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|fos
argument_list|)
init|)
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|dir
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|,
specifier|final
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|zipEntryPath
init|=
name|dir
operator|.
name|relativize
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|zos
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
name|zipEntryPath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|file
argument_list|,
name|zos
argument_list|)
expr_stmt|;
name|zos
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|zipFile
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
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
literal|"Unable to zip backup dir: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
block|{
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
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
name|s
parameter_list|,
specifier|final
name|String
name|s1
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

