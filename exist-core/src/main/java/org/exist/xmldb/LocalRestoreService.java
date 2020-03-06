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
name|exist
operator|.
name|backup
operator|.
name|Restore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|RestoreListener
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
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_class
specifier|public
class|class
name|LocalRestoreService
extends|extends
name|AbstractLocalService
implements|implements
name|EXistRestoreService
block|{
specifier|public
name|LocalRestoreService
parameter_list|(
specifier|final
name|Subject
name|user
parameter_list|,
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|LocalCollection
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|parent
argument_list|)
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
specifier|final
annotation|@
name|Nullable
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
name|Restore
name|restore
init|=
operator|new
name|Restore
argument_list|()
decl_stmt|;
name|withDb
argument_list|(
parameter_list|(
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
block|{
try|try
block|{
name|restore
operator|.
name|restore
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|newAdminPassword
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|backup
argument_list|)
argument_list|,
operator|new
name|RestoreListenerAdapter
argument_list|(
name|restoreListener
argument_list|)
argument_list|,
name|overwriteApps
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
argument_list|)
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
specifier|private
specifier|static
class|class
name|RestoreListenerAdapter
implements|implements
name|RestoreListener
block|{
specifier|private
specifier|final
name|RestoreServiceTaskListener
name|restoreListener
decl_stmt|;
specifier|public
name|RestoreListenerAdapter
parameter_list|(
specifier|final
name|RestoreServiceTaskListener
name|restoreListener
parameter_list|)
block|{
name|this
operator|.
name|restoreListener
operator|=
name|restoreListener
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|started
parameter_list|(
specifier|final
name|long
name|numberOfFiles
parameter_list|)
block|{
name|restoreListener
operator|.
name|started
argument_list|(
name|numberOfFiles
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingDescriptor
parameter_list|(
specifier|final
name|String
name|backupDescriptor
parameter_list|)
block|{
name|restoreListener
operator|.
name|processingDescriptor
argument_list|(
name|backupDescriptor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|createdCollection
parameter_list|(
specifier|final
name|String
name|collection
parameter_list|)
block|{
name|restoreListener
operator|.
name|createdCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restoredResource
parameter_list|(
specifier|final
name|String
name|resource
parameter_list|)
block|{
name|restoreListener
operator|.
name|restoredResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|skipResources
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|long
name|count
parameter_list|)
block|{
name|restoreListener
operator|.
name|skipResources
argument_list|(
name|message
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|restoreListener
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|restoreListener
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|restoreListener
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finished
parameter_list|()
block|{
name|restoreListener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

