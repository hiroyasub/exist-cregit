begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|aider
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

begin_comment
comment|/**  * Instantiates an appropriate Permission class based on the current configuration  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|PermissionAiderFactory
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
name|PermissionAiderFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|PermissionAider
name|getPermission
parameter_list|(
name|String
name|ownerName
parameter_list|,
name|String
name|groupName
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|PermissionAider
name|permission
init|=
literal|null
decl_stmt|;
try|try
block|{
name|permission
operator|=
operator|new
name|SimpleACLPermissionAider
argument_list|(
name|ownerName
argument_list|,
name|groupName
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while instantiating security permission class."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|permission
return|;
block|}
block|}
end_class

end_unit

