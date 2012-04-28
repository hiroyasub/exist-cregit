begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|*
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
name|org
operator|.
name|apache
operator|.
name|log4j
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
name|util
operator|.
name|MimeTable
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
name|MimeType
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
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|CompiledExpression
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
name|CollectionManagementService
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XPathQueryService
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
name|XQueryService
import|;
end_import

begin_comment
comment|/**  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedExistTester
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|EmbeddedExistTester
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|URI
init|=
name|XmldbURI
operator|.
name|LOCAL_DB
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|protected
specifier|static
name|Collection
name|rootCollection
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|static
name|XPathQueryService
name|xpxqService
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|static
name|Database
name|database
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|static
name|CollectionManagementService
name|cmService
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|static
name|XQueryService
name|xqService
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|isInitialized
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
name|void
name|initLog4J
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isInitialized
condition|)
block|{
name|Layout
name|layout
init|=
operator|new
name|PatternLayout
argument_list|(
literal|"%d{ISO8601} [%t] %-5p (%F [%M]:%L) - %m %n"
argument_list|)
decl_stmt|;
name|Appender
name|appender
init|=
operator|new
name|ConsoleAppender
argument_list|(
name|layout
argument_list|)
decl_stmt|;
name|BasicConfigurator
operator|.
name|resetConfiguration
argument_list|()
expr_stmt|;
name|BasicConfigurator
operator|.
name|configure
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|isInitialized
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|before
parameter_list|()
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting test.."
argument_list|)
expr_stmt|;
name|initLog4J
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting test.."
argument_list|)
expr_stmt|;
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
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
name|xpxqService
operator|=
operator|(
name|XPathQueryService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|cmService
operator|=
operator|(
name|CollectionManagementService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|xqService
operator|=
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|after
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping test.."
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
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
name|database
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|before_test
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n-------------------------------------------------------\n"
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|Collection
name|createCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create collection "
operator|+
name|collectionName
argument_list|)
expr_stmt|;
name|Collection
name|newCollection
init|=
name|collection
operator|.
name|getChildCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCollection
operator|==
literal|null
condition|)
block|{
name|cmService
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
name|newCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/"
operator|+
name|collectionName
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newCollection
argument_list|)
expr_stmt|;
return|return
name|newCollection
return|;
block|}
specifier|protected
specifier|static
name|void
name|storeResource
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|documentName
parameter_list|,
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Store "
operator|+
name|documentName
argument_list|)
expr_stmt|;
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|documentName
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|mime
operator|.
name|isXMLType
argument_list|()
condition|?
literal|"XMLResource"
else|:
literal|"BinaryResource"
decl_stmt|;
name|Resource
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
name|documentName
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|static
name|ResourceSet
name|executeQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing "
operator|+
name|query
argument_list|)
expr_stmt|;
name|CompiledExpression
name|compiledQuery
init|=
name|xqService
operator|.
name|compile
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xqService
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
specifier|static
name|String
name|executeOneValue
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|protected
specifier|static
name|byte
index|[]
name|readFile
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|File
name|src
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading file: "
operator|+
name|src
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|src
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
decl_stmt|;
comment|// Transfer bytes from in to out
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
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
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
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|protected
name|String
name|getXMLResource
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|resource
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collection
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|String
name|retval
init|=
name|res
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|retval
return|;
block|}
block|}
end_class

end_unit

