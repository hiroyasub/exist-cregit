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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|xmldb
operator|.
name|UserManagementService
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
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  *  * @author  Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
class|class
name|CollectionDeferredPermission
extends|extends
name|AbstractDeferredPermission
argument_list|<
name|Collection
argument_list|>
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|CollectionDeferredPermission
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|CollectionDeferredPermission
parameter_list|(
specifier|final
name|RestoreListener
name|listener
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|owner
parameter_list|,
specifier|final
name|String
name|group
parameter_list|,
specifier|final
name|Integer
name|mode
parameter_list|)
block|{
name|super
argument_list|(
name|listener
argument_list|,
name|collection
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|()
block|{
try|try
block|{
specifier|final
name|UserManagementService
name|service
decl_stmt|;
if|if
condition|(
name|getTarget
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
block|{
name|service
operator|=
operator|(
name|UserManagementService
operator|)
name|getTarget
argument_list|()
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Collection
name|parent
init|=
name|getTarget
argument_list|()
operator|.
name|getParentCollection
argument_list|()
decl_stmt|;
name|service
operator|=
operator|(
name|UserManagementService
operator|)
name|parent
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
name|service
operator|.
name|setPermissions
argument_list|(
name|getTarget
argument_list|()
argument_list|,
name|getOwner
argument_list|()
argument_list|,
name|getGroup
argument_list|()
argument_list|,
name|getMode
argument_list|()
argument_list|,
name|getAces
argument_list|()
argument_list|)
expr_stmt|;
comment|//persist
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
name|String
name|name
init|=
literal|"unknown"
decl_stmt|;
try|try
block|{
name|name
operator|=
name|getTarget
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|x
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|x
operator|.
name|getMessage
argument_list|()
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|msg
init|=
literal|"ERROR: Failed to set permissions on Collection '"
operator|+
name|name
operator|+
literal|"'."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|xe
argument_list|)
expr_stmt|;
name|getListener
argument_list|()
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

