begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
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
name|Permission
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

begin_comment
comment|/**  * Interface of common accessors for any generic database resource  *  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  * @author Adam Retter  */
end_comment

begin_interface
specifier|public
interface|interface
name|Resource
block|{
comment|/**      * Get the URI of the resource within the database      *      * @return The URI of the resource in the database      */
name|XmldbURI
name|getURI
parameter_list|()
function_decl|;
comment|/**      * Get the Permissions of the resource      *      * @return The current permissions assigned to the      * resource      */
name|Permission
name|getPermissions
parameter_list|()
function_decl|;
comment|/**      * Get the metadata associated      * with the resource      *      * @return The metadata associated with the resource      */
name|ResourceMetadata
name|getMetadata
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

