begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|runner
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|PermissionDeniedException
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
name|ClassLoaderSource
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
name|DatabaseConfigurationException
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
name|ExistSAXParserFactory
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
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunNotifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|InitializationError
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
name|*
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|*
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
import|;
end_import

begin_comment
comment|/**  * A JUnit test runner which can run the XML formatter XQuery tests  * of eXist-db using $EXIST_HOME/src/org/exist/xquery/lib/test.xq.  *  * @author Adam Retter  */
end_comment

begin_class
specifier|public
class|class
name|XMLTestRunner
extends|extends
name|AbstractTestRunner
block|{
specifier|private
specifier|static
specifier|final
name|SAXParserFactory
name|SAX_PARSER_FACTORY
init|=
name|ExistSAXParserFactory
operator|.
name|getSAXParserFactory
argument_list|()
decl_stmt|;
static|static
block|{
name|SAX_PARSER_FACTORY
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
name|Document
name|doc
decl_stmt|;
specifier|private
specifier|final
name|XMLTestInfo
name|info
decl_stmt|;
comment|/**      * @param path The path to the XML file containing the tests.      * @param parallel whether the tests should be run in parallel.      *      * @throws InitializationError if the test runner could not be constructed.      */
specifier|public
name|XMLTestRunner
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|boolean
name|parallel
parameter_list|)
throws|throws
name|InitializationError
block|{
name|super
argument_list|(
name|path
argument_list|,
name|parallel
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|doc
operator|=
name|parse
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
decl||
name|IOException
decl||
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationError
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|info
operator|=
name|extractTestInfo
argument_list|(
name|path
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|XMLTestInfo
name|extractTestInfo
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|Document
name|doc
parameter_list|)
throws|throws
name|InitializationError
block|{
name|String
name|testSetName
init|=
literal|null
decl_stmt|;
name|String
name|description
init|=
literal|null
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|testNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|docElement
init|=
name|doc
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|docElement
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InitializationError
argument_list|(
literal|"Invalid XML test document: "
operator|+
name|path
argument_list|)
throw|;
block|}
specifier|final
name|NodeList
name|children
init|=
name|docElement
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
name|children
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
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|child
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
condition|)
block|{
case|case
literal|"testName"
case|:
name|testSetName
operator|=
name|child
operator|.
name|getTextContent
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"description"
case|:
name|description
operator|=
name|child
operator|.
name|getTextContent
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"test"
case|:
comment|// prefer @id over<task> for the test name
name|String
name|testName
init|=
name|getIdValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|testName
operator|==
literal|null
condition|)
block|{
name|testName
operator|=
name|getTaskText
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|testName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InitializationError
argument_list|(
literal|"Could not find @id or<task> within<test> of XML<TestSet> document:"
operator|+
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|testNames
operator|.
name|add
argument_list|(
name|testName
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// ignored
break|break;
block|}
block|}
block|}
if|if
condition|(
name|testSetName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InitializationError
argument_list|(
literal|"Could not find<testName> in XML<TestSet> document: "
operator|+
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|XMLTestInfo
argument_list|(
name|testSetName
argument_list|,
name|description
argument_list|,
name|testNames
argument_list|)
return|;
block|}
specifier|private
specifier|static
annotation|@
name|Nullable
name|String
name|getIdValue
parameter_list|(
specifier|final
name|Node
name|test
parameter_list|)
block|{
name|String
name|id
init|=
operator|(
operator|(
name|Element
operator|)
name|test
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|id
operator|=
name|id
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|id
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|id
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
annotation|@
name|Nullable
name|String
name|getTaskText
parameter_list|(
specifier|final
name|Node
name|test
parameter_list|)
block|{
specifier|final
name|NodeList
name|testChildren
init|=
name|test
operator|.
name|getChildNodes
argument_list|()
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
name|testChildren
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Node
name|testChild
init|=
name|testChildren
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|testChild
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|testChild
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
operator|&&
name|testChild
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"task"
argument_list|)
condition|)
block|{
name|String
name|textContent
init|=
name|testChild
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|textContent
operator|!=
literal|null
condition|)
block|{
name|textContent
operator|=
name|textContent
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|textContent
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|textContent
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|getSuiteName
parameter_list|()
block|{
specifier|final
name|String
name|suiteName
init|=
literal|"xmlts."
operator|+
name|info
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// add "xmlts." prefix
return|return
name|suiteName
return|;
block|}
annotation|@
name|Override
specifier|public
name|Description
name|getDescription
parameter_list|()
block|{
specifier|final
name|Description
name|description
init|=
name|Description
operator|.
name|createSuiteDescription
argument_list|(
name|getSuiteName
argument_list|()
argument_list|,
operator|(
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|childName
range|:
name|info
operator|.
name|getChildNames
argument_list|()
control|)
block|{
name|description
operator|.
name|addChild
argument_list|(
name|Description
operator|.
name|createTestDescription
argument_list|(
name|getSuiteName
argument_list|()
argument_list|,
name|childName
argument_list|,
operator|(
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|description
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
specifier|final
name|RunNotifier
name|notifier
parameter_list|)
block|{
try|try
block|{
specifier|final
name|String
name|pkgName
init|=
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|query
init|=
operator|new
name|ClassLoaderSource
argument_list|(
name|pkgName
operator|+
literal|"/xml-test-runner.xq"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|XQueryContext
argument_list|,
name|Tuple2
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|externalVariableDeclarations
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"doc"
argument_list|,
name|doc
argument_list|)
argument_list|,
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"id"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
argument_list|,
comment|// set callback functions for notifying junit!
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"test-ignored-function"
argument_list|,
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
operator|new
name|ExtTestIgnoredFunction
argument_list|(
name|context
argument_list|,
name|getSuiteName
argument_list|()
argument_list|,
name|notifier
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"test-started-function"
argument_list|,
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
operator|new
name|ExtTestStartedFunction
argument_list|(
name|context
argument_list|,
name|getSuiteName
argument_list|()
argument_list|,
name|notifier
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"test-failure-function"
argument_list|,
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
operator|new
name|ExtTestFailureFunction
argument_list|(
name|context
argument_list|,
name|getSuiteName
argument_list|()
argument_list|,
name|notifier
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"test-assumption-failed-function"
argument_list|,
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
operator|new
name|ExtTestAssumptionFailedFunction
argument_list|(
name|context
argument_list|,
name|getSuiteName
argument_list|()
argument_list|,
name|notifier
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"test-error-function"
argument_list|,
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
operator|new
name|ExtTestErrorFunction
argument_list|(
name|context
argument_list|,
name|getSuiteName
argument_list|()
argument_list|,
name|notifier
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
lambda|->
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|"test-finished-function"
argument_list|,
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
operator|new
name|ExtTestFinishedFunction
argument_list|(
name|context
argument_list|,
name|getSuiteName
argument_list|()
argument_list|,
name|notifier
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|executeQuery
argument_list|(
name|query
argument_list|,
name|externalVariableDeclarations
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
decl||
name|IOException
decl||
name|EXistException
decl||
name|PermissionDeniedException
decl||
name|XPathException
name|e
parameter_list|)
block|{
comment|//TODO(AR) what to do here?
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Document
name|parse
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SAXParser
name|parser
init|=
name|SAX_PARSER_FACTORY
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
specifier|final
name|XMLReader
name|xr
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|xr
operator|.
name|setFeature
argument_list|(
literal|"http://xml.org/sax/features/external-general-entities"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setFeature
argument_list|(
literal|"http://xml.org/sax/features/external-parameter-entities"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setFeature
argument_list|(
name|FEATURE_SECURE_PROCESSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// we have to use eXist-db's SAXAdapter, otherwise un-referenced namespaces as used by xpath assertions may be stripped by Xerces.
specifier|final
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
specifier|private
specifier|static
class|class
name|XMLTestInfo
block|{
annotation|@
name|Nullable
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Nullable
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|childNames
decl_stmt|;
specifier|private
name|XMLTestInfo
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|childNames
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|childNames
operator|=
name|childNames
expr_stmt|;
block|}
annotation|@
name|Nullable
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getChildNames
parameter_list|()
block|{
return|return
name|childNames
return|;
block|}
block|}
block|}
end_class

end_unit

