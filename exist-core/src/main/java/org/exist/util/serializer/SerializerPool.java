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
name|KeyedPoolableObjectFactory
import|;
end_import

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
name|impl
operator|.
name|StackKeyedObjectPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
operator|.
name|Serializer
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|SerializerPool
extends|extends
name|StackKeyedObjectPool
block|{
specifier|private
specifier|final
specifier|static
name|SerializerPool
name|instance
init|=
operator|new
name|SerializerPool
argument_list|(
operator|new
name|SerializerObjectFactory
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|SerializerPool
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
comment|/**      * @param factory the object factory      * @param max the maximum size of the pool      * @param init the initial size of the pool      */
specifier|public
name|SerializerPool
parameter_list|(
name|KeyedPoolableObjectFactory
name|factory
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|init
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|max
argument_list|,
name|init
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|Object
name|borrowObject
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
try|try
block|{
return|return
name|super
operator|.
name|borrowObject
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Error while creating serializer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|DOMStreamer
name|borrowDOMStreamer
parameter_list|(
name|Serializer
name|delegate
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ExtendedDOMStreamer
name|serializer
init|=
operator|(
name|ExtendedDOMStreamer
operator|)
name|borrowObject
argument_list|(
name|DOMStreamer
operator|.
name|class
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setSerializer
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
return|return
name|serializer
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|returnObject
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|super
operator|.
name|returnObject
argument_list|(
name|obj
operator|.
name|getClass
argument_list|()
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Error while returning serializer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

