begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|examples
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
name|FileOutputStream
import|;
end_import

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
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|XmlRpc
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
name|XmlRpcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  *  Example code for demonstrating XMLRPC methods getDocumentData  * and getNextChunk. Please run 'admin-examples setup' first, this will  * download the required mondial.xml document.  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|RetrieveChunked
block|{
comment|/**      * @param args ignored command line arguments      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|// Download file (ohoh not in spec) using xmldb url
name|String
name|xmldbUri
init|=
literal|"xmldb:exist://guest:guest@localhost:8080/exist/xmlrpc/db/mondial/mondial.xml"
decl_stmt|;
name|XmldbURI
name|uri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|xmldbUri
argument_list|)
decl_stmt|;
comment|// Construct url for xmlrpc, without collections / document
comment|// username/password yet hardcoded, need to update XmldbUri fir this
name|String
name|url
init|=
literal|"http://guest:guest@"
operator|+
name|uri
operator|.
name|getAuthority
argument_list|()
operator|+
name|uri
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|uri
operator|.
name|getCollectionPath
argument_list|()
decl_stmt|;
comment|// Hardcoded yet too
name|String
name|filename
init|=
literal|"mondial.xml"
decl_stmt|;
try|try
block|{
comment|// Setup xmlrpc client
name|XmlRpc
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
operator|new
name|XmlRpcClient
argument_list|(
name|url
argument_list|)
decl_stmt|;
comment|// Setup xml serializer
name|Hashtable
name|options
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"encoding"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|// Setup xmlrpc parameters
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|options
argument_list|)
expr_stmt|;
comment|// Setup output stream
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
comment|// Shoot first method write data
name|Hashtable
name|ht
init|=
operator|(
name|Hashtable
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"getDocumentData"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
operator|(
operator|(
name|Integer
operator|)
name|ht
operator|.
name|get
argument_list|(
literal|"offset"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|ht
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
name|String
name|handle
init|=
operator|(
name|String
operator|)
name|ht
operator|.
name|get
argument_list|(
literal|"handle"
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// When there is more data to download
while|while
condition|(
name|offset
operator|!=
literal|0
condition|)
block|{
comment|// Clean and re-setup xmlrpc parameters
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|offset
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get and write next chunk
name|ht
operator|=
operator|(
name|Hashtable
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"getNextChunk"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|data
operator|=
operator|(
name|byte
index|[]
operator|)
name|ht
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
expr_stmt|;
name|offset
operator|=
operator|(
operator|(
name|Integer
operator|)
name|ht
operator|.
name|get
argument_list|(
literal|"offset"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
comment|// Finish transport
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

