begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: XmlrpcDownloadThread.java 223 2007-04-21 22:13:05Z dizzzz $  */
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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|BlockingOutputStream
import|;
end_import

begin_comment
comment|/**  *   Wrap XmlrpcDownload class into a thread for XmlrpcInputStream.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|XmlrpcDownloadThread
extends|extends
name|Thread
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XmlrpcDownloadThread
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XmldbURL
name|xmldbURL
decl_stmt|;
specifier|private
name|BlockingOutputStream
name|bos
decl_stmt|;
comment|/**      *  Constructor of XmlrpcDownloadThread.      *       * @param url Document location in database.      * @param bos Stream to which the document is written.      */
specifier|public
name|XmlrpcDownloadThread
parameter_list|(
name|XmldbURL
name|url
parameter_list|,
name|BlockingOutputStream
name|bos
parameter_list|)
block|{
name|xmldbURL
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|bos
operator|=
name|bos
expr_stmt|;
block|}
comment|/**      * Write resource to the output stream.      */
specifier|public
name|void
name|run
parameter_list|()
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Thread started."
argument_list|)
expr_stmt|;
name|IOException
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|XmlrpcDownload
name|xuc
init|=
operator|new
name|XmlrpcDownload
argument_list|()
decl_stmt|;
name|xuc
operator|.
name|stream
argument_list|(
name|xmldbURL
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|exception
operator|=
name|ex
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
comment|// NEEDED!
name|bos
operator|.
name|close
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Thread stopped."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

