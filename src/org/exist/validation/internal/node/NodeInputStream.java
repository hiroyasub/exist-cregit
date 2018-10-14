begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
operator|.
name|node
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
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
name|io
operator|.
name|BlockingInputStream
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|NodeValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ThreadUtils
operator|.
name|newInstanceThread
import|;
end_import

begin_comment
comment|/**  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|NodeInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
name|BlockingInputStream
name|bis
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|nodeSerializerThreadId
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/**      * Creates a new instance of NodeInputStream      */
specifier|public
name|NodeInputStream
parameter_list|(
specifier|final
name|Database
name|database
parameter_list|,
specifier|final
name|Serializer
name|serializer
parameter_list|,
specifier|final
name|NodeValue
name|node
parameter_list|)
block|{
name|this
operator|.
name|bis
operator|=
operator|new
name|BlockingInputStream
argument_list|()
expr_stmt|;
specifier|final
name|Thread
name|thread
init|=
name|newInstanceThread
argument_list|(
name|database
argument_list|,
literal|"node-input-stream-serializer-"
operator|+
name|nodeSerializerThreadId
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
operator|new
name|NodeSerializerRunnable
argument_list|(
name|serializer
argument_list|,
name|node
argument_list|,
name|bis
operator|.
name|getOutputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|bis
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|bis
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|bis
operator|.
name|skip
argument_list|(
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|bis
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|bis
operator|.
name|read
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|bis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|bis
operator|.
name|available
argument_list|()
return|;
block|}
block|}
end_class

end_unit

