begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|test
package|;
end_package

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

begin_class
specifier|public
class|class
name|DocumentUpdateTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_COLLECTION_NAME
init|=
literal|"testup"
decl_stmt|;
specifier|private
name|Database
name|database
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
comment|/**      * Test if the doc, collection and document functions are correctly      * notified upon document updates. Call a function once on the empty collection,       * then call it again after a document was added, and compare the results.      */
specifier|public
name|void
name|testUpdate
parameter_list|()
block|{
name|String
name|imports
init|=
literal|"import module namespace xdb='http://exist-db.org/xquery/xmldb';\n"
operator|+
literal|"import module namespace util='http://exist-db.org/xquery/util';\n"
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- TEST 1: doc() function --"
argument_list|)
expr_stmt|;
name|String
name|query
init|=
name|imports
operator|+
literal|"declare function local:get-doc($path as xs:string) {\n"
operator|+
literal|"    doc($path)\n"
operator|+
literal|"};\n"
operator|+
literal|"let $col := xdb:create-collection('/db', 'testup')\n"
operator|+
literal|"let $path := '/db/testup/test1.xml'\n"
operator|+
literal|"let $doc := xdb:store($col, 'test1.xml',<test><n>1</n></test>)\n"
operator|+
literal|"let $d1 := local:get-doc($path)\n"
operator|+
literal|"let $remove := xdb:remove('/db/testup', 'test1.xml')\n"
operator|+
literal|"return string-join((string(count(local:get-doc($path))), string(doc-available($path))), ' ')"
decl_stmt|;
name|String
name|result
init|=
name|execQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
literal|"0 false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- TEST 2: document() function --"
argument_list|)
expr_stmt|;
name|query
operator|=
name|imports
operator|+
literal|"declare function local:get-doc($path as xs:string) {\n"
operator|+
literal|"    document($path)\n"
operator|+
literal|"};\n"
operator|+
literal|"let $col := xdb:create-collection('/db', 'testup')\n"
operator|+
literal|"let $path := '/db/testup/test1.xml'\n"
operator|+
literal|"let $d1 := local:get-doc($path)\n"
operator|+
literal|"let $doc := xdb:store($col, 'test1.xml',<test><n>1</n></test>)\n"
operator|+
literal|"return string-join((string(count(local:get-doc($path))), string(doc-available($path))), ' ')"
expr_stmt|;
name|result
operator|=
name|execQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
literal|"1 true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- TEST 3: collection() function --"
argument_list|)
expr_stmt|;
name|query
operator|=
name|imports
operator|+
literal|"declare function local:xpath($collection as xs:string) {\n"
operator|+
literal|"    for $c in collection($collection) return $c//n\n"
operator|+
literal|"};\n"
operator|+
literal|"let $col := xdb:create-collection('/db', 'testup')\n"
operator|+
literal|"let $path := '/db/testup'\n"
operator|+
literal|"let $d1 := local:xpath($path)//n/text()\n"
operator|+
literal|"let $doc := xdb:store($col, 'test1.xml',<test><n>1</n></test>)\n"
operator|+
literal|"return local:xpath($path)//n/text()"
expr_stmt|;
name|result
operator|=
name|execQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- TEST 4: 'update insert' statement --"
argument_list|)
expr_stmt|;
name|query
operator|=
name|imports
operator|+
literal|"declare function local:xpath($collection as xs:string) {\n"
operator|+
literal|"    collection($collection)\n"
operator|+
literal|"};\n"
operator|+
literal|"let $col := xdb:create-collection('/db', 'testup')\n"
operator|+
literal|"let $path := '/db/testup'\n"
operator|+
literal|"let $d1 := local:xpath($path)//n\n"
operator|+
literal|"let $doc := xdb:store($col, 'test1.xml',<test><n>1</n></test>)\n"
operator|+
literal|"return (\n"
operator|+
literal|"	update insert<n>2</n> into collection($path)/test,\n"
operator|+
literal|"	count(local:xpath($path)//n)\n"
operator|+
literal|")"
expr_stmt|;
name|result
operator|=
name|execQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-- TEST 5: 'update replace' statement --"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"let $doc := "
operator|+
literal|"<test> "
operator|+
literal|"<link href=\"features\"/> "
operator|+
literal|"(: it works with only 1 link :) "
operator|+
literal|"<link href=\"features/test\"/> "
operator|+
literal|"</test> "
operator|+
literal|"let $links := $doc/link/@href "
operator|+
literal|"return "
operator|+
literal|"for $link in $links "
operator|+
literal|"return ( "
operator|+
literal|"update replace $link with \"123\", "
operator|+
literal|"(: without the output on the next line, it works :) "
operator|+
literal|"xs:string($link) "
operator|+
literal|")"
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
name|ResourceSet
name|r
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|getSize
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"123"
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
name|void
name|testUpdateAttribute
parameter_list|()
block|{
name|String
name|query1
init|=
literal|"let $content :="
operator|+
literal|"<A><B><C d=\"xxx\">ccc1</C><C d=\"yyy\" e=\"zzz\">ccc2</C></B></A> "
operator|+
literal|"let $uri := xmldb:store(\"/db/\", \"marktest7.xml\", $content) "
operator|+
literal|"let $doc := doc($uri) "
operator|+
literal|"let $xxx := update delete $doc//@*"
operator|+
literal|"return $doc"
decl_stmt|;
name|String
name|query2
init|=
literal|"let $doc := doc(\"/db/marktest7.xml\") "
operator|+
literal|"return "
operator|+
literal|"( for $elem in $doc//* "
operator|+
literal|"return update insert attribute AAA {\"BBB\"} into $elem, $doc) "
decl_stmt|;
try|try
block|{
name|String
name|result1
init|=
name|execQuery
argument_list|(
name|query1
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
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
try|try
block|{
name|String
name|result2
init|=
name|execQuery
argument_list|(
name|query2
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
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
specifier|private
name|String
name|execQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
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
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
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
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
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
comment|/*      * @see TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
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
name|service
operator|.
name|removeCollection
argument_list|(
name|TEST_COLLECTION_NAME
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
name|database
operator|=
literal|null
expr_stmt|;
name|testCollection
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
name|DocumentUpdateTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

