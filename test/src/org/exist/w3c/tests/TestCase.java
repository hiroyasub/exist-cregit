begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|w3c
operator|.
name|tests
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|FileReader
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|Assert
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
name|Diff
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|SAXAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
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
name|BrokerPool
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
name|LocalCollection
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
name|LocalXMLResource
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
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
name|AtomicValue
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
name|Sequence
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TestCase
block|{
specifier|public
specifier|static
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
name|database
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|int
name|inUse
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
name|Collection
name|testCollection
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Thread
name|shutdowner
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|testLocation
init|=
literal|"test/external/"
decl_stmt|;
specifier|static
class|class
name|Shutdowner
implements|implements
name|Runnable
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|inUse
operator|==
literal|0
condition|)
block|{
name|database
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"database was shutdown"
argument_list|)
expr_stmt|;
name|database
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|database
operator|==
literal|null
condition|)
block|{
name|database
operator|=
operator|new
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
argument_list|(
literal|"jetty"
argument_list|)
expr_stmt|;
name|database
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
comment|//				testCollection = DatabaseManager.getCollection("xmldb:exist:///db/XQTS", "admin", "");
if|if
condition|(
name|shutdowner
operator|==
literal|null
condition|)
block|{
name|shutdowner
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Shutdowner
argument_list|()
argument_list|)
expr_stmt|;
name|shutdowner
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
name|inUse
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//		System.out.println("setUpBeforeClass PASSED");
block|}
specifier|public
specifier|abstract
name|void
name|loadTS
parameter_list|()
throws|throws
name|Exception
function_decl|;
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|inUse
operator|--
expr_stmt|;
comment|//		System.out.println("tearDownAfterClass PASSED");
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|database
init|)
block|{
if|if
condition|(
name|testCollection
operator|==
literal|null
condition|)
block|{
name|loadTS
argument_list|()
expr_stmt|;
name|testCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/XQTS"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCollection
operator|==
literal|null
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"There is no Test Suite data at database"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.println("tearDown PASSED");
block|}
specifier|public
name|boolean
name|compareResult
parameter_list|(
name|String
name|testCase
parameter_list|,
name|String
name|folder
parameter_list|,
name|Element
name|outputFile
parameter_list|,
name|Sequence
name|result
parameter_list|)
block|{
if|if
condition|(
name|outputFile
operator|==
literal|null
condition|)
name|Assert
operator|.
name|fail
argument_list|(
literal|"no expected result information"
argument_list|)
expr_stmt|;
name|File
name|expectedResult
init|=
operator|new
name|File
argument_list|(
name|testLocation
operator|+
name|folder
argument_list|,
name|outputFile
operator|.
name|getNodeValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|expectedResult
operator|.
name|canRead
argument_list|()
condition|)
name|Assert
operator|.
name|fail
argument_list|(
literal|"can't read expected result"
argument_list|)
expr_stmt|;
name|String
name|compare
init|=
name|outputFile
operator|.
name|getAttribute
argument_list|(
literal|"compare"
argument_list|)
decl_stmt|;
try|try
block|{
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|expectedResult
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Resource
name|xmldbResource
init|=
name|getResource
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|res
init|=
name|xmldbResource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|l
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getItemCount
argument_list|()
operator|==
literal|1
condition|)
name|l
operator|=
operator|(
name|int
operator|)
name|expectedResult
operator|.
name|length
argument_list|()
expr_stmt|;
else|else
name|l
operator|=
name|res
operator|.
name|length
argument_list|()
expr_stmt|;
name|l
operator|+=
name|fixResultLength
argument_list|(
name|testCase
argument_list|)
expr_stmt|;
name|int
name|skipped
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|l
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|l
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|reader
operator|.
name|ready
argument_list|()
condition|)
block|{
name|skipped
operator|+=
name|l
operator|-
name|x
expr_stmt|;
break|break;
block|}
name|chars
index|[
name|x
index|]
operator|=
operator|(
name|char
operator|)
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|chars
index|[
name|x
index|]
operator|==
literal|'\r'
condition|)
block|{
name|chars
index|[
name|x
index|]
operator|=
operator|(
name|char
operator|)
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|getItemCount
argument_list|()
operator|==
literal|1
operator|&&
name|skipped
operator|!=
literal|0
condition|)
block|{
name|char
index|[]
name|oldChars
init|=
name|chars
decl_stmt|;
name|chars
operator|=
operator|new
name|char
index|[
name|l
operator|-
name|skipped
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|oldChars
argument_list|,
literal|0
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|l
operator|-
name|skipped
argument_list|)
expr_stmt|;
block|}
comment|//			System.out.println(res);
comment|//			System.out.println(String.copyValueOf(chars));
name|String
name|expResult
init|=
name|String
operator|.
name|copyValueOf
argument_list|(
name|chars
argument_list|)
decl_stmt|;
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|compare
operator|.
name|equals
argument_list|(
literal|"XML"
argument_list|)
condition|)
block|{
try|try
block|{
name|ok
operator|=
name|diffXML
argument_list|(
name|expResult
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
operator|!
name|ok
condition|)
block|{
if|if
condition|(
operator|!
name|expResult
operator|.
name|equals
argument_list|(
name|res
argument_list|)
condition|)
if|if
condition|(
name|compare
operator|.
name|equals
argument_list|(
literal|"Fragment"
argument_list|)
operator|||
name|compare
operator|.
name|equals
argument_list|(
literal|"Inspect"
argument_list|)
condition|)
block|{
try|try
block|{
name|ok
operator|=
name|diffXML
argument_list|(
name|expResult
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
if|if
condition|(
operator|!
name|ok
condition|)
block|{
comment|//workaround problematic results
if|if
condition|(
name|expResult
operator|.
name|equals
argument_list|(
literal|"<?pi ?>"
argument_list|)
operator|&&
operator|(
name|res
operator|.
name|equals
argument_list|(
literal|"<?pi?>"
argument_list|)
operator|)
condition|)
empty_stmt|;
else|else
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|//workaround problematic results
if|if
condition|(
name|expResult
operator|.
name|equals
argument_list|(
literal|"&amp;"
argument_list|)
operator|&&
name|res
operator|.
name|equals
argument_list|(
literal|"&"
argument_list|)
condition|)
empty_stmt|;
if|else if
condition|(
name|expResult
operator|.
name|equals
argument_list|(
literal|"&lt;"
argument_list|)
operator|&&
name|res
operator|.
name|equals
argument_list|(
literal|"<"
argument_list|)
condition|)
empty_stmt|;
else|else
block|{
comment|//last try
name|expResult
operator|=
name|expResult
operator|.
name|replaceAll
argument_list|(
literal|"&lt;"
argument_list|,
literal|"<"
argument_list|)
expr_stmt|;
name|expResult
operator|=
name|expResult
operator|.
name|replaceAll
argument_list|(
literal|"&gt;"
argument_list|,
literal|">"
argument_list|)
expr_stmt|;
name|expResult
operator|=
name|expResult
operator|.
name|replaceAll
argument_list|(
literal|"&amp;"
argument_list|,
literal|"&"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expResult
operator|.
name|equals
argument_list|(
name|res
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|(
name|compare
operator|.
name|equals
argument_list|(
literal|"Text"
argument_list|)
operator|||
name|compare
operator|.
name|equals
argument_list|(
literal|"Fragment"
argument_list|)
operator|)
operator|&&
operator|(
name|i
operator|.
name|hasNext
argument_list|()
operator|)
condition|)
block|{
name|reader
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
literal|' '
operator|!=
operator|(
name|char
operator|)
name|reader
operator|.
name|read
argument_list|()
condition|)
comment|//							if (compare.equals("Fragment"))
name|reader
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//							else
comment|//								return false;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|int
name|fixResultLength
parameter_list|(
name|String
name|testCase
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
specifier|private
name|boolean
name|diffXML
parameter_list|(
name|String
name|expResult
parameter_list|,
name|String
name|res
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|res
operator|=
name|res
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|res
operator|=
name|res
operator|.
name|replaceAll
argument_list|(
literal|"\t"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|expResult
operator|=
name|expResult
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|expResult
operator|=
name|expResult
operator|.
name|replaceAll
argument_list|(
literal|"\t"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
name|expResult
operator|.
name|trim
argument_list|()
argument_list|,
name|res
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|identical
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"expected:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|expResult
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"get:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|NORMALIZE_HTML
init|=
literal|"normalize-html"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|NORMALIZE_HTML
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Resource
name|getResource
parameter_list|(
name|Object
name|r
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LocalCollection
name|collection
init|=
literal|null
decl_stmt|;
name|User
name|user
init|=
literal|null
decl_stmt|;
name|LocalXMLResource
name|res
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|r
operator|instanceof
name|NodeProxy
condition|)
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|r
decl_stmt|;
name|res
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|collection
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|Node
condition|)
block|{
name|res
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|collection
argument_list|,
name|XmldbURI
operator|.
name|EMPTY_URI
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContentAsDOM
argument_list|(
operator|(
name|Node
operator|)
name|r
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|AtomicValue
condition|)
block|{
name|res
operator|=
operator|new
name|LocalXMLResource
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|collection
argument_list|,
name|XmldbURI
operator|.
name|EMPTY_URI
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|Resource
condition|)
return|return
operator|(
name|Resource
operator|)
name|r
return|;
try|try
block|{
name|Field
name|field
init|=
name|res
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"outputProperties"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
name|res
argument_list|,
operator|new
name|Properties
argument_list|(
name|defaultProperties
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
return|return
name|res
return|;
block|}
specifier|public
name|NodeImpl
name|loadVarFromURI
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|XMLReader
name|xr
decl_stmt|;
try|try
block|{
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|xr
operator|=
name|parser
operator|.
name|getXMLReader
argument_list|()
expr_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
comment|//			URL url = new URL(uri);
comment|//			InputStreamReader isr = new InputStreamReader(url.openStream(), "UTF-8");
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|uri
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|isr
argument_list|)
decl_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|isr
operator|.
name|close
argument_list|()
expr_stmt|;
name|adapter
operator|.
name|getDocument
argument_list|()
operator|.
name|setDocumentURI
argument_list|(
operator|new
name|File
argument_list|(
name|uri
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|NodeImpl
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
comment|//workaround BOM
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Content is not allowed in prolog."
argument_list|)
condition|)
block|{
try|try
block|{
name|String
name|xml
init|=
name|readFileAsString
argument_list|(
operator|new
name|File
argument_list|(
name|uri
argument_list|)
argument_list|)
decl_stmt|;
name|xml
operator|=
name|xml
operator|.
name|trim
argument_list|()
operator|.
name|replaceFirst
argument_list|(
literal|"^([\\W]+)<"
argument_list|,
literal|"<"
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|adapter
operator|.
name|getDocument
argument_list|()
operator|.
name|setDocumentURI
argument_list|(
operator|new
name|File
argument_list|(
name|uri
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|NodeImpl
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|String
name|readFileAsString
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|file
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|FileInputStream
name|f
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|f
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
return|;
block|}
specifier|public
name|String
name|sequenceToString
parameter_list|(
name|Sequence
name|seq
parameter_list|)
block|{
name|String
name|res
init|=
literal|""
decl_stmt|;
try|try
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Resource
name|resource
init|=
name|getResource
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|+=
name|resource
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|res
operator|+=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

