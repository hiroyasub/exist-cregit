begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_comment
comment|/**  * Extends the {@link org.xmldb.api.modules.CollectionManagementService}  * interface with extensions specific to eXist, in particular moving and copying  * collections and resources.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|CollectionManagementServiceImpl
extends|extends
name|CollectionManagementService
block|{
specifier|public
name|void
name|move
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|destination
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|moveResource
parameter_list|(
name|String
name|resourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

