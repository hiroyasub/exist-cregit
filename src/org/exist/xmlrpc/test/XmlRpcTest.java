begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|test
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|WebServer
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

begin_comment
comment|/**  * JUnit test for XMLRPC interface methods.  *   * This test assumes that the XMLRPC server is running at port 8081  * of the local host. The server is normally started from Ant before calling  * the test.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XmlRpcTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"http://localhost:8081"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA
init|=
literal|"<test>"
operator|+
literal|"<para>Ã¤Ã¤Ã¶Ã¶Ã¼Ã¼ÃÃÃÃÃÃÃÃ</para>"
operator|+
literal|"<para>\uC5F4\uB2E8\uACC4</para>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TARGET_COLLECTION
init|=
literal|"/db/xmlrpc/"
decl_stmt|;
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|XmlRpcTest
argument_list|(
literal|"XmlRpcTest"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|WebServer
name|webServer
init|=
literal|null
decl_stmt|;
specifier|public
name|XmlRpcTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStore
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating collection "
operator|+
name|TARGET_COLLECTION
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
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
name|TARGET_COLLECTION
argument_list|)
expr_stmt|;
name|Boolean
name|result
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing document "
operator|+
name|XML_DATA
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|TARGET_COLLECTION
operator|+
literal|"test.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Document stored."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRetrieveDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Retrieving document "
operator|+
name|TARGET_COLLECTION
operator|+
literal|"test.xml"
argument_list|)
expr_stmt|;
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
name|TARGET_COLLECTION
operator|+
literal|"test.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|options
argument_list|)
expr_stmt|;
comment|// execute the call
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
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
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"getDocument"
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
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCharEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing charsets returned by query"
argument_list|)
expr_stmt|;
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|String
name|query
init|=
literal|"distinct-values(//para)"
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|query
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Hashtable
argument_list|()
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
name|Hashtable
name|result
init|=
operator|(
name|Hashtable
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"queryP"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Vector
name|resources
init|=
operator|(
name|Vector
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|resources
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|String
name|value
init|=
operator|(
name|String
operator|)
name|resources
operator|.
name|elementAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
literal|"Ã¤Ã¤Ã¶Ã¶Ã¼Ã¼ÃÃÃÃÃÃÃÃ"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Result1: "
operator|+
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|resources
operator|.
name|elementAt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
literal|"\uC5F4\uB2E8\uACC4"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Result2: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|String
name|query
init|=
literal|"(::pragma exist:serialize indent=no::) //para"
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|query
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Hashtable
argument_list|()
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
name|byte
index|[]
name|result
init|=
operator|(
name|byte
index|[]
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"query"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|result
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExecuteQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|String
name|query
init|=
literal|"distinct-values(//para)"
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|query
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Hashtable
argument_list|()
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Executing query: "
operator|+
name|query
argument_list|)
expr_stmt|;
name|Integer
name|handle
init|=
operator|(
name|Integer
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"executeQuery"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|handle
argument_list|)
expr_stmt|;
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
name|Integer
name|hits
init|=
operator|(
name|Integer
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"getHits"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|hits
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found: "
operator|+
name|hits
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hits
operator|.
name|intValue
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Hashtable
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|item
init|=
operator|(
name|byte
index|[]
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"retrieve"
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
operator|new
name|String
argument_list|(
name|item
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|XmlRpcClient
name|getClient
parameter_list|()
throws|throws
name|MalformedURLException
block|{
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
name|URI
argument_list|)
decl_stmt|;
name|xmlrpc
operator|.
name|setBasicAuthentication
argument_list|(
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|xmlrpc
return|;
block|}
block|}
end_class

end_unit

