begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|io
operator|.
name|BlockingOutputStream
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
name|SequenceIterator
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
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|NodeInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BlockingInputStream
name|bis
decl_stmt|;
specifier|private
name|BlockingOutputStream
name|bos
decl_stmt|;
specifier|private
name|NodeSerializerThread
name|rt
decl_stmt|;
comment|/** Creates a new instance of NodeInputStream */
specifier|public
name|NodeInputStream
parameter_list|(
name|Serializer
name|serializer
parameter_list|,
name|SequenceIterator
name|siNode
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Initializing NodeInputStream"
argument_list|)
expr_stmt|;
name|bis
operator|=
operator|new
name|BlockingInputStream
argument_list|()
expr_stmt|;
name|bos
operator|=
name|bis
operator|.
name|getOutputStream
argument_list|()
expr_stmt|;
name|rt
operator|=
operator|new
name|NodeSerializerThread
argument_list|(
name|serializer
argument_list|,
name|siNode
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|rt
operator|.
name|start
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Initializing NodeInputStream done"
argument_list|)
expr_stmt|;
block|}
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

