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
name|PoolableObjectFactory
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
name|StackObjectPool
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DOMStreamerPool
extends|extends
name|StackObjectPool
block|{
specifier|private
specifier|final
specifier|static
name|DOMStreamerPool
name|instance
init|=
operator|new
name|DOMStreamerPool
argument_list|(
operator|new
name|DOMStreamerObjectFactory
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|DOMStreamerPool
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
specifier|protected
name|DOMStreamerPool
parameter_list|(
name|PoolableObjectFactory
name|factory
parameter_list|,
name|int
name|maxIdle
parameter_list|,
name|int
name|initIdleCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|maxIdle
argument_list|,
name|initIdleCapacity
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DOMStreamer
name|borrowDOMStreamer
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|DOMStreamer
operator|)
name|borrowObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
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
name|void
name|returnDOMStreamer
parameter_list|(
name|DOMStreamer
name|streamer
parameter_list|)
block|{
if|if
condition|(
name|streamer
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|returnObject
argument_list|(
name|streamer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
block|}
end_class

end_unit

