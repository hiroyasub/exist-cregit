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
name|FileInputStream
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
name|io
operator|.
name|InputStream
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * Example code for demonstrating XMLRPC methods upload and parseLocal.  *  * Execute: bin\run.bat org.exist.examples.xmlrpc.StoreChunked  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|StoreChunked
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
name|args
index|[]
parameter_list|)
block|{
comment|// Upload file to this uri:
specifier|final
name|String
name|xmldbUri
init|=
literal|"xmldb:exist://guest:guest@localhost:8080/exist/xmlrpc/db/admin2.png"
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|String
name|path
init|=
name|uri
operator|.
name|getCollectionPath
argument_list|()
decl_stmt|;
comment|// TODO: Filename hardcoded
specifier|final
name|String
name|filename
init|=
literal|"webapp/resources/admin2.png"
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|filename
argument_list|)
init|)
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
name|setServerURL
argument_list|(
operator|new
name|URL
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicUserName
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicPassword
argument_list|(
literal|"guest"
argument_list|)
expr_stmt|;
name|client
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
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
argument_list|()
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
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|fis
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
name|len
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
comment|// All data transported, parse data on server
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
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"image/png"
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
comment|// exceptions
comment|// Check result
if|if
condition|(
name|result
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"document stored."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"could not store document."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XmlRpcException
decl||
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

