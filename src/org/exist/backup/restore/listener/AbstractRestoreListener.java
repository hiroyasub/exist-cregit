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
name|Observable
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRestoreListener
implements|implements
name|RestoreListener
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Problem
argument_list|>
name|problems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|currentCollectionName
decl_stmt|;
specifier|private
name|String
name|currentResourceName
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Observable
argument_list|>
name|observables
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|restoreStarting
parameter_list|()
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
name|restoreFinished
parameter_list|()
block|{
name|info
argument_list|(
literal|"Finished restore of backup."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|createCollection
parameter_list|(
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
name|setCurrentBackup
parameter_list|(
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
name|setCurrentCollection
parameter_list|(
name|String
name|currentCollectionName
parameter_list|)
block|{
name|this
operator|.
name|currentCollectionName
operator|=
name|currentCollectionName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentResource
parameter_list|(
name|String
name|currentResourceName
parameter_list|)
block|{
name|this
operator|.
name|currentResourceName
operator|=
name|currentResourceName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|observe
parameter_list|(
name|Observable
name|observable
parameter_list|)
block|{
if|if
condition|(
name|observables
operator|==
literal|null
condition|)
block|{
name|observables
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|observables
operator|.
name|contains
argument_list|(
name|observable
argument_list|)
condition|)
block|{
name|observables
operator|.
name|add
argument_list|(
name|observable
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|restored
parameter_list|(
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
name|warn
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|problems
operator|.
name|add
argument_list|(
operator|new
name|Warning
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
name|String
name|message
parameter_list|)
block|{
name|problems
operator|.
name|add
argument_list|(
operator|new
name|Error
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProblems
parameter_list|()
block|{
return|return
name|problems
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|warningsAndErrorsAsString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"------------------------------------\n"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Problems occured found during restore:\n"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Problem
name|problem
range|:
name|problems
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|problem
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|abstract
class|class
name|Problem
block|{
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
specifier|public
name|Problem
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|protected
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
block|}
specifier|private
class|class
name|Error
extends|extends
name|Problem
block|{
specifier|public
name|Error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ERROR: "
operator|+
name|getMessage
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|Warning
extends|extends
name|Problem
block|{
specifier|public
name|Warning
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"WARN: "
operator|+
name|getMessage
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

