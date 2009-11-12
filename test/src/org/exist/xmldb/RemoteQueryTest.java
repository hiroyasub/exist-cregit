begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2006-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|net
operator|.
name|BindException
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
name|Iterator
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|TestConstants
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
name|xmlrpc
operator|.
name|RpcAPI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|XmlRpcTest
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

begin_class
specifier|public
class|class
name|RemoteQueryTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
name|String
name|uri
init|=
literal|"xmldb:exist://localhost:8088/xmlrpc"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|static
name|StandaloneServer
name|server
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
specifier|private
name|Collection
name|xmlrpcCollection
decl_stmt|;
specifier|public
name|void
name|testResourceSet
parameter_list|()
block|{
try|try
block|{
name|String
name|query
init|=
literal|"//SPEECH[SPEAKER = 'HAMLET']"
decl_stmt|;
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
name|service
operator|.
name|setProperty
argument_list|(
literal|"highlight-matches"
argument_list|,
literal|"none"
argument_list|)
expr_stmt|;
name|CompiledExpression
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|359
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|r
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|r
operator|.
name|getContentAsDOM
argument_list|()
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|r
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|testExternalVar
parameter_list|()
block|{
try|try
block|{
name|String
name|query
init|=
name|XmlRpcTest
operator|.
name|QUERY_MODULE_DATA
decl_stmt|;
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
name|service
operator|.
name|setProperty
argument_list|(
literal|"highlight-matches"
argument_list|,
literal|"none"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setNamespace
argument_list|(
literal|"tm"
argument_list|,
literal|"http://exist-db.org/test/module"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setNamespace
argument_list|(
literal|"tm-query"
argument_list|,
literal|"http://exist-db.org/test/module/query"
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"tm:imported-external-string"
argument_list|,
literal|"imported-string-value"
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
literal|"tm-query:local-external-string"
argument_list|,
literal|"local-string-value"
argument_list|)
expr_stmt|;
name|CompiledExpression
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|r
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|r
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://localhost"
argument_list|)
condition|)
name|initServer
argument_list|()
expr_stmt|;
try|try
block|{
comment|// initialize driver
name|Class
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|testCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|Resource
name|xr
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"hamlet.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/shakespeare/hamlet.xml"
argument_list|)
decl_stmt|;
name|xr
operator|.
name|setContent
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|xr
argument_list|)
expr_stmt|;
name|xmlrpcCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"xmlrpc"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|xmlrpcCollection
argument_list|)
expr_stmt|;
name|Resource
name|br
init|=
name|xmlrpcCollection
operator|.
name|createResource
argument_list|(
name|TestConstants
operator|.
name|TEST_MODULE_URI
operator|.
name|toString
argument_list|()
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|br
operator|)
operator|.
name|setMimeType
argument_list|(
name|MimeType
operator|.
name|XQUERY_TYPE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|br
operator|.
name|setContent
argument_list|(
name|XmlRpcTest
operator|.
name|MODULE_DATA
argument_list|)
expr_stmt|;
name|xmlrpcCollection
operator|.
name|storeResource
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initServer
parameter_list|()
block|{
try|try
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
name|getThrowables
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
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|CollectionImpl
operator|)
name|testCollection
operator|)
operator|.
name|isRemoteCollection
argument_list|()
condition|)
block|{
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|testCollection
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
name|testCollection
operator|=
literal|null
expr_stmt|;
name|xmlrpcCollection
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tearDown PASSED"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
name|RemoteQueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

