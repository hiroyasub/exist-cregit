begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_interface
specifier|public
interface|interface
name|ExtNodeSet
extends|extends
name|NodeSet
block|{
specifier|public
name|NodeProxy
name|hasDescendantsInSet
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|NodeId
name|ancestorId
parameter_list|,
name|boolean
name|includeSelf
parameter_list|,
name|int
name|contextId
parameter_list|,
name|boolean
name|copyMatches
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

