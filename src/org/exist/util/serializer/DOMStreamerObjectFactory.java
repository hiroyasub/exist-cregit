begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|BasePoolableObjectFactory
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DOMStreamerObjectFactory
extends|extends
name|BasePoolableObjectFactory
block|{
comment|/** 	 *  	 */
specifier|public
name|DOMStreamerObjectFactory
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject() 	 */
specifier|public
name|Object
name|makeObject
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|DOMStreamer
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.commons.pool.BasePoolableObjectFactory#activateObject(java.lang.Object) 	 */
specifier|public
name|void
name|activateObject
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|Exception
block|{
operator|(
operator|(
name|DOMStreamer
operator|)
name|obj
operator|)
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

