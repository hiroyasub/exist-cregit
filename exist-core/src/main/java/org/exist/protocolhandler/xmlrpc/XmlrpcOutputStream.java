begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: XmlrpcOutputStream.java 223 2007-04-21 22:13:05Z dizzzz $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmlrpc
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
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

begin_comment
comment|/**  * Write document to remote database (using xmlrpc) using output stream.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|XmlrpcOutputStream
extends|extends
name|OutputStream
block|{
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|uploadThreadId
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|BlockingOutputStream
name|bos
decl_stmt|;
comment|/**      * Constructor of XmlrpcOutputStream.      *      * @param threadGroup the group for the threads created by this stream.      * @param url         Location of document in database.      */
specifier|public
name|XmlrpcOutputStream
parameter_list|(
specifier|final
name|ThreadGroup
name|threadGroup
parameter_list|,
specifier|final
name|XmldbURL
name|url
parameter_list|)
block|{
specifier|final
name|BlockingInputStream
name|bis
init|=
operator|new
name|BlockingInputStream
argument_list|()
decl_stmt|;
name|this
operator|.
name|bos
operator|=
name|bis
operator|.
name|getOutputStream
argument_list|()
expr_stmt|;
specifier|final
name|Runnable
name|runnable
init|=
operator|new
name|XmlrpcUploadRunnable
argument_list|(
name|url
argument_list|,
name|bis
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|threadGroup
argument_list|,
name|runnable
argument_list|,
name|threadGroup
operator|.
name|getName
argument_list|()
operator|+
literal|".xmlrpc.upload-"
operator|+
name|uploadThreadId
operator|.
name|getAndIncrement
argument_list|()
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
name|void
name|write
parameter_list|(
specifier|final
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|bos
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|bos
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|bos
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
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
name|bos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|bos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

