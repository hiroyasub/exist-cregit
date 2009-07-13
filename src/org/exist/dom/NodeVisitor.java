begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_comment
comment|/**  * Visitor pattern: used for traversing the DOM tree  * implement to get notified of the current node.  *   * @author wolf  * @deprecated replaced by XMLStreamListener  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeVisitor
block|{
annotation|@
name|Deprecated
specifier|public
name|boolean
name|visit
parameter_list|(
name|StoredNode
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

