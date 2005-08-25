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
name|BindException
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
name|Iterator
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|StandaloneServer
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
name|EXistOutputKeys
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
name|test
operator|.
name|DOMTestJUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|MultiException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * JUnit test for XMLRPC interface methods.  * @author wolf  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|XmlRpcTest
extends|extends
name|XMLTestCase
block|{
specifier|private
specifier|static
name|StandaloneServer
name|server
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"http://localhost:8088/xmlrpc"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA
init|=
literal|"<test>"
operator|+
literal|"<para>\u00E4\u00E4\u00F6\u00F6\u00FC\u00FC\u00C4\u00C4\u00D6\u00D6\u00DC\u00DC\u00DF\u00DF</para>"
operator|+
literal|"<para>\uC5F4\uB2E8\uACC4</para>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XSL_DATA
init|=
literal|"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
operator|+
literal|"version=\"1.0\">"
operator|+
literal|"<xsl:param name=\"testparam\"/>"
operator|+
literal|"<xsl:template match=\"test\"><test><xsl:apply-templates/></test></xsl:template>"
operator|+
literal|"<xsl:template match=\"para\">"
operator|+
literal|"<p><xsl:value-of select=\"$testparam\"/>:<xsl:apply-templates/></p></xsl:template>"
operator|+
literal|"</xsl:stylesheet>"
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
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Don't worry about closing the server : the shutdown hook will do the job
name|initServer
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initServer
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|new
name|StandaloneServer
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{}
decl_stmt|;
name|server
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MultiException
name|e
parameter_list|)
block|{
name|boolean
name|rethrow
init|=
literal|true
decl_stmt|;
name|Iterator
name|i
init|=
name|e
operator|.
name|getExceptions
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Exception
name|e0
init|=
operator|(
name|Exception
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e0
operator|instanceof
name|BindException
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"A server is running already !"
argument_list|)
expr_stmt|;
name|rethrow
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|rethrow
condition|)
throw|throw
name|e
throw|;
block|}
block|}
block|}
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
name|params
operator|.
name|setElementAt
argument_list|(
name|XSL_DATA
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|params
operator|.
name|setElementAt
argument_list|(
name|TARGET_COLLECTION
operator|+
literal|"test.xsl"
argument_list|,
literal|1
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
literal|"Documents stored."
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Retrieving document with stylesheet applied"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"stylesheet"
argument_list|,
literal|"test.xsl"
argument_list|)
expr_stmt|;
name|data
operator|=
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
comment|//TODO : check the number of resources before !
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
literal|"\u00E4\u00E4\u00F6\u00F6\u00FC\u00FC\u00C4\u00C4\u00D6\u00D6\u00DC\u00DC\u00DF\u00DF"
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
name|testQueryWithStylesheet
parameter_list|()
throws|throws
name|Exception
block|{
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
name|EXistOutputKeys
operator|.
name|STYLESHEET
argument_list|,
literal|"test.xsl"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|EXistOutputKeys
operator|.
name|STYLESHEET_PARAM
operator|+
literal|".testparam"
argument_list|,
literal|"Test"
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
comment|//TODO : check the number of resources before !
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
literal|"//para[1]"
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
name|options
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
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
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|options
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
name|assertNotNull
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|item
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|out
init|=
operator|new
name|String
argument_list|(
name|item
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received: "
operator|+
name|out
argument_list|)
expr_stmt|;
name|assertXMLEqual
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<p>Test: \u00E4\u00E4\u00F6\u00F6\u00FC\u00FC\u00C4\u00C4\u00D6\u00D6\u00DC\u00DC\u00DF\u00DF</p>"
argument_list|,
name|out
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
literal|0
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
name|item
operator|=
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
name|item
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCollectionWithAccents
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
literal|"Creating collection with accents in name ..."
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
literal|"/db/Citt\u00E0"
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
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
literal|"/db/Citt\u00E0/test.xml"
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
literal|"parse"
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
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db"
argument_list|)
expr_stmt|;
name|Hashtable
name|collection
init|=
operator|(
name|Hashtable
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"describeCollection"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Vector
name|collections
init|=
operator|(
name|Vector
operator|)
name|collection
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
decl_stmt|;
name|String
name|colWithAccent
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collections
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|childName
init|=
operator|(
name|String
operator|)
name|collections
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|childName
operator|.
name|equals
argument_list|(
literal|"Citt\u00E0"
argument_list|)
condition|)
name|colWithAccent
operator|=
name|childName
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Child collection: "
operator|+
name|childName
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"added collection not found"
argument_list|,
name|colWithAccent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Retrieving document /db/Citt\u00E0/test.xml"
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
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/"
operator|+
name|colWithAccent
operator|+
literal|"/test.xml"
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
name|TestRunner
operator|.
name|run
argument_list|(
name|XmlRpcTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//Explicit shutdown for the shutdown hook
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

