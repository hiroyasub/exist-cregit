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
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
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
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XmlrpcOutputStream
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
name|XmlrpcUploadThread
name|rt
decl_stmt|;
comment|/**      *  Constructor of XmlrpcOutputStream.       *       * @param xmldbURL Location of document in database.      * @throws MalformedURLException Thrown for illegalillegal URLs.      */
specifier|public
name|XmlrpcOutputStream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Initializing XmlrpcOutputStream"
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
name|XmlrpcUploadThread
argument_list|(
name|xmldbURL
argument_list|,
name|bis
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
literal|"Initializing XmlrpcOutputStream done"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
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
specifier|public
name|void
name|write
parameter_list|(
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
specifier|public
name|void
name|write
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

