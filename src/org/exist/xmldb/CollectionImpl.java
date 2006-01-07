begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  *     * Copyright (C) 2001-04 Wolfgang M. Meier wolfgang@exist-db.org  *   * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *   * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Date
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
name|Resource
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
comment|/**  * This interface extends org.xmldb.api.base.Collection with extensions specific to eXist.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CollectionImpl
extends|extends
name|Collection
block|{
specifier|public
name|boolean
name|isRemoteCollection
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Returns the time of creation of the collection. 	 * @return 	 */
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/* Alternative methods, especially to be used from jsp */
specifier|public
name|String
index|[]
name|getChildCollections
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|String
index|[]
name|getResources
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|void
name|storeResource
parameter_list|(
name|Resource
name|res
parameter_list|,
name|Date
name|a
parameter_list|,
name|Date
name|b
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
specifier|public
name|XmldbURI
name|getURI
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

