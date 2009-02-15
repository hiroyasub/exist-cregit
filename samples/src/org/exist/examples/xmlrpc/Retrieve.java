begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  *  Retrieve a document from the database using XMLRPC.  *  * Execute bin\run.bat org.exist.examples.xmlrpc.Retrieve<remotedoc>  *  *  @author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *  created    August 1, 2002  */
end_comment

begin_class
specifier|public
class|class
name|Retrieve
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|uri
init|=
literal|"http://localhost:8080/exist/xmlrpc"
decl_stmt|;
specifier|protected
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"usage: org.exist.examples.xmlrpc.Retrieve path-to-document"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
block|}
name|XmlRpcClient
name|client
init|=
operator|new
name|XmlRpcClient
argument_list|()
decl_stmt|;
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
name|uri
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
name|HashMap
name|options
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
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
name|options
operator|.
name|put
argument_list|(
literal|"expand-xincludes"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"process-xsl-pi"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
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
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
operator|(
name|String
operator|)
name|client
operator|.
name|execute
argument_list|(
literal|"getDocumentAsString"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|xml
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

