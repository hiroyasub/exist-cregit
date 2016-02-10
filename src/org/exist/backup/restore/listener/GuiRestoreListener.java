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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JFrame
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|SwingUtilities
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
name|RestoreDialog
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|GuiRestoreListener
extends|extends
name|AbstractRestoreListener
block|{
specifier|private
specifier|final
name|RestoreDialog
name|dialog
decl_stmt|;
specifier|public
name|GuiRestoreListener
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GuiRestoreListener
parameter_list|(
name|JFrame
name|parent
parameter_list|)
block|{
name|dialog
operator|=
operator|new
name|RestoreDialog
argument_list|(
name|parent
argument_list|,
literal|"Restoring data ..."
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dialog
operator|.
name|setVisible
argument_list|(
literal|true
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
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|dialog
operator|.
name|displayMessage
argument_list|(
name|message
argument_list|)
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
name|super
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|dialog
operator|.
name|displayMessage
argument_list|(
name|message
argument_list|)
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
name|super
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|dialog
operator|.
name|displayMessage
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|observe
parameter_list|(
specifier|final
name|Observable
name|observable
parameter_list|)
block|{
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|observable
operator|.
name|addObserver
argument_list|(
name|dialog
operator|.
name|getObserver
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentBackup
parameter_list|(
specifier|final
name|String
name|currentBackup
parameter_list|)
block|{
name|super
operator|.
name|setCurrentBackup
argument_list|(
name|currentBackup
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|dialog
operator|.
name|setBackup
argument_list|(
name|currentBackup
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentCollection
parameter_list|(
specifier|final
name|String
name|currentCollectionName
parameter_list|)
block|{
name|super
operator|.
name|setCurrentCollection
argument_list|(
name|currentCollectionName
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|dialog
operator|.
name|setCollection
argument_list|(
name|currentCollectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentResource
parameter_list|(
specifier|final
name|String
name|currentResourceName
parameter_list|)
block|{
name|super
operator|.
name|setCurrentResource
argument_list|(
name|currentResourceName
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
parameter_list|()
lambda|->
name|dialog
operator|.
name|setResource
argument_list|(
name|currentResourceName
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|hideDialog
parameter_list|()
block|{
name|dialog
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

