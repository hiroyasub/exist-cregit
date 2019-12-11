begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: XmlrpcUpload.java 223 2007-04-21 22:13:05Z dizzzz $  */
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClientConfigImpl
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
name|util
operator|.
name|MimeTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|MimeType
import|;
end_import

begin_comment
comment|/**  * Write document using XMLRPC to remote database and read the data   * from an input stream.  *   * Sends a document to an eXist-db server using XMLRPC. The document can be  * either XML or non-XML (binary). Chunked means that the document is send   * as smaller parts to the server, the servler glues the parts together. There  * is no limitation on the size of the documents that can be transported.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|XmlrpcUpload
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XmlrpcUpload
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Write data from a (input)stream to the specified XMLRPC url and leave      * the input stream open.      *       * @param xmldbURL URL pointing to location on eXist-db server.      * @param is Document stream      * @throws IOException When something is wrong.      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Begin document upload"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Setup xmlrpc client
specifier|final
name|XmlRpcClient
name|client
init|=
operator|new
name|XmlRpcClient
argument_list|()
decl_stmt|;
specifier|final
name|XmlRpcClientConfigImpl
name|config
init|=
operator|new
name|XmlRpcClientConfigImpl
argument_list|()
decl_stmt|;
name|config
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setEnabledForExtensions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setServerURL
argument_list|(
operator|new
name|URL
argument_list|(
name|xmldbURL
operator|.
name|getXmlRpcURL
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|xmldbURL
operator|.
name|hasUserInfo
argument_list|()
condition|)
block|{
name|config
operator|.
name|setBasicUserName
argument_list|(
name|xmldbURL
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicPassword
argument_list|(
name|xmldbURL
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|String
name|contentType
init|=
name|MimeType
operator|.
name|BINARY_TYPE
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|xmldbURL
operator|.
name|getDocumentName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
name|contentType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
comment|// Initialize xmlrpc parameters
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|handle
init|=
literal|null
decl_stmt|;
comment|// Copy data from inputstream to database
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|handle
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|handle
operator|=
operator|(
name|String
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"upload"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
comment|// All data transported, now parse data on server
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|xmldbURL
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
specifier|final
name|Boolean
name|result
init|=
operator|(
name|Boolean
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"parseLocal"
argument_list|,
name|params
argument_list|)
decl_stmt|;
comment|// Check XMLRPC result
if|if
condition|(
name|result
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document stored."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not store document."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not store document."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finished document upload"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

