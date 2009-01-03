begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|QueryServiceTest
extends|extends
name|DatabaseTestCase
block|{
annotation|@
name|Test
specifier|public
name|void
name|let1
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|let
argument_list|(
literal|"$a"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|single
argument_list|(
literal|"$a"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|let2
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|let
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|single
argument_list|(
literal|"$a"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|let3
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|f
operator|.
name|namespaceBindings
argument_list|()
operator|.
name|put
argument_list|(
literal|""
argument_list|,
literal|"http://example.com"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|let
argument_list|(
literal|"$a"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|single
argument_list|(
literal|"$a"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DatabaseException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|let4
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|let
argument_list|(
literal|"$a"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|single
argument_list|(
literal|"$a"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|query
argument_list|()
operator|.
name|single
argument_list|(
literal|"$a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DatabaseException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|let5
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|single
argument_list|(
literal|"$_1"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|query
argument_list|()
operator|.
name|single
argument_list|(
literal|"$_1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|importModule1
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module1"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"module namespace ex = 'http://example.com';\n"
operator|+
literal|"declare function ex:foo() { 'foo' };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|importModule
argument_list|(
name|doc
argument_list|)
operator|.
name|single
argument_list|(
literal|"ex:foo()"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|importModule2
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|createFolder
argument_list|(
literal|"/top/next"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module1"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"\n\nmodule  namespace  _123=\"http://example.com?a=1-2&amp;b=4\" ;\n"
operator|+
literal|"declare function _123:foo() { 'foo' };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|importModule
argument_list|(
name|doc
argument_list|)
operator|.
name|single
argument_list|(
literal|"_123:foo()"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|importModule3
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Document
name|doc1
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module1"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"module namespace ex = 'http://example.com';\n"
operator|+
literal|"declare function ex:foo() { 'foo' };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc2
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module2"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"module namespace ex = 'http://example.com';\n"
operator|+
literal|"declare function ex:foo() { 'bar' };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|importModule
argument_list|(
name|doc1
argument_list|)
operator|.
name|single
argument_list|(
literal|"ex:foo()"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|f
operator|.
name|query
argument_list|()
operator|.
name|importModule
argument_list|(
name|doc2
argument_list|)
operator|.
name|single
argument_list|(
literal|"ex:foo()"
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DatabaseException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|importModule4
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Document
name|doc1
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module1"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"module namespace ex = 'http://example.com';\n"
operator|+
literal|"declare function ex:foo() { 'foo' };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc2
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module2"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"module namespace ex = 'http://example.com/other';\n"
operator|+
literal|"declare function ex:foo() { 'bar' };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|query
argument_list|()
operator|.
name|importModule
argument_list|(
name|doc1
argument_list|)
operator|.
name|importModule
argument_list|(
name|doc2
argument_list|)
operator|.
name|single
argument_list|(
literal|"ex:foo()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|importModule5
parameter_list|()
block|{
name|Folder
name|f
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Document
name|module
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"module"
argument_list|)
argument_list|,
name|Source
operator|.
name|blob
argument_list|(
literal|"module namespace ex = 'http://example.com';\n"
operator|+
literal|"declare function ex:root() { / };\n"
argument_list|)
argument_list|)
decl_stmt|;
name|XMLDocument
name|doc1
init|=
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"doc1"
argument_list|)
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|documents
argument_list|()
operator|.
name|load
argument_list|(
name|Name
operator|.
name|create
argument_list|(
literal|"doc2"
argument_list|)
argument_list|,
name|Source
operator|.
name|xml
argument_list|(
literal|"<foo/>"
argument_list|)
argument_list|)
expr_stmt|;
name|QueryService
name|qs
init|=
name|db
operator|.
name|query
argument_list|(
name|doc1
argument_list|)
operator|.
name|importModule
argument_list|(
name|module
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|qs
operator|.
name|all
argument_list|(
literal|"/"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|qs
operator|.
name|all
argument_list|(
literal|"ex:root()"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze1
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"zero-or-one(//blah)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"item()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze2
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"exactly-one(//blah)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ONE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"item()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze3
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"one-or-more(//blah)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"item()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze4
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"//blah"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QueryService
operator|.
name|QueryAnalysis
operator|.
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|qa
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"node()"
argument_list|,
name|qa
operator|.
name|returnTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze5
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"$blah"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|qa
operator|.
name|requiredVariables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|qa
operator|.
name|requiredVariables
argument_list|()
argument_list|,
name|hasItems
argument_list|(
operator|new
name|QName
argument_list|(
literal|null
argument_list|,
literal|"blah"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze6
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"declare namespace bar = 'http://example.com'; $bar:blah"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|qa
operator|.
name|requiredVariables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|qa
operator|.
name|requiredVariables
argument_list|()
argument_list|,
name|hasItems
argument_list|(
operator|new
name|QName
argument_list|(
literal|"http://example.com"
argument_list|,
literal|"blah"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze7
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"local:foo()"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|qa
operator|.
name|requiredFunctions
argument_list|()
argument_list|,
name|hasItems
argument_list|(
operator|new
name|QName
argument_list|(
literal|"http://www.w3.org/2005/xquery-local-functions"
argument_list|,
literal|"foo"
argument_list|,
literal|"local"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|analyze8
parameter_list|()
block|{
name|QueryService
operator|.
name|QueryAnalysis
name|qa
init|=
name|db
operator|.
name|getFolder
argument_list|(
literal|"/"
argument_list|)
operator|.
name|query
argument_list|()
operator|.
name|analyze
argument_list|(
literal|"declare namespace bar = 'http://example.com'; bar:foo()"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|qa
operator|.
name|requiredFunctions
argument_list|()
argument_list|,
name|hasItems
argument_list|(
operator|new
name|QName
argument_list|(
literal|"http://example.com"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

