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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XPathQueryServiceImpl
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
name|ResourceIterator
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

begin_class
specifier|public
class|class
name|XPathQueryTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|nested
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<test><c></c><b><c><b></b></c></b><b></b><c></c></test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|numbers
init|=
literal|"<test>"
operator|+
literal|"<item id='1'><price>5.6</price><stock>22</stock></item>"
operator|+
literal|"<item id='2'><price>7.4</price><stock>43</stock></item>"
operator|+
literal|"<item id='3'><price>18.4</price><stock>5</stock></item>"
operator|+
literal|"<item id='4'><price>65.54</price><stock>16</stock></item>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|namespaces
init|=
literal|"<test xmlns='http://www.foo.com'>"
operator|+
literal|"<section>"
operator|+
literal|"<title>Test Document</title>"
operator|+
literal|"<c:comment xmlns:c='http://www.other.com'>This is my comment</c:comment>"
operator|+
literal|"</section>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|strings
init|=
literal|"<test>"
operator|+
literal|"<string>Hello World!</string>"
operator|+
literal|"<string value='Hello World!'/>"
operator|+
literal|"<string>Hello</string>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|nested2
init|=
literal|"<RootElement>"
operator|+
literal|"<ChildA>"
operator|+
literal|"<ChildB id=\"2\"/>"
operator|+
literal|"</ChildA>"
operator|+
literal|"</RootElement>"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
specifier|public
name|XPathQueryTest
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
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
literal|"xmldb:exist:///db"
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
comment|//			XMLResource doc =
comment|//				(XMLResource) root.createResource("r_and_j.xml", "XMLResource");
comment|//			doc.setContent(new File("samples/shakespeare/r_and_j.xml"));
comment|//			root.storeResource(doc);
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
specifier|public
name|void
name|testStarAxis
parameter_list|()
block|{
name|ResourceSet
name|result
decl_stmt|;
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"numbers.xml"
argument_list|,
name|numbers
argument_list|)
decl_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"numbers.xml"
argument_list|,
literal|"/*/item"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStarAxis 1: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XPath: /*/item"
argument_list|,
literal|4
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"numbers.xml"
argument_list|,
literal|"/test/*"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStarAxis  2: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XPath: /test/*"
argument_list|,
literal|4
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"numbers.xml"
argument_list|,
literal|"/test/descendant-or-self::*"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStarAxis  3: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XPath: /test/descendant-or-self::*"
argument_list|,
literal|12
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStarAxis 4: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|// TODO: needs to be fixed:
name|assertEquals
argument_list|(
literal|"XPath: /*/*"
argument_list|,
literal|12
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStarAxis(): XMLDBException: "
operator|+
name|e
argument_list|)
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
name|testParentSelfAxis
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"nested2.xml"
argument_list|,
name|nested2
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"nested2.xml"
argument_list|,
literal|"/RootElement/descendant::*/parent::ChildA"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"nested2.xml"
argument_list|,
literal|"/RootElement/descendant::*[self::ChildB]/parent::RootElement"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"nested2.xml"
argument_list|,
literal|"/RootElement/descendant::*[self::ChildA]/parent::RootElement"
argument_list|,
literal|1
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
name|void
name|testNumbers
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"numbers.xml"
argument_list|,
name|numbers
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"sum(/test/item/price)"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"96.94"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"round(sum(/test/item/price))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"97.0"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"floor(sum(/test/item/stock))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"86.0"
argument_list|,
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
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"/test/item[round(price + 3)> 60]"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"min( 123456789123456789123456789, "
operator|+
literal|"123456789123456789123456789123456789123456789 )"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"minimum of big integers"
argument_list|,
literal|"123456789123456789123456789"
argument_list|,
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
name|void
name|testStrings
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"strings.xml"
argument_list|,
name|strings
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"substring(/test/string[1], 1, 5)"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hello"
argument_list|,
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
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"/test/string[starts-with(string(.), 'Hello')]"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"count(/test/item/price)"
argument_list|,
literal|1
argument_list|,
literal|"Query should return an empty set (wrong document)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
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
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStrings(): XMLDBException: "
operator|+
name|e
argument_list|)
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
name|testBoolean
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
literal|"Testing effective boolean value of expressions ..."
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"numbers.xml"
argument_list|,
name|numbers
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(1.0)"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of 1.0 should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(0.0)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of 0.0 should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(xs:double(0.0))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of double 0.0 should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(xs:double(1.0))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of double 1.0 should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(xs:float(1.0))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of float 1.0 should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(xs:float(0.0))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of float 0.0 should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(xs:integer(0))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of integer 0 should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(xs:integer(1))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of integer 1 should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"'true' cast as xs:boolean"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of 'true' cast to xs:boolean should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"'false' cast as xs:boolean"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of 'false' cast to xs:boolean should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean('Hello')"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of string 'Hello' should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean('')"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of empty string should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(())"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of empty sequence should be false"
argument_list|,
literal|"false"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(('Hello'))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of sequence with non-empty string should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean((0.0, 0.0))"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of sequence with two elements should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(//item[@id = '1']/price)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of 5.6 should be true"
argument_list|,
literal|"true"
argument_list|,
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
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"numbers.xml"
argument_list|,
literal|"boolean(current-time())"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"boolean value of current-time() should be true"
argument_list|,
literal|"true"
argument_list|,
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
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testBoolean(): XMLDBException: "
operator|+
name|e
argument_list|)
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
name|testNot
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"strings.xml"
argument_list|,
name|strings
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"/test/string[not(@value)]"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"not(/test/abcd)"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|r
init|=
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"not(/test)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|r
operator|=
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|r
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"/test/string[not(@id)]"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|r
operator|=
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<string>Hello World!</string>"
argument_list|,
name|r
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test with non-existing items
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"document()/blah[not(blah)]"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"//*[string][not(@value)]"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"//*[string][not(@blah)]"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"strings.xml"
argument_list|,
literal|"//*[blah][not(@blah)]"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStrings(): XMLDBException: "
operator|+
name|e
argument_list|)
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
specifier|private
name|ResourceSet
name|queryResource
parameter_list|(
name|XPathQueryService
name|service
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|queryResource
argument_list|(
name|service
argument_list|,
name|resource
argument_list|,
name|query
argument_list|,
name|expected
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** 	 * @param service 	 * @throws XMLDBException 	 */
specifier|private
name|ResourceSet
name|queryResource
parameter_list|(
name|XPathQueryService
name|service
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|queryResource
argument_list|(
name|resource
argument_list|,
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** 	 * @return 	 * @throws XMLDBException 	 */
specifier|private
name|XPathQueryService
name|storeXMLStringAndGetQueryService
parameter_list|(
name|String
name|documentName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|documentName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|service
return|;
block|}
specifier|public
name|void
name|testNamespaces
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"namespaces.xml"
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
name|service
operator|.
name|setNamespace
argument_list|(
literal|"t"
argument_list|,
literal|"http://www.foo.com"
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"namespaces.xml"
argument_list|,
literal|"//t:section"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"namespaces.xml"
argument_list|,
literal|"/t:test//c:comment"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"namespaces.xml"
argument_list|,
literal|"//c:*"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"namespaces.xml"
argument_list|,
literal|"//*:comment"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
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
name|void
name|testNestedElements
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"nested.xml"
argument_list|,
name|nested
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|queryResource
argument_list|(
literal|"nested.xml"
argument_list|,
literal|"//c"
argument_list|)
decl_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
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
name|void
name|testStaticVariables
parameter_list|()
block|{
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"numbers.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|numbers
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|XPathQueryServiceImpl
name|service2
init|=
operator|(
name|XPathQueryServiceImpl
operator|)
name|service
decl_stmt|;
name|service2
operator|.
name|declareVariable
argument_list|(
literal|"name"
argument_list|,
literal|"MONTAGUE"
argument_list|)
expr_stmt|;
name|service2
operator|.
name|declareVariable
argument_list|(
literal|"name"
argument_list|,
literal|"43"
argument_list|)
expr_stmt|;
comment|//ResourceSet result = service.query("//SPEECH[SPEAKER=$name]");
name|result
operator|=
name|service2
operator|.
name|query
argument_list|(
name|doc
argument_list|,
literal|"//item[stock=$name]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStaticVariables 1: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|service2
operator|.
name|query
argument_list|(
literal|"$name"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStaticVariables 2: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|service2
operator|.
name|query
argument_list|(
name|doc
argument_list|,
literal|"//item[stock=43]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStaticVariables 3: ========"
argument_list|)
expr_stmt|;
name|printResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|service2
operator|.
name|query
argument_list|(
name|doc
argument_list|,
literal|"//item"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// assertEquals( 10, result.getSize() );
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testStaticVariables(): XMLDBException: "
operator|+
name|e
argument_list|)
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
comment|/** 	 * @param result 	 * @throws XMLDBException 	 */
specifier|private
name|void
name|printResult
parameter_list|(
name|ResourceSet
name|result
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|ResourceIterator
name|i
init|=
name|result
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|Resource
name|r
init|=
name|i
operator|.
name|nextResource
argument_list|()
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
specifier|public
name|void
name|testMembersAsResource
parameter_list|()
block|{
try|try
block|{
comment|//			XPathQueryService service =
comment|//				(XPathQueryService) testCollection.getService(
comment|//					"XPathQueryService",
comment|//					"1.0");
comment|//			ResourceSet result = service.query("//SPEECH[LINE&= 'marriage']");
name|XPathQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
literal|"numbers.xml"
argument_list|,
name|numbers
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"//item/price"
argument_list|)
decl_stmt|;
name|Resource
name|r
init|=
name|result
operator|.
name|getMembersAsResource
argument_list|()
decl_stmt|;
name|String
name|content
init|=
operator|(
name|String
operator|)
name|r
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*(<price>.*){4}"
argument_list|,
name|Pattern
operator|.
name|DOTALL
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"get whole document numbers.xml"
argument_list|,
name|m
operator|.
name|matches
argument_list|()
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
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|XPathQueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

