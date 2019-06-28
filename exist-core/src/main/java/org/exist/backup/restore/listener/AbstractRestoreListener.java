begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2005-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id: Restore.java 15109 2011-08-09 13:03:09Z deliriumsky $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRestoreListener
implements|implements
name|RestoreListener
block|{
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
name|currentBackup
parameter_list|)
block|{
name|info
argument_list|(
literal|"Processing backup: "
operator|+
name|currentBackup
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

