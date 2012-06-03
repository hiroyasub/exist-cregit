begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|xqts
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
name|Reader
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DefaultLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|ProjectHelper
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
name|ElementImpl
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
name|NodeListImpl
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
name|xacml
operator|.
name|AccessContext
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
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|w3c
operator|.
name|tests
operator|.
name|TestCase
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
name|xquery
operator|.
name|CompiledXQuery
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
name|XPathException
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
name|XQuery
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
name|DateTimeValue
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
name|w3c
operator|.
name|dom
operator|.
name|Attr
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|XQTS_case
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|XQTS_folder
init|=
literal|"test/external/XQTS_1_0_3/"
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sources
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moduleSources
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|loadTS
parameter_list|()
throws|throws
name|Exception
block|{
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
name|File
name|buildFile
init|=
operator|new
name|File
argument_list|(
literal|"webapp/xqts/build.xml"
argument_list|)
decl_stmt|;
name|Project
name|p
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|p
operator|.
name|setUserProperty
argument_list|(
literal|"ant.file"
argument_list|,
name|buildFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|setUserProperty
argument_list|(
literal|"config.basedir"
argument_list|,
literal|"../../"
operator|+
name|XQTS_folder
argument_list|)
expr_stmt|;
name|DefaultLogger
name|consoleLogger
init|=
operator|new
name|DefaultLogger
argument_list|()
decl_stmt|;
name|consoleLogger
operator|.
name|setErrorPrintStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|consoleLogger
operator|.
name|setOutputPrintStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|consoleLogger
operator|.
name|setMessageOutputLevel
argument_list|(
name|Project
operator|.
name|MSG_INFO
argument_list|)
expr_stmt|;
name|p
operator|.
name|addBuildListener
argument_list|(
name|consoleLogger
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|fireBuildStarted
argument_list|()
expr_stmt|;
name|p
operator|.
name|init
argument_list|()
expr_stmt|;
name|ProjectHelper
name|helper
init|=
name|ProjectHelper
operator|.
name|getProjectHelper
argument_list|()
decl_stmt|;
name|p
operator|.
name|addReference
argument_list|(
literal|"ant.projectHelper"
argument_list|,
name|helper
argument_list|)
expr_stmt|;
name|helper
operator|.
name|parse
argument_list|(
name|p
argument_list|,
name|buildFile
argument_list|)
expr_stmt|;
name|p
operator|.
name|executeTarget
argument_list|(
literal|"store"
argument_list|)
expr_stmt|;
name|p
operator|.
name|fireBuildFinished
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BuildException
name|e
parameter_list|)
block|{
name|p
operator|.
name|fireBuildFinished
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
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
throw|throw
operator|new
name|Exception
argument_list|(
literal|"XQTS collection wasn't found"
argument_list|)
throw|;
if|if
condition|(
name|sources
operator|==
literal|null
condition|)
block|{
name|sources
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"declare namespace catalog=\"http://www.w3.org/2005/02/query-test-XQTSCatalog\";"
operator|+
literal|"let $XQTSCatalog := xmldb:document('/db/XQTS/XQTSCatalog.xml') "
operator|+
literal|"return $XQTSCatalog//catalog:sources//catalog:source"
decl_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
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
name|results
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|source
init|=
operator|(
name|ElementImpl
operator|)
operator|(
operator|(
name|XMLResource
operator|)
name|results
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|sources
operator|.
name|put
argument_list|(
name|source
operator|.
name|getAttribute
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
name|XQTS_folder
operator|+
name|source
operator|.
name|getAttribute
argument_list|(
literal|"FileName"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|moduleSources
operator|==
literal|null
condition|)
block|{
name|moduleSources
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"declare namespace catalog=\"http://www.w3.org/2005/02/query-test-XQTSCatalog\";"
operator|+
literal|"let $XQTSCatalog := xmldb:document('/db/XQTS/XQTSCatalog.xml') "
operator|+
literal|"return $XQTSCatalog//catalog:sources//catalog:module"
decl_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
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
name|results
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|source
init|=
operator|(
name|ElementImpl
operator|)
operator|(
operator|(
name|XMLResource
operator|)
name|results
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|moduleSources
operator|.
name|put
argument_list|(
name|source
operator|.
name|getAttribute
argument_list|(
literal|"ID"
argument_list|)
argument_list|,
literal|"test/external/XQTS_1_0_3/"
operator|+
name|source
operator|.
name|getAttribute
argument_list|(
literal|"FileName"
argument_list|)
operator|+
literal|".xq"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//	private static final String catalogNS = "http://www.w3.org/2005/02/query-test-XQTSCatalog";
specifier|protected
name|void
name|groupCase
parameter_list|(
name|String
name|testGroup
parameter_list|,
name|String
name|testCase
parameter_list|)
block|{
comment|//ignore tests
comment|//        if (testGroup.equals("FunctionCallExpr")&& testCase.equals("K-FunctionCallExpr-11"))
comment|//            return;
comment|//        else if (testGroup.equals("SeqCollectionFunc")) {
comment|//            if (testCase.equals("fn-collection-4d")
comment|//                || testCase.equals("fn-collection-5d")
comment|//                || testCase.equals("fn-collection-9")
comment|//                || testCase.equals("fn-collection-10d"))
comment|//                return;
comment|//        if (testCase.equals("K2-NodeTest-11"))
comment|//            return; //Added by p.b. as a quick attempt to work around current blocking code
comment|//        if (testCase.equals("Constr-cont-document-3"))
comment|//            return; //Added by p.b. as a quick attempt to work around current blocking code
try|try
block|{
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"declare namespace catalog=\"http://www.w3.org/2005/02/query-test-XQTSCatalog\";\n"
operator|+
literal|"let $XQTSCatalog := xmldb:document('/db/XQTS/XQTSCatalog.xml')\n"
operator|+
literal|"let $tc := $XQTSCatalog/catalog:test-suite//catalog:test-group[@name eq \""
operator|+
name|testGroup
operator|+
literal|"\"]/catalog:test-case[@name eq \""
operator|+
name|testCase
operator|+
literal|"\"]\n"
operator|+
literal|"return $tc"
decl_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|""
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
operator|!=
literal|1
argument_list|)
expr_stmt|;
name|ElementImpl
name|TC
init|=
operator|(
name|ElementImpl
operator|)
operator|(
operator|(
name|XMLResource
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
comment|//collect test case information
name|String
name|folder
init|=
literal|""
decl_stmt|;
name|String
name|scenario
init|=
literal|""
decl_stmt|;
name|String
name|script
init|=
literal|""
decl_stmt|;
comment|//DateTimeValue scriptDateTime = null;
name|NodeListImpl
name|inputFiles
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|NodeListImpl
name|outputFiles
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|ElementImpl
name|contextItem
init|=
literal|null
decl_stmt|;
name|NodeListImpl
name|modules
init|=
operator|new
name|NodeListImpl
argument_list|()
decl_stmt|;
name|String
name|expectedError
init|=
literal|""
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|NodeList
name|childNodes
init|=
name|TC
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
name|childNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|childNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|name
operator|=
operator|(
operator|(
name|Attr
operator|)
name|child
operator|)
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"FilePath"
argument_list|)
condition|)
name|folder
operator|=
operator|(
operator|(
name|Attr
operator|)
name|child
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"scenario"
argument_list|)
condition|)
name|scenario
operator|=
operator|(
operator|(
name|Attr
operator|)
name|child
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|name
operator|=
operator|(
operator|(
name|ElementImpl
operator|)
name|child
operator|)
operator|.
name|getLocalName
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"query"
argument_list|)
condition|)
block|{
name|ElementImpl
name|el
init|=
operator|(
operator|(
name|ElementImpl
operator|)
name|child
operator|)
decl_stmt|;
name|script
operator|=
name|el
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
comment|//scriptDateTime = new DateTimeValue(el.getAttribute("date"));
block|}
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"input-file"
argument_list|)
condition|)
name|inputFiles
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"output-file"
argument_list|)
condition|)
name|outputFiles
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"contextItem"
argument_list|)
condition|)
name|contextItem
operator|=
operator|(
name|ElementImpl
operator|)
name|child
expr_stmt|;
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"module"
argument_list|)
condition|)
name|modules
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|else if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"expected-error"
argument_list|)
condition|)
name|expectedError
operator|=
operator|(
operator|(
name|ElementImpl
operator|)
name|child
operator|)
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
break|break;
default|default :
empty_stmt|;
block|}
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
comment|//compile& evaluate
name|File
name|caseScript
init|=
operator|new
name|File
argument_list|(
name|XQTS_folder
operator|+
literal|"Queries/XQuery/"
operator|+
name|folder
argument_list|,
name|script
operator|+
literal|".xq"
argument_list|)
decl_stmt|;
try|try
block|{
name|XQueryContext
name|context
decl_stmt|;
name|XQuery
name|xquery
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|xquery
operator|=
name|broker
operator|.
name|getXQueryService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_XQUERY_RAISE_ERROR_ON_FAILED_RETRIEVAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|TEST
argument_list|)
expr_stmt|;
comment|//map modules' namespaces to location
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moduleMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_STATIC_MODULE_MAP
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
name|modules
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|module
init|=
operator|(
name|ElementImpl
operator|)
name|modules
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|module
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|moduleMap
operator|.
name|put
argument_list|(
name|module
operator|.
name|getAttribute
argument_list|(
literal|"namespace"
argument_list|)
argument_list|,
name|moduleSources
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_STATIC_MODULE_MAP
argument_list|,
name|moduleMap
argument_list|)
expr_stmt|;
comment|//declare variable
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inputFiles
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|inputFile
init|=
operator|(
name|ElementImpl
operator|)
name|inputFiles
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|inputFile
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
comment|//use DocUtils
comment|//context.declareVariable(
comment|//inputFile.getAttribute("variable"),
comment|//DocUtils.getDocument(context, sources.get(id))
comment|//);
comment|//in-memory nodes
name|context
operator|.
name|declareVariable
argument_list|(
name|inputFile
operator|.
name|getAttribute
argument_list|(
literal|"variable"
argument_list|)
argument_list|,
name|loadVarFromURI
argument_list|(
name|context
argument_list|,
name|sources
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|contextSequence
init|=
literal|null
decl_stmt|;
comment|//set context item
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|contextItem
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|contextSequence
operator|=
name|loadVarFromURI
argument_list|(
name|context
argument_list|,
name|sources
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fixBrokenTests
argument_list|(
name|context
argument_list|,
name|testGroup
argument_list|,
name|testCase
argument_list|)
expr_stmt|;
comment|//compile
name|CompiledXQuery
name|compiled
init|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|FileSource
argument_list|(
name|caseScript
argument_list|,
literal|"UTF8"
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|//execute
name|Sequence
name|result
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
comment|//compare result with one provided by test case
name|boolean
name|ok
init|=
literal|false
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
name|outputFiles
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|outputFile
init|=
operator|(
name|ElementImpl
operator|)
name|outputFiles
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|compareResult
argument_list|(
name|script
argument_list|,
literal|"XQTS_1_0_3/ExpectedTestResults/"
operator|+
name|folder
argument_list|,
name|outputFile
argument_list|,
name|result
argument_list|)
condition|)
block|{
name|ok
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|//collect information if result is wrong
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuffer
name|exp
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outputFiles
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|outputFile
init|=
operator|(
name|ElementImpl
operator|)
name|outputFiles
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|File
name|expectedResult
init|=
operator|new
name|File
argument_list|(
name|XQTS_folder
operator|+
literal|"ExpectedTestResults/"
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
comment|//avoid to big output
if|if
condition|(
name|expectedResult
operator|.
name|length
argument_list|()
operator|>=
literal|1024
condition|)
block|{
name|exp
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|exp
operator|.
name|append
argument_list|(
literal|"{TOO BIG}"
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|exp
operator|.
name|append
argument_list|(
literal|"{'"
argument_list|)
expr_stmt|;
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
name|char
name|ch
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|ready
argument_list|()
condition|)
block|{
name|ch
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
name|ch
operator|==
literal|'\r'
condition|)
name|ch
operator|=
operator|(
name|char
operator|)
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
name|exp
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|exp
operator|.
name|append
argument_list|(
literal|"'}"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exp
operator|.
name|append
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|res
init|=
name|sequenceToString
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|exp
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|exp
operator|.
name|append
argument_list|(
literal|"error "
operator|+
name|expectedError
argument_list|)
expr_stmt|;
name|StringBuilder
name|data
init|=
operator|new
name|StringBuilder
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
name|inputFiles
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ElementImpl
name|inputFile
init|=
operator|(
name|ElementImpl
operator|)
name|inputFiles
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|inputFile
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|data
operator|.
name|append
argument_list|(
name|inputFile
operator|.
name|getAttribute
argument_list|(
literal|"variable"
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|append
argument_list|(
literal|" = \n"
argument_list|)
expr_stmt|;
name|data
operator|.
name|append
argument_list|(
name|readFileAsString
argument_list|(
operator|new
name|File
argument_list|(
name|sources
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|,
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"expected "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"["
operator|+
name|exp
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|" got "
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"["
operator|+
name|res
operator|+
literal|"]\n"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"script:\n"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|readFileAsString
argument_list|(
name|caseScript
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"data:\n"
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//                String error = e.getMessage();
if|if
condition|(
operator|!
name|expectedError
operator|.
name|isEmpty
argument_list|()
condition|)
empty_stmt|;
if|else if
condition|(
name|expectedError
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
empty_stmt|;
comment|//TODO:check e.getCode()
comment|//                else if (error.indexOf(expectedError) != -1)
comment|//                    ;
comment|//                else {
comment|//                    if (error.startsWith("err:")) error = error.substring(4);
comment|//
comment|//                    if (error.indexOf(expectedError) == -1)
comment|//                        Assert.fail("expected error is "+expectedError+", got "+error+" ["+e.getMessage()+"]");
comment|//                }
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|XMLDBException
condition|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"SENR0001"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|expectedError
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
block|}
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"\n during script evaluation:\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|.
name|append
argument_list|(
name|readFileAsString
argument_list|(
name|caseScript
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|"ERROR - "
operator|+
name|e1
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|fail
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fixBrokenTests
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|group
parameter_list|,
name|String
name|test
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|group
operator|.
name|equals
argument_list|(
literal|"ContextImplicitTimezoneFunc"
argument_list|)
condition|)
block|{
name|TimeZone
name|implicitTimeZone
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT-5:00"
argument_list|)
decl_stmt|;
comment|// getDefault();
comment|//if( implicitTimeZone.inDaylightTime( new Date() ) ) {
comment|//implicitTimeZone.setRawOffset( implicitTimeZone.getRawOffset() + implicitTimeZone.getDSTSavings() );
comment|//}
name|context
operator|.
name|setTimeZone
argument_list|(
name|implicitTimeZone
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|group
operator|.
name|equals
argument_list|(
literal|"ContextCurrentDatetimeFunc"
argument_list|)
operator|||
name|group
operator|.
name|equals
argument_list|(
literal|"ContextCurrentDateFunc"
argument_list|)
operator|||
name|group
operator|.
name|equals
argument_list|(
literal|"ContextCurrentTimeFunc"
argument_list|)
condition|)
block|{
name|DateTimeValue
name|dt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-time-4"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T13:38:03.455-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-time-6"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T13:38:18.059-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-time-7"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T13:38:18.059-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-time-10"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T13:38:18.09-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|startsWith
argument_list|(
literal|"fn-current-time-"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T10:15:03.408-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-dateTime-6"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T17:10:00.312-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-datetime-7"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T17:10:00.312-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-dateTime-10"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T17:10:00.344-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-dateTime-21"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T17:10:00.453-05:00"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|equals
argument_list|(
literal|"fn-current-dateTime-24"
argument_list|)
condition|)
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T17:10:00.469-05:00"
argument_list|)
expr_stmt|;
else|else
name|dt
operator|=
operator|new
name|DateTimeValue
argument_list|(
literal|"2005-12-05T17:10:00.203-05:00"
argument_list|)
expr_stmt|;
comment|//if (dt != null)
name|context
operator|.
name|setCalendar
argument_list|(
name|dt
operator|.
name|calendar
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
end_class

end_unit

