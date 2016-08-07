begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
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
name|ErrorCodes
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  *  * @author jim.fuller@webcomposite.com  */
end_comment

begin_class
specifier|public
class|class
name|EvalTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|()
decl_stmt|;
specifier|private
name|Resource
name|invokableQuery
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INVOKABLE_QUERY_FILENAME
init|=
literal|"invokable.xql"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INVOKABLE_QUERY_EXTERNAL_VAR_NAME
init|=
literal|"some-value"
decl_stmt|;
specifier|public
name|EvalTest
parameter_list|()
block|{
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|invokableQuery
operator|=
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|createResource
argument_list|(
name|INVOKABLE_QUERY_FILENAME
argument_list|,
literal|"BinaryResource"
argument_list|)
expr_stmt|;
name|invokableQuery
operator|.
name|setContent
argument_list|(
literal|"declare variable $"
operator|+
name|INVOKABLE_QUERY_EXTERNAL_VAR_NAME
operator|+
literal|" external;\n"
operator|+
literal|"<hello>{$"
operator|+
name|INVOKABLE_QUERY_EXTERNAL_VAR_NAME
operator|+
literal|"}</hello>"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|invokableQuery
operator|)
operator|.
name|setMimeType
argument_list|(
literal|"application/xquery"
argument_list|)
expr_stmt|;
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|storeResource
argument_list|(
name|invokableQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|removeResource
argument_list|(
name|invokableQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|eval
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $query := 'let $a := 1 return $a'\n"
operator|+
literal|"return\n"
operator|+
literal|"util:eval($query)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
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
literal|"1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|evalWithExternalVars
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $value := 'world' return\n"
operator|+
literal|"\tutil:eval(xs:anyURI('/db/"
operator|+
name|INVOKABLE_QUERY_FILENAME
operator|+
literal|"'), false(), (xs:QName('"
operator|+
name|INVOKABLE_QUERY_EXTERNAL_VAR_NAME
operator|+
literal|"'), $value))"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|LocalXMLResource
name|res
init|=
operator|(
name|LocalXMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Node
name|n
init|=
name|res
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|n
operator|.
name|getLocalName
argument_list|()
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"world"
argument_list|,
name|n
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|evalwithPI
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $query := 'let $a :=<test><?pi test?></test> return count($a//processing-instruction())'\n"
operator|+
literal|"return\n"
operator|+
literal|"util:eval($query)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
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
literal|"1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|evalInline
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $xml := document{<test><a><b/></a></test>}\n"
operator|+
literal|"let $query := 'count(.//*)'\n"
operator|+
literal|"return\n"
operator|+
literal|"util:eval-inline($xml,$query)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
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
literal|"3"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEvalWithContextVariable
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $xml :=<test><a/><b/></test>\n"
operator|+
literal|"let $context :=<static-context>\n"
operator|+
literal|"<variable name='xml'>{$xml}</variable>\n"
operator|+
literal|"</static-context>\n"
operator|+
literal|"let $query := 'count($xml//*) mod 2 = 0'\n"
operator|+
literal|"return\n"
operator|+
literal|"util:eval-with-context($query, $context, false())"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
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
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEvalSupplyingContext
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $xml :=<test><a/></test>\n"
operator|+
literal|"let $context :=<static-context>\n"
operator|+
literal|"<default-context>{$xml}</default-context>\n"
operator|+
literal|"</static-context>\n"
operator|+
literal|"let $query := 'count(.//*) mod 2 = 0'\n"
operator|+
literal|"return\n"
operator|+
literal|"util:eval-with-context($query, $context, false())"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
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
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEvalSupplyingContextAndVariable
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $xml :=<test><a/></test>\n"
operator|+
literal|"let $context :=<static-context>\n"
operator|+
literal|"<variable name='xml'>{$xml}</variable>\n"
operator|+
literal|"<default-context>{$xml}</default-context>\n"
operator|+
literal|"</static-context>\n"
operator|+
literal|"let $query := 'count($xml//*) + count(.//*)'\n"
operator|+
literal|"return\n"
operator|+
literal|"util:eval-with-context($query, $context, false())"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
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
literal|"3"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|evalInContextWithPreDeclaredNamespace
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createCollection
argument_list|(
literal|"testEvalInContextWithPreDeclaredNamespace"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query
init|=
literal|"xquery version \"1.0\";\r\n"
operator|+
literal|"declare namespace db = \"http://docbook.org/ns/docbook\";\r\n"
operator|+
literal|"import module namespace util = \"http://exist-db.org/xquery/util\";\r\n"
operator|+
literal|"let $q := \"/db:article\" return\r\n"
operator|+
literal|"util:eval($q)"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|evalInContextWithPreDeclaredNamespaceAcrossLocalFunctionBoundary
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createCollection
argument_list|(
literal|"testEvalInContextWithPreDeclaredNamespace"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query
init|=
literal|"xquery version \"1.0\";\r\n"
operator|+
literal|"import module namespace util = \"http://exist-db.org/xquery/util\";\r\n"
operator|+
literal|"declare namespace db = \"http://docbook.org/ns/docbook\";\r\n"
operator|+
literal|"declare function local:process($q as xs:string) {\r\n"
operator|+
literal|"\tutil:eval($q)\r\n"
operator|+
literal|"};\r\n"
operator|+
literal|"let $q := \"/db:article\" return\r\n"
operator|+
literal|"local:process($q)"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|//should fail with - Error while evaluating expression: /db:article. XPST0081: No namespace defined for prefix db [at line 5, column 9]
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|evalInContextWithPreDeclaredNamespaceAcrossModuleBoundary
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|testHome
init|=
name|createCollection
argument_list|(
literal|"testEvalInContextWithPreDeclaredNamespace"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|processorModule
init|=
literal|"xquery version \"1.0\";\r\n"
operator|+
literal|"module namespace processor = \"http://processor\";\r\n"
operator|+
literal|"import module namespace util = \"http://exist-db.org/xquery/util\";\r\n"
operator|+
literal|"declare function processor:process($q as xs:string) {\r\n"
operator|+
literal|"\tutil:eval($q)\r\n"
operator|+
literal|"};"
decl_stmt|;
name|writeModule
argument_list|(
name|testHome
argument_list|,
literal|"processor.xqm"
argument_list|,
name|processorModule
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query
init|=
literal|"xquery version \"1.0\";\r\n"
operator|+
literal|"import module namespace processor = \"http://processor\" at \"xmldb:exist://"
operator|+
name|testHome
operator|.
name|getName
argument_list|()
operator|+
literal|"/processor.xqm\";\r\n"
operator|+
literal|"declare namespace db = \"http://docbook.org/ns/docbook\";\r\n"
operator|+
literal|"let $q := \"/db:article\" return\r\n"
operator|+
literal|"processor:process($q)"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|/**      * The original issue was caused by VariableReference inside util:eval      * not calling XQueryContext#popNamespaceContext when a variable      * reference could not be resolved, which led to the wrong      * namespaces being present in the XQueryContext the next time      * the same query was executed      */
annotation|@
name|Test
specifier|public
name|void
name|evalWithMissingVariableReferenceShouldReportTheSameErrorEachTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|testHomeName
init|=
literal|"testEvalWithMissingVariableReferenceShouldReportTheSameErrorEachTime"
decl_stmt|;
specifier|final
name|Collection
name|testHome
init|=
name|createCollection
argument_list|(
name|testHomeName
argument_list|)
decl_stmt|;
specifier|final
name|String
name|configModuleName
init|=
literal|"config-test.xqm"
decl_stmt|;
specifier|final
name|String
name|configModule
init|=
literal|"xquery version \"1.0\";\r\n"
operator|+
literal|"module namespace ct = \"http://config/test\";\r\n"
operator|+
literal|"declare variable $ct:var1 { request:get-parameter(\"var1\", ()) };"
decl_stmt|;
name|writeModule
argument_list|(
name|testHome
argument_list|,
name|configModuleName
argument_list|,
name|configModule
argument_list|)
expr_stmt|;
specifier|final
name|String
name|testModuleName
init|=
literal|"test.xqy"
decl_stmt|;
specifier|final
name|String
name|testModule
init|=
literal|"import module namespace ct = \"http://config/test\" at \"xmldb:exist:///db/"
operator|+
name|testHomeName
operator|+
literal|"/"
operator|+
name|configModuleName
operator|+
literal|"\";\r\n"
operator|+
literal|"declare namespace x = \"http://x\";\r\n"
operator|+
literal|"declare function local:hello() {\r\n"
operator|+
literal|" (\r\n"
operator|+
literal|"<x:hello>hello</x:hello>,\r\n"
operator|+
literal|"util:eval(\"$ct:var1\")\r\n"
operator|+
literal|")\r\n"
operator|+
literal|"};\r\n"
operator|+
literal|"local:hello()"
decl_stmt|;
name|writeModule
argument_list|(
name|testHome
argument_list|,
name|testModuleName
argument_list|,
name|testModule
argument_list|)
expr_stmt|;
comment|//run the 1st time
try|try
block|{
name|executeModule
argument_list|(
name|testHome
argument_list|,
name|testModuleName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|XPathException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
argument_list|,
operator|(
operator|(
name|XPathException
operator|)
name|cause
operator|)
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//run a 2nd time, error code should be the same!
try|try
block|{
name|executeModule
argument_list|(
name|testHome
argument_list|,
name|testModuleName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|XPathException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
argument_list|,
operator|(
operator|(
name|XPathException
operator|)
name|cause
operator|)
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|Collection
name|collection
init|=
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
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
name|CollectionManagementService
name|cmService
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
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
name|XmldbURI
operator|.
name|LOCAL_DB
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
name|executeModule
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|moduleName
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XPathQueryServiceImpl
name|service
init|=
operator|(
name|XPathQueryServiceImpl
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|moduleUri
init|=
operator|(
operator|(
name|CollectionImpl
operator|)
name|collection
operator|)
operator|.
name|getPathURI
argument_list|()
operator|.
name|append
argument_list|(
name|moduleName
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|executeStoredQuery
argument_list|(
name|moduleUri
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

