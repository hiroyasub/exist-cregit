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
name|exist
operator|.
name|security
operator|.
name|ACLPermission
operator|.
name|ACE_ACCESS_TYPE
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
name|ACLPermission
operator|.
name|ACE_TARGET
import|;
end_import

begin_comment
comment|/**  * Represents the permissions for a skipped entry in the restore process, e.g. apply() does nothing  *  * @author  Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|SkippedEntryDeferredPermission
implements|implements
name|DeferredPermission
block|{
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|addACE
parameter_list|(
specifier|final
name|int
name|index
parameter_list|,
specifier|final
name|ACE_TARGET
name|target
parameter_list|,
specifier|final
name|String
name|who
parameter_list|,
specifier|final
name|ACE_ACCESS_TYPE
name|access_type
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

