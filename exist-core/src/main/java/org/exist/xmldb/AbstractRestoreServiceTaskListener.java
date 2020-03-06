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
name|util
operator|.
name|FileUtils
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRestoreServiceTaskListener
implements|implements
name|RestoreServiceTaskListener
block|{
annotation|@
name|Override
specifier|public
name|void
name|startedZipForTransfer
parameter_list|(
specifier|final
name|long
name|totalUncompressedSize
parameter_list|)
block|{
name|info
argument_list|(
literal|"Creating Zip of restore data (uncompressed="
operator|+
name|FileUtils
operator|.
name|humanSize
argument_list|(
name|totalUncompressedSize
argument_list|)
operator|+
literal|")..."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addedFileToZipForTransfer
parameter_list|(
specifier|final
name|long
name|uncompressedSize
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|finishedZipForTransfer
parameter_list|()
block|{
name|info
argument_list|(
literal|"Finished creating Zip of restore data."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startedTransfer
parameter_list|(
specifier|final
name|long
name|transferSize
parameter_list|)
block|{
name|info
argument_list|(
literal|"Transferring restore data to remote server (size="
operator|+
name|FileUtils
operator|.
name|humanSize
argument_list|(
name|transferSize
argument_list|)
operator|+
literal|")..."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|transferred
parameter_list|(
specifier|final
name|long
name|chunkSize
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|finishedTransfer
parameter_list|()
block|{
name|info
argument_list|(
literal|"Finished transferring restore data to remote server."
argument_list|)
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
name|info
argument_list|(
literal|"Starting restore of backup..."
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
name|info
argument_list|(
literal|"Restoring from: "
operator|+
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
name|info
argument_list|(
literal|"Creating collection "
operator|+
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
name|info
argument_list|(
literal|"Restored "
operator|+
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
name|warn
argument_list|(
literal|"Skipping "
operator|+
name|count
operator|+
literal|" resources. "
operator|+
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
name|info
argument_list|(
literal|"Finished restore of backup."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

