begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|xquery
package|;
end_package

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
name|source
operator|.
name|FileSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
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
name|XMLFilenameFilter
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
name|DatabaseInstanceManager
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
name|XQueryService
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
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
name|Document
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|Database
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
name|ResourceSet
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|util
operator|.
name|Optional
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
name|ParserConfigurationException
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|TestRunner
block|{
specifier|private
name|Collection
name|rootCollection
decl_stmt|;
specifier|protected
specifier|abstract
name|String
name|getDirectory
parameter_list|()
function_decl|;
annotation|@
name|Test
specifier|public
name|void
name|runXMLBasedTests
parameter_list|()
throws|throws
name|TransformerException
throws|,
name|XMLDBException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
specifier|final
name|XMLFilenameFilter
name|filter
init|=
operator|new
name|XMLFilenameFilter
argument_list|()
decl_stmt|;
specifier|final
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|File
index|[]
name|files
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|files
operator|=
name|dir
operator|.
name|listFiles
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|filter
operator|.
name|accept
argument_list|(
name|dir
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|files
operator|=
operator|new
name|File
index|[]
block|{
name|dir
block|}
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|XQueryService
name|xqs
init|=
operator|(
name|XQueryService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|query
init|=
operator|new
name|FileSource
argument_list|(
operator|new
name|File
argument_list|(
literal|"test/src/xquery/runTests.xql"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|File
name|file
range|:
name|files
control|)
block|{
try|try
block|{
specifier|final
name|Document
name|doc
init|=
name|parse
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|xqs
operator|.
name|declareVariable
argument_list|(
literal|"doc"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|xqs
operator|.
name|declareVariable
argument_list|(
literal|"id"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|execute
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|tsResults
init|=
name|parseXmlResults
argument_list|(
operator|(
name|Element
operator|)
name|resource
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
decl_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|tsResults
argument_list|)
expr_stmt|;
name|tsResults
operator|.
name|forEach
argument_list|(
name|this
operator|::
name|printResults
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|t
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" while running: "
operator|+
name|file
argument_list|)
expr_stmt|;
throw|throw
name|t
throw|;
block|}
block|}
block|}
name|assertSuccess
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|runXQueryBasedTests
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|File
index|[]
name|suites
init|=
name|dir
operator|.
name|listFiles
argument_list|(
name|file
lambda|->
operator|(
name|file
operator|.
name|canRead
argument_list|()
operator|&&
name|file
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"suite"
argument_list|)
operator|&&
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xql"
argument_list|)
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|suites
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|File
name|suite
range|:
name|suites
control|)
block|{
specifier|final
name|XQueryService
name|xqs
init|=
operator|(
name|XQueryService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|xqs
operator|.
name|setModuleLoadPath
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Source
name|query
init|=
operator|new
name|FileSource
argument_list|(
name|suite
argument_list|,
literal|"UTF-8"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|execute
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|tsResults
init|=
name|parseXQueryResults
argument_list|(
operator|(
name|Element
operator|)
name|resource
operator|.
name|getContentAsDOM
argument_list|()
argument_list|)
decl_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|tsResults
argument_list|)
expr_stmt|;
name|tsResults
operator|.
name|forEach
argument_list|(
name|this
operator|::
name|printResults
argument_list|)
expr_stmt|;
block|}
block|}
name|assertSuccess
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
comment|/**      * Uses JUnits assertArrayEquals to report test failures and errors      */
specifier|private
name|void
name|assertSuccess
parameter_list|(
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|tss
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actual
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|tss
operator|.
name|forEach
argument_list|(
name|ts
lambda|->
name|ts
operator|.
name|getTestCases
argument_list|()
operator|.
name|forEach
argument_list|(
name|tc
lambda|->
block|{
block_content|if (tc instanceof TestCaseFailed
argument_list|)
block|{
name|expected
operator|.
name|add
argument_list|(
operator|(
operator|(
name|TestCaseFailed
operator|)
name|tc
operator|)
operator|.
name|expected
operator|.
name|orElse
argument_list|(
literal|"{UNKNOWN EXPECTED: "
operator|+
name|tc
operator|.
name|name
operator|+
literal|"}"
argument_list|)
argument_list|)
block|;
name|actual
operator|.
name|add
argument_list|(
operator|(
operator|(
name|TestCaseFailed
operator|)
name|tc
operator|)
operator|.
name|actual
operator|.
name|orElse
argument_list|(
literal|"{UNKNOWN ACTUAL: "
operator|+
name|tc
operator|.
name|name
operator|+
literal|"}"
argument_list|)
argument_list|)
block|;                     }
if|else if
condition|(
name|tc
operator|instanceof
name|TestCaseError
condition|)
block|{
name|expected
operator|.
name|add
argument_list|(
literal|"{UNKNOWN EXPECTED: "
operator|+
name|tc
operator|.
name|name
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|actual
operator|.
name|add
argument_list|(
literal|"{ERROR: "
operator|+
operator|(
operator|(
name|TestCaseError
operator|)
name|tc
operator|)
operator|.
name|reason
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
block|)
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertArrayEquals
argument_list|(
name|expected
operator|.
name|toArray
argument_list|()
argument_list|,
name|actual
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/**      * Prints the results of a test suite to the console      */
end_comment

begin_function
unit|private
name|void
name|printResults
parameter_list|(
specifier|final
name|TestSuite
name|ts
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"XQuery Test suite: "
operator|+
name|ts
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|getTestCases
argument_list|()
operator|.
name|forEach
argument_list|(
name|testCase
lambda|->
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|'\t'
operator|+
name|testCase
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**      * Parses the output of eXist's XML based XQuery test suite      *      * @param The XML element<testset> from the test suite output      * @return The results of the tests      */
end_comment

begin_function
specifier|private
name|List
argument_list|<
name|TestSuite
argument_list|>
name|parseXmlResults
parameter_list|(
specifier|final
name|Element
name|testset
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|TestSuite
name|ts
init|=
operator|new
name|TestSuite
argument_list|(
name|getFirstChildElement
argument_list|(
name|testset
argument_list|,
literal|"testName"
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|getText
argument_list|)
operator|.
name|orElse
argument_list|(
literal|"{UNKNOWN}"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NodeList
name|nlTests
init|=
name|testset
operator|.
name|getElementsByTagName
argument_list|(
literal|"test"
argument_list|)
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
name|nlTests
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Element
name|test
init|=
operator|(
name|Element
operator|)
name|nlTests
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|test
operator|.
name|getAttribute
argument_list|(
literal|"n"
argument_list|)
operator|+
name|Optional
operator|.
name|ofNullable
argument_list|(
name|test
operator|.
name|getAttribute
argument_list|(
literal|"id"
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|id
lambda|->
literal|" ("
operator|+
name|id
operator|+
literal|")"
argument_list|)
operator|.
name|orElse
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|pass
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|test
operator|.
name|getAttribute
argument_list|(
literal|"pass"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TestCase
name|tc
decl_stmt|;
if|if
condition|(
name|pass
condition|)
block|{
name|tc
operator|=
operator|new
name|TestCasePassed
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tc
operator|=
name|getFirstChildElement
argument_list|(
name|test
argument_list|,
literal|"result"
argument_list|)
operator|.
name|map
argument_list|(
name|result
lambda|->
name|getFirstChildElement
argument_list|(
name|result
argument_list|,
literal|"error"
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|getText
argument_list|)
operator|.
operator|<
name|TestCase
operator|>
name|map
argument_list|(
name|err
lambda|->
operator|new
name|TestCaseError
argument_list|(
name|name
argument_list|,
name|err
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
operator|new
name|TestCaseFailed
argument_list|(
name|name
argument_list|,
name|getFirstChildElement
argument_list|(
name|test
argument_list|,
literal|"task"
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|getText
argument_list|)
operator|.
name|orElse
argument_list|(
literal|"{UNKNOWN}"
argument_list|)
argument_list|,
name|getFirstChildElement
argument_list|(
name|test
argument_list|,
literal|"expected"
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|getText
argument_list|)
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|getText
argument_list|(
name|result
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|add
argument_list|(
name|tc
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|add
argument_list|(
name|ts
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
end_function

begin_comment
comment|/**      * Parses the output of eXist's XQuery based XQuery test suite      *      * @param The XML element<testsuites> from the test suite output      * @return The results of the tests      */
end_comment

begin_function
specifier|private
name|List
argument_list|<
name|TestSuite
argument_list|>
name|parseXQueryResults
parameter_list|(
specifier|final
name|Element
name|testsuites
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|TestSuite
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|nlTestSuite
init|=
name|testsuites
operator|.
name|getElementsByTagName
argument_list|(
literal|"testsuite"
argument_list|)
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
name|nlTestSuite
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Element
name|testsuite
init|=
operator|(
name|Element
operator|)
name|nlTestSuite
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|TestSuite
name|ts
init|=
operator|new
name|TestSuite
argument_list|(
name|testsuite
operator|.
name|getAttribute
argument_list|(
literal|"package"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NodeList
name|nlTestCase
init|=
name|testsuite
operator|.
name|getElementsByTagName
argument_list|(
literal|"testcase"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nlTestCase
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Element
name|testcase
init|=
operator|(
name|Element
operator|)
name|nlTestCase
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Element
argument_list|>
name|maybeFailure
init|=
name|getFirstChildElement
argument_list|(
name|testcase
argument_list|,
literal|"failure"
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Element
argument_list|>
name|maybeError
init|=
name|getFirstChildElement
argument_list|(
name|testcase
argument_list|,
literal|"error"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|testcase
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
specifier|final
name|TestCase
name|tc
init|=
name|maybeFailure
operator|.
expr|<
name|TestCase
operator|>
name|map
argument_list|(
name|failure
lambda|->
block|{
specifier|final
name|Optional
argument_list|<
name|Element
argument_list|>
name|output
init|=
name|getFirstChildElement
argument_list|(
name|testcase
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
return|return
operator|new
name|TestCaseFailed
argument_list|(
name|name
argument_list|,
name|failure
operator|.
name|getAttribute
argument_list|(
literal|"message"
argument_list|)
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|getText
argument_list|(
name|failure
argument_list|)
argument_list|)
argument_list|,
name|output
operator|.
name|map
argument_list|(
name|this
operator|::
name|getText
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|orElse
argument_list|(
name|maybeError
operator|.
expr|<
name|TestCase
operator|>
name|map
argument_list|(
name|error
lambda|->
operator|new
name|TestCaseError
argument_list|(
name|name
argument_list|,
name|error
operator|.
name|getAttribute
argument_list|(
literal|"message"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
operator|new
name|TestCasePassed
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ts
operator|.
name|add
argument_list|(
name|tc
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|add
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
end_function

begin_comment
comment|/**      * Extracts all child text node values from an element      * (non-recursive)      */
end_comment

begin_function
specifier|private
specifier|final
name|String
name|getText
parameter_list|(
specifier|final
name|Element
name|elem
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|nlChildren
init|=
name|elem
operator|.
name|getChildNodes
argument_list|()
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
name|nlChildren
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|n
init|=
name|nlChildren
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|n
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
end_function

begin_comment
comment|/**      * Gets the first named child element from a parent element that matches      */
end_comment

begin_function
specifier|private
name|Optional
argument_list|<
name|Element
argument_list|>
name|getFirstChildElement
parameter_list|(
specifier|final
name|Element
name|parent
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|parent
operator|.
name|getElementsByTagName
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|nl
lambda|->
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_class
specifier|private
class|class
name|TestSuite
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|TestCase
argument_list|>
name|testCases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|TestSuite
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|TestCase
name|testCase
parameter_list|)
block|{
name|testCases
operator|.
name|add
argument_list|(
name|testCase
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|TestCase
argument_list|>
name|getTestCases
parameter_list|()
block|{
return|return
name|testCases
return|;
block|}
block|}
end_class

begin_class
specifier|private
specifier|abstract
class|class
name|TestCase
block|{
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|TestCase
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
block|}
end_class

begin_class
specifier|private
class|class
name|TestCasePassed
extends|extends
name|TestCase
block|{
specifier|private
name|TestCasePassed
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PASSED: "
operator|+
name|name
return|;
block|}
block|}
end_class

begin_class
specifier|private
class|class
name|TestCaseError
extends|extends
name|TestCase
block|{
specifier|final
name|String
name|reason
decl_stmt|;
specifier|private
name|TestCaseError
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ERROR: "
operator|+
name|name
operator|+
literal|". "
operator|+
name|reason
operator|+
literal|"."
return|;
block|}
block|}
end_class

begin_class
specifier|private
class|class
name|TestCaseFailed
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|String
name|reason
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|expected
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|actual
decl_stmt|;
specifier|private
name|TestCaseFailed
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|actual
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
name|this
operator|.
name|actual
operator|=
name|actual
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"FAILED: "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|". "
argument_list|)
operator|.
name|append
argument_list|(
name|reason
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
name|expected
operator|.
name|map
argument_list|(
name|e
lambda|->
name|builder
operator|.
name|append
argument_list|(
literal|" Expected: '"
argument_list|)
operator|.
name|append
argument_list|(
name|e
argument_list|)
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
argument_list|)
expr_stmt|;
name|actual
operator|.
name|map
argument_list|(
name|a
lambda|->
name|builder
operator|.
name|append
argument_list|(
literal|" Actual: '"
argument_list|)
operator|.
name|append
argument_list|(
name|a
argument_list|)
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_function
annotation|@
name|Before
specifier|public
name|void
name|setUpBefore
parameter_list|()
throws|throws
name|Exception
block|{
comment|// initialize driver
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|rootCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|After
specifier|public
name|void
name|tearDownAfter
parameter_list|()
block|{
if|if
condition|(
name|rootCollection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
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
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|rootCollection
operator|=
literal|null
expr_stmt|;
block|}
end_function

begin_function
specifier|protected
specifier|static
name|Document
name|parse
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ParserConfigurationException
block|{
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
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
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
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
return|return
name|adapter
operator|.
name|getDocument
argument_list|()
return|;
block|}
end_function

unit|}
end_unit

