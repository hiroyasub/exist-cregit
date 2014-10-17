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

begin_comment
comment|/**  * This interface is used to report changes of the node id or the storage address  * of a node to classes which have to keep node sets up to date during processing.  * Used by the XUpdate classes to update the query result sets.  *  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeIndexListener
block|{
comment|/**      * The internal id of a node has changed. The storage address is      * still the same, so one can find the changed node by comparing      * its storage address.      *      * @param node      */
name|void
name|nodeChanged
parameter_list|(
name|NodeHandle
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

