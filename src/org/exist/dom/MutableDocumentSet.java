begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Manages a set of documents.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|MutableDocumentSet
extends|extends
name|DocumentSet
block|{
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
function_decl|;
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|checkDuplicates
parameter_list|)
function_decl|;
name|void
name|addAll
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
function_decl|;
name|void
name|addCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

