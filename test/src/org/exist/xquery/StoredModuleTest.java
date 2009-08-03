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
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|BasicConfigurator
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
name|Logger
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
name|EXistResource
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
name|BinaryResource
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
name|XQueryService
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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|StoredModuleTest
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|StoredModuleTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MODULE
init|=
literal|"module namespace itg-modules = \"http://localhost:80/itg/xquery\";\n"
operator|+
literal|"declare variable $itg-modules:colls as xs:string+ external;\n"
operator|+
literal|"declare variable $itg-modules:coll as xs:string external;\n"
operator|+
literal|"declare variable $itg-modules:ordinal as xs:integer external;\n"
operator|+
literal|"declare function itg-modules:check-coll() as xs:boolean {\n"
operator|+
literal|"   if (fn:empty($itg-modules:coll)) then fn:false()\n"
operator|+
literal|"   else fn:true()\n"
operator|+
literal|"};"
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|rootCollection
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|CollectionManagementService
name|cmService
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|XQueryService
name|xqService
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Database
name|database
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|first
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting..."
argument_list|)
expr_stmt|;
try|try
block|{
name|BasicConfigurator
operator|.
name|configure
argument_list|()
expr_stmt|;
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
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|""
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
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down"
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
block|}
specifier|private
name|Collection
name|createCollection
parameter_list|(
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
name|collection
init|=
name|rootCollection
operator|.
name|getChildCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
comment|//cmService.removeCollection(collectionName);
name|cmService
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
name|collection
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
name|collection
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
specifier|private
name|void
name|writeModule
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|modulename
parameter_list|,
name|String
name|module
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create module "
operator|+
name|modulename
argument_list|)
expr_stmt|;
name|BinaryResource
name|res
init|=
operator|(
name|BinaryResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|modulename
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setMimeType
argument_list|(
literal|"application/xquery"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|module
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|ResourceSet
name|executeQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
annotation|@
name|Test
specifier|public
name|void
name|testDB
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rootCollection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|xqService
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|cmService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
name|c
init|=
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|writeModule
argument_list|(
name|c
argument_list|,
literal|"test.xqm"
argument_list|,
name|MODULE
argument_list|)
expr_stmt|;
name|String
name|query
init|=
literal|"import module namespace itg-modules = \"http://localhost:80/itg/xquery\" at "
operator|+
literal|"\"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/test.xqm\"; itg-modules:check-coll()"
decl_stmt|;
name|String
name|cols
index|[]
init|=
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|}
decl_stmt|;
name|xqService
operator|.
name|setNamespace
argument_list|(
literal|"itg-modules"
argument_list|,
literal|"http://localhost:80/itg/xquery"
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cols
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|xqService
operator|.
name|declareVariable
argument_list|(
literal|"itg-modules:coll"
argument_list|,
name|cols
index|[
name|i
index|]
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Result: "
operator|+
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModule1
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"module1"
decl_stmt|;
name|String
name|module
init|=
literal|"module namespace mod1 = 'urn:module1';"
operator|+
literal|"declare function mod1:showMe() as xs:string {"
operator|+
literal|"'hi from module 1'"
operator|+
literal|"};"
decl_stmt|;
name|String
name|query
init|=
literal|"import module namespace mod1 = 'urn:module1' "
operator|+
literal|"at  'xmldb:exist:/"
operator|+
name|collectionName
operator|+
literal|"/module1.xqm'; "
operator|+
literal|"mod1:showMe()"
decl_stmt|;
name|Collection
name|c
init|=
name|createCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|writeModule
argument_list|(
name|c
argument_list|,
literal|"module1.xqm"
argument_list|,
name|module
argument_list|)
expr_stmt|;
name|ResourceSet
name|rs
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|rs
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hi from module 1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Error relative resolve"
argument_list|)
specifier|public
name|void
name|bugtestmodule23
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collection2Name
init|=
literal|"module2"
decl_stmt|;
name|String
name|collection3Name
init|=
literal|"module2/module3"
decl_stmt|;
name|String
name|module2
init|=
literal|"module namespace mod2 = 'urn:module2'; "
operator|+
literal|"import module namespace mod3 = 'urn:module3' "
operator|+
literal|"at  'module3/module3.xqm'; "
operator|+
literal|"declare function mod2:showMe() as xs:string {"
operator|+
literal|" mod3:showMe() "
operator|+
literal|"};"
decl_stmt|;
name|String
name|module3a
init|=
literal|"module namespace mod3 = 'urn:module3';"
operator|+
literal|"declare function mod3:showMe() as xs:string {"
operator|+
literal|"'hi from module 3a'"
operator|+
literal|"};"
decl_stmt|;
name|String
name|query
init|=
literal|"import module namespace mod2 = 'urn:module2' "
operator|+
literal|"at  'module2/module2.xqm'; "
operator|+
literal|"mod2:showMe()"
decl_stmt|;
name|String
name|module3b
init|=
literal|"module namespace mod3 = 'urn:module3';"
operator|+
literal|"declare function mod3:showMe() as xs:string {"
operator|+
literal|"'hi from module 3b'"
operator|+
literal|"};"
decl_stmt|;
name|Collection
name|c2
init|=
name|createCollection
argument_list|(
name|collection2Name
argument_list|)
decl_stmt|;
name|writeModule
argument_list|(
name|c2
argument_list|,
literal|"module2.xqm"
argument_list|,
name|module2
argument_list|)
expr_stmt|;
comment|//writeModule(c2, "module3.xqm", module3b);
name|Collection
name|c3
init|=
name|createCollection
argument_list|(
name|collection3Name
argument_list|)
decl_stmt|;
name|writeModule
argument_list|(
name|c3
argument_list|,
literal|"module3.xqm"
argument_list|,
name|module3a
argument_list|)
expr_stmt|;
try|try
block|{
name|ResourceSet
name|rs
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|rs
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hi from module 3a"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

