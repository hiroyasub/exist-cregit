begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012-2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|*
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
name|Files
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
name|Optional
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
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|persistent
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
name|DBBroker
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
name|txn
operator|.
name|TransactionManager
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
name|txn
operator|.
name|Txn
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
name|*
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
name|NamedNodeMap
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

begin_comment
comment|/**  * JUnit tests generator from QT3 test suite catalog.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|QT3TS_To_junit
block|{
specifier|private
specifier|static
specifier|final
name|String
name|sep
init|=
name|File
operator|.
name|separator
decl_stmt|;
comment|/**      * @param args      * @throws Exception       */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|QT3TS_To_junit
name|convertor
init|=
operator|new
name|QT3TS_To_junit
argument_list|()
decl_stmt|;
try|try
block|{
name|convertor
operator|.
name|startup
argument_list|()
expr_stmt|;
comment|//            convertor.create();
name|convertor
operator|.
name|load
argument_list|()
expr_stmt|;
name|convertor
operator|.
name|create
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
block|}
finally|finally
block|{
name|convertor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
specifier|private
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|startup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|TransactionManager
name|txnMgr
init|=
name|db
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|txnMgr
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|collection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|QT3TS_case
operator|.
name|QT3_URI
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|txnMgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|release
parameter_list|()
throws|throws
name|Exception
block|{
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|release
argument_list|()
expr_stmt|;
name|db
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
literal|"database was shutdownDB"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|load
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|folder
init|=
operator|new
name|File
argument_list|(
name|QT3TS_case
operator|.
name|FOLDER
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|folder
operator|.
name|listFiles
argument_list|()
decl_stmt|;
specifier|final
name|TransactionManager
name|txnMgr
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|txnMgr
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"CVS"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"drivers"
argument_list|)
condition|)
continue|continue;
comment|//ignore
name|loadDirectory
argument_list|(
name|txn
argument_list|,
name|file
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|".project"
argument_list|)
condition|)
continue|continue;
comment|//ignore
name|loadFile
argument_list|(
name|txn
argument_list|,
name|file
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
name|txnMgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|loadDirectory
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|File
name|folder
parameter_list|,
name|Collection
name|col
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
name|folder
operator|.
name|exists
argument_list|()
operator|&&
name|folder
operator|.
name|canRead
argument_list|()
operator|)
condition|)
return|return;
name|Collection
name|current
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
literal|null
argument_list|,
name|col
operator|.
name|getURI
argument_list|()
operator|.
name|append
argument_list|(
name|folder
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
literal|null
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|folder
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"CVS"
argument_list|)
condition|)
continue|continue;
comment|//ignore
name|loadDirectory
argument_list|(
name|txn
argument_list|,
name|file
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|loadFile
argument_list|(
name|txn
argument_list|,
name|file
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|loadFile
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|File
name|file
parameter_list|,
name|Collection
name|col
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".html"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xsd"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"badxml.xml"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"BCisInvalid.xml"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"InvalidUmlaut.xml"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"InvalidXMLId.xml"
argument_list|)
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"invalid-xml.xml"
argument_list|)
condition|)
return|return;
if|if
condition|(
operator|!
operator|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
name|file
operator|.
name|canRead
argument_list|()
operator|)
condition|)
return|return;
name|MimeType
name|mime
init|=
name|getMimeTable
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
operator|&&
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
name|IndexInfo
name|info
init|=
name|col
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//info.getDocument().getMetadata().setMimeType();
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|col
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|col
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|,
name|MimeType
operator|.
name|BINARY_TYPE
operator|.
name|getName
argument_list|()
argument_list|,
name|file
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|MimeTable
name|mtable
init|=
literal|null
decl_stmt|;
specifier|private
name|MimeTable
name|getMimeTable
parameter_list|()
block|{
if|if
condition|(
name|mtable
operator|==
literal|null
condition|)
block|{
name|mtable
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
return|return
name|mtable
return|;
block|}
name|String
name|tsQuery
init|=
literal|"declare namespace qt='http://www.w3.org/2010/09/qt-fots-catalog'; "
operator|+
literal|"let $catalog := xmldb:document('/db/QT3/catalog.xml') "
operator|+
literal|"return $catalog//qt:test-set"
decl_stmt|;
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|Exception
block|{
name|Optional
argument_list|<
name|Path
argument_list|>
name|existHome
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|Path
name|src
init|=
name|FileUtils
operator|.
name|resolve
argument_list|(
name|existHome
argument_list|,
literal|"test/src/org/exist/xquery/xqts/qt3"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|src
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isReadable
argument_list|(
name|src
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"QT3 junit tests folder unreadable."
argument_list|)
throw|;
block|}
name|XQuery
name|xqs
init|=
name|db
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|Sequence
name|results
init|=
name|xqs
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|tsQuery
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeProxy
name|p
range|:
name|results
operator|.
name|toNodeSet
argument_list|()
control|)
block|{
name|NamedNodeMap
name|attrs
init|=
name|p
operator|.
name|getNode
argument_list|()
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|String
name|file
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"file"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|processSet
argument_list|(
name|src
argument_list|,
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processSet
parameter_list|(
name|Path
name|src
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|tsQuery
init|=
literal|"declare namespace qt='http://www.w3.org/2010/09/qt-fots-catalog'; "
operator|+
literal|"let $catalog := xmldb:document('/db/QT3/"
operator|+
name|file
operator|+
literal|"') "
operator|+
literal|"return $catalog//qt:test-case"
decl_stmt|;
name|XQuery
name|xqs
init|=
name|db
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|Sequence
name|results
init|=
name|xqs
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|tsQuery
argument_list|,
literal|null
argument_list|,
name|AccessContext
operator|.
name|TEST
argument_list|)
decl_stmt|;
name|testCases
argument_list|(
name|src
argument_list|,
name|file
argument_list|,
name|name
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
comment|//<test-case name="fn-absint1args-2">
comment|//<description>Test: absint1args-2 The "abs" function with the arguments set as follows: $arg = xs:int(mid range)</description>
comment|//<created by="Carmelo Montanez" on="2004-12-13"/>
comment|//<environment ref="empty"/>
comment|//<test>fn:abs(xs:int("-1873914410"))</test>
comment|//<result>
comment|//<all-of>
comment|//<assert-eq>1873914410</assert-eq>
comment|//<assert-type>xs:integer</assert-type>
comment|//</all-of>
comment|//</result>
comment|//</test-case>
specifier|private
name|void
name|testCases
parameter_list|(
name|Path
name|src
parameter_list|,
name|String
name|file
parameter_list|,
name|String
name|group
parameter_list|,
name|Sequence
name|results
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|className
init|=
name|adoptString
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|StringBuilder
name|subPath
init|=
operator|new
name|StringBuilder
argument_list|(
name|src
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|_package_
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//adoptString(group);
name|String
index|[]
name|strs
init|=
name|file
operator|.
name|split
argument_list|(
literal|"/"
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
name|strs
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|subPath
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|append
argument_list|(
name|strs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|_package_
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|strs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|File
name|folder
init|=
operator|new
name|File
argument_list|(
name|subPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|folder
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|jTest
init|=
operator|new
name|File
argument_list|(
name|folder
argument_list|,
name|className
operator|+
literal|".java"
argument_list|)
decl_stmt|;
name|FileWriter
name|fstream
init|=
operator|new
name|FileWriter
argument_list|(
name|jTest
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
operator|new
name|BufferedWriter
argument_list|(
name|fstream
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"package org.exist.xquery.xqts.qt3"
operator|+
name|_package_
operator|+
literal|";\n\n"
operator|+
literal|"import org.exist.xquery.xqts.QT3TS_case;\n"
operator|+
literal|"import org.junit.*;\n\n"
operator|+
literal|"public class "
operator|+
name|className
operator|+
literal|" extends QT3TS_case {\n"
operator|+
literal|"    private String file = \""
operator|+
name|file
operator|+
literal|"\";\n\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeProxy
name|p
range|:
name|results
operator|.
name|toNodeSet
argument_list|()
control|)
block|{
name|Node
name|testSet
init|=
name|p
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|testSet
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|testName
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|String
name|adoptTestName
init|=
name|adoptString
argument_list|(
name|testName
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"    /* "
operator|+
name|testName
operator|+
literal|" */\n"
operator|+
literal|"    @Test\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|adoptTestName
operator|.
name|contains
argument_list|(
literal|"fold_left_008"
argument_list|)
operator|||
name|adoptTestName
operator|.
name|contains
argument_list|(
literal|"fold_left_020"
argument_list|)
operator|||
name|adoptTestName
operator|.
name|contains
argument_list|(
literal|"fn_deep_equal_node_args_3"
argument_list|)
operator|||
name|adoptTestName
operator|.
name|contains
argument_list|(
literal|"fn_deep_equal_node_args_4"
argument_list|)
operator|||
name|adoptTestName
operator|.
name|contains
argument_list|(
literal|"fn_deep_equal_node_args_5"
argument_list|)
operator|||
name|adoptTestName
operator|.
name|contains
argument_list|(
literal|"fold_right_013"
argument_list|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"    @Ignore\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"    public void test_"
operator|+
name|adoptTestName
operator|+
literal|"() {\n"
operator|+
literal|"        testCase(file, \""
operator|+
name|testName
operator|+
literal|"\");\n"
operator|+
literal|"    }\n\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|adoptString
parameter_list|(
name|String
name|caseName
parameter_list|)
block|{
name|String
name|result
init|=
name|caseName
operator|.
name|replace
argument_list|(
literal|"-"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

