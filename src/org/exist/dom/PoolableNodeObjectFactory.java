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
name|dom
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
name|BaseKeyedPoolableObjectFactory
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|PoolableNodeObjectFactory
extends|extends
name|BaseKeyedPoolableObjectFactory
block|{
comment|/** 	 *  	 */
specifier|public
name|PoolableNodeObjectFactory
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.commons.pool.KeyedPoolableObjectFactory#makeObject(java.lang.Object) 	 */
specifier|public
name|Object
name|makeObject
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|key
operator|==
name|ElementImpl
operator|.
name|class
condition|)
return|return
operator|new
name|ElementImpl
argument_list|()
return|;
if|else if
condition|(
name|key
operator|==
name|TextImpl
operator|.
name|class
condition|)
return|return
operator|new
name|TextImpl
argument_list|()
return|;
if|else if
condition|(
name|key
operator|==
name|AttrImpl
operator|.
name|class
condition|)
return|return
operator|new
name|AttrImpl
argument_list|()
return|;
if|else if
condition|(
name|key
operator|==
name|ProcessingInstructionImpl
operator|.
name|class
condition|)
return|return
operator|new
name|ProcessingInstructionImpl
argument_list|()
return|;
if|else if
condition|(
name|key
operator|==
name|CommentImpl
operator|.
name|class
condition|)
return|return
operator|new
name|CommentImpl
argument_list|()
return|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to create object of type "
operator|+
name|key
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

