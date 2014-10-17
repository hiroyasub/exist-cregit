begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
comment|/**  * Applies Null Object Design Pattern  *  * @author Jean-Marc Vanel - http:///jmvanel.free.fr  */
end_comment

begin_class
specifier|public
class|class
name|NullNodeIndexListener
implements|implements
name|NodeIndexListener
block|{
comment|/**      * Singleton      */
specifier|public
specifier|static
specifier|final
name|NodeIndexListener
name|INSTANCE
init|=
operator|new
name|NullNodeIndexListener
argument_list|()
decl_stmt|;
comment|/**      * @see org.exist.dom.persistent.NodeIndexListener#nodeChanged(StoredNode)      */
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|nodeChanged
parameter_list|(
specifier|final
name|NodeHandle
name|node
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

